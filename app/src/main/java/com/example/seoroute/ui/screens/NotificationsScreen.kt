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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.R
import com.example.seoroute.model.NavTab
import com.example.seoroute.ui.components.SelectableFilterPill
import com.example.seoroute.ui.components.SeoRouteBottomNavBar
import com.example.seoroute.ui.theme.SeoBackground
import com.example.seoroute.ui.theme.SeoBorder
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoPrimaryLight
import com.example.seoroute.ui.theme.SeoSurface
import com.example.seoroute.ui.theme.SeoTextMuted
import com.example.seoroute.ui.theme.SeoTextPrimary
import com.example.seoroute.ui.theme.SeoTextSecondary

private data class NotificationItemData(
    val id: String,
    val category: String, // "Weather", "Bookings"
    val title: String,
    val line1: String,
    val line2: String,
    val timestamp: String? = null,
    val actionLabel: String? = null,
    @DrawableRes val iconRes: Int,
    val iconCircleColor: Color
)

private val sampleNotifications = listOf(
    NotificationItemData(
        id = "notif_rain",
        category = "Weather",
        title = "Rain expected at your event",
        line1 = "Han River Walk · Nov 21",
        line2 = "80% chance of rain. Check updates.",
        timestamp = "2 hours ago",
        iconRes = R.drawable.ic_notif_rain,
        iconCircleColor = Color(0xFFFFF0E9)
    ),
    NotificationItemData(
        id = "notif_confirmed",
        category = "Bookings",
        title = "Reservation confirmed",
        line1 = "Your ticket for Future Seoul Meetup",
        line2 = "is ready to view in My Tickets.",
        timestamp = "Yesterday",
        iconRes = R.drawable.ic_notif_check,
        iconCircleColor = Color(0xFFE6FAF3)
    ),
    NotificationItemData(
        id = "notif_update",
        category = "Bookings",
        title = "An event has been updated",
        line1 = "The organizer updated the venue for",
        line2 = "Autumn Garden Walk.",
        actionLabel = "View update",
        iconRes = R.drawable.ic_notif_megaphone,
        iconCircleColor = SeoPrimaryLight
    )
)

// Your Notification Page
@Composable
fun NotificationsScreen(
    selectedNavTab: NavTab? = null,
    onNavTabSelected: (NavTab) -> Unit,
    onViewUpdateClick: () -> Unit = {},
    onReservationNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Bookings", "Weather")

    val displayedNotifications = remember(selectedFilter) {
        if (selectedFilter == "All") {
            sampleNotifications
        } else {
            sampleNotifications.filter { it.category == selectedFilter }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SeoBackground,
        bottomBar = {
            SeoRouteBottomNavBar(
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

            // Title & Subtitle
            Text(
                text = "Notifications",
                color = SeoTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 29.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Event updates and weather advisories",
                color = SeoTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Filter Pills: All, Bookings, Weather
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filters.forEach { tab ->
                    SelectableFilterPill(
                        text = tab,
                        selected = selectedFilter == tab,
                        onClick = { selectedFilter = tab },
                        unselectedBackgroundColor = SeoSurface,
                        height = 32.dp,
                        horizontalPadding = when (tab) {
                            "All" -> 20.dp
                            "Bookings" -> 21.dp
                            else -> 20.dp
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Notification Cards
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                displayedNotifications.forEach { item ->
                    NotificationCard(
                        item = item,
                        onCardClick = {
                            if (item.id == "notif_confirmed") {
                                onReservationNotificationClick()
                            } else if (item.id == "notif_update") {
                                onViewUpdateClick()
                            }
                        },
                        onActionClick = onViewUpdateClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun NotificationCard(
    item: NotificationItemData,
    onCardClick: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        color = SeoSurface,
        border = BorderStroke(1.dp, SeoBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left circular icon container (40dp)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(item.iconCircleColor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.title,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 1.dp)
            ) {
                Text(
                    text = item.title,
                    color = SeoTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.line1,
                    color = SeoTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.line2,
                    color = SeoTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 15.sp
                )

                if (item.timestamp != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = item.timestamp,
                        color = SeoTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 13.sp
                    )
                }

                if (item.actionLabel != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .width(104.dp)
                            .height(26.dp)
                            .clip(RoundedCornerShape(50))
                            .background(SeoPrimaryLight)
                            .clickable { onActionClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.actionLabel,
                            color = SeoPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
