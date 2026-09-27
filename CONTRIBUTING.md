# Contributing to OpenLingo

Thanks for taking the time to contribute! Read this short guide before opening a pull request.

## Code of conduct

This project follows the [OpenLingo Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold it.

## Building the app

- JDK 17, Android SDK 36, and nothing else: the Gradle wrapper is committed and pinned to **Gradle 9.6.0**, so always invoke `./gradlew` from `duo-android/` rather than a system `gradle`.
- Build: `./gradlew :app:assembleDebug`
- Tests: `./gradlew :app:test --rerun-tasks` (pure JVM — Robolectric + in-memory Room, no device needed)

`--rerun-tasks` is not optional: a warm build cache makes Gradle report the test task `UP-TO-DATE`, run **zero** tests and still print `BUILD SUCCESSFUL`, which is indistinguishable from a real green run. Read the actual totals from the JUnit XML in `app/build/test-results/testDebugUnitTest/` rather than the console summary. Note that this directory retains a result file for any test class that existed at the last run, so a class deleted or merged since then lingers in it — cross-check the class count against `app/src/test/` rather than trusting the number of XML files.

Run the test suite before pushing. The curriculum integrity tests in particular will catch ID collisions, dangling foreign keys, and missing audio files.

## Adding curriculum content

- Lesson content lives in compiled Kotlin data classes under `duo-android/app/src/main/java/com/duo/app/data/local/curriculum/` (`ExpandedCurriculumData.kt`, `AdvancedCurriculumData.kt`, `B1CurriculumData.kt`). The basic A1 units are seeded inline in `LocalProgressRepository.kt`.
- Keep entity IDs globally unique and `orderIndex` contiguous within each unit and lesson.
- Audio: generated Kokoro-82M Ogg files go in `duo-android/app/src/main/assets/audio/{es,ja}/` and are referenced as `asset:///audio/{lang}/{name}.ogg`.
- New content is seeded on first launch, and re-seeded with REPLACE-on-conflict on later launches, so adding lessons does not require a database migration.

## Creating a release keystore (local only)

Signing secrets live in `duo-android/keystore.properties`, which is gitignored. **Never commit the keystore, the `.jks`, or its passwords.**

From `duo-android/`, generate the keystore at exactly the path the build expects (`keytool` prompts you for the passwords interactively):

```bash
keytool -genkeypair -v -keystore keystore/openlingo-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 -alias openlingo
```

Then create `duo-android/keystore.properties` — all four keys, with `storeFile` **relative to `duo-android/`**:

```properties
storeFile=keystore/openlingo-release.jks
storePassword=<the password you gave keytool>
keyAlias=openlingo
keyPassword=<the password you gave keytool>
```

`storeFile` must point at the file you just created. The build resolves it relative to `duo-android/`, so a path written relative to your home directory, or a stale `.jks` left over from an earlier attempt, will either fail the build or silently sign with the wrong key.

Verify it with `./gradlew :app:assembleRelease`; the APK lands in `duo-android/app/build/outputs/apk/release/`.

With no `keystore.properties`, `:app:assembleRelease` **fails** rather than falling back to the debug key — a debug-signed `app-release.apk` has the same name and path as a real one, so the mistake would otherwise only surface when somebody installed it. `:app:assembleDebug` and `:app:test` need no keystore and keep working.

To produce a throwaway local release build signed with your machine's debug key, opt in explicitly:

```bash
./gradlew :app:assembleRelease -PallowDebugSigning
```

The build prints a warning. Never upload or share that artifact, and never use it to overwrite a real installation — debug keys are per-machine, so updates over it will fail.

## Commit style

Use conventional commits: `feat:`, `fix:`, `docs:`, `chore:`.

## Pull requests

- Open an issue first for big changes so the design gets a second pair of eyes.
- Keep PRs focused — one concern per PR.
- Keep the app 100% offline: no new `INTERNET` permission, no network calls, no accounts.
