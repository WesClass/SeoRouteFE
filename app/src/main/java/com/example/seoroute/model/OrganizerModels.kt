package com.example.seoroute.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.example.seoroute.R
import com.example.seoroute.network.ApiManifestTicketDto
import com.example.seoroute.network.ApiOrganizerProfileDto
import com.example.seoroute.network.ApiTicketingStatsDto
import com.example.seoroute.network.SeoRouteApiClient
import com.example.seoroute.ui.theme.SeoFilterChipBg
import com.example.seoroute.ui.theme.SeoGreenBg
import com.example.seoroute.ui.theme.SeoGreenText
import com.example.seoroute.ui.theme.SeoTextSecondary

enum class OrganizerNavTab(val label: String, @DrawableRes val iconRes: Int) {
    OVERVIEW("Overview", R.drawable.ic_org_nav_overview),
    EVENTS("Events", R.drawable.ic_org_nav_events),
    PEOPLE("People", R.drawable.ic_org_nav_people),
    PROFILE("Profile", R.drawable.ic_nav_profile)
}

enum class AdminNavTab(val label: String, @DrawableRes val iconRes: Int) {
    OVERVIEW("Overview", R.drawable.ic_org_nav_overview),
    EVENTS("Events", R.drawable.ic_org_nav_events),
    PEOPLE("People", R.drawable.ic_org_nav_people)
}

data class AdminAgencyItem(
    val id: String,
    val monogram: String,
    val name: String,
    val supportEmail: String,
    val activeEventsCount: Int,
    val avatarBgColor: Color,
    val avatarBorderColor: Color,
    val avatarTextColor: Color,
    val badgeBgColor: Color,
    val badgeBorderColor: Color,
    val badgeTextColor: Color,
    val showDivider: Boolean = false
)

enum class OrganizerEventStatus(
    val badgeLabel: String,
    val editorLabel: String,
    val textColor: Color,
    val backgroundColor: Color
) {
    PUBLISHED(
        badgeLabel = "PUBLISHED",
        editorLabel = "Active",
        textColor = SeoGreenText,
        backgroundColor = SeoGreenBg
    ),
    DRAFT(
        badgeLabel = "DRAFT",
        editorLabel = "In Draft",
        textColor = SeoTextSecondary,
        backgroundColor = SeoFilterChipBg
    ),
    CANCELLED(
        badgeLabel = "CANCELLED",
        editorLabel = "Cancelled",
        textColor = Color(0xFF924549),
        backgroundColor = Color(0xFFFF96B4)
    )
}

data class OrganizerEventItem(
    val id: String,
    val title: String,
    val shortSubtitle: String,
    val dateLabel: String,
    val dateRangeText: String,
    val isDateRange: Boolean,
    val startTime: String,
    val endTime: String,
    val venueName: String,
    val isOutdoor: Boolean,
    val admissionMode: String, // "Free Open", "Free Booking", "Paid Ticket"
    val bookedCount: Int,
    val capacityLimit: Int,
    val status: OrganizerEventStatus,
    @DrawableRes val thumbnailRes: Int,
    @DrawableRes val coverPreviewRes: Int = R.drawable.org_cover_preview
) {
    val summaryMetaText: String
        get() = "$dateLabel · $bookedCount/$capacityLimit booked"
}

data class GateStaffMember(
    val id: String,
    val gateTitle: String,
    val internalEmail: String,
    val isReady: Boolean,
    val statusDetail: String,
    val role: String = "ticketing"
)

data class AttendeeCheckInItem(
    val id: String,
    val name: String,
    val isCheckedIn: Boolean,
    val avatarColorIsWarm: Boolean = false,
    val ticketSecret: String = ""
) {
    val initial: String
        get() = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "G"
    val statusText: String
        get() = if (isCheckedIn) "Checked in" else "Not arrived"
}

object OrganizerRepository {
    val dashboardEvents = listOf(
        OrganizerEventItem(
            id = "org_future_seoul",
            title = "Future Seoul Meetup",
            shortSubtitle = "Technology, design and local creators.",
            dateLabel = "Nov 17",
            dateRangeText = "Nov 17, 2026 - Nov 20, 2026",
            isDateRange = true,
            startTime = "02:00 PM",
            endTime = "06:30 PM",
            venueName = "Mapo Startup Center",
            isOutdoor = true,
            admissionMode = "Free Booking",
            bookedCount = 18,
            capacityLimit = 25,
            status = OrganizerEventStatus.PUBLISHED,
            thumbnailRes = R.drawable.event_future_seoul
        ),
        OrganizerEventItem(
            id = "org_creative_draft_dash",
            title = "Creative City Social",
            shortSubtitle = "Networking night for local makers and artists.",
            dateLabel = "Nov 29",
            dateRangeText = "Nov 29, 2026",
            isDateRange = false,
            startTime = "06:00 PM",
            endTime = "09:30 PM",
            venueName = "Seoul Plaza",
            isOutdoor = true,
            admissionMode = "Free Booking",
            bookedCount = 0,
            capacityLimit = 50,
            status = OrganizerEventStatus.DRAFT,
            thumbnailRes = R.drawable.event_seoul_culture_night
        )
    )

    val allOrganizerEvents = mutableStateListOf(
        OrganizerEventItem(
            id = "org_future_seoul",
            title = "Future Seoul Meetup",
            shortSubtitle = "Technology, design and local creators.",
            dateLabel = "Nov 17",
            dateRangeText = "Nov 17, 2026 - Nov 20, 2026",
            isDateRange = true,
            startTime = "02:00 PM",
            endTime = "06:30 PM",
            venueName = "Mapo Startup Center",
            isOutdoor = true,
            admissionMode = "Free Booking",
            bookedCount = 18,
            capacityLimit = 25,
            status = OrganizerEventStatus.PUBLISHED,
            thumbnailRes = R.drawable.event_future_seoul
        ),
        OrganizerEventItem(
            id = "org_creative_cancelled",
            title = "Creative City Social",
            shortSubtitle = "Outdoor music & social gathering at Seoul Plaza.",
            dateLabel = "Nov 30",
            dateRangeText = "Nov 30, 2026",
            isDateRange = false,
            startTime = "05:00 PM",
            endTime = "09:00 PM",
            venueName = "Seoul Plaza",
            isOutdoor = true,
            admissionMode = "Free Booking",
            bookedCount = 27,
            capacityLimit = 100,
            status = OrganizerEventStatus.CANCELLED,
            thumbnailRes = R.drawable.event_seoul_culture_night
        ),
        OrganizerEventItem(
            id = "org_creative_draft_1",
            title = "Creative City Social",
            shortSubtitle = "Networking night for local makers and artists.",
            dateLabel = "Nov 29",
            dateRangeText = "Nov 29, 2026",
            isDateRange = false,
            startTime = "06:00 PM",
            endTime = "09:30 PM",
            venueName = "Seoul Plaza",
            isOutdoor = true,
            admissionMode = "Free Booking",
            bookedCount = 0,
            capacityLimit = 50,
            status = OrganizerEventStatus.DRAFT,
            thumbnailRes = R.drawable.event_seoul_culture_night
        ),
        OrganizerEventItem(
            id = "org_creative_draft_2",
            title = "Creative City Social",
            shortSubtitle = "Networking night for local makers and artists.",
            dateLabel = "Nov 29",
            dateRangeText = "Nov 29, 2026",
            isDateRange = false,
            startTime = "06:00 PM",
            endTime = "09:30 PM",
            venueName = "Seoul Plaza",
            isOutdoor = true,
            admissionMode = "Free Booking",
            bookedCount = 0,
            capacityLimit = 50,
            status = OrganizerEventStatus.DRAFT,
            thumbnailRes = R.drawable.event_seoul_culture_night
        ),
        OrganizerEventItem(
            id = "org_creative_draft_3",
            title = "Creative City Social",
            shortSubtitle = "Networking night for local makers and artists.",
            dateLabel = "Nov 29",
            dateRangeText = "Nov 29, 2026",
            isDateRange = false,
            startTime = "06:00 PM",
            endTime = "09:30 PM",
            venueName = "Seoul Plaza",
            isOutdoor = true,
            admissionMode = "Free Booking",
            bookedCount = 0,
            capacityLimit = 50,
            status = OrganizerEventStatus.DRAFT,
            thumbnailRes = R.drawable.event_seoul_culture_night
        )
    )

    val gateStaffList = mutableStateListOf(
        GateStaffMember(
            id = "staff_gate_a",
            gateTitle = "Gate A - Entrance Scanner",
            internalEmail = "gate1@seoulcity.internal",
            isReady = true,
            statusDetail = "• 312 Scans completed"
        ),
        GateStaffMember(
            id = "staff_gate_b",
            gateTitle = "Gate B - VIP Check-in",
            internalEmail = "gate2@seoulcity.internal",
            isReady = false,
            statusDetail = "• Standby mode"
        )
    )

    val organizerProfile = mutableStateOf(
        ApiOrganizerProfileDto(
            id = "00000000-0000-0000-0000-000000000002",
            name = "Seoul Metropolitan Culture\nDivision",
            certificationNumber = "104-83-02941",
            supportEmail = "support@seoulculture.go.kr",
            supportPhone = "+82 2-120-7700",
            isSystem = false
        )
    )

    val allOrganizersList = mutableStateListOf<ApiOrganizerProfileDto>()
    val adminAgenciesList = mutableStateListOf(
        AdminAgencyItem(
            id = "agency_seoul_culture",
            monogram = "SC",
            name = "Seoul Metropolitan Culture Division",
            supportEmail = "support@seoulculture.go.kr",
            activeEventsCount = 14,
            avatarBgColor = Color(0xFFEEF2FF),
            avatarBorderColor = Color(0xFFE0E7FF),
            avatarTextColor = Color(0xFF4F46E5),
            badgeBgColor = Color(0xFFECFDF5),
            badgeBorderColor = Color(0xFFD1FAE5),
            badgeTextColor = Color(0xFF047857),
            showDivider = false
        ),
        AdminAgencyItem(
            id = "agency_mapo_hub",
            monogram = "MS",
            name = "Mapo Startup Hub",
            supportEmail = "admin@mapohub.kr",
            activeEventsCount = 6,
            avatarBgColor = Color(0xFFF0F9FF),
            avatarBorderColor = Color(0xFFE0F2FE),
            avatarTextColor = Color(0xFF0284C7),
            badgeBgColor = Color(0xFFF0F9FF),
            badgeBorderColor = Color(0xFFE0F2FE),
            badgeTextColor = Color(0xFF0369A1),
            showDivider = false
        ),
        AdminAgencyItem(
            id = "agency_jongno_heritage",
            monogram = "JH",
            name = "Jongno Heritage Foundation",
            supportEmail = "contact@jongno-heritage.org",
            activeEventsCount = 4,
            avatarBgColor = Color(0xFFFFFBEB),
            avatarBorderColor = Color(0xFFFEF3C7),
            avatarTextColor = Color(0xFFD97706),
            badgeBgColor = Color(0xFFFFFBEB),
            badgeBorderColor = Color(0xFFFEF3C7),
            badgeTextColor = Color(0xFFB45309),
            showDivider = true
        )
    )
    val ticketingEvents = mutableStateListOf<OrganizerEventItem>()
    val selectedTicketingEventIndex = mutableStateOf(0)
    val activeEventStats = mutableStateOf<ApiTicketingStatsDto?>(null)
    val activeEventManifest = mutableStateListOf<ApiManifestTicketDto>()

    val attendeesList = mutableStateListOf(
        AttendeeCheckInItem(
            id = "att_jin_park",
            name = "Jin Park",
            isCheckedIn = true,
            avatarColorIsWarm = false
        ),
        AttendeeCheckInItem(
            id = "att_sophie_kim",
            name = "Sophie Kim",
            isCheckedIn = true,
            avatarColorIsWarm = true
        ),
        AttendeeCheckInItem(
            id = "att_alex_lee",
            name = "Alex Lee",
            isCheckedIn = false,
            avatarColorIsWarm = false
        ),
        AttendeeCheckInItem(
            id = "att_minji_choi",
            name = "Minji Choi",
            isCheckedIn = false,
            avatarColorIsWarm = true
        ),
        AttendeeCheckInItem(
            id = "att_ryan_jung",
            name = "Ryan Jung",
            isCheckedIn = false,
            avatarColorIsWarm = false
        )
    )

    val lastScannedAttendee = mutableStateOf(
        AttendeeCheckInItem(
            id = "att_jin_park",
            name = "Jin Park",
            isCheckedIn = true,
            avatarColorIsWarm = false
        )
    )

    suspend fun syncFromBackend() {
        SeoRouteApiClient.fetchAdminOrganizers().onSuccess { orgs ->
            if (orgs.isNotEmpty()) {
                allOrganizersList.clear()
                allOrganizersList.addAll(orgs)
                orgs.forEachIndexed { idx, dto ->
                    if (adminAgenciesList.none { it.name.equals(dto.name, ignoreCase = true) || it.id == dto.id }) {
                        val monogram = dto.name.split(" ")
                            .filter { it.isNotBlank() }
                            .take(2)
                            .joinToString("") { it.first().uppercaseChar().toString() }
                            .ifEmpty { "EO" }
                        adminAgenciesList.add(
                            AdminAgencyItem(
                                id = dto.id,
                                monogram = monogram,
                                name = dto.name,
                                supportEmail = dto.supportEmail,
                                activeEventsCount = 4 + (idx * 2),
                                avatarBgColor = Color(0xFFEEF2FF),
                                avatarBorderColor = Color(0xFFE0E7FF),
                                avatarTextColor = Color(0xFF4F46E5),
                                badgeBgColor = Color(0xFFECFDF5),
                                badgeBorderColor = Color(0xFFD1FAE5),
                                badgeTextColor = Color(0xFF047857)
                            )
                        )
                    }
                }
            }
        }
        if (!SeoRouteApiClient.accessToken.isNullOrBlank()) {
            SeoRouteApiClient.fetchOrganizerProfile().onSuccess { profile ->
                organizerProfile.value = profile
            }
            SeoRouteApiClient.fetchOrganizerEvents().onSuccess { events ->
                if (events.isNotEmpty()) {
                    allOrganizerEvents.clear()
                    allOrganizerEvents.addAll(events)
                }
            }
            // Both `organizer` and `ticketing` roles can call `/api/v1/ticketing/events`
            SeoRouteApiClient.fetchTicketingEvents().onSuccess { tEvents ->
                if (tEvents.isNotEmpty()) {
                    ticketingEvents.clear()
                    ticketingEvents.addAll(tEvents)
                    if (allOrganizerEvents.isEmpty()) {
                        allOrganizerEvents.addAll(tEvents)
                    }
                    val firstId = tEvents.first().id
                    refreshTicketingManifestAndStats(firstId)
                }
            }
            SeoRouteApiClient.fetchOrganizerStaff().onSuccess { staff ->
                if (staff.isNotEmpty()) {
                    gateStaffList.clear()
                    gateStaffList.addAll(staff)
                }
            }
        }
    }

    suspend fun refreshTicketingManifestAndStats(eventId: String) {
        SeoRouteApiClient.fetchTicketingStats(eventId).onSuccess { stats ->
            activeEventStats.value = stats
        }
        SeoRouteApiClient.fetchTicketingManifest(eventId).onSuccess { manifest ->
            activeEventManifest.clear()
            activeEventManifest.addAll(manifest)
        }
        SeoRouteApiClient.fetchOrganizerEventAttendees(eventId).onSuccess { remoteAttendees ->
            if (remoteAttendees.isNotEmpty()) {
                attendeesList.clear()
                remoteAttendees.forEachIndexed { idx, row ->
                    val secret = activeEventManifest.firstOrNull { it.id == row.ticketId }?.ticketSecret.orEmpty()
                    attendeesList += AttendeeCheckInItem(
                        id = row.ticketId,
                        name = row.name,
                        isCheckedIn = row.status.equals("attended", ignoreCase = true) || row.scannedAt != null,
                        avatarColorIsWarm = idx % 2 == 1,
                        ticketSecret = secret
                    )
                }
            }
        }
    }

    fun getAvailableAssignedEvents(): List<OrganizerEventItem> {
        return ticketingEvents.ifEmpty { allOrganizerEvents }.ifEmpty { dashboardEvents }
    }

    fun getCurrentAssignedEvent(): OrganizerEventItem {
        val list = getAvailableAssignedEvents()
        val idx = selectedTicketingEventIndex.value.coerceIn(0, (list.size - 1).coerceAtLeast(0))
        return list[idx]
    }

    fun markAttendeeCheckedInLocal(attendeeId: String): AttendeeCheckInItem {
        val idx = attendeesList.indexOfFirst { it.id == attendeeId }
        val currentEvent = getCurrentAssignedEvent()

        // Increment checked-in counter in activeEventStats and booked/checked-in display
        val currentStats = activeEventStats.value
        if (currentStats != null) {
            val nextCheckedIn = (currentStats.checkedIn + 1).coerceAtMost(currentStats.total.coerceAtLeast(1))
            val nextNotYet = (currentStats.total - nextCheckedIn).coerceAtLeast(0)
            activeEventStats.value = currentStats.copy(
                checkedIn = nextCheckedIn,
                notYet = nextNotYet
            )
        } else {
            val checkedCount = (attendeesList.count { it.isCheckedIn } + 1).coerceAtLeast(9)
            val totalCap = currentEvent.capacityLimit.coerceAtLeast(18)
            activeEventStats.value = ApiTicketingStatsDto(
                eventId = currentEvent.id,
                checkedIn = checkedCount.coerceAtMost(totalCap),
                notYet = (totalCap - checkedCount).coerceAtLeast(0),
                total = totalCap,
                serverTime = ""
            )
        }

        return if (idx >= 0) {
            val updated = attendeesList[idx].copy(isCheckedIn = true)
            attendeesList[idx] = updated
            lastScannedAttendee.value = updated
            updated
        } else {
            val fallback = lastScannedAttendee.value.copy(isCheckedIn = true)
            lastScannedAttendee.value = fallback
            fallback
        }
    }

    fun createDefaultNewEvent(): OrganizerEventItem = OrganizerEventItem(
        id = "org_new_${allOrganizerEvents.size + 1}",
        title = "Future Seoul Meetup",
        shortSubtitle = "Technology, design and local creators.",
        dateLabel = "Nov 17",
        dateRangeText = "Nov 17, 2026 - Nov 20, 2026",
        isDateRange = true,
        startTime = "02:00 PM",
        endTime = "06:30 PM",
        venueName = "Mapo Startup Center",
        isOutdoor = true,
        admissionMode = "Free Booking",
        bookedCount = 18,
        capacityLimit = 25,
        status = OrganizerEventStatus.PUBLISHED,
        thumbnailRes = R.drawable.event_future_seoul
    )

    fun saveOrUpdateEvent(updated: OrganizerEventItem) {
        val index = allOrganizerEvents.indexOfFirst { it.id == updated.id }
        if (index >= 0) {
            allOrganizerEvents[index] = updated
        } else {
            allOrganizerEvents.add(0, updated)
        }
    }

    fun deleteEvent(eventId: String) {
        allOrganizerEvents.removeAll { it.id == eventId }
    }

    fun removeGateStaffLocal(staffId: String) {
        gateStaffList.removeAll { it.id == staffId }
    }

    fun addDummyGateStaff() {
        val nextNum = gateStaffList.size + 1
        val gateLetter = ('A' + (nextNum - 1).coerceAtMost(25))
        gateStaffList.add(
            GateStaffMember(
                id = "staff_gate_$nextNum",
                gateTitle = "Gate $gateLetter - Check-in Scanner",
                internalEmail = "gate$nextNum@seoulcity.internal",
                isReady = true,
                statusDetail = "• 0 Scans completed",
                role = "ticketing"
            )
        )
    }
}
