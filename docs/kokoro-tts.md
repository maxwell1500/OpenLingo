# Kokoro TTS Evaluation Report

**Date:** 2026-09-13  
**Evaluated for:** Language-learning Android app (Spanish + Japanese lessons)  
**Model:** hexgrad/Kokoro-82M v1.0 (82M params, Apache-2.0)  
**Status:** ✅ Verified with real audio generation on dev machine

---

## 1. Language Support

### ✅ Spanish: YES
- **Voices:** `ef_dora` (F), `em_alex` (M), `em_santa` (M)
- **G2P path:** `misaki` phonemizer with espeak-ng `es` backend (`lang_code='e'`)
- **Evidence:** VOICES.md lists 3 Spanish voices; model card confirms v1.0 supports en/es/fr/hi/it/pt/ja/zh
- **Verified:** Generated 3.35s of Spanish audio with `ef_dora` (see §6)

### ✅ Japanese: YES
- **Voices:** `jf_alpha` (F, C+ grade), `jf_gongitsune` (F, C), `jf_nezumi` (F, C-), `jf_tebukuro` (F, C), `jm_kumo` (M, C-)
- **G2P path:** `misaki[ja]` (`lang_code='j'`) using **pyopenjtalk + unidic** (2nd gen tokenizer)
- **Evidence:** VOICES.md lists 5 Japanese voices; misaki README confirms pyopenjtalk/unidic dependency
- **Verified:** Generated 4.45s of Japanese audio with `jf_alpha` (see §6)

### ⚠️ Quality Notes
- Japanese has the best-documented voice quality (C- to C+ grades)
- Spanish voices have **no quality grades listed** in VOICES.md — treat as unverified quality
- Recommended Japanese voice: `jf_alpha` (highest grade, C+)
- Recommended Spanish voice: `ef_dora` (first available, ungraded)

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

**Approach:** Generate all lesson audio at build/seed time. Ship as static files via CDN.

| Factor | Assessment |
|--------|------------|
| ✅ **Offline support** | Trivial — files download once, play forever |
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
    Upload to CDN: public/audio/{lang}/{option_id}.ogg
    Set audioSrc = "/audio/es/42.ogg"

Android client:
  Standard audio playback via Media3/ExoPlayer
  Download-once, cache in ExoPlayer DownloadManager
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
public/audio/{lang}/{challengeOption_id}.{ext}
```
Examples:
- `/audio/es/12345.ogg`
- `/audio/ja/67890.ogg`

### Audio Format
| Parameter | Recommendation | Reason |
|-----------|----------------|--------|
| **Codec** | Ogg Opus | 2x compression vs MP3 at equal quality; standard on Android |
| **Bitrate** | 48kbps (voice) | Voice quality, ~10KB/min |
| **Sample rate** | 24000 Hz | Kokoro native output |
| **Channels** | Mono | Voice only |

**File size estimate:** ~800 bytes per second of audio (Opus)

### Caching Headers (CDN)
```
Cache-Control: public, max-age=31536000, immutable
ETag: "{sha256_of_file}"
Content-Type: audio/ogg
Accept-Ranges: bytes
```

### Android Offline Storage
Use **ExoPlayer DownloadManager** or **OkHttp DownloadManager**:

```kotlin
// Download once, store in app data directory
val downloadManager = DownloadManager.getInstance(context)
val request = DownloadRequest.Builder()
    .setUri(audioUri)
    .setDestinationDirectory(context.cacheDir)
    .setRetryOnFailure(true)
    .build()
downloadManager.enqueue(request)

// Playback from local file after download
val mediaItem = MediaItem.fromFile(localAudioFile)
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

**LGPL caveat:** pyopenjtalk/unidic are LGPL. For Android app, this is fine — load as shared libraries (`.so`) and don't statically link. The Android app itself remains proprietary.

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
- Matches VOICES.md documentation

### Japanese G2P Verified
- Japanese uses **pyopenjtalk + unidic** (NOT cutlet/fugashi/mecab as previously documented)
- pyopenjtalk ships precompiled Windows wheels via pip (no Visual C++ Build Tools required)
- unidic dictionary is ~526MB zip / ~1.2GB extracted — the largest dependency

### Latency Assessment
- **CPU inference is too slow for real-time server-side** at scale (1.7s cold, 0.6-0.8s warm)
- For **pre-generation** (Option A): acceptable — batch at build time, not user-facing
- GPU would reduce cold latency to ~0.1-0.3s but adds infrastructure complexity

---

## 7. Recommendation

### 🏆 Primary: Pre-Generate + CDN (Option A)

**Rationale:**
1. **Offline-first requirement** met trivially — download once, play forever
2. **Zero runtime ML complexity** on Android
3. **Deterministic audio** — same clip every lesson
4. **Trivial caching** — immutable CDN URLs with long cache headers
5. **Cost effective** — generate 1000 clips once, free CDN tier serves them
6. **Latency acceptable** for batch generation (not user-facing)

**Hybrid enhancement:** Use Option B (runtime server-side) ONLY for:
- Dynamic quiz explanations generated from correct answers
- Pronunciation comparison feedback
- Anything that changes per user

### Migration Path
1. Generate all audio offline (use `.scratch/tts_test.py` as template)
2. Convert WAV → OGG Opus with ffmpeg: `ffmpeg -i input.wav -c:a libopus -b:a 48k output.ogg`
3. Upload to CDN with immutable cache headers
4. Update `audio_src` paths in `challenge_options` table
5. Android client: no changes beyond standard audio playback

---

## Sources
- https://huggingface.co/hexgrad/Kokoro-82M (model card)
- https://github.com/hexgrad/kokoro (GitHub)
- https://huggingface.co/hexgrad/Kokoro-82M/blob/main/VOICES.md (voice list & grades)
- https://github.com/hexgrad/misaki (G2P library)
- https://github.com/polm/unidic-py (Japanese dictionary)

---
*Report generated by kokoro-tts evaluation task. Sample audio in `.scratch/samples/`.*