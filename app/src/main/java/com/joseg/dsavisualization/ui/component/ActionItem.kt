package com.joseg.dsavisualization.ui.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/*
 * For more information on BlendMode visit: https://developer.android.com/reference/android/graphics/BlendMode
 *
 * For a more thorough explanation on why do we need to add an extra canvas layer for Compose to properly
 * apply the BlendMode in the graphic layer and other alternatives to accomplish the same behavior see:
 * - https://stackoverflow.com/questions/74723358/jetpack-compose-canvas-drawtext-color-blending
 * - https://stackoverflow.com/questions/65653560/jetpack-compose-applying-porterduffmode-to-image
 * */


@Composable
fun ActionRowScope.ActionItem(
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedLabelColor: Color = ActionItemDefaults.selectedLabelColor,
    unselectedLabelColor: Color = ActionItemDefaults.unselectedLabelColor,
    indicatorColor: Color = ActionItemDefaults.indicatorColor,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val transition = updateTransition(targetState = selected)
    val labelColor by transition.animateColor(transitionSpec = {
        tween(actionItemAlphaAnimationDuration, actionItemAlphaAnimationDelay)
    }) {
        if (it) selectedLabelColor else unselectedLabelColor
    }
    val alpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(actionItemAlphaAnimationDuration, actionItemAlphaAnimationDelay)
    )
    val ripple = rememberRipple(color = indicatorColor)
    val labelStyle: @Composable () -> Unit = @Composable {
        ProvideTextStyle(
            value = MaterialTheme.typography.labelMedium.copy(color = labelColor),
            content = label
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .selectable(
                selected = selected,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = ripple,
                onClick = onClick,
            ),
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
            .background(indicatorColor)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            labelStyle()
        }
    }
}

// Creates a canvas layer inside of a ContentDrawScope
//private fun ContentDrawScope.drawWithLayer(block: ContentDrawScope.() -> Unit) {
//    with(drawContext.canvas.nativeCanvas) {
//        val checkPoint = saveLayer(null, null)
//        block()
//        restoreToCount(checkPoint)
//    }
//}

object ActionItemDefaults {
    val selectedLabelColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSecondaryContainer
    val unselectedLabelColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface
    val indicatorColor: Color
        @Composable get() = MaterialTheme.colorScheme.secondaryContainer
}

private val height = 40.dp
private const val actionItemAlphaAnimationDuration = 110
private const val actionItemAlphaAnimationDelay = 50
private val borderThickness = 0.2.dp