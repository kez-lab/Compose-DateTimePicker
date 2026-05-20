package com.kez.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Multiplier for infinite scroll list size.
 * Uses a reasonable multiplier instead of Int.MAX_VALUE to prevent memory issues
 * while still providing a virtually infinite scrolling experience.
 */
private const val INFINITE_SCROLL_MULTIPLIER = 1000

/**
 * A generic picker component that displays a list of items and allows the user to select one.
 * Follows Material3 component design patterns.
 *
 * @param items The list of items to display.
 * @param state The state of the picker.
 * @param modifier The modifier to be applied to the picker.
 * @param startIndex The initial index to display.
 * @param visibleItemsCount The number of items visible at once. Must be a positive odd number.
 * @param colors The colors used by the picker. See [PickerDefaults.colors].
 * @param textStyles The text styles used by the picker. See [PickerDefaults.textStyles].
 * @param selectedItemBackgroundShape The shape of the background of the selected item area.
 * @param itemPadding The padding around each item.
 * @param fadingEdgeGradient The gradient to use for fading edges.
 * @param horizontalAlignment The horizontal alignment of items.
 * @param verticalAlignment The vertical alignment of the text within items.
 * @param dividerThickness The thickness of the dividers.
 * @param dividerShape The shape of the dividers.
 * @param isDividerVisible Whether the divider should be visible.
 * @param isInfinity Whether the picker should loop infinitely.
 * @param pickerLabel Accessibility label for the picker (e.g., "Hour", "Minute", "Year").
 * @param itemContentDescription Accessibility description for each item value.
 * @param previousItemActionLabel Accessibility action label for selecting the previous item. Pass null or blank to omit the action.
 * @param nextItemActionLabel Accessibility action label for selecting the next item. Pass null or blank to omit the action.
 * @param content Optional custom content composable for rendering each item.
 */
@Composable
fun <T> Picker(
    items: List<T>,
    state: PickerState<T>,
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    visibleItemsCount: Int = PickerDefaults.VisibleItemsCount,
    colors: PickerColors = PickerDefaults.colors(),
    textStyles: PickerTextStyles = PickerDefaults.textStyles(),
    selectedItemBackgroundShape: Shape = PickerDefaults.SelectedItemBackgroundShape,
    itemPadding: PaddingValues = PickerDefaults.ItemPadding,
    fadingEdgeGradient: Brush = PickerDefaults.fadingEdgeGradient(),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    dividerThickness: Dp = PickerDefaults.DividerThickness,
    dividerShape: Shape = PickerDefaults.DividerShape,
    isDividerVisible: Boolean = true,
    isInfinity: Boolean = true,
    pickerLabel: String? = null,
    itemContentDescription: (T) -> String = { it.toString() },
    previousItemActionLabel: String? = PickerDefaults.PreviousItemActionLabel,
    nextItemActionLabel: String? = PickerDefaults.NextItemActionLabel,
    content: @Composable ((T) -> Unit)? = null
) {
    require(items.isNotEmpty()) { "Items list must not be empty" }
    require(visibleItemsCount > 0 && visibleItemsCount % 2 == 1) {
        "visibleItemsCount must be a positive odd number, but was $visibleItemsCount"
    }
    require(startIndex >= 0 && startIndex < items.size) {
        "startIndex must be in range [0, ${items.size}), but was $startIndex"
    }

    val density = LocalDensity.current
    val visibleItemsMiddle = remember(visibleItemsCount) { visibleItemsCount / 2 }
    val scope = rememberCoroutineScope()

    val adjustedItems = if (!isInfinity) {
        List(visibleItemsMiddle) { null } + items + List(visibleItemsMiddle) { null }
    } else {
        items
    }

    val listScrollCount = if (isInfinity) {
        adjustedItems.size * INFINITE_SCROLL_MULTIPLIER
    } else {
        adjustedItems.size
    }

    val listScrollMiddle = remember(listScrollCount) { listScrollCount / 2 }
    val listStartIndex =
        remember(listScrollCount, adjustedItems.size, visibleItemsMiddle, startIndex) {
            if (isInfinity) {
                listScrollMiddle - listScrollMiddle % adjustedItems.size - visibleItemsMiddle + startIndex
            } else {
                startIndex
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

    val listState = key(state, listStartIndex, listScrollCount) {
        rememberLazyListState(initialFirstVisibleItemIndex = listStartIndex)
    }
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val textStyle = textStyles.textStyle
    val selectedTextStyle = textStyles.selectedTextStyle

    val selectedLineHeight = selectedTextStyle.lineHeight.takeIf { it.isSpecified } ?: 0.sp
    val unselectedLineHeight = textStyle.lineHeight.takeIf { it.isSpecified } ?: 0.sp
    val largestLineHeight =
        if (selectedLineHeight > unselectedLineHeight) selectedLineHeight else unselectedLineHeight

    val selectedFontSize = selectedTextStyle.fontSize.takeIf { it.isSpecified } ?: 0.sp
    val unselectedFontSize = textStyle.fontSize.takeIf { it.isSpecified } ?: 0.sp
    val largestFontSize =
        if (selectedFontSize > unselectedFontSize) selectedFontSize else unselectedFontSize

    val textHeightSp = if (largestLineHeight > 0.sp) largestLineHeight else largestFontSize

    val itemHeight = with(density) {
        textHeightSp.toDp() +
                itemPadding.calculateTopPadding() +
                itemPadding.calculateBottomPadding()
    }

    LaunchedEffect(listState, adjustedItems, visibleItemsMiddle, state) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .mapNotNull { index -> getItem(index + visibleItemsMiddle) }
            .distinctUntilChanged()
            .collect { item -> state.updateSelectedItemFromScroll(item) }
    }

    LaunchedEffect(
        listState,
        items,
        visibleItemsMiddle,
        visibleItemsCount,
        isInfinity,
        listScrollCount,
        state.selectionRequestVersion
    ) {
        val selectionRequest = state.activeSelectionRequest ?: return@LaunchedEffect
        val requestedItem = selectionRequest.item

        val currentCenterIndex = listState.firstVisibleItemIndex + visibleItemsMiddle
        val currentCenteredItem = getItem(currentCenterIndex)
        if (currentCenteredItem == requestedItem) {
            currentCenteredItem?.let {
                state.completeSelectionRequest(selectionRequest.version, it)
            } ?: state.clearSelectionRequest(selectionRequest.version)
            return@LaunchedEffect
        }

        val selectedItemIndex = items.indexOf(requestedItem)
        if (selectedItemIndex < 0) {
            currentCenteredItem?.let {
                state.completeSelectionRequest(selectionRequest.version, it)
            } ?: state.clearSelectionRequest(selectionRequest.version)
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
        val settledCenteredItem = getItem(listState.firstVisibleItemIndex + visibleItemsMiddle)
        settledCenteredItem?.let {
            state.completeSelectionRequest(selectionRequest.version, it)
        } ?: state.clearSelectionRequest(selectionRequest.version)
    }

    val normalizedPickerLabel = pickerLabel.asPickerAccessibilityLabelOrNull()
    val normalizedPreviousItemActionLabel = previousItemActionLabel.asPickerAccessibilityLabelOrNull()
    val normalizedNextItemActionLabel = nextItemActionLabel.asPickerAccessibilityLabelOrNull()
    val selectedItemDescription = itemContentDescription(state.selectedItem)
    val pickerDescription = pickerAccessibilityDescription(normalizedPickerLabel, selectedItemDescription)
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

    val accessibilityActions = buildList {
        if (normalizedPreviousItemActionLabel != null && adjacentFirstVisibleItemIndex(offset = -1) != null) {
            add(
                CustomAccessibilityAction(
                    label = normalizedPreviousItemActionLabel,
                    action = { selectAdjacentItem(offset = -1) }
                )
            )
        }
        if (normalizedNextItemActionLabel != null && adjacentFirstVisibleItemIndex(offset = 1) != null) {
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
            // Provide picker-level accessibility information
            if (hasPickerDescription) {
                contentDescription = pickerDescription
                stateDescription = selectedItemDescription
                liveRegion = LiveRegionMode.Polite
            }
            collectionInfo = CollectionInfo(rowCount = items.size, columnCount = 1)
            if (hasPickerDescription && accessibilityActions.isNotEmpty()) {
                customActions = accessibilityActions
            }
        }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    color = colors.selectedItemBackgroundColor,
                    shape = selectedItemBackgroundShape
                )
                .fillMaxWidth()
                .height(itemHeight)
        ) {
            if (isDividerVisible) {
                HorizontalDivider(
                    color = colors.dividerColor,
                    thickness = dividerThickness,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = colors.dividerColor,
                            shape = dividerShape
                        )
                        .align(Alignment.TopCenter)
                )

                HorizontalDivider(
                    color = colors.dividerColor,
                    thickness = dividerThickness,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = colors.dividerColor,
                            shape = dividerShape
                        )
                        .align(Alignment.BottomCenter)
                )
            }
        }

        val sharedInteractionSource = remember { MutableInteractionSource() }

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = horizontalAlignment,
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize()
                .height(itemHeight * visibleItemsCount)
                .fadingEdge(fadingEdgeGradient)
        ) {
            items(
                listScrollCount,
                key = { it },
            ) { index ->
                val fraction by remember {
                    derivedStateOf {
                        val currentItem =
                            listState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == index }
                        currentItem?.offset?.let { offset ->
                            val itemHeightPx = with(density) { itemHeight.toPx() }
                            val fraction =
                                (offset - itemHeightPx * visibleItemsMiddle) / itemHeightPx
                            abs(fraction.coerceIn(-1f, 1f))
                        } ?: 0f
                    }
                }

                val item = getItem(index)
                val isSelected = item == state.selectedItem
                val itemText = item?.toString() ?: ""
                val itemDescription = item?.let(itemContentDescription) ?: ""
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
                                    pickerAccessibilityDescription(normalizedPickerLabel, itemDescription)
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
                            enabled = item != null,
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
                            content(item)
                        } else {
                            Text(
                                text = itemText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = textStyle.copy(
                                    fontSize = lerp(
                                        start = selectedTextStyle.fontSize,
                                        stop = textStyle.fontSize,
                                        fraction
                                    ),
                                    color = lerp(
                                        start = colors.selectedTextColor,
                                        stop = colors.textColor,
                                        fraction = fraction
                                    ),
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun String?.asPickerAccessibilityLabelOrNull(): String? =
    this?.trim()?.takeIf { it.isNotEmpty() }

internal fun pickerAccessibilityDescription(label: String?, value: String): String {
    val normalizedLabel = label.asPickerAccessibilityLabelOrNull()
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
fun PickerPreview() {
    val state = rememberPickerState("Item 1")
    Picker(
        items = listOf("1", "2", "3"),
        state = state
    )
}

@Preview(name = "Long Text Items", group = "Picker - Variations", showBackground = true)
@Composable
fun PickerLongTextPreview() {
    val state = rememberPickerState("Long Item 1")
    Picker(
        items = listOf("Long Item 1", "Long Item 2", "Long Item 3"),
        state = state
    )
}

@Preview(name = "Many Items", group = "Picker - Variations", showBackground = true)
@Composable
fun PickerManyItemsPreview() {
    val items = (1..100).map { "Item $it" }
    val state = rememberPickerState("Item 50")
    Picker(
        items = items,
        state = state,
        startIndex = 49
    )
}

@Preview(name = "No Divider", group = "Picker - Variations", showBackground = true)
@Composable
fun PickerNoDividerPreview() {
    val state = rememberPickerState("Item 2")
    Picker(
        items = listOf("Item 1", "Item 2", "Item 3"),
        state = state,
        isDividerVisible = false
    )
}

@Preview(name = "Custom Colors", group = "Picker - Styles", showBackground = true)
@Composable
fun PickerCustomColorsPreview() {
    val state = rememberPickerState("Blue")
    Picker(
        items = listOf("Red", "Green", "Blue"),
        state = state,
        colors = PickerDefaults.colors(
            dividerColor = androidx.compose.ui.graphics.Color.Blue,
            selectedTextColor = androidx.compose.ui.graphics.Color.Blue,
            textColor = androidx.compose.ui.graphics.Color.Gray
        )
    )
}

@Preview(name = "Bounded Scroll", group = "Picker - Variations", showBackground = true)
@Composable
fun PickerBoundedPreview() {
    val state = rememberPickerState("Item 2")
    Picker(
        items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5"),
        state = state,
        isInfinity = false
    )
}

@Preview(name = "5 Visible Items", group = "Picker - Styles", showBackground = true)
@Composable
fun Picker5VisibleItemsPreview() {
    val items = (1..10).map { "Item $it" }
    val state = rememberPickerState("Item 5")
    Picker(
        items = items,
        state = state,
        startIndex = 4,
        visibleItemsCount = 5
    )
}

@Preview(name = "Selected Background", group = "Picker - Styles", showBackground = true)
@Composable
fun PickerSelectedBackgroundPreview() {
    val state = rememberPickerState("Item 2")
    Picker(
        items = listOf("Item 1", "Item 2", "Item 3"),
        state = state,
        colors = PickerDefaults.colors(
            dividerColor = androidx.compose.ui.graphics.Color.Blue,
            selectedTextColor = androidx.compose.ui.graphics.Color.Blue,
            textColor = androidx.compose.ui.graphics.Color.Gray,
            selectedItemBackgroundColor = androidx.compose.ui.graphics.Color.Blue.copy(alpha = 0.1f)
        )
    )
}
