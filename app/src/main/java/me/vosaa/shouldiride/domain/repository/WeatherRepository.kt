package me.vosaa.shouldiride.domain.repository

import me.vosaa.shouldiride.domain.model.WeatherForecast

interface WeatherRepository {
    suspend fun getWeatherForecast(lat: Double, lon: Double): Pair<String, List<WeatherForecast>>
} 