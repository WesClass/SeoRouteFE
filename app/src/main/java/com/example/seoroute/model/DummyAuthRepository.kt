package com.example.seoroute.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

data class DummyUserAccount(
    val username: String,
    val email: String,
    val password: String
)

// In-memory dummy authentication manager for frontend testing without a backend.
object DummyAuthRepository {
    val demoAccount = DummyUserAccount(
        username = "SeoulExplorer",
        email = "demo@seoroute.com",
        password = "password123"
    )

    private val registeredUsers = mutableStateListOf(
        demoAccount,
        DummyUserAccount(
            username = "JisooKim",
            email = "jisoo@seoroute.com",
            password = "password123"
        )
    )

    val currentUser = mutableStateOf<DummyUserAccount?>(null)

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
                    "Invalid email or password. Try demo@seoroute.com / password123"
                )
            )
        }
    }

    fun signInWithGoogle(): DummyUserAccount {
        val googleUser = DummyUserAccount(
            username = "GoogleExplorer",
            email = "google.user@gmail.com",
            password = ""
        )
        currentUser.value = googleUser
        return googleUser
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
            password = passwordInput
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
