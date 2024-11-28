# Should I Ride 🛴

A modern Android application that helps riders decide if it's a good day for a trip on their e-scooter or bicycle. The app provides a daily weather forecast with a custom "Ride Score" that considers temperature, wind speed, and precipitation probability to ensure a safe and comfortable journey.

## Features 🌟

- 5-day weather forecast for workdays (7:00 AM)
- Custom Ride Score (0-100) based on:
  - Temperature safety (optimal range for riding)
  - Wind conditions (safety for lightweight vehicles)
  - Rain probability (road safety and visibility)
- Material 3 Design with intuitive UI
- Location-based weather data
- Detailed weather metrics for informed decisions

## Tech Stack 🛠

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **Coroutines & Flow** - Asynchronous programming
- **Hilt** - Dependency injection
- **Retrofit** - Network calls
- **Clean Architecture** - Project structure
- **MVVM** - Presentation pattern
- **Material 3** - Design system

## Architecture 🏗

The project follows Clean Architecture principles with three main layers:

### Domain Layer
- Business logic and entities
- Repository interfaces
- Weather forecast models

### Data Layer
- OpenWeatherMap API integration
- Repository implementations
- Weather data models

### Presentation Layer
- Weather forecast display
- Ride score visualization
- Loading and error states

## Getting Started 🚀

### Prerequisites
- Android Studio Hedgehog | 2023.1.1 or newer
- JDK 17 or newer
- Android SDK 34

### Setup
1. Clone the repository
2. Get an API key from [OpenWeatherMap](https://openweathermap.org/api)
3. Add your API key to the `WeatherRepository.kt` file
4. Build and run the project

## How Ride Score Works 🎯

The Ride Score is calculated using multiple factors, with rain being the most critical factor for safety:

- **Rain Probability (50%)**
  - Perfect score (50 points): No rain
  - High score (45 points): < 15% chance
  - Medium score (35 points): < 30% chance
  - Low score (25 points): < 45% chance
  - Very low score (15 points): < 60% chance
  - Minimal score (5 points): < 75% chance
  - No riding recommended (0 points): ≥ 75% chance
  
- **Temperature (25%)**
  - Optimal range (25 points): 0°C to 30°C
  - Reduced scores for:
    - Below 0°C: Gradual reduction
    - Above 30°C: Penalty for high temperatures
    - Below -5°C or above 35°C: Significant penalties
  
- **Wind Speed (25%)**
  - Perfect conditions (25 points): < 3 m/s
  - Good conditions (21-24 points): 3-6 m/s
  - Moderate conditions (13-20 points): 6-12 m/s
  - High caution (5 points): 12-15 m/s
  - Not recommended (0 points): > 15 m/s

The final score (0-100) helps riders make informed decisions:
- 70-100: Excellent conditions
- 50-69: Good conditions with some caution
- 30-49: Exercise caution
- 0-29: Not recommended for riding

## Contributing 🤝

Contributions are welcome! Feel free to:
- Report bugs
- Suggest features
- Submit pull requests

## Acknowledgments 🙏

- OpenWeatherMap API for weather data
- Material Design 3 for design guidelines
- Android development community 