package com.graphite.paper.launcher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.graphite.paper.launcher.model.IconColorMode
import com.graphite.paper.launcher.model.IconShape
import com.graphite.paper.launcher.model.LauncherSettings
import com.graphite.paper.launcher.model.SortOrder
import com.graphite.paper.launcher.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "launcher_preferences")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val SHOW_SEARCH_BAR = booleanPreferencesKey("show_search_bar")
        val SHOW_DATE = booleanPreferencesKey("show_date")
        val SHOW_CLOCK = booleanPreferencesKey("show_clock")
        val SHOW_ICONS = booleanPreferencesKey("show_icons")
        val ICON_SHAPE = stringPreferencesKey("icon_shape")
        val ICON_COLOR_MODE = stringPreferencesKey("icon_color_mode")
        val CUSTOM_ICON_TINT = longPreferencesKey("custom_icon_tint")
        val ICON_SIZE_DP = intPreferencesKey("icon_size_dp")
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SUBTLE_PAPER_GRAIN = booleanPreferencesKey("subtle_paper_grain")
        val SHOW_SECTION_HEADERS = booleanPreferencesKey("show_section_headers")
        val ALPHABET_INDEX_ENABLED = booleanPreferencesKey("alphabet_index_enabled")
        val HIDDEN_PACKAGES = stringSetPreferencesKey("hidden_packages")
    }

    val settingsFlow: Flow<LauncherSettings> = context.dataStore.data.map { preferences ->
        val showSearchBar = preferences[PreferencesKeys.SHOW_SEARCH_BAR] ?: true
        val showDate = preferences[PreferencesKeys.SHOW_DATE] ?: true
        val showClock = preferences[PreferencesKeys.SHOW_CLOCK] ?: true
        val showIcons = preferences[PreferencesKeys.SHOW_ICONS] ?: true

        val iconShapeStr = preferences[PreferencesKeys.ICON_SHAPE] ?: IconShape.ROUNDED_SQUARE.name
        val iconShape = runCatching { IconShape.valueOf(iconShapeStr) }.getOrDefault(IconShape.ROUNDED_SQUARE)

        val iconColorModeStr = preferences[PreferencesKeys.ICON_COLOR_MODE] ?: IconColorMode.THEME_MATCHED.name
        val iconColorMode = runCatching { IconColorMode.valueOf(iconColorModeStr) }.getOrDefault(IconColorMode.THEME_MATCHED)

        val customIconTint = preferences[PreferencesKeys.CUSTOM_ICON_TINT] ?: 0xFF252525L
        val iconSizeDp = preferences[PreferencesKeys.ICON_SIZE_DP] ?: 36

        val sortOrderStr = preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.ALPHABETICAL_ASC.name
        val sortOrder = runCatching { SortOrder.valueOf(sortOrderStr) }.getOrDefault(SortOrder.ALPHABETICAL_ASC)

        val themeModeStr = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.PAPER_LIGHT.name
        val themeMode = runCatching { ThemeMode.valueOf(themeModeStr) }.getOrDefault(ThemeMode.PAPER_LIGHT)

        val subtlePaperGrain = preferences[PreferencesKeys.SUBTLE_PAPER_GRAIN] ?: true
        val showSectionHeaders = preferences[PreferencesKeys.SHOW_SECTION_HEADERS] ?: true
        val alphabetIndexEnabled = preferences[PreferencesKeys.ALPHABET_INDEX_ENABLED] ?: true
        val hiddenPackages = preferences[PreferencesKeys.HIDDEN_PACKAGES] ?: emptySet()

        LauncherSettings(
            showSearchBar = showSearchBar,
            showDate = showDate,
            showClock = showClock,
            showIcons = showIcons,
            iconShape = iconShape,
            iconColorMode = iconColorMode,
            customIconTint = customIconTint,
            iconSizeDp = iconSizeDp,
            sortOrder = sortOrder,
            themeMode = themeMode,
            subtlePaperGrain = subtlePaperGrain,
            showSectionHeaders = showSectionHeaders,
            alphabetIndexEnabled = alphabetIndexEnabled,
            hiddenPackages = hiddenPackages
        )
    }

    suspend fun setShowSearchBar(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_SEARCH_BAR] = enabled }
    }

    suspend fun setShowDate(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_DATE] = enabled }
    }

    suspend fun setShowClock(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_CLOCK] = enabled }
    }

    suspend fun setShowIcons(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_ICONS] = enabled }
    }

    suspend fun setIconShape(shape: IconShape) {
        context.dataStore.edit { it[PreferencesKeys.ICON_SHAPE] = shape.name }
    }

    suspend fun setIconColorMode(mode: IconColorMode) {
        context.dataStore.edit { it[PreferencesKeys.ICON_COLOR_MODE] = mode.name }
    }

    suspend fun setCustomIconTint(tint: Long) {
        context.dataStore.edit { it[PreferencesKeys.CUSTOM_ICON_TINT] = tint }
    }

    suspend fun setIconSizeDp(sizeDp: Int) {
        context.dataStore.edit { it[PreferencesKeys.ICON_SIZE_DP] = sizeDp }
    }

    suspend fun setSortOrder(order: SortOrder) {
        context.dataStore.edit { it[PreferencesKeys.SORT_ORDER] = order.name }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = mode.name }
    }

    suspend fun setSubtlePaperGrain(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SUBTLE_PAPER_GRAIN] = enabled }
    }

    suspend fun setShowSectionHeaders(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_SECTION_HEADERS] = enabled }
    }

    suspend fun setAlphabetIndexEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ALPHABET_INDEX_ENABLED] = enabled }
    }

    suspend fun hidePackage(packageName: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.HIDDEN_PACKAGES] ?: emptySet()
            preferences[PreferencesKeys.HIDDEN_PACKAGES] = current + packageName
        }
    }

    suspend fun unhidePackage(packageName: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.HIDDEN_PACKAGES] ?: emptySet()
            preferences[PreferencesKeys.HIDDEN_PACKAGES] = current - packageName
        }
    }

    suspend fun resetDefaults() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
