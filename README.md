# Should I Ride

A modern Android app that helps you decide if it is safe and comfortable to ride your bicycle or e‑scooter. The app shows a weekday morning forecast and a custom Ride Score based on temperature, wind, and rain probability.

## Core Features

- Weekday forecast filtered to 07:00 for the current week
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

## Architecture Overview

- Domain: repository interfaces and the `WeatherForecast` model
- Data: Retrofit service, OpenWeather DTOs, and `WeatherRepositoryImpl`
- Presentation: `WeatherViewModel` and Compose UI
- DI: `NetworkModule` and `LocationModule`

See `docs/architecture.md` and `docs/modules.md` for diagrams and details.

## Requirements

- Android Studio (latest stable)
- JDK 11+
- Android SDK 35 (compile) / minSdk 26

## Setup

1. Obtain an OpenWeather API key (`https://openweathermap.org/api`).
2. Replace the placeholder in `WeatherRepositoryImpl.apiKey` with your key.
   - TODO: Clarify functionality and externalize API key to `local.properties`/`BuildConfig` for security.
3. Ensure your device/emulator has location enabled and set a mock location if needed.

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

## How Ride Score Works

The score prioritizes safety:
- Rain probability (0–40 points)
- Wind speed (0–35 points)
- Temperature (0–25 points)

Critical conditions (very high rain chance, dangerous wind, or extreme temperatures) cap the final score at 30 and surface a warning in the UI.

## Contribution Guidelines

- Use Kotlin and follow the existing code style
- Keep architecture boundaries (Domain/Data/Presentation) intact
- Do not hardcode secrets; prefer build-time config (see TODO above)
- Write small, focused changes and include tests where practical
- Use clear commit messages and PR descriptions

## License

This project uses external APIs and libraries as noted in the source. Review their licenses before distribution. 