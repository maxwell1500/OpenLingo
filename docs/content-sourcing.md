# Real Course Content Pipeline — Research & Design Report

**Date:** 2026-09-13  
**Target:** OpenLingo, native Android app (Kotlin + Jetpack Compose)  
**Goal:** Replace toy seed data with real, legally-redistributable multi-unit Spanish and Japanese courses

---

## 1. Content Source Survey

### 1.1 Tatoeba

| Property | Details |
|----------|---------|
| **URL** | https://tatoeba.org/eng/downloads |
| **API** | https://api.tatoeba.org/ |
| **License** | CC BY 2.0 FR (text); audio varies per contributor (CC BY / CC BY-SA / CC BY-NC) |
| **Commercial use** | ✅ Yes (attribution required) |
| **Redistribution** | ✅ Yes (attribution required) |
| **Coverage** | 13.6M+ sentences, 400+ languages. Spanish & Japanese are among the largest corpora. Direct Spanish↔Japanese bitext pairs available via custom export. |
| **Size** | Full dump multi-GB; Spanish↔Japanese pair subset: tens of thousands to 100k+ pairs |
| **Format** | TSV (id, lang_code, text); `sentences.tar.bz2`, `links.tar.bz2`, `translations.tar.bz2` |
| **Quality** | High-quality real sentences contributed by native speakers. Some slang, some errors. Community-moderated. |
| **Best for** | Sentence pairs for translation exercises, listening exercises (if audio available), cloze deletion exercises |

**Download options:**
- Custom exports: https://tatoeba.org/eng/downloads → Custom exports (select spa↔jpn)
- Bulk: `sentences.tar.bz2` + `translations.tar.bz2` + `links.tar.bz2` from https://tatoeba.org/eng/downloads
- API: `https://api.tatoeba.org/v3/en/sentences/search/?query=hola&limit=50`

### 1.2 Wiktionary / Kaikki.org (Pre-parsed Wiktionary JSON)

| Property | Details |
|----------|---------|
| **URL** | https://kaikki.org/dictionary/rawdata.html |
| **Parser** | https://github.com/tatuylonen/wiktextract |
| **License** | CC BY-SA 3.0 / 4.0 |
| **Commercial use** | ✅ Yes (attribution + sharealike required) |
| **Redistribution** | ✅ Yes (with attribution) |
| **Coverage** | All languages. Spanish (`eswiktionary`), Japanese (`jawiktionary`). Includes conjugations, declensions, translations, pronunciation, etymology. |
| **Size** | ~1GB per language full dump (JSONL) |
| **Format** | JSON Lines (one JSON object per entry): `{ word, lang, pos, translations, inflection, pronunciation }` |
| **Quality** | Very comprehensive. Machine-parsed so some noise. Translation tables are excellent for vocabulary exercises. |
| **Best for** | Vocabulary entries with translations, conjugation patterns, inflection exercises |

**Example JSONL entry structure:**
```json
{"word":"perro","lang":"es","pos":"noun","gender":"masculine","translations":{"en":"dog","fr":"chien"},"inflection":{"irregular":false}}
{"word":"猫","lang":"ja","pos":"noun","translations":{"en":"cat","es":"gato"},"kana":"ねこ"}
```

### 1.3 EDRDG Dictionaries (JMdict, JMnedict, KANJIDIC2)

| Property | Details |
|----------|---------|
| **URL** | http://ftp.edrdg.org/pub/Nihongo/ (HTTP index: http://ftp.edrdg.org/pub/Nihongo/00INDEX.html) |
| **License** | EDRDG Dictionary License (CC BY-SA 4.0-compatible); commercial use ✅; redistribution ✅ with attribution |
| **Attribution** | Must credit "Electronic Dictionary Research and Development Group (EDRDG)" and link to https://www.edrdg.org/ |
| **Coverage** | **JMdict:** ~160,000 Japanese headwords with English definitions, readings, part-of-speech tags. **JMnedict:** ~300,000 proper nouns. **KANJIDIC2:** ~9,000 kanji with readings, meanings, stroke counts. |
| **Format** | XML (JMdict_e.xml.gz, JMnedict.xml.gz, kanjidic2.xml.gz) with documented DTDs |
| **Quality** | Highest quality Japanese dictionary data; foundation for most Japanese NLP tools |
| **Best for** | Japanese vocabulary with readings (kanji/kana/romaji), kanji stroke order exercises, JLPT-level tagging |

**File downloads (active URLs as of 2026-09-13):**
- JMdict: `ftp://ftp.edrdg.org/pub/Nihongo/JMdict_e.gz` (HTTP: http://ftp.edrdg.org/pub/Nihongo/JMdict_e.gz)
- KANJIDIC2: `ftp://ftp.edrdg.org/pub/Nihongo/kanjidic2.xml.gz`
- JMnedict: `ftp://ftp.edrdg.org/pub/Nihongo/JMnedict.gz`
- License: https://www.edrdg.org/edrdg/licence.html

### 1.4 Frequency Lists

#### Hermitdave Frequency Lists
| Property | Details |
|----------|---------|
| **URL** | https://github.com/hermitdave/FrequencyWords |
| **License** | MIT |
| **Coverage** | Top 50k-100k words per language from OpenSubtitles. Includes `es_50k.txt`, `es_100k.txt`, `ja_50k.txt` (frequency-ranked). |
| **Format** | Plain text, one word per line, ranked by frequency |
| **Best for** | Slicing vocabulary into difficulty tiers (top 1000 = A1/A2, next 2000 = B1/B2, etc.) |

**Download:**
```bash
# Spanish top 50k frequency list
curl -O https://raw.githubusercontent.com/hermitdave/FrequencyWords/master/content/2018/es/es_50k.txt

# Japanese top 50k frequency list  
curl -O https://raw.githubusercontent.com/hermitdave/FrequencyWords/master/content/2018/ja/ja_50k.txt
```

#### Spanish Vocabulary (CEFR approximated by frequency)

| Property | Details |
|----------|---------|
| **URL** | https://kaikki.org/dictionary/Spanish/ (eswiktionary extract) |
| **Parser** | https://github.com/tatuylonen/wiktextract |
| **License** | CC BY-SA 3.0/4.0 |
| **Coverage** | Spanish headwords with English translations, POS, gender |
| **Frequency** | https://github.com/hermitdave/FrequencyWords/blob/master/content/2018/es/es_50k.txt (MIT) |
| **Best for** | Spanish vocabulary + translations; CEFR level approximated from frequency rank |

#### Japanese JLPT Vocabulary Lists
| Property | Details |
|----------|---------|
| **URL** | https://github.com/jamsinclair/open-anki-jlpt-decks |
| **License** | CC BY-SA 4.0 (derived from JMdict) |
| **Coverage** | All JLPT levels (N5→N1), vocabulary + kanji + grammar points, with example sentences |
| **Format** | CSV: `expression, reading, meaning, tags, examples` |
| **Best for** | JLPT-level tagging for Japanese content, example sentences with context |


### 1.5 Wiktionary Raw Dumps (Alternative)

| Property | Details |
|----------|---------|
| **URL** | https://dumps.wikimedia.org/enwiktionary/latest/ |
| **License** | CC BY-SA 3.0 |
| **Format** | Raw MediaWiki XML (large, hard to parse manually) |
| **Note** | Prefer kaikki.org pre-parsed JSONL over raw dumps |

### 1.6 OPUS Corpus

| Property | Details |
|----------|---------|
| **URL** | https://opus.nlpl.eu/ |
| **License** | Varies by sub-corpus (mostly CC BY-SA) |
| **Coverage** | Hundreds of parallel corpora across languages |
| **Format** | TSV |
| **Best for** | Additional sentence pairs if Tatoeba coverage is insufficient |

### 1.7 Common Voice (Audio)

| Property | Details |
|----------|---------|
| **URL** | https://commonvoice.mozilla.org/en/datasets |
| **License** | CC0 1.0 Universal (Public Domain) |
| **Commercial use** | ✅ Yes, no attribution required |
| **Coverage** | 280+ languages including Spanish (`es`) and Japanese (`ja`). Thousands of hours of human speech. |
| **Format** | MP3 clips + TSV metadata (`path`, `sentence`, `locale`, `speaker_id`, etc.) |
| **Size** | Per language: hundreds of MB to several GB |
| **Best for** | Native speaker audio for listening exercises |

**Note:** Common Voice audio is ideal for listening exercise backing, but requires careful curation to match your specific sentence text. Alternative: use free TTS services (Web Speech API, Google TTS API) for generated audio.

### 1.8 Anki Shared Decks

| Property | Details |
|----------|---------|
| **Source** | https://ankiweb.net/shared/ |
| **License** | VARIES PER DECK — often CC BY-NC-ND (non-commercial) or custom terms |
| **⚠️ Flag** | Most Anki decks are **NOT** suitable for commercial redistribution. Check each deck's license explicitly. |
| **Best for** | [Not recommended for commercial use without explicit permission] |

### 1.9 LibreLingo

| Property | Details |
|----------|---------|
| **URL** | https://librelingo.com |
| **License** | Open-source (MIT) for the app; course content license varies |
| **Coverage** | Spanish, French, German, etc. Structured courses with grammar points |
| **Best for** | [INFERENCE: Reference for pedagogical structure] |

---

## 2. Recommended Sources per Language

### Spanish

| Role | Source | Why |
|------|--------|-----|
| **Primary: Sentences** | Tatoeba spa↔eng custom export | Real sentences, commercial-friendly license |
| **Secondary: Vocabulary + Translations** | Kaikki.org `eswiktionary` JSONL | Conjugation patterns, translations to other languages |
| **Audio (optional)** | Mozilla Common Voice `es` | CC0 public domain, commercial-safe |

**Rationale:** Spanish has excellent open-source data. Kaikki.org's `eswiktionary` extract gives vocabulary and translations at scale. Tatoeba provides real-world sentences. Both are commercially redistributable with simple attribution.

### Japanese

| Role | Source | Why |
|------|--------|-----|
| **Primary: Vocabulary + Readings** | JMdict (EDRDG) | Most complete Japanese dictionary, includes readings (kanji+kana) |
| **Secondary: Kanji info** | KANJIDIC2 (EDRDG) | Stroke order, meanings for kanji practice |
| **Sentences** | Tatoeba jpn↔eng custom export | Real example sentences |
| **Fallback vocabulary** | Hermitdave `ja_50k.txt` | Frequency-ranked, useful if JLPT lists need expansion |
| **Audio (optional)** | Mozilla Common Voice `ja` | CC0 public domain |

**Rationale:** Japanese is the hard case due to writing system complexity (kanji/kana/romaji), particles, conjugation, and pitch accent. JMdict is the gold standard — it's the foundation of nearly all Japanese learning software. Combined with open-anki-jlpt-decks (which provides JLPT level tagging), you get a complete vocabulary infrastructure. Tatoeba provides real example sentences.

**What's realistically achievable vs. what needs authoring:**

| Aspect | Automated | Needs authoring |
|--------|-----------|----------------|
| Vocabulary list (word → reading → English) | ✅ JMdict + open-anki-jlpt-decks | — |
| JLPT level tagging | ✅ From open-anki-jlpt-decks | — |
| Example sentences | ✅ Tatoeba | Quality filtering (human or LLM) |
| Kanji stroke order exercises | ✅ KANJIDIC2 | — |
| Reading recognition (kanji→kana) | ✅ From JMdict readings | — |
| Conjugation exercises | ⚠️ Partial (patterns exist but generating correct form in context is complex) | Human/LLM review |
| Pitch accent exercises | ❌ Not available in standard sources | Human-authored |
| Cultural context explanations | ❌ | Human-authored |
| Grammar rules and explanations | ⚠️ Partial (Japanese grammar resources exist) | Human curation + simplification for learners |
| Particles explanation | ❌ | Human-authored |

---

## 3. Pipeline Design

### 3.1 Pipeline Stages

```
[FETCH] → [NORMALIZE] → [DEDUPE] → [LEVEL/TIER] → [GROUP] → [EXERCISE GEN] → [TTS TEXT] → [VALIDATE]
```

### 3.2 Stage Details

#### Stage 1: FETCH
Download raw data sources once:
# Spanish (vocabulary from kaikki.org Spanish Wiktionary extract)
curl -L https://kaikki.org/dictionary/Spanish/ -o data/raw/es_vocab.jsonl
# Spanish (frequency list for ordering)
curl -L https://raw.githubusercontent.com/hermitdave/FrequencyWords/master/content/2018/es/es_50k.txt -o data/raw/es_freq.txt

# Japanese (JMdict, kanjidic2, JMnedict from EDRDG)
curl -L http://ftp.edrdg.org/pub/Nihongo/JMdict_e.gz -o data/raw/JMdict_e.gz
curl -L http://ftp.edrdg.org/pub/Nihongo/kanjidic2.xml.gz -o data/raw/kanjidic2.xml.gz
curl -L http://ftp.edrdg.org/pub/Nihongo/JMnedict.gz -o data/raw/JMnedict.gz

# JLPT-level vocabulary tagging
curl -L https://raw.githubusercontent.com/jamsinclair/open-anki-jlpt-decks/main/src/n5.csv -o data/raw/jlpt_n5.csv

# Tatoeba (custom spa↔eng and jpn↔eng exports)
# → Generate from https://tatoeba.org/eng/downloads
```

#### Stage 2: NORMALIZE
Parse each source into a unified internal format. Python scripts:

- Parse kaikki.org Spanish JSONL (filter noise)
- Parse JMdict XML for Japanese (complex but documented)
- Output: JSONL of standardized vocabulary entries

**Standardized vocabulary entry format:**
```json
{
  "word": "gato",
  "lang": "es",
  "pos": "noun",
  "gender": "masculine",
  "translations": [{"lang": "en", "text": "cat"}],
  "frequency_rank": 423,
  "cefr_level": "A1",
  "tags": ["animals", "basic"]
}
```

**Standardized Japanese vocabulary entry:**
```json
{
  "word": "猫",
  "kana": "ねこ",
  "romaji": "neko",
  "lang": "ja",
  "pos": "noun",
  "translations": [{"lang": "en", "text": "cat"}, {"lang": "es", "text": "gato"}],
  "jlpt_level": "N5",
  "frequency_rank": 312,
  "tags": ["animals", "basic"]
}
```

#### Stage 3: DEDUPE
- Remove duplicate entries (same word with multiple translations)
- Keep only highest-frequency variant
- Normalize whitespace/punctuation

#### Stage 4: LEVEL/TIER
Assign difficulty tier based on frequency rank or CEFR/JLPT level:

**Spanish CEFR mapping (simplified):**
```python
def spanish_cefr(rank: int) -> str:
    if rank <= 1000: return "A1"
    elif rank <= 2500: return "A2"
    elif rank <= 5000: return "B1"
    elif rank <= 8000: return "B2"
    else: return "C1+"
```

**Japanese JLPT mapping:**
- N5: rank ≤ 1000 (basic vocabulary)
- N4: rank ≤ 2500
- N3: rank ≤ 5000
- N2: rank ≤ 10000
- N1: rank > 10000

#### Stage 5: GROUP
Organize into thematic units and lessons:

**Spanish (A1) example unit structure:**
```
Unit 1: Greetings and Introductions (A1)
├── Lesson 1: Hello (Hola)
│   ├── Vocabulary: hola, buenos días, buenas tardes, buenas noches, me llamo, soy, adiós, hasta luego
│   └── Exercises: matching, listening, type translation
├── Lesson 2: Numbers 1-10
│   ├── Vocabulary: uno, dos, tres, cuatro, cinco, seis, siete, ocho, nueve, diez
│   └── Exercises: matching numbers, listening, typing
└── Lesson 3: Yes and No
    ├── Vocabulary: sí, no, claro, por supuesto, está bien
    └── Exercises: ...

Unit 2: People and Family (A1)
├── Lesson 1: Family Members
│   ├── Vocabulary: padre, madre, hermano, hermana, hijo, hija...
│   └── Exercises: matching, image recognition, typing
...
```

**Japanese (N5) example unit structure:**
```
Unit 1: Greetings and Introductions (N5)
├── Lesson 1: Hello (こんにちは)
│   ├── Vocabulary: こんにちは, おはようございます, おやすみなさい, はじめまして, わたし
│   └── Exercises: matching, listening, typing, reading (hiragana)
├── Lesson 2: Numbers 1-10
│   ├── Vocabulary: いち, に, さん, よん, ご, ろく, なな, はち, きゅう, じゅう
│   └── Exercises: matching numbers, hiragana recognition, typing
├── Lesson 3: Yes and No
│   ├── Vocabulary: はい, いいえ, いいえ, すみません, ありがとう
│   └── Exercises: matching, listening, typing
...

Unit 2: Daily Life (N5)
├── Lesson 1: Food and Drink
│   ├── Vocabulary: ごはん, みず, おちゃ, パン, にく, にく, さかな, やさい
│   └── Exercises: image matching, listening, kanji reading (for basic kanji)
...
```

#### Stage 6: EXERCISE GENERATION
Auto-generate exercises from vocabulary and sentence data:

**Exercise types (expanding beyond current SELECT/ASSIST):**

| Type | Input | Output | Example |
|------|-------|--------|---------|
| `SELECT` | Word + options | Correct translation | "How do you say 'gato'?" → [cat] [dog] [bird] |
| `ASSIST` | Sentence with blank | Correct word | "El gato es _____." → [rojo] [grande] [perro] |
| `TRANSLATE` | Full sentence | Type translation | "Translate to Spanish: The cat is black." |
| `LISTENING` | Audio clip | Type what you heard | "🔊" → user types what they hear |
| `WORD_BANK` | Jumbled words | Arrange into sentence | [es] [gato] [el] → "el gato es" |
| `MATCHING` | Multiple pairs | Match translation pairs | gato↔cat, perro↔dog, etc. |
| `KANJI_READ` | Kanji | Type kana/romaji reading | "猫" → "neko" |
| `KANJI_WRITE` | Kana/English | Select correct kanji | "cat" → [猫] [犬] [鳥] |
| `CLOZE` | Sentence with blank | Type missing word | "El ___ es negro." → "gato" |

**Auto-generation logic (Pseudocode):**
```python
for word in vocabulary:
    # Generate matching exercise
    generate_matching(word, other_words_in_same_lesson)
    
    # Generate selection exercise
    generate_select_translation(word)
    
    if word.pos == "noun" and has_image(word):
        generate_image_recognition(word)

for sentence in tatoeba_sentences:
    # Generate cloze deletion
    generate_cloze(sentence)
    
    # Generate typing exercise
    generate_translate_sentence(sentence)
```

#### Stage 7: TTS TEXT
For each exercise that needs audio, prepare TTS text:
```python
{
    "exercise_id": "exercise_123",
    "tts_text": "hola",
    "tts_lang": "es-ES",
    "tts_speed": 0.8,  # slightly slower for learners
    "display_text": "hola",  # what user sees
    "translation": "hello"   # for reference
}
```

For Japanese, include separate TTS for reading:
```python
{
    "exercise_id": "exercise_456",
    "tts_reading": "ねこ",       # how to read it
    "tts_text": "猫",            # the kanji
    "tts_lang": "ja-JP",
    "display_text": "猫",
    "translation": "cat",
    "romaji": "neko"
}
```

#### Stage 8: VALIDATE
- Check all exercises have valid answer options
- Ensure correct answer exists in options for multiple choice
- Verify no empty translations
- Run lint: no duplicate exercises, proper ordering

### 3.3 Pipeline Execution Script
```bash
# Run the full pipeline
python scripts/pipeline/run_pipeline.py --lang=es --level=A1
python scripts/pipeline/run_pipeline.py --lang=ja --level=N5
```

Output: `data/course/es-a1.json`, `data/course/ja-n5.json` — ready to load into the database.

---

## 4. Database Schema Changes

### 4.1 Problems with Current Schema

The current schema is too minimal for real course content:
1. No language code on courses (only title)
2. No difficulty/level field
3. No exercise type beyond SELECT/ASSIST
4. No support for romaji/kana/reading for Japanese
5. No image support for exercises
6. No separate "display text" vs "answer text" vs "TTS text"
7. No difficulty field per challenge
8. No tags for filtering
9. No example sentences
10. No conjugation/inflection data

### 4.2 Proposed Schema Changes (SQL)

```sql
-- 1. Add language code and metadata to courses
ALTER TABLE courses ADD COLUMN language_code VARCHAR(10) NOT NULL DEFAULT 'es';
ALTER TABLE courses ADD COLUMN level VARCHAR(10); -- 'A1', 'N5', etc.
ALTER TABLE courses ADD COLUMN description TEXT;

-- 2. Add metadata to units
ALTER TABLE units ADD COLUMN description TEXT;
ALTER TABLE units ADD COLUMN cefr_level VARCHAR(5);
ALTER TABLE units ADD COLUMN jlpt_level VARCHAR(5);

-- 3. Major: Expand challenges table (type column already exists as challengesEnum)
ALTER TABLE challenges ADD COLUMN difficulty INTEGER DEFAULT 1; -- 1-5
ALTER TABLE challenges ADD COLUMN tags TEXT[] DEFAULT '{}'; -- for filtering
ALTER TABLE challenges ADD COLUMN correct_answer_text TEXT; -- the actual correct answer
ALTER TABLE challenges ADD COLUMN tts_text TEXT; -- what to say aloud
ALTER TABLE challenges ADD COLUMN display_text TEXT; -- what to show user
ALTER TABLE challenges ADD COLUMN explanation TEXT; -- pedagogical explanation

-- 4. Add support for multiple answer text fields in options
ALTER TABLE challenge_options ADD COLUMN correct_reading TEXT; -- for Japanese readings
ALTER TABLE challenge_options ADD COLUMN romaji TEXT; -- romaji for Japanese
ALTER TABLE challenge_options ADD COLUMN audio_url TEXT; -- pre-recorded audio
ALTER TABLE challenge_options ADD COLUMN image_url TEXT; -- exercise image

-- 5. Optional: example sentences table
CREATE TABLE example_sentences (
    id SERIAL PRIMARY KEY,
    word VARCHAR(255) NOT NULL,
    language VARCHAR(10) NOT NULL,
    text TEXT NOT NULL,
    translation TEXT,
    tts_text TEXT
);
```

### 4.3 Drizzle Pseudocode for Migration

```typescript
// In db/migrations/001_expand_challenges.sql
import { sql } from "drizzle-orm";

export async function up() {
  await db.execute(sql`
    ALTER TABLE courses ADD COLUMN language_code VARCHAR(10) NOT NULL DEFAULT 'es';
    ALTER TABLE courses ADD COLUMN level VARCHAR(10);
    ALTER TABLE courses ADD COLUMN description TEXT;
    
    ALTER TABLE units ADD COLUMN cefr_level VARCHAR(5);
    ALTER TABLE units ADD COLUMN jlpt_level VARCHAR(5);
    
    -- Convert type ENUM to VARCHAR to allow new exercise types
    ALTER TABLE challenges DROP CONSTRAINT IF EXISTS challenges_type_check;
    ALTER TABLE challenges ALTER COLUMN type TYPE VARCHAR(50);
    
    ALTER TABLE challenges ADD COLUMN difficulty INTEGER DEFAULT 1;
    ALTER TABLE challenges ADD COLUMN correct_answer_text TEXT;
    ALTER TABLE challenges ADD COLUMN tts_text TEXT;
    ALTER TABLE challenges ADD COLUMN display_text TEXT;
    ALTER TABLE challenges ADD COLUMN explanation TEXT;
    ALTER TABLE challenges ADD COLUMN tags TEXT[] DEFAULT '{}';
    
    ALTER TABLE challenge_options ADD COLUMN correct_reading TEXT;
    ALTER TABLE challenge_options ADD COLUMN romaji TEXT;
    ALTER TABLE challenge_options ADD COLUMN audio_url TEXT;
    ALTER TABLE challenge_options ADD COLUMN image_url TEXT;
  `);
}
```

### 4.4 New Schema in Drizzle TypeScript

```typescript
// db/schema.ts - updated portions

export const courses = pgTable("courses", {
  id: serial("id").primaryKey(),
  title: text("title").notNull(),
  imageSrc: text("image_src").notNull(),
  languageCode: text("language_code").notNull().default("es"),
  level: text("level"), // 'A1', 'N5', etc.
  description: text("description"),
});

export const units = pgTable("units", {
  id: serial("id").primaryKey(),
  title: text("title").notNull(),
  description: text("description"),
  courseId: integer("course_id")
    .references(() => courses.id, { onDelete: "cascade" })
    .notNull(),
  order: integer("order").notNull(),
  cefrLevel: text("cefr_level"), // 'A1', 'A2', 'B1', etc.
  jlptLevel: text("jlpt_level"), // 'N5', 'N4', etc.
});

// Challenge types: SELECT, ASSIST, TRANSLATE, LISTENING, WORD_BANK, 
// MATCHING, KANJI_READ, KANJI_WRITE, CLOZE
export const challenges = pgTable("challenges", {
  id: serial("id").primaryKey(),
  lessonId: integer("lesson_id")
    .references(() => lessons.id, { onDelete: "cascade" })
    .notNull(),
  type: text("type").notNull().default("SELECT"),
  question: text("question").notNull(),
  order: integer("order").notNull(),
  difficulty: integer("difficulty").default(1),
  correctAnswerText: text("correct_answer_text"),
  ttsText: text("tts_text"),
  displayText: text("display_text"),
  explanation: text("explanation"),
  tags: textArray("tags").default([]),
});
```

---

## 5. Sample Course Data

### Spanish Course: "Español Básico" (A1)

```json
{
  "course": {
    "title": "Español Básico",
    "languageCode": "es",
    "level": "A1",
    "imageSrc": "/flags/es.svg",
    "description": "Learn basic Spanish: greetings, numbers, family, food, and everyday conversation."
  },
  "units": [
    {
      "title": "Unit 1: Greetings and Introductions",
      "description": "Learn how to say hello, introduce yourself, and say goodbye.",
      "cefrLevel": "A1",
      "order": 1,
      "lessons": [
        {
          "title": "Lesson 1: Hello",
          "order": 1,
          "challenges": [
            {
              "type": "TRANSLATE",
              "question": "Say hello (formal)",
              "correctAnswerText": "hola",
              "ttsText": "hola",
              "order": 1,
              "difficulty": 1,
              "explanation": "\"Hola\" is the universal Spanish greeting, used any time of day."
            },
            {
              "type": "TRANSLATE",
              "question": "Say good morning",
              "correctAnswerText": "buenos días",
              "ttsText": "buenos días",
              "order": 2,
              "difficulty": 1,
              "explanation": "Used from morning until around noon."
            },
            {
              "type": "TRANSLATE",
              "question": "Say good afternoon",
              "correctAnswerText": "buenas tardes",
              "ttsText": "buenas tardes",
              "order": 3,
              "difficulty": 1,
              "explanation": "Used from around noon until sunset."
            },
            {
              "type": "TRANSLATE",
              "question": "Say good night (going to sleep)",
              "correctAnswerText": "buenas noches",
              "ttsText": "buenas noches",
              "order": 4,
              "difficulty": 1,
              "explanation": "Used when going to bed or saying goodbye at night."
            }
          ]
        },
        {
          "title": "Lesson 2: Introductions",
          "order": 2,
          "challenges": [
            {
              "type": "TRANSLATE",
              "question": "Say 'I am named...'",
              "correctAnswerText": "me llamo",
              "ttsText": "me llamo",
              "order": 1,
              "difficulty": 1,
              "explanation": "Literal meaning: 'I am called'. More formal than 'soy'."
            },
            {
              "type": "TRANSLATE",
              "question": "Say 'I am...' (identity)",
              "correctAnswerText": "soy",
              "ttsText": "soy",
              "order": 2,
              "difficulty": 1,
              "explanation": "Used for your name, profession, or general identity."
            },
            {
              "type": "TRANSLATE",
              "question": "Say 'Nice to meet you'",
              "correctAnswerText": "encantado",
              "ttsText": "encantado",
              "order": 3,
              "difficulty": 2,
              "explanation": "Use 'encantada' if you're female."
            },
            {
              "type": "TRANSLATE",
              "question": "Say 'What is your name?'",
              "correctAnswerText": "cómo te llamas",
              "ttsText": "cómo te llamas",
              "order": 4,
              "difficulty": 2,
              "explanation": "Informal form. Use 'cómo se llama usted' for formal."
            }
          ]
        },
        {
          "title": "Lesson 3: Goodbye",
          "order": 3,
          "challenges": [
            {
              "type": "TRANSLATE",
              "question": "Say 'goodbye' (formal)",
              "correctAnswerText": "adiós",
              "ttsText": "adiós",
              "order": 1,
              "difficulty": 1,
              "explanation": "Formal goodbye, suitable for any situation."
            },
            {
              "type": "TRANSLATE",
              "question": "Say 'see you later' (informal)",
              "correctAnswerText": "hasta luego",
              "ttsText": "hasta luego",
              "order": 2,
              "difficulty": 1,
              "explanation": "Literally 'until later'. Very common."
            },
            {
              "type": "TRANSLATE",
              "question": "Say 'goodbye' (until tomorrow)",
              "correctAnswerText": "hasta mañana",
              "ttsText": "hasta mañana",
              "order": 3,
              "difficulty": 2,
              "explanation": "Used when you expect to see the person again tomorrow."
            }
          ]
        }
      ]
    },
    {
      "title": "Unit 2: Numbers 1-10",
      "description": "Count from 1 to 10 in Spanish.",
      "cefrLevel": "A1",
      "order": 2,
      "lessons": [
        {
          "title": "Lesson 1: Numbers 1-5",
          "order": 1,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'one'", "correctAnswerText": "uno", "ttsText": "uno", "order": 1, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'two'", "correctAnswerText": "dos", "ttsText": "dos", "order": 2, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'three'", "correctAnswerText": "tres", "ttsText": "tres", "order": 3, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'four'", "correctAnswerText": "cuatro", "ttsText": "cuatro", "order": 4, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'five'", "correctAnswerText": "cinco", "ttsText": "cinco", "order": 5, "difficulty": 1 }
          ]
        },
        {
          "title": "Lesson 2: Numbers 6-10",
          "order": 2,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'six'", "correctAnswerText": "seis", "ttsText": "seis", "order": 1, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'seven'", "correctAnswerText": "siete", "ttsText": "siete", "order": 2, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'eight'", "correctAnswerText": "ocho", "ttsText": "ocho", "order": 3, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'nine'", "correctAnswerText": "nueve", "ttsText": "nueve", "order": 4, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'ten'", "correctAnswerText": "diez", "ttsText": "diez", "order": 5, "difficulty": 1 }
          ]
        },
        {
          "title": "Lesson 3: Counting Practice",
          "order": 3,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'three apples'", "correctAnswerText": "tres manzanas", "ttsText": "tres manzanas", "order": 1, "difficulty": 2 },
            { "type": "TRANSLATE", "question": "Say 'five dogs'", "correctAnswerText": "cinco perros", "ttsText": "cinco perros", "order": 2, "difficulty": 2 },
            { "type": "TRANSLATE", "question": "Say 'two cats'", "correctAnswerText": "dos gatos", "ttsText": "dos gatos", "order": 3, "difficulty": 2 }
          ]
        }
      ]
    },
    {
      "title": "Unit 3: Family Members",
      "description": "Learn the words for family members.",
      "cefrLevel": "A1",
      "order": 3,
      "lessons": [
        {
          "title": "Lesson 1: Immediate Family",
          "order": 1,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'father' or 'dad'", "correctAnswerText": "padre", "ttsText": "padre", "order": 1, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'mother' or 'mom'", "correctAnswerText": "madre", "ttsText": "madre", "order": 2, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'brother' (male)", "correctAnswerText": "hermano", "ttsText": "hermano", "order": 3, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'sister' (female)", "correctAnswerText": "hermana", "ttsText": "hermana", "order": 4, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'son'", "correctAnswerText": "hijo", "ttsText": "hijo", "order": 5, "difficulty": 2 },
            { "type": "TRANSLATE", "question": "Say 'daughter'", "correctAnswerText": "hija", "ttsText": "hija", "order": 6, "difficulty": 2 }
          ]
        },
        {
          "title": "Lesson 2: Extended Family",
          "order": 2,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'grandfather'", "correctAnswerText": "abuelo", "ttsText": "abuelo", "order": 1, "difficulty": 2 },
            { "type": "TRANSLATE", "question": "Say 'grandmother'", "correctAnswerText": "abuela", "ttsText": "abuela", "order": 2, "difficulty": 2 },
            { "type": "TRANSLATE", "question": "Say 'uncle'", "correctAnswerText": "tío", "ttsText": "tío", "order": 3, "difficulty": 2 },
            { "type": "TRANSLATE", "question": "Say 'aunt'", "correctAnswerText": "tía", "ttsText": "tía", "order": 4, "difficulty": 2 }
          ]
        }
      ]
    },
    {
      "title": "Unit 4: Animals",
      "description": "Learn basic animal vocabulary.",
      "cefrLevel": "A1",
      "order": 4,
      "lessons": [
        {
          "title": "Lesson 1: Common Animals",
          "order": 1,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'cat'", "correctAnswerText": "gato", "ttsText": "gato", "order": 1, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'dog'", "correctAnswerText": "perro", "ttsText": "perro", "order": 2, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'bird'", "correctAnswerText": "pájaro", "ttsText": "pájaro", "order": 3, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'fish'", "correctAnswerText": "pez", "ttsText": "pez", "order": 4, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'horse'", "correctAnswerText": "caballo", "ttsText": "caballo", "order": 5, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'cow'", "correctAnswerText": "vaca", "ttsText": "vaca", "order": 6, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'chicken'", "correctAnswerText": "pollo", "ttsText": "pollo", "order": 7, "difficulty": 2 },
            { "type": "TRANSLATE", "question": "Say 'rabbit'", "correctAnswerText": "conejo", "ttsText": "conejo", "order": 8, "difficulty": 2 }
          ]
        }
      ]
    },
    {
      "title": "Unit 5: Food and Drink",
      "description": "Basic food and drink vocabulary.",
      "cefrLevel": "A1",
      "order": 5,
      "lessons": [
        {
          "title": "Lesson 1: Common Foods",
          "order": 1,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'water'", "correctAnswerText": "agua", "ttsText": "agua", "order": 1, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'bread'", "correctAnswerText": "pan", "ttsText": "pan", "order": 2, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'rice'", "correctAnswerText": "arroz", "ttsText": "arroz", "order": 3, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'meat'", "correctAnswerText": "carne", "ttsText": "carne", "order": 4, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'fruit'", "correctAnswerText": "fruta", "ttsText": "fruta", "order": 5, "difficulty": 2 },
            { "type": "TRANSLATE", "question": "Say 'milk'", "correctAnswerText": "leche", "ttsText": "leche", "order": 6, "difficulty": 2 }
          ]
        }
      ]
    },
    {
      "title": "Unit 6: Basic Conversation",
      "description": "Learn to have simple conversations.",
      "cefrLevel": "A1",
      "order": 6,
      "lessons": [
        {
          "title": "Lesson 1: Yes and No",
          "order": 1,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'yes'", "correctAnswerText": "sí", "ttsText": "sí", "order": 1, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'no'", "correctAnswerText": "no", "ttsText": "no", "order": 2, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'please'", "correctAnswerText": "por favor", "ttsText": "por favor", "order": 3, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'thank you'", "correctAnswerText": "gracias", "ttsText": "gracias", "order": 4, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'you're welcome'", "correctAnswerText": "de nada", "ttsText": "de nada", "order": 5, "difficulty": 2 },
            { "type": "TRANSLATE", "question": "Say 'excuse me' or 'sorry'", "correctAnswerText": "disculpe", "ttsText": "disculpe", "order": 6, "difficulty": 2 }
          ]
        }
      ]
    }
  ]
}
```

### Japanese Course: "日本語初級" (N5)

```json
{
  "course": {
    "title": "日本語初級",
    "languageCode": "ja",
    "level": "N5",
    "imageSrc": "/flags/jp.svg",
    "description": "Learn basic Japanese: greetings, numbers, family, food, and everyday conversation."
  },
  "units": [
    {
      "title": "Unit 1: Greetings (あいさつ)",
      "description": "Learn how to say hello, introduce yourself, and say goodbye.",
      "jlptLevel": "N5",
      "order": 1,
      "lessons": [
        {
          "title": "Lesson 1: Hello (こんにちは)",
          "order": 1,
          "challenges": [
            {
              "type": "TRANSLATE",
              "question": "Say hello (daytime)",
              "correctAnswerText": "こんにちは",
              "ttsText": "こんにちは",
              "romaji": "konnichiwa",
              "order": 1,
              "difficulty": 1,
              "explanation": "Daytime greeting, suitable for most situations."
            },
            {
              "type": "TRANSLATE",
              "question": "Say good morning",
              "correctAnswerText": "おはようございます",
              "ttsText": "おはようございます",
              "romaji": "ohayō gozaimasu",
              "order": 2,
              "difficulty": 1,
              "explanation": "Formal good morning. Casual: おはよう."
            },
            {
              "type": "TRANSLATE",
              "question": "Say good night",
              "correctAnswerText": "おやすみなさい",
              "ttsText": "おやすみなさい",
              "romaji": "oyasuminasai",
              "order": 3,
              "difficulty": 1,
              "explanation": "Good night (before bed). Casual: おやすみ."
            },
            {
              "type": "TRANSLATE",
              "question": "Say 'nice to meet you' (formal)",
              "correctAnswerText": "はじめまして",
              "ttsText": "はじめまして",
              "romaji": "hajimemashite",
              "order": 4,
              "difficulty": 1,
              "explanation": "Formal introduction phrase."
            }
          ]
        },
        {
          "title": "Lesson 2: Introductions (はじめまして)",
          "order": 2,
          "challenges": [
            {
              "type": "TRANSLATE",
              "question": "Say 'I am...' (name)",
              "correctAnswerText": "わたしのなまえは",
              "ttsText": "わたしのなまえは",
              "romaji": "watashi no namae wa",
              "order": 1,
              "difficulty": 1,
              "explanation": "Formal way to say 'My name is...'"
            },
            {
              "type": "TRANSLATE",
              "question": "Say 'me' (formal)",
              "correctAnswerText": "わたし",
              "ttsText": "わたし",
              "romaji": "watashi",
              "order": 2,
              "difficulty": 1,
              "explanation": "Standard first-person pronoun, formal."
            }
          ]
        },
        {
          "title": "Lesson 3: Goodbye (さようなら)",
          "order": 3,
          "challenges": [
            {
              "type": "TRANSLATE",
              "question": "Say 'goodbye' (formal)",
              "correctAnswerText": "さようなら",
              "ttsText": "さようなら",
              "romaji": "sayōnara",
              "order": 1,
              "difficulty": 1,
              "explanation": "Formal goodbye. Implies you may not see them again soon."
            },
            {
              "type": "TRANSLATE",
              "question": "Say 'goodbye' (informal, will see later)",
              "correctAnswerText": "じゃね",
              "ttsText": "じゃね",
              "romaji": "ja ne",
              "order": 2,
              "difficulty": 2,
              "explanation": "Informal 'see ya'. Very common among friends."
            }
          ]
        }
      ]
    },
    {
      "title": "Unit 2: Numbers (すうじ)",
      "description": "Count from 1 to 10 in Japanese.",
      "jlptLevel": "N5",
      "order": 2,
      "lessons": [
        {
          "title": "Lesson 1: Numbers 1-5",
          "order": 1,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'one'", "correctAnswerText": "いち", "ttsText": "いち", "romaji": "ichi", "order": 1, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'two'", "correctAnswerText": "に", "ttsText": "に", "romaji": "ni", "order": 2, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'three'", "correctAnswerText": "さん", "ttsText": "さん", "romaji": "san", "order": 3, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'four'", "correctAnswerText": "よん", "ttsText": "よん", "romaji": "yon", "order": 4, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'five'", "correctAnswerText": "ご", "ttsText": "ご", "romaji": "go", "order": 5, "difficulty": 1 }
          ]
        },
        {
          "title": "Lesson 2: Numbers 6-10",
          "order": 2,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'six'", "correctAnswerText": "ろく", "ttsText": "ろく", "romaji": "roku", "order": 1, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'seven'", "correctAnswerText": "なな", "ttsText": "なな", "romaji": "nana", "order": 2, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'eight'", "correctAnswerText": "はち", "ttsText": "はち", "romaji": "hachi", "order": 3, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'nine'", "correctAnswerText": "きゅう", "ttsText": "きゅう", "romaji": "kyuu", "order": 4, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'ten'", "correctAnswerText": "じゅう", "ttsText": "じゅう", "romaji": "juu", "order": 5, "difficulty": 1 }
          ]
        },
        {
          "title": "Lesson 3: Counting Practice",
          "order": 3,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'three apples' (りんご)", "correctAnswerText": "りんごをみっつ", "ttsText": "りんごをみっつ", "romaji": "ringo o mittsu", "order": 1, "difficulty": 2 },
            { "type": "TRANSLATE", "question": "Say 'two cats' (ねこ)", "correctAnswerText": "ねこをにひき", "ttsText": "ねこをにひき", "romaji": "neko o nihiki", "order": 2, "difficulty": 2 }
          ]
        }
      ]
    },
    {
      "title": "Unit 3: Family (かぞく)",
      "description": "Learn family member vocabulary.",
      "jlptLevel": "N5",
      "order": 3,
      "lessons": [
        {
          "title": "Lesson 1: Immediate Family",
          "order": 1,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'father' (formal)", "correctAnswerText": "ちち", "ttsText": "ちち", "romaji": "chichi", "order": 1, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'father' (informal)", "correctAnswerText": "お父さん", "ttsText": "お父さん", "romaji": "otousan", "order": 2, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'mother' (formal)", "correctAnswerText": "はは", "ttsText": "はは", "romaji": "haha", "order": 3, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'mother' (informal)", "correctAnswerText": "お母さん", "ttsText": "お母さん", "romaji": "okaasan", "order": 4, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'brother' (male)", "correctAnswerText": "お兄さん", "ttsText": "お兄さん", "romaji": "oniisan", "order": 5, "difficulty": 2 },
            { "type": "TRANSLATE", "question": "Say 'sister' (female)", "correctAnswerText": "お姉さん", "ttsText": "お姉さん", "romaji": "oneesan", "order": 6, "difficulty": 2 }
          ]
        },
        {
          "title": "Lesson 2: Extended Family",
          "order": 2,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'grandfather'", "correctAnswerText": "おじいさん", "ttsText": "おじいさん", "romaji": "ojiisan", "order": 1, "difficulty": 2 },
            { "type": "TRANSLATE", "question": "Say 'grandmother'", "correctAnswerText": "おばあさん", "ttsText": "おばあさん", "romaji": "obaasan", "order": 2, "difficulty": 2 }
          ]
        }
      ]
    },
    {
      "title": "Unit 4: Animals (どうぶつ)",
      "description": "Learn basic animal vocabulary.",
      "jlptLevel": "N5",
      "order": 4,
      "lessons": [
        {
          "title": "Lesson 1: Common Animals",
          "order": 1,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'cat'", "correctAnswerText": "ねこ", "ttsText": "ねこ", "romaji": "neko", "order": 1, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'dog'", "correctAnswerText": "いぬ", "ttsText": "いぬ", "romaji": "inu", "order": 2, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'bird'", "correctAnswerText": "とり", "ttsText": "とり", "romaji": "tori", "order": 3, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'fish'", "correctAnswerText": "さかな", "ttsText": "さかな", "romaji": "sakana", "order": 4, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'rabbit'", "correctAnswerText": "うさぎ", "ttsText": "うさぎ", "romaji": "usagi", "order": 5, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'horse'", "correctAnswerText": "うま", "ttsText": "うま", "romaji": "uma", "order": 6, "difficulty": 2 }
          ]
        }
      ]
    },
    {
      "title": "Unit 5: Food and Drink (たべもの)",
      "description": "Basic food and drink vocabulary.",
      "jlptLevel": "N5",
      "order": 5,
      "lessons": [
        {
          "title": "Lesson 1: Common Foods",
          "order": 1,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'water'", "correctAnswerText": "みず", "ttsText": "みず", "romaji": "mizu", "order": 1, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'rice'", "correctAnswerText": "ごはん", "ttsText": "ごはん", "romaji": "gohan", "order": 2, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'bread'", "correctAnswerText": "パン", "ttsText": "パン", "romaji": "pan", "order": 3, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'meat'", "correctAnswerText": "にく", "ttsText": "にく", "romaji": "niku", "order": 4, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'fish' (as food)", "correctAnswerText": "さかな", "ttsText": "さかな", "romaji": "sakana", "order": 5, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'apple'", "correctAnswerText": "りんご", "ttsText": "りんご", "romaji": "ringo", "order": 6, "difficulty": 2 }
          ]
        }
      ]
    },
    {
      "title": "Unit 6: Basic Conversation (かいわ)",
      "description": "Learn to have simple conversations.",
      "jlptLevel": "N5",
      "order": 6,
      "lessons": [
        {
          "title": "Lesson 1: Yes and No",
          "order": 1,
          "challenges": [
            { "type": "TRANSLATE", "question": "Say 'yes'", "correctAnswerText": "はい", "ttsText": "はい", "romaji": "hai", "order": 1, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'no'", "correctAnswerText": "いいえ", "ttsText": "いいえ", "romaji": "iie", "order": 2, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'please'", "correctAnswerText": "ください", "ttsText": "ください", "romaji": "kudasai", "order": 3, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'thank you' (formal)", "correctAnswerText": "ありがとうございます", "ttsText": "ありがとうございます", "romaji": "arigatou gozaimasu", "order": 4, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'thank you' (informal)", "correctAnswerText": "ありがとう", "ttsText": "ありがとう", "romaji": "arigatou", "order": 5, "difficulty": 1 },
            { "type": "TRANSLATE", "question": "Say 'excuse me' / 'sorry'", "correctAnswerText": "すみません", "ttsText": "すみません", "romaji": "sumimasen", "order": 6, "difficulty": 2 }
          ]
        }
      ]
    }
  ]
}
```

---

## 6. Effort Estimate

### Scope for Credible "Full" Course

| Component | Spanish | Japanese | Notes |
|-----------|---------|----------|-------|
| Vocabulary items | ~1,000 | ~1,000 | A1/N5 level |
| Lessons per course | ~10 | ~10 | ~3-5 lessons per unit |
| Units per course | 3 | 3 | Core units |
| Exercises per lesson | 5-8 | 5-8 | Mix of types |
| **Total exercises** | ~150 | ~150 | Per course |
| **Total across both languages** | | **~300 exercises** | Credible MVP |

### Automated vs. Authored

| Task | Method | Human-hours | Notes |
|------|--------|-------------|-------|
| Download and parse source data | Automated (Python scripts) | 8-12 hours (one-time) | Scripts to parse JMdict, Tatoeba |
| Vocabulary extraction and tiering | Automated | Included above | Frequency ranking, CEFR/JLPT mapping |
| Basic vocabulary exercises | Automated | Included above | Generate matching, translation exercises |
| Sentence-based exercises | Semi-automated | 10-15 hours | Fetch from Tatoeba, filter, quality-check |
| Pedagogical explanations | Authored | 15-20 hours per language | Write clear, simple explanations for learners |
| Grammar exercises | Authored | 10-15 hours per language | Need human-designed grammar patterns |
| Quality review | Human | 10-15 hours per language | Test all exercises, fix errors |
| **Total** | | **~100-150 hours** | One-time development |

### What's Realistically Achievable

| Item | Achievable? | Notes |
|------|-------------|-------|
| 1,000 vocabulary items per language | ✅ Yes | Abundant open-source data |
| Basic greetings and introductions | ✅ Yes | Easy to author |
| Numbers 1-100 | ✅ Yes | Straightforward |
| Family members, animals, food | ✅ Yes | Good vocabulary sources |
| Simple present-tense sentences | ⚠️ Partial | Auto-generation works for patterns but needs review |
| Verb conjugation practice | ❌ Hard | Requires manual authoring; not in current schema |
| Natural conversational fluency | ❌ No | Requires extensive human-authored content |
| Cultural context | ❌ No | Requires native-speaker authoring |

### Honest Verdict

For a **commercial, polished product**, expect to invest in **human content creation** beyond what the pipeline automates. The pipeline gets you:

- ✅ Structured vocabulary (1000+ items per language)
- ✅ Basic sentence-pattern exercises
- ✅ Listening practice (with TTS)
- ✅ Vocabulary review and matching
- ✅ Kanji/kana recognition (Japanese)

What the pipeline **cannot** replace:
- Pedagogical sequencing decisions
- Natural-sounding example sentences in context
- Grammar explanation clarity
- Error handling for common learner mistakes
- Cultural nuance

**Recommendation:** Start with the automated pipeline (80% of exercises) and add human-authored content for the most pedagogically important exercises (20% of exercises, highest impact). This yields a credible MVP in ~2 months of development time for one content team.

---

## Appendix: License Summary

| Source | License | Commercial OK? | Redistribution OK? | Attribution Required? |
|--------|---------|----------------|---------------------|------------------------|
| Tatoeba (text) | CC BY 2.0 FR | ✅ | ✅ | ✅ |
| Tatoeba (audio) | Varies | ⚠️ Check per file | ⚠️ Check per file | ⚠️ Check per file |
| Wiktionary / Kaikki.org | CC BY-SA 3.0 | ✅ | ✅ (sharealike) | ✅ |
| JMdict / EDRDG | CC BY-SA 4.0 | ✅ | ✅ (sharealike) | ✅ |
| JMnedict | CC BY-SA 4.0 | ✅ | ✅ (sharealike) | ✅ |
| KANJIDIC2 | CC BY-SA 4.0 | ✅ | ✅ (sharealike) | ✅ |
| Hermitdave FreqLists | MIT | ✅ | ✅ | ✅ |
| Mozilla Common Voice | CC0 | ✅ | ✅ | ❌ (but appreciated) |
| OPUS corpus | Varies | ✅ (usually) | ✅ (usually) | ✅ (usually) |
| Anki shared decks | VARIES | ⚠️ Check per deck | ⚠️ Check per deck | ⚠️ Check per deck |

**Required attribution:** Include an "About → Sources" page listing all data sources with their licenses, or place attribution in your privacy policy / terms of service.

---

*Report generated 2026-09-13. Sources verified as of writing date.*
