package com.esentiele.app.ui.components

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DynamicPalette(
    val dominant: Color = Color(0xFFC9A96E),
    val vibrant: Color = Color(0xFFC9A96E),
    val muted: Color = Color(0xFF9B8E7E),
    val darkVibrant: Color = Color(0xFFA8893A),
    val lightMuted: Color = Color(0xFFF5F0E8)
)

val LocalDynamicPalette = compositionLocalOf { DynamicPalette() }

suspend fun extractPalette(bitmap: Bitmap): DynamicPalette {
    return withContext(Dispatchers.Default) {
        val palette = Palette.from(bitmap).generate()
        val fallback = 0xFFC9A96E.toInt()
        DynamicPalette(
            dominant = Color(palette.getDominantColor(fallback)),
            vibrant = Color(palette.getVibrantColor(fallback)),
            muted = Color(palette.getMutedColor(0xFF9B8E7E.toInt())),
            darkVibrant = Color(palette.getDarkVibrantColor(0xFFA8893A.toInt())),
            lightMuted = Color(palette.getLightMutedColor(0xFFF5F0E8.toInt()))
        )
    }
}

@Composable
fun DynamicThemeWrapper(
    bitmap: Bitmap?,
    content: @Composable () -> Unit
) {
    var palette by remember { mutableStateOf(DynamicPalette()) }

    LaunchedEffect(bitmap) {
        if (bitmap != null) {
            palette = extractPalette(bitmap)
        }
    }

    CompositionLocalProvider(LocalDynamicPalette provides palette) {
        content()
    }
}
