package com.example.seoroute.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.seoroute.R
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

enum class NavTab(val label: String, @DrawableRes val iconRes: Int) {
    DISCOVER("Discover", R.drawable.ic_nav_discover),
    SAVED("Saved", R.drawable.ic_nav_saved),
    TICKETS("Tickets", R.drawable.ic_nav_tickets),
    PROFILE("Profile", R.drawable.ic_nav_profile)
}

enum class VibeCategory(val label: String, val filterLabel: String, @DrawableRes val iconRes: Int) {
    MUSIC("Music", "Music", R.drawable.ic_vibe_music),
    ART("Art", "Arts", R.drawable.ic_vibe_art),
    FESTIVAL("Festival", "Festival", R.drawable.ic_vibe_festival),
    OUTDOORS("Outdoors", "Outdoors", R.drawable.ic_vibe_outdoors)
}

data class EventTagStyle(
    val text: String,
    val textColor: Color,
    val backgroundColor: Color
)

enum class EventDetailType {
    PUBLIC,
    MANAGED
}

data class SeoEvent(
    val id: String,
    val title: String,
    val subtitle: String,
    val shortDateLocation: String,
    val fullDate: String,
    val timeRange: String?,
    val venueName: String,
    val fullLocation: String,
    val districtCaption: String,
    @DrawableRes val imageRes: Int,
    val listBadge: EventTagStyle,
    val savedListBadge: EventTagStyle = listBadge,
    val detailTags: List<EventTagStyle>,
    val category: VibeCategory,
    val isFree: Boolean,
    val isOutdoor: Boolean,
    val admissionType: String, // "Free open", "Booking", "Paid"
    val detailSectionTitle: String,
    val descriptionLines: List<String>,
    val detailType: EventDetailType,
    val reservedSpots: Int? = null,
    val totalSpots: Int? = null,
    val organizerMonogram: String,
    val organizerName: String,
    val organizerSubtitle: String,
    val ctaButtonText: String,
    val mapHighlightColor: Color,
    val bookingShortDate: String = "Tuesday, Nov 17",
    val bookingShortTime: String = "2:00 PM",
    val ticketDateTime: String = "Nov 17, 2026 · 2:00 PM",
    val bookingCode: String = "BOOKING #SEH-48291"
)

data class BookedTicket(
    val id: String,
    val event: SeoEvent,
    val statusText: String = "CONFIRMED",
    val subtitleText: String,
    val isHeroLayout: Boolean
)

object SampleEvents {
    private val PurpleTag = { label: String ->
        EventTagStyle(label, SeoPrimary, SeoPrimaryLight)
    }
    private val GreenTag = { label: String ->
        EventTagStyle(label, SeoGreenText, SeoGreenBg)
    }
    private val GrayTag = { label: String ->
        EventTagStyle(label, SeoTextSecondary, SeoFilterChipBg)
    }
    private val BookingListTag = EventTagStyle("Booking", SeoBookingText, SeoBookingBg)

    val seoulCultureNight = SeoEvent(
        id = "seoul_culture_night",
        title = "Seoul Culture Night",
        subtitle = "Discover music, art and life after dark.",
        shortDateLocation = "Nov 14 · Seoul Plaza",
        fullDate = "Saturday, 14 November 2026",
        timeRange = "6:00 PM – 10:00 PM",
        venueName = "Seoul Plaza",
        fullLocation = "Seoul Plaza, Jung-gu",
        districtCaption = "Jung-gu · Illustrative map",
        imageRes = R.drawable.event_seoul_culture_night,
        listBadge = PurpleTag("Free"),
        detailTags = listOf(
            PurpleTag("Music"),
            PurpleTag("Outdoor"),
            GreenTag("Free open")
        ),
        category = VibeCategory.MUSIC,
        isFree = true,
        isOutdoor = true,
        admissionType = "Free open",
        detailSectionTitle = "About this event",
        descriptionLines = listOf(
            "Experience an evening of performances,",
            "creative installations and local culture",
            "in the heart of Seoul."
        ),
        detailType = EventDetailType.PUBLIC,
        organizerMonogram = "SC",
        organizerName = "Seoul Culture Team",
        organizerSubtitle = "Arts & culture community",
        ctaButtonText = "Register",
        mapHighlightColor = SeoMapPlazaGreen,
        bookingShortDate = "Saturday, Nov 14",
        bookingShortTime = "6:00 PM",
        ticketDateTime = "Nov 14, 2026 · 6:00 PM"
    )

    val artInTheCity = SeoEvent(
        id = "art_in_the_city",
        title = "Art in the City",
        subtitle = "Urban installations and contemporary galleries.",
        shortDateLocation = "Nov 08 · Jung-gu",
        fullDate = "Sunday, 08 November 2026",
        timeRange = "11:00 AM – 7:00 PM",
        venueName = "Seoul Plaza",
        fullLocation = "Jung-gu Art District",
        districtCaption = "Jung-gu · Illustrative map",
        imageRes = R.drawable.event_art_museum,
        listBadge = PurpleTag("Free"),
        detailTags = listOf(
            PurpleTag("Art"),
            PurpleTag("Outdoor"),
            GreenTag("Free open")
        ),
        category = VibeCategory.ART,
        isFree = true,
        isOutdoor = true,
        admissionType = "Free open",
        detailSectionTitle = "About this event",
        descriptionLines = listOf(
            "Explore contemporary artworks and interactive",
            "installations across historic Jung-gu streets",
            "in the heart of Seoul."
        ),
        detailType = EventDetailType.PUBLIC,
        organizerMonogram = "SC",
        organizerName = "Seoul Culture Team",
        organizerSubtitle = "Arts & culture community",
        ctaButtonText = "Register",
        mapHighlightColor = SeoMapPlazaGreen,
        bookingShortDate = "Sunday, Nov 08",
        bookingShortTime = "11:00 AM",
        ticketDateTime = "Nov 08, 2026 · 11:00 AM"
    )

    val museumAfterHours = SeoEvent(
        id = "museum_after_hours",
        title = "Museum After Hours",
        subtitle = "Late-night exhibitions and ambient live sets.",
        shortDateLocation = "Nov 12 · Jongno-gu",
        fullDate = "Thursday, 12 November 2026",
        timeRange = "7:00 PM – 11:00 PM",
        venueName = "Seoul Plaza",
        fullLocation = "Jongno Museum Square",
        districtCaption = "Jongno-gu · Illustrative map",
        imageRes = R.drawable.event_art_museum,
        listBadge = PurpleTag("Free"),
        detailTags = listOf(
            PurpleTag("Music"),
            GrayTag("Indoor"),
            GreenTag("Free open")
        ),
        category = VibeCategory.MUSIC,
        isFree = true,
        isOutdoor = false,
        admissionType = "Free open",
        detailSectionTitle = "About this event",
        descriptionLines = listOf(
            "Experience an evening of performances,",
            "creative installations and local culture",
            "in the heart of Seoul."
        ),
        detailType = EventDetailType.PUBLIC,
        organizerMonogram = "SC",
        organizerName = "Seoul Culture Team",
        organizerSubtitle = "Arts & culture community",
        ctaButtonText = "Register",
        mapHighlightColor = SeoMapPlazaGreen,
        bookingShortDate = "Thursday, Nov 12",
        bookingShortTime = "7:00 PM",
        ticketDateTime = "Nov 12, 2026 · 7:00 PM"
    )

    val futureSeoulMeetup = SeoEvent(
        id = "future_seoul_meetup",
        title = "Future Seoul Meetup",
        subtitle = "Technology, design and local creators.",
        shortDateLocation = "Nov 17 · Mapo-gu",
        fullDate = "Tuesday, 17 November 2026",
        timeRange = null,
        venueName = "Mapo Startup Center",
        fullLocation = "Mapo Startup Center",
        districtCaption = "Mapo-gu · Illustrative map",
        imageRes = R.drawable.event_future_seoul,
        listBadge = BookingListTag,
        savedListBadge = PurpleTag("Booking"),
        detailTags = listOf(
            PurpleTag("Festival"),
            GreenTag("Free booking"),
            GrayTag("Indoor")
        ),
        category = VibeCategory.FESTIVAL,
        isFree = true,
        isOutdoor = false,
        admissionType = "Booking",
        detailSectionTitle = "Event information",
        descriptionLines = listOf(
            "Meet Seoul-based founders and designers.",
            "Registration is free but seats are limited."
        ),
        detailType = EventDetailType.MANAGED,
        reservedSpots = 18,
        totalSpots = 25,
        organizerMonogram = "FS",
        organizerName = "Future Seoul",
        organizerSubtitle = "Tech & design community",
        ctaButtonText = "Interested",
        mapHighlightColor = SeoMapVenuePurple,
        bookingShortDate = "Tuesday, Nov 17",
        bookingShortTime = "2:00 PM",
        ticketDateTime = "Nov 17, 2026 · 2:00 PM"
    )

    val autumnGardenWalk = SeoEvent(
        id = "autumn_garden_walk",
        title = "Autumn Garden Walk",
        subtitle = "Guided foliage tour and acoustic sessions.",
        shortDateLocation = "Nov 20 · Seongdong",
        fullDate = "Friday, 20 November 2026",
        timeRange = "2:00 PM – 5:30 PM",
        venueName = "Seoul Forest Park",
        fullLocation = "Seongdong-gu Garden Plaza",
        districtCaption = "Seongdong · Illustrative map",
        imageRes = R.drawable.event_autumn_garden,
        listBadge = PurpleTag("Free"),
        detailTags = listOf(
            PurpleTag("Outdoor"),
            GreenTag("Free open")
        ),
        category = VibeCategory.OUTDOORS,
        isFree = true,
        isOutdoor = true,
        admissionType = "Free open",
        detailSectionTitle = "About this event",
        descriptionLines = listOf(
            "Experience an afternoon walk surrounded by",
            "autumn foliage and local culture",
            "in the heart of Seoul."
        ),
        detailType = EventDetailType.PUBLIC,
        organizerMonogram = "SC",
        organizerName = "Seoul Culture Team",
        organizerSubtitle = "Arts & culture community",
        ctaButtonText = "Register",
        mapHighlightColor = SeoMapPlazaGreen,
        bookingShortDate = "Friday, Nov 20",
        bookingShortTime = "2:00 PM",
        ticketDateTime = "Nov 20, 2026 · 2:00 PM"
    )

    val hanRiverWalk = SeoEvent(
        id = "han_river_walk",
        title = "Han River Walk",
        subtitle = "Sunset stroll and community picnic by the river.",
        shortDateLocation = "Sat, Nov 21",
        fullDate = "Saturday, 21 November 2026",
        timeRange = "4:30 PM – 7:30 PM",
        venueName = "Yeouido Hangang Park",
        fullLocation = "Yeouido Hangang Park",
        districtCaption = "Yeongdeungpo · Illustrative map",
        imageRes = R.drawable.event_han_river_walk,
        listBadge = PurpleTag("Free"),
        detailTags = listOf(
            PurpleTag("Outdoor"),
            GreenTag("Free booking")
        ),
        category = VibeCategory.OUTDOORS,
        isFree = true,
        isOutdoor = true,
        admissionType = "Booking",
        detailSectionTitle = "Event information",
        descriptionLines = listOf(
            "Enjoy a sunset walk along the Han River.",
            "Registration is free for all community members."
        ),
        detailType = EventDetailType.MANAGED,
        reservedSpots = 20,
        totalSpots = 30,
        organizerMonogram = "SR",
        organizerName = "SeoRoute Crew",
        organizerSubtitle = "Outdoor & city walks",
        ctaButtonText = "Interested",
        mapHighlightColor = SeoMapPlazaGreen,
        bookingShortDate = "Saturday, Nov 21",
        bookingShortTime = "4:30 PM",
        ticketDateTime = "Nov 21, 2026 · 4:30 PM"
    )

    val exploreEvents = listOf(
        seoulCultureNight,
        museumAfterHours,
        futureSeoulMeetup,
        autumnGardenWalk
    )

    val savedEvents = listOf(
        museumAfterHours,
        seoulCultureNight,
        futureSeoulMeetup,
        autumnGardenWalk
    )

    val myTickets = listOf(
        BookedTicket(
            id = "ticket_future_seoul",
            event = futureSeoulMeetup,
            subtitleText = "Tue, Nov 17 · Mapo Startup Center",
            isHeroLayout = true
        ),
        BookedTicket(
            id = "ticket_han_river",
            event = hanRiverWalk,
            subtitleText = "Sat, Nov 21",
            isHeroLayout = false
        )
    )
}
