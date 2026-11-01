package com.graphite.paper.launcher.model

import androidx.compose.ui.graphics.ImageBitmap

data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val iconBitmap: ImageBitmap? = null,
    val isPinned: Boolean = false,
    val isHidden: Boolean = false,
    val firstInstallTime: Long = 0L,
    val lastUsedTime: Long = 0L
)
