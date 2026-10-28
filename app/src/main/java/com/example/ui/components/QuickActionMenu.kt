package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ButtonTheme
import com.example.model.TouchAction

@Composable
fun QuickActionMenu(
    visible: Boolean,
    actions: List<TouchAction>,
    theme: ButtonTheme,
    onDismiss: () -> Unit,
    onActionSelected: (TouchAction) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.85f),
        exit = fadeOut() + scaleOut(targetScale = 0.85f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable { onDismiss() }
                .testTag("quick_action_menu_backdrop"),
            contentAlignment = Alignment.Center
        ) {
            val accentColor = Color(theme.primaryColorHex)

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.96f))
                    .border(1.5.dp, accentColor.copy(alpha = 0.7f), RoundedCornerShape(28.dp))
                    .clickable(enabled = false) {}
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pintasan Virtual",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("close_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup Menu",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val safeActions = if (actions.size >= 9) actions.take(9) else List(9) { TouchAction.NONE }

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (row in 0 until 3) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (col in 0 until 3) {
                                val index = row * 3 + col
                                val action = safeActions.getOrNull(index) ?: TouchAction.NONE
                                MenuActionItem(
                                    action = action,
                                    theme = theme,
                                    onClick = {
                                        onDismiss()
                                        onActionSelected(action)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuActionItem(
    action: TouchAction,
    theme: ButtonTheme,
    onClick: () -> Unit
) {
    val (icon, label) = getActionDisplay(action)
    val accentColor = Color(theme.primaryColorHex)

    Column(
        modifier = Modifier
            .size(86.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1E293B).copy(alpha = 0.85f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(6.dp)
            .testTag("menu_action_${action.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

fun getActionDisplay(action: TouchAction): Pair<ImageVector, String> {
    return when (action) {
        TouchAction.SCREENSHOT -> Pair(Icons.Default.PhotoCamera, "Tangkapan")
        TouchAction.LOCK_SCREEN -> Pair(Icons.Default.Lock, "Kunci Layar")
        TouchAction.VOLUME_PANEL -> Pair(Icons.Default.VolumeUp, "Volume")
        TouchAction.POWER_DIALOG -> Pair(Icons.Default.PowerSettingsNew, "Daya / Mati")
        TouchAction.HOME -> Pair(Icons.Default.Home, "Beranda")
        TouchAction.BACK -> Pair(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
        TouchAction.RECENTS -> Pair(Icons.Default.Layers, "Terbaru")
        TouchAction.NOTIFICATIONS -> Pair(Icons.Default.Notifications, "Notifikasi")
        TouchAction.QUICK_SETTINGS -> Pair(Icons.Default.Tune, "Setelan Cepat")
        TouchAction.SPLIT_SCREEN -> Pair(Icons.Default.Crop, "Layar Bagi")
        TouchAction.FLASHLIGHT -> Pair(Icons.Default.FlashlightOn, "Senter")
        TouchAction.OPEN_MENU -> Pair(Icons.Default.Widgets, "Buka Menu")
        TouchAction.NONE -> Pair(Icons.Default.Close, "Kosong")
    }
}
