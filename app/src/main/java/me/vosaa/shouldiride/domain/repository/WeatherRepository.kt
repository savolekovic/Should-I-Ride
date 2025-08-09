package me.vosaa.shouldiride.domain.repository

import me.vosaa.shouldiride.domain.model.WeatherForecast

/**
 * Contract for fetching weather forecasts for a given coordinate.
 */
interface WeatherRepository {
    /**
     * Returns a pair of (city name, list of normalized forecasts) for the given coordinates.
     */
    suspend fun getWeatherForecast(lat: Double, lon: Double): Pair<String, List<WeatherForecast>>
} 