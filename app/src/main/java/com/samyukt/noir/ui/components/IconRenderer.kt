package com.samyukt.noir.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.samyukt.noir.model.IconColorMode
import com.samyukt.noir.model.IconShape

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

        val r = minOf(w, h) / 2f
        val cx = w / 2f
        val cy = h / 2f

        path.moveTo(cx + r, cy)
        val c = r * 0.58f

        path.cubicTo(cx + r, cy + c, cx + c, cy + r, cx, cy + r)
        path.cubicTo(cx - c, cy + r, cx - r, cy + c, cx - r, cy)
        path.cubicTo(cx - r, cy - c, cx - c, cy - r, cx, cy - r)
        path.cubicTo(cx + c, cy - r, cx + r, cy - c, cx + r, cy)
        path.close()

        return Outline.Generic(path)
    }
}

/**
 * Legacy compatibility delegate routing to the centralized [NoirAppIcon] engine.
 */
@Composable
fun IconRenderer(
    iconBitmap: ImageBitmap?,
    label: String,
    iconSizeDp: Int,
    iconShape: IconShape,
    iconColorMode: IconColorMode,
    customTint: Long,
    modifier: Modifier = Modifier,
    packageName: String = ""
) {
    NoirAppIcon(
        packageName = packageName,
        label = label,
        iconSizeDp = iconSizeDp,
        iconShape = iconShape,
        iconColorMode = iconColorMode,
        customTint = customTint,
        fallbackBitmap = iconBitmap,
        modifier = modifier
    )
}
