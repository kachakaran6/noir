# Changelog

All notable changes to the NOIR Launcher project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

