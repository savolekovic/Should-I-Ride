package me.vosaa.bestbikeday.data.repository

import me.vosaa.bestbikeday.data.remote.WeatherApiService
import me.vosaa.bestbikeday.domain.model.WeatherForecast
import me.vosaa.bestbikeday.domain.repository.WeatherRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
)  {
    // ... existing implementation
} 