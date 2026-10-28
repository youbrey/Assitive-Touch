package com.example.ui.components

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RingVolume
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent

@Composable
fun VolumeControlDialog(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager }

    var mediaVol by remember {
        mutableIntStateOf(audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 7)
    }
    val maxMedia = remember {
        audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15
    }

    var ringVol by remember {
        mutableIntStateOf(audioManager?.getStreamVolume(AudioManager.STREAM_RING) ?: 5)
    }
    val maxRing = remember {
        audioManager?.getStreamMaxVolume(AudioManager.STREAM_RING) ?: 10
    }

    var alarmVol by remember {
        mutableIntStateOf(audioManager?.getStreamVolume(AudioManager.STREAM_ALARM) ?: 6)
    }
    val maxAlarm = remember {
        audioManager?.getStreamMaxVolume(AudioManager.STREAM_ALARM) ?: 10
    }

    var notifVol by remember {
        mutableIntStateOf(audioManager?.getStreamVolume(AudioManager.STREAM_NOTIFICATION) ?: 5)
    }
    val maxNotif = remember {
        audioManager?.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION) ?: 10
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable { onDismiss() }
            .testTag("volume_dialog_backdrop"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0F172A))
                .border(1.5.dp, CyanAccent.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .clickable(enabled = false) {}
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Volume",
                        tint = CyanAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kontrol Volume Sistem",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_volume_dialog")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            VolumeSliderItem(
                label = "Media / Musik",
                icon = Icons.Default.MusicNote,
                value = mediaVol,
                maxValue = maxMedia,
                onValueChange = { newVal ->
                    mediaVol = newVal
                    audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, newVal, 0)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            VolumeSliderItem(
                label = "Nada Dering (Panggilan)",
                icon = Icons.Default.RingVolume,
                value = ringVol,
                maxValue = maxRing,
                onValueChange = { newVal ->
                    ringVol = newVal
                    audioManager?.setStreamVolume(AudioManager.STREAM_RING, newVal, 0)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            VolumeSliderItem(
                label = "Notifikasi",
                icon = Icons.Default.Notifications,
                value = notifVol,
                maxValue = maxNotif,
                onValueChange = { newVal ->
                    notifVol = newVal
                    audioManager?.setStreamVolume(AudioManager.STREAM_NOTIFICATION, newVal, 0)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            VolumeSliderItem(
                label = "Alarm",
                icon = Icons.Default.Alarm,
                value = alarmVol,
                maxValue = maxAlarm,
                onValueChange = { newVal ->
                    alarmVol = newVal
                    audioManager?.setStreamVolume(AudioManager.STREAM_ALARM, newVal, 0)
                }
            )
        }
    }
}

@Composable
private fun VolumeSliderItem(
    label: String,
    icon: ImageVector,
    value: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E293B).copy(alpha = 0.75f))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (value == 0) Icons.Default.VolumeMute else icon,
                    contentDescription = label,
                    tint = CyanAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    fontSize = 13.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "$value / $maxValue",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..maxValue.toFloat(),
            steps = if (maxValue > 1) maxValue - 1 else 0,
            colors = SliderDefaults.colors(
                thumbColor = CyanAccent,
                activeTrackColor = CyanAccent,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("slider_${label.replace(" ", "_").lowercase()}")
        )
    }
}
