package com.samyukt.noir.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samyukt.noir.model.AppInfo
import com.samyukt.noir.model.IconColorMode
import com.samyukt.noir.model.IconShape
import com.samyukt.noir.ui.theme.EditorialSerif
import com.samyukt.noir.ui.theme.GraphiteTheme
import com.samyukt.noir.ui.theme.SwissSans

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContextMenu(
    app: AppInfo?,
    iconShape: IconShape,
    iconColorMode: IconColorMode,
    customTint: Long,
    onDismiss: () -> Unit,
    onLaunch: (AppInfo) -> Unit,
    onAppInfo: (AppInfo) -> Unit,
    onHideApp: (AppInfo) -> Unit,
    onUninstall: (AppInfo) -> Unit
) {
    if (app == null) return

    val colors = GraphiteTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.background,
        contentColor = colors.primaryText,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding()
        ) {
            // App Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconRenderer(
                    iconBitmap = app.iconBitmap,
                    label = app.label,
                    iconSizeDp = 44,
                    iconShape = iconShape,
                    iconColorMode = iconColorMode,
                    customTint = customTint
                )
                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.label,
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            letterSpacing = 0.3.sp,
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
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 0.5.dp, color = colors.divider)
            Spacer(modifier = Modifier.height(8.dp))

            // Actions
            ContextMenuActionRow(
                label = "Launch Application",
                icon = Icons.AutoMirrored.Outlined.OpenInNew,
                onClick = {
                    onDismiss()
                    onLaunch(app)
                }
            )

            ContextMenuActionRow(
                label = "Application Details & Permissions",
                icon = Icons.Outlined.Info,
                onClick = {
                    onDismiss()
                    onAppInfo(app)
                }
            )

            ContextMenuActionRow(
                label = "Hide from Launcher",
                icon = Icons.Outlined.VisibilityOff,
                onClick = {
                    onDismiss()
                    onHideApp(app)
                }
            )

            ContextMenuActionRow(
                label = "Uninstall Application",
                icon = Icons.Outlined.DeleteOutline,
                isDestructive = true,
                onClick = {
                    onDismiss()
                    onUninstall(app)
                }
            )
        }
    }
}

@Composable
private fun ContextMenuActionRow(
    label: String,
    icon: ImageVector,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val colors = GraphiteTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (isDestructive) colors.secondaryText else colors.primaryText
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = label,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = SwissSans,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                letterSpacing = 0.2.sp,
                color = if (isDestructive) colors.secondaryText else colors.primaryText
            )
        )
    }
}
