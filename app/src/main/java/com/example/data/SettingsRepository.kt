package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AssistiveSettings
import com.example.model.ButtonTheme
import com.example.model.TouchAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AssistiveSettings> = _settings.asStateFlow()

    private fun loadSettings(): AssistiveSettings {
        val isEnabled = prefs.getBoolean(KEY_IS_ENABLED, false)
        val buttonSizeDp = prefs.getFloat(KEY_BUTTON_SIZE, 52f)
        val idleOpacity = prefs.getFloat(KEY_IDLE_OPACITY, 0.35f)
        val activeOpacity = prefs.getFloat(KEY_ACTIVE_OPACITY, 0.95f)
        val snapToEdge = prefs.getBoolean(KEY_SNAP_EDGE, true)
        val vibrate = prefs.getBoolean(KEY_VIBRATE, true)

        val themeName = prefs.getString(KEY_THEME, ButtonTheme.CYBER_NEON.name)
        val theme = runCatching { ButtonTheme.valueOf(themeName ?: "") }.getOrDefault(ButtonTheme.CYBER_NEON)

        val singleTap = runCatching {
            TouchAction.valueOf(prefs.getString(KEY_SINGLE_TAP, TouchAction.OPEN_MENU.name) ?: "")
        }.getOrDefault(TouchAction.OPEN_MENU)

        val doubleTap = runCatching {
            TouchAction.valueOf(prefs.getString(KEY_DOUBLE_TAP, TouchAction.SCREENSHOT.name) ?: "")
        }.getOrDefault(TouchAction.SCREENSHOT)

        val longPress = runCatching {
            TouchAction.valueOf(prefs.getString(KEY_LONG_PRESS, TouchAction.POWER_DIALOG.name) ?: "")
        }.getOrDefault(TouchAction.POWER_DIALOG)

        val posX = prefs.getFloat(KEY_POS_X, 0.92f)
        val posY = prefs.getFloat(KEY_POS_Y, 0.45f)

        val savedMenuActions = prefs.getString(KEY_MENU_ACTIONS, null)?.split(",")?.mapNotNull { name ->
            runCatching { TouchAction.valueOf(name) }.getOrNull()
        } ?: AssistiveSettings.defaultMenuActions

        val menuActions = if (savedMenuActions.size == 9) savedMenuActions else AssistiveSettings.defaultMenuActions

        return AssistiveSettings(
            isEnabled = isEnabled,
            buttonSizeDp = buttonSizeDp,
            idleOpacity = idleOpacity,
            activeOpacity = activeOpacity,
            snapToEdge = snapToEdge,
            vibrateOnTap = vibrate,
            theme = theme,
            singleTapAction = singleTap,
            doubleTapAction = doubleTap,
            longPressAction = longPress,
            menuActions = menuActions,
            lastPositionXRatio = posX,
            lastPositionYRatio = posY
        )
    }

    fun updateSettings(transform: (AssistiveSettings) -> AssistiveSettings) {
        val newSettings = transform(_settings.value)
        _settings.value = newSettings
        persistSettings(newSettings)
    }

    fun setEnabled(enabled: Boolean) {
        updateSettings { it.copy(isEnabled = enabled) }
    }

    fun setButtonSize(sizeDp: Float) {
        updateSettings { it.copy(buttonSizeDp = sizeDp) }
    }

    fun setIdleOpacity(opacity: Float) {
        updateSettings { it.copy(idleOpacity = opacity) }
    }

    fun setActiveOpacity(opacity: Float) {
        updateSettings { it.copy(activeOpacity = opacity) }
    }

    fun setSnapToEdge(snap: Boolean) {
        updateSettings { it.copy(snapToEdge = snap) }
    }

    fun setVibrate(vibrate: Boolean) {
        updateSettings { it.copy(vibrateOnTap = vibrate) }
    }

    fun setTheme(theme: ButtonTheme) {
        updateSettings { it.copy(theme = theme) }
    }

    fun setSingleTapAction(action: TouchAction) {
        updateSettings { it.copy(singleTapAction = action) }
    }

    fun setDoubleTapAction(action: TouchAction) {
        updateSettings { it.copy(doubleTapAction = action) }
    }

    fun setLongPressAction(action: TouchAction) {
        updateSettings { it.copy(longPressAction = action) }
    }

    fun setMenuAction(index: Int, action: TouchAction) {
        updateSettings { current ->
            val updated = current.menuActions.toMutableList()
            if (index in updated.indices) {
                updated[index] = action
            }
            current.copy(menuActions = updated)
        }
    }

    fun resetMenuActions() {
        updateSettings { it.copy(menuActions = AssistiveSettings.defaultMenuActions) }
    }

    fun savePosition(ratioX: Float, ratioY: Float) {
        updateSettings { it.copy(lastPositionXRatio = ratioX, lastPositionYRatio = ratioY) }
    }

    private fun persistSettings(settings: AssistiveSettings) {
        prefs.edit().apply {
            putBoolean(KEY_IS_ENABLED, settings.isEnabled)
            putFloat(KEY_BUTTON_SIZE, settings.buttonSizeDp)
            putFloat(KEY_IDLE_OPACITY, settings.idleOpacity)
            putFloat(KEY_ACTIVE_OPACITY, settings.activeOpacity)
            putBoolean(KEY_SNAP_EDGE, settings.snapToEdge)
            putBoolean(KEY_VIBRATE, settings.vibrateOnTap)
            putString(KEY_THEME, settings.theme.name)
            putString(KEY_SINGLE_TAP, settings.singleTapAction.name)
            putString(KEY_DOUBLE_TAP, settings.doubleTapAction.name)
            putString(KEY_LONG_PRESS, settings.longPressAction.name)
            putFloat(KEY_POS_X, settings.lastPositionXRatio)
            putFloat(KEY_POS_Y, settings.lastPositionYRatio)
            putString(KEY_MENU_ACTIONS, settings.menuActions.joinToString(",") { it.name })
            apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "assistive_touch_prefs"
        private const val KEY_IS_ENABLED = "key_is_enabled"
        private const val KEY_BUTTON_SIZE = "key_button_size"
        private const val KEY_IDLE_OPACITY = "key_idle_opacity"
        private const val KEY_ACTIVE_OPACITY = "key_active_opacity"
        private const val KEY_SNAP_EDGE = "key_snap_edge"
        private const val KEY_VIBRATE = "key_vibrate"
        private const val KEY_THEME = "key_theme"
        private const val KEY_SINGLE_TAP = "key_single_tap"
        private const val KEY_DOUBLE_TAP = "key_double_tap"
        private const val KEY_LONG_PRESS = "key_long_press"
        private const val KEY_POS_X = "key_pos_x"
        private const val KEY_POS_Y = "key_pos_y"
        private const val KEY_MENU_ACTIONS = "key_menu_actions"
    }
}
