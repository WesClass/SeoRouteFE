package com.example.seoroute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.seoroute.model.EventDetailType
import com.example.seoroute.model.NavTab
import com.example.seoroute.model.SampleEvents
import com.example.seoroute.model.SeoEvent
import com.example.seoroute.model.VibeCategory
import com.example.seoroute.ui.screens.BookingSuccessScreen
import com.example.seoroute.ui.screens.ConfirmBookingScreen
import com.example.seoroute.ui.screens.CreateAccountScreen
import com.example.seoroute.ui.screens.DigitalTicketScreen
import com.example.seoroute.ui.screens.DiscoverScreen
import com.example.seoroute.ui.screens.ExploreEventsScreen
import com.example.seoroute.ui.screens.ForgotPasswordScreen
import com.example.seoroute.ui.screens.ManagedEventDetailScreen
import com.example.seoroute.ui.screens.MyTicketsScreen
import com.example.seoroute.ui.screens.NotificationsScreen
import com.example.seoroute.ui.screens.PublicEventDetailScreen
import com.example.seoroute.ui.screens.SavedEventsScreen
import com.example.seoroute.ui.screens.SignInScreen
import com.example.seoroute.ui.screens.UserProfileScreen
import com.example.seoroute.ui.screens.WelcomeScreen
import com.example.seoroute.ui.theme.SeoRouteTheme

sealed interface SeoRouteDestination {
    data object Welcome : SeoRouteDestination
    data object SignIn : SeoRouteDestination
    data object ForgotPassword : SeoRouteDestination
    data object CreateAccount : SeoRouteDestination
    data object Discover : SeoRouteDestination
    data class Explore(val showFilterModal: Boolean = false) : SeoRouteDestination
    data class PublicEventDetail(val event: SeoEvent = SampleEvents.seoulCultureNight) : SeoRouteDestination
    data class ManagedEventDetail(val event: SeoEvent = SampleEvents.futureSeoulMeetup) : SeoRouteDestination
    data class ConfirmBooking(val event: SeoEvent = SampleEvents.futureSeoulMeetup) : SeoRouteDestination
    data class BookingSuccess(val event: SeoEvent = SampleEvents.futureSeoulMeetup) : SeoRouteDestination
    data object MyTickets : SeoRouteDestination
    data class DigitalTicket(val event: SeoEvent = SampleEvents.futureSeoulMeetup) : SeoRouteDestination
    data object SavedEvents : SeoRouteDestination
    data object Notifications : SeoRouteDestination
    data object UserProfile : SeoRouteDestination
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SeoRouteTheme {
                SeoRouteApp(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                )
            }
        }
    }
}

@Composable
fun SeoRouteApp(
    modifier: Modifier = Modifier,
    initialDestination: SeoRouteDestination = SeoRouteDestination.Welcome
) {
    var currentDestination by remember { mutableStateOf(initialDestination) }
    var previousListDestination by remember { mutableStateOf<SeoRouteDestination>(SeoRouteDestination.Discover) }
    var selectedNavTab by remember {
        mutableStateOf(
            when (initialDestination) {
                is SeoRouteDestination.MyTickets -> NavTab.TICKETS
                is SeoRouteDestination.SavedEvents -> NavTab.SAVED
                is SeoRouteDestination.UserProfile -> NavTab.PROFILE
                else -> NavTab.DISCOVER
            }
        )
    }
    var selectedVibe by remember { mutableStateOf(VibeCategory.MUSIC) }

    val handleBottomNavTabSelected: (NavTab) -> Unit = { tab ->
        selectedNavTab = tab
        currentDestination = when (tab) {
            NavTab.DISCOVER -> SeoRouteDestination.Discover
            NavTab.SAVED -> SeoRouteDestination.SavedEvents
            NavTab.TICKETS -> SeoRouteDestination.MyTickets
            NavTab.PROFILE -> SeoRouteDestination.UserProfile
        }
    }

    val navigateToEventDetail: (SeoEvent) -> Unit = { event ->
        previousListDestination = currentDestination
        currentDestination = if (event.detailType == EventDetailType.MANAGED) {
            SeoRouteDestination.ManagedEventDetail(event)
        } else {
            SeoRouteDestination.PublicEventDetail(event)
        }
    }

    BackHandler(enabled = currentDestination !is SeoRouteDestination.Welcome && currentDestination !is SeoRouteDestination.Discover) {
        currentDestination = when (val dest = currentDestination) {
            is SeoRouteDestination.SignIn -> SeoRouteDestination.Welcome
            is SeoRouteDestination.ForgotPassword,
            is SeoRouteDestination.CreateAccount -> SeoRouteDestination.SignIn
            is SeoRouteDestination.Explore -> {
                if (dest.showFilterModal) {
                    SeoRouteDestination.Explore(showFilterModal = false)
                } else {
                    SeoRouteDestination.Discover
                }
            }
            is SeoRouteDestination.PublicEventDetail,
            is SeoRouteDestination.ManagedEventDetail -> previousListDestination
            is SeoRouteDestination.ConfirmBooking -> {
                if (dest.event.detailType == EventDetailType.MANAGED) {
                    SeoRouteDestination.ManagedEventDetail(dest.event)
                } else {
                    SeoRouteDestination.PublicEventDetail(dest.event)
                }
            }
            is SeoRouteDestination.BookingSuccess -> {
                selectedNavTab = NavTab.DISCOVER
                SeoRouteDestination.Discover
            }
            is SeoRouteDestination.DigitalTicket -> {
                selectedNavTab = NavTab.TICKETS
                SeoRouteDestination.MyTickets
            }
            is SeoRouteDestination.Notifications,
            is SeoRouteDestination.UserProfile,
            is SeoRouteDestination.MyTickets,
            is SeoRouteDestination.SavedEvents -> {
                selectedNavTab = NavTab.DISCOVER
                SeoRouteDestination.Discover
            }
            SeoRouteDestination.Welcome,
            SeoRouteDestination.Discover -> dest
        }
    }

    when (val destination = currentDestination) {
        is SeoRouteDestination.Welcome -> {
            WelcomeScreen(
                onGetStartedClick = {
                    currentDestination = SeoRouteDestination.CreateAccount
                },
                onSignInClick = {
                    currentDestination = SeoRouteDestination.SignIn
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.SignIn -> {
            SignInScreen(
                onSignInSuccess = {
                    selectedNavTab = NavTab.DISCOVER
                    currentDestination = SeoRouteDestination.Discover
                },
                onCreateAccountClick = {
                    currentDestination = SeoRouteDestination.CreateAccount
                },
                onForgotPasswordClick = {
                    currentDestination = SeoRouteDestination.ForgotPassword
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.ForgotPassword -> {
            ForgotPasswordScreen(
                onResetPasswordClick = {
                    currentDestination = SeoRouteDestination.SignIn
                },
                onBackToSignInClick = {
                    currentDestination = SeoRouteDestination.SignIn
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.CreateAccount -> {
            CreateAccountScreen(
                onCreateAccountSuccess = {
                    selectedNavTab = NavTab.DISCOVER
                    currentDestination = SeoRouteDestination.Discover
                },
                onSignInClick = {
                    currentDestination = SeoRouteDestination.SignIn
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.Discover -> {
            DiscoverScreen(
                selectedVibe = selectedVibe,
                selectedNavTab = selectedNavTab,
                onVibeClick = { vibe ->
                    selectedVibe = vibe
                },
                onSearchClick = {
                    currentDestination = SeoRouteDestination.Explore(showFilterModal = false)
                },
                onSeeAllClick = {
                    currentDestination = SeoRouteDestination.Explore(showFilterModal = false)
                },
                onEventClick = navigateToEventDetail,
                onNavTabSelected = handleBottomNavTabSelected,
                onNotificationsClick = {
                    currentDestination = SeoRouteDestination.Notifications
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.Explore -> {
            ExploreEventsScreen(
                showFilterOverlay = destination.showFilterModal,
                onToggleFilterOverlay = { show ->
                    currentDestination = SeoRouteDestination.Explore(showFilterModal = show)
                },
                selectedNavTab = selectedNavTab,
                onNavTabSelected = handleBottomNavTabSelected,
                onBackClick = {
                    currentDestination = SeoRouteDestination.Discover
                },
                onEventClick = navigateToEventDetail,
                modifier = modifier
            )
        }

        is SeoRouteDestination.PublicEventDetail -> {
            PublicEventDetailScreen(
                event = destination.event,
                onBackClick = {
                    currentDestination = previousListDestination
                },
                onActionClick = {
                    currentDestination = SeoRouteDestination.ConfirmBooking(destination.event)
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.ManagedEventDetail -> {
            ManagedEventDetailScreen(
                event = destination.event,
                onBackClick = {
                    currentDestination = previousListDestination
                },
                onActionClick = {
                    currentDestination = SeoRouteDestination.ConfirmBooking(destination.event)
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.ConfirmBooking -> {
            ConfirmBookingScreen(
                event = destination.event,
                onBackClick = {
                    currentDestination = if (destination.event.detailType == EventDetailType.MANAGED) {
                        SeoRouteDestination.ManagedEventDetail(destination.event)
                    } else {
                        SeoRouteDestination.PublicEventDetail(destination.event)
                    }
                },
                onConfirmBooking = {
                    currentDestination = SeoRouteDestination.BookingSuccess(destination.event)
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.BookingSuccess -> {
            BookingSuccessScreen(
                event = destination.event,
                onViewMyTicketClick = {
                    currentDestination = SeoRouteDestination.DigitalTicket(destination.event)
                },
                onBackToDiscoverClick = {
                    selectedNavTab = NavTab.DISCOVER
                    currentDestination = SeoRouteDestination.Discover
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.MyTickets -> {
            MyTicketsScreen(
                selectedNavTab = NavTab.TICKETS,
                onNavTabSelected = handleBottomNavTabSelected,
                onViewDigitalTicketClick = { event ->
                    currentDestination = SeoRouteDestination.DigitalTicket(event)
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.DigitalTicket -> {
            DigitalTicketScreen(
                event = destination.event,
                onBackClick = {
                    selectedNavTab = NavTab.TICKETS
                    currentDestination = SeoRouteDestination.MyTickets
                },
                onDoneClick = {
                    selectedNavTab = NavTab.TICKETS
                    currentDestination = SeoRouteDestination.MyTickets
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.SavedEvents -> {
            SavedEventsScreen(
                selectedNavTab = NavTab.SAVED,
                onNavTabSelected = handleBottomNavTabSelected,
                onEventClick = navigateToEventDetail,
                onFilterClick = {
                    selectedNavTab = NavTab.DISCOVER
                    currentDestination = SeoRouteDestination.Explore(showFilterModal = true)
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.Notifications -> {
            NotificationsScreen(
                selectedNavTab = null,
                onNavTabSelected = handleBottomNavTabSelected,
                onViewUpdateClick = {
                    navigateToEventDetail(SampleEvents.autumnGardenWalk)
                },
                onReservationNotificationClick = {
                    selectedNavTab = NavTab.TICKETS
                    currentDestination = SeoRouteDestination.MyTickets
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.UserProfile -> {
            UserProfileScreen(
                selectedNavTab = NavTab.PROFILE,
                onNavTabSelected = handleBottomNavTabSelected,
                onMyReservationsClick = {
                    selectedNavTab = NavTab.TICKETS
                    currentDestination = SeoRouteDestination.MyTickets
                },
                onNotificationsClick = {
                    currentDestination = SeoRouteDestination.Notifications
                },
                onSignOutClick = {
                    currentDestination = SeoRouteDestination.SignIn
                },
                modifier = modifier
            )
        }
    }
}

@Preview(name = "01_Welcome 1", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun Preview01Welcome1() {
    SeoRouteTheme {
        WelcomeScreen(
            onGetStartedClick = {},
            onSignInClick = {}
        )
    }
}

@Preview(name = "02_Sign_in 2", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun Preview02SignIn2() {
    SeoRouteTheme {
        SignInScreen(
            onSignInSuccess = {},
            onCreateAccountClick = {},
            onForgotPasswordClick = {}
        )
    }
}

@Preview(name = "Forgot Password", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewForgotPassword() {
    SeoRouteTheme {
        ForgotPasswordScreen(
            onResetPasswordClick = {},
            onBackToSignInClick = {}
        )
    }
}

@Preview(name = "Create Account", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewCreateAccount() {
    SeoRouteTheme {
        CreateAccountScreen(
            onCreateAccountSuccess = {},
            onSignInClick = {}
        )
    }
}

@Preview(name = "03_Discover 3", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun Preview03Discover3() {
    SeoRouteTheme {
        SeoRouteApp(initialDestination = SeoRouteDestination.Discover)
    }
}

@Preview(name = "04_Explore_events 1", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun Preview04ExploreEvents1() {
    SeoRouteTheme {
        SeoRouteApp(initialDestination = SeoRouteDestination.Explore(showFilterModal = false))
    }
}

@Preview(name = "Event Filter", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewEventFilter() {
    SeoRouteTheme {
        SeoRouteApp(initialDestination = SeoRouteDestination.Explore(showFilterModal = true))
    }
}

@Preview(name = "06_Public_event_detail 1", showBackground = true, widthDp = 390, heightDp = 1108)
@Composable
fun Preview06PublicEventDetail1() {
    SeoRouteTheme {
        PublicEventDetailScreen(
            event = SampleEvents.seoulCultureNight,
            onBackClick = {}
        )
    }
}

@Preview(name = "07_Managed_event_detail 1", showBackground = true, widthDp = 390, heightDp = 1108)
@Composable
fun Preview07ManagedEventDetail1() {
    SeoRouteTheme {
        ManagedEventDetailScreen(
            event = SampleEvents.futureSeoulMeetup,
            onBackClick = {}
        )
    }
}

@Preview(name = "08_Confirm_booking 1", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun Preview08ConfirmBooking1() {
    SeoRouteTheme {
        ConfirmBookingScreen(
            event = SampleEvents.futureSeoulMeetup,
            onBackClick = {},
            onConfirmBooking = {}
        )
    }
}

@Preview(name = "09_Booking_success 1", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun Preview09BookingSuccess1() {
    SeoRouteTheme {
        BookingSuccessScreen(
            event = SampleEvents.futureSeoulMeetup,
            onViewMyTicketClick = {},
            onBackToDiscoverClick = {}
        )
    }
}

@Preview(name = "10_My_tickets 1", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun Preview10MyTickets1() {
    SeoRouteTheme {
        MyTicketsScreen(
            onNavTabSelected = {},
            onViewDigitalTicketClick = {}
        )
    }
}

@Preview(name = "11_Digital_ticket 1", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun Preview11DigitalTicket1() {
    SeoRouteTheme {
        DigitalTicketScreen(
            event = SampleEvents.futureSeoulMeetup,
            onBackClick = {},
            onDoneClick = {}
        )
    }
}

@Preview(name = "12_Saved_events 1", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun Preview12SavedEvents1() {
    SeoRouteTheme {
        SavedEventsScreen(
            onNavTabSelected = {},
            onEventClick = {}
        )
    }
}

@Preview(name = "13_Notifications 1", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun Preview13Notifications1() {
    SeoRouteTheme {
        NotificationsScreen(
            onNavTabSelected = {}
        )
    }
}

@Preview(name = "20_User_profile 1", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun Preview20UserProfile1() {
    SeoRouteTheme {
        UserProfileScreen(
            onNavTabSelected = {}
        )
    }
}
