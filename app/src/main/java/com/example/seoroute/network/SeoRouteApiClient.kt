package com.example.seoroute.network

import android.content.Context
import com.example.seoroute.BuildConfig
import com.example.seoroute.R
import com.example.seoroute.model.BookedTicket
import com.example.seoroute.model.EventDetailType
import com.example.seoroute.model.EventTagStyle
import com.example.seoroute.model.GateStaffMember
import com.example.seoroute.model.OrganizerEventItem
import com.example.seoroute.model.OrganizerEventStatus
import com.example.seoroute.model.SeoEvent
import com.example.seoroute.model.VibeCategory
import com.example.seoroute.ui.theme.SeoBookingBg
import com.example.seoroute.ui.theme.SeoBookingText
import com.example.seoroute.ui.theme.SeoFilterChipBg
import com.example.seoroute.ui.theme.SeoGreenBg
import com.example.seoroute.ui.theme.SeoGreenText
import com.example.seoroute.ui.theme.SeoMapPlazaGreen
import com.example.seoroute.ui.theme.SeoMapVenuePurple
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoPrimaryLight
import com.example.seoroute.ui.theme.SeoTextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

data class ApiUserDto(
    val id: String,
    val email: String,
    val name: String,
    val photoUrl: String?,
    val role: String,
    val organizerId: String?,
    val isActive: Boolean
)

data class ApiSessionResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val user: ApiUserDto
)

data class ApiOrganizerProfileDto(
    val id: String,
    val name: String,
    val certificationNumber: String,
    val supportEmail: String,
    val supportPhone: String,
    val isSystem: Boolean
)

data class ApiNotificationDto(
    val id: String,
    val eventId: String?,
    val type: String,
    val title: String,
    val body: String,
    val isRead: Boolean,
    val createdAt: String
)

data class ApiQrPayloadDto(
    val ticketId: String,
    val ticketSecret: String,
    val algorithm: String,
    val periodSecs: Long,
    val counter: Long,
    val token: String,
    val expiresAt: String
)

data class ApiTicketingStatsDto(
    val eventId: String,
    val checkedIn: Int,
    val notYet: Int,
    val total: Int,
    val serverTime: String
)

data class ApiManifestTicketDto(
    val id: String,
    val ticketSecret: String,
    val status: String,
    val scannedAt: String?
)

data class ApiScanResultDto(
    val id: String,
    val clientScanId: String,
    val ticketId: String,
    val eventId: String,
    val deviceId: String,
    val result: String,
    val scannedAt: String
)

data class ApiAttendeeRowDto(
    val ticketId: String,
    val userId: String,
    val name: String,
    val email: String,
    val status: String,
    val bookedAt: String,
    val scannedAt: String?
)

/**
 * Retrofit + OkHttp + DataStore Client for the deployed SeoRoute Backend (`BuildConfig.BASE_URL`).
 */
object SeoRouteApiClient {
    const val BASE_URL: String = BuildConfig.BASE_URL
    private val SEOUL_ZONE: ZoneId = ZoneId.of("Asia/Seoul")
    private val JSON_MEDIA_TYPE = "application/json; charset=UTF-8".toMediaType()

    @Volatile
    var accessToken: String? = null

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
            .header("Accept", "application/json")
        val token = accessToken
        if (!token.isNullOrBlank()) {
            builder.header("Authorization", "Bearer $token")
        }
        chain.proceed(builder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val service: SeoRouteRetrofitService = retrofit.create(SeoRouteRetrofitService::class.java)

    /**
     * Initializes Jetpack DataStore and restores any saved JWT token into memory.
     */
    suspend fun initDataStore(context: Context) {
        TokenDataStore.init(context)
        val stored = TokenDataStore.getToken()
        if (!stored.isNullOrBlank() && accessToken.isNullOrBlank()) {
            accessToken = stored
        }
    }

    suspend fun clearStoredSession() {
        accessToken = null
        TokenDataStore.clearToken()
    }

    private fun String.asJsonBody(): RequestBody = this.toRequestBody(JSON_MEDIA_TYPE)

    private fun unwrapResponse(response: Response<String>, endpointLabel: String): String {
        val bodyText = response.body() ?: ""
        if (response.isSuccessful) {
            return bodyText
        }
        val errorText = response.errorBody()?.string().orEmpty()
        val apiErrorMsg = runCatching {
            JSONObject(errorText).optJSONObject("error")?.optString("message")
        }.getOrNull()
        throw IllegalStateException(
            apiErrorMsg?.takeIf { it.isNotBlank() }
                ?: "HTTP ${response.code()} error from $endpointLabel"
        )
    }

    // -------------------------------------------------------------------------
    // 1. Authentication (/auth/dev & /auth/sync) with DataStore token persistence
    // -------------------------------------------------------------------------

    suspend fun devLogin(
        email: String,
        name: String,
        role: String = "attendee",
        organizerId: String? = null
    ): Result<ApiSessionResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().apply {
                put("email", email)
                put("name", name)
                put("role", role)
                if (organizerId != null) {
                    put("organizer_id", organizerId)
                } else {
                    put("organizer_id", JSONObject.NULL)
                }
            }
            val raw = unwrapResponse(service.devLogin(body.toString().asJsonBody()), "/auth/dev")
            val json = JSONObject(raw)
            val session = parseSessionResponse(json)
            accessToken = session.accessToken
            TokenDataStore.saveToken(
                token = session.accessToken,
                email = session.user.email,
                name = session.user.name,
                role = session.user.role
            )
            session
        }
    }

    suspend fun firebaseSync(idToken: String): Result<ApiSessionResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().apply {
                put("id_token", idToken)
            }
            val raw = unwrapResponse(service.firebaseSync(body.toString().asJsonBody()), "/auth/sync")
            val json = JSONObject(raw)
            val session = parseSessionResponse(json)
            accessToken = session.accessToken
            TokenDataStore.saveToken(
                token = session.accessToken,
                email = session.user.email,
                name = session.user.name,
                role = session.user.role
            )
            session
        }
    }

    // -------------------------------------------------------------------------
    // 2. Public Discovery (/events, /events/{id}) via Retrofit
    // -------------------------------------------------------------------------

    suspend fun fetchPublicEvents(
        query: String? = null,
        category: String? = null,
        accessType: String? = null,
        isOutdoor: Boolean? = null,
        sort: String = "soonest",
        page: Int = 1,
        perPage: Int = 30
    ): Result<List<SeoEvent>> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = unwrapResponse(
                service.getPublicEvents(
                    query = query?.takeIf { it.isNotBlank() },
                    category = category?.takeIf { it.isNotBlank() },
                    accessType = accessType?.takeIf { it.isNotBlank() },
                    isOutdoor = isOutdoor,
                    sort = sort,
                    page = page,
                    perPage = perPage
                ),
                "/events"
            )
            val root = JSONObject(raw)
            val dataArray = root.optJSONArray("data") ?: JSONArray()
            val list = mutableListOf<SeoEvent>()
            for (i in 0 until dataArray.length()) {
                list += parseEventToSeoEvent(dataArray.getJSONObject(i))
            }
            list
        }
    }

    suspend fun fetchEventById(eventId: String): Result<SeoEvent> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = unwrapResponse(service.getEventById(eventId), "/events/$eventId")
            parseEventToSeoEvent(JSONObject(raw))
        }
    }

    // -------------------------------------------------------------------------
    // 3. Interests & Bookings (/events/{id}/interest, /events/{id}/book, /me/tickets, /me/interests)
    // -------------------------------------------------------------------------

    suspend fun addInterest(eventId: String): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = unwrapResponse(service.addInterest(eventId, "{}".asJsonBody()), "/events/$eventId/interest")
            val json = JSONObject(raw)
            json.optInt("interested_count", 1)
        }
    }

    suspend fun removeInterest(eventId: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            unwrapResponse(service.removeInterest(eventId), "/events/$eventId/interest")
            Unit
        }
    }

    suspend fun bookEvent(
        eventId: String,
        isPaid: Boolean = false,
        paymentMethod: String = "simulated_card"
    ): Result<BookedTicket> = withContext(Dispatchers.IO) {
        runCatching {
            val body = if (isPaid) {
                JSONObject().put("payment_method", paymentMethod).toString()
            } else {
                "{}"
            }
            val raw = unwrapResponse(service.bookEvent(eventId, body.asJsonBody()), "/events/$eventId/book")
            parseTicketView(JSONObject(raw), index = 0)
        }
    }

    suspend fun fetchMyTickets(): Result<List<BookedTicket>> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = unwrapResponse(service.getMyTickets(page = 1, perPage = 30), "/me/tickets")
            val root = JSONObject(raw)
            val arr = root.optJSONArray("data") ?: JSONArray()
            val tickets = mutableListOf<BookedTicket>()
            for (i in 0 until arr.length()) {
                tickets += parseTicketView(arr.getJSONObject(i), index = i)
            }
            tickets
        }
    }

    suspend fun fetchMyInterests(): Result<List<SeoEvent>> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = unwrapResponse(service.getMyInterests(), "/me/interests")
            val arr = JSONArray(raw)
            val list = mutableListOf<SeoEvent>()
            for (i in 0 until arr.length()) {
                list += parseEventToSeoEvent(arr.getJSONObject(i))
            }
            list
        }
    }

    suspend fun fetchTicketQr(ticketId: String): Result<ApiQrPayloadDto> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = unwrapResponse(service.getTicketQr(ticketId), "/me/tickets/$ticketId/qr")
            val json = JSONObject(raw)
            ApiQrPayloadDto(
                ticketId = json.optString("ticket_id", ticketId),
                ticketSecret = json.optString("ticket_secret", ""),
                algorithm = json.optString("algorithm", "HMAC-SHA256"),
                periodSecs = json.optLong("period_secs", 15L),
                counter = json.optLong("counter", 0L),
                token = json.optString("token", ""),
                expiresAt = json.optString("expires_at", "")
            )
        }
    }

    /**
     * Generates the rotating HMAC-SHA256 QR token per the Dynamic QR protocol in API.md:
     * counter = floor(unix_time_seconds / period_secs)
     * hmac    = HMAC-SHA256(key = UTF8(ticket_secret), message = ASCII(decimal(counter)))
     * token   = "<ticket_id>:<lowercase hex hmac>"
     */
    fun generateDynamicQrToken(
        ticketId: String,
        ticketSecret: String,
        periodSecs: Long = 15L,
        unixTimeSeconds: Long = System.currentTimeMillis() / 1000L
    ): String {
        if (ticketSecret.isBlank()) return "$ticketId:demo"
        val counter = unixTimeSeconds / periodSecs.coerceAtLeast(1L)
        val mac = Mac.getInstance("HmacSHA256")
        val keySpec = SecretKeySpec(ticketSecret.toByteArray(Charsets.UTF_8), "HmacSHA256")
        mac.init(keySpec)
        val digest = mac.doFinal(counter.toString().toByteArray(Charsets.US_ASCII))
        val hex = digest.joinToString("") { "%02x".format(it) }
        return "$ticketId:$hex"
    }

    // -------------------------------------------------------------------------
    // 4. Profile & Notifications (/me, /me/notifications)
    // -------------------------------------------------------------------------

    suspend fun fetchNotifications(): Result<List<ApiNotificationDto>> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = unwrapResponse(service.getNotifications(limit = 50), "/me/notifications")
            val root = JSONObject(raw)
            val arr = root.optJSONArray("data") ?: JSONArray()
            val list = mutableListOf<ApiNotificationDto>()
            for (i in 0 until arr.length()) {
                val item = arr.getJSONObject(i)
                list += ApiNotificationDto(
                    id = item.optString("id"),
                    eventId = item.optString("event_id").takeIf { it.isNotBlank() && it != "null" },
                    type = item.optString("type", "general"),
                    title = item.optString("title", "Notification"),
                    body = item.optString("body", ""),
                    isRead = item.optBoolean("is_read", false),
                    createdAt = item.optString("created_at", "")
                )
            }
            list
        }
    }

    suspend fun markNotificationRead(notificationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            unwrapResponse(
                service.markNotificationRead(notificationId, "{}".asJsonBody()),
                "/me/notifications/$notificationId/read"
            )
            Unit
        }
    }

    // -------------------------------------------------------------------------
    // 5. Organizer Endpoints (/organizer/profile, /organizer/events, /organizer/staff, /admin/organizers)
    // -------------------------------------------------------------------------

    suspend fun fetchOrganizerProfile(): Result<ApiOrganizerProfileDto> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = unwrapResponse(service.getOrganizerProfile(), "/organizer/profile")
            parseOrganizerProfile(JSONObject(raw))
        }
    }

    suspend fun updateOrganizerProfile(
        name: String,
        certificationNumber: String,
        supportEmail: String,
        supportPhone: String
    ): Result<ApiOrganizerProfileDto> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().apply {
                put("name", name)
                put("certification_number", certificationNumber)
                put("support_email", supportEmail)
                put("support_phone", supportPhone)
            }
            val raw = unwrapResponse(
                service.updateOrganizerProfile(body.toString().asJsonBody()),
                "/organizer/profile"
            )
            parseOrganizerProfile(JSONObject(raw))
        }
    }

    suspend fun fetchOrganizerEvents(allPages: Boolean = false): Result<List<OrganizerEventItem>> = withContext(Dispatchers.IO) {
        runCatching {
            val list = mutableListOf<OrganizerEventItem>()
            var page = 1
            do {
                val raw = unwrapResponse(
                    service.getOrganizerEvents(page = page, perPage = 50),
                    "/organizer/events"
                )
                val root = JSONObject(raw)
                val events = root.getJSONArray("data")
                for (i in 0 until events.length()) {
                    list += parseEventToOrganizerItem(events.getJSONObject(i))
                }
                val totalPages = root.optJSONObject("meta")?.optInt("total_pages", 1) ?: 1
                page++
            } while (allPages && page <= totalPages)
            list.distinctBy { it.id }
        }
    }

    suspend fun createOrganizerEvent(item: OrganizerEventItem): Result<OrganizerEventItem> = withContext(Dispatchers.IO) {
        runCatching {
            val accessTypeApi = when {
                item.admissionMode.equals("Free Open", ignoreCase = true) -> "free_open"
                item.admissionMode.equals("Paid Ticket", ignoreCase = true) -> "paid"
                else -> "free_booking"
            }
            val body = JSONObject().apply {
                put("title", item.title)
                put("description", item.shortSubtitle.ifBlank { "Created from SeoRoute Organizer Studio" })
                put("category", "Festival")
                putEventSchedule(item)
                val venue = item.venueName.trim()
                require(venue.isNotEmpty()) { "Enter a venue name." }
                put("venue_name", venue)
                put("is_outdoor", item.isOutdoor)
                put("access_type", accessTypeApi)
                if (accessTypeApi == "free_open") {
                    put("price", 0)
                    put("capacity", JSONObject.NULL)
                } else if (accessTypeApi == "paid") {
                    put("price", 15000)
                    put("capacity", item.capacityLimit.coerceAtLeast(1))
                } else {
                    put("price", 0)
                    put("capacity", item.capacityLimit.coerceAtLeast(1))
                }
            }
            val raw = unwrapResponse(
                service.createOrganizerEvent(body.toString().asJsonBody()),
                "/organizer/events"
            )
            parseEventToOrganizerItem(JSONObject(raw))
        }
    }

    suspend fun updateOrganizerEvent(item: OrganizerEventItem): Result<OrganizerEventItem> = withContext(Dispatchers.IO) {
        runCatching {
            if (item.status == OrganizerEventStatus.CANCELLED) {
                return@runCatching cancelOrganizerEvent(item.id, "Cancelled by organizer").getOrThrow()
            }
            val body = JSONObject().apply {
                put("title", item.title)
                put("description", item.shortSubtitle)
                put("venue_name", item.venueName)
                put("is_outdoor", item.isOutdoor)
                putEventSchedule(item)
                put("status", if (item.status == OrganizerEventStatus.DRAFT) "postponed" else "published")
                if (!item.admissionMode.equals("Free Open", ignoreCase = true)) {
                    put("capacity", item.capacityLimit.coerceAtLeast(1))
                }
            }
            val raw = unwrapResponse(
                service.updateOrganizerEvent(item.id, body.toString().asJsonBody()),
                "/organizer/events/${item.id}"
            )
            parseEventToOrganizerItem(JSONObject(raw))
        }
    }

    private fun JSONObject.putEventSchedule(item: OrganizerEventItem) {
        val dates = item.dateRangeText.split(" - ", limit = 2)
        require(!item.isDateRange || dates.size == 2) { "Enter both dates for a date range." }
        val dateFormat = DateTimeFormatter.ofPattern("MMM d, uuuu", Locale.ENGLISH)
            .withResolverStyle(java.time.format.ResolverStyle.STRICT)
        val timeFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
            .withResolverStyle(java.time.format.ResolverStyle.STRICT)
        val (start, end) = try {
            val startDate = java.time.LocalDate.parse(dates.first().trim(), dateFormat)
            val endDate = if (item.isDateRange) java.time.LocalDate.parse(dates.last().trim(), dateFormat) else startDate
            startDate.atTime(java.time.LocalTime.parse(item.startTime.trim(), timeFormat)).atZone(SEOUL_ZONE).toInstant() to
                endDate.atTime(java.time.LocalTime.parse(item.endTime.trim(), timeFormat)).atZone(SEOUL_ZONE).toInstant()
        } catch (error: java.time.format.DateTimeParseException) {
            throw IllegalArgumentException("Use a date like Dec 10, 2026 and a time like 02:00 PM.", error)
        }
        require(!end.isBefore(start)) { "The event must end at or after its start time." }
        put("start_time", start.toString())
        put("end_time", end.toString())
    }

    suspend fun cancelOrganizerEvent(
        eventId: String,
        reason: String = "Cancelled via Organizer Studio"
    ): Result<OrganizerEventItem> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().put("reason", reason).toString()
            val raw = unwrapResponse(
                service.cancelOrganizerEvent(eventId, body.asJsonBody()),
                "/organizer/events/$eventId/cancel"
            )
            parseEventToOrganizerItem(JSONObject(raw))
        }
    }

    suspend fun fetchOrganizerEventAttendees(eventId: String): Result<List<ApiAttendeeRowDto>> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = unwrapResponse(
                service.getOrganizerEventAttendees(eventId = eventId, page = 1, perPage = 50),
                "/organizer/events/$eventId/attendees"
            )
            val root = JSONObject(raw)
            val arr = root.optJSONArray("data") ?: JSONArray()
            val list = mutableListOf<ApiAttendeeRowDto>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list += ApiAttendeeRowDto(
                    ticketId = obj.optString("ticket_id"),
                    userId = obj.optString("user_id"),
                    name = obj.optString("name", "Guest Attendee"),
                    email = obj.optString("email", ""),
                    status = obj.optString("status", "registered"),
                    bookedAt = obj.optString("booked_at", ""),
                    scannedAt = obj.optString("scanned_at").takeIf { it.isNotBlank() && it != "null" }
                )
            }
            list
        }
    }

    suspend fun fetchOrganizerStaff(): Result<List<GateStaffMember>> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = unwrapResponse(service.getOrganizerStaff(), "/organizer/staff")
            val arr = JSONArray(raw)
            val list = mutableListOf<GateStaffMember>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val role = obj.optString("role", "ticketing")
                val name = obj.optString("name", "Gate Staff ${i + 1}")
                val email = obj.optString("email", "gate${i + 1}@seoulcity.internal")
                val isActive = obj.optBoolean("is_active", true)
                val lastLogin = obj.optString("last_login_at")
                val hasLoggedIn = lastLogin.isNotBlank() && lastLogin != "null"
                val gateLetter = ('A' + i.coerceAtMost(25))
                list += GateStaffMember(
                    id = obj.optString("id", "staff_$i"),
                    gateTitle = if (role == "organizer") "$name (Organizer)" else "Gate $gateLetter - $name",
                    internalEmail = email,
                    isReady = isActive && (hasLoggedIn || role == "organizer"),
                    statusDetail = if (hasLoggedIn) "• Active scanner" else "• Standby mode",
                    role = role
                )
            }
            list
        }
    }

    suspend fun addOrganizerStaff(email: String): Result<GateStaffMember> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().put("email", email).toString()
            val raw = unwrapResponse(
                service.addOrganizerStaff(body.asJsonBody()),
                "/organizer/staff"
            )
            val obj = JSONObject(raw)
            val name = obj.optString("name", "Gate Staff")
            GateStaffMember(
                id = obj.optString("id", email),
                gateTitle = "Gate Scanner - $name",
                internalEmail = obj.optString("email", email),
                isReady = true,
                statusDetail = "• Ready for scanning",
                role = obj.optString("role", "ticketing")
            )
        }
    }

    /**
     * DELETE /api/v1/organizer/staff/{user_id}
     * Removes a ticketing staff member from the organizer (reverts role back to attendee). Returns 204.
     */
    suspend fun deleteOrganizerStaff(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            unwrapResponse(service.deleteOrganizerStaff(userId), "/organizer/staff/$userId")
            Unit
        }
    }

    // -------------------------------------------------------------------------
    // 6. Ticketing & Event-Day Gate Endpoints (usable by both `ticketing` & `organizer` roles)
    // -------------------------------------------------------------------------

    suspend fun fetchTicketingEvents(): Result<List<OrganizerEventItem>> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = unwrapResponse(service.getTicketingEvents(), "/ticketing/events")
            val arr = JSONArray(raw)
            val list = mutableListOf<OrganizerEventItem>()
            for (i in 0 until arr.length()) {
                list += parseEventToOrganizerItem(arr.getJSONObject(i))
            }
            list
        }
    }

    suspend fun fetchTicketingManifest(eventId: String): Result<List<ApiManifestTicketDto>> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = unwrapResponse(service.getTicketingManifest(eventId), "/ticketing/events/$eventId/tickets")
            val root = JSONObject(raw)
            val ticketsArr = root.optJSONArray("tickets") ?: JSONArray()
            val list = mutableListOf<ApiManifestTicketDto>()
            for (i in 0 until ticketsArr.length()) {
                val obj = ticketsArr.getJSONObject(i)
                list += ApiManifestTicketDto(
                    id = obj.optString("id"),
                    ticketSecret = obj.optString("ticket_secret"),
                    status = obj.optString("status", "registered"),
                    scannedAt = obj.optString("scanned_at").takeIf { it.isNotBlank() && it != "null" }
                )
            }
            list
        }
    }

    suspend fun fetchTicketingStats(eventId: String): Result<ApiTicketingStatsDto> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = unwrapResponse(service.getTicketingStats(eventId), "/ticketing/events/$eventId/stats")
            val obj = JSONObject(raw)
            ApiTicketingStatsDto(
                eventId = obj.optString("event_id", eventId),
                checkedIn = obj.optInt("checked_in", 0),
                notYet = obj.optInt("not_yet", 0),
                total = obj.optInt("total", 0),
                serverTime = obj.optString("server_time", "")
            )
        }
    }

    suspend fun checkInTicketOnline(
        eventId: String,
        qrToken: String,
        deviceId: String = "android-seoroute-gate-scanner"
    ): Result<ApiScanResultDto> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().apply {
                put("qr_token", qrToken)
                put("device_id", deviceId)
            }
            val raw = unwrapResponse(
                service.checkInTicketOnline(eventId, body.toString().asJsonBody()),
                "/ticketing/events/$eventId/check-in"
            )
            val obj = JSONObject(raw)
            ApiScanResultDto(
                id = obj.optString("id"),
                clientScanId = obj.optString("client_scan_id"),
                ticketId = obj.optString("ticket_id"),
                eventId = obj.optString("event_id", eventId),
                deviceId = obj.optString("device_id", deviceId),
                result = obj.optString("result", "unknown"),
                scannedAt = obj.optString("scanned_at", "")
            )
        }
    }

    suspend fun fetchAdminStats(): Result<JSONObject> = withContext(Dispatchers.IO) {
        runCatching { JSONObject(unwrapResponse(service.getAdminStats(), "/admin/stats")) }
    }

    suspend fun fetchAdminOrganizers(): Result<List<ApiOrganizerProfileDto>> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = unwrapResponse(service.getAdminOrganizers(), "/admin/organizers")
            val arr = JSONArray(raw)
            val list = mutableListOf<ApiOrganizerProfileDto>()
            for (i in 0 until arr.length()) {
                list += parseOrganizerProfile(arr.getJSONObject(i))
            }
            list
        }
    }

    suspend fun createAdminOrganizer(
        name: String,
        certificationNumber: String,
        supportEmail: String,
        supportPhone: String
    ): Result<ApiOrganizerProfileDto> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().apply {
                put("name", name)
                put("certification_number", certificationNumber)
                put("support_email", supportEmail)
                put("support_phone", supportPhone)
            }
            val raw = unwrapResponse(
                service.createAdminOrganizer(body.toString().asJsonBody()),
                "/admin/organizers"
            )
            parseOrganizerProfile(JSONObject(raw))
        }
    }

    // -------------------------------------------------------------------------
    // JSON Mapping Helpers (Backend DTO -> UI Models)
    // -------------------------------------------------------------------------

    private fun parseSessionResponse(json: JSONObject): ApiSessionResponse {
        val userObj = json.getJSONObject("user")
        return ApiSessionResponse(
            accessToken = json.getString("access_token"),
            tokenType = json.optString("token_type", "Bearer"),
            expiresIn = json.optLong("expires_in", 86400L),
            user = ApiUserDto(
                id = userObj.optString("id"),
                email = userObj.optString("email"),
                name = userObj.optString("name"),
                photoUrl = userObj.optString("photo_url").takeIf { it.isNotBlank() && it != "null" },
                role = userObj.optString("role", "attendee"),
                organizerId = userObj.optString("organizer_id").takeIf { it.isNotBlank() && it != "null" },
                isActive = userObj.optBoolean("is_active", true)
            )
        )
    }

    private fun parseOrganizerProfile(json: JSONObject): ApiOrganizerProfileDto {
        return ApiOrganizerProfileDto(
            id = json.optString("id"),
            name = json.optString("name", "Hangang Live Productions"),
            certificationNumber = json.optString("certification_number", "104-83-02941"),
            supportEmail = json.optString("support_email", "support@seoulculture.go.kr"),
            supportPhone = json.optString("support_phone", "+82 2-120-7700"),
            isSystem = json.optBoolean("is_system", false)
        )
    }

    private fun parseEventToSeoEvent(obj: JSONObject): SeoEvent {
        val id = obj.optString("id")
        val title = obj.optString("title", "Seoul Cultural Event")
        val description = obj.optString("description", "Discover music, art and life in Seoul.")
        val categoryStr = obj.optString("category", "Festival")
        val venueName = obj.optString("venue_name", "Seoul Plaza")
        val district = if (obj.isNull("district")) "" else obj.optString("district").trim()
        val isOutdoor = obj.optBoolean("is_outdoor", true)
        val accessType = obj.optString("access_type", "free_open")
        val price = obj.optInt("price", 0)
        val capacity = if (obj.isNull("capacity")) null else obj.optInt("capacity")
        val remaining = if (obj.isNull("remaining_capacity")) null else obj.optInt("remaining_capacity")
        val organizerName = obj.optString("organizer_name", "Seoul Culture Team")
        val startTimeIso = obj.optString("start_time")
        val endTimeIso = obj.optString("end_time")

        val startZdt = parseSeoulZdt(startTimeIso)
        val endZdt = parseSeoulZdt(endTimeIso)

        val shortDate = startZdt?.format(DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)) ?: "Nov 17"
        val fullDate = startZdt?.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ENGLISH))
            ?: "Tuesday, 17 November 2026"
        val bookingShortDate = startZdt?.format(DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.ENGLISH))
            ?: "Tuesday, Nov 17"
        val startClock = startZdt?.format(DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)) ?: "2:00 PM"
        val endClock = endZdt?.format(DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)) ?: "6:00 PM"
        val ticketDateTime = startZdt?.format(DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a", Locale.ENGLISH))
            ?: "Nov 17, 2026 · 2:00 PM"

        val vibeCategory = when {
            categoryStr.contains("Concert", ignoreCase = true) ||
                categoryStr.contains("Music", ignoreCase = true) -> VibeCategory.MUSIC
            categoryStr.contains("Exhibition", ignoreCase = true) ||
                categoryStr.contains("Art", ignoreCase = true) -> VibeCategory.ART
            categoryStr.contains("Sport", ignoreCase = true) ||
                categoryStr.contains("Outdoor", ignoreCase = true) -> VibeCategory.OUTDOORS
            else -> VibeCategory.FESTIVAL
        }

        val imageRes = pickDrawableForEvent(title, categoryStr, isOutdoor)

        val purpleTag = { label: String -> EventTagStyle(label, SeoPrimary, SeoPrimaryLight) }
        val greenTag = { label: String -> EventTagStyle(label, SeoGreenText, SeoGreenBg) }
        val grayTag = { label: String -> EventTagStyle(label, SeoTextSecondary, SeoFilterChipBg) }
        val bookingListTag = EventTagStyle("Booking", SeoBookingText, SeoBookingBg)

        val listBadge = when (accessType) {
            "free_booking" -> bookingListTag
            "paid" -> purpleTag("₩$price")
            else -> purpleTag("Free")
        }
        val savedBadge = when (accessType) {
            "free_booking" -> purpleTag("Booking")
            "paid" -> purpleTag("₩$price")
            else -> purpleTag("Free")
        }
        val admissionDisplay = when (accessType) {
            "free_booking" -> "Booking"
            "paid" -> "Paid"
            else -> "Free open"
        }
        val detailTags = buildList {
            add(purpleTag(vibeCategory.label))
            when (accessType) {
                "free_booking" -> add(greenTag("Free booking"))
                "paid" -> add(greenTag("₩$price"))
                else -> add(greenTag("Free open"))
            }
            add(if (isOutdoor) purpleTag("Outdoor") else grayTag("Indoor"))
        }

        val isManaged = accessType == "free_booking" || accessType == "paid"
        val reservedSpots = if (capacity != null && remaining != null) {
            (capacity - remaining).coerceAtLeast(0)
        } else null

        val monogram = organizerName
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
            .ifEmpty { "SR" }

        return SeoEvent(
            id = id,
            title = title,
            subtitle = description,
            shortDateLocation = if (district.isBlank()) "$shortDate · $venueName" else "$shortDate · $district",
            fullDate = fullDate,
            timeRange = "$startClock – $endClock",
            venueName = venueName,
            fullLocation = if (district.isNotBlank()) "$venueName, $district" else venueName,
            districtCaption = if (district.isBlank()) "Illustrative map" else "$district · Illustrative map",
            imageRes = imageRes,
            listBadge = listBadge,
            savedListBadge = savedBadge,
            detailTags = detailTags,
            category = vibeCategory,
            isFree = price == 0,
            isOutdoor = isOutdoor,
            admissionType = admissionDisplay,
            detailSectionTitle = if (isManaged) "Event information" else "About this event",
            descriptionLines = description.chunked(46).ifEmpty { listOf(description) },
            detailType = if (isManaged) EventDetailType.MANAGED else EventDetailType.PUBLIC,
            reservedSpots = reservedSpots,
            totalSpots = capacity,
            organizerMonogram = monogram,
            organizerName = organizerName,
            organizerSubtitle = "$categoryStr · Seoul community",
            ctaButtonText = when (accessType) {
                "free_booking" -> "Reserve spot"
                "paid" -> "Book ticket (₩$price)"
                else -> "Interested"
            },
            mapHighlightColor = if (isManaged) SeoMapVenuePurple else SeoMapPlazaGreen,
            bookingShortDate = bookingShortDate,
            bookingShortTime = startClock,
            ticketDateTime = ticketDateTime,
            bookingCode = "BOOKING #${id.take(8).uppercase()}"
        )
    }

    private fun parseTicketView(obj: JSONObject, index: Int): BookedTicket {
        val ticketId = obj.optString("id")
        val status = obj.optString("status", "registered").uppercase()
        val eventObj = obj.optJSONObject("event") ?: JSONObject()
        val seoEvent = parseEventToSeoEvent(eventObj).copy(
            bookingCode = "TICKET #${ticketId.take(8).uppercase()}"
        )
        val statusLabel = when (status) {
            "REGISTERED" -> "CONFIRMED"
            "ATTENDED" -> "ATTENDED"
            "CANCELLED" -> "CANCELLED"
            else -> status
        }
        return BookedTicket(
            id = ticketId,
            event = seoEvent,
            statusText = statusLabel,
            subtitleText = "${seoEvent.bookingShortDate} · ${seoEvent.venueName}",
            isHeroLayout = index == 0
        )
    }

    private fun parseEventToOrganizerItem(obj: JSONObject): OrganizerEventItem {
        val id = obj.optString("id")
        val title = obj.optString("title", "Untitled Event")
        val description = obj.optString("description", "")
        val categoryStr = obj.optString("category", "Festival")
        val venueName = obj.optString("venue_name", "Seoul Plaza")
        val isOutdoor = obj.optBoolean("is_outdoor", true)
        val accessType = obj.optString("access_type", "free_booking")
        val capacity = if (obj.isNull("capacity")) 50 else obj.optInt("capacity", 50)
        val remaining = if (obj.isNull("remaining_capacity")) capacity else obj.optInt("remaining_capacity", capacity)
        val booked = (capacity - remaining).coerceAtLeast(0)
        val statusRaw = obj.optString("status", "published")

        val startZdt = parseSeoulZdt(obj.optString("start_time"))
        val endZdt = parseSeoulZdt(obj.optString("end_time"))
        val dateLabel = startZdt?.format(DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)) ?: "Dec 01"
        val fullDateRange = if (startZdt != null && endZdt != null) {
            val s = startZdt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH))
            val e = endZdt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH))
            if (s == e) s else "$s - $e"
        } else {
            "Dec 01, 2026"
        }
        val startClock = startZdt?.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)) ?: "02:00 PM"
        val endClock = endZdt?.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)) ?: "06:00 PM"

        val orgStatus = when (statusRaw.lowercase()) {
            "cancelled" -> OrganizerEventStatus.CANCELLED
            "postponed", "draft" -> OrganizerEventStatus.DRAFT
            else -> OrganizerEventStatus.PUBLISHED
        }
        val admissionLabel = when (accessType) {
            "free_open" -> "Free Open"
            "paid" -> "Paid Ticket"
            else -> "Free Booking"
        }

        return OrganizerEventItem(
            id = id,
            title = title,
            shortSubtitle = description,
            dateLabel = dateLabel,
            dateRangeText = fullDateRange,
            isDateRange = fullDateRange.contains("-"),
            startTime = startClock,
            endTime = endClock,
            venueName = venueName,
            isOutdoor = isOutdoor,
            admissionMode = admissionLabel,
            bookedCount = booked,
            capacityLimit = capacity,
            status = orgStatus,
            thumbnailRes = pickDrawableForEvent(title, categoryStr, isOutdoor)
        )
    }

    private fun parseSeoulZdt(isoString: String?): ZonedDateTime? {
        if (isoString.isNullOrBlank() || isoString == "null") return null
        return runCatching {
            Instant.parse(isoString).atZone(SEOUL_ZONE)
        }.getOrNull()
    }

    private fun pickDrawableForEvent(title: String, category: String, isOutdoor: Boolean): Int {
        return when {
            title.contains("Hangang", ignoreCase = true) ||
                title.contains("Banpo", ignoreCase = true) ||
                title.contains("River", ignoreCase = true) -> R.drawable.event_han_river_walk
            title.contains("Museum", ignoreCase = true) ||
                category.contains("Exhibition", ignoreCase = true) -> R.drawable.event_art_museum
            title.contains("Indie", ignoreCase = true) ||
                title.contains("Meetup", ignoreCase = true) -> R.drawable.event_future_seoul
            title.contains("Garden", ignoreCase = true) ||
                title.contains("Picnic", ignoreCase = true) -> R.drawable.event_autumn_garden
            isOutdoor -> R.drawable.event_seoul_culture_night
            else -> R.drawable.event_future_seoul
        }
    }
}
