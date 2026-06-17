package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = WishCartCharcoal,
    surface = Color(0xFF2C3035),
    onPrimary = WishCartCharcoal,
    onSecondary = WishCartWhite,
    onBackground = WishCartWhite,
    onSurface = WishCartWhite
  )

private val LightColorScheme =
  lightColorScheme(
    primary = WishCartNavy,
    secondary = WishCartTeal,
    tertiary = PurpleGrey40,
    background = WishCartWhite,
    surface = WishCartLightGray,
    onPrimary = WishCartWhite,
    onSecondary = WishCartCharcoal,
    onBackground = WishCartCharcoal,
    onSurface = WishCartCharcoal
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
