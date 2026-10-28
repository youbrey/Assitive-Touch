package com.example.model

data class AssistiveSettings(
    val isEnabled: Boolean = false,
    val buttonSizeDp: Float = 52f,
    val idleOpacity: Float = 0.35f,
    val activeOpacity: Float = 0.95f,
    val idleTimeoutMs: Long = 2500L,
    val snapToEdge: Boolean = true,
    val vibrateOnTap: Boolean = true,
    val theme: ButtonTheme = ButtonTheme.CYBER_NEON,
    val singleTapAction: TouchAction = TouchAction.OPEN_MENU,
    val doubleTapAction: TouchAction = TouchAction.SCREENSHOT,
    val longPressAction: TouchAction = TouchAction.POWER_DIALOG,
    val menuActions: List<TouchAction> = defaultMenuActions,
    val lastPositionXRatio: Float = 0.92f,
    val lastPositionYRatio: Float = 0.45f
) {
    companion object {
        val defaultMenuActions = listOf(
            TouchAction.SCREENSHOT,
            TouchAction.LOCK_SCREEN,
            TouchAction.VOLUME_PANEL,
            TouchAction.POWER_DIALOG,
            TouchAction.HOME,
            TouchAction.BACK,
            TouchAction.RECENTS,
            TouchAction.NOTIFICATIONS,
            TouchAction.QUICK_SETTINGS
        )
    }
}
