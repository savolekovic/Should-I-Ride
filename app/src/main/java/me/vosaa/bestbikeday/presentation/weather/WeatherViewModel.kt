package me.vosaa.bestbikeday.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.vosaa.bestbikeday.domain.repository.WeatherRepository
import me.vosaa.bestbikeday.domain.model.WeatherForecast
import javax.inject.Inject
import me.vosaa.bestbikeday.data.location.LocationService


@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationService: LocationService
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        checkLocationAndFetchData()
    }

    private fun checkLocationAndFetchData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                if (locationService.hasLocationPermission()) {
                    val location = locationService.getCurrentLocation()
                    if (location != null) {
                        val forecasts = repository.getWeatherForecast(
                            lat = location.latitude,
                            lon = location.longitude
                        )
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                forecasts = forecasts,
                                location = "Current Location"
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

    fun refreshWeatherData() {
        checkLocationAndFetchData()
    }
}

data class WeatherUiState(
    val isLoading: Boolean = false,
    val forecasts: List<WeatherForecast> = emptyList(),
    val error: String? = null,
    val location: String = ""
)
