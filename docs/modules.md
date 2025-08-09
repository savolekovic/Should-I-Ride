# Modules & Responsibilities

## Domain
- `domain/model/WeatherForecast.kt`: UI-facing forecast model (includes `period`, `timestamp`)
- `domain/model/RidePeriod.kt`: Enum for Morning/Midday/Evening, with helper utilities
- `domain/repository/WeatherRepository.kt`: Contract to fetch forecasts; includes `getWeatherForecastsByPeriod`

## Data
- `data/remote/WeatherApiService.kt`: Retrofit API definition (`GET forecast`)
- `data/remote/model/*`: DTOs matching OpenWeather responses
- `data/repository/WeatherRepositoryImpl.kt`: Implements the domain repository; filters and maps data; computes Ride Score; groups by `RidePeriod`
- `data/location/LocationService.kt`: Wraps fused location client; checks permission/provider and returns last known location

## Presentation
- `presentation/weather/WeatherViewModel.kt`: Coordinates location + data fetch, exposes `WeatherUiState` with period selection
- `presentation/weather/WeatherScreen.kt`: Requests permissions; renders loading/error/content
- `presentation/weather/components/*`: Small, focused UI components composing the screen (adds period `TabRow`)

## Widget
- `widget/RideWidgetProvider.kt`: Glance `RideWidget` + `RideWidgetProvider`
- `widget/WidgetUpdater`: Bridge called from `WeatherViewModel` to persist latest forecast to DataStore and trigger widget update
- `res/xml/ride_widget_info.xml`: Widget provider metadata

## Dependency Injection
- `di/NetworkModule.kt`: Provides OkHttp, Retrofit, `WeatherApiService`, and binds `WeatherRepository`
- `di/LocationModule.kt`: Provides `FusedLocationProviderClient`

## Application
- `BikeApplication.kt`: Enables Hilt
- `MainActivity.kt`: Hosts Compose content

## Tests
- Unit tests: `WeatherRepositoryTest` (Ride Score behavior), `WeatherRepositoryPeriodTest` (period grouping)
- Instrumented tests: `WeatherIntegrationTest` with Hilt test modules

## Build
- minSdk 26, target/compile 35
- Compose + Material3 enabled
- Glance + DataStore dependencies added