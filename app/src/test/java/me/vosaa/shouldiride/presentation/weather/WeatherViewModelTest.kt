import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import me.vosaa.shouldiride.data.location.LocationData
import me.vosaa.shouldiride.data.location.LocationService
import me.vosaa.shouldiride.domain.model.WeatherForecast
import me.vosaa.shouldiride.domain.repository.WeatherRepository
import me.vosaa.shouldiride.presentation.weather.WeatherViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class WeatherViewModelTest {
    private lateinit var viewModel: WeatherViewModel
    private lateinit var mockRepository: WeatherRepository
    private lateinit var mockLocationService: LocationService

    @Before
    fun setup() {
        mockRepository = mock()
        mockLocationService = mock()
    }

    @Test
    fun whenAppStarts_loadingStateIsShown() = runTest {
        // Given: Location permission is granted and location is available
        whenever(mockLocationService.hasLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getCurrentLocation()).thenReturn(
            LocationData(latitude = 42.4518639, longitude = 19.2700494)
        )

        viewModel = WeatherViewModel(mockRepository, mockLocationService)

        val initialState = viewModel.uiState.value

        assertTrue(initialState.isLoading)
        assertTrue(initialState.forecasts.isEmpty())
    }

    @Test
    fun whenApiCallFails_errorStateIsShown() = runTest {
        // Given: Location permission is granted and location is available
        whenever(mockLocationService.hasLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getCurrentLocation()).thenReturn(
            LocationData(latitude = 42.4518639, longitude = 19.2700494)
        )

        whenever(mockRepository.getWeatherForecast(any(), any())).thenThrow(
            RuntimeException("Network error")
        )

        viewModel = WeatherViewModel(mockRepository, mockLocationService)

        val finalState = viewModel.uiState.value
        assertEquals("Network error", finalState.error)
        assertFalse(finalState.isLoading)
    }

    @Test
    fun whenLocationPermissionDenied_showsError() = runTest {
        // Given: Location permission is denied
        whenever(mockLocationService.hasLocationPermission()).thenReturn(false)

        viewModel = WeatherViewModel(mockRepository, mockLocationService)

        val finalState = viewModel.uiState.value
        assertEquals("Location permission required", finalState.error)
        assertFalse(finalState.isLoading)
    }

    @Test
    fun whenLocationUnavailable_showsError() = runTest {
        // Given: Location permission granted but location unavailable
        whenever(mockLocationService.hasLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getCurrentLocation()).thenReturn(null)

        viewModel = WeatherViewModel(mockRepository, mockLocationService)

        val finalState = viewModel.uiState.value
        assertEquals("Unable to get location", finalState.error)
        assertFalse(finalState.isLoading)
    }

    @Test
    fun whenApiCallSucceeds_forecastsAreShown() = runTest {
        // Given: Location permission is granted and location is available
        whenever(mockLocationService.hasLocationPermission()).thenReturn(true)
        whenever(mockLocationService.getCurrentLocation()).thenReturn(
            LocationData(latitude = 42.4518639, longitude = 19.2700494)
        )

        val cityName = "Test City"
        val mockForecasts = listOf(
            WeatherForecast(
                date = "Today",
                temperature = 20,
                conditions = "Clear",
                windSpeed = 5.0,
                rainChance = 10,
                bikeScore = 85,
                hasCriticalConditions = false
            )
        )

        whenever(mockRepository.getWeatherForecast(any(), any())).thenReturn(
            Pair(
                cityName,
                mockForecasts
            )
        )

        viewModel = WeatherViewModel(mockRepository, mockLocationService)

        val finalState = viewModel.uiState.value
        assertFalse(finalState.isLoading)
        assertNull(finalState.error)
        assertEquals(mockForecasts, finalState.forecasts)
        assertEquals(cityName, finalState.location)
    }
} 