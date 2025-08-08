# API and Components Documentation

This document describes the public APIs, functions, and UI components in the project, with examples and usage instructions.

- Kotlin, Jetpack Compose, Hilt (DI), Retrofit, Coroutines/Flow
- Clean Architecture layers: data, domain, presentation, ui

## Table of Contents
- App Setup and Entry Points
- Dependency Injection (DI) Modules
- Data Layer
  - Remote API (`WeatherApiService`)
  - Remote Models
  - Location (`LocationService`)
- Domain Layer
  - Repository Interface (`WeatherRepository`)
  - Domain Models
- Data Repository Implementation (`WeatherRepositoryImpl`)
  - Bike Score Calculation
- Presentation Layer
  - `WeatherViewModel` and `WeatherUiState`
- Composable UI Components
  - `WeatherScreen`
  - `WeatherContent`
  - `WeatherCard`
  - `BikeScoreIndicator`
  - `WeatherInfoItem`
  - `ErrorContent`
  - `LoadingIndicator`
- Theme Utilities
  - `BestBikeDayTheme`
  - Colors and helpers
- Manifest and Permissions
- Examples

---

## App Setup and Entry Points

- `me.vosaa.shouldiride.BikeApplication` (Application): annotated with `@HiltAndroidApp` to enable Hilt.
- `me.vosaa.shouldiride.MainActivity` (Activity): annotated with `@AndroidEntryPoint`. Sets Compose content and renders the weather feature.

Example usage:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      BestBikeDayTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          WeatherScreen(viewModel = hiltViewModel())
        }
      }
    }
  }
}
```

---

## Dependency Injection (DI) Modules

- `me.vosaa.shouldiride.di.NetworkModule`
  - `@Provides fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor`
  - `@Provides fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient`
  - `@Provides fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit`
  - `@Provides fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService`
  - `@Provides fun provideWeatherRepository(apiService: WeatherApiService): WeatherRepository`

- `me.vosaa.shouldiride.di.LocationModule`
  - `@Provides fun provideFusedLocationClient(context: Context): FusedLocationProviderClient`

Injecting in a class:

```kotlin
@HiltViewModel
class WeatherViewModel @Inject constructor(
  private val repository: WeatherRepository,
  private val locationService: LocationService
) : ViewModel() { /* ... */ }
```

---

## Data Layer

### Remote API: `WeatherApiService`
Location: `me.vosaa.shouldiride.data.remote.WeatherApiService`

```kotlin
interface WeatherApiService {
  @GET("forecast")
  suspend fun getWeatherForecast(
    @Query("lat") lat: Double,
    @Query("lon") lon: Double,
    @Query("units") units: String = "metric",
    @Query("appid") apiKey: String
  ): ForecastResponse
}
```

- Returns a 5-day/3-hour forecast (`ForecastResponse`) from OpenWeatherMap. The repository filters it to weekdays at 07:00.

Optional direct usage example (if not using DI):

```kotlin
val retrofit = Retrofit.Builder()
  .baseUrl("https://api.openweathermap.org/data/2.5/")
  .addConverterFactory(GsonConverterFactory.create())
  .build()
val api = retrofit.create(WeatherApiService::class.java)
val result = api.getWeatherForecast(lat = 50.0, lon = 14.4, apiKey = "<API_KEY>")
```

### Remote Models
Location: `me.vosaa.shouldiride.data.remote.model.OpenWeatherResponse.kt`

Key types used by the forecast endpoint:
- `ForecastResponse(cod: String, message: Int, cnt: Int, list: List<ForecastItem>, city: City)`
- `ForecastItem(dt: Long, main: MainWeatherData, weather: List<Weather>, clouds: Clouds, wind: Wind, visibility: Int, pop: Double, sys: ForecastSys, dt_txt: String)`
- `City(id: Long, name: String, coord: Coordinates, country: String, population: Int, timezone: Int, sunrise: Long, sunset: Long)`
- `MainWeatherData(temp: Double, feels_like: Double, temp_min: Double, temp_max: Double, pressure: Int, humidity: Int, sea_level: Int, grnd_level: Int)`
- `Weather(id: Int, main: String, description: String, icon: String)`
- `Wind(speed: Double, deg: Int, gust: Double)`
- `Clouds(all: Int)`, `Rain(1h: Double)`, `ForecastSys(pod: String)`, `Coordinates(lat: Double, lon: Double)`

### Location: `LocationService`
Location: `me.vosaa.shouldiride.data.location.LocationService`

```kotlin
class LocationService @Inject constructor(
  private val locationClient: FusedLocationProviderClient,
  @ApplicationContext private val context: Context
) {
  fun hasLocationPermission(): Boolean
  suspend fun getCurrentLocation(): LocationData?
}

data class LocationData(val latitude: Double, val longitude: Double)
```

Usage:

```kotlin
if (locationService.hasLocationPermission()) {
  val loc = locationService.getCurrentLocation()
  if (loc != null) {
    // use loc.latitude, loc.longitude
  }
}
```

---

## Domain Layer

### Repository Interface: `WeatherRepository`
Location: `me.vosaa.shouldiride.domain.repository.WeatherRepository`

```kotlin
interface WeatherRepository {
  suspend fun getWeatherForecast(lat: Double, lon: Double): Pair<String, List<WeatherForecast>>
}
```
- Returns a pair of `(cityName, forecasts)`.

### Domain Model: `WeatherForecast`
Location: `me.vosaa.shouldiride.domain.model.WeatherForecast`

```kotlin
data class WeatherForecast(
  val date: String,
  val temperature: Int,
  val conditions: String,
  val windSpeed: Double,
  val rainChance: Int,
  val bikeScore: Int,
  val hasCriticalConditions: Boolean
)
```

---

## Data Repository Implementation: `WeatherRepositoryImpl`
Location: `me.vosaa.shouldiride.data.repository.WeatherRepositoryImpl`

Public API (from interface):

```kotlin
suspend fun getWeatherForecast(lat: Double, lon: Double): Pair<String, List<WeatherForecast>>
```

Behavior:
- Calls `WeatherApiService.getWeatherForecast(...)`.
- Filters to weekdays in the current week at 07:00.
- Maps data to `WeatherForecast` and computes a ride score.

Helper:

```kotlin
fun calculateRideRating(forecast: ForecastItem): WeatherScoreResult

data class WeatherScoreResult(val score: Int, val hasCriticalConditions: Boolean)
```

### Bike Score Calculation
- Rain probability (0–40 points)
- Wind speed (0–35 points)
- Temperature (0–25 points)
- Critical conditions (heavy rain >70%, wind >15 m/s, temp < -5°C or > 40°C) cap score to ≤ 30 and mark `hasCriticalConditions=true`.

---

## Presentation Layer

### `WeatherViewModel`
Location: `me.vosaa.shouldiride.presentation.weather.WeatherViewModel`

```kotlin
@HiltViewModel
class WeatherViewModel @Inject constructor(
  private val repository: WeatherRepository,
  private val locationService: LocationService
) : ViewModel() {
  val uiState: StateFlow<WeatherUiState>
  fun refreshWeatherData()
}

data class WeatherUiState(
  val isLoading: Boolean = true,
  val forecasts: List<WeatherForecast> = emptyList(),
  val error: String? = null,
  val location: String = ""
)
```

Usage in Compose:

```kotlin
@Composable
fun Screen() {
  val vm: WeatherViewModel = hiltViewModel()
  val state by vm.uiState.collectAsStateWithLifecycle()
  // use state
}
```

---

## Composable UI Components

All components are in `me.vosaa.shouldiride.presentation.weather` or its `components` package.

### `WeatherScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier)`
- Requests location permissions on first composition.
- Renders loading, error, or `WeatherContent` based on `uiState`.

### `WeatherContent(forecasts: List<WeatherForecast>, location: String, modifier: Modifier = Modifier)`
- Displays a header and a list of `WeatherCard` items.

### `WeatherCard(forecast: WeatherForecast, modifier: Modifier = Modifier)`
- Shows date, time (07:00), condition description, critical warnings, bike score, and key metrics.

### `BikeScoreIndicator(score: Int, hasCriticalConditions: Boolean)`
- Circular badge colored by `getBikeScoreColor(score, hasCriticalConditions)` and an optional "Not Safe to Ride" label.

### `WeatherInfoItem(painter: Painter, value: String, label: String)`
- Small icon+text pair for a single metric.

### `ErrorContent(error: String)`
- Centered card with an error icon and message.

### `LoadingIndicator()`
- Centered progress indicator.

Basic example with mock data:

```kotlin
val mock = listOf(
  WeatherForecast(
    date = "Today", temperature = 18, conditions = "clear sky",
    windSpeed = 3.2, rainChance = 0, bikeScore = 85, hasCriticalConditions = false
  )
)
WeatherContent(forecasts = mock, location = "Prague, CZ")
```

---

## Theme Utilities

Location: `me.vosaa.shouldiride.ui.theme`

- `BestBikeDayTheme(darkTheme: Boolean = isSystemInDarkTheme(), dynamicColor: Boolean = true, content: @Composable () -> Unit)`
- Colors: `Primary`, `Surface`, `BadWeatherColor`, `PoorWeatherColor`, `GoodWeatherColor`
- Helper: `fun getBikeScoreColor(score: Int, hasCriticalConditions: Boolean): Color`

Usage:

```kotlin
BestBikeDayTheme {
  // app UI
}
```

---

## API Key Configuration

The OpenWeatherMap API key is currently defined inside `me.vosaa.shouldiride.data.repository.WeatherRepositoryImpl` as a private constant. Replace it with your own key or refactor to use `BuildConfig` or a secure provider.

Example using `BuildConfig` (preferred):

```kotlin
class WeatherRepositoryImpl @Inject constructor(
  private val apiService: WeatherApiService,
) : WeatherRepository {
  private val apiKey = BuildConfig.OPEN_WEATHERMAP_API_KEY
  // ...
}
```

Add the key to your `local.properties` and wire it via `build.gradle` fields as needed.

---

## Manifest and Permissions

`app/src/main/AndroidManifest.xml` declares:
- `android.permission.INTERNET`
- `android.permission.ACCESS_COARSE_LOCATION`
- `android.permission.ACCESS_FINE_LOCATION`

`WeatherScreen` requests location permissions at runtime using `rememberLauncherForActivityResult` and triggers data refresh when granted.

---

## Examples

### Fetching forecasts via repository

```kotlin
viewModelScope.launch {
  val (city, forecasts) = repository.getWeatherForecast(lat = 50.08, lon = 14.43)
  // use city and forecasts
}
```

### Manual permission + location fetch with `LocationService`

```kotlin
if (locationService.hasLocationPermission()) {
  val location = locationService.getCurrentLocation()
  if (location != null) {
    // call repository with location.latitude and location.longitude
  } else {
    // handle missing location
  }
} else {
  // ask for permissions
}
```

### End-to-end Compose usage (minimal)

```kotlin
@Composable
fun App() {
  BestBikeDayTheme {
    val vm: WeatherViewModel = hiltViewModel()
    WeatherScreen(viewModel = vm)
  }
}
```