package me.vosaa.shouldiride.presentation.weather.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.vosaa.shouldiride.domain.model.RidePeriod
import me.vosaa.shouldiride.domain.model.WeatherForecast

/**
 * Renders the screen title, location subtitle, a period selector, and a lazy list of
 * [WeatherCard]s representing each forecast for the selected period.
 */
@Composable
fun WeatherContent(
    forecasts: List<WeatherForecast>,
    location: String,
    periods: List<RidePeriod> = RidePeriod.defaultOrder(),
    selected: RidePeriod = RidePeriod.MORNING,
    onSelectPeriod: (RidePeriod) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Should I Ride",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = location,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
        }

        // Period tabs
        val availablePeriods = if (periods.isEmpty()) RidePeriod.defaultOrder() else periods
        val selectedIndex = availablePeriods.indexOfFirst { it == selected }.coerceAtLeast(0)
        TabRow(selectedTabIndex = selectedIndex) {
            availablePeriods.forEachIndexed { index, period ->
                Tab(
                    selected = index == selectedIndex,
                    onClick = { onSelectPeriod(period) },
                    text = { Text(text = "${period.displayName} • ${period.displayTime}") }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(forecasts) { forecast ->
                WeatherCard(forecast = forecast)
            }
        }
    }
}