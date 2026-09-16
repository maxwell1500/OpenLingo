# OpenLingo — Android Architecture

## 1. Overview

OpenLingo is a single-Activity, Jetpack Compose Android app that runs 100% offline. It has no network stack and does not request the `INTERNET` permission: all content (curriculum, audio, icons) ships inside the APK, and every mutable state lives in a local Room database.

State flows in one direction:

```
┌─────────────────────────────────────────────────────────────┐
│  Compose UI (MainActivity + screens + components)          │
│           ▲ collect                                         │
│           │ StateFlow                                       │
│  MainViewModel ──── reads ──── LocalProgressRepository     │
│                                  │  (suspend / Flow ops)    │
│                                  ▼                          │
│                      DuoDatabase (Room v8)                 │
│     courses · units · lessons · challenges ·               │
│     challenge_options · user_progress ·                    │
│     challenge_progress · character_mastery ·               │
│     mistakes · daily_activity                              │
└─────────────────────────────────────────────────────────────┘
```

`DuoApplication.onCreate` kicks everything off: it calls `LocalProgressRepository.initializeIfNeeded()` (seed + day rollover) and arms the `AlarmManager` midnight reset.

## 2. Persistence (Room v8)

`DuoDatabase` (`duo_local.db`) exposes ten tables:

| Table | Entity | Purpose |
|-------|--------|---------|
| `courses` | `CourseEntity` | The two language courses (id 1 = Spanish, id 2 = Japanese) |
| `units` | `UnitEntity` | 20 units (10 per course), each with a theme and `orderIndex` |
| `lessons` | `LessonEntity` | 38 lessons, ordered within a unit |
| `challenges` | `ChallengeEntity` | 123 challenges (`SELECT`, `ASSIST`, `WORD_BANK`, `LISTEN`, `MATCH_PAIRS`), each with an optional `audioSrc` |
| `challenge_options` | `ChallengeOptionEntity` | Choices/word-bank fragments with `correct` flags, optional romaji and audio |
| `user_progress` | `UserProgressEntity` | Single local guest profile (`guest_local`): points, hearts, streak, `brokenStreak`, `activeCourseId`, sound/haptics/romaji settings, `themeAccent` |
| `challenge_progress` | `ChallengeProgressEntity` | Per-challenge completion for the guest user |
| `character_mastery` | `CharacterMasteryEntity` | Kana tracing progress: attempts + `masteredAt` per character and script |
| `mistakes` | `MistakeEntity` | Wrong answers awaiting review, one row per challenge |
| `daily_activity` | `DailyActivityEntity` | XP per calendar date (`yyyy-MM-dd`), backs the daily quest |

Migrations preserve user data (all additive — `ADD COLUMN` / `CREATE TABLE IF NOT EXISTS`):

- `4 → 5` creates `character_mastery` and `mistakes`
- `5 → 6` adds `soundEnabled`, `hapticsEnabled`, `onboardingSeen` to `user_progress`
- `6 → 7` adds `brokenStreak` and creates `daily_activity`
- `7 → 8` adds `themeAccent` (default `TEAL`) — current version

## 3. Curriculum seeding

All curriculum is compiled into the app as Kotlin data — there are no bundled JSON/SQL fixtures.

- `LocalProgressRepository.initializeIfNeeded()` ensures the `guest_local` user exists and seeds the **basic A1 units** (Spanish units 1–2, Japanese units 11–12) inline via `seedSpanishCourse()` / `seedJapaneseCourse()`.
- The remaining units come from compiled `UnitPayload` lists in `data/local/curriculum/`:
  - `ExpandedCurriculumData` — units 3–5 (`spanishExpandedUnits`, `japaneseExpandedUnits`)
  - `AdvancedCurriculumData` — units 6–8 (`spanishAdvancedUnits`, `japaneseAdvancedUnits`)
  - `B1CurriculumData` — units 9–10 (`spanishB1Units`, `japaneseB1Units`)
- Each `UnitPayload` bundles a `UnitEntity` with its `lessons`, `challenges`, and `options`.
- Totals: **20 units / 38 lessons / 123 challenges**.
- Inserts use REPLACE-on-conflict, so newly added lessons/challenges roll out to existing installs without a wipe — no migration needed for content growth.

## 4. Audio

`AudioPlayer` (`audio/AudioPlayer.kt`) plays Kokoro-82M generated Ogg files bundled in `app/src/main/assets/audio/{es,ja}/` through Media3 ExoPlayer (`MediaItem.fromUri("asset:///…")`), with adjustable playback speed (the 🐢 slow button). If a referenced asset is missing, it falls back to the platform `TextToSpeech` engine, guaranteeing 100% audio coverage offline.

## 5. Progress model

- **Points**: 10 XP per correct challenge (`POINTS_PER_CHALLENGE`), +5 perfect-lesson bonus (`PERFECT_BONUS`).
- **Hearts**: max 5 (`MAX_HEARTS`); losing a heart is the only cost for a wrong answer. Hearts refill freely on demand (hearts pill) and refill to full on every new calendar day (`refreshDailyState`).
- **Streaks**: maintained per calendar day from `lastActiveDate`. Missing a full day stashes the old streak in `brokenStreak` and resets to 1; completing a lesson while `brokenStreak > 0` repairs it (streak repair).
- **Daily activity**: XP is recorded per day in `daily_activity`; the daily quest is a 30 XP goal (`DAILY_QUEST_XP`) read straight from that table.

## 6. Portability (no cloud)

There is no account system and no sync service. Progress moves between devices via a JSON backup:

- `OpenLingoBackup` (`data/local/models/OpenLingoBackup.kt`, KotlinX Serialization) with exactly these fields:
  - `version: Int` (currently 1)
  - `exportedAt: Long`
  - `userProgress: UserProgressBackup?` — `points`, `hearts`, `streak`, `lastActiveDate`, `showRomaji`, `soundEnabled`, `hapticsEnabled`, `themeAccent`
  - `completedChallengeIds: List<Int>`
  - `characterMastery: List<CharacterMasteryBackup>` — `character`, `script`, `attempts`, `masteredAt`
  - `mistakes: List<MistakeBackup>` — `challengeId`, `lessonId`, `timestamp`
  - `dailyActivity: List<DailyActivityBackup>` — `date`, `xp`
- **Export** serializes with `BackupJson` (pretty-printed, unknown keys tolerated, defaults encoded) and hands the JSON to the system share intent. **Import** accepts pasted JSON and upserts it back into Room.
- This file-based round-trip replaces the removed Clerk/Next.js web sync.

## 7. Background work

All alarms are local `AlarmManager` `RTC_WAKEUP` intents — no FCM, no network:

- `DailyResetReceiver` — fires at midnight, runs `refreshDailyState` (streak roll, heart refill), and re-arms itself for the next midnight.
- `StreakReminderReceiver` — fires at 7 PM as an offline streak nudge.
- `BootRescheduleReceiver` — listens for `BOOT_COMPLETED` and re-arms both alarms after a reboot.

## 8. Widget

`OpenLingoWidgetProvider` (`widget/`) renders the home-screen widget: current streak and daily quest XP progress, refreshed directly from Room.

## 9. Theming

`ThemeAccent` (`ui/theme/Theme.kt`) defines four Material 3 accents — `TEAL` (default, "Onsen Teal 🦫"), `MATCHA`, `SAKURA`, `YUZU` — each with primary/secondary colors. The selection is persisted in `user_progress.themeAccent` and applied app-wide through the `DuoTheme` composable.

## 10. Tests

The test suite is pure JVM (Robolectric + in-memory Room; no emulator):

- `CurriculumIntegrityTest` — global ID uniqueness, foreign-key resolution, contiguous `orderIndex` per unit/lesson, answerability (every challenge solvable, choice challenges have distractors), every referenced audio file exists in assets, and kana syllabaries are complete.
- `LocalProgressRepositoryTest` — seeding, day rollover, streaks/repair, hearts, and backup export/import round-trip.
- `AchievementsTest` — achievement unlock logic.

## 11. Build & signing

- Gradle (wrapper ≥ 9.x on dev machines), JDK 17, `compileSdk 36`, `minSdk 26`.
- Debug builds sign with the standard debug key.
- Release builds read `duo-android/keystore.properties` (gitignored; `storeFile`, `storePassword`, `keyAlias`, `keyPassword`). Without it, the build falls back to the debug key — suitable for local testing only.
- `duo-android/fastlane/` holds the Google Play store metadata.
