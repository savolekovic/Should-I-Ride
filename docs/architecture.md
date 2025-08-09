# Architecture

The app follows a simplified Clean Architecture with MVVM on the presentation layer. Responsibilities are split across Domain, Data, and Presentation, and wired using Hilt for dependency injection.

## Layered Overview

```
+------------------+        +---------------------+         +------------------+
|   Presentation   |        | Dependency Injection|         |      Widget      |
|------------------|        |---------------------|         |------------------|
| Jetpack Compose  |<------>| Hilt Modules        |   --->  | Glance AppWidget |
| WeatherViewModel |        | (Network, Location) |         | RideWidget       |
+--------^---------+        +----------^----------+         +------------------+
         |                               |
         | State (Flow)                  | provides
         v                               |
+------------------+        +---------- | --------+
|      Domain      |<-------------------+          
|------------------|                                  
| WeatherRepository| (interface)                       
| WeatherForecast  | (model)                           
| RidePeriod       | (enum)                            
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

## Data Flow (Hourly Periods)

```
[Device time]
  └─> WeatherViewModel suggests next RidePeriod via nextPeriodFor(hour)
       └─> Periods = [07, 12, 18]

WeatherViewModel
  └─> WeatherRepository.getWeatherForecastsByPeriod(lat, lon, Periods)
       ├─> WeatherApiService.forecast
       ├─> Filter: weekday && weekOfYear && hour ∈ Periods
       ├─> Group: Map<RidePeriod, List<ForecastItem>>
       └─> Map: List<WeatherForecast> (adds period, timestamp, score)

UI
  ├─> TabRow(Morning | Midday | Evening)
  └─> LazyColumn for selected period
```

### Widget Update Flow

```
WeatherViewModel (after fetch)
  -> WidgetUpdater.updateWithForecasts(map, city, context)
     -> Choose next RidePeriod based on current time
     -> Persist selected item in DataStore
     -> Glance RideWidget.updateAll()
Home Screen -> RideWidget reads state -> renders title, subtitle, score, temp, wind, rain
```

## Ride Score

- Rain: 0–40 points (higher probability reduces score)
- Wind: 0–35 points (higher speed reduces score)
- Temperature: 0–25 points (ideal ~10–25°C)
- Critical conditions cap the score at 30.

## Permissions and Location

- App requests coarse/fine location at first composition of the screen.
- `LocationService` validates permission and provider state and reads the last known location using the fused location provider.

## Notes

- API key is read from `BuildConfig.OPEN_WEATHER_API_KEY` injected from `local.properties`.
- Time filtering uses device local time and week-of-year.