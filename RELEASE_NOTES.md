# NOIR Launcher v0.1.2 — Xiaomi/POCO Compatibility & Icon Shape Engine

Welcome to **NOIR Launcher** (`v0.1.2`, `versionCode: 3`).

This release provides comprehensive compatibility for Xiaomi and POCO devices running MIUI and HyperOS, introduces a dedicated Device Compatibility diagnostics screen, completely resolves the icon shape customization engine across all five supported geometries, and provides instantaneous live icon updates.

---

### Highlights & Key Improvements

1. **Xiaomi & POCO Gesture Navigation Diagnostics**
   - **Multi-Property OEM Detection**: Detects Xiaomi, POCO, and Redmi hardware, MIUI versions, HyperOS releases, and active navigation modes (3-button vs gestures).
   - **Transparent System Policy Guidance**: Explains that Xiaomi firmware disables full-screen gestures for third-party launchers, confirms that 3-button navigation works 100% reliably, and sets realistic expectations without unsupported hacks.
   - **Device Compatibility Screen**: Accessible at `Settings → Device Compatibility` with live hardware telemetry, system software detection, and status badges (`Compatible`, `System restricted`, `Unknown`).
   - **Supported Navigation Settings Action**: One-tap button attempting verified Xiaomi full-screen settings intents (`Settings$FullScreenDisplaySettingsActivity`), standard gesture settings, and display settings with manual fallback guidance.

2. **Complete Icon Shape Engine Fix (`NoirAppIcon`)**
   - **Root Cause Identified**: Previous pipeline used pre-baked bitmaps from `loadIcon()`, which contained OEM-enforced masks and transparent corners, rendering Compose-level clipping ineffective for shapes like Square.
   - **Adaptive Layer Unpacking**: `NoirIconEngine` now extracts and unpacks `AdaptiveIconDrawable` into its constituent background, foreground, and monochrome layers.
   - **True Full-Bleed Shaping**: Background layer expands to fill the entire canvas before shape clipping, ensuring crisp, full-bleed corners for Square, smooth curves for Rounded Square, perfect circles for Circle, and mathematical super-ellipses for Squircle.
   - **Legacy Icon Wrapper Plates**: Legacy non-adaptive icons are centered at 72% within shaped background plates, ensuring uniform geometry across all installed apps without clipping artwork.
   - **Android 13+ Themed Icons**: Uses official `drawable.monochrome` layer when `Theme Matched` or `Custom Colors` is active.
   - **Immediate Live Updates**: Backed by a multi-property in-memory LRU cache with instant invalidation upon shape or color changes—no app restart required.

---

### Release Verification & Checksums

- **Package Name**: `com.samyukt.noir`
- **Application Name**: `NOIR`
- **Artifact**: `NOIR-v0.1.2.apk`
- **Version Name**: `0.1.2`
- **Version Code**: `3`
- **SHA-256**: `2D1DBEE55B4E17E1E00D5C2541830DFD29A6C6B8624A52C9173922C7CCC01476`
- **Signing Scheme**: APK Signature Scheme v2 (Verified via Android SDK `apksigner`)
- **Target OS**: Android 8.0 (API 26) through Android 15 (API 35)

---

### Installation Instructions

```bash
# Clean install or update onto device
adb install -r NOIR-v0.1.2.apk
```
