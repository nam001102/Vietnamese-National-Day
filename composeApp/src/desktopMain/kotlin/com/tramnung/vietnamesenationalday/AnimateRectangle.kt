package com.tramnung.vietnamesenationalday

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AnimateRectangle() {
    val rectWidth = 300f
    val rectHeight = 200f
    val ballRadius = 4f
    val duration = 4000
    val fillDuration = 2000

    val progress = remember { Animatable(0f) }
    val strokeWidth = 2.dp
    val strokePx = with(LocalDensity.current) { strokeWidth.toPx() }

    var showBall by remember { mutableStateOf(true) }
    var fillRectangle by remember { mutableStateOf(false) }
    var fillRectangleFinish by remember { mutableStateOf(false) }
    val fillProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = duration, easing = LinearEasing)
            )
            showBall = false // Hide the ball after animation completes
            fillRectangle = true // Start filling the rectangle
            fillProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = fillDuration)
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize().padding(top = 20.dp)) {
        val perimeter = 2 * (rectWidth + rectHeight)
        val ballPosition = perimeter * progress.value

        // Draw the rectangle outline
        val pathLength = ballPosition
        drawLineRectangle(0f, 0f, pathLength, this, rectWidth, rectHeight, strokePx, Color.Black)

        // Draw the ball
        if (showBall) {
            val (x, y) = when {
                ballPosition <= rectWidth -> ballPosition to 0f // Top edge
                ballPosition <= rectWidth + rectHeight -> rectWidth to (ballPosition - rectWidth) // Right edge
                ballPosition <= 2 * rectWidth + rectHeight -> (rectWidth - (ballPosition - rectWidth - rectHeight)) to rectHeight // Bottom edge
                else -> 0f to (rectHeight - (ballPosition - 2 * rectWidth - rectHeight)) // Left edge
            }

            drawCircle(
                color = Color.Red,
                radius = ballRadius,
                center = this.center.copy(x = this.center.x + x - rectWidth / 2, y = this.center.y + y - rectHeight / 2)
            )
        }

        // Fill the rectangle from the center outwards in a star shape
        if (fillRectangle) {
            val centerX = this.center.x
            val centerY = this.center.y
            val maxRadius = Math.min(rectWidth, rectHeight) / 1
            val currentRadius = maxRadius * fillProgress.value

            // Clip the canvas to the rectangle bounds
            clipRect(
                left = centerX - rectWidth / 2,
                top = centerY - rectHeight / 2,
                right = centerX + rectWidth / 2,
                bottom = centerY + rectHeight / 2
            ) {
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        color = Color.Red
                        style = PaintingStyle.Fill
                    }

                    val path = Path().apply {
                        val steps = 100
                        for (i in 0 until steps) {
                            val angle = (2 * Math.PI * i / steps).toFloat()
                            val x = centerX + (currentRadius * Math.cos(angle.toDouble())).toFloat()
                            val y = centerY + (currentRadius * Math.sin(angle.toDouble())).toFloat()
                            if (i == 0) {
                                moveTo(x, y)
                            } else {
                                lineTo(x, y)
                            }
                        }
                        close()
                    }

                    // Draw the path within the clipped rectangle
                    canvas.drawPath(path, paint)
                    fillRectangleFinish = true
                }
            }
        }
    }
    if (fillRectangleFinish){
        AnimatedStar()
    }
}

fun drawLineRectangle(
    startX: Float,
    startY: Float,
    pathLength: Float,
    scope: DrawScope,
    rectWidth: Float,
    rectHeight: Float,
    strokePx: Float,
    color: Color
) {
    when {
        pathLength <= rectWidth -> {
            scope.drawLine(
                color = color,
                start = scope.center.copy(x = scope.center.x - rectWidth / 2 + startX, y = scope.center.y - rectHeight / 2 + startY),
                end = scope.center.copy(x = scope.center.x - rectWidth / 2 + pathLength, y = scope.center.y - rectHeight / 2),
                strokeWidth = strokePx
            )
        }
        pathLength <= rectWidth + rectHeight -> {
            scope.drawLine(
                color = color,
                start = scope.center.copy(x = scope.center.x - rectWidth / 2 + startX, y = scope.center.y - rectHeight / 2),
                end = scope.center.copy(x = scope.center.x + rectWidth / 2, y = scope.center.y - rectHeight / 2),
                strokeWidth = strokePx
            )
            scope.drawLine(
                color = color,
                start = scope.center.copy(x = scope.center.x + rectWidth / 2, y = scope.center.y - rectHeight / 2),
                end = scope.center.copy(x = scope.center.x + rectWidth / 2, y = scope.center.y - rectHeight / 2 + (pathLength - rectWidth)),
                strokeWidth = strokePx
            )
        }
        pathLength <= 2 * rectWidth + rectHeight -> {
            scope.drawLine(
                color = color,
                start = scope.center.copy(x = scope.center.x - rectWidth / 2 + startX, y = scope.center.y - rectHeight / 2),
                end = scope.center.copy(x = scope.center.x + rectWidth / 2, y = scope.center.y - rectHeight / 2),
                strokeWidth = strokePx
            )
            scope.drawLine(
                color = color,
                start = scope.center.copy(x = scope.center.x + rectWidth / 2, y = scope.center.y - rectHeight / 2),
                end = scope.center.copy(x = scope.center.x + rectWidth / 2, y = scope.center.y + rectHeight / 2),
                strokeWidth = strokePx
            )
            scope.drawLine(
                color = color,
                start = scope.center.copy(x = scope.center.x + rectWidth / 2, y = scope.center.y + rectHeight / 2),
                end = scope.center.copy(x = scope.center.x + rectWidth / 2 - (pathLength - rectWidth - rectHeight), y = scope.center.y + rectHeight / 2),
                strokeWidth = strokePx
            )
        }
        else -> {
            scope.drawLine(
                color = color,
                start = scope.center.copy(x = scope.center.x - rectWidth / 2 + startX, y = scope.center.y - rectHeight / 2),
                end = scope.center.copy(x = scope.center.x + rectWidth / 2, y = scope.center.y - rectHeight / 2),
                strokeWidth = strokePx
            )
            scope.drawLine(
                color = color,
                start = scope.center.copy(x = scope.center.x + rectWidth / 2, y = scope.center.y - rectHeight / 2),
                end = scope.center.copy(x = scope.center.x + rectWidth / 2, y = scope.center.y + rectHeight / 2),
                strokeWidth = strokePx
            )
            scope.drawLine(
                color = color,
                start = scope.center.copy(x = scope.center.x + rectWidth / 2, y = scope.center.y + rectHeight / 2),
                end = scope.center.copy(x = scope.center.x - rectWidth / 2, y = scope.center.y + rectHeight / 2),
                strokeWidth = strokePx
            )
            scope.drawLine(
                color = color,
                start = scope.center.copy(x = scope.center.x - rectWidth / 2, y = scope.center.y + rectHeight / 2),
                end = scope.center.copy(x = scope.center.x - rectWidth / 2, y = scope.center.y + rectHeight / 2 - (pathLength - 2 * rectWidth - rectHeight)),
                strokeWidth = strokePx
            )
        }
    }
}
