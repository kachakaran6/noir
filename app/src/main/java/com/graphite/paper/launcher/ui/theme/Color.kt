package com.graphite.paper.launcher.ui.theme

import androidx.compose.ui.graphics.Color

// Light Paper Palette
val PaperBackground = Color(0xFFF2F0E9)
val PrimaryGraphite = Color(0xFF252525)
val SecondaryGraphite = Color(0xFF65645F)
val MutedText = Color(0xFF92918B)
val SubtleDivider = Color(0xFFDAD8D0)
val PureWhite = Color(0xFFFFFFFF)
val DeepBlack = Color(0xFF151515)
val PaperCardSurface = Color(0xFFEBE8E0)

// Dark Graphite Palette
val DarkBackground = Color(0xFF191919)
val DarkPrimaryText = Color(0xFFF0EFEA)
val DarkSecondaryText = Color(0xFFA4A39E)
val DarkDivider = Color(0xFF343434)
val DarkSubtleSurface = Color(0xFF242424)
val DarkMutedText = Color(0xFF75746E)

data class GraphiteColors(
    val background: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val mutedText: Color,
    val divider: Color,
    val surface: Color,
    val isDark: Boolean
)

val LightGraphiteColors = GraphiteColors(
    background = PaperBackground,
    primaryText = PrimaryGraphite,
    secondaryText = SecondaryGraphite,
    mutedText = MutedText,
    divider = SubtleDivider,
    surface = PaperCardSurface,
    isDark = false
)

val DarkGraphiteColors = GraphiteColors(
    background = DarkBackground,
    primaryText = DarkPrimaryText,
    secondaryText = DarkSecondaryText,
    mutedText = DarkMutedText,
    divider = DarkDivider,
    surface = DarkSubtleSurface,
    isDark = true
)

