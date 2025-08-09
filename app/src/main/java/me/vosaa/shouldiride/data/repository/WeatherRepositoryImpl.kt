package me.vosaa.shouldiride.data.repository

import me.vosaa.shouldiride.data.remote.WeatherApiService
import me.vosaa.shouldiride.data.remote.model.ForecastItem
import me.vosaa.shouldiride.domain.model.WeatherForecast
import me.vosaa.shouldiride.domain.repository.WeatherRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Result of the bike-ride scoring algorithm containing a final score and a
 * flag indicating if any critical (unsafe) condition is present.
 */
data class WeatherScoreResult(
    val score: Int,
    val hasCriticalConditions: Boolean
)

/**
 * Concrete [WeatherRepository] that talks to OpenWeather and transforms raw
 * forecast data into a domain-friendly model for the UI.
 */
class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
) : WeatherRepository {
    /** Day-of-week formatter used for card headers. */
    private val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    /**
     * OpenWeather API key.
     * TODO: Clarify functionality and move to a secure source (BuildConfig, local.properties,
     * or an injected config provider). Hardcoding keys is unsafe.
     */
    private val apiKey = "63816ac463b32cf49aad527b75298a20"

    /**
     * Returns a pair of (cityName, weekdayMorningForecasts) for the current week.
     *
     * The list is filtered to include only workdays (Monday–Friday) at 07:00 and
     * only for the current week, then mapped into domain [WeatherForecast] items.
     */
    override suspend fun getWeatherForecast(lat: Double, lon: Double): Pair<String, List<WeatherForecast>> {
        return try {
            val response = apiService.getWeatherForecast(lat, lon, apiKey = apiKey)
            val currentTime = Calendar.getInstance()
            val currentWeek = currentTime.get(Calendar.WEEK_OF_YEAR)

            val list = response.list
                // Keep only forecast entries at 07:00 local time for workdays in the current week
                .filter { forecast ->
                    val forecastTime = Calendar.getInstance().apply {
                        timeInMillis = forecast.dt * 1000
                    }

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

                    val weatherScore = calculateRideRating(forecast)
                    
                    WeatherForecast(
                        date = if (isToday) "Today" else dayFormat.format(Date(forecast.dt * 1000)),
                        temperature = forecast.main.temp.toInt(),
                        conditions = forecast.weather.firstOrNull()?.description ?: "",
                        windSpeed = forecast.wind.speed,
                        rainChance = (forecast.pop * 100).toInt(),
                        bikeScore = weatherScore.score,
                        hasCriticalConditions = weatherScore.hasCriticalConditions
                    )
                }
            Pair(response.city.name, list)
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Computes a rideability score (0–100) for a single forecast item and flags
     * critical conditions (rain probability very high, dangerous wind, or extreme temperatures).
     */
    fun calculateRideRating(forecast: ForecastItem): WeatherScoreResult {
        // Rain probability scoring (0-40 points)
        val rainScore = when (forecast.pop) {
            in 0.0..0.15 -> 40
            in 0.15..0.30 -> 30
            in 0.30..0.45 -> 20
            in 0.45..0.60 -> 10
            else -> 0             
        }

        // Wind speed scoring (0-35 points)
        val windScore = when (forecast.wind.speed) {
            in 0.0..3.0 -> 35
            in 3.0..6.0 -> 30
            in 6.0..9.0 -> 20
            in 9.0..12.0 -> 10
            in 12.0..15.0 -> 5
            else -> 0             
        }

        // Temperature scoring (0-25 points)
        val tempScore = when (forecast.main.temp) {
            in Double.NEGATIVE_INFINITY..0.0 -> 0
            in 0.0..5.0 -> 10
            in 5.0..10.0 -> 15
            in 10.0..25.0 -> 25
            in 25.0..30.0 -> 20
            in 30.0..35.0 -> 10
            else -> 0
        }

        // Check for critical conditions
        val hasCriticalConditions = when {
            forecast.pop > 0.7 -> true            // Heavy rain probability (>70% chance of rain)
            forecast.wind.speed > 15.0 -> true    // Dangerous wind conditions (>15 m/s or ~54 km/h)
            forecast.main.temp < -5.0 -> true     // Too cold (<-5°C)
            forecast.main.temp > 40.0 -> true     // Too hot (>40°C)
            else -> false
        }

        val finalScore = (rainScore + windScore + tempScore).coerceIn(0, 100)

        return WeatherScoreResult(
            score = if (hasCriticalConditions) finalScore.coerceAtMost(30) else finalScore,
            hasCriticalConditions = hasCriticalConditions
        )
    }
} 