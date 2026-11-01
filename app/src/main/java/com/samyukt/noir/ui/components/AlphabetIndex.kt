package com.samyukt.noir.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
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
import com.samyukt.noir.ui.theme.EditorialSerif
import com.samyukt.noir.ui.theme.GraphiteTheme
import com.samyukt.noir.ui.theme.SwissSans
import kotlinx.coroutines.launch
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
    val scope = rememberCoroutineScope()

    var totalHeightPx by remember { mutableFloatStateOf(1f) }
    var isDragging by remember { mutableStateOf(false) }
    var touchYPx by remember { mutableFloatStateOf(0f) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    var lastHapticIndex by remember { mutableIntStateOf(-1) }

    // Smooth spring animation for drag activation / release (0f when idle, 1f when dragging)
    val dragIntensity = remember { Animatable(0f) }

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

    // Touch progress along alphabet (0.0 to 26.0)
    val touchProgress = if (isDragging && totalHeightPx > 0) {
        (touchYPx / totalHeightPx * itemCount).coerceIn(0f, (itemCount - 1).toFloat())
    } else {
        val activeIdx = activeLetter?.let { ALPHABET_LIST.indexOf(it) }?.takeIf { it >= 0 } ?: 0
        activeIdx.toFloat()
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(42.dp)
            .onGloballyPositioned { coordinates ->
                totalHeightPx = coordinates.size.height.toFloat().coerceAtLeast(1f)
            }
            .pointerInput(itemCount) {
                val touchSlop = viewConfiguration.touchSlop
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val startX = down.position.x
                    val startY = down.position.y

                    var isBackSwipe = false
                    isDragging = true
                    scope.launch {
                        dragIntensity.animateTo(1f, spring(stiffness = Spring.StiffnessHigh))
                    }
                    updateSelection(down.position.y, dragging = true)

                    while (true) {
                        val event = awaitPointerEvent()
                        val pointer = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!pointer.pressed) {
                            break
                        }

                        val dx = pointer.position.x - startX
                        val dy = pointer.position.y - startY

                        // Detect Android right-edge back gesture:
                        // Swiping inward from the right edge horizontally (dx < -touchSlop with dominant horizontal motion)
                        if (dx < -touchSlop && abs(dx) > abs(dy) * 1.25f) {
                            // Do not consume! Let system handle edge back gesture
                            isBackSwipe = true
                            break
                        }

                        pointer.consume()
                        updateSelection(pointer.position.y, dragging = true)
                    }

                    isDragging = false
                    scope.launch {
                        dragIntensity.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                    }

                    if (!isBackSwipe) {
                        updateSelection(touchYPx, dragging = false)
                    }
                }
            }
    ) {
        // Floating Letter Preview Badge (aligned to current drag position)
        val previewYOffset = with(density) {
            (touchYPx - 27.dp.toPx()).coerceIn(
                0f,
                (totalHeightPx - 54.dp.toPx()).coerceAtLeast(0f)
            ).roundToInt()
        }

        AnimatedVisibility(
            visible = isDragging,
            enter = fadeIn(spring(stiffness = Spring.StiffnessHigh)) + scaleIn(spring(stiffness = Spring.StiffnessHigh)),
            exit = fadeOut(spring(stiffness = Spring.StiffnessMediumLow)) + scaleOut(spring(stiffness = Spring.StiffnessMediumLow)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset { IntOffset(x = -58.dp.roundToPx(), y = previewYOffset) }
        ) {
            val currentLetter = ALPHABET_LIST.getOrElse(selectedIndex) { "#" }
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .shadow(elevation = 6.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(colors.background)
                    .border(width = 1.dp, color = colors.divider, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentLetter,
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = EditorialSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 26.sp,
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

                // Gaussian influence curve for smooth physical arc (sigma = 2.2 items)
                val gaussianInfluence = exp(-(dist * dist) / (2f * 2.2f * 2.2f)).coerceIn(0f, 1f)

                val itemYPx = (index + 0.5f) * itemHeightPx - (itemHeightPx / 2f)
                val itemYOffsetDp = with(density) { itemYPx.toDp() }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = itemYOffsetDp)
                        .graphicsLayer {
                            val intensity = dragIntensity.value
                            val deflectionDp = -gaussianInfluence * 18f * intensity
                            translationX = deflectionDp * density.density
                            val s = 1.0f + (gaussianInfluence * 0.6f * intensity)
                            scaleX = s
                            scaleY = s
                            alpha = if (isAvailable || isActive || isDragging) 1f else 0.4f
                        }
                        .semantics {
                            contentDescription = "Alphabet letter $letter"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Subtle indicator behind active letter during drag
                    if (isDragging && index == selectedIndex) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(colors.surface.copy(alpha = 0.8f))
                        )
                    }

                    val letterColor = when {
                        isDragging && index == selectedIndex -> colors.primaryText
                        isActive -> colors.primaryText
                        isAvailable -> colors.secondaryText
                        else -> colors.mutedText.copy(alpha = 0.35f)
                    }

                    Text(
                        text = letter,
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = SwissSans,
                            fontWeight = if ((isDragging && index == selectedIndex) || isActive) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 9.sp,
                            color = letterColor,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }
    }
}
