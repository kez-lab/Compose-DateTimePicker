package com.kez.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
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
    modifier: Modifier = Modifier,
    state: PickerState<T>,
    startIndex: Int = 0,
    visibleItemsCount: Int = 3,
    textStyle: TextStyle = LocalTextStyle.current,
    selectedTextStyle: TextStyle = LocalTextStyle.current,
    dividerColor: Color = LocalContentColor.current,
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
    isInfinity: Boolean = true
) {
    val density = LocalDensity.current
    val visibleItemsMiddle = remember { visibleItemsCount / 2 }

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
        selectedTextStyle.fontSize.toDp() + itemPadding.calculateTopPadding() + itemPadding.calculateBottomPadding()
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .mapNotNull { index -> getItem(index + visibleItemsMiddle) }
            .distinctUntilChanged()
            .collect { item -> state.selectedItem = item }
    }

    Box(modifier = modifier) {
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

                val currentItemText by remember {
                    mutableStateOf(getItem(index)?.toString().orEmpty())
                }

                Text(
                    text = currentItemText,
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
                        )
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .height(itemHeight)
                        .wrapContentHeight(align = itemTextAlignment)
                        .fillMaxWidth()
                        .padding(itemPadding)
                )
            }
        }

        if (isDividerVisible) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(itemHeight)
            ) {
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