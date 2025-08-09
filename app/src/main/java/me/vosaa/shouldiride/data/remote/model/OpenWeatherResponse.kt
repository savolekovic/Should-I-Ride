package me.vosaa.shouldiride.data.remote.model

/**
 * OpenWeather "current weather" response subset. Fields mirror the upstream API
 * schema. See OpenWeather docs for field semantics.
 */
data class OpenWeatherResponse(
    val coord: Coordinates,
    val weather: List<Weather>,
    val main: MainWeatherData,
    val visibility: Int,
    val wind: Wind,
    val rain: Rain?,
    val clouds: Clouds,
    val dt: Long,
    val sys: SystemInfo,
    val timezone: Int,
    val id: Long,
    val name: String,
    val cod: Int
)

/** Coordinates in decimal degrees. */
data class Coordinates(
    val lon: Double,
    val lat: Double
)

/** High-level and detailed weather condition. */
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

/**
 * Temperature and pressure readings. All temperatures are in the unit set by
 * query param (we request metric).
 */
data class MainWeatherData(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int,
    val grnd_level: Int
)

/** Wind speed and direction. */
data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

/** Rain volume for the last hour. */
data class Rain(
    val `1h`: Double
)

/** Cloudiness percentage. */
data class Clouds(
    val all: Int
)

/** System metadata such as country and sun times. */
data class SystemInfo(
    val type: Int,
    val id: Long,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

/**
 * OpenWeather 5-day/3-hour forecast response. The app filters this list to
 * workdays at 07:00 local time.
 */
data class ForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>,
    val city: City
)

/** A single 3-hour forecast item. */
data class ForecastItem(
    val dt: Long,
    val main: MainWeatherData,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val sys: ForecastSys,
    val dt_txt: String
)

/** Additional main metrics for forecast payloads. */
data class MainWeather(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double
)

/** Forecast part-of-day (day/night). */
data class ForecastSys(
    val pod: String
)

/** City metadata for the forecast. */
data class City(
    val id: Long,
    val name: String,
    val coord: Coordinates,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)