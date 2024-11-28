package me.vosaa.bestbikeday.domain.repository

import android.util.Log
import me.vosaa.bestbikeday.data.remote.model.ForecastItem
import me.vosaa.bestbikeday.data.remote.WeatherApiService
import me.vosaa.bestbikeday.domain.model.WeatherForecast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService,
) {
    private val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    private val apiKey = "63816ac463b32cf49aad527b75298a20"

    suspend fun getWeatherForecast(lat: Double, lon: Double): List<WeatherForecast> {
        return try {
            val response = apiService.getWeatherForecast(lat, lon, apiKey = apiKey)
            val currentTime = Calendar.getInstance()
            val currentWeek = currentTime.get(Calendar.WEEK_OF_YEAR)

            val list = response.list
                .filter { forecast ->
                    val forecastTime = Calendar.getInstance().apply {
                        timeInMillis = forecast.dt * 1000
                    }

                    // Check if it's 7:00 AM, it's a workday, and it's in the current week
                    forecastTime.get(Calendar.HOUR_OF_DAY) == 7 &&
                            (forecastTime.get(Calendar.DAY_OF_YEAR) >= currentTime.get(Calendar.DAY_OF_YEAR)) &&
                            forecastTime.get(Calendar.DAY_OF_WEEK) in Calendar.MONDAY..Calendar.FRIDAY &&
                            forecastTime.get(Calendar.WEEK_OF_YEAR) == currentWeek
                }
                .take(5)
                .map { forecast ->
                    val forecastDate = Calendar.getInstance().apply {
                        timeInMillis = forecast.dt * 1000
                    }
                    val isToday =
                        forecastDate.get(Calendar.DAY_OF_YEAR) == currentTime.get(Calendar.DAY_OF_YEAR) &&
                                forecastDate.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR)

                    WeatherForecast(
                        date = if (isToday) "Today" else dayFormat.format(Date(forecast.dt * 1000)),
                        temperature = forecast.main.temp.toInt(),
                        conditions = forecast.weather.firstOrNull()?.description ?: "",
                        windSpeed = forecast.wind.speed,
                        rainChance = (forecast.pop * 100).toInt(),
                        bikeScore = calculateBikeRating(forecast)
                    )
                }
            list
        } catch (e: Exception) {
            throw e
        }
    }

    private fun calculateBikeRating(forecast: ForecastItem): Int {

        // Temperature factor (0-40 points)
        val tempScore = when {
            forecast.main.temp < -5 -> 0      // Too cold, dangerous
            forecast.main.temp < 0 -> (forecast.main.temp + 5) * 8  // Gradual penalty below 0°C
            forecast.main.temp in 0.0..30.0 -> 40                  // Ideal range
            forecast.main.temp <= 35 -> 40 - (forecast.main.temp - 30) * 2  // Gradual penalty above 30°C
            else -> 20                      // Very hot, significant penalty
        }

        // me.vosaa.bestbikeday.data.remote.model.Wind speed factor (0-30 points)
        val windScore = when {
            forecast.wind.speed < 3 -> 30  // Perfect
            forecast.wind.speed <= 6 -> 30 - (forecast.wind.speed - 3) * 5  // Light breeze
            forecast.wind.speed <= 12 -> 15 - (forecast.wind.speed - 6) * 2 // Moderate wind
            forecast.wind.speed <= 15 -> 5                                   // Strong wind
            else -> 0                                                       // Too windy
        }

        // me.vosaa.bestbikeday.data.remote.model.Rain factor (0-40 points) - Adjusted for 0-40 scale
        val rainScore = when {
            forecast.pop == 0.0 -> 40       // No chance of rain (perfect)
            forecast.pop < 0.2 -> (40 - forecast.pop * 100).toInt()  // Gradual penalty
            forecast.pop < 0.5 -> 25       // Moderate chance (25/40)
            forecast.pop < 0.8 -> 10        // High chance (10/40)
            else -> 0                       // Heavy rain (0/40)
        }

        // Calculate weighted score
        val weightedTemp = tempScore.toDouble() * 0.4
        val weightedWind = windScore.toDouble() * 0.3
        val weightedRain = rainScore.toDouble() * 0.3

        Log.d("utility", "TempScore: $tempScore")
        Log.d("utility", "windScore: $windScore")
        Log.d("utility", "rainScore: $rainScore")

        val result = (weightedTemp + weightedWind + weightedRain).coerceIn(0.0, 100.0).toInt()

        Log.d("utility", "Final Result: $result")

        // Final score (clamped to 0-100 range)
        return result
    }


} 