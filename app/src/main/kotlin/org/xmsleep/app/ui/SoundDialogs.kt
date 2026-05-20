package org.xmsleep.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.xmsleep.app.R
import org.xmsleep.app.audio.AudioManager

/**
 * 音量调节对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeDialog(
    sound: AudioManager.Sound,
    currentVolume: Float,
    onDismiss: () -> Unit,
    onVolumeChange: (Float) -> Unit
) {
    val context = LocalContext.current
    var volume by remember { mutableStateOf(currentVolume) }

    val soundName = when (sound) {
        AudioManager.Sound.UMBRELLA_RAIN -> context.getString(R.string.sound_umbrella_rain)
        AudioManager.Sound.ROWING -> context.getString(R.string.sound_rowing)
        AudioManager.Sound.OFFICE -> context.getString(R.string.sound_office)
        AudioManager.Sound.LIBRARY -> context.getString(R.string.sound_library)
        AudioManager.Sound.HEAVY_RAIN -> context.getString(R.string.sound_heavy_rain)
        AudioManager.Sound.TYPEWRITER -> context.getString(R.string.sound_typewriter)
        AudioManager.Sound.THUNDER -> context.getString(R.string.sound_thunder)
        AudioManager.Sound.CLOCK -> context.getString(R.string.sound_clock)
        AudioManager.Sound.FOREST_BIRDS -> context.getString(R.string.sound_forest_birds)
        AudioManager.Sound.DRIFTING -> context.getString(R.string.sound_drifting)
        AudioManager.Sound.CAMPFIRE -> context.getString(R.string.sound_campfire)
        AudioManager.Sound.WIND -> context.getString(R.string.sound_wind)
        AudioManager.Sound.KEYBOARD -> context.getString(R.string.sound_keyboard)
        AudioManager.Sound.SNOW_WALKING -> context.getString(R.string.sound_snow_walking)
        else -> context.getString(R.string.sound_default)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(soundName) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    context.getString(R.string.adjust_volume),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = volume,
                        onValueChange = {
                            volume = it
                            onVolumeChange(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        valueRange = 0f..1f,
                        steps = 19
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "0%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${(volume * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "100%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(context.getString(R.string.ok))
            }
        }
    )
}

/**
 * 倒计时设置对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerDialog(
    onDismiss: () -> Unit,
    onTimerSet: (Int) -> Unit,
    currentTimerMinutes: Int = 0
) {
    val context = LocalContext.current
    var selectedMinutes by remember { mutableStateOf(if (currentTimerMinutes > 0) currentTimerMinutes else 30) }
    val presetMinutes = listOf(15, 30, 45, 60, 90, 120)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(context.getString(R.string.set_countdown)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (currentTimerMinutes > 0) {
                    val hours = currentTimerMinutes / 60
                    val mins = currentTimerMinutes % 60
                    val statusText = if (hours > 0) {
                        context.getString(R.string.hours_minutes, hours, if (mins > 0) mins else 0)
                    } else {
                        context.getString(R.string.minutes_only, mins)
                    }
                    Text(
                        text = context.getString(R.string.current_countdown, statusText),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    context.getString(R.string.quick_select),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val rows = presetMinutes.chunked(3)
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { mins ->
                                FilterChip(
                                    onClick = { selectedMinutes = mins },
                                    label = { Text(context.getString(R.string.minutes, mins)) },
                                    selected = selectedMinutes == mins,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(3 - rowItems.size) { Spacer(modifier = Modifier.weight(1f)) }
                        }
                    }
                }
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = selectedMinutes.toFloat(),
                        onValueChange = { selectedMinutes = it.toInt() },
                        valueRange = 5f..180f,
                        steps = 34,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = context.getString(R.string.five_minutes),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (selectedMinutes >= 60) {
                                val h = selectedMinutes / 60
                                val m = selectedMinutes % 60
                                context.getString(R.string.hours_minutes, h, m)
                            } else {
                                context.getString(R.string.minutes_only, selectedMinutes)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = context.getString(R.string.three_hours),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (currentTimerMinutes > 0) {
                    TextButton(onClick = { onTimerSet(0); onDismiss() }) {
                        Text(context.getString(R.string.cancel_countdown))
                    }
                }
                TextButton(onClick = { onTimerSet(selectedMinutes) }) {
                    Text(context.getString(R.string.ok))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(context.getString(R.string.cancel))
            }
        }
    )
}
