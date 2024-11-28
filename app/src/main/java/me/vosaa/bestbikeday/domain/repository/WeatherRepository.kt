package me.vosaa.bestbikeday.domain.repository

import me.vosaa.bestbikeday.domain.model.WeatherForecast

interface WeatherRepository {
    suspend fun getWeatherForecast(lat: Double, lon: Double): List<WeatherForecast>
} 