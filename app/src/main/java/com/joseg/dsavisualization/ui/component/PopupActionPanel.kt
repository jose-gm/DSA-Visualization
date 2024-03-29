package com.joseg.dsavisualization.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun PopupActionPanel(
    modifier: Modifier = Modifier,
    alignment: Alignment = PopupActionPanelDefaults.alignment,
    color: Color = PopupActionPanelDefaults.color,
    elevation: PopupActionPanelElevation = PopupActionPanelDefaults.elevation,
    content: @Composable () -> Unit
) {
    val scale = remember { Animatable(0f) }
    Popup(
        alignment = alignment,
        onDismissRequest = null
    ) {
        Surface(
            modifier = modifier
                .padding(margin)
                .fillMaxWidth()
                .wrapContentHeight()
                .scale(scale.value),
            shape = PopupActionPanelDefaults.shape,
            color = color,
            tonalElevation = elevation.tonalElevation,
            shadowElevation = elevation.shadowElevation,
            content = content
        )
    }

    LaunchedEffect(scale) {
        scale.animateTo(1f)
    }
}

object PopupActionPanelDefaults {
    val color: Color @Composable get() = MaterialTheme.colorScheme.surface
    val elevation: PopupActionPanelElevation = PopupActionPanelElevation(tonalElevation, shadowElevation)
    val alignment: Alignment = Alignment.TopStart
    val shape: Shape = RoundedCornerShape(5.dp)
}

data class PopupActionPanelElevation(
    val tonalElevation: Dp,
    val shadowElevation: Dp
)

private val tonalElevation: Dp = 6.dp
private val shadowElevation: Dp = 1.dp
private val margin: Dp = 16.dp