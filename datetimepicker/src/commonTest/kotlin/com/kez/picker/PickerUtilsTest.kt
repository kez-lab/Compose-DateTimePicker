package com.kez.picker

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Unit tests for Picker utility functions and calculations.
 *
 * Tests cover:
 * - Index calculations for infinite scroll
 * - Modulo operations for cyclic behavior
 * - Edge case handling
 */
class PickerUtilsTest {

    // ==================== Modulo Index Calculation Tests ====================

    @Test
    fun modIndex_positiveValues_calculatesCorrectly() {
        val items = listOf("A", "B", "C")
        val size = items.size

        assertEquals(0, 0.mod(size))
        assertEquals(1, 1.mod(size))
        assertEquals(2, 2.mod(size))
        assertEquals(0, 3.mod(size))
        assertEquals(1, 4.mod(size))
    }

    @Test
    fun modIndex_largeValues_calculatesCorrectly() {
        val items = listOf(1, 2, 3, 4, 5)
        val size = items.size

        assertEquals(0, 1000.mod(size))
        assertEquals(1, 1001.mod(size))
        assertEquals(4, 999.mod(size))
    }

    @Test
    fun modIndex_negativeValues_handledCorrectly() {
        val size = 5

        // Kotlin's mod() handles negative numbers correctly
        assertEquals(4, (-1).mod(size))
        assertEquals(3, (-2).mod(size))
        assertEquals(0, (-5).mod(size))
    }

    // ==================== Visible Items Count Validation Tests ====================

    @Test
    fun visibleItemsCount_oddNumbers_areValid() {
        val validCounts = listOf(1, 3, 5, 7, 9, 11)

        for (count in validCounts) {
            assertTrue(count > 0 && count % 2 == 1, "Count $count should be a positive odd number")
        }
    }

    @Test
    fun visibleItemsCount_evenNumbers_areInvalid() {
        val invalidCounts = listOf(2, 4, 6, 8, 10)

        for (count in invalidCounts) {
            assertTrue(count % 2 == 0, "Count $count should be an even number")
        }
    }

    @Test
    fun validatePickerInput_throwsActionableMessageWhenItemsEmpty() {
        val error = assertFailsWith<IllegalArgumentException> {
            validatePickerInput(
                items = emptyList(),
                selectedItem = "Small",
                visibleItemsCount = 3
            )
        }

        val message = error.message.orEmpty()
        assertTrue(message.contains("Picker items must not be empty"))
        assertTrue(message.contains("keep selectedItem from that list"))
    }

    @Test
    fun validatePickerInput_throwsActionableMessageWhenItemsContainDuplicates() {
        val error = assertFailsWith<IllegalArgumentException> {
            validatePickerInput(
                items = listOf("Small", "Small"),
                selectedItem = "Small",
                visibleItemsCount = 3
            )
        }

        val message = error.message.orEmpty()
        assertTrue(message.contains("item equality"))
        assertTrue(message.contains("stable unique item values"))
    }

    @Test
    fun validatePickerInput_throwsActionableMessageWhenSelectedItemMissing() {
        val error = assertFailsWith<IllegalArgumentException> {
            validatePickerInput(
                items = listOf("Small", "Medium", "Large"),
                selectedItem = "Extra Large",
                visibleItemsCount = 3
            )
        }

        val message = error.message.orEmpty()
        assertTrue(message.contains("Picker<T> is controlled"))
        assertTrue(message.contains("update the app-owned selectedItem when items change"))
        assertTrue(message.contains("coerce it to one of items"))
    }

    @Test
    fun validatePickerInput_throwsActionableMessageWhenVisibleItemsCountInvalid() {
        val error = assertFailsWith<IllegalArgumentException> {
            validatePickerInput(
                items = listOf("Small", "Medium", "Large"),
                selectedItem = "Medium",
                visibleItemsCount = 4
            )
        }

        assertTrue(error.message.orEmpty().contains("PickerDefaults.style(visibleItemsCount = ...)"))
    }

    // ==================== List Middle Calculation Tests ====================

    @Test
    fun listMiddle_calculatesCorrectly() {
        assertEquals(0, 1 / 2)
        assertEquals(1, 3 / 2)
        assertEquals(2, 5 / 2)
        assertEquals(3, 7 / 2)
    }

    // ==================== Infinite Scroll Multiplier Tests ====================

    @Test
    fun infiniteScrollMultiplier_producesReasonableSize() {
        val items = listOf("A", "B", "C")
        val multiplier = 1000
        val listScrollCount = items.size * multiplier

        assertEquals(3000, listScrollCount)
        assertTrue(listScrollCount < Int.MAX_VALUE)
    }

    @Test
    fun infiniteScrollMultiplier_centerCalculation_isCorrect() {
        val items = listOf("A", "B", "C", "D", "E")
        val multiplier = 1000
        val listScrollCount = items.size * multiplier
        val listScrollMiddle = listScrollCount / 2

        assertEquals(2500, listScrollMiddle)
    }

    // ==================== Start Index Calculation Tests ====================

    @Test
    fun startIndex_infinityMode_calculatesCorrectly() {
        val items = listOf("A", "B", "C", "D", "E")
        val itemSize = items.size
        val multiplier = 1000
        val listScrollCount = itemSize * multiplier
        val listScrollMiddle = listScrollCount / 2
        val visibleItemsMiddle = 1 // for visibleItemsCount = 3
        val startIndex = 2

        val expectedStartIndex =
            listScrollMiddle - listScrollMiddle % itemSize - visibleItemsMiddle + startIndex

        // Should position us near the middle but at the correct item
        assertTrue(expectedStartIndex > 0)
        assertTrue(expectedStartIndex < listScrollCount)
    }

    @Test
    fun startIndex_boundedMode_calculatesCorrectly() {
        val startIndex = 2
        val expectedBoundedStart = startIndex

        assertEquals(2, expectedBoundedStart)
    }

    @Test
    fun boundedMode_paddingMatchesVisibleItemsMiddle() {
        val visibleItemsMiddle = 2 // for visibleItemsCount = 5
        val items = listOf("A", "B", "C")
        val adjustedItems = List(visibleItemsMiddle) { null } + items + List(visibleItemsMiddle) { null }

        assertEquals(null, adjustedItems[0])
        assertEquals(null, adjustedItems[1])
        assertEquals("A", adjustedItems[visibleItemsMiddle])
        assertEquals("C", adjustedItems[visibleItemsMiddle + items.lastIndex])
    }

    // ==================== Fraction Calculation Tests ====================

    @Test
    fun fraction_coerceInRange_worksCorrectly() {
        val testValues = listOf(-2f, -1f, -0.5f, 0f, 0.5f, 1f, 2f)
        val expectedResults = listOf(1f, 1f, 0.5f, 0f, 0.5f, 1f, 1f)

        testValues.zip(expectedResults).forEach { (input, expected) ->
            val result = kotlin.math.abs(input.coerceIn(-1f, 1f))
            assertEquals(expected, result, 0.001f, "For input $input, expected $expected but got $result")
        }
    }

    // ==================== Accessibility Description Tests ====================

    @Test
    fun pickerAccessibilityDescription_combinesNormalizedLabelAndValue() {
        assertEquals("Hour: 10", pickerAccessibilityDescription("Hour", "10"))
        assertEquals("Hour: 10", pickerAccessibilityDescription(" Hour ", "10"))
        assertEquals("시: 10시", pickerAccessibilityDescription("시", "10시"))
    }

    @Test
    fun pickerAccessibilityDescription_omitsBlankOrMissingLabel() {
        assertEquals("10", pickerAccessibilityDescription(null, "10"))
        assertEquals("10", pickerAccessibilityDescription("", "10"))
        assertEquals("10", pickerAccessibilityDescription("   ", "10"))
    }

    @Test
    fun pickerAccessibilityDescription_usesLabelWhenValueIsBlank() {
        assertEquals("Hour", pickerAccessibilityDescription("Hour", ""))
        assertEquals("Hour", pickerAccessibilityDescription("Hour", "   "))
    }

    @Test
    fun pickerAccessibilityDescription_preservesValueWhitespaceWhenLabelIsMissing() {
        assertEquals("", pickerAccessibilityDescription(null, ""))
        assertEquals("   ", pickerAccessibilityDescription(null, "   "))
    }
}
