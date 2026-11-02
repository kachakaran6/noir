package com.samyukt.noir.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

enum class CompatibilityStatus {
    COMPATIBLE,
    SYSTEM_RESTRICTED,
    UNKNOWN
}

enum class NavigationMode(val displayName: String) {
    THREE_BUTTON("3-Button Navigation"),
    GESTURES("Full-Screen Gestures"),
    TWO_BUTTON("2-Button Navigation"),
    UNKNOWN("Unknown / System Managed")
}

data class DeviceCompatibilityInfo(
    val manufacturer: String,
    val brand: String,
    val model: String,
    val androidVersion: String,
    val sdkInt: Int,
    val osFlavor: String,
    val isXiaomiOrPoco: Boolean,
    val currentNavMode: NavigationMode,
    val isDefaultLauncher: Boolean,
    val status: CompatibilityStatus,
    val restrictionSummary: String,
    val guidanceTitle: String?,
    val guidanceDetails: List<String>
)

class DeviceCompatibilityManager(private val context: Context) {

    fun getCompatibilityInfo(isDefaultLauncher: Boolean): DeviceCompatibilityInfo {
        val manufacturer = Build.MANUFACTURER.orEmpty()
        val brand = Build.BRAND.orEmpty()
        val model = Build.MODEL.orEmpty()
        val sdkInt = Build.VERSION.SDK_INT
        val release = Build.VERSION.RELEASE

        val miuiVersion = getSystemProperty("ro.miui.ui.version.name")
        val hyperOsVersion = getSystemProperty("ro.mi.os.version.name")

        val isXiaomi = manufacturer.contains("Xiaomi", ignoreCase = true) ||
                brand.contains("Xiaomi", ignoreCase = true)
        val isPoco = brand.contains("POCO", ignoreCase = true) ||
                model.contains("POCO", ignoreCase = true) ||
                getSystemProperty("ro.product.brand")?.contains("POCO", ignoreCase = true) == true
        val isRedmi = brand.contains("Redmi", ignoreCase = true) ||
                getSystemProperty("ro.product.brand")?.contains("Redmi", ignoreCase = true) == true

        val isXiaomiOrPoco = isXiaomi || isPoco || isRedmi || !miuiVersion.isNullOrBlank() || !hyperOsVersion.isNullOrBlank()

        val osFlavor = when {
            !hyperOsVersion.isNullOrBlank() -> "Xiaomi HyperOS ($hyperOsVersion)"
            !miuiVersion.isNullOrBlank() -> "MIUI ($miuiVersion)"
            isPoco -> "POCO OS / Android"
            isXiaomi -> "Xiaomi Android"
            else -> "Standard Android"
        }

        val navMode = detectNavigationMode()

        // Xiaomi / POCO / Redmi firmware blocks full-screen gestures for 3rd party launchers
        val status = if (isXiaomiOrPoco) {
            CompatibilityStatus.SYSTEM_RESTRICTED
        } else {
            CompatibilityStatus.COMPATIBLE
        }

        val restrictionSummary = if (status == CompatibilityStatus.SYSTEM_RESTRICTED) {
            "Your device restricts full-screen gesture navigation for some third-party launchers."
        } else {
            "Your device is fully compatible with third-party launcher navigation."
        }

        val guidanceTitle = if (status == CompatibilityStatus.SYSTEM_RESTRICTED) {
            "POCO / Xiaomi Gesture Policy"
        } else {
            null
        }

        val guidanceDetails = if (status == CompatibilityStatus.SYSTEM_RESTRICTED) {
            listOf(
                "This is a device manufacturer restriction enforced directly by Xiaomi / POCO firmware (MIUI & HyperOS).",
                "NOIR cannot independently override this system restriction without unsupported system modifications.",
                "Three-button navigation (Back, Home, Recents) is fully supported and provides 100% reliable operation.",
                "Gesture availability depends on device firmware updates and OEM policy."
            )
        } else {
            emptyList()
        }

        return DeviceCompatibilityInfo(
            manufacturer = manufacturer.replaceFirstChar { it.uppercase() },
            brand = brand.uppercase(),
            model = model,
            androidVersion = "Android $release (API $sdkInt)",
            sdkInt = sdkInt,
            osFlavor = osFlavor,
            isXiaomiOrPoco = isXiaomiOrPoco,
            currentNavMode = navMode,
            isDefaultLauncher = isDefaultLauncher,
            status = status,
            restrictionSummary = restrictionSummary,
            guidanceTitle = guidanceTitle,
            guidanceDetails = guidanceDetails
        )
    }

    private fun detectNavigationMode(): NavigationMode {
        return try {
            // Check Xiaomi specific full screen gesture setting
            val fsg = Settings.Global.getInt(context.contentResolver, "force_fsg_nav_bar", -1)
            if (fsg == 1) return NavigationMode.GESTURES
            if (fsg == 0) return NavigationMode.THREE_BUTTON

            // Standard Android navigation mode
            when (Settings.Secure.getInt(context.contentResolver, "navigation_mode", -1)) {
                0 -> NavigationMode.THREE_BUTTON
                1 -> NavigationMode.TWO_BUTTON
                2 -> NavigationMode.GESTURES
                else -> NavigationMode.UNKNOWN
            }
        } catch (e: Exception) {
            NavigationMode.UNKNOWN
        }
    }

    fun openNavigationSettings(): Boolean {
        // 1. Try Xiaomi Full Screen Display Settings Activity
        val miuiGestureIntent = Intent().apply {
            component = ComponentName(
                "com.android.settings",
                "com.android.settings.Settings\$FullScreenDisplaySettingsActivity"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (canResolve(miuiGestureIntent)) {
            try {
                context.startActivity(miuiGestureIntent)
                return true
            } catch (e: Exception) {
                // fall through
            }
        }

        // 2. Try Xiaomi System Navigation Settings Activity
        val miuiNavIntent = Intent().apply {
            component = ComponentName(
                "com.android.settings",
                "com.android.settings.Settings\$SystemNavigationSettingsActivity"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (canResolve(miuiNavIntent)) {
            try {
                context.startActivity(miuiNavIntent)
                return true
            } catch (e: Exception) {
                // fall through
            }
        }

        // 3. Try standard Android gesture navigation action
        val gestureSettings = Intent("android.settings.GESTURE_NAVIGATION_SETTINGS").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (canResolve(gestureSettings)) {
            try {
                context.startActivity(gestureSettings)
                return true
            } catch (e: Exception) {
                // fall through
            }
        }

        // 4. Try Display Settings
        val displayIntent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (canResolve(displayIntent)) {
            try {
                context.startActivity(displayIntent)
                return true
            } catch (e: Exception) {
                // fall through
            }
        }

        // 5. Fallback General Settings
        val generalSettings = Intent(Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(generalSettings)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun canResolve(intent: Intent): Boolean {
        return context.packageManager.resolveActivity(intent, 0) != null
    }

    private fun getSystemProperty(key: String): String? {
        return try {
            val clazz = Class.forName("android.os.SystemProperties")
            val method = clazz.getMethod("get", String::class.java)
            val value = method.invoke(null, key) as? String
            value?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }
}
