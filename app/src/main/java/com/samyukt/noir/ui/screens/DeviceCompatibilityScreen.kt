package com.samyukt.noir.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samyukt.noir.ui.components.PaperSurface
import com.samyukt.noir.ui.theme.EditorialSerif
import com.samyukt.noir.ui.theme.GraphiteTheme
import com.samyukt.noir.ui.theme.SwissSans
import com.samyukt.noir.util.CompatibilityStatus
import com.samyukt.noir.util.DeviceCompatibilityInfo

@Composable
fun DeviceCompatibilityScreen(
    compatibilityInfo: DeviceCompatibilityInfo,
    onBack: () -> Unit,
    onOpenNavigationSettings: () -> Unit,
    subtlePaperGrain: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = GraphiteTheme.colors
    val scrollState = rememberScrollState()

    PaperSurface(
        enableGrain = subtlePaperGrain,
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
                        text = "DEVICE COMPATIBILITY",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            letterSpacing = 0.8.sp,
                            color = colors.primaryText
                        )
                    )
                    Text(
                        text = "PLATFORM & OEM DIAGNOSTICS",
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
                // Section: Compatibility Status Banner
                Text(
                    text = "NAVIGATION STATUS",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = SwissSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        letterSpacing = 1.6.sp,
                        color = colors.secondaryText
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                val statusBadgeText = when (compatibilityInfo.status) {
                    CompatibilityStatus.COMPATIBLE -> "COMPATIBLE"
                    CompatibilityStatus.SYSTEM_RESTRICTED -> "SYSTEM RESTRICTED"
                    CompatibilityStatus.UNKNOWN -> "UNKNOWN / UNABLE TO DETERMINE"
                }

                val statusIcon = when (compatibilityInfo.status) {
                    CompatibilityStatus.COMPATIBLE -> Icons.Outlined.CheckCircle
                    CompatibilityStatus.SYSTEM_RESTRICTED -> Icons.Outlined.WarningAmber
                    CompatibilityStatus.UNKNOWN -> Icons.Outlined.Info
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(3.dp))
                        .background(colors.surface)
                        .border(
                            width = 0.75.dp,
                            color = if (compatibilityInfo.status == CompatibilityStatus.SYSTEM_RESTRICTED) {
                                colors.secondaryText.copy(alpha = 0.4f)
                            } else {
                                colors.divider
                            },
                            shape = RoundedCornerShape(3.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = statusIcon,
                                contentDescription = null,
                                tint = colors.primaryText,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = statusBadgeText,
                                style = androidx.compose.ui.text.TextStyle(
                                    fontFamily = SwissSans,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.2.sp,
                                    color = colors.primaryText
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = compatibilityInfo.restrictionSummary,
                            style = androidx.compose.ui.text.TextStyle(
                                fontFamily = SwissSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = colors.primaryText
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Hardware & System Specs Grid
                Text(
                    text = "DEVICE TELEMETRY",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = SwissSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        letterSpacing = 1.6.sp,
                        color = colors.secondaryText
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(3.dp))
                        .background(colors.surface)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    TelemetryRow(label = "Manufacturer", value = compatibilityInfo.manufacturer)
                    HorizontalDivider(thickness = 0.5.dp, color = colors.divider.copy(alpha = 0.5f))
                    TelemetryRow(label = "Brand / Model", value = "${compatibilityInfo.brand} ${compatibilityInfo.model}")
                    HorizontalDivider(thickness = 0.5.dp, color = colors.divider.copy(alpha = 0.5f))
                    TelemetryRow(label = "Android Version", value = compatibilityInfo.androidVersion)
                    HorizontalDivider(thickness = 0.5.dp, color = colors.divider.copy(alpha = 0.5f))
                    TelemetryRow(label = "System Software", value = compatibilityInfo.osFlavor)
                    HorizontalDivider(thickness = 0.5.dp, color = colors.divider.copy(alpha = 0.5f))
                    TelemetryRow(
                        label = "Default Home App",
                        value = if (compatibilityInfo.isDefaultLauncher) "NOIR Launcher" else "Other Launcher"
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = colors.divider.copy(alpha = 0.5f))
                    TelemetryRow(label = "Active Navigation", value = compatibilityInfo.currentNavMode.displayName)
                }

                // Section: Guidance for restricted devices (Xiaomi / POCO)
                if (compatibilityInfo.status == CompatibilityStatus.SYSTEM_RESTRICTED) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "MANUFACTURER POLICY DETAILS",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = SwissSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            letterSpacing = 1.6.sp,
                            color = colors.secondaryText
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(3.dp))
                            .background(colors.surface)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Xiaomi / POCO Gesture Limitations",
                            style = androidx.compose.ui.text.TextStyle(
                                fontFamily = EditorialSerif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                letterSpacing = 0.4.sp,
                                color = colors.primaryText
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        compatibilityInfo.guidanceDetails.forEach { detail ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "•",
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = SwissSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = colors.secondaryText
                                    ),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = detail,
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = SwissSans,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = colors.primaryText
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Button: Open Navigation Settings
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(2.dp))
                                .background(colors.primaryText)
                                .clickable(onClick = onOpenNavigationSettings)
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                                contentDescription = null,
                                tint = colors.background,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "OPEN NAVIGATION SETTINGS",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontFamily = SwissSans,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.0.sp,
                                    color = colors.background
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Manual fallback: Open System Settings → Home screen / Additional settings → System navigation, then select 'Buttons'.",
                            style = androidx.compose.ui.text.TextStyle(
                                fontFamily = SwissSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = colors.mutedText,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun TelemetryRow(
    label: String,
    value: String
) {
    val colors = GraphiteTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = SwissSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = colors.secondaryText
            )
        )
        Text(
            text = value,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = SwissSans,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = colors.primaryText
            )
        )
    }
}
