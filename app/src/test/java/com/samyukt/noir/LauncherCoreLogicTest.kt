package com.samyukt.noir

import com.samyukt.noir.model.AppInfo
import com.samyukt.noir.model.IconColorMode
import com.samyukt.noir.model.IconShape
import com.samyukt.noir.model.SortOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LauncherCoreLogicTest {

    private val sampleApps = listOf(
        AppInfo(packageName = "com.brave.browser", activityName = ".Main", label = "Brave", firstInstallTime = 1000L, lastUsedTime = 5000L),
        AppInfo(packageName = "com.spotify.music", activityName = ".Main", label = "Spotify", firstInstallTime = 2000L, lastUsedTime = 9000L),
        AppInfo(packageName = "com.android.settings", activityName = ".Main", label = "Settings", firstInstallTime = 500L, lastUsedTime = 1000L),
        AppInfo(packageName = "org.telegram.messenger", activityName = ".Main", label = "Telegram", firstInstallTime = 4000L, lastUsedTime = 8000L),
        AppInfo(packageName = "com.oneoneone.vpn", activityName = ".Main", label = "1.1.1.1", firstInstallTime = 1500L, lastUsedTime = 2000L)
    )

    @Test
    fun testSearchFiltering_caseInsensitive() {
        val query = "spoti"
        val filtered = sampleApps.filter {
            it.label.lowercase().contains(query) || it.packageName.lowercase().contains(query)
        }
        assertEquals(1, filtered.size)
        assertEquals("Spotify", filtered[0].label)
    }

    @Test
    fun testSearchFiltering_byPackageName() {
        val query = "telegram"
        val filtered = sampleApps.filter {
            it.label.lowercase().contains(query) || it.packageName.lowercase().contains(query)
        }
        assertEquals(1, filtered.size)
        assertEquals("org.telegram.messenger", filtered[0].packageName)
    }

    @Test
    fun testSearchFiltering_emptyQueryReturnsAll() {
        val query = ""
        val filtered = if (query.isEmpty()) sampleApps else sampleApps.filter {
            it.label.lowercase().contains(query)
        }
        assertEquals(5, filtered.size)
    }

    @Test
    fun testAlphabeticalSorting_Ascending() {
        val sorted = sampleApps.sortedBy { it.label.lowercase() }
        assertEquals("1.1.1.1", sorted[0].label)
        assertEquals("Brave", sorted[1].label)
        assertEquals("Settings", sorted[2].label)
        assertEquals("Spotify", sorted[3].label)
        assertEquals("Telegram", sorted[4].label)
    }

    @Test
    fun testAlphabeticalSorting_Descending() {
        val sorted = sampleApps.sortedByDescending { it.label.lowercase() }
        assertEquals("Telegram", sorted[0].label)
        assertEquals("Spotify", sorted[1].label)
        assertEquals("Settings", sorted[2].label)
        assertEquals("Brave", sorted[3].label)
        assertEquals("1.1.1.1", sorted[4].label)
    }

    @Test
    fun testSorting_RecentlyInstalled() {
        val sorted = sampleApps.sortedByDescending { it.firstInstallTime }
        assertEquals("Telegram", sorted[0].label) // 4000L
        assertEquals("Spotify", sorted[1].label)  // 2000L
        assertEquals("1.1.1.1", sorted[2].label)  // 1500L
        assertEquals("Brave", sorted[3].label)    // 1000L
        assertEquals("Settings", sorted[4].label) // 500L
    }

    @Test
    fun testSorting_RecentlyUsed() {
        val sorted = sampleApps.sortedByDescending { it.lastUsedTime }
        assertEquals("Spotify", sorted[0].label)  // 9000L
        assertEquals("Telegram", sorted[1].label) // 8000L
        assertEquals("Brave", sorted[2].label)    // 5000L
        assertEquals("1.1.1.1", sorted[3].label)  // 2000L
        assertEquals("Settings", sorted[4].label) // 1000L
    }

    @Test
    fun testAlphabetIndex_InitialExtraction() {
        val availableLetters = sampleApps.map { app ->
            val first = app.label.firstOrNull()?.uppercaseChar() ?: '#'
            if (first in 'A'..'Z') first.toString() else "#"
        }.toSet()

        assertTrue(availableLetters.contains("#")) // 1.1.1.1 maps to #
        assertTrue(availableLetters.contains("B")) // Brave
        assertTrue(availableLetters.contains("S")) // Settings, Spotify
        assertTrue(availableLetters.contains("T")) // Telegram
        assertFalse(availableLetters.contains("Z"))
    }

    @Test
    fun testHiddenPackagesExclusion() {
        val hidden = setOf("com.brave.browser", "com.oneoneone.vpn")
        val visible = sampleApps.filter { !hidden.contains(it.packageName) }
        assertEquals(3, visible.size)
        assertFalse(visible.any { it.packageName == "com.brave.browser" })
        assertFalse(visible.any { it.packageName == "com.oneoneone.vpn" })
    }

    @Test
    fun testIconShapeValues() {
        val shapes = IconShape.values()
        assertEquals(5, shapes.size)
        assertTrue(shapes.contains(IconShape.ORIGINAL))
        assertTrue(shapes.contains(IconShape.CIRCLE))
        assertTrue(shapes.contains(IconShape.ROUNDED_SQUARE))
        assertTrue(shapes.contains(IconShape.SQUARE))
        assertTrue(shapes.contains(IconShape.SQUIRCLE))
    }

    @Test
    fun testIconColorModes() {
        val modes = IconColorMode.values()
        assertEquals(3, modes.size)
        assertTrue(modes.contains(IconColorMode.ORIGINAL_COLORS))
        assertTrue(modes.contains(IconColorMode.THEME_MATCHED))
        assertTrue(modes.contains(IconColorMode.CUSTOM_COLORS))
    }
}

