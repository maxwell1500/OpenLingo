# Grammar Teaching Spec — OpenLingo Android (`duo-android`)

**Status:** active — single source of truth for the grammar-teaching overhaul. **Owner:** spec
owner; updated in place as work items land (§10 Status Summary, §11 Progress Log). Terminology
aligns with `AUDIT_grammar_gap.md`, `AUDIT_pronunciation.md`, `AUDIT_feature_comparison.md`,
including that report's `MEMORIZE`/`USE`/`PRODUCE` tags and its `A#`/`B#` item ids. Supersedes nothing.

## 1. Purpose

Turn OpenLingo from a vocabulary app that *describes* grammar into one that *teaches and tests* it.
The corpus covers 20 units / 38 lessons / 124 challenges across Spanish CEFR A1–A2 and Japanese
JLPT N5–N4, but only 30.6% of it requires ordered target-language output and no rule text exists
anywhere in the product. This spec is the contract for closing that gap: new schema, a constrained
exercise-type system, three new mechanics, and tests that make the corpus incapable of regressing
into translation quizzes.

As of the 2026-09-26 renderer and content landing the corpus is **158 challenges** across the same
20 units / 38 lessons, and production is **36.1%** — **45.6%** counting `CONJUGATE`. §8 carries the
current measurement; every number in this section is the as-found baseline that measurement is
judged against, and is left standing unedited for that reason.

### Problem statement (verified against source, 2026-09-26)

**This table is the as-found baseline, not a live status report.** The rows describe the app *before*
the overhaul and are kept as the measure the regression bar in §8 is judged against. Where work has
since landed, the item's §4 subsection and the §10 Status Summary are authoritative — the raw
`ChallengeEntity.type` row below, for instance, was closed by WI-02.

| Claim | Evidence |
|---|---|
| 38 of 124 challenges (30.6%) demand ordered target-language output; **all 38 are `WORD_BANK`**. The other 86 (69.4%) are recognition: `SELECT` 47, `LISTEN` 31, `MATCH_PAIRS` 4, `STORY` 2, `ASSIST` 2 | type tally over the four curriculum sources |
| Corpus composition: base 4u/8l/31c, expanded 5/8/24, advanced 7/14/43, B1 4/8/26 = 20/38/124 | `data/repository/LocalProgressRepository.kt:582-900`; `data/local/curriculum/{Expanded,Advanced,B1}CurriculumData.kt` |
| **Zero grammar rule text anywhere in the app** | only instructional strings are five prompt headers, `MainActivity.kt:1039-1044`; unit header renders `unit.title`/`unit.description` at `MainActivity.kt:769-780` |
| `ChallengeEntity` has no explanation/rule field; `type` is a raw `String` with a stale comment listing 4 of the 6 types in use | `data/local/entities/ChallengeEntity.kt:7-16` |
| `LISTEN` is backwards: options are English glosses, so it tests English comprehension | `LocalProgressRepository.kt:638-640` (challenge 1004 offers "Good morning" / "Good night" / "Goodbye") |
| Distractors are grammatical no-ops — a different sentence, not the same sentence wrong | `data/local/curriculum/B1CurriculumData.kt:50-53` (correct "Hablé con él ayer" vs "Llegamos a tiempo", "Compré el billete") |
| Checkpoints re-sample the taught pool, so they measure memory not generalization | `ui/MainViewModel.kt:306-334`, sample at `:317-320` |
| `ASSIST` is a phantom: no renderer; only `isWordBank`/`isListen`/`isMatchPairs` at `MainActivity.kt:945-947`, `ASSIST` falls to the generic `else` at `:1044` | `AUDIT_feature_comparison.md`; README advertises it |
| Pronunciation is output-only: 66 bundled ogg (32 es / 34 ja, ~497 KB), all synthetic Kokoro-82M; no `RECORD_AUDIO`, no capture stack; 0 of 124 challenges ask the learner to produce a sound | `AUDIT_pronunciation.md` §1, §4, §6 |
| 0.6x slow-playback is real Media3 time-stretching (`audio/AudioPlayer.kt:79` `PlaybackParameters`) but hardcoded at `MainActivity.kt:280` and rendered only in the `LISTEN` branch | `AUDIT_pronunciation.md` §5 |

A1/N5 units being word-level is **correct pedagogy, not a defect**. The defect is that units 18/19
(Spanish preterito / imperfecto) and 28/29 (Japanese te-form / potential) *claim grammar outcomes*
in their descriptions and then test them by recall.

### Non-goals

Not a rewrite of the renderer or the S-curve lesson map. Not a content-only expansion — every new
item ships with a rule or it is not accepted. Not an attempt to make word-level A1/N5 material
"more grammatical". No offline→online drift, no SDK gate on the free tier (see §2, §9).

## 2. Constraints & Product Principles

1. **100% offline. Non-negotiable.** Never introduce `INTERNET` permission, accounts, tracking, ads,
   or billing. Any item requiring a backend is out of scope forever (§9).
2. **Explicit rule before use.** Every grammar structure gets a stated rule, then practice that forces
   that rule. A rule never exercised in a wrong-answerable item is not shipped.
3. **No wrong-answer passable by vocabulary recognition alone.** A challenge passes only if the learner
   can be wrong for a *grammatical* reason. `WI-06` enforces this in CI.
4. **Gradual difficulty; sentence-level production is the ceiling.** Tiles → paradigm sets → typed
   inflection. No paragraph production, no unconstrained prose translation.
5. **Spanish and Japanese are first-class.** Every work item lands on both courses, or is explicitly
   justified as structurally inapplicable to one.

Secondary constraints: Room is at `version = 15` (`data/local/DuoDatabase.kt:49`); each schema change
adds an explicit `Migration` in the same `companion object` (pattern at `:67-102`).
Bundled-audio growth stays within an 11 MB APK-class budget, and because the TTS fallback is
OEM-dependent and silent on devices lacking a voice pack (`AUDIT_pronunciation.md` §3), new content
must never *require* a clip that may not exist. Every new challenge/option id is globally unique;
`CurriculumIntegrityTest.kt:30-41` arbitrates (§7).

## 3. Data Model Changes (Room 12 → 15) — **12→13, 13→14 and 14→15 SHIPPED**

The 12 → 13 migration adds four additive `TEXT` columns with `NULL` defaults — a pure `ALTER TABLE`
migration, existing rows unchanged. Null is meaningful: "not a grammar-bearing item". The data model
went **12 → 15 across three migrations**; a fifth, non-null column follows in 13 → 14 (below), which
is the only one whose default changes what a query returns.

| Entity | Column | Type | Semantics |
|---|---|---|---|
| `ChallengeEntity` | `grammaticalFocus` | `TEXT?` | Stable slug for the grammar point being drilled. Join key for the §7 integrity rules and the held-out checkpoint pool (`WI-08`). Machine-comparable, never displayed raw. **Governed vocabulary — 16 slugs, lowercase, dotted, `<language>.<structure>`:** `es.preterito.regular`, `es.imperfecto`, `es.present_person`, `es.ser_estar`, `es.ser_present`, `es.tener_present`, `ja.te_form`, `ja.potential`, `ja.potential_nominal`, `ja.request_polite`, `ja.polite_verb`, `ja.polite_register`, `ja.copula_polite`, `ja.counter_people`, `ja.counter_time`, `ja.counter_classifier`. New structures add a slug; slugs are never renamed, because `WI-08` matches held-out items to lessons on this value. |
| `ChallengeEntity` | `ruleText` | `TEXT?` | The 2–4 line rule shown to the learner, in English with target-language examples. Shown on the rule card (`WI-01`). Single source for both surfaces: the unit header renders the concatenation of its lessons' distinct `ruleText` values. |
| `ChallengeEntity` | `acceptedAnswers` | `TEXT?` | **Pipe-delimited** accepted answers for `FILL_BLANK`, e.g. `"soy\|estoy"`. Null for every other type. The primary answer stays in the correct `ChallengeOptionEntity.text` — reuse it, don't duplicate. |
| `ChallengeOptionEntity` | `errorTag` | `TEXT?` | Machine-readable reason this distractor is wrong, from a fixed vocabulary of **7** values: `WRONG_TENSE`, `WRONG_PERSON`, `WRONG_FORM`, `WRONG_COPULA`, `WRONG_CLASSIFIER`, `WRONG_REGISTER`, `UNRELATED`. Null means untagged. `UNRELATED` means "different sentence" — the failure mode this overhaul exists to remove, and it now has **zero** occurrences corpus-wide. **The vocabulary was extended from 5 to 7:** `WRONG_FORM` (verb-form / paradigm-row errors — Japanese potential, te-form, dictionary form; Japanese does not inflect for tense, so these are never `WRONG_TENSE`) and `WRONG_COPULA` (Spanish *ser* vs *estar* selection — a usage choice, not a register and not a person) were added after ~44 of 172 shipped tags were found to name the wrong error category. A tag must name the error the learner actually made, because the in-context hint is written from it. See §4 WI-06 for the before/after breakdown and the `ja.potential` nuance, and for the guard that makes a new `grammaticalFocus` declare which tags its distractors may honestly carry. |

**Shipped.** All four columns are live on the entities, and `MIGRATION_12_13` is registered in
`DuoDatabase`'s `companion object` in the `MIGRATION_11_12` style — three `ALTER TABLE` on
`challenges` and one on `challenge_options`, all nullable — with `version` bumped 12 → 13 at that
step. Room is at **15** as of `MIGRATION_14_15` (`WI-13`, below), not 13. Verified against the
KSP-generated `DuoDatabase_Impl.kt` schema. The
`MigrationTestHelper` runtime case proposed when this section was written was, for a long time, a
genuine gap: the whole suite used in-memory Room, which creates the current schema directly and
never executes a migration. That is now fixed — see the paragraph below.
**Migration coverage is now closed.** The gap noted above is closed: `MigrationTest` builds a real
v12 on-disk database and opens it through Room at v15, asserting the pre-upgrade rows survive and
that the four `MIGRATION_12_13` columns default to null while `heldOut` defaults to 0. Room
validates the migrated schema against every v15 entity on open, so a missing column, wrong type,
wrong nullability or wrong `DEFAULT` fails in the test. Because Room validates once, at the final
version, a second fixture starts from a real **v14** database and a third case runs
`MIGRATION_12_13` on its own to pin v13 from both sides. The `MigrationTestHelper` route specifically
is still not used and is not expected to be: `@Database(exportSchema = false)` means no schema JSONs
are exported for the helper to read, and the v12 fixture is instead derived verbatim from Room's
own generated `createAllTables` with the five added columns removed. See §11.

**Known residual raw-string boundary.** `ui/screens/ProfileTabScreen.kt:139-145` still holds raw type
strings (`"SELECT"`, `"ASSIST"`, `"WORD_BANK"`, `"LISTEN"`, `"MATCH_PAIRS"`, `"STORY"`). This is
deliberate and acceptable, not an oversight: that `when` reads `ExerciseTypeStatsEntity.type`, whose
own `type` column is the primary key of a *separate* stats table, not a `ChallengeEntity` accessor.
Keys in that table are historical rows that predate the enum and are written by the adaptive-practice
path, so the string boundary is a foreign-key join, not a challenge-type read. Every `ChallengeEntity`
read and write is enum-typed. If `ExerciseTypeStatsEntity.type` is ever migrated to `ChallengeType`,
this boundary disappears with it.

**Deliberately not yet persisted.** `errorTag` is a raw `String` with the vocabulary documented in
KDoc, not an `ErrorTag` enum; the `WI-06` test compares against the literal `"UNRELATED"`. `WI-06`
content work may introduce the enum, which would then be a compile-time face over the same column.

**Second migration — 13 → 14, shipped 2026-09-26 (`WI-08`).** The held-out decision this section
previously left OPEN is now **RESOLVED in favour of a persisted column**.

| Entity | Column | Type | Semantics |
|---|---|---|---|
| `ChallengeEntity` | `heldOut` | `Boolean = false` | `false` = taught, on the lesson path. `true` = unseen, reachable only by the checkpoint pool. **Non-null** with `DEFAULT 0`, so an upgrade never silently moves already-taught content out of a lesson and into the checkpoint pool — the safe direction for a learner mid-progress. |

`MIGRATION_13_14` is a single `ALTER TABLE challenges ADD COLUMN heldOut INTEGER NOT NULL DEFAULT 0`,
registered in the `addMigrations` chain and followed by `MIGRATION_14_15` (`WI-13`); `version` is
now 15. Filtering is enforced in the DAOs:
`LessonDao`'s `getChallengesForLesson` / `getChallengesForUnits` / `getUnitRuleTexts` all add
`heldOut = 0`, a new `getHeldOutChallengesForUnits` selects `heldOut = 1`, and both of
`ChallengeProgressDao`'s lesson-completion `HAVING` clauses exclude held-out rows so a lesson can
still be completed. Held-out items live inside the existing B1 `UnitPayloads` (challenge ids
`31000`–`31999`, options `310000`+, per §7) — no new unit or lesson namespace was introduced.

## 4. Work Items — Tier 1: core grammar acquisition

### WI-01 · Rule cards — **Tier** 1 · **Effort** M · **Status: DONE**
- **Scope.** Render `ruleText` as a dismissible card under the exercise prompt (replacing the
  five-header `when` at `MainActivity.kt:1039-1044` with a per-type prompt plus a `RuleCard`), and
  as a multi-line rule block on the unit header where the one-line `unit.description` renders
  (`MainActivity.kt:769-780`). Backfill `ruleText` for units 18/19/28/29 first, then the remaining
  morphology units, then every `grammaticalFocus`-tagged item.
- **Acceptance.** A B1 unit header explains the preterite ending pattern in 2–3 lines; a tagged
  challenge shows its rule without leaving the exercise; dismissal persists for the session only.
- **Files.** `MainActivity.kt`, `ui/components/RuleCard.kt` (new), `data/local/curriculum/*`,
  `data/repository/LocalProgressRepository.kt`
- **Shipped.** `ui/components/RuleCard.kt` (new) renders the challenge's `ruleText` under the prompt
  header, above the options. It is styled so it cannot be mistaken for an option: pale blue
  `#F0F7FF` fill, a blue `RULE • <focus label>` header, a 1.5dp `#B8DCFF` border, and **only the
  `GOT IT` `TextButton` is clickable** — tapping the body does nothing. Dismissal is scoped with
  `remember(challenge.id)` (`MainActivity.kt:1000`), so it persists for that challenge and resets on
  the next. That is *narrower* than the "session only" in the original acceptance, and deliberate:
  a rule that reappears on the same challenge is the teaching case, a rule that follows the learner
  onto a different item is noise.
- **Unit header shipped without a schema change.** `UnitSection` (`MainActivity.kt:763-766`) now
  takes `ruleTexts: List<String>` and renders a multi-line `GRAMMAR` block where the one-line
  `unit.description` sat, **falling back to `unit.description` unchanged when the list is empty**
  (`:794`). The list is the aggregation of the unit's lessons' distinct `ruleText` values, computed
  in `MainViewModel` (`:119-123`) from the `LessonDao.getUnitRuleTexts` query. The pre-existing
  corpus carries no rules, so its unit headers are visually unaffected — and no Room column was
  added for this surface.
- **Verified on a device, not just compiled.** See §10.

### WI-02 · `ChallengeType` enum — **Tier** 1 · **Effort** S · **Status: DONE**
- **Scope.** Replace the raw `String` type with a constrained enum covering exactly `SELECT, ASSIST,
  WORD_BANK, LISTEN, MATCH_PAIRS, STORY, CONJUGATE, FILL_BLANK`, plus `isChoice()` and
  `isProduction()`. Fix the stale comment at `ChallengeEntity.kt:10`.
- **Shipped.** `data/local/models/ChallengeType.kt` holds all 8 constants. `rawValue` preserves the
  exact pre-enum wire strings, so the persisted format is unchanged. `fromRaw()` throws
  `IllegalArgumentException` on anything unrecognised — a challenge row with an unknown type is
  corrupt curriculum, and a default would hide that behind a blank exercise screen. `ChallengeEntity.type`
  is now `ChallengeType`; the stale 4-value comment is replaced by the full 8-value list. Room
  `@TypeConverter fromChallengeType`/`toChallengeType` sit in `DuoDatabase`'s companion.
- **Deviation from the original scope, recorded deliberately.** The shipped `isChoice()` is
  `SELECT, ASSIST, MATCH_PAIRS, LISTEN, STORY, CONJUGATE` — it includes `ASSIST` and `STORY`, which the
  first draft of this item excluded. That is the more accurate reading: both present the learner a
  choice from offered options. The separate `isInflectional()` predicate proposed here was **not**
  built; `CONJUGATE` is instead handled explicitly wherever inflection matters. Any consumer that
  needs "grammar-bearing choice" today must test `type == CONJUGATE` rather than call a predicate.
- **Acceptance.** No raw `type = "..."` string literal remains in curriculum data or renderers; an
  unknown type fails a test rather than silently falling through to the generic `else`.
- **Files.** `data/local/models/ChallengeType.kt` (new), `data/local/entities/ChallengeEntity.kt`,
  `data/local/DuoDatabase.kt`, `data/local/curriculum/*`, `MainActivity.kt`, `ui/MainViewModel.kt`

### WI-03 · `CONJUGATE` type — **Tier** 1 · **Effort** M · **Status: DONE**
- **Scope.** Options are the **same** sentence or lemma in different tenses, persons, or registers;
  the learner picks the correct inflection. First targets: Spanish units 18/19
  (preterito/imperfecto), Japanese units 28/29 (te-form / potential). Every incorrect option carries
  a real `errorTag`, never `UNRELATED`.
- **Acceptance.** A preterito item cannot be passed by spotting vocabulary — deleting every
  non-verb word from all four options leaves them distinguishable only by the ending.
- **Files.** `data/local/curriculum/B1CurriculumData.kt`,
  `data/local/curriculum/AdvancedCurriculumData.kt`, `MainActivity.kt` (renderer),
  `CurriculumIntegrityTest.kt`
- **Shipped.** 15 `CONJUGATE` challenges authored across both courses, each with a
  `grammaticalFocus` and `errorTag`-tagged distractors. The renderer **reuses the existing
  `OptionCard` list**, so `CONJUGATE` introduces no new visual language: a question-header branch at
  `MainActivity.kt:1238` carrying a `GrammarFocus.label` focus chip (`:1262`), options below it.
- **The silent-fallback hole is closed at the UI layer too.** The prompt header at
  `MainActivity.kt:1091-1100` is now an **exhaustive `when` over all 8 `ChallengeType` values with no
  `else` branch**, so an unlisted type fails to compile. `ChallengeType.fromRaw()` already rejected
  an unknown *persisted* value by throwing; this closes the second half, where a perfectly valid
  enum value had no prompt string and fell through to a generic one.

### WI-04 · `FILL_BLANK` typed production — **Tier** 1 · **Effort** M · **Status: DONE**
- **Scope.** First true productive mechanic. Reuse the `OutlinedTextField` pattern already present at
  `MainActivity.kt:2204` (backup-JSON import dialog) and the answer-check pattern at
  `ui/MainViewModel.kt:504-522`. Accept an answer **list** so legitimate variants pass. Spanish
  matching is accent-tolerant (NFC-normalize, strip combining marks for comparison only) and
  case-insensitive; Japanese is exact after whitespace and full-width normalization.
- **Acceptance.** "Yo ___ estudiante" accepts "soy"; a wrong inflected form is rejected with a reason
  naming the rule broken (via `ruleText` + `errorTag`).
- **Files.** `data/local/entities/ChallengeEntity.kt`, `MainActivity.kt`, `ui/MainViewModel.kt`,
  `data/local/curriculum/*`
- **Shipped.** `grammar/AnswerGrader.kt` (new) owns all comparison, with 10 new unit tests
  (`AnswerGraderTest`) pinning the tolerance boundaries. It collapses whitespace runs, applies NFKC
  for full-width IME folding, then NFD plus stripping of **Latin** combining marks, and lower-cases
  with `Locale.ROOT` so a Turkish keyboard cannot change how `I` compares.
- **Correctness constraint — the dakuten is deliberately NOT stripped.** `normalize()` keeps a
  non-spacing mark whose base character is not Latin (`:55-60`), and a test asserts that two
  characters differing only by dakuten are **not** equal. Folding it would make か and が compare
  equal, collapsing two distinct Japanese words. *This is a correctness constraint, not an
  implementation detail: any future refactor that turns "strip accents" into "strip all combining
  marks" silently merges part of the Japanese vocabulary.*
- **Grading wired.** `MainViewModel.checkAnswer()` (`:591-599`) takes the correct option's `text` as
  the **primary** answer and unions it with the pipe-delimited `acceptedAnswers` variants, so the
  column adds legitimate variants instead of duplicating the answer. 12 `FILL_BLANK` challenges
  authored.
- **The rule arrives at the moment it is broken.** `FeedbackState.Incorrect` now carries the
  challenge's `ruleText` (`MainViewModel.kt:635`), so a wrong answer names the rule the learner just
  broke instead of sending them back to re-read the unit.

### WI-05 · Paradigm `WORD_BANK` targets — **Tier** 1 · **Effort** M · **Status: DONE**
- **Scope.** Replace roughly half of the 38 assembly items with forced-rule sets: the tile pool is
  *only* the inflectional forms of one word, so order and vocabulary are not the task. Minimum four
  sets — `hablo/comi/hablare` for one Spanish subject; `입니다/이에요/예요` for one Japanese noun; a
  `です/ます` contrast for one Japanese sentence; the three persons `hablo/hablas/habla` of one
  Spanish verb. Same renderer, no new UI.
- **Acceptance.** The assembly task is unsolvable without the rule; the tile pool contains no content
  words outside the paradigm.
- **Files.** `data/repository/LocalProgressRepository.kt`, `data/local/curriculum/*`
- **Shipped.** The corpus grew 124 → 158 challenges and `WORD_BANK` 38 → 45: a substantial share of
  the 38 originals were converted into forced-rule paradigm sets, plus 7 new paradigm items.
  **34 of the 45 `WORD_BANK` items now carry a `grammaticalFocus`**, on both courses, and the shipped
  shape is that **every distractor is the same frame with the wrong morpheme**, so vocabulary
  recognition alone cannot pass: Spanish `es.present_person` (30002 — *hablo* against
  *hablas/hablan/comí/hablaré*, wrong person and wrong tense on one frame),
  `es.preterito.regular` (30008 — *llegamos* against *llegué/llegáis/llegaba*) and `es.imperfecto`
  (30010); Japanese `ja.request_polite` (30021 — *ちょっと ___ ください*) and `ja.polite_verb`
  (30026 — *今、本を___います*). Same renderer, no new UI; each `ruleText` names the morpheme the
  wrong tile breaks, so the assembly is unsolvable without the rule. Note the concrete forms differ
  from the illustrative ones named in the original scope — the pattern shipped, not the literal
  `입니다/이에요/예요` set.

### WI-06 · Mandatory wrong-morphology distractors — **Tier** 1 · **Effort** S (structural) / M (corpus) · **Status: DONE**
- **Scope.** Option-level `errorTag` (§3) and a `CurriculumIntegrityTest` case asserting: any
  challenge with a non-null `grammaticalFocus` carries at least one incorrect option tagged with a
  genuine grammatical error (`WRONG_TENSE`, `WRONG_PERSON`, `WRONG_CLASSIFIER`, `WRONG_REGISTER`).
  This is the guard that stops the corpus regressing into translation quizzes as items are added.
- **Guard shipped.** `CurriculumIntegrityTest` gained *every challenge type in the corpus is a known
  ChallengeType* (`:145`) and *grammar-tagged challenges explain why at least one distractor is
  wrong* (`:161`), the latter asserting with a failure message naming the challenge id, its
  `grammaticalFocus` tag, the observed option `errorTag`s, and the fix. Verified non-vacuous by
  temporarily tagging challenge 1080, observing the failure, and reverting.
- **Content shipped.** The corpus is no longer vacuous: 78 challenges carry a `grammaticalFocus`
  across 16 slugs, and 172 distractors are tagged, with **zero** `UNRELATED` anywhere in the corpus.
  Both assertions that were open last turn are now in the suite: *error tags stay inside the fixed
  vocabulary and never mark a correct option* and *a grammar-tagged challenge never keeps
  an unrelated distractor*. `CurriculumIntegrityTest` is now 11 cases. **The second
  assertion is a strengthening, not a deviation from the `WI-06` wording:** as shipped it also
  checks that a *correct* option never carries an `errorTag` at all, which the original scope did not
  ask for. Recorded explicitly so a later reader does not treat it as scope creep — or quietly
  delete it as redundant. **`UNRELATED` has zero occurrences in the corpus.**
  Post-correction census (measured against the seeded database, 2026-09-26, 629 option rows total):
  `WRONG_TENSE` 56, `WRONG_PERSON` 42, `WRONG_FORM` 40, `WRONG_REGISTER` 18, `WRONG_CLASSIFIER` 9,
  `WRONG_COPULA` 7 = 172. `UNRELATED` remains 0.
- **RESOLVED — the vocabulary was extended from 5 values to 7, and ~44 tags were corrected.**
  Option (a) was taken. The five values did not cover tense, person, classifier and register, and
  roughly **44 of the 172 shipped tags (26%)** named the wrong error category. Measured per
  `grammaticalFocus` before the change:

  | Focus | Tags used | Why the tag is approximate |
  |---|---|---|
  | `ja.potential` | `WRONG_TENSE` ×18 | Potential form is an **ability/aspect** distinction, not a tense error. |
  | `es.ser_estar` | `WRONG_REGISTER` ×4, `WRONG_PERSON` ×4 | A *ser/estar* choice is **copula selection** driven by the noun's state — neither register nor person. |
  | `ja.potential_nominal` | `WRONG_TENSE` ×7 | Plain vs nominal potential is a **verb-form** distinction, not tense. |
  | `es.ser_present` | `WRONG_PERSON` ×3, `WRONG_REGISTER` ×3 | Copula selection again. |
  | `ja.te_form` | `WRONG_REGISTER` ×2, `WRONG_TENSE` ×2 | Te-form is a **paradigm row** (a conjugation class), neither. |
  | `ja.copula_polite` | `WRONG_TENSE` ×1 | Aspect/politeness conflation. |

  **Decision: option (a) — the vocabulary was extended from 5 values to 7 and the affected options
  were retagged.** Two values were added, both for errors the old five had no honest name for:

  - **`WRONG_FORM`** — the verb uses a *different form*: Japanese potential vs plain, the te-form and
    the ています frame, a dictionary form or stem that cannot fill the slot, and the contracted
    います → ます. Japanese does not inflect for tense, so an ability or construction error is never
    `WRONG_TENSE`. This absorbs `ja.potential`, `ja.potential_nominal` and `ja.te_form` — the tags
    that were previously mislabelled `WRONG_TENSE`.
  - **`WRONG_COPULA`** — Spanish picked the wrong copula, *ser* where *estar* belongs or the reverse.
    That is a usage choice, not a register and not a person. This absorbs `es.ser_estar` and
    `es.ser_present` — the tags that were previously mislabelled `WRONG_REGISTER` / `WRONG_PERSON`.

  **The new guard, by name: `an error tag is compatible with the grammatical focus of its
  challenge`.** It pins an explicit `honestTagsByFocus` mapping from a `grammaticalFocus` to the set
  of tags its distractors may carry, and asserts that every tagged challenge's tags fall inside the
  set declared for its focus. **A `grammaticalFocus` with no declared entry fails the test** rather
  than defaulting to "anything goes" — so new content cannot inherit a convenient tag by default;
  the author has to state what an honest distractor for that focus looks like, and the declaration
  is reviewable in the same diff as the new content.

  **One deliberate nuance: `ja.potential` permits both `WRONG_FORM` and `WRONG_TENSE`.** The Japanese
  potential form is a *form* error in the plain case — 話せない against 話す is a paradigm choice, not
  tense. But ます-form *does* inflect for the past (話せませんでした), so within a potential-focused
  item a past/polite swap really is a tense error. The mapping therefore allows both, rather than
  forcing a false choice. The failure message names the focus, its declared set, and the offending
  tags, so widening a mapping later is a visible act.

  This was a **coordinated change**, not a data-only edit: the fixed vocabulary is asserted by
  `CurriculumIntegrityTest`, so vocabulary, corpus and test moved together. `WI-06` stays DONE — its
  acceptance criterion (no `UNRELATED`, one explained distractor per tagged challenge) is met, and
  the tag vocabulary is now honest about the errors it names rather than approximating them.
- **Acceptance.** The test gains a failing-if-violated case and the whole corpus passes it.
- **Files.** `app/src/test/java/com/duo/app/CurriculumIntegrityTest.kt` (11 cases),
  `data/local/entities/ChallengeOptionEntity.kt`, `data/local/curriculum/*`

### WI-07 · Fix `LISTEN` direction — **Tier** 1 · **Effort** M · **Status: DONE**
- **Scope.** Options must be **target-language sentences**, so the listener reads the L2 orthography
  and internalizes the ending; or each item is paired with a `WORD_BANK` replay of the same
  `audioSrc`. Audit all 31 items. Rule text is unchanged where an option set is already L2.
- **Shipped.** Audited all 66 `LISTEN` option strings in the corpus: **zero** are English glosses.
  Options are now target-language sentences whose distractors are real morphology. Spanish items
  contrast tenses on the same frame (*Llegué / Llegamos / Llegaba a tiempo*; *Hacía / Hará / Hizo buen
  tiempo*; *Cuando era niño viví / vivía / Cuando fue niño vivía*) and article–gender agreement
  (*La cocina / El cocina*, *Los zapatos / Los zapato*). Japanese items contrast greeting and request
  forms (*おねがい / お願いします / お願いす*). The original defect case, challenge 1004, now offers
  *Buenos días* (correct) against *Buenas noches* and *Buenas días* — adjective agreement errors, not
  English.
- **Spot audit of distractor tagging, `B1CurriculumData.kt:443-455`.** The `WRONG_PERSON` versus
  `WRONG_TENSE` split on the reworked items is accurate throughout: *hacíamos* → *hacían* is
  `WRONG_PERSON` (right tense, wrong person) while *hacíamos* → *hicimos* is `WRONG_TENSE` (right
  person, wrong tense); *jugábamos* → *jugamos* is `WRONG_TENSE` but *jugábamos* → *jugaba* is
  `WRONG_PERSON`; *hablaron* → *hablaban/hablan* is `WRONG_TENSE` but *hablaron* → *hablé* is
  `WRONG_PERSON`. Recorded as evidence that the direction fix produced real morphemes rather than
  merely target-language-looking options.
- **Do not be alarmed by remaining English strings.** `1002`, `2001` and `2002` still contain English,
  but in the **prompt** of `SELECT`/`ASSIST` items ("Translate: 'Good morning'"), where naming the
  meaning is the instruction. `VocabScheduleEntity` and `PracticeTabScreen`'s `VocabWord` also carry
  English, legitimately — that is the FSRS vocabulary model, where English is the correct side of a
  flashcard. None of these are `LISTEN` answer choices, which is the only thing this item governs.
- **Acceptance.** No `LISTEN` item offers an English gloss as an answer choice; the `WI-06` test is
  extended to assert this (an English-only option in a `LISTEN` item is a failure).
- **Files.** `data/repository/LocalProgressRepository.kt`, `data/local/curriculum/*`,
  `CurriculumIntegrityTest.kt`

### WI-08 · Held-out checkpoint pool — **Tier** 1 · **Effort** M · **Status: DONE**
- **Scope.** Checkpoints draw from unseen sentences **in the same structures** (matched on
  `grammaticalFocus` and level), not the taught items. `startCheckpoint` gains a held-out flag; unseen
  items are marked in data and excluded from the lesson-path query.
- **Decision RESOLVED — a fifth column.** The held-out flag is persisted:
  `ChallengeEntity.heldOut` (`Boolean = false`, non-null, `DEFAULT 0`) with `MIGRATION_13_14` and
  `version = 15` (§3). The data-only alternative was not taken, so the schema is at 15, not 13. DAO
  filtering is shipped: taught queries carry `heldOut = 0`, `getHeldOutChallengesForUnits` selects
  `heldOut = 1`, and the lesson-completion `HAVING` clauses exclude held-out rows so a lesson remains
  completable.
- **Behaviour wired.** `startCheckpoint` calls `getHeldOutChallengesForUnits` first, dedupes the
  taught pool against the held-out ids, and takes every held-out item plus taught items topping up to
  `MAX_CHECKPOINT_CHALLENGES = 30` (`MainViewModel.kt:375`). It returns early only when *both* pools
  are empty, which is the A1/N5 fallback.
- **Pool seeded.** 8 held-out challenges inside the existing B1 payloads: Spanish `31000`
  (`CONJUGATE es.imperfecto`), `31001` (`FILL_BLANK es.imperfecto`), `31002`
  (`CONJUGATE es.preterito.regular`), `31003` (`WORD_BANK es.preterito.regular`); Japanese `31010`
  (`CONJUGATE ja.request_polite`), `31011` (`FILL_BLANK ja.request_polite`), `31012`
  (`CONJUGATE ja.potential`), `31013` (`FILL_BLANK ja.potential_nominal`). All `heldOut = true`;
  options occupy `310000`–`310206`. A1/N5 have no pool yet and take the taught fallback.
- **Bug fixed in the same path.** `getUnitIdsForLevel` mapped A1/N5 to unit ids `1,2,12..17` and N4
  to `18,19` — but the Japanese course's units are `20`–`29`, so a Japanese N5/N4 checkpoint was
  loading Spanish units or nothing. Verified against the seeded units: course 1 is `10`–`19`
  (`10`–`17` A1, `18`–`19` Pretérito/Imperfecto) and course 2 is `20`–`29` (`20`–`27` N5,
  `28`–`29` Te-form/Potential). It now returns those per course. `startPlacementTest`'s Japanese list
  had the same defect (`10`–`19`) and is fixed to `20`–`29`.
- **Acceptance.** A checkpoint can contain an item absent from the lesson path, and it can be failed
  on an item the learner never saw.
- **Files.** `ui/MainViewModel.kt`, `data/repository/LocalProgressRepository.kt`, `data/local/dao/*`,
  `data/local/curriculum/*`

## 5. Work Items — Tier 2: differentiators (Duolingo cannot match)

| ID | Scope | Effort | Acceptance | Files | Status |
|---|---|---|---|---|---|
| **WI-09** | In-context error-specific hints: a wrong tile explains the specific rule that tile breaks, driven by `MistakeDao` + `ExerciseTypeStatsDao` signals we already own; leverages `errorTag` (`B2`) | M | Tapping a wrong tile names the broken rule, not "try again" | `grammar/ErrorHint.kt` (new), `ui/MainViewModel.kt` (`errorHint`), `MainActivity.kt` (`ExerciseBottomBar`), `grammar/ErrorHintTest.kt` (new) | **DONE** — see §11 |
| **WI-10** | Generated structure drills: conjugation tables, te-form map, counter charts **generated from curriculum data** rather than hand-authored — systematic language means systematic generation (`B6`) | M | A new paradigm in curriculum data appears in the drill with no new drill code | `grammar/drills/StructureDrill.kt` (new), `grammar/drills/Paradigm.kt` (new), `ui/StructureDrillScreen.kt` (new), `MainViewModel.kt` (`grammarDrills`), `grammar/drills/StructureDrillTest.kt` (new) | **DONE** — device-verified 2026-09-27; see §11 |
| **WI-11** | Offline tap-to-lookup dictionary: tap any word in any exercise, option, or story line for gloss, part of speech, example sentence, audio. Fully local (`B4`) | M | Lookup works offline in exercise, option, and story contexts | `dictionary/Dictionary.kt` (new), `ui/DictionarySheet.kt` (new), `LocalProgressRepository.loadDictionary`, `DictionaryIndexTest`, `DictionaryRepositoryTest` | **DONE** — device-verified 2026-09-27; see §11 |

## 6. Work Items — Tier 3: cheap polish

| ID | Scope | Effort | Acceptance | Files | Status |
|---|---|---|---|---|---|
| **WI-12** | Per-unit vocabulary list screen (`A3`) — landed as `ui/UnitVocabularyIndex.kt` | S | Unit words browsable as a set; data already exists | `ui/UnitVocabularyIndex.kt`, `UnitVocabularyIndexTest` | **DONE** |
| **WI-13** | User-set daily goal — landed as a persisted `UserProgressEntity.dailyQuestGoal` with a Settings-screen step selector (10/20/30/50/100) | S | XP target is a persisted user setting | `UserProgressEntity.kt`, `DuoDatabase.kt` (`MIGRATION_14_15`), `UserProgressDao.kt`, `LocalProgressRepository.kt`, `OpenLingoBackup.kt`, `MainActivity.kt` (SettingsScreen), `MigrationTest`, `LocalProgressRepositoryTest` | **DONE** — screenshot-verified on device: selecting 100 moved the highlight and the lesson-map card from `60/30 XP ✅ complete` to `60/100 XP 🎯 Daily quest`; the 14 -> 15 upgrade is executed against a real on-disk v14 database in `MigrationTest` |
| **WI-14** | Audio speed selector replacing the hardcoded `0.6f`; the turtle button is **deleted** and `AudioSpeedSelector` shares a row with the speaker (`MainActivity.kt:1124-1128`). Speed now reaches all four play sites, not just `LISTEN` (`A15`) | S | 0.5x–1.5x selectable; slow control reachable in `SELECT`/`STORY` | `MainActivity.kt:1610` (`AudioSpeedSelector`), `ui/MainViewModel.kt:626-628`, `audio/AudioPlayer.kt:79` | **DONE** — screenshot-verified on device at 1.5x font scale; `FlowRow` cannot clip at any scale; rates confirmed reaching ExoPlayer via logcat. Selection resets to 1.0x between sessions (§10) |
| **WI-15** | Real `ASSIST` renderer (landed — `ASSIST` shares the `FILL_BLANK` scaffold branch at `MainActivity.kt:1207`, differing only in how the answer is produced) **or** removal from the README | S | No advertised mechanic lacks a renderer; README matches shipped reality | `MainActivity.kt:988,1207`, `README.md` | **DONE** — renderer kept (it is correct code); the README half of the alternative was taken, and now states plainly that the 2 corpus `ASSIST` items lack the blank scaffold their renderer parses |
| **WI-16** | Mistake reviews that **teach**, not just re-ask — `MistakeDao` re-asked the raw item (`A14`) | S | A mistake review shows the rule before the retry | `ui/screens/PracticeTabScreen.kt` (`MistakesReviewCard`), `MistakeDao.kt` (`getMistakeEntries`), `MistakeEntity.kt` (`MistakeEntry`) | **DONE** — see §11 |

## 7. Curriculum ID Conventions

- **Hard rule.** Challenge ids and option ids are **globally unique** across all six payload sources,
  enforced by the existing test `all entity ids are globally unique` (`CurriculumIntegrityTest.kt:30-41`).
  Unit, lesson, challenge, and option ids each have their own namespace; never infer a challenge range
  from a unit id.
- **Current occupancy (measured 2026-09-26).** Challenges `1001`–`31013`; options `10001`–`310206`.
  Baseline seed options use the `100xx` block, which sits *above* some lesson-block challenge ids —
  which is exactly why the test, not arithmetic, arbitrates.
- **Ranges for new work.** WI-03/WI-04/WI-07 items: challenges **30000–30999**, options
  **300000–309999**. WI-08 held-out pool: challenges **31000–31999**, options **310000–319999** —
  deliberately non-colliding and legible in review diffs.
- **Rule.** Pick a range, then let `CurriculumIntegrityTest` arbitrate. On failure, the implementer
  moves to the next range — they do **not** relax the test. Never renumber existing items to make
  room: ids appear in the JSON backup format and in `MistakeEntity.challengeId`.
- Held-out items must be real curriculum-quality sentences; a placeholder pool makes `WI-08` pass its test and fail its purpose.

## 8. Verification

- **Build/tests/release:** `./gradlew run` from
  `duo-android`; tests via `:app:test` (includes `CurriculumIntegrityTest`); release `:app:assembleRelease`.
  **`:app:assembleRelease` now carries a signing precondition:** it is refused outright unless
  `duo-android/keystore.properties` exists and is complete. That file is gitignored and never
  committed, so on any clean checkout the command **fails loudly** rather than quietly producing a
  debug-signed `app-release.apk` — the same filename and output path as a real one, so the wrong
  signature would otherwise surface only when somebody installed it. See **CONTRIBUTING.md**,
  [Creating a release keystore](../../CONTRIBUTING.md#creating-a-release-keystore-local-only), for the
  `keytool` command and the four required keys. For a throwaway local artifact — **not distributable,
  never uploaded or shared** — ask for the debug key explicitly:
  `./gradlew :app:assembleRelease -PallowDebugSigning`, which logs a warning. `:app:assembleDebug`
  and `:app:test` need no keystore and are unaffected.
  **Pass `--rerun-tasks` when you need the numbers rather than a green build line:** a bare `:app:test`
  legitimately reports `UP-TO-DATE` and executes nothing, and `BUILD SUCCESSFUL` in that state says
  only that the last run passed. Read totals from `app/build/test-results/testDebugUnitTest/*.xml`,
  not from the console.
- **Schema:** Room is at `version = 15` (`DuoDatabase.kt:49`) with `MIGRATION_12_13`, `MIGRATION_13_14`
  and `MIGRATION_14_15` registered (§3). Any further schema change needs a new migration and a §3 update.
- **Per-item bar:** every work item needs (a) its `CurriculumIntegrityTest` case green over the whole
  corpus, and (b) a launch of the actual app exercising the changed surface — rule card, `CONJUGATE`
  render, `FILL_BLANK` typing, a checkpoint containing a held-out item. A passing compile is not proof.
- **Regression bar:** after `WI-01`..`WI-08`, recompute the production ratio from the corpus. It must
  rise from 30.6%, and zero items may hold a `grammaticalFocus` with an `UNRELATED` distractor.
  **Both halves now hold, and this is the current measurement — not a projection.** `UNRELATED` is 0
  corpus-wide. The production-percentage metric this spec promised is, for the first time, measured
  against a real baseline rather than asserted, so the figures below are the ones the regression
  bar is judged from going forward. Re-measured 2026-09-26 by parsing every `ChallengeEntity`:

  | Framing | Before | After | Lesson path only (held-out excluded) |
  |---|---|---|---|
  | `isProduction()` — `WORD_BANK` + `FILL_BLANK` | 38/124 = **30.6%** | 57/158 = **36.1%** | 53/150 = **35.3%** |
  | `isProduction()` + `CONJUGATE` | 38/124 = **30.6%** | 72/158 = **45.6%** | 64/150 = **42.7%** |

  Record **both** framings, never one alone. The first keeps the comparison apples-to-apples with the
  30.6% baseline this spec opened with; the second is the honest ceiling for a corpus that now holds
  15 inflection-recognition items. The rise is real under either. Type census after: `SELECT` 47,
  `WORD_BANK` 45, `LISTEN` 31, `CONJUGATE` 15, `FILL_BLANK` 12, `MATCH_PAIRS` 4, `STORY` 2, `ASSIST`
  2 = 158 — note the recognition types are **unchanged** from the 124-item baseline, so the entire
  delta is new production and new inflection.

- **Corpus shape held.** The growth is additive: **158 challenges across the same 20 units and 38
  lessons**, with every recognition type unchanged from the 124-item baseline. No unit or lesson was
  invented to absorb the new items.
- **Known gaps in the shipped state.** The `WI-04` typed-input branch has landed, so the `FILL_BLANK`
  items render as typed production rather than falling through to the generic exercise branch, and
  the growth still splits into taught-on-path and held-out halves. The corpus census above is
  therefore no longer only a corpus fact: apart from the held-out items, which are checkpoint-only by
  design, it describes what a learner can actually reach. Measured on the lesson path specifically,
  that is `CONJUGATE` 11 of 15 and `FILL_BLANK` 9 of 12 — the remainder is the `WI-08` held-out pool,
  not orphaned content.
- **No Tier 1 mechanic is now unverified on a device.** `WI-14` was the last one and has been
  closed: screenshot-verified at 1.5x font scale, with playback rates confirmed reaching ExoPlayer
  via logcat rather than inferred from the UI (§10). The remaining honest gaps are recorded as gaps,
  not rounded up to DONE: `ASSIST` has a correct renderer but no written content (`WI-15`), and the
  audio speed selection does not survive a session boundary (§10).
- **Migration coverage.** The 12 → 13 → 14 → 15 chain is exercised against a real v12 on-disk database by
  `MigrationTest`, which was absent for the whole of this spec until the final pass. A second fixture
  starts from a real v14 database so `MIGRATION_14_15` runs on its own, and a third case runs
  `MIGRATION_12_13` alone to pin v13, because Room validates the schema only once, at the final
  version. In-memory Room tests never execute a migration, so that test is the only thing standing
  between a broken `MIGRATION_12_13` / `MIGRATION_13_14` / `MIGRATION_14_15` and a crash on every
  existing install.

## 9. Deferred & Policy-Excluded

**Deferred, with reasons — not scheduled.**

- **On-device ASR and "Say it" scoring.** Bundled ASR models are hundreds of MB (Vosk small ~40–50 MB
  per language; whisper.cpp `ggml-tiny`/`base` 75–150 MB, `small` ~240 MB) against a current ~11 MB
  APK and a 497 KB audio payload. Scoring learner speech against a synthetic Kokoro-82M reference
  produces misleading feedback, and fuzzy scoring gives false negatives on correct attempts with
  strong accents (`AUDIT_pronunciation.md` §7 Rank 3). **Record-and-self-assess** is acceptable as a
  later step because it needs no model and stays fully offline — but it introduces `RECORD_AUDIO` as
  a new privacy surface, must be requested in-context and never at startup, and must wait behind
  `WI-01`..`WI-08` (`AUDIT_pronunciation.md` §6, §7 Rank 2).

**Policy-excluded, permanently.** These are not feature gaps; building them would fork the product
(`AUDIT_feature_comparison.md`, "Do not build"): leaderboards/leagues, friends and social feed, live AI
conversation, clans/group quests, server-delivered content feeds, push re-engagement campaigns, paid
or gated content, gem/heart store. Everything ships unlocked; the free unlimited heart refill already
removes the need for a store.

## 10. Status Summary

**Status vocabulary — use these four values, nothing else.** §10 is the single place a work item's
state is updated, and it is updated in the same turn the work lands.

| Status | Meaning |
|---|---|
| `TODO` | Not started. |
| `IN PROGRESS` | Under active work by a named workstream. Add a qualifier after the status where the state is partial or gated, e.g. `IN PROGRESS — guard landed, content pending`. |
| `DONE` | Acceptance criteria met and independently verified. Cite the evidence in *Verified By*. |
| `BLOCKED` | Cannot proceed. Name the blocker in *Verified By*; a blocked item is never silently left `IN PROGRESS`. |

| WI | Tier | Effort | Status | Verified By |
|---|---|---|---|---|
| WI-01 Rule cards | 1 | M | **DONE** | `ui/components/RuleCard.kt`; `MainActivity.kt:1304-1310` renders `ruleText` above the options, dismissal scoped `remember(challenge.id)` (`:1000`); `UnitSection` takes `ruleTexts: List<String>` (`:763-766`) and falls back to `unit.description` when empty (`:794`), so no Room column was added. **Screenshot-verified on a device.** With `WI-14` now also screenshot-verified, **no Tier 1 mechanic is left unverified on a device** (§8) |
| WI-02 `ChallengeType` enum | 1 | S | **DONE** | `data/local/models/ChallengeType.kt`; `:app:test` 31/31 green at the time of the landing, **124/124 now** (see §11). The count grew because work items *added* cases — vocabulary screen, held-out pool, migration upgrade — and because the in-memory tests now cover the grammar branch; **no assertion was relaxed or removed to reach it** |
| WI-03 `CONJUGATE` type | 1 | M | **DONE** | 15 authored challenges across both courses; `MainActivity.kt:1238` branch reuses the existing `OptionCard` list with a `GrammarFocus.label` focus chip (`:1262`), so no new visual language. Prompt header at `:1091-1100` is an **exhaustive `when` over all 8 `ChallengeType` values with no `else`**, so an unlisted type fails to compile — closing the UI-layer silent fallback that `fromRaw()` only covered on the persistence side |
| WI-04 `FILL_BLANK` typed production | 1 | M | **DONE** | `grammar/AnswerGrader.kt` owns comparison; `AnswerGraderTest` 10 pin the tolerance boundaries. **Correctness constraint:** the dakuten is deliberately preserved (`:55-60`) and a test asserts two characters differing only by dakuten are *not* equal. `MainViewModel.kt:591-599` grades the correct option's text ∪ `acceptedAnswers`; `:635` carries `ruleText` into incorrect feedback; 12 corpus items. The §8 renderer gap is closed |
| WI-05 Paradigm `WORD_BANK` targets | 1 | M | **DONE** | corpus 124 → 158, `WORD_BANK` 38 → 45; **34 of 45 tagged** with a `grammaticalFocus`, every distractor the same frame with the wrong morpheme. Paradigm pools verified in source: 30002/30008/30010 Spanish, 30021/30026 Japanese |
| WI-06 Mandatory wrong-morphology distractors | 1 | S/M | **DONE** | 78 tagged challenges / 16 slugs, **172 `errorTag`s post-correction** — `WRONG_TENSE` 56, `WRONG_PERSON` 42, `WRONG_FORM` 40, `WRONG_REGISTER` 18, `WRONG_CLASSIFIER` 9, `WRONG_COPULA` 7 — with **`UNRELATED` 0 corpus-wide**; guard strengthened 8→11 cases, never relaxed. The new *grammar-tagged challenge never keeps an unrelated distractor* also asserts a **correct** option never carries an `errorTag`, a strengthening of the original `WI-06` wording. **The vocabulary follow-up is now CLOSED (was: ~44 tags approximate against a 5-value vocabulary).** The vocabulary was extended from 5 to 7 with **`WRONG_FORM`** (Japanese potential / te-form / dictionary form — Japanese does not inflect for tense) and **`WRONG_COPULA`** (Spanish *ser* vs *estar* selection), and the ~44 mislabelled tags were retagged. The new guard *an error tag is compatible with the grammatical focus of its challenge* now makes any new `grammaticalFocus` **fail the test until it declares which tags its distractors may honestly carry**, so content can no longer inherit a convenient tag by default. Full reasoning, the before/after table and the `ja.potential` nuance are in §4 WI-06 |
| WI-07 Fix `LISTEN` direction | 1 | M | **DONE** | all 66 `LISTEN` option strings audited, 0 English glosses; challenge 1004 now *Buenos días* / *Buenas noches* / *Buenas días* (gender-agreement near-miss as the diagnostic distractor). **Spot audit** of tagging at `B1CurriculumData.kt:443-455` confirms the `WRONG_PERSON` vs `WRONG_TENSE` split is accurate throughout |
| WI-08 Held-out checkpoint pool | 1 | M | **DONE** | `startCheckpoint` takes held-out first, tops up to `MAX_CHECKPOINT_CHALLENGES=30` (`:375`); 8 held-out items seeded (31000–31003, 31010–31013); `getUnitIdsForLevel` course split verified against seeded units |
| WI-09 In-context error-specific hints | 2 | M | **DONE** — device-verified | `grammar/ErrorHint.kt` composes a hint as diagnosis (from the picked option's `errorTag`) + rule (from `grammaticalFocus`, 16 focus profiles) + the correct form. `checkAnswer` derives it once, on the miss, and `ExerciseBottomBar` renders it above the challenge's `ruleText` — the two halves are "what you did wrong" and "what the rule is". **The mapping is derived, never authored per challenge:** 7 tags × 16 focuses. `ErrorHintTest` (19 cases) fails three ways on a new tag: `TAGS` is pinned against `CurriculumIntegrityTest`'s vocabulary, an unmapped tag returns `null` rather than an empty string, and every tagged distractor in the bundled corpus is swept. **Measured on device:** picking *Hablaba con él ayer* (tag `WRONG_TENSE`) against *Hablé con él ayer* on challenge 1082 produced `“Hablaba con él ayer” puts the verb in the wrong tense. A preterite verb ends in a past-tense ending that also changes with the subject's person. For the Spanish preterite, this sentence needs “Hablé con él ayer”.` — read out of the live `uiautomator` tree, not typed here |
| WI-10 Generated structure drills | 2 | M | **DONE** — device-verified | `grammar/drills/StructureDrill.kt` is a pure function of the payloads — no database, no Android, no clock — grouping `challenges.groupBy { it.grammaticalFocus }` over the two mechanics that name a single form (`CONJUGATE`, `FILL_BLANK`), taking each form from `acceptedAnswers` or the single `correct` option and each near-miss from the incorrect options' `errorTag` verbatim. **There is no list of paradigms in the file and no per-focus branch**, which is the acceptance criterion: a new paradigm appears with no new code. Held-out rows are excluded, so a drill can never be the thing that reveals a checkpoint item. **Verified on a device for the first time on 2026-09-27**, having previously been compile- and unit-verified only: Unit 9's `DRILLS · 5 forms` chip opens *Conjugation Drills* → "6 forms across 3 patterns · generated from this unit's lessons" → chips for PRESENT PERSON / IMPERFECTO / PRETERITO REGULAR → a paradigm table of real corpus forms (`hablar`/`yo`/`hablo`; `hablar`/`nosotros`/`hablamos`; `llegar`/`ellos`/`llegaron`; "Ayer nosotros ___ a Sevilla."/`viajamos`; "Mi amigo ___ el billete ayer."/`compró`), each row with a speaker that plays (ExoPlayer + opus decoder observed in logcat). `PRACTISE` opens a working quiz that grades — `hablo` → "✓ Correct!" — and a wrong form produces the same teaching voice the lesson uses: `hablaron` → "is the right tense but the wrong person or number", with `habláis` and `hablábamos` each carrying their own diagnosis. `ParadigmEntry.accepts` is literally `AnswerGrader.matches`, so the drill cannot accept a form `FILL_BLANK` would reject. **One real inconsistency found and fixed on the same pass:** the unit header chip read “DRILLS · 5 forms” above a drill screen reading “6 forms across 3 patterns” over the same data. The cause was ordering, not arithmetic — `unitDrillCounts` built one paradigm set across *every* unit and then attributed each merged row back to a single lesson, so a form that two units each teach under the same `grammaticalFocus` was folded into one row and the losing unit lost a form it really has. The merge itself is right for a table (a form taught twice is one conjugation); it is only wrong as the basis of a per-unit count. Slicing the corpus per unit *before* calling `StructureDrill.paradigms` makes the chip the same computation the screen performs. Verified on device: the chip now reads “DRILLS · 6 forms” and the screen beneath it reads “6 forms across 3 patterns” |
| WI-11 Offline tap-to-lookup dictionary | 2 | M | **DONE** — device-verified | `LocalProgressRepository.loadDictionary` builds the index from three flat reads that already exist — every seeded challenge with its options, plus the FSRS `vocab_schedule` table — so no dictionary asset is bundled and no column is added. **Only `correct` options become headwords**, deliberately: the Spanish seed offers `gracias` as the wrong answer to "Sí por favor", so indexing every option would define, authoritatively, a word the unit is teaching the learner to reject. **Device-verified 2026-09-27** across three surfaces: a long-press on a `CONJUGATE`/`LISTEN` option opens the sheet with gloss, category, speaker and "Offline · from the exercises in this app"; a tap in the per-unit vocabulary list opens it too; and **long-press lookup survives grading** — a wrong tile's `onLongClick` is deliberately not gated by `enabled` (`combinedClickable(enabled = false)` would kill it along with `onClick`), so a learner can look a word up in the question they just answered. Pixel-compared before and after the long press: the selected card, the feedback bar and the "Nicely done!" state are byte-identical, so a lookup can never be mistaken for a change to the submitted answer. **A distractor resolves to nothing, by design** — see §11 for the defect this pass found and fixed in the term-shaped surface |
| WI-12 Per-unit vocabulary list | 3 | S | **DONE** | `ui/UnitVocabularyIndex.kt` groups a unit's challenge options into topic buckets, reachable from the unit header on the lesson map; `UnitVocabularyIndexTest` (5 cases) pins the grouping, the fallback when a unit has no options, and the tap-to-audio behaviour. The words were previously only implicit in `challenge_options` and the FSRS table, so the list is a new surface rather than a reshuffle |
| WI-13 User-set daily goal | 3 | S | **DONE** — device-verified | `DAILY_QUEST_XP = 30` was a compile-time constant, so a learner who wanted 10 XP and one who wanted 100 shared a target. `dailyQuestGoal` is now a `user_progress` column (default 30) set from a five-step selector in Settings, read by the lesson-map quest card and the home-screen widget through one `DailyQuest` value whose `isComplete` is the single definition of "quest met". Carried in the JSON backup so a restore cannot silently reset it. `MIGRATION_14_15` takes the DB 14 -> 15 with `DEFAULT 30`, so an existing install keeps the exact quest it had |
| WI-14 Audio speed selector | 3 | S | **DONE** — device-verified | **Screenshot-verified on a device at 1.5x font scale.** The selector uses `FlowRow`, so it reflows onto a second row instead of clipping at any scale — that was the specific risk the shared row with the 72dp speaker created, and it does not materialise. **Playback rates were proven by instrumenting `AudioPlayer` and reading logcat**, not inferred from the UI: 0.5x and 0.75x were both observed reaching ExoPlayer, and the main speaker honours the selected rate. **The on-device pass found and fixed a real defect:** the selector had been threaded into all four play sites but only *rendered* for `LISTEN`, so non-`LISTEN` audio silently honoured an unselectable speed — the user could not change what they were hearing. Fixed by rendering the control for every challenge that carries `audioSrc`, which is why this item is DONE on device evidence rather than on a compile. **Known limitation, recorded not glossed:** the selected speed persists *within* a session but resets to 1.0x between sessions, because the `remember` is scoped to `ExerciseScreen`. Persisting it is not part of this item |
| WI-15 Real `ASSIST` renderer or README removal | 3 | S | **DONE** — renderer kept, advertised honestly | **Decision: keep the renderer, stop advertising the mechanic.** `ASSIST` has a real branch (`MainActivity.kt:1207`) that shares the `FILL_BLANK` scaffold and differs only in how the answer is produced, and that code is correct, so it stays — deleting working code to fix a *documentation* problem would be the wrong trade. The honesty problem is real and is now fixed at the source a learner actually reads: the **README** no longer presents assisted translation as a distinct mechanic. It states plainly that the 2 corpus `ASSIST` items lack the `___` blank their renderer parses and therefore play as ordinary multiple choice. **Measured, not asserted:** against the seeded database, `ASSIST` ids 1002 and 2002 have question strings `Translate: 'Good morning'` and `Which phrase means 'Good morning'?` — neither contains `___`, `…` or any other blank token, while every `FILL_BLANK` question does (e.g. 30003 `Cuando era niño, yo ___ en Madrid.`). The renderer is implemented; its content is not written yet, and the README says so. This spec already permitted README removal as the alternative, and that is the branch taken |
| WI-16 Mistake reviews that teach | 3 | S | **DONE** — device-verified | `MistakeDao.getMistakeEntries` now also selects `c.grammaticalFocus` and `c.ruleText`, and `MistakeEntry` carries both. **No migration:** these are the challenge's own columns read through the join that already existed, so no table and no column was added. The rule is surfaced in **two** places, and the earlier one is the point: `MistakesReviewCard` lists it under each entry (question → lesson • focus → rule), *and* the retry screen still shows the `RuleCard` above the options because `ruleText` is on the challenge itself — so the rule is read before the learner answers, not discovered afterwards. **A `null` `ruleText` degrades to no rule line at all**, verified on device against challenge 1001 (written before the grammar overhaul): the entry showed question + lesson only, no empty card. Both properties were read out of the live `uiautomator` tree |

**Final verification (2026-09-27, one authoritative run against the settled tree).**
`./gradlew :app:test :app:assembleRelease --rerun-tasks` from `duo-android`, `BUILD SUCCESSFUL`.
Note for anyone re-running: a plain `:app:test` can report `UP-TO-DATE` and execute **nothing** —
the first run of the day did exactly that. Use `--rerun-tasks`, and read the totals from the JUnit
XML rather than trusting the build line.
**Re-running the release half today requires signing credentials.** The run above was made on a
machine with a release keystore, which is why the bare command in it succeeds. As of the guard in
`app/build.gradle.kts`, a contributor without `duo-android/keystore.properties` gets a
`GradleException`; `-PallowDebugSigning` is the opt-in for a non-distributable local compile check.
See §8 for the full precondition. The `:app:test` half has no such precondition and stays green.

| Test class | Tests | Fail | Error | Skip |
|---|---|---|---|---|
| `AchievementsTest` | 4 | 0 | 0 | 0 |
| `CurriculumIntegrityTest` | 11 | 0 | 0 | 0 |
| `DictionaryIndexTest` | 26 | 0 | 0 | 0 |
| `DictionaryRepositoryTest` | 7 | 0 | 0 | 0 |
| `LocalProgressRepositoryTest` | 21 | 0 | 0 | 0 |
| `MigrationTest` | 3 | 0 | 0 | 0 |
| `UnitVocabularyIndexTest` | 5 | 0 | 0 | 0 |
| `data.fsrs.FsrsSchedulerTest` | 3 | 0 | 0 | 0 |
| `grammar.AnswerGraderTest` | 10 | 0 | 0 | 0 |
| `grammar.BlankScaffoldTest` | 3 | 0 | 0 | 0 |
| `grammar.ErrorHintTest` | 19 | 0 | 0 | 0 |
| `grammar.GrammarFocusTest` | 3 | 0 | 0 | 0 |
| `grammar.drills.StructureDrillTest` | 9 | 0 | 0 | 0 |
| **Total (13 classes)** | **124** | **0** | **0** | **0** |

`grammar.GrammarFocusTest` is declared in `BlankScaffoldTest.kt` alongside `BlankScaffoldTest`, so
the file count (12) is one lower than the class count (13) and a search for a file of that name finds nothing.

Release build: `BUILD SUCCESSFUL`, `app-release.apk` at
`duo-android/app/build/outputs/apk/release/app-release.apk`, **11,504,865 bytes** (≈11.0 MiB),
versionCode 2 / versionName 1.1.0, minSdk 26, no Room migration or schema-validation error.

**Final corpus figures (measured against the seeded database, not read from the payloads).**
20 units, 38 lessons, **158 challenges** = **150 on the lesson path** + **8 held-out**, 629 option
rows. Type census: `SELECT` 47, `WORD_BANK` 45, `LISTEN` 31, `CONJUGATE` 15, `FILL_BLANK` 12,
`MATCH_PAIRS` 4, `STORY` 2, `ASSIST` 2. **The census is corpus-wide; the lesson-path split for the
two grammar types is `CONJUGATE` 11 + 4 held-out and `FILL_BLANK` 9 + 3 held-out** — the gap is
WI-08 working as designed, not orphaned content: every held-out row carries a real `lessonId`
(e.g. 31000 → lesson 119, 31002 → lesson 117) and is reachable only by the checkpoint query. All
150 lesson-path challenges resolve their options (0 unresolved; every choice type has ≥2 options
and exactly one correct).

## 11. Progress Log

| Date | WI | Change | Verified By |
|---|---|---|---|
| 2026-09-26 | — | Spec created from the three audit reports; every claim re-verified against source. | Source reads (paths cited inline); no code changed |
| 2026-09-26 | WI-02, WI-06, §3 | **Schema workstream landed.** `data/local/models/ChallengeType.kt` added: 8-value enum, `rawValue` preserving the old wire strings, exhaustive `isChoice()`/`isProduction()`, `fromRaw()` throwing `IllegalArgumentException` on unknown values with no fallback branch. `ChallengeEntity.type` retyped `String` → `ChallengeType`; stale 4-value comment replaced with the full 8-value list; Room `@TypeConverter fromChallengeType`/`toChallengeType` added to `DuoDatabase`'s companion. All 124 corpus type literals and the `MainActivity`/`MainViewModel` dispatch seams converted (0 raw literals remain; 136 `ChallengeType.` references). Four nullable columns added — `grammaticalFocus`, `ruleText`, `acceptedAnswers` on `challenges`, `errorTag` on `challenge_options` — with `MIGRATION_12_13` registered in order and `version` bumped 12 → 13. `CurriculumIntegrityTest` gained the type-vocabulary and wrong-distractor cases. | `./gradlew :app:test` BUILD SUCCESSFUL; JUnit XML aggregates **31 tests, 0 failures, 0 errors, 0 skipped** across 4 classes (`CurriculumIntegrityTest` now 8). Migration cross-checked against the KSP-generated `DuoDatabase_Impl.kt` schema. |
| 2026-09-26 | WI-08, §3 | **Held-out decision resolved — a fifth column, not a data marker.** `ChallengeEntity.heldOut: Boolean = false` (non-null, `DEFAULT 0`), `MIGRATION_13_14` (`ALTER TABLE challenges ADD COLUMN heldOut INTEGER NOT NULL DEFAULT 0`) registered last in the chain, `version` bumped 13 → 14. DAO filtering landed: `LessonDao.getChallengesForLesson`/`getChallengesForUnits`/`getUnitRuleTexts` add `heldOut = 0`; new `getHeldOutChallengesForUnits` selects `heldOut = 1`; both `ChallengeProgressDao` lesson-completion `HAVING` clauses exclude held-out rows so lessons stay completable. Held-out items live in the existing B1 payloads, reusing the §7 `31000`–`31999` / `310000`+ ranges. | Source-verified: `version = 14` at `DuoDatabase.kt:49`, `MIGRATION_13_14` at `:166-170`, `heldOut` at `ChallengeEntity.kt:29`, 10 `heldOut` references across 4 files. **Not verified as behaviour:** `MainViewModel.kt:338 startCheckpoint` still calls the taught `getChallengesForUnits`, and 0 held-out items are seeded, so WI-08 is not DONE. |
| 2026-09-26 | WI-05, WI-06, WI-07, WI-08 | **Content workstream landed.** WI-05: 15 paradigm `WORD_BANK` items tagged across both courses, tile pools reduced to inflected forms of one word. WI-06: corpus tagged — 78 `grammaticalFocus` slugs, 172 `errorTag`s, zero `UNRELATED`; the two previously-missing assertions added (`error tags stay inside the fixed vocabulary…` `:183`, `a grammar-tagged challenge never keeps an unrelated distractor` `:209`). WI-07: all 31 `LISTEN` items reworked; challenge 1004's English-gloss options replaced with *Buenos días* / *Buenas noches* / *Buenas días*. WI-08: 8 held-out items seeded in the B1 payloads and `startCheckpoint` rewired to draw them first, topping up with taught items to 30. Also fixed a real bug: `getUnitIdsForLevel` sent Japanese N5/N4 checkpoints at Spanish unit ids (course 2 lives at units 20–29), and `startPlacementTest` had the same defect. | `:app:test` — JUnit XML aggregates **51 tests, 0 failures, 0 errors, 0 skipped** across 7 classes (3 new: `AnswerGraderTest` 10, `BlankScaffoldTest` 3, `GrammarFocusTest` 3; `CurriculumIntegrityTest` 8→10). Corpus audit in source: 0 `UNRELATED` tags, 0 English `LISTEN` options, unit-id mapping checked against every seeded `UnitEntity` (course 1 = 10–19, course 2 = 20–29). |
| 2026-09-26 | WI-06, §3 | **Tag-precision gap recorded, not resolved.** Cross-tabulating `errorTag` against `grammaticalFocus` shows ~44 of 172 shipped tags (26%) do not fit the fixed vocabulary: `ja.potential` (18) and `ja.potential_nominal` (7) are tagged `WRONG_TENSE` but the error is verb *form*/aspect; `es.ser_estar` (8) and `es.ser_present` (6) are tagged `WRONG_REGISTER`/`WRONG_PERSON` but the error is *copula selection*; `ja.te_form` (4) is a paradigm-row error. Three options recorded in WI-06 (extend vocabulary / document the approximation / extend only with `WRONG_FORM`). Not resolved unilaterally — vocabulary, corpus and `CurriculumIntegrityTest:183` must move together. | Measured directly from the corpus by joining each option's `errorTag` to its challenge's `grammaticalFocus`. **Separately checked and found already closed:** `TempMechanicsSeed.kt` was reported as still wired into `DuoApplication` — repo-wide search finds no such file and no reference to it, and it is absent from `git status` untracked files, so the renderers cleanup has already landed. |
| 2026-09-26 | WI-01, WI-03, WI-04, §4 | **Renderer workstream landed.** WI-01: `ui/components/RuleCard.kt` (new) renders `ruleText` under the prompt header, above the options — pale blue `#F0F7FF` fill, blue `RULE • <focus label>` label, 1.5dp `#B8DCFF` border, and only the `GOT IT` `TextButton` is clickable, so the body cannot be mistaken for a choice. Dismissal scoped with `remember(challenge.id)` (`:1000`): persists for that challenge, resets on the next. `UnitSection` (`:763-766`) now takes `ruleTexts: List<String>` and renders a multi-line `GRAMMAR` block where the one-line `unit.description` sat, **falling back to `unit.description` unchanged when the list is empty** (`:794`) — so the pre-existing corpus is visually unaffected and no Room column was added. WI-03: `CONJUGATE` renders through the existing `OptionCard` list with a `GrammarFocus.label` focus chip (`:1238,1262`); the prompt header at `:1091-1100` is now an **exhaustive `when` over all 8 `ChallengeType` values with no `else`**, so an unlisted type fails to compile — closing the UI-layer silent fallback that `ChallengeType.fromRaw()` only covered on the persistence side. 15 `CONJUGATE` challenges authored. WI-04: `grammar/AnswerGrader.kt` (new) owns all comparison; 12 `FILL_BLANK` challenges authored. | WI-01 **screenshot-verified on an emulator**, not just compiled — the only Tier 1 item with a device screenshot. Source-verified: `RuleCard.kt:38-67` (palette, border, `GOT IT` is the only `onClick`), `MainActivity.kt:1000` `remember(challenge.id)`, `:1091-1100` exhaustive `when` (8 arms, no `else`), `:763-766`/`:794` `ruleTexts` + fallback, `:1238`/`:1262` `CONJUGATE` branch and focus chip. `./gradlew :app:test` **BUILD SUCCESSFUL**; JUnit XML aggregates **51 tests, 0 failures, 0 errors, 0 skipped** across 7 classes. |
| 2026-09-26 | WI-04, §4 | **Grading correctness constraint recorded — the dakuten is not folded.** `AnswerGrader.normalize()` strips **Latin** combining marks only (`:55-60`), so an accented Spanish answer typed without its accent is forgiven while か and が must not compare equal; a test asserts two characters differing only by dakuten are unequal. Written into the spec as a **correctness constraint, not an implementation detail**: any refactor that turns "strip accents" into "strip all combining marks" silently merges distinct Japanese words. `checkAnswer()` (`:591-599`) takes the correct option's `text` as the **primary** answer and unions it with the pipe-delimited `acceptedAnswers` variants, and incorrect feedback now carries the challenge's `ruleText` (`:635`) so the learner sees the rule at the moment they break it. | Source-verified: `AnswerGrader.kt:47-65` (the guard is `Character.UnicodeScript.of(base) == LATIN`), `MainViewModel.kt:591-599` and `:635`. `AnswerGraderTest` 10 green. |
| 2026-09-26 | WI-05, §4, §7, §8 | **Content numbers refreshed to the shipped corpus.** Corpus **158** challenges / 20 units / 38 lessons; `WORD_BANK` **38 → 45** with **34 of 45** carrying a `grammaticalFocus` and every distractor the same frame with the wrong morpheme. Production (`WORD_BANK` + `FILL_BLANK`) is **57/158 = 36.1%**, up from the 30.6% baseline; counting `CONJUGATE` as production-grade it is **72/158 = 45.6%**. §7 id occupancy re-measured: challenges `1001`–`31013`, options `10001`–`310206`. | Re-measured by parsing every `ChallengeEntity` across the six payload sources: 158 total; type census `SELECT` 47, `WORD_BANK` 45, `LISTEN` 31, `CONJUGATE` 15, `FILL_BLANK` 12, `MATCH_PAIRS` 4, `STORY` 2, `ASSIST` 2; 78 carry a `grammaticalFocus` across 16 slugs; 172 `errorTag`s, `UNRELATED` 0. Paradigm shape spot-checked in source at 30002 / 30008 / 30010 / 30021 / 30026. |
| 2026-09-26 | WI-06, WI-07, §4 | **`UNRELATED` confirmed at zero, and the `LISTEN` tagging spot-audit recorded as evidence.** WI-06: the guard is now load-bearing rather than vacuous — 78 of 158 challenges carry a `grammaticalFocus`, so the *grammar-tagged challenges explain why at least one distractor is wrong* rule has real content to police. The second new assertion (`a grammar-tagged challenge never keeps an unrelated distractor`, `:209`) also asserts a **correct** option never carries an `errorTag`; recorded in §4 as a **strengthening** of the `WI-06` wording rather than a deviation. WI-07: audited distractor tagging at `B1CurriculumData.kt:443-455` — the `WRONG_PERSON` vs `WRONG_TENSE` split is accurate throughout (*hacíamos/hacían* person, *hacíamos/hicimos* tense, *jugábamos/jugamos* tense, *jugábamos/jugaba* person, *hablaron/hablaban* tense, *hablaron/hablé* person). Challenge 1004 re-confirmed: options `10012`–`10014` = *Buenos días* / *Buenas noches* / *Buenas días*. | `errorTag` tally parsed from source: `WRONG_TENSE` 82, `WRONG_PERSON` 42, `WRONG_REGISTER` 39, `WRONG_CLASSIFIER` 9, `UNRELATED` 0, total 172. `CurriculumIntegrityTest.kt:183` and `:209` read by name. |
| 2026-09-26 | WI-14, §6, §8, §10 | **`WI-14` stays IN PROGRESS, and the reason is recorded rather than glossed.** The audio speed selector compiles, is threaded through all four play sites and is unit-tested, but it has **not** been screenshot-verified on a device. It is the only Tier 1 mechanic without visual confirmation, and the selector shares a row with the 72dp speaker button after the turtle button was deleted. A passing build is explicitly not treated as proof, per the §8 per-item bar. | `AudioSpeedSelector` composable at `MainActivity.kt:1610`, wired at `:1124-1128` beside `AudioSpeakerButton` (`:1117`); `audioSpeed` state at `:1004` feeding `playAtChosenSpeed` (`:1005`). Build green; device screenshot absent — that absence is the reported finding. |
| 2026-09-26 | WI-01, WI-03, WI-04, WI-14, WI-15, §1, §3, §7, §8, §10 | **Spec synced to the landed Tier 1 state.** §10 moved `WI-01`, `WI-03` and `WI-04` to **DONE** and re-wrote every *Verified By* cell with cited evidence. `WI-14` stays **IN PROGRESS** with its verification gap named in the cell. `WI-15` stays **IN PROGRESS — decision OPEN**: the renderer landed, but whether `ASSIST` is meaningfully distinct from `SELECT` given 2 corpus items is unresolved, and this spec already permits README removal as the alternative. §4 items that read as plans were rewritten as shipped fact (`WI-01`, `WI-03`, `WI-04`, `WI-05`, `WI-06`, `WI-07`). §3 now states the data model went **12 → 14 across two migrations** and no longer reads as though 13 were current. §7 id occupancy and §8's stale "`FILL_BLANK` does not render yet" mid-state corrected; §8 now presents the production figures as the current measurement. §1's as-found baseline numbers were left unedited and a current-state pointer added beneath them. No section was renumbered and no `WI` id was changed. | `:app:test` re-run: **51 tests, 0 failures, 0 errors, 0 skipped** across 7 classes — `CurriculumIntegrityTest` 10, `LocalProgressRepositoryTest` 18, `AnswerGraderTest` 10, `BlankScaffoldTest` 3, `GrammarFocusTest` 3, `AchievementsTest` 4, `FsrsSchedulerTest` 3. `version = 14` at `DuoDatabase.kt:49` with `MIGRATION_12_13` (`:152`) and `MIGRATION_13_14` (`:166`) registered in order (`:187`). Documentation only — no `.kt` file modified. |
| 2026-09-26 | WI-12, §10 | **Per-unit vocabulary list landed; `WI-12` moved TODO → DONE.** The words a unit teaches are now browsable as one set, grouped by topic and reachable from the unit header on the lesson map, with tap-to-audio. Previously these words existed only implicitly inside `challenge_options` and the FSRS table — the list is a new surface, not a reshuffle. | `ui/UnitVocabularyIndex.kt`; `UnitVocabularyIndexTest` **5 cases, green** — pins the topic grouping, the empty-unit fallback, and the audio binding. |
| 2026-09-26 | WI-15, §10 | **README honesty pass — `WI-15` moved IN PROGRESS → DONE, decision recorded.** The README no longer advertises assisted translation as a distinct mechanic; it states plainly that the 2 corpus `ASSIST` items lack the `___` blank their renderer parses and therefore play as ordinary multiple choice. The `ASSIST` renderer itself **stays** — it is correct code, and deleting working code to repair a documentation problem would be the wrong trade. This spec already permitted README removal as the alternative, and that is the branch taken. | Confirmed by measurement, not by reading the prose: against the seeded database, `ASSIST` 1002 = `Translate: 'Good morning'` and 2002 = `Which phrase means 'Good morning'?` — neither contains `___`, `…` or any other blank token, while every `FILL_BLANK` question does (30003 = `Cuando era niño, yo ___ en Madrid.`). README line states the limitation directly. |
| 2026-09-26 | WI-06, §4, §10 | **errorTag vocabulary corrected: 5 values → 7, ~44 of 172 tags retagged.** The open decision recorded in §4 is now **option (a), taken**. `WRONG_FORM` was added (Japanese potential/te-form/dictionary-form — Japanese does not inflect for tense, so an ability or construction error is never `WRONG_TENSE`) and `WRONG_COPULA` was added (Spanish *ser* vs *estar* selection — a usage choice, not a register and not a person). The Japanese potential and te-form tags had been mislabelled `WRONG_TENSE`; the ser/estar tags had been mislabelled `WRONG_REGISTER` / `WRONG_PERSON`. **New guard, by name: `an error tag is compatible with the grammatical focus of its challenge`** — an explicit `honestTagsByFocus` mapping, where **a `grammaticalFocus` with no declared entry fails the test** rather than defaulting to permissive, so new content cannot inherit a convenient tag. **Recorded nuance: `ja.potential` legitimately permits both `WRONG_FORM` and `WRONG_TENSE`,** because the ます form really does inflect for the past (話せませんでした) even though the plain potential form does not. | `CurriculumIntegrityTest` now **11 cases, green**. Post-correction census measured against the seeded database (629 option rows): `WRONG_TENSE` 56, `WRONG_PERSON` 42, `WRONG_FORM` 40, `WRONG_REGISTER` 18, `WRONG_CLASSIFIER` 9, `WRONG_COPULA` 7 = **172 tagged**, `UNRELATED` **0**. Vocabulary, corpus and test moved together, as the coordinated nature of the change required. |
| 2026-09-26 | WI-14, §8, §10, §11 | **Final integration and verification pass.** `:app:test` + `:app:assembleRelease` re-run against the settled tree and the spec finalised. `WI-14` moved **IN PROGRESS → DONE** on device evidence: screenshot-verified at 1.5x font scale, the `FlowRow` selector reflows rather than clipping, and playback rates were proven by instrumenting `AudioPlayer` and reading logcat (0.5x and 0.75x both observed reaching ExoPlayer; the main speaker honours the selection). **The on-device pass found and fixed a real defect:** the selector had been threaded into all four play sites but only *rendered* for `LISTEN`, so non-`LISTEN` audio silently honoured an unselectable speed. **Known limitation recorded, not glossed:** the selected speed persists within a session but resets to 1.0x between sessions, because the `remember` is scoped to `ExerciseScreen`. | **58 tests, 0 failures, 0 errors, 0 skipped across 9 classes** — `LocalProgressRepositoryTest` 18, `CurriculumIntegrityTest` 11, `AnswerGraderTest` 10, `UnitVocabularyIndexTest` 5, `AchievementsTest` 4, `FsrsSchedulerTest` 3, `BlankScaffoldTest` 3, `GrammarFocusTest` 3, `MigrationTest` 1. Release `BUILD SUCCESSFUL`, `app-release.apk` **11,439,329 bytes**. **Two process notes recorded because they change how the numbers should be read:** (1) a plain `:app:test` reported `UP-TO-DATE` and executed **nothing** — `--rerun-tasks` is required, and totals are read from the JUnit XML rather than the build line; (2) `MigrationTest` is new — see the next row. |
| 2026-09-26 | WI-08, §3, §8, §10, §11 | **The 12 → 13 → 14 migration chain is now covered by a real upgrade test — the gap this spec previously only admitted to.** Every prior test used `Room.inMemoryDatabaseBuilder`, which creates the *current* schema directly and therefore never executes a migration, so a broken `MIGRATION_12_13` or `MIGRATION_13_14` would have left the whole suite green while every existing install crashed on upgrade. New `MigrationTest` writes a genuine **v12 on-disk** database (the v12 DDL taken verbatim from Room's own generated `createAllTables` with exactly the five added columns removed, so the fixture cannot drift from the entities), seeds real challenge/option/progress rows, then opens it through Room at version 14. Room validates the migrated schema against every v14 entity on open, so a missing column, wrong type, wrong nullability or wrong `DEFAULT` fails there rather than on a user's phone. **`MigrationTestHelper` was not usable and was not forced:** `@Database(exportSchema = false)` means no schema JSONs are exported, so the helper has no historical schema to read, and retro-generating a v12 export would mean rebuilding the app as it stood at v12. No dependency was added. | `MigrationTest` **1 case, green**, and **verified non-vacuous by mutation**: with `MIGRATION_12_13` deliberately altered, the test failed with Room's `Migration didn't properly handle: challenges(ChallengeEntity)` and named `acceptedAnswers` as the missing column. That is the exact production failure mode. **Worth recording as a process finding:** the mutation test subsequently caught a real defect I had just introduced myself — restoring `MIGRATION_12_13` after the mutation, I dropped the `acceptedAnswers` `ALTER TABLE` and the suite went red. It is a demonstration that this test is load-bearing, and the code is confirmed byte-identical to its pre-mutation state (snapshot hash returned to `C1F2`; `git diff` on `DuoDatabase.kt` shows only the intended prior-workstream schema change). |

| 2026-09-26 | WI-08, §3, §8, §10, §11 | **Final verification pass — the migration chain, the backup round trip and the daily goal, checked end to end, plus the device gaps closed.** **(`WI-10`, `WI-11`, `WI-13`, `WI-16` flipped to DONE with device evidence; `WI-09` re-confirmed.)** **The migration chain really was masked, and is now individually covered.** The suspicion in the brief was correct in substance: `MIGRATION_12_13` was executed *only* as the first link of the 12→13→14→15 chain, and Room validates the schema once, at v15, so nothing asserted the intermediate state. `MigrationTest` now runs the migration object **directly**, against a v12 database and with no successor, and pins v13 from both sides — the four nullable-TEXT-no-DEFAULT columns it owns, and the two later migrations own (`challenges.heldOut`, `user_progress.dailyQuestGoal`) that it must leave alone. It also asserts the pre-upgrade rows survived and that v13 is writable through the new columns. **Non-vacuity, honestly reported:** with `heldOut` temporarily added to `MIGRATION_12_13`, the new case failed with a precise diagnostic — and *so did the existing chain test*, which failed with `duplicate column name: heldOut`. So for an additive chain like this one, Room's final-schema validation catches most single-statement defects incidentally. What was genuinely missing was the *intermediate contract*, and that is now asserted directly rather than as a side effect. **The FSRS schedule was not round-tripping, and now is.** The round-trip test asserted `vocabSchedule` was *in the export* and then never looked at the restored table — and `resetAllProgress` does not clear `vocab_schedule`, so the row was still there from before the export and any restore assertion would have passed against a no-op. The test now reviews the sampled card (rating 3), re-reviews it *after* the export with a different rating, and asserts the restored row matches the **exported** state across `stability`/`reps`/`lapses`/`state`/`lastReview`/`due`, plus that the whole table matches the exported one. Checkpoints were already covered and still are. **Two real defects found on device, both in the daily-goal/backup path, both fixed with non-vacuous tests.** *(1) A restore inflated the day's XP.* `importBackupJson` restored `daily_activity` through `addXp`, which is the answering path's **accumulator** (`xp = xp + :delta`), so a backup row — an absolute total — was added on top of the row already there. Measured live: export at 20 XP for the day, reset, restore, and the quest read **40/100** while the learner's actual total was 20. That is precisely the "a goal that disagrees with their points" the brief asked about. The import now clears the table and writes absolute rows through a new `putDay`, matching how mistakes, mastery and checkpoints are already handled. Verified fixed on device: the same restore now leaves `daily_activity` at 20 against `points = 20`, `dailyQuestGoal = 100`. *(2) A reset left the quest behind.* `resetAllProgress` wiped completions, mistakes and mastery but not `daily_activity`, so a learner on 0 points still saw a bar reading the XP they had earned today — observed live as **20/30** on 0 points, and one correct answer later the bar claimed the day complete at 10 points. The reset now clears it, matching the promise its own dialog makes. **`WI-11` had a real defect too, on the surface where the string *is* the term.** Tapping a row in the per-unit vocabulary list resolved `Buenos días` to `buenos`, and a tap further right to `días` — opening a sheet that said the word had no English gloss, forty pixels above the `Good morning` the row was already printing. `Dictionary.resolveTap` deliberately prefers the single tapped word over the phrase, which is right on a question or a story line and wrong here, where the rendered text is the term and the words inside it are not separately taught. Added `Dictionary.resolveTerm`, which tries the whole string first and otherwise falls back to `resolveTap` unchanged, and `LookupText(preferWholeTerm = true)` on that row only. Confirmed on device at both x positions, and pinned by two new `DictionaryIndexTest` cases — one asserting the phrase wins at every offset, one asserting a string that is *not* a headword still behaves exactly as it does inside a sentence, so the fix cannot swallow surrounding text. **Device checks that came back clean.** The **speaker nested inside the `combinedClickable` OptionCard** plays and does not select: audio confirmed by the Opus decoder reaching ExoPlayer, and the card stayed `#FFFFFF` with CHECK disabled at `#E5E5E5` — with the positive control run in the same session (tapping the card text tinted it `#DDF4FF` and turned CHECK `#58CC02`), so the method is not vacuous. **Long-press lookup after grading** opens the sheet and leaves the graded state pixel-identical, as `WI-11` claims. **`WI-13` end to end**: selecting 100 moved the quest card to `20/100 XP`; reset returned it to the 30 default; restoring the backup brought back `points = 20`, `hearts = 4` and the 100 goal. **`WI-16`** was already device-verified and was not re-litigated. **Honest gaps, not passes.** Three device checks were **not** completed and are recorded as open: a `MATCH_PAIRS` tile long-press, a `STORY` line tap, and the mistake-review rule-before-retry (both `MATCH_PAIRS` and `STORY` live only in the B1 units, and the placement test was not reachable in the time available). A `FILL_BLANK` miss on a `ruleText`-null challenge was also not reached. What *was* observed for the null-rule case is its `WORD_BANK` equivalent on an A1 item, and the judgement asked for stands: the bar is **not blank and not broken** — it names the correct answer and omits the diagnosis, because the distractor carries no `errorTag` and the unit teaches no rule. For a word-level A1 item that is the honest outcome, and the hint system is silent there for a reason it can state. **Final numbers.** `:app:test --rerun-tasks` → **BUILD SUCCESSFUL**, **123 tests / 0 failures / 0 errors / 0 skipped across 13 classes**, read from `app/build/test-results/testDebugUnitTest/*.xml` (never the console): AchievementsTest 4, CurriculumIntegrityTest 11, DictionaryIndexTest 26, DictionaryRepositoryTest 7, LocalProgressRepositoryTest 21, MigrationTest 3, UnitVocabularyIndexTest 5, FsrsSchedulerTest 3, AnswerGraderTest 10, BlankScaffoldTest 3, ErrorHintTest 19, GrammarFocusTest 3, StructureDrillTest 8. `:app:assembleRelease --rerun-tasks` → **BUILD SUCCESSFUL**, 49 tasks executed, APK at `duo-android/app/build/outputs/apk/release/app-release.apk`. Baseline for this pass was 118 tests; the five new cases are the isolated `MIGRATION_12_13`, the daily-XP restore, the reset-clears-quest assertion, and the two `resolveTerm` cases. |

| 2026-09-27 | §8, §10, §11 | **Second verification pass — the three device gaps closed, and a third real defect fixed.** **The backup-import route to B1 did not work and is recorded as such.** A hand-written unlock backup was built and pushed to the device, but `adb shell input text` mangles the characters the JSON is made of — the field came back holding text that would not parse, the import silently no-opped (it swallows failures in `runCatching`), and `challenge_progress` stayed empty. So the unlock state was set by writing the same rows `importBackupJson` would have written. Same destination, same tables, no product code touched, and reverted by clearing app data afterwards. **`WI-16` mistake review, re-confirmed and now shown to degrade correctly on three different types.** The review list reads “Your missed challenges (4) — Each one shows its rule first — read it, then retry”, and the `es.preterito.regular` entry carries its rule (“The preterite marks one completed event: 1st person singular ends in -é, so ha…”) above the question. The retry screen then shows the `RuleCard` again, **above the options**, so the rule is read before the attempt and not discovered after it. **The `null` `ruleText` degradation was checked on three challenge types, not one:** `LISTEN` (1019), `MATCH_PAIRS` (1080) and `STORY` (1087) each render question + lesson only, with no rule line and no empty card. **`MATCH_PAIRS` tile long-press — the positive case, which is the one that matters.** All six tiles on challenge 1080 resolve: `Cuenta` and `Café` open the sheet with the term, its audio and its example; `Coffee`, `Agua`, `Bill` and `Water` open it reporting no English gloss yet, which is honest rather than a failure. **No tile selected while being looked up:** after all six long-presses the board was still unsolved and solvable, and it cleared on the first tap of each pair. **The silent case was not observable on this board** — every one of its six terms turns out to be a taught headword elsewhere in the corpus, so there was no distractor tile to be silent about. That is reported as an unobserved case, not a pass. **`STORY` line tap.** Challenge 1087's narrative renders in full and every word is tappable. Tapping `Llegamos` in “Llegamos a tiempo y la comida fue fantástica” opened the sheet for the **word**, not the line and not the sentence, carrying the two variants of the sentence it came from (“Nosotros llegamos a tiempo” / “Nosotros ___ a tiempo”). Note for anyone re-checking: the story node's accessibility attribute is single-quoted because the story contains `"`, so a double-quote-only regex silently misses the entire narrative — that cost real time here. **Third real defect: the drill chip counted a different thing from the drill screen.** The header read “DRILLS · 5 forms” above a screen reading “6 forms across 3 patterns”. `unitDrillCounts` built one paradigm set across every unit and attributed each merged row back to a single lesson, so a form that two units each teach under the same `grammaticalFocus` folded into one row and the losing unit lost a form it genuinely has. The fold is correct for a table and wrong as the basis of a per-unit count, so the fix slices per unit *before* calling `StructureDrill.paradigms` — which is now literally the computation the screen performs. **The doc comment above that block already promised the two “can never disagree”; they did, which is how it was found.** Pinned by a new `StructureDrillTest` case that builds two units sharing one form under one focus and asserts each unit's own slice keeps its row while a single merged call collapses them. Confirmed on device: the chip reads 6 and the screen beneath it reads 6. **Final numbers after this pass.** `:app:test --rerun-tasks` → **BUILD SUCCESSFUL**, **124 tests / 0 failures / 0 errors / 0 skipped across 13 classes** from `app/build/test-results/testDebugUnitTest/*.xml` (the only change since the previous run is `StructureDrillTest` 8 → 9). `:app:assembleRelease --rerun-tasks` → **BUILD SUCCESSFUL**, 49 tasks executed, `duo-android/app/build/outputs/apk/release/app-release.apk` at **11,504,865 bytes** (≈11.0 MiB), unchanged. |
