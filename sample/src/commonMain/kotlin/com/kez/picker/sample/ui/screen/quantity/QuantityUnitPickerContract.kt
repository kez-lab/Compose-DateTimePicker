package com.kez.picker.sample.ui.screen.quantity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlin.math.abs

internal enum class QuantityUnit(
    val symbol: String,
    val gramsPerUnit: Long,
    val displayName: String,
    private val singularName: String,
    private val pluralName: String
) {
    GRAM(
        symbol = "g",
        gramsPerUnit = 1L,
        displayName = "Gram",
        singularName = "gram",
        pluralName = "grams"
    ),
    KILOGRAM(
        symbol = "kg",
        gramsPerUnit = 1_000L,
        displayName = "Kilogram",
        singularName = "kilogram",
        pluralName = "kilograms"
    );

    fun spokenQuantity(quantity: Int): String =
        "$quantity ${if (quantity == 1) singularName else pluralName}"
}

internal data class QuantityUnitSelection(
    val quantity: Int,
    val unit: QuantityUnit
) {
    init {
        require(quantity > 0) { "quantity must be positive, but was $quantity." }
    }

    val normalizedGrams: Long
        get() = quantity.toLong() * unit.gramsPerUnit

    fun displayText(): String = "$quantity ${unit.symbol}"
}

internal data class QuantityUnitItems(
    val unitItems: List<QuantityUnit>,
    val quantityItems: Map<QuantityUnit, List<Int>>
) {
    init {
        require(unitItems.isNotEmpty()) { "QuantityUnit unitItems must not be empty." }
        require(unitItems.distinct().size == unitItems.size) {
            "QuantityUnit unitItems must not contain duplicate values."
        }
        require(quantityItems.keys == unitItems.toSet()) {
            "QuantityUnit quantityItems must contain exactly one source for each configured unit. " +
                    "units=$unitItems, sourceUnits=${quantityItems.keys}."
        }
        unitItems.forEach { unit ->
            val quantities = quantityItems.getValue(unit)
            require(quantities.isNotEmpty()) {
                "QuantityUnit quantities for ${unit.name} must not be empty."
            }
            require(quantities.distinct().size == quantities.size) {
                "QuantityUnit quantities for ${unit.name} must not contain duplicates."
            }
            val invalidQuantities = quantities.filter { it <= 0 }.distinct()
            require(invalidQuantities.isEmpty()) {
                "QuantityUnit quantities for ${unit.name} must be positive. " +
                        "Invalid values: $invalidQuantities."
            }
        }
    }

    fun quantitiesFor(unit: QuantityUnit): List<Int> =
        quantityItems[unit].orEmpty()

    fun contains(selection: QuantityUnitSelection): Boolean =
        selection.unit in unitItems && selection.quantity in quantitiesFor(selection.unit)

    fun coerceSelection(selection: QuantityUnitSelection): QuantityUnitSelection {
        if (selection.unit in unitItems) {
            return closestSelectionForUnit(
                normalizedGrams = selection.normalizedGrams,
                unit = selection.unit
            )
        }

        return unitItems
            .flatMapIndexed { unitIndex, unit ->
                quantitiesFor(unit).map { quantity ->
                    IndexedSelection(
                        unitIndex = unitIndex,
                        selection = QuantityUnitSelection(quantity = quantity, unit = unit)
                    )
                }
            }
            .minWith(
                compareBy<IndexedSelection> {
                    abs(it.selection.normalizedGrams - selection.normalizedGrams)
                }
                    .thenBy { it.selection.normalizedGrams }
                    .thenBy { it.unitIndex }
                    .thenBy { it.selection.quantity }
            )
            .selection
    }

    fun repairedSelectionAfterUnit(
        currentSelection: QuantityUnitSelection,
        unit: QuantityUnit
    ): QuantityUnitSelection {
        if (unit !in unitItems) return currentSelection
        return closestSelectionForUnit(
            normalizedGrams = currentSelection.normalizedGrams,
            unit = unit
        )
    }

    fun repairedSelectionAfterQuantity(
        currentSelection: QuantityUnitSelection,
        quantity: Int
    ): QuantityUnitSelection {
        if (quantity !in quantitiesFor(currentSelection.unit)) return currentSelection
        return QuantityUnitSelection(quantity = quantity, unit = currentSelection.unit)
    }

    private fun closestSelectionForUnit(
        normalizedGrams: Long,
        unit: QuantityUnit
    ): QuantityUnitSelection = quantitiesFor(unit)
        .map { quantity -> QuantityUnitSelection(quantity = quantity, unit = unit) }
        .minWith(
            compareBy<QuantityUnitSelection> {
                abs(it.normalizedGrams - normalizedGrams)
            }
                .thenBy { it.normalizedGrams }
                .thenBy { it.quantity }
        )
}

internal val DefaultQuantityUnitItems = QuantityUnitItems(
    unitItems = listOf(QuantityUnit.GRAM, QuantityUnit.KILOGRAM),
    quantityItems = mapOf(
        QuantityUnit.GRAM to (100..5_000 step 100).toList(),
        QuantityUnit.KILOGRAM to (1..5).toList()
    )
)

internal enum class QuantityUnitColumn {
    QUANTITY,
    UNIT
}

internal data class QuantityUnitPickerLayout(
    val columnOrder: List<QuantityUnitColumn> = listOf(
        QuantityUnitColumn.QUANTITY,
        QuantityUnitColumn.UNIT
    )
) {
    init {
        require(
            columnOrder.size == QuantityUnitColumn.entries.size &&
                    columnOrder.toSet() == QuantityUnitColumn.entries.toSet()
        ) {
            "QuantityUnitPickerLayout.columnOrder must contain each column exactly once, " +
                    "but was $columnOrder."
        }
    }
}

@Composable
internal fun rememberQuantityUnitPickerState(
    items: QuantityUnitItems,
    initialSelection: QuantityUnitSelection
): QuantityUnitPickerState {
    val rememberedItems = remember { items }
    val rememberedInitialSelection = remember { initialSelection }
    val saver = remember(rememberedItems) { quantityUnitPickerStateSaver(rememberedItems) }
    return rememberSaveable(saver = saver) {
        QuantityUnitPickerState(
            initialSelection = rememberedItems.coerceSelection(rememberedInitialSelection)
        )
    }
}

@Stable
internal class QuantityUnitPickerState(
    initialSelection: QuantityUnitSelection
) {
    private var mutableSelectedSelection: QuantityUnitSelection by mutableStateOf(initialSelection)

    val selectedSelection: QuantityUnitSelection
        get() = mutableSelectedSelection

    val selectedQuantity: Int
        get() = selectedSelection.quantity

    val selectedUnit: QuantityUnit
        get() = selectedSelection.unit

    fun selectSelection(selection: QuantityUnitSelection) {
        mutableSelectedSelection = selection
    }

    fun selectSelection(selection: QuantityUnitSelection, items: QuantityUnitItems) {
        selectSelection(items.coerceSelection(selection))
    }

    companion object {
        val Saver: Saver<QuantityUnitPickerState, Any> = listSaver(
            save = {
                listOf(
                    it.selectedQuantity,
                    it.selectedUnit.name
                )
            },
            restore = {
                QuantityUnitPickerState(
                    initialSelection = QuantityUnitSelection(
                        quantity = it[0] as Int,
                        unit = QuantityUnit.valueOf(it[1] as String)
                    )
                )
            }
        )
    }
}

internal fun quantityUnitPickerStateSaver(
    items: QuantityUnitItems
): Saver<QuantityUnitPickerState, Any> = listSaver(
    save = {
        listOf(
            it.selectedQuantity,
            it.selectedUnit.name
        )
    },
    restore = {
        QuantityUnitPickerState(
            initialSelection = items.coerceSelection(
                QuantityUnitSelection(
                    quantity = it[0] as Int,
                    unit = QuantityUnit.valueOf(it[1] as String)
                )
            )
        )
    }
)

internal fun commitQuantityUnitSelection(
    state: QuantityUnitPickerState,
    nextSelection: QuantityUnitSelection,
    onSelectionCommitted: (QuantityUnitSelection) -> Unit
): Boolean {
    if (nextSelection == state.selectedSelection) return false
    state.selectSelection(nextSelection)
    onSelectionCommitted(nextSelection)
    return true
}

private data class IndexedSelection(
    val unitIndex: Int,
    val selection: QuantityUnitSelection
)
