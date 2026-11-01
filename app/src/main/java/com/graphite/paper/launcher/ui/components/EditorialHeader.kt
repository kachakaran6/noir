package com.graphite.paper.launcher.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.graphite.paper.launcher.ui.theme.EditorialSerif
import com.graphite.paper.launcher.ui.theme.GraphiteTheme
import com.graphite.paper.launcher.ui.theme.SwissSans
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditorialHeader(
    showDate: Boolean,
    showClock: Boolean,
    totalAppsCount: Int,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GraphiteTheme.colors

    var currentTime by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000)
        }
    }

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Editorial Masthead Top Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INDEX — $totalAppsCount ITEMS",
                style = GraphiteTheme.colors.let {
                    androidx.compose.ui.text.TextStyle(
                        fontFamily = SwissSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        letterSpacing = 1.6.sp,
                        color = colors.mutedText
                    )
                }
            )

            // Minimal settings trigger
            Row(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onOpenSettings
                    )
                    .padding(vertical = 4.dp, horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "PREFERENCES",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = SwissSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        letterSpacing = 1.4.sp,
                        color = colors.secondaryText
                    )
                )
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Preferences",
                    modifier = Modifier.size(12.dp),
                    tint = colors.secondaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Time and Date
        if (showClock || showDate) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                if (showDate) {
                    Text(
                        text = dateFormat.format(currentTime).uppercase(Locale.getDefault()),
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            letterSpacing = 0.8.sp,
                            color = colors.primaryText
                        )
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                if (showClock) {
                    Text(
                        text = timeFormat.format(currentTime),
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = SwissSans,
                            fontWeight = FontWeight.Light,
                            fontSize = 20.sp,
                            letterSpacing = 1.0.sp,
                            color = colors.secondaryText
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Fine graphite hairline divider
        HorizontalDivider(
            thickness = 0.75.dp,
            color = colors.divider
        )
    }
}
