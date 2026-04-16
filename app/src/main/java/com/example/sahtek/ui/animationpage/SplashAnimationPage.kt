package com.example.sahtek.ui.animationpage

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashAnimationPage(onAnimationComplete: () -> Unit = {}) {
    val primaryBlue = Color(0xFF1976D2)
    val secondaryBlue = Color(0xFF42A5F5)
    val accentCyan = Color(0xFF00BCD4)
    val lightBackground = Color(0xFFF8FAFC)

    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val ecgAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(20f) }
    val taglineAlpha = remember { Animatable(0f) }

    var heartbeatProgress by remember { mutableStateOf(0f) }
    val heartbeatAnim = rememberInfiniteTransition(label = "Heartbeat")
    val pulseScale by heartbeatAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    LaunchedEffect(Unit) {
        // Phase 1: Logo Entry
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(1000, easing = EaseOutQuart))
        }
        launch {
            logoScale.animateTo(1f, animationSpec = tween(1200, easing = EaseOutBack))
        }

        delay(600)
        // Phase 2: ECG and Text
        launch {
            ecgAlpha.animateTo(1f, animationSpec = tween(800))
        }
        launch {
            textAlpha.animateTo(1f, animationSpec = tween(1000))
            textOffsetY.animateTo(0f, animationSpec = tween(1000, easing = EaseOutCubic))
        }

        delay(800)
        // Phase 3: Tagline
        launch {
            taglineAlpha.animateTo(0.7f, animationSpec = tween(1200))
        }

        // Heartbeat progress animation
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < 3500) {
            heartbeatProgress = ((System.currentTimeMillis() - startTime) % 2000) / 2000f
            delay(16)
        }

        delay(500)
        onAnimationComplete()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = lightBackground
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, lightBackground),
                        center = Offset.Unspecified,
                        radius = Float.POSITIVE_INFINITY
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .graphicsLayer {
                            scaleX = logoScale.value * pulseScale
                            scaleY = logoScale.value * pulseScale
                            alpha = logoAlpha.value
                        }
                        .drawBehind {
                            drawModernSLogo(primaryBlue, secondaryBlue, accentCyan)
                            if (ecgAlpha.value > 0) {
                                drawProfessionalECG(
                                    progress = heartbeatProgress,
                                    color = secondaryBlue,
                                    alpha = ecgAlpha.value
                                )
                            }
                        }
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "SAHTEK",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    color = primaryBlue,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = textAlpha.value
                            translationY = textOffsetY.value
                        }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Votre santé, notre priorité",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                    color = Color.Gray,
                    modifier = Modifier.graphicsLayer { alpha = taglineAlpha.value }
                )
            }
        }
    }
}

private fun DrawScope.drawModernSLogo(
    primary: Color,
    secondary: Color,
    accent: Color
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val scale = size.width / 200f
    val strokeWidth = 16f * scale

    // Background Glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(secondary.copy(alpha = 0.15f), Color.Transparent),
            center = center,
            radius = size.width / 1.5f
        )
    )

    val path = Path().apply {
        moveTo(centerX + 40f * scale, centerY - 50f * scale)
        cubicTo(
            centerX + 70f * scale, centerY - 80f * scale,
            centerX - 10f * scale, centerY - 90f * scale,
            centerX - 30f * scale, centerY - 50f * scale
        )
        cubicTo(
            centerX - 50f * scale, centerY - 10f * scale,
            centerX + 50f * scale, centerY + 10f * scale,
            centerX + 30f * scale, centerY + 50f * scale
        )
        cubicTo(
            centerX + 10f * scale, centerY + 90f * scale,
            centerX - 70f * scale, centerY + 80f * scale,
            centerX - 40f * scale, centerY + 50f * scale
        )
    }

    // Shadow/Depth effect
    withTransform({
        translate(0f, 4f)
    }) {
        drawPath(
            path = path,
            color = primary.copy(alpha = 0.1f),
            style = Stroke(width = strokeWidth + 4f, cap = StrokeCap.Round)
        )
    }

    // Main Logo Path
    drawPath(
        path = path,
        brush = Brush.linearGradient(
            colors = listOf(primary, secondary, accent),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        ),
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

private fun DrawScope.drawProfessionalECG(
    progress: Float,
    color: Color,
    alpha: Float
) {
    val width = size.width * 0.8f
    val startX = (size.width - width) / 2
    val centerY = size.height / 2 + 10f
    val strokeWidth = 3f

    val path = Path().apply {
        moveTo(startX, centerY)
        val points = 100
        for (i in 0..points) {
            val x = startX + (i.toFloat() / points) * width
            val relX = i.toFloat() / points
            
            // Professional ECG Waveform calculation
            val y = centerY + calculateProfessionalHeartbeat(relX, progress) * 25f
            
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
    }

    // Glow Effect
    drawPath(
        path = path,
        color = color.copy(alpha = 0.2f * alpha),
        style = Stroke(width = strokeWidth * 3, cap = StrokeCap.Round)
    )

    // Main Line
    drawPath(
        path = path,
        color = color.copy(alpha = alpha),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}

private fun calculateProfessionalHeartbeat(relX: Float, progress: Float): Float {
    val t = (relX - progress + 1f) % 1f
    return when {
        t < 0.1f -> 0f
        t < 0.15f -> -0.1f * sin((t - 0.1f) / 0.05f * PI).toFloat() // P wave
        t < 0.2f -> 0f
        t < 0.22f -> 0.2f * sin((t - 0.2f) / 0.02f * PI).toFloat() // Q wave
        t < 0.25f -> -1f * sin((t - 0.22f) / 0.03f * PI).toFloat() // R wave
        t < 0.28f -> 0.3f * sin((t - 0.25f) / 0.03f * PI).toFloat() // S wave
        t < 0.35f -> 0f
        t < 0.45f -> -0.2f * sin((t - 0.35f) / 0.1f * PI).toFloat() // T wave
        else -> 0f
    }
}
