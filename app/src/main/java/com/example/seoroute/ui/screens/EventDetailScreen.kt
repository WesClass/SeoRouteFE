package com.example.seoroute.ui.screens

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.R
import com.example.seoroute.model.EventDetailType
import com.example.seoroute.model.SampleEvents
import com.example.seoroute.model.SeoEvent
import com.example.seoroute.ui.components.EventBadgePill
import com.example.seoroute.ui.components.IllustrativeLocationMap
import com.example.seoroute.ui.components.OrganizerSummaryCard
import com.example.seoroute.ui.theme.SeoBorder
import com.example.seoroute.ui.theme.SeoFilterChipBg
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoSurface
import com.example.seoroute.ui.theme.SeoTextPrimary
import com.example.seoroute.ui.theme.SeoTextSecondary

// Public Event Detail
@Composable
fun PublicEventDetailScreen(
    event: SeoEvent = SampleEvents.seoulCultureNight,
    onBackClick: () -> Unit,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    EventDetailScreen(
        event = event,
        onBackClick = onBackClick,
        onActionClick = onActionClick,
        modifier = modifier
    )
}

// Managed Event Detail
@Composable
fun ManagedEventDetailScreen(
    event: SeoEvent = SampleEvents.futureSeoulMeetup,
    onBackClick: () -> Unit,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    EventDetailScreen(
        event = event,
        onBackClick = onBackClick,
        onActionClick = onActionClick,
        modifier = modifier
    )
}

// Your Event Detail Page
@Composable
fun EventDetailScreen(
    event: SeoEvent,
    onBackClick: () -> Unit,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isSaved by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SeoSurface,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SeoSurface)
                    .navigationBarsPadding()
            ) {
                HorizontalDivider(thickness = 1.dp, color = SeoBorder)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 22.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SeoPrimary)
                            .clickable {
                                onActionClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = event.ctaButtonText,
                            color = SeoSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Hero Image + overlapping rounded content card
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = event.imageRes),
                    contentDescription = event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(330.dp)
                )

                // Top navigation buttons: Back (left) and Heart/Save (right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 22.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeroActionIconButton(
                        iconRes = R.drawable.ic_detail_back,
                        contentDescription = "Back",
                        onClick = onBackClick
                    )
                    HeroActionIconButton(
                        iconRes = R.drawable.ic_detail_heart,
                        contentDescription = "Save event",
                        tint = if (isSaved) SeoPrimary else SeoTextPrimary,
                        onClick = { isSaved = !isSaved }
                    )
                }

                // Rounded white detail body starting at 311.dp (overlapping hero image by ~20.dp)
                Surface(
                    modifier = Modifier
                        .padding(top = 310.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
                    color = SeoSurface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp)
                    ) {
                        // Event Tags row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            event.detailTags.forEach { tag ->
                                val pillMinWidth = when (tag.text) {
                                    "Music", "Outdoor" -> 112.dp
                                    "Free booking" -> 102.dp
                                    "Festival" -> 91.dp
                                    "Free open" -> 84.dp
                                    "Indoor" -> 63.dp
                                    else -> 76.dp
                                }
                                EventBadgePill(
                                    tag = tag,
                                    minWidth = pillMinWidth,
                                    height = 26.dp,
                                    horizontalPadding = 16.dp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Title
                        Text(
                            text = event.title,
                            color = SeoTextPrimary,
                            fontSize = if (event.detailType == EventDetailType.PUBLIC) 27.sp else 26.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 32.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Subtitle
                        Text(
                            text = event.subtitle,
                            color = SeoTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Metadata Rows
                        EventMetadataRow(
                            iconRes = R.drawable.ic_calendar,
                            text = event.fullDate
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (event.detailType == EventDetailType.PUBLIC) {
                            event.timeRange?.let { time ->
                                EventMetadataRow(
                                    iconRes = R.drawable.ic_detail_clock,
                                    text = time
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            EventMetadataRow(
                                iconRes = R.drawable.ic_detail_location,
                                text = event.fullLocation
                            )
                        } else {
                            EventMetadataRow(
                                iconRes = R.drawable.ic_detail_location,
                                text = event.fullLocation
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            val reserved = event.reservedSpots ?: 18
                            val total = event.totalSpots ?: 25
                            EventMetadataRow(
                                iconRes = R.drawable.ic_detail_users,
                                text = "$reserved / $total spots reserved"
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            SpotsProgressBar(
                                reserved = reserved,
                                total = total
                            )
                        }

                        Spacer(modifier = Modifier.height(22.dp))
                        HorizontalDivider(thickness = 1.dp, color = SeoBorder)
                        Spacer(modifier = Modifier.height(20.dp))

                        // Section Title: "About this event" or "Event information"
                        Text(
                            text = event.detailSectionTitle,
                            color = SeoTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 21.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        event.descriptionLines.forEachIndexed { index, line ->
                            Text(
                                text = line,
                                color = SeoTextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 18.sp
                            )
                            if (index < event.descriptionLines.lastIndex) {
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Location section
                        Text(
                            text = "Location",
                            color = SeoTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 21.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        IllustrativeLocationMap(
                            venueName = event.venueName,
                            districtCaption = event.districtCaption,
                            highlightBlockColor = event.mapHighlightColor
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Organizer Summary Card
                        OrganizerSummaryCard(
                            monogram = event.organizerMonogram,
                            name = event.organizerName,
                            subtitle = event.organizerSubtitle
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroActionIconButton(
    @DrawableRes iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = SeoTextPrimary
) {
    Surface(
        modifier = modifier
            .size(38.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(13.dp),
        color = SeoSurface.copy(alpha = 0.92f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EventMetadataRow(
    @DrawableRes iconRes: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = text,
            color = SeoTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun SpotsProgressBar(
    reserved: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val fraction = (reserved.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50))
            .background(SeoFilterChipBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(SeoPrimary)
        )
    }
}
