# MyKisah — Android Journey Tracking App

## Project Overview

MyKisah is an Android native app that reconstructs a user's travel journey by reading GPS coordinates and timestamps embedded in photo EXIF metadata. Photos taken with a camera (stored in DCIM/Camera) are parsed, stored locally, and visualized on an interactive map with a chronological timeline.

- **Package:** `com.ramdan.MyKisah`
- **Type:** Android Native (Kotlin + Jetpack Compose)
- **Min SDK:** 26 (Android 8.0) | **Target/Compile SDK:** 35
- **Build status:** Compiling successfully ✅

---

## Tech Stack

| Layer | Library | Version |
|---|---|---|
| Language | Kotlin | 1.9.25 |
| UI | Jetpack Compose + Material3 | BOM 2024.09.00 |
| Architecture | MVVM | — |
| DI | Hilt | 2.51.1 |
| Database | Room | 2.6.1 |
| Map | Osmdroid (OpenStreetMap) | 6.1.20 |
| Image loading | Coil | 2.7.0 |
| EXIF parsing | AndroidX ExifInterface | 1.3.7 |
| Async | Kotlin Coroutines + Flow | 1.8.1 |
| Build tools | AGP 8.5.2 / Gradle 8.10.2 / KSP 1.9.25-1.0.20 | — |

> **Note:** The `kotlin-compose` Gradle plugin is NOT used — incompatible with Kotlin 1.9.x. Compose is enabled via `composeOptions { kotlinCompilerExtensionVersion = "1.5.15" }` in `app/build.gradle.kts`.

---

## Project Structure

```
app/src/main/kotlin/com/ramdan/MyKisah/
├── MyKisahApp.kt                      # @HiltAndroidApp Application class
├── MainActivity.kt                    # Entry point; permission handling via ActivityResultContracts
├── data/
│   ├── local/
│   │   ├── ExifParser.kt              # Parses GPS float[] + DateTimeOriginal from EXIF
│   │   ├── MediaStoreReader.kt        # Queries DCIM/Camera via MediaStore (async)
│   │   ├── MyKisahDatabase.kt         # Room database definition
│   │   ├── PhotoLocationDao.kt        # DAO; getAllChronological() sorted by timestamp ASC
│   │   └── PhotoLocationEntity.kt     # Room entity: id, imageUri, lat, lng, timestamp
│   └── repository/
│       └── PhotoLocationRepository.kt # Orchestrates MediaStore → EXIF → Room → Flow
├── di/
│   └── DatabaseModule.kt              # Hilt @Module: provides Room DB + DAO
├── domain/model/
│   └── PhotoLocation.kt               # Domain model with dateFormatted + timeFormatted helpers
└── presentation/
    ├── MainViewModel.kt               # StateFlow<MainUiState>; exposes syncPhotos()
    ├── MainScreen.kt                  # Scaffold: TopBar + Map (40%) + Timeline (60%)
    ├── map/
    │   └── JourneyMapView.kt          # AndroidView wrapping Osmdroid; amber polyline; auto-zoom BoundingBox
    ├── permissions/
    │   └── PermissionScreen.kt        # Permission gate UI (READ_MEDIA_IMAGES + ACCESS_MEDIA_LOCATION)
    ├── theme/
    │   ├── Color.kt                   # Sand/Amber palette (light) + Ink/Amber (dark)
    │   └── Theme.kt                   # MyKisahTheme; light+dark colorScheme; FontFamily.Serif fallback
    └── timeline/
        └── TimelineList.kt            # LazyColumn; vertical connector line; Coil thumbnail; tap → map focus

app/src/main/res/
├── values/
│   ├── strings.xml
│   └── themes.xml                     # parent: Theme.MaterialComponents.DayNight.NoActionBar
└── xml/
    └── network_security_config.xml    # Allows cleartext for tile.openstreetmap.org
```

---

## Data Flow

```
MainActivity
  └─ checks permissions
       ├─ denied  → PermissionScreen
       └─ granted → MainScreen
                      └─ user taps Sync
                           └─ MainViewModel.syncPhotos()
                                └─ MediaStoreReader → list of URIs (DCIM/Camera)
                                     └─ ExifParser.parse(uri) → GPS float[] + DateTimeOriginal
                                          └─ PhotoLocationEntity → Room.insertAll()
                                               └─ dao.getAllChronological() : Flow
                                                    └─ MainUiState.photos (StateFlow)
                                                         ├─ JourneyMapView: polyline + markers, auto-fit BoundingBox
                                                         └─ TimelineList: card per photo, tap → selectPhoto(index) → map.animateTo()
```

---

## Android Manifest — Required Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.ACCESS_MEDIA_LOCATION" />
```

The `<application>` tag must include:

```xml
android:usesCleartextTraffic="true"
android:networkSecurityConfig="@xml/network_security_config"
```

---

## Build Configuration Notes

### `gradle/libs.versions.toml`
- All dependency versions are centralized here via Version Catalog.
- `google-material = "1.12.0"` is required because `themes.xml` uses `Theme.MaterialComponents` as parent.

### `gradle.properties`
```properties
android.useAndroidX=true
android.enableJetifier=true
android.suppressUnsupportedCompileSdk=35
```

### `gradle/wrapper/gradle-wrapper.properties`
```properties
distributionUrl=https://services.gradle.org/distributions/gradle-8.10.2-bin.zip
```

---

## Known Issues & Workarounds

| Issue | Status | Workaround |
|---|---|---|
| `ACCESS_MEDIA_LOCATION` not granted | ⚠️ Runtime | GPS reads as (0,0) on Android 10+; must be granted explicitly |
| Sync is full clear-then-reinsert | ⚠️ TODO | For incremental sync: add `syncedAt` column + compare `DATE_MODIFIED` |
| Custom fonts not bundled | ⚠️ TODO | Theme.kt falls back to `FontFamily.Serif/SansSerif`; add TTF to `res/font/` and reference via `Font(R.font.xxx)` |
| `PermissionGate()` composable is a stub | ℹ️ By design | Permission logic lives in `MainActivity` via `ActivityResultContracts` |
| `getLatLong(FloatArray)` deprecated | ⚠️ Warning | `ExifParser.kt:26` — still functional; migrate to `latLong` attribute in future |
| `statusBarColor` setter deprecated | ⚠️ Warning | `Theme.kt:67` — cosmetic only; no behavior impact |

---

## Build History — Errors Resolved

1. `gradle-9.4.1` download timeout → downgraded to `gradle-8.10.2`
2. `Unresolved reference: kotlin.compose` plugin → removed plugin; use `composeOptions` block instead
3. `android.useAndroidX not enabled` → added to `gradle.properties`
4. `resource attr/colorPrimaryVariant not found` → changed `themes.xml` parent to `Theme.MaterialComponents.DayNight.NoActionBar` + added `google.material:1.12.0`
5. `Unresolved reference: animateFloatAsState` → replaced with `rememberInfiniteTransition` + `animateFloat`
6. `Unresolved reference: R.font.xxx` → removed custom font declarations; using system fallback
7. `Unresolved reference: permissions` (Accompanist) → removed import; Accompanist not in dependencies

---

## Planned Features

- [ ] Filter timeline by date range
- [ ] Marker clustering for large photo sets (Osmdroid `MarkerClusterer`)
- [ ] Photo detail screen (full image + coordinates + map snippet)
- [ ] Export journey as PDF / share
- [ ] Incremental sync (no clear-all on each sync)
- [ ] Bundle custom fonts: DM Serif Display + Inter
- [ ] Osmdroid offline tile caching configuration

---

## AI Context Notes

> This section is for AI assistants reading this file.

- **Do not suggest** adding the `kotlin-compose` Gradle plugin — it is intentionally omitted (incompatible with Kotlin 1.9.x).
- **Do not suggest** Accompanist permissions library — it is not in the dependency tree.
- The app uses `AndroidView` to embed Osmdroid inside Compose — this is intentional since Osmdroid has no native Compose API.
- Room entities use `Long` for `timestamp` (Unix epoch ms), converted to human-readable format in the domain model layer (`PhotoLocation.dateFormatted`, `PhotoLocation.timeFormatted`).
- All database operations go through `PhotoLocationRepository`, not directly through the DAO from the ViewModel.
- Hilt modules are in `di/DatabaseModule.kt`; no other DI modules exist currently.
- The `PermissionGate()` composable in `PermissionScreen.kt` is a UI-only stub — actual permission requests are handled in `MainActivity` using `ActivityResultContracts.RequestMultiplePermissions`.
