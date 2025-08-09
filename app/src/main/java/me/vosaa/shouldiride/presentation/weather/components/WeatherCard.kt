package me.vosaa.shouldiride.presentation.weather.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.vosaa.shouldiride.R
import me.vosaa.shouldiride.domain.model.WeatherForecast
import me.vosaa.shouldiride.ui.theme.BadWeatherColor
import me.vosaa.shouldiride.ui.theme.Surface
import kotlin.math.roundToInt

/**
 * Card summarizing a day's 07:00 forecast with bike score and key metrics.
 */
@Composable
fun WeatherCard(forecast: WeatherForecast, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = forecast.date,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "7:00",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    if (forecast.hasCriticalConditions) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_warning),
                                contentDescription = "Warning",
                                tint = BadWeatherColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = getCriticalConditionMessage(forecast),
                                style = MaterialTheme.typography.bodySmall,
                                color = BadWeatherColor
                            )
                        }
                    }
                    Text(
                        text = forecast.conditions,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                BikeScoreIndicator(
                    score = forecast.bikeScore,
                    hasCriticalConditions = forecast.hasCriticalConditions
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherInfoItem(
                    painter = painterResource(R.drawable.ic_thermostat),
                    value = "${forecast.temperature}°C",
                    label = "Temp"
                )
                WeatherInfoItem(
                    painter = painterResource(R.drawable.ic_air),
                    value = "${forecast.windSpeed.roundToInt()} m/s",
                    label = "Wind"
                )
                WeatherInfoItem(
                    painter = painterResource(R.drawable.ic_cloud_rain),
                    value = "${forecast.rainChance}%",
                    label = "Rain"
                )
            }
        }
    }
}

/**
 * Returns a user-facing warning for the specific critical condition that applies.
 */
private fun getCriticalConditionMessage(forecast: WeatherForecast): String {
    return when {
        forecast.rainChance > 70 -> "Heavy Rain Expected"
        forecast.windSpeed > 15.0 -> "Strong Wind Warning"
        forecast.temperature < -5 -> "Extreme Cold"
        forecast.temperature > 40 -> "Extreme Heat"
        else -> "Unsafe Conditions"
    }
}