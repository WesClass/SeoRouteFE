package com.example.seoroute.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.R
import com.example.seoroute.model.EventTagStyle
import com.example.seoroute.model.SampleEvents
import com.example.seoroute.model.SeoEvent
import com.example.seoroute.ui.components.EventBadgePill
import com.example.seoroute.ui.theme.SeoBackground
import com.example.seoroute.ui.theme.SeoBorder
import com.example.seoroute.ui.theme.SeoGreenBg
import com.example.seoroute.ui.theme.SeoGreenText
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoPrimaryLight
import com.example.seoroute.ui.theme.SeoSurface
import com.example.seoroute.ui.theme.SeoTextPrimary
import com.example.seoroute.ui.theme.SeoTextSecondary
import java.text.NumberFormat
import java.util.Locale

// Confirm Booking Page
@Composable
fun ConfirmBookingScreen(
    event: SeoEvent = SampleEvents.futureSeoulMeetup,
    onBackClick: () -> Unit,
    onConfirmBooking: () -> Unit,
    modifier: Modifier = Modifier,
    submitting: Boolean = false,
    bookingError: String? = null
) {
    val isPaid = !event.isFree
    var agreedToTerms by remember(event.id, isPaid) { mutableStateOf(!isPaid) }
    val paidPrice = event.priceKrw?.takeIf { it > 0 }
    val total = if (!isPaid) "FREE" else paidPrice?.let {
        "₩" + NumberFormat.getIntegerInstance(Locale.KOREA).format(it)
    } ?: "Unavailable"
    val canConfirm = agreedToTerms && !submitting && (!isPaid || paidPrice != null)

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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(18.dp))

                // Top Header: Back arrow + "Confirm booking"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_header_back),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(
                        text = if (isPaid) "Review and pay" else "Confirm booking",
                        color = SeoTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 29.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Review your reservation",
                    color = SeoTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Event Summary Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = SeoSurface,
                    border = BorderStroke(1.dp, SeoBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = event.imageRes),
                                contentDescription = event.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(width = 90.dp, height = 84.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(vertical = 4.dp),
                                verticalArrangement = Arrangement.Top
                            ) {
                                val titleText = if (event.id == SampleEvents.futureSeoulMeetup.id) {
                                    "Future Seoul\nMeetup"
                                } else {
                                    event.title
                                }
                                Text(
                                    text = titleText,
                                    color = SeoTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 19.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${event.bookingShortDate} · ${event.bookingShortTime}",
                                    color = SeoTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        // FREE pill at bottom right
                        EventBadgePill(
                            tag = if (isPaid) EventTagStyle("PAID TICKET", SeoPrimary, SeoPrimaryLight)
                            else EventTagStyle("FREE", SeoGreenText, SeoGreenBg),
                            minWidth = 58.dp,
                            height = 23.dp,
                            horizontalPadding = 14.dp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 4.dp, bottom = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(34.dp))

                // Reservation details heading
                Text(
                    text = "Reservation details",
                    color = SeoTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Reservation details card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = SeoSurface,
                    border = BorderStroke(1.dp, SeoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                    ) {
                        // Row 1: General admission aligned right
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "General admission",
                                color = SeoTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        HorizontalDivider(thickness = 1.dp, color = SeoBorder)

                        // Row 2: Quantity / 1 guest
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Quantity",
                                color = SeoTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                            Text(
                                text = "1 guest",
                                color = SeoTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        HorizontalDivider(thickness = 1.dp, color = SeoBorder)

                        // Row 3: Total / FREE
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total",
                                color = SeoTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = total,
                                color = if (isPaid) SeoTextPrimary else SeoGreenText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // "Before you reserve" info banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SeoPrimaryLight)
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_info_circle),
                                contentDescription = "Info",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (isPaid) "Simulated payment" else "Before you reserve",
                                color = SeoTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (isPaid) "No real charge. A digital ticket will appear in My Tickets after confirmation."
                            else "A digital ticket will appear in My Tickets.",
                            color = SeoTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Present it at the venue for check-in.",
                            color = SeoTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Terms & Policies Checkbox Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { agreedToTerms = !agreedToTerms },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (agreedToTerms) SeoPrimary else SeoSurface)
                            .then(
                                if (!agreedToTerms) {
                                    Modifier.border(1.5.dp, SeoBorder, RoundedCornerShape(6.dp))
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (agreedToTerms) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_checkbox_check),
                                contentDescription = "Checked",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "I agree to the event terms and policies.",
                        color = SeoTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                val feedback = bookingError ?: if (isPaid && paidPrice == null) "Ticket price is unavailable." else null
                if (feedback != null) {
                    Text(feedback, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
                }
            }

            // Bottom CTA Button: "Confirm free reservation"
            Box(
                modifier = Modifier
                    .padding(top = 36.dp, bottom = 36.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (canConfirm) SeoPrimary else SeoTextSecondary)
                    .clickable(enabled = canConfirm) { onConfirmBooking() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        submitting -> "Confirming…"
                        isPaid && paidPrice != null -> "Simulate payment · $total"
                        isPaid -> "Payment unavailable"
                        else -> "Confirm free reservation"
                    },
                    color = SeoSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
