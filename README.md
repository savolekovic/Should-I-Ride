# Should I Ride

A modern Android app that helps you decide if it is safe and comfortable to ride your bicycle or e‑scooter. The app shows time‑of‑day forecasts and a custom Ride Score based on temperature, wind, and rain probability.

## Core Features

- Weekday forecasts for three ride periods: Morning (07:00), Midday (12:00), Evening (18:00)
- Swipe/tab between periods and see time‑specific Ride Scores
- Home screen widget showing the next ride period’s score and key weather data
- Location-based weather using OpenWeather
- Ride Score (0–100) factoring rain, wind, and temperature
- Material 3 UI built with Jetpack Compose
- Clear loading and error states

## Tech Stack

- Kotlin, Coroutines, Flow
- Jetpack Compose (Material 3)
- MVVM + Clean Architecture (Domain, Data, Presentation)
- Hilt (DI)
- Retrofit + OkHttp (network)
- Google Play Services Location
- Glance (App Widgets)
- DataStore (widget state)

## Architecture Overview

- Domain: repository interfaces, `RidePeriod` enum, and the `WeatherForecast` model (now includes `period` and `timestamp`)
- Data: Retrofit service, OpenWeather DTOs, and `WeatherRepositoryImpl` (now groups forecasts by `RidePeriod`)
- Presentation: `WeatherViewModel` and Compose UI with period selector tabs
- Widget: Glance `RideWidget` and `RideWidgetProvider`; updates when new data is fetched
- DI: `NetworkModule` and `LocationModule`

See `docs/architecture.md` and `docs/modules.md` for diagrams and details.

## Requirements

- Android Studio (latest stable)
- JDK 11+
- Android SDK 35 (compile) / minSdk 26

## Setup

1. Obtain an OpenWeather API key (`https://openweathermap.org/api`).
2. Create or edit `local.properties` in the project root and add:
   ```
   OPEN_WEATHER_API_KEY=your_key_here
   ```
3. Build the app. The key is injected as `BuildConfig.OPEN_WEATHER_API_KEY`.
4. Ensure your device/emulator has location enabled.

## Build & Run

- From Android Studio: Open the project and Run the `app` configuration.
- From terminal:

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

To run tests:

```bash
./gradlew testDebugUnitTest
./gradlew connectedAndroidTest
```

## New in this version

- Hourly Forecast View: Morning/Midday/Evening periods with dedicated Ride Scores
- Home Screen Widget: Shows the next ride period’s score and key weather at a glance; auto‑updates after fetch

### Screenshots (placeholders)

- Main screen with period tabs: [docs/img/screen-periods.png]
- Widget small: [docs/img/widget-small.png]
- Widget medium: [docs/img/widget-medium.png]

## How Ride Score Works

The score prioritizes safety:
- Rain probability (0–40 points)
- Wind speed (0–35 points)
- Temperature (0–25 points)

Critical conditions (very high rain chance, dangerous wind, or extreme temperatures) cap the final score at 30 and surface a warning in the UI.

## Contribution Guidelines

- Use Kotlin and follow the existing code style
- Keep architecture boundaries (Domain/Data/Presentation) intact
- Do not hardcode secrets; prefer build-time config (see Setup)
- Write small, focused changes and include tests where practical
- Use clear commit messages and PR descriptions

## License

This project uses external APIs and libraries as noted in the source. Review their licenses before distribution. 