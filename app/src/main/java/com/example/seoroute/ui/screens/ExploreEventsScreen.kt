package com.example.seoroute.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import com.example.seoroute.model.NavTab
import com.example.seoroute.model.SampleEvents
import com.example.seoroute.model.SeoEvent
import com.example.seoroute.ui.components.EventHorizontalCard
import com.example.seoroute.ui.components.SelectableFilterPill
import com.example.seoroute.ui.components.SeoRouteBottomNavBar
import com.example.seoroute.ui.components.SeoSearchBar
import com.example.seoroute.ui.theme.SeoBackground
import com.example.seoroute.ui.theme.SeoBorder
import com.example.seoroute.ui.theme.SeoFilterChipBg
import com.example.seoroute.ui.theme.SeoMapBlock
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoPrimaryLight
import com.example.seoroute.ui.theme.SeoSurface
import com.example.seoroute.ui.theme.SeoTextPrimary
import com.example.seoroute.ui.theme.SeoTextSecondary

data class EventFilterState(
    val admissionType: String = "All events",
    val locationType: String = "Any",
    val dateRange: String = "Choose a date range",
    val category: String = "Music"
)

// Your Explore Events Page
@Composable
fun ExploreEventsScreen(
    showFilterOverlay: Boolean,
    onToggleFilterOverlay: (Boolean) -> Unit,
    selectedNavTab: NavTab,
    onNavTabSelected: (NavTab) -> Unit,
    onBackClick: () -> Unit,
    onEventClick: (SeoEvent) -> Unit,
    modifier: Modifier = Modifier,
    initialSelectedPills: Set<String> = if (showFilterOverlay) setOf("All") else setOf("All", "Music")
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedQuickFilters by remember(showFilterOverlay) { mutableStateOf(initialSelectedPills) }
    var filterState by remember { mutableStateOf(EventFilterState()) }

    val quickFilterLabels = if (showFilterOverlay) {
        listOf("All", "This week", "Free", "Outdoor")
    } else {
        listOf("All", "Music", "Free", "Outdoor")
    }

    val displayedEvents = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            SampleEvents.exploreEvents
        } else {
            SampleEvents.exploreEvents.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                    it.shortDateLocation.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
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
                Spacer(modifier = Modifier.height(22.dp))

                // Top Header: Circular Back Button + Explore title & subtitle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E4F7))
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(22.dp))
                    Column {
                        Text(
                            text = "Explore",
                            color = SeoTextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Find what inspires you in Seoul",
                            color = SeoTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Search bar with filter icon
                SeoSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Exhibitions, music, festivals...",
                    showFilterButton = true,
                    onFilterClick = { onToggleFilterOverlay(true) }
                )

                Spacer(modifier = Modifier.height(17.dp))

                // Horizontal quick filter pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    quickFilterLabels.forEach { label ->
                        val isSelected = selectedQuickFilters.contains(label)
                        SelectableFilterPill(
                            text = label,
                            selected = isSelected,
                            onClick = {
                                selectedQuickFilters = if (isSelected && selectedQuickFilters.size > 1) {
                                    selectedQuickFilters - label
                                } else {
                                    selectedQuickFilters + label
                                }
                            },
                            unselectedBackgroundColor = SeoSurface,
                            height = 32.dp,
                            horizontalPadding = if (label == "All") 17.dp else 22.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // "124 events found" & "Newest" sort header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "124 events found",
                        color = SeoTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = "Newest",
                        color = SeoPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Events List
                Column(
                    verticalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    displayedEvents.forEach { event ->
                        EventHorizontalCard(
                            event = event,
                            onClick = { onEventClick(event) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Event Filter Overlay
        if (showFilterOverlay) {
            EventFilterSheetOverlay(
                filterState = filterState,
                onFilterStateChange = { filterState = it },
                onReset = {
                    filterState = EventFilterState(
                        admissionType = "All events",
                        locationType = "Any",
                        dateRange = "Choose a date range",
                        category = "Music"
                    )
                },
                onApply = { onToggleFilterOverlay(false) },
                onDismiss = { onToggleFilterOverlay(false) }
            )
        }
    }
}

// Your Event Filter
@Composable
fun EventFilterSheetOverlay(
    filterState: EventFilterState,
    onFilterStateChange: (EventFilterState) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x66192238))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxSize(0.805f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* consume click inside sheet */ },
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = SeoSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Drag handle pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(width = 40.dp, height = 4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(SeoMapBlock)
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Header: Filter Icon + "Filter events" + "Reset"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_filter_sliders),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Filter events",
                                color = SeoTextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Reset",
                            color = SeoPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onReset() }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(thickness = 1.dp, color = SeoBorder)
                    Spacer(modifier = Modifier.height(22.dp))

                    // 1. Admission type
                    Text(
                        text = "Admission type",
                        color = SeoTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("All events", "Free open", "Booking", "Paid").forEach { option ->
                            SelectableFilterPill(
                                text = option,
                                selected = filterState.admissionType == option,
                                onClick = {
                                    onFilterStateChange(filterState.copy(admissionType = option))
                                },
                                unselectedBackgroundColor = SeoFilterChipBg,
                                height = 35.dp,
                                horizontalPadding = 18.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // 2. Location type
                    Text(
                        text = "Location type",
                        color = SeoTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("Any", "Indoor", "Outdoor").forEach { option ->
                            SelectableFilterPill(
                                text = option,
                                selected = filterState.locationType == option,
                                onClick = {
                                    onFilterStateChange(filterState.copy(locationType = option))
                                },
                                unselectedBackgroundColor = SeoFilterChipBg,
                                height = 35.dp,
                                horizontalPadding = 20.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // 3. Date
                    Text(
                        text = "Date",
                        color = SeoTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SeoFilterChipBg)
                            .clickable {
                                val nextDate = if (filterState.dateRange == "Choose a date range") {
                                    "Nov 08 – Nov 20, 2026"
                                } else {
                                    "Choose a date range"
                                }
                                onFilterStateChange(filterState.copy(dateRange = nextDate))
                            }
                            .padding(horizontal = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = "Calendar",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = filterState.dateRange,
                            color = SeoTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // 4. Category
                    Text(
                        text = "Category",
                        color = SeoTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("Music", "Arts", "Festival", "Outdoors").forEach { option ->
                            SelectableFilterPill(
                                text = option,
                                selected = filterState.category == option,
                                onClick = {
                                    onFilterStateChange(filterState.copy(category = option))
                                },
                                unselectedBackgroundColor = SeoFilterChipBg,
                                height = 35.dp,
                                horizontalPadding = 16.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Bottom CTA Button: "Show 124 events"
                Box(
                    modifier = Modifier
                        .padding(bottom = 32.dp, top = 8.dp)
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SeoPrimary)
                        .clickable { onApply() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Show 124 events",
                        color = SeoSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
