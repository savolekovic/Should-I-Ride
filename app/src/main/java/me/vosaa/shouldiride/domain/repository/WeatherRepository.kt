package me.vosaa.shouldiride.domain.repository

import me.vosaa.shouldiride.domain.model.RidePeriod
import me.vosaa.shouldiride.domain.model.WeatherForecast

/**
 * Contract for fetching weather forecasts for a given coordinate.
 */
interface WeatherRepository {
    /**
     * Returns a pair of (city name, list of normalized forecasts) for the given coordinates.
     *
     * This method returns only Morning forecasts for backward-compatibility.
     */
    suspend fun getWeatherForecast(lat: Double, lon: Double): Pair<String, List<WeatherForecast>>

    /**
     * Returns forecasts grouped by [RidePeriod] for the given coordinates.
     *
     * @param lat Latitude in decimal degrees
     * @param lon Longitude in decimal degrees
     * @param periods The ride periods to include; defaults to all supported
     * @return Pair of city name and a Map keyed by [RidePeriod]
     */
    suspend fun getWeatherForecastsByPeriod(
        lat: Double,
        lon: Double,
        periods: List<RidePeriod> = RidePeriod.defaultOrder()
    ): Pair<String, Map<RidePeriod, List<WeatherForecast>>>
} 