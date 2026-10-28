package com.example.system

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import androidx.core.app.NotificationCompat
import com.example.AssistiveTouchApp
import com.example.MainActivity
import com.example.R
import com.example.model.AssistiveSettings
import com.example.model.TouchAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs

class AssistiveOverlayService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var windowManager: WindowManager
    private var floatingView: AssistiveTouchView? = null
    private var overlayMenuDialog: AssistiveOverlayMenuDialog? = null
    private lateinit var layoutParams: WindowManager.LayoutParams

    private val handler = Handler(Looper.getMainLooper())
    private var isDragging = false
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var lastTapTime = 0L
    private val doubleTapTimeout = 280L
    private val longPressTimeout = 500L
    private var isLongPressed = false

    private var currentSettings = AssistiveSettings()

    private val idleFadeRunnable = Runnable {
        floatingView?.animate()
            ?.alpha(currentSettings.idleOpacity)
            ?.setDuration(350)
            ?.start()
    }

    private val longPressRunnable = Runnable {
        isLongPressed = true
        if (currentSettings.vibrateOnTap) {
            ActionExecutor.performHapticFeedback(this)
        }
        ActionExecutor.execute(
            context = this,
            action = currentSettings.longPressAction,
            onShowMenu = { showMenuOverlay() }
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())

        val repository = AssistiveTouchApp.instance.settingsRepository
        serviceScope.launch {
            repository.settings.collectLatest { settings ->
                currentSettings = settings
                if (!settings.isEnabled) {
                    stopSelf()
                    return@collectLatest
                }
                updateFloatingViewFromSettings()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            AssistiveTouchApp.instance.settingsRepository.setEnabled(false)
            stopSelf()
            return START_NOT_STICKY
        }

        if (Settings.canDrawOverlays(this)) {
            if (floatingView == null) {
                initFloatingView()
            }
        } else {
            stopSelf()
        }
        return START_STICKY
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFloatingView() {
        val touchView = AssistiveTouchView(this).also { floatingView = it }
        val density = resources.displayMetrics.density
        val sizePx = (currentSettings.buttonSizeDp * density).toInt()

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val metrics = getDisplayBounds()
        val screenWidth = metrics.widthPixels
        val screenHeight = metrics.heightPixels

        val initialPosX = (screenWidth * currentSettings.lastPositionXRatio).toInt().coerceIn(0, screenWidth - sizePx)
        val initialPosY = (screenHeight * currentSettings.lastPositionYRatio).toInt().coerceIn(100, screenHeight - sizePx - 100)

        layoutParams = WindowManager.LayoutParams(
            sizePx,
            sizePx,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = initialPosX
            y = initialPosY
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // Support full screen cutout (waterdrop notch on Redmi Note 8 Pro)
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        touchView.buttonTheme = currentSettings.theme
        touchView.alpha = currentSettings.idleOpacity

        touchView.setOnTouchListener { _, event ->
            handleTouchEvent(event)
        }

        try {
            windowManager.addView(touchView, layoutParams)
            resetIdleTimer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleTouchEvent(event: MotionEvent): Boolean {
        val view = floatingView ?: return false
        val touchSlop = ViewConfiguration.get(this).scaledTouchSlop

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handler.removeCallbacks(idleFadeRunnable)
                view.alpha = currentSettings.activeOpacity

                initialX = layoutParams.x
                initialY = layoutParams.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                isDragging = false
                isLongPressed = false

                handler.postDelayed(longPressRunnable, longPressTimeout)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - initialTouchX
                val dy = event.rawY - initialTouchY

                if (!isDragging && (abs(dx) > touchSlop || abs(dy) > touchSlop)) {
                    isDragging = true
                    handler.removeCallbacks(longPressRunnable)
                }

                if (isDragging) {
                    layoutParams.x = (initialX + dx).toInt()
                    layoutParams.y = (initialY + dy).toInt()
                    try {
                        windowManager.updateViewLayout(view, layoutParams)
                    } catch (_: Exception) {}
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                handler.removeCallbacks(longPressRunnable)

                if (isDragging) {
                    snapToEdgeIfNeeded()
                } else if (!isLongPressed) {
                    val now = System.currentTimeMillis()
                    if (now - lastTapTime < doubleTapTimeout) {
                        // Double tap detected!
                        lastTapTime = 0L
                        if (currentSettings.vibrateOnTap) {
                            ActionExecutor.performHapticFeedback(this)
                        }
                        ActionExecutor.execute(
                            context = this,
                            action = currentSettings.doubleTapAction,
                            onShowMenu = { showMenuOverlay() }
                        )
                    } else {
                        // Single tap detected
                        lastTapTime = now
                        handler.postDelayed({
                            if (lastTapTime == now) {
                                if (currentSettings.vibrateOnTap) {
                                    ActionExecutor.performHapticFeedback(this)
                                }
                                ActionExecutor.execute(
                                    context = this,
                                    action = currentSettings.singleTapAction,
                                    onShowMenu = { showMenuOverlay() }
                                )
                            }
                        }, doubleTapTimeout)
                    }
                }

                resetIdleTimer()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(longPressRunnable)
                resetIdleTimer()
                return true
            }
        }
        return false
    }

    private fun snapToEdgeIfNeeded() {
        val view = floatingView ?: return
        val metrics = getDisplayBounds()
        val screenWidth = metrics.widthPixels
        val viewWidth = layoutParams.width

        val targetX = if (currentSettings.snapToEdge) {
            val middle = screenWidth / 2
            if (layoutParams.x + viewWidth / 2 < middle) {
                0
            } else {
                screenWidth - viewWidth
            }
        } else {
            layoutParams.x.coerceIn(0, screenWidth - viewWidth)
        }

        // Animate horizontal snap smoothly
        val animator = ValueAnimator.ofInt(layoutParams.x, targetX).apply {
            duration = 200
            interpolator = DecelerateInterpolator()
            addUpdateListener { va ->
                layoutParams.x = va.animatedValue as Int
                try {
                    windowManager.updateViewLayout(view, layoutParams)
                } catch (_: Exception) {}
            }
        }
        animator.start()

        // Save position ratio
        val ratioX = targetX.toFloat() / screenWidth.coerceAtLeast(1)
        val ratioY = layoutParams.y.toFloat() / metrics.heightPixels.coerceAtLeast(1)
        AssistiveTouchApp.instance.settingsRepository.savePosition(ratioX, ratioY)
    }

    private fun resetIdleTimer() {
        handler.removeCallbacks(idleFadeRunnable)
        handler.postDelayed(idleFadeRunnable, currentSettings.idleTimeoutMs)
    }

    private fun showMenuOverlay() {
        if (overlayMenuDialog == null) {
            overlayMenuDialog = AssistiveOverlayMenuDialog(this, windowManager) { action ->
                if (currentSettings.vibrateOnTap) {
                    ActionExecutor.performHapticFeedback(this)
                }
                ActionExecutor.execute(
                    context = this,
                    action = action,
                    onShowMenu = null
                )
            }
        }
        overlayMenuDialog?.show(currentSettings.menuActions, currentSettings.theme)
    }

    private fun updateFloatingViewFromSettings() {
        val view = floatingView ?: return
        val density = resources.displayMetrics.density
        val newSize = (currentSettings.buttonSizeDp * density).toInt()

        layoutParams.width = newSize
        layoutParams.height = newSize
        view.buttonTheme = currentSettings.theme

        try {
            windowManager.updateViewLayout(view, layoutParams)
        } catch (_: Exception) {}

        resetIdleTimer()
    }

    private fun getDisplayBounds(): DisplayMetrics {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(metrics)
        return metrics
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Assistive Touch Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Menjaga tombol virtual tetap aktif di atas aplikasi lain"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingOpenApp = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, AssistiveOverlayService::class.java).apply {
            action = ACTION_STOP
        }
        val pendingStop = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.overlay_notification_title))
            .setContentText(getString(R.string.overlay_notification_text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingOpenApp)
            .addAction(0, getString(R.string.stop_service), pendingStop)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        handler.removeCallbacksAndMessages(null)
        overlayMenuDialog?.dismiss()
        floatingView?.let { view ->
            try {
                windowManager.removeView(view)
            } catch (_: Exception) {}
        }
        floatingView = null
    }

    companion object {
        const val CHANNEL_ID = "assistive_touch_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_STOP = "com.example.assistivetouch.ACTION_STOP"

        fun start(context: Context) {
            val intent = Intent(context, AssistiveOverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, AssistiveOverlayService::class.java)
            context.stopService(intent)
        }
    }
}
