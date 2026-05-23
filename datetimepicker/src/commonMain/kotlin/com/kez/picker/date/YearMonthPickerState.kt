package com.kez.picker.date

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.kez.picker.YearMonthPickerItems
import com.kez.picker.util.currentDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/**
 * Creates and remembers a [YearMonthPickerState] from a [LocalDate].
 * Initial year and month are read when the state is first created.
 *
 * The day value is ignored because [YearMonthPicker] only selects year and month.
 *
 * @param initialDate The initial date whose year and month should be selected. Defaults to the current date.
 * @return A [YearMonthPickerState] initialized with the year and month from [initialDate].
 * @throws IllegalArgumentException if [initialDate]'s year is outside the supported 1000..9999 range.
 */
@Composable
fun rememberYearMonthPickerState(
    initialDate: LocalDate = currentDate()
): YearMonthPickerState {
    val rememberedInitialDate = remember { initialDate }
    return rememberYearMonthPickerState(
        initialYear = rememberedInitialDate.year,
        initialMonth = rememberedInitialDate.month.number
    )
}

/**
 * Creates and remembers a [YearMonthPickerState] whose initial year/month is coerced by [items].
 *
 * Initial values and [items] are read when the state is first created. The day value of
 * [initialDate] is ignored because [YearMonthPicker] only selects year and month. This is useful
 * when the picker is rendered with custom item lists or year/month bounds and restored app state may
 * fall outside those rules.
 *
 * @param items Selectable values used to coerce [initialDate] before creating the state.
 * @param initialDate The requested initial date.
 * @return A [YearMonthPickerState] initialized to the closest selectable year/month.
 */
@Composable
fun rememberYearMonthPickerState(
    items: YearMonthPickerItems,
    initialDate: LocalDate = currentDate()
): YearMonthPickerState {
    val rememberedInitialDate = remember { initialDate }
    val rememberedItems = remember { items }
    val coercedInitialDate = remember(rememberedInitialDate, rememberedItems) {
        rememberedItems.coerceDate(rememberedInitialDate)
    }
    return rememberYearMonthPickerState(initialDate = coercedInitialDate)
}

/**
 * Creates and remembers a [YearMonthPickerState] with explicit year and month values.
 * Initial year and month are read when the state is first created.
 *
 * @param initialYear The initial year to be selected. Must be in 1000..9999.
 * @param initialMonth The initial month to be selected. Must be in 1..12.
 * @return A [YearMonthPickerState] initialized with the given year and month.
 */
@Composable
fun rememberYearMonthPickerState(
    initialYear: Int,
    initialMonth: Int
): YearMonthPickerState {
    val rememberedInitialYear = remember { initialYear }
    val rememberedInitialMonth = remember { initialMonth }
    return rememberSaveable(saver = YearMonthPickerState.Saver) {
        YearMonthPickerState(rememberedInitialYear, rememberedInitialMonth)
    }
}

/**
 * Creates and remembers a [YearMonthPickerState] whose explicit initial year/month is coerced by [items].
 *
 * @param items Selectable values used to coerce [initialYear] and [initialMonth].
 * @param initialYear The requested initial year.
 * @param initialMonth The requested initial month.
 * @return A [YearMonthPickerState] initialized to the closest selectable year/month.
 */
@Composable
fun rememberYearMonthPickerState(
    items: YearMonthPickerItems,
    initialYear: Int,
    initialMonth: Int
): YearMonthPickerState {
    val rememberedInitialYear = remember { initialYear }
    val rememberedInitialMonth = remember { initialMonth }
    val rememberedItems = remember { items }
    val coercedInitialYearMonth = remember(
        rememberedInitialYear,
        rememberedInitialMonth,
        rememberedItems
    ) {
        rememberedItems.coerceYearMonth(
            year = rememberedInitialYear,
            month = rememberedInitialMonth
        )
    }
    return rememberYearMonthPickerState(
        initialYear = coercedInitialYearMonth.year,
        initialMonth = coercedInitialYearMonth.month
    )
}

/**
 * State holder for the [YearMonthPicker].
 *
 * Manages the selected year and month.
 *
 * @param initialYear The initial year to be selected. Must be in 1000..9999.
 * @param initialMonth The initial month to be selected.
 * @throws IllegalArgumentException if [initialYear] or [initialMonth] is outside the supported range.
 */
@Stable
class YearMonthPickerState(
    initialYear: Int,
    initialMonth: Int
) {
    /**
     * Creates a [YearMonthPickerState] from [initialYearMonth].
     */
    constructor(initialYearMonth: YearMonth) : this(
        initialYear = initialYearMonth.year,
        initialMonth = initialYearMonth.month
    )

    /**
     * Creates a [YearMonthPickerState] from the year/month portion of [initialDate].
     *
     * The day value is ignored because [YearMonthPicker] only selects year and month.
     */
    constructor(initialDate: LocalDate) : this(YearMonth.from(initialDate))

    init {
        require(initialYear in 1000..9999) {
            "initialYear must be in range [1000, 9999], but was $initialYear"
        }
        require(initialMonth in 1..12) {
            "initialMonth must be in range [1, 12], but was $initialMonth"
        }
    }

    private var mutableSelectedYear: Int by mutableStateOf(initialYear)
    private var mutableSelectedMonth: Int by mutableStateOf(initialMonth)

    /**
     * The currently selected year.
     */
    val selectedYear: Int
        get() = mutableSelectedYear

    /**
     * The currently selected month (1-12).
     */
    val selectedMonth: Int
        get() = mutableSelectedMonth

    /**
     * The selected year/month value.
     */
    val selectedYearMonth: YearMonth
        get() = YearMonth(selectedYear, selectedMonth)

    /**
     * The selected year and month represented as the first day of that month.
     */
    val selectedMonthDate: LocalDate
        get() = selectedYearMonth.atDay()

    /**
     * Programmatically selects [year] and [month].
     *
     * @throws IllegalArgumentException if [year] or [month] is outside the supported range.
     */
    fun selectYearMonth(year: Int, month: Int) {
        updateYearMonth(year, month)
    }

    /**
     * Programmatically selects [yearMonth].
     */
    fun selectYearMonth(yearMonth: YearMonth) {
        updateYearMonth(yearMonth.year, yearMonth.month)
    }

    /**
     * Programmatically selects the closest year/month to [yearMonth] that is allowed by [items].
     *
     * Use this overload when app-owned state can contain values outside custom picker lists or
     * year/month bounds.
     */
    fun selectYearMonth(yearMonth: YearMonth, items: YearMonthPickerItems) {
        selectYearMonth(items.coerceYearMonth(yearMonth))
    }

    /**
     * Programmatically selects the closest year/month to [year] and [month] that is allowed by [items].
     */
    fun selectYearMonth(year: Int, month: Int, items: YearMonthPickerItems) {
        selectYearMonth(items.coerceYearMonth(year = year, month = month))
    }

    internal fun selectYear(year: Int) {
        updateYearMonth(year, selectedMonth)
    }

    internal fun selectMonth(month: Int) {
        updateYearMonth(selectedYear, month)
    }

    private fun updateYearMonth(year: Int, month: Int) {
        require(year in 1000..9999) {
            "year must be in range [1000, 9999], but was $year"
        }
        require(month in 1..12) {
            "month must be in range [1, 12], but was $month"
        }
        mutableSelectedYear = year
        mutableSelectedMonth = month
    }

    /**
     * Programmatically selects the year and month from [date].
     *
     * The day value is ignored because [YearMonthPicker] only selects year and month.
     *
     * @throws IllegalArgumentException if [date]'s year is outside the supported range.
     */
    fun selectDate(date: LocalDate) {
        selectYearMonth(YearMonth.from(date))
    }

    /**
     * Programmatically selects the closest year/month to [date] that is allowed by [items].
     *
     * The day value is ignored because [YearMonthPicker] only selects year and month.
     */
    fun selectDate(date: LocalDate, items: YearMonthPickerItems) {
        selectYearMonth(YearMonth.from(date), items)
    }

    companion object {
        /**
         * Saves and restores [YearMonthPickerState] across configuration changes.
         */
        val Saver: Saver<YearMonthPickerState, Any> = listSaver(
            save = { listOf(it.selectedYear, it.selectedMonth) },
            restore = {
                YearMonthPickerState(
                    initialYear = it[0] as Int,
                    initialMonth = it[1] as Int
                )
            }
        )
    }
}
