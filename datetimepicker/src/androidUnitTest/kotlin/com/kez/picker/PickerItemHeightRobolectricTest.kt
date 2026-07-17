package com.kez.picker

import androidx.compose.ui.test.junit4.createComposeRule
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PickerItemHeightRobolectricTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun picker_largeColumnWithExplicitProbeDoesNotFormatEveryItemForHeightMeasurement() {
        val items = (1..10_000).toList()
        val formatCallCount = AtomicInteger()
        val format = PickerDefaults.itemFormat<Int>(
            itemText = { item ->
                formatCallCount.incrementAndGet()
                item.toString()
            },
            itemHeightProbeText = "0123456789"
        )

        composeRule.setContent {
            Picker(
                items = items,
                selectedItem = 5_000,
                onSelectedItemChange = {},
                format = format,
                isInfinity = false
            )
        }
        composeRule.waitForIdle()

        assertTrue(
            "Expected bounded formatting work, but itemText ran ${formatCallCount.get()} times.",
            formatCallCount.get() <= 128
        )
    }
}
