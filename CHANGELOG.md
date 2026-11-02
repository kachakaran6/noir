# Changelog

All notable changes to the NOIR Launcher project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.2] - 2026-11-02

### Added
- **Xiaomi & POCO Device Compatibility Layer**: Multi-property OEM detection covering manufacturer (`Xiaomi`), device branding (`POCO`, `Redmi`), system properties (`ro.mi.os.version.name` for HyperOS, `ro.miui.ui.version.name` for MIUI), and active navigation mode (`Settings.Global.force_fsg_nav_bar`).
- **Device Compatibility Diagnostics Screen**: Dedicated `Settings → Device Compatibility` screen displaying real-time platform telemetry, default launcher status, and compatibility analysis.
- **POCO / Xiaomi Gesture Navigation Guidance**: Transparent explanation of manufacturer-level third-party launcher full-screen gesture blocks with direct "Open Navigation Settings" intent launcher and manual fallback steps.
- **Centralized `NoirAppIcon` Engine**: Re-engineered complete icon rendering pipeline with layer unpacking for `AdaptiveIconDrawable` (full-bleed background, centered foreground, and Android 13+ `monochrome` layer).
- **Universal Legacy Icon Shaping**: Legacy non-adaptive bitmaps are wrapped in mathematically shaped surface plates to guarantee consistent geometry across all 5 supported shapes without distortion.
- **Multi-Property In-Memory LRU Cache**: Implemented high-performance caching keyed by package, shape, color mode, tint, size, and theme mode with instant cache invalidation upon user selection.

### Changed
- **Immediate Live Icon Shape Updates**: Resolved root cause where pre-baked OEM-masked bitmaps prevented shape changes; icon shape selections now apply instantaneously across the home screen, context menu, and settings previews.
- **Back Navigation Coverage**: Scoped `BackHandler` integration for `Screen.DEVICE_COMPATIBILITY`.

## [0.1.1] - 2026-11-02

### Added
- **Predictive Back Navigation Support**: Enabled `android:enableOnBackInvokedCallback="true"` for modern Android 13/14+ predictive back gestures.
- **Back Navigation Coordination**: Integrated scoped `BackHandler` logic across all screens (Settings, Icon Customization, Hidden Apps, search query, keyboard dismissal, and bottom sheet menus).
- **Edge Back Gesture Discrimination**: Alphabet scrubber now detects horizontal inward edge swipes and gracefully yields to Android system left/right edge back gestures.
- **GPU-Accelerated Scrubber Architecture**: Re-engineered vertical alphabet arc using `graphicsLayer` RenderNodes with instant touch-down activation and spring damping.

### Changed
- **Application List Polish**: Completely removed right-facing arrow indicators (`›`) and trailing spacer padding from every application row, allowing titles to utilize full horizontal width.
- **Canonical NOIR 'N' Icon Branding**: Completely replaced legacy assets with the official editorial serif "N" tactile paper icon across all standard mipmap densities (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`), adaptive foreground, black background, and Material You monochrome layer.
- **Application Display Name**: Standardized application label to `NOIR` in `strings.xml`.
- **List Synchronization**: Eliminated coroutine backlog during rapid dragging by introducing coordinated single-job cancellation for list scrolling.

## [0.1.0] - 2026-11-02

### Added
- **Core Editorial Home Interface**: Warm paper background (`#F2F0E9`), graphite typography (`#252525`), and real-time 120 FPS canvas paper grain texture.
- **Official Android HOME Registration**: Registered `CATEGORY_HOME` and `CATEGORY_DEFAULT` with `singleTask` launch mode in `AndroidManifest.xml`.
- **Default Launcher Manager**: Real-time OS verification of active launcher via `PackageManager.resolveActivity` and native role request via `RoleManager.ROLE_HOME`.
- **In-App Default Launcher Settings**: Dedicated card under `Preferences -> Default Launcher` displaying live status, current launcher label, and one-tap default selection.
- **Curved Alphabet Navigation Scrubber**: Mathematical Gaussian curve deflection on right edge with active letter magnification, floating letter badge, and tactile haptic feedback.
- **Application Search Engine**: Real-time case-insensitive search by application name or package name with smooth collapsible search bar toggle.
- **Multi-Strategy Sorting**: Alphabetical A–Z, Alphabetical Z–A, Recently Installed, and Recently Used (via Android `UsageStatsManager`).
- **Comprehensive Icon Customization**:
  - Shapes: Original, Circle, Rounded Square, Square, Squircle.
  - Color Modes: Original Colors, Theme Matched Monochrome, and Custom Tint.
  - Scalable icon size slider (24dp to 56dp) with live real-time preview screen.
- **Dual Editorial Themes**: Warm Paper Light (`#F2F0E9`) and Deep Charcoal Dark (`#151515`) with dynamic system bar contrast control.
- **App Privacy Vault**: Long-press context menu to inspect, uninstall, or hide apps, with dedicated Hidden Apps management screen.
- **Automated Test Suite**: Full unit tests covering search filtering, sorting algorithms, alphabet extraction, and icon shape configurations (`LauncherCoreLogicTest.kt`).
- **Production Release Signing**: Automated release signing using APK Signature Scheme v2.

