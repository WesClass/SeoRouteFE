package com.example.seoroute.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.seoroute.network.SeoRouteApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class DummyUserAccount(
    val username: String,
    val email: String,
    val password: String,
    val isOrganizer: Boolean = false,
    val role: String = if (isOrganizer) "organizer" else "attendee",
    val userId: String? = null,
    val organizerId: String? = null
) {
    val isTicketingStaffOnly: Boolean
        get() = role.equals("ticketing", ignoreCase = true)

    val isAdmin: Boolean
        get() = role.equals("admin", ignoreCase = true)

    val isFullOrganizer: Boolean
        get() = role.equals("organizer", ignoreCase = true) || role.equals("admin", ignoreCase = true)
}

/**
 * Authentication manager that connects to the live backend (`POST /api/v1/auth/dev` and `/api/v1/auth/sync`)
 * with an automatic offline fallback to seeded accounts.
 */
object DummyAuthRepository {
    val demoAccount = DummyUserAccount(
        username = "Kim Minji",
        email = "attendee1@example.com",
        password = "password123",
        isOrganizer = false,
        role = "attendee"
    )

    val demoOrganizerAccount = DummyUserAccount(
        username = "Hangang Live EO",
        email = "organizer@hanganglive.example",
        password = "password123",
        isOrganizer = true,
        role = "organizer",
        organizerId = "00000000-0000-0000-0000-000000000002"
    )

    val demoTicketingAccount = DummyUserAccount(
        username = "Gate A Scanner",
        email = "gate1@hanganglive.example",
        password = "password123",
        isOrganizer = true,
        role = "ticketing",
        organizerId = "00000000-0000-0000-0000-000000000002"
    )

    val demoAdminAccount = DummyUserAccount(
        username = "Platform Admin",
        email = "admin@seoroute.example",
        password = "password123",
        isOrganizer = true,
        role = "admin"
    )

    private val registeredUsers = mutableStateListOf(
        demoAccount,
        demoOrganizerAccount,
        demoTicketingAccount,
        demoAdminAccount,
        DummyUserAccount(
            username = "Gate B Scanner",
            email = "gate2@hanganglive.example",
            password = "password123",
            isOrganizer = true,
            role = "ticketing",
            organizerId = "00000000-0000-0000-0000-000000000002"
        ),
        DummyUserAccount(
            username = "SeoulExplorer",
            email = "demo@seoroute.com",
            password = "password123",
            isOrganizer = false
        ),
        DummyUserAccount(
            username = "JisooKim",
            email = "jisoo@seoroute.com",
            password = "password123",
            isOrganizer = false
        )
    )

    val currentUser = mutableStateOf<DummyUserAccount?>(null)

    fun signOut() {
        currentUser.value = null
        SeoRouteApiClient.accessToken = null
        CoroutineScope(Dispatchers.IO).launch {
            SeoRouteApiClient.clearStoredSession()
        }
    }

    /**
     * Authenticates against the deployed backend `POST /api/v1/auth/dev` and stores the JWT token,
     * then syncs live events/tickets/organizer data. Falls back to local validation if offline.
     */
    suspend fun signInRemoteOrLocal(emailInput: String, passwordInput: String): Result<DummyUserAccount> {
        val trimmedEmail = emailInput.trim()
        if (trimmedEmail.isEmpty() || passwordInput.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please enter both your email and password."))
        }

        val knownLocal = registeredUsers.firstOrNull {
            it.email.equals(trimmedEmail, ignoreCase = true)
        }
        val role = when {
            knownLocal != null -> knownLocal.role
            trimmedEmail.contains("admin", ignoreCase = true) -> "admin"
            trimmedEmail.contains("gate", ignoreCase = true) ||
                trimmedEmail.contains("ticket", ignoreCase = true) ||
                trimmedEmail.contains("staff", ignoreCase = true) -> "ticketing"
            trimmedEmail.contains("organizer", ignoreCase = true) -> "organizer"
            else -> "attendee"
        }
        val displayName = knownLocal?.username
            ?: trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercaseChar() }
        val defaultOrgId = knownLocal?.organizerId
            ?: if (trimmedEmail.endsWith("@hanganglive.example", ignoreCase = true)) {
                "00000000-0000-0000-0000-000000000002"
            } else null

        val remoteResult = SeoRouteApiClient.devLogin(
            email = trimmedEmail,
            name = displayName,
            role = role,
            organizerId = defaultOrgId
        )

        return remoteResult.fold(
            onSuccess = { session ->
                val isOrgOrTicketing = session.user.role == "organizer" ||
                    session.user.role == "ticketing" ||
                    session.user.role == "admin"
                val userAccount = DummyUserAccount(
                    username = session.user.name,
                    email = session.user.email,
                    password = passwordInput,
                    isOrganizer = isOrgOrTicketing,
                    role = session.user.role,
                    userId = session.user.id,
                    organizerId = session.user.organizerId
                )
                currentUser.value = userAccount
                SampleEvents.syncFromBackend()
                if (userAccount.isOrganizer) {
                    OrganizerRepository.syncFromBackend()
                }
                Result.success(userAccount)
            },
            onFailure = {
                // Offline fallback
                signIn(emailInput, passwordInput)
            }
        )
    }

    fun signIn(emailInput: String, passwordInput: String): Result<DummyUserAccount> {
        val trimmedEmail = emailInput.trim()
        if (trimmedEmail.isEmpty() || passwordInput.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please enter both your email and password."))
        }
        val matchedUser = registeredUsers.firstOrNull {
            it.email.equals(trimmedEmail, ignoreCase = true) && it.password == passwordInput
        }
        return if (matchedUser != null) {
            currentUser.value = matchedUser
            Result.success(matchedUser)
        } else {
            Result.failure(
                IllegalArgumentException(
                    "Invalid email or password. Try attendee1@example.com or organizer@hanganglive.example / password123"
                )
            )
        }
    }

    suspend fun signInWithGoogleRemoteOrLocal(): DummyUserAccount {
        val remote = SeoRouteApiClient.devLogin(
            email = demoAccount.email,
            name = demoAccount.username,
            role = "attendee"
        )
        return remote.fold(
            onSuccess = { session ->
                val acc = DummyUserAccount(
                    username = session.user.name,
                    email = session.user.email,
                    password = "",
                    isOrganizer = session.user.role == "organizer",
                    userId = session.user.id,
                    organizerId = session.user.organizerId
                )
                currentUser.value = acc
                SampleEvents.syncFromBackend()
                acc
            },
            onFailure = { signInWithGoogle() }
        )
    }

    fun signInWithGoogle(): DummyUserAccount {
        val googleUser = DummyUserAccount(
            username = "GoogleExplorer",
            email = "google.user@gmail.com",
            password = "",
            isOrganizer = false
        )
        currentUser.value = googleUser
        return googleUser
    }

    suspend fun createAccountRemoteOrLocal(
        usernameInput: String,
        emailInput: String,
        passwordInput: String
    ): Result<DummyUserAccount> {
        val localValidation = createAccount(usernameInput, emailInput, passwordInput)
        if (localValidation.isFailure) return localValidation

        val createdLocal = localValidation.getOrThrow()
        val role = if (createdLocal.isOrganizer) "organizer" else "attendee"
        SeoRouteApiClient.devLogin(
            email = createdLocal.email,
            name = createdLocal.username,
            role = role
        ).onSuccess { session ->
            val synced = createdLocal.copy(
                userId = session.user.id,
                organizerId = session.user.organizerId,
                isOrganizer = session.user.role == "organizer"
            )
            currentUser.value = synced
            SampleEvents.syncFromBackend()
            if (synced.isOrganizer) {
                OrganizerRepository.syncFromBackend()
            }
            return Result.success(synced)
        }
        return Result.success(createdLocal)
    }

    fun createAccount(
        usernameInput: String,
        emailInput: String,
        passwordInput: String
    ): Result<DummyUserAccount> {
        val trimmedUsername = usernameInput.trim()
        val trimmedEmail = emailInput.trim()

        if (trimmedUsername.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please choose a username."))
        }
        if (trimmedEmail.isEmpty() || !trimmedEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        if (passwordInput.length < 8) {
            return Result.failure(IllegalArgumentException("Password must be at least 8 characters."))
        }
        if (registeredUsers.any { it.email.equals(trimmedEmail, ignoreCase = true) }) {
            return Result.failure(IllegalArgumentException("An account with this email already exists."))
        }

        val newUser = DummyUserAccount(
            username = trimmedUsername,
            email = trimmedEmail,
            password = passwordInput,
            isOrganizer = trimmedEmail.contains("organizer", ignoreCase = true)
        )
        registeredUsers.add(newUser)
        currentUser.value = newUser
        return Result.success(newUser)
    }

    fun sendPasswordReset(emailInput: String): Result<String> {
        val trimmedEmail = emailInput.trim()
        if (trimmedEmail.isEmpty() || !trimmedEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        return Result.success("Password reset link sent to $trimmedEmail!")
    }
}
