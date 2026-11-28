package com.example.seoroute.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.R
import com.example.seoroute.model.OrganizerEventItem
import com.example.seoroute.model.OrganizerEventStatus
import com.example.seoroute.model.OrganizerRepository
import com.example.seoroute.ui.theme.SeoSurface
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val SlateBg = Color(0xFFF8FAFC)
private val SlateBorder = Color(0xFFE2E8F0)
private val SlateCardBorder = Color(0xFFE8EDF5)
private val SlateHeading = Color(0xFF1E2434)
private val SlateInputText = Color(0xFF334155)
private val SlateMutedText = Color(0xFF475569)
private val SlateCaption = Color(0xFF64748B)
private val SlateSubtle = Color(0xFF94A3B8)
private val SlatePillBg = Color(0xFFF1F5F9)
private val OrganizerBrandPurple = Color(0xFF5B50F6)
private val SeoulTimeZone = ZoneId.of("Asia/Seoul")
private val EventDateFormat = DateTimeFormatter.ofPattern("MMM d, uuuu", Locale.ENGLISH)
private val EventTimeFormat = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

@Composable
private fun ScheduleDateField(label: String, value: String, onSelected: (String) -> Unit) {
    val context = LocalContext.current
    SchedulePickerField(label, value) {
        val date = runCatching { LocalDate.parse(value, EventDateFormat) }
            .getOrElse { LocalDate.now(SeoulTimeZone) }
        DatePickerDialog(
            context,
            { _, year, month, day ->
                onSelected(LocalDate.of(year, month + 1, day).format(EventDateFormat))
            },
            date.year,
            date.monthValue - 1,
            date.dayOfMonth
        ).show()
    }
}

@Composable
private fun ScheduleTimeField(label: String, value: String, onSelected: (String) -> Unit) {
    val context = LocalContext.current
    SchedulePickerField(label, value) {
        val time = runCatching { LocalTime.parse(value, EventTimeFormat) }
            .getOrElse { LocalTime.now(SeoulTimeZone) }
        TimePickerDialog(
            context,
            { _, hour, minute -> onSelected(LocalTime.of(hour, minute).format(EventTimeFormat)) },
            time.hour,
            time.minute,
            android.text.format.DateFormat.is24HourFormat(context)
        ).show()
    }
}

@Composable
private fun SchedulePickerField(label: String, value: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = SlateBg,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SlateBorder)
    ) {
        Text(
            text = "$label: $value",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            color = SlateInputText,
            fontSize = 12.sp
        )
    }
}
/**
 * Create/Edit Event screen implementation (Figma node 126:911).
 */
@Composable
fun CreateEditEventScreen(
    initialEvent: OrganizerEventItem = OrganizerRepository.createDefaultNewEvent(),
    onBackClick: () -> Unit,
    onSaveAndPublish: (OrganizerEventItem) -> Unit,
    onDeleteEvent: (String) -> Unit,
    modifier: Modifier = Modifier,
    saving: Boolean = false,
    saveError: String? = null,
    showDeleteAction: Boolean = true
) {
    var title by remember(initialEvent.id) { mutableStateOf(initialEvent.title) }
    var shortSubtitle by remember(initialEvent.id) { mutableStateOf(initialEvent.shortSubtitle) }
    val isNewEvent = initialEvent.id.startsWith("org_new_")
    var isDateRange by remember(initialEvent.id) { mutableStateOf(!isNewEvent && initialEvent.isDateRange) }
    var dateText by remember(initialEvent.id) {
        val date = if (isNewEvent) LocalDate.now(SeoulTimeZone).plusDays(1).format(EventDateFormat)
        else initialEvent.dateRangeText
        mutableStateOf(date)
    }
    var startTime by remember(initialEvent.id) { mutableStateOf(initialEvent.startTime) }
    var endTime by remember(initialEvent.id) { mutableStateOf(initialEvent.endTime) }
    var venueName by remember(initialEvent.id) { mutableStateOf(initialEvent.venueName) }
    var isOutdoor by remember(initialEvent.id) { mutableStateOf(initialEvent.isOutdoor) }
    var admissionMode by remember(initialEvent.id) { mutableStateOf(initialEvent.admissionMode) }
    var capacityText by remember(initialEvent.id) { mutableStateOf(initialEvent.capacityLimit.toString()) }
    var eventStatus by remember(initialEvent.id) { mutableStateOf(initialEvent.status) }
    var coverPreviewRes by remember(initialEvent.id) { mutableStateOf(initialEvent.coverPreviewRes) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SlateBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateBg)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onBackClick() },
                    shape = CircleShape,
                    color = SeoSurface,
                    border = BorderStroke(1.dp, SlatePillBg),
                    shadowElevation = 1.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_org_chevron_back),
                            contentDescription = "Go back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Create/Edit Event",
                    color = SlateHeading,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SeoSurface.copy(alpha = 0.96f))
                    .navigationBarsPadding()
            ) {
                HorizontalDivider(thickness = 1.dp, color = SlateBorder)
                if (saveError != null) {
                    Text(saveError, color = Color(0xFFB91C1C), modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Delete Event Button (Figma node 129:1181)
                    if (showDeleteAction) Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFFFF1F2))
                            .clickable { onDeleteEvent(initialEvent.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_org_trash),
                            contentDescription = "Delete Event",
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Save & Publish Primary Action Button (Figma node 126:1071)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(OrganizerBrandPurple)
                            .clickable(enabled = !saving) {
                                val parsedCap = capacityText.toIntOrNull() ?: initialEvent.capacityLimit
                                val shortDate = dateText.substringBefore(",").trim().ifEmpty { initialEvent.dateLabel }
                                onSaveAndPublish(
                                    initialEvent.copy(
                                        title = title.ifBlank { "Untitled Event" },
                                        shortSubtitle = shortSubtitle,
                                        dateLabel = shortDate,
                                        dateRangeText = dateText,
                                        isDateRange = isDateRange,
                                        startTime = startTime,
                                        endTime = endTime,
                                        venueName = venueName,
                                        isOutdoor = isOutdoor,
                                        admissionMode = admissionMode,
                                        capacityLimit = parsedCap,
                                        status = eventStatus,
                                        coverPreviewRes = coverPreviewRes
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (saving) "Saving…" else "Save & Publish Event",
                            color = SeoSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.35.sp,
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
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. COVER PHOTO Section (Figma node 126:924)
            EditorSectionCard {
                EditorSectionLabel(text = "COVER PHOTO")
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFC7D2FE), RoundedCornerShape(12.dp))
                        .clickable {
                            coverPreviewRes = if (coverPreviewRes == R.drawable.org_cover_preview) {
                                R.drawable.event_future_seoul
                            } else {
                                R.drawable.org_cover_preview
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(id = coverPreviewRes),
                        contentDescription = "Event cover preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Subtle dark scrim matching rgba(15,23,42,0.3)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x4D0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(SeoSurface.copy(alpha = 0.95f))
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_org_camera),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Change Image",
                                color = Color(0xFF1E293B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // 2. EVENT TITLE & SHORT SUBTITLE Section (Figma node 126:937)
            EditorSectionCard {
                EditorSectionLabel(text = "EVENT TITLE")
                Spacer(modifier = Modifier.height(6.dp))
                EditorTextInputBox(
                    value = title,
                    onValueChange = { title = it },
                    textSizeSp = 14,
                    textColor = SlateHeading
                )

                Spacer(modifier = Modifier.height(14.dp))

                EditorSectionLabel(text = "SHORT SUBTITLE")
                Spacer(modifier = Modifier.height(6.dp))
                EditorTextInputBox(
                    value = shortSubtitle,
                    onValueChange = { shortSubtitle = it },
                    textSizeSp = 12,
                    textColor = SlateMutedText
                )
            }

            // 3. DATE & SCHEDULE Section (Figma node 126:950)
            EditorSectionCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EditorSectionLabel(text = "DATE & SCHEDULE")

                    // Single Day / Date Range segmented toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SlatePillBg)
                            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                            .padding(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ScheduleModeButton(
                            text = "Single Day",
                            selected = !isDateRange,
                            onClick = {
                                isDateRange = false
                                dateText = dateText.substringBefore(" - ").trim()
                            }
                        )
                        ScheduleModeButton(
                            text = "Date Range",
                            selected = isDateRange,
                            onClick = {
                                isDateRange = true
                                if (!dateText.contains(" - ") && dateText.isNotBlank()) {
                                    dateText = "$dateText - $dateText"
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val firstDate = dateText.substringBefore(" - ")
                ScheduleDateField(if (isDateRange) "Start date" else "Event date", firstDate) { selected ->
                    val lastDate = dateText.substringAfter(" - ", selected)
                    dateText = if (isDateRange) "$selected - $lastDate" else selected
                }
                if (isDateRange) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ScheduleDateField("End date", dateText.substringAfter(" - ", firstDate)) { selected ->
                        dateText = "$firstDate - $selected"
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Start Time & End Time row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        ScheduleTimeField("Start time", startTime) { startTime = it }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        ScheduleTimeField("End time", endTime) { endTime = it }
                    }
                }
            }

            // 4. VENUE & LOCATION Section (Figma node 126:981)
            EditorSectionCard {
                EditorSectionLabel(text = "VENUE & LOCATION")
                Spacer(modifier = Modifier.height(12.dp))

                EditorTextInputBox(
                    value = venueName,
                    onValueChange = { venueName = it },
                    textSizeSp = 12,
                    textColor = SlateInputText
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Map Location Preview Box ("Select Address")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateBg)
                        .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            venueName = if (venueName == "Mapo Startup Center") {
                                "Seoul Plaza, Jung-gu"
                            } else {
                                "Mapo Startup Center"
                            }
                        }
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE0E7FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_org_map_pin_filled),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Select Address",
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Location Environment",
                    color = SlateMutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EditorPillOption(
                        text = "Indoor",
                        selected = !isOutdoor,
                        onClick = { isOutdoor = false },
                        modifier = Modifier.weight(1f)
                    )
                    EditorPillOption(
                        text = "Outdoor",
                        selected = isOutdoor,
                        onClick = { isOutdoor = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (isOutdoor) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_org_weather_warn),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Weather alerts & push notifications enabled for outdoor venue.",
                            color = Color(0xFFD97706),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // 5. ADMISSION & TICKETING Section (Figma node 126:1014)
            EditorSectionCard {
                EditorSectionLabel(text = "ADMISSION & TICKETING")
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Free Open", "Free Booking", "Paid Ticket").forEach { mode ->
                        EditorPillOption(
                            text = mode,
                            selected = admissionMode == mode,
                            onClick = { admissionMode = mode },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Capacity Limit",
                            color = SlateInputText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Total available reservations",
                            color = SlateSubtle,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateBg)
                                .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 9.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            BasicTextField(
                                value = capacityText,
                                onValueChange = { next ->
                                    if (next.length <= 5 && next.all { it.isDigit() }) {
                                        capacityText = next
                                    }
                                },
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = Color(0xFF1E293B),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                ),
                                cursorBrush = SolidColor(OrganizerBrandPurple),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "spots",
                            color = SlateCaption,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }

            // 6. EVENT STATUS Section (Figma node 126:1037)
            EditorSectionCard {
                EditorSectionLabel(text = "EVENT STATUS")
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OrganizerEventStatus.entries.forEach { statusOption ->
                        val isSelected = eventStatus == statusOption
                        val bg = if (isSelected) Color(0x80EEF2FF) else SlateBg
                        val borderMod = if (isSelected) {
                            Modifier.border(2.dp, OrganizerBrandPurple, RoundedCornerShape(12.dp))
                        } else {
                            Modifier.border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bg)
                                .then(borderMod)
                                .clickable { eventStatus = statusOption }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = statusOption.editorLabel,
                                color = if (isSelected) OrganizerBrandPurple else SlateMutedText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun EditorSectionCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SeoSurface,
        border = BorderStroke(1.dp, SlateCardBorder),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp),
            content = content
        )
    }
}

@Composable
private fun EditorSectionLabel(text: String) {
    Text(
        text = text,
        color = Color.Black,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.6.sp,
        lineHeight = 16.sp
    )
}

@Composable
private fun EditorTextInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    textSizeSp: Int,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SlateBg)
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 15.dp, vertical = 11.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = textColor,
                fontSize = textSizeSp.sp,
                fontWeight = FontWeight.Normal
            ),
            cursorBrush = SolidColor(OrganizerBrandPurple),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ScheduleModeButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) SeoSurface else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) OrganizerBrandPurple else SlateCaption,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun EditorPillOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) OrganizerBrandPurple else SlatePillBg)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) SeoSurface else SlateMutedText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}
