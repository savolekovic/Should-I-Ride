package me.vosaa.shouldiride

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import me.vosaa.shouldiride.presentation.weather.WeatherScreen
import me.vosaa.shouldiride.ui.theme.BestBikeDayTheme

/**
 * Host activity that sets up the Compose content and injects the app's
 * [WeatherViewModel] via Hilt for the single-screen experience.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Initializes edge-to-edge UI and renders the [WeatherScreen].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BestBikeDayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherScreen(
                        viewModel = hiltViewModel()
                    )
                }
            }
        }
    }
}