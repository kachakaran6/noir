package com.samyukt.noir.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.samyukt.noir.model.ThemeMode

val LocalGraphiteColors = staticCompositionLocalOf { LightGraphiteColors }

object GraphiteTheme {
    val colors: GraphiteColors
        @Composable
        @ReadOnlyComposable
        get() = LocalGraphiteColors.current
}

@Composable
fun GraphitePaperTheme(
    themeMode: ThemeMode = ThemeMode.PAPER_LIGHT,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.PAPER_LIGHT -> false
        ThemeMode.GRAPHITE_DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val graphiteColors = if (isDark) DarkGraphiteColors else LightGraphiteColors

    val colorScheme = if (isDark) {
        darkColorScheme(
            background = graphiteColors.background,
            surface = graphiteColors.surface,
            onBackground = graphiteColors.primaryText,
            onSurface = graphiteColors.primaryText,
            primary = graphiteColors.primaryText,
            secondary = graphiteColors.secondaryText,
            outline = graphiteColors.divider
        )
    } else {
        lightColorScheme(
            background = graphiteColors.background,
            surface = graphiteColors.surface,
            onBackground = graphiteColors.primaryText,
            onSurface = graphiteColors.primaryText,
            primary = graphiteColors.primaryText,
            secondary = graphiteColors.secondaryText,
            outline = graphiteColors.divider
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = graphiteColors.background.toArgb()
                window.navigationBarColor = graphiteColors.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(
        LocalGraphiteColors provides graphiteColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = GraphiteTypography,
            content = content
        )
    }
}

