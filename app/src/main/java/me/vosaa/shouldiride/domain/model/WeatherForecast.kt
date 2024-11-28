package me.vosaa.shouldiride.domain.model

data class WeatherForecast(
    val date: String,
    val temperature: Int,
    val conditions: String,
    val windSpeed: Double,
    val rainChance: Int,
    val bikeScore: Int,
    val hasCriticalConditions: Boolean
) 