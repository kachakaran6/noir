package com.graphite.paper.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.graphite.paper.launcher.model.IconSize
import com.graphite.paper.launcher.model.IconTint
import com.graphite.paper.launcher.model.LauncherSettings
import com.graphite.paper.launcher.model.SortOrder
import com.graphite.paper.launcher.model.ThemeMode
import com.graphite.paper.launcher.ui.components.PaperSurface
import com.graphite.paper.launcher.ui.theme.EditorialSerif
import com.graphite.paper.launcher.ui.theme.GraphiteTheme
import com.graphite.paper.launcher.ui.theme.SwissSans
import com.graphite.paper.launcher.viewmodel.LauncherUiState

@Composable
fun SettingsScreen(
    uiState: LauncherUiState,
    onBack: () -> Unit,
    onToggleSearchBar: (Boolean) -> Unit,
    onToggleDate: (Boolean) -> Unit,
    onToggleClock: (Boolean) -> Unit,
    onToggleIcons: (Boolean) -> Unit,
    onSetIconSize: (IconSize) -> Unit,
    onSetIconTint: (IconTint) -> Unit,
    onSetSortOrder: (SortOrder) -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onToggleGrain: (Boolean) -> Unit,
    onToggleSectionHeaders: (Boolean) -> Unit,
    onOpenHiddenApps: () -> Unit,
    onResetDefaults: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GraphiteTheme.colors
    val scrollState = rememberScrollState()

    PaperSurface(
        enableGrain = uiState.settings.subtlePaperGrain,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.primaryText,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = onBack)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "PREFERENCES",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            letterSpacing = 0.8.sp,
                            color = colors.primaryText
                        )
                    )
                    Text(
                        text = "GRAPHITE PAPER LAUNCHER",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = SwissSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            letterSpacing = 1.4.sp,
                            color = colors.mutedText
                        )
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = colors.divider)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Section: Home Screen Header
                SettingsSectionTitle("HOME SCREEN HEADER")

                SettingsSwitchRow(
                    title = "Show Search Bar",
                    subtitle = "Minimal fast application filter",
                    checked = uiState.settings.showSearchBar,
                    onCheckedChange = onToggleSearchBar
                )

                SettingsSwitchRow(
                    title = "Show Date",
                    subtitle = "Editorial calendar indicator",
                    checked = uiState.settings.showDate,
                    onCheckedChange = onToggleDate
                )

                SettingsSwitchRow(
                    title = "Show Clock",
                    subtitle = "Minimal 24h digital time indicator",
                    checked = uiState.settings.showClock,
                    onCheckedChange = onToggleClock
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Application List Appearance
                SettingsSectionTitle("APPLICATION LIST")

                SettingsSwitchRow(
                    title = "Display Application Icons",
                    subtitle = "Toggle for pure text-only editorial minimalism",
                    checked = uiState.settings.showIcons,
                    onCheckedChange = onToggleIcons
                )

                if (uiState.settings.showIcons) {
                    SettingsSubLabel("ICON COLOR PALETTE")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconTint.values().forEach { tint ->
                            SettingsChip(
                                label = tint.displayName,
                                selected = uiState.settings.iconTint == tint,
                                onClick = { onSetIconTint(tint) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingsSubLabel("ICON SCALE")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconSize.values().forEach { size ->
                            SettingsChip(
                                label = size.displayName,
                                selected = uiState.settings.iconSize == size,
                                onClick = { onSetIconSize(size) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                SettingsSwitchRow(
                    title = "Alphabetical Section Dividers",
                    subtitle = "Hairline dividers for alphabetical groupings",
                    checked = uiState.settings.showSectionHeaders,
                    onCheckedChange = onToggleSectionHeaders
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingsSubLabel("LIST SORTING")
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SortOrder.values().forEach { order ->
                        SettingsOptionRow(
                            label = order.displayName,
                            selected = uiState.settings.sortOrder == order,
                            onClick = { onSetSortOrder(order) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Appearance & Texture
                SettingsSectionTitle("APPEARANCE & TEXTURE")

                SettingsSubLabel("THEME")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeMode.values().forEach { mode ->
                        SettingsChip(
                            label = mode.displayName,
                            selected = uiState.settings.themeMode == mode,
                            onClick = { onSetThemeMode(mode) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                SettingsSwitchRow(
                    title = "Tactile Paper Grain Texture",
                    subtitle = "Procedural micro-fiber grain rendered at 120fps",
                    checked = uiState.settings.subtlePaperGrain,
                    onCheckedChange = onToggleGrain
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Hidden Applications
                SettingsSectionTitle("PRIVACY")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenHiddenApps)
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = colors.primaryText
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hidden Applications",
                            style = androidx.compose.ui.text.TextStyle(
                                fontFamily = SwissSans,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = colors.primaryText
                            )
                        )
                        Text(
                            text = "${uiState.settings.hiddenPackages.size} applications currently hidden",
                            style = androidx.compose.ui.text.TextStyle(
                                fontFamily = SwissSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = colors.mutedText
                            )
                        )
                    }
                    Text(
                        text = "MANAGE ›",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = SwissSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            letterSpacing = 1.0.sp,
                            color = colors.secondaryText
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Defaults & System
                SettingsSectionTitle("SYSTEM")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onResetDefaults)
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.RestartAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = colors.secondaryText
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Reset Launcher Defaults",
                            style = androidx.compose.ui.text.TextStyle(
                                fontFamily = SwissSans,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = colors.primaryText
                            )
                        )
                        Text(
                            text = "Restore original Graphite Paper configuration",
                            style = androidx.compose.ui.text.TextStyle(
                                fontFamily = SwissSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = colors.mutedText
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    val colors = GraphiteTheme.colors
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = title,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = SwissSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                color = colors.secondaryText
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(thickness = 0.5.dp, color = colors.divider)
    }
}

@Composable
private fun SettingsSubLabel(text: String) {
    val colors = GraphiteTheme.colors
    Text(
        text = text,
        style = androidx.compose.ui.text.TextStyle(
            fontFamily = SwissSans,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            letterSpacing = 1.2.sp,
            color = colors.mutedText
        ),
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = GraphiteTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = SwissSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = colors.primaryText
                )
            )
            Text(
                text = subtitle,
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = SwissSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = colors.mutedText
                )
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.background,
                checkedTrackColor = colors.primaryText,
                uncheckedThumbColor = colors.secondaryText,
                uncheckedTrackColor = colors.divider
            )
        )
    }
}

@Composable
private fun SettingsChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GraphiteTheme.colors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(if (selected) colors.primaryText else colors.surface)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = SwissSans,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp,
                color = if (selected) colors.background else colors.primaryText
            ),
            maxLines = 1
        )
    }
}

@Composable
private fun SettingsOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = GraphiteTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = SwissSans,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 13.sp,
                color = colors.primaryText
            ),
            modifier = Modifier.weight(1f)
        )

        if (selected) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = colors.primaryText,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
