package com.samyukt.noir.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samyukt.noir.model.AppInfo
import com.samyukt.noir.model.SortOrder
import com.samyukt.noir.ui.components.AlphabetIndex
import com.samyukt.noir.ui.components.AppContextMenu
import com.samyukt.noir.ui.components.AppListItem
import com.samyukt.noir.ui.components.EditorialHeader
import com.samyukt.noir.ui.components.MinimalSearchBar
import com.samyukt.noir.ui.components.PaperSurface
import com.samyukt.noir.ui.components.SectionHeader
import com.samyukt.noir.ui.theme.EditorialSerif
import com.samyukt.noir.ui.theme.GraphiteTheme
import com.samyukt.noir.ui.theme.SwissSans
import com.samyukt.noir.viewmodel.LauncherUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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
    onSetDefaultLauncher: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GraphiteTheme.colors
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var scrollJob by remember { mutableStateOf<Job?>(null) }
    var dismissDefaultBanner by remember { mutableStateOf(false) }

    // Intercept Back on HomeScreen only when Search or Context Menu is active to close them smoothly
    BackHandler(enabled = uiState.searchQuery.isNotEmpty() || uiState.selectedAppForMenu != null) {
        if (uiState.selectedAppForMenu != null) {
            onSelectAppForMenu(null)
        } else if (uiState.searchQuery.isNotEmpty()) {
            onSearchQueryChange("")
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }

    val isAlphabetical = uiState.settings.sortOrder == SortOrder.ALPHABETICAL_ASC ||
            uiState.settings.sortOrder == SortOrder.ALPHABETICAL_DESC

    val showHeaders = uiState.settings.showSectionHeaders && isAlphabetical && uiState.searchQuery.isEmpty()
    val showAlphabetIndex = uiState.settings.alphabetIndexEnabled && isAlphabetical && uiState.apps.isNotEmpty()

    // Map letters to item index in LazyColumn
    val letterToIndexMap = remember(uiState.apps, showHeaders) {
        val map = mutableMapOf<String, Int>()
        var currentIndex = 0
        if (showHeaders) {
            val grouped = uiState.apps.groupBy { app ->
                val firstChar = app.label.firstOrNull()?.uppercaseChar() ?: '#'
                if (firstChar in 'A'..'Z') firstChar.toString() else "#"
            }
            grouped.forEach { (letter, appsInGroup) ->
                map[letter] = currentIndex
                currentIndex += 1 // Section header item
                currentIndex += appsInGroup.size
            }
        } else {
            uiState.apps.forEachIndexed { index, app ->
                val first = app.label.firstOrNull()?.uppercaseChar() ?: '#'
                val letter = if (first in 'A'..'Z') first.toString() else "#"
                if (!map.containsKey(letter)) {
                    map[letter] = index
                }
            }
        }
        map
    }

    // Active letter tracking based on scroll position
    val activeLetter by remember(letterToIndexMap) {
        derivedStateOf {
            val firstVisible = listState.firstVisibleItemIndex
            letterToIndexMap.entries
                .filter { it.value <= firstVisible }
                .maxByOrNull { it.value }
                ?.key ?: letterToIndexMap.keys.firstOrNull()
        }
    }

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

            // Default Launcher Notice Banner (Editorial & Non-intrusive)
            if (!uiState.defaultLauncherStatus.isDefault && !dismissDefaultBanner && uiState.searchQuery.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(colors.surface)
                        .clickable(onClick = onSetDefaultLauncher)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SET AS DEFAULT LAUNCHER",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = SwissSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            letterSpacing = 1.2.sp,
                            color = colors.primaryText
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "TAP TO SET ›",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = SwissSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp,
                            color = colors.secondaryText
                        )
                    )
                }
            }

            // Application List and Vertical Alphabet Index
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .navigationBarsPadding()
                ) {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = 48.dp,
                            end = if (showAlphabetIndex) 36.dp else 0.dp
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (showHeaders) {
                            val grouped = uiState.apps.groupBy { app ->
                                val firstChar = app.label.firstOrNull()?.uppercaseChar() ?: '#'
                                if (firstChar in 'A'..'Z') firstChar.toString() else "#"
                            }

                            grouped.forEach { (initial, appsInGroup) ->
                                item(key = "header_$initial") {
                                    SectionHeader(letter = initial)
                                }

                                items(
                                    items = appsInGroup,
                                    key = { it.packageName }
                                ) { app ->
                                    AppListItem(
                                        app = app,
                                        showIcons = uiState.settings.showIcons,
                                        iconSizeDp = uiState.settings.iconSizeDp,
                                        iconShape = uiState.settings.iconShape,
                                        iconColorMode = uiState.settings.iconColorMode,
                                        customTint = uiState.settings.customIconTint,
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
                                    iconSizeDp = uiState.settings.iconSizeDp,
                                    iconShape = uiState.settings.iconShape,
                                    iconColorMode = uiState.settings.iconColorMode,
                                    customTint = uiState.settings.customIconTint,
                                    onLaunch = { onLaunchApp(app) },
                                    onLongPress = { onSelectAppForMenu(app) }
                                )
                            }
                        }
                    }

                    // Fixed Alphabetical Index on the Right Edge
                    if (showAlphabetIndex) {
                        AlphabetIndex(
                            activeLetter = activeLetter,
                            availableLetters = uiState.availableLetters,
                            onLetterSelected = { selectedLetter, isDragging ->
                                val target = letterToIndexMap[selectedLetter]
                                    ?: run {
                                        // Fallback to closest available letter
                                        letterToIndexMap.entries
                                            .filter { it.key >= selectedLetter }
                                            .minByOrNull { it.key }?.value
                                    }
                                if (target != null) {
                                    scrollJob?.cancel()
                                    scrollJob = coroutineScope.launch {
                                        if (isDragging) {
                                            listState.scrollToItem(target)
                                        } else {
                                            listState.animateScrollToItem(target)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight()
                                .padding(end = 4.dp, top = 8.dp, bottom = 48.dp)
                        )
                    }
                }
            }
        }

        // Long Press Context Menu
        AppContextMenu(
            app = uiState.selectedAppForMenu,
            iconShape = uiState.settings.iconShape,
            iconColorMode = uiState.settings.iconColorMode,
            customTint = uiState.settings.customIconTint,
            onDismiss = { onSelectAppForMenu(null) },
            onLaunch = { onLaunchApp(it) },
            onAppInfo = { onAppInfo(it) },
            onHideApp = { onHideApp(it) },
            onUninstall = { onUninstallApp(it) }
        )
    }
}
