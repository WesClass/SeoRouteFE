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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.R
import com.example.seoroute.model.DummyAuthRepository
import com.example.seoroute.ui.theme.SeoGreenBg
import com.example.seoroute.ui.theme.SeoGreenText
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoPrimaryLight
import com.example.seoroute.ui.theme.SeoSurface
import com.example.seoroute.ui.theme.SeoTextMuted
import com.example.seoroute.ui.theme.SeoTextPrimary
import com.example.seoroute.ui.theme.SeoTextSecondary

// Welcome Screen
@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit,
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Background Seoul photo
        Image(
            painter = painterResource(id = R.drawable.event_seoul_culture_night),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Navy overlay (#252C5C at 70% opacity, matching welcome_overlay.svg)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xB3252C5C))
        )

        // Decorative top-right purple circle (Figma node 70:3274)
        Box(
            modifier = Modifier
                .offset(x = 189.dp, y = 68.dp)
                .size(280.dp)
                .clip(CircleShape)
                .background(Color(0x476B5DDF))
        )

        // Decorative bottom-left coral circle (Figma node 70:3275)
        Box(
            modifier = Modifier
                .offset(x = (-40).dp, y = 498.dp)
                .size(220.dp)
                .clip(CircleShape)
                .background(Color(0x30F98269))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(106.dp))

                // Gradient-masked SeoRoute Logo & Wordmark (Figma node 103:13)
                val logoBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFAC4BF4),
                        Color(0xFFD2B8ED)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(450f, 950f)
                )
                Image(
                    painter = painterResource(id = R.drawable.welcome_logo_mask),
                    contentDescription = "SeoRoute",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(335.dp)
                        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(
                                    brush = logoBrush,
                                    blendMode = BlendMode.SrcIn
                                )
                            }
                        }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tagline (Figma nodes 70:3280 & 70:3281)
                Column(modifier = Modifier.padding(horizontal = 11.dp)) {
                    Text(
                        text = "Discover, reserve and experience\nthe best of Seoul.",
                        color = Color(0xFFE3E2FF),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 23.sp
                    )
                }
            }

            // Bottom Actions: "Get started" button + "Already have an account? Sign in"
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 52.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SeoPrimary)
                        .clickable { onGetStartedClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Get started",
                        color = SeoSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = SeoSurface, fontWeight = FontWeight.Normal)) {
                            append("Already have an account? ")
                        }
                        withStyle(
                            SpanStyle(
                                color = SeoSurface,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Sign in")
                        }
                    },
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable { onSignInClick() }
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }
        }
    }
}

// Sign In Screen
@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FC))
            .verticalScroll(rememberScrollState())
    ) {
        // Shared Brand Header
        AuthBrandHeader(
            title = "Welcome back",
            subtitle = "Sign in to discover Seoul."
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            AuthFormField(
                label = "Email address",
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                placeholder = "Enter your email",
                iconRes = R.drawable.ic_auth_mail
            )

            Spacer(modifier = Modifier.height(22.dp))

            AuthFormField(
                label = "Password",
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                placeholder = "Enter your password",
                iconRes = R.drawable.ic_auth_lock,
                isPassword = true
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                AuthFeedbackBanner(
                    message = errorMessage!!,
                    isError = true,
                    actionText = "Fill demo credentials",
                    onActionClick = {
                        email = DummyAuthRepository.demoAccount.email
                        password = DummyAuthRepository.demoAccount.password
                        errorMessage = null
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Links row: "Create account" (left) and "Forgot password?" (right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Create account",
                    color = SeoPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onCreateAccountClick() }
                        .padding(vertical = 2.dp)
                )
                Text(
                    text = "Forgot password?",
                    color = SeoPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onForgotPasswordClick() }
                        .padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Primary "Sign in" button with dummy auth validation
            AuthPrimaryButton(
                text = "Sign in",
                onClick = {
                    DummyAuthRepository.signIn(email, password)
                        .onSuccess { onSignInSuccess() }
                        .onFailure { err -> errorMessage = err.message }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // "OR" Separator
            AuthOrSeparator()

            Spacer(modifier = Modifier.height(18.dp))

            // "Continue with Google" button (instant dummy Google sign-in)
            ContinueWithGoogleButton(
                onClick = {
                    DummyAuthRepository.signInWithGoogle()
                    onSignInSuccess()
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Helpful demo helper chip for quick testing without typing
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(50))
                    .background(SeoPrimaryLight)
                    .clickable {
                        email = DummyAuthRepository.demoAccount.email
                        password = DummyAuthRepository.demoAccount.password
                        errorMessage = null
                    }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Demo login: ${DummyAuthRepository.demoAccount.email} (tap to fill)",
                    color = SeoPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Forgot Password screen implementation (Figma node 77:189) with dummy reset feedback.
 */
@Composable
fun ForgotPasswordScreen(
    onResetPasswordClick: () -> Unit,
    onBackToSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FC))
            .verticalScroll(rememberScrollState())
    ) {
        AuthBrandHeader(
            title = "Forgot password?",
            subtitle = "Get a reset link in your email."
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthFormField(
                label = "Email address",
                value = email,
                onValueChange = {
                    email = it
                    feedbackMessage = null
                },
                placeholder = "Enter your email",
                iconRes = R.drawable.ic_auth_mail
            )

            if (feedbackMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                AuthFeedbackBanner(
                    message = feedbackMessage!!,
                    isError = isError
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            AuthPrimaryButton(
                text = "Reset Password",
                onClick = {
                    DummyAuthRepository.sendPasswordReset(email)
                        .onSuccess { msg ->
                            isError = false
                            feedbackMessage = msg
                        }
                        .onFailure { err ->
                            isError = true
                            feedbackMessage = err.message
                        }
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            // "<- Back to sign in" link
            Row(
                modifier = Modifier
                    .clickable { onBackToSignInClick() }
                    .padding(vertical = 6.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_left_purple),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back to sign in",
                    color = SeoPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

// Create Account Screen
@Composable
fun CreateAccountScreen(
    onCreateAccountSuccess: () -> Unit,
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FC))
            .verticalScroll(rememberScrollState())
    ) {
        AuthBrandHeader(
            title = "Create account",
            subtitle = "Start your journey with SeoRoute"
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthFormField(
                label = "Username",
                value = username,
                onValueChange = {
                    username = it
                    errorMessage = null
                },
                placeholder = "Choose a username",
                iconRes = R.drawable.ic_auth_user
            )

            Spacer(modifier = Modifier.height(20.dp))

            AuthFormField(
                label = "Email address",
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                placeholder = "Enter your email",
                iconRes = R.drawable.ic_auth_mail
            )

            Spacer(modifier = Modifier.height(20.dp))

            AuthFormField(
                label = "Password",
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                placeholder = "Create a password",
                iconRes = R.drawable.ic_auth_lock,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Use at least 8 characters.",
                color = SeoTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                AuthFeedbackBanner(
                    message = errorMessage!!,
                    isError = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AuthPrimaryButton(
                text = "Create account",
                onClick = {
                    DummyAuthRepository.createAccount(username, email, password)
                        .onSuccess { onCreateAccountSuccess() }
                        .onFailure { err -> errorMessage = err.message }
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            AuthOrSeparator()

            Spacer(modifier = Modifier.height(16.dp))

            ContinueWithGoogleButton(
                onClick = {
                    DummyAuthRepository.signInWithGoogle()
                    onCreateAccountSuccess()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // "Already have an account? Sign in"
            Row(
                modifier = Modifier
                    .clickable { onSignInClick() }
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    color = SeoTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "Sign in",
                    color = SeoPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun AuthFeedbackBanner(
    message: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val bgColor = if (isError) Color(0xFFFEECEB) else SeoGreenBg
    val textColor = if (isError) Color(0xFFD92D20) else SeoGreenText

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = message,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.sp
        )
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = actionText,
                color = SeoPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

// Auth Brand Header shared by Sign In, Forgot Password, and Create Account screens.
@Composable
fun AuthBrandHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(235.dp)
            .background(SeoPrimaryLight)
    ) {
        Box(
            modifier = Modifier
                .offset(x = 220.dp, y = (-17).dp)
                .size(190.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCD5FF))
        )

        Box(
            modifier = Modifier
                .offset(x = 296.dp, y = 135.dp)
                .size(140.dp)
                .clip(CircleShape)
                .background(Color(0xFFB9AEFF))
        )

        Box(
            modifier = Modifier
                .offset(x = (-2).dp, y = 41.dp)
                .size(width = 127.dp, height = 77.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.seoroute_logo),
                contentDescription = "SeoRoute Logo",
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = SeoTextPrimary, fontWeight = FontWeight.Medium)) {
                    append("Seo")
                }
                withStyle(SpanStyle(color = Color(0xFF6E24FA), fontWeight = FontWeight.Medium)) {
                    append("Route")
                }
            },
            fontSize = 13.sp,
            modifier = Modifier.offset(x = 31.dp, y = 122.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 24.dp, vertical = 18.dp)
        ) {
            Text(
                text = title,
                color = SeoTextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle,
                color = SeoTextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun AuthFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = SeoTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            color = SeoSurface,
            border = BorderStroke(1.dp, Color(0xFFE8EBF3))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = SeoTextMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = true,
                        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                        textStyle = TextStyle(
                            color = SeoTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        cursorBrush = SolidColor(SeoPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SeoPrimary)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = SeoSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AuthOrSeparator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Color(0xFFE8EBF3)
        )
        Text(
            text = "OR",
            color = SeoTextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Color(0xFFE8EBF3)
        )
    }
}

@Composable
private fun ContinueWithGoogleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = SeoSurface,
        border = BorderStroke(1.dp, Color(0xFFE8EBF3))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = "Google",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(25.dp)
            )
            Text(
                text = "Continue with Google",
                color = SeoTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
