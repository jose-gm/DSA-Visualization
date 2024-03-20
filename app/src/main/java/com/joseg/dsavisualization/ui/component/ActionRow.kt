package com.joseg.dsavisualization.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFold
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ActionRow(
    modifier: Modifier = Modifier,
    containerColor: Color = ActionDefaults.containerColor,
    contentColor: Color = ActionDefaults.contentColor,
    edgePadding: Dp = ActionDefaults.edgePadding,
    contentPadding: Dp = ActionDefaults.contentPadding,
    actionItems: @Composable ActionRowScope.() -> Unit
) {
    Surface(
        modifier = modifier.selectableGroup(),
        color = containerColor,
        contentColor = contentColor
    ) {
        Layout(
            content = @Composable { ActionRowScopeImplementation.actionItems() },
            modifier = Modifier.fillMaxWidth()
        ) { measurables, constraints ->
            val edgePaddingPx = edgePadding.roundToPx()
            val contentPaddingPx = contentPadding.roundToPx()
            val actionRowWidth = constraints.maxWidth
            val actionItemCount = measurables.size
            val actionItemWidth = if (measurables.isNotEmpty()) {
                var actionItemWidthTemp = actionRowWidth / actionItemCount
                actionItemWidthTemp -= (edgePaddingPx * 2) / actionItemCount
                actionItemWidthTemp -= (contentPaddingPx * (actionItemCount - 1)) / actionItemCount
                actionItemWidthTemp
            } else { 0 }
            val actionRowHeight = measurables.fastFold(0) { max, measurable ->
                maxOf(max, measurable.maxIntrinsicHeight(actionItemWidth))
            }

            val actionItemPlaceables = measurables.map {
                it.measure(constraints.copy(
                    minWidth = actionItemWidth,
                    maxWidth = actionItemWidth,
                    minHeight = actionRowHeight,
                    maxHeight = actionRowHeight
                ))
            }

            layout(actionRowWidth, actionRowHeight) {
                var offsetX = edgePaddingPx
                actionItemPlaceables.fastForEach {
                    it.place(offsetX, 0)
                    offsetX += it.width + contentPaddingPx - 1
                }
            }
        }
    }
}

@Composable
fun ScrollableActionRow(
    selectedActionItemIndex: Int?,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    containerColor: Color = ActionDefaults.containerColor,
    contentColor: Color = ActionDefaults.contentColor,
    edgePadding: Dp = ActionDefaults.edgePadding,
    horizontalPadding: Dp = ActionDefaults.contentPadding,
    actionItems: @Composable ActionRowScope.() -> Unit
) {
    Surface(
        modifier = modifier.selectableGroup(),
        color = containerColor,
        contentColor = contentColor
    ) {
        val coroutineScope = rememberCoroutineScope()
        val actionItemScrollStateData = remember(scrollState, coroutineScope) {
            ActionItemScrollStateData(scrollState, coroutineScope)
        }

        Layout(
            content = { ActionRowScopeImplementation.actionItems() },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(align = Alignment.CenterStart)
                .horizontalScroll(scrollState)
                .clipToBounds()
        ) { measurables, constraints ->
            val edgePaddingPx = edgePadding.roundToPx()
            val horizontalPaddingPx = horizontalPadding.roundToPx()
            val actionItemMinimumWidth = actionItemMinimumWidth.roundToPx()
            val actionItemCount = measurables.size
            val actionRowHeight = measurables.fastFold(0) { max, measurable ->
                maxOf(max, measurable.maxIntrinsicHeight(Constraints.Infinity))
            }

            val actionItemPlaceables = measurables.map {
                it.measure(
                    constraints.copy(
                        minWidth = actionItemMinimumWidth,
                        maxWidth = maxOf(actionItemMinimumWidth, it.maxIntrinsicWidth(actionRowHeight)),
                        minHeight = actionRowHeight,
                        maxHeight = actionRowHeight
                    )
                )
            }

            val actionRowWidth = actionItemPlaceables.fastFold(
                initial = (edgePaddingPx * 2) + (horizontalPaddingPx * (actionItemCount - 1))
            ) { sum, placeable ->
                sum + placeable.width
            }

            layout(actionRowWidth, actionRowHeight) {
                val actionItemsPositions = mutableListOf<ActionItemPosition>()
                var offsetX = edgePaddingPx
                actionItemPlaceables.fastForEach {
                    it.place(offsetX, 0)
                    actionItemsPositions.add(ActionItemPosition(left = offsetX.toDp(), width = it.width.toDp()))
                    offsetX += it.width + horizontalPaddingPx - 1
                }

                selectedActionItemIndex?.let {
                    actionItemScrollStateData.onLaidOut(
                        it,
                        actionItemsPositions,
                        this@Layout
                    )
                }
            }
        }
    }
}

@Immutable
private data class ActionItemPosition(val left: Dp, val width: Dp) {
    val right: Dp get() = left + width
}

private class ActionItemScrollStateData(
    private val scrollState: ScrollState,
    private val coroutineScope: CoroutineScope,
    private var selectedActionItemIndex: Int? = null
) {
    fun onLaidOut(
        selectedActionItem: Int,
        actionItemPositions: List<ActionItemPosition>,
        density: Density
    ) {
        if (selectedActionItemIndex != selectedActionItem) {
            selectedActionItemIndex = selectedActionItem
            actionItemPositions.getOrNull(selectedActionItem)?.let {
                val actionItemCenterScrollOffset = calculateActionItemScrollOffset(
                    selectedActionItem,
                    actionItemPositions,
                    density
                )
                if (scrollState.value != actionItemCenterScrollOffset) {
                    coroutineScope.launch {
                        scrollState.animateScrollTo(
                            actionItemCenterScrollOffset,
                            animationSpec = tween(150, easing = FastOutSlowInEasing)
                        )
                    }
                }
            }
        }
    }

    private fun calculateActionItemScrollOffset(
        selectedActionItem: Int,
        actionItemPositions: List<ActionItemPosition>,
        density: Density
    ): Int {
        with(density) {
            val actionRowTotalWidth = actionItemPositions.last().right.roundToPx()
            val actionRowVisibleWidth = actionRowTotalWidth - scrollState.maxValue
            val scrollCenterVisibleWidth = actionRowVisibleWidth / 2
            val actionItemOffset = actionItemPositions[selectedActionItem].left.roundToPx()
            val actionItemWidth = actionItemPositions[selectedActionItem].width.roundToPx()
            val actionItemCenterScrollOffset = actionItemOffset - (scrollCenterVisibleWidth - (actionItemWidth/2))
            val availableSpace = (actionRowTotalWidth - actionRowVisibleWidth).coerceAtLeast(0)

            return actionItemCenterScrollOffset.coerceIn(0, availableSpace)
        }
    }
}

object ActionDefaults {
    val containerColor: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceContainer

    val contentColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface

    val edgePadding: Dp get() = 8.dp

    val contentPadding: Dp get() = 12.dp
}

interface ActionRowScope
object ActionRowScopeImplementation : ActionRowScope

private val actionItemMinimumWidth = 80.dp