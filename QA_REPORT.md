# QA Test Report — NOIR Launcher v0.1.2

**Package Name**: `com.samyukt.noir`  
**Application Name**: `NOIR`  
**Test Date**: 2026-11-02  
**Test Suite**: `LauncherCoreLogicTest.kt` + Build Verification  
**Build Variant**: `release` (signed with APK Signature Scheme v2)  
**Version**: `0.1.2` (`versionCode: 3`)  
**SHA-256 Checksum**: `2D1DBEE55B4E17E1E00D5C2541830DFD29A6C6B8624A52C9173922C7CCC01476`

---

## 1. Xiaomi / POCO Navigation Compatibility Diagnostics

| Test ID | Test Case | Expected Behavior | Actual Behavior | Status |
|:---|:---|:---|:---|:---:|
| OEM-01 | POCO & Xiaomi Detection | Identifies Xiaomi/POCO/Redmi brand, manufacturer, MIUI & HyperOS props | Verified via `DeviceCompatibilityManager` detection logic | **PASS** |
| OEM-02 | Navigation Mode Query | Identifies active mode (3-Button, Full-Screen Gestures, 2-Button) | Reads system secure/global settings including `force_fsg_nav_bar` | **PASS** |
| OEM-03 | Status Categorization | Marks Xiaomi/POCO as `SYSTEM_RESTRICTED`; standard AOSP as `COMPATIBLE` | Accurately assigns status without false gesture availability claims | **PASS** |
| OEM-04 | Diagnostic UI | `Settings → Device Compatibility` renders telemetry grid and status badge | Renders hardware, brand, OS software, launcher and nav status | **PASS** |
| OEM-05 | Navigation Settings Action | "Open Navigation Settings" button triggers verified system intents | Safe fallback hierarchy: Xiaomi FullScreenDisplay, SystemNav, Display | **PASS** |
| OEM-06 | Three-Button Fallback | 3-button navigation Back/Home/Recents functions reliably on restricted devices | Works 100% reliably; no crashes or exit events | **PASS** |
| OEM-07 | Back Navigation Integration | Pressing Back on Device Compatibility screen returns to Settings | Scoped `BackHandler` verified for `Screen.DEVICE_COMPATIBILITY` | **PASS** |

---

## 2. Icon Shape Engine Overhaul (`NoirAppIcon`)

| Test ID | Test Case | Expected Behavior | Actual Behavior | Status |
|:---|:---|:---|:---|:---:|
| SHAPE-01 | Root Cause Resolution | Replaces pre-baked masked bitmaps with full adaptive layer unpacking | `NoirIconEngine` extracts raw background, foreground, and monochrome | **PASS** |
| SHAPE-02 | Shape: Square | Renders full-bleed square with background filling all 4 corners | Background fills entire square geometry without transparent cuts | **PASS** |
| SHAPE-03 | Shape: Circle | Clips adaptive layers to perfect circular boundary | Full-bleed circular icon rendered cleanly | **PASS** |
| SHAPE-04 | Shape: Rounded Square | Smooth 22% rounded corner radius with full-bleed background | Uniform rounded square contours verified | **PASS** |
| SHAPE-05 | Shape: Squircle | Mathematical super-ellipse ($n=3.2$) curve applied to canvas | True squircle shape rendered with zero distortion | **PASS** |
| SHAPE-06 | Shape: Original | Preserves OEM / legacy application icon presentation as-is | Original artwork untouched | **PASS** |
| SHAPE-07 | Legacy Icon Wrapper | Non-adaptive bitmaps wrapped in shaped surface plate (72% centered) | Prevents clipping of legacy non-adaptive app artwork | **PASS** |
| SHAPE-08 | Live Shape Update | Changing shape in settings updates all visible icons immediately | In-memory cache invalidated; immediate recomposition occurs | **PASS** |
| SHAPE-09 | Android 13+ Themed Icons | Uses official `drawable.monochrome` layer in Theme-Matched mode | Material You themed icons render with clean surface background | **PASS** |
| SHAPE-10 | Custom Tint Mode | Luminance/monochrome tint applied consistently across all icons | Verified via custom color matrix and monochrome layer | **PASS** |

---

## 3. Automated Test Suite & Release Verification

| Test ID | Test Case | Expected Behavior | Actual Behavior | Status |
|:---|:---|:---|:---|:---:|
| TEST-01 | POCO Detection Logic Unit Test | Correctly identifies POCO, Redmi, Xiaomi combinations | `testDeviceCompatibility_PocoDetectionLogic` passed | **PASS** |
| TEST-02 | Icon Key Cache Isolation | Differentiates keys across shapes, color modes, and sizes | `testIconEngineKey_ShapeDistinction` passed | **PASS** |
| TEST-03 | Gradle Test Task | `testReleaseUnitTest` executes all unit tests successfully | 25/25 unit tests passed in 34s | **PASS** |
| TEST-04 | Gradle Release Build | `assembleRelease` produces signed APK | BUILD SUCCESSFUL in 49s | **PASS** |
| TEST-05 | APK Signature v2 | Verified with Android SDK `apksigner` | `Verified using v2 scheme: true` | **PASS** |
| TEST-06 | Artifact Integrity | SHA-256 computed and matches release tag | `2D1DBEE55B4E17E1E00D5C2541830DFD29A6C6B8624A52C9173922C7CCC01476` | **PASS** |
