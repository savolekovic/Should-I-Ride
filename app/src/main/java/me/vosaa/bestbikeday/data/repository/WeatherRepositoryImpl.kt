package me.vosaa.bestbikeday.data.repository

import android.util.Log
import me.vosaa.bestbikeday.data.remote.WeatherApiService
import me.vosaa.bestbikeday.data.remote.model.ForecastItem
import me.vosaa.bestbikeday.domain.model.WeatherForecast
import me.vosaa.bestbikeday.domain.repository.WeatherRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
) : WeatherRepository {
    private val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    private val apiKey = "63816ac463b32cf49aad527b75298a20"

    override suspend fun getWeatherForecast(lat: Double, lon: Double): Pair<String, List<WeatherForecast>> {
        return try {
            val response = apiService.getWeatherForecast(lat, lon, apiKey = apiKey)
            val currentTime = Calendar.getInstance()
            val currentWeek = currentTime.get(Calendar.WEEK_OF_YEAR)

            val list = response.list
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

                    WeatherForecast(
                        date = if (isToday) "Today" else dayFormat.format(Date(forecast.dt * 1000)),
                        temperature = forecast.main.temp.toInt(),
                        conditions = forecast.weather.firstOrNull()?.description ?: "",
                        windSpeed = forecast.wind.speed,
                        rainChance = (forecast.pop * 100).toInt(),
                        bikeScore = calculateRideRating(forecast)
                    )
                }
            Pair(response.city.name, list)
        } catch (e: Exception) {
            throw e
        }
    }

    private fun calculateRideRating(forecast: ForecastItem): Int {
        // Temperature factor (0-25 points)
        val tempScore = when {
            forecast.main.temp < -5 -> 0      
            forecast.main.temp < 0 -> (forecast.main.temp + 5) * 5  
            forecast.main.temp in 0.0..30.0 -> 25                  
            forecast.main.temp <= 35 -> 25 - (forecast.main.temp - 30) * 2  
            else -> 15                      
        }

        // Wind speed factor (0-25 points)
        val windScore = when {
            forecast.wind.speed < 3 -> 25  
            forecast.wind.speed <= 6 -> 25 - (forecast.wind.speed - 3) * 4  
            forecast.wind.speed <= 12 -> 13 - (forecast.wind.speed - 6)  
            forecast.wind.speed <= 15 -> 5                               
            else -> 0                                                   
        }

        // Rain factor (0-50 points)
        val rainScore = when {
            forecast.pop == 0.0 -> 50       
            forecast.pop < 0.15 -> 45       
            forecast.pop < 0.30 -> 35       
            forecast.pop < 0.45 -> 25       
            forecast.pop < 0.60 -> 15       
            forecast.pop < 0.75 -> 5        
            else -> 0                       
        }

        val weightedTemp = tempScore.toDouble() * 0.25   
        val weightedWind = windScore.toDouble() * 0.25   
        val weightedRain = rainScore.toDouble() * 0.50   

        Log.d("utility", "TempScore: $tempScore")
        Log.d("utility", "windScore: $windScore")
        Log.d("utility", "rainScore: $rainScore")

        val result = (weightedTemp + weightedWind + weightedRain).coerceIn(0.0, 100.0).toInt()

        Log.d("utility", "Final Result: $result")

        return result
    }
} 