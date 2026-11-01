package com.graphite.paper.launcher.model

enum class IconSize(val dpVal: Int, val displayName: String) {
    SMALL(26, "Small"),
    MEDIUM(34, "Medium"),
    LARGE(42, "Large")
}

enum class IconTint(val displayName: String) {
    GRAPHITE_MONOCHROME("Graphite Monochrome"),
    ORIGINAL_COLOR("Original Color")
}

enum class SortOrder(val displayName: String) {
    ALPHABETICAL_ASC("Alphabetical (A → Z)"),
    ALPHABETICAL_DESC("Alphabetical (Z → A)"),
    RECENTLY_INSTALLED("Recently Installed")
}

enum class ThemeMode(val displayName: String) {
    PAPER_LIGHT("Paper Light"),
    GRAPHITE_DARK("Graphite Dark"),
    SYSTEM("Follow System")
}

data class LauncherSettings(
    val showSearchBar: Boolean = true,
    val showDate: Boolean = true,
    val showClock: Boolean = true,
    val showIcons: Boolean = true,
    val iconSize: IconSize = IconSize.MEDIUM,
    val iconTint: IconTint = IconTint.GRAPHITE_MONOCHROME,
    val sortOrder: SortOrder = SortOrder.ALPHABETICAL_ASC,
    val themeMode: ThemeMode = ThemeMode.PAPER_LIGHT,
    val subtlePaperGrain: Boolean = true,
    val showSectionHeaders: Boolean = true,
    val hiddenPackages: Set<String> = emptySet()
)
