package com.example.seoroute.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.R
import com.example.seoroute.model.NavTab
import com.example.seoroute.model.SampleEvents
import com.example.seoroute.model.SeoEvent
import com.example.seoroute.ui.components.EventHorizontalCard
import com.example.seoroute.ui.components.SeoRouteBottomNavBar
import com.example.seoroute.ui.theme.SeoBackground
import com.example.seoroute.ui.theme.SeoPrimaryLight
import com.example.seoroute.ui.theme.SeoTextPrimary
import com.example.seoroute.ui.theme.SeoTextSecondary

// Your Saved Events Page
@Composable
fun SavedEventsScreen(
    selectedNavTab: NavTab = NavTab.SAVED,
    onNavTabSelected: (NavTab) -> Unit,
    onEventClick: (SeoEvent) -> Unit,
    onFilterClick: () -> Unit = {},
    modifier: Modifier = Modifier
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

            // Title & Subtitle
            Text(
                text = "Event Reservations",
                color = SeoTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 29.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your shortlist for exploring Seoul",
                color = SeoTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // "7 saved events" + Filter sliders icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "7 saved events",
                    color = SeoTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onFilterClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_filter_sliders),
                        contentDescription = "Filter saved events",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Saved Events Cards
            Column(
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                SampleEvents.savedEvents.forEach { event ->
                    EventHorizontalCard(
                        event = event,
                        badgeOverride = event.savedListBadge,
                        onClick = { onEventClick(event) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(35.dp))

            // Curated city banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SeoPrimaryLight)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_sparkle_purple),
                    contentDescription = null,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "A curated city, just for you.",
                        color = SeoTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Save events to plan a perfect day.",
                        color = SeoTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
