package com.graphite.paper.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphite.paper.launcher.ui.screens.HiddenAppsScreen
import com.graphite.paper.launcher.ui.screens.HomeScreen
import com.graphite.paper.launcher.ui.screens.SettingsScreen
import com.graphite.paper.launcher.ui.theme.GraphitePaperTheme
import com.graphite.paper.launcher.viewmodel.LauncherViewModel
import com.graphite.paper.launcher.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            // Back button navigation
            BackHandler(enabled = uiState.currentScreen != Screen.HOME || uiState.searchQuery.isNotEmpty() || uiState.selectedAppForMenu != null) {
                when {
                    uiState.selectedAppForMenu != null -> viewModel.selectAppForMenu(null)
                    uiState.currentScreen == Screen.HIDDEN_APPS -> viewModel.navigateTo(Screen.SETTINGS)
                    uiState.currentScreen == Screen.SETTINGS -> viewModel.navigateTo(Screen.HOME)
                    uiState.searchQuery.isNotEmpty() -> viewModel.onSearchQueryChange("")
                }
            }

            GraphitePaperTheme(themeMode = uiState.settings.themeMode) {
                Crossfade(targetState = uiState.currentScreen, label = "ScreenTransition") { screen ->
                    when (screen) {
                        Screen.HOME -> {
                            HomeScreen(
                                uiState = uiState,
                                onSearchQueryChange = viewModel::onSearchQueryChange,
                                onOpenSettings = { viewModel.navigateTo(Screen.SETTINGS) },
                                onLaunchApp = viewModel::launchApp,
                                onSelectAppForMenu = viewModel::selectAppForMenu,
                                onAppInfo = viewModel::openAppDetails,
                                onHideApp = viewModel::hideApp,
                                onUninstallApp = viewModel::uninstallApp
                            )
                        }
                        Screen.SETTINGS -> {
                            SettingsScreen(
                                uiState = uiState,
                                onBack = { viewModel.navigateTo(Screen.HOME) },
                                onToggleSearchBar = viewModel::toggleSearchBar,
                                onToggleDate = viewModel::toggleDate,
                                onToggleClock = viewModel::toggleClock,
                                onToggleIcons = viewModel::toggleIcons,
                                onSetIconSize = viewModel::setIconSize,
                                onSetIconTint = viewModel::setIconTint,
                                onSetSortOrder = viewModel::setSortOrder,
                                onSetThemeMode = viewModel::setThemeMode,
                                onToggleGrain = viewModel::toggleGrain,
                                onToggleSectionHeaders = viewModel::toggleSectionHeaders,
                                onOpenHiddenApps = { viewModel.navigateTo(Screen.HIDDEN_APPS) },
                                onResetDefaults = viewModel::resetDefaults
                            )
                        }
                        Screen.HIDDEN_APPS -> {
                            HiddenAppsScreen(
                                uiState = uiState,
                                onBack = { viewModel.navigateTo(Screen.SETTINGS) },
                                onUnhideApp = viewModel::unhideApp
                            )
                        }
                    }
                }
            }
        }
    }
}
