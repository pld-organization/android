package com.example.sahtek.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SahtekBlue,
    secondary = SahtekBlueLight,
    tertiary = SahtekMint,
    background = SahtekDarkBackground,
    surface = SahtekDarkSurface,
    surfaceVariant = SahtekDarkSurfaceAlt,
    onPrimary = SahtekSurface,
    onSecondary = SahtekDarkTextPrimary,
    onTertiary = SahtekTextPrimary,
    onBackground = SahtekDarkTextPrimary,
    onSurface = SahtekDarkTextPrimary,
    onSurfaceVariant = SahtekDarkTextSecondary,
    outline = SahtekBorder,
    error = SahtekError
)

private val LightColorScheme = lightColorScheme(
    primary = SahtekBlue,
    secondary = SahtekBlueLight,
    tertiary = SahtekMint,
    background = SahtekBackground,
    surface = SahtekSurface,
    surfaceVariant = SahtekSurfaceAlt,
    onPrimary = SahtekSurface,
    onSecondary = SahtekTextPrimary,
    onTertiary = SahtekTextPrimary,
    onBackground = SahtekTextPrimary,
    onSurface = SahtekTextPrimary,
    onSurfaceVariant = SahtekTextSecondary,
    outline = SahtekBorder,
    error = SahtekError
)

@Composable
fun SahtekTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
