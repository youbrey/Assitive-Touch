package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.ButtonTheme

@Composable
fun FloatingTouchButton(
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    theme: ButtonTheme = ButtonTheme.CYBER_NEON,
    alpha: Float = 1.0f
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = alpha.coerceIn(0.1f, 1f),
        animationSpec = tween(durationMillis = 250),
        label = "ButtonAlpha"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val center = Offset(w / 2f, h / 2f)
            val radius = (minOf(w, h) / 2f) - 4f
            if (radius <= 0) return@Canvas

            when (theme) {
                ButtonTheme.CLASSIC_IOS -> {
                    // Classic iOS frosted circular dark background
                    val bgColor = Color(theme.secondaryColorHex).copy(alpha = animatedAlpha * 0.9f)
                    drawCircle(color = bgColor, radius = radius, center = center)

                    val outerBorder = Color.White.copy(alpha = animatedAlpha * 0.45f)
                    drawCircle(color = outerBorder, radius = radius - 1f, center = center, style = Stroke(width = 3f))

                    val middleRing = Color.White.copy(alpha = animatedAlpha * 0.25f)
                    drawCircle(color = middleRing, radius = radius * 0.68f, center = center, style = Stroke(width = 2f))

                    val centerDot = Color.White.copy(alpha = animatedAlpha)
                    drawCircle(color = centerDot, radius = radius * 0.40f, center = center)
                }

                ButtonTheme.CYBER_NEON -> {
                    // Outer glow
                    val glowBrush = Brush.radialGradient(
                        colors = listOf(
                            Color(theme.primaryColorHex).copy(alpha = animatedAlpha * 0.55f),
                            Color(theme.accentColorHex).copy(alpha = animatedAlpha * 0.25f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius
                    )
                    drawCircle(brush = glowBrush, radius = radius, center = center)

                    // Inner dark body
                    val bgColor = Color(theme.secondaryColorHex).copy(alpha = animatedAlpha * 0.95f)
                    drawCircle(color = bgColor, radius = radius * 0.85f, center = center)

                    // Electric border
                    val neonColor = Color(theme.primaryColorHex).copy(alpha = animatedAlpha)
                    drawCircle(color = neonColor, radius = radius * 0.85f, center = center, style = Stroke(width = 3.5f))

                    // Secondary purple ring
                    val accentRing = Color(theme.accentColorHex).copy(alpha = animatedAlpha * 0.8f)
                    drawCircle(color = accentRing, radius = radius * 0.58f, center = center, style = Stroke(width = 2f))

                    // Center tactile core
                    drawCircle(color = neonColor, radius = radius * 0.30f, center = center)
                }

                ButtonTheme.EMERALD_GLOW -> {
                    val glowBrush = Brush.radialGradient(
                        colors = listOf(
                            Color(theme.primaryColorHex).copy(alpha = animatedAlpha * 0.5f),
                            Color(0xFF00E676).copy(alpha = animatedAlpha * 0.2f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius
                    )
                    drawCircle(brush = glowBrush, radius = radius, center = center)

                    val bgColor = Color(theme.secondaryColorHex).copy(alpha = animatedAlpha * 0.95f)
                    drawCircle(color = bgColor, radius = radius * 0.82f, center = center)

                    val emeraldBorder = Color(theme.primaryColorHex).copy(alpha = animatedAlpha)
                    drawCircle(color = emeraldBorder, radius = radius * 0.82f, center = center, style = Stroke(width = 3f))

                    val centerDot = Color(theme.accentColorHex).copy(alpha = animatedAlpha)
                    drawCircle(color = centerDot, radius = radius * 0.35f, center = center)
                }

                ButtonTheme.SUNSET_BLAZE -> {
                    val glowBrush = Brush.radialGradient(
                        colors = listOf(
                            Color(theme.primaryColorHex).copy(alpha = animatedAlpha * 0.5f),
                            Color(theme.accentColorHex).copy(alpha = animatedAlpha * 0.2f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius
                    )
                    drawCircle(brush = glowBrush, radius = radius, center = center)

                    val bgColor = Color(theme.secondaryColorHex).copy(alpha = animatedAlpha * 0.95f)
                    drawCircle(color = bgColor, radius = radius * 0.82f, center = center)

                    val amberBorder = Color(theme.primaryColorHex).copy(alpha = animatedAlpha)
                    drawCircle(color = amberBorder, radius = radius * 0.82f, center = center, style = Stroke(width = 3.2f))

                    val centerDot = Color(theme.accentColorHex).copy(alpha = animatedAlpha)
                    drawCircle(color = centerDot, radius = radius * 0.36f, center = center)
                }

                ButtonTheme.DARK_GLASS -> {
                    val bgColor = Color(theme.secondaryColorHex).copy(alpha = animatedAlpha * 0.92f)
                    drawCircle(color = bgColor, radius = radius * 0.90f, center = center)

                    val silverBorder = Color.White.copy(alpha = animatedAlpha * 0.6f)
                    drawCircle(color = silverBorder, radius = radius * 0.90f, center = center, style = Stroke(width = 2.5f))

                    val innerRing = Color.White.copy(alpha = animatedAlpha * 0.2f)
                    drawCircle(color = innerRing, radius = radius * 0.62f, center = center, style = Stroke(width = 1.5f))

                    val centerDot = Color(0xFFE2E8F0).copy(alpha = animatedAlpha * 0.9f)
                    drawCircle(color = centerDot, radius = radius * 0.32f, center = center)
                }

                ButtonTheme.MINIMAL_DOT -> {
                    val bgColor = Color(theme.secondaryColorHex).copy(alpha = animatedAlpha * 0.75f)
                    drawCircle(color = bgColor, radius = radius * 0.75f, center = center)

                    val border = Color.White.copy(alpha = animatedAlpha * 0.5f)
                    drawCircle(color = border, radius = radius * 0.75f, center = center, style = Stroke(width = 2f))

                    val centerDot = Color.White.copy(alpha = animatedAlpha)
                    drawCircle(color = centerDot, radius = radius * 0.35f, center = center)
                }
            }
        }
    }
}
