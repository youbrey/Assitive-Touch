package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import com.example.util.MiuiDeviceHelper
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssistiveSettings
import com.example.model.TouchAction
import com.example.ui.components.FloatingTouchButton
import com.example.ui.theme.CardSurfaceDark
import com.example.ui.theme.CardSurfaceBorder
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.Slate400
import com.example.ui.viewmodel.MainViewModel
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    settings: AssistiveSettings,
    isOverlayGranted: Boolean,
    isAccessibilityGranted: Boolean,
    onShowVolumeMixer: () -> Unit,
    onShowQuickMenu: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Master Switch Card
        MasterStatusCard(
            isEnabled = settings.isEnabled,
            isOverlayGranted = isOverlayGranted,
            isAccessibilityGranted = isAccessibilityGranted,
            onToggle = { enabled ->
                if (enabled && !isOverlayGranted) {
                    requestOverlayPermission(context)
                }
                viewModel.setServiceEnabled(context, enabled)
            }
        )

        // Permission Checklist Card
        PermissionChecklistCard(
            isOverlayGranted = isOverlayGranted,
            isAccessibilityGranted = isAccessibilityGranted,
            onGrantOverlay = { requestOverlayPermission(context) },
            onGrantAccessibility = { requestAccessibilityPermission(context) }
        )

        // Khusus Redmi Note 8 Pro & Android 10 (MIUI Optimization)
        RedmiMiuiOptimizationCard(context = context)

        // Interactive Live Preview Playground
        InteractivePlaygroundCard(
            settings = settings,
            onAction = { action ->
                viewModel.executeAction(
                    context = context,
                    action = action,
                    onShowMenu = onShowQuickMenu,
                    onShowVolumeDialog = onShowVolumeMixer
                )
            }
        )

        // Quick System Testing Bar
        QuickActionTestSection(
            onExecute = { action ->
                viewModel.executeAction(
                    context = context,
                    action = action,
                    onShowMenu = onShowQuickMenu,
                    onShowVolumeDialog = onShowVolumeMixer
                )
            }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun MasterStatusCard(
    isEnabled: Boolean,
    isOverlayGranted: Boolean,
    isAccessibilityGranted: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val statusColor by animateColorAsState(
        targetValue = if (isEnabled && isOverlayGranted) EmeraldGlow else Color(0xFFFFB300),
        label = "statusColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("master_status_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardSurfaceBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEnabled && isOverlayGranted) "Layanan Berjalan" else if (isEnabled) "Izin Belum Lengkap" else "Tombol Nonaktif",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Assistive Touch Virtual",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Tombol melayang multi-fungsi di atas aplikasi lain",
                    fontSize = 12.sp,
                    color = Slate400
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = CyanAccent,
                    uncheckedThumbColor = Slate400,
                    uncheckedTrackColor = Color(0xFF1E293B)
                ),
                modifier = Modifier.testTag("service_toggle_switch")
            )
        }
    }
}

@Composable
private fun PermissionChecklistCard(
    isOverlayGranted: Boolean,
    isAccessibilityGranted: Boolean,
    onGrantOverlay: () -> Unit,
    onGrantAccessibility: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("permission_checklist_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardSurfaceBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Izin Sistem yang Diperlukan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Overlay Permission Item
            PermissionRowItem(
                title = "Tampil di Atas Aplikasi Lain (Overlay)",
                description = "Wajib agar tombol virtual melayang dapat muncul di semua layar",
                isGranted = isOverlayGranted,
                actionLabel = "Beri Izin",
                onAction = onGrantOverlay
            )

            // Accessibility Permission Item
            PermissionRowItem(
                title = "Layanan Aksesibilitas (Sistem)",
                description = "Dibutuhkan untuk Tangkapan Layar, Kunci Layar, dan Menu Matikan Daya/Restart",
                isGranted = isAccessibilityGranted,
                actionLabel = "Aktifkan",
                onAction = onGrantAccessibility
            )
        }
    }
}

@Composable
private fun PermissionRowItem(
    title: String,
    description: String,
    isGranted: Boolean,
    actionLabel: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.7f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isGranted) EmeraldGlow else Color(0xFFFFB300),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Slate400,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (!isGranted) {
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color(0xFF0A0F1D)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = actionLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Text(
                text = "Aktif",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldGlow,
                modifier = Modifier.padding(end = 6.dp)
            )
        }
    }
}

@Composable
private fun InteractivePlaygroundCard(
    settings: AssistiveSettings,
    onAction: (TouchAction) -> Unit
) {
    val density = LocalDensity.current
    var lastInteractionTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isTouching by remember { mutableStateOf(false) }

    val currentAlpha = if (isTouching) settings.activeOpacity else settings.idleOpacity

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("interactive_playground_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardSurfaceBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Uji Coba Langsung (Live Sandbox)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "Geser & Ketuk",
                    fontSize = 11.sp,
                    color = Slate400
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Coba seret tombol virtual di area bawah. Uji ketukan tunggal (${settings.singleTapAction.title}), ketuk 2x (${settings.doubleTapAction.title}), dan tekan lama (${settings.longPressAction.title}).",
                fontSize = 12.sp,
                color = Slate400,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sandbox Frame
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF090D16))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                val boxWidthPx = with(density) { maxWidth.toPx() }
                val boxHeightPx = with(density) { maxHeight.toPx() }
                val buttonSizePx = with(density) { settings.buttonSizeDp.dp.toPx() }

                var offsetX by remember { mutableFloatStateOf(boxWidthPx * 0.85f - buttonSizePx) }
                var offsetY by remember { mutableFloatStateOf(boxHeightPx * 0.45f) }

                var lastTapTime by remember { mutableLongStateOf(0L) }
                val handler = remember { Handler(Looper.getMainLooper()) }

                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .pointerInput(settings) {
                            detectDragGestures(
                                onDragStart = {
                                    isTouching = true
                                    lastInteractionTime = System.currentTimeMillis()
                                },
                                onDragEnd = {
                                    isTouching = false
                                    // Edge snap logic
                                    if (settings.snapToEdge) {
                                        val middle = (boxWidthPx - buttonSizePx) / 2f
                                        offsetX = if (offsetX < middle) 0f else (boxWidthPx - buttonSizePx)
                                    }
                                },
                                onDragCancel = {
                                    isTouching = false
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    offsetX = (offsetX + dragAmount.x).coerceIn(0f, boxWidthPx - buttonSizePx)
                                    offsetY = (offsetY + dragAmount.y).coerceIn(0f, boxHeightPx - buttonSizePx)
                                }
                            )
                        }
                        .clickable {
                            val now = System.currentTimeMillis()
                            isTouching = true
                            if (now - lastTapTime < 280) {
                                lastTapTime = 0L
                                onAction(settings.doubleTapAction)
                            } else {
                                lastTapTime = now
                                handler.postDelayed({
                                    if (lastTapTime == now) {
                                        onAction(settings.singleTapAction)
                                    }
                                }, 280)
                            }
                            handler.postDelayed({ isTouching = false }, 2000)
                        }
                        .testTag("sandbox_floating_button")
                ) {
                    FloatingTouchButton(
                        size = settings.buttonSizeDp.dp,
                        theme = settings.theme,
                        alpha = currentAlpha
                    )
                }

                Text(
                    text = "Area Simulasi Tombol Melayang",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun QuickActionTestSection(
    onExecute: (TouchAction) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quick_action_test_section"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardSurfaceBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Uji Coba Fungsi Utama",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Picu tindakan sistem secara instan untuk memeriksa respons perangkat Anda",
                fontSize = 12.sp,
                color = Slate400
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionTestChip(
                    label = "Tangkapan Layar",
                    icon = Icons.Default.PhotoCamera,
                    modifier = Modifier.weight(1f),
                    onClick = { onExecute(TouchAction.SCREENSHOT) }
                )
                ActionTestChip(
                    label = "Kunci Layar",
                    icon = Icons.Default.Lock,
                    modifier = Modifier.weight(1f),
                    onClick = { onExecute(TouchAction.LOCK_SCREEN) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionTestChip(
                    label = "Mixer Volume",
                    icon = Icons.Default.VolumeUp,
                    modifier = Modifier.weight(1f),
                    onClick = { onExecute(TouchAction.VOLUME_PANEL) }
                )
                ActionTestChip(
                    label = "Daya & Restart",
                    icon = Icons.Default.PowerSettingsNew,
                    modifier = Modifier.weight(1f),
                    onClick = { onExecute(TouchAction.POWER_DIALOG) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            ActionTestChip(
                label = "Toggle Senter / Flashlight",
                icon = Icons.Default.FlashlightOn,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onExecute(TouchAction.FLASHLIGHT) }
            )
        }
    }
}

@Composable
private fun ActionTestChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E293B).copy(alpha = 0.8f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = CyanAccent,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            maxLines = 1
        )
    }
}

private fun requestOverlayPermission(context: Context) {
    try {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        val fallback = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(fallback)
    }
}

private fun requestAccessibilityPermission(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        // Fallback
    }
}

@Composable
fun RedmiMiuiOptimizationCard(context: Context) {
    val isRedmi = remember { MiuiDeviceHelper.isRedmiNote8Pro() || MiuiDeviceHelper.isMiuiDevice() }
    val isBatteryOptimized = remember { MiuiDeviceHelper.isBatteryOptimizationIgnored(context) }
    val deviceSummary = remember { MiuiDeviceHelper.getDeviceSummary() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("redmi_miui_optimization_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardSurfaceBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Optimasi Redmi Note 8 Pro & Android 10",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyanAccent.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "MIUI 11/12",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent
                    )
                }
            }

            Text(
                text = "Perangkat terdeteksi: $deviceSummary\nSistem MIUI pada Android 10 memiliki manajemen baterai dan RAM yang ketat. Aktifkan 3 setelan di bawah agar tombol virtual tidak tertutup:",
                fontSize = 12.sp,
                color = Slate400,
                lineHeight = 16.sp
            )

            // Step 1: Autostart (Mulai Otomatis)
            MiuiSettingRow(
                title = "1. Izin Mulai Otomatis (Autostart)",
                description = "Wajib agar tombol virtual tidak mati saat pembersihan RAM latar belakang",
                actionLabel = "Buka Autostart",
                onClick = { MiuiDeviceHelper.openMiuiAutostart(context) }
            )

            // Step 2: Pop-up windows while running in background
            MiuiSettingRow(
                title = "2. Izin Jendela Pop-up MIUI",
                description = "Izin khusus MIUI agar tombol dapat melayang di atas semua aplikasi",
                actionLabel = "Atur Pop-up",
                onClick = { MiuiDeviceHelper.openMiuiPopupPermission(context) }
            )

            // Step 3: No restrictions battery saver
            MiuiSettingRow(
                title = "3. Penghemat Baterai: Tidak Ada Pembatasan",
                description = if (isBatteryOptimized) "Sudah dioptimalkan (Tidak dibatasi)" else "Pilih 'Tidak Ada Pembatasan' agar MIUI tidak mematikan service",
                actionLabel = "Setel Baterai",
                isCompleted = isBatteryOptimized,
                onClick = { MiuiDeviceHelper.openMiuiBatterySaver(context) }
            )

            // Tip Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.8f))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💡 Tips Tambahan: Buka Recent Apps (geser ke atas), tahan kartu AssistiveTouch, lalu ketuk ikon gembok untuk mengunci aplikasi di memori.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun MiuiSettingRow(
    title: String,
    description: String,
    actionLabel: String,
    isCompleted: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.6f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = description,
                fontSize = 10.5.sp,
                color = Slate400,
                lineHeight = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCompleted) EmeraldGlow else CyanAccent,
                contentColor = Color(0xFF0A0F1D)
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (isCompleted) "Selesai" else actionLabel,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
