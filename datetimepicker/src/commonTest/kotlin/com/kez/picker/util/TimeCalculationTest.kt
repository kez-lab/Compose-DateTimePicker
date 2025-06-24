package com.kez.picker.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime

class TimeCalculationTest {
    // 현재 날짜를 고정하여 가져온다
    private val testDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    private val currentYear: Int = testDate.year
    private val currentMonth: Int = testDate.monthNumber
    private val currentDate: LocalDate = testDate

    // 24시간 형식에서 23:45 입력 시 동일한 시간이 반환되는지 확인
    @Test
    fun calculateTime_with24HourFormat() {
        val result = calculateTime(
            hour = 23,
            minute = 45,
            timeFormat = TimeFormat.HOUR_24
        )

        val expected = LocalDateTime(
            year = currentYear,
            monthNumber = currentMonth,
            dayOfMonth = currentDate.dayOfMonth,
            hour = 23,
            minute = 45
        )

        assertEquals(expected, result)
    }

    // 오후 3시 15분을 입력했을 때 15:15로 변환되는지 확인
    @Test
    fun calculateTime_with12HourFormat_PM() {
        val result = calculateTime(
            hour = 3,
            minute = 15,
            timeFormat = TimeFormat.HOUR_12,
            period = TimePeriod.PM
        )

        val expected = LocalDateTime(
            year = currentYear,
            monthNumber = currentMonth,
            dayOfMonth = currentDate.dayOfMonth,
            hour = 15,
            minute = 15
        )

        assertEquals(expected, result)
    }

    // 자정(AM 12:00)을 입력하면 0시로 계산되는지 확인
    @Test
    fun calculateTime_with12HourFormat_AM_Midnight() {
        val result = calculateTime(
            hour = 12,
            minute = 0,
            timeFormat = TimeFormat.HOUR_12,
            period = TimePeriod.AM
        )

        val expected = LocalDateTime(
            year = currentYear,
            monthNumber = currentMonth,
            dayOfMonth = currentDate.dayOfMonth,
            hour = 0,
            minute = 0
        )

        assertEquals(expected, result)
    }

    // 정오(PM 12:30)을 입력하면 그대로 12:30으로 계산되는지 확인
    @Test
    fun calculateTime_with12HourFormat_PM_Noon() {
        val result = calculateTime(
            hour = 12,
            minute = 30,
            timeFormat = TimeFormat.HOUR_12,
            period = TimePeriod.PM
        )

        val expected = LocalDateTime(
            year = currentYear,
            monthNumber = currentMonth,
            dayOfMonth = currentDate.dayOfMonth,
            hour = 12,
            minute = 30
        )

        assertEquals(expected, result)
    }
}
