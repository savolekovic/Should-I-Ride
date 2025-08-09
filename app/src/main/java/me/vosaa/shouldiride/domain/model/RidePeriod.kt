package me.vosaa.shouldiride.domain.model

/**
 * Enumerates supported ride periods during a day and their representative hour.
 *
 * @property displayName User-facing label for the period
 * @property displayTime Time label in HH:mm
 * @property hourOfDay 24-hour clock hour used to select forecast entries
 */
enum class RidePeriod(val displayName: String, val displayTime: String, val hourOfDay: Int) {
    MORNING(displayName = "Morning", displayTime = "07:00", hourOfDay = 7),
    MIDDAY(displayName = "Midday", displayTime = "12:00", hourOfDay = 12),
    EVENING(displayName = "Evening", displayTime = "18:00", hourOfDay = 18);

    companion object {
        /** Default order of periods used in UI and filtering. */
        fun defaultOrder(): List<RidePeriod> = listOf(MORNING, MIDDAY, EVENING)

        /** Returns the corresponding period for a 24-hour [hour], or null if no match. */
        fun fromHour(hour: Int): RidePeriod? = defaultOrder().firstOrNull { it.hourOfDay == hour }

        /** Computes the next upcoming period for the provided [hourNow] (0..23). */
        fun nextPeriodFor(hourNow: Int): RidePeriod = when (hourNow) {
            in 0..6 -> MORNING
            in 7..11 -> MIDDAY
            in 12..17 -> EVENING
            else -> MORNING
        }
    }
}