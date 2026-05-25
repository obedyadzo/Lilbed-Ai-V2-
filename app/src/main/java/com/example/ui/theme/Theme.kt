package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = Purple80,
  secondary = PurpleGrey80,
  tertiary = Pink80,
  background = Color(0xFF090A10),
  surface = Color(0xFF131422),
  onPrimary = Color(0xFF030508),
  onSecondary = Color.White,
  onTertiary = Color(0xFF030508),
  onBackground = Color(0xFFE8ECF5),
  onSurface = Color(0xFFE8ECF5)
)

private val LightColorScheme = lightColorScheme(
  primary = Purple40,
  secondary = PurpleGrey40,
  tertiary = Pink40,
  background = Color(0xFFFAFBFC),
  surface = Color.White,
  onPrimary = Color.White,
  onSecondary = Color.White,
  onTertiary = Color.White,
  onBackground = Color(0xFF0C0E15),
  onSurface = Color(0xFF0C0E15)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force futuristic deep styling for Lilbed brand by default
  dynamicColor: Boolean = false, // Disable dynamic colors to preserve our beautiful Neon Glassmorphism identity!
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
