package com.example.seoroute.ui.screens

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.seoroute.model.EventTagStyle
import com.example.seoroute.model.NavTab
import com.example.seoroute.ui.components.EventBadgePill
import com.example.seoroute.ui.components.SeoRouteBottomNavBar
import com.example.seoroute.ui.theme.SeoBackground
import com.example.seoroute.ui.theme.SeoBorder
import com.example.seoroute.ui.theme.SeoGreenBg
import com.example.seoroute.ui.theme.SeoGreenText
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoPrimaryLight
import com.example.seoroute.ui.theme.SeoSurface
import com.example.seoroute.ui.theme.SeoTextPrimary
import com.example.seoroute.ui.theme.SeoTextSecondary

// Your Profile Page
@Composable
fun UserProfileScreen(
    selectedNavTab: NavTab = NavTab.PROFILE,
    onNavTabSelected: (NavTab) -> Unit,
    onPersonalInfoClick: () -> Unit = {},
    onMyReservationsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    onOrganizerStudioClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currentUser = DummyAuthRepository.currentUser.value
    val displayName = currentUser?.username ?: "Guest account"
    val displayEmail = currentUser?.email ?: "user@example.com"
    val avatarLetter = currentUser?.username?.firstOrNull()?.uppercaseChar()?.toString() ?: "R"

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
                text = "My profile",
                color = SeoTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 29.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Manage account and preferences",
                color = SeoTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // User Avatar & Identity Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(98.dp)
                        .clip(CircleShape)
                        .background(SeoPrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avatarLetter,
                        color = SeoPrimary,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = displayName,
                        color = SeoTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = displayEmail,
                        color = SeoTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(9.dp))
                    EventBadgePill(
                        tag = EventTagStyle("END USER", SeoGreenText, SeoGreenBg),
                        minWidth = 88.dp,
                        height = 25.dp,
                        horizontalPadding = 16.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(42.dp))

            // "Account" Section Header
            Text(
                text = "Account",
                color = SeoTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Account Menu Cards
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileMenuCard(
                    iconRes = R.drawable.ic_prof_user,
                    title = "Personal information",
                    onClick = onPersonalInfoClick
                )
                ProfileMenuCard(
                    iconRes = R.drawable.ic_prof_ticket,
                    title = "My reservations",
                    onClick = onMyReservationsClick
                )
                ProfileMenuCard(
                    iconRes = R.drawable.ic_prof_bell,
                    title = "Notifications",
                    onClick = onNotificationsClick
                )
                if (onOrganizerStudioClick != null) {
                    ProfileMenuCard(
                        iconRes = R.drawable.ic_org_nav_overview,
                        title = "Organizer Studio",
                        onClick = onOrganizerStudioClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Sign out button row
            Row(
                modifier = Modifier
                    .clickable {
                        DummyAuthRepository.currentUser.value = null
                        onSignOutClick()
                    }
                    .padding(vertical = 8.dp, horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_prof_signout),
                    contentDescription = "Sign out",
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign out",
                    color = Color(0xFFFF745D),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun ProfileMenuCard(
    @DrawableRes iconRes: Int,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = SeoSurface,
        border = BorderStroke(1.dp, SeoBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(21.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    color = SeoTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_prof_chevron),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
