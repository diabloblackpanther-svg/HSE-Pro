package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SafetyOrangePrimary,
    onPrimary = IndustrialDarkNavy,
    primaryContainer = SafetyOrangeDark,
    onPrimaryContainer = SafetyOrangeLight,
    secondary = SafetyYellow,
    onSecondary = IndustrialDarkNavy,
    secondaryContainer = IndustrialSurface,
    onSecondaryContainer = SafetyYellowContainer,
    tertiary = EmeraldGreen,
    onTertiary = IndustrialDarkNavy,
    tertiaryContainer = DarkSurfaceVariant,
    onTertiaryContainer = EmeraldGreenContainer,
    error = HazardRed,
    onError = LightSurface,
    errorContainer = IndustrialSurface,
    onErrorContainer = HazardRedContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = SafetyOrangePrimary,
    onPrimary = LightSurface,
    primaryContainer = SafetyOrangeLight,
    onPrimaryContainer = SafetyOrangeDark,
    secondary = SafetyYellow,
    onSecondary = IndustrialDarkNavy,
    secondaryContainer = SafetyYellowContainer,
    onSecondaryContainer = IndustrialDarkNavy,
    tertiary = EmeraldGreen,
    onTertiary = LightSurface,
    tertiaryContainer = EmeraldGreenContainer,
    onTertiaryContainer = IndustrialDarkNavy,
    error = HazardRed,
    onError = LightSurface,
    errorContainer = HazardRedContainer,
    onErrorContainer = HazardRed,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = IndustrialSlate
)

@Composable
fun HSEProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
