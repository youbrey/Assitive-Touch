package com.example.ui.viewmodel

import android.content.Context
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AssistiveTouchApp
import com.example.model.AssistiveSettings
import com.example.model.ButtonTheme
import com.example.model.TouchAction
import com.example.system.ActionExecutor
import com.example.system.AssistiveAccessibilityService
import com.example.system.AssistiveOverlayService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel : ViewModel() {

    private val repository = AssistiveTouchApp.instance.settingsRepository

    val settings: StateFlow<AssistiveSettings> = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.settings.value
    )

    private val _isOverlayGranted = MutableStateFlow(false)
    val isOverlayGranted: StateFlow<Boolean> = _isOverlayGranted.asStateFlow()

    private val _isAccessibilityGranted = MutableStateFlow(false)
    val isAccessibilityGranted: StateFlow<Boolean> = _isAccessibilityGranted.asStateFlow()

    fun checkPermissions(context: Context) {
        _isOverlayGranted.value = Settings.canDrawOverlays(context)
        _isAccessibilityGranted.value = AssistiveAccessibilityService.isRunning()
    }

    fun setServiceEnabled(context: Context, enabled: Boolean) {
        repository.setEnabled(enabled)
        if (enabled) {
            if (Settings.canDrawOverlays(context)) {
                AssistiveOverlayService.start(context)
            }
        } else {
            AssistiveOverlayService.stop(context)
        }
    }

    fun setTheme(theme: ButtonTheme) = repository.setTheme(theme)
    fun setButtonSize(sizeDp: Float) = repository.setButtonSize(sizeDp)
    fun setIdleOpacity(opacity: Float) = repository.setIdleOpacity(opacity)
    fun setActiveOpacity(opacity: Float) = repository.setActiveOpacity(opacity)
    fun setSnapToEdge(snap: Boolean) = repository.setSnapToEdge(snap)
    fun setVibrate(vibrate: Boolean) = repository.setVibrate(vibrate)
    fun setSingleTap(action: TouchAction) = repository.setSingleTapAction(action)
    fun setDoubleTap(action: TouchAction) = repository.setDoubleTapAction(action)
    fun setLongPress(action: TouchAction) = repository.setLongPressAction(action)
    fun setMenuAction(index: Int, action: TouchAction) = repository.setMenuAction(index, action)
    fun resetMenuActions() = repository.resetMenuActions()

    fun executeAction(
        context: Context,
        action: TouchAction,
        onShowMenu: (() -> Unit)? = null,
        onShowVolumeDialog: (() -> Unit)? = null
    ) {
        if (settings.value.vibrateOnTap) {
            ActionExecutor.performHapticFeedback(context)
        }
        ActionExecutor.execute(
            context = context,
            action = action,
            onShowMenu = onShowMenu,
            onShowVolumeDialog = onShowVolumeDialog
        )
    }
}
