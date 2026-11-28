package com.example.seoroute.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.model.AdminNavTab
import com.example.seoroute.model.NavTab
import com.example.seoroute.model.OrganizerNavTab
import com.example.seoroute.ui.theme.SeoBorder
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoSurface
import com.example.seoroute.ui.theme.SeoTextMuted
import com.example.seoroute.ui.theme.SeoTextSecondary

data class BottomNavItemSpec<T>(
    val tab: T,
    val label: String,
    @DrawableRes val iconRes: Int
)

/**
 * Generic modular bottom navigation bar shared across both End-User pages and Organizer Studio pages.
 */
@Composable
fun <T> ModularBottomNavBar(
    items: List<BottomNavItemSpec<T>>,
    selectedTab: T?,
    onTabSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SeoSurface)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(
            thickness = 1.dp,
            color = SeoBorder
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = item.tab == selectedTab
                val iconTint = if (isSelected) SeoPrimary else SeoTextMuted
                val labelColor = if (isSelected) SeoPrimary else SeoTextSecondary
                val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(item.tab) }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.label,
                        color = labelColor,
                        fontSize = 10.sp,
                        fontWeight = fontWeight,
                        textAlign = TextAlign.Center,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}

private val UserNavItems = NavTab.entries.map {
    BottomNavItemSpec(tab = it, label = it.label, iconRes = it.iconRes)
}

private val OrganizerNavItems = OrganizerNavTab.entries.map {
    BottomNavItemSpec(tab = it, label = it.label, iconRes = it.iconRes)
}

private val AdminNavItems = AdminNavTab.entries.map {
    BottomNavItemSpec(tab = it, label = it.label, iconRes = it.iconRes)
}

/**
 * End-user bottom navigation bar (Discover, Saved, Tickets, Profile).
 */
@Composable
fun SeoRouteBottomNavBar(
    selectedTab: NavTab?,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    ModularBottomNavBar(
        items = UserNavItems,
        selectedTab = selectedTab,
        onTabSelected = onTabSelected,
        modifier = modifier
    )
}

/**
 * Organizer Studio bottom navigation bar (Overview, Events, People, Profile).
 */
@Composable
fun OrganizerBottomNavBar(
    selectedTab: OrganizerNavTab?,
    onTabSelected: (OrganizerNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    ModularBottomNavBar(
        items = OrganizerNavItems,
        selectedTab = selectedTab,
        onTabSelected = onTabSelected,
        modifier = modifier
    )
}

/**
 * Super Admin Application 3-tab bottom navigation bar (Overview, Events, People).
 */
@Composable
fun AdminBottomNavBar(
    selectedTab: AdminNavTab?,
    onTabSelected: (AdminNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    ModularBottomNavBar(
        items = AdminNavItems,
        selectedTab = selectedTab,
        onTabSelected = onTabSelected,
        modifier = modifier
    )
}
