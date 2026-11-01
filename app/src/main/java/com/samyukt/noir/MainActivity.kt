package com.samyukt.noir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samyukt.noir.ui.screens.HiddenAppsScreen
import com.samyukt.noir.ui.screens.HomeScreen
import com.samyukt.noir.ui.screens.IconCustomizationScreen
import com.samyukt.noir.ui.screens.SettingsScreen
import com.samyukt.noir.ui.theme.GraphitePaperTheme
import com.samyukt.noir.viewmodel.LauncherViewModel
import com.samyukt.noir.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val canNavigateBack = uiState.currentScreen != Screen.HOME ||
                    uiState.selectedAppForMenu != null ||
                    uiState.searchQuery.isNotEmpty()

            // Back button navigation: enabled only when there is an active subscreen, search, or dialog.
            // When at root home screen, BackHandler is disabled so native Android launcher gestures are preserved.
            BackHandler(enabled = canNavigateBack) {
                when {
                    uiState.selectedAppForMenu != null -> viewModel.selectAppForMenu(null)
                    uiState.searchQuery.isNotEmpty() -> viewModel.onSearchQueryChange("")
                    uiState.currentScreen == Screen.ICON_CUSTOMIZATION -> viewModel.navigateTo(Screen.SETTINGS)
                    uiState.currentScreen == Screen.HIDDEN_APPS -> viewModel.navigateTo(Screen.SETTINGS)
                    uiState.currentScreen == Screen.SETTINGS -> viewModel.navigateTo(Screen.HOME)
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
                                onUninstallApp = viewModel::uninstallApp,
                                onSetDefaultLauncher = { viewModel.requestDefaultLauncher(this@MainActivity) }
                            )
                        }
                        Screen.SETTINGS -> {
                            SettingsScreen(
                                uiState = uiState,
                                onBack = { viewModel.navigateTo(Screen.HOME) },
                                onSetDefaultLauncher = { viewModel.requestDefaultLauncher(this@MainActivity) },
                                onToggleSearchBar = viewModel::toggleSearchBar,
                                onToggleDate = viewModel::toggleDate,
                                onToggleClock = viewModel::toggleClock,
                                onToggleIcons = viewModel::toggleIcons,
                                onOpenIconCustomization = { viewModel.navigateTo(Screen.ICON_CUSTOMIZATION) },
                                onSetSortOrder = viewModel::setSortOrder,
                                onSetThemeMode = viewModel::setThemeMode,
                                onToggleGrain = viewModel::toggleGrain,
                                onToggleSectionHeaders = viewModel::toggleSectionHeaders,
                                onToggleAlphabetIndex = viewModel::toggleAlphabetIndex,
                                onOpenUsageSettings = viewModel::openUsageAccessSettings,
                                onOpenHiddenApps = { viewModel.navigateTo(Screen.HIDDEN_APPS) },
                                onResetDefaults = viewModel::resetDefaults
                            )
                        }
                        Screen.ICON_CUSTOMIZATION -> {
                            IconCustomizationScreen(
                                uiState = uiState,
                                onBack = { viewModel.navigateTo(Screen.SETTINGS) },
                                onSetIconShape = viewModel::setIconShape,
                                onSetIconColorMode = viewModel::setIconColorMode,
                                onSetCustomTint = viewModel::setCustomIconTint,
                                onSetIconSizeDp = viewModel::setIconSizeDp
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

    override fun onResume() {
        super.onResume()
        viewModel.refreshDefaultLauncherStatus()
    }
}
