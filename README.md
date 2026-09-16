# OpenLingo 🦫

OpenLingo is a 100% free, open-source, native Android app for learning Spanish (CEFR A1 → B1) and Japanese (JLPT N5 → N4). It works fully offline: no account, no ads, no billing, no tracking, and no `INTERNET` permission at all. Your progress lives entirely on your device — and a chill capybara keeps you company along the way.

## Features

- Offline curricula: 20 units, 38 lessons, 123 challenges across Spanish and Japanese
- Bundled Kokoro-82M TTS audio (Ogg) — every word and phrase is pronounceable, no network needed
- Winding S-curve lesson map with golden crowns per completed unit
- Hearts with an instant free refill, plus a daily refill at midnight
- Daily quests (30 XP goal) and streaks with streak repair after a missed day
- Spaced-repetition mistakes review (Practice tab)
- Kana tracing for all 46 hiragana + 46 katakana, plus 60-second Kana Blitz
- 4 theme accents: Onsen Teal, Matcha Green, Sakura Pink, Yuzu Citrus
- Home-screen widget showing your streak and daily quest progress
- Optional 7 PM offline streak reminder
- JSON backup export/import for moving progress between devices — no cloud required
- Fully offline: no `INTERNET` permission, no telemetry, nothing leaves your device

## Curriculum

Each course is organized into 10 units that unlock in sequence along the S-curve map:

- **Spanish** — CEFR A1 → B1: greetings and everyday phrases, food and routines, city and travel, shopping and money, health, past tenses and narrative
- **Japanese** — JLPT N5 → N4: kana, greetings, loanwords and katakana, daily verbs, counters and kanji basics, te-forms, potential/ability, politeness

Every lesson mixes exercise types: multiple choice, assisted translation, word bank assembly, match pairs, listening, and dialogue comprehension.

## Tech stack

- Kotlin + Jetpack Compose (Material 3)
- Room (v8, with data-preserving migrations)
- Media3 ExoPlayer for audio playback
- KotlinX Serialization for progress backups
- AlarmManager for the midnight reset and 7 PM reminder
- Pure-JVM test suite (Robolectric + in-memory Room)

There is no network stack at all — the app does not request the `INTERNET` permission.

## Repository layout

| Path | What it is |
|------|------------|
| `duo-android/` | The Android app (Gradle project `:app`) |
| `docs/` | Architecture, content-sourcing, and Kokoro TTS docs |
| `duo-android/docs/CURRICULUM_B1_N4_ROADMAP.md` | Roadmap toward full CEFR B1 / JLPT N4 coverage |
| `duo-android/fastlane/` | Google Play store metadata |

## Getting started

Prerequisites: **JDK 17** and **Android SDK 36**.

1. Open `duo-android/` in Android Studio, or use your own Gradle ≥ 9.x (the repo doesn't ship a wrapper; the wrapper path on a dev machine is machine-specific).
2. Build: `gradle :app:assembleDebug`
3. Run the tests: `gradle :app:testDebugUnitTest`
4. Install `duo-android/app/build/outputs/apk/debug/app-debug.apk` on a device or emulator.
On first launch the APK seeds the full curriculum locally — there is nothing to download.

## Building a release APK

```bash
gradle :app:assembleRelease
```

Release signing reads `duo-android/keystore.properties`, which is gitignored and must **never** be committed. It holds `storeFile`, `storePassword`, `keyAlias`, and `keyPassword`. If the file is absent, the build transparently falls back to the debug key — fine for local testing, but not for Play Store uploads.
The signed release APK lands in `duo-android/app/build/outputs/apk/release/`.

## Progress backup & restore

There is no cloud account and no device sync service. Instead, Settings offers:

- **Export** — shares a pretty-printed JSON file of your full progress (points, streak, completed challenges, kana mastery, mistakes, daily activity)
- **Import** — paste that JSON back (e.g. on a new device) to restore your progress.

This is the portability mechanism that replaces any server-side sync.

## Documentation

- [Android architecture](docs/android-architecture.md)
- [Content sourcing pipeline](docs/content-sourcing.md)
- [Kokoro TTS evaluation](docs/kokoro-tts.md)
- [Curriculum B1/N4 roadmap](duo-android/docs/CURRICULUM_B1_N4_ROADMAP.md)

## Privacy

There is no account, no analytics, and no tracking of any kind. Because the app requests no `INTERNET` permission, nothing can ever leave your device except what you explicitly share through the JSON export.

## License

MIT — see [LICENSE](LICENSE).
