package com.graphite.paper.launcher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.graphite.paper.launcher.model.IconColorMode
import com.graphite.paper.launcher.model.IconShape
import com.graphite.paper.launcher.ui.theme.EditorialSerif
import com.graphite.paper.launcher.ui.theme.GraphiteTheme

// Custom Squircle Shape (super-ellipse curve)
val SquircleShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val w = size.width
        val h = size.height

        val n = 3.2f // squircle curvature exponent
        val r = minOf(w, h) / 2f
        val cx = w / 2f
        val cy = h / 2f

        path.moveTo(cx + r, cy)
        // Approximate smooth squircle with cubic beziers
        val c = r * 0.58f

        path.cubicTo(cx + r, cy + c, cx + c, cy + r, cx, cy + r)
        path.cubicTo(cx - c, cy + r, cx - r, cy + c, cx - r, cy)
        path.cubicTo(cx - r, cy - c, cx - c, cy - r, cx, cy - r)
        path.cubicTo(cx + c, cy - r, cx + r, cy - c, cx + r, cy)
        path.close()

        return Outline.Generic(path)
    }
}

@Composable
fun IconRenderer(
    iconBitmap: ImageBitmap?,
    label: String,
    iconSizeDp: Int,
    iconShape: IconShape,
    iconColorMode: IconColorMode,
    customTint: Long,
    modifier: Modifier = Modifier
) {
    val colors = GraphiteTheme.colors
    val sizeDp: Dp = iconSizeDp.dp

    val shape = when (iconShape) {
        IconShape.ORIGINAL -> RectangleShape
        IconShape.CIRCLE -> CircleShape
        IconShape.ROUNDED_SQUARE -> RoundedCornerShape(percent = 22)
        IconShape.SQUARE -> RoundedCornerShape(2.dp)
        IconShape.SQUIRCLE -> SquircleShape
    }

    val shouldClip = iconShape != IconShape.ORIGINAL

    // Color Filter determination
    val colorFilter: ColorFilter? = when (iconColorMode) {
        IconColorMode.ORIGINAL_COLORS -> null
        IconColorMode.THEME_MATCHED -> {
            remember(colors.isDark) {
                val matrix = ColorMatrix()
                matrix.setToSaturation(0f)
                if (colors.isDark) {
                    // Slightly brighten in dark mode for contrast
                    val cm = ColorMatrix(
                        floatArrayOf(
                            1.2f, 0f, 0f, 0f, 15f,
                            0f, 1.2f, 0f, 0f, 15f,
                            0f, 0f, 1.2f, 0f, 15f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                    cm.timesAssign(matrix)
                    ColorFilter.colorMatrix(cm)
                } else {
                    // High-contrast graphite print in light mode
                    ColorFilter.colorMatrix(matrix)
                }
            }
        }
        IconColorMode.CUSTOM_COLORS -> {
            remember(customTint) {
                val targetColor = Color(customTint)
                val tr = targetColor.red
                val tg = targetColor.green
                val tb = targetColor.blue

                // Luminance-to-custom-tint color matrix
                val cm = ColorMatrix(
                    floatArrayOf(
                        0.299f * tr, 0.587f * tr, 0.114f * tr, 0f, 0f,
                        0.299f * tg, 0.587f * tg, 0.114f * tg, 0f, 0f,
                        0.299f * tb, 0.587f * tb, 0.114f * tb, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                ColorFilter.colorMatrix(cm)
            }
        }
    }

    Box(
        modifier = modifier
            .size(sizeDp)
            .then(if (shouldClip) Modifier.clip(shape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (iconBitmap != null) {
            Image(
                bitmap = iconBitmap,
                contentDescription = label,
                modifier = Modifier.size(sizeDp),
                colorFilter = colorFilter
            )
        } else {
            // Editorial typography fallback badge
            Box(
                modifier = Modifier
                    .size(sizeDp)
                    .background(colors.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label.take(1).uppercase(),
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = EditorialSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = (iconSizeDp * 0.45f).sp,
                        color = colors.primaryText
                    )
                )
            }
        }
    }
}

