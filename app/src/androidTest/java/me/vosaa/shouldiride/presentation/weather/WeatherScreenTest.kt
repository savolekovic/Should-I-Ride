import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import me.vosaa.shouldiride.domain.model.WeatherForecast
import me.vosaa.shouldiride.presentation.weather.WeatherCard
import org.junit.Rule
import org.junit.Test

class WeatherScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenWeatherIsDangerous_warningIsShown() {
        val dangerousForecast = WeatherForecast(
            date = "Today",
            temperature = 20,
            conditions = "Heavy Rain",
            windSpeed = 16.0,
            rainChance = 80,
            bikeScore = 25,
            hasCriticalConditions = true
        )

        composeTestRule.setContent {
            WeatherCard(forecast = dangerousForecast)
        }

        // First verify "Not Safe to Ride" is shown
        composeTestRule.onNodeWithText("Not Safe to Ride").assertExists()
            .assertIsDisplayed()

        // Then verify the specific warning based on conditions
        // Check for either rain or wind warning since both conditions are critical
        composeTestRule.onNodeWithText("Heavy Rain Expected")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun whenWeatherIsGood_noWarningIsShown() {
        val goodForecast = WeatherForecast(
            date = "Today",
            temperature = 20,
            conditions = "Clear",
            windSpeed = 5.0,
            rainChance = 10,
            bikeScore = 85,
            hasCriticalConditions = false
        )

        composeTestRule.setContent {
            WeatherCard(forecast = goodForecast)
        }

        // Verify no warning is shown
        composeTestRule.onNodeWithText("Not Safe to Ride").assertDoesNotExist()
    }
} 