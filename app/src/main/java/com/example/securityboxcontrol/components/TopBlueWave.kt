package com.example.securityboxcontrol.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
fun TopBlueWave(
    color: Color,
    waveHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val wavePx = waveHeight.toPx()

        // Fill everything blue first
        drawRect(
            color = color,
            topLeft = Offset(0f, 0f),
            size = Size(w, h)
        )

        val path = Path().apply {
            // start at bottom-left (slightly above bottom)
            moveTo(0f, h - wavePx)

            quadraticBezierTo(
                w / 2f, h + wavePx,
                w, h - wavePx
            )
            // close the shape down to the bottom edge
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(
            path = path,
            color = Color.White
        )
    }
}