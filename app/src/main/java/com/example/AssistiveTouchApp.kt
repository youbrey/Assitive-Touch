package com.example

import android.app.Application
import com.example.data.SettingsRepository

class AssistiveTouchApp : Application() {

    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        settingsRepository = SettingsRepository(this)
    }

    companion object {
        lateinit var instance: AssistiveTouchApp
            private set
    }
}
