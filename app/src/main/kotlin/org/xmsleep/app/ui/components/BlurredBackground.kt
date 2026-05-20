package org.xmsleep.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 半透明背景组件（模拟毛玻璃效果）
 * 
 * 注意：此组件已被弃用，现在使用 Haze 库实现真正的毛玻璃效果
 * 
 * 真正的毛玻璃效果实现位于 MainScreen.kt 中：
 * - 使用 Modifier.haze(state = hazeState) 标记内容源
 * - 使用 Modifier.hazeChild(state = hazeState, style = HazeStyle(...)) 应用模糊效果
 * 
 * Haze 库提供了真正的 backdrop blur（背景模糊）效果：
 * - 背景内容被模糊，但导航栏本身保持清晰
 * - 支持自定义模糊半径、色调和噪点
 * - 性能优化，适用于 Android 12+ 设备
 * 
 * 这个实现使用半透明背景 + 边框来模拟毛玻璃效果（仅作为后备方案）
 */
@Deprecated(
    message = "Use Haze library for true backdrop blur effect",
    replaceWith = ReplaceWith("Modifier.hazeChild(state, style)")
)
@Composable
fun BlurredBackground(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 32.dp,
    backgroundColor: Color = Color.White.copy(alpha = 0.85f),
    borderColor: Color = Color.White.copy(alpha = 0.2f)
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(
                width = 0.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
    )
}
