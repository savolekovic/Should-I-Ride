package me.vosaa.shouldiride

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import me.vosaa.shouldiride.di.NetworkModule
import me.vosaa.shouldiride.domain.repository.WeatherRepository
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(NetworkModule::class)
class WeatherIntegrationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: WeatherRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun verifyCompleteWeatherForecastFlow() = runTest {
        // When: We request weather forecast
        val (cityName, forecasts) = repository.getWeatherForecast(
            lat = 42.4518639,
            lon = 19.2700494
        )
        
        // Then: We should get valid data
        assertNotNull(cityName)
        assertNotNull(forecasts)
        assertTrue(forecasts.isNotEmpty())
        
        // And: All forecasts should be for workdays
        forecasts.forEach { forecast ->
            // Verify forecast is for workdays
            assertTrue("Forecast should be for workdays",
                forecast.date == "Today" || 
                forecast.date in listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
            )
            
            // Verify bike score is calculated
            assertTrue("Bike score should be between 0 and 100",
                forecast.bikeScore in 0..100
            )
            
            // Verify critical conditions flag is set correctly
            if (forecast.rainChance > 70 || forecast.windSpeed > 15.0) {
                assertTrue("Should have critical conditions flag",
                    forecast.hasCriticalConditions
                )
            }
        }
    }
} 