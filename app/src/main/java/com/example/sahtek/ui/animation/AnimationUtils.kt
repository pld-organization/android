package com.example.sahtek.ui.animation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutExpo
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

// ============ ANIMATION TIMING ============
object AnimationDurations {
    const val FAST = 200
    const val NORMAL = 300
    const val SLOW = 500
    const val VERY_SLOW = 800
}

// ============ ANIMATION SPECS ============
object AnimationSpecs {
    fun quickEase(): AnimationSpec<Float> = tween(
        durationMillis = AnimationDurations.FAST,
        easing = EaseInOutCubic
    )

    fun smoothEase(): AnimationSpec<Float> = tween(
        durationMillis = AnimationDurations.NORMAL,
        easing = EaseInOutCubic
    )

    fun smoothEaseExpanded(): AnimationSpec<Float> = tween(
        durationMillis = AnimationDurations.SLOW,
        easing = EaseInOutExpo
    )

    fun elasticSpring(): AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    fun smoothSpring(): AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )
}

// ============ FADE IN ANIMATION ============
suspend fun Animatable<Float, *>.animateFadeIn(
    duration: Int = AnimationDurations.NORMAL
) {
    animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = duration, easing = EaseInOutCubic)
    )
}

suspend fun Animatable<Float, *>.animateFadeOut(
    duration: Int = AnimationDurations.NORMAL
) {
    animateTo(
        targetValue = 0f,
        animationSpec = tween(durationMillis = duration, easing = EaseInOutCubic)
    )
}

// ============ SCALE ANIMATION ============
suspend fun Animatable<Float, *>.animateScaleIn(
    duration: Int = AnimationDurations.NORMAL
) {
    animateTo(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
}

// ============ SLIDE ANIMATION ============
suspend fun Animatable<Float, *>.animateSlideInFromBottom(
    duration: Int = AnimationDurations.NORMAL
) {
    animateTo(
        targetValue = 0f,
        animationSpec = tween(durationMillis = duration, easing = EaseInOutCubic)
    )
}

// ============ MODIFIER EXTENSIONS ============

/**
 * Add fade in animation on composition
 */
fun Modifier.fadeInAnimation(
    duration: Int = AnimationDurations.NORMAL,
    delay: Int = 0
): Modifier = composed {
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        alpha.animateTo(
            1f,
            animationSpec = tween(
                durationMillis = duration,
                delayMillis = delay,
                easing = EaseInOutCubic
            )
        )
    }
    
    graphicsLayer(alpha = alpha.value)
}

/**
 * Add scale in animation on composition
 */
fun Modifier.scaleInAnimation(
    duration: Int = AnimationDurations.NORMAL,
    delay: Int = 0
): Modifier = composed {
    val scale = remember { Animatable(0.8f) }
    
    LaunchedEffect(Unit) {
        scale.animateTo(
            1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }
    
    graphicsLayer(
        scaleX = scale.value,
        scaleY = scale.value
    )
}

/**
 * Add slide up animation from bottom
 */
fun Modifier.slideUpAnimation(
    duration: Int = AnimationDurations.NORMAL,
    delay: Int = 0
): Modifier = composed {
    val offsetY = remember { Animatable(100f) }
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        offsetY.animateTo(
            0f,
            animationSpec = tween(
                durationMillis = duration,
                delayMillis = delay,
                easing = EaseInOutCubic
            )
        )
        alpha.animateTo(1f, animationSpec = tween(durationMillis = duration / 2))
    }
    
    graphicsLayer(
        translationY = offsetY.value,
        alpha = alpha.value
    )
}

/**
 * Add press animation effect
 */
fun Modifier.pressAnimation(): Modifier = composed {
    val scale = remember { Animatable(1f) }
    val interactionSource = remember { MutableInteractionSource() }
    
    this.clickable(
        interactionSource = interactionSource,
        indication = ripple(bounded = true),
        onClick = {}
    )
    
    graphicsLayer(
        scaleX = scale.value,
        scaleY = scale.value
    )
}

/**
 * Animate color changes smoothly
 */
@Composable
fun animateColorChange(
    targetColor: Color,
    duration: Int = AnimationDurations.NORMAL
): Color {
    return animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = duration, easing = EaseInOutCubic),
        label = "Color Animation"
    ).value
}

/**
 * Shimmer loading animation
 */
fun Modifier.shimmerLoadingAnimation(): Modifier = composed {
    val shimmerTranslateAnimation = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        shimmerTranslateAnimation.animateTo(
            targetValue = 1000f,
            animationSpec = tween(durationMillis = 1200, easing = EaseInOutCubic)
        )
        shimmerTranslateAnimation.snapTo(0f)
    }
    
    this.background(color = Color(0xFFE2E8F0))
}

/**
 * Rotate animation (for loading spinners)
 */
fun Modifier.rotateAnimation(
    duration: Int = 1200
): Modifier = composed {
    val rotation = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = tween(durationMillis = duration, easing = androidx.compose.animation.core.LinearEasing)
        )
        rotation.snapTo(0f)
    }
    
    graphicsLayer(rotationZ = rotation.value)
}
