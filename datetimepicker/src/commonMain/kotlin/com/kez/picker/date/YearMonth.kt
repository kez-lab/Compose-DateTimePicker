package com.kez.picker.date

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/**
 * Year and month value selected by [YearMonthPicker].
 *
 * This type avoids using a first-day [LocalDate] as the primary value when an app only needs a
 * year/month pair.
 *
 * @param year Year value in 1000..9999.
 * @param month Month value in 1..12.
 */
data class YearMonth(
    val year: Int,
    val month: Int
) {
    init {
        require(year in 1000..9999) {
            "year must be in range [1000, 9999], but was $year"
        }
        require(month in 1..12) {
            "month must be in range [1, 12], but was $month"
        }
    }

    /**
     * Converts this value to a [LocalDate] using [dayOfMonth].
     */
    fun atDay(dayOfMonth: Int = 1): LocalDate = LocalDate(year, month, dayOfMonth)

    companion object {
        /**
         * Creates a [YearMonth] from [date], ignoring the day value.
         */
        fun from(date: LocalDate): YearMonth = YearMonth(
            year = date.year,
            month = date.month.number
        )
    }
}
