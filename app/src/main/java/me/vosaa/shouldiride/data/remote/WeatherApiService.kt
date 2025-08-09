package me.vosaa.shouldiride.data.remote

import me.vosaa.shouldiride.data.remote.model.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service for OpenWeather endpoints used by the app.
 */
interface WeatherApiService {
    /**
     * Fetches a 5-day/3-hour forecast for a location.
     *
     * @param lat Latitude in decimal degrees
     * @param lon Longitude in decimal degrees
     * @param units Temperature unit system (metric by default)
     * @param apiKey OpenWeather API key
     */
    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): ForecastResponse
} 