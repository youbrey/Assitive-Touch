package com.example.system

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.model.AssistiveSettings
import com.example.model.ButtonTheme
import com.example.model.TouchAction

class AssistiveOverlayMenuDialog(
    private val context: Context,
    private val windowManager: WindowManager,
    private val onActionSelected: (TouchAction) -> Unit
) {

    private var rootOverlayView: FrameLayout? = null
    private var isShowing = false

    fun show(actions: List<TouchAction>, theme: ButtonTheme) {
        if (isShowing) return

        val density = context.resources.displayMetrics.density
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val fullScreenParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        val overlay = FrameLayout(context).apply {
            setBackgroundColor(Color.argb(130, 0, 0, 0))
            setOnClickListener { dismiss() }
        }

        // Card Container
        val card = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            val pad = (20 * density).toInt()
            setPadding(pad, pad, pad, pad)

            val bg = GradientDrawable().apply {
                setColor(Color.argb(235, 18, 24, 38))
                cornerRadius = 28 * density
                setStroke((1.5f * density).toInt(), (theme.primaryColorHex and 0xFFFFFFFF).toInt())
            }
            background = bg
            isClickable = true
        }

        // Title Header
        val titleView = TextView(context).apply {
            text = "Menu Pintasan Sistem"
            textSize = 15f
            setTextColor((theme.primaryColorHex and 0xFFFFFFFF).toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            val padBottom = (16 * density).toInt()
            setPadding(0, 0, 0, padBottom)
        }
        card.addView(titleView)

        // 3x3 Grid
        val grid = GridLayout(context).apply {
            columnCount = 3
            rowCount = 3
            alignmentMode = GridLayout.ALIGN_BOUNDS
        }

        val displayActions = if (actions.size >= 9) actions.take(9) else AssistiveSettings.defaultMenuActions

        displayActions.forEach { action ->
            val itemView = buildActionItemView(action, theme, density) {
                dismiss()
                onActionSelected(action)
            }
            grid.addView(itemView)
        }

        card.addView(grid)

        val cardParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }
        overlay.addView(card, cardParams)

        try {
            windowManager.addView(overlay, fullScreenParams)
            rootOverlayView = overlay
            isShowing = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun buildActionItemView(
        action: TouchAction,
        theme: ButtonTheme,
        density: Float,
        onClick: () -> Unit
    ): View {
        val item = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            val size = (84 * density).toInt()
            layoutParams = GridLayout.LayoutParams().apply {
                width = size
                height = size
                val m = (4 * density).toInt()
                setMargins(m, m, m, m)
            }

            val itemBg = GradientDrawable().apply {
                setColor(Color.argb(70, 30, 41, 59))
                cornerRadius = 18 * density
            }
            background = itemBg
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
        }

        val icon = ImageView(context).apply {
            val iconSize = (32 * density).toInt()
            layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
            setColorFilter(Color.WHITE)

            // Map action to standard Android platform icons
            val iconRes = when (action) {
                TouchAction.SCREENSHOT -> android.R.drawable.ic_menu_camera
                TouchAction.LOCK_SCREEN -> android.R.drawable.ic_lock_power_off
                TouchAction.VOLUME_PANEL -> android.R.drawable.ic_lock_silent_mode_off
                TouchAction.POWER_DIALOG -> android.R.drawable.ic_menu_close_clear_cancel
                TouchAction.HOME -> android.R.drawable.ic_menu_today
                TouchAction.BACK -> android.R.drawable.ic_menu_revert
                TouchAction.RECENTS -> android.R.drawable.ic_menu_agenda
                TouchAction.NOTIFICATIONS -> android.R.drawable.ic_popup_reminder
                TouchAction.QUICK_SETTINGS -> android.R.drawable.ic_menu_preferences
                TouchAction.SPLIT_SCREEN -> android.R.drawable.ic_menu_crop
                TouchAction.FLASHLIGHT -> android.R.drawable.ic_menu_compass
                else -> android.R.drawable.ic_menu_help
            }
            setImageResource(iconRes)
        }
        item.addView(icon)

        val label = TextView(context).apply {
            text = when (action) {
                TouchAction.SCREENSHOT -> "Tangkapan"
                TouchAction.LOCK_SCREEN -> "Kunci Layar"
                TouchAction.VOLUME_PANEL -> "Volume"
                TouchAction.POWER_DIALOG -> "Daya & Mati"
                TouchAction.HOME -> "Beranda"
                TouchAction.BACK -> "Kembali"
                TouchAction.RECENTS -> "Terbaru"
                TouchAction.NOTIFICATIONS -> "Notifikasi"
                TouchAction.QUICK_SETTINGS -> "Setelan"
                TouchAction.SPLIT_SCREEN -> "Bagi Layar"
                TouchAction.FLASHLIGHT -> "Senter"
                else -> action.title
            }
            textSize = 10.5f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            val topMargin = (6 * density).toInt()
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, topMargin, 0, 0)
            }
        }
        item.addView(label)

        return item
    }

    fun dismiss() {
        if (!isShowing) return
        rootOverlayView?.let { view ->
            try {
                windowManager.removeView(view)
            } catch (_: Exception) {}
        }
        rootOverlayView = null
        isShowing = false
    }
}
