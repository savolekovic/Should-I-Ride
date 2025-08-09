package me.vosaa.shouldiride.ui.theme

import androidx.compose.ui.graphics.Color

/** Primary brand color. */
val Primary = Color(0xFF2196F3)
/** Default card surface background color. */
val Surface = Color(0xFFF5F5F5)

// Updated weather score colors for better gradient transitions
/** Red used for critical/unsafe conditions. */
val BadWeatherColor = Color(0xFFE53935)
/** Orange used for poor but rideable conditions. */
val PoorWeatherColor = Color(0xFFFF9800)
/** Green used for good conditions. */
val GoodWeatherColor = Color(0xFF4CAF50)

/**
 * Legacy helper mapping score to color. Prefer [getBikeScoreColor] in Theme.kt
 * which also considers critical conditions.
 */
fun getBikeScoreColor(score: Int): Color {
    return when {
        score < 30 -> BadWeatherColor           // Unsafe for scooters
        score < 50 -> PoorWeatherColor     // High caution advised
        score < 70 -> PoorWeatherColor    // Moderate caution
        else -> GoodWeatherColor                // Good scooter conditions
    }
}
