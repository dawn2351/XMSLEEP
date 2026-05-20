package org.xmsleep.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.xmsleep.app.R
import org.xmsleep.app.i18n.LanguageManager
import org.xmsleep.app.ui.BackgroundSelection

/**
 * 背景选择对话框
 * 
 * 显示可选的背景动画选项，支持实时预览和选择
 * 底部提供主题颜色选择（仅无背景时可用）
 * 
 * @param currentSelection 当前选中的背景
 * @param paletteColors 调色板颜色列表
 * @param currentColor 当前主题色
 * @param onSelectionChange 选择变化时的回调（用于实时预览）
 * @param onColorChange 主题色变化时的回调
 * @param onDismiss 关闭对话框的回调
 * @param onConfirm 确认选择的回调
 * @param currentLanguage 当前语言（用于强制重组以更新文本）
 */
@Composable
fun BackgroundSelectionDialog(
    currentSelection: BackgroundSelection,
    paletteColors: List<Color>,
    currentColor: Color,
    onSelectionChange: (BackgroundSelection) -> Unit,
    onColorChange: (Color) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    currentLanguage: org.xmsleep.app.i18n.LanguageManager.Language? = null
) {
    val context = LocalContext.current
    val localizedContext = remember(currentLanguage) {
        if (currentLanguage != null) LanguageManager.createLocalizedContext(context, currentLanguage) else context
    }

    // 所有可选的背景选项
    val backgroundOptions = remember {
        listOf(
            BackgroundSelection.None,
            BackgroundSelection.Background1,
            BackgroundSelection.Background2,
            BackgroundSelection.Background3,
            BackgroundSelection.Background4,
            BackgroundSelection.Background5,
            BackgroundSelection.Background6,
            BackgroundSelection.Background7
        )
    }

    // 获取字符串资源，使用 localizedContext 确保多语言实时生效
    val dialogTitle = remember(currentLanguage) { localizedContext.getString(R.string.select_background) }
    val confirmText = remember(currentLanguage) { localizedContext.getString(android.R.string.ok) }
    val cancelText = remember(currentLanguage) { localizedContext.getString(android.R.string.cancel) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = dialogTitle)
        },
        text = {
            val isNoneSelected = currentSelection == BackgroundSelection.None
            val colorSectionTitle = remember(currentLanguage) { localizedContext.getString(R.string.theme_color) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
            ) {
                // 背景选择网格（固定高度，可内部滚动）
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 380.dp)
                ) {
                    items(backgroundOptions) { option ->
                        BackgroundOptionItem(
                            option = option,
                            isSelected = option == currentSelection,
                            onClick = { onSelectionChange(option) },
                            currentLanguage = currentLanguage
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 主题颜色选择区（仅无背景时显示）
                if (isNoneSelected) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = colorSectionTitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(paletteColors) { color ->
                                val isSelected = currentColor == color
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .then(
                                            if (isSelected) {
                                                Modifier.border(
                                                    2.dp,
                                                    MaterialTheme.colorScheme.primary,
                                                    CircleShape
                                                )
                                            } else {
                                                Modifier
                                            }
                                        )
                                        .clickable { onColorChange(color) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (color.luminance() > 0.35f) {
                                                Color.Black.copy(alpha = 0.7f)
                                            } else {
                                                Color.White
                                            },
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = cancelText)
            }
        }
    )
}

/**
 * 背景选项项
 * 
 * 显示单个背景选项，包括缩略图预览和名称
 * 
 * @param option 背景选项
 * @param isSelected 是否选中
 * @param onClick 点击回调
 * @param currentLanguage 当前语言（用于强制重组以更新文本）
 */
@Composable
private fun BackgroundOptionItem(
    option: BackgroundSelection,
    isSelected: Boolean,
    onClick: () -> Unit,
    currentLanguage: org.xmsleep.app.i18n.LanguageManager.Language? = null
) {
    val context = LocalContext.current
    val localizedContext = remember(currentLanguage) {
        if (currentLanguage != null) LanguageManager.createLocalizedContext(context, currentLanguage) else context
    }

    // 获取显示名称和选中文本，使用 localizedContext 确保多语言实时生效
    val displayName = remember(option, currentLanguage) { option.getDisplayName(localizedContext) }
    val selectedText = remember(currentLanguage) { localizedContext.getString(R.string.selected) }
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 如果不是 None，显示缩略图
            if (option != BackgroundSelection.None && option.resourceId != null) {
                AnimatedWebPImage(
                    drawableResId = option.resourceId,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    isPlaying = false // 缩略图不播放动画
                )
            } else {
                // 无背景选项显示占位图标
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.Icons.Default.HideImage,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            // 选中状态指示器（勾选图标）
            if (isSelected) {
                androidx.compose.material3.Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.Check,
                            contentDescription = selectedText,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
