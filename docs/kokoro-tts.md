# Kokoro TTS Evaluation Report

**Date:** 2026-09-13  
**Evaluated for:** Language-learning Android app (Spanish + Japanese lessons)  
**Model:** hexgrad/Kokoro-82M v1.0 (82M params, Apache-2.0)  
**Status:** ✅ Audio generation works — but see §8. **The phonemiser had
serious defects that this report originally did not detect**, because the
verification step measured the wrong stage of the pipeline. All of them are now
found, fixed and documented below.

**Second pass (2026-09-28):** a spec-vs-audio detector was built (§8.6),
hardened and re-run (§8.8); the census it required found curriculum defects
(§8.9); and the Spanish corpus now has a declared pronunciation, so §9 has been
rewritten — it previously said Spanish had none. **Read §8.8.6 before quoting
any green result in this document.**

**Third pass (2026-09-28):** the audio was **listened to** for the first time
(§10). A phone recogniser independent of Kokoro and misaki was run over all 397
shipped clips and asked which phones are actually present. Results: the
Japanese わ and っ fixes are **confirmed in the audio** (0 confirmed Japanese
audio defects); a **real Spanish defect** was found by listening and fixed
(/x/ heard as /ʃ/ in three `dixo`/`hizo`/`hice` clips); and **two of the three
instruments returned limits or nulls**, which are results. §8.7's cepstral
measurement is now **superseded and withdrawn** — the pipeline is
nondeterministic, which puts a hard ceiling under any pre-versus-post
distance. **Read §10 before quoting any green result in this document, and read
§9.6 for what is still unverified — which still begins and ends with nobody
having read a single declared reading.**

---

## 1. Language Support

### ✅ Spanish: YES
- **Voices:** `ef_dora` (F), `em_alex` (M), `em_santa` (M)
- **G2P path:** `misaki` phonemizer with espeak-ng `es` backend (`lang_code='e'`)
- **Evidence:** VOICES.md lists 3 Spanish voices; model card confirms v1.0 supports en/es/fr/hi/it/pt/ja/zh
- **Verified:** Generated 3.35s of Spanish audio with `ef_dora` (see §6), and
  a phone recogniser was later run over all 198 Spanish clips (§10.2). **The
  recogniser is a detector, not a judge**: it found a real `/x/` → `/ʃ/` defect.
  ~~It cannot hear /θ/ at all, so no ceceo/seseo conclusion is available from
  it in either direction (§10.4).~~ **Superseded by §11.2:** once that
  instrument was *calibrated* on espeak-ng's genuine interdental it came back
  at 52.1% recall against a 2.1% false-alarm rate, so it is not `θ`-blind — and
  the conclusion it could not previously supply is now available, and it is
  **negative**: the shipped audio does not contain an audible interdental
  (§11.2). No native speaker has reviewed any Spanish reading and none will
  (§9.6, §11.1).

### ✅ Japanese: YES
- **Voices:** `jf_alpha` (F, C+ grade), `jf_gongitsune` (F, C), `jf_nezumi` (F, C-), `jf_tebukuro` (F, C), `jm_kumo` (M, C-)
- **G2P path:** `misaki[ja]` (`lang_code='j'`) — `misaki.ja.JAG2P`, which uses
  its **own fugashi `Tagger` (cutlet)**, *not* pyopenjtalk. **This corrects an
  earlier statement in this report**, which claimed pyopenjtalk+unidic was the
  2nd-gen tokenizer; §8 explains why that mattered.
- **Evidence:** VOICES.md lists 5 Japanese voices. The G2P behaviour below was
  established by inspecting the installed `misaki/cutlet.py` and by sweeping
  all 199 Japanese clips, not by reading documentation.
- **Verified:** Generated audio with `jf_alpha` (see §6), re-verified all 397
  shipped clips through the real pipeline (see §8), **and** had the audio
  itself phone-recognised over all 199 Japanese clips (§10.1) — 0 confirmed
  audio defects, 6 unresolved. That confirms **which phones are present in the
  waveform**; it says nothing about whether the audio sounds native, and no
  native speaker has reviewed any reading (§9.6).

### ⚠️ Quality Notes
- Japanese has the best-documented voice quality (C- to C+ grades)
- Spanish voices have **no quality grades listed** in VOICES.md — treat as unverified quality
- Recommended Japanese voice: `jf_alpha` (highest grade, C+)
- Recommended Spanish voice: `ef_dora` (first available, ungraded)

### ⚠️ Spanish dialect — read this before "fixing" anything

`ef_dora` is a **Castilian** voice. *(Provenance unsourced and withdrawn — see
the update below; the table's audio verdicts are also superseded by §11.2 and
§11.4.)* The Spanish G2P is a single espeak-ng Castilian engine, so its
dialect output is *correct for the voice that ships*:

| output | appears in | correct for ef_dora? |
|--------|-----------|----------------------|
| `θ` | cine, gracias, hacía, cocina, cabeza | **YES** — Castilian seseo/ceceo |
| `ʎ` | llegué, ella, llave, pastilla, pasillo | **YES** — non-yeísmo |

`em_alex` and `em_santa` are **American** voices, *(same withdrawal — see below)* and for them these would be
wrong. Since all three share one G2P, the app's Spanish audio is dialectally
mismatched to two of its three voices. **This is a decision, not a bug.** Do
not "fix" θ or ʎ without first deciding which variety the app teaches. As it
stands, with `ef_dora` shipping, **θ and ʎ are correct and have been
deliberately preserved** — see §8.3.

**Update, 2026-09-29 — read §11.2 and §11.4 before relying on either row.**
Everything above is about the **phoneme string** the pipeline feeds Kokoro, and
the string is clean. The **audio** is a separate question, and it has now been
answered against the shipped renders. On the interdental: the `/θ/` is **not
audible** — `ef_dora` emits `θ` for 6.2% of the same minimal pairs against
espeak-ng's own 52.1%, at a 0% false-alarm rate. On the lateral: the `/ʎ/` has
**no validated cue at all** in this project, so the audio's yeísmo status is
**unknown** — not Castilian, not yeísta, not established. "θ and ʎ are correct"
is a statement about the specification, not a clearance of the render, and one
of the two halves is now known to be wrong and the other to be untested. **This
is a decision, not a bug — and the decision is still unmade.**

**And the voices' origin is unsourced.** The table above calls `ef_dora` a
Castilian voice and the other two American. **VOICES.md publishes no
nationality, region or origin for any Spanish voice** — its Spanish section
carries a name, a gender glyph and a hash, and a search of the file for
`mexic`, `spain`, `castil`, `latin` and `accent` returns nothing. That reading
was an inference from voice *names* and is **withdrawn as a fact** (§9.6). The
variety of the shipped audio is **not established**; only the phoneme string
the pipeline asked for is. This matters beyond tidiness: §11.3 records the
identical trap in a candidate voice, whose `es_ES` label is "a Coqui path
convention, not a fact about the speaker". A label that does not track the
recording cannot select a variety — in either direction.



---

## 2. Install & Dependencies

### Python Environment (verified: Python 3.12.10 on Windows)

```bash
# Create venv
python -m venv .venv_tts
.venv_tts/Scripts/activate

# Core TTS
pip install kokoro soundfile

# Japanese G2P (required for Japanese)
pip install "misaki[ja]"

# Download unidic dictionary (~526MB)
python -m unidic download

# Verify
python -c "import kokoro; print(kokoro.__version__)"
```

### Dependency Chain
| Component | Purpose | License | Notes |
|-----------|---------|---------|-------|
| `kokoro` | TTS pipeline | Apache-2.0 | Main package |
| `misaki` | English/Spanish/French G2P | Apache-2.0 | espeak-ng backend |
| `misaki[ja]` | Japanese G2P extras | Apache-2.0 | Adds pyopenjtalk |
| `pyopenjtalk` | Japanese phoneme analysis | LGPL-2.1 | Ships Windows wheels |
| `unidic` | Japanese dictionary | LGPL-2.1 | 526MB zip / ~1.2GB extracted |
| `torch` | PyTorch backend | BSD-3 | ~500MB CPU wheels |
| `espeakng_loader` | Bundled espeak-ng binary | BSD | Via misaki phonemizer |

### Windows Compatibility (verified on this machine)
- ✅ pyopenjtalk: ships precompiled Windows wheels via pip (no C build required)
- ✅ unidic: downloads and extracts fine
- ✅ espeak-ng: bundled via `espeakng_loader` (no Windows store/WinRT dependency)
- ⚠️ PyTorch CPU wheels are large (~500MB download)
- ⚠️ unidic download is large (526MB zip) and can be slow

---

## 3. Deployment Options Evaluation

### Option A: Pre-Generate Offline (RECOMMENDED ⭐)

**Approach:** Generate all lesson audio at build/seed time. Ship as static files bundled in the app's assets (`app/src/main/assets/audio/`).

| Factor | Assessment |
|--------|------------|
| ✅ **Offline support** | Trivial — assets ship with the APK, play forever |
| ✅ **Determinism** | Same audio every time; no runtime variability |
| ✅ **No runtime cost** | Zero TTS inference at app runtime |
| ✅ **Battery friendly** | Just decode and play |
| ✅ **Simple client** | Standard audio playback, no ML runtime |
| ⚠️ **Storage** | ~20-50KB per 5s clip (Opus); 1000 clips ≈ 25MB |
| ⚠️ **Content updates** | Regenerate and push updated files |

**Architecture:**
```
Build server (one-time / on content change):
  For each challengeOption with text:
    kokoro.generate(text, voice=lang_voice) → .wav
    ffmpeg convert → .ogg (Opus, 48kbps)
    Copy to app assets: app/src/main/assets/audio/{lang}/{name}.ogg
    Set audioSrc = "asset:///audio/es/42.ogg"

Android client:
  Standard audio playback via Media3/ExoPlayer
  Play directly from bundled assets — no download, no network
```

### Option B: Runtime Server-Side TTS

| Factor | Assessment |
|--------|------------|
| ⚠️ **Latency** | ~1.7s cold / 0.6-0.8s warm per clip (observed, CPU) |
| ⚠️ **GPU dependency** | PyTorch CPU inference is slow for high traffic |
| ⚠️ **Cost** | ~$0.06/hr audio at commercial rates |
| ❌ **Complexity** | Requires FastAPI + PyTorch service, GPU optional |
| ✅ **Dynamic text** | Generate audio for arbitrary user text |

**Observed CPU inference:** ~1.7s cold, ~0.6-0.8s warm for 10-40 word clips on i7-12700F.

### Option C: On-Device Android Inference

| Factor | Assessment |
|--------|------------|
| ❌ **Model size** | 82M params ≈ 160MB (FP32) or 40MB (INT8) |
| ❌ **RAM** | ~500MB+ peak during inference |
| ❌ **Battery** | Continuous CPU usage |
| ❌ **Threading** | Must offload from UI thread |
| ⚠️ **Complexity** | ONNX Runtime Mobile integration |

**Feasibility:** Technically possible via `onnxruntime-mobile` but **not recommended** for this use case. Model + dependencies would add ~200MB to app size.

---

## 4. Audio Asset Contract

### File Naming Convention
```
app/src/main/assets/audio/{lang}/{name}.ogg
```
Examples (as referenced in `audioSrc` fields):
- `asset:///audio/es/el_dormitorio.ogg`
- `asset:///audio/ja/konnichiwa.ogg`

### Audio Format
| Parameter | Recommendation | Reason |
|-----------|----------------|--------|
| **Codec** | Ogg Opus | 2x compression vs MP3 at equal quality; standard on Android |
| **Bitrate** | 48kbps (voice) | Voice quality, ~10KB/min |
| **Sample rate** | 24000 Hz | Kokoro native output |
| **Channels** | Mono | Voice only |

**File size estimate:** ~800 bytes per second of audio (Opus)

### Caching Headers

No longer applicable — audio ships inside the APK as bundled assets, so no CDN cache headers are needed.

### Android Offline Storage

Not needed — audio ships in `app/src/main/assets/audio/{lang}/` and plays straight from the APK:

```kotlin
val mediaItem = MediaItem.fromUri("asset:///audio/es/el_dormitorio.ogg")
```

### audioSrc Migration

Current schema (`challengeOptions` table):
```sql
audio_src TEXT  -- e.g. "/es_man.mp3"
```

No schema change needed. Update values from legacy paths:
```sql
-- Migration: update legacy audio paths
UPDATE challenge_options 
SET audio_src = '/audio/' || lang || '/' || id || '.ogg'
WHERE audio_src LIKE '/%.mp3';
```

---

## 5. Licensing

| Component | License | Commercial Use |
|-----------|---------|----------------|
| Kokoro-82M weights | Apache-2.0 | ✅ Yes |
| misaki G2P | Apache-2.0 | ✅ Yes |
| pyopenjtalk | LGPL-2.1 | ⚠️ Dynamic link or GPL-infectious |
| unidic dictionary | LGPL-2.1 | ⚠️ Same as above |
| espeak-ng | BSD | ✅ Yes |
| Training data | Public domain / CC BY / Synthetic | ✅ Yes |

**Commercial use:** Fully allowed. Kokoro-82M is explicitly Apache-2.0 licensed for commercial deployment.

**LGPL caveat:** pyopenjtalk/unidic are LGPL. For Android app, this is fine — load as shared libraries (`.so`) and don't statically link. The Android app itself is open source (MIT).

---

## 6. Verification (observed)

### Environment
- OS: Windows 11 Education, x64
- CPU: 12th Gen Intel i7-12700F (no discrete GPU)
- Python: 3.12.10 (Scoop)
- Disk: ~50GB free

### Setup & Install (actual times)

```bash
# Python 3.12 (Scoop) + venv
python -m venv .venv_tts
.venv_tts/Scripts/pip install kokoro soundfile
# Japanese G2P (adds pyopenjtalk + unidic; ~526MB unidic download)
.venv_tts/Scripts/pip install "misaki[ja]"
.venv_tts/Scripts/python -m unidic download
# Verify
.venv_tts/Scripts/python -c "import kokoro; print(kokoro.__version__)"
```

| Step | Time |
|------|------|
| venv creation + kokoro+soundfile install | ~45s (PyTorch ~500MB download) |
| misaki[ja] + unidic pip install | ~35s |
| unidic dictionary download | ~3min (526MB zip → 1.2GB extracted) |
| Kokoro model weights download (hexgrad/Kokoro-82M) | ~60s (322MB) |

### Generation Results

| Language | Voice | Text | Cold Synth | Warm Synth | Output | Duration |
|----------|-------|------|------------|------------|--------|----------|
| Spanish | ef_dora | "¡Hola! Este es un mensaje de prueba para aprender español." (41 words) | 1,737ms | 611ms | `.scratch/samples/es.wav` (160KB) | 3.35s |
| Japanese | jf_alpha | "こんにちは。これはテストです。日本語を学びましょう。" (13 words) | 1,756ms | 808ms | `.scratch/samples/ja.wav` (213KB) | 4.45s |

Output: 24000 Hz, mono, WAV (PCM 16-bit)

### Pipeline Load Times
- First `KPipeline(lang_code='e')`: 83.7s (model weights download + load)
- Second `KPipeline(lang_code='j')`: 1.1s (weights already cached)

### Spanish G2P Verified
- `lang_code='e'` uses espeak-ng via misaki's phonemizer backend
- NOT a separate `misaki[es]` package — confirmed by inspecting installed packages
- espeak-ng binary bundled via `espeakng_loader` pip package (no Windows store dependency)
### Japanese G2P Verified
- Japanese uses **misaki's own fugashi/cutlet tagger** over unidic. This
  corrects an earlier claim in this report that it used pyopenjtalk — it does
  not, and the distinction is not cosmetic (see §8.4).
- pyopenjtalk ships precompiled Windows wheels via pip (no Visual C++ Build
  Tools required) and is still present as a dependency.
- unidic dictionary is ~526MB zip / ~1.2GB extracted — the largest dependency

### Latency Assessment
- **CPU inference is too slow for real-time server-side** at scale (1.7s cold, 0.6-0.8s warm)
- For **pre-generation** (Option A): acceptable — batch at build time, not user-facing
- GPU would reduce cold latency to ~0.1-0.3s but adds infrastructure complexity

---

## 7. Recommendation

### 🏆 Primary: Pre-Generate + Bundled Assets (Option A)

**Rationale:**
1. **Offline-first requirement** met trivially — assets ship with the APK, play forever
2. **Zero runtime ML complexity** on Android
3. **Deterministic audio** — same clip every lesson
4. **No hosting or network** — audio ships inside the APK, nothing to cache
5. **Cost effective** — generate clips once, no CDN needed
6. **Latency acceptable** for batch generation (not user-facing)

**Hybrid enhancement (not currently possible — the app has no `INTERNET` permission):** Use Option B (runtime server-side) ONLY for:
- Dynamic quiz explanations generated from correct answers
- Pronunciation comparison feedback
- Anything that changes per user

### Migration Path
1. Generate all audio offline (use `.scratch/tts_test.py` as template)
2. Convert WAV → OGG Opus with ffmpeg: `ffmpeg -i input.wav -c:a libopus -b:a 48k output.ogg`
3. Copy the files into `app/src/main/assets/audio/{lang}/`
4. Update `audio_src` paths in `challenge_options` table
5. Android client: no changes beyond standard audio playback

---

## 8. G2P Defects Found, Fixed, and Still Open

Everything here was found by phonemising all 397 shipped clips through the
**real** pipeline and comparing against native reference pronunciations. It is
recorded here because the original "Verified: Generated ... audio" claim in
this report was not sufficient: generation working is not the same as the
phonemes being right.

### 8.0 The G2P is voice-independent — so a phoneme defect cannot be fixed by changing voice

Measured, not assumed: **0 of 229 rendered items produced different phonemes
across all 8 voices.** `kokoro/pipeline.py` holds one G2P object per language
and never passes the voice to it; the voice only reaches the acoustic model.

**Consequence:** switching `jf_alpha` → `jm_kumo` or `ef_dora` → `em_alex`
changes nothing about pronunciation. Every defect below is a G2P defect and
is fixed in the G2P. A full voice A/B across 5 Japanese and 3 Spanish voices
confirmed this and found no voice that handles short utterances better.

### 8.1 `pyopenjtalk.g2p()` is NOT a valid verification of what the audio says

This is the most important operational point, because it is how the defects
below shipped through review.

The audio path is:
```
pyopenjtalk.g2p  →  misaki/fugashi tagger  →  misaki kana→IPA  →  Kokoro
      ^                                  ^            ^
      stops here                        the audio starts at the kana→IPA table
```

The generator scripts defined their verification as `pyopenjtalk.g2p(...)`,
which **bypasses the kana→IPA table entirely** — a different stage from the one
the audio goes through. It reports:

| text | `pyopenjtalk` reported | the audio actually said |
|------|------------------------|-------------------------|
| 買った | `k a cl t a` | `kaʔta` |
| がっこう | `g a cl k o o` | `ɡaʔkoː` |
| 私 | `w a t a sh i` | `βatakɯɕi` |

So verification looked clean while the audio was wrong. **All verification now
goes through `g2p_fixes.jpn_g2p()` / `spa_g2p()` / `make_pipeline()`, which
wrap the same patched KPipeline the audio is rendered with.** `probe_g2p.py`
is kept but explicitly relabelled a tagger-only probe, and prints both stages
side by side.

### 8.2 Defects found and fixed

| defect | clips | what was wrong | fix |
|--------|-------|----------------|-----|
| **わ → β** | 53 (22 LISTEN) | `misaki/cutlet.py` maps `chr(12431)` わ to `'βa'` — β is U+03B2, the **Spanish voiced** bilabial fricative. Japanese わ is **voiceless** labiodental. The same table already uses the correct `ɸ` for ふ eight times, so it is internally inconsistent. | Patched in `g2p_fixes.py`. **Upstream bug** — the same line is in `hexgrad/misaki` on `main`, inherited from `polm/cutlet`. |
| **っ → ʔ** | 30 (7 LISTEN) | `misaki/cutlet.py::_get_single_mapping` has the correct geminate branch **commented out** and returns `'ʔ'` instead. Japanese has **no glottal stops**, so a lesson drilling 学校 as beginning with a held /k/ shipped a glottal stop. pyopenjtalk produced the correct kana (ガッコー) — the corruption was downstream. | Restored, and extended: upstream's consonant set omits every affricate and sibilant, so upstream would still emit `ʔ` for っち/っし. |
| **日本語 → にっぽんご** | 7 (6 LISTEN) | misaki's fugashi tagger reads 日本語 as **にっぽんご** ("Nippon-go") — a *different word*, not a near-miss. `pyopenjtalk` correctly gives ニホンゴ. For a language app this was the worst class of error in the whole investigation. | Reading-override mechanism in `g2p_fixes.py` (`READING_OVERRIDES`), matched on the concatenated surface so it survives however fugashi splits the word. |
| **川 → がわ** | 1 | fugashi inserts a spurious /ɡ/: `ɡaɡa` instead of `ɡawa`. | Same override mechanism. |
| **停留所 → りゅーしょ** | 1 | bus stop is ていりゅう**じょ**; fugashi gives てーりゅー**しょ**. | Same override mechanism. |
| **一枚 / 一冊** | 2 | fugashi reads 一 as いっ in 一枚 (should be いちまい) and as いち in 一冊 (should be いっさつ) — note the sokuon is present in one and must be absent in the other. | Overrides are keyed on the **whole word** (`一枚`, `一冊`), not on 一, so the four readings fugashi already gets right — 一緒, 一人, 一つ, 一切 — are untouched. |
| **Spanish /ç/ for /x/** | 9 (2 LISTEN) | Measured directly on the shipped audio: these clips really did say `ç` where Castilian wants `x` (`dˈiço`, `muçˈeɾ`, `oçalˈa`), because one generation script applied a blanket `x → ç`. | Re-rendered with correct Castilian `x`. |
| **Spanish `cogió` doubled palatal** | 1 | espeak emits both `/x/` and a spurious `/j/` for one written g: `koxjˈo`. | `koxjˈo` → `koˈɣo`. |
| **映画 read as エーガ** | 3 | unidic-lite's own entry for エイガ is phonemised `eːɡa` — a long vowel where the word has a glide. The app's declared `eiga` is right. | `READING_OVERRIDES` entry keyed on the surface `映画`; re-rendered. |
| **辛い read as つらい** | 1 | unidic-lite only knows the つらい ("painful") reading. In this corpus 辛い is always *spicy food* — every option of challenge 60026 is 辛いもの followed by a 食べます-family verb — so the clip taught "painful things" in a sentence about heat. | Override to からい, keyed on the bare kanji, which is safe precisely because "painful" does not occur. Re-rendered. |
| **休日 read as キュージツ** | 1 | unidic-lite reads 休日 as キュージツ — correct for 休日 on its own (a national holiday) and wrong inside 定休日, which is ていきゅうび. The app already declared `teikyubi`. | Override keyed on `定休日`, leaving a bare 休日 alone. Re-rendered. |
| **Spanish `y` after `u` read as /j/** | 1 | espeak-ng's `es` backend reads a written `y` following `u` as the consonant /j/: `muy` came out `mˈuj`, where Spanish is unambiguously /mui/ (`cuy`, `buy`, `arguy` fail the same way). The other four vowel-`y` words in the corpus — *doy, hoy, soy, voy* — were already correct, so the class was one clip: `estˈuβe mˈuj kansˈaðo` → `estˈuβe ˈmui kansˈaðo`. | Fixed in the shared G2P module (`SPANISH_UY_FIXES`), not in the generator script, so no later Spanish render can reintroduce it. Re-render asserts the rendered phonemes match the intended G2P **before** overwriting an asset. `suya` and `tuya` are deliberately untouched: there the `y` is correctly the consonant /ʝ/. |
| **Spanish `/x/` heard as `/ʃ/` in *dixo* / *hizo* / *hice*** | 3 | Found by **listening**, not by string comparison: a phone recogniser heard /ʃ/ where the intended string said /x/ in `dixo`/`hizo`/`hice` position. The app was effectively saying *dixo* with the sound of "sh". A ten-rate control in the same context heard /x/ 10/10 and /ʃ/ 10/10 with durations matching to 0.03 s, so the difference is in the shipped audio. | Re-rendered from the same intended string. Full before → after phone sequences in **§10.2**. |
| **Spanish `corras` stressed on the wrong syllable** | 1 | espeak emitted `kˈoras`; a two-syllable LLANA word ending in `-s` cannot be stressed on its first syllable, so this is settled by the accent rule and the spelling alone — **no perceptual judgement involved**, and none is claimed. | `SPANISH_WORD_FIXES` gains `"corras": "koˈras"`. See **§10.2**; the recogniser heard no change, as expected, since stress was never in its calibration. |

Total re-rendered in the first pass: **97 clips** (76 Japanese phonemiser +
1 Spanish phonemiser, then 11 Japanese reading + 9 Spanish dialect). All 397
clips re-verified through the real pipeline; **0 defects remain in the classes
above.** The 97 is the count *as of that pass* and is not the project's final
figure: the dictionary-reading overrides, the `u`+`y` repair, the spec-vs-audio
work in §8.8, the Spanish specification in §9 and the phone-level work in §10
each re-rendered further clips. Per-class counts are given in the section that
did the work. **The `muy` fix in this table is now confirmed at the phone
level, not only in the string — see §10.2.**

### 8.3 What was deliberately NOT "fixed"

Three classes were investigated and found to be **false positives**. Acting on
them would have made the app worse:

* **`θ` and `ʎ` (70 class-hits, 54 unique clips) — CORRECT for `ef_dora`.**
  Castilian Spanish. See the dialect note above. Preserved.
* **Plain `/x/` in `jefe`, `mujer`, `viajar`, `viaje`, `gire`, `jinete`,
  `tarjeta`, `gente`, `dije`, `dijimos`, `dijo`, `trabaja`, `trabajo` — CORRECT.**
  `/x/` is the normal realization of written j and g before **e, i**. One
  generation script's blanket `x → ç` was wrong for all of these.
* **Capital `W` and `A` in Spanish phonemes — NOT a bug.** They are Kokoro's
  own diphthong symbols: the English G2P writes `now → nˈW`, `day → dˈA`,
  `boy → bˈY`. So `aunque → ˈWnke` is /aʊnke/ and `peina → pˈAna` is /peɪna/,
  both correct. Both symbols are in the 114-symbol vocabulary and are spoken as
  diphthongs, not as English letters.

> **Superseded in part by §11.2 and §11.4.** The `θ` and `ʎ` class above is
> correct *in the phoneme strings* and establishes nothing about the render:
> the interdental turns out **not to be audible** in the shipped audio, and the
> lateral is **unmeasured** by any instrument this project has. "Castilian
> Spanish" is also no longer asserted as a fact about the voice — **VOICES.md
> publishes no nationality for any Spanish voice**, and the reading was an
> inference from the voice names (§9.6). What survives from that row is the
> guard it was written for: those phones are present in the strings, so nobody
> has quietly "fixed" the dialect to please a checker. The `/x/` row above is
> unaffected and was confirmed in the audio in §10.2.

### 8.4 Still open / not fixed

* **`tsuki.ogg` has an English graded option** (`Moon / Month`). This is
  **by design** — challenge 2040 is a vocabulary-gloss SELECT asking what 月
  means, with English answer options. The clip plays 月 correctly. Do not
  "fix" it.
* **14 Spanish clips could not be classified** for `/x/` vs `/ç/` by acoustic
  measurement; the two candidates are too close in long story clips. They were
  left untouched. **Partly superseded:** the G2P-string side of this question
  is now closed — a sweep of 185 distinct Spanish clip texts finds **0 /ç/**
  (§9.4), so there is no longer any doubt about what *should* be rendered. A
  phone recogniser was later run over the corpus and also found **0 /ç/ in 198
  clips** (§10.2), but that is a **weaker** second opinion, not a resolution:
  the recogniser **cannot name /ç/` at all** (4 of 10 deliberately-rendered
  ceceo stimuli) — only that a ceceo stimulus is not a velar one, thanks to a
  0% false-alarm rate and a one-sided `/x/` test (§10.4). These 14 were not
  classified per-clip. **Unverified.**
* **CLOSED — four mutually inconsistent Spanish `/x/` handlers** used to exist
  across `generate_b1_audio.py`, `gen_vocab_audio.py`, `generate_b1b_audio.py`
  and `generate_b1_grammar_audio.py`, with only the blanket `x → ç` in
  `generate_b1b_audio.py` shown to be wrong. All four now route through the
  shared `g2p_fixes` module, so the inconsistency cannot recur. See §8.5.
* **`川` is not the only word fugashi may misread.** A full sweep of all 199
  Japanese clips found 54 clips where fugashi and pyopenjtalk disagree. Most
  are benign (the particle を read as お, long-vowel contractions, valid
  orthographic variants), and in several cases **pyopenjtalk is the wrong one**
  (降りました, 定休日). Only the five clearly-wrong readings were pinned at
  the time. Others may exist in content not yet shipped.
* **The pinned set has since grown, and the sweep has since widened.** Since
  that pass: `来させる`, `私` and `明日` in §8.6.2, the three
  dictionary-reading overrides 映画 / 辛い / 定休日 in §8.2, and then a
  corpus-wide sweep of all **1095 Japanese options** (§8.8.2) that found a
  further **15** copy-paste readings sitting on options that own no clip —
  invisible to the clip-level sweep, because an audio check can only inspect
  what has audio. **Options with no clip remain outside the reach of any
  audio-level check, permanently.**
* **Short-utterance weakness is unchanged and unaddressed.** The model card
  documents weak performance below ~10–20 tokens, and the app ships many
  single-word clips. "Bundling short utterances together" was tested: it measurably
  **corrupts** the Japanese G2P (はし → `βaɕi`, misaki emits a non-Japanese
  segment), so it is **not** a safe mitigation as-is.

### 8.5 Reusable guidance

* **Phonemise through `g2p_fixes`, never through a bare `KPipeline` or
  `pyopenjtalk.g2p`.** The §8.2 defects were real and live upstream, so a bare
  pipeline still carries every one of them. This rule now has a fixed location
  to point at.
* **Canonical location:
  `duo-android/tools/g2p_fixes.py`, inside the repository.** The module used
  to live only at `H:/Projects/DuoLingo/.scratch/g2p_fixes.py`, outside Git,
  which made the audio this report evaluates **unreproducible from a clean
  clone** — the single worst property a generation script can have, because the
  fix and the thing it fixes then travel apart. It is now vendored in-repo and
  development-only. The `.scratch` path survives as a thin shim that
  re-exports the in-repo module, so every existing script keeps working
  unchanged; **do not edit the shim and do not treat it as canonical.**
* It is a **sibling of `app/`, not a descendant**, so it cannot be packaged
  into the APK. The app's Gradle config declares no `sourceSets`, `resources`
  or `packaging` block that could sweep it in; the only packaged inputs are
  `app/src/main/{assets,java,res}`.
* The four generator scripts that still constructed a bare `KPipeline` were
  routed through the shared module: `generate_b1_audio.py`,
  `gen_vocab_audio.py`, `generate_b1b_audio.py` and
  `generate_b1_grammar_audio.py`. A fifth, `probe_g2p.py`, was checked and
  turned out **never to construct one**, so it needed no change and was left
  alone.
* The upstream defects are patched at runtime; the installed `misaki` package
  is **not** edited, so the fixes stay reviewable and reversible.
* A Korean/Chinese/English generator added later must go through
  `make_pipeline()` too, or it will silently carry whatever defects that
  language's G2P has — none of which have been audited.
* **A hand-written reading may not silently differ from the rule-generated
  reading for the same word without a stated reason.** This is a new rule,
  earned by §9.5, and it is the one most likely to be violated by accident:
  overrides look like data, so a wrong one is indistinguishable from a right
  one at a glance. The audit behind it found 63 hand-written entries that
  differ from the generator's output and **20 of those 63 were the override
  being wrong and the engine being right.** Every wrong reading in that table
  came from a hand-written entry; **none came from the rule engine.** If you
  override, write down why the engine is wrong.
* **Generation must go through `make_pipeline()` with a bound model.** This is
  a hard contract, and it now has a guard rail because violating it was silent
  (§10.8). `make_pipeline` used to default to `model=None`, which `KPipeline`
  treats as falsy, producing a **quiet** pipeline that yields the correct
  phonemes with `audio=None` for every chunk — while every G2P assertion in the
  calling script still passed, and the failure surfaced as an `IndexError`
  from inside `soundfile` naming no project file. The default is now
  `model=True`, `make_pipeline` **raises `ValueError` on `model=None`** rather
  than building a quiet pipeline, and all four generators **raise rather than
  skip** when any chunk's audio is `None`. Pass a loaded `KModel` to share one
  model across languages; pass `model=False` only for a deliberately
  phoneme-only probe.
* **The canonical in-repo location is `duo-android/tools/g2p_fixes.py`.**
  `H:/Projects/DuoLingo/.scratch/g2p_fixes.py` is a **thin shim** that
  re-exports it, kept so existing scripts keep working; **do not edit the shim
  and do not treat it as canonical.** Any finding recorded in this document
  against a reading table, a patch, or a pipeline default is a finding about
  the in-repo module — edit that one, and verify the shim still resolves to it.
* **A check that passes and an output that is missing are independent facts.**
  A script that only asserts on G2P strings will not notice that the audio was
  never produced. Assert on the audio too, or do not believe the green.

---

### 8.6 Spec-vs-audio detection (added 2026-09-28)

> **Status: first pass, outcome superseded.** This subsection records what the
> detector found when it was first built. The **"after" column and the 25
> remaining confirmed rows below are no longer the current state** — they are
> now at 0 unresolved, and several rows in the tables have been reclassified
> in light of what the fixes turned out to be. Read this as the *method*, then
> read **§8.8** for the current results. Nothing here has been deleted, because
> the reasoning that produced the current state depends on it.

Everything above compares the audio against *our* idea of a correct reading.
A stronger check became available later: the curriculum Kotlin already carries
hand-authored **`romaji` fields declaring what each Japanese word is supposed
to sound like** — 198 of 199 Japanese clips have one. Those fields are visible
to the learner in the app.

For each Japanese clip we compare

- **A** = `G2P(clip text)` — what the audio pipeline feeds Kokoro
- **B** = `G2P(kana from the app's declared romaji)` — what the app promises

Where A and B differ, **the audio contradicts a specification the learner can
see**. No native ear and no linguistic judgement is required, because the app
states the intent itself.

**What this proves.** The audio matches (or contradicts) the app's declared
intent.

**What this does NOT prove.** That the declared intent is linguistically
correct. Those are different claims, and conflating them would be the easiest
way to make this app worse. Of the 25 confirmed rows §8.8 resolves, a
substantial share are cases where **the app's romaji is the wrong one** and the
audio is right.

#### 8.6.1 Results

| | before | after |
|---|---:|---:|
| Japanese clips | 199 | 199 |
| declared romaji present | 198 | 198 |
| **agreement (audio matches declared intent)** | **157** | **172** |
| flagged by phoneme comparison | 41 | 35 |
| — confirmed at kana level | 31 | 25 |
| — jaconv romaji→kana artefact | 10 | 10 |

**Every one of the 25 remaining confirmed rows was believed at this point to be
a defect in the specification or in the converter, not in the audio.** That
grouping turned out to be **wrong for one row** — `teikyubi` below was the
sides reversed, and the audio was the wrong one. The rest of the statement
stands. They fall into two groups:

*App romaji is wrong (7 clips as first classified — the fix is in the Kotlin,
and every one of these has since been resolved on the app side; see §8.8.1):*

| clip | audio (correct) | app romaji (wrong) |
|---|---|---|
| `g_passive_mirerareru` | 見られる `みられる` | `mirerareru` → みれられる — parses the stem as 見れる, which the text is not |
| `g_causative_matasaseru` | 待たせる `またせる` | `matasaseru` adds a mora |
| `g_causative_nomasaseru` | 飲ませる `のませる` | `nomasaseru` adds a mora |
| `ja_cond_shigato_ga_owattara_kaerimashou` | 仕事 `しごと` | `shigato` — a misspelling of *shigoto* |
| `ja_keigo_gomiruninaru` | ご覧になる `ごらんになる` | `gomiru` drops らん |
| `senen` | 千円 `せんえん` | `Senen` — capitalisation only; the G2P is right. Left alone (§8.8.3) |
| `teikyubi` | 定休日 — **reclassified**: this row had the sides backwards | the app's `teikyubi` was right all along and the **audio** was the wrong one. unidic-lite read 休日 as キュージツ; fixed by override and re-render (§8.2) |

*jaconv romaji→kana artefact (16 clips):* the converter mishandles long vowels
(`goukei`→ごうけい vs ごーけい), syllabic `n` (`minna`→みんあ vs みんなで), the
りゃ行 (`ryoushosho`→りょうしょしょ), and drops a sokuon
(`narimashita`→なりました vs になりました). The romaji in these is fine; the
converter is not. A better romaji→kana path would let the detector adjudicate
them.

#### 8.6.2 Fixes applied from this method

| clip | was | now | basis |
|---|---|---|---|
| `g_causative_kuramaseru` | `kʲitasaseɾɯ` きたさせる | `kɯɾasaseɾɯ` くらさせる | app declares `kurasaseru`; くらさせる is the causative of くる and きたさせる is a different word |
| `g_causative_tabesaseteimasu` | `ɸatakɯɕi` わたくし | `ɸataɕi` わたし | app declares `watashi`; pyopenjtalk also gives わたし |
| `g_causative_gyunyu_nomaseru` | `ɸatakɯɕi` わたくし | `ɸataɕi` わたし | same |
| `ja_rareru_watashi_wa_sono_eiga_o_mireraremasu` | `ɸatakɯɕi` わたくし | `ɸataɕi` わたし | same |
| `ashita_wa_hareru_to_omoimasu` | `asɨ` あす | `aɕita` あした | app declares `ashita`; pyopenjtalk also gives あした |
| `ja_keigo_ashita_wa_ukagaimasu` | `asɨ` あす | `aɕita` あした | same |
| `ja_cond_ashita_ame_ga_furunara_ryokou_ni_ikimashou` | `asɨ` あす | `aɕita` あした | same |

**私 → わたし** and **明日 → あした** are now settled, and deliberately so.
Both あす and あした are valid Japanese and わたくし is a real (archaic,
humble) reading, so neither was a defect *on linguistic grounds*. They were
defects because **the app already declares a reading in a field the learner can
read, and the audio said something else.** Where the app has made the choice,
the audio must match it. Pinned via `READING_OVERRIDES`.

**Three were deliberately NOT pinned**, and the code says why inline:
見られる, 待たせる and 飲ませる. Both dictionaries agree with the G2P in all
three, so the app's romaji is the one that is wrong, and pinning would replace
correct audio with a non-word. The fix belongs in the Kotlin. **It has since
been made** — see §8.8.1, where the root cause turned out to be a mora count
rather than a choice of reading.

`来させる` is the weakest-evidenced of the three pinned entries: **neither
dictionary corroborates it** — both read きたさせる, because neither knows the
token is the causative of くる. The basis is the app's declared romaji plus the
judgement that くらさせる is the causative. It is flagged as such in
`g2p_fixes.py`.

#### 8.6.3 One Japanese clip is outside the net

`tsuki.ogg` (challenge 2040) is a vocabulary-gloss SELECT — "What does 月
(tsuki) mean?" — whose options are English, so the option owning the clip
carries no `romaji` field. **One clip in 199 cannot be checked by this method.**

---

### 8.7 How audible are the fixes? A calibrated acoustic measurement

> **Status: method superseded, results withdrawn, reasoning kept.** The
> question this section asked was real — were the わ and っ fixes in §8.2 only
> cosmetic? — and the answer it produced is **no longer the current answer**,
> for two reasons recorded in **§10**: the phone recogniser that was rejected
> below was later found, calibrated and run, and it established what this
> cepstral method could not (**§10.1**); and the pipeline was found to be
> **nondeterministic at −18.5 dB relative RMS**, which puts a hard ceiling
> under every distance this section reports (**§10.7**). Nothing here has been
> deleted, because the reasoning that produced the current state depends on
> it — but **no number in this section is a current result**, and the severity
> ranking withdrawn in §8.7.5 was already withdrawn for other reasons. Read
> this as the *method*, and read **§10** for what is now established.

The わ and っ fixes in §8.2 were verified at the **G2P-string level only** when
this section was written. We knew what phonemes the model was asked for, before
and after, and did not know whether the two actually *sound* different. If ɸ
and β differ by too small a margin to matter, the fix is cosmetic rather than a
real improvement, and we should say so rather than assume. **That gap is now
closed — at the phone level — by §10.1.** What closed it was not a better
version of this instrument; it was a different class of instrument.

#### 8.7.1 The instrument, and what was deliberately not used

MFCC front end (26 mel bands, 13 cepstral coefficients) plus DTW alignment,
implemented in numpy only, because this venv has no scipy and no librosa.
Distance is the DTW path cost per unit path length, in cepstral units.

Every render in the experiment uses the **same voice, same text, same speed,
same encoder settings**, so speaker, channel, loudness and duration confounds
cancel and what remains is the phonetic difference in question. That is why a
differential measure is the right tool and a whole-word recogniser is not: both
`ɸ` and `β` map to the same letters, so a word-level ASR cannot see either
defect. A full-corpus word-level ASR round trip was run and confirmed this —
it transcribed the corrected and the uncorrected 日本語 audio identically.

**A phone recogniser was considered and rejected.** Allosaurus is primarily an
English phone classifier; its Japanese accuracy is unknown and unverified; and
building a shaky instrument and then reporting its output as evidence is
precisely the failure mode this document has been guarding against throughout.
It was not used.

> **Superseded, and this is the pivot of the whole section.** The objection was
> never to using a phone recogniser — it was to using *Allosaurus*, an
> uncalibrated one. A **Japanese** phone recogniser independent of Kokoro and
> misaki was later found, its false-alarm rate measured on real audio before
> any corpus was read, and its limits stated in advance: **§10.1**. The
> objection was right, and honouring it is what made the later instrument
> trustworthy when it was finally used properly.

#### 8.7.2 The instrument was validated before any effect size was computed

This comes first because it is what licenses the numbers below.

| test | result |
|---|---|
| **NULL** — the same text rendered twice, same settings | **0.628** (the noise floor) |
| **SIGNAL** — わ rendered as か | 6.827 |
| **SIGNAL** — わ rendered as に | 7.559 |
| **SIGNAL** — わ with a different vowel | 6.428 |
| weakest real one-consonant change | **6.428** |
| **separation (signal ÷ null)** | **10.2×** |

The measure separates a one-consonant change by more than an order of magnitude
above its own noise floor, so the effect sizes it reports are not the noise.
Had this check failed, the experiment would have been reported as unfit rather
than producing numbers anyway.

#### 8.7.3 The effect sizes

```
effect_size = distance(pre-fix audio, post-fix audio)
            ─────────────────────────────────────────────────
             distance(post-fix audio, a deliberately-wrong render)
```

This is a **dimensionless ratio, not a distance.** Both numbers come from the
same measure on the same clips, so the calibration cancels. Do not compare it
to a raw distance in cepstral units — that is meaningless.

* **≥ 1.0** would mean the fix is as audible as substituting a different
  consonant.
* **0.2–0.5** means audible in principle but a small fraction of a real word
  change.
* **< 0.2** means marginal.

The "wrong" render is the same text with a deliberately substituted
consonant, built from the **phoneme positions** rather than by substituting in
the text: for わ, the `ɸ` occupying the slot the pre-fix `β` occupied is
replaced by `k`; for っ, the character the `ʔ` became is deleted, which is
exactly the sokuon dropped entirely (かった → かた).

| | **わ fix** | **っ fix** |
|---|---:|---:|
| clips measured | 53 | 27 |
| ORIGINAL (recovered from git HEAD) | **53** | **27** |
| SIMULATED | **0** | **0** |
| **median effect_size** | **0.301** | **0.222** |
| mean | 0.380 | 0.299 |
| range | 0.213 – 1.080 | 0.178 – 0.756 |
| 0.2 – 0.5 (modest) | 81 % | 63 % |
| < 0.2 (marginal) | 0 % | 22 % |
| ≥ 0.5 (substantial) | 19 % | 15 % |
| ≥ 1.0 (as big as a consonant swap) | 2 % (1 clip) | 0 % |

**All 80 measured rows are ORIGINAL audio recovered read-only from
`git show HEAD:`, not simulated.** No simulation was needed and none was used:
both fixes were applied to clips that already differed from HEAD, so the
genuine pre-remediation waveform was available for every row. Nothing is
labelled SIMULATED because nothing is.

The っ count is 27 rather than the 30 in §8.2. This run classifies by whether
the `ʔ` disappears from the phoneme string, which catches the 3 clips whose
sokuon is written with kanji (切符 and friends) and which the earlier
kana-character test had missed; it then excludes 7 clips that also carry the わ
defect, because a わ difference would confound a geminate measurement.

**Reading the distributions, not just the medians.** The two classes behave
differently in a way the medians hide. わ has a real tail — 19 % of clips are
substantial and one exceeds a full consonant swap. っ has no tail at all and
22 % of its clips fall below 0.2.

#### 8.7.4 The geminate question, and a null result

Is the post-fix audio closer to a correct geminate than the pre-fix audio is?

A circular reference would be "a render of the post-fix phoneme string", since
that *is* the post-fix audio. So the reference was built by a **different
route**: the sokuon is replaced by an **explicit doubled kana** (かった →
かかった), so the geminate is produced by two real kana rather than by copying
the following consonant. Both must be /katta/. The reference is constructed in
the kana domain via pyopenjtalk and never touches the G2P.

```
clips measured                                 25
POST closer to the correct-geminate reference   12   (48 %)
POST further from it                           13   (52 %)
mean d(pre , ref) = 13.70      mean d(post, ref) = 13.51
mean delta = +0.20   (median -0.11)      instrument noise floor: 0.628
```

**This is a NULL result and it is reported as one.** The mean difference is
+0.20 against an instrument noise floor of 0.628 — inside the noise — and 13
of 25 clips move the *wrong* way. **The measurement does not establish that
the っ fix moved the audio toward correctness.**

It is equally important not to read this the other way. A glottal stop and a
geminate are **both brief, both voiceless, and sit in the same inter-vocalic
position**. That makes cepstral distance close to the worst possible choice of
instrument for this particular contrast. **The null is a limitation of the
measurement, not evidence that the audio did not change.** Converting an
instrument's blindness into a claim about the audio would be the same error as
overstating a result in the other direction.

> **Why the null was the right answer, established later.** §10.1 showed that
> `cl` is an **oral-construction** detector that says nothing about closure
> *duration* — so this method was pointed at the wrong parameter, not merely
> noisy. §10.6 records a targeted duration analysis attempted afterwards,
> which **failed both of its own gates** and is reported as a failure, not a
> result. §10.7 then found the pipeline nondeterministic at **−18.5 dB
> relative RMS**, which puts a hard ceiling under any pre-versus-post
> difference this method could have detected. **Choose an instrument that
> measures the property you care about.**

#### 8.7.5 A severity ranking this measurement does not support

An earlier draft of this document ranked the っ geminate defect as the **higher
severity** of the two fixes. **The acoustic measurement does not support that
ranking** and the claim is withdrawn: わ has a real tail with 19 % of clips
substantial, while っ has none and 22 % of its clips fall below 0.2. On the
acoustic evidence っ is the *quieter* of the two fixes.

> **And the ground it rested on is gone.** The withdrawal above is correct and
> stays, but the distribution it cites is no longer admissible evidence: the
> two waveforms it separates are pre-fix audio recovered from `git HEAD`
> against freshly rendered post-fix audio, and **whether that comparison is
> meaningful at all depends on whether Kokoro's nondeterminism (§10.7) is in
> the raw waveform or only in the Opus encode. That was not separated, and it
> is an open question.** The categorical argument kept below does not depend
> on it, and §10.1 has since established the geminate at the phone level
> instead — 27 of 33 affected clips confirmed to contain a real oral geminate,
> with the glottal stop definitively absent in those. **That is a statement
> about which phones are present in the waveform, not a claim about how large
> the fix sounds.**

The categorical argument is kept, and kept separately, because it stands on
its own and does not depend on any acoustic result:

> **Japanese has no glottal stop.** The pre-fix audio asked the model for a
> glottal stop — a phoneme the language does not contain — and the fix removes
> it. A learner hearing the pre-fix audio was learning a sound that does not
> exist in Japanese.

**This is a correctness argument, not a magnitude one, and it is NOT supported
by the acoustic measurement.** It should not be presented as though the
measurement agreed with it. It is verifiable from the G2P string alone.

#### 8.7.6 What actually justified keeping the っ fix

> **Status: superseded by §10.1.** What follows is what the case looked like
> when the only evidence was a phoneme string and a cepstral distance. The
> **conclusion it reached still stands**, and the **reason it reached it** has
> since been overtaken: the glottal-stop absence is no longer a
> phoneme-string-only claim, it is a phone-level one. The claim has *not* been
> upgraded into a statement about how the fix sounds, and it never will be.
>
> With the acoustic case unproven, the justification was the **phoneme-level
> argument only**:
* **What was verified then.** The G2P string before the fix was `kaʔta`; after
  it is `katta`. Checkable by reading the phoneme output of the pipeline, and
  requiring no listening and no acoustic measurement.
* **What was NOT verified then.** That the two waveforms differ by enough for
  a learner to notice. §8.7.4 is a null and §8.7.3 puts the median effect at
  0.222.
* **What is verified now (§10.1).** That the shipped audio contains a real
  **oral** geminate at the sokuon position in **27 of the 33** affected clips,
  with a `cl` hit at a false-alarm rate of **0/166**. A glottal stop has no
  oral release and cannot produce a `cl` token, so the glottal stop is
  **definitively absent** in those 27. The other **6 remain unresolved** and
  are reported as unresolved, not as clearances.
* **What is still NOT verified.** That any of this is audible to a learner.
  §10.7 puts a hard ceiling under that question for this pipeline, and no
  instrument in this document answers it.
* **Therefore.** The fix is kept on two independent grounds: the audio no
  longer requests a phoneme Japanese does not contain, and — established
  since — the shipped audio does not contain one. **Neither is a magnitude
  claim, and the phone recogniser is not evidence of one.**

#### 8.7.7 Summary

> **Status: withdrawn as a current result.** The table below is the state of
> the cepstral method's findings. §10.1 and §10.7 supersede it; see the status
> note at the head of §8.7.

| | わ fix | っ fix |
|---|---|---|
| median effect_size | 0.301 | 0.222 |
| fraction ≥ 0.5 | 19 % | 15 % |
| fraction ≥ 1.0 | 2 % (1 clip) | 0 % |
| closer to a correct-geminate reference | n/a | **no — null** |
| justification for keeping the fix | phoneme-level: わ is voiceless labiodental, β is voiced | phoneme-level: **Japanese has no glottal stop** |
| **phone-level status now (§10.1)** | **β absent from all 199 clips**, false-alarm rate **0/24** | **glottal stop absent in 27 of 33**, false-alarm rate **0/166**; **6 unresolved** |

**Both fixes are acoustically real and both are marginal.** The phonemes are
now correct and the audio is measurably closer to correct, but the change is
small in perceptual terms — a fraction of what substituting a consonant would
sound like. The わ fix is the more audible of the two. For the っ fix the
honest statement is the correctness one in §8.7.6, not a magnitude one.

> **Withdrawn.** "Acoustically real and marginal" is a claim about
> **perceptual magnitude**, and this instrument cannot make one: it is a proxy
> for perceptual difference, and the pipeline is **nondeterministic at −18.5 dB
> relative RMS** (§10.7), so a difference of this size was never resolvable by
> this method in the first place. What survives is the correctness claim, and
> it is now carried by §10.1 at the phone level — **0 confirmed Japanese audio
> defects** — which is a different and narrower statement.

A spectral distance is a **proxy** for perceptual difference, not a perceptual
test. It says nothing about intelligibility, and nothing about whether a
learner would notice the difference without a reference. Only a controlled
listening test can answer that, and it is outside the scope of this work.

---

### 8.8 Hardening the detector, and the results (added 2026-09-28, after §8.6)

§8.6 built the comparison and reported its first pass. Two things happened
next: the rows it found were **fixed**, and then the instrument itself was
**hardened** — and the hardening immediately found a real defect the loose
version had been hiding. Both are recorded here because the second is the more
useful lesson.

#### 8.8.1 The 25 confirmed rows: 25 → 0 unresolved

**14 rows were corrected in the Kotlin — 19 `romaji` fields across 21 option
ids.** These are three different counts of three different things: rows the
detector flagged, fields edited, and option ids touched. They do not have to
agree, and a reader checking one against another should not treat a mismatch
as an error.

The three items a prior turn **deliberately refused to pin** — 見られる,
待たせる and 飲ませる — were resolved on the app side, and the reason is worth
recording because the earlier diagnosis was wrong in an instructive way. The
defect was **not a wrong choice of reading**. It was a **mora count**:

| item | app declared | the Japanese has | what went wrong |
|---|---|---|---|
| 見られる | `mirerareru` | み・ら・れ・る — 4 mora | the fifth mora in `-rareru` does not exist |
| 待たせる | `matasaseru` | 4-mora causative form | the leading mora of the stem was re-parsed |
| 飲ませる | `nomasaseru` | 4-mora causative form | as above |

The earlier turn read these as "the app picked an invalid reading" and
declined to pin on the grounds that a non-word is worse than a wrong romaji
field. The truth is that the app's *letters* were fine and its *segmentation*
was not, which is a different class of error and a different fix. **All 25
confirmed rows now stand at 0 unresolved.**

#### 8.8.2 The corpus-wide option sweep — 1095 options

The detector above can only see options that **own a clip**. A sweep over all
**1095 Japanese options** — including the ones that own nothing and are
therefore permanently outside any audio check — found **15 further instances
of the same copy-paste classes**:

| declared | should be | class |
|---|---|---|
| `hirougohan` | `ohiru` | earlier item's reading pasted forward |
| `okomi` | `oyomi` | same |
| `shigato` | `shigoto` | misspelling |
| `harwa` | `harawa` | dropped /a/ |
| `kyoo` | `kyou` | long-vowel spelling |
| `ie ni te` | `ie ni ite` | dropped /i/ — see §8.8.4 |

**All fixed.** This is the clearest demonstration in the report of the
limitation stated in §8.6.3: an audio-level check can only ever inspect what
has audio.

#### 8.8.3 Consistency sweep over 155 repeated words

155 words are declared more than once across the curriculum. Comparing the
declarations against each other found **14 disagreements** — and **every one
of the 14 is capitalisation only** (`Senen` vs `senen`). These were reported
and **deliberately left alone**. Normalising case is cosmetic churn across a
large number of files, it teaches a learner nothing, and it would have buried
the one change in that sweep that actually mattered (§8.8.4) in a diff of
case-only edits. Reported, not fixed, on purpose.

#### 8.8.4 The instrument was wrong, and fixing it found a real defect

The original comparison normalised both sides by **collapsing repeated
phoneme symbols**. That is a reasonable-looking simplification and it is
wrong: it makes the comparison blind to exactly the contrasts that matter
most, because a geminate and its single-consonant counterpart collapse to the
same string.

It was replaced by **a strict and a loose normalisation**. A row that matches
only under the loose form is no longer scored as agreement: it is reported as
**UNVERIFIABLE**, with the reason stated on the row. The summary line was
renamed so that "agreement" can no longer be read as "the two strings are the
same" — it reads as **adjudicated agreement**, which is the only thing the
method has ever earned.

**The change paid for itself immediately.** Every option of one challenge
declared `ie ni te kudasai` against a text reading 家にいてください — the い was
dropped, so the specification itself was wrong and the audio was faithfully
speaking it. `ɲiite` and `ɲite` both collapse to `ɲite`, so the loose
comparison had been reporting these rows as agreement. Only the strict form
separates them. The declaration is now `ie ni ite kudasai`.

Note what this row is, because it is easy to misread as a contradiction of
§8.6's method: it is the case where **the app was wrong and the detector was
built to catch exactly that.** The audio was a faithful rendering of a
specification that was itself wrong. Faithful rendering of a wrong
specification is still wrong audio, and this is the clearest single
illustration of why §8.8.6 is not a disclaimer bolted on at the end.

#### 8.8.5 The safeguard, so the tables cannot rot into excuses

Every confirmed row above is recorded in an **adjudication table**. A guard
now **fails the run** if an entry in that table is no longer a row the
detector flags.

This exists because of a specific failure mode of this kind of document. A
table of "known issues" is only evidence while it is complete. Once a fix
lands, each stale row becomes a plausible excuse for a fresh discrepancy —
"that is on the known list" — and the list stops being a measurement and
becomes a defence. Making the run fail converts the table from prose into an
assertion, so it cannot quietly stop describing reality.

#### 8.8.6 What all of this proves, stated as precisely as it can be

> **A green spec-vs-audio result proves that the audio matches the authored
> specification. It does not prove that the specification is linguistically
> correct.** A wrong reading that the voice follows faithfully passes this
> check cleanly. **No reading on either language side — Japanese or Spanish —
> has had a native speaker's eye.** Every number in this section is a
> statement about *consistency*, and none of them is a statement about
> *correctness*.

This is the discipline the whole document has been arguing towards, so it is
worth being blunt about the asymmetry: every check in §8, §9 **and §10** is a
machine checking a machine against a file somebody wrote, or against another
machine's output. That is a real check — it is what caught the わ, っ, 日本語,
`muy` and the three `/x/` clips — and it is not a substitute for one person
reading the declared readings and saying "yes, that is how you say it." **That
person has not read them.**

§10 sharpens the asymmetry rather than closing it. A phone recogniser asks a
third question — *is that phone in the waveform?* — and it is a genuinely
different class of evidence from either a string comparison or a cepstral
distance. It is still a machine asking a machine. **The hole in this document
is unchanged in size and shape: it is exactly one native speaker's afternoon.**

---

### 8.9 Content defects the census found along the way (added 2026-09-28)

Verifying pronunciation required a census of what each clip actually says
versus what the lesson frame grades. That census turned out to be an
independent defect-finder, and it found one real curriculum bug and one false
alarm worth writing down.

#### 8.9.1 A graded sentence that dropped a word the audio speaks

`si_no_hubiera_llovido_habria_ido_a_la_piscina.ogg` — the audio says **habría
ido a la piscina**, but the graded FILL_BLANK frame omitted **ido**. A learner
who transcribed what they heard correctly would have been **marked wrong for
hearing it correctly.**

Fixed in the Kotlin. **The audio was authoritative** and the frame was wrong;
the clip is not the thing that gets edited to match a typo in a lesson frame.

A sweep of **all 38 Spanish FILL_BLANK items that carry a Spanish clip** now
finds **0** graded sentences omitting a content word the clip speaks. That
number is a check on the 38, not a claim about the other Spanish challenge
types.

#### 8.9.2 A reported doubled kanji that was the tool's own artefact

A doubled kanji `人人は` was reported in `g_rel_jisho_tsukawanai.ogg`.
**It is a false positive, and it is a false positive produced by the census
tool itself**: its FILL_BLANK reconstruction duplicated the character at the
boundary where the accepted answer ends with the same character the question
resumes with. The doubled form is an artefact of reconstructing the sentence,
not text that ships and not text the audio speaks.

**This is not a curriculum defect and the clip was not touched.** It is
recorded so the same report is not chased a second time, and so the boundary
condition in the reconstruction is treated as a known artefact of the
instrument rather than as a finding.

A sweep for *genuinely* doubled kanji across **3144 options and 687
challenges** found **0**. A zero here is the **expected** result, not a
lucky one: Japanese writes reduplication with the iteration mark 々, not by
repeating the character, so a doubled-kanji detector is looking for a pattern
this corpus should not contain. Recorded as **0 found, 0 expected**, not as
0 found and 0 checked.

---

## 9. The Spanish specification — and what it still does not prove

**An earlier version of this section said Spanish had no declared pronunciation
at all and that no verification method could run on it. That is no longer
true, and the section is rewritten rather than amended.** The old claim was
accurate when written and is now wrong; leaving it standing next to the new
evidence would be the exact failure this document has been guarding against
all along.

### 9.1 What now exists

A **Castilian romanisation was authored for the Spanish corpus.** It lives in
the same field the Japanese side already used — `romaji` on
`ChallengeOptionEntity` — and it is the thing the §8.6 detector needs in order
to run on Spanish at all.

| | count |
|---|---:|
| Spanish clips with a declared pronunciation | **198 of 198** |
| readings on `ChallengeOptionEntity.romaji`, across five curriculum source files | **254** |
| passage-level readings on `ChallengeEntity.romaji`, STORY challenges | **15** |
| **Room migrations required** | **0** |
| **database version** | **15 — unchanged** |

The no-migration result is worth stating explicitly because it was not
assumed. **Both fields were already nullable on the existing schema**, so
declaring a reading needed a curriculum edit and nothing else. There is no
version bump to review, no migration to get wrong, and no window in which a
device could hold a half-populated `romaji` column. Had the fields been
`NOT NULL`, this work would have carried a schema change and all the risk
that comes with one.

### 9.2 The exclusions, which are decisions

Three classes of option carry no declared reading. Each exclusion is a
judgement, not an oversight, and the reasoning matters more than the count.

| excluded | count | why |
|---|---:|---|
| `WRONG_` distractor options | **567** | The app's own rule is that audio speaking a form the item calls an error would teach the wrong thing — see the header comment in the Japanese curriculum files, which is why no distractor owns a clip. Declaring a reading for a distractor would therefore **declare the defect as the intent**: the specification would assert that `matasaseru` is how you say 待たせる, which is the precise defect §8.8.1 removed from the app. |
| English-side options | **16** | The clip speaks the Spanish target, not the option's text. `Coffee` is the label of the choice, not something the audio says. A reading here would compare the audio against a string it was never asked to say. |
| low-confidence rows | **3** | A short SELECT option and a whole STORY passage share one audio file, so the option text and the clip text describe **different strings**. See §9.3. |

### 9.3 The 3 low-confidence rows, left unresolved on purpose

Three rows are recorded as low confidence because one audio file serves two
purposes: a short SELECT option and a whole STORY passage point at the same
clip while describing different text. The conflict **was reported and
deliberately left unresolved.**

Both available fixes are curriculum edits with real costs: rebind the clip so
one challenge owns it, or rewrite passage text to match the option. Either one
silently changes what a learner is asked. When the honest answer is "these two
rows disagree and we do not yet know which one is right", recording that is
worth more than picking one and calling the section closed. **Unresolved, and
recorded as unresolved.**

### 9.4 What the Spanish check proves, and what it does not

The same method as §8.6 now runs on Spanish: compare `G2P(clip text)` — what
the audio pipeline fed Kokoro — against `G2P` of the authored reading.

> **A passing spec-vs-audio check proves that the audio matches the authored
> specification. It does not prove that the specification is linguistically
> correct.** A reading that is wrong and that the voice follows faithfully
> passes this check cleanly. **No reading on either language side — Spanish or
> Japanese — has had a native speaker's eye.**

This is the single most important honesty statement in this document, and it
is stated here as prominently as the technique is, because the technique is
the part that feels like proof. It is not. The Spanish specification is **a
newly written artefact of exactly the kind §9.5 then audited and found 20
errors in** — which is direct evidence, from this very corpus, of how much a
written specification can be wrong while every consistency check passes
cleanly.

**This statement survives the phone-level work in §10 unchanged, and that is
the point.** §10 ran a phone recogniser over all 397 shipped clips and found
the audio matches the intended phonemes on the questions the instrument can
answer. **That is the first half of the same claim, not the second half.** A
recogniser confirms that the decoder produced the phones the string asked
for; it does not read the specification and it does not know that the
specification is right. The Spanish `muy` fix being *heard* (§10.2) and the 20
wrong hand-written readings in §9.5 are not in tension — one is a claim about
the audio, the other about the file, and **only a person can close the gap
between them.** *(As of §11.1, no such person will. The gap is unclosable, not
merely unclosed, and the document reports against that ceiling rather than
implying a check that will never take place.)*

After the `u`+`y` repair in §8.2, a sweep over **185 distinct Spanish clip
texts** finds:

| property | distinct clip texts | status |
|---|---:|---|
| ceceo — written `s` where Castilian requires `θ` | **0** | ✅ |
| seseo — `s` and `θ` collapsed into one sound | **0** | ✅ *string sweep only — superseded, see §11.2* |
| `/ç/` | **0** | ✅ |
| `/θ/` present (deliberate, per §1 and §8.3) | **30** | preserved in the **strings** — the audio is not, see §11.2 |
| `/ʎ/` present (deliberate, non-yeísmo) | **21** | preserved in the **strings** — the audio is unmeasured, see §11.4 |

This certifies two things at once. The **nine Spanish assets** that one
generation script had once wrongly re-rendered with `/ç/` (§8.2) have **not
regressed**. And the deliberate Castilian values from §1 are still there — the
check is not "0 θ and 0 ʎ", which would mean someone had quietly "fixed" the
dialect to please a checker.

The 30 and 21 here are **not** a contradiction of the "70 class-hits, 54
unique clips" in §8.3. §8.3 counted *clips* in a pattern sweep over shipped
assets; this counts *distinct clip texts* in a sweep of the G2P output, where
several clips sharing a text are one row. Different denominators, different
units, both measured.

**One fix to a reading table was found in this class during the work and
corrected** — see §9.5.

**An independent acoustic check now runs alongside this one, and it is
weaker, not stronger.** A phone recogniser was run over all 198 Spanish clips
(§10.2, §10.4) and also found **0 /ç/ in 198 clips**. But it **cannot hear
/θ/ at all** — /θ/ is emitted for only **27%** of correct /θ/ and the same
symbol is used for /s/ — so **no ceceo-versus-seseo conclusion is available
from acoustic evidence in either direction.** The 30 and 21 above rest on the
G2P-side certification, and that remains the guard on the nine reverted
assets. The two agreeing zeros are a consistency check between two instruments
of different classes, **not** two independent confirmations of the dialect
choice.

**The seseo row above is superseded by §11.2, and the way it was wrong is the
point.** Every row in that table comes from sweeping the **G2P output** — the
phoneme strings the pipeline handed Kokoro — so a ✅ beside "seseo" certified
that no *string* collapses `θ` into `s`. It could not say anything about the
render, and in the event the render does not contain an audible interdental at
all. Read that row as what it always was: a guard on the strings. It remains
exactly the right guard on the nine reverted `/ç/` assets.

### 9.5 The generalisable lesson: hand-written overrides are the risk

Most Spanish readings were produced by a **rule engine**; hand-written
overrides supplied the rest. The two were then audited against each other:

| | count |
|---|---:|
| hand-written entries audited | **173** |
| …that differ from the rule engine's value | **63** |
| …where **the override was wrong and the engine was right** | **20** |
| …where the override was right and the engine was wrong | **43** |

**All 20 were corrected.** Among them:

* `gracias` and `quizá` written with **`/s/` where Castilian requires `/θ/`** —
  the dialect this app deliberately teaches, lost in its own reading table.
* `calle` and `silla` written with **yeísmo** — `ʝ` where the app's §1 dialect
  decision requires `ʎ`. The other half of the same decision.
* trill/tap errors in `cerrado` and `reservar`.

> **Every wrong reading in that table came from a hand-written override. Not
> one came from the rule engine.**

That asymmetry is the finding. A rule that is wrong is wrong **uniformly** and
is found by testing the rule. A hand-written entry is wrong **one word at a
time**, looks exactly like a right one in a diff, and is invisible to any
sweep that only checks the pipeline. That is why §8.5 now carries this as a
standing rule: **a hand-written reading may not silently differ from the
generated value for the same word without a stated reason.**

It is also the cleanest available argument for the disclaimer in §9.4. The
engine half of the Spanish table was right and the machine agreed with it. The
hand-written half was wrong 20 times, and **no green detector result caught any
of those 20** — they were found by a human reading one list against another.

### 9.6 What is still unverified

The structural hole is closed. **The linguistic one is not**, and §10 did not
close it either — it closed a *different* one. The distinction is worth
stating without softening, because §10 is the newest evidence in this
document and the easiest to over-read.

**What §10 changed.** The **Spanish specification now exists** — 198 of 198
clips have a declared Castilian reading (§9.1) — and **a phone recogniser was
run over all 397 shipped clips**, over the audio rather than over strings. It
confirmed the Japanese わ and っ fixes at the phone level (**0 confirmed
Japanese audio defects**, §10.1), found and fixed a real Spanish `/x/` → `/ʃ/`
defect in three clips (§10.2), and bounded itself hard: two of the three
instruments in that work returned limits or nulls, and those limits are
results (§10.4–§10.7).

**What §10 did not change.** It did not read a single declared reading. **No
reading on either language side — Spanish or Japanese — has had a native
speaker's eye, and none has.** A phone sequence is not a judgement of how
speech sounds: it cannot weigh naturalness, accent, rhythm, stress realism, or
native likeness, and every green result in §10 is a statement about which
phones are in the waveform. **The one human check this document has been
arguing for is still the one it does not have — and as of §11.1 it is not going
to.** That is a permanent constraint on the project, not a pending task, and it
changes how this subsection should be read: the items below are not a queue of
work waiting to be assigned, they are the boundary of what will be known about
this audio.

**This subsection has been partly overtaken by §11, which is the newest evidence
in the document and the easiest to under-read.** Superseded items are marked
in place, with the original text left visible and the reason given, rather than
silently edited out.

**Unverified, on both language sides:**

* **That any declared reading is linguistically correct.** No reading on
  either side has had a native speaker's eye. Every green result in §8, §9
  and §10 is a statement about the audio matching a written file or a written
  intent, and nothing more (§8.8.6, §9.4, §10).
* **That the Castilian values are what the app should teach.** *Partly
  superseded — read §11.2 and §11.4 before this bullet.* §9.4 certified that `θ`
  and `ʎ` are present **in the phoneme strings** and that no `ç` or seseo has
  crept into them. It did **not** certify the choice, which is still the open
  product decision §1 describes. Two of its three results have since been
  overturned *about the audio*, and the difference matters: the interdental is
  **not audible** in the shipped renders (§11.2) and the lateral is
  **unmeasured** — no instrument here has a validated cue for `/ʎ/` versus
  `/ʝ/` (§11.4). Only the trill (§11.4) and `/x/` (§10.2) survive from that
  certification as statements about the audio.
* **The provenance of the three Spanish voices is unsourced, and the claim that
  was built on it is withdrawn.** This section used to state that `ef_dora` is
  Castilian and `em_alex` and `em_santa` are American. **VOICES.md publishes no
  nationality, region or origin for any Spanish voice** — its Spanish section
  carries only a name, a gender glyph and a hash, and a search of the file for
  `mexic`, `spain`, `castil`, `latin` and `accent` returns nothing. The
  Castilian/American reading was an inference from voice *names* and general
  recollection, not a source, and it is removed rather than softened. **This is
  now a live contradiction**, not a stylistic quibble: §11.3 records that CSS10's
  `es_ES` label is "a Coqui path convention, not a fact about the speaker —
  the same trap `ef_dora` already set". The trap is the same in both cases, and
  a label that does not track the recording cannot select a variety. The
  variety of the shipped audio is **not established**; only the phoneme string
  the pipeline asked for is.
* **Spanish voice quality.** VOICES.md grades no Spanish voice. "ef_dora is
  first available" is not a quality claim; it is an ordering of the list.
* **Whether `ll` survives as `/ʎ/` in the shipped Spanish audio.** The G2P
  emits 37 /ʎ/, but the phone recogniser cannot separate /ʎ/ from /ʝ/
  (p=0.37). **Unverified** (§10.4) — and this one survived a second look rather
  than being overlooked: §11.4 re-examined it with a purpose-built evaluation
  and **closed it as unmeasurable**, because the band measure's yeísmo
  sensitivity control is +0.51 dB and cannot separate a contrast that certainly
  exists. It is not the lateral's absence that is established; it is that this
  project cannot see it either way.
* ~~**The 6 unresolved Japanese geminate clips** (§10.1) — two nasal
  geminations, two sibilant geminations, two stop geminates. Reported as
  unresolved, not as clearances.~~ **All six are now closed (§11.6).** The
  composition in this bullet was also wrong and is corrected here rather than
  left to diverge: the true split is **two nasal, three sibilant, one stop** —
  `issatsu` is 一冊, `/sː/`, a sibilant, and `matta` is the only true stop.
  **Four are clearances**: the three sibilants at 2.50–3.50× the corpus's own
  single median, each exceeding every one of ~160 singles, plus `matta` as a
  90.0 ms closure against 172 non-geminate clips (median 40 ms, p95 60 ms, max
  74 ms) of which **0 reach 90 ms**. **Two are closed as unmeasurable** — the
  nasal geminates, where no instrument here can separate a geminate from a
  long single nasal. None of the four is a reported defect; neither of the two
  is a reported clearance.
* **`dijimos.ogg`** (§10.4) — the evidence does not separate "different
  audio" from "instrument noise". **Not patched. Unverified.**
* **The 14 Spanish clips still unclassified by acoustic measurement** (§8.4).
  Their G2P strings are now certified, and the §10 recogniser provides a
  usable one-sided `/x/` test, but it was not run per-clip on these 14 and
  **cannot name /ç/` at all**. **Unverified.**
* **The 3 low-confidence Spanish rows** (§9.3) — reported, not resolved.
* **The 14 capitalisation-only disagreements** among repeated Japanese words
  (§8.8.3) — left alone deliberately, so a learner-visible inconsistency
  remains in the app for the sake of a clean diff.
* **Options that own no clip.** 15 Japanese options were wrong (§8.8.2) and
  were only findable by reading the options. The 567 `WRONG_` Spanish options
  and 16 English-side options are excluded by design and are **not covered by
  any audio check at all.**
* **Short-utterance weakness** (§8.4) — unchanged and unaddressed.
* **Perceptual audibility of any G2P fix in §8.2 — all of them, on both
  languages.** §8.7 attempted it for わ and っ with a cepstral method and
  returned a null; §10.6's targeted duration analysis failed both of its own
  gates; and §10.7 has since shown the pipeline is **nondeterministic at
  −18.5 dB relative RMS**, which is one reason that method's noise floor was
  as high as it was. §10 **confirms which phones are present and nothing about
  how large any of it sounds.** `muy` is now confirmed *present* (§10.2); the
  `corras` stress fix is settled by the accent rule; neither is a claim about
  audibility.
* **Whether Kokoro's nondeterminism is in the raw waveform or only in the
  Opus encode** (§10.7). Until that is separated, it is **unverified** whether
  pre-fix audio read from git is comparable at all with freshly rendered
  post-fix audio — which is the precondition for any future re-run of §8.7.

**What would close the main one:** a native speaker reading the declared
readings on both language sides and saying yes. **That is not going to happen
(§11.1).** This paragraph is left in place as written, because the sentence
after it — that a phone recogniser does not substitute for it and this document
does not claim it does — is exactly right, and it is now the operative
statement: **the check is unclosable, not merely unclosed.** The document stops
at that ceiling and reports against it honestly rather than implying a
verification that will never take place.

## 10. Phone-level verification — the audio was listened to (added 2026-09-28)

Everything in §8 and §9 works on **strings**: a phoneme the pipeline was asked
for, or a phoneme a specification file declares. Both checks are real, and both
are limited in the same way — they cannot tell whether the decoder *produced*
the sound that was requested. This section is a different class of evidence.
An acoustic phone recogniser, sharing no weights, no training data and no code
with Kokoro or misaki, was run over the shipped assets and asked **which phones
are actually present in the waveform.**

> **What a phone sequence can do:** show which phones are present.
> **What it cannot do:** judge naturalness, accent, rhythm, stress realism, or
> whether anything sounds native.
>
> A green detector here proves the audio matches the intended string. It does
> **not** prove the intended string was linguistically right, and nothing in
> this section should be read as a judgement of how the Spanish or Japanese
> sounds. The asymmetry stated in §8.8.6 and §9.4 is unchanged by anything
> here — it is only the first half of each claim that is now measured.

Two of the three instruments used in this work returned limits or nulls. Those
are results, and they are reported as results below (§10.5, §10.6, §10.7).

### 10.1 Japanese: the わ and っ fixes are confirmed in the audio

The instrument is **`prj-beatrice/japanese-hubert-base-phoneme-ctc-v4`** —
HuBERT-base CTC, pretrained on `rinna/japanese-hubert-base` and fine-tuned on
ReazonSpeech. It shares **no weights, no data and no code** with Kokoro or
misaki; the one shared dependency is that pyopenjtalk and misaki both read
unidic, so every comparison's *intended* side is derived from this project's
own patched `KPipeline` and mapped by hand, never read off pyopenjtalk.

The briefed first choice — the w-okada VOICEVOX-family models
(`w-okada/voice-japanese-phoneme-ctc-v2`, `…-phoneme-vits-v1`) — **returned
HTTP 401 on the Hugging Face Hub** for every `w-okada` repo and was
unreachable; an `author=w-okada` listing returns an empty set. The substitute
was chosen on what it could measure, and the failure is recorded rather than
quietly worked around.

The intended side is produced by the same patched pipeline the renderer uses,
and confirms the patch is live: **intended IPA over 199 clips contains ɸ in 56
clips, β in 0, ʔ in 0.**

**わ / ɸ versus β — 0 defects.** 56 clips contain an intended ɸ. The
recogniser's 48-token inventory has **no β and no distinct ɸ**, so the question
it can answer is not "ɸ or β" but the one the bug actually turns on: **is this
labial fricative voiceless or voiced?**

| what was heard at a ɸ position (63 positions across the 56 clips) | positions |
|---|---:|
| voiced labial approximant (the natural Japanese [w]) | 36 |
| voiceless labial fricative (the intended ɸ) | 25 |
| nothing | 2 |
| **voiced labial fricative — the β bug** | **0** |

The false-alarm rate is **0/24**, measured against genuinely voiced ば. So the
β bug is **absent from all 199 clips**, and that statement is bounded by the
instrument in the one direction it can bound: a voiced-labial reading would
have appeared. Silence from the detector on a position proves nothing; here the
positive result is that the buggy reading is **not** there.

**っ geminate versus ʔ — 0 defects, 6 unresolved.** The inventory has no
glottal-stop token, and ReazonSpeech contains none, so a ʔ could only decode as
whatever it acoustically resembles. `cl` is the geminate token. Over the 199
clips, against a ground truth taken from the G2P rather than from the
recogniser:

| | |
|---|---:|
| clips whose intended IPA has a geminate | 33 |
| hit rate (`cl` heard where a geminate was intended) | **27/33** |
| clips with no intended geminate | 166 |
| false-alarm rate (`cl` heard where none was intended) | **0/166** |

**27 of the 33 affected clips are confirmed to contain a real oral geminate**,
at a false-alarm rate of **0/166**, so the glottal stop is **definitively
absent** in those 27. A glottal stop has no oral release and cannot produce a
`cl` token.

**6 clips remain unresolved**, and are reported as unresolved rather than as
clearances. Their causes are coherent, not mysterious:

> **Superseded by §11.6: four of these six are now clearances and two are closed
> as unmeasurable.** The composition in this list is also corrected there — the
> true split is **two nasal, three sibilant, one stop**, not two of each:
> `issatsu` is 一冊, `/sː/`, a sibilant, and `matta` is the only true stop. The
> three sibilants are confirmed present by duration against the corpus's own
> single-consonant median, and `matta` by its 90.0 ms closure against 172
> non-geminate clips, none of which reach 90 ms. The two nasals could not be
> adjudicated by any instrument this project has. **The reasoning below still
> stands** as the account of *why this particular instrument could not answer
> them*; it was the right answer about the instrument, and a better one was
> needed.

| clip | text | intended | heard | why it is not answerable |
|---|---|---|---|---|
| `konnichiwa` | 今日は | `koɲɲiʨiɸa` | `k o N n i ch i w a` | nasal gemination: Japanese /nː/ is a **long nasal**, not an oral geminate; CTC collapses it |
| `ja_vol_minna_de_densha_ni_norimashou` | みんなで電車に… | `mʲinna …` | `m i N n a …` | same, ん+な |
| `ja_keigo_ossyaru` | おっしゃる | `oɕɕaɾɯ` | `o sh a r u` | sibilant gemination /ʃː/ is a **long fricative**, collapsed |
| `kinou_zasshi_o_kaimashita` | きのう、雑誌を… | `ʣaɕɕi` | `… z a sh i …` | same, っ+し |
| `matta` | 待った | `matta` | `m a t a` | /tːa/ vs /ta/ is near-ambiguous without lexical knowledge; `cl` posterior **0.002** — the model actively denies a geminate |
| `issatsu` | 一冊 | `issaʦɨ` | `i s a z u` | っ+さ, same ambiguity; `cl` posterior **0.037** |

Weak secondary evidence, offered as such: all six still contain an ordinary
oral consonant (`t`, `sh`, `N n`, `z`) at the position, which a glottal stop
cannot produce. That is weaker than a `cl` hit and is **not** counted as a
clearance.

**Total confirmed Japanese audio defects: 0.** No clip was re-rendered, no
reading was pinned, and `g2p_fixes.py` was left byte-for-byte as found, because
no disagreement was judged to be a genuine audio defect. Pinning a reading to
match a *symbol convention* would replace correct audio with a different word —
the failure mode overrides exist to prevent.

The two classes the work was built around, β and ʔ, are both **absent from the
shipped audio**. That is a statement about which phones are in the waveform. It
is not a statement that the audio sounds good, natural, or idiomatic, and
§10.1 is not read that way.

### 10.2 Spanish: a real defect, found by listening

The Spanish instrument is **`facebook/wav2vec2-xlsr-53-espeak-cv-ft`**, chosen
because it emits **espeak phonemes** — the same inventory the app's own
espeak-based Spanish G2P produces, so the two sides speak the same notation
directly. The other candidate, `facebook/wav2vec2-lv-60-espeak-cv-ft`, was run
over the **identical 380-stimulus calibration set** and rejected on the
measurements, not on download counts:

| | xlsr-53 | lv-60 |
|---|---:|---:|
| exact phone-sequence recovery on correct audio | **8%** | 1% |
| `/x/` and `/ç/` retained as distinct tokens at all | **yes** (8/10, 4/10) | **no** (0/10 each) |

**The defect: `/x/` was heard as `/ʃ/` in three clips**, always in `dixo` /
`hizo` / `hice` position. The app was effectively saying *dixo* with the sound
of "sh".

The control that rules out the instrument — rendered from the same pipeline,
in the same context, at ten rates:

| stimulus | heard |
|---|---|
| `nos ðˈixo ke` (what the app asks for) | **/x/ 10/10** |
| `nos ðˈiʃo ke` | **/ʃ/ 10/10** |
| `ðˈixos aβˈiamos` | **/x/ 10/10** |
| `ˈdixo` isolated | /x/ 10/10 |

Durations agree to within **0.03 s**, so these are the same utterance said
differently, not two different utterances. The model never substitutes /ʃ/ on
a correct render, and the difference is in the shipped audio.

Before → after, heard phone sequences, measured on the shipped assets:

| clip | before | after |
|---|---|---|
| `la_maestra_dijo_que_habia_terminado.ogg` | `l a m a e s t r a d i **ʃ** o k e a v i a t e r m i n a ð o` | `l a m a e s t r a d i **x** o k e a v i a t e r m i n a ð o` |
| `nos_dijo_que_habiamos_perdido_el_tren.ogg` | `n o s ð i **ʃ** o k e a v i a m o s p e r ð i ð o e l t r e n` | `n o s ð i **x** o k e a v i a m o s p e r ð i ð o e l t r e n` |
| `la_llamada_que_nunca_hizo.ogg` | `a dʒ e r l e ð i **ʃ** e k e a v i a t e r m i n a ð o …` | `a dʒ e r l e ð i **x** e k e a β i a t e r m i n a ð o …` |

**`muy` is confirmed by listening.** The earlier `u`+`y` repair (§8.2) is no
longer a string-level claim: the shipped clip contains **no [j] and no [w]**
anywhere. That is the signature of the patched reading, not of the old [j]
reading. The recogniser's one-sided test backs it: /j/ is heard in **9 of 10**
renders of `mˈuj` and **0 of 10** of `ˈmui` (p=0.00012), with a false-alarm
rate of 3/75 = 4% on correct audio.

**`corras` → `koˈras` is settled without any perceptual judgement at all.**
espeak was stressing `kˈoras` on the wrong syllable. A two-syllable LLANA word
ending in `-s` cannot be stressed on its first syllable, so this follows from
the accent rule and the spelling alone, and no listening can improve on it.
**The recogniser heard no change** — before `n o k o r a s k o m p ɾ i s a` and
after the same sequence — and that is exactly what should be expected: stress
was never inside this instrument's calibration. An inconclusive instrument is
not a reason to undo a correct fix, and none is claimed here. What the fix does
prove is narrow and worth stating: the *letters* are identical before and
after, so the change is a stress mark and nothing else.

### 10.3 A lead that was raised, investigated, and closed

Recorded rather than quietly deleted, so that a reader of the final document
cannot mistake silence for a decision never taken.

A **`quería` "trill defect"** was raised, investigated, and **withdrawn as
unfounded**. It rested on two mistakes:

* **The espeak symbols were read backwards.** In espeak IPA `r` **is** the
  trill and the rhotic hook `ɾ` **is** the tap, so `kerˈia` already carried the
  trill. The corpus word in the flagged clip is `querría`, written with a
  double `rr`, and espeak gives it the trill. There was never a defect there.
* **A false premise about `q`.** A written `q` never takes a trill. The `u` in
  `qu` is a silent orthographic filler that exists so the following letter is
  read as `e` or `i`; `que` is /ke/ with no rhotic at all. All **13**
  `qu`-before-vowel words in the corpus are correct.

The principle underneath the withdrawn lead was sound — an orthographic fact
needs no acoustic confirmation — and applied properly it found a real defect,
the `corras` stress error above. The lesson kept: **check the notation before
reporting a defect in the notation.** A recogniser's symbol convention is
where this class of error comes from.

### 10.4 What the Spanish instrument cannot establish

This matters as much as what it found, because it bounds every Spanish claim
in this section. Stated plainly:

* ~~**It cannot hear /θ/ at all.**~~ **Superseded and inverted by §11.2.** This
  bullet was correct about the *instrument as used* and wrong about the
  *instrument*. The 27% figure was the recogniser's recall on 30 corpus words;
  once it was measured on audio where `θ` is certainly present, the same
  instrument came back at **52.1% recall against a 2.1% false-alarm rate** on
  espeak-ng's genuine interdental — a 25× likelihood ratio, so it is not
  `θ`-blind. The 27% sat low because its word set was 30 different corpus words
  rather than 16 minimal pairs, not because the instrument could not see the
  phone. **The conclusion is now available and it is negative**: the app's own
  audio emits `θ` for 6.2% of the same words at a 0% false-alarm rate (§11.2).
  The 33 `θ→s` disagreements across the corpus remain the instrument's
  **notation**, but the reason no dialect conclusion was drawn from them was the
  missing calibration — and the calibration is what supplied the answer.
* **It cannot separate /ʎ/ from /ʝ/** (p=0.37). Whether `ll` survives as /ʎ/
  in the shipped audio is **unverified** — and §11.4 confirms this rather than
  overturning it, having re-attacked the contrast with a purpose-built
  evaluation and closed it as **unmeasurable**: the band measure's yeísmo
  sensitivity control is +0.51 dB and cannot separate a contrast that certainly
  exists, so no number is admissible in either direction. The G2P emits 37 /ʎ/
  and the audio was rendered from that string, but no instrument this project
  has can confirm what came out of the decoder.
* **It cannot adjudicate trill versus tap two-sidedly.** /ɾ/ is recovered in
  only **4 of 10** tap renders.
* **It cannot name /ç/.** /ç/ is recovered in only **4 of 10**
  deliberately-rendered ceceo stimuli; the rest come back /h/, /i/, /j/. A
  /ç/ *absence* across the corpus is meaningful only because the false-alarm
  rate is **0%** and because the one-sided /x/ test shows a ceceo stimulus is
  not a velar one. On that basis, the corpus-wide sweep found **0 /ç/ in 198
  clips** — a meaningful zero, and the only way it is meaningful.
* **Exact phone-sequence recovery on correct audio is 8%** and the per-phone
  error rate is **27%**. Only 13 of 198 clips match their intended phone string
  exactly. **This instrument is a detector, not a judge.**
* **It says nothing about naturalness, prosody, stress realism, or whether
  anything sounds native.** No claim of that kind is made anywhere above. A
  phone sequence is not a judgement about how speech sounds.

**Still unresolved:** `dijimos.ogg` — intended `dixˈimos`, shipped heard
`ð ə j iː m ɔ s`, a current-pipeline re-render heard `ð ɨ h iː m ɔ z`. The model
never hears /x/ reliably in that environment, so the shipped clip differs from
both the intended string and a current render, but **the evidence does not
separate "different audio" from "instrument noise" there. Not patched.
Unverified.**

### 10.5 A pre-declared criterion that had no statistical power

Recorded as a methodological lesson, because the shape of it — a check
declared in advance and then found to be unable to answer anything — is the
kind of thing a reader of a results table would otherwise never learn about.

The Spanish calibration **declared its adjudication criterion before touching
the corpus**:

> A contrast is ADJUDICABLE iff (1) the phone is heard in its own condition in
> ≥80% of renders, (2) that phone is absent in the other condition in ≥80%,
> and (3) the median phone-level distance between the conditions exceeds the
> 95th percentile of the same-string null.

It then turned out that **criterion (3) had no power**. The null is the same
phoneme string rendered at ten speaking rates, 405 render pairs; restricted to
the contrast phones, its distance is median 0, **p95 1**, max 3. So the
criterion compared an effect of size about **1** against a null whose 95th
percentile was also **1** — and "inconclusive" would have been reported for a
contrast the instrument separates perfectly. That is a **power failure, not
evidence of absence.** The declared criterion is reported as declared **and** a
second, better-matched statistic is reported alongside it. Neither is hidden.

A second flaw, in the same place. The first run used **5 renders per side**,
and with n=5 the smallest attainable two-sided p-value is 2/C(10,5) =
**0.0079** — above the **α/7 = 0.0071** a seven-contrast Bonferroni test
needs. The test was **unsatisfiable by construction**: it could not have
returned significance at any effect size. Repeats were raised to **10**, so
power was bought with renders rather than with a weaker threshold. The
threshold was not loosened.

**One failure was caught and deliberately kept.** The rule separating notation
artefacts from real audio defects had its notation check **backwards on its
first run**, and turned **164 taps into 164 apparent defects**. It is recorded
in the source report rather than quietly fixed. On that run the *instrument*
was the thing at fault and the rule said the opposite — which is why the
notation question has to be settled before any disagreement is called a defect,
and why §10.4 states the instrument's notation limits before anything it
found is treated as real.

### 10.6 The duration measurement is a null, and it stays one

A targeted duration and waveform-shape analysis of the geminate was attempted
— deliberately, because the fix most at risk of being cosmetic is the one that
replaces a glottal stop with a real geminate, and §8.7.4 could not settle it.

**It failed both of its own gates, and is reported as a failure rather than
as a result:**

* the closure detector **failed its own positive control** — it did not find
  the closure that the control stimuli were built to contain;
* the closure-duration **noise floor could not be established beyond n=1**, so
  there was no threshold to test against.

The worker **correctly refused to convert eleven nulls into a floor.** That is
the whole discipline of this document applied to its own instrument: a null is
not a small number, and eleven nulls are not evidence of a small number.

**Why the null is the right answer rather than a failure.** The phone
recogniser in §10.1 showed that `cl` is an **oral-construction** detector and
says nothing about closure *duration* — it tracks oral constriction, not time.
So the earlier cepstral-distance method in §8.7.4 was **pointed at the wrong
parameter**, not merely noisy. A method aimed at the wrong parameter returns
nothing no matter how many renders it is given, which is precisely what
happened.

> **This is the clearest result in the whole document: choose an instrument
> that measures the property you care about.** A better-tuned version of the
> wrong instrument is still the wrong instrument.

### 10.7 Kokoro is not deterministic, and that bounds every acoustic claim

Rendering the same text **twice** through the identical patched pipeline and
voice produces bit-identical waveforms for roughly the **first 200 ms**, after
which they **diverge at about −18.5 dB relative RMS**. Sample counts always
match exactly. The divergence is in the **neural decoder**, not in the
phoneme stage and not in the input.

The consequence is a hard ceiling, and it applies to this document as much as
to any future work in it:

> **Any pre-versus-post difference smaller than run-to-run divergence is
> uninterpretable**, regardless of how correct the phoneme strings are.

This **independently vindicates the §8.7.4 null**, though not for the reason one
might assume. The cepstral method's noise floor of 0.628 already exceeded its
median effect of 0.222, so the null did not need this to be correct. What this
adds is the reason the noise floor was **so** high: the instrument was asked to
resolve a pre-versus-post difference across a pipeline that does not render the
same waveform twice, so part of what it was measuring as "noise" was the
decoder's own variability rather than measurement error. A null is still a
null; this explains the floor instead of excusing the result.

**One distinction was not fully separated, and is flagged as an open question.**
It is unknown whether the nondeterminism is in the **raw waveform** or only in
the **lossy Opus encode**. That matters, because it decides what a
pre-versus-post comparison means when the "pre" audio is read from git and the
"post" audio is freshly rendered: if the divergence is only in the encode, the
two are still comparable after decoding; if it is in the waveform, they are
not. **Unverified, and it bounds any future use of §8.7's method.**

### 10.8 A silent, reproducible bug in the shared tooling

Found while setting up the phone-level work, and recorded because the failure
mode is more instructive than the bug.

`make_pipeline` in `duo-android/tools/g2p_fixes.py` defaulted to
`model=None`, which kokoro's `KPipeline` treats as **falsy**. The result is a
documented **quiet** pipeline: it yields the *correct phonemes* with
`audio=None` for every chunk. **All four generator scripts used the broken
default.**

The failure mode is what makes it worth writing down. The generators' existing
`if not chunks: continue` guard **does not fire**, because the pipeline *does*
yield a chunk. The `None` then flows into `sf.write`, which raises
`IndexError: tuple index out of range` from **inside soundfile** — a traceback
naming no project file and pointing nowhere near the cause — **while every G2P
check in the script still passes.** Someone debugging this would spend their
time in the audio library, looking for a decoding fault, on a pipeline that
was never asked to produce audio in the first place.

Three things changed, and all three are guard rails rather than one fix:

* the default is now **`model=True`**;
* `make_pipeline` **raises a `ValueError` on `model=None`**, naming the trap
  and the three legitimate ways to call it (a bound `KModel`, `model=True`, or
  an explicit `model=False` for a genuinely phoneme-only pipeline);
* **all four generators now raise rather than skip** when any chunk's audio is
  `None`, so a quiet pipeline cannot reach `sf.write` again.

The generalisable rule, and it is the same one §8.5 now carries as standing
guidance: **a check that passes and an output that is missing are independent
facts, and a script that only checks the first will not notice the second.**
Every G2P assertion in these generators was green while the audio was absent.


## 11. Independent acoustic verification, and the boundary it cannot cross (added 2026-09-29)

This section reports a further verification effort run against the 397 shipped
clips with instruments that share no weights, no training data and no code
with Kokoro or misaki: a CTC phone recogniser, a Viterbi forced aligner, two
neural MOS predictors, and a matched same-sentence comparison against VOICEVOX.
The artifacts live outside the repository under `.scratch/`
(`reference_search/`, `duration_alignment/`, `naturalness/`, `variety_audit/`).
**Nothing in the repository was modified by that work** — no `.ogg`, no Kotlin,
no `g2p_fixes.py`, no documentation.

> **The 397 in this section is the corpus size at measurement time.** Two
> Spanish clips have since been deleted and the corpus is now 395 (196 Spanish,
> 199 Japanese) — see §12.2. The figures below describe work run against 397
> clips and are left standing as history rather than restated; §12 is the
> current-state record.

### 11.1 The verification boundary — permanent, not pending

> **No native speaker will ever review this audio.** That is a permanent
> property of this project, not a scheduling problem and not a task waiting to
> be done. Every other line in this document that says a check "needs a native
> speaker" is describing something that will not happen.
>
> What the automated instruments **can** establish is mechanical: which phones
> are present in a waveform, how long they last, whether two renders of one
> text differ, whether a clip decodes at all and is free of gross artefacts.
> What they **cannot** establish is any judgement of naturalness, accent
> quality, or pedagogical suitability — whether a learner would find a clip
> intelligible, pleasant, or worth imitating. No amount of re-running them
> changes that; it is the boundary of what the instruments know, not a gap in
> the work.
>
> Every result below is reported at that ceiling. A green instrument result
> means **mechanically consistent**, never **good**. The review harness under
> `duo-android/tools/review/` exists for whoever may one day use it; it is not
> a substitute for a check that has happened, and this document does not claim
> it is one.
>
> **One clarification, because it cuts the other way.** This boundary is about
> what the instruments can *judge*, not about what they can *change*. A
> different synthesiser produces measurably different audio — that is the whole
> content of §11.2, and §11.3 now records a permissively-licensed engine that
> does fix the interdental the shipped voice lacks. So the engine choice is a
> **live decision with a measured option**, and this document does not treat it
> as foreclosed. What no engine and no instrument can supply is still missing:
> whether any of it sounds like a person, and whether it is fit to teach.

### 11.2 Spanish: the interdental is not audible — a real defect

**The defect.** On the same 16 minimal pairs (espeak-ng rendered at three
speaking rates, so its column is 48), a CTC recogniser calibrated on
espeak-ng's genuine interdental emits `θ` for:

| voice | θ emitted | false alarms |
|---|---:|---:|
| espeak-ng (`es`, formant synthesis, a genuine interdental by construction) | **25/48 = 52.1%** | 1/48 |
| Kokoro **`ef_dora`** (the voice the app ships) | **1/16 = 6.2%** | **0/16** |
| Kokoro `em_alex` | 1/16 = 6.2% | 0/16 |
| Kokoro `em_santa` | 0/16 = 0% | 0/16 |

Fisher exact, espeak-ng against all three Kokoro voices pooled (25/48 vs
2/48): **p = 1.4e-7**. The instrument is not `θ`-blind — it hears a real
interdental at 25× its own false-alarm rate — so the app's `θ` rate is
indistinguishable from zero *on this measure* and eight times below the
anchor.

**The decisive control, and why it is about the audio and not the instrument.**
96 espeak-ng control renders (48 `θ`, 48 `s`, three rates) were aligned through
the identical path with the forced phoneme sequence held constant, and the
aligner's own log-posterior was read at the two spans:

| | θ log-posterior | s log-posterior | θ − s |
|---|---:|---:|---:|
| **espeak-ng (genuine interdental)** | **−0.93** | −0.26 | **−0.69 nats** |
| Kokoro `ef_dora` | −5.21 | −0.10 | **−5.11 nats** |
| Kokoro `em_alex` | −5.93 | −0.78 | −5.15 nats |
| Kokoro `em_santa` | −4.45 | −4.96 | +0.51 nats |

The model hears espeak-ng's interdental with near-peak confidence, 0.69 nats
below its own `/s/`. Kokoro's `θ` span sits 3.5–5.0 nats below where a real
interdental lands. With the sequence held constant on both sides, that
difference is attributable to the **render**. `em_santa` must be read
differently: its `/s/` is depressed too (−4.96), so its near-zero gap is vocal
uncertainty, not a `θ` that landed.

A third cue agrees in magnitude: the paired 2500–4500 Hz band difference is
−3.44 dB on espeak-ng (8/8 correct sign) and −0.81 dB on `ef_dora` (6/8), about
a quarter of the anchor. Two further measures — relative intensity and
centroid — are reported as **blind**, not as evidence: the anchor itself does
not separate on them.

**Scale.** **40 of the 198 Spanish clips** carry `/θ/` in the intended phoneme
string the pipeline fed Kokoro (§9.4.1's "30" is a count of *distinct clip
texts*, a different denominator). On the learner-visible side, **38 shipped
Kotlin option rows** encode one of the two Castilian-only phones in their
`romaji`: 26 write `z` (=/θ/), 15 write `ll` (=/ʎ/), 3 carry both. All 38
reach a learner — the `romaji` field is rendered in the option list, the
LISTEN transcript, the unit vocabulary list, structure drills and the
dictionary sheet, with no course gate. Language was derived by following the
entity foreign keys rather than assumed, because Japanese `romaji` uses `z` for
/z/ and has no `ll`, so a naive letter search would have been badly wrong.

**Switching *Kokoro's* voice does not fix it.** `em_alex` measures no better on
the decisive posterior cue (−5.15 against −5.11), and `em_santa` is unclear
overall: its `/s/` is depressed as much as its `/θ/`. The problem is not a bad
voice choice among three. **A different engine does fix it, though** —
`MeloTTS-Spanish` is MIT-licensed and produces an audible interdental without
losing the trill — with four substantial caveats, including a stochastic
realisation and an undisclosed training corpus. See §11.3; the decision is the
owner's and has not been made.

**Four candidate Castilian references were tested and rejected** on acoustics,
and §11.3 rejects three of the same four again on licensing. The comparison
above rests on a formant synthesiser because nothing else was available:

| candidate | acoustic verdict |
|---|---|
| Piper `es_ES-davefx-medium` | **Rejected.** Finetuned from the US English `lessac` voice — the acoustic model never saw Spanish data. `θ` 2332 Hz against `/s/` 2491 Hz with overlapping ranges, `/ʎ/` 0.577 against `/ʝ/` 0.585 of span, and 0–1 trill closures indistinguishable from a word with no rhotic at all (12.8 vs 13.0 Hz). |
| Piper `es_ES-sharvard-medium` | **Rejected.** The same three measures were computed on this voice and it fails on the same grounds. The figures are not repeated here because only one voice's numbers were recorded; §11.3 rejects it on licence grounds independently, which is the stronger reason. |
| `facebook/mms-tts-spa` | **Rejected.** Trained on real recorded Spanish, the right shape of candidate — but the whole-word centroid is in the phonetically correct direction by only 512 Hz, correct in 5 of 6 pairs, sign test p = 0.22; localising to the fricative span drops that to 2 of 6, so two measures of the same thing disagree and neither can be trusted. The trill is **reversed**: more apparent closures in the taps than the trills. The model card also never states the source corpus or region, so its variety is unverifiable either way. |
| Coqui CSS10 (`tts_models/es/css10/vits`) | **Rejected — and this one now has numbers.** **0 `θ` emissions across 15 `θ` renders and 0 across 15 `s` renders**, indistinguishable from its own false-alarm rate (Fisher p = 1.000). Its aligner-posterior gap of −1.02 nats looks like a partial pass against espeak-ng's −0.69, but that alignment is **not admissible**: too many spans sit on the posterior floor, and the free recogniser reads a different phone at the forced span, so the gap measures the aligner's compliance rather than the audio. The structural reason is decisive — the render path hands it **orthographic text, never a phone**, so `/θ/` is collapsed onto the letter `z` and it could not be a text-to-interdental system by construction. See §11.3 for the licence and speaker-provenance verdict, which is where this candidate actually fails. |
| Lingua Libre | **Rejected — two independent reasons, plus a label finding worth keeping.** *(1) No target word carries a variety label at all.* The search for a European-Spanish-labelled **target word** returns nothing — `ll_euro_targets.json` is literally `{}`. The target minimal pairs come from **six different recorders** across 15 files, and the per-file metadata carries only `Artist`, `Credit`, `Categories`, `DateTimeOriginal` and `LicenseShortName` — **no variety field of any kind**. No speaker can be selected by variety. *(2) Even if one could, the coverage is too thin and cross-speaker — and this holds regardless of what the labels said.* `casa` is recorded by GlyphEnjoyer alone (CC0) and `caza` by AdrianAbdulBaha alone (CC BY-SA 4.0), so the single most important minimal pair in this investigation, `casa`/`caza`, would compare **two different people**. `llave` (Eavqwiki), `carro` (Rdrg109) and `peso` (Rodrigo5260) are likewise single-recorder words, and only Marreromarco holds both halves of a pair (`masa`/`maza`). A between-speaker difference is precisely the confound this investigation eliminated everywhere else. *(3) Separately, and the reason the earlier spec-derived "European Spanish" route was abandoned: the labels do not track the recording.* Of the 19,000 files in the cached category enumeration, exactly **7** carry an explicit `(European Spanish)` label and exactly **7** an `(American Spanish)` one — **all fourteen by the same speaker, Noaius Paticus, reading the same seven place names** (Ciudad de Cebú, Legazpi, Lucena, Muñoz, Puerto Princesa, Trece Martires, Valenzuela), each recorded once under each label. A label that can be applied twice to one recording selects nothing. That labelled set is **not** the target-word set — a different speaker on a different word set — which is how an earlier draft of this row came to misattribute these words to the target-word recorders. |

**Cost of acting, measured through the real path** (not estimated), so the
decision is visibly not a compute problem: model load 1.6 s once, generate
0.61 s/clip, Opus encode 0.10 s/clip — **0.72 s/clip** over 6 real shipped
clips. Re-rendering the 53 clips carrying `θ` or `ʎ/` takes ~38 s; a voice
change is all 198 clips, ~142 s. All 397 shipped containers pass an Opus-header
audit (version 1, mono, mapping family 0, encoder input 24 kHz), and clips
rendered through the same path pass it too, so a re-render does not regress the
container.

**A word on that compute, since it is now actionable.** The figures above are
measured for **Kokoro** and they are not the route any more. One permissively
licensed alternative has since been tested and passes (§11.3), so the cost
question is live again — though **no MeloTTS timing artifact exists on disk**, so
no per-clip figure is quoted here rather than one reconstructed. The Kokoro
numbers are kept because they are measured, and because the owner should not
conclude the blocker was ever compute.

**What is deliberately not done here.** No audio was re-rendered into the app,
no pipeline was switched, and the declared variety was not changed. Which
Spanish the app teaches, and whether to change engine at all, is the open
product decision §1 describes; the owner has not made it, and this section
records the finding and the options rather than a chosen outcome.


### 11.3 The audio option is open — and MeloTTS-Spanish is the one candidate that passes

§11.2 records a defect and the obvious response is to re-render the Spanish
audio with a voice that produces an audible interdental. **An earlier draft of
this subsection declared that option closed. It is not.** It closed on the
licensing and provenance of the candidates then under test, and a further
candidate has since been tested: **`MeloTTS-Spanish`, MIT-licensed, and the
first in this project's history to pass both powered cues without trading
anything away.** The other routes are still dead, and the caveats below are
substantial — a pass on two cues is not a clean fix — so the option is
**open with four conditions the owner must decide**, not settled.

**Why the licence was the binding constraint.** MeloTTS-Spanish is the first
candidate for which the generated audio can be **MIT-licensed**: the model card
states the weights are free for both commercial and non-commercial use, and the
repository `LICENSE` is the MIT text. That is the opposite of XTTS v2, whose
licence encumbers the output itself, and it is why F-Droid is not an obstacle.
An earlier premise in the project's own notes held that the audio option was
blocked by a Python version; it was not, and it was not blocked by licences
either — it was blocked by the *particular* candidates available at the time.
**One fully-permissive Spanish engine has now been established.**

**What it measures, on the two powered cues, against the anchor.** The criterion
was fixed before the run and applied unchanged.

| cue | espeak-ng anchor | `ef_dora` (shipped) | **MeloTTS-Spanish** |
|---|---:|---:|---:|
| `θ` emission | 52.1% (25/48) | 6.2% (1/16) | **35.4% (17/48)** |
| false alarm | 1/48 | 0/16 | **0/48** |
| aligner posterior gap | −0.69 nats | −5.11 nats | **−0.97 nats** |
| trill recall | 77.8% (21/27) | 85.7% (12/14) | **100% (9/9)** |
| trill false alarm | 29.6% (8/27) | 28.6% (4/14) | **0/9** |

**The 35.4% is itself a correction, and is recorded as one.** The scored
artifact's own summary field still reads 17.7%, because the tally added the `s`
words into the `θ` group and then used that combined length as the `θ`
denominator — `17/96` where the manifest holds **48** distinct `θ` renders
(16 words × 3 rates), halving the reported rate. The bug was caught by counting
the files on disk rather than trusting the printed figure, and the recount is
done by a script that takes every denominator from the file list. **So the
correct figure is 17/48 = 35.4%, and the artifact's stored summary is the wrong
one** — anyone reading `candidate_eval.json` directly will get 17.7%.

**What "passes" means, stated precisely rather than generously.** On the
emission cue, 17/48 against a **0/48** false-alarm rate gives Fisher
**p = 2.7e-6**: the interdental is unambiguously present where the `θ` slot is
and never appears in the `s` slot. Against the **anchor** it is *not*
statistically distinguishable — 17/48 versus espeak-ng's 25/48 gives
**p = 0.15**. That is the correct sense of a pass: **indistinguishable from a
formant synthesiser making a genuine interdental, not equal to it.** On the
posterior cue, −0.97 nats sits with the anchor's −0.69 and nowhere near the
shipped voice's −5.11; no significance is quoted for that cue because only the
mean is recorded, not a distribution to test. **The trill improves rather than
degrading** — 9/9 with 0/9 false alarms, against espeak-ng's own 77.8% — which
matters because a fix that traded the interdental for a lost trill would not be
a fix.

**Four things the owner must decide before this ships.** They are recorded at
the same weight as the pass, because a pass on two cues is not a clean fix.

1. **The training corpus is undisclosed, not dirty.** No primary source names
    it: the README, `docs/training.md`, the `melo/` tree, the model card and a
    GitHub issue search were all checked. There is nothing to evaluate and
    nothing wrong to point at — but it also means the **data → weights chain
    cannot be certified**, which is the one assurance the Piper and XTTS
    candidates at least offered.
2. **The speaker's regional origin is not established, and the app must not
    call this voice Castilian.** The only variety-bearing artefacts are the
    language code `ES` and a folder path `es/ES`; no speaker is named, and the
    model card wrongly declares `language: [ko]` on a Spanish model. **A folder
    path is not a provenance.** This project exists because the app was
    teaching a variety its audio did not deliver, and shipping a voice labelled
    "Castilian" whose origin is a directory name would be the same failure
    wearing a new hat. Describe it as **Spanish with undocumented origin, and
    say so in the credits** — the same standard §9.6 now applies to `ef_dora`.
3. **The pipeline changes, and a certification is lost with it.** MeloTTS does
    its own text normalisation, so `g2p_fixes` leaves the loop entirely. The
    G2P-side certification in `es_phone/FINDINGS.md` §5 — the one that proves
    no spelling in the corpus lacks a `/θ/` — **no longer applies to the
    audio** and would have to be replaced by whatever guarantee MeloTTS's own
    front end offers. That is a real reduction in assurance, not a formality.
4. **The realisation is stochastic and incomplete, and a learner will hear the
    difference.** In the `θ` condition it appears in **17 of 48** renders and in
    **13 of 32** of the independent null-θ set — a third to two-fifths — and
    **never** in the `s` slot, at 0/48. It is not a function of the word
    either: across 16 `θ`-words × 3 rates, four words carry it every time
    (`abrazo`, `peces`, `roza`, `soza`), two in two renders of three (`caza`,
    `loza`), one in one of three (`maza`), and **nine in none at all** (`aza`,
    `bazo`, `cima`, `cocer`, `coza`, `gaza`, `meza`, `paza`, `taza`). And it is
    not even stable per word: the same text in the same context produces `θ` in
    some renders and not others. Whether that is a feature — exposing the
    seseo/Castilian split rather than hiding it — or a defect, in the sense of
    inconsistent model audio, is **a product decision, and it should be made
    knowing the rate rather than discovering it after release.**

**What is settled and what is not.** Settled: a permissively-licensed engine
exists that produces an audible interdental, does not lose the trill, and emits
the phone **never** where it should not. Not settled, and not measurable by the
instruments available: the lateral `/ʎ/` **remains unmeasured for every voice
including MeloTTS** — its aligner gap of +1.90 nats is explicitly labelled
`lateral_gap_unvalidated` and carries no weight (§11.4). The naturalness of
MeloTTS is likewise unmeasured: the MOS resolution limit (§11.5) is far below
what a single wrong phone costs, so a predictor of this class could not have
told us it was better *and could not tell us it is worse*. **No audio was
re-rendered into the app and no pipeline was switched by the work reported
here.** The container requirement is already met: a MeloTTS render re-encoded
through the app's exact ffmpeg call audits through the Opus header as version 1,
mono, mapping family 0, encoder input rate 24000 — the same audit all 397
shipped clips pass.

**The other three candidates are still rejected**, and they are the reason the
other routes are dead:

| candidate | licence / provenance verdict |
|---|---|
| **XTTS v2** (`coqui/XTTS-v2`) | **Not usable as a fix — now measured, and it fails acoustically as well as legally.** *Acoustics:* `θ` emitted for **1 of 96** renders (**1.0%**) at a 0/96 false-alarm rate, posterior gap **−3.93 nats** — squarely in the shipped voice's territory, nowhere near the anchor's. It also has **no built-in Spanish speaker**: all six speakers sampled are XTTS's default cross-lingual list (Aaron Dreschner, Baldur Sanjin, Ferran Simen, Lilya Stainthorpe, Tammie Ema, Zofija Kendrick), so there is no Peninsular voice to select even in principle. *Licence:* gated behind the **Coqui Public Model License 1.0.0**, which permits only non-commercial use. The decisive point is that it **encumbers the output, not merely the weights**: "output" appears in the grant ("non-commercial use of a machine learning model **and its outputs**"), in the definition of non-commercial use, in the notices clause, in the no-liability clause, and in the definitions ("**Use** means anything you do with the model **or its output**"). The common assumption that pre-rendered audio escapes the licence is therefore **not supported by the text**, and accepting it is a legal act that was not taken. |
| **Coqui CSS10 Spanish VITS** | **Not usable as a fix — on every axis.** *Licence:* a self-declared `bsd-3-clause` metadata tag with **no LICENSE file, no licence text, no copyright holder and no year**; the model card is empty apart from its YAML frontmatter. That is an inference of intent, not a grant. *Data chain — clean, and worth recording as such:* LibriVox public domain, Benito Pérez Galdós texts, Apache-2.0 dataset. *Speaker provenance — not established:* the only primary identification is the LibriVox handle "Tux", and `speaker_ids.json` reads `{"tux": 0}`. The `es_ES` framing is a **Coqui path convention, not a fact about the speaker** — the same trap `ef_dora` already set. *Acoustics:* it fails the decisive cue **outright** — **0 `θ` emissions across 15 `θ` renders and 0 across 15 `s` renders**, so its emission is indistinguishable from its own false-alarm rate (Fisher p = 1.000). The structural reason is decisive: the render path hands the model **orthographic text, never a phone** (`tts_to_file(text=word)`), so `/θ/` is collapsed onto the letter `z` and the system could not be a text-to-interdental engine by construction. Its aligner-posterior gap of −1.02 nats *looks* like a partial pass against espeak-ng's −0.69, but that alignment is **not admissible**: a large share of spans sit on the posterior floor and the free recogniser reads a different phone at the forced span, so the gap is measuring the aligner's compliance, not the audio. The two remaining candidate cues — relative intensity and centroid — are **blind**: the anchor, a formant synthesiser making a genuine interdental, does not separate on them either. |
| **Piper `es_ES` (all five voices)** | **Not usable as a fix.** Every one is fine-tuned from an English base voice whose own licence is not permissive — `lessac` from a **research licence agreement** and Ryan under **CC BY-NC-SA 4.0**. The Spanish data being CC BY 4.0 or CC0 is real, but data licences do not constrain the weights. The part that looks cleanest is the part that does not matter. |

**What this changes, and what it does not.** It **opens** the re-render route:
MeloTTS-Spanish is available, permissively licensed, and measurably fixes the
interdental without losing the trill. It does **not** make the variety decision
for the owner, and it is emphatically not a clean fix — the four conditions
above are the substance of the decision, and the third of them (the pipeline
change and the certification it costs) is the one most likely to be
underestimated. So the realistic options are now: switch the Spanish engine to
MeloTTS and accept a stochastic interdental plus an undisclosed corpus and an
undocumented speaker origin, described honestly as Spanish-with-unknown-origin;
keep `ef_dora` and state the variety and the defect honestly; or obtain a
disclosed, permissively-licensed Castilian model if one appears. **None of
these is a decision this document makes.** No audio was re-rendered into the
app, no pipeline was switched, and no licence was accepted in the course of
establishing any of this.

### 11.4 What the voice does get right — and what is still unmeasured

Of the three Castilian contrasts this project declares, **one has been measured
and passed** (the trill), **one has been measured and failed** (the interdental,
§11.2), and **one has not been measured at all** (the lateral). The trill is
**confirmed present**, and realised at least as robustly as the formant
synthesiser used as the anchor manages it:

| | trill recovered | false alarms | |
|---|---:|---:|---|
| espeak-ng control | **21/27 = 77.8%** | 8/27 = 29.6% | sign test p = 0.0059 |
| Kokoro **`ef_dora`** | **12/14 = 85.7%** | 4/14 = 28.6% | Fisher p = 0.006 |
| Kokoro `em_alex` | 6/14 = 42.9% | 4/14 = 28.6% | not separable (p = 0.70) |
| Kokoro `em_santa` | 5/14 = 35.7% | 2/14 = 14.3% | not separable (p = 0.39) |

The shipped voice's trill recall is **higher** than espeak-ng's own at a
comparable false-alarm rate. The two uninformative voices are near-blind on
this instrument — their CTC decodes are largely unrecognisable — which is a
statement about the instrument, not about a missing trill. The app ships
`ef_dora` only.

An earlier closure-counting instrument was built, tuned on espeak-ng, and then
**rejected**: it passed on its tuning words and scored 4 of 6 on held-out
pairs, one of which turned out not to be a trill at all. No number from it is
reported as a result. The trill claim above rests on the recogniser, whose
positive control already passes on the app's own voice.

**The lateral `/ʎ/` is UNMEASURED, and "no yeísmo" is therefore not supported
either.** An earlier draft of this section said the lateral was delivered and
that "no yeísmo" survived on a measurement. **That claim is withdrawn.** It was
inherited from an earlier session's summary and passed along without ever being
checked against its own control, and there is no control behind it.

The reason is the failure mode this document has retired instruments for more
than once. The band measure's **yeísmo sensitivity control is +0.51 dB**, which
cannot separate a contrast that certainly exists. A cue that cannot see the
thing it is pointed at produces no admissible number **in either direction** —
so the paired `/ʎ/`-versus-`/ʝ/` figures carried elsewhere in this work
(p = 0.11 on 10 pairs) are not evidence either, and neither is the aligner
posterior for the lateral.

So the honest state of the yeísmo question is:

* **Not** that the lateral is absent. No instrument here can see the contrast,
  so nothing supports either answer.
* **Not** that the lateral is present. The 5-of-5 pair-discrimination result
  quoted in the withdrawn draft is real, but it carries **no yeísmo sensitivity
  control of its own**: it reports that the two words of each pair decode
  differently, and a difference between two words is not a measurement of the
  `/ʎ/`-versus-`/ʝ/` contrast. The evaluation harness states the position
  outright — there is no validated cue for this contrast, so it produces no
  number — and the one posterior figure it does carry is labelled
  `lateral_gap_unvalidated` precisely because the `θ` calibration does not
  transfer to a different phone without a control of its own.
* **Unknown**, for every voice including the one the app ships.

`duo-android/tools/review/source/AUDIT.md` §4 is corrected to match. **Both**
halves of its original "no seseo, no yeísmo" are now unsupported, and they
failed differently, which is worth being precise about: the `θ` claim was a
check of the text-to-phoneme step written up as a check of the audio, and the
`ʎ` claim never had a check at all — it was propagated across sessions without
its control. Neither is a measurement.

**Why this shape is worth writing down.** A voice whose trill is confirmed
present and whose only measured failure is the interdental does not sound
obviously broken. Every check that reads strings is blind to it, and so is
every neural MOS predictor. A project that verified only what its own pipeline
emits would have shipped it indefinitely and correctly believed it had not.

### 11.5 Neural MOS predictors cannot substitute for a listener

Two predictors were run over all 397 clips: **UTMOS22 strong** and **DNSMOS
P.808** — different training corpora, different architectures, both trained on
human ratings of quality. The instrument check came first, on clean real human
speech, and it is negative.

* **UTMOS22 mis-orders real human speech against synthetic clips.** It scores
  our Japanese clips 3.76 and our Spanish clips 3.63, and scores *real human*
  Japanese (ReazonSpeech) 1.86 and *real human* Spanish (MLS/LibriVox) 2.65.
  It also rates the five known-damaged pre-repair assets (3.67) at or above the
  repaired clips. DNSMOS orders the human-vs-synthetic anchor classes the
  other way round. Two instruments trained on human ratings disagree about
  which speech is better, so **neither absolute number is interpretable across
  languages**.
* **The two predictors barely agree with each other.** Spearman across the
  clips: **ja ρ = 0.153, es ρ = 0.089**. Neither is corroborated at clip level.
  DNSMOS's ranking is substantially a duration ranking (ρ(score, clip duration)
  = 0.58 ja, 0.63 es) — it penalises short clips for being short.
* **The resolution limit, stated explicitly.** The smallest degradation
  reliably detected is **added noise at 30 dB SNR** (0.455 MOS, 24/24 clips)
  or a **fully flattened F0** (1.94, 24/24). Below that:

| phone substitution (same G2P, same model, one phone changed) | UTMOS22 Δ | DNSMOS Δ |
|---|---:|---:|
| `gracias`: `θ → s` — **the project's own defect class** | **+0.04** | **+0.05** |
| `duerme`: `e → a` | −0.24 | **+0.34** |
| `hola`: `o → e` | −0.54 | +0.04 |
| `takai`: `kai → kui` | +0.31 | −0.59 |
| `ame`: `m → b` | −0.29 | −0.32 |
| `la_mesa`: `esa → isa` | −0.49 | −0.22 |
| `arigatou`: `g → k` | −0.05 | −0.18 |
| **mean** | **−0.163 (6/8 fell)** | −0.115 (5/8 fell) |

  A single wrong phone moves the score by about **0.16** on average and
  sometimes moves it **the wrong way**. The seseo case — `gracias` with `/s/`
  where the project requires a dental — is scored *higher* by both
  instruments. **Phone-level correctness is not measurable by these
  instruments at all**, and it is precisely where a human ear is least
  replaceable.

**Conclusion, and it is the same boundary as §11.1 expressed in numbers:** a
neural MOS predictor cannot tell a learner whether they would understand this
voice, whether a clip sounds like a person, or which clips are better than
which. Its usable output is narrow: these 397 clips are decodable and free of
the mechanical damage a MOS instrument can name. The five genuinely damaged
pre-repair renders score −0.20 to +0.18 against the shipped clips — mixed sign,
magnitude under 0.2, that is, noise.

Note the asymmetry with §11.2, and it is the whole point: **the MOS predictors
cannot see a phone-level defect, and the recogniser can.** The interdental
failure was invisible to both MOS instruments — the substitution table above
shows the seseo case scoring *higher* on each — and it took a purpose-built
recogniser with a calibrated control to find it. One class of instrument was
blind to exactly the defect that mattered while another was sharp enough to
measure it, which is why "the clips scored well" was never available as
evidence for them.


### 11.6 Japanese: prosody against matched renders, and duration at the phone level

**Matched-sentence prosody.** For all 199 Japanese clips there is a VOICEVOX
render **of the identical sentence** in two voices, so content, position, text
length and unit count are held constant and only the voice varies. The null is
sayō against つむぎ on the same sentence: the smallest distance two correct
renderings of one text can have.

| measure (median) | Kokoro | VOICEVOX sayō | VOICEVOX つむぎ | sayō↔つむぎ null |
|---|---:|---:|---:|---:|
| rate, mora per total second | **3.30** | 4.79 | 5.56 | 0.72 |
| voiced fraction | **0.87** | 0.79 | 0.83 | 0.029 |
| internal pausing, kokoro − reference (ms) | 0 (ref.) | **−20** | 0 | 15 |
| final drop (st) | **−2.63** | −1.17 | −0.10 | 1.24 |
| F0 range (st) | 8.67 | 5.28 | 8.47 | 3.34 |

1. **The clips are slower**, in **91% of the 199 pairs** — a gap of 1.25–1.99
   mora/s against a cross-voice null of 0.72. This is the strongest
   voice-attributable result in the exercise, and it is the one place two
   independent instruments point the same way (the DTW comparison of §8.7
   adjudicated part of its flagged set to rate as well).
2. **The clips are delivered as one unbroken phrase.** 0.87 of the clip is
   voiced against sayō's 0.79, and internal pausing is 20 ms shorter; both
   exceed the null.
3. **The utterance-final fall is heavier**, but only partly attributable: it
   exceeds the null against つむぎ (−1.80 against 1.24) and not against sayō
   (−1.09 against 1.24), because sayō itself falls −1.17. It is systematic
   rather than a minority outlier — 45% of clips fall more than 3 st at the end,
   against 21% for sayō and 11% for つむぎ.
4. **Narrow pitch dynamics and a heavy statement fall are properties of
   synthetic speech in general, and are NOT defects of this voice.** Kokoro's
   8.67 st of F0 range is as wide as つむぎ's (8.47) and wider than sayō's
   (5.28), and the paired difference sits inside the null. What the earlier
   human-corpus comparison measured — *all three* synthesisers (8.67, 5.28,
   8.47) below the human corpora (10.25 read, 16.99 natural news) — is a
   property of synthetic speech. **That earlier finding is corrected here, not
   retracted: it was a real measurement that does not survive a matched
   comparison.** Declination, initial rise, nPVI and final lengthening all sit
   inside the sayō/つむぎ band and are voice identity, not quality.

   VOICEVOX is a synthesiser, so all of this establishes *Kokoro differs from
   a good synthesiser on the same sentence*. It does not establish *Kokoro
   differs from a human*, and it does not establish that either is better.

**Duration and alignment (CTC forced alignment, frame stride 20 ms).** The
instrument was validated first on the VOICEVOX renders, where the orthography
guarantees the answer: phones with no span 0/199, non-monotonic alignments
0/199, median phone 60 ms. An instrument that cannot do that could not measure
length, and nothing below would be reportable.

* **The long-vowel contrast is present and large.** Long vowels come out
  **2.67×** short ones in the app's audio (60 ms n=1329, 160 ms n=74) against
  **3.00×** in the reference — a known difference recovered on a corpus where
  the spelling guarantees it. **The おばさん / おばあさん distinction is carried
  by vowel length and the voice does realise it.** This had never previously
  been checked at all.
* **Mora timing is less regular than the reference's.** Within-clip CV of mora
  duration is **0.681 against 0.403** on the same sentences (median mora 80 ms
  against 100 ms). Japanese is mora-timed; the app's morae are markedly less
  even. That is a naturalness finding, not a correctness one, and it is
  reported against its corpus-internal reference rather than a textbook.
* **Three previously-unresolved sibilant geminates are confirmed present.**
  Each is 2.5–3.5× the corpus's own median single occurrence, exceeds every
  one of ~160 single occurrences, and carries a healthy posterior:

  | clip | geminate | measured | singles median | ratio | posterior |
  |---|---|---:|---:|---:|---:|
  | `ja_keigo_ossyaru` | /ɕː/ | 140 ms | 40 ms | 3.50 | −2.38 |
  | `issatsu` | /sː/ | 120 ms | 40 ms | 3.00 | −2.26 |
  | `kinou_zasshi_o_kaimashita` | /ɕː/ | 100 ms | 40 ms | 2.50 | −1.82 |

  The earlier "unresolved" verdict on these three is **closed**.

* **The two nasal geminates are now closed as unmeasurable — not merely open.**
  `konnichiwa` /ɲː/ and `ja_vol_minna_de_densha_ni_norimashou` /nː/ are not
  reported as defective and not reported as verified, because **three
  independent instruments have now been pointed at them and none can separate
  a nasal geminate from a long single nasal.**

  1. *The forced aligner's internal nasal boundary* — refused as a ratio
     measured against a 20 ms floor, with posteriors of −15.81 and −11.00.
  2. *The aligner's vowel-to-vowel interval, decomposed with the recogniser's
     own frame labels.* This supersedes the earlier "posterior floor" reason on
     its own. The interval looks like it separates — 100 ms at a geminate site
     against a 60 ms singleton median — but decomposed, **+20 ms of that +40 ms
     gap is nasal frames and +20 ms is CTC blanks the model declined to label.**
     And the nasal component **ties the ceiling of its own null**: on the
     reference, singleton nasal durations are 0 ms ×16, 20 ms ×67 and 40 ms ×16,
     and both of the reference's own geminate sites read exactly **40 ms** — the
     singleton **maximum**, already exceeded by 16 of 99 ordinary single nasals.
     A positive control that ties the top of its own null is not a positive
     control.
  3. *The waveform itself*, measured with no model in the loop: per 20 ms frame,
     40 ms window, 16 kHz, voicing by autocorrelation peak `r0 ≥ 0.5` plus an
     RMS floor. **Four separate nasal cues were tried and each was defeated by a
     specific vowel** — low-frequency dominance and second-formant presence
     both fail on `/i/`, which is a high front vowel with a low F1 and puts
     nothing at all between 1 and 3 kHz, and the first-pole-bandwidth cue fails
     on `/i/` with the sign inverted (in synthetic voices the nasal's F1 is
     *narrower* than the vowel's, the opposite of a human nasal). The fourth,
     the third-formant region, looked good on hand-picked frames — VOICEVOX's
     `/i/` of み sits 17 dB above the nasal beside it — and then **failed its
     own control**: against a population of **6358 vowel frames** (the middle
     two frames of every pure-vowel slot in both corpora) the third-formant
     ratios reach **−60 dB**, while the geminates' own frames sit at **−45 to
     −47 dB** — *inside* the vowel population, not below it. Across the whole
     operating curve no setting both avoids vowel false positives and puts the
     smaller geminate above the singleton p90; the strict settings read the
     known geminate at 0 ms of murmur, and looser ones let the singleton null
     rise to meet it. **The measure cannot find a geminate that is guaranteed
     to be there, so it is reported blind and not applied.**

  One thing this did settle, and it corrects an earlier reading: there is **0 ms
  of silence** in any of the four target intervals in either corpus, against a
  singleton null whose own p75 is 0 ms. The app's `konnichiwa` frames run at
  `r0` = 0.88–0.96 with an RMS within 10% of the neighbouring `/o/`. The
  "unlabelled frames are room tone" explanation is **retracted** — a CTC blank
  is a blank, not absence of speech, which §10 has already said once for the
  Spanish model. The closure stands, for the reason above rather than the one
  that preceded it.
  **So: all six of §9.6's unresolved Japanese geminate clips are now closed —
  four as confirmed present, two as unmeasurable.** None of the four is reported
  as a defect, and neither of the two is reported as verified. Note the split
  is **4 + 2, not 3 + 3**: the four include `matta`, the only true stop, which
  was adjudicated separately as a long closure — **90.0 ms against a null of
  172 clips whose intended string has no geminate** (median 40 ms, p95 60 ms,
  max 74 ms), and **0 of 172 reach 90 ms** — sitting mid-distribution among the
  27 HuBERT-confirmed geminates (median 92 ms). The three sibilants are the ones
  tabulated above. §9.6's own composition of the six — "two nasal, two
  sibilant, two stop" — was itself wrong and is corrected in place: the true
  composition is **two nasal, three sibilant, one stop**, because `issatsu` is
  一冊, `/sː/`, a sibilant, and `matta` is the only true stop.

* One systematic artefact is reported rather than quietly fixed: the **final
  phone of every clip** absorbs the trailing silence (median 360 ms against
  40 ms for every other phone, in *both* corpora, because the path has to end
  somewhere). It is excluded from every statistic above. Leaving it in inflated
  the apparent mora spread to 0.877 and produced a spurious 846 ms "long
  vowel".


### 11.7 One known inconsistency, reported and deliberately not fixed: `llave`

The learner-visible romanisation for `La llave` is `la llˈabhe`
(`B1CurriculumData.kt:1535`, `source/spanish_romanisation.tsv` row 501190, and
the shipped `tools/review/data/clips.json` all carry the same string), and
`build_table.REVIEW` is where it is pinned. In that table's own alphabet `bh`
is /β/, and the project's patched G2P agrees on the **phone**:
`g2p_fixes.spa_g2p("La llave")` returns `la ʎˈaβe`, which is exactly the
`phonemes` field `clips.json` already ships, and the same /β/ shows up
intervocalically elsewhere (`llamo` → `β` in `Hola βˈejo`).

A third spelling of this row, `ˈllabeh`, has been reported against it. **It
could not be reproduced**: that string occurs nowhere in the repository or under
`.scratch/`, no word in the 270-row table ends in `eh`, and the G2P that the
app actually renders from carries `β`. So the reported disagreement is real as
a *disagreement between sources* but not yet reproduced against an artifact,
and that is recorded rather than smoothed over.

**Status: known, reported, not fixed.** The fix is to reconcile the three
copies of the string — the rule engine's output, the table row, and the Kotlin
literal — and to establish where `ˈllabeh` comes from before changing any of
them. Nothing here is fixed, because a reviewer's first disagreement with the
reading table should not land on a row the project has not reconciled with
itself. See also `tools/review/source/AUDIT.md` §8.

### 11.8 What §11 changes, and what it leaves exactly as it was

**Changed by this section:**

* **"No seseo" is withdrawn** as an acoustic claim
  (`tools/review/source/AUDIT.md` §4, and §9.4.1's "seseo — 0 ✅" row, which is
  a G2P-side sweep of the *strings* and was never evidence about the render).
* **"No yeísmo" is withdrawn too.** It was inherited from a summary and never
  carried a control; the band measure's yeísmo sensitivity control is +0.51 dB,
  so no number exists in either direction. The `/ʎ/` lateral is **unmeasured**,
  not confirmed and not refuted (§11.4). **Both halves of the original "no
  seseo, no yeísmo" are now unsupported**, and they failed differently: one was
  a G2P check written up as an audio check, the other a summary claim
  propagated without its control.
* **The interdental defect is now a documented, unfixed defect** affecting 40
  of 198 Spanish clips and 26 of the 38 learner-visible romanisation rows
  (§11.2). The variety decision in §1 remains unmade; this section makes the
  cost of it visible, not the decision.
* ~~**The re-render remedy is closed** (§11.3).~~ **Withdrawn.** This
  subsection previously recorded that no fully-permissive Spanish engine could
  be established, and that the re-render route was closed on licensing before
  it reached acoustics. **That is no longer true.** `MeloTTS-Spanish` is
  MIT-licensed, passes both powered cues, and improves the trill rather than
  degrading it — the first candidate in this project's history to do so. The
  option is **open, with four conditions** (§11.3): an undisclosed training
  corpus, an unestablished speaker origin that must not be described as
  Castilian, a pipeline change that costs the G2P-side certification, and a
  stochastic realisation a learner will sometimes hear. The three other routes
  remain dead on their own merits: XTTS v2 is gated behind a licence that
  encumbers its output **and** fails the interdental at 1.0% with no Spanish
  speaker, CSS10's licence is an unbacked tag with no speaker provenance and it
  fails the interdental outright, and every Piper `es_ES` voice inherits a
  non-permissive licence through its English base. **No engine was switched and
  no audio was re-rendered; the decision is still the owner's.**
* **All six unresolved Japanese geminate clips are now closed** — **four**
  confirmed present and **two** closed as unmeasurable by any instrument here
  (§11.6). The split is 4 + 2, not 3 + 3, and this bullet previously had it
  wrong.
* **The long-vowel contrast is confirmed**, which had never been checked.
* **The narrow-F0-range finding is corrected**, not retracted (§11.6).

**Explicitly unchanged:**

* **`/ç/` was not re-examined** in this work. The nine assets once wrongly
  re-rendered with `/ç/` are still not reintroduced; that remains a fact about
  the G2P strings, not a fresh acoustic clearance.
* **The trill stays confirmed** — 85.7% recall against a 28.6% false-alarm
  rate, Fisher p = 0.006, above espeak-ng's own 77.8%. That one has a passing
  control and it is the only Castilian contrast in this document with one.
* **No audio was changed, no phoneme string was changed, no licence was
  accepted, and the declared variety was not changed.** No `.ogg`, Kotlin file
  or `g2p_fixes.py` was modified by any of the work reported here.
* **§11.1 is permanent.** No result in this section closes the gap between
  "mechanically consistent" and "good", and no later section may be read as
  having tried to.

## 12. Corpus state after the dialect work (added 2026-09-29)

### 12.1 What this section is, and what it is not

This section records the state of the shipped corpus after a workstream that
was **not** about audio. It exists because the corpus changed underneath the
numbers quoted in §8 to §11, and because for the first time the app now says
something about Spanish varieties to the learner. Every count below was measured
on 2026-09-29 against the working tree; none is carried over from an earlier
section, and where an earlier section and this one disagree, §12.8 lists the
disagreement rather than quietly overwriting it.

Nothing here reopens §9, re-examines a romanisation rule, or softens any
retraction, null, calibration caveat or `UNVERIFIABLE` marker elsewhere in this
document. Where this work found a stale romanisation claim it is **reported in
§12.8 and left in place**, because a separate workstream owns that inventory and
the owner has not yet decided whether to apply it.

### 12.2 Spanish audio: 198 -> 196

Two assets were deleted: `esteis.ogg` and `hubieseis.ogg`.

Both belonged to CONJUGATE challenges `70015` and `80024`, which each gained a
second correct option — the *ustedes* form of the same verb — and therefore no
longer have a single target form for a clip to speak. A clip that names one form
as the answer is actively misleading once two forms are graded correct, which
is the same reasoning §11.3 applies to voice provenance: do not ship an artefact
that asserts something the data no longer supports.

Measured after deletion:

| | count | how |
|---|---:|---|
| Spanish `.ogg` on disk | **196** | `ls assets/audio/es/*.ogg` |
| Japanese `.ogg` on disk | **199** | `ls assets/audio/ja/*.ogg` — untouched |
| all clips on disk | **395** | sum of the two, was 397 |
| distinct Spanish clips referenced from the five Spanish data locations | **196** | `grep -rho "asset:///audio/es/[a-z0-9_]*\.ogg"`, deduplicated |
| references to the two deleted filenames | **0** | `grep -rn "esteis\.ogg\|hubieseis\.ogg" app/src/main/java` |
| on-disk assets referenced by nothing | **0** | 196 on disk, 196 referenced, sets equal |

The reference count and the on-disk count are equal in both directions, so the
deletion left no dangling reference and no orphan. This is the same identity
§10.2 established at 198/198; it now holds at 196/196.

### 12.3 Two new contract tests — and the honest bound on the first

`CurriculumIntegrityTest` gained two tests:

| test | line | what it holds |
|---|---:|---|
| `a rule card that names multiple correct forms has all of them in the options` | 1033 | a rule card that promises two correct forms must have both among the challenge's `correct = true` options |
| `a challenge with multiple correct options carries no challenge-level audio` | 1126 | a challenge offering more than one correct option must not carry a challenge-level `audioSrc` — the clip would name one form as the answer. `WORD_BANK` is excluded, because its clip speaks the whole assembled sentence |

The second test is why `esteis.ogg` and `hubieseis.ogg` were deleted rather than
re-pointed.

**The coverage bound, stated plainly.** The first test skips any rule card from
which it extracts fewer than two plausible form tokens (`if (namedForms.size < 2)
return@forEach`). Its guarantee is therefore *"a rule card whose forms the
extractor can see"* — **not** *"every rule card that names multiple forms"*. The
two are not the same set, and this document should not be read as claiming the
stronger invariant.

There is a **live example that is permanently skipped**:
`SpanishVocabularyCurriculumData.kt:1841`, the *llevar* / *estar* card, which
reads "…with llevar or with estar, and both are correct here." That card is
correctly worded and correctly keyed, and the test still never checks it: the
only text in front of the marker is the English word "and", which the trailing
pattern's stopword guard filters out, leaving fewer than two named forms. The
guard is not what breaks it — before the guard the same pattern yielded the
single token `and`, which is also below the threshold — but the card has never
been verified by this test and is not verified now.

Both tests were proven in both directions. Reverting `estén` at
`B1CurriculumData.kt:1878` to `correct = false` makes the first test fail with
`challenge 70015 rule card names 'estén' as correct but it is not among the
correct options ([estéis])`; restoring it makes the suite green. The guard
removes false positives without silencing that true one.

Measured: **183 tests, 0 failures, 0 errors, 0 skipped**, from a fresh JUnit XML
read after `--rerun-tasks` with the previous XML deleted. The `@Test` count in
source is also 183, so the two agree.

### 12.4 The app now names a region in exactly nine learner-facing strings

Until 2026-09-29 the app made **zero** learner-facing claims about Spanish
varieties. It now makes nine. This is a genuine change in what the product tells
a learner, and it is recorded here because a future reader could otherwise
reasonably assume the app still names no variety — which was true until this
week and is now false.

Measured by parsing every `ChallengeEntity`, `ChallengeOptionEntity`,
`UnitEntity` and `LessonEntity` constructor call across the five Spanish data
locations, and testing every string-valued field — `text`, `question`,
`ruleText`, `acceptedAnswers`, `romaji`, `title`, `description` — for a
locative construction or a variety demonym. **3627 string values** were
scanned.

| | count |
|---|---:|
| values naming a region or variety | **9** |
| of those, in `ruleText` | **9** |
| in `text`, `question`, `acceptedAnswers`, `romaji`, `title` or `description` | **0** |
| asserting a variety for the **audio** | **0** |

All nine are `ruleText` on a `ChallengeEntity`. None appears in a question, an
option, a word-bank tile, an accepted answer, a romanisation, or a title. None
of the nine says anything about how the shipped audio is pronounced.

### 12.5 §11.3 already forbade what this work nearly shipped

§11.3 (lines 2015-2022) states:

> **The speaker's regional origin is not established, and the app must not call
> this voice Castilian.** The only variety-bearing artefacts are the language
> code `ES` and a folder path `es/ES`; no speaker is named […] A folder path is
> not a provenance. This project exists because the app was teaching a variety
> its audio did not deliver, and shipping a voice labelled "Castilian" whose
> origin is a directory name would be the same failure wearing a new hat.

A rule card written during this work ended "The clip is Castilian, so it says
el dormitorio." That is the exact failure §11.3 names, in the one place a
learner reads it. It was caught and removed.

**The corpus now complies.** No learner-facing string calls the shipped voice
Castilian, or Mexican, or European, or names any other variety for the audio.
The nine region names in §12.4 are all about **words** — which noun a region
prefers — and none is a claim about the voice. Where a note must explain a
clip, it states only what is checkable against the file: `AdvancedCurriculumData.kt:34`
ends "The clip says el dormitorio, the form offered here", which is verifiable
against the clip's own filename and the correct option's `audioSrc`.

### 12.6 The standing rule for regional claims in learner-facing text

§11.1 is permanent: no native speaker will ever review this content. A regional
claim therefore **can never be verified later**, which makes hedging useless —
there is no future check that would rescue a hedge. The only safe class of
statement is one that stays true even if the underlying split is wrong. The rule
applied to every one of the nine:

* State regional **defaults**, never exclusions.
* Never assert that two regions use different things for the same thing,
  unless one side is provably region-specific — and nothing here can be proven
  without the review that will not happen.
* Prefer "also used in X" to "used only in X".
* When a usage genuinely cannot be checked, **drop the claim** rather than
  hedge it into vagueness.

Two of the nine were reworded under this rule because their framing was
over-stated, and four were added. Each edited site carries a comment in the
Kotlin source saying the phrasing is weak **on purpose**, so a later author who
"tightens" it is warned that they would be making it false. The clearest case:
`SpanishVocabularyCurriculumData.kt:673` previously read "Spanish clears the
table with the same phrase in two regions", which asserted a two-way split that
cannot hold, because *recoger la mesa* is not Spain-specific. That sentence is
gone.

One claim is recorded here as **UNVERIFIABLE** rather than fixed or dropped:
whether *levantar la mesa* is idiomatic in Mexico, and whether it is genuinely
unused in Spain. The rule card now asserts only a shared default plus an
additive fact, so the unverifiable part is no longer load-bearing.

### 12.7 The variety model: two axes, not two varieties

Record this because it replaces any implicit "Castilian versus Mexican"
framing, which the corpus should not be built around.

The sound layer is **two independent binary axes**:

* interdental — `/θ/` against seseo `/s/`
* lateral — lleísmo `/ʎ/` against yeísmo `/ʝ/`

Four combinations are logically available and **three are attested** in the
corpus. The fourth, `/θ/` with `/ʝ/`, is **not attested** and should not be
assumed to exist in the wild without evidence. This is the same non-transitivity
§11.2 and §11.4 already force: seseo and yeísmo vary independently, and southern
Spain and the Canaries sit with Latin America on both axes, not with Castilian.

The practical consequence is about data modelling, not linguistics. **A named
profile enum hard-codes a model a future variety would have to unwind** — it
cannot express an attested combination that was not anticipated, and it makes
the unattested fourth combination look like a bug rather than an open question.
Represent the axes as axes. The variety decision §1 describes remains unmade,
and this section does not make it.

### 12.8 Numbers this section found stale, and did not change

Reported rather than overwritten, per the working rule for this pass.

| location | says | measured now | why not edited |
|---|---:|---:|---|
| §8.6.1 results table, line 617 | `declared romaji present` 198 / 198 | historical before/after detector run | a historical result, not a current inventory |
| §9.1, line 1148 | `Spanish clips with a declared pronunciation` **198 of 198** | **196 of 196** | inside §9, the romanisation specification this pass is forbidden to touch |
| §9.1, line 1149 | readings on `ChallengeOptionEntity.romaji` **254** | **257** | same |
| §9.1, line 1150 | passage-level readings, STORY **15** | **13** | same |
| §11.3, line 1929 | "a voice change is all 198 clips, ~142 s" | 196 clips | the one genuinely **forward-looking** figure among them; the per-clip cost is unchanged, so the estimate scales to ~141 s |

The eleven occurrences of "397" in §8–§11 are all statements of the form "a
recogniser was run over all 397 shipped clips". Those describe measurements
**taken at a time when 397 was the corpus size**, and rewriting them to 395
would falsify the record of what was measured. They stand as history. This
section is the current-state counterweight.

The §12.1–§12.8 pass above edited no romanisation rule, IPA specification or
reading. §12.9 records a rewrite that was applied by a separate workstream, and
measures what it left behind.

### 12.9 The seseante romanisation rewrite, and how it was finished (added 2026-09-29, amended same day)

**The classification rule, which is the whole point.** Never regex the romaji.
Decide from the sibling `text` field's Spanish orthography, then look up the
romaji symbol. The romaji `z` is ambiguous *and* may be followed by anything:

| source word | orthography | romaji | action |
|---|---|---|---|
| *zapatos*, *cabeza*, *manzanas*, *empezado*, *terraza*, *aplazado* | written `z` before **a** | `zapˈatos`, `kabhˈeza` | **leave alone** — the project treats /θ/ here as already /s/ |
| *estación*, *marzo* | written `z` before **o** | `estazˈion`, `mˈarzo` | `z` → `s` |
| *cocina*, *oficina*, *gracias*, *ciudad*, *cerrado*, *entonces* | `c` before **e**/**i** | `kosˈina`, `ofizˈina` | `z` → `s` |
| *hacía*, *parecía* | `c` before **í** | `asˈia`, `paresˈia` | `z` → `s` |
| *necesitaba*, *necesito* | `c` before **e** (the `ce` of *neces-*) | `nezesitˈabha` | `z` → `s` |
| *llave*, *llueve*, *lluvia*, *llega*, *llegado*, *llegamos*, *llegué*, *llena*, *llamada*, *llamé*, *llamaría*, *llevó*, *billete*, *pastilla*, *pasillo*, *toallas*, *bollos*, *ventanilla*, *maravilloso*, *Sevilla* | `ll` that is the historical lateral **/ʎ/** in Castilian — word-initial, or syllable-medial or syllable-final before a vowel, including where it follows a vowel | `la yˈabhe`, `pastˈiya` | **`ll` → `y` — a seseante change** |
| no corpus word | `ll` that is **/ʝ/ in every variety** — a glide or a consonantal cluster rather than the lateral, e.g. *caballero*, *cabello*. **Neither word appears in the corpus at all** (0 occurrences, so no `romaji`), and nothing here needed rewriting | — | **`y` — alphabet normalisation, NOT a variety change** |
| *Madrid*, *verdad* | word-final `-d` | `madhrˈidh` | trailing `dh` → `d` |
| any | intervocalic /ð/ | `nˈadha`, `mˈadhre` | **leave alone** — out of scope |
| any | `bh` | `fabhˈor` | **leave alone** — `bh` is /β/, never /θ/; /θ/ is `z` |

**Enumeration, which is the proof of exhaustiveness.** Every `romaji` literal in
`AdvancedCurriculumData.kt`, `B1CurriculumData.kt`,
`ExpandedCurriculumData.kt`, `SpanishVocabularyCurriculumData.kt` and the inline
`seedSpanishCourse()` in `LocalProgressRepository.kt` was enumerated and
classified: **270 literals** — 257 `ChallengeOptionEntity.romaji` plus 13
`ChallengeEntity.romaji` on STORY passages, which were classified from the
Spanish passage in their own `question`. The sweep was not seeded from a known
bad list; 270 is the total that exists, measured the same way before and after
the edit, so nothing was added or lost.

**Why the first sweep under-covered.** It swept the romaji with a pattern
roughly `z[ei]`, which matches only a `z` immediately followed by `e` or `i`.
This corpus puts the stress mark directly after the `z` — `estazˈion`,
`ofizˈina`, `azˈia` — so the `z` is followed by `ˈ`, not a vowel, and the
pattern missed every one. The values it did catch were caught by luck. It
reported `remaining: 0`, which its own arithmetic could not support and which
was wrong. **Regexing the respelling cannot work, because the respelling
interleaves stress marks.** Classify from the orthography.

**Reconciliation against the 38 already applied.** 20 literals were rewritten
first, then 18 more, and the originals all survive. Re-measuring after those 38
found **15** literals still carrying a Castilian-only reading — **not 14, and
not 32**. Of the 15, **9 required the rewrite** and 6 were correct as they
stood. The 32 figure was distinct **source lines**: some lines carry a symbol
that needs no change, and some carry more than one literal.

**The nine, read back from disk after editing.**

| file:line | id | text | before | after |
|---|---:|---|---|---|
| `B1CurriculumData.kt:433` | 10330 | Cuando era niño vivía en Madrid | `…en madhrˈidh` | `…en madhrˈid` |
| `B1CurriculumData.kt:446` | 10340 | Cuando era niño vivía en Madrid | `…en madhrˈidh` | `…en madhrˈid` |
| `B1CurriculumData.kt:470` | 10343 | Hacía buen tiempo todos los días | `azˈia buen…` | `asˈia buen…` |
| `B1CurriculumData.kt:483` | 10353 | Hacía buen tiempo todos los días | `azˈia buen…` | `asˈia buen…` |
| `B1CurriculumData.kt:1849` | 700100 | No creo que sea la verdad | `…la berdhˈadh` | `…la berdhˈad` |
| `B1CurriculumData.kt:2582` | 800151 | parecía | `parezˈia` | `paresˈia` |
| `B1CurriculumData.kt:2754` | 80046 | STORY *La llamada que nunca hice* | `…la berdhˈadh…` | `…la berdhˈad…` |
| `SpanishVocabularyCurriculumData.kt:1030` | 929600 | La nevera está en la cocina. | `…en la kozˈina` | `…en la kosˈina` |
| `SpanishVocabularyCurriculumData.kt:2348` | 1010000 | Mi jefe trabaja desde casa desde marzo. | `…dˈesde mˈarzo` | `…dˈesde mˈarso` |

**The six deliberately left alone**, all `z` before `a` and therefore already
/s/ under the project rule: `AdvancedCurriculumData.kt:162` `zapˈatos`, `:203`
`kabhˈeza`; `B1CurriculumData.kt:1011` `empezˈadho`;
`ExpandedCurriculumData.kt:136` `manzˈanas`;
`SpanishVocabularyCurriculumData.kt:703` `terrˈaza`, `:2353` `aplazˈadho`.

**State after the sweep.** Re-scanning all 270 literals for `z`, `ll` and a
trailing `dh` returns exactly those 6 and **0** literals needing a further
change. That is a positive result from the full enumeration, not a
`remaining: 0` asserted from a partial sweep.

**The two `ella` rows are alphabet normalisation, not a variety fix — do not
revert them.** `B1CurriculumData.kt:2241` (`ˈeya se bistˈio`) and `:2855` (`ˈeya
dˈijo kˈe estˈabha lˈista`) are the **second** class in the table above: the
`ll` is /ʝ/ in every variety, so writing it `y` is alphabet normalisation and is
correct for Castilian and seseante alike. The owner was told this and approved
it.

An independent read-only audit flagged both rows as high-severity violations on
the grounds that the `ll` should be `/ʎ/` and therefore `ll`. **That audit is
wrong and the code is right.** It is recorded here because the row above is what
provoked it: a rule phrased by syllable rather than by phoneme reads as though
`ella` were excluded from the seseante class, and a reader auditing against that
row will "discover" a defect that does not exist. The table is now phrased by
phoneme for exactly that reason.

**The `z`-before-vowel G2P defect is real, and the audio is right anyway — and
that is a standing risk, not a clean bill of health.** This was measured
acoustically, not inferred.

*The G2P input is wrong.* espeak-ng's `es` voice emits `θ` for orthographic `z`
before a/o/u, where Spanish is /s/ in every variety. Confirmed against the
recorded output of the real `make_pipeline(lang_code='e')` in
`data/intended.json`. Ten shipped clips are affected:

| clip | intended output |
|---|---|
| `los_zapatos.ogg` | `los θapˈatos` |
| `la_cabeza.ogg` | `la kaβˈeθa` |
| `yo_como_manzanas.ogg` | `ʝˈo kˌomo manθˈanas` |
| `mi_jefe_trabaja_desde_casa.ogg` | `mˈaɾθo` |
| `la_reunion_se_ha_aplazado.ogg` | `ˌaplaθˈaðo` |
| `cuando_llegue_la_pelicula_ya_habia_empezado.ogg`, `el_tren_perdido.ogg` | `ˌempeθˈaðo` |
| `story_901.ogg`, `story_902.ogg` | `terˈaθa` |
| `story_904.ogg` | `plˈaθa` |

Kokoro's vocabulary does contain `θ` at id 119, so the token reaches the
acoustic model as a distinct token; nothing downstream substitutes /s/ in the
phoneme encoding.

*The audio is nevertheless correct.* The phone recogniser cannot separate θ
from s at all — on the 380-stimulus calibration it emitted `s` for 10 of 10 θ
renders and 10 of 10 s renders, p = 0.58 — so θ-token absence proves nothing.
Identification was done on the waveform instead: high-frequency energy ratio,
`hf_ratio` = energy above 4 kHz over energy in 3.5–10 kHz. A dental θ is low,
flat noise; an alveolar s is high and peaked. The measure was validated on
espeak-ng's own synthesiser first — `kaza` 0.111 vs `kasa` 0.825, `zena` 0.010
vs `sena` 0.530 — a clean empty gap, decision cut 0.32.

Handed a θ directly, `ef_dora` scores 0.729 [0.662, 0.828]; handed an s, 0.727
[0.646, 0.809]. The means differ by 0.002 and the ranges overlap completely.
**The voice renders θ as /s/.** That independently corroborates the earlier 6.2%
figure from a different direction. Of the 10 affected clips, the 2 that
produced any θ token both contain other legitimate θ words (*story_901* has
cocina/hace/sucia, *story_904* has centro/ciudad/hacía). Restricted to the 8
affected clips containing no other θ word: **0 of 8**, against 0 of 103 in the
/s/ negative control, exact p = 1.000. The cleanest single case,
`la_cabeza.ogg`, is one word whose only fricative is the θ: `hf_ratio` 0.901,
squarely in the /s/ band.

*The fragility is the point.* The G2P input is genuinely wrong, and the audio is
right **only because this particular voice does not realise the θ it is
handed.** A future voice that does realise it would turn all ten clips into a
defect with no code change at all. This is a permanent risk to the corpus, and
it is a condition to re-check whenever the voice or the G2P front-end changes.

Note that this closes an open question rather than settling the `z`-before-`a`
row of the table above. That row rests on the project rule that orthographic `z`
before a vowel is already /s/; the measurement here supports the rule's
*outcome* for these ten clips in the shipped voice, and does not establish that
/θ/ is impossible in that position.

**Japanese romaji is byte-identical before and after**, over the **812** `romaji`
values in `JapaneseN4CurriculumData.kt`, in four serialisations:

| serialisation | digest |
|---|---|
| file order, newline-joined | `36a09be5f40db4538a2c839cbfc1f3a8ef996206c904b48f68b4c33238c68554` |
| sorted, newline-joined | `7090078a0615aa16dfdd353a9e9af41a085c53435b5f78c1167b1cedc43cba7a` |
| file order, concatenated | `eeb6c786ed584c25aec3e92f3171973e0ccd1678351bb534b55fd644313bd329` |
| sorted, concatenated | `e6e263d1bf7c285891b44283c65dcc072f5a2f953eb4db62fcdf7ad843076550` |

All four identical before and after, computed with the same code over the same
value set on both sides.

**A correction to an earlier figure in this document.** An earlier draft of this
section said **961** Japanese romaji values, and the 1,247 figure quoted in the
same draft is also wrong. Both came from counting *lines containing the
substring* `romaji =` rather than romaji **values**: `JapaneseN4CurriculumData.kt`
has **961** such lines, of which **149 are `romaji = null`** (the N4 vocabulary
block leaves the reading null for its loanword and English items). The true
count is **812**, confirmed three ways — a regex over the file, a line
substring count net of the nulls, and a constructor-level scan (987
`ChallengeOptionEntity` calls of which 812 carry a `romaji`, and 201
`ChallengeEntity` of which 0 do). The digests above are over the correct 812.

**A supplied digest remains UNVERIFIED.** `a1fa6f94…` over 1,042 values was
supplied with this work and **still cannot be reproduced**: the tree holds 812
Japanese romaji values, not 1,042, and none of the four serialisations yields
it. The serialisation it was computed over is not recoverable from the tree, so
it is **not** recorded here as a verified invariant. The before/after identity
above is the claim this section stands behind. The 1,042 figure was propagated
through two briefs without ever being checked, and should not be propagated
again.

The invariant is enforced better in the test suite anyway. Every `romaji`
assertion in it is on **Japanese**: `CurriculumIntegrityTest.kt:869-906` pins a
table of `(option id, text, romaji)` triples — `Triple(6300086, "見られる",
"mirareru")` and its siblings — asserting the text alongside the reading so an
id cannot be re-pointed at different content to make a row pass. The others
assert `"Eki"`, `"Konnichiwa"` and `"hanasemasu"`. **No test asserts on Spanish
romaji**, which is why a partial sweep could report success.

**Test suite.** 183 tests, 0 failures, 0 errors, 0 skipped, from a fresh JUnit
XML read after `--rerun-tasks` with the previous XML deleted. Unchanged, which
is the evidence that this was a text-only change.

**An earlier claim in this section was wrong and is corrected here.** The gap
was described as sitting among distractors, with
`LocalProgressRepository.kt:730` (`de nˈadha`) and `:731` (`por fabhˈor`) as
evidence. Neither is a defect: the `dh` in `de nˈadha` is **intervocalic** and
explicitly out of scope; `bh` is **/β/**, not /θ/ — in this alphabet /θ/ is
`z` — and the statement that "`bh` is /θ/`" was incorrect. Measured across the
whole Spanish distractor population: 6 `correct = false` options carry a
`romaji` at all, and **0** encode /θ/, /ʎ/ or a word-final /ð/. They are
`la mujˈer`, `ˈola`, `de nˈadha`, `por fabhˈor`, `la mˈadhre` and
`bˈuenos dˈias` — all clean. The real gap was in the correct options and the
STORY passages, which is where all nine fixes landed.

**Flagged, not resolved: clips on distractors.** All **6** Spanish distractors
that carry a `romaji` also carry an `audioSrc` — every one of them, in
`LocalProgressRepository.kt` (unit 10, lessons 100-103), and all with
`errorTag = null`. One is unambiguous on its own terms: option `10002`, text
`"La mujer"`, ships `la_madre.ogg` — the clip for a **different word**, which is
the "audio that speaks a form the item calls an error teaches the wrong thing
out loud" case. The rule stated at `B1CurriculumData.kt:36-43` — no `WRONG_`
distractor in units 32-35 carries a clip — is **not violated**: measured, there
are **0** distractors with a clip across B1 lessons 600-605. But all six of the
Spanish instances sit **outside** that rule's stated scope, in the inline seed
and in unit 10. Whether the rule should extend to them is the owner's call and
is not resolved here.

---

## Sources
- https://huggingface.co/hexgrad/Kokoro-82M (model card)
- https://github.com/hexgrad/kokoro (GitHub)
- https://huggingface.co/hexgrad/Kokoro-82M/blob/main/VOICES.md (voice list & grades)
- https://github.com/hexgrad/misaki (G2P library)
- https://github.com/polm/unidic-py (Japanese dictionary)
- https://huggingface.co/prj-beatrice/japanese-hubert-base-phoneme-ctc-v4 (§10.1 Japanese phone recogniser)
- https://huggingface.co/rinna/japanese-hubert-base (its pretraining base)
- https://huggingface.co/facebook/wav2vec2-xlsr-53-espeak-cv-ft (§10.2 Spanish phone recogniser)
- https://huggingface.co/facebook/wav2vec2-lv-60-espeak-cv-ft (the rejected alternative, §10.2)
- arXiv:2109.11680 (wav2vec2-XLS-R, the paper both Spanish checkpoints come from)
- https://huggingface.co/prj-beatrice/utmos22-torch-native (§11.5 UTMOS22 strong)
- https://huggingface.co/prj-beatrice/utmosv2-torch-native (§11.5; run on anchors, then dropped as too slow — a stated limitation, not a silent omission)
- https://github.com/microsoft/DNS-Challenge (§11.5 DNSMOS P.808)
- https://github.com/VOICEVOX/voicevox_engine (§11.6 matched same-sentence reference renders)
- https://huggingface.co/facebook/mms-tts-spa (§11.2, rejected candidate Castilian reference)
- Piper `es_ES-davefx-medium` / `es_ES-sharvard-medium` and Coqui `tts_models/es/css10/vits` (§11.2, §11.3, rejected candidate Castilian references)
- Wikimedia Commons, *Lingua Libre pronunciation-spa* (§11.2, rejected candidate Castilian reference)
- https://huggingface.co/coqui/XTTS-v2/raw/main/LICENSE.txt (§11.3, Coqui Public Model License 1.0.0 — the surviving authoritative copy; the model card's `coqui.ai/cpml` link is dead)
- https://huggingface.co/neongeckocom/tts-vits-css10-es (§11.3, CSS10 Spanish VITS — model card carries a licence tag and no licence file)
- https://github.com/Kyubyong/css10 (§11.3, CSS10 dataset, Apache-2.0)
- https://librivox.org/pages/public-domain/ (§11.3, LibriVox recordings are public domain)

---
*Report generated by kokoro-tts evaluation task. Sample audio in `.scratch/samples/`.*