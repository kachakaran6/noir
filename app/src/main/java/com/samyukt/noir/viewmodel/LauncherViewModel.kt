package com.samyukt.noir.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.samyukt.noir.data.AppRepository
import com.samyukt.noir.data.SettingsRepository
import com.samyukt.noir.model.AppInfo
import com.samyukt.noir.model.IconColorMode
import com.samyukt.noir.model.IconShape
import com.samyukt.noir.model.LauncherSettings
import com.samyukt.noir.model.SortOrder
import com.samyukt.noir.model.ThemeMode
import com.samyukt.noir.util.DefaultLauncherManager
import com.samyukt.noir.util.DefaultLauncherStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    SETTINGS,
    ICON_CUSTOMIZATION,
    HIDDEN_APPS
}

data class LauncherUiState(
    val apps: List<AppInfo> = emptyList(),
    val totalInstalledCount: Int = 0,
    val previewApps: List<AppInfo> = emptyList(),
    val availableLetters: Set<String> = emptySet(),
    val hiddenApps: List<AppInfo> = emptyList(),
    val settings: LauncherSettings = LauncherSettings(),
    val searchQuery: String = "",
    val currentScreen: Screen = Screen.HOME,
    val selectedAppForMenu: AppInfo? = null,
    val defaultLauncherStatus: DefaultLauncherStatus = DefaultLauncherStatus(),
    val hasUsageStatsAccess: Boolean = false,
    val isLoading: Boolean = true
)

private data class StateBundle(
    val rawApps: List<AppInfo>,
    val settings: LauncherSettings,
    val query: String,
    val screen: Screen
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository = AppRepository(application)
    private val settingsRepository = SettingsRepository(application)
    private val defaultLauncherManager = DefaultLauncherManager(application)

    private val searchQuery = MutableStateFlow("")
    private val currentScreen = MutableStateFlow(Screen.HOME)
    private val selectedAppForMenu = MutableStateFlow<AppInfo?>(null)
    private val defaultLauncherStatus = MutableStateFlow(defaultLauncherManager.checkDefaultLauncherStatus())

    val uiState: StateFlow<LauncherUiState> = combine(
        combine(
            appRepository.getInstalledAppsFlow(),
            settingsRepository.settingsFlow,
            searchQuery,
            currentScreen
        ) { rawApps, settings, query, screen ->
            StateBundle(rawApps, settings, query, screen)
        },
        selectedAppForMenu,
        defaultLauncherStatus
    ) { (rawApps, settings, query, screen), menuApp, defaultStatus ->
        val trimmedQuery = query.trim().lowercase()

        // Filter out hidden packages for the main list
        val visibleApps = rawApps.filter { app ->
            !settings.hiddenPackages.contains(app.packageName)
        }

        // Apply search query
        val searchedApps = if (trimmedQuery.isEmpty()) {
            visibleApps
        } else {
            visibleApps.filter { app ->
                app.label.lowercase().contains(trimmedQuery) ||
                        app.packageName.lowercase().contains(trimmedQuery)
            }
        }

        // Apply sorting
        val sortedApps = when (settings.sortOrder) {
            SortOrder.ALPHABETICAL_ASC -> searchedApps.sortedBy { it.label.lowercase() }
            SortOrder.ALPHABETICAL_DESC -> searchedApps.sortedByDescending { it.label.lowercase() }
            SortOrder.RECENTLY_INSTALLED -> searchedApps.sortedByDescending { it.firstInstallTime }
            SortOrder.RECENTLY_USED -> searchedApps.sortedWith(
                compareByDescending<AppInfo> { it.lastUsedTime }
                    .thenByDescending { it.firstInstallTime }
            )
        }

        // Compute available letters for alphabetical index
        val availableLetters = sortedApps.map { app ->
            val first = app.label.firstOrNull()?.uppercaseChar() ?: '#'
            if (first in 'A'..'Z') first.toString() else "#"
        }.toSet()

        // Preview apps (first 4 items for live icon previews)
        val previewApps = rawApps.take(4)

        // Hidden apps list for management
        val hiddenAppsList = rawApps.filter { app ->
            settings.hiddenPackages.contains(app.packageName)
        }

        val hasUsageAccess = appRepository.hasUsageStatsPermission()

        LauncherUiState(
            apps = sortedApps,
            totalInstalledCount = rawApps.size,
            previewApps = previewApps,
            availableLetters = availableLetters,
            hiddenApps = hiddenAppsList,
            settings = settings,
            searchQuery = query,
            currentScreen = screen,
            selectedAppForMenu = menuApp,
            defaultLauncherStatus = defaultStatus,
            hasUsageStatsAccess = hasUsageAccess,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LauncherUiState()
    )

    fun refreshDefaultLauncherStatus() {
        defaultLauncherStatus.value = defaultLauncherManager.checkDefaultLauncherStatus()
    }

    fun requestDefaultLauncher(activity: Activity) {
        defaultLauncherManager.requestDefaultLauncher(activity)
    }

    fun openDefaultLauncherSettings() {
        defaultLauncherManager.openDefaultLauncherSettings(getApplication())
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun navigateTo(screen: Screen) {
        currentScreen.value = screen
    }

    fun selectAppForMenu(app: AppInfo?) {
        selectedAppForMenu.value = app
    }

    fun launchApp(app: AppInfo) {
        appRepository.launchApp(app.packageName)
    }

    fun openAppDetails(app: AppInfo) {
        appRepository.openAppDetails(app.packageName)
    }

    fun uninstallApp(app: AppInfo) {
        appRepository.uninstallApp(app.packageName)
    }

    fun hideApp(app: AppInfo) {
        viewModelScope.launch {
            settingsRepository.hidePackage(app.packageName)
        }
    }

    fun unhideApp(packageName: String) {
        viewModelScope.launch {
            settingsRepository.unhidePackage(packageName)
        }
    }

    fun toggleSearchBar(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setShowSearchBar(enabled) }
    }

    fun toggleDate(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setShowDate(enabled) }
    }

    fun toggleClock(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setShowClock(enabled) }
    }

    fun toggleIcons(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setShowIcons(enabled) }
    }

    fun setIconShape(shape: IconShape) {
        viewModelScope.launch { settingsRepository.setIconShape(shape) }
    }

    fun setIconColorMode(mode: IconColorMode) {
        viewModelScope.launch { settingsRepository.setIconColorMode(mode) }
    }

    fun setCustomIconTint(tint: Long) {
        viewModelScope.launch { settingsRepository.setCustomIconTint(tint) }
    }

    fun setIconSizeDp(sizeDp: Int) {
        viewModelScope.launch { settingsRepository.setIconSizeDp(sizeDp) }
    }

    fun setSortOrder(order: SortOrder) {
        viewModelScope.launch { settingsRepository.setSortOrder(order) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun toggleGrain(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setSubtlePaperGrain(enabled) }
    }

    fun toggleSectionHeaders(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setShowSectionHeaders(enabled) }
    }

    fun toggleAlphabetIndex(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setAlphabetIndexEnabled(enabled) }
    }

    fun openUsageAccessSettings() {
        appRepository.openUsageAccessSettings()
    }

    fun resetDefaults() {
        viewModelScope.launch { settingsRepository.resetDefaults() }
    }
}
