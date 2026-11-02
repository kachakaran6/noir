package com.samyukt.noir.ui.components

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samyukt.noir.model.IconColorMode
import com.samyukt.noir.model.IconShape
import com.samyukt.noir.ui.theme.EditorialSerif
import com.samyukt.noir.ui.theme.GraphiteTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.min

data class IconRenderKey(
    val packageName: String,
    val iconShape: IconShape,
    val iconColorMode: IconColorMode,
    val customTint: Long,
    val sizePx: Int,
    val isDark: Boolean
)

object NoirIconEngine {

    private val rawDrawableCache = ConcurrentHashMap<String, Drawable>()
    private val bitmapLruCache = object : LruCache<IconRenderKey, ImageBitmap>(350) {}

    fun clearCache() {
        bitmapLruCache.evictAll()
    }

    fun invalidatePackage(packageName: String) {
        rawDrawableCache.remove(packageName)
        // Evict matching keys
        val snapshot = bitmapLruCache.snapshot()
        snapshot.keys.forEach { key ->
            if (key.packageName == packageName) {
                bitmapLruCache.remove(key)
            }
        }
    }

    fun getCachedBitmap(key: IconRenderKey): ImageBitmap? {
        return bitmapLruCache.get(key)
    }

    suspend fun loadAndRenderIcon(
        context: Context,
        packageName: String,
        iconShape: IconShape,
        iconColorMode: IconColorMode,
        customTint: Long,
        sizePx: Int,
        isDark: Boolean
    ): ImageBitmap? = withContext(Dispatchers.Default) {
        val key = IconRenderKey(packageName, iconShape, iconColorMode, customTint, sizePx, isDark)
        bitmapLruCache.get(key)?.let { return@withContext it }

        val rawDrawable = rawDrawableCache.computeIfAbsent(packageName) {
            runCatching {
                val pm = context.packageManager
                val intent = pm.getLaunchIntentForPackage(packageName)
                if (intent != null) {
                    val resolveInfo = pm.resolveActivity(intent, 0)
                    resolveInfo?.loadIcon(pm) ?: pm.getApplicationIcon(packageName)
                } else {
                    pm.getApplicationIcon(packageName)
                }
            }.getOrNull() ?: BitmapDrawable(context.resources, Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888))
        }

        val renderedBitmap = renderShapedIcon(
            drawable = rawDrawable,
            iconShape = iconShape,
            iconColorMode = iconColorMode,
            customTint = customTint,
            sizePx = sizePx,
            isDark = isDark
        )?.asImageBitmap()

        if (renderedBitmap != null) {
            bitmapLruCache.put(key, renderedBitmap)
        }

        renderedBitmap
    }

    private fun renderShapedIcon(
        drawable: Drawable,
        iconShape: IconShape,
        iconColorMode: IconColorMode,
        customTint: Long,
        sizePx: Int,
        isDark: Boolean
    ): Bitmap? {
        if (sizePx <= 0) return null

        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        if (iconShape == IconShape.ORIGINAL) {
            // Render original artwork as-is
            drawable.setBounds(0, 0, sizePx, sizePx)
            drawable.draw(canvas)
            applyColorMode(bitmap, iconColorMode, customTint, isDark)
            return bitmap
        }

        // Custom shape clipping path
        val clipPath = createShapePath(iconShape, sizePx.toFloat())
        canvas.save()
        canvas.clipPath(clipPath)

        val isAdaptive = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable is AdaptiveIconDrawable

        if (isAdaptive) {
            val adaptive = drawable as AdaptiveIconDrawable
            val bleed = (sizePx * 0.16f).toInt()

            if (iconColorMode == IconColorMode.THEME_MATCHED &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                adaptive.monochrome != null
            ) {
                // Official Material You / Noir themed icon monochrome layer
                val surfaceBg = if (isDark) 0xFF252525.toInt() else 0xFFE2E0D8.toInt()
                paint.color = surfaceBg
                canvas.drawRect(0f, 0f, sizePx.toFloat(), sizePx.toFloat(), paint)

                val mono = adaptive.monochrome!!.mutate()
                val tintColor = if (isDark) 0xFFF2F0E9.toInt() else 0xFF151515.toInt()
                mono.setTint(tintColor)
                mono.setBounds(-bleed, -bleed, sizePx + bleed, sizePx + bleed)
                mono.draw(canvas)
            } else if (iconColorMode == IconColorMode.CUSTOM_COLORS &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                adaptive.monochrome != null
            ) {
                val surfaceBg = if (isDark) 0xFF222220.toInt() else 0xFFEAE8E0.toInt()
                paint.color = surfaceBg
                canvas.drawRect(0f, 0f, sizePx.toFloat(), sizePx.toFloat(), paint)

                val mono = adaptive.monochrome!!.mutate()
                mono.setTint(customTint.toInt())
                mono.setBounds(-bleed, -bleed, sizePx + bleed, sizePx + bleed)
                mono.draw(canvas)
            } else {
                // Draw full-bleed background layer to fill corners
                val bg = adaptive.background?.mutate()
                bg?.setBounds(0, 0, sizePx, sizePx)
                bg?.draw(canvas)

                // Draw foreground layer centered and proportioned
                val fg = adaptive.foreground?.mutate()
                fg?.setBounds(-bleed, -bleed, sizePx + bleed, sizePx + bleed)
                fg?.draw(canvas)

                applyColorMode(bitmap, iconColorMode, customTint, isDark)
            }
        } else {
            // Legacy / non-adaptive icon: wrap in shaped surface plate so it conforms to the selected shape
            val surfaceBg = if (isDark) 0xFF2A2A28.toInt() else 0xFFE8E6DF.toInt()
            paint.color = surfaceBg
            canvas.drawRect(0f, 0f, sizePx.toFloat(), sizePx.toFloat(), paint)

            val inset = (sizePx * 0.14f).toInt()
            drawable.setBounds(inset, inset, sizePx - inset, sizePx - inset)
            drawable.draw(canvas)

            applyColorMode(bitmap, iconColorMode, customTint, isDark)
        }

        canvas.restore()
        return bitmap
    }

    private fun createShapePath(iconShape: IconShape, size: Float): Path {
        val path = Path()
        when (iconShape) {
            IconShape.CIRCLE -> {
                path.addOval(RectF(0f, 0f, size, size), Path.Direction.CW)
            }
            IconShape.ROUNDED_SQUARE -> {
                val r = size * 0.22f
                path.addRoundRect(RectF(0f, 0f, size, size), r, r, Path.Direction.CW)
            }
            IconShape.SQUARE -> {
                val r = size * 0.06f // subtle 3-4dp rounded square corner for premium feel
                path.addRoundRect(RectF(0f, 0f, size, size), r, r, Path.Direction.CW)
            }
            IconShape.SQUIRCLE -> {
                // True mathematical squircle approximation
                val r = size / 2f
                val cx = r
                val cy = r
                val c = r * 0.58f

                path.moveTo(cx + r, cy)
                path.cubicTo(cx + r, cy + c, cx + c, cy + r, cx, cy + r)
                path.cubicTo(cx - c, cy + r, cx - r, cy + c, cx - r, cy)
                path.cubicTo(cx - r, cy - c, cx - c, cy - r, cx, cy - r)
                path.cubicTo(cx + c, cy - r, cx + r, cy - c, cx + r, cy)
                path.close()
            }
            IconShape.ORIGINAL -> {
                path.addRect(RectF(0f, 0f, size, size), Path.Direction.CW)
            }
        }
        return path
    }

    private fun applyColorMode(
        bitmap: Bitmap,
        colorMode: IconColorMode,
        customTint: Long,
        isDark: Boolean
    ) {
        if (colorMode == IconColorMode.ORIGINAL_COLORS) return

        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        when (colorMode) {
            IconColorMode.THEME_MATCHED -> {
                val matrix = ColorMatrix()
                matrix.setSaturation(0f)
                if (isDark) {
                    val bright = ColorMatrix(
                        floatArrayOf(
                            1.2f, 0f, 0f, 0f, 15f,
                            0f, 1.2f, 0f, 0f, 15f,
                            0f, 0f, 1.2f, 0f, 15f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                    bright.postConcat(matrix)
                    paint.colorFilter = ColorMatrixColorFilter(bright)
                } else {
                    paint.colorFilter = ColorMatrixColorFilter(matrix)
                }
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
            }
            IconColorMode.CUSTOM_COLORS -> {
                val targetColor = Color(customTint)
                val tr = targetColor.red
                val tg = targetColor.green
                val tb = targetColor.blue

                val cm = ColorMatrix(
                    floatArrayOf(
                        0.299f * tr, 0.587f * tr, 0.114f * tr, 0f, 0f,
                        0.299f * tg, 0.587f * tg, 0.114f * tg, 0f, 0f,
                        0.299f * tb, 0.587f * tb, 0.114f * tb, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                paint.colorFilter = ColorMatrixColorFilter(cm)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
            }
            IconColorMode.ORIGINAL_COLORS -> Unit
        }
    }
}

@Composable
fun NoirAppIcon(
    packageName: String,
    label: String,
    iconSizeDp: Int,
    iconShape: IconShape,
    iconColorMode: IconColorMode,
    customTint: Long,
    fallbackBitmap: ImageBitmap? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = GraphiteTheme.colors
    val density = LocalDensity.current
    val isDark = colors.isDark

    val sizeDp: Dp = iconSizeDp.dp
    val sizePx = remember(iconSizeDp, density) {
        with(density) { sizeDp.roundToPx().coerceAtLeast(24) }
    }

    val key = remember(packageName, iconShape, iconColorMode, customTint, sizePx, isDark) {
        IconRenderKey(packageName, iconShape, iconColorMode, customTint, sizePx, isDark)
    }

    val renderedBitmap by produceState<ImageBitmap?>(
        initialValue = NoirIconEngine.getCachedBitmap(key) ?: fallbackBitmap,
        key1 = key
    ) {
        val cached = NoirIconEngine.getCachedBitmap(key)
        if (cached != null) {
            value = cached
        } else {
            val loaded = NoirIconEngine.loadAndRenderIcon(
                context = context,
                packageName = packageName,
                iconShape = iconShape,
                iconColorMode = iconColorMode,
                customTint = customTint,
                sizePx = sizePx,
                isDark = isDark
            )
            value = loaded ?: fallbackBitmap
        }
    }

    val composeClipShape = when (iconShape) {
        IconShape.ORIGINAL -> RectangleShape
        IconShape.CIRCLE -> CircleShape
        IconShape.ROUNDED_SQUARE -> RoundedCornerShape(percent = 22)
        IconShape.SQUARE -> RoundedCornerShape(percent = 6)
        IconShape.SQUIRCLE -> SquircleShape
    }

    Box(
        modifier = modifier
            .size(sizeDp)
            .then(if (iconShape != IconShape.ORIGINAL) Modifier.clip(composeClipShape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (renderedBitmap != null) {
            Image(
                bitmap = renderedBitmap!!,
                contentDescription = label,
                modifier = Modifier.size(sizeDp)
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
