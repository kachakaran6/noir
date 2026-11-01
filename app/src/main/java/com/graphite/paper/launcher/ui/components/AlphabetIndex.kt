package com.graphite.paper.launcher.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.graphite.paper.launcher.ui.theme.GraphiteTheme
import com.graphite.paper.launcher.ui.theme.SwissSans

val ALPHABET_LIST = listOf("#") + ('A'..'Z').map { it.toString() }

@Composable
fun AlphabetIndex(
    activeLetter: String?,
    availableLetters: Set<String>,
    onLetterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GraphiteTheme.colors
    val haptic = LocalHapticFeedback.current
    var totalHeight by remember { mutableStateOf(1f) }
    var lastSelectedIndex by remember { mutableStateOf<Int?>(null) }

    fun handleSelection(yPos: Float) {
        val count = ALPHABET_LIST.size
        val itemHeight = totalHeight / count.toFloat()
        val index = (yPos / itemHeight).toInt().coerceIn(0, count - 1)
        if (index != lastSelectedIndex) {
            lastSelectedIndex = index
            val letter = ALPHABET_LIST[index]
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onLetterSelected(letter)
        }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(22.dp)
            .padding(vertical = 4.dp)
            .onGloballyPositioned { coordinates ->
                totalHeight = coordinates.size.height.toFloat().coerceAtLeast(1f)
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        handleSelection(offset.y)
                    }
                )
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        handleSelection(offset.y)
                    },
                    onDragEnd = {
                        lastSelectedIndex = null
                    },
                    onDragCancel = {
                        lastSelectedIndex = null
                    },
                    onVerticalDrag = { change, _ ->
                        change.consume()
                        handleSelection(change.position.y)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ALPHABET_LIST.forEach { letter ->
                val isActive = activeLetter != null && activeLetter.equals(letter, ignoreCase = true)
                val isAvailable = availableLetters.contains(letter)

                val targetColor = when {
                    isActive -> colors.primaryText
                    isAvailable -> colors.secondaryText.copy(alpha = 0.85f)
                    else -> colors.mutedText.copy(alpha = 0.25f)
                }

                val animatedColor by animateColorAsState(
                    targetValue = targetColor,
                    animationSpec = tween(150),
                    label = "AlphabetColor"
                )

                Text(
                    text = letter,
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = SwissSans,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        fontSize = if (isActive) 10.sp else 8.5.sp,
                        letterSpacing = 0.sp,
                        color = animatedColor
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
    }
}
