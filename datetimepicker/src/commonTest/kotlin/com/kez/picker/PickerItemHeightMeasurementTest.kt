package com.kez.picker

import com.kez.picker.util.TimePeriod
import kotlin.test.Test
import kotlin.test.assertEquals

class PickerItemHeightMeasurementTest {

    @Test
    fun pickerItemHeightTexts_builtinTemporalFormatsUseBoundedProbes() {
        val formats = listOf(
            PickerDefaults.timePickerFormat().hour to "0123456789",
            PickerDefaults.timePickerFormat().minute to "0123456789",
            PickerDefaults.datePickerFormat().year to "0123456789",
            PickerDefaults.datePickerFormat().month to "0123456789",
            PickerDefaults.datePickerFormat().day to "0123456789",
            PickerDefaults.yearMonthPickerFormat().year to "0123456789",
            PickerDefaults.yearMonthPickerFormat().month to "0123456789"
        )

        listOf(10, 100, 10_000).forEach { itemCount ->
            formats.forEach { (format, expectedProbe) ->
                assertEquals(
                    listOf(expectedProbe),
                    pickerItemHeightTexts(
                        items = (1..itemCount).toList(),
                        format = format
                    ),
                    "itemCount=$itemCount"
                )
            }
        }
    }

    @Test
    fun pickerItemHeightTexts_builtinPeriodFormatUsesBoundedProbe() {
        assertEquals(
            listOf("AMPM"),
            pickerItemHeightTexts(
                items = TimePeriod.entries,
                format = PickerDefaults.timePickerFormat().period
            )
        )
    }

    @Test
    fun pickerItemHeightTexts_genericFormatMeasuresEveryItemExactly() {
        listOf(10, 100, 10_000).forEach { itemCount ->
            var formatCallCount = 0
            val format = PickerDefaults.itemFormat<Int>(
                itemText = { item ->
                    formatCallCount += 1
                    "value-$item"
                }
            )

            val texts = pickerItemHeightTexts(
                items = (1..itemCount).toList(),
                format = format
            )

            assertEquals(itemCount, formatCallCount, "itemCount=$itemCount")
            assertEquals(itemCount, texts.size, "itemCount=$itemCount")
            assertEquals("value-1", texts.first())
            assertEquals("value-$itemCount", texts.last())
        }
    }

    @Test
    fun pickerItemHeightTexts_customTemporalFormatterMeasuresEveryItemExactly() {
        val format = PickerDefaults.datePickerFormat(
            yearItemText = { "${it}년" }
        ).year

        assertEquals(
            (1000..9999).map { "${it}년" },
            pickerItemHeightTexts(
                items = (1000..9999).toList(),
                format = format
            )
        )
    }

    @Test
    fun pickerItemHeightTexts_copiedCustomFormatterReturnsToExactMeasurement() {
        val defaultFormat = PickerDefaults.datePickerFormat().year
        val copiedFormat = defaultFormat.copy(
            itemText = { "${it}년" }
        )

        assertEquals(
            listOf("1000년", "1001년"),
            pickerItemHeightTexts(
                items = listOf(1000, 1001),
                format = copiedFormat
            )
        )
    }

    @Test
    fun pickerItemHeightTexts_customFormatterCannotSpoofBuiltInIdentityWithEquals() {
        val formatter = object : (Int) -> String {
            override fun invoke(item: Int): String = "${item} custom"

            override fun equals(other: Any?): Boolean = true

            override fun hashCode(): Int = 0
        }
        val format = PickerDefaults.datePickerFormat(
            yearItemText = formatter
        ).year

        assertEquals(
            listOf("1000 custom", "1001 custom"),
            pickerItemHeightTexts(
                items = listOf(1000, 1001),
                format = format
            )
        )
    }
}
