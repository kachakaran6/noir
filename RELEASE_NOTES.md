# NOIR Launcher v0.1.1 — Maintenance & Navigation Release

Welcome to **NOIR Launcher** (`v0.1.1`, `versionCode: 2`).

This release delivers major UI polish, full native Android back navigation support (including left and right edge gestures and predictive back), clean row typography without chevron clutter, the new canonical editorial "N" icon across all device densities, and an ultra-fast alphabet navigation scrubber.

---

### Highlights & Key Improvements

1. **Editorial Application List Polish**
   - Completely removed right-facing arrow indicators (`›`) from every application row.
   - Removed reserved trailing spacer padding so application names utilize the full horizontal width.
   - Preserves all row click, long-press, context-menu, and ripple animations.

2. **Native Android Back Navigation Architecture**
   - **System 3-Button & Physical Back**: Fully functional across all screens; returns to previous parent screen or dismisses active overlays without exiting the launcher.
   - **System Edge-Swipe Gestures**: Android left-edge and right-edge back swipe gestures work reliably.
   - **Gesture Discrimination**: Right-side alphabet index detects horizontal inward back gestures (`dx < -touchSlop`) and immediately yields control to Android OS back navigation without consuming events.
   - **Predictive Back Navigation**: Enabled `android:enableOnBackInvokedCallback="true"` for smooth Android 13/14+ back animations.
   - **Modal & Search Dismissal**: Pressing Back automatically closes the soft keyboard, clears search query, or closes open bottom sheets/dialogs before leaving the screen.

3. **Canonical NOIR "N" Paper Icon Branding**
   - Completely replaced all legacy assets with the master editorial serif **"N"** paper sticker icon featuring warm cream texture, minimal sparkle, vertical line, and folded charcoal corner.
   - Generated density-specific adaptive foregrounds and legacy icons:
     - `mipmap-mdpi` (108px / 48px)
     - `mipmap-hdpi` (162px / 72px)
     - `mipmap-xhdpi` (216px / 96px)
     - `mipmap-xxhdpi` (324px / 144px)
     - `mipmap-xxxhdpi` (432px / 192px)
   - Standardized adaptive icon XML with `<background android:drawable="@drawable/ic_launcher_background" />` (#000000) and 72dp safe-zone compliance.
   - Removed all legacy G vectors and references.
   - Verified official display name: **NOIR**.

4. **Ultra-Fast Alphabet Navigation Scrubber**
   - Re-architected curved letter arc with Jetpack Compose `Modifier.graphicsLayer` RenderNodes, eliminating recomposition overhead during drag.
   - Instant touch-down response with spring intensity transitions (`Animatable`).
   - Floating 54dp tactile letter badge follows dragging smoothly.
   - Immediate list synchronization with single-job coroutine cancellation to prevent scroll queuing.

---

### Release Verification & Checksums

- **Package Name**: `com.samyukt.noir`
- **Application Name**: `NOIR`
- **Artifact**: `NOIR-v0.1.1.apk`
- **Version Name**: `0.1.1`
- **Version Code**: `2`
- **SHA-256**: `9856E0FE32318E1BA40FA2FAEE04786B267F0957179C71CF548CF9FCFAA6D470`
- **Signing Scheme**: APK Signature Scheme v2 (Verified via Android SDK `apksigner`)
- **Target OS**: Android 8.0 (API 26) through Android 15 (API 35)

---

### Installation Instructions

```bash
# Clean install onto connected device
adb uninstall com.samyukt.noir
adb install NOIR-v0.1.1.apk
```
