# Architecture

The app follows a simplified Clean Architecture with MVVM on the presentation layer. Responsibilities are split across Domain, Data, and Presentation, and wired using Hilt for dependency injection.

## Layered Overview

```
+------------------+        +---------------------+
|     Presentation |        | Dependency Injection|
|------------------|        |---------------------|
| Jetpack Compose  |<------>| Hilt Modules        |
| WeatherViewModel |        | (Network, Location) |
+--------^---------+        +----------^----------+
         |                               |
         | State (Flow)                  |
         v                               |
+------------------+        +---------- | --------+
|       Domain     |<-------------------+          
|------------------|                                  
| WeatherRepository| (interface)                       
| WeatherForecast  | (model)                           
+--------^---------+                                   
         |                                             
         | Implementation                              
         v                                             
+------------------------------+                       
|            Data              |                       
|------------------------------|                       
| Retrofit WeatherApiService   |--> OpenWeather        
| DTOs (OpenWeatherResponse)   |                       
| WeatherRepositoryImpl        |                       
+------------------------------+                       
```

## Data Flow

1. UI composes `WeatherScreen` which collects state from `WeatherViewModel`.
2. `WeatherViewModel` checks permissions via `LocationService` and fetches device location.
3. `WeatherViewModel` calls `WeatherRepository.getWeatherForecast(lat, lon)`.
4. `WeatherRepositoryImpl` requests the 5‑day/3‑hour forecast and filters to weekdays at 07:00 for the current week.
5. Repository maps DTOs to domain `WeatherForecast`, computes a Ride Score, and returns the city name with the list.
6. UI renders a list of cards, including warnings when conditions are critical.

## Ride Score

- Rain: 0–40 points (higher probability reduces score)
- Wind: 0–35 points (higher speed reduces score)
- Temperature: 0–25 points (ideal ~10–25°C)
- Critical conditions cap the score at 30.

## Permissions and Location

- App requests coarse/fine location at first composition of the screen.
- `LocationService` validates permission and provider state and reads the last known location using the fused location provider.

## Notes

- API key is currently read from `WeatherRepositoryImpl`.
  - TODO: Clarify functionality and move to secure config (e.g., `BuildConfig`).
- Time filtering uses device local time and week-of-year.