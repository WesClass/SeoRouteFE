package com.example.seoroute.ui.screens

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.R
import com.example.seoroute.model.DummyAuthRepository
import com.example.seoroute.model.GateStaffMember
import com.example.seoroute.model.OrganizerNavTab
import com.example.seoroute.model.OrganizerRepository
import com.example.seoroute.network.SeoRouteApiClient
import com.example.seoroute.ui.components.OrganizerBottomNavBar
import com.example.seoroute.ui.theme.SeoSurface
import kotlinx.coroutines.launch

private val OrgSettingsBg = Color(0xFFF8FAFC)
private val OrgSlateDark = Color(0xFF0F172A)
private val OrgSlateBody = Color(0xFF1E293B)
private val OrgSlateCaption = Color(0xFF64748B)
private val OrgSlateMuted = Color(0xFF94A3B8)
private val OrgCardBorder = Color(0xFFF1F5F9)
private val OrgPurple = Color(0xFF5E53F0)

/**
 * Organizer Profile & Ticketing Staff screen implementation (Figma node 126:671).
 * Reuses the modular [OrganizerBottomNavBar].
 */
@Composable
fun OrganizerProfileStaffScreen(
    selectedNavTab: OrganizerNavTab = OrganizerNavTab.PROFILE,
    onNavTabSelected: (OrganizerNavTab) -> Unit,
    onSignOutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val staffMembers = OrganizerRepository.gateStaffList
    val profile by OrganizerRepository.organizerProfile
    val currentUser = DummyAuthRepository.currentUser.value
    var hotlineNumber by remember(profile.supportPhone) { mutableStateOf(profile.supportPhone) }
    var staffActionFeedback by remember { mutableStateOf<String?>(null) }
    var scannerStatusMessage by remember { mutableStateOf<String?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val scannableEvents = OrganizerRepository.ticketingEvents.ifEmpty {
        OrganizerRepository.allOrganizerEvents
    }
    var selectedScanEventIndex by remember { mutableStateOf(0) }
    val activeScanEvent = scannableEvents.getOrNull(selectedScanEventIndex) ?: scannableEvents.firstOrNull()
    val stats = OrganizerRepository.activeEventStats.value
    val manifest = OrganizerRepository.activeEventManifest

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrgSettingsBg,
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
            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Header
            Text(
                text = "Organizer Settings",
                color = OrgSlateDark,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.65).sp,
                lineHeight = 34.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Seoul City Tourism & Culture Agency",
                color = OrgSlateCaption,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Agency Identity Row (Figma node 126:677)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEDEAFC)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SC",
                        color = OrgPurple,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.6).sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = profile.name,
                        color = OrgSlateDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFE6FBF2))
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "OFFICIAL ORGANIZER",
                            color = Color(0xFF0E9F6E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Section 1: AGENCY IDENTITY & CONTACT (Figma node 126:692)
            Text(
                text = "AGENCY IDENTITY & CONTACT",
                color = OrgSlateCaption,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.6.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SeoSurface,
                border = BorderStroke(1.dp, OrgCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Row 1: Business Registration
                    AgencyContactRow(
                        iconRes = R.drawable.ic_org_building,
                        title = "Business Registration",
                        subtitle = "Reg ID: ${profile.certificationNumber}",
                        subtitleColor = OrgSlateMuted,
                        trailingContent = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_org_chevron_right_slate),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )

                    HorizontalDivider(thickness = 1.dp, color = OrgCardBorder)

                    // Row 2: Public Support Email
                    AgencyContactRow(
                        iconRes = R.drawable.ic_org_mail,
                        title = "Public Support Email",
                        subtitle = profile.supportEmail,
                        subtitleColor = OrgSlateCaption,
                        trailingContent = {
                            Text(
                                text = "Synced",
                                color = OrgSlateMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    )

                    HorizontalDivider(thickness = 1.dp, color = OrgCardBorder)

                    // Row 3: Support Hotline
                    AgencyContactRow(
                        iconRes = R.drawable.ic_org_phone,
                        title = "Support Hotline",
                        subtitle = hotlineNumber,
                        subtitleColor = OrgSlateCaption,
                        trailingContent = {
                            Text(
                                text = "Edit",
                                color = OrgPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.clickable {
                                    val nextPhone = if (hotlineNumber == "+82 2-120-7700") {
                                        "+82 2-120-8800"
                                    } else {
                                        "+82 2-120-7700"
                                    }
                                    hotlineNumber = nextPhone
                                    coroutineScope.launch {
                                        SeoRouteApiClient.updateOrganizerProfile(
                                            name = profile.name,
                                            certificationNumber = profile.certificationNumber,
                                            supportEmail = profile.supportEmail,
                                            supportPhone = nextPhone
                                        ).onSuccess { updated ->
                                            OrganizerRepository.organizerProfile.value = updated
                                        }
                                    }
                                }
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // "Edit Organization Profile >" link aligned end
            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        staffActionFeedback = "Organization profile synced with ${profile.name}"
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Edit Organization Profile",
                    color = OrgPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.width(6.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_org_chevron_small_purple),
                    contentDescription = null,
                    modifier = Modifier.size(width = 6.dp, height = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: ON-SITE STAFF (Figma node 126:743)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ON-SITE STAFF",
                    color = OrgSlateCaption,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
                Text(
                    text = "${staffMembers.size} Active Devices",
                    color = OrgSlateMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Register accounts for venue gate staff to scan tickets offline on event day.",
                color = OrgSlateCaption,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Affiliation Notice & "+ Add Gate Staff" Banner (Figma node 126:751)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x99EEF2FF))
                    .border(1.dp, Color(0xFFE0E7FF), RoundedCornerShape(12.dp))
                    .padding(13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Domain: Seoul City",
                        color = Color(0xFF334155),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Box(
                    modifier = Modifier
                        .width(147.dp)
                        .clip(RoundedCornerShape(50))
                        .background(OrgPurple)
                        .clickable {
                            val candidateEmail = if (staffMembers.none {
                                    it.internalEmail.equals("gate2@hanganglive.example", ignoreCase = true)
                                }
                            ) {
                                "gate2@hanganglive.example"
                            } else {
                                "attendee2@example.com"
                            }
                            coroutineScope.launch {
                                SeoRouteApiClient.addOrganizerStaff(candidateEmail)
                                    .onSuccess { added ->
                                        if (staffMembers.none { it.id == added.id }) {
                                            staffMembers.add(added)
                                        }
                                        staffActionFeedback = "Added ${added.internalEmail} as ticketing staff"
                                    }
                                    .onFailure {
                                        OrganizerRepository.addDummyGateStaff()
                                        staffActionFeedback = "Added gate staff account"
                                    }
                            }
                        }
                        .padding(vertical = 6.dp, horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ Add Gate\nStaff",
                        color = SeoSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 16.sp,
                        letterSpacing = 0.3.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (staffActionFeedback != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = staffActionFeedback!!,
                    color = OrgPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scanner Staff Cards List (Figma node 126:758) with Delete Staff support (DELETE /organizer/staff/{id})
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                staffMembers.forEach { staff ->
                    val canRemove = staff.role != "organizer"
                    GateStaffItemCard(
                        staff = staff,
                        canDelete = canRemove,
                        onToggleReady = {
                            val idx = staffMembers.indexOfFirst { it.id == staff.id }
                            if (idx >= 0) {
                                val nextReady = !staff.isReady
                                staffMembers[idx] = staff.copy(
                                    isReady = nextReady,
                                    statusDetail = if (nextReady) "• Ready for scanning" else "• Standby mode"
                                )
                            }
                        },
                        onDeleteStaff = {
                            coroutineScope.launch {
                                SeoRouteApiClient.deleteOrganizerStaff(staff.id)
                                    .onSuccess {
                                        OrganizerRepository.removeGateStaffLocal(staff.id)
                                        staffActionFeedback = "Removed ${staff.internalEmail} from gate staff"
                                    }
                                    .onFailure {
                                        OrganizerRepository.removeGateStaffLocal(staff.id)
                                        staffActionFeedback = "Removed ${staff.internalEmail}"
                                    }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 3: EVENT-DAY GATE SCANNER (Works for both Organizer and Ticketing Staff roles)
            Text(
                text = "EVENT-DAY GATE SCANNER (ORGANIZER & STAFF)",
                color = OrgSlateCaption,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Signed in as ${currentUser?.email ?: "organizer@hanganglive.example"} (${(currentUser?.role ?: "organizer").uppercase()}). Both Organizer and Ticketing accounts can download offline manifests and check in attendees on event day.",
                color = OrgSlateCaption,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SeoSurface,
                border = BorderStroke(1.dp, OrgCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeScanEvent?.title ?: "No active event",
                                color = OrgSlateDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (stats != null) {
                                    "Live Check-in: ${stats.checkedIn}/${stats.total} checked in (${stats.notYet} remaining)"
                                } else {
                                    "Manifest cached: ${manifest.size} tickets ready for offline/online gate verification"
                                },
                                color = OrgSlateCaption,
                                fontSize = 12.sp
                            )
                        }

                        if (scannableEvents.size > 1) {
                            Text(
                                text = "Next Event",
                                color = OrgPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable {
                                        val nextIdx = (selectedScanEventIndex + 1) % scannableEvents.size
                                        selectedScanEventIndex = nextIdx
                                        val nextEvent = scannableEvents[nextIdx]
                                        coroutineScope.launch {
                                            OrganizerRepository.refreshTicketingManifestAndStats(nextEvent.id)
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Sync Manifest & Stats button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEEF2FF))
                                .clickable {
                                    val evt = activeScanEvent ?: return@clickable
                                    coroutineScope.launch {
                                        isScanning = true
                                        OrganizerRepository.refreshTicketingManifestAndStats(evt.id)
                                        isScanning = false
                                        val count = OrganizerRepository.activeEventManifest.size
                                        scannerStatusMessage =
                                            "Synced offline manifest for '${evt.title}' ($count tickets)"
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sync Manifest",
                                color = OrgPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Scan / Verify Next Attendee QR button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0E9F6E))
                                .clickable {
                                    val evt = activeScanEvent ?: return@clickable
                                    coroutineScope.launch {
                                        isScanning = true
                                        if (OrganizerRepository.activeEventManifest.isEmpty()) {
                                            OrganizerRepository.refreshTicketingManifestAndStats(evt.id)
                                        }
                                        val nextTicket = OrganizerRepository.activeEventManifest.firstOrNull {
                                            it.status == "registered"
                                        } ?: OrganizerRepository.activeEventManifest.firstOrNull()

                                        if (nextTicket != null) {
                                            val liveQrToken = SeoRouteApiClient.generateDynamicQrToken(
                                                ticketId = nextTicket.id,
                                                ticketSecret = nextTicket.ticketSecret
                                            )
                                            SeoRouteApiClient.checkInTicketOnline(
                                                eventId = evt.id,
                                                qrToken = liveQrToken
                                            ).onSuccess { scanRes ->
                                                OrganizerRepository.refreshTicketingManifestAndStats(evt.id)
                                                scannerStatusMessage =
                                                    "Gate Scan Result: ${scanRes.result.uppercase()} (Ticket #${scanRes.ticketId.take(8)})"
                                            }.onFailure { err ->
                                                scannerStatusMessage = "Scan error: ${err.message}"
                                            }
                                        } else {
                                            scannerStatusMessage =
                                                "No registered tickets in manifest for '${evt.title}' yet."
                                        }
                                        isScanning = false
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isScanning) "Scanning..." else "Scan Gate QR",
                                color = SeoSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (scannerStatusMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFECFDF5))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = scannerStatusMessage!!,
                                color = Color(0xFF047857),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Organizer / Gate Staff Log out button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable {
                        DummyAuthRepository.signOut()
                        onSignOutClick()
                    },
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFEECEB),
                border = BorderStroke(1.dp, Color(0xFFFECACA))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_prof_signout),
                        contentDescription = "Log out",
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log out of Organizer Studio",
                        color = Color(0xFFD92D20),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun AgencyContactRow(
    @DrawableRes iconRes: Int,
    title: String,
    subtitle: String,
    subtitleColor: Color,
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(OrgSettingsBg),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    color = OrgSlateBody,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 20.sp
                )
                Text(
                    text = subtitle,
                    color = subtitleColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 16.sp
                )
            }
        }
        trailingContent()
    }
}

@Composable
private fun GateStaffItemCard(
    staff: GateStaffMember,
    canDelete: Boolean = true,
    onToggleReady: () -> Unit,
    onDeleteStaff: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val iconBoxBg = if (staff.isReady) Color(0xFFECFDF5) else OrgCardBorder
    val iconRes = if (staff.isReady) R.drawable.ic_org_qr_green else R.drawable.ic_org_qr_gray
    val statusBadgeBg = if (staff.isReady) Color(0xFFECFDF5) else OrgCardBorder
    val statusBadgeText = if (staff.isReady) "Ready" else "Offline"
    val statusBadgeColor = if (staff.isReady) Color(0xFF047857) else Color(0xFF475569)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = SeoSurface,
        border = BorderStroke(1.dp, OrgCardBorder),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBoxBg),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = staff.gateTitle,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = staff.gateTitle,
                        color = OrgSlateBody,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = staff.internalEmail,
                        color = OrgSlateMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(statusBadgeBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = statusBadgeText,
                                color = statusBadgeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = staff.statusDetail,
                            color = OrgSlateMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (canDelete) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFEECEB))
                            .clickable { onDeleteStaff() }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Remove",
                            color = Color(0xFFD92D20),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable { onToggleReady() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_org_more_vert),
                        contentDescription = "Staff options",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
