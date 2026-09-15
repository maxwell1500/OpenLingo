package com.duo.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.duo.app.data.local.character.JapaneseCharacter
import kotlin.math.hypot

@Composable
fun StrokeDrawingCanvas(
    character: JapaneseCharacter,
    completedStrokes: Int,
    onStrokeCompleted: (strokeIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentStrokeIndex = completedStrokes
    val currentStroke = character.strokes.getOrNull(currentStrokeIndex)

    // Current finger drawing path
    val userPoints = remember(currentStrokeIndex) { mutableStateListOf<Offset>() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFFCFCFC))
            .border(2.dp, Color(0xFFE5E5E5), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center,
    ) {
        // Japanese 4-Quadrant practice grid (faint dashed lines)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Cross dashed lines
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
            drawLine(
                color = Color(0xFFECECEC),
                start = Offset(w / 2f, 0f),
                end = Offset(w / 2f, h),
                strokeWidth = 2f,
                pathEffect = dashEffect,
            )
            drawLine(
                color = Color(0xFFECECEC),
                start = Offset(0f, h / 2f),
                end = Offset(w, h / 2f),
                strokeWidth = 2f,
                pathEffect = dashEffect,
            )

            // 1. Draw all completed strokes in permanent dark Japanese ink
            for (i in 0 until completedStrokes) {
                val stroke = character.strokes[i]
                if (stroke.points.size >= 2) {
                    val path = Path().apply {
                        moveTo(stroke.points[0].x * w, stroke.points[0].y * h)
                        for (p in 1 until stroke.points.size) {
                            lineTo(stroke.points[p].x * w, stroke.points[p].y * h)
                        }
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF2B2B2B),
                        style = Stroke(
                            width = 28f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                    )
                }
            }

            // 2. Draw future stroke outlines in faint light gray
            for (i in (completedStrokes + 1) until character.strokes.size) {
                val stroke = character.strokes[i]
                if (stroke.points.size >= 2) {
                    val path = Path().apply {
                        moveTo(stroke.points[0].x * w, stroke.points[0].y * h)
                        for (p in 1 until stroke.points.size) {
                            lineTo(stroke.points[p].x * w, stroke.points[p].y * h)
                        }
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFFEEEEEE),
                        style = Stroke(
                            width = 22f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                    )
                }
            }

            // 3. Draw active stroke guide (cyan/blue glowing path with directional numbered circle)
            if (currentStroke != null && currentStroke.points.size >= 2) {
                val guidePath = Path().apply {
                    moveTo(currentStroke.points[0].x * w, currentStroke.points[0].y * h)
                    for (p in 1 until currentStroke.points.size) {
                        lineTo(currentStroke.points[p].x * w, currentStroke.points[p].y * h)
                    }
                }

                // Guide faint track
                drawPath(
                    path = guidePath,
                    color = Color(0xFFDDF4FF),
                    style = Stroke(
                        width = 26f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )

                // Start node indicator circle (Duolingo blue)
                val startPos = Offset(currentStroke.points[0].x * w, currentStroke.points[0].y * h)
                drawCircle(
                    color = Color(0xFF1CB0F6),
                    radius = 20f,
                    center = startPos,
                )
                drawCircle(
                    color = Color.White,
                    radius = 8f,
                    center = startPos,
                )

                // End target circle
                val endPoint = currentStroke.points.last()
                val endPos = Offset(endPoint.x * w, endPoint.y * h)
                drawCircle(
                    color = Color(0xFF1CB0F6),
                    radius = 12f,
                    center = endPos,
                    style = Stroke(width = 4f),
                )
            }

            // 4. Draw active user dragging line (lively green ink)
            if (userPoints.size >= 2) {
                val userPath = Path().apply {
                    moveTo(userPoints[0].x, userPoints[0].y)
                    for (i in 1 until userPoints.size) {
                        lineTo(userPoints[i].x, userPoints[i].y)
                    }
                }
                drawPath(
                    path = userPath,
                    color = Color(0xFF58CC02),
                    style = Stroke(
                        width = 28f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }
        }

        // Pointer input handler for gesture tracking and stroke verification
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(currentStrokeIndex) {
                    if (currentStroke == null) return@pointerInput

                    detectDragGestures(
                        onDragStart = { offset ->
                            userPoints.clear()
                            userPoints.add(offset)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            userPoints.add(change.position)
                        },
                        onDragEnd = {
                            if (userPoints.isNotEmpty()) {
                                val w = size.width.toFloat()
                                val h = size.height.toFloat()

                                val startGuide = Offset(
                                    currentStroke.points.first().x * w,
                                    currentStroke.points.first().y * h
                                )
                                val endGuide = Offset(
                                    currentStroke.points.last().x * w,
                                    currentStroke.points.last().y * h
                                )

                                val userStart = userPoints.first()
                                val userEnd = userPoints.last()

                                val startDist = hypot(userStart.x - startGuide.x, userStart.y - startGuide.y)
                                val endDist = hypot(userEnd.x - endGuide.x, userEnd.y - endGuide.y)

                                val tolerance = 150f
                                val isStartClose = startDist < tolerance
                                val isEndClose = endDist < tolerance

                                if (isStartClose && isEndClose && userPoints.size >= 4) {
                                    onStrokeCompleted(currentStrokeIndex)
                                }
                            }
                            userPoints.clear()
                        },
                        onDragCancel = {
                            userPoints.clear()
                        }
                    )
                }
        )
    }
}
