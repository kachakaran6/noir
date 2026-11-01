package com.graphite.paper.launcher.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.graphite.paper.launcher.model.AppInfo
import com.graphite.paper.launcher.model.SortOrder
import com.graphite.paper.launcher.ui.components.AppContextMenu
import com.graphite.paper.launcher.ui.components.AppListItem
import com.graphite.paper.launcher.ui.components.EditorialHeader
import com.graphite.paper.launcher.ui.components.MinimalSearchBar
import com.graphite.paper.launcher.ui.components.PaperSurface
import com.graphite.paper.launcher.ui.components.SectionHeader
import com.graphite.paper.launcher.ui.theme.EditorialSerif
import com.graphite.paper.launcher.ui.theme.GraphiteTheme
import com.graphite.paper.launcher.ui.theme.SwissSans
import com.graphite.paper.launcher.viewmodel.LauncherUiState

@Composable
fun HomeScreen(
    uiState: LauncherUiState,
    onSearchQueryChange: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onLaunchApp: (AppInfo) -> Unit,
    onSelectAppForMenu: (AppInfo?) -> Unit,
    onAppInfo: (AppInfo) -> Unit,
    onHideApp: (AppInfo) -> Unit,
    onUninstallApp: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GraphiteTheme.colors
    val listState = rememberLazyListState()

    // Determine grouping if alphabetical
    val showHeaders = uiState.settings.showSectionHeaders &&
            (uiState.settings.sortOrder == SortOrder.ALPHABETICAL_ASC || uiState.settings.sortOrder == SortOrder.ALPHABETICAL_DESC) &&
            uiState.searchQuery.isEmpty()

    PaperSurface(
        enableGrain = uiState.settings.subtlePaperGrain,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Editorial Header
            EditorialHeader(
                showDate = uiState.settings.showDate,
                showClock = uiState.settings.showClock,
                totalAppsCount = uiState.totalInstalledCount,
                onOpenSettings = onOpenSettings
            )

            // Minimal Search Bar (smoothly collapsed when disabled)
            AnimatedVisibility(
                visible = uiState.settings.showSearchBar,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                MinimalSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChange
                )
            }

            // Application List or Empty State
            if (uiState.apps.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 64.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NO MATCHING APPLICATIONS",
                            style = androidx.compose.ui.text.TextStyle(
                                fontFamily = EditorialSerif,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                letterSpacing = 1.0.sp,
                                color = colors.secondaryText
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No items match query \"${uiState.searchQuery}\"",
                            style = androidx.compose.ui.text.TextStyle(
                                fontFamily = SwissSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = colors.mutedText
                            )
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .navigationBarsPadding()
                ) {
                    if (showHeaders) {
                        // Group by first character
                        val grouped = uiState.apps.groupBy { app ->
                            val firstChar = app.label.firstOrNull()?.uppercaseChar() ?: '#'
                            if (firstChar.isLetter()) firstChar else '#'
                        }

                        grouped.forEach { (initial, appsInGroup) ->
                            item(key = "header_$initial") {
                                SectionHeader(letter = initial.toString())
                            }

                            items(
                                items = appsInGroup,
                                key = { it.packageName }
                            ) { app ->
                                AppListItem(
                                    app = app,
                                    showIcons = uiState.settings.showIcons,
                                    iconSize = uiState.settings.iconSize,
                                    iconTint = uiState.settings.iconTint,
                                    onLaunch = { onLaunchApp(app) },
                                    onLongPress = { onSelectAppForMenu(app) }
                                )
                            }
                        }
                    } else {
                        items(
                            items = uiState.apps,
                            key = { it.packageName }
                        ) { app ->
                            AppListItem(
                                app = app,
                                showIcons = uiState.settings.showIcons,
                                iconSize = uiState.settings.iconSize,
                                iconTint = uiState.settings.iconTint,
                                onLaunch = { onLaunchApp(app) },
                                onLongPress = { onSelectAppForMenu(app) }
                            )
                        }
                    }
                }
            }
        }

        // Long Press Context Menu
        AppContextMenu(
            app = uiState.selectedAppForMenu,
            onDismiss = { onSelectAppForMenu(null) },
            onLaunch = { onLaunchApp(it) },
            onAppInfo = { onAppInfo(it) },
            onHideApp = { onHideApp(it) },
            onUninstall = { onUninstallApp(it) }
        )
    }
}
