package com.kez.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

/**
 * Multiplier for infinite scroll list size.
 * Uses a reasonable multiplier instead of Int.MAX_VALUE to prevent memory issues
 * while still providing a virtually infinite scrolling experience.
 */
private const val INFINITE_SCROLL_MULTIPLIER = 1000

private val ItemHeightProbeTexts = listOf(
    "0123456789",
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",
    "가나다라마바사아자차카타파하",
    "년월일"
)

private val ItemHeightSafetyPadding = 4.dp

/**
 * Information passed to custom [Picker] item content.
 *
 * @param T The picker item type.
 * @property item The item value represented by this row.
 * @property text The visible text produced by [PickerItemFormat] for this item.
 * @property isSelected Whether this item is the currently selected item.
 * @property isEnabled Whether the picker currently allows user interaction.
 * @property distanceFraction A value from `0f` to `1f` where `0f` is the centered selected row and
 * `1f` is the outer visible edge.
 * @property textStyle The default interpolated text style for this item.
 * @property contentColor The default interpolated content color for this item.
 */
class PickerItemScope<T : Any> internal constructor(
    val item: T,
    val text: String,
    val isSelected: Boolean,
    val isEnabled: Boolean,
    val distanceFraction: Float,
    val textStyle: TextStyle,
    val contentColor: Color
)

@Composable
internal fun <T : Any> rememberPickerItemHeight(
    items: List<T>,
    format: PickerItemFormat<T>,
    style: PickerStyle
): Dp {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer(cacheSize = 16)
    val itemPadding = style.itemPadding
    val textStyle = style.textStyles.textStyle
    val selectedTextStyle = style.textStyles.selectedTextStyle
    val measuredTextHeight = remember(items, format, textStyle, selectedTextStyle, textMeasurer) {
        val itemTexts = items.map(format.itemText)
        (itemTexts + ItemHeightProbeTexts).maxOf { itemText ->
            val text = AnnotatedString(itemText)
            max(
                textMeasurer.measure(
                    text = text,
                    style = textStyle,
                    overflow = TextOverflow.Clip,
                    softWrap = false,
                    maxLines = 1
                ).size.height,
                textMeasurer.measure(
                    text = text,
                    style = selectedTextStyle,
                    overflow = TextOverflow.Clip,
                    softWrap = false,
                    maxLines = 1
                ).size.height
            )
        }
    }

    return with(density) {
        measuredTextHeight.toDp() +
            itemPadding.calculateTopPadding() +
            itemPadding.calculateBottomPadding() +
            ItemHeightSafetyPadding
    }
}

/**
 * A generic picker component that displays a list of items and allows the user to select one.
 * Follows Material3 component design patterns.
 *
 * @param items The list of items to display. Treat this list as immutable while the picker is
 * composed; create and pass a new list when available values change.
 * @param selectedItem The currently selected item. It must exist in [items].
 * @param onSelectedItemChange Called when scroll or click interaction selects a new item.
 * @param modifier The modifier to be applied to the picker.
 * @param enabled Whether user scroll, click, and semantics selection actions are enabled.
 * @param format Visible item text and optional accessibility value descriptions. When [content] is
 * provided, visible text is still exposed through [PickerItemScope.text].
 * @param style Visual and layout styling for the picker.
 * @param semantics Accessibility label and custom action labels for the picker.
 * @param isInfinity Whether the picker should loop infinitely.
 * @param content Optional custom content composable for rendering each item.
 */
@Composable
fun <T : Any> Picker(
    items: List<T>,
    selectedItem: T,
    onSelectedItemChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    format: PickerItemFormat<T> = PickerDefaults.itemFormat(),
    style: PickerStyle = PickerDefaults.style(),
    semantics: PickerSemantics = PickerDefaults.semantics(),
    isInfinity: Boolean = true,
    content: @Composable ((PickerItemScope<T>) -> Unit)? = null
) {
    val visibleItemsCount = style.visibleItemsCount
    remember(items, selectedItem, visibleItemsCount) {
        validatePickerInput(
            items = items,
            selectedItem = selectedItem,
            visibleItemsCount = visibleItemsCount
        )
    }

    val density = LocalDensity.current
    val visibleItemsMiddle = remember(visibleItemsCount) { visibleItemsCount / 2 }
    val scope = rememberCoroutineScope()
    val currentOnSelectedItemChange by rememberUpdatedState(onSelectedItemChange)
    val currentSelectedItem by rememberUpdatedState(selectedItem)
    val selectedItemIndex = items.indexOf(selectedItem)
    val colors = style.colors
    val textStyles = style.textStyles
    val selectedItemBackgroundShape = style.selectedItemBackgroundShape
    val itemPadding = style.itemPadding
    val fadingEdgeGradient = style.fadingEdgeGradient
    val horizontalAlignment = style.horizontalAlignment
    val dividerThickness = style.dividerThickness
    val dividerShape = style.dividerShape
    val dividerWidth = style.dividerWidth
    val isDividerVisible = style.isDividerVisible
    val itemHeight = rememberPickerItemHeight(
        items = items,
        format = format,
        style = style
    )
    val itemHeightPx = with(density) { itemHeight.toPx() }

    val adjustedItems: List<T?> = remember(items, isInfinity, visibleItemsMiddle) {
        if (!isInfinity) {
            List(visibleItemsMiddle) { null } + items + List(visibleItemsMiddle) { null }
        } else {
            items
        }
    }

    val listScrollCount = if (isInfinity) {
        adjustedItems.size * INFINITE_SCROLL_MULTIPLIER
    } else {
        adjustedItems.size
    }

    val listScrollMiddle = remember(listScrollCount) { listScrollCount / 2 }
    val listStartIndex =
        remember(listScrollCount, adjustedItems.size, visibleItemsMiddle, selectedItemIndex) {
            if (isInfinity) {
                listScrollMiddle - listScrollMiddle % adjustedItems.size - visibleItemsMiddle + selectedItemIndex
            } else {
                selectedItemIndex
            }
        }

    fun getItem(index: Int): T? {
        if (adjustedItems.isEmpty()) return null
        return if (isInfinity) {
            val safeIndex = index.mod(adjustedItems.size)
            adjustedItems.getOrNull(safeIndex)
        } else {
            adjustedItems.getOrNull(index)
        }
    }

    val listState = key(items, isInfinity, visibleItemsCount, listScrollCount) {
        rememberLazyListState(initialFirstVisibleItemIndex = listStartIndex)
    }
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val textStyle = textStyles.textStyle
    val selectedTextStyle = textStyles.selectedTextStyle
    val dividerColor = if (enabled) colors.dividerColor else colors.disabledDividerColor
    val selectedItemBackgroundColor = if (enabled) {
        colors.selectedItemBackgroundColor
    } else {
        colors.disabledSelectedItemBackgroundColor
    }
    val textColor = if (enabled) colors.textColor else colors.disabledTextColor
    val selectedTextColor = if (enabled) {
        colors.selectedTextColor
    } else {
        colors.disabledSelectedTextColor
    }

    LaunchedEffect(listState, adjustedItems, visibleItemsMiddle, enabled) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.isScrollInProgress }
            .mapNotNull { (index, isScrollInProgress) ->
                getItem(index + visibleItemsMiddle)?.let { item -> item to isScrollInProgress }
            }
            .distinctUntilChanged()
            .collect { (item, isScrollInProgress) ->
                if (enabled && !isScrollInProgress && item != currentSelectedItem) {
                    currentOnSelectedItemChange(item)
                }
            }
    }

    LaunchedEffect(
        listState,
        items,
        visibleItemsMiddle,
        visibleItemsCount,
        isInfinity,
        listScrollCount,
        selectedItem
    ) {
        val currentCenterIndex = listState.firstVisibleItemIndex + visibleItemsMiddle
        val currentCenteredItem = getItem(currentCenterIndex)
        if (currentCenteredItem == selectedItem) {
            return@LaunchedEffect
        }

        val maxFirstVisibleIndex = (listScrollCount - visibleItemsCount).coerceAtLeast(0)
        val targetFirstVisibleIndex = if (isInfinity) {
            val itemCount = items.size
            val currentCycleStart = currentCenterIndex - currentCenterIndex.mod(itemCount)
            val targetFirstVisibleIndex = listOf(
                currentCycleStart - itemCount + selectedItemIndex,
                currentCycleStart + selectedItemIndex,
                currentCycleStart + itemCount + selectedItemIndex
            )
                .map { targetCenterIndex -> targetCenterIndex - visibleItemsMiddle }
                .filter { it in 0..maxFirstVisibleIndex }
                .minByOrNull { abs((it + visibleItemsMiddle) - currentCenterIndex) }

            targetFirstVisibleIndex
                ?: (listScrollMiddle - listScrollMiddle % itemCount - visibleItemsMiddle + selectedItemIndex)
        } else {
            selectedItemIndex
        }.coerceIn(0, maxFirstVisibleIndex)

        if (targetFirstVisibleIndex != listState.firstVisibleItemIndex) {
            listState.scrollToItem(targetFirstVisibleIndex)
        }
    }

    val normalizedPickerLabel = semantics.pickerLabel.asPickerSemanticsLabelOrNull()
    val normalizedPreviousItemActionLabel =
        semantics.previousItemActionLabel.asPickerSemanticsLabelOrNull()
    val normalizedNextItemActionLabel =
        semantics.nextItemActionLabel.asPickerSemanticsLabelOrNull()
    val selectedItemText = format.itemText(selectedItem)
    val selectedItemDescription = format.itemContentDescription?.invoke(selectedItem) ?: selectedItemText
    val pickerDescription = pickerSemanticsDescription(normalizedPickerLabel, selectedItemDescription)
    val hasPickerDescription = pickerDescription.isNotBlank()

    fun adjacentFirstVisibleItemIndex(offset: Int): Int? {
        if (items.size <= 1) return null

        val maxFirstVisibleIndex = (listScrollCount - visibleItemsCount).coerceAtLeast(0)
        val targetFirstVisibleIndex = listState.firstVisibleItemIndex + offset

        return if (isInfinity) {
            when {
                targetFirstVisibleIndex < 0 -> maxFirstVisibleIndex
                targetFirstVisibleIndex > maxFirstVisibleIndex -> 0
                else -> targetFirstVisibleIndex
            }
        } else {
            targetFirstVisibleIndex.takeIf { it in 0..maxFirstVisibleIndex }
        }
    }

    fun selectAdjacentItem(offset: Int): Boolean {
        val targetFirstVisibleIndex = adjacentFirstVisibleItemIndex(offset) ?: return false

        scope.launch {
            listState.animateScrollToItem(targetFirstVisibleIndex)
        }
        return true
    }

    fun fallbackDistanceFractionFor(index: Int): Float {
        val centerIndex = listState.firstVisibleItemIndex + visibleItemsMiddle
        val itemDistance = (index - centerIndex).coerceIn(-1, 1)
        return abs(itemDistance).toFloat()
    }

    val semanticsActions = buildList {
        if (enabled && normalizedPreviousItemActionLabel != null && adjacentFirstVisibleItemIndex(offset = -1) != null) {
            add(
                CustomAccessibilityAction(
                    label = normalizedPreviousItemActionLabel,
                    action = { selectAdjacentItem(offset = -1) }
                )
            )
        }
        if (enabled && normalizedNextItemActionLabel != null && adjacentFirstVisibleItemIndex(offset = 1) != null) {
            add(
                CustomAccessibilityAction(
                    label = normalizedNextItemActionLabel,
                    action = { selectAdjacentItem(offset = 1) }
                )
            )
        }
    }

    Box(
        modifier = modifier.semantics {
            // Provide picker-level semantics information
            if (hasPickerDescription) {
                contentDescription = pickerDescription
                stateDescription = selectedItemDescription
                liveRegion = LiveRegionMode.Polite
            }
            if (!enabled) {
                disabled()
            }
            collectionInfo = CollectionInfo(rowCount = items.size, columnCount = 1)
            if (hasPickerDescription && semanticsActions.isNotEmpty()) {
                customActions = semanticsActions
            }
        }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    color = selectedItemBackgroundColor,
                    shape = selectedItemBackgroundShape
                )
                .fillMaxWidth()
                .height(itemHeight)
        ) {
            if (isDividerVisible) {
                PickerDivider(
                    width = dividerWidth,
                    color = dividerColor,
                    thickness = dividerThickness,
                    shape = dividerShape,
                    alignment = Alignment.TopCenter
                )

                PickerDivider(
                    width = dividerWidth,
                    color = dividerColor,
                    thickness = dividerThickness,
                    shape = dividerShape,
                    alignment = Alignment.BottomCenter
                )
            }
        }

        val sharedInteractionSource = remember { MutableInteractionSource() }

        key(adjustedItems, listScrollCount) {
            LazyColumn(
                state = listState,
                flingBehavior = flingBehavior,
                userScrollEnabled = enabled,
                horizontalAlignment = horizontalAlignment,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(itemHeight * visibleItemsCount)
                    .fadingEdge(fadingEdgeGradient)
            ) {
                items(
                    listScrollCount,
                    key = { it },
                ) { index ->
                    val item = getItem(index)
                    val isSelected = item == selectedItem
                    val fraction by remember(listState, index, itemHeightPx, visibleItemsMiddle) {
                        derivedStateOf {
                            val currentItem =
                                listState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == index }
                            currentItem?.offset?.let { offset ->
                                if (itemHeightPx <= 0f) {
                                    fallbackDistanceFractionFor(index)
                                } else {
                                    val fraction =
                                        (offset - itemHeightPx * visibleItemsMiddle) / itemHeightPx
                                    abs(fraction.coerceIn(-1f, 1f))
                                }
                            } ?: fallbackDistanceFractionFor(index)
                        }
                    }

                    val itemText = item?.let(format.itemText) ?: ""
                    val itemDescription = item?.let { value ->
                        format.itemContentDescription?.invoke(value) ?: itemText
                    } ?: ""
                    val itemContentColor = lerp(
                        start = selectedTextColor,
                        stop = textColor,
                        fraction = fraction
                    )
                    val itemTextStyle = textStyle.copy(
                        fontSize = lerp(
                            start = selectedTextStyle.fontSize,
                            stop = textStyle.fontSize,
                            fraction
                        ),
                        color = itemContentColor,
                    )
                    val itemIndex = if (isInfinity) {
                        index % items.size
                    } else {
                        (index - visibleItemsMiddle).coerceAtLeast(0)
                    }

                    Box(
                        modifier = Modifier
                            .height(itemHeight)
                            .fillMaxWidth()
                            .semantics {
                                if (item != null) {
                                    role = Role.Button
                                    // Enhanced content description with picker context
                                    contentDescription =
                                        pickerSemanticsDescription(normalizedPickerLabel, itemDescription)
                                    selected = isSelected
                                    collectionItemInfo = CollectionItemInfo(
                                        rowIndex = itemIndex,
                                        rowSpan = 1,
                                        columnIndex = 0,
                                        columnSpan = 1
                                    )
                                }
                            }
                            .clickable(
                                enabled = enabled && item != null,
                                role = Role.Button,
                                indication = null,
                                interactionSource = sharedInteractionSource,
                                onClick = {
                                    val currentCenterIndex =
                                        listState.firstVisibleItemIndex + visibleItemsMiddle
                                    if (index != currentCenterIndex) {
                                        scope.launch {
                                            val targetIndex = (index - visibleItemsMiddle)
                                                .coerceIn(0, listScrollCount - 1)
                                            listState.animateScrollToItem(targetIndex)
                                        }
                                    }
                                }
                            )
                            .padding(itemPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item != null) {
                            if (content != null) {
                                content(
                                    PickerItemScope(
                                        item = item,
                                        text = itemText,
                                        isSelected = isSelected,
                                        isEnabled = enabled,
                                        distanceFraction = fraction,
                                        textStyle = itemTextStyle,
                                        contentColor = itemContentColor
                                    )
                                )
                            } else {
                                Text(
                                    text = itemText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = itemTextStyle,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Renders a single selection divider line for [Picker].
 *
 * Uses a plain [Box] rather than [androidx.compose.material3.HorizontalDivider] because the latter
 * forces `fillMaxWidth`, which would prevent [PickerDividerWidth.Fraction] and
 * [PickerDividerWidth.Fixed] from controlling the divider width.
 */
@Composable
private fun BoxScope.PickerDivider(
    width: PickerDividerWidth,
    color: Color,
    thickness: Dp,
    shape: Shape,
    alignment: Alignment
) {
    val widthModifier = when (width) {
        PickerDividerWidth.Fill -> Modifier.fillMaxWidth()
        is PickerDividerWidth.Fraction -> Modifier.fillMaxWidth(width.fraction)
        is PickerDividerWidth.Fixed -> Modifier.width(width.width)
    }
    Box(
        modifier = Modifier
            .then(widthModifier)
            .height(thickness)
            .background(color = color, shape = shape)
            .align(alignment)
    )
}

internal fun <T : Any> validatePickerInput(
    items: List<T>,
    selectedItem: T,
    visibleItemsCount: Int
) {
    require(items.isNotEmpty()) {
        "Picker items must not be empty. Provide at least one item and keep selectedItem from that list."
    }
    require(items.distinct().size == items.size) {
        "Picker items must not contain duplicate values because Picker uses item equality as its " +
                "selection key. Use stable unique item values or pass unique IDs as items when " +
                "display labels repeat."
    }
    require(selectedItem in items) {
        "selectedItem must exist in items. Picker<T> is controlled, so update the app-owned " +
                "selectedItem when items change or coerce it to one of items before composing " +
                "Picker. selectedItem=$selectedItem, items=$items"
    }
    require(visibleItemsCount > 0 && visibleItemsCount % 2 == 1) {
        "PickerDefaults.style(visibleItemsCount = ...) must use a positive odd number, but was " +
                "$visibleItemsCount."
    }
}

private fun String?.asPickerSemanticsLabelOrNull(): String? =
    this?.trim()?.takeIf { it.isNotEmpty() }

internal fun pickerSemanticsDescription(label: String?, value: String): String {
    val normalizedLabel = label.asPickerSemanticsLabelOrNull()
    return when {
        normalizedLabel != null && value.isNotBlank() -> "$normalizedLabel: $value"
        normalizedLabel != null -> normalizedLabel
        else -> value
    }
}

/**
 * Apply a fading edge effect to a modifier.
 *
 * @param brush The gradient brush to use for the fading effect.
 * @return The modified modifier with the fading edge effect.
 */
private fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Preview(name = "Basic Picker", group = "Picker - Basic", showBackground = true)
@Composable
private fun PickerPreview() {
    val items = listOf("1", "2", "3")
    var selectedItem by remember { mutableStateOf("1") }
    Picker(
        items = items,
        selectedItem = selectedItem,
        onSelectedItemChange = { selectedItem = it }
    )
}

@Preview(name = "Long Text Items", group = "Picker - Variations", showBackground = true)
@Composable
private fun PickerLongTextPreview() {
    val items = listOf("Long Item 1", "Long Item 2", "Long Item 3")
    var selectedItem by remember { mutableStateOf("Long Item 1") }
    Picker(
        items = items,
        selectedItem = selectedItem,
        onSelectedItemChange = { selectedItem = it }
    )
}

@Preview(name = "Many Items", group = "Picker - Variations", showBackground = true)
@Composable
private fun PickerManyItemsPreview() {
    val items = (1..100).map { "Item $it" }
    var selectedItem by remember { mutableStateOf("Item 50") }
    Picker(
        items = items,
        selectedItem = selectedItem,
        onSelectedItemChange = { selectedItem = it }
    )
}

@Preview(name = "No Divider", group = "Picker - Variations", showBackground = true)
@Composable
private fun PickerNoDividerPreview() {
    val items = listOf("Item 1", "Item 2", "Item 3")
    var selectedItem by remember { mutableStateOf("Item 2") }
    Picker(
        items = items,
        selectedItem = selectedItem,
        onSelectedItemChange = { selectedItem = it },
        style = PickerDefaults.style(isDividerVisible = false)
    )
}

@Preview(name = "Custom Colors", group = "Picker - Styles", showBackground = true)
@Composable
private fun PickerCustomColorsPreview() {
    val items = listOf("Red", "Green", "Blue")
    var selectedItem by remember { mutableStateOf("Blue") }
    Picker(
        items = items,
        selectedItem = selectedItem,
        onSelectedItemChange = { selectedItem = it },
        style = PickerDefaults.style(
            colors = PickerDefaults.colors(
                dividerColor = androidx.compose.ui.graphics.Color.Blue,
                selectedTextColor = androidx.compose.ui.graphics.Color.Blue,
                textColor = androidx.compose.ui.graphics.Color.Gray
            )
        )
    )
}

@Preview(name = "Bounded Scroll", group = "Picker - Variations", showBackground = true)
@Composable
private fun PickerBoundedPreview() {
    val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")
    var selectedItem by remember { mutableStateOf("Item 2") }
    Picker(
        items = items,
        selectedItem = selectedItem,
        onSelectedItemChange = { selectedItem = it },
        isInfinity = false
    )
}

@Preview(name = "5 Visible Items", group = "Picker - Styles", showBackground = true)
@Composable
private fun Picker5VisibleItemsPreview() {
    val items = (1..10).map { "Item $it" }
    var selectedItem by remember { mutableStateOf("Item 5") }
    Picker(
        items = items,
        selectedItem = selectedItem,
        onSelectedItemChange = { selectedItem = it },
        style = PickerDefaults.style(visibleItemsCount = 5)
    )
}

@Preview(name = "Fraction Divider Width", group = "Picker - Styles", showBackground = true)
@Composable
private fun PickerFractionDividerWidthPreview() {
    val items = listOf("Item 1", "Item 2", "Item 3")
    var selectedItem by remember { mutableStateOf("Item 2") }
    Picker(
        items = items,
        selectedItem = selectedItem,
        onSelectedItemChange = { selectedItem = it },
        style = PickerDefaults.style(dividerWidth = PickerDividerWidth.Fraction(0.6f))
    )
}

@Preview(name = "Fixed Divider Width", group = "Picker - Styles", showBackground = true)
@Composable
private fun PickerFixedDividerWidthPreview() {
    val items = listOf("Item 1", "Item 2", "Item 3")
    var selectedItem by remember { mutableStateOf("Item 2") }
    Picker(
        items = items,
        selectedItem = selectedItem,
        onSelectedItemChange = { selectedItem = it },
        style = PickerDefaults.style(dividerWidth = PickerDividerWidth.Fixed(48.dp))
    )
}

@Preview(name = "Selected Background", group = "Picker - Styles", showBackground = true)
@Composable
private fun PickerSelectedBackgroundPreview() {
    val items = listOf("Item 1", "Item 2", "Item 3")
    var selectedItem by remember { mutableStateOf("Item 2") }
    Picker(
        items = items,
        selectedItem = selectedItem,
        onSelectedItemChange = { selectedItem = it },
        style = PickerDefaults.style(
            colors = PickerDefaults.colors(
                dividerColor = androidx.compose.ui.graphics.Color.Blue,
                selectedTextColor = androidx.compose.ui.graphics.Color.Blue,
                textColor = androidx.compose.ui.graphics.Color.Gray,
                selectedItemBackgroundColor = androidx.compose.ui.graphics.Color.Blue.copy(alpha = 0.1f)
            )
        )
    )
}
