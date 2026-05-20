package org.xmsleep.app.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.materialkolor.hct.Hct
import com.materialkolor.ktx.toHct
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.xmsleep.app.R

/**
 * 音频可视化器组件（三条竖线动画）
 */
@Composable
fun AudioVisualizer(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    color: Color = MaterialTheme.colorScheme.primary,
    barCount: Int = 3
) {
    val bar1Height = remember { Animatable(0.25f) }
    val bar2Height = remember { Animatable(0.25f) }
    val bar3Height = remember { Animatable(0.25f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            launch {
                bar1Height.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(400, easing = EaseInOut),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            launch {
                delay(75)
                bar2Height.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(450, easing = EaseInOut),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            launch {
                delay(150)
                bar3Height.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(500, easing = EaseInOut),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
        } else {
            bar1Height.animateTo(0.25f, animationSpec = tween(200, easing = EaseInOut))
            bar2Height.animateTo(0.25f, animationSpec = tween(200, easing = EaseInOut))
            bar3Height.animateTo(0.25f, animationSpec = tween(200, easing = EaseInOut))
        }
    }

    Canvas(modifier = modifier) {
        val barSpacing = 3.dp.toPx()
        val barWidth = (size.width - barSpacing * (barCount - 1)) / barCount
        val minHeight = size.height * 0.25f
        val maxHeight = size.height
        val heights = listOf(bar1Height.value, bar2Height.value, bar3Height.value)
        val cornerRadius = barWidth / 2
        for (i in 0 until barCount) {
            val x = i * (barWidth + barSpacing) + barWidth / 2
            val heightRatio = heights[i].coerceIn(0.25f, 1f)
            val barHeight = minHeight + (maxHeight - minHeight) * heightRatio
            val topY = size.height - barHeight
            drawRoundRect(
                color = color,
                topLeft = Offset(x - barWidth / 2, topY),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
        }
    }
}

/**
 * 空状态动画组件（Lottie，主题色适配）
 */
@Composable
fun EmptyStateAnimation(
    modifier: Modifier = Modifier,
    animationSize: Dp = 240.dp
) {
    val colorScheme = MaterialTheme.colorScheme
    val backgroundHct = colorScheme.background.toHct()
    val isDark = backgroundHct.tone < 50.0
    val primaryHct = colorScheme.primary.toHct()

    val sz = animationSize
    val darkColor = Color(Hct.from(primaryHct.hue, primaryHct.chroma.coerceAtMost(35.0), if (isDark) 55.0 else 45.0).toInt())
    val mediumColor = Color(Hct.from(primaryHct.hue, primaryHct.chroma.coerceAtMost(32.0), if (isDark) 62.0 else 65.0).toInt())
    val lightColor = Color(Hct.from(primaryHct.hue, primaryHct.chroma.coerceAtMost(30.0), if (isDark) 68.0 else 75.0).toInt())
    val backgroundColor = if (isDark) {
        Color(Hct.from(primaryHct.hue, primaryHct.chroma.coerceAtMost(35.0), 28.0).toInt())
    } else {
        Color(Hct.from(primaryHct.hue, primaryHct.chroma.coerceAtMost(20.0), 96.0).toInt())
    }
    val secondaryHct = colorScheme.secondary.toHct()
    val secondaryColor = Color(Hct.from(secondaryHct.hue, secondaryHct.chroma.coerceAtMost(28.0), secondaryHct.tone).toInt())
    val darkGrayColor = if (isDark) colorScheme.onSurface.copy(alpha = 0.87f) else colorScheme.onSurface.copy(alpha = 0.6f)
    val mediumGrayColor = if (isDark) colorScheme.onSurfaceVariant.copy(alpha = 0.7f) else colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    val lightGrayColor = if (isDark) colorScheme.surfaceVariant.copy(alpha = 0.5f) else colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val strokeColor = if (isDark) colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else colorScheme.outline

    AndroidView(
        factory = { ctx ->
            LottieAnimationView(ctx).apply {
                setAnimation(R.raw.empty_state)
                repeatCount = LottieDrawable.INFINITE
                playAnimation()
            }
        },
        update = { view ->
            view.addValueCallback(
                KeyPath("**"),
                LottieProperty.COLOR,
                ThemeColorCallback(
                    darkColor = darkColor.toArgb(),
                    mediumColor = mediumColor.toArgb(),
                    lightColor = lightColor.toArgb(),
                    secondaryColor = secondaryColor.toArgb(),
                    backgroundColor = backgroundColor.toArgb(),
                    darkGrayColor = darkGrayColor.toArgb(),
                    mediumGrayColor = mediumGrayColor.toArgb(),
                    lightGrayColor = lightGrayColor.toArgb(),
                    primaryHct = primaryHct,
                    isDarkMode = isDark
                )
            )
            val strokeArgb = if (strokeColor.alpha < 1.0f) {
                val alpha = (strokeColor.alpha * 255).toInt()
                val rgb = strokeColor.toArgb() and 0xFFFFFF
                (alpha shl 24) or rgb
            } else {
                strokeColor.toArgb()
            }
            view.addValueCallback(KeyPath("**"), LottieProperty.STROKE_COLOR, LottieValueCallback(strokeArgb))
            view.invalidate()
        },
        modifier = with(Modifier) { modifier.then(size(sz)) }
    )
}

/**
 * 箭头方向枚举
 */
enum class ChevronDirection {
    VERTICAL,
    HORIZONTAL
}

/**
 * 自定义箭头图标（SVG 路径，支持上/下/左/右）
 */
@Composable
fun CustomChevronIcon(
    isExpanded: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    direction: ChevronDirection = ChevronDirection.VERTICAL
) {
    Canvas(modifier = modifier) {
        val svgWidth = 532f
        val svgHeight = 149f
        val scaleX = size.width / svgWidth
        val scaleY = size.height / svgHeight
        val scale = minOf(scaleX, scaleY)
        val offsetX = (size.width - svgWidth * scale) / 2
        val offsetY = (size.height - svgHeight * scale) / 2

        fun sx(v: Float) = v * scale + offsetX
        fun sy(v: Float) = v * scale + offsetY

        val path = Path().apply {
            moveTo(sx(223.598f), sy(6.42769f))
            cubicTo(sx(251.054f), sy(-2.14256f), sx(280.469f), sy(-2.14257f), sx(307.925f), sy(6.42769f))
            lineTo(sx(502.378f), sy(67.1259f))
            cubicTo(sx(524.256f), sy(73.9555f), sx(536.456f), sy(97.2288f), sx(529.627f), sy(119.107f))
            cubicTo(sx(522.797f), sy(140.986f), sx(499.524f), sy(153.186f), sx(477.645f), sy(146.356f))
            lineTo(sx(283.193f), sy(85.6572f))
            cubicTo(sx(271.842f), sy(82.114f), sx(259.681f), sy(82.114f), sx(248.33f), sy(85.6572f))
            lineTo(sx(53.8777f), sy(146.356f))
            cubicTo(sx(31.9991f), sy(153.186f), sx(8.72577f), sy(140.986f), sx(1.89622f), sy(119.107f))
            cubicTo(sx(-4.93322f), sy(97.2288f), sx(7.26671f), sy(73.9555f), sx(29.1452f), sy(67.1259f))
            lineTo(sx(223.598f), sy(6.42769f))
            close()
        }

        val rotationAngle = when (direction) {
            ChevronDirection.VERTICAL -> if (isExpanded) 180f else 0f
            ChevronDirection.HORIZONTAL -> if (isExpanded) 90f else -90f
        }
        val centerX = size.width / 2
        val centerY = size.height / 2

        if (rotationAngle != 0f) {
            val matrix = Matrix().apply {
                reset()
                translate(centerX, centerY)
                rotateZ(rotationAngle)
                translate(-centerX, -centerY)
            }
            val rotatedPath = Path()
            rotatedPath.addPath(path, Offset.Zero)
            rotatedPath.transform(matrix)
            drawPath(path = rotatedPath, color = color)
        } else {
            drawPath(path = path, color = color)
        }
    }
}
