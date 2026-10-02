# Feature Gap Analysis: OpenLingo vs Duolingo

_Generated from repo ground truth. Every "we have" claim cites a file path in
`duo-android/app/src/main/java/com/openlingo/app/`. Every Duolingo-side claim is tagged
`[FEATURE]` and comes from product knowledge of the Duolingo mobile app, not from
this repository._

## Ground truth: what OpenLingo already ships (verified)

| Feature | Evidence |
|---|---|
| Winding S-curve lesson map, crowns | `MainActivity.kt:574` `LessonMapScreen`; `ui/screens/CharactersTabScreen.kt` |
| Multiple choice (`SELECT`) | `MainActivity.kt:1040` dispatch; `data/entities/ChallengeEntity.kt` |
| Assisted translation (`ASSIST`) | falls through to the `else -> "Translate this phrase"` branch at `MainActivity.kt:1044` — **no dedicated ASSIST renderer** |
| Word bank / sentence builder | `MainActivity.kt:945`, `1041` (`WORD_BANK`) |
| Match pairs | `MainActivity.kt:947`, `1042` region (`MATCH_PAIRS`) |
| Listening comprehension | `MainActivity.kt:946` (`LISTEN`), transcript fallback at `:954` |
| 0.6x slow audio button | `MainActivity.kt:280` `playVoice(src, 0.6f)` |
| Story mode | `MainActivity.kt:1043`, `1130` (`STORY`); data in `data/curriculum/B1CurriculumData.kt` |
| Hearts (5, free refill, daily refill) | `MainActivity.kt:146` `refillHearts()`, `:481-521` heart button; `data/repository/LocalProgressRepository.kt:119-121` midnight refill |
| Daily quests (user-set XP goal, default 30) | `LocalProgressRepository.kt:322-352` `getDailyQuestGoal()` / `getDailyQuest()`; rendered by `MainActivity.kt:629-654` (lesson map) and `OpenLingoWidgetProvider.kt`; set in `SettingsScreen` |
| Streaks + streak repair | `LocalProgressRepository.kt:118-123` broken-streak reset; `UserProgressEntity.brokenStreak` |
| Achievements | `ui/Achievements.kt:22-40` (9 achievements), rendered in `ui/screens/ProfileTabScreen.kt:185-190` |
| JSON backup v2 (checkpoints + FSRS) | `MainActivity.kt:313-314`; `data/models/OpenLingoBackup.kt` |
| FSRS-4.5 flashcards | `data/fsrs/FsrsScheduler.kt`; `data/local/dao/VocabScheduleDao.kt` |
| Checkpoint tests (A1/A2, N5/N4) | `MainActivity.kt:1806` `CheckpointScreen`; `data/local/dao/CheckpointScoreDao.kt` |
| Placement test | `MainActivity.kt:256` `onTakePlacement`; `MainViewModel.kt` `startPlacementTest` |
| Adaptive weakest-skill practice | `data/local/dao/ExerciseTypeStatsDao.kt`; `ui/MainViewModel.kt:206` `evaluateChallenges`; `ui/screens/PracticeTabScreen.kt` |
| Mistakes review | `data/local/dao/MistakeDao.kt`; `ui/screens/PracticeTabScreen.kt:685-688` |
| Kana tracing (46+46) | `ui/screens/CharactersTabScreen.kt`, `ui/components/StrokeDrawingCanvas.kt`, `data/character/HiraganaStrokes.kt` |
| Kana Blitz (60s) | `ui/screens/CharactersTabScreen.kt` |
| Romaji/furigana toggle | `MainActivity.kt:990-1003` |
| Home-screen widget | `widget/OpenLingoWidgetProvider.kt` |
| 7 PM offline streak reminder | `alarm/StreakReminderReceiver.kt` |
| Dark mode + 4 accents | `ui/theme/Theme.kt`; `MainActivity.kt:131` `ThemeAccent` |
| No `INTERNET` permission, MIT | `README.md:3`, `README.md:91` |

---

## Table A — Duolingo features OpenLingo does NOT have

`[FEATURE]` = Duolingo-side claim from product knowledge.

| # | Duolingo feature `[FEATURE]` | Our status | Why / effort if added |
|---|---|---|---|
| A1 | Lesson-intro screens with explicit grammar rules ("Tap what you see") | **Missing** | We have no grammar surface at all. A `GrammarNote` field on `ChallengeEntity` + one intro card composable. **M** |
| A2 | "Say it" / speaking exercises (mic + speech recognition) `[FEATURE]` | **Missing** | Requires `RECORD_AUDIO` + on-device ASR; no ASR bundled today (`audio/AudioPlayer.kt` is playback only). **L** |
| A3 | Per-unit vocabulary lists / "Words" browser | **Partial** | Vocabulary exists only implicitly in challenge options and in the FSRS table. A grouped-by-unit list screen off `VocabScheduleEntity` is cheap. **S** |
| A4 | In-app full sentence translation (tap a sentence, see translation) `[FEATURE]` | **Missing** | Needs a bidirectional sentence corpus. Storage is easy; authoring coverage is the cost. **M** |
| A5 | Leagues / leaderboards | **Excluded-by-policy** | Server-backed global ranking. Contradicts no-network/no-account/no-tracking. **N/A** |
| A6 | Friends, add-friend, social feed `[FEATURE]` | **Excluded-by-policy** | Requires accounts and a backend. **N/A** |
| A7 | Live conversation with AI characters | **Excluded-by-policy** | Cloud inference. Only viable as on-device, which the brief excludes. **N/A** |
| A8 | User-set daily goal (XP target 10/20/30/50) `[FEATURE]` | **Missing** | Our quest XP is a hardcoded constant (`LocalProgressRepository.kt:198`). Make it a user setting. **S** |
| A9 | "Content"/news feed, events, seasonal stories | **Excluded-by-policy** | Content delivery assumes a server. Could be shipped as bundled seasonal lesson packs instead. **M (as bundled)** |
| A10 | Typing / free-text production (write the sentence, not pick tiles) | **Missing** | We only ever assemble from tiles. Text input with accent-tolerant matching. **M** |
| A11 | Clan / group quests | **Excluded-by-policy** | Multiplayer. **N/A** |
| A12 | Placement test with CEFR-level result reporting | **Partial** | Test exists (`MainViewModel.kt:303`), but no persistent per-level placement score row. **S** |
| A13 | Listening dictation (type what you hear) `[FEATURE]` | **Missing** | Free-text input (A10) plus audio. **M** |
| A14 | Mistake-review that re-teaches with explanation, not just re-asks | **Partial** | `MistakeDao.kt` re-asks the raw item. Adding a per-mistake explanation field is **S**. |
| A15 | Audio speed control with more than two steps (0.5x–1.5x) `[FEATURE]` | **Partial** | Hardcoded `0.6f` at `MainActivity.kt:280`. A slider is **S**. |
| A16 | Offline lessons for the *paid* path, gated content | **Excluded-by-policy** | No billing, deliberately. Everything ships unlocked. **N/A** |
| A17 | Duo hearts purchase / gem store | **Excluded-by-policy** | No billing. Unlimited free refill (`refillHearts`) already covers the need. **N/A** |
| A18 | Push-notification re-engagement campaigns | **Excluded-by-policy** | Marketing. We already have a local 7 PM reminder. **N/A** |
| A19 | Practice-any-skill from a skill tree without unlocking | **Partial** | `PracticeTabScreen.kt` covers weakest-area practice; arbitrary skill selection is **S**. |
| A20 | Reports / long-term accuracy analytics per skill over time | **Partial** | `ExerciseTypeStatsDao` is cumulative-only; a time series is **M**. |
| A21 | Writing system lessons (kanji/kana stroke-order feedback as a graded exercise) | **Partial** | Tracing exists in the Characters tab but is not a scored lesson exercise. Wire into `ExerciseTypeStatsEntity`. **M** |
| A22 | Kana/character mnemonics, story-based memory aids `[FEATURE]` | **Missing** | Content authoring. **M** |
| A23 | Sentence-translation "speak this line" per story character | **Missing** | Overlaps A2. **L** |

---

## Table B — What OpenLingo could add that Duolingo does NOT have

Differentiator column names the OpenLingo-only edge being exploited.

| # | Feature | Differentiator | Effort |
|---|---|---|---|
| B1 | Offline grammar rule engine: structured `GrammarNote` (pattern, examples, exceptions) surfaced at lesson intro, on error, and in-context from the exercise being failed | Curriculum is MIT and editable JSON — Duolingo's grammar is server-side copy we cannot inspect or fork | **M** |
| B2 | In-context grammar hints driven by the learner's *actual* error: a wrong word-bank tile explains the specific rule that tile breaks | Our `MistakeEntity` + per-type stats give the error signal Duolingo does not surface to the learner | **M** |
| B3 | "Say it" with on-device recording and waveform playback, graded by an optional on-device ASR or self-rated | No mic/network today; bundled Kokoro audio makes the reference clip free | **L** |
| B4 | Offline dictionary / glossary: tap any word in any exercise, option, or story line to get gloss, POS, example sentence, audio | Full curriculum JSON is present on-device, so lookup is local and instant | **M** |
| B5 | Curriculum transparency: in-app viewer + editor for the raw curriculum JSON, and export/import of *curriculum* (not just progress), backed by `CurriculumIntegrityTest.kt` | Duolingo's content is closed; ours is a git-tracked MIT artifact | **M** |
| B6 | Focused structure drills: conjugation tables, te-form map, kana chart with audio, generated from curriculum data rather than hand-authored | Spanish verbs / Japanese conjugation are systematic; we can generate them instead of authoring them | **M** |
| B7 | Shadowing drills: play a line at 0.6x/1x, record, play back your take next to the reference | Builds the oral muscle Duolingo's tap-only `[FEATURE]` loop never trains | **M** (given B3) |
| B8 | Offline mini dictionary corpus (frequency-ranked, deinflected) shipped as an asset | Replaces Duolingo's server dictionary lookup `[FEATURE]` | **L** |
| B9 | Deterministic reproducible practice seeds: a shareable seed string that recreates an exact practice session | Privacy-preserving reproducibility; Duolingo has no shareable state without an account | **S** |
| B10 | Full local progress timeline with streak heatmap and per-skill accuracy history, exportable as JSON | We already own the data (`DailyActivityDao`, `ExerciseTypeStatsDao`) and can show it without a server | **S** |
| B11 | User-extensible curriculum: drop in a new `UnitPayload` JSON file and the app seeds a new unit | Duolingo users cannot add their own units; we can | **M** |
| B12 | Accessibility-first: TalkBack-tuned kana tracing, large-text mode, high-contrast themes, reduced-motion toggle | Offline, no telemetry constraints make this cheap to do well | **S** |

---

## Table C — Merged, ranked by learning value × effort

Learning value is scored on: does it move the learner from recognising to *producing* sentences?

| Rank | Item | Value | Effort | Why it's worth it |
|---|---|---|---|---|
| 1 | A10 Free-text production typing (replaces/augments tile assembly) | High | M | Tile assembly never requires recall — this is the single biggest gap in production |
| 2 | A1 + B1 Grammar notes at lesson intro | High | M | Explicit rule, then use; the core of grammar acquisition |
| 3 | A14 Mistake reviews with explanations | High | S | Turns logged errors into learning; cheap given `MistakeDao` |
| 4 | A3 Per-unit vocabulary list | High | S | Learners want to review a unit's words as a set; data already exists |
| 5 | B2 In-context error-specific hints | High | M | Feedback aimed at the actual mistake, not a generic "try again" |
| 6 | A4 In-app sentence translation | Med-High | M | The learner's own output needs checking, not just tapping |
| 7 | B6 Generated structure drills (conjugation/te-form/kana) | Med-High | M | Systematic language, systematically generated — high content-per-effort |
| 8 | B7 Shadowing drill (on B3 recording) | Med-High | M | Oral production; offline reference audio makes it free to author |
| 9 | A13 Listening dictation | Med | M | Ties B6-style typing to the audio we already ship |
| 10 | A8 User-set daily goal | Med | S | Personalises the quest loop; trivial once the constant is a setting |
| 11 | B5 Curriculum viewer / export | Med | M | The open-source promise, made tangible inside the app |
| 12 | B4 Offline dictionary / tap-to-lookup | Med | M | Removes the single biggest friction in self-study; fully offline |
| 13 | B10 Local progress timeline + heatmap | Low-Med | S | Retention and self-accountability without tracking |
| 14 | B12 Accessibility pass | Low-Med | S | Broadens the reachable audience; mostly polish |
| 15 | A15 Audio speed slider | Low | S | Two minutes of work, noticeable comprehension help |
| 16 | A21 Tracing as a scored exercise | Low-Med | M | Reuses existing stroke data inside the lesson loop |
| 17 | B9 Shareable practice seeds | Low | S | Nice-to-have, not learning-critical |
| 18 | B11 User-extensible curriculum units | Low | M | Powerful for the community, low direct learner value |
| 19 | A20 Time-series analytics | Low | M | Reporting, not learning |
| 20 | A19 Arbitrary skill selection | Low | S | Marginal over existing weakest-skill practice |
| 21 | A12 Persistent placement scores | Low | S | Small fidelity gap |
| 22 | A22 Mnemonics | Low-Med | M | Content-heavy, unproven offline payoff |
| 23 | A23 Speak story lines | Low | L | Depends entirely on A2 shipping first |
| 24 | B2/B3/B8 on-device ASR and corpus | Low (uncertain) | L | Only worth starting after B3's recorder is proven |

## Do not build

The following are **excluded by policy** and should stay excluded — the whole value
proposition of OpenLingo is a 100% offline app with no account, no `INTERNET`
permission, and no tracking (`README.md:3`, `README.md:91`). Building any of them
would not be a feature gap, it would be a fork of the product:

- **A5** leagues/leaderboards, **A6** friends/social, **A7** live AI conversation,
  **A11** clans, **A9** server-delivered content feed, **A18** push campaigns —
  all require a backend and an identity.
- **A16** paid/gated content and **A17** hearts/gems store — billing is excluded;
  the free unlimited heart refill (`LocalProgressRepository.kt:314-317`) already
  removes the need for hearts at all.
- **B3/B8** on-device ASR at production quality — a bundled ASR model is
  hundreds of MB; only prototype a small grammar-constrained recogniser.

Also do not build a Duolingo-style "engagement layer" (variable-reward
mechanics, streak-loss anxiety loops, notifications) beyond the quiet, local
reminder we already have (`alarm/StreakReminderReceiver.kt`). It is not what
makes this app different.
