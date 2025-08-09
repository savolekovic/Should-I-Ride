package me.vosaa.shouldiride.presentation.weather.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.vosaa.shouldiride.ui.theme.BadWeatherColor
import me.vosaa.shouldiride.ui.theme.getBikeScoreColor

/**
 * Circular indicator that visualizes the ride score and warns when conditions
 * are flagged as critical.
 *
 * @param score Ride score from 0 to 100
 * @param hasCriticalConditions Whether any unsafe conditions are present
 */
@Composable
fun BikeScoreIndicator(score: Int, hasCriticalConditions: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(getBikeScoreColor(score, hasCriticalConditions)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$score",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "score",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        if (hasCriticalConditions) {
            Text(
                text = "Not Safe to Ride",
                color = BadWeatherColor,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}