package me.vosaa.bestbikeday.ui.theme

import androidx.compose.ui.graphics.Color

// Main theme colors
val Primary = Color(0xFF2196F3)
val Surface = Color(0xFFFFFFFF)

// Updated me.vosaa.bestbikeday.data.remote.model.Weather score colors for better gradient transitions
val BadWeatherColor = Color(0xFFD32F2F)     // Darker Red - unsafe conditions
val MediumLowWeatherColor = Color(0xFFFFA726)  // Orange - high caution
val MediumHighWeatherColor = Color(0xFFFFF176) // Yellow - moderate caution
val GoodWeatherColor = Color(0xFF4CAF50)    // Green - ideal conditions

fun getBikeScoreColor(score: Int): Color {
    return when {
        score < 30 -> BadWeatherColor           // Unsafe for scooters
        score < 50 -> MediumLowWeatherColor     // High caution advised
        score < 70 -> MediumHighWeatherColor    // Moderate caution
        else -> GoodWeatherColor                // Good scooter conditions
    }
}
