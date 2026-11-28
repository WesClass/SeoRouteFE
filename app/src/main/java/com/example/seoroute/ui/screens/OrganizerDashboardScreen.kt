package com.example.seoroute.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.R
import com.example.seoroute.model.DummyAuthRepository
import com.example.seoroute.model.EventTagStyle
import com.example.seoroute.model.OrganizerEventItem
import com.example.seoroute.model.OrganizerEventStatus
import com.example.seoroute.model.OrganizerNavTab
import com.example.seoroute.model.OrganizerRepository
import com.example.seoroute.ui.components.EventBadgePill
import com.example.seoroute.ui.components.OrganizerBottomNavBar
import com.example.seoroute.ui.theme.SeoBackground
import com.example.seoroute.ui.theme.SeoBorder
import com.example.seoroute.ui.theme.SeoGreenBg
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoPrimaryLight
import com.example.seoroute.ui.theme.SeoSurface
import com.example.seoroute.ui.theme.SeoTextPrimary
import com.example.seoroute.ui.theme.SeoTextSecondary

/**
 * 14_Organizer_dashboard 1 screen implementation (Figma node 109:1394).
 */
@Composable
fun OrganizerDashboardScreen(
    selectedNavTab: OrganizerNavTab = OrganizerNavTab.OVERVIEW,
    onNavTabSelected: (OrganizerNavTab) -> Unit,
    onCreateEventClick: () -> Unit,
    onScanTicketClick: () -> Unit,
    onSeeAllEventsClick: () -> Unit,
    onEditEventClick: (OrganizerEventItem) -> Unit,
    onSignOutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val eventsPreview = OrganizerRepository.allOrganizerEvents.take(2).ifEmpty {
        OrganizerRepository.dashboardEvents
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SeoBackground,
        bottomBar = {
            OrganizerBottomNavBar(
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
            Spacer(modifier = Modifier.height(20.dp))

            // Top Brand Kicker, Greeting & Log out button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ORGANIZER STUDIO",
                    color = SeoPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 15.sp
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
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Good morning,\nSeoul Creators.",
                color = SeoTextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Dark Navy Total Reservations Card (Figma node 109:1410)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(123.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF232954)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 19.dp, vertical = 18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "TOTAL RESERVATIONS",
                            color = Color(0xFFC9C9F5),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 14.sp
                        )
                        Text(
                            text = "142",
                            color = SeoSurface,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 46.sp
                        )
                        Text(
                            text = "Across 4 published events",
                            color = Color(0xFFD9DAFF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 15.sp
                        )
                    }

                    // "+12 this week" pill badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(bottom = 12.dp)
                            .width(115.dp)
                            .height(29.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF42487A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+12 this week",
                            color = SeoSurface,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Quick actions section
            Text(
                text = "Quick actions",
                color = SeoTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                OrganizerQuickActionCard(
                    title = "Create event",
                    iconRes = R.drawable.ic_org_plus_circle,
                    backgroundColor = SeoPrimaryLight,
                    onClick = onCreateEventClick,
                    modifier = Modifier.weight(1f)
                )
                OrganizerQuickActionCard(
                    title = "Scan ticket",
                    iconRes = R.drawable.ic_org_scan,
                    backgroundColor = SeoGreenBg,
                    onClick = onScanTicketClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            // "Your events" header + "See all"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your events",
                    color = SeoTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
                Text(
                    text = "See all",
                    color = SeoPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSeeAllEventsClick() }
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            // Event cards list
            Column(
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                eventsPreview.forEach { event ->
                    OrganizerEventRowCard(
                        event = event,
                        onEditClick = { onEditEventClick(event) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * View Events ("All Events") screen implementation (Figma node 126:1102).
 */
@Composable
fun OrganizerViewEventsScreen(
    selectedNavTab: OrganizerNavTab = OrganizerNavTab.EVENTS,
    onNavTabSelected: (OrganizerNavTab) -> Unit,
    onEditEventClick: (OrganizerEventItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val events = OrganizerRepository.allOrganizerEvents

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SeoBackground,
        bottomBar = {
            OrganizerBottomNavBar(
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
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ORGANIZER STUDIO",
                color = SeoPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "All Events",
                color = SeoTextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(26.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                events.forEach { event ->
                    OrganizerEventRowCard(
                        event = event,
                        onEditClick = { onEditEventClick(event) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun OrganizerQuickActionCard(
    title: String,
    @DrawableRes iconRes: Int,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(93.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 17.dp, vertical = 18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(23.dp)
            )
            Text(
                text = title,
                color = SeoTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 17.sp
            )
        }
    }
}

/**
 * Reusable Organizer Event Row Card shared between 14_Organizer_dashboard 1 and View Events.
 */
@Composable
fun OrganizerEventRowCard(
    event: OrganizerEventItem,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(93.dp)
            .clickable { onEditClick() },
        shape = RoundedCornerShape(16.dp),
        color = SeoSurface,
        border = BorderStroke(1.dp, SeoBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = event.thumbnailRes),
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 75.dp, height = 72.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 11.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.title,
                    color = SeoTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 17.sp
                )
                Text(
                    text = event.summaryMetaText,
                    color = SeoTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 14.sp
                )
                val badgeMinWidth = when (event.status) {
                    OrganizerEventStatus.PUBLISHED -> 91.dp
                    OrganizerEventStatus.CANCELLED -> 91.dp
                    OrganizerEventStatus.DRAFT -> 58.dp
                }
                EventBadgePill(
                    tag = EventTagStyle(
                        text = event.status.badgeLabel,
                        textColor = event.status.textColor,
                        backgroundColor = event.status.backgroundColor
                    ),
                    minWidth = badgeMinWidth,
                    height = 22.dp,
                    horizontalPadding = 10.dp
                )
            }

            Text(
                text = "Edit",
                color = SeoPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 18.dp)
                    .clickable { onEditClick() }
            )
        }
    }
}
