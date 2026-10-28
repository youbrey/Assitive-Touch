package com.example.system

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.widget.Toast
import com.example.model.TouchAction

object ActionExecutor {

    private var isTorchOn: Boolean = false

    fun performHapticFeedback(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Optimized for Android 10 (API 29 / Redmi Note 8 Pro)
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(30)
                }
            }
        } catch (_: Exception) {
            // Ignore if vibration fails or device has no vibrator
        }
    }

    fun execute(
        context: Context,
        action: TouchAction,
        onShowMenu: (() -> Unit)? = null,
        onShowVolumeDialog: (() -> Unit)? = null
    ) {
        when (action) {
            TouchAction.NONE -> Unit
            TouchAction.OPEN_MENU -> onShowMenu?.invoke()
            TouchAction.VOLUME_PANEL -> {
                if (onShowVolumeDialog != null) {
                    onShowVolumeDialog.invoke()
                } else {
                    // Trigger native Android volume UI
                    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                    audioManager?.adjustSuggestedStreamVolume(
                        AudioManager.ADJUST_SAME,
                        AudioManager.USE_DEFAULT_STREAM_TYPE,
                        AudioManager.FLAG_SHOW_UI
                    )
                }
            }
            TouchAction.FLASHLIGHT -> toggleFlashlight(context)
            TouchAction.SCREENSHOT,
            TouchAction.LOCK_SCREEN,
            TouchAction.POWER_DIALOG,
            TouchAction.HOME,
            TouchAction.BACK,
            TouchAction.RECENTS,
            TouchAction.NOTIFICATIONS,
            TouchAction.QUICK_SETTINGS,
            TouchAction.SPLIT_SCREEN -> {
                val service = AssistiveAccessibilityService.instance
                if (service != null) {
                    val success = service.executeAction(action)
                    if (!success) {
                        Toast.makeText(
                            context,
                            "Perintah ${action.title} tidak didukung pada versi sistem ini.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Aktifkan Layanan Aksesibilitas Assistive Touch untuk ${action.title}",
                        Toast.LENGTH_LONG
                    ).show()
                    promptAccessibilitySettings(context)
                }
            }
        }
    }

    private fun promptAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Buka Pengaturan -> Aksesibilitas untuk mengaktifkan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleFlashlight(context: Context) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager ?: return
        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return
            isTorchOn = !isTorchOn
            cameraManager.setTorchMode(cameraId, isTorchOn)
            val msg = if (isTorchOn) "Senter dinyalakan" else "Senter dimatikan"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        } catch (e: CameraAccessException) {
            Toast.makeText(context, "Senter tidak dapat diakses saat ini.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Perangkat tidak mendukung senter.", Toast.LENGTH_SHORT).show()
        }
    }
}
