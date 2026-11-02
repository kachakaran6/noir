package com.samyukt.noir.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samyukt.noir.model.AppInfo
import com.samyukt.noir.model.IconColorMode
import com.samyukt.noir.model.IconShape
import com.samyukt.noir.ui.theme.EditorialSerif
import com.samyukt.noir.ui.theme.GraphiteTheme
import com.samyukt.noir.ui.theme.SwissSans

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppListItem(
    app: AppInfo,
    showIcons: Boolean,
    iconSizeDp: Int,
    iconShape: IconShape,
    iconColorMode: IconColorMode,
    customTint: Long,
    onLaunch: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GraphiteTheme.colors
    val haptic = LocalHapticFeedback.current
    val rowHeight = maxOf(50.dp, (iconSizeDp + 16).dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(rowHeight)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = colors.primaryText.copy(alpha = 0.08f)),
                onClick = onLaunch,
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongPress()
                }
            )
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcons) {
            NoirAppIcon(
                packageName = app.packageName,
                label = app.label,
                iconSizeDp = iconSizeDp,
                iconShape = iconShape,
                iconColorMode = iconColorMode,
                customTint = customTint,
                fallbackBitmap = app.iconBitmap
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        // Application Name
        Text(
            text = app.label,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = SwissSans,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                letterSpacing = 0.2.sp,
                color = colors.primaryText
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SectionHeader(
    letter: String,
    modifier: Modifier = Modifier
) {
    val colors = GraphiteTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = letter.uppercase(),
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = EditorialSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    letterSpacing = 1.2.sp,
                    color = colors.secondaryText
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 0.5.dp,
                color = colors.divider.copy(alpha = 0.7f)
            )
        }
    }
}
