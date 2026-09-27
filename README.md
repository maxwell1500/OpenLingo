# OpenLingo 🦫

OpenLingo is a 100% free, open-source, native Android app for learning Spanish (CEFR A1 → B1) and Japanese (JLPT N5 → N4). It works fully offline: no account, no ads, no billing, no tracking, and no `INTERNET` permission at all. Your progress lives entirely on your device — and a chill capybara keeps you company along the way.

## Features

- Offline curricula: 20 units, 38 lessons, 158 challenges across Spanish and Japanese
- **Production-first exercises**: 45.6% of the corpus (72 of 158 challenges) makes the learner type, assemble, or conjugate a target-language form instead of only recognising one from the options — word bank assembly, verb conjugation, and typed fill-in-the-blank
- **Grammar rule cards**: each grammar challenge can carry a short rule that is shown alongside the challenge and repeated in the wrong-answer feedback, so the reason an answer is right arrives at the moment the learner gets it wrong
- **Verb conjugation** (`CONJUGATE`): the options are inflected forms of a single word rather than translations, so the learner picks the right entry in the paradigm, not the right meaning
- **Typed fill-in-the-blank** (`FILL_BLANK`): the learner types the missing form into a sentence scaffold. Grading forgives a missing Spanish accent and folds the full-width characters a Japanese IME emits, while still rejecting wrong word order and wrong inflected forms
- **Listening in the target language**: all 31 listening challenges are answered in the language being learned, not with English glosses
- Bundled Kokoro-82M TTS audio (Ogg) — every word and phrase is pronounceable, no network needed
- **Checkpoint tests**: CEFR A1/B1 and JLPT N5/N4 level mastery tests with celebratory pass badges and auto-queueing of missed questions. Sessions are drawn from a held-out pool of sentences in the same grammar structures, none of which ever appear on a lesson path, so passing measures generalisation rather than recall of taught sentences
- **FSRS-4.5 spaced repetition**: Modern Free Spaced Repetition Scheduler algorithm for vocabulary review with 4-grade rating (Again, Hard, Good, Easy) and due-count badges
- **Per-unit vocabulary list**: the words a unit teaches as one browsable, listenable list grouped by topic, reachable from the unit header on the lesson map — the words were previously only implicit in challenge options and the FSRS table
- **Offline tap-to-lookup dictionary**: tap any word in a question, story line or option — or hold an answer — to get its definition, its audio, and the sentence it came from, with no network and no bundled dictionary asset. The headwords and glosses are the target-language answers the curriculum already teaches and the English the same challenge already pairs them with. A distractor is deliberately not a headword, so a word the unit is teaching the learner to reject never gets an authoritative definition
- **Generated structure drills**: each unit's conjugation tables are derived from the lessons it already has — no drill code, no separately authored content. A paradigm groups every form the unit teaches under one grammar point, rows carry a speaker, and `PRACTISE` quizzes one form at a time using the same comparison a lesson uses, so a missing Spanish accent is forgiven in both and a Japanese dakuten is in neither. Nothing in a drill touches your progress: it is a study surface, not an attempt
- **Error-specific in-context hints**: a wrong answer explains itself. The app knows which grammar error the option the learner picked encodes, and says so — "hablaron is the right tense but the wrong person or number" — then states the rule and the form that fills the slot, instead of "try again"
- **Mistake reviews that teach**: the mistakes you have made come back with the rule attached, shown *before* you re-attempt them, so a repeat mistake is a chance to learn the rule rather than a second guess
- **Your own daily goal**: set your daily XP quest to 10, 20, 30, 50 or 100 in Settings. It is a real per-user setting, it survives a backup and restore, and an app upgrade leaves every existing learner on exactly the quest they had
- **Adaptive difficulty**: Real-time accuracy tracking across every exercise type (`SELECT`, `ASSIST`, `WORD_BANK`, `LISTEN`, `MATCH_PAIRS`, `STORY`, `CONJUGATE`, `FILL_BLANK`) with weakest-area prioritization in practice and profile breakdown
- **Story mode**: Interactive narrative reading comprehension challenges with character dialogue, offline audio, and question prompts
- **Placement test**: Quick cross-curriculum assessment on first launch allowing experienced learners to test out of earlier levels
- Winding S-curve lesson map with golden crowns per completed unit
- Hearts with an instant free refill, plus a daily refill at midnight
- Daily quests and streaks with streak repair after a missed day
- Spaced-repetition mistakes review (Practice tab)
- Kana tracing for all 46 hiragana + 46 katakana, plus 60-second Kana Blitz
- Dark mode (System / Light / Dark) and 4 theme accents: Onsen Teal, Matcha Green, Sakura Pink, Yuzu Citrus
- Home-screen widget showing your streak and daily quest progress
- Optional 7 PM offline streak reminder
- JSON backup export/import (v2 with checkpoints and FSRS schedules) for moving progress between devices — no cloud required
- Fully offline: no `INTERNET` permission, no telemetry, nothing leaves your device

## Curriculum

Each course is organized into 10 units that unlock in sequence along the S-curve map:

- **Spanish** — CEFR A1 → B1: greetings and everyday phrases, food and routines, city and travel, shopping and money, health, past tenses and narrative
- **Japanese** — JLPT N5 → N4: kana, greetings, loanwords and katakana, daily verbs, counters and kanji basics, te-forms, potential/ability, politeness

Every lesson mixes exercise types: multiple choice, verb conjugation, word bank assembly, typed fill-in-the-blank, match pairs, listening, and dialogue comprehension. Two `ASSIST` challenges exist in the corpus, but neither carries the `___` blank their renderer looks for, so both currently play as ordinary multiple choice — the mechanic is implemented, its content is not written yet.

## Tech stack

- Kotlin + Jetpack Compose (Material 3)
- Room (v15, with data-preserving migrations MIGRATION_4_5 through MIGRATION_14_15)
- Media3 ExoPlayer for audio playback
- KotlinX Serialization for progress backups
- AlarmManager for the midnight reset and 7 PM reminder
- Pure-JVM test suite (Robolectric, in-memory Room, plus a real on-disk 12 → 14 migration upgrade)

There is no network stack at all — the app does not request the `INTERNET` permission.

## Repository layout

| Path | What it is |
|------|------------|
| `duo-android/` | The Android app (Gradle project `:app`) |
| `docs/` | Architecture, content-sourcing, and Kokoro TTS docs |
| `duo-android/docs/CURRICULUM_B1_N4_ROADMAP.md` | Roadmap toward full CEFR B1 / JLPT N4 coverage |
| `duo-android/docs/GRAMMAR_TEACHING_SPEC.md` | Spec for the grammar-teaching workstream: rule cards, `CONJUGATE`/`FILL_BLANK`, the held-out checkpoint pool, and per-item status |
| `duo-android/docs/AUDIT_grammar_gap.md` | Audit of what the app never taught grammatically, and what the spec grew out of |
| `duo-android/docs/AUDIT_pronunciation.md` | Audit of the Japanese kana/romaji and TTS pronunciation surface |
| `duo-android/docs/AUDIT_feature_comparison.md` | Feature-by-feature comparison against Duolingo, marking what is matched, exceeded, or absent |
| `duo-android/fastlane/` | Google Play store metadata |

## Getting started

Prerequisites: **JDK 17** and **Android SDK 36**. Nothing else — the Gradle wrapper is committed and pinned to **Gradle 9.6.0**, so the build no longer depends on whatever Gradle happens to be ambient on the machine (previously it tracked whatever `ubuntu-latest` shipped, underneath AGP 9.4.0).

1. Open `duo-android/` in Android Studio, or use the committed wrapper from `duo-android/`:
2. Build: `./gradlew :app:assembleDebug`
3. Run the tests: `./gradlew :app:test --rerun-tasks`
4. Install `duo-android/app/build/outputs/apk/debug/app-debug.apk` on a device or emulator.

`--rerun-tasks` matters: with a warm build cache Gradle reports the test task `UP-TO-DATE`, executes **zero** tests and still prints `BUILD SUCCESSFUL`, so an incremental run reads as a green run that tested nothing. For real totals, read the JUnit XML in `app/build/test-results/testDebugUnitTest/`, never the console summary. One caveat: that directory keeps a result file for every test class that existed at the last run, so a class deleted or merged since then lingers in it — cross-check the class count against `app/src/test/` rather than trusting the number of XML files.
On first launch the APK seeds the full curriculum locally — there is nothing to download.

## Building a release APK

```bash
cd duo-android
./gradlew :app:assembleRelease
```

Release signing reads `duo-android/keystore.properties`, which is gitignored and must **never** be committed. It holds `storeFile`, `storePassword`, `keyAlias`, and `keyPassword`; see [CONTRIBUTING.md](CONTRIBUTING.md#creating-a-release-keystore-local-only) for the `keytool` command and the exact file contents.

If that file is missing or incomplete, the build **fails loudly** instead of producing an artifact. This is deliberate: a debug-signed release keeps the same filename (`app-release.apk`) and the same output path as a real one, so a green build would hide a wrong signature until somebody tried to install it.

For a throwaway local build only, ask for the debug signature explicitly:

```bash
./gradlew :app:assembleRelease -PallowDebugSigning
```

That prints a loud warning, and the APK is signed with your machine's local debug key. It cannot be uploaded to Google Play and must not be shared or installed by anyone else — debug keys are per-machine, so updates over it will fail. `:app:assembleDebug` and `:app:test` need no keystore at all and are unaffected.

The signed release APK lands in `duo-android/app/build/outputs/apk/release/`.

## Progress backup & restore

There is no cloud account and no device sync service. Instead, Settings offers:

- **Export** — shares a pretty-printed JSON file of your full progress: points, hearts, streak, preferences, your daily goal, completed challenges, kana mastery, mistakes, daily activity, checkpoint scores, and the whole FSRS review schedule
- **Import** — paste that JSON back (e.g. on a new device) to restore your progress.

This is the portability mechanism that replaces any server-side sync.

## Documentation

- [Android architecture](docs/android-architecture.md)
- [Content sourcing pipeline](docs/content-sourcing.md)
- [Kokoro TTS evaluation](docs/kokoro-tts.md)
- [Curriculum B1/N4 roadmap](duo-android/docs/CURRICULUM_B1_N4_ROADMAP.md)
- [Privacy policy](PRIVACY.md)

## Privacy

There is no account, no analytics, and no tracking of any kind. Because the app requests no `INTERNET` permission, nothing can ever leave your device except what you explicitly share through the JSON export.

The full policy, including exactly which permissions are declared and why, what is stored on your device, and how to delete it, is in [PRIVACY.md](PRIVACY.md).

## License

MIT — see [LICENSE](LICENSE).
