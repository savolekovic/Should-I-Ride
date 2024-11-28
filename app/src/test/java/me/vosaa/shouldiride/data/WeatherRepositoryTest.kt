import me.vosaa.shouldiride.data.remote.WeatherApiService
import me.vosaa.shouldiride.data.remote.model.Clouds
import me.vosaa.shouldiride.data.remote.model.ForecastItem
import me.vosaa.shouldiride.data.remote.model.ForecastSys
import me.vosaa.shouldiride.data.remote.model.MainWeatherData
import me.vosaa.shouldiride.data.remote.model.Weather
import me.vosaa.shouldiride.data.remote.model.Wind
import me.vosaa.shouldiride.data.repository.WeatherRepositoryImpl
import me.vosaa.shouldiride.domain.repository.WeatherRepository
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class WeatherRepositoryTest {
    private lateinit var repository: WeatherRepository
    private lateinit var mockApiService: WeatherApiService

    @Before
    fun setup() {
        mockApiService = mock()
        repository = WeatherRepositoryImpl(mockApiService)
    }

    @Test
    fun whenRainProbabilityIsHigh_conditionsAreCritical() {
        val forecast = ForecastItem(
            dt = 1234567,
            main = MainWeatherData(
                temp = 20.0,
                feels_like = 20.0,
                temp_min = 18.0,
                temp_max = 22.0,
                pressure = 1013,
                humidity = 65,
                sea_level = 1013,
                grnd_level = 1013
            ),
            weather = listOf(
                Weather(
                    id = 500,
                    main = "Rain",
                    description = "light rain",
                    icon = "10d"
                )
            ),
            clouds = Clouds(all = 75),
            wind = Wind(speed = 5.0, deg = 180, gust = 7.0),
            visibility = 10000,
            pop = 0.8,
            sys = ForecastSys(pod = "d"),
            dt_txt = "2024-03-28 12:00:00"
        )

        val result = (repository as WeatherRepositoryImpl).calculateBikeRating(forecast)

        assertTrue(result.hasCriticalConditions)
        assertTrue(result.score <= 30)
    }

    @Test
    fun whenWindSpeedIsDangerous_conditionsAreCritical() {
        val forecast = ForecastItem(
            dt = 1234567,
            main = MainWeatherData(
                temp = 20.0,
                feels_like = 20.0,
                temp_min = 18.0,
                temp_max = 22.0,
                pressure = 1013,
                humidity = 65,
                sea_level = 1013,
                grnd_level = 1013
            ),
            weather = listOf(
                Weather(
                    id = 800,
                    main = "Clear",
                    description = "clear sky",
                    icon = "01d"
                )
            ),
            clouds = Clouds(all = 0),
            wind = Wind(speed = 16.0, deg = 180, gust = 20.0),
            visibility = 10000,
            pop = 0.1,
            sys = ForecastSys(pod = "d"),
            dt_txt = "2024-03-28 12:00:00"
        )

        val result = (repository as WeatherRepositoryImpl).calculateBikeRating(forecast)

        assertTrue(result.hasCriticalConditions)
        assertTrue(result.score <= 30)
    }

    @Test
    fun whenConditionsAreIdeal_scoreIsHigh() {
        val forecast = ForecastItem(
            dt = 1234567,
            main = MainWeatherData(
                temp = 20.0,
                feels_like = 20.0,
                temp_min = 18.0,
                temp_max = 22.0,
                pressure = 1013,
                humidity = 65,
                sea_level = 1013,
                grnd_level = 1013
            ),
            weather = listOf(
                Weather(
                    id = 800,
                    main = "Clear",
                    description = "clear sky",
                    icon = "01d"
                )
            ),
            clouds = Clouds(all = 10),
            wind = Wind(speed = 3.0, deg = 180, gust = 5.0),
            visibility = 10000,
            pop = 0.1,
            sys = ForecastSys(pod = "d"),
            dt_txt = "2024-03-28 12:00:00"
        )

        val result = (repository as WeatherRepositoryImpl).calculateBikeRating(forecast)

        assertFalse(result.hasCriticalConditions)
        assertTrue(result.score >= 70)
    }
} 