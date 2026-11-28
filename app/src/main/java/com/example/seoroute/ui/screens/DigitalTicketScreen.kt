package com.example.seoroute.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.R
import com.example.seoroute.model.SampleEvents
import com.example.seoroute.model.SeoEvent
import com.example.seoroute.network.SeoRouteApiClient
import com.example.seoroute.ui.theme.SeoBackground
import com.example.seoroute.ui.theme.SeoBorder
import com.example.seoroute.ui.theme.SeoGreenBg
import com.example.seoroute.ui.theme.SeoGreenText
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoSurface
import com.example.seoroute.ui.theme.SeoTextPrimary
import com.example.seoroute.ui.theme.SeoTextSecondary
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.delay
import kotlin.math.floor

// Your Digital Ticket Page
@Composable
fun DigitalTicketScreen(
    event: SeoEvent = SampleEvents.futureSeoulMeetup,
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ticket = SampleEvents.myTickets.firstOrNull { it.event.id == event.id }
    val session = SeoRouteApiClient.accessToken
    var qrToken by remember(ticket?.id, session) { mutableStateOf<String?>(null) }
    var qrError by remember(ticket?.id, session) { mutableStateOf<String?>(null) }
    var retry by remember(ticket?.id, session) { mutableStateOf(0) }

    LaunchedEffect(ticket?.id, session, retry) {
        qrError = null
        qrToken = null
        if (ticket == null || session.isNullOrBlank()) {
            qrError = "Ticket QR unavailable."
            return@LaunchedEffect
        }
        val payload = SeoRouteApiClient.fetchTicketQr(ticket.id).getOrElse {
            qrError = "Could not load the ticket QR."
            return@LaunchedEffect
        }
        if (payload.ticketId != ticket.id || payload.ticketSecret.isBlank() || payload.periodSecs <= 0 ||
            payload.algorithm != "HMAC-SHA256"
        ) {
            qrError = "The server returned invalid ticket QR data."
            return@LaunchedEffect
        }
        while (true) {
            val now = System.currentTimeMillis()
            qrToken = SeoRouteApiClient.generateDynamicQrToken(
                ticketId = payload.ticketId,
                ticketSecret = payload.ticketSecret,
                periodSecs = payload.periodSecs,
                unixTimeSeconds = now / 1000L
            )
            val millisToNextCode = (payload.periodSecs - now / 1000L % payload.periodSecs) * 1000L - now % 1000L
            delay(millisToNextCode)
        }
    }

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

                // Top Header: Back arrow + "Your digital ticket"
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
                        text = "Your digital ticket",
                        color = SeoTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 29.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Present this at the venue entrance",
                    color = SeoTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Main Digital Ticket Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = SeoSurface,
                    border = BorderStroke(1.dp, SeoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 13.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Hero image with CONFIRMED pill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(111.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            Image(
                                painter = painterResource(id = event.imageRes),
                                contentDescription = event.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .padding(start = 12.dp, top = 14.dp)
                                    .height(23.dp)
                                    .width(98.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(SeoSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "CONFIRMED",
                                    color = SeoGreenText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(21.dp))

                        // Event Title & Date/Time aligned start
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 9.dp)
                        ) {
                            Text(
                                text = event.title,
                                color = SeoTextPrimary,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 25.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = event.ticketDateTime,
                                color = SeoTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            HorizontalDivider(thickness = 1.dp, color = SeoBorder)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // QR Code Container
                        Surface(
                            modifier = Modifier.size(195.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = SeoSurface,
                            border = BorderStroke(1.dp, SeoBorder)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val token = qrToken
                                if (token != null) {
                                    TicketQrCodeCanvas(token, Modifier.size(175.dp))
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(qrError ?: "Loading ticket QR…", textAlign = TextAlign.Center)
                                        if (qrError != null && ticket != null && !session.isNullOrBlank()) {
                                            androidx.compose.material3.TextButton(onClick = { retry++ }) {
                                                Text("Retry")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Booking Code
                        Text(
                            text = event.bookingCode,
                            color = SeoTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(13.dp))

                        // "Valid for one check-in" green pill
                        Box(
                            modifier = Modifier
                                .width(183.dp)
                                .height(29.dp)
                                .clip(RoundedCornerShape(50))
                                .background(SeoGreenBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Valid for one check-in",
                                color = SeoGreenText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Do not share your ticket QR publicly.",
                            color = SeoTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // Bottom "Done" Button
            Box(
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 36.dp)
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SeoPrimary)
                    .clickable { onDoneClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Done",
                    color = SeoSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun TicketQrCodeCanvas(token: String, modifier: Modifier = Modifier) {
    // ZXing includes the four-module quiet zone in the returned matrix.
    val matrix = remember(token) { QRCodeWriter().encode(token, BarcodeFormat.QR_CODE, 0, 0) }
    Canvas(modifier = modifier.semantics { contentDescription = "Ticket check-in QR code" }) {
        drawRect(Color.White)
        val moduleSize = floor(size.minDimension / matrix.width)
        val left = floor((size.width - matrix.width * moduleSize) / 2)
        val top = floor((size.height - matrix.height * moduleSize) / 2)
        for (row in 0 until matrix.height) {
            for (column in 0 until matrix.width) {
                if (matrix[column, row]) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(left + column * moduleSize, top + row * moduleSize),
                        size = Size(moduleSize, moduleSize)
                    )
                }
            }
        }
    }
}
