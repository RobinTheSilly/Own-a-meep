package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryHoney,
    onPrimary = Color.White,
    primaryContainer = PrimaryHoneyLight,
    onPrimaryContainer = PrimaryHoneyDark,
    secondary = SecondaryMint,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6F5E3),
    onSecondaryContainer = Color(0xFF1E5E35),
    tertiary = TertiaryRose,
    onTertiary = Color.White,
    background = PastelYellowBg,
    onBackground = OnSurfaceWarm,
    surface = SurfaceCream,
    onSurface = OnSurfaceWarm,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = OnSurfaceMuted
)

private val DarkColorScheme = lightColorScheme(
    primary = PrimaryHoney,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF6B4908),
    onPrimaryContainer = PrimaryHoneyLight,
    secondary = SecondaryMint,
    onSecondary = Color.Black,
    tertiary = TertiaryRose,
    background = Color(0xFF2C2719),
    onBackground = Color(0xFFFFF7DB),
    surface = Color(0xFF383222),
    onSurface = Color(0xFFFFF7DB),
    surfaceVariant = Color(0xFF47402F),
    onSurfaceVariant = Color(0xFFD8CEBA)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
