# Grammar Gap Audit — OpenLingo Android

Read-only audit. Every count below was produced by grep over the four curriculum
sources named in §0. No code or curriculum data was modified.

## 0. Corpus actually audited

There are **four** curriculum sources, not one directory:

| Source | Units | Lessons | Challenges |
|---|---|---|---|
| `data/repository/LocalProgressRepository.kt` (base seed, lines 582–900) | 4 (ids 10, 11, 20, 21) | 8 (100–103, 200–203) | 31 |
| `data/local/curriculum/ExpandedCurriculumData.kt` | 5 (12, 13, 14, 22, 23) | 8 | 24 |
| `data/local/curriculum/AdvancedCurriculumData.kt` | 7 (15, 16, 17, 24, 25, 26, 27) | 14 | 43 |
| `data/local/curriculum/B1CurriculumData.kt` | 4 (18, 19, 28, 29) | 8 | 26 |
| **Total** | **20** | **38** | **124** |

Seeding is wired at `LocalProgressRepository.kt:900-935`
(`seedExpandedCurricula`, `seedAdvancedCurricula`, `seedB1Curricula`).

## (a) Exercise type → challenge count

`type` is a free-form `String` on `ChallengeEntity`
(`data/local/entities/ChallengeEntity.kt:10`):
`// SELECT | ASSIST | WORD_BANK | LISTEN`. **There is no enum class** — types are
string literals scattered across the four sources, and the UI dispatches on raw
strings (`MainActivity.kt:945-947, 1039-1044`).

| Type | Base | Expanded | Advanced | B1/N4 | Total | % |
|---|---|---|---|---|---|---|
| SELECT | 13 | 10 | 15 | 9 | **47** | 37.9% |
| WORD_BANK | 8 | 8 | 14 | 8 | **38** | 30.6% |
| LISTEN | 8 | 4 | 14 | 5 | **31** | 25.0% |
| MATCH_PAIRS | 0 | 2 | 0 | 2 | **4** | 3.2% |
| STORY | 0 | 0 | 0 | 2 | **2** | 1.6% |
| ASSIST | 2 | 0 | 0 | 0 | **2** | 1.6% |
| **Total** | 31 | 24 | 43 | 26 | **124** | 100% |

Rendering paths (`MainActivity.kt`):
- `SELECT` / `ASSIST` / `LISTEN` / `STORY` all fall through to the same
  **options list** branch (`:1182` `isMatchPairs` → `:1190` `isWordBank` → `else`
  options loop). `ASSIST` is not a distinct interaction; it renders identically
  to `SELECT`. Display names exist only in a stats label map
  (`ui/screens/ProfileTabScreen.kt:139-144`).
- `MATCH_PAIRS` (`:947`, `:1183`) — tile matching.
- `WORD_BANK` (`:1191`) — token assembly, order checked in
  `ui/MainViewModel.kt:510-521` (string equality of the joined tile sequence).

## (b) Per-unit grammar structure map

Tag legend — **MEMORIZE**: the structure is only ever probed by SELECT /
ASSIST / MATCH_PAIRS / LISTEN recognition of an isolated item or an English
gloss. **USE**: it appears in a WORD_BANK assembly, a STORY item, or a LISTEN
item that requires ordering/completing the sentence.

### Spanish, CEFR A1 (course 1)

| Unit | Structures introduced (from `description =` + item text) | Types present | Tag |
|---|---|---|---|
| 10 Spanish Essentials | ser/*soy*, greeting formulas, gender articles | SELECT, ASSIST, WORD_BANK, LISTEN | USE (partial) |
| 11 People & Family | kinship nouns, `el/la` gender | SELECT, WORD_BANK, LISTEN | USE (partial) |
| 12 Food & Dining | polite request `por favor`, café nouns, `y` coordination | SELECT, WORD_BANK, LISTEN, MATCH_PAIRS, STORY | USE (partial) |
| 13 Daily Routine & Action Verbs | **-ar/-er/-ir present-tense conjugation** (hablo, como, vivo) — named in unit description only | SELECT, WORD_BANK, LISTEN | **MEMORIZE** |
| 14 City & Navigation | `está` locative, `a/en` prepositions, `¿dónde?` | SELECT, WORD_BANK | **MEMORIZE** |
| 15 Home & Furniture | `estar` + adjective (`La cocina está limpia`), `ser` + adjective (`La mesa es grande`) | SELECT, WORD_BANK, LISTEN | USE (via WORD_BANK) |
| 16 Shopping & Money | `¿cuánto cuesta?`, `tener` (tengo), price adjectives | SELECT, WORD_BANK, LISTEN | USE (via WORD_BANK) |
| 17 Health & Body | `tener` + symptom (fiebre, dolor de cabeza), body parts | SELECT, WORD_BANK, LISTEN | USE (via WORD_BANK) |
| 18 Past Tense — Pretérito | **preterite -é/-í/-í endings** (hablé, comí, viví, llegué) | SELECT, WORD_BANK, LISTEN, STORY | **MEMORIZE** — all pretérito items are English→Spanish gloss MCQs; no conjugation contrast is ever contrasted against a wrong-tense distractor |
| 19 Past Tense — Imperfecto | **imperfecto -aba/-ía** (era, tenía, vivía) | SELECT, WORD_BANK, LISTEN | **MEMORIZE** — same pattern; "The weather was nice every day" is one fixed memorized string |

The critical gap: **Units 18 and 19 are the only units whose entire subject is
morphology, and every one of their 12 challenges is a whole-sentence recall
item.** A learner can pass both units without ever conjugating anything —
options are always complete correct sentences.

### Japanese, JLPT N5 (course 2)

| Unit | Structures introduced | Types present | Tag |
|---|---|---|---|
| 20 Hiragana & Greetings | kana inventory, はい/いいえ | SELECT, ASSIST, WORD_BANK, LISTEN | MEMORIZE (kana) |
| 21 Daily Life & Food | ①②③ counters only for numbers 1–3 | SELECT, WORD_BANK, LISTEN | MEMORIZE (counters) |
| 22 Loanwords & Katakana | katakana orthography | SELECT, WORD_BANK, LISTEN, MATCH_PAIRS | MEMORIZE (script) |
| 23 Daily Verbs & Actions | **polite present forms** (たべます, のみます, いきます) — one SELECT asks "Which verb means 'To eat' (polite)?" | SELECT, WORD_BANK, LISTEN | **MEMORIZE** — the plain/dictionary form is never contrasted |
| 24 Numbers & Counters | **counter classifiers** 人/冊/円 | SELECT, WORD_BANK, LISTEN | **MEMORIZE** — counters are never tested against the wrong classifier |
| 25 Katakana Travel & Food | katakana place/food nouns | SELECT, WORD_BANK, LISTEN | MEMORIZE |
| 26 Kanji Nature & Time | 山/月/time kanji readings | SELECT, WORD_BANK, LISTEN | MEMORIZE (kanji) |
| 27 N5 Politeness | **です/ます, お願いします, 大丈夫** | SELECT, WORD_BANK, LISTEN | USE (partial) |

### Japanese, JLPT N4 extension (course 2)

| Unit | Structures introduced | Types present | Tag |
|---|---|---|---|
| 28 Te-form & Requests | **-te form + ください**, direction requests | SELECT, WORD_BANK, LISTEN, MATCH_PAIRS | USE (partial) — the te-form is assembled as tokens, but the *conjugation rule* (う→って) is never stated or contrasted |
| 29 Potential & Ability | **potential form** (話せます, 書くことができます) | SELECT, WORD_BANK, LISTEN, MATCH_PAIRS | USE (partial) — no plain-form → potential-form contrast item exists |

**Structures with zero exercises at any level** (verified by reading every
`question =` string in the four sources): conditional/subjunctive, Spanish
direct/indirect object pronoun placement, `se` constructions, Japanese
`keigo` (beyond ください), conditionals `たら/ば`, passive `られる`, causative.

## (c) Rule / explanation surfaces — inventory

**No grammar rule text exists anywhere in the app.** Exhaustive search result:

- `MainActivity.kt` — the only instructional strings in the exercise surface are
  the five prompt headers at `:1039-1044` ("Select the correct meaning",
  "Tap the matching tiles", "Tap what you hear", "Read the story and answer the
  question", "Translate this phrase"). No hints, no rule cards, no per-challenge
  explanation field exists on `ChallengeEntity` (`:7-16`).
- Lesson/unit map — `MainActivity.kt:769-780` renders `unit.title` +
  `unit.description`. These are *topic* blurbs ("Learn essential -ar, -er, and
  -ir verbs: hablar, comer, vivir"), never rules or paradigms. They are the
  closest thing to explanation in the product, and they are a single line.
- Practice tab — `ui/screens/PracticeTabScreen.kt:212-240` is FSRS vocabulary
  review; the flashcard faces (`:384+`) are single-word foreign ↔ native pairs.
  No sentence frames, no conjugation.
- Checkpoints — `ui/MainViewModel.kt:306-326` draw a random ≤30 sample from the
  *same* challenge pool. A checkpoint is a re-quiz of the identical items, so it
  adds no grammar demand.
- The only `OutlinedTextField` in the app (`:2204`) is the backup-JSON import
  dialog.

## (d) Memorization vs. usage ratio

Of 124 challenges, only **WORD_BANK (38, 30.6%)** asks the learner to produce a
target-language string in a chosen order. The remaining 86 (69.4%) are
recognition or matching:

| Interaction | Count | Produces target-language output? |
|---|---|---|
| SELECT / ASSIST / LISTEN / MATCH_PAIRS / STORY | 86 | No — pick one provided option |
| WORD_BANK | 38 | Yes — assemble tokens in order |

Three compounding distortions:

1. **LISTEN (31) is English-side comprehension, not L2 listening.** Its options
   are English glosses (`LocalProgressRepository.kt:637-640`: challenge 1004 →
   "Good morning" / "Good night" / "Goodbye"). No LISTEN item requires the
   learner to hear a target-language sentence and produce or order it.
2. **Morphology units are memorization units.** Units 18, 19, 23, 24, 28, 29
   teach tenses, politeness, counters, and verb potential — the six hardest
   structure sets in the corpus — and all of them are satisfied by recalling one
   complete memorized string from 3–4 options. Distractors are near-synonyms or
   unrelated words, never the same sentence in a *wrong* tense / *wrong*
   classifier / *wrong* register.
3. **The production surface is thin per lesson.** A typical lesson is
   SELECT + WORD_BANK + LISTEN (e.g. `AdvancedCurriculumData.kt:33-35`): exactly
   one production attempt, 3 challenges.

The balance is off specifically at the *ceiling*: the A1/N5 units honestly
*should* be word-level, but Units 18/19 (Spanish B1 pretérito + imperfecto) and
28/29 (N4 te-form + potential) claim grammar outcomes the data never tests.

## (e) Prioritized additions

1. **Add an inflected-verb challenge type (highest leverage).** Every Spanish
   unit description promises conjugation (`-ar/-er/-ir`, `:132`; pretérito,
   preterite; imperfecto) but no `ChallengeEntity` type can ask for a form. Add
   a `CONJUGATE` type whose options are the *same* sentence in 3 tenses or
   3 persons. 6 items per unit converts Units 18/19 from recall to knowledge.
2. **Give the 38 WORD_BANK items grammar-bearing content.** Currently the
   assembly targets are fixed memorized sentences ("Assemble: 'The weather was
   nice every day'"). Replace ~half with paradigm items that force the rule:
   `hablo/comí/hablaré` for the same subject, `입니다/이에요/예요` for the same
   noun, `입니다/있어요` for the same verb. Same renderer, no new UI.
3. **Make wrong-form distractors mandatory for morphology.** Add a curriculum
   integrity assertion (next to `CurriculumIntegrityTest.kt:94-105`) requiring
   that any challenge tagged with a grammar focus carries at least one
   same-lemma, wrong-morphology distractor. Without this the app cannot regress
   into pure translation even as items are added.
4. **Add a rule/explanation surface.** The cheapest honest version: a
   `grammaticalFocus` + `ruleText` field on `ChallengeEntity`, rendered as a
   dismissible card under the prompt in `MainActivity.kt:1037-1049`, and a
   3-line "rule card" on the unit header where `unit.description` is currently
   rendered (`:769-780`). Without at least this, no learner can ever discover
   *why* a form is correct.
5. **Fix LISTEN's direction.** The 31 LISTEN items should either offer
   target-language sentence options (so the listener reads the L2 orthography
   and internalizes the ending), or be paired with a WORD_BANK replay of the
   same `audioSrc`. As-is they test English ear training, not Spanish/Japanese.
6. **Add one typed production exercise per unit (fill-in-the-blank).** A single
   `OutlinedTextField`-based `FILL_BLANK` type with an accepted-answer list
   would be the first true productive check; the field widget already exists in
   the app (`:2204`) and the ViewModel check pattern is at
   `MainViewModel.kt:504-522`. A mandatory conjugation gap ("Yo ___ estudiante"
   → `soy`) is a 10-line change with the highest pedagogical yield of any item.
7. **Counter and register contrast items for Japanese.** Units 24 (人/冊/円) and
   27 (です/ます) need MCQs whose distractors are the *same* noun with the
   *wrong* classifier and the *same* sentence in casual form. Both are
   single-string option swaps; no new machinery.
8. **Separate the checkpoint pool from the lesson pool.** `startCheckpoint`
   (`ui/MainViewModel.kt:317-320`) re-samples the same 124 items, so checkpoints
   currently measure memory of the curriculum rather than generalization. Once
   types 1/6 exist, checkpoints should draw from a held-out set of unseen
   sentences in the same structures.
