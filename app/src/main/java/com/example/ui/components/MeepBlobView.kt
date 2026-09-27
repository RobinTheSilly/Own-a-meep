package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ThoughtType
import com.example.ui.theme.MeepCheekPink
import com.example.ui.theme.MeepEyeBlack
import com.example.ui.theme.MeepWhite
import com.example.ui.theme.PastelYellowDarker
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

data class FloatingParticle(
    val id: Long,
    val text: String,
    val startX: Float,
    val startY: Float
)

@Composable
fun MeepBlobView(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 140.dp,
    name: String = "Meep",
    colorHex: Long = 0xFFFFFFFF,
    hatId: String? = null,
    glassesId: String? = null,
    neckId: String? = null,
    isSleeping: Boolean = false,
    isWalking: Boolean = false,
    isEating: Boolean = false,
    thought: ThoughtType? = null,
    onThoughtClick: ((ThoughtType) -> Unit)? = null,
    onMeepClick: (() -> Unit)? = null
) {
    // Breathing & bounce animations
    val infiniteTransition = rememberInfiniteTransition(label = "meep_anim")

    val breathingScaleY by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheY"
    )

    val breathingScaleX by infiniteTransition.animateFloat(
        initialValue = 1.04f,
        targetValue = 0.96f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheX"
    )

    val walkingWiggle by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 280, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "walkingWiggle"
    )

    val sleepingZzzFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "zzzFloat"
    )

    // Periodic natural blinking
    var isBlinking by remember { mutableStateOf(false) }
    LaunchedEffect(isSleeping) {
        if (!isSleeping) {
            while (true) {
                delay((2500..5000).random().toLong())
                isBlinking = true
                delay(140)
                isBlinking = false
            }
        }
    }

    // Floating heart/sparkle particles on tap
    val particles = remember { mutableStateListOf<FloatingParticle>() }

    fun addParticle(emoji: String) {
        val p = FloatingParticle(
            id = System.currentTimeMillis() + (0..1000).random(),
            text = emoji,
            startX = (sizeDp.value * 0.5f) + (-20..20).random(),
            startY = (sizeDp.value * 0.4f)
        )
        particles.add(p)
    }

    // Clean up particles
    LaunchedEffect(particles.size) {
        if (particles.isNotEmpty()) {
            delay(1200)
            if (particles.isNotEmpty()) {
                particles.removeAt(0)
            }
        }
    }

    val currentScaleX = if (isWalking) breathingScaleX * 1.05f else breathingScaleX
    val currentScaleY = if (isWalking) breathingScaleY * 0.95f else breathingScaleY
    val currentRotation = if (isWalking) walkingWiggle else 0f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.wrapContentSize()
    ) {
        // Thought bubble above head if craving something
        if (thought != null && !isSleeping) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .testTag("thought_bubble")
                    .clickable { onThoughtClick?.invoke(thought) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = thought.iconEmoji,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = thought.actionHint,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF6B4908)
                    )
                }
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(sizeDp)
                .clickable {
                    addParticle(if (isSleeping) "💤" else listOf("💖", "✨", "🌸", "⭐").random())
                    onMeepClick?.invoke()
                }
                .testTag("meep_character_${name.lowercase()}")
        ) {
            // Shadow, Blob, Eyes, Mouth, Accessories
            Canvas(modifier = Modifier.size(sizeDp)) {
                val w = size.width
                val h = size.height

                val centerX = w / 2f
                val centerY = h * 0.62f

                // Drop shadow
                drawOval(
                    color = Color(0x33332A1C),
                    topLeft = Offset(centerX - w * 0.32f * currentScaleX, h * 0.88f),
                    size = Size(w * 0.64f * currentScaleX, h * 0.10f)
                )

                // Draw Cape behind body if equipped
                if (neckId == "neck_cape") {
                    drawCape(centerX, centerY, w, h, currentRotation)
                }

                // Meep Body (Organic droplet blob)
                val bodyColor = Color(colorHex)
                drawMeepBody(
                    centerX = centerX,
                    centerY = centerY,
                    width = w * 0.72f * currentScaleX,
                    height = h * 0.68f * currentScaleY,
                    rotationDegrees = currentRotation,
                    baseColor = bodyColor
                )

                // Face elements
                val faceCenterY = centerY - h * 0.04f
                val eyeSpacing = w * 0.16f
                val eyeWidth = w * 0.085f
                val eyeHeight = h * 0.12f

                // Blushing Cheeks
                val cheekRadius = w * 0.065f
                drawCircle(
                    color = MeepCheekPink.copy(alpha = 0.65f),
                    radius = cheekRadius,
                    center = Offset(centerX - eyeSpacing * 1.5f, faceCenterY + h * 0.05f)
                )
                drawCircle(
                    color = MeepCheekPink.copy(alpha = 0.65f),
                    radius = cheekRadius,
                    center = Offset(centerX + eyeSpacing * 1.5f, faceCenterY + h * 0.05f)
                )

                // Eyes
                if (isSleeping) {
                    // Sleepy curved eyes: ︶  ︶
                    drawSleepyEye(centerX - eyeSpacing, faceCenterY, eyeWidth)
                    drawSleepyEye(centerX + eyeSpacing, faceCenterY, eyeWidth)
                } else if (isBlinking) {
                    // Blinking line: —  —
                    drawLine(
                        color = MeepEyeBlack,
                        start = Offset(centerX - eyeSpacing - eyeWidth / 2f, faceCenterY),
                        end = Offset(centerX - eyeSpacing + eyeWidth / 2f, faceCenterY),
                        strokeWidth = 6f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = MeepEyeBlack,
                        start = Offset(centerX + eyeSpacing - eyeWidth / 2f, faceCenterY),
                        end = Offset(centerX + eyeSpacing + eyeWidth / 2f, faceCenterY),
                        strokeWidth = 6f,
                        cap = StrokeCap.Round
                    )
                } else {
                    // Big shiny anime eyes with sparkles
                    drawShinyEye(centerX - eyeSpacing, faceCenterY, eyeWidth, eyeHeight)
                    drawShinyEye(centerX + eyeSpacing, faceCenterY, eyeWidth, eyeHeight)
                }

                // Mouth
                drawMouth(centerX, faceCenterY + h * 0.06f, w, h, isEating, isSleeping)

                // Glasses
                if (glassesId != null) {
                    drawGlasses(glassesId, centerX, faceCenterY, eyeSpacing, eyeWidth)
                }

                // Neck accessories (Bowtie, Scarf, Bell)
                if (neckId != null && neckId != "neck_cape") {
                    drawNeckAccessory(neckId, centerX, centerY + h * 0.22f, w)
                }

                // Hat
                if (hatId != null) {
                    drawHat(hatId, centerX, centerY - h * 0.32f * currentScaleY, w, h)
                }
            }

            // Floating particles (hearts, stars, Zzz)
            particles.forEach { p ->
                Text(
                    text = p.text,
                    fontSize = 20.sp,
                    modifier = Modifier.offset(y = (-30).dp)
                )
            }

            // Sleep Zzz floating indicator
            if (isSleeping) {
                Text(
                    text = "💤 Zzz",
                    fontSize = 16.sp,
                    color = Color(0xFF6B4908),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = sleepingZzzFloat.dp)
                )
            }
        }

        // Cute Name Label
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = PastelYellowDarker.copy(alpha = 0.85f),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF4A3E26),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
            )
        }
    }
}

private fun DrawScope.drawMeepBody(
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
    rotationDegrees: Float,
    baseColor: Color
) {
    val path = Path()
    val halfW = width / 2f
    val halfH = height / 2f

    // Soft squishy pear-shaped droplet blob
    val top = centerY - halfH
    val bottom = centerY + halfH
    val left = centerX - halfW
    val right = centerX + halfW

    path.moveTo(centerX, top)
    // Top right curve down to waist
    path.cubicTo(
        centerX + halfW * 0.65f, top,
        right, centerY - halfH * 0.2f,
        right, centerY + halfH * 0.35f
    )
    // Bottom right rounded chubby base
    path.cubicTo(
        right, bottom,
        centerX + halfW * 0.5f, bottom,
        centerX, bottom
    )
    // Bottom left rounded chubby base
    path.cubicTo(
        centerX - halfW * 0.5f, bottom,
        left, bottom,
        left, centerY + halfH * 0.35f
    )
    // Left waist up to top curve
    path.cubicTo(
        left, centerY - halfH * 0.2f,
        centerX - halfW * 0.65f, top,
        centerX, top
    )
    path.close()

    // Subtle 3D volumetric gradient
    val brush = Brush.radialGradient(
        colors = listOf(
            baseColor,
            baseColor.copy(red = (baseColor.red * 0.95f), green = (baseColor.green * 0.95f), blue = (baseColor.blue * 0.95f)),
            Color(0xFFE2D6C0)
        ),
        center = Offset(centerX - width * 0.15f, centerY - height * 0.2f),
        radius = width * 0.85f
    )

    drawPath(path = path, brush = brush)

    // Soft rim highlight on top
    drawOval(
        color = Color.White.copy(alpha = 0.55f),
        topLeft = Offset(centerX - width * 0.25f, top + height * 0.08f),
        size = Size(width * 0.5f, height * 0.22f)
    )
}

private fun DrawScope.drawShinyEye(
    x: Float,
    y: Float,
    w: Float,
    h: Float
) {
    // Black oval
    drawOval(
        color = MeepEyeBlack,
        topLeft = Offset(x - w / 2f, y - h / 2f),
        size = Size(w, h)
    )
    // Big sparkle highlight (top-right)
    drawCircle(
        color = Color.White,
        radius = w * 0.28f,
        center = Offset(x + w * 0.15f, y - h * 0.16f)
    )
    // Small sparkle highlight (bottom-left)
    drawCircle(
        color = Color.White,
        radius = w * 0.14f,
        center = Offset(x - w * 0.16f, y + h * 0.2f)
    )
}

private fun DrawScope.drawSleepyEye(
    x: Float,
    y: Float,
    w: Float
) {
    val path = Path()
    path.moveTo(x - w / 2f, y)
    path.quadraticTo(x, y + w * 0.5f, x + w / 2f, y)
    drawPath(
        path = path,
        color = MeepEyeBlack,
        style = Stroke(width = 5f, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawMouth(
    x: Float,
    y: Float,
    w: Float,
    h: Float,
    isEating: Boolean,
    isSleeping: Boolean
) {
    if (isSleeping) {
        // Sleepy tiny smile
        val path = Path()
        path.moveTo(x - w * 0.035f, y)
        path.quadraticTo(x, y + h * 0.02f, x + w * 0.035f, y)
        drawPath(path, MeepEyeBlack, style = Stroke(width = 4f, cap = StrokeCap.Round))
    } else if (isEating) {
        // Open munching mouth
        drawOval(
            color = Color(0xFFD64550),
            topLeft = Offset(x - w * 0.06f, y),
            size = Size(w * 0.12f, h * 0.08f)
        )
    } else {
        // Cute happy curve (w style or open smile)
        val path = Path()
        val mouthW = w * 0.05f
        path.moveTo(x - mouthW, y)
        path.quadraticTo(x - mouthW / 2f, y + h * 0.025f, x, y)
        path.quadraticTo(x + mouthW / 2f, y + h * 0.025f, x + mouthW, y)
        drawPath(path, MeepEyeBlack, style = Stroke(width = 4f, cap = StrokeCap.Round))
    }
}

private fun DrawScope.drawHat(
    hatId: String,
    x: Float,
    y: Float,
    w: Float,
    h: Float
) {
    when (hatId) {
        "hat_sprout" -> {
            // Little green plant sprout
            val stemPath = Path()
            stemPath.moveTo(x, y + 25f)
            stemPath.quadraticTo(x - 5f, y + 10f, x, y)
            drawPath(stemPath, Color(0xFF4CAF50), style = Stroke(width = 6f, cap = StrokeCap.Round))
            // Left leaf
            drawOval(
                color = Color(0xFF66BB6A),
                topLeft = Offset(x - 22f, y - 6f),
                size = Size(20f, 12f)
            )
            // Right leaf
            drawOval(
                color = Color(0xFF81C784),
                topLeft = Offset(x + 2f, y - 8f),
                size = Size(20f, 12f)
            )
        }
        "hat_party" -> {
            // Party cone
            val cone = Path()
            cone.moveTo(x, y - 40f)
            cone.lineTo(x + 26f, y + 20f)
            cone.lineTo(x - 26f, y + 20f)
            cone.close()
            drawPath(cone, Color(0xFFFF5252))
            // Stripes
            val stripe = Path()
            stripe.moveTo(x - 14f, y - 5f)
            stripe.lineTo(x + 14f, y - 5f)
            stripe.lineTo(x + 20f, y + 8f)
            stripe.lineTo(x - 20f, y + 8f)
            stripe.close()
            drawPath(stripe, Color(0xFFFFD740))
            // Pompom on top
            drawCircle(Color(0xFF40C4FF), radius = 10f, center = Offset(x, y - 42f))
        }
        "hat_crown" -> {
            // Royal golden crown
            val crown = Path()
            val crownW = 54f
            val crownH = 34f
            crown.moveTo(x - crownW / 2f, y + 15f)
            crown.lineTo(x - crownW / 2f, y - crownH / 2f)
            crown.lineTo(x - crownW / 4f, y)
            crown.lineTo(x, y - crownH)
            crown.lineTo(x + crownW / 4f, y)
            crown.lineTo(x + crownW / 2f, y - crownH / 2f)
            crown.lineTo(x + crownW / 2f, y + 15f)
            crown.close()
            drawPath(crown, Color(0xFFFFD700))
            // Ruby gems on peaks
            drawCircle(Color(0xFFFF1744), radius = 5f, center = Offset(x, y - crownH))
            drawCircle(Color(0xFF00E676), radius = 4f, center = Offset(x - crownW / 2f, y - crownH / 2f))
            drawCircle(Color(0xFF00E676), radius = 4f, center = Offset(x + crownW / 2f, y - crownH / 2f))
        }
        "hat_straw" -> {
            // Straw hat
            drawOval(Color(0xFFE0C179), topLeft = Offset(x - 45f, y + 8f), size = Size(90f, 20f))
            drawOval(Color(0xFFC7A75B), topLeft = Offset(x - 26f, y - 10f), size = Size(52f, 26f))
            // Red ribbon
            drawOval(Color(0xFFE91E63), topLeft = Offset(x - 27f, y + 4f), size = Size(54f, 8f))
        }
        "hat_flower" -> {
            // Flower crown
            val flowerColors = listOf(Color(0xFFFF80AB), Color(0xFFFFEB3B), Color(0xFF80D8FF), Color(0xFFB388FF))
            for (i in -3..3) {
                val fx = x + i * 14f
                val fy = y + 15f - (i * i) * 0.7f
                drawCircle(Color(0xFF4CAF50), radius = 6f, center = Offset(fx, fy + 4f))
                drawCircle(flowerColors[(i + 4) % flowerColors.size], radius = 8f, center = Offset(fx, fy))
                drawCircle(Color.White, radius = 3f, center = Offset(fx, fy))
            }
        }
        "hat_bunny" -> {
            // Bunny ears
            val leftEar = Path()
            leftEar.moveTo(x - 22f, y + 15f)
            leftEar.cubicTo(x - 35f, y - 10f, x - 35f, y - 55f, x - 18f, y - 55f)
            leftEar.cubicTo(x - 5f, y - 55f, x - 10f, y - 10f, x - 14f, y + 15f)
            drawPath(leftEar, Color.White)
            // Left pink inner
            drawOval(Color(0xFFFFB2C9), topLeft = Offset(x - 26f, y - 48f), size = Size(10f, 36f))

            val rightEar = Path()
            rightEar.moveTo(x + 14f, y + 15f)
            rightEar.cubicTo(x + 10f, y - 10f, x + 5f, y - 55f, x + 18f, y - 55f)
            rightEar.cubicTo(x + 35f, y - 55f, x + 35f, y - 10f, x + 22f, y + 15f)
            drawPath(rightEar, Color.White)
            // Right pink inner
            drawOval(Color(0xFFFFB2C9), topLeft = Offset(x + 16f, y - 48f), size = Size(10f, 36f))
        }
        "hat_tophat" -> {
            // Dapper top hat
            drawOval(Color(0xFF263238), topLeft = Offset(x - 38f, y + 8f), size = Size(76f, 16f))
            drawRoundRect(
                Color(0xFF37474F),
                topLeft = Offset(x - 22f, y - 35f),
                size = Size(44f, 44f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Gold band
            drawRect(Color(0xFFFFD54F), topLeft = Offset(x - 22f, y + 3f), size = Size(44f, 6f))
        }
        "hat_chef" -> {
            // Chef toque
            drawRect(Color.White, topLeft = Offset(x - 24f, y), size = Size(48f, 18f))
            drawCircle(Color.White, radius = 18f, center = Offset(x - 16f, y - 8f))
            drawCircle(Color.White, radius = 22f, center = Offset(x, y - 16f))
            drawCircle(Color.White, radius = 18f, center = Offset(x + 16f, y - 8f))
        }
        "hat_detective" -> {
            // Sherlock deerstalker cap
            drawOval(Color(0xFF8D6E63), topLeft = Offset(x - 36f, y + 2f), size = Size(72f, 22f))
            drawArc(
                Color(0xFF6D4C41),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(x - 26f, y - 24f),
                size = Size(52f, 40f)
            )
            drawCircle(Color(0xFF4E342E), radius = 6f, center = Offset(x, y - 24f))
        }
    }
}

private fun DrawScope.drawGlasses(
    glassesId: String,
    x: Float,
    y: Float,
    eyeSpacing: Float,
    eyeWidth: Float
) {
    when (glassesId) {
        "glasses_round" -> {
            val r = eyeWidth * 0.85f
            drawCircle(Color(0xFF424242), radius = r, center = Offset(x - eyeSpacing, y), style = Stroke(4f))
            drawCircle(Color(0xFF424242), radius = r, center = Offset(x + eyeSpacing, y), style = Stroke(4f))
            drawLine(Color(0xFF424242), start = Offset(x - eyeSpacing + r, y), end = Offset(x + eyeSpacing - r, y), strokeWidth = 4f)
        }
        "glasses_sun" -> {
            // Star sunglasses
            val starColor = Color(0xFFFFB300)
            drawCircle(starColor, radius = eyeWidth * 0.9f, center = Offset(x - eyeSpacing, y))
            drawCircle(starColor, radius = eyeWidth * 0.9f, center = Offset(x + eyeSpacing, y))
            drawLine(Color(0xFF37474F), start = Offset(x - eyeSpacing, y), end = Offset(x + eyeSpacing, y), strokeWidth = 5f)
        }
        "glasses_heart" -> {
            // Heart specs
            drawCircle(Color(0xFFFF4081), radius = eyeWidth * 0.85f, center = Offset(x - eyeSpacing, y), style = Stroke(5f))
            drawCircle(Color(0xFFFF4081), radius = eyeWidth * 0.85f, center = Offset(x + eyeSpacing, y), style = Stroke(5f))
            drawLine(Color(0xFFFF4081), start = Offset(x - eyeSpacing + 12f, y), end = Offset(x + eyeSpacing - 12f, y), strokeWidth = 4f)
        }
        "glasses_monocle" -> {
            drawCircle(Color(0xFFFFD700), radius = eyeWidth * 0.85f, center = Offset(x + eyeSpacing, y), style = Stroke(4f))
            drawLine(Color(0xFFFFD700), start = Offset(x + eyeSpacing + eyeWidth * 0.85f, y), end = Offset(x + eyeSpacing + eyeWidth * 0.85f + 15f, y + 25f), strokeWidth = 2f)
        }
    }
}

private fun DrawScope.drawNeckAccessory(
    neckId: String,
    x: Float,
    y: Float,
    w: Float
) {
    when (neckId) {
        "neck_bowtie" -> {
            val bow = Path()
            bow.moveTo(x, y)
            bow.lineTo(x - 18f, y - 10f)
            bow.lineTo(x - 18f, y + 10f)
            bow.close()
            bow.moveTo(x, y)
            bow.lineTo(x + 18f, y - 10f)
            bow.lineTo(x + 18f, y + 10f)
            bow.close()
            drawPath(bow, Color(0xFFE53935))
            drawCircle(Color(0xFFC62828), radius = 6f, center = Offset(x, y))
        }
        "neck_scarf" -> {
            drawRoundRect(
                Color(0xFF00ACC1),
                topLeft = Offset(x - 35f, y - 8f),
                size = Size(70f, 16f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            // Hanging tail
            drawRoundRect(
                Color(0xFF00838F),
                topLeft = Offset(x + 8f, y),
                size = Size(16f, 32f),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }
        "neck_bell" -> {
            drawOval(Color(0xFFD81B60), topLeft = Offset(x - 30f, y - 5f), size = Size(60f, 10f))
            drawCircle(Color(0xFFFFD700), radius = 9f, center = Offset(x, y + 6f))
            drawCircle(Color(0xFF5D4037), radius = 2.5f, center = Offset(x, y + 8f))
        }
    }
}

private fun DrawScope.drawCape(
    centerX: Float,
    centerY: Float,
    w: Float,
    h: Float,
    rotationDegrees: Float
) {
    val cape = Path()
    cape.moveTo(centerX - w * 0.28f, centerY + h * 0.1f)
    cape.lineTo(centerX + w * 0.28f, centerY + h * 0.1f)
    cape.lineTo(centerX + w * 0.42f, centerY + h * 0.52f)
    cape.lineTo(centerX - w * 0.42f, centerY + h * 0.52f)
    cape.close()
    drawPath(cape, Color(0xFFD32F2F))
}
