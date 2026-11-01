package com.graphite.paper.launcher.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.graphite.paper.launcher.ui.theme.EditorialSerif
import com.graphite.paper.launcher.ui.theme.GraphiteTheme
import com.graphite.paper.launcher.ui.theme.SwissSans
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt

val ALPHABET_LIST = listOf("#") + ('A'..'Z').map { it.toString() }

@Composable
fun AlphabetIndex(
    activeLetter: String?,
    availableLetters: Set<String>,
    onLetterSelected: (letter: String, isDragging: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GraphiteTheme.colors
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current

    var totalHeightPx by remember { mutableFloatStateOf(1f) }
    var isDragging by remember { mutableStateOf(false) }
    var touchYPx by remember { mutableFloatStateOf(0f) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    var lastHapticIndex by remember { mutableIntStateOf(-1) }

    val itemCount = ALPHABET_LIST.size

    fun updateSelection(yPos: Float, dragging: Boolean) {
        touchYPx = yPos.coerceIn(0f, totalHeightPx)
        val fraction = (touchYPx / totalHeightPx.coerceAtLeast(1f)).coerceIn(0f, 0.999f)
        val newIndex = (fraction * itemCount).toInt().coerceIn(0, itemCount - 1)

        if (newIndex != selectedIndex || dragging != isDragging) {
            selectedIndex = newIndex
            if (newIndex != lastHapticIndex) {
                lastHapticIndex = newIndex
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            onLetterSelected(ALPHABET_LIST[newIndex], dragging)
        }
    }

    // Continuous floating touch progress (0.0 to 26.0)
    val touchProgress = if (isDragging && totalHeightPx > 0) {
        (touchYPx / totalHeightPx * itemCount).coerceIn(0f, (itemCount - 1).toFloat())
    } else {
        val activeIdx = activeLetter?.let { ALPHABET_LIST.indexOf(it) }?.takeIf { it >= 0 } ?: 0
        activeIdx.toFloat()
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(56.dp)
            .onGloballyPositioned { coordinates ->
                totalHeightPx = coordinates.size.height.toFloat().coerceAtLeast(1f)
            }
            .pointerInput(itemCount) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    isDragging = true
                    updateSelection(down.position.y, dragging = true)

                    drag(down.id) { change ->
                        if (change.positionChange() != androidx.compose.ui.geometry.Offset.Zero) {
                            change.consume()
                            updateSelection(change.position.y, dragging = true)
                        }
                    }

                    // Gesture release / settle
                    isDragging = false
                    updateSelection(touchYPx, dragging = false)
                }
            }
    ) {
        // Floating Letter Preview Badge (aligned to current drag position)
        val previewYOffset = with(density) {
            (touchYPx - 26.dp.toPx()).coerceIn(
                0f,
                (totalHeightPx - 52.dp.toPx()).coerceAtLeast(0f)
            ).roundToInt()
        }

        AnimatedVisibility(
            visible = isDragging,
            enter = fadeIn(tween(120)) + scaleIn(spring(stiffness = Spring.StiffnessMediumLow)),
            exit = fadeOut(tween(150)) + scaleOut(tween(150)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset { IntOffset(x = -72.dp.roundToPx(), y = previewYOffset) }
        ) {
            val currentLetter = ALPHABET_LIST.getOrElse(selectedIndex) { "#" }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(elevation = 3.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(colors.background)
                    .border(width = 0.75.dp, color = colors.divider, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentLetter,
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = EditorialSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 24.sp,
                        color = colors.primaryText,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }

        // Curved Alphabet Column
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(36.dp)
        ) {
            val itemHeightPx = totalHeightPx / itemCount.toFloat()

            ALPHABET_LIST.forEachIndexed { index, letter ->
                val isAvailable = availableLetters.contains(letter)
                val isActive = activeLetter != null && activeLetter.equals(letter, ignoreCase = true)

                // Distance from touch progress in item units
                val dist = abs(index - touchProgress)

                // Gaussian influence curve for smooth physical arc (sigma = 2.0 items)
                val gaussianInfluence = if (isDragging) {
                    exp(-(dist * dist) / (2f * 2.0f * 2.0f)).coerceIn(0f, 1f)
                } else if (isActive) {
                    0.25f
                } else {
                    0f
                }

                // Smoothly animated arc horizontal deflection (offset towards the left)
                val targetOffsetDp = -gaussianInfluence * 18f
                val animatedOffsetDp by animateFloatAsState(
                    targetValue = targetOffsetDp,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy),
                    label = "CurvedOffset_$letter"
                )

                // Smooth scale
                val targetScale = 1.0f + gaussianInfluence * 0.55f
                val animatedScale by animateFloatAsState(
                    targetValue = targetScale,
                    animationSpec = tween(100),
                    label = "Scale_$letter"
                )

                // Color calculation
                val targetColor = when {
                    isDragging && index == selectedIndex -> colors.primaryText
                    isActive -> colors.primaryText
                    isAvailable -> colors.secondaryText.copy(alpha = 0.85f)
                    else -> colors.mutedText.copy(alpha = 0.25f)
                }

                val itemYPx = (index + 0.5f) * itemHeightPx - (itemHeightPx / 2f)
                val itemYOffsetDp = with(density) { itemYPx.toDp() }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(
                            x = animatedOffsetDp.dp - 6.dp,
                            y = itemYOffsetDp
                        )
                        .scale(animatedScale)
                        .graphicsLayer {
                            this.alpha = if (isAvailable || isActive || isDragging) 1f else 0.4f
                        }
                        .semantics {
                            contentDescription = "Alphabet letter $letter"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Small circular highlight pill behind the active letter when dragging
                    if (isDragging && index == selectedIndex) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(colors.surface.copy(alpha = 0.7f))
                        )
                    }

                    Text(
                        text = letter,
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = SwissSans,
                            fontWeight = if ((isDragging && index == selectedIndex) || isActive) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 9.sp,
                            color = targetColor,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }
    }
}
