package org.xmsleep.app.ui.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.delay
import org.xmsleep.app.MainActivity
import org.xmsleep.app.R

private const val NOTIFICATION_CHANNEL_ID = "tomato_timer_channel"
private const val NOTIFICATION_ID = 1003  // 番茄钟通知（1001 被 MusicService，1002 被 BreathingService 占用）

@Composable
fun TomatoTimerView(
    modifier: Modifier = Modifier,
    focusDurationMinutes: Int = 25,
    breakDurationMinutes: Int = 5,
    onTimerComplete: () -> Unit = {}
) {
    val context = LocalContext.current
    val view = LocalView.current
    
    var isRunning by remember { mutableStateOf(false) }
    var isBreak by remember { mutableStateOf(false) }
    var selectedFocusMinutes by remember { mutableIntStateOf(focusDurationMinutes) }
    var timeLeftMillis by remember { 
        mutableLongStateOf(selectedFocusMinutes * 60 * 1000L) 
    }
    var todayCompletedPomodoros by remember { mutableIntStateOf(0) }
    
    val totalMillis = if (isBreak) {
        5 * 60 * 1000L
    } else {
        selectedFocusMinutes * 60 * 1000L
    }
    
    val progress = if (totalMillis > 0) {
        timeLeftMillis.toFloat() / totalMillis.toFloat()
    } else {
        1f
    }
    
    // 创建通知渠道
    LaunchedEffect(Unit) {
        createNotificationChannel(context)
    }
    
    // 屏幕常亮控制
    DisposableEffect(isRunning) {
        if (isRunning) {
            view.keepScreenOn = true
            showTimerNotification(context, timeLeftMillis, isBreak)
        } else {
            view.keepScreenOn = false
            cancelNotification(context)
        }
        onDispose {
            view.keepScreenOn = false
            cancelNotification(context)
        }
    }
    
    // 计时逻辑
    LaunchedEffect(isRunning) {
        while (isRunning && timeLeftMillis > 0) {
            delay(1000)
            timeLeftMillis -= 1000
            updateNotification(context, timeLeftMillis, isBreak)
        }
        if (timeLeftMillis <= 0 && isRunning) {
            isRunning = false
            if (!isBreak) {
                todayCompletedPomodoros++
            }
            showCompletionNotification(context, isBreak)
            isBreak = !isBreak
            timeLeftMillis = if (isBreak) {
                5 * 60 * 1000L
            } else {
                selectedFocusMinutes * 60 * 1000L
            }
            onTimerComplete()
        }
    }
    
    val primaryColor = MaterialTheme.colorScheme.primary
    val breakColor = MaterialTheme.colorScheme.tertiary // 使用主题的 tertiary 颜色表示休息
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 模块1: 今日完成数 + 时长选择
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 今日完成数
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Text(
                    text = stringResource(R.string.tomato_today_completed, todayCompletedPomodoros),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 时长选择
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DurationChip(
                    text = "25",
                    selected = selectedFocusMinutes == 25,
                    onClick = { 
                        if (!isRunning) {
                            selectedFocusMinutes = 25
                            timeLeftMillis = 25 * 60 * 1000L
                        }
                    }
                )
                DurationChip(
                    text = "30",
                    selected = selectedFocusMinutes == 30,
                    onClick = { 
                        if (!isRunning) {
                            selectedFocusMinutes = 30
                            timeLeftMillis = 30 * 60 * 1000L
                        }
                    }
                )
                DurationChip(
                    text = "45",
                    selected = selectedFocusMinutes == 45,
                    onClick = { 
                        if (!isRunning) {
                            selectedFocusMinutes = 45
                            timeLeftMillis = 45 * 60 * 1000L
                        }
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(60.dp))
        
        // 模块2: 时间显示区域
        Box(
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isBreak) stringResource(R.string.tomato_break) else stringResource(R.string.tomato_focus),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isBreak) breakColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatTimeMillis(timeLeftMillis),
                    fontSize = 100.sp,
                    fontWeight = FontWeight.W900,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                // 进度横线
                Canvas(
                    modifier = Modifier
                        .width(100.dp)
                        .height(6.dp)
                ) {
                    // 背景横线
                    drawRoundRect(
                        color = Color.Gray.copy(alpha = 0.2f),
                        size = Size(size.width, size.height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                    )
                    // 进度横线
                    drawRoundRect(
                        color = if (isBreak) breakColor else primaryColor,
                        size = Size(size.width * progress, size.height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(60.dp))
        
        // 模块3: 控制按钮
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 重置按钮
            FilledTonalIconButton(
                onClick = {
                    isRunning = false
                    isBreak = false
                    timeLeftMillis = focusDurationMinutes * 60 * 1000L
                },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.tomato_reset)
                )
            }
            
            // 开始/暂停按钮
            FilledIconButton(
                onClick = { isRunning = !isRunning },
                modifier = Modifier.size(108.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isBreak) breakColor else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) stringResource(R.string.tomato_pause) else stringResource(R.string.tomato_start),
                    modifier = Modifier.size(54.dp)
                )
            }
            
            // 占位保持对称
            Box(modifier = Modifier.size(56.dp))
        }
    }
}

@Composable
private fun DurationChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.height(36.dp)
    )
}

private fun formatTimeMillis(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "番茄计时器",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "显示番茄计时器状态"
            setShowBadge(false)
        }
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}

private fun showTimerNotification(context: Context, timeLeftMillis: Long, isBreak: Boolean) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    
    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setContentTitle(if (isBreak) "休息中" else "专注中")
        .setContentText(formatTimeMillis(timeLeftMillis))
        .setSmallIcon(R.drawable.ic_notification)
        .setContentIntent(pendingIntent)
        .setOngoing(true)
        .setSilent(true)
        .build()
    
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.notify(NOTIFICATION_ID, notification)
}

private fun updateNotification(context: Context, timeLeftMillis: Long, isBreak: Boolean) {
    showTimerNotification(context, timeLeftMillis, isBreak)
}

private fun showCompletionNotification(context: Context, isBreak: Boolean) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    
    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setContentTitle(if (isBreak) "休息结束" else "专注完成")
        .setContentText(if (isBreak) "开始新的专注吧！" else "干得不错，休息一下吧！")
        .setSmallIcon(R.drawable.ic_notification)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()
    
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.notify(NOTIFICATION_ID, notification)
}

private fun cancelNotification(context: Context) {
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.cancel(NOTIFICATION_ID)
}
