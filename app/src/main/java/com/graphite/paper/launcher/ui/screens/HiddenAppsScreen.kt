package com.graphite.paper.launcher.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.graphite.paper.launcher.model.AppInfo
import com.graphite.paper.launcher.ui.components.PaperSurface
import com.graphite.paper.launcher.ui.theme.EditorialSerif
import com.graphite.paper.launcher.ui.theme.GraphiteTheme
import com.graphite.paper.launcher.ui.theme.SwissSans
import com.graphite.paper.launcher.viewmodel.LauncherUiState

@Composable
fun HiddenAppsScreen(
    uiState: LauncherUiState,
    onBack: () -> Unit,
    onUnhideApp: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GraphiteTheme.colors

    val grayscaleFilter = remember {
        val matrix = ColorMatrix()
        matrix.setToSaturation(0f)
        ColorFilter.colorMatrix(matrix)
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
                        text = "HIDDEN APPLICATIONS",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            letterSpacing = 0.8.sp,
                            color = colors.primaryText
                        )
                    )
                    Text(
                        text = "${uiState.hiddenApps.size} HIDDEN FROM INDEX",
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

            if (uiState.hiddenApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO APPLICATIONS HIDDEN",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            letterSpacing = 1.0.sp,
                            color = colors.mutedText
                        )
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(
                        items = uiState.hiddenApps,
                        key = { it.packageName }
                    ) { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (app.iconBitmap != null) {
                                Image(
                                    bitmap = app.iconBitmap,
                                    contentDescription = app.label,
                                    modifier = Modifier.size(34.dp),
                                    colorFilter = grayscaleFilter
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.label,
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = SwissSans,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = colors.primaryText
                                    )
                                )
                                Text(
                                    text = app.packageName,
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = SwissSans,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 11.sp,
                                        color = colors.mutedText
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clickable { onUnhideApp(app.packageName) }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Visibility,
                                    contentDescription = "Unhide",
                                    tint = colors.primaryText,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "UNHIDE",
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = SwissSans,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.0.sp,
                                        color = colors.primaryText
                                    )
                                )
                            }
                        }
                        HorizontalDivider(thickness = 0.5.dp, color = colors.divider.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}
