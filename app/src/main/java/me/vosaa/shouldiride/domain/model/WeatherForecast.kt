package me.vosaa.shouldiride.domain.model

/**
 * Domain model used by the UI to represent a normalized forecast item.
 *
 * @property date Day label such as "Today" or localized weekday name
 * @property temperature Temperature in Celsius, rounded to integer for display
 * @property conditions Human-readable weather description
 * @property windSpeed Wind speed in m/s
 * @property rainChance Probability of precipitation as percentage [0,100]
 * @property bikeScore Computed rideability score [0,100]
 * @property hasCriticalConditions Whether conditions are unsafe to ride
 */
data class WeatherForecast(
    val date: String,
    val temperature: Int,
    val conditions: String,
    val windSpeed: Double,
    val rainChance: Int,
    val bikeScore: Int,
    val hasCriticalConditions: Boolean
) 