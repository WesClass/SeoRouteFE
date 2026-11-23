package com.example.seoroute.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = SeoPrimary,
    secondary = SeoTextSecondary,
    tertiary = SeoGreenText,
    background = SeoBackground,
    surface = SeoSurface,
    onPrimary = SeoSurface,
    onSecondary = SeoSurface,
    onBackground = SeoTextPrimary,
    onSurface = SeoTextPrimary
)

@Composable
fun SeoRouteTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
