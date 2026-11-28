package com.example.seoroute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.seoroute.model.AdminNavTab
import com.example.seoroute.model.DummyAuthRepository
import com.example.seoroute.model.EventDetailType
import com.example.seoroute.model.NavTab
import com.example.seoroute.model.OrganizerEventItem
import com.example.seoroute.model.OrganizerNavTab
import com.example.seoroute.model.OrganizerRepository
import com.example.seoroute.model.SampleEvents
import com.example.seoroute.model.SeoEvent
import com.example.seoroute.model.VibeCategory
import com.example.seoroute.network.SeoRouteApiClient
import kotlinx.coroutines.launch
import com.example.seoroute.ui.screens.AdminConsoleScreen
import com.example.seoroute.ui.screens.AdminManageOrganizersScreen
import com.example.seoroute.ui.screens.AdminOverviewScreen
import com.example.seoroute.ui.screens.AttendeeManagementScreen
import com.example.seoroute.ui.screens.BookingSuccessScreen
import com.example.seoroute.ui.screens.CheckInResultScreen
import com.example.seoroute.ui.screens.CheckInScannerScreen
import com.example.seoroute.ui.screens.ConfirmBookingScreen
import com.example.seoroute.ui.screens.CreateAccountScreen
import com.example.seoroute.ui.screens.CreateEditEventScreen
import com.example.seoroute.ui.screens.DigitalTicketScreen
import com.example.seoroute.ui.screens.DiscoverScreen
import com.example.seoroute.ui.screens.ExploreEventsScreen
import com.example.seoroute.ui.screens.ForgotPasswordScreen
import com.example.seoroute.ui.screens.ManagedEventDetailScreen
import com.example.seoroute.ui.screens.MyTicketsScreen
import com.example.seoroute.ui.screens.NotificationsScreen
import com.example.seoroute.ui.screens.OrganizerDashboardScreen
import com.example.seoroute.ui.screens.OrganizerProfileStaffScreen
import com.example.seoroute.ui.screens.OrganizerViewEventsScreen
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

    // Organizer Studio & Ticketing Staff Destinations
    data object OrganizerDashboard : SeoRouteDestination
    data object OrganizerViewEvents : SeoRouteDestination
    data class OrganizerCreateEditEvent(
        val event: OrganizerEventItem = OrganizerRepository.createDefaultNewEvent()
    ) : SeoRouteDestination
    data class OrganizerProfileStaff(
        val activeTab: OrganizerNavTab = OrganizerNavTab.PROFILE
    ) : SeoRouteDestination
    data class AttendeeManagement(
        val eventTitle: String = "Future Seoul Meetup"
    ) : SeoRouteDestination
    data object CheckInScanner : SeoRouteDestination
    data class CheckInResult(
        val attendeeName: String = "Jin Park",
        val eventTitle: String = "Future Seoul Meetup"
    ) : SeoRouteDestination

    // Admin Application Destinations
    data object AdminOverview : SeoRouteDestination
    data object AdminConsole : SeoRouteDestination
    data object AdminManageOrganizers : SeoRouteDestination
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
    var previousOrganizerDestination by remember {
        mutableStateOf<SeoRouteDestination>(SeoRouteDestination.OrganizerDashboard)
    }
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
    val coroutineScope = rememberCoroutineScope()
    val appContext = LocalContext.current.applicationContext

    // Initialize DataStore token persistence & fetch live public events from BuildConfig.BASE_URL on startup
    LaunchedEffect(Unit) {
        SeoRouteApiClient.initDataStore(appContext)
        SampleEvents.syncFromBackend()
    }

    val handleBottomNavTabSelected: (NavTab) -> Unit = { tab ->
        selectedNavTab = tab
        currentDestination = when (tab) {
            NavTab.DISCOVER -> SeoRouteDestination.Discover
            NavTab.SAVED -> SeoRouteDestination.SavedEvents
            NavTab.TICKETS -> SeoRouteDestination.MyTickets
            NavTab.PROFILE -> SeoRouteDestination.UserProfile
        }
    }

    val isTicketingStaffOnly = DummyAuthRepository.currentUser.value?.isTicketingStaffOnly == true

    val handleOrganizerNavTabSelected: (OrganizerNavTab) -> Unit = { tab ->
        if (DummyAuthRepository.currentUser.value?.isTicketingStaffOnly == true) {
            // Ticketing staff role can only access the Attendee check-in & Scanner flow
            currentDestination = SeoRouteDestination.AttendeeManagement(
                eventTitle = OrganizerRepository.getCurrentAssignedEvent().title
            )
        } else {
            currentDestination = when (tab) {
                OrganizerNavTab.OVERVIEW -> SeoRouteDestination.OrganizerDashboard
                OrganizerNavTab.EVENTS -> SeoRouteDestination.OrganizerViewEvents
                OrganizerNavTab.PEOPLE -> SeoRouteDestination.AttendeeManagement(
                    eventTitle = OrganizerRepository.getCurrentAssignedEvent().title
                )
                OrganizerNavTab.PROFILE -> SeoRouteDestination.OrganizerProfileStaff(activeTab = OrganizerNavTab.PROFILE)
            }
        }
    }

    val handleAdminNavTabSelected: (AdminNavTab) -> Unit = { tab ->
        currentDestination = when (tab) {
            AdminNavTab.OVERVIEW -> SeoRouteDestination.AdminOverview
            AdminNavTab.EVENTS -> SeoRouteDestination.AdminConsole
            AdminNavTab.PEOPLE -> SeoRouteDestination.AdminManageOrganizers
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

    BackHandler(
        enabled = currentDestination !is SeoRouteDestination.Welcome &&
            currentDestination !is SeoRouteDestination.Discover &&
            currentDestination !is SeoRouteDestination.OrganizerDashboard &&
            currentDestination !is SeoRouteDestination.AdminOverview &&
            !(isTicketingStaffOnly && currentDestination is SeoRouteDestination.AttendeeManagement)
    ) {
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
            is SeoRouteDestination.OrganizerViewEvents,
            is SeoRouteDestination.OrganizerProfileStaff,
            is SeoRouteDestination.AttendeeManagement -> SeoRouteDestination.OrganizerDashboard
            is SeoRouteDestination.CheckInScanner -> SeoRouteDestination.AttendeeManagement()
            is SeoRouteDestination.CheckInResult -> SeoRouteDestination.AttendeeManagement()
            is SeoRouteDestination.OrganizerCreateEditEvent -> previousOrganizerDestination
            is SeoRouteDestination.AdminConsole,
            is SeoRouteDestination.AdminManageOrganizers -> SeoRouteDestination.AdminOverview
            SeoRouteDestination.Welcome,
            SeoRouteDestination.Discover,
            SeoRouteDestination.OrganizerDashboard,
            SeoRouteDestination.AdminOverview -> dest
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
                onOrganizerSignInSuccess = {
                    currentDestination = SeoRouteDestination.OrganizerDashboard
                },
                onTicketingSignInSuccess = {
                    currentDestination = SeoRouteDestination.AttendeeManagement(
                        eventTitle = OrganizerRepository.getCurrentAssignedEvent().title
                    )
                },
                onAdminSignInSuccess = {
                    currentDestination = SeoRouteDestination.AdminOverview
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
            var submitting by remember(destination.event.id) { mutableStateOf(false) }
            var bookingError by remember(destination.event.id) { mutableStateOf<String?>(null) }
            ConfirmBookingScreen(
                event = destination.event,
                submitting = submitting,
                bookingError = bookingError,
                onBackClick = {
                    currentDestination = if (destination.event.detailType == EventDetailType.MANAGED) {
                        SeoRouteDestination.ManagedEventDetail(destination.event)
                    } else {
                        SeoRouteDestination.PublicEventDetail(destination.event)
                    }
                },
                onConfirmBooking = {
                    if (submitting) return@ConfirmBookingScreen
                    submitting = true
                    bookingError = null
                    coroutineScope.launch {
                        val evt = destination.event
                        val result = if (evt.admissionType == "Free open") {
                            SeoRouteApiClient.addInterest(evt.id).map { evt }
                        } else {
                            SeoRouteApiClient.bookEvent(
                                eventId = evt.id,
                                isPaid = !evt.isFree
                            ).map { it.event }
                        }
                        result.onSuccess { bookedEvent ->
                            SampleEvents.syncFromBackend()
                            if (currentDestination == destination) {
                                currentDestination = SeoRouteDestination.BookingSuccess(bookedEvent)
                            }
                        }.onFailure {
                            bookingError = it.message ?: "Could not confirm the booking."
                        }
                        submitting = false
                    }
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
                onOrganizerStudioClick = null,
                onSignOutClick = {
                    currentDestination = SeoRouteDestination.SignIn
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.OrganizerDashboard -> {
            OrganizerDashboardScreen(
                selectedNavTab = OrganizerNavTab.OVERVIEW,
                onNavTabSelected = handleOrganizerNavTabSelected,
                onCreateEventClick = {
                    previousOrganizerDestination = SeoRouteDestination.OrganizerDashboard
                    currentDestination = SeoRouteDestination.OrganizerCreateEditEvent(
                        OrganizerRepository.createDefaultNewEvent()
                    )
                },
                onScanTicketClick = {
                    currentDestination = SeoRouteDestination.CheckInScanner
                },
                onSeeAllEventsClick = {
                    currentDestination = SeoRouteDestination.OrganizerViewEvents
                },
                onEditEventClick = { event ->
                    previousOrganizerDestination = SeoRouteDestination.OrganizerDashboard
                    currentDestination = SeoRouteDestination.OrganizerCreateEditEvent(event)
                },
                onSignOutClick = {
                    currentDestination = SeoRouteDestination.SignIn
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.OrganizerViewEvents -> {
            OrganizerViewEventsScreen(
                selectedNavTab = OrganizerNavTab.EVENTS,
                onNavTabSelected = handleOrganizerNavTabSelected,
                onEditEventClick = { event ->
                    previousOrganizerDestination = SeoRouteDestination.OrganizerViewEvents
                    currentDestination = SeoRouteDestination.OrganizerCreateEditEvent(event)
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.OrganizerCreateEditEvent -> {
            var saving by remember(destination.event.id) { mutableStateOf(false) }
            var saveError by remember(destination.event.id) { mutableStateOf<String?>(null) }
            val fromAdmin = previousOrganizerDestination == SeoRouteDestination.AdminConsole
            CreateEditEventScreen(
                initialEvent = destination.event,
                saving = saving,
                saveError = saveError,
                onBackClick = {
                    currentDestination = previousOrganizerDestination
                },
                onSaveAndPublish = { savedEvent ->
                    val isNew = savedEvent.id.startsWith("org_new_")
                    when {
                        fromAdmin && isNew -> saveError = "Creating events as an admin requires backend support."
                        !fromAdmin && !isNew && savedEvent.id.startsWith("org_") -> {
                            OrganizerRepository.saveOrUpdateEvent(savedEvent)
                            currentDestination = SeoRouteDestination.OrganizerViewEvents
                        }
                        !saving -> {
                            saving = true
                            saveError = null
                            coroutineScope.launch {
                                val result = if (isNew) SeoRouteApiClient.createOrganizerEvent(savedEvent)
                                else SeoRouteApiClient.updateOrganizerEvent(savedEvent)
                                saving = false
                                result.onSuccess {
                                    if (currentDestination == destination) {
                                        currentDestination = if (fromAdmin) SeoRouteDestination.AdminConsole
                                        else SeoRouteDestination.OrganizerViewEvents
                                    }
                                    if (!fromAdmin) OrganizerRepository.syncFromBackend()
                                    SampleEvents.syncFromBackend()
                                }.onFailure { saveError = it.message ?: "Could not save the event." }
                            }
                        }
                        else -> Unit
                    }
                },
                onDeleteEvent = { eventId ->
                    OrganizerRepository.deleteEvent(eventId)
                    coroutineScope.launch {
                        if (!eventId.startsWith("org_")) {
                            SeoRouteApiClient.cancelOrganizerEvent(eventId)
                            OrganizerRepository.syncFromBackend()
                        }
                    }
                    currentDestination = previousOrganizerDestination
                },
                showDeleteAction = !fromAdmin,
                modifier = modifier
            )
        }

        is SeoRouteDestination.OrganizerProfileStaff -> {
            OrganizerProfileStaffScreen(
                selectedNavTab = destination.activeTab,
                onNavTabSelected = handleOrganizerNavTabSelected,
                onSignOutClick = {
                    currentDestination = SeoRouteDestination.SignIn
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.AttendeeManagement -> {
            AttendeeManagementScreen(
                eventTitle = destination.eventTitle,
                selectedNavTab = OrganizerNavTab.PEOPLE,
                onNavTabSelected = handleOrganizerNavTabSelected,
                onBackClick = {
                    if (!isTicketingStaffOnly) {
                        currentDestination = SeoRouteDestination.OrganizerDashboard
                    }
                },
                onOpenScannerClick = {
                    currentDestination = SeoRouteDestination.CheckInScanner
                },
                onSignOutClick = {
                    currentDestination = SeoRouteDestination.SignIn
                },
                isTicketingStaffOnly = isTicketingStaffOnly,
                modifier = modifier
            )
        }

        is SeoRouteDestination.CheckInScanner -> {
            CheckInScannerScreen(
                onBackClick = {
                    currentDestination = SeoRouteDestination.AttendeeManagement(
                        eventTitle = OrganizerRepository.getCurrentAssignedEvent().title
                    )
                },
                onScanSuccess = { attendee ->
                    val eventTitle = OrganizerRepository.getCurrentAssignedEvent().title
                    currentDestination = SeoRouteDestination.CheckInResult(
                        attendeeName = attendee.name,
                        eventTitle = eventTitle
                    )
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.CheckInResult -> {
            CheckInResultScreen(
                attendeeName = destination.attendeeName,
                eventTitle = destination.eventTitle,
                onScanNextTicketClick = {
                    currentDestination = SeoRouteDestination.CheckInScanner
                },
                onBackToAttendeesClick = {
                    currentDestination = SeoRouteDestination.AttendeeManagement(
                        eventTitle = destination.eventTitle
                    )
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.AdminOverview -> {
            AdminOverviewScreen(
                selectedNavTab = AdminNavTab.OVERVIEW,
                onNavTabSelected = handleAdminNavTabSelected,
                onAddAgencyClick = {
                    currentDestination = SeoRouteDestination.AdminManageOrganizers
                },
                onCreateEventClick = {
                    previousOrganizerDestination = SeoRouteDestination.AdminConsole
                    currentDestination = SeoRouteDestination.OrganizerCreateEditEvent(
                        OrganizerRepository.createDefaultNewEvent()
                    )
                },
                onSignOutClick = {
                    currentDestination = SeoRouteDestination.SignIn
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.AdminConsole -> {
            AdminConsoleScreen(
                selectedNavTab = AdminNavTab.EVENTS,
                onNavTabSelected = handleAdminNavTabSelected,
                onCreateEventClick = {
                    previousOrganizerDestination = SeoRouteDestination.AdminConsole
                    currentDestination = SeoRouteDestination.OrganizerCreateEditEvent(
                        OrganizerRepository.createDefaultNewEvent()
                    )
                },
                onEditEventClick = { event ->
                    previousOrganizerDestination = SeoRouteDestination.AdminConsole
                    currentDestination = SeoRouteDestination.OrganizerCreateEditEvent(event)
                },
                modifier = modifier
            )
        }

        is SeoRouteDestination.AdminManageOrganizers -> {
            AdminManageOrganizersScreen(
                selectedNavTab = AdminNavTab.PEOPLE,
                onNavTabSelected = handleAdminNavTabSelected,
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

@Preview(name = "14_Organizer_dashboard 1", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun Preview14OrganizerDashboard1() {
    SeoRouteTheme {
        OrganizerDashboardScreen(
            onNavTabSelected = {},
            onCreateEventClick = {},
            onScanTicketClick = {},
            onSeeAllEventsClick = {},
            onEditEventClick = {}
        )
    }
}

@Preview(name = "View Events", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewOrganizerViewEvents() {
    SeoRouteTheme {
        OrganizerViewEventsScreen(
            onNavTabSelected = {},
            onEditEventClick = {}
        )
    }
}

@Preview(name = "Create/Edit Event", showBackground = true, widthDp = 390, heightDp = 1349)
@Composable
fun PreviewCreateEditEvent() {
    SeoRouteTheme {
        CreateEditEventScreen(
            onBackClick = {},
            onSaveAndPublish = {},
            onDeleteEvent = {}
        )
    }
}

@Preview(name = "Organizer Profile & Ticketing Staff", showBackground = true, widthDp = 390, heightDp = 958)
@Composable
fun PreviewOrganizerProfileStaff() {
    SeoRouteTheme {
        OrganizerProfileStaffScreen(
            onNavTabSelected = {}
        )
    }
}

@Preview(name = "16_Attendee_management 1", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun Preview16AttendeeManagement1() {
    SeoRouteTheme {
        AttendeeManagementScreen(
            onNavTabSelected = {},
            onBackClick = {}
        )
    }
}

@Preview(name = "17_Check_in_scanner 1", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun Preview17CheckInScanner1() {
    SeoRouteTheme {
        CheckInScannerScreen(
            onBackClick = {},
            onScanSuccess = {}
        )
    }
}

@Preview(name = "18_Check_in_result 1", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun Preview18CheckInResult1() {
    SeoRouteTheme {
        CheckInResultScreen(
            onScanNextTicketClick = {},
            onBackToAttendeesClick = {}
        )
    }
}

@Preview(name = "Overview Page", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewAdminOverviewPage() {
    SeoRouteTheme {
        AdminOverviewScreen(
            onNavTabSelected = {}
        )
    }
}

@Preview(name = "Console", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewAdminConsole() {
    SeoRouteTheme {
        AdminConsoleScreen(
            onNavTabSelected = {}
        )
    }
}

@Preview(name = "Add/Edit Organizers", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewAdminManageOrganizers() {
    SeoRouteTheme {
        AdminManageOrganizersScreen(
            onNavTabSelected = {}
        )
    }
}
