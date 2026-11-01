package com.samyukt.noir.model

enum class IconShape(val displayName: String) {
    ORIGINAL("Original"),
    CIRCLE("Circle"),
    ROUNDED_SQUARE("Rounded Square"),
    SQUARE("Square"),
    SQUIRCLE("Squircle")
}

enum class IconColorMode(val displayName: String) {
    ORIGINAL_COLORS("Original Colors"),
    THEME_MATCHED("Theme Matched"),
    CUSTOM_COLORS("Custom Colors")
}

enum class CuratedTint(val displayName: String, val colorValue: Long) {
    GRAPHITE("Graphite", 0xFF252525L),
    CHARCOAL("Charcoal", 0xFF383838L),
    SLATE("Slate", 0xFF4A4D52L),
    STONE("Stone", 0xFF73716BL),
    WARM_GREY("Warm Grey", 0xFF8C887FL),
    PURE_BLACK("Pure Black", 0xFF151515L),
    SOFT_WHITE("Soft White", 0xFFF0EFEAL)
}

enum class SortOrder(val displayName: String) {
    ALPHABETICAL_ASC("Alphabetical (A → Z)"),
    ALPHABETICAL_DESC("Alphabetical (Z → A)"),
    RECENTLY_INSTALLED("Recently Installed"),
    RECENTLY_USED("Recently Used")
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
    val iconShape: IconShape = IconShape.ROUNDED_SQUARE,
    val iconColorMode: IconColorMode = IconColorMode.THEME_MATCHED,
    val customIconTint: Long = 0xFF252525L,
    val iconSizeDp: Int = 36,
    val sortOrder: SortOrder = SortOrder.ALPHABETICAL_ASC,
    val themeMode: ThemeMode = ThemeMode.PAPER_LIGHT,
    val subtlePaperGrain: Boolean = true,
    val showSectionHeaders: Boolean = true,
    val alphabetIndexEnabled: Boolean = true,
    val hiddenPackages: Set<String> = emptySet()
)
