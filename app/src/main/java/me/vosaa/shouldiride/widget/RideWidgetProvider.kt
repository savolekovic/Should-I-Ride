package me.vosaa.shouldiride.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.update
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.state.currentState
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.unit.dp
import me.vosaa.shouldiride.domain.model.RidePeriod
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Glance AppWidget showing the next ride period score and key weather data.
 */
class RideWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Responsive(setOf())
    override val stateDefinition = PreferencesGlanceStateDefinition

    @Composable
    override fun Content() {
        val prefs = currentState<Preferences>()
        val state = WidgetState.fromPreferences(prefs)

        Column(modifier = GlanceModifier.fillMaxSize().padding(12.dp)) {
            Text(
                text = state.title,
                style = TextStyle(color = ColorProvider(android.graphics.Color.WHITE))
            )
            Row(modifier = GlanceModifier.padding(top = 8.dp)) {
                Text(text = state.subtitle)
            }
            Row(modifier = GlanceModifier.padding(top = 8.dp)) {
                Text(text = "Score: ${state.score}")
            }
            Row(modifier = GlanceModifier.padding(top = 4.dp)) {
                Text(text = "Temp: ${state.temperature}°C  Wind: ${state.windSpeed} m/s  Rain: ${state.rainChance}%")
            }
        }
    }
}

/** Receiver for the Ride widget. */
class RideWidgetProvider : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RideWidget()
}

/** Preference keys used to persist widget state. */
private object WidgetPrefs {
    val title = stringPreferencesKey("title")
    val subtitle = stringPreferencesKey("subtitle")
    val score = intPreferencesKey("score")
    val wind = doublePreferencesKey("wind")
    val temp = intPreferencesKey("temp")
    val rain = intPreferencesKey("rain")
    val period = stringPreferencesKey("period")
    val timestamp = longPreferencesKey("ts")
}

private data class WidgetState(
    val title: String,
    val subtitle: String,
    val score: Int,
    val temperature: Int,
    val windSpeed: Double,
    val rainChance: Int
) {
    companion object {
        fun fromPreferences(prefs: Preferences): WidgetState {
            return WidgetState(
                title = prefs[WidgetPrefs.title] ?: "Should I Ride",
                subtitle = prefs[WidgetPrefs.subtitle] ?: "Loading",
                score = prefs[WidgetPrefs.score] ?: 0,
                temperature = prefs[WidgetPrefs.temp] ?: 0,
                windSpeed = prefs[WidgetPrefs.wind] ?: 0.0,
                rainChance = prefs[WidgetPrefs.rain] ?: 0
            )
        }
    }
}

/**
 * Bridge called by the ViewModel to push latest period forecast into the widget state.
 */
object WidgetUpdater {
    suspend fun updateWithForecasts(
        byPeriod: Map<RidePeriod, List<me.vosaa.shouldiride.domain.model.WeatherForecast>>,
        city: String,
        context: Context
    ) {
        val now = System.currentTimeMillis()
        val next = byPeriod[RidePeriod.nextPeriodFor(java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY))]
            ?.firstOrNull()
            ?: byPeriod.values.flatten().minByOrNull { (it.timestamp - now).let { d -> if (d < 0) Long.MAX_VALUE else d } }

        if (next != null) {
            val df = SimpleDateFormat("EEE HH:mm", Locale.getDefault())
            val subtitle = "${city} • ${next.period.displayName} ${next.period.displayTime} (${df.format(Date(next.timestamp))})"

            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(RideWidget::class.java)
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[WidgetPrefs.title] = "Should I Ride"
                    prefs[WidgetPrefs.subtitle] = subtitle
                    prefs[WidgetPrefs.score] = next.bikeScore
                    prefs[WidgetPrefs.wind] = next.windSpeed
                    prefs[WidgetPrefs.temp] = next.temperature
                    prefs[WidgetPrefs.rain] = next.rainChance
                    prefs[WidgetPrefs.period] = next.period.name
                    prefs[WidgetPrefs.timestamp] = next.timestamp
                }
                RideWidget().update(context, glanceId)
            }
        }
    }
}