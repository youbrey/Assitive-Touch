package com.example.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast

object MiuiDeviceHelper {

    fun isMiuiDevice(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()
        return manufacturer.contains("xiaomi") ||
                brand.contains("xiaomi") ||
                brand.contains("redmi") ||
                brand.contains("poco")
    }

    fun isRedmiNote8Pro(): Boolean {
        val model = Build.MODEL.lowercase()
        val device = Build.DEVICE.lowercase()
        val product = Build.PRODUCT.lowercase()
        return model.contains("note 8 pro") ||
                device.contains("begonia") ||
                product.contains("begonia")
    }

    fun isAndroid10(): Boolean {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
    }

    fun getDeviceSummary(): String {
        val deviceName = if (isRedmiNote8Pro()) {
            "Redmi Note 8 Pro (Begonia)"
        } else {
            "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"
        }
        val osVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
        return "$deviceName • $osVersion"
    }

    fun isBatteryOptimizationIgnored(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        return powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false
    }

    /**
     * Opens MIUI Autostart settings to prevent MIUI from killing the background service.
     */
    fun openMiuiAutostart(context: Context) {
        val intents = listOf(
            Intent().setComponent(
                ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
            ),
            Intent().setComponent(
                ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")
            ),
            Intent().setComponent(
                ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")
            ),
            Intent(Settings.ACTION_SETTINGS)
        )

        for (intent in intents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return
            } catch (_: Exception) {}
        }
        Toast.makeText(context, "Buka Pengaturan -> Keamanan -> Mulai Otomatis (Autostart)", Toast.LENGTH_LONG).show()
    }

    /**
     * Opens MIUI specific "Display pop-up windows while running in the background" permission.
     * This is required on MIUI (Redmi Note 8 Pro) for overlays to show!
     */
    fun openMiuiPopupPermission(context: Context) {
        val intents = listOf(
            Intent("miui.intent.action.APP_PERM_EDITOR").apply {
                setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
                putExtra("extra_pkgname", context.packageName)
            },
            Intent("miui.intent.action.APP_PERM_EDITOR").apply {
                setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
                putExtra("extra_pkgname", context.packageName)
            },
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        )

        for (intent in intents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return
            } catch (_: Exception) {}
        }
        Toast.makeText(context, "Buka Pengaturan Aplikasi -> Perizinan Lainnya -> Tampil Jendela Pop-up", Toast.LENGTH_LONG).show()
    }

    /**
     * Opens MIUI Battery Saver settings to choose "No Restrictions" (Tidak Ada Pembatasan).
     */
    fun openMiuiBatterySaver(context: Context) {
        val intents = listOf(
            Intent().apply {
                setComponent(ComponentName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"))
                putExtra("package_name", context.packageName)
                putExtra("package_label", context.applicationInfo.loadLabel(context.packageManager))
            },
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
            },
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        )

        for (intent in intents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return
            } catch (_: Exception) {}
        }
        Toast.makeText(context, "Pilih opsi 'Tidak Ada Pembatasan' pada Penghemat Baterai", Toast.LENGTH_LONG).show()
    }
}
