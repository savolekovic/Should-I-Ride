package me.vosaa.shouldiride.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.vosaa.shouldiride.data.location.LocationService
import me.vosaa.shouldiride.domain.model.WeatherForecast
import me.vosaa.shouldiride.domain.repository.WeatherRepository
import javax.inject.Inject

/**
 * ViewModel that coordinates location permission/state and weather retrieval,
 * exposing a single immutable UI state to the Compose layer.
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationService: LocationService
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUiState())

    /**
     * Collectable UI state. Starts by checking location permissions and fetching
     * data once subscribed.
     */
    val uiState = _uiState
        .onStart { checkLocationAndFetchData() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            WeatherUiState()
        )

    /**
     * Checks permission and device location state, then loads forecasts from the repository.
     * Updates [_uiState] with loading, error, and success states accordingly.
     */
    private fun checkLocationAndFetchData() {
        viewModelScope.launch {
            try {
                if (locationService.hasLocationPermission()) {
                    val locationData = locationService.getCurrentLocation()
                    if (locationData != null) {
                        val (cityName, forecasts) = repository.getWeatherForecast(
                            lat = locationData.latitude,
                            lon = locationData.longitude
                        )
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                forecasts = forecasts,
                                location = cityName
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Unable to get location"
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Location permission required"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    /** Requests a refresh of weather data using the current location. */
    fun refreshWeatherData() {
        checkLocationAndFetchData()
    }
}

/**
 * Immutable UI model rendered by the weather screen.
 *
 * @property isLoading Whether data is currently loading
 * @property forecasts List of transformed forecasts ready for display
 * @property error Optional error message for user feedback
 * @property location Human-readable location/city name
 */
data class WeatherUiState(
    val isLoading: Boolean = true,
    val forecasts: List<WeatherForecast> = emptyList(),
    val error: String? = null,
    val location: String = ""
)
