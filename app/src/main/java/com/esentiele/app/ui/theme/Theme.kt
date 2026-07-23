package com.esentiele.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ChampagneGold,
    secondary = RoseGold,
    tertiary = DeepGold,
    background = RichOnyx,
    surface = DarkCharcoal,
    surfaceVariant = SoftGraphite,
    onPrimary = DeepBlack,
    onSecondary = IvoryWhite,
    onTertiary = DeepBlack,
    onBackground = IvoryWhite,
    onSurface = IvoryWhite,
    onSurfaceVariant = MutedChampagne,
    error = MutedRuby,
    onError = IvoryWhite,
    outline = SubtleSilver
)

private val LightColorScheme = lightColorScheme(
    primary = ChampagneGold,
    secondary = RoseGold,
    tertiary = DeepGold,
    background = CreamBackground,
    surface = PaleSurface,
    surfaceVariant = IvoryWhite,
    onPrimary = DeepBlack,
    onSecondary = IvoryWhite,
    onTertiary = DeepBlack,
    onBackground = DarkBrown,
    onSurface = DarkBrown,
    onSurfaceVariant = WarmGrey,
    error = MutedRuby,
    onError = IvoryWhite,
    outline = SubtleSilver
)

@Composable
fun EsentieleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Defaulting to Dark Scheme to align with the premium brand identity unless light theme is explicitly requested/system default
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

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
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
