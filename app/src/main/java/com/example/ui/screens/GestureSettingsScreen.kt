package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssistiveSettings
import com.example.model.TouchAction
import com.example.model.TouchGesture
import com.example.ui.components.getActionDisplay
import com.example.ui.theme.CardSurfaceBorder
import com.example.ui.theme.CardSurfaceDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.Slate400
import com.example.ui.viewmodel.MainViewModel

@Composable
fun GestureSettingsScreen(
    viewModel: MainViewModel,
    settings: AssistiveSettings
) {
    val scrollState = rememberScrollState()

    var editingGesture by remember { mutableStateOf<TouchGesture?>(null) }
    var editingSlotIndex by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Gesture Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("gestures_configuration_card"),
            shape = RoundedCornerShape(22.dp),
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
                        imageVector = Icons.Default.Gesture,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gestur Cepat Tombol Virtual",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "Tentukan aksi instan saat menekan atau mengetuk tombol virtual tanpa harus membuka menu.",
                    fontSize = 12.sp,
                    color = Slate400,
                    lineHeight = 16.sp
                )

                // Single Tap Row
                GestureRowItem(
                    gesture = TouchGesture.SINGLE_TAP,
                    currentAction = settings.singleTapAction,
                    onClick = { editingGesture = TouchGesture.SINGLE_TAP }
                )

                // Double Tap Row
                GestureRowItem(
                    gesture = TouchGesture.DOUBLE_TAP,
                    currentAction = settings.doubleTapAction,
                    onClick = { editingGesture = TouchGesture.DOUBLE_TAP }
                )

                // Long Press Row
                GestureRowItem(
                    gesture = TouchGesture.LONG_PRESS,
                    currentAction = settings.longPressAction,
                    onClick = { editingGesture = TouchGesture.LONG_PRESS }
                )
            }
        }

        // 9 Slots Menu Customization Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("menu_slots_configuration_card"),
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
                            imageVector = Icons.Default.Widgets,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kustomisasi 9 Slot Menu",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    TextButton(
                        onClick = { viewModel.resetMenuActions() },
                        modifier = Modifier.testTag("reset_menu_slots_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Reset", fontSize = 12.sp, color = CyanAccent)
                    }
                }

                Text(
                    text = "Ketuk kotak di bawah untuk mengganti ikon & pintasan pada menu melayang Anda.",
                    fontSize = 12.sp,
                    color = Slate400,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                val actions = settings.menuActions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (row in 0 until 3) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (col in 0 until 3) {
                                val idx = row * 3 + col
                                val act = actions.getOrNull(idx) ?: TouchAction.NONE
                                MenuSlotCard(
                                    index = idx,
                                    action = act,
                                    onClick = { editingSlotIndex = idx }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Dialog for picking gesture action
    if (editingGesture != null) {
        val gesture = editingGesture!!
        ActionPickerDialog(
            title = "Pilih Aksi untuk ${gesture.label}",
            currentSelection = when (gesture) {
                TouchGesture.SINGLE_TAP -> settings.singleTapAction
                TouchGesture.DOUBLE_TAP -> settings.doubleTapAction
                TouchGesture.LONG_PRESS -> settings.longPressAction
            },
            onSelect = { action ->
                when (gesture) {
                    TouchGesture.SINGLE_TAP -> viewModel.setSingleTap(action)
                    TouchGesture.DOUBLE_TAP -> viewModel.setDoubleTap(action)
                    TouchGesture.LONG_PRESS -> viewModel.setLongPress(action)
                }
                editingGesture = null
            },
            onDismiss = { editingGesture = null }
        )
    }

    // Dialog for picking menu slot action
    if (editingSlotIndex in 0..8) {
        val currentSlotAction = settings.menuActions.getOrNull(editingSlotIndex) ?: TouchAction.NONE
        ActionPickerDialog(
            title = "Pilih Aksi untuk Slot ${editingSlotIndex + 1}",
            currentSelection = currentSlotAction,
            onSelect = { action ->
                viewModel.setMenuAction(editingSlotIndex, action)
                editingSlotIndex = -1
            },
            onDismiss = { editingSlotIndex = -1 }
        )
    }
}

@Composable
private fun GestureRowItem(
    gesture: TouchGesture,
    currentAction: TouchAction,
    onClick: () -> Unit
) {
    val (icon, _) = getActionDisplay(currentAction)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.7f))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("gesture_row_${gesture.name.lowercase()}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = gesture.label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = gesture.description,
                fontSize = 11.sp,
                color = Slate400
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1E293B))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = currentAction.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = Slate400,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun MenuSlotCard(
    index: Int,
    action: TouchAction,
    onClick: () -> Unit
) {
    val (icon, label) = getActionDisplay(action)

    Column(
        modifier = Modifier
            .size(92.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.8f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(6.dp)
            .testTag("menu_slot_$index"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(CyanAccent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = CyanAccent,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = "Slot ${index + 1}",
            fontSize = 9.sp,
            color = Slate400
        )
    }
}

@Composable
private fun ActionPickerDialog(
    title: String,
    currentSelection: TouchAction,
    onSelect: (TouchAction) -> Unit,
    onDismiss: () -> Unit
) {
    val allActions = TouchAction.entries

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Tutup", color = CyanAccent)
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                allActions.forEach { action ->
                    val isSelected = action == currentSelection
                    val (icon, _) = getActionDisplay(action)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CyanAccent.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable { onSelect(action) }
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelect(action) },
                            colors = RadioButtonDefaults.colors(selectedColor = CyanAccent, unselectedColor = Slate400)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) CyanAccent else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = action.title,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CyanAccent else Color.White
                            )
                            Text(
                                text = action.description,
                                fontSize = 10.5.sp,
                                color = Slate400,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(20.dp)
    )
}
