package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.AssistiveSettings
import com.example.model.TouchAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29]) // Android 10 (API 29) for Redmi Note 8 Pro compatibility
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertNotNull(appName)
        assertTrue(appName.isNotBlank())
    }

    @Test
    fun `default assistive settings contains screenshot lock and power`() {
        val settings = AssistiveSettings()
        assertEquals(TouchAction.SCREENSHOT, settings.doubleTapAction)
        assertEquals(TouchAction.POWER_DIALOG, settings.longPressAction)
        assertEquals(TouchAction.OPEN_MENU, settings.singleTapAction)
        assertTrue(settings.menuActions.contains(TouchAction.SCREENSHOT))
        assertTrue(settings.menuActions.contains(TouchAction.LOCK_SCREEN))
        assertTrue(settings.menuActions.contains(TouchAction.VOLUME_PANEL))
        assertTrue(settings.menuActions.contains(TouchAction.POWER_DIALOG))
    }
}
