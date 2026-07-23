package com.esentiele.app.domain.model

data class ColorPalette(
    val seasonType: String,
    val bestColors: List<String>,
    val avoidColors: List<String>,
    val description: String
) {
    companion object {
        val WarmSpring = ColorPalette(
            seasonType = "Warm Spring",
            bestColors = listOf("#F4C430", "#FF7E00", "#7CFC00", "#FFC0CB"),
            avoidColors = listOf("#000000", "#808080", "#000080"),
            description = "Bright, warm, and clear colors."
        )

        val CoolSummer = ColorPalette(
            seasonType = "Cool Summer",
            bestColors = listOf("#C0C0C0", "#E6E6FA", "#87CEEB", "#FFB6C1"),
            avoidColors = listOf("#FF4500", "#FFA500", "#FFFF00"),
            description = "Soft, cool, and muted colors."
        )

        val WarmAutumn = ColorPalette(
            seasonType = "Warm Autumn",
            bestColors = listOf("#8B4513", "#D2691E", "#556B2F", "#DAA520"),
            avoidColors = listOf("#FF69B4", "#00FFFF", "#E6E6FA"),
            description = "Rich, warm, and earthy colors."
        )

        val CoolWinter = ColorPalette(
            seasonType = "Cool Winter",
            bestColors = listOf("#000000", "#FFFFFF", "#0000FF", "#DC143C"),
            avoidColors = listOf("#D2B48C", "#F5DEB3", "#BDB76B"),
            description = "Vivid, cool, and high-contrast colors."
        )
    }
}
