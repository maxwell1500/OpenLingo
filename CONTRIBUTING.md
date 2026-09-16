# Contributing to OpenLingo

Thanks for taking the time to contribute! Read this short guide before opening a pull request.

## Code of conduct

This project follows the [OpenLingo Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold it.

## Building the app

- JDK 17, Android SDK 36, Gradle ≥ 9.x (the repo doesn't ship a wrapper; Android Studio's bundled Gradle works out of the box).
- Build: `gradle :app:assembleDebug`
- Tests: `gradle :app:testDebugUnitTest` (pure JVM — Robolectric + in-memory Room, no device needed)

Run the test suite before pushing. The curriculum integrity tests in particular will catch ID collisions, dangling foreign keys, and missing audio files.

## Adding curriculum content

- Lesson content lives in compiled Kotlin data classes under `duo-android/app/src/main/java/com/duo/app/data/local/curriculum/` (`ExpandedCurriculumData.kt`, `AdvancedCurriculumData.kt`, `B1CurriculumData.kt`). The basic A1 units are seeded inline in `LocalProgressRepository.kt`.
- Keep entity IDs globally unique and `orderIndex` contiguous within each unit and lesson.
- Audio: generated Kokoro-82M Ogg files go in `duo-android/app/src/main/assets/audio/{es,ja}/` and are referenced as `asset:///audio/{lang}/{name}.ogg`.
- New content is seeded on first launch, and re-seeded with REPLACE-on-conflict on later launches, so adding lessons does not require a database migration.

## Creating a release keystore (local only)

Signing secrets live in `duo-android/keystore.properties`, which is gitignored. **Never commit the keystore or its passwords.**

```bash
keytool -genkeypair -v -keystore openlingo-release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 -alias openlingo
```

Then create `duo-android/keystore.properties` locally:

```properties
storeFile=...
storePassword=...
keyAlias=openlingo
keyPassword=...
```

Without that file, release builds fall back to the debug key (fine for local testing).

## Commit style

Use conventional commits: `feat:`, `fix:`, `docs:`, `chore:`.

## Pull requests

- Open an issue first for big changes so the design gets a second pair of eyes.
- Keep PRs focused — one concern per PR.
- Keep the app 100% offline: no new `INTERNET` permission, no network calls, no accounts.
