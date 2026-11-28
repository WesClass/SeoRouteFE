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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.R
import com.example.seoroute.model.AttendeeCheckInItem
import com.example.seoroute.model.DummyAuthRepository
import com.example.seoroute.model.OrganizerEventItem
import com.example.seoroute.model.OrganizerNavTab
import com.example.seoroute.model.OrganizerRepository
import com.example.seoroute.network.SeoRouteApiClient
import com.example.seoroute.ui.components.OrganizerBottomNavBar
import com.example.seoroute.ui.theme.SeoBackground
import com.example.seoroute.ui.theme.SeoBorder
import com.example.seoroute.ui.theme.SeoGreenBg
import com.example.seoroute.ui.theme.SeoGreenText
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoPrimaryLight
import com.example.seoroute.ui.theme.SeoSurface
import com.example.seoroute.ui.theme.SeoTextMuted
import com.example.seoroute.ui.theme.SeoTextPrimary
import com.example.seoroute.ui.theme.SeoTextSecondary
import kotlinx.coroutines.launch

/**
 * 16_Attendee_management 1 screen implementation (Figma node 109:1152).
 * When accessed by a `ticketing` (gate staff) account, Organizer Studio tabs (Overview, Events, Profile)
 * are hidden so gate staff only manage assigned event check-ins and scan tickets.
 */
@Composable
fun AttendeeManagementScreen(
    eventTitle: String = "Future Seoul Meetup",
    selectedNavTab: OrganizerNavTab = OrganizerNavTab.PEOPLE,
    onNavTabSelected: (OrganizerNavTab) -> Unit,
    onBackClick: () -> Unit,
    onOpenScannerClick: (AttendeeCheckInItem?) -> Unit = {},
    onSignOutClick: () -> Unit = {},
    isTicketingStaffOnly: Boolean = DummyAuthRepository.currentUser.value?.isTicketingStaffOnly == true,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val allAttendees = OrganizerRepository.attendeesList
    val assignedEvents = OrganizerRepository.getAvailableAssignedEvents()
    var selectedEventIdx by OrganizerRepository.selectedTicketingEventIndex
    val safeIdx = selectedEventIdx.coerceIn(0, (assignedEvents.size - 1).coerceAtLeast(0))
    val activeAssignedEvent: OrganizerEventItem = assignedEvents.getOrNull(safeIdx)
        ?: OrganizerRepository.createDefaultNewEvent()
    val displayedEventTitle = activeAssignedEvent.title.ifBlank { eventTitle }
    val stats = OrganizerRepository.activeEventStats.value

    val filteredAttendees = if (searchQuery.isBlank()) {
        allAttendees
    } else {
        allAttendees.filter { it.name.contains(searchQuery.trim(), ignoreCase = true) }
    }

    val checkedInCount = stats?.checkedIn ?: allAttendees.count { it.isCheckedIn }.coerceAtLeast(8)
    val totalCount = stats?.total?.takeIf { it > 0 }
        ?: activeAssignedEvent.bookedCount.takeIf { it > 0 }
        ?: 18
    val percentCheckedIn = if (totalCount > 0) ((checkedInCount * 100) / totalCount).coerceIn(0, 100) else 44
    val progressFraction = (percentCheckedIn / 100f).coerceIn(0.05f, 1f)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SeoBackground,
        bottomBar = {
            if (!isTicketingStaffOnly) {
                OrganizerBottomNavBar(
                    selectedTab = selectedNavTab,
                    onTabSelected = onNavTabSelected
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // Top Header: Back arrow (for Organizer) or Gate Staff badge + "Attendees" + Scan QR / Log out
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isTicketingStaffOnly) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_header_back),
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(22.dp)
                                .clickable { onBackClick() }
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                    }
                    Text(
                        text = "Attendees",
                        color = SeoTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 29.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(SeoPrimaryLight)
                            .clickable { onOpenScannerClick(null) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Scan QR",
                            color = SeoPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isTicketingStaffOnly) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFFEECEB))
                                .clickable {
                                    DummyAuthRepository.signOut()
                                    onSignOutClick()
                                }
                                .padding(horizontal = 11.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Log out",
                                color = Color(0xFFD92D20),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Assigned Event Selector Banner (shows which event the gate/ticketing staff is responsible for)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = SeoSurface,
                border = BorderStroke(1.dp, SeoBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(SeoGreenText)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ASSIGNED GATE EVENT",
                                color = SeoPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = displayedEventTitle,
                            color = SeoTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 18.sp
                        )
                        Text(
                            text = "${activeAssignedEvent.dateLabel} · ${activeAssignedEvent.venueName} · Capacity ${activeAssignedEvent.capacityLimit}",
                            color = SeoTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 14.sp
                        )
                    }

                    if (assignedEvents.size > 1) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SeoPrimaryLight)
                                .clickable {
                                    val nextIdx = (safeIdx + 1) % assignedEvents.size
                                    selectedEventIdx = nextIdx
                                    val nextEvt = assignedEvents[nextIdx]
                                    coroutineScope.launch {
                                        OrganizerRepository.refreshTicketingManifestAndStats(nextEvt.id)
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Switch Event (${safeIdx + 1}/${assignedEvents.size})",
                                color = SeoPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dark Navy CHECK-IN PROGRESS Card (Figma node 109:1170)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(101.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF232954)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 19.dp, vertical = 15.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "CHECK-IN PROGRESS",
                        color = Color(0xFFC9C9F5),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 13.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "$checkedInCount / $totalCount",
                            color = SeoSurface,
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 32.sp
                        )
                        Text(
                            text = "$percentCheckedIn% checked in",
                            color = SeoSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    // Progress track (#474D7A) and amber fill (#F5AD52)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF474D7A))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressFraction)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFF5AD52))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Search Attendees input box (Figma node 109:1176, height 44dp)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = SeoSurface,
                border = BorderStroke(1.dp, SeoBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 17.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search",
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search attendees...",
                                color = SeoTextMuted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = SeoTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = SolidColor(SeoPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // "Confirmed attendees" header + "18 total"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Confirmed attendees",
                    color = SeoTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 21.sp
                )
                Text(
                    text = "$totalCount total",
                    color = SeoTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Attendee Rows List (Figma nodes 109:1183..109:1221)
            Column(modifier = Modifier.fillMaxWidth()) {
                filteredAttendees.forEachIndexed { index, attendee ->
                    AttendeeListRow(
                        attendee = attendee,
                        onRowClick = {
                            OrganizerRepository.lastScannedAttendee.value = attendee
                            onOpenScannerClick(attendee)
                        }
                    )
                    if (index < filteredAttendees.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 59.dp),
                            thickness = 1.dp,
                            color = SeoBorder
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun AttendeeListRow(
    attendee: AttendeeCheckInItem,
    onRowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarBg = if (attendee.avatarColorIsWarm) Color(0xFFFFF0E8) else SeoPrimaryLight
    val avatarTextColor = if (attendee.avatarColorIsWarm) Color(0xFFFF745D) else SeoPrimary
    val statusColor = if (attendee.isCheckedIn) SeoGreenText else SeoTextSecondary
    val trailingIcon = if (attendee.isCheckedIn) R.drawable.ic_notif_check else R.drawable.ic_attendee_clock

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { onRowClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(avatarBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = attendee.initial,
                    color = avatarTextColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(13.dp))

            Column {
                Text(
                    text = attendee.name,
                    color = SeoTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = attendee.statusText,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 15.sp
                )
            }
        }

        Image(
            painter = painterResource(id = trailingIcon),
            contentDescription = attendee.statusText,
            modifier = Modifier
                .padding(end = 17.dp)
                .size(19.dp)
        )
    }
}

// 6x16 QR pattern blocks from Figma node 109:1282..109:1353
private val SCANNER_QR_ROWS = listOf(
    listOf(0, 1, 6, 7, 12, 13),
    listOf(0, 3, 6, 9, 12, 15),
    listOf(1, 4, 7, 10, 13, 16),
    listOf(3, 4, 9, 10, 15, 16),
    listOf(0, 1, 6, 7, 12, 13),
    listOf(0, 3, 6, 9, 12, 15),
    listOf(1, 4, 7, 10, 13, 16),
    listOf(3, 4, 9, 10, 15, 16),
    listOf(0, 1, 6, 7, 12, 13),
    listOf(0, 3, 6, 9, 12, 15),
    listOf(1, 4, 7, 10, 13, 16),
    listOf(3, 4, 9, 10, 15, 16)
)

/**
 * 17_Check_in_scanner 1 screen implementation (Figma node 109:1242).
 * Connects to live backend `POST /api/v1/ticketing/events/{id}/check-in` or transitions to 18_Check_in_result 1.
 */
@Composable
fun CheckInScannerScreen(
    onBackClick: () -> Unit,
    onScanSuccess: (AttendeeCheckInItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isScanning by remember { mutableStateOf(false) }
    val assignedEvent = OrganizerRepository.getCurrentAssignedEvent()

    val triggerCheckIn: () -> Unit = {
        if (!isScanning) {
            isScanning = true
            coroutineScope.launch {
                val targetAttendee = OrganizerRepository.attendeesList.firstOrNull { !it.isCheckedIn }
                    ?: OrganizerRepository.lastScannedAttendee.value
                val activeEvent = OrganizerRepository.getCurrentAssignedEvent()

                if (!SeoRouteApiClient.accessToken.isNullOrBlank()) {
                    val manifestTicket = OrganizerRepository.activeEventManifest.firstOrNull {
                        it.status == "registered"
                    } ?: OrganizerRepository.activeEventManifest.firstOrNull()

                    if (manifestTicket != null) {
                        val qrToken = SeoRouteApiClient.generateDynamicQrToken(
                            ticketId = manifestTicket.id,
                            ticketSecret = manifestTicket.ticketSecret
                        )
                        SeoRouteApiClient.checkInTicketOnline(
                            eventId = activeEvent.id,
                            qrToken = qrToken
                        )
                        OrganizerRepository.refreshTicketingManifestAndStats(activeEvent.id)
                    }
                }

                val checkedIn = OrganizerRepository.markAttendeeCheckedInLocal(targetAttendee.id)
                isScanning = false
                onScanSuccess(checkedIn)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF232954))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(18.dp))

                // Top Header: White Back Arrow + "Scan tickets"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_header_back_white),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(
                        text = "Scan tickets",
                        color = SeoSurface,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Point the camera at the guest QR code.",
                    color = Color(0xFFD1D1E7),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Scanning for: ${assignedEvent.title} (${assignedEvent.venueName})",
                    color = Color(0xFFC9C9F5),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Camera Viewfinder Container (Figma node 109:1271, 342x425dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(425.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF3E456B))
                        .clickable { triggerCheckIn() }
                ) {
                    // Decorative background circles inside viewfinder (Figma nodes 109:1272 & 109:1273)
                    Box(
                        modifier = Modifier
                            .offset(x = 1.dp, y = 59.dp)
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                    )
                    Box(
                        modifier = Modifier
                            .offset(x = 145.dp, y = 170.dp)
                            .size(256.dp)
                            .clip(CircleShape)
                            .background(Color(0x18FFFFFF))
                    )

                    // 4 Corner Brackets + Center QR Dot Pattern
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val bracketColor = Color(0xFFF3F4FA)
                        val strokePx = 5.dp.toPx()
                        val armLen = 37.dp.toPx()

                        val leftX = 54.dp.toPx()
                        val rightX = size.width - 54.dp.toPx()
                        val topY = 72.dp.toPx()
                        val bottomY = 354.dp.toPx()

                        // Top-left L bracket
                        drawLine(
                            color = bracketColor,
                            start = Offset(leftX, topY),
                            end = Offset(leftX + armLen, topY),
                            strokeWidth = strokePx,
                            cap = StrokeCap.Square
                        )
                        drawLine(
                            color = bracketColor,
                            start = Offset(leftX, topY),
                            end = Offset(leftX, topY + armLen),
                            strokeWidth = strokePx,
                            cap = StrokeCap.Square
                        )

                        // Top-right L bracket
                        drawLine(
                            color = bracketColor,
                            start = Offset(rightX - armLen, topY),
                            end = Offset(rightX, topY),
                            strokeWidth = strokePx,
                            cap = StrokeCap.Square
                        )
                        drawLine(
                            color = bracketColor,
                            start = Offset(rightX, topY),
                            end = Offset(rightX, topY + armLen),
                            strokeWidth = strokePx,
                            cap = StrokeCap.Square
                        )

                        // Bottom-left L bracket
                        drawLine(
                            color = bracketColor,
                            start = Offset(leftX, bottomY),
                            end = Offset(leftX + armLen, bottomY),
                            strokeWidth = strokePx,
                            cap = StrokeCap.Square
                        )
                        drawLine(
                            color = bracketColor,
                            start = Offset(leftX, bottomY - armLen),
                            end = Offset(leftX, bottomY),
                            strokeWidth = strokePx,
                            cap = StrokeCap.Square
                        )

                        // Bottom-right L bracket
                        drawLine(
                            color = bracketColor,
                            start = Offset(rightX - armLen, bottomY),
                            end = Offset(rightX, bottomY),
                            strokeWidth = strokePx,
                            cap = StrokeCap.Square
                        )
                        drawLine(
                            color = bracketColor,
                            start = Offset(rightX, bottomY - armLen),
                            end = Offset(rightX, bottomY),
                            strokeWidth = strokePx,
                            cap = StrokeCap.Square
                        )

                        // Center QR code dots (Figma nodes 109:1282..109:1353)
                        val dotSize = 7.dp.toPx()
                        val stepX = 8.dp.toPx()
                        val startX = (size.width - (17 * stepX)) / 2f
                        val startY = 153.dp.toPx()
                        val rowYOffsets = listOf(0f, 8f, 24f, 32f, 48f, 56f, 72f, 80f, 96f, 104f, 120f, 128f)

                        SCANNER_QR_ROWS.forEachIndexed { rIdx, cols ->
                            val y = startY + rowYOffsets[rIdx].dp.toPx()
                            cols.forEach { col ->
                                drawRoundRect(
                                    color = Color(0xFFD6DAE8),
                                    topLeft = Offset(startX + col * stepX, y),
                                    size = Size(dotSize, dotSize),
                                    cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // "Secure QR verification" pill card (Figma node 109:1354, 284x65dp centered)
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .width(284.dp)
                            .height(65.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF3E456B)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 22.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_shield_check_white),
                                contentDescription = "Secure QR verification",
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(13.dp))
                            Column {
                                Text(
                                    text = "Secure QR verification",
                                    color = SeoSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tickets are validated online",
                                    color = Color(0xFFD1D1E7),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // Bottom "Enter ticket code manually" white button (Figma node 109:1360)
            Box(
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 36.dp)
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SeoSurface)
                    .clickable { triggerCheckIn() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isScanning) "Validating ticket..." else "Enter ticket code manually",
                    color = Color(0xFF252C5C),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * 18_Check_in_result 1 screen implementation (Figma node 109:1362).
 */
@Composable
fun CheckInResultScreen(
    attendeeName: String = "Jin Park",
    eventTitle: String = "Future Seoul Meetup",
    onScanNextTicketClick: () -> Unit,
    onBackToAttendeesClick: () -> Unit,
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
                Spacer(modifier = Modifier.height(110.dp))

                // Double Circle Green Checkmark Badge (Figma nodes 109:1375, 109:1376, 109:1377)
                Box(
                    modifier = Modifier
                        .size(152.dp)
                        .clip(CircleShape)
                        .background(SeoGreenBg),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .clip(CircleShape)
                            .background(SeoGreenText),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_success_check),
                            contentDescription = "Check-in successful",
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(34.dp))

                Text(
                    text = "Check-in successful",
                    color = SeoTextPrimary,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "This ticket is now marked as used.",
                    color = SeoTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Attendee & Event Details Card (Figma node 109:1381, 342x167dp)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(167.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = SeoSurface,
                    border = BorderStroke(1.dp, SeoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "ATTENDEE",
                                color = SeoTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 13.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = attendeeName,
                                color = SeoTextPrimary,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 25.sp
                            )
                        }

                        HorizontalDivider(thickness = 1.dp, color = SeoBorder)

                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Event",
                                    color = SeoTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                Text(
                                    text = eventTitle,
                                    color = SeoTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Status",
                                    color = SeoTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                Box(
                                    modifier = Modifier
                                        .width(104.dp)
                                        .height(25.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(SeoGreenBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "CHECKED IN",
                                        color = SeoGreenText,
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

            // Bottom Actions: "Scan next ticket" primary button + "Back to attendees" link
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp, bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(53.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SeoPrimary)
                        .clickable { onScanNextTicketClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Scan next ticket",
                        color = SeoSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(17.dp))

                Text(
                    text = "Back to attendees",
                    color = SeoPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable { onBackToAttendeesClick() }
                        .padding(vertical = 4.dp, horizontal = 12.dp)
                )
            }
        }
    }
}
