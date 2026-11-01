package com.graphite.paper.launcher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import com.graphite.paper.launcher.ui.theme.GraphiteTheme
import java.util.Random

@Composable
fun PaperSurface(
    modifier: Modifier = Modifier,
    enableGrain: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = GraphiteTheme.colors

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        if (enableGrain) {
            // Very subtle procedural paper texture: deterministic points
            val dotColor = if (colors.isDark) {
                Color.White.copy(alpha = 0.018f)
            } else {
                Color.Black.copy(alpha = 0.022f)
            }

            // Generate deterministic fiber points once
            val points = remember {
                val rand = Random(42L)
                val list = ArrayList<Offset>(300)
                for (i in 0 until 300) {
                    list.add(Offset(rand.nextFloat(), rand.nextFloat()))
                }
                list
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val scaledPoints = points.map { Offset(it.x * w, it.y * h) }
                drawPoints(
                    points = scaledPoints,
                    pointMode = PointMode.Points,
                    color = dotColor,
                    strokeWidth = 1.2f
                )
            }
        }

        content()
    }
}
