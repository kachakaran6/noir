package com.samyukt.noir.util

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.provider.Settings

data class DefaultLauncherStatus(
    val isDefault: Boolean = false,
    val currentDefaultPackage: String? = null,
    val currentDefaultLabel: String? = null
)

class DefaultLauncherManager(private val context: Context) {

    fun checkDefaultLauncherStatus(): DefaultLauncherStatus {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }

        val resolveInfo: ResolveInfo? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.resolveActivity(
                homeIntent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.resolveActivity(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)
        }

        val pkgName = resolveInfo?.activityInfo?.packageName
        val isSystemResolver = pkgName == null || pkgName == "android" || pkgName.contains("resolver", ignoreCase = true)
        val isDefault = !isSystemResolver && pkgName == context.packageName

        val label = if (resolveInfo != null && !isSystemResolver) {
            runCatching {
                resolveInfo.loadLabel(context.packageManager)?.toString()
            }.getOrNull() ?: pkgName
        } else {
            null
        }

        return DefaultLauncherStatus(
            isDefault = isDefault,
            currentDefaultPackage = if (isSystemResolver) null else pkgName,
            currentDefaultLabel = label
        )
    }

    fun requestDefaultLauncher(activity: Activity, requestCode: Int = 1001) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = activity.getSystemService(RoleManager::class.java)
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
                    try {
                        @Suppress("DEPRECATION")
                        activity.startActivityForResult(intent, requestCode)
                        return
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        openDefaultLauncherSettings(activity)
    }

    fun openDefaultLauncherSettings(context: Context) {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val fallbackIntent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (e2: Exception) {
                val generalSettings = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(generalSettings)
            }
        }
    }
}

