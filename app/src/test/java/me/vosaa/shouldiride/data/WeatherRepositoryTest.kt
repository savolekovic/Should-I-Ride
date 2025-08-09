package me.vosaa.shouldiride.data

import kotlinx.coroutines.runBlocking
import me.vosaa.shouldiride.data.remote.WeatherApiService
import me.vosaa.shouldiride.data.remote.model.*
import me.vosaa.shouldiride.data.repository.WeatherRepositoryImpl
import me.vosaa.shouldiride.domain.model.RidePeriod
import me.vosaa.shouldiride.domain.repository.WeatherRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.Calendar

class WeatherRepositoryPeriodTest {
    private lateinit var repository: WeatherRepository
    private lateinit var mockApiService: WeatherApiService

    @Before
    fun setup() {
        mockApiService = mock()
        repository = WeatherRepositoryImpl(mockApiService)
    }

    @Test
    fun getWeatherForecastsByPeriod_groupsByHours() = runBlocking {
        val now = Calendar.getInstance()
        val week = now.get(Calendar.WEEK_OF_YEAR)
        val baseDay = now.get(Calendar.DAY_OF_YEAR)

        fun itemAtHour(hour: Int, dayOffset: Int = 0): ForecastItem {
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                set(Calendar.DAY_OF_YEAR, baseDay + dayOffset)
                set(Calendar.WEEK_OF_YEAR, week)
            }
            return ForecastItem(
                dt = cal.timeInMillis / 1000,
                main = MainWeatherData(20.0, 20.0, 18.0, 22.0, 1013, 50, 1013, 1013),
                weather = listOf(Weather(800, "Clear", "clear sky", "01d")),
                clouds = Clouds(all = 0),
                wind = Wind(speed = 3.0, deg = 180, gust = 5.0),
                visibility = 10000,
                pop = 0.1,
                sys = ForecastSys(pod = "d"),
                dt_txt = ""
            )
        }

        val response = ForecastResponse(
            cod = "200",
            message = 0,
            cnt = 3,
            list = listOf(
                itemAtHour(7),
                itemAtHour(12),
                itemAtHour(18)
            ),
            city = City(0, "Test City", Coordinates(0.0, 0.0), "TC", 0, 0, 0, 0)
        )

        mockApiService = mock {
            onBlocking { getWeatherForecast(lat = 0.0, lon = 0.0, apiKey = org.mockito.kotlin.any()) } doReturn response
        }
        repository = WeatherRepositoryImpl(mockApiService)

        val (_, map) = repository.getWeatherForecastsByPeriod(0.0, 0.0, RidePeriod.defaultOrder())
        assertEquals(1, map[RidePeriod.MORNING]?.size)
        assertEquals(1, map[RidePeriod.MIDDAY]?.size)
        assertEquals(1, map[RidePeriod.EVENING]?.size)
    }
}