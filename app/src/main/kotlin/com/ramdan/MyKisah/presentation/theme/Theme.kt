package com.ramdan.MyKisah.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

val MyKisahTypography = Typography(
    displayMedium = TextStyle(fontFamily = FontFamily.Serif, fontSize = 28.sp, fontWeight = FontWeight.Normal),
    titleLarge = TextStyle(fontFamily = FontFamily.Serif, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, fontWeight = FontWeight.Normal),
    labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.Medium)
)

private val LightColorScheme = lightColorScheme(
    primary = Amber500,
    onPrimary = Charcoal800,
    primaryContainer = Sand100,
    onPrimaryContainer = Charcoal800,
    background = Sand50,
    onBackground = Charcoal800,
    surface = Color.White,
    onSurface = Charcoal800,
    surfaceVariant = Sand100,
    onSurfaceVariant = Charcoal600,
    outline = Sand100,
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = Amber400,
    onPrimary = Ink900,
    primaryContainer = Ink700,
    onPrimaryContainer = Amber400,
    background = Ink900,
    onBackground = Slate300,
    surface = Ink800,
    onSurface = Slate300,
    surfaceVariant = Ink700,
    onSurfaceVariant = Slate300,
    outline = Ink600,
    error = ErrorRed
)

@Composable
fun MyKisahTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MyKisahTypography,
        content = content
    )
}