package com.example.seoroute.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.R
import com.example.seoroute.model.NavTab
import com.example.seoroute.model.SampleEvents
import com.example.seoroute.model.SeoEvent
import com.example.seoroute.model.VibeCategory
import com.example.seoroute.ui.components.EventHorizontalCard
import com.example.seoroute.ui.components.SeoRouteBottomNavBar
import com.example.seoroute.ui.components.SeoSearchBar
import com.example.seoroute.ui.theme.SeoBackground
import com.example.seoroute.ui.theme.SeoBorder
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoPrimaryBorder
import com.example.seoroute.ui.theme.SeoPrimaryLight
import com.example.seoroute.ui.theme.SeoSurface
import com.example.seoroute.ui.theme.SeoTextPrimary
import com.example.seoroute.ui.theme.SeoTextSecondary

// Discover Page
@Composable
fun DiscoverScreen(
    selectedVibe: VibeCategory,
    selectedNavTab: NavTab,
    onVibeClick: (VibeCategory) -> Unit,
    onSearchClick: () -> Unit,
    onSeeAllClick: () -> Unit,
    onEventClick: (SeoEvent) -> Unit,
    onNavTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
    onNotificationsClick: () -> Unit = {}
) {
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

            // Top Header Row: SEOUL / TODAY + Headline + Notification Bell
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth(0.82f)) {
                    Text(
                        text = "SEOUL / TODAY",
                        color = SeoPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Find your next\nunforgettable day.",
                        color = SeoTextPrimary,
                        fontSize = 29.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 34.sp
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(38.dp)
                        .clickable { onNotificationsClick() },
                    shape = CircleShape,
                    color = SeoSurface,
                    border = BorderStroke(1.dp, SeoBorder)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_bell),
                            contentDescription = "Notifications",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Search Bar
            SeoSearchBar(
                query = "",
                onQueryChange = {},
                placeholder = "Search events, places, artists...",
                readOnly = true,
                onSearchBarClick = onSearchClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Explore by vibe
            Text(
                text = "Explore by vibe",
                color = SeoTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                VibeCategory.entries.forEach { vibe ->
                    val isSelected = vibe == selectedVibe
                    VibeCategoryCard(
                        vibe = vibe,
                        isSelected = isSelected,
                        onClick = { onVibeClick(vibe) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Featured events header
            SectionHeaderRow(
                title = "Featured events",
                actionText = "See all",
                onActionClick = onSeeAllClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Featured Event Card (shows live backend event when synced, or default seoulCultureNight)
            val featuredEvent = SampleEvents.exploreEvents.firstOrNull() ?: SampleEvents.seoulCultureNight
            FeaturedEventCard(
                event = featuredEvent,
                badgeText = "WEEKEND PICK",
                onClick = { onEventClick(featuredEvent) }
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Happening soon header
            SectionHeaderRow(
                title = "Happening soon",
                actionText = "See all",
                onActionClick = onSeeAllClick
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Happening soon card (shows second live backend event when synced, or artInTheCity)
            val happeningSoonEvent = SampleEvents.exploreEvents.getOrNull(1) ?: SampleEvents.artInTheCity
            EventHorizontalCard(
                event = happeningSoonEvent,
                onClick = { onEventClick(happeningSoonEvent) }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun VibeCategoryCard(
    vibe: VibeCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) SeoPrimaryLight else SeoSurface
    val borderColor = if (isSelected) SeoPrimaryBorder else SeoBorder
    val textColor = if (isSelected) SeoPrimary else SeoTextSecondary
    val iconTint = if (isSelected) SeoPrimary else SeoTextPrimary

    Surface(
        modifier = modifier
            .height(74.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = vibe.iconRes),
                contentDescription = vibe.label,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(9.dp))
            Text(
                text = vibe.label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
private fun SectionHeaderRow(
    title: String,
    actionText: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = SeoTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 22.sp
        )
        Text(
            text = actionText,
            color = SeoPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onActionClick() }
        )
    }
}

@Composable
private fun FeaturedEventCard(
    event: SeoEvent,
    badgeText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(176.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = event.imageRes),
            contentDescription = event.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.72f),
                            Color.Black
                        ),
                        startY = 80f
                    )
                )
        )
        // WEEKEND PICK badge
        Box(
            modifier = Modifier
                .padding(start = 16.dp, top = 17.dp)
                .height(25.dp)
                .clip(RoundedCornerShape(50))
                .background(SeoPrimaryLight)
                .padding(horizontal = 13.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = badgeText,
                color = SeoPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        // Bottom Event Title & Date
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, bottom = 24.dp, end = 18.dp)
        ) {
            Text(
                text = event.title,
                color = SeoSurface,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = event.shortDateLocation,
                color = SeoSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 15.sp
            )
        }
    }
}
