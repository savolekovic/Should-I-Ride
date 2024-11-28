package me.vosaa.shouldiride.ui.theme

import androidx.compose.ui.graphics.Color

// Main theme colors
val Primary = Color(0xFF2196F3)
val Surface = Color(0xFFF5F5F5)

// Updated me.vosaa.bestbikeday.data.remote.model.Weather score colors for better gradient transitions
val BadWeatherColor = Color(0xFFE53935)  // Red for critical conditions
val PoorWeatherColor = Color(0xFFFF9800) // Orange for poor but rideable conditions
val GoodWeatherColor = Color(0xFF4CAF50)    // Green for good conditions

fun getBikeScoreColor(score: Int): Color {
    return when {
        score < 30 -> BadWeatherColor           // Unsafe for scooters
        score < 50 -> PoorWeatherColor     // High caution advised
        score < 70 -> PoorWeatherColor    // Moderate caution
        else -> GoodWeatherColor                // Good scooter conditions
    }
}
