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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.R
import com.example.seoroute.model.BookedTicket
import com.example.seoroute.model.EventTagStyle
import com.example.seoroute.model.NavTab
import com.example.seoroute.model.SampleEvents
import com.example.seoroute.model.SeoEvent
import com.example.seoroute.ui.components.EventBadgePill
import com.example.seoroute.ui.components.SelectableFilterPill
import com.example.seoroute.ui.components.SeoRouteBottomNavBar
import com.example.seoroute.ui.theme.SeoBackground
import com.example.seoroute.ui.theme.SeoBorder
import com.example.seoroute.ui.theme.SeoGreenBg
import com.example.seoroute.ui.theme.SeoGreenText
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoSurface
import com.example.seoroute.ui.theme.SeoTextPrimary
import com.example.seoroute.ui.theme.SeoTextSecondary

// Your Ticket Page
@Composable
fun MyTicketsScreen(
    selectedNavTab: NavTab = NavTab.TICKETS,
    onNavTabSelected: (NavTab) -> Unit,
    onViewDigitalTicketClick: (SeoEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Upcoming") }
    val ticketFilters = listOf("Upcoming", "Past", "Cancelled")

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
                text = "My tickets",
                color = SeoTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 29.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Everything you booked, all in one place",
                color = SeoTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Filter Pills: Upcoming, Past, Cancelled
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ticketFilters.forEach { tab ->
                    SelectableFilterPill(
                        text = tab,
                        selected = selectedFilter == tab,
                        onClick = { selectedFilter = tab },
                        unselectedBackgroundColor = SeoSurface,
                        height = 34.dp,
                        horizontalPadding = when (tab) {
                            "Upcoming" -> 20.dp
                            "Past" -> 21.dp
                            else -> 18.dp
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Ticket Cards List
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                SampleEvents.myTickets.forEach { ticket ->
                    if (ticket.isHeroLayout) {
                        HeroTicketCard(
                            ticket = ticket,
                            onClick = { onViewDigitalTicketClick(ticket.event) }
                        )
                    } else {
                        CompactTicketCard(
                            ticket = ticket,
                            onClick = { onViewDigitalTicketClick(ticket.event) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun HeroTicketCard(
    ticket: BookedTicket,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = SeoSurface,
        border = BorderStroke(1.dp, SeoBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            // Top Hero Banner with CONFIRMED badge overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Image(
                    painter = painterResource(id = ticket.event.imageRes),
                    contentDescription = ticket.event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp, top = 13.dp)
                        .height(23.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(50))
                        .background(SeoSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ticket.statusText,
                        color = SeoGreenText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Column(modifier = Modifier.padding(horizontal = 6.dp)) {
                Text(
                    text = ticket.event.title,
                    color = SeoTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = ticket.subtitleText,
                    color = SeoTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(thickness = 1.dp, color = SeoBorder)
                Spacer(modifier = Modifier.height(14.dp))

                // Footer row: Ticket icon + "View digital ticket" + Chevron
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_ticket_small),
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "View digital ticket",
                            color = SeoPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_chevron_purple),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactTicketCard(
    ticket: BookedTicket,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = SeoSurface,
        border = BorderStroke(1.dp, SeoBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 13.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = ticket.event.imageRes),
                    contentDescription = ticket.event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 86.dp, height = 78.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(13.dp))
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = ticket.event.title,
                        color = SeoTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = ticket.subtitleText,
                        color = SeoTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    EventBadgePill(
                        tag = EventTagStyle(ticket.statusText, SeoGreenText, SeoGreenBg),
                        minWidth = 95.dp,
                        height = 22.dp,
                        horizontalPadding = 14.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 6.dp)) {
                HorizontalDivider(thickness = 1.dp, color = SeoBorder)
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View digital ticket",
                        color = SeoPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_chevron_purple),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
