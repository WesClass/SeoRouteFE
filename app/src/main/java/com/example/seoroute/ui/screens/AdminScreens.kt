package com.example.seoroute.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.R
import com.example.seoroute.model.AdminAgencyItem
import com.example.seoroute.model.AdminNavTab
import com.example.seoroute.model.DummyAuthRepository
import com.example.seoroute.model.OrganizerEventItem
import com.example.seoroute.model.OrganizerEventStatus
import com.example.seoroute.model.OrganizerRepository
import com.example.seoroute.network.SeoRouteApiClient
import com.example.seoroute.ui.components.AdminBottomNavBar
import com.example.seoroute.ui.theme.SeoBackground
import com.example.seoroute.ui.theme.SeoBorder
import com.example.seoroute.ui.theme.SeoGreenBg
import com.example.seoroute.ui.theme.SeoGreenText
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoPrimaryLight
import com.example.seoroute.ui.theme.SeoSurface
import com.example.seoroute.ui.theme.SeoTextPrimary
import com.example.seoroute.ui.theme.SeoTextSecondary
import kotlinx.coroutines.launch

private val AdminBg = Color(0xFFF8F9FD)
private val AdminNavyDark = Color(0xFF0F172A)
private val AdminSlateText = Color(0xFF64748B)
private val AdminSlateMuted = Color(0xFF94A3B8)
private val AdminCardBorder = Color(0xFFE2E8F0)
private val AdminDividerColor = Color(0xFFF1F5F9)
private val AdminPrimaryButton = Color(0xFF5E54F3)

/**
 * Overview Page screen implementation (Figma node 2125:301 + 2125:380).
 * Reuses the modular 3-tab [AdminBottomNavBar] (Overview, Events, People).
 */
@Composable
fun AdminOverviewScreen(
    selectedNavTab: AdminNavTab = AdminNavTab.OVERVIEW,
    onNavTabSelected: (AdminNavTab) -> Unit,
    onAddAgencyClick: () -> Unit = { onNavTabSelected(AdminNavTab.PEOPLE) },
    onCreateEventClick: () -> Unit = { onNavTabSelected(AdminNavTab.EVENTS) },
    onSignOutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var stats by remember { mutableStateOf<org.json.JSONObject?>(null) }
    var statsError by remember { mutableStateOf(false) }
    var statsRequest by remember { mutableStateOf(0) }
    LaunchedEffect(statsRequest) {
        statsError = false
        SeoRouteApiClient.fetchAdminStats()
            .onSuccess { stats = it }
            .onFailure { statsError = true }
    }
    val activeEventsCount = stats?.countText("active_events", "published_events", "events_published")
    val organizersCount = stats?.countText("organizers", "total_organizers")
    val bookingsCount = stats?.countText("active_tickets", "tickets_active")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SeoBackground,
        bottomBar = {
            AdminBottomNavBar(
                selectedTab = selectedNavTab,
                onTabSelected = onNavTabSelected
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header: "Overview" + Log out pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Overview",
                    color = SeoTextPrimary,
                    fontSize = 29.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 35.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFFEECEB))
                        .clickable {
                            DummyAuthRepository.signOut()
                            onSignOutClick()
                        }
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Log out",
                        color = Color(0xFFD92D20),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Review platform activity",
                color = SeoTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            when {
                statsError -> Text(
                    "Could not load statistics. Tap to retry.",
                    modifier = Modifier.clickable { statsRequest++ },
                    color = SeoPrimary
                )
                stats == null -> Text("Loading statistics…", color = AdminSlateText)
                listOf(activeEventsCount, organizersCount, bookingsCount).any { it == null } ->
                    Text("Some counts are unavailable.", color = AdminSlateText)
            }

            // 3 Side-by-Side Metric Cards (Figma nodes 2125:316, 2125:319, 2125:322)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminMetricCard(
                    valueText = activeEventsCount ?: "—",
                    labelText = "Active events",
                    valueColor = SeoPrimary,
                    backgroundColor = SeoPrimaryLight,
                    borderColor = Color(0xFFE4E0FF),
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    valueText = organizersCount ?: "—",
                    labelText = "Organizers",
                    valueColor = SeoTextPrimary,
                    backgroundColor = SeoSurface,
                    borderColor = SeoBorder,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    valueText = bookingsCount ?: "—",
                    labelText = "Active tickets",
                    valueColor = SeoTextPrimary,
                    backgroundColor = SeoSurface,
                    borderColor = SeoBorder,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions Section (Figma nodes 2125:325 & 2125:380)
            Text(
                text = "Quick Actions",
                color = SeoTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: "+ Add New Agency" (Figma node 2125:381)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(128.dp)
                        .clickable { onAddAgencyClick() },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFEAEFFF),
                    border = BorderStroke(1.dp, Color(0xFFE0E7FF))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(17.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = SeoSurface,
                            shadowElevation = 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_admin_building_purple),
                                    contentDescription = "Add New Agency",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "+ Add New Agency",
                                color = Color(0xFF5B50F6),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 19.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Onboard organizer",
                                color = AdminSlateText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Card 2: "Create Event" (Figma node 2125:388)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(128.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFECFDF5),
                                    Color(0xFFF0FDFA)
                                )
                            )
                        )
                        .border(1.dp, Color(0xFFD1FAE5), RoundedCornerShape(16.dp))
                        .clickable { onCreateEventClick() }
                        .padding(17.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = SeoSurface,
                            shadowElevation = 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_admin_calendar_green),
                                    contentDescription = "Create Event",
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Create Event",
                                color = Color(0xFF047857),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 19.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Publish city event",
                                color = AdminSlateText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // System Activity Feed (Figma nodes 2125:363, 2125:354, 2125:340, 2125:326)
            Text(
                text = "System activity",
                color = SeoTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Feed Item 1: New event published
                AdminActivityRowCard(
                    iconRes = R.drawable.ic_notif_check,
                    iconBoxBg = SeoGreenBg,
                    isCircularIcon = true,
                    title = "New event published",
                    subtitle = "Future Seoul Meetup · 3h ago",
                    badgeText = "Live",
                    badgeBg = SeoGreenBg,
                    badgeTextColor = SeoGreenText
                )

                // Feed Item 2: Agency account created
                AdminActivityRowCard(
                    iconRes = R.drawable.ic_admin_building_purple,
                    iconBoxBg = Color(0xFFEEF2FF),
                    isCircularIcon = false,
                    title = "Agency account created",
                    subtitle = "Mapo Arts Council • 1d ago",
                    badgeText = "Ready",
                    badgeBg = Color(0xFFEEF2FF),
                    badgeTextColor = Color(0xFF5B50F6)
                )

                // Feed Item 3: Capacity warning (85%)
                AdminActivityRowCard(
                    iconRes = R.drawable.ic_admin_warning_amber,
                    iconBoxBg = Color(0xFFFFFBEB),
                    isCircularIcon = false,
                    title = "Capacity warning (85%)",
                    subtitle = "Seoul Culture Night • 2d ago",
                    badgeText = "Notice",
                    badgeBg = Color(0xFFFFFBEB),
                    badgeTextColor = Color(0xFFB45309)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

private fun org.json.JSONObject.countText(vararg keys: String): String? =
    keys.firstNotNullOfOrNull { key -> opt(key)?.toString()?.toLongOrNull()?.toString() }

@Composable
private fun AdminMetricCard(
    valueText: String,
    labelText: String,
    valueColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(104.dp),
        shape = RoundedCornerShape(18.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 15.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = valueText,
                color = valueColor,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 31.sp
            )
            Text(
                text = labelText,
                color = SeoTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
private fun AdminActivityRowCard(
    @DrawableRes iconRes: Int,
    iconBoxBg: Color,
    isCircularIcon: Boolean,
    title: String,
    subtitle: String,
    badgeText: String,
    badgeBg: Color,
    badgeTextColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(71.dp),
        shape = RoundedCornerShape(16.dp),
        color = SeoSurface,
        border = BorderStroke(1.dp, AdminDividerColor),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(if (isCircularIcon) CircleShape else RoundedCornerShape(12.dp))
                        .background(iconBoxBg),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = title,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        color = AdminNavyDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = subtitle,
                        color = AdminSlateText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(badgeBg)
                    .padding(horizontal = 10.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeText,
                    color = badgeTextColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

/**
 * Console ("Admin Console") screen implementation (Figma node 2125:3).
 * Reuses the modular 3-tab [AdminBottomNavBar] (Events tab active).
 */
@Composable
fun AdminConsoleScreen(
    selectedNavTab: AdminNavTab = AdminNavTab.EVENTS,
    onNavTabSelected: (AdminNavTab) -> Unit,
    onCreateEventClick: () -> Unit = {},
    onEditEventClick: (OrganizerEventItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All Events") }
    var events by remember { mutableStateOf<List<OrganizerEventItem>?>(null) }
    var eventsError by remember { mutableStateOf(false) }
    var eventsRequest by remember { mutableStateOf(0) }
    val filterTabs = listOf("All Events", "Published", "Postponed / Draft", "Cancelled")
    LaunchedEffect(eventsRequest) {
        eventsError = false
        SeoRouteApiClient.fetchOrganizerEvents(allPages = true)
            .onSuccess { events = it }
            .onFailure { eventsError = true }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AdminBg,
        bottomBar = {
            AdminBottomNavBar(
                selectedTab = selectedNavTab,
                onTabSelected = onNavTabSelected
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Title & Subtitle
            Text(
                text = "Admin Console",
                color = Color(0xFF16192E),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.6).sp,
                lineHeight = 32.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Manage events across organizers",
                color = Color(0xFF687289),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Primary "+ Create New Event" Button (Figma node 2125:7)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable { onCreateEventClick() },
                shape = RoundedCornerShape(16.dp),
                color = AdminPrimaryButton,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+",
                        color = SeoSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create New Event",
                        color = SeoSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Event Status Filter Pills (Figma node 2125:62)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterTabs.forEach { tab ->
                    val isSelected = selectedFilter == tab
                    Surface(
                        modifier = Modifier
                            .height(34.dp)
                            .clickable { selectedFilter = tab },
                        shape = RoundedCornerShape(50),
                        color = if (isSelected) AdminPrimaryButton else SeoSurface,
                        border = if (isSelected) null else BorderStroke(1.dp, AdminDividerColor)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                color = if (isSelected) SeoSurface else Color(0xFF687289),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val visibleEvents = events.orEmpty().filter { event ->
                when (selectedFilter) {
                    "Published" -> event.status == OrganizerEventStatus.PUBLISHED
                    "Postponed / Draft" -> event.status == OrganizerEventStatus.DRAFT
                    "Cancelled" -> event.status == OrganizerEventStatus.CANCELLED
                    else -> true
                }
            }
            when {
                eventsError -> Text(
                    "Could not load events. Tap to retry.",
                    modifier = Modifier.clickable { eventsRequest++ },
                    color = SeoPrimary
                )
                events == null -> Text("Loading events…", color = AdminSlateText)
                visibleEvents.isEmpty() -> Text("No events found.", color = AdminSlateText)
                else -> Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    visibleEvents.forEach { event ->
                        AdminConsoleEventCard(
                            title = event.title,
                            dateVenue = listOf(event.dateRangeText, event.venueName).joinToString(" · "),
                            thumbnailRes = event.thumbnailRes,
                            tags = listOf(
                                Triple(event.admissionMode, SeoPrimary, SeoPrimaryLight),
                                Triple(if (event.isOutdoor) "Outdoor" else "Indoor", AdminSlateText, AdminDividerColor)
                            ),
                            capacityBadge = null,
                            progressFraction = null,
                            onEditClick = { onEditEventClick(event) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun AdminConsoleEventCard(
    title: String,
    dateVenue: String,
    @DrawableRes thumbnailRes: Int,
    tags: List<Triple<String, Color, Color>>,
    capacityBadge: Triple<String, Color, Color>?,
    progressFraction: Float?,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SeoSurface,
        border = BorderStroke(1.dp, AdminDividerColor),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(id = thumbnailRes),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            color = Color(0xFF16192E),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_org_more_vert),
                            contentDescription = "Options",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = dateVenue,
                        color = Color(0xFF687289),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        tags.forEach { (label, textColor, bgColor) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(bgColor)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = textColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    if (capacityBadge != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(capacityBadge.third)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = capacityBadge.first,
                                color = capacityBadge.second,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (progressFraction != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(AdminDividerColor)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFraction)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(AdminPrimaryButton)
                    )
                }
            }

            Spacer(modifier = Modifier.height(11.dp))
            HorizontalDivider(thickness = 1.dp, color = AdminDividerColor)
            Spacer(modifier = Modifier.height(11.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Edit pill button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x99EEF0FD))
                        .clickable { onEditClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_admin_edit_pencil),
                        contentDescription = "Edit",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Edit",
                        color = Color(0xFF5245EB),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            }
        }
    }
}

/**
 * Add/Edit Organizers ("Organizers & Agencies") screen implementation (Figma node 2125:193).
 * Connects to live backend `GET /api/v1/admin/organizers` and `POST /api/v1/admin/organizers`.
 * Reuses the modular 3-tab [AdminBottomNavBar] (People tab active).
 */
@Composable
fun AdminManageOrganizersScreen(
    selectedNavTab: AdminNavTab = AdminNavTab.PEOPLE,
    onNavTabSelected: (AdminNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddModal by remember { mutableStateOf(false) }
    var editingAgencyId by remember { mutableStateOf<String?>(null) }
    var newAgencyName by remember { mutableStateOf("") }
    var newAgencyEmail by remember { mutableStateOf("") }
    var newAgencyCert by remember { mutableStateOf("") }
    var newAgencyPhone by remember { mutableStateOf("") }
    var creatingAgency by remember { mutableStateOf(false) }
    var statusBanner by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val agencies = OrganizerRepository.adminAgenciesList
    val scrollState = rememberScrollState()

    val filteredAgencies = if (searchQuery.isBlank()) {
        agencies
    } else {
        agencies.filter {
            it.name.contains(searchQuery.trim(), ignoreCase = true) ||
                it.supportEmail.contains(searchQuery.trim(), ignoreCase = true)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AdminBg,
        bottomBar = {
            AdminBottomNavBar(
                selectedTab = selectedNavTab,
                onTabSelected = onNavTabSelected
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Title & Subtitle (Figma nodes 2125:211 & 2125:212)
            Text(
                text = "Organizers & Agencies",
                color = AdminNavyDark,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.75).sp,
                lineHeight = 34.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Create and manage event organizer accounts",
                color = AdminSlateText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // "+ Add New Organizer" Button (Figma node 2125:213)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable(enabled = !creatingAgency) {
                        showAddModal = !showAddModal || editingAgencyId != null
                        editingAgencyId = null
                        newAgencyName = ""
                        newAgencyEmail = ""
                        newAgencyCert = ""
                        newAgencyPhone = ""
                        statusBanner = null
                    },
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF5F55F3),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+",
                        color = SeoSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add New Organizer",
                        color = SeoSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // The admin API supports creation. Edits update the in-memory list.
            if (showAddModal) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = SeoSurface,
                    border = BorderStroke(1.dp, AdminCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (editingAgencyId == null) "New Organizer / Agency" else "Edit Organizer / Agency",
                            color = AdminNavyDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        BasicTextField(
                            value = newAgencyName,
                            onValueChange = { newAgencyName = it },
                            singleLine = true,
                            textStyle = TextStyle(color = AdminNavyDark, fontSize = 13.sp),
                            decorationBox = { inner ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AdminBg)
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    if (newAgencyName.isEmpty()) {
                                        Text("Agency name (e.g. Gangnam Arts Center)", color = AdminSlateMuted, fontSize = 13.sp)
                                    }
                                    inner()
                                }
                            }
                        )
                        BasicTextField(
                            value = newAgencyEmail,
                            onValueChange = { newAgencyEmail = it },
                            singleLine = true,
                            textStyle = TextStyle(color = AdminNavyDark, fontSize = 13.sp),
                            decorationBox = { inner ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AdminBg)
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    if (newAgencyEmail.isEmpty()) {
                                        Text("Support email (e.g. contact@gangnamarts.kr)", color = AdminSlateMuted, fontSize = 13.sp)
                                    }
                                    inner()
                                }
                            }
                        )
                        if (editingAgencyId == null) {
                            androidx.compose.material3.OutlinedTextField(
                                value = newAgencyCert,
                                onValueChange = { newAgencyCert = it },
                                label = { Text("Certification number") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            androidx.compose.material3.OutlinedTextField(
                                value = newAgencyPhone,
                                onValueChange = { newAgencyPhone = it },
                                label = { Text("Support phone") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (editingAgencyId != null) {
                                Text(
                                    text = "Cancel",
                                    color = AdminSlateText,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .clickable {
                                            editingAgencyId = null
                                            showAddModal = false
                                            statusBanner = null
                                        }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF5F55F3))
                                    .clickable(enabled = !creatingAgency) {
                                        val editingId = editingAgencyId
                                        val name = newAgencyName.trim()
                                        val email = newAgencyEmail.trim()
                                        if (name.isEmpty() || email.isEmpty()) {
                                            statusBanner = "Enter the organizer name and support email."
                                            return@clickable
                                        }
                                        val savedName = name
                                        val savedEmail = email
                                        val monogram = savedName.split(" ")
                                            .filter { it.isNotBlank() }
                                            .take(2)
                                            .joinToString("") { it.first().uppercaseChar().toString() }
                                            .ifEmpty { "GA" }
                                        if (editingId != null) {
                                            val index = agencies.indexOfFirst { it.id == editingId }
                                            if (index == -1) {
                                                statusBanner = "This organizer is no longer in the list."
                                                return@clickable
                                            }
                                            agencies[index] = agencies[index].copy(
                                                name = savedName,
                                                supportEmail = savedEmail,
                                                monogram = monogram
                                            )
                                            editingAgencyId = null
                                            showAddModal = false
                                            statusBanner = "Updated '$savedName' for this session."
                                            return@clickable
                                        }
                                        val certification = newAgencyCert.trim()
                                        val phone = newAgencyPhone.trim()
                                        if (certification.isEmpty() || phone.isEmpty()) {
                                            statusBanner = "Enter the certification number and support phone."
                                            return@clickable
                                        }
                                        creatingAgency = true
                                        statusBanner = null
                                        coroutineScope.launch {
                                            val result = SeoRouteApiClient.createAdminOrganizer(
                                                name = savedName,
                                                certificationNumber = certification,
                                                supportEmail = savedEmail,
                                                supportPhone = phone
                                            )
                                            creatingAgency = false
                                            val created = result.getOrElse {
                                                statusBanner = it.message ?: "Could not create the organizer."
                                                return@launch
                                            }
                                            agencies.removeAll { it.id == created.id }
                                            agencies.add(
                                                0,
                                                AdminAgencyItem(
                                                    id = created.id,
                                                    monogram = monogram,
                                                    name = created.name,
                                                    supportEmail = created.supportEmail,
                                                    activeEventsCount = 0,
                                                    avatarBgColor = Color(0xFFEEF2FF),
                                                    avatarBorderColor = Color(0xFFE0E7FF),
                                                    avatarTextColor = Color(0xFF4F46E5),
                                                    badgeBgColor = Color(0xFFECFDF5),
                                                    badgeBorderColor = Color(0xFFD1FAE5),
                                                    badgeTextColor = Color(0xFF047857)
                                                )
                                            )
                                            newAgencyName = ""
                                            newAgencyEmail = ""
                                            showAddModal = false
                                            statusBanner = "Created organizer '$savedName'"
                                        }
                                    }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = when {
                                        creatingAgency -> "Saving…"
                                        editingAgencyId == null -> "Save Organizer"
                                        else -> "Save Changes Locally"
                                    },
                                    color = SeoSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            if (statusBanner != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = statusBanner!!,
                    color = Color(0xFF4F46E5),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Search Bar: "Search by agency name or email..." (Figma node 2125:218)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                color = SeoSurface,
                border = BorderStroke(1.dp, AdminCardBorder),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search by agency name or email...",
                                color = AdminSlateMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = AdminNavyDark,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = SolidColor(Color(0xFF5F55F3)),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Agency Cards List (Figma nodes 2125:222, 2125:242, 2125:262)
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                filteredAgencies.forEach { agency ->
                    AdminAgencyCard(
                        agency = agency,
                        onEditClick = {
                            if (creatingAgency) return@AdminAgencyCard
                            editingAgencyId = agency.id
                            newAgencyName = agency.name
                            newAgencyEmail = agency.supportEmail
                            statusBanner = null
                            showAddModal = true
                            coroutineScope.launch { scrollState.animateScrollTo(0) }
                        },
                        onDeleteClick = {
                            agencies.removeAll { it.id == agency.id }
                            statusBanner = "Removed '${agency.name}' from this session's list."
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun AdminAgencyCard(
    agency: AdminAgencyItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = SeoSurface,
        border = BorderStroke(1.dp, AdminCardBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // 48dp Rounded-16dp Monogram Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(agency.avatarBgColor)
                        .border(1.dp, agency.avatarBorderColor, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = agency.monogram,
                        color = agency.avatarTextColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = agency.name,
                        color = AdminNavyDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = agency.supportEmail,
                        color = AdminSlateText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(11.dp))

                    // Active events pill badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(agency.badgeBgColor)
                            .border(1.dp, agency.badgeBorderColor, RoundedCornerShape(50))
                            .padding(horizontal = 11.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${agency.activeEventsCount} Active events",
                            color = agency.badgeTextColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            if (agency.showDivider) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(thickness = 1.dp, color = AdminDividerColor)
                Spacer(modifier = Modifier.height(10.dp))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom right Actions: Edit (indigo) & Delete (rose)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Edit",
                    color = Color(0xFF4F46E5),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onEditClick() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Delete",
                    color = Color(0xFFF43F5E),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onDeleteClick() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
