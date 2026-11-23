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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.R
import com.example.seoroute.model.SampleEvents
import com.example.seoroute.model.SeoEvent
import com.example.seoroute.ui.theme.SeoBackground
import com.example.seoroute.ui.theme.SeoBorder
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoPrimaryLight
import com.example.seoroute.ui.theme.SeoSurface
import com.example.seoroute.ui.theme.SeoTextPrimary
import com.example.seoroute.ui.theme.SeoTextSecondary

// Booking Page
@Composable
fun BookingSuccessScreen(
    event: SeoEvent = SampleEvents.futureSeoulMeetup,
    onViewMyTicketClick: () -> Unit,
    onBackToDiscoverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SeoBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(104.dp))

                // Double circle success checkmark badge (Figma nodes 109:531, 109:532, 109:533)
                Box(
                    modifier = Modifier
                        .size(168.dp)
                        .clip(CircleShape)
                        .background(SeoPrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(SeoPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_success_check),
                            contentDescription = "Booking confirmed",
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Title & Subtitle
                Text(
                    text = "You’re on the list!",
                    color = SeoTextPrimary,
                    fontSize = 29.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 35.sp
                )

                Spacer(modifier = Modifier.height(9.dp))

                Text(
                    text = "Your reservation was confirmed.",
                    color = SeoTextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Confirmed Event Summary Card (Figma node 109:537)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(138.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = SeoSurface,
                    border = BorderStroke(1.dp, SeoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = event.title.uppercase(),
                            color = SeoPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 14.sp
                        )

                        HorizontalDivider(thickness = 1.dp, color = SeoBorder)

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = event.bookingShortDate,
                                    color = SeoTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = event.bookingShortTime,
                                    color = SeoTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = event.venueName,
                                color = SeoTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Bottom Actions: "View my ticket" button + "Back to discover" link
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 52.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(53.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SeoPrimary)
                        .clickable { onViewMyTicketClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "View my ticket",
                        color = SeoSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Back to discover",
                    color = SeoPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable { onBackToDiscoverClick() }
                        .padding(vertical = 4.dp, horizontal = 12.dp)
                )
            }
        }
    }
}
