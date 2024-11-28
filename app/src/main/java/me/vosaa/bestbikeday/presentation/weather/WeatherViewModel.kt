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


@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        fetchWeatherData()
    }


    private fun fetchWeatherData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Using Podgorica coordinates
                val forecasts = repository.getWeatherForecast(
                    lat = 42.4518639,
                    lon = 19.2700494
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        forecasts = forecasts,
                        location = "Podgorica"
                    )
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
}

data class WeatherUiState(
    val isLoading: Boolean = false,
    val forecasts: List<WeatherForecast> = emptyList(),
    val error: String? = null,
    val location: String = ""
)
