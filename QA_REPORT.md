# QA Test Report — NOIR Launcher v0.1.1

**Package Name**: `com.samyukt.noir`  
**Application Name**: `NOIR`  
**Test Date**: 2026-11-02  
**Test Environment**: Physical Android Device `3086027538000H5` (Android 13 / API 33)  
**Build Variant**: `release` (signed with APK Signature Scheme v2)  
**Version**: `0.1.1` (`versionCode: 2`)  
**SHA-256 Checksum**: `9856E0FE32318E1BA40FA2FAEE04786B267F0957179C71CF548CF9FCFAA6D470`

---

## 1. UI Polish & Typography Verification

| Test ID | Test Case | Expected Behavior | Actual Behavior | Status |
|:---|:---|:---|:---|:---:|
| POLISH-01 | Chevron Removal | No chevrons (`›`) or arrows in application rows | Completely removed; row names occupy full horizontal width | **PASS** |
| POLISH-02 | Row Alignment & Spacing | Icon and label maintain 16dp spacing and vertical centering | Clean editorial alignment verified | **PASS** |
| POLISH-03 | Row Interactions | Tap launches app, long-press triggers context menu and haptic | Verified on physical device | **PASS** |
| POLISH-04 | App Display Name | Manifest and string resources report official name `NOIR` | Verified `NOIR` in `strings.xml` and Android Settings | **PASS** |

---

## 2. Android Back Navigation Architecture

| Test ID | Test Case | Expected Behavior | Actual Behavior | Status |
|:---|:---|:---|:---|:---:|
| NAV-01 | 3-Button System Back | Pressing system Back in Settings returns to Home screen | Successfully navigates from Settings to Home | **PASS** |
| NAV-02 | Nested Back Navigation | Icon Customization -> Settings -> Home | Back button traverses nested screens in correct reverse order | **PASS** |
| NAV-03 | Left Edge Swipe Gesture | Inward swipe from left screen edge triggers system back | Navigates back smoothly | **PASS** |
| NAV-04 | Right Edge Swipe Gesture | Inward swipe from right screen edge triggers system back | Navigates back smoothly without interception | **PASS** |
| NAV-05 | Alphabet Gesture Discrimination | Inward horizontal swipe on right edge yields to system back | `dx < -touchSlop` cancels scrubber and passes touch to OS | **PASS** |
| NAV-06 | Search Back Dismissal | Pressing Back while searching clears input and closes keyboard | Input cleared, focus cleared, soft keyboard hidden | **PASS** |
| NAV-07 | Context Menu Dismissal | Pressing Back while bottom sheet is open dismisses sheet | Modal sheet dismisses cleanly, returns to Home | **PASS** |
| NAV-08 | Root Home Safety | Back on root Home screen does not exit or crash launcher | Native HOME category handles event safely | **PASS** |
| NAV-09 | Predictive Back Support | `android:enableOnBackInvokedCallback="true"` enabled | Ahead-of-time back callbacks registered on API 33+ | **PASS** |

---

## 3. NOIR "N" Icon Asset & Density Verification

| Test ID | Test Case | Expected Behavior | Actual Behavior | Status |
|:---|:---|:---|:---|:---:|
| ICON-06 | Master Asset Fidelity | Editorial serif "N" on cream paper with sparkle and curled corner | Verified high-res master resampled across all mipmaps | **PASS** |
| ICON-07 | Density Assets | `mdpi` through `xxxhdpi` foregrounds & legacy icons | Generated and verified across all 5 mipmap densities | **PASS** |
| ICON-08 | Adaptive Safe Zone | 72dp safe zone circle (66.6%) leaves margins for OEM masks | Paper sticker fits inside 64% of canvas; zero edge clipping | **PASS** |
| ICON-09 | Adaptive Background | Black background `#000000` matches underside of curl | Seamless edge blending across all mask shapes | **PASS** |
| ICON-10 | Legacy Cleanliness | Removal of old G vector and legacy template references | Deleted `ic_launcher_foreground.xml` and old assets | **PASS** |

---

## 4. Alphabet Scrubber Performance

| Test ID | Test Case | Expected Behavior | Actual Behavior | Status |
|:---|:---|:---|:---|:---:|
| SCRUB-01 | Instant Touch-Down | Scrubber activates immediately upon finger touch | Instant activation; zero noticeable debounce delay | **PASS** |
| SCRUB-02 | GPU RenderNode Arc | Arc deflection and scale computed on GPU via `graphicsLayer` | Zero recompositions during continuous dragging | **PASS** |
| SCRUB-03 | Floating Preview Badge | 54dp tactile circular badge tracks finger position | Centered letter preview renders with high contrast | **PASS** |
| SCRUB-04 | Rapid List Scrubbing | Fast scrubbing synchronizes app list without lag | Single-job coroutine cancellation eliminates queue latency | **PASS** |
| SCRUB-05 | Release Spring Settle | Scrubber returns smoothly to resting position on release | Spring-based damping ratio low bouncy settling | **PASS** |

---

## 5. Security & Release Verification

| Test ID | Test Case | Expected Behavior | Actual Behavior | Status |
|:---|:---|:---|:---|:---:|
| SEC-01 | Release Signing | Signed with APK Signature Scheme v2 | Verified via Android SDK `apksigner` | **PASS** |
| SEC-02 | SHA-256 Integrity | Exact hash recorded for release distribution | Matches `9856E0FE32318E1BA40FA2FAEE04786B267F0957179C71CF548CF9FCFAA6D470` | **PASS** |
| SEC-03 | Clean Installation | Successfully uninstalls previous package and installs clean APK | Verified on physical device `3086027538000H5` | **PASS** |
