package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.PastelYellowBg
import com.example.ui.theme.PastelYellowSquiggle
import kotlin.math.sin

@Composable
fun AnimatedSquiggleBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "squiggles")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val driftY by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "driftY"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PastelYellowBg)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val squiggleColor = PastelYellowSquiggle.copy(alpha = 0.55f)
            val strokeWidth = 7f

            // Squiggle parameters: base Y, amplitude, wavelength, speed factor
            val configs = listOf(
                Triple(height * 0.12f, 28f, width * 0.35f),
                Triple(height * 0.28f, 22f, width * 0.25f),
                Triple(height * 0.45f, 32f, width * 0.40f),
                Triple(height * 0.62f, 26f, width * 0.30f),
                Triple(height * 0.78f, 30f, width * 0.38f),
                Triple(height * 0.92f, 20f, width * 0.28f)
            )

            configs.forEachIndexed { index, (baseY, amp, waveLen) ->
                val path = Path()
                val offsetPhase = phase + (index * 1.1f)
                val currentBaseY = baseY + (if (index % 2 == 0) driftY else -driftY)

                val startX = -50f
                val endX = width + 50f
                var x = startX
                var isFirst = true

                while (x <= endX) {
                    val angle = (x / waveLen) * 2 * Math.PI.toFloat() + offsetPhase
                    val y = currentBaseY + amp * sin(angle)
                    if (isFirst) {
                        path.moveTo(x, y)
                        isFirst = false
                    } else {
                        path.lineTo(x, y)
                    }
                    x += 20f
                }

                drawPath(
                    path = path,
                    color = squiggleColor,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Cute little floating pastel blobs & dots near squiggles
                val dotX = (width * ((index * 0.23f + phase * 0.05f) % 1f))
                val dotY = currentBaseY + sin(offsetPhase) * 15f
                drawCircle(
                    color = squiggleColor.copy(alpha = 0.45f),
                    radius = 9f,
                    center = Offset(dotX, dotY)
                )
            }
        }

        content()
    }
}
