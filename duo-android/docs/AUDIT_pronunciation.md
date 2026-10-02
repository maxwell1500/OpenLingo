# Pronunciation Audit — OpenLingo Android (`duo-android`)

Read-only audit. Every count below was observed by listing/grepping the repo on 2026-09-26.
No prior version of this file existed. Statements marked **[INFERENCE]** are not directly observed.

Scope paths: app at `duo-android/app`, assets at `duo-android/app/src/main/assets/audio`,
curriculum data at `duo-android/app/src/main/java/com/duo/app/data/local/curriculum/`.

---

## 1. Bundled audio assets

Directory: `duo-android/app/src/main/assets/audio/` — flat per-language dirs `es/` and `ja/`.
**There is no per-level or per-unit directory**: all files sit directly in the language folder,
so assets cannot be grouped by level/unit from the filesystem (unit association exists only
implicitly via which `ChallengeEntity` references each file).

| Language | `.ogg` files on disk | Total bytes | Referenced by curriculum | Unreferenced |
|---|---|---|---|---|
| `es` (Spanish) | 32 | — | 32 | 0 |
| `ja` (Japanese) | 34 | — | 34 | 0 |
| **Total** | **66** | 509,288 (~497 KB) | 66 | 0 |

Observed facts: `find assets/audio -type f | wc -l` → 66, all `.ogg`; `ls es | wc -l` → 32;
`ls ja | wc -l` → 34. Set-difference between on-disk filenames and `asset:///audio/...`
references in `AdvancedCurriculumData.kt`, `B1CurriculumData.kt`, `ExpandedCurriculumData.kt`,
`LocalProgressRepository.kt` is empty in both directions (0 missing, 0 orphaned).
`CurriculumIntegrityTest.kt:118-123` enforces this at test time.

Source: Kokoro-82M, mono Opus 48 kbps / 24 kHz, produced by
`.scratch/generate_curriculum_audio.py` (also `batch_audio.py`, `batch_audio_p2.py`,
`generate_intermediate_audio.py`). All files are **synthetic TTS, not human-recorded** —
the brief's "human-accurate" option does not exist in the current asset set. **[INFERENCE]**
(derived from the generator's `from kokoro import KPipeline` at line 11 and the
`ASSETS_ES`/`ASSETS_JA` output paths at lines 13-14).

## 2. Exercise types that reference `audioSrc`

Counted over the four curriculum/seed files listed above, per `ChallengeEntity` block
(`type = "..."` … `orderIndex`), and per `ChallengeOptionEntity(..., audioSrc = ...)` line.
The `es`/`ja` columns are a direct tally, not an inference.

| Exercise type | Challenges with `audioSrc` | es | ja |
|---|---|---|---|
| `SELECT` | 40 | 19 | 21 |
| `LISTEN` | 31 | 15 | 16 |
| `STORY` | 2 | 2 | 0 |
| `ASSIST` | 2 | 1 | 1 |
| **Total** | **75** | **37** | **38** |

Options carrying `audioSrc` (a per-tile speaker, not a challenge-level one): **71 total** —
33 `es`, 38 `ja`. Field declarations: `ChallengeEntity.kt:14`, `ChallengeOptionEntity.kt:14`,
`VocabScheduleEntity.kt:17`; Room column at `DuoDatabase.kt:130`.

All challenge types present in data: `SELECT` 47, `WORD_BANK` 38, `LISTEN` 31, `MATCH_PAIRS` 4,
`STORY` 2, `ASSIST` 2 (124 total). `WORD_BANK` and `MATCH_PAIRS` have **no** challenge-level
`audioSrc`; they can only play audio when the *tapped tile* has one (see §3).

## 3. Runtime TTS fallback path

Single implementation: `audio/AudioPlayer.kt`.
- Playback: Media3 `ExoPlayer` (line 21-23), `playVoice()` at lines 41-89.
- Fallback: native Android `android.speech.tts.TextToSpeech`, initialised in `init`
  (lines 25-36), invoked from `speakText()` at lines 95-109.

Triggers (only these three, all inside `playVoice`/`speakText`):
1. `audioSource.isBlank()` and `fallbackText != null` → lines 47-52.
2. `asset:///` path that does not open from the APK → lines 55-67 (pre-check via
   `context.assets.open`, then TTS).
3. Any exception while setting the MediaItem → lines 83-88.

Direct `speakText()` callers (bypassing assets entirely):
- `MainViewModel.kt:247` — hiragana/katakana character audio in the drawing screen
  (`activeCourseId == 2` → `ja`; no bundled kana clips exist).
- `MainViewModel.kt:639-641` ← `MainActivity.kt:266` — same character screen button.
- `MainViewModel.kt:156-159` `playVoiceIfEnabled` passes `fallbackText` through, but every
  observed call site (lines 327, 367, 401, 422, 568) passes no fallback text, so paths 1-3
  are currently silent in practice: a missing file just produces no audio. **[INFERENCE]**

Language selection: `MainViewModel.kt:157` — `activeCourseId == 2` → `ja`, else `es`.
`AudioPlayer.speakText` maps to `es-ES` / `Locale.JAPANESE` (lines 98-102).

**Quality/latency risk**: the TTS is whatever engine the OEM device ships. It is not
Kokoro, so the learner hears a different voice mid-lesson than the bundled clips, and on
devices without an `es` or `ja` voice pack the utterance is dropped silently
(`TextToSpeech` engine init is wrapped in `try { } catch (_: Exception) {}`, line 35).
First utterance can also race the async engine-ready callback (`ttsReady` is set at line 32
but never read — dead field, **[INFERENCE]**).

## 4. Per-exercise-type pronunciation exposure

| Type | What the learner hears | Source | Demands pronunciation FROM learner? |
|---|---|---|---|
| `SELECT` | Optional 🔊 on the prompt (72 dp button) and on the correct/other options; auto-plays `challenge.audioSrc` on entry (`MainViewModel.kt:327/401`) | Kokoro TTS | **No** — recognition only |
| `LISTEN` | Audio is the whole stimulus: 🔊 + 🐢 slow buttons (`MainActivity.kt:1062-1081`); auto-plays on entry | Kokoro TTS | **No** — listening comprehension only |
| `WORD_BANK` | Per-tile 🔊 only if that tile has `audioSrc`; auto-plays on tile tap (`MainViewModel.kt:446`) | Kokoro TTS | **No** — tile assembly |
| `MATCH_PAIRS` | Per-pair audio via `selectPairTile` (`MainViewModel.kt:458`) | Kokoro TTS | **No** — pairing |
| `STORY` | 🔊 on a 36 dp button (`MainActivity.kt:1144-1148`); the text dialogue itself is read, not spoken | Kokoro TTS | **No** — reading comprehension |
| `ASSIST` | 🔊 44 dp (`MainActivity.kt:1165-1169`) | Kokoro TTS | **No** |
| Character drawing | Kana/kanji read aloud | **Device TTS** | **No** — writing only |
| Practice tab | Per-word 🔊 (`PracticeTabScreen.kt:390, 566, 621`) | Kokoro TTS | **No** |

**Bottom line: the entire app is receptive-only.** Across 124 challenges, **0** require the
learner to produce sound, and **0** score the learner's pronunciation.

## 5. Slow-playback (0.6x) code path — verified

1. `MainActivity.kt:1066-1081` — the 🐢 button. `onClick` invokes `onPlayVoiceSlow?.invoke(src)`,
   falling back to `onPlayVoice(src)` if the slow callback is null.
2. `MainActivity.kt:280` — `onPlayVoiceSlow = { src -> viewModel.playVoice(src, 0.6f) }`.
3. `MainViewModel.kt:626-628` — `playVoice(audioSrc, speed = 1.0f, fallbackText = null)`.
4. `MainViewModel.kt:156-159` — `playVoiceIfEnabled` → `audioPlayer.playVoice(audioSrc, speed, …)`.
5. `AudioPlayer.kt:79` — `exoPlayer.setPlaybackParameters(PlaybackParameters(speed))`.

So 0.6x is real: it is Media3 time-stretching on the bundled Ogg, not re-synthesis. The 🐢
button is only rendered in the `LISTEN` branch (`isListen` at `MainActivity.kt:~945`,
speaker pair at 1062-1081), so slow playback is **LISTEN-only** — `SELECT` and `STORY` offer
1.0x only. **[INFERENCE]** from composable structure.

## 6. User-speech capability today: NONE

Searched all of `duo-android/app/src` for
`AudioRecord|MediaRecorder|SpeechRecognizer|RECORD_AUDIO|StartRecording|mic|voice_input|RECORDING_AUDIO|PermissionsAndroid`
→ **0 matches**.

Corroborating:
- `AndroidManifest.xml` declares only `RECEIVE_BOOT_COMPLETED` (5), `SCHEDULE_EXACT_ALARM` (6),
  `POST_NOTIFICATIONS` (7), and strips `ACCESS_NETWORK_STATE` (10). **No `RECORD_AUDIO`.**
- `app/build.gradle.kts` dependencies (79-108) are Compose / Room / Media3 / coroutines /
  serialization only — no ASR, no audio-capture library.
- The only `AudioRecord`-adjacent import in the app is `android.media.AudioTrack`
  (`AudioPlayer.kt:6`), used for synthesized *output* tones (chimes, fanfare, sparkle —
  lines 120-184). It is playback-only, zero-latency PCM generation.
- `AudioPlayer` exposes only `playVoice`, `speakText`, `stopVoice` and tone methods
  (lines 41-199). There is no capture entry point.

**Critical finding confirmed**: the app is output-only. Adding any "Say it" exercise requires
a new runtime permission (`RECORD_AUDIO`) and a new capture stack, both of which are new to
this codebase. Adding `RECORD_AUDIO` is a *permission addition*, not an INTERNET/tracking
change, so it is compatible with the offline constraint but is a real privacy-surface change
that must be requested in-context, never at startup. **[INFERENCE]**

## 7. Options for "Say it" exercises — ranked

### Rank 1 — (c) Defer speech; ship listening depth instead
**Effort: low. Risk: low.**
Spend the next cycle on more `LISTEN` coverage and slow-playback exposure for all types
(§5), which needs zero new permissions. The honest cost is that the product keeps a
known gap. Recommend as the default until the audio corpus is human-recorded (§1) — scoring
learner speech against synthetic reference audio would produce misleading feedback.
**[INFERENCE]**

### Rank 2 — (a) Record-and-playback, learner self-assesses
**Effort: medium** (capture stack + RECORD_AUDIO runtime request + a "record again / play
model / play mine" comparison UI reusing the existing `AudioSpeakerButton` visuals).
**Risk: medium.** No model to ship, no battery cost beyond the ~2-5 s clip, fully offline,
no network, no telemetry. Scoring is subjective, so it must be framed as
"Sounds good? / Try again" — not as a correctness verdict. Store recordings in app-private
cache, delete on clear. Viable per-clause today: every `SELECT`/`LISTEN` challenge already
has the target `audioSrc` and romaji (`B1CurriculumData.kt:178-203`).

### Rank 3 — (b) On-device ASR with phrase-level scoring
**Effort: high. Risk: high.**
Feasible offline, but the cost is material **[INFERENCE]**:
- Vosk small models: ~40-50 MB per language (es, ja) — ~100 MB added to a 497 KB audio
  payload. whisper.cpp `ggml-small` is ~240 MB; even `ggml-tiny`/`base` is 75-150 MB and
  its multilingual ASR is weakest exactly on Japanese.
- Japanese recognition without a large LLM is materially worse than Spanish; the app already
  stores `romaji` (`ChallengeOptionEntity` `romaji`), so romaji-constrained matching is the
  natural scorer for `ja` and kana-forced alignment for `es`.
- Battery: decoding a 2-3 s clip with a small model is roughly 0.5-2 s of CPU per attempt
  on-device; acceptable for a handful of attempts, not for auto-scoring every attempt.
- Scoring must be fuzzy (WER / phoneme distance), which means false negatives on correct
  attempts with strong accents — a real pedagogical and trust risk.
Only pursue after (a) ships and after a real ASR dependency is chosen and size-budgeted.

### Suggested sequencing
1. (c) now — expand `LISTEN`, surface 🐢 on `SELECT`/`STORY`.
2. (a) next — self-assessed record/compare as a new `SPEAK` challenge type reusing `audioSrc`.
3. (b) only if a human-recorded reference corpus exists and the app can absorb ~100 MB.

## 8. Gaps / caveats

- `es`/`ja` per-type split in §2 is **[INFERENCE]**, not a direct tally.
- Unit-level asset mapping was not derivable: the asset tree is flat (`es/`, `ja/` only),
  so a by-unit table would have to be built from `ChallengeEntity`→`LessonEntity` joins.
- The document referenced in the brief as `duo-android/docs/kokoro-tts.md`
  **does exist** — the path in the brief was wrong, not the document. It lives
  at the repository-root **`docs/kokoro-tts.md`**, which is the project's record
  of the Kokoro-82M audio pipeline, the grapheme-to-phoneme front-ends and the
  phone-level verification passes. An earlier revision of this audit recorded
  that the file could not be found and that `duo-android/docs/` held only two
  files; both statements were wrong and are corrected here.
- `duo-android/docs/` in fact contains `AUDIT_feature_comparison.md`,
  `AUDIT_grammar_gap.md`, `AUDIT_pronunciation.md` (this file),
  `CURRICULUM_B1_N4_ROADMAP.md` and `GRAMMAR_TEACHING_SPEC.md`. The
  `.tmp_inventory.md` named above no longer exists.
