package me.vosaa.shouldiride.presentation.weather

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.vosaa.shouldiride.data.location.LocationService
import me.vosaa.shouldiride.domain.model.RidePeriod
import me.vosaa.shouldiride.domain.model.WeatherForecast
import me.vosaa.shouldiride.domain.repository.WeatherRepository
import me.vosaa.shouldiride.widget.WidgetUpdater
import javax.inject.Inject

/**
 * ViewModel that coordinates location permission/state and weather retrieval,
 * exposing a single immutable UI state to the Compose layer.
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationService: LocationService,
    @ApplicationContext private val appContext: Context
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
                        val (cityName, byPeriod) = repository.getWeatherForecastsByPeriod(
                            lat = locationData.latitude,
                            lon = locationData.longitude,
                            periods = RidePeriod.defaultOrder()
                        )
                        val hourNow = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                        val suggested = RidePeriod.nextPeriodFor(hourNow)
                        val selected = if (byPeriod.containsKey(suggested)) suggested
                        else RidePeriod.defaultOrder().firstOrNull { byPeriod.containsKey(it) } ?: RidePeriod.MORNING
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                forecastsByPeriod = byPeriod,
                                selectedPeriod = selected,
                                forecasts = byPeriod[selected].orEmpty(),
                                location = cityName,
                                error = null
                            )
                        }
                        // Update widget with the latest data
                        WidgetUpdater.updateWithForecasts(byPeriod, cityName, appContext)
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

    /** Updates selected [RidePeriod] and derives [WeatherUiState.forecasts]. */
    fun selectPeriod(period: RidePeriod) {
        val byPeriod = _uiState.value.forecastsByPeriod
        _uiState.update { it.copy(selectedPeriod = period, forecasts = byPeriod[period].orEmpty()) }
    }
}

/**
 * Immutable UI model rendered by the weather screen.
 *
 * @property isLoading Whether data is currently loading
 * @property forecasts List of transformed forecasts ready for display for the selected period
 * @property error Optional error message for user feedback
 * @property location Human-readable location/city name
 * @property forecastsByPeriod Map of forecasts grouped by ride period
 * @property selectedPeriod Currently selected ride period
 */
data class WeatherUiState(
    val isLoading: Boolean = true,
    val forecasts: List<WeatherForecast> = emptyList(),
    val error: String? = null,
    val location: String = "",
    val forecastsByPeriod: Map<RidePeriod, List<WeatherForecast>> = emptyMap(),
    val selectedPeriod: RidePeriod = RidePeriod.MORNING
)
