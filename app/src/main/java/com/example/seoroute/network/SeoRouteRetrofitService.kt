package com.example.seoroute.network

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service definition for the deployed SeoRoute Backend (`BuildConfig.BASE_URL`).
 */
interface SeoRouteRetrofitService {

    @POST("auth/dev")
    suspend fun devLogin(@Body body: RequestBody): Response<String>

    @POST("auth/sync")
    suspend fun firebaseSync(@Body body: RequestBody): Response<String>

    @GET("events")
    suspend fun getPublicEvents(
        @Query("q") query: String? = null,
        @Query("category") category: String? = null,
        @Query("access_type") accessType: String? = null,
        @Query("is_outdoor") isOutdoor: Boolean? = null,
        @Query("sort") sort: String = "soonest",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<String>

    @GET("events/{id}")
    suspend fun getEventById(@Path("id") eventId: String): Response<String>

    @POST("events/{id}/interest")
    suspend fun addInterest(@Path("id") eventId: String, @Body body: RequestBody): Response<String>

    @DELETE("events/{id}/interest")
    suspend fun removeInterest(@Path("id") eventId: String): Response<String>

    @POST("events/{id}/book")
    suspend fun bookEvent(@Path("id") eventId: String, @Body body: RequestBody): Response<String>

    @GET("me/tickets")
    suspend fun getMyTickets(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<String>

    @GET("me/interests")
    suspend fun getMyInterests(): Response<String>

    @GET("me/tickets/{id}/qr")
    suspend fun getTicketQr(@Path("id") ticketId: String): Response<String>

    @GET("me/notifications")
    suspend fun getNotifications(@Query("limit") limit: Int = 50): Response<String>

    @POST("me/notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") notificationId: String, @Body body: RequestBody): Response<String>

    @GET("organizer/profile")
    suspend fun getOrganizerProfile(): Response<String>

    @PUT("organizer/profile")
    suspend fun updateOrganizerProfile(@Body body: RequestBody): Response<String>

    @GET("organizer/events")
    suspend fun getOrganizerEvents(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50
    ): Response<String>

    @POST("organizer/events")
    suspend fun createOrganizerEvent(@Body body: RequestBody): Response<String>

    @PATCH("organizer/events/{id}")
    suspend fun updateOrganizerEvent(@Path("id") eventId: String, @Body body: RequestBody): Response<String>

    @POST("organizer/events/{id}/cancel")
    suspend fun cancelOrganizerEvent(@Path("id") eventId: String, @Body body: RequestBody): Response<String>

    @GET("organizer/events/{id}/attendees")
    suspend fun getOrganizerEventAttendees(
        @Path("id") eventId: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50
    ): Response<String>

    @GET("organizer/staff")
    suspend fun getOrganizerStaff(): Response<String>

    @POST("organizer/staff")
    suspend fun addOrganizerStaff(@Body body: RequestBody): Response<String>

    @DELETE("organizer/staff/{userId}")
    suspend fun deleteOrganizerStaff(@Path("userId") userId: String): Response<String>

    @GET("ticketing/events")
    suspend fun getTicketingEvents(): Response<String>

    @GET("ticketing/events/{id}/tickets")
    suspend fun getTicketingManifest(@Path("id") eventId: String): Response<String>

    @GET("ticketing/events/{id}/stats")
    suspend fun getTicketingStats(@Path("id") eventId: String): Response<String>

    @POST("ticketing/events/{id}/check-in")
    suspend fun checkInTicketOnline(@Path("id") eventId: String, @Body body: RequestBody): Response<String>

    @GET("admin/organizers")
    suspend fun getAdminOrganizers(): Response<String>

    @POST("admin/organizers")
    suspend fun createAdminOrganizer(@Body body: RequestBody): Response<String>

    @GET("admin/stats")
    suspend fun getAdminStats(): Response<String>
}
