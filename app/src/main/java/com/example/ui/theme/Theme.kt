package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RoyalBlueAccent,
    secondary = SilverLuxury,
    tertiary = SilverShine,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = Color(0xFF112239),
    onPrimary = PureWhite,
    onSecondary = DarkBackground,
    onTertiary = DarkBackground,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = SilverShine,
    outline = SilverBorder
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalBluePrimary,
    secondary = RoyalBlueSecondary,
    tertiary = SilverLuxury,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = Color(0xFFEEF3F8),
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onTertiary = RoyalBluePrimary,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextPrimary,
    outline = SilverBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
