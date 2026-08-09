package com.beershop.pos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand Colors - Beer/Bar theme
val BeerRed = Color(0xFFB71C1C)
val BeerDarkRed = Color(0xFF7F0000)
val BeerGold = Color(0xFFFFC107)
val BeerAmber = Color(0xFFFF8F00)
val BeerBrown = Color(0xFF5D4037)
val BeerCream = Color(0xFFFFF8E1)

val DarkBeerRed = Color(0xFFEF5350)
val DarkBeerGold = Color(0xFFFFD54F)

private val LightColorScheme = lightColorScheme(
    primary = BeerRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = BeerBrown,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDAD6),
    onSecondaryContainer = Color(0xFF2C1512),
    tertiary = BeerAmber,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDDB3),
    onTertiaryContainer = Color(0xFF2A1700),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color.White,
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF201A1A),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF201A1A),
    surfaceVariant = Color(0xFFF5DDDB),
    onSurfaceVariant = Color(0xFF534342),
    outline = Color(0xFF857371),
    inverseSurface = Color(0xFF362F2E),
    inverseOnSurface = Color(0xFFFBEEEC),
    inversePrimary = Color(0xFFFFB4AB),
    surfaceTint = BeerRed
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkBeerRed,
    onPrimary = Color(0xFF690005),
    primaryContainer = Color(0xFF93000A),
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = Color(0xFFE7BDB6),
    onSecondary = Color(0xFF442A26),
    secondaryContainer = Color(0xFF5D3F3B),
    onSecondaryContainer = Color(0xFFFFDAD6),
    tertiary = Color(0xFFFFB950),
    onTertiary = Color(0xFF472900),
    tertiaryContainer = Color(0xFF663F00),
    onTertiaryContainer = Color(0xFFFFDDB3),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF201A1A),
    onBackground = Color(0xFFECE0DE),
    surface = Color(0xFF201A1A),
    onSurface = Color(0xFFECE0DE),
    surfaceVariant = Color(0xFF534342),
    onSurfaceVariant = Color(0xFFD8C2BF),
    outline = Color(0xFFA08C8A),
    inverseSurface = Color(0xFFECE0DE),
    inverseOnSurface = Color(0xFF362F2E),
    inversePrimary = Color(0xFFB71C1C),
    surfaceTint = DarkBeerRed
)

@Composable
fun BeerShopPOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
