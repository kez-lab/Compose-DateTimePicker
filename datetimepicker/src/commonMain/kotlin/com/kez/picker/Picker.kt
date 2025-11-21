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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
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

/**
 * A generic picker component that displays a list of items and allows the user to select one.
 *
 * @param items The list of items to display.
 * @param modifier The modifier to be applied to the picker.
 * @param state The state of the picker.
 * @param startIndex The initial index to display.
 * @param visibleItemsCount The number of items visible at once.
 * @param textStyle The style of the text for unselected items.
 * @param selectedTextStyle The style of the text for the selected item.
 * @param dividerColor The color of the dividers.
 * @param selectedItemBackgroundColor The background color of the selected item area.
 * @param selectedItemBackgroundShape The shape of the background of the selected item area.
 * @param itemPadding The padding around each item.
 * @param fadingEdgeGradient The gradient to use for fading edges.
 * @param horizontalAlignment The horizontal alignment of items.
 * @param itemTextAlignment The vertical alignment of the text within items.
 * @param dividerThickness The thickness of the dividers.
 * @param dividerShape The shape of the dividers.
 * @param isDividerVisible Whether the divider should be visible.
 * @param isInfinity Whether the picker should loop infinitely.
 */
@Composable
fun <T> Picker(
    items: List<T>,
    state: PickerState<T>,
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    visibleItemsCount: Int = 3,
    textStyle: TextStyle = LocalTextStyle.current,
    selectedTextStyle: TextStyle = LocalTextStyle.current,
    dividerColor: Color = LocalContentColor.current,
    selectedItemBackgroundColor: Color = Color.Transparent,
    selectedItemBackgroundShape: Shape = RoundedCornerShape(12.dp),
    itemPadding: PaddingValues = PaddingValues(8.dp),
    fadingEdgeGradient: Brush = Brush.verticalGradient(
        0f to Color.Transparent,
        0.5f to Color.Black,
        1f to Color.Transparent
    ),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    itemTextAlignment: Alignment.Vertical = Alignment.CenterVertically,
    dividerThickness: Dp = 1.dp,
    dividerShape: Shape = RoundedCornerShape(10.dp),
    isDividerVisible: Boolean = true,
    isInfinity: Boolean = true,
    content: @Composable ((T) -> Unit)? = null
) {
    val density = LocalDensity.current
    val visibleItemsMiddle = remember { visibleItemsCount / 2 }
    val scope = rememberCoroutineScope()

    val adjustedItems = if (!isInfinity) {
        listOf(null) + items + listOf(null)
    } else {
        items
    }

    val listScrollCount = if (isInfinity) {
        Int.MAX_VALUE
    } else {
        adjustedItems.size
    }

    val listScrollMiddle = remember { listScrollCount / 2 }
    val listStartIndex = remember {
        if (isInfinity) {
            listScrollMiddle - listScrollMiddle % adjustedItems.size - visibleItemsMiddle + startIndex
        } else {
            startIndex + 1
        }
    }

    fun getItem(index: Int) = adjustedItems[index % adjustedItems.size]

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = listStartIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val itemHeight = with(density) {
        selectedTextStyle.fontSize
            .toPx()
            .toDp()
            .plus(itemPadding.calculateTopPadding())
            .plus(itemPadding.calculateBottomPadding())
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .mapNotNull { index -> getItem(index + visibleItemsMiddle) }
            .distinctUntilChanged()
            .collect { item -> state.selectedItem = item }
    }

    Box(modifier = modifier) {
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
                HorizontalDivider(
                    color = dividerColor,
                    thickness = dividerThickness,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = dividerColor,
                            shape = dividerShape
                        )
                        .align(Alignment.TopCenter)
                )

                HorizontalDivider(
                    color = dividerColor,
                    thickness = dividerThickness,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = dividerColor,
                            shape = dividerShape
                        )
                        .align(Alignment.BottomCenter)
                )
            }
        }
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
                
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .clickable(
                            enabled = item != null,
                            role = Role.Button,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                val currentCenterIndex = listState.firstVisibleItemIndex + visibleItemsMiddle
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
                                text = item.toString(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = textStyle.copy(
                                    fontSize = lerp(
                                        selectedTextStyle.fontSize,
                                        textStyle.fontSize,
                                        fraction
                                    ),
                                    color = lerp(
                                        selectedTextStyle.color,
                                        textStyle.color,
                                        fraction
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
        state = state,
        textStyle = TextStyle(fontSize = 16.sp),
        selectedTextStyle = TextStyle(fontSize = 24.sp)
    )
}

@Preview(name = "Long Text Items", group = "Picker - Variations", showBackground = true)
@Composable
fun PickerLongTextPreview() {
    val state = rememberPickerState("Long Item 1")
    Picker(
        items = listOf("Long Item 1", "Long Item 2", "Long Item 3"),
        state = state,
        textStyle = TextStyle(fontSize = 16.sp),
        selectedTextStyle = TextStyle(fontSize = 24.sp)
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
        startIndex = 49,
        textStyle = TextStyle(fontSize = 16.sp),
        selectedTextStyle = TextStyle(fontSize = 24.sp)
    )
}

@Preview(name = "No Divider", group = "Picker - Variations", showBackground = true)
@Composable
fun PickerNoDividerPreview() {
    val state = rememberPickerState("Item 2")
    Picker(
        items = listOf("Item 1", "Item 2", "Item 3"),
        state = state,
        isDividerVisible = false,
        textStyle = TextStyle(fontSize = 16.sp),
        selectedTextStyle = TextStyle(fontSize = 24.sp)
    )
}

@Preview(name = "Custom Colors", group = "Picker - Styles", showBackground = true)
@Composable
fun PickerCustomColorsPreview() {
    val state = rememberPickerState("Blue")
    Picker(
        items = listOf("Red", "Green", "Blue"),
        state = state,
        textStyle = TextStyle(fontSize = 16.sp, color = Color.Gray),
        selectedTextStyle = TextStyle(fontSize = 24.sp, color = Color.Blue),
        dividerColor = Color.Blue
    )
}

@Preview(name = "Bounded Scroll", group = "Picker - Variations", showBackground = true)
@Composable
fun PickerBoundedPreview() {
    val state = rememberPickerState("Item 2")
    Picker(
        items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5"),
        state = state,
        isInfinity = false,
        textStyle = TextStyle(fontSize = 16.sp),
        selectedTextStyle = TextStyle(fontSize = 24.sp)
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
        visibleItemsCount = 5,
        textStyle = TextStyle(fontSize = 16.sp),
        selectedTextStyle = TextStyle(fontSize = 24.sp)
    )
}

@Preview(name = "Selected Background", group = "Picker - Styles", showBackground = true)
@Composable
fun PickerSelectedBackgroundPreview() {
    val state = rememberPickerState("Item 2")
    Picker(
        items = listOf("Item 1", "Item 2", "Item 3"),
        state = state,
        textStyle = TextStyle(fontSize = 16.sp, color = Color.Gray),
        selectedTextStyle = TextStyle(fontSize = 24.sp, color = Color.Blue),
        dividerColor = Color.Blue,
        selectedItemBackgroundColor = Color.Blue.copy(alpha = 0.1f)
    )
}