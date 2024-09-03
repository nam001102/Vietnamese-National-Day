package com.tramnung.vietnamesenationalday

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun AnimatedStar(){
    val starSize = 100
    val ballRadius = 4f
    val duration = 4000
    val fillDuration = 2000

    val progress = remember { Animatable(0f) }
    val strokeWidth = 2.dp
    val strokePx = with(LocalDensity.current) { strokeWidth.toPx() }

    var showBall by remember { mutableStateOf(true) }
    var fillStar by remember { mutableStateOf(false) }
    val fillProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = duration, easing = LinearEasing)
            )
            showBall = false // Hide the ball after animation completes
            fillStar = true // Start filling the rectangle
            fillProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = fillDuration)
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = starSize
        val height = starSize
        val centerX = size.width / 2
        val centerY = size.height / 2

        val perimeter = 2 * (width + height)
        val ballPosition = perimeter * progress.value

//        // Draw the ball
//        if (showBall) {
//            val (x, y) = when {
//                ballPosition <= starSize * 2 -> ballPosition to 0f // Top edge
//                ballPosition <= starSize * 4 -> starSize to (ballPosition - starSize * 2) // Right edge
//                ballPosition <= starSize * 6 -> (starSize - (ballPosition - starSize * 4)) to starSize // Bottom edge
//                else -> 0f to (starSize - (ballPosition - starSize * 6)) // Left edge
//            }
//
//            drawCircle(
//                color = Color.Red,
//                radius = ballRadius,
//                center = Offset(centerX + x.toFloat() - starSize / 2, centerY + y.toFloat() - starSize / 2)
//            )
//        }


        drawLineStar(ballPosition, this, strokePx, Color.Black)

        // Fill the star from the center outward
        if (fillStar) {
            val maxRadius = starSize
            val currentRadius = maxRadius * fillProgress.value
            drawIntoCanvas { canvas ->

                val path = Path().apply {
                    val vertices = listOf(
                        Offset(centerX + 0f, centerY - 18f * 2),
                        Offset(centerX + 6f * 2, centerY - 0f * 2),
                        Offset(centerX + 24f * 2, centerY - 0f * 2),
                        Offset(centerX + 8f * 2, centerY + 9f * 2),
                        Offset(centerX + 14f * 2, centerY + 28f * 2),
                        Offset(centerX + 0f * 2, centerY + 15.2f * 2),
                        Offset(centerX - 14f * 2, centerY + 28f * 2),
                        Offset(centerX - 8f * 2, centerY + 9f * 2),
                        Offset(centerX - 24f * 2, centerY - 0f * 2),
                        Offset(centerX - 6f * 2, centerY - 0f * 2)
                    )

                    moveTo(vertices[0].x, vertices[0].y)
                    for (i in 1 until vertices.size) {
                        lineTo(vertices[i].x, vertices[i].y)
                    }
                    close()
                }

                // Clip the canvas to the path
                clipPath(path) {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            color = Color.Yellow
                            style = PaintingStyle.Fill
                        }

                        val path = Path().apply {
                            val steps = 100
                            for (i in 0 until steps) {
                                val angle = (2 * Math.PI * i / steps).toFloat()
                                val x =
                                    centerX + (currentRadius * Math.cos(angle.toDouble())).toFloat()
                                val y =
                                    centerY + (currentRadius * Math.sin(angle.toDouble())).toFloat()
                                if (i == 0) {
                                    moveTo(x, y)
                                } else {
                                    lineTo(x, y)
                                }
                            }
                            close()
                        }
                        canvas.drawPath(path, paint)
                    }
                }
            }
        }
    }
}

fun drawLineStar(
    pathLength: Float,
    scope: DrawScope,
    strokePx: Float,
    color: Color
) {
    val canvasWidth = scope.size.width
    val canvasHeight = scope.size.height

    // Calculate center of the canvas
    val centerX = canvasWidth / 2
    val centerY = canvasHeight / 2

    // Define star vertices, centered on canvas
    val vertices = listOf(
        Offset(centerX + 0f * 2,  centerY - 18f * 2),    //Top
        Offset(centerX + 6f * 2, centerY - 0f * 2),   //Top Right
        Offset(centerX + 24f * 2, centerY - 0f * 2),  //Right Top
        Offset(centerX + 8f * 2, centerY + 9f * 2),    //Right Bottom
        Offset(centerX + 14f * 2, centerY + 28f * 2),   //Bottom Right
        Offset(centerX + 0f * 2, centerY + 15.2f * 2),   //Bottom Top
        Offset(centerX - 14f * 2, centerY + 28f * 2),    //Bottom Left
        Offset(centerX - 8f * 2, centerY + 9f * 2),   //Left Bottom
        Offset(centerX - 24f * 2, + centerY - 0f * 2),   //Left Top
        Offset(centerX - 6f * 2, + centerY - 0f * 2), //Top Left
    )

    // Draw star lines up to the pathLength
    var remainingLength = pathLength
    for (i in vertices.indices) {
        val start = vertices[i]
        val end = vertices[(i + 1) % vertices.size]

        val lineLength = sqrt(
            (end.x - start.x).toDouble().pow(2.0) +
                    (end.y - start.y).toDouble().pow(2.0)
        ).toFloat()

        if (remainingLength <= lineLength) {
            val ratio = remainingLength / lineLength
            scope.drawLine(
                color = color,
                start = start,
                end = Offset(
                    start.x + (end.x - start.x) * ratio,
                    start.y + (end.y - start.y) * ratio
                ),
                strokeWidth = strokePx
            )
            break
        } else {
            scope.drawLine(
                color = color,
                start = start,
                end = end,
                strokeWidth = strokePx
            )
            remainingLength -= lineLength
        }
    }
}
