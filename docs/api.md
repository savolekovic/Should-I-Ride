# Public API Notes

## Domain
- WeatherRepository.getWeatherForecast(lat, lon): Pair<String, List<WeatherForecast>>
  - Returns Morning forecasts only (backward compatibility)
- WeatherRepository.getWeatherForecastsByPeriod(lat, lon, periods): Pair<String, Map<RidePeriod, List<WeatherForecast>>>
  - Groups forecasts into Morning/Midday/Evening buckets

## Models
- WeatherForecast: adds `period: RidePeriod` and `timestamp: Long`
- RidePeriod: `MORNING(07:00)`, `MIDDAY(12:00)`, `EVENING(18:00)` with helpers `defaultOrder`, `fromHour`, `nextPeriodFor`

## Widget
- WidgetUpdater.updateWithForecasts(map, city, context)
  - Selects next period item, persists to DataStore, triggers Glance update