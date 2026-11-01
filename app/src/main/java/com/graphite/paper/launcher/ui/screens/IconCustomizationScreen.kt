package com.graphite.paper.launcher.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.graphite.paper.launcher.model.CuratedTint
import com.graphite.paper.launcher.model.IconColorMode
import com.graphite.paper.launcher.model.IconShape
import com.graphite.paper.launcher.ui.components.IconRenderer
import com.graphite.paper.launcher.ui.components.PaperSurface
import com.graphite.paper.launcher.ui.components.SquircleShape
import com.graphite.paper.launcher.ui.theme.EditorialSerif
import com.graphite.paper.launcher.ui.theme.GraphiteTheme
import com.graphite.paper.launcher.ui.theme.SwissSans
import com.graphite.paper.launcher.viewmodel.LauncherUiState

@Composable
fun IconCustomizationScreen(
    uiState: LauncherUiState,
    onBack: () -> Unit,
    onSetIconShape: (IconShape) -> Unit,
    onSetIconColorMode: (IconColorMode) -> Unit,
    onSetCustomTint: (Long) -> Unit,
    onSetIconSizeDp: (Int) -> Unit,
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
                        text = "APPLICATION ICONS",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = EditorialSerif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            letterSpacing = 0.8.sp,
                            color = colors.primaryText
                        )
                    )
                    Text(
                        text = "SHAPES, PALETTES & SCALE",
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
                // LIVE PREVIEW SECTION
                SectionHeading("LIVE PREVIEW")

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.surface)
                        .padding(vertical = 16.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val sampleApps = uiState.previewApps.take(4)
                        sampleApps.forEach { app ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(68.dp)
                            ) {
                                IconRenderer(
                                    iconBitmap = app.iconBitmap,
                                    label = app.label,
                                    iconSizeDp = uiState.settings.iconSizeDp,
                                    iconShape = uiState.settings.iconShape,
                                    iconColorMode = uiState.settings.iconColorMode,
                                    customTint = uiState.settings.customIconTint
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = app.label,
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = SwissSans,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 11.sp,
                                        color = colors.primaryText
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ICON SHAPE
                SectionHeading("ICON SHAPE")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconShape.values().forEach { shape ->
                        ShapeSelectorCard(
                            shape = shape,
                            selected = uiState.settings.iconShape == shape,
                            onClick = { onSetIconShape(shape) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // COLOR MODES
                SectionHeading("COLOR MODE")

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconColorMode.values().forEach { mode ->
                        val selected = uiState.settings.iconColorMode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSetIconColorMode(mode) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mode.displayName,
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = SwissSans,
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = colors.primaryText
                                    )
                                )
                                Text(
                                    text = when (mode) {
                                        IconColorMode.ORIGINAL_COLORS -> "Natural application colors and gradients"
                                        IconColorMode.THEME_MATCHED -> "Monochrome graphite/paper matching active theme"
                                        IconColorMode.CUSTOM_COLORS -> "Tinted with selected curated graphite shade"
                                    },
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = SwissSans,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 12.sp,
                                        color = colors.mutedText
                                    )
                                )
                            }

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
                }

                if (uiState.settings.iconColorMode == IconColorMode.CUSTOM_COLORS) {
                    Spacer(modifier = Modifier.height(12.dp))
                    SubHeading("CURATED MONOCHROME PALETTE")

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CuratedTint.values().forEach { tint ->
                            val isChosen = uiState.settings.customIconTint == tint.colorValue
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSetCustomTint(tint.colorValue) }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(tint.colorValue))
                                        .then(
                                            if (isChosen) {
                                                Modifier.border(2.dp, colors.primaryText, CircleShape)
                                            } else {
                                                Modifier.border(0.5.dp, colors.divider, CircleShape)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isChosen) {
                                        val checkTint = if (tint == CuratedTint.SOFT_WHITE || tint == CuratedTint.WARM_GREY) Color.Black else Color.White
                                        Icon(
                                            imageVector = Icons.Outlined.Check,
                                            contentDescription = null,
                                            tint = checkTint,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = tint.displayName,
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = SwissSans,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 8.sp,
                                        color = colors.secondaryText
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ICON SIZE
                SectionHeading("ICON SIZE")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Scale (${uiState.settings.iconSizeDp} dp)",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = SwissSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = colors.primaryText
                        )
                    )

                    // Quick presets
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Small" to 28, "Medium" to 36, "Large" to 44).forEach { (label, dpVal) ->
                            val isPresetSelected = uiState.settings.iconSizeDp == dpVal
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (isPresetSelected) colors.primaryText else colors.surface)
                                    .clickable { onSetIconSizeDp(dpVal) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = SwissSans,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp,
                                        color = if (isPresetSelected) colors.background else colors.primaryText
                                    )
                                )
                            }
                        }
                    }
                }

                Slider(
                    value = uiState.settings.iconSizeDp.toFloat(),
                    onValueChange = { onSetIconSizeDp(it.toInt()) },
                    valueRange = 24f..56f,
                    steps = 15,
                    colors = SliderDefaults.colors(
                        thumbColor = colors.primaryText,
                        activeTrackColor = colors.primaryText,
                        inactiveTrackColor = colors.divider
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ShapeSelectorCard(
    shape: IconShape,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GraphiteTheme.colors

    val shapeOutline = when (shape) {
        IconShape.ORIGINAL -> RoundedCornerShape(0.dp)
        IconShape.CIRCLE -> CircleShape
        IconShape.ROUNDED_SQUARE -> RoundedCornerShape(percent = 22)
        IconShape.SQUARE -> RoundedCornerShape(2.dp)
        IconShape.SQUIRCLE -> SquircleShape
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(if (selected) colors.surface else Color.Transparent)
            .border(
                width = if (selected) 1.dp else 0.5.dp,
                color = if (selected) colors.primaryText else colors.divider,
                shape = RoundedCornerShape(3.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Geometric shape glyph preview
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(shapeOutline)
                .background(if (selected) colors.primaryText else colors.secondaryText)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = shape.displayName,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = SwissSans,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 9.sp,
                letterSpacing = 0.2.sp,
                color = colors.primaryText
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SectionHeading(title: String) {
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
private fun SubHeading(text: String) {
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

