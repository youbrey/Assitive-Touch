package com.example.system

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.view.View
import com.example.model.ButtonTheme

class AssistiveTouchView(context: Context) : View(context) {

    var buttonTheme: ButtonTheme = ButtonTheme.CYBER_NEON
        set(value) {
            field = value
            invalidate()
        }

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val rectF = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val cx = w / 2f
        val cy = h / 2f
        val radius = (minOf(w, h) / 2f) - 4f
        if (radius <= 0) return

        when (buttonTheme) {
            ButtonTheme.CLASSIC_IOS -> {
                // Classic iOS frosted circular dark background with white outer & inner ring
                bgPaint.color = (buttonTheme.secondaryColorHex and 0xFFFFFFFF).toInt()
                canvas.drawCircle(cx, cy, radius, bgPaint)

                borderPaint.color = Color.argb(120, 255, 255, 255)
                borderPaint.strokeWidth = 3f
                canvas.drawCircle(cx, cy, radius - 1.5f, borderPaint)

                // Outer concentric accent circle
                ringPaint.color = Color.argb(70, 255, 255, 255)
                ringPaint.strokeWidth = 2f
                canvas.drawCircle(cx, cy, radius * 0.68f, ringPaint)

                // Inner white rounded dot
                centerPaint.color = Color.WHITE
                canvas.drawCircle(cx, cy, radius * 0.40f, centerPaint)
            }
            ButtonTheme.CYBER_NEON -> {
                // Cyber Neon glow
                glowPaint.shader = RadialGradient(
                    cx, cy, radius,
                    intArrayOf(
                        (buttonTheme.primaryColorHex and 0xFFFFFFFF).toInt(),
                        (buttonTheme.accentColorHex and 0xFFFFFFFF).toInt(),
                        Color.TRANSPARENT
                    ),
                    floatArrayOf(0f, 0.7f, 1f),
                    Shader.TileMode.CLAMP
                )
                glowPaint.alpha = 110
                canvas.drawCircle(cx, cy, radius, glowPaint)

                bgPaint.color = (buttonTheme.secondaryColorHex and 0xFFFFFFFF).toInt()
                canvas.drawCircle(cx, cy, radius * 0.85f, bgPaint)

                borderPaint.color = (buttonTheme.primaryColorHex and 0xFFFFFFFF).toInt()
                borderPaint.strokeWidth = 3.5f
                canvas.drawCircle(cx, cy, radius * 0.85f, borderPaint)

                ringPaint.color = (buttonTheme.accentColorHex and 0xFFFFFFFF).toInt()
                ringPaint.strokeWidth = 2f
                canvas.drawCircle(cx, cy, radius * 0.58f, ringPaint)

                centerPaint.color = (buttonTheme.primaryColorHex and 0xFFFFFFFF).toInt()
                canvas.drawCircle(cx, cy, radius * 0.30f, centerPaint)
            }
            ButtonTheme.EMERALD_GLOW -> {
                glowPaint.shader = RadialGradient(
                    cx, cy, radius,
                    intArrayOf(
                        (buttonTheme.primaryColorHex and 0xFFFFFFFF).toInt(),
                        Color.argb(80, 0, 230, 118),
                        Color.TRANSPARENT
                    ),
                    floatArrayOf(0f, 0.75f, 1f),
                    Shader.TileMode.CLAMP
                )
                canvas.drawCircle(cx, cy, radius, glowPaint)

                bgPaint.color = (buttonTheme.secondaryColorHex and 0xFFFFFFFF).toInt()
                canvas.drawCircle(cx, cy, radius * 0.82f, bgPaint)

                borderPaint.color = (buttonTheme.primaryColorHex and 0xFFFFFFFF).toInt()
                borderPaint.strokeWidth = 3f
                canvas.drawCircle(cx, cy, radius * 0.82f, borderPaint)

                centerPaint.color = (buttonTheme.accentColorHex and 0xFFFFFFFF).toInt()
                canvas.drawCircle(cx, cy, radius * 0.35f, centerPaint)
            }
            ButtonTheme.SUNSET_BLAZE -> {
                glowPaint.shader = RadialGradient(
                    cx, cy, radius,
                    intArrayOf(
                        (buttonTheme.primaryColorHex and 0xFFFFFFFF).toInt(),
                        (buttonTheme.accentColorHex and 0xFFFFFFFF).toInt(),
                        Color.TRANSPARENT
                    ),
                    floatArrayOf(0f, 0.65f, 1f),
                    Shader.TileMode.CLAMP
                )
                glowPaint.alpha = 130
                canvas.drawCircle(cx, cy, radius, glowPaint)

                bgPaint.color = (buttonTheme.secondaryColorHex and 0xFFFFFFFF).toInt()
                canvas.drawCircle(cx, cy, radius * 0.82f, bgPaint)

                borderPaint.color = (buttonTheme.primaryColorHex and 0xFFFFFFFF).toInt()
                borderPaint.strokeWidth = 3.2f
                canvas.drawCircle(cx, cy, radius * 0.82f, borderPaint)

                centerPaint.color = (buttonTheme.accentColorHex and 0xFFFFFFFF).toInt()
                canvas.drawCircle(cx, cy, radius * 0.36f, centerPaint)
            }
            ButtonTheme.DARK_GLASS -> {
                bgPaint.color = (buttonTheme.secondaryColorHex and 0xFFFFFFFF).toInt()
                canvas.drawCircle(cx, cy, radius * 0.90f, bgPaint)

                borderPaint.color = Color.argb(160, 200, 200, 200)
                borderPaint.strokeWidth = 2.5f
                canvas.drawCircle(cx, cy, radius * 0.90f, borderPaint)

                ringPaint.color = Color.argb(60, 255, 255, 255)
                ringPaint.strokeWidth = 1.5f
                canvas.drawCircle(cx, cy, radius * 0.62f, ringPaint)

                centerPaint.color = Color.argb(220, 220, 220, 220)
                canvas.drawCircle(cx, cy, radius * 0.32f, centerPaint)
            }
            ButtonTheme.MINIMAL_DOT -> {
                bgPaint.color = (buttonTheme.secondaryColorHex and 0xFFFFFFFF).toInt()
                canvas.drawCircle(cx, cy, radius * 0.75f, bgPaint)

                borderPaint.color = Color.argb(140, 255, 255, 255)
                borderPaint.strokeWidth = 2f
                canvas.drawCircle(cx, cy, radius * 0.75f, borderPaint)

                centerPaint.color = Color.WHITE
                canvas.drawCircle(cx, cy, radius * 0.35f, centerPaint)
            }
        }
    }
}
