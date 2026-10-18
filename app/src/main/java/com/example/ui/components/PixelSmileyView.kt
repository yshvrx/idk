package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Signature Feature: Interactive Pixelated 8-Bit Smiley.
 * Follows finger/pointer coordinates, blinks organically,
 * and reacts with an excited bounce and happy expression on task completions.
 */
@Composable
fun PixelSmileyCard(
    celebrateTrigger: Long = 0L,
    modifier: Modifier = Modifier
) {
    var touchTargetX by remember { mutableFloatStateOf(0f) }
    var touchTargetY by remember { mutableFloatStateOf(0f) }
    var isTouching by remember { mutableStateOf(false) }

    var isBlinking by remember { mutableStateOf(false) }
    var isHappyWink by remember { mutableStateOf(false) }

    val bounceAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val speechQuotes = remember {
        listOf(
            "Ready to conquer today.",
            "One calm step at a time.",
            "Focused minds finish strong.",
            "Simplicity is true sophistication.",
            "Great flow right now!"
        )
    }
    var currentQuoteIndex by remember { mutableIntStateOf(0) }

    // Organic blinking timer
    LaunchedEffect(Unit) {
        while (true) {
            delay((2400..5000).random().toLong())
            isBlinking = true
            delay(160)
            isBlinking = false
        }
    }

    // Reaction when tasks are completed
    LaunchedEffect(celebrateTrigger) {
        if (celebrateTrigger > 0) {
            isHappyWink = true
            bounceAnim.animateTo(
                targetValue = -18f,
                animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f)
            )
            bounceAnim.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f)
            )
            delay(1200)
            isHappyWink = false
        }
    }

    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceColor)
            .border(1.dp, outlineColor, RoundedCornerShape(16.dp))
            .testTag("pixel_smiley_widget_card")
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isTouching = true
                        touchTargetX = offset.x - size.width / 2f
                        touchTargetY = offset.y - size.height / 2f
                    },
                    onDrag = { change, _ ->
                        touchTargetX = change.position.x - size.width / 2f
                        touchTargetY = change.position.y - size.height / 2f
                    },
                    onDragEnd = {
                        isTouching = false
                        touchTargetX = 0f
                        touchTargetY = 0f
                    },
                    onDragCancel = {
                        isTouching = false
                        touchTargetX = 0f
                        touchTargetY = 0f
                    }
                )
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Move) {
                            val pos = event.changes.firstOrNull()?.position
                            if (pos != null) {
                                touchTargetX = pos.x - size.width / 2f
                                touchTargetY = pos.y - size.height / 2f
                            }
                        } else if (event.type == PointerEventType.Exit) {
                            touchTargetX = 0f
                            touchTargetY = 0f
                        }
                    }
                }
            }
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF34C759))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FOCUS COMPANION",
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceVariant,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = speechQuotes[currentQuoteIndex],
                    style = MaterialTheme.typography.titleMedium,
                    color = onSurface,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (isTouching) "Watching your touch..." else "Interactive retro 8-bit buddy",
                    style = MaterialTheme.typography.labelSmall,
                    color = onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // The Interactive Pixel Smiley Avatar
            Box(
                modifier = Modifier
                    .offset { IntOffset(0, bounceAnim.value.roundToInt()) }
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, outlineColor, RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        currentQuoteIndex = (currentQuoteIndex + 1) % speechQuotes.size
                        scope.launch {
                            isHappyWink = true
                            bounceAnim.animateTo(-12f, spring(dampingRatio = 0.4f))
                            bounceAnim.animateTo(0f, spring(dampingRatio = 0.6f))
                            delay(600)
                            isHappyWink = false
                        }
                    }
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                PixelSmileyCanvas(
                    targetDx = touchTargetX,
                    targetDy = touchTargetY,
                    isBlinking = isBlinking,
                    isHappy = isHappyWink,
                    pixelColor = onSurface,
                    accentColor = Color(0xFF34C759)
                )
            }
        }
    }
}

/**
 * 12x12 Pixel Grid Canvas rendering true 8-bit retro art.
 */
@Composable
fun PixelSmileyCanvas(
    targetDx: Float,
    targetDy: Float,
    isBlinking: Boolean,
    isHappy: Boolean,
    pixelColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier.size(52.dp)
) {
    // Determine pupil direction offset (-1, 0, +1)
    val angle = atan2(targetDy, targetDx)
    val distance = (targetDx * targetDx + targetDy * targetDy)
    val threshold = 400f // ~20dp distance

    val pupilShiftX = if (distance > threshold) {
        val cosVal = cos(angle)
        when {
            cosVal > 0.38f -> 1
            cosVal < -0.38f -> -1
            else -> 0
        }
    } else 0

    val pupilShiftY = if (distance > threshold) {
        val sinVal = sin(angle)
        when {
            sinVal > 0.38f -> 1
            sinVal < -0.38f -> -1
            else -> 0
        }
    } else 0

    Canvas(modifier = modifier) {
        val gridSize = 12
        val blockSize = size.width / gridSize

        fun drawPixel(col: Int, row: Int, color: Color) {
            drawRect(
                color = color,
                topLeft = Offset(col * blockSize, row * blockSize),
                size = Size(blockSize * 0.94f, blockSize * 0.94f)
            )
        }

        // 8-bit circular face outline on 12x12 grid
        val faceGrid = listOf(
            // Row 0
            listOf(0,0,0,0,1,1,1,1,0,0,0,0),
            // Row 1
            listOf(0,0,1,1,1,1,1,1,1,1,0,0),
            // Row 2
            listOf(0,1,1,1,1,1,1,1,1,1,1,0),
            // Row 3 (Eyes upper)
            listOf(0,1,1,1,1,1,1,1,1,1,1,0),
            // Row 4 (Eyes lower)
            listOf(1,1,1,1,1,1,1,1,1,1,1,1),
            // Row 5 (Cheeks)
            listOf(1,1,1,1,1,1,1,1,1,1,1,1),
            // Row 6 (Nose level)
            listOf(1,1,1,1,1,1,1,1,1,1,1,1),
            // Row 7 (Mouth upper)
            listOf(1,1,1,1,1,1,1,1,1,1,1,1),
            // Row 8 (Mouth lower)
            listOf(0,1,1,1,1,1,1,1,1,1,1,0),
            // Row 9
            listOf(0,1,1,1,1,1,1,1,1,1,1,0),
            // Row 10
            listOf(0,0,1,1,1,1,1,1,1,1,0,0),
            // Row 11
            listOf(0,0,0,0,1,1,1,1,0,0,0,0)
        )

        // Draw vibrant retro yellow pixel skin matching the app logo
        val faceBaseColor = Color(0xFFFFD60A)
        for (r in 0 until gridSize) {
            val row = faceGrid[r]
            for (c in 0 until gridSize) {
                if (row[c] == 1) {
                    drawPixel(c, r, faceBaseColor)
                }
            }
        }

        // Distinct border pixels
        val outlinePixels = listOf(
            Pair(4,0), Pair(5,0), Pair(6,0), Pair(7,0),
            Pair(2,1), Pair(3,1), Pair(8,1), Pair(9,1),
            Pair(1,2), Pair(10,2),
            Pair(1,3), Pair(10,3),
            Pair(0,4), Pair(11,4),
            Pair(0,5), Pair(11,5),
            Pair(0,6), Pair(11,6),
            Pair(0,7), Pair(11,7),
            Pair(1,8), Pair(10,8),
            Pair(1,9), Pair(10,9),
            Pair(2,10), Pair(3,10), Pair(8,10), Pair(9,10),
            Pair(4,11), Pair(5,11), Pair(6,11), Pair(7,11)
        )
        outlinePixels.forEach { (c, r) ->
            drawPixel(c, r, pixelColor.copy(alpha = 0.5f))
        }

        // Draw Eyes
        if (isHappy) {
            // Joyful curved anime/retro eyes ^^
            // Left eye: (3,3), (4,2), (5,3)
            drawPixel(3, 4, accentColor)
            drawPixel(4, 3, accentColor)
            drawPixel(5, 4, accentColor)

            // Right eye: (6,3), (7,2), (8,3)
            drawPixel(6, 4, accentColor)
            drawPixel(7, 3, accentColor)
            drawPixel(8, 4, accentColor)

            // Joyful open mouth
            drawPixel(4, 7, pixelColor)
            drawPixel(7, 7, pixelColor)
            drawPixel(4, 8, pixelColor)
            drawPixel(5, 8, pixelColor)
            drawPixel(6, 8, pixelColor)
            drawPixel(7, 8, pixelColor)
            drawPixel(5, 9, accentColor)
            drawPixel(6, 9, accentColor)
        } else if (isBlinking) {
            // Closed eyes slit
            drawPixel(3, 4, pixelColor)
            drawPixel(4, 4, pixelColor)
            drawPixel(7, 4, pixelColor)
            drawPixel(8, 4, pixelColor)

            // Subtle gentle smile
            drawPixel(4, 8, pixelColor)
            drawPixel(5, 8, pixelColor)
            drawPixel(6, 8, pixelColor)
            drawPixel(7, 8, pixelColor)
        } else {
            // Normal gaze with pupil tracking
            // Eye socket left: cols 3..4, rows 3..4
            // Eye socket right: cols 7..8, rows 3..4
            val leftEyeBaseX = 3
            val leftEyeBaseY = 4
            val rightEyeBaseX = 7
            val rightEyeBaseY = 4

            // White eye background
            drawPixel(leftEyeBaseX, leftEyeBaseY, Color.White)
            drawPixel(leftEyeBaseX + 1, leftEyeBaseY, Color.White)
            drawPixel(leftEyeBaseX, leftEyeBaseY - 1, Color.White)
            drawPixel(leftEyeBaseX + 1, leftEyeBaseY - 1, Color.White)

            drawPixel(rightEyeBaseX, rightEyeBaseY, Color.White)
            drawPixel(rightEyeBaseX + 1, rightEyeBaseY, Color.White)
            drawPixel(rightEyeBaseX, rightEyeBaseY - 1, Color.White)
            drawPixel(rightEyeBaseX + 1, rightEyeBaseY - 1, Color.White)

            // Dark Pupils with shift
            val pLeftX = (leftEyeBaseX + (if (pupilShiftX > 0) 1 else 0)).coerceIn(3, 4)
            val pLeftY = (leftEyeBaseY + pupilShiftY).coerceIn(3, 4)
            drawPixel(pLeftX, pLeftY, pixelColor)

            val pRightX = (rightEyeBaseX + (if (pupilShiftX > 0) 1 else 0)).coerceIn(7, 8)
            val pRightY = (rightEyeBaseY + pupilShiftY).coerceIn(3, 4)
            drawPixel(pRightX, pRightY, pixelColor)

            // Gentle smile
            drawPixel(3, 7, pixelColor)
            drawPixel(4, 8, pixelColor)
            drawPixel(5, 8, pixelColor)
            drawPixel(6, 8, pixelColor)
            drawPixel(7, 8, pixelColor)
            drawPixel(8, 7, pixelColor)
        }
    }
}
