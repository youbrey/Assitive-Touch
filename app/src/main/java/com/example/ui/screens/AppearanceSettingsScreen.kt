package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssistiveSettings
import com.example.model.ButtonTheme
import com.example.ui.components.FloatingTouchButton
import com.example.ui.theme.CardSurfaceBorder
import com.example.ui.theme.CardSurfaceDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.Slate400
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AppearanceSettingsScreen(
    viewModel: MainViewModel,
    settings: AssistiveSettings
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Live Preview Hero Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("appearance_preview_card"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurfaceDark),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardSurfaceBorder))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pratinjau Tampilan Tombol",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tema: ${settings.theme.title} • Ukuran: ${settings.buttonSizeDp.toInt()}dp",
                    fontSize = 12.sp,
                    color = Slate400
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF090D16))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FloatingTouchButton(
                            size = settings.buttonSizeDp.dp,
                            theme = settings.theme,
                            alpha = settings.idleOpacity
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Transparan (${(settings.idleOpacity * 100).toInt()}%)",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                        Text(
                            text = "Saat Diam / Idle",
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FloatingTouchButton(
                            size = settings.buttonSizeDp.dp,
                            theme = settings.theme,
                            alpha = settings.activeOpacity
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aktif (${(settings.activeOpacity * 100).toInt()}%)",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                        Text(
                            text = "Saat Disentuh",
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        // Theme Selection Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("theme_selection_card"),
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
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Koleksi Tema Ikon Tombol",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "Pilih estetika tombol virtual sesuai dengan kepribadian dan selera visual Anda.",
                    fontSize = 12.sp,
                    color = Slate400
                )

                ButtonTheme.entries.forEach { theme ->
                    val isSelected = theme == settings.theme
                    val themeAccent = Color(theme.primaryColorHex)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) themeAccent.copy(alpha = 0.12f) else Color(0xFF0F172A).copy(alpha = 0.7f))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) themeAccent else Color.White.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { viewModel.setTheme(theme) }
                            .padding(14.dp)
                            .testTag("theme_option_${theme.name.lowercase()}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FloatingTouchButton(
                            size = 42.dp,
                            theme = theme,
                            alpha = 1.0f
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = theme.title,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) themeAccent else Color.White
                            )
                            Text(
                                text = theme.description,
                                fontSize = 11.sp,
                                color = Slate400,
                                lineHeight = 15.sp
                            )
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(themeAccent)
                            )
                        }
                    }
                }
            }
        }

        // Sliders & Customization Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("appearance_sliders_card"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurfaceDark),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardSurfaceBorder))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatPaint,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ukuran & Tingkat Transparansi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Button Size Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ukuran Tombol",
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${settings.buttonSizeDp.toInt()} dp",
                            fontSize = 12.sp,
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Sesuaikan ukuran agar pas di layar tanpa menghalangi konten",
                        fontSize = 11.sp,
                        color = Slate400
                    )
                    Slider(
                        value = settings.buttonSizeDp,
                        onValueChange = { viewModel.setButtonSize(it) },
                        valueRange = 36f..72f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAccent,
                            activeTrackColor = CyanAccent,
                            inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("button_size_slider")
                    )
                }

                // Idle Opacity Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Transparansi Saat Diam (Idle Opacity)",
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${(settings.idleOpacity * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Tombol otomatis memudar menjadi transparan agar tidak mengganggu aktivitas Anda",
                        fontSize = 11.sp,
                        color = Slate400
                    )
                    Slider(
                        value = settings.idleOpacity,
                        onValueChange = { viewModel.setIdleOpacity(it) },
                        valueRange = 0.15f..0.85f,
                        steps = 13,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAccent,
                            activeTrackColor = CyanAccent,
                            inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("idle_opacity_slider")
                    )
                }

                // Active Opacity Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Opasitas Saat Disentuh",
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${(settings.activeOpacity * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = settings.activeOpacity,
                        onValueChange = { viewModel.setActiveOpacity(it) },
                        valueRange = 0.60f..1.0f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAccent,
                            activeTrackColor = CyanAccent,
                            inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("active_opacity_slider")
                    )
                }

                // Snap to Edge Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0F172A).copy(alpha = 0.7f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tempel Otomatis ke Tepi Layar",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Tombol otomatis bergeser merapat ke tepi layar kiri atau kanan saat dilepas",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                    }
                    Switch(
                        checked = settings.snapToEdge,
                        onCheckedChange = { viewModel.setSnapToEdge(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CyanAccent
                        ),
                        modifier = Modifier.testTag("snap_to_edge_switch")
                    )
                }

                // Haptic Vibration Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0F172A).copy(alpha = 0.7f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Getaran Sentuhan (Haptic Feedback)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Sensasi getaran lembut saat menekan tombol virtual",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                    }
                    Switch(
                        checked = settings.vibrateOnTap,
                        onCheckedChange = { viewModel.setVibrate(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CyanAccent
                        ),
                        modifier = Modifier.testTag("vibration_feedback_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
