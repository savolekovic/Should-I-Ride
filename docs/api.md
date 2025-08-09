# API Documentation

The app uses OpenWeather's 5‑day/3‑hour forecast API.

## Base URL

`https://api.openweathermap.org/data/2.5/`

## Endpoints

### GET forecast

- Path: `forecast`
- Query parameters:
  - `lat` (Double): latitude
  - `lon` (Double): longitude
  - `units` (String): set to `metric`
  - `appid` (String): API key

### Response Models (subset)

- `ForecastResponse`:
  - `city: City`
  - `list: List<ForecastItem>` — 3‑hour interval items
- `ForecastItem`:
  - `dt: Long` (epoch seconds)
  - `main: MainWeatherData`
  - `weather: List<Weather>`
  - `wind: Wind`
  - `pop: Double` (probability of precipitation [0,1])

See source in `data/remote/model/*` for full mapping.

## Mapping to Domain

Repository filters `ForecastResponse.list` to items at 07:00 Monday–Friday of the current week, maps to `domain/model/WeatherForecast`, and computes a Ride Score per item.

## Authentication

- API key is required via `appid` query parameter.
- Currently specified in `WeatherRepositoryImpl.apiKey`.
  - TODO: Clarify functionality and externalize configuration.