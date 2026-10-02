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
│                      DuoDatabase (Room v15)                │
│     courses · units · lessons · challenges ·               │
│     challenge_options · user_progress ·                    │
│     challenge_progress · character_mastery ·               │
│     mistakes · daily_activity · checkpoint_scores ·         │
│     vocab_schedule · exercise_type_stats                    │
└─────────────────────────────────────────────────────────────┘
```

`DuoApplication.onCreate` kicks everything off: it calls `LocalProgressRepository.initializeIfNeeded()` (seed + day rollover) and arms the `AlarmManager` midnight reset.

## 2. Persistence (Room v15)

`OpenLingoDatabase` (`openlingo_local.db`) exposes thirteen tables:

| Table | Entity | Purpose |
|-------|--------|---------|
| `courses` | `CourseEntity` | The two language courses (id 1 = Spanish, id 2 = Japanese) |
| `units` | `UnitEntity` | 36 units (18 per course), each with a theme and `orderIndex` |
| `lessons` | `LessonEntity` | 88 lessons, ordered within a unit |
| `challenges` | `ChallengeEntity` | 686 challenges (`SELECT`, `ASSIST`, `WORD_BANK`, `LISTEN`, `MATCH_PAIRS`, `STORY`, `CONJUGATE`, `FILL_BLANK`), each with an optional `audioSrc` plus optional grammar columns (`grammaticalFocus`, `ruleText`, `acceptedAnswers`) and a `heldOut` flag that keeps an item off the lesson path so only a checkpoint can reach it |
| `challenge_options` | `ChallengeOptionEntity` | Choices/word-bank fragments with `correct` flags, optional romaji and audio |
| `user_progress` | `UserProgressEntity` | Single local guest profile (`guest_local`): points, hearts, streak, `brokenStreak`, `activeCourseId`, sound/haptics/romaji settings, `themeAccent`, `themeMode`, `dailyQuestGoal` |
| `challenge_progress` | `ChallengeProgressEntity` | Per-challenge completion for the guest user |
| `character_mastery` | `CharacterMasteryEntity` | Kana tracing progress: attempts + `masteredAt` per character and script |
| `mistakes` | `MistakeEntity` | Wrong answers awaiting review, one row per challenge |
| `daily_activity` | `DailyActivityEntity` | XP per calendar date (`yyyy-MM-dd`), backs the daily quest |
| `checkpoint_scores` | `CheckpointScoreEntity` | Per-level checkpoint results (`A1`/`A2`/`B1`/`N5`/`N4`) as `correct`/`total` pairs |
| `vocab_schedule` | `VocabScheduleEntity` | Free Spaced Repetition Scheduler state per **seeded review card**, fully offline — a hand-written 14-row seed in `LocalProgressRepository.seedInitialVocabSchedule` (6 `es`, 8 `ja`), not a corpus table and not grown as units are added. It is the smallest of three vocabulary-ish tables; see §2.1 |
| `exercise_type_stats` | `ExerciseTypeStatsEntity` | Aggregate attempts/correct per exercise type, used to prioritise weak areas |

Migrations preserve user data (all additive — `ADD COLUMN` / `CREATE TABLE IF NOT EXISTS`):

- `4 → 5` creates `character_mastery` and `mistakes`
- `5 → 6` adds `soundEnabled`, `hapticsEnabled`, `onboardingSeen` to `user_progress`
- `6 → 7` adds `brokenStreak` and creates `daily_activity`
- `7 → 8` adds `themeAccent` (default `TEAL`)
- `8 → 9` adds `themeMode` (default `SYSTEM`)
- `9 → 10` creates `checkpoint_scores`
- `10 → 11` creates `vocab_schedule`
- `11 → 12` creates `exercise_type_stats`
- `12 → 13` adds `grammaticalFocus`, `ruleText`, `acceptedAnswers` to `challenges` and `errorTag` to `challenge_options`
- `13 → 14` adds `heldOut` to `challenges` (default `0`)
- `14 → 15` adds `dailyQuestGoal` to `user_progress` (default `30`) — current version

### 2.1 Which vocabulary count is which

The app has no single hand-written vocabulary list, so "how many words does it teach?" has three defensible answers. They are:

| Surface | What it counts | Count |
|---|---|---|
| Corpus headwords (`challenge_options` where `correct = 1`, keyed `text.trim().lowercase()`) | Every distinct target-language string at least one challenge marks as the correct answer. This is what `DictionaryIndex.build` indexes and what `UnitVocabularyIndex.key` compares against | **690 Spanish, 592 Japanese** (681 / 582 on a lesson path; 9 / 10 only in the held-out checkpoint pool) |
| Offline dictionary at runtime (`LocalProgressRepository.loadDictionary`) | The corpus headwords **plus** the 14 `vocab_schedule` `foreign` terms, which contribute 3 Spanish words no challenge marks correct (`hola`, `gracias`, `la cuenta, por favor`) | **693 Spanish, 592 Japanese** |
| Per-unit vocabulary list (`UnitVocabularyIndex.wordsByUnit`) | The distinct target-language strings the unit's own **lesson-path** correct options teach, grouped by the lesson that teaches each one. Deliberately **not** intersected with `vocab_schedule`: that table is a 14-row review seed and gating the list on it capped all 32 units at 3 Spanish and 8 Japanese words. Held-out checkpoint answers are excluded — a browsable study list must not print the answer the checkpoint exists to test | **681 Spanish, 582 Japanese** across the 36 units |

The published figure in `README.md` is the first row. The distinction is load-bearing rather than pedantic: only a `correct` option becomes a headword, because the Spanish seed offers `gracias` as the wrong answer to "Sí por favor" and indexing every option would define, authoritatively, a word the unit is teaching the learner to reject.

## 3. Curriculum seeding

All curriculum is compiled into the app as Kotlin data — there are no bundled JSON/SQL fixtures.

- `LocalProgressRepository.initializeIfNeeded()` ensures the `guest_local` user exists and seeds the **basic A1 units** (Spanish unit ids 10–11, Japanese unit ids 20–21) inline via `seedSpanishCourse()` / `seedJapaneseCourse()`.
- The remaining units come from compiled `UnitPayload` lists in `data/local/curriculum/`:
  - `ExpandedCurriculumData` — Spanish units 3–5, Japanese units 3–4 (`spanishExpandedUnits`, `japaneseExpandedUnits`)
  - `AdvancedCurriculumData` — Spanish units 6–8, Japanese units 5–8 (`spanishAdvancedUnits`, `japaneseAdvancedUnits`)
  - `B1CurriculumData` — Spanish units 9–14 and Japanese units 9–10 (`spanishA2Units`, `spanishB1Units`, `japaneseN4Units`). The file name is the roadmap workstream, not the level. Spanish units 9–10 (`spanishA2Units`, unit ids 18–19) teach regular preterite and imperfecto only, which is CEFR **A2**, so the checkpoint that draws them is labelled A2. Spanish units 11–16 (`spanishB1Units`, unit ids 30–35) carry the irregular and stem-changing preterite, the past perfect, the regular and irregular conditional, the polite periphrasis and the connectives of purpose, cause, result and concession (units 11–12), then the subjunctive, the imperative, the direct and indirect object pronouns, `gustar` and the reflexive verbs including impersonal `se` (units 13–14), then the subjunctive perfect and the unreal past, reported speech with and without tense backshift, and `por` against `para` (units 15–16). Twenty-one new `grammaticalFocus` slugs across the six units, each with an `ErrorHint.FocusProfile` and a declared set of honest `errorTag`s in `CurriculumIntegrityTest` — all of it the **B1** material, and all of it what the B1 checkpoint draws on. The Japanese units 9–10 (te-form, potential) are genuinely JLPT N4.
  - `SpanishVocabularyCurriculumData` — Spanish units 17–18 (`spanishVocabularyUnits`, unit ids 36–37, lessons 900–905, challenges `90000`+ and options `900000`+), appended to `spanishB1Units` from a separate object because folding 164 challenges into `B1CurriculumData` overflowed the JVM's 64 KB method limit and the object stopped compiling.
  - `JapaneseN4CurriculumData` — Japanese units 11–18 (`japaneseN4ExtensionUnits`, unit ids 40–47, lessons 400–405 and 406–412, then 800–805 and 850–855, challenges `60000`-`60043` plus the `LISTEN` block `61000`-`61008`, then `62000`-`62040` and the `LISTEN` block `61100`-`61106`). Units 11–12 add the three N4 points the corpus was missing outright: the past (ました / plain 断定形 / the ない-form negative), the plain-against-polite register pair, and the い/な adjective class distinction — then ability, opinion as 〜と思います, and the あげる / くれる / もらう trio with から and に. Ability has two routes and the unit teaches both: the potential on its own (乗れます, 食べられません, 歌えます) and the plain verb + ことができます (運転することができます), where こと is the する→す nominaliser and so the verb keeps its plain form — never a ます form. Units 13–14 add the passive (受身: the れる formation, the agent particle に, and 受け身 + ている), the causative (使役: the せ formation, させる plus て/ます) and the relative clause (修飾節: a plain-form clause bound to its noun with の). Units 15–16 add the three conditionals (・たら, ・なら, ・ば), the polite volitional (・ましょう), 尊敬語 and 謙譲語 as a minimal introduction, and the three readings of られる — potential, passive and 得る — with the lesson text stating outright that られる by itself never says which. Seven new `grammaticalFocus` slugs there, twenty-one across the six units, each with an `ErrorHint.FocusProfile` and a declared set of honest `errorTag`s in `CurriculumIntegrityTest`.
  - Audio is now present in these units. Spanish units 11–16 carry 148 Kokoro clips, units 17–18 add 18 more, and Japanese units 11–16 carry 132 with units 17–18 adding 33. The whole bundle is 395 — 196 `es` and 199 `ja` — every one referenced from a `*.kt` source and every reference resolving to a file on disk. A `SELECT` challenge and its correct option share a clip, and the one `FILL_BLANK` whose scaffold names the whole target sentence (60025, 車を運転することができます) carries that sentence's clip. The rules are in the two corpus files' headers. `CurriculumIntegrityTest` fails any item whose clip is not on disk, which is what keeps a typo'd `audioSrc` from shipping, and it now also checks that a typed …
- Each `UnitPayload` bundles a `UnitEntity` with its `lessons`, `challenges`, and `options`.
- Totals: **36 units / 88 lessons / 686 challenges** (664 on the lesson path, 22 held out for checkpoints). Held-out pools per checkpoint level: A2 4, B1 8, N4 10 — the new units add none. Per level: A1 8 units / 15 lessons / 48 challenges, A2 2 / 4 / 31, B1 8 / 25 / 328, N5 8 / 15 / 51, N4 10 / 29 / 227. Challenge mix: 212 `SELECT`, 110 `CONJUGATE`, 100 `LISTEN`, 101 `FILL_BLANK`, 69 `WORD_BANK`, 71 `MATCH_PAIRS`, 21 `STORY`, 2 `ASSIST`. 66 distinct `grammaticalFocus` slugs, each registered in both `ErrorHint.focusProfiles` and the integrity test's honest-tag map, and each with a learner-visible name in `GrammarFocus.DISPLAY_NAMES`. Audio: 395 bundled clips, 196 `es` and 199 `ja`, every one referenced and every reference resolving to a file.
- Inserts use REPLACE-on-conflict, so newly added lessons/challenges roll out to existing installs without a wipe — no migration needed for content growth.

## 4. Audio

`AudioPlayer` (`audio/AudioPlayer.kt`) plays Kokoro-82M generated Ogg files bundled in `app/src/main/assets/audio/{es,ja}/` through Media3 ExoPlayer (`MediaItem.fromUri("asset:///…")`), with selectable playback speed: the `AudioSpeedSelector` composable offers 0.5x / 0.75x / 1.0x (`AUDIO_SPEEDS`), replacing the deleted turtle slow button. If a referenced asset is missing, it falls back to the platform `TextToSpeech` engine, guaranteeing 100% audio coverage offline.

## 5. Progress model

- **Points**: 10 XP per correct challenge (`POINTS_PER_CHALLENGE`), +5 perfect-lesson bonus (`PERFECT_BONUS`).
- **Hearts**: max 5 (`MAX_HEARTS`); losing a heart is the only cost for a wrong answer. Hearts refill freely on demand (hearts pill) and refill to full on every new calendar day (`refreshDailyState`).
- **Streaks**: maintained per calendar day from `lastActiveDate`. Missing a full day stashes the old streak in `brokenStreak` and resets to 1; completing a lesson while `brokenStreak > 0` repairs it (streak repair).
- **Daily activity**: XP is recorded per day in `daily_activity`; the daily quest is a per-learner goal read straight from that table. The target is `user_progress.dailyQuestGoal` (default 30) and is set in Settings from `DAILY_QUEST_XP_OPTIONS` — 10 / 20 / 30 / 50 / 100 XP; `DAILY_QUEST_XP` survives only as the default for a row that has never set one.

## 6. Portability (no cloud)

There is no account system and no sync service. Progress moves between devices via a JSON backup:

- `OpenLingoBackup` (`data/local/models/OpenLingoBackup.kt`, KotlinX Serialization) with exactly these fields:
  - `version: Int` (currently 2)
  - `exportedAt: Long`
  - `userProgress: UserProgressBackup?` — `points`, `hearts`, `streak`, `lastActiveDate`, `showRomaji`, `soundEnabled`, `hapticsEnabled`, `themeAccent`, `themeMode`, `dailyQuestGoal`
  - `completedChallengeIds: List<Int>`
  - `characterMastery: List<CharacterMasteryBackup>` — `character`, `script`, `attempts`, `masteredAt`
  - `mistakes: List<MistakeBackup>` — `challengeId`, `lessonId`, `timestamp`
  - `dailyActivity: List<DailyActivityBackup>` — `date`, `xp`
  - `checkpointScores: List<CheckpointScoreBackup>` — `courseId`, `level`, `correct`, `total`, `timestamp`
  - `vocabSchedule: List<VocabScheduleBackup>` — `id`, `language`, `foreign`, `romaji`, `translation`, `audioSrc`, `category`, `difficulty`, `stability`, `reps`, `lapses`, `state`, `lastReview`, `due`
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

`ThemeAccent` (`ui/theme/Theme.kt`) defines four Material 3 accents — `TEAL` (default, "Onsen Teal 🦫"), `MATCHA`, `SAKURA`, `YUZU` — each with primary/secondary colors. The selection is persisted in `user_progress.themeAccent` and applied app-wide through the `OpenLingoTheme` composable. A separate `ThemeMode` (`SYSTEM` / `LIGHT` / `DARK`, default `SYSTEM`) is persisted in `user_progress.themeMode`.

## 10. Tests

The test suite is pure JVM — Robolectric, so no emulator — across 13 classes and 159 tests. Most classes use `Room.inMemoryDatabaseBuilder`, which builds the current schema directly and never runs a migration; `MigrationTest` instead writes real on-disk v12 and v14 database files and opens them through Room, so the migrations are exercised against a real upgrade:

- `CurriculumIntegrityTest` — global ID uniqueness, foreign-key resolution, contiguous `orderIndex` per unit/lesson, answerability (every challenge solvable, choice challenges have distractors), every referenced audio file exists in assets, and kana syllabaries are complete.
- `LocalProgressRepositoryTest` — seeding, day rollover, streaks/repair, hearts, and backup export/import round-trip.
- `AchievementsTest` — achievement unlock logic.
- `MigrationTest` — real on-disk upgrades: v12 → v15 through the whole chain, `MIGRATION_14_15` alone from v14, and `MIGRATION_12_13` alone from v12.
- `DictionaryIndexTest`, `DictionaryRepositoryTest` — the offline dictionary's index and its database-backed lookups.
- `UnitVocabularyIndexTest` — the per-unit vocabulary list joined to `vocab_schedule`.
- `FsrsSchedulerTest` — spaced-repetition scheduling.
- `AnswerGraderTest`, `ErrorHintTest`, `BlankScaffoldTest`, `GrammarFocusTest`, `StructureDrillTest` — grammar answer tolerance, error-specific hints, blank scaffolding, focus labels, and generated structure drills.

## 11. Build & signing

- Gradle 9.6.0 (pinned by the committed wrapper), JDK 17, `compileSdk 36`, `minSdk 26`.
- Debug builds sign with the standard debug key and need no keystore.
- Release builds read `duo-android/keystore.properties` (gitignored; `storeFile`, `storePassword`, `keyAlias`, `keyPassword`). Without it the release packaging tasks **fail** — a debug-signed `app-release.apk` is indistinguishable by name and path from a real one, so it is only ever produced by the explicit `./gradlew :app:assembleRelease -PallowDebugSigning` opt-in, which logs a warning and is not distributable.
- `duo-android/fastlane/` holds the Google Play store metadata.
