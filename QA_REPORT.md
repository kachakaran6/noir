# QA Test Report — NOIR Launcher v0.1.0

**Test Date**: 2026-11-02  
**Test Environment**: Physical Android Device `3086027538000H5` (Android 13 / API 33)  
**Build Variant**: `release` (signed with APK Signature Scheme v2)  
**Version**: `0.1.0` (`versionCode: 1`)  

---

## 1. Default Android Launcher Integration

| Test ID | Test Case | Expected Behavior | Actual Behavior | Status |
|:---|:---|:---|:---|:---:|
| LAUN-01 | Android HOME Intent Filter | Registered with `MAIN`, `HOME`, and `DEFAULT` categories | Confirmed in `AndroidManifest.xml` | **PASS** |
| LAUN-02 | Default Launcher Detection | System detects whether NOIR is active default home app | Accurately resolved via `PackageManager.resolveActivity` | **PASS** |
| LAUN-03 | RoleManager Invocation | Tapping "Change" opens system default home app selector | Opens `RoleManager.createRequestRoleIntent(ROLE_HOME)` | **PASS** |
| LAUN-04 | Home Key Navigation | Pressing Home key returns to NOIR launcher | Verified on connected physical device | **PASS** |
| LAUN-05 | Home Back Key Behavior | Pressing Back on root Home screen does not exit launcher | Back event consumed; launcher remains active | **PASS** |
| LAUN-06 | Returning from 3rd Party App | Launching an app and returning via Home / Back works seamlessly | Apps launch with `FLAG_ACTIVITY_NEW_TASK` and return smoothly | **PASS** |

---

## 2. Home Screen & Navigation

| Test ID | Test Case | Expected Behavior | Actual Behavior | Status |
|:---|:---|:---|:---|:---:|
| HOME-01 | Installed App Discovery | Loads all installed launcher activities from PackageManager | 138 apps loaded and displayed with names and icons | **PASS** |
| HOME-02 | Dynamic App Updates | BroadcastReceiver catches package added, removed, replaced | Verified with callback flow | **PASS** |
| HOME-03 | Search Filtering | Real-time case-insensitive filtering by label or package | Query "spoti" isolates Spotify immediately | **PASS** |
| HOME-04 | Search Bar Visibility Toggle | Disabling search bar in settings collapses it smoothly | Tested with `AnimatedVisibility` | **PASS** |
| HOME-05 | Alphabetical Sorting (A-Z) | Stable ascending alphabetical sort | Verified via unit test & device UI | **PASS** |
| HOME-06 | Alphabetical Sorting (Z-A) | Stable descending alphabetical sort | Verified via unit test & device UI | **PASS** |
| HOME-07 | Recently Installed Sorting | Orders by `firstInstallTime` descending | Verified via unit test & device UI | **PASS** |
| HOME-08 | Recently Used Sorting | Orders by `UsageStatsManager` last time used | Verified with permission check & intent fallback | **PASS** |
| HOME-09 | Curved Alphabet Navigator | Tapping or dragging along A-Z scrubs list and magnifies letter | Verified on device; tapping M jumped to section M | **PASS** |
| HOME-10 | Floating Letter Badge | Badge appears during drag scrub with target letter | Renders 52dp paper badge with haptic tick | **PASS** |

---

## 3. Icon Customization & Themes

| Test ID | Test Case | Expected Behavior | Actual Behavior | Status |
|:---|:---|:---|:---|:---:|
| ICON-01 | Shape Customization | Renders Original, Circle, Rounded Square, Square, Squircle | All 5 shapes clip properly without stretching source | **PASS** |
| ICON-02 | Color Mode: Original | Source colors preserved | Tested on device | **PASS** |
| ICON-03 | Color Mode: Theme Matched | Monochrome graphite palette applied to icon layers | Tested on device | **PASS** |
| ICON-04 | Color Mode: Custom Tint | User-selected tint applied across all app icons | Tested on device | **PASS** |
| ICON-05 | Icon Size Adjustment | Slider adjusts icon size from 24dp to 56dp | Live preview updates immediately | **PASS** |
| THEME-01 | Paper Light Mode | `#F2F0E9` background, graphite text, dark status bar icons | Verified via device screencap | **PASS** |
| THEME-02 | Charcoal Dark Mode | `#151515` background, light graphite text, light status bar icons | Verified via device screencap | **PASS** |
| THEME-03 | Paper Grain Texture | Procedural canvas noise rendered behind surface | Renders at 120 FPS smoothly | **PASS** |

---

## 4. App Privacy & Persistence

| Test ID | Test Case | Expected Behavior | Actual Behavior | Status |
|:---|:---|:---|:---|:---:|
| PRIV-01 | Context Menu Display | Long pressing app row shows info, hide, uninstall | Dialog opens cleanly with correct app details | **PASS** |
| PRIV-02 | App Hiding & Vault | Hidden app excluded from home list; listed in Hidden Apps | Hidden apps persist in DataStore and can be restored | **PASS** |
| PERS-01 | Preference Persistence | Settings survive application force stop and reboot | Backed by Jetpack DataStore Preferences | **PASS** |
| WIDG-01 | Built-in Editorial Widgets | Date, Clock, and Metric header render accurately | Renders live time and localized date correctly | **PASS** |
| WIDG-02 | Third-Party AppWidgetHost | Hosting 3rd-party Android app widgets | Intentionally not supported in minimalist editorial list design | **BLOCKED / NOT IN SCOPE** |

---

## 5. Automated Build & Verification Gates

| Gate ID | Check | Result |
|:---|:---|:---:|
| BUILD-01 | Gradle compilation (`assembleRelease`) | **PASS** |
| BUILD-02 | Unit test execution (`testDebugUnitTest`) | **PASS** (11/11 tests passed) |
| BUILD-03 | Lint vital release check (`lintVitalRelease`) | **PASS** |
| SIGN-01 | APK signature verification (`apksigner`) | **PASS** (v2 Scheme verified) |
| RUN-01 | Physical device install & execution (`3086027538000H5`) | **PASS** |
