package org.xmsleep.app.theme

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.materialkolor.dynamicColorScheme

val DefaultThemeColor = Color(0xFF6750A4)

/**
 * XMSLEEP 应用主题
 * 使用 MaterialKolor 生成动态配色方案（简化版）
 * 
 * 动态颜色逻辑（参考 OpenTune）：
 * - 如果 seedColor == DefaultThemeColor 且 useDynamicColor = true，使用系统动态颜色
 * - 否则使用自定义 seedColor 生成主题
 */
@Composable
fun XMSLEEPTheme(
    isDark: Boolean,
    seedColor: Color = DefaultThemeColor,
    useDynamicColor: Boolean = false,
    useBlackBackground: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    
    // 设置系统状态栏样式
    val activity = context as? ComponentActivity
    DisposableEffect(activity, isDark) {
        if (activity != null) {
            if (isDark) {
                activity.enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
                    navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
                )
            } else {
                activity.enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.light(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ),
                    navigationBarStyle = SystemBarStyle.light(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ),
                )
            }
        }
        onDispose { }
    }
    
    // 生成配色方案
    val colorScheme = remember(isDark, useBlackBackground, seedColor, useDynamicColor) {
        // 如果启用动态颜色且系统支持（Android 12+），使用系统动态颜色
        if (useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ 使用系统动态颜色（从壁纸提取）
            if (isDark) {
                dynamicDarkColorScheme(context).pureBlack(useBlackBackground, isDark)
            } else {
                dynamicLightColorScheme(context).pureBlack(false, isDark)
            }
        } else {
            // 使用自定义主题色（使用 MaterialKolor）
            dynamicColorScheme(
                seedColor = seedColor,
                isDark = isDark,
                isAmoled = false
            ).pureBlack(useBlackBackground, isDark)
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

/**
 * 应用纯黑背景（深色模式下）
 */
fun ColorScheme.pureBlack(apply: Boolean, isDarkTheme: Boolean) =
    if (apply && isDarkTheme) {
        copy(
            surface = Color.Black,
            background = Color.Black,
            surfaceContainer = Color.Black,
            surfaceContainerLow = Color.Black,
            surfaceContainerLowest = Color.Black,
        )
    } else {
        this
    }
