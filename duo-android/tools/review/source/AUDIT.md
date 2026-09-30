# Spanish romanisation — method, coverage, and what is not verified

**These are authored readings awaiting native-speaker review.** I am not a
native Spanish speaker, nothing here was listened to, and no automated check
can confirm that a romanisation is right. A native reviewer should treat this
table as a draft to correct, not as a reference. Where I am unsure I have said
so in the `confidence` column and in `dialectNote`, because a table with honest
gaps is worth more than a confident-looking wrong one.

---

## 1. What was produced

| file | rows |
|---|---:|
| `spanish_romanisation.tsv` | 270 |
| `candidates.tsv` | 270 — the option ↔ clip binding each row came from |
| `es_respelling.py` | the rule engine and the alphabet |
| `build_table.py` | the review decisions and the table build |
| `extract_es_options.py` | the option ↔ clip extraction from the Kotlin |

Columns are `optionId, lessonId, esText, romanisation, dialectNote,
confidence`, as specified.

## 2. Coverage

| | count |
|---|---:|
| Spanish clips in the app | **198** |
| Spanish clips with at least one row in the table | **198 (100%)** |
| Spanish options attached to a clip | **270** |
| distinct option texts | 242 |
| rows carrying a Spanish reading | 254 |
| rows whose text is English and therefore not romanised | 16 |
| lessons represented | 42 |
| rows with a non-empty `dialectNote` | 140 |

**Confidence distribution**

| confidence | rows |
|---|---:|
| high | 244 |
| medium | 7 |
| low | 3 |
| n/a (not Spanish) | 16 |

The three `low` rows are low **because of the corpus, not because of the
reading**: `compre_el_billete.ogg` and `cuando_era_nino.ogg` are each attached
both to a short Spanish SELECT option and to a STORY challenge whose passage
is the same file. The authoritative text for those clips is the whole passage
and is itself marked `inferred-uncertain`, so the option's phrase and the
clip's text are not the same audio. The romanisation is fine; the pair cannot
be compared. I did not resolve this, because resolving it means changing the
curriculum, which is not this task.

## 3. Which options were deliberately NOT romanised

837 options sit under a challenge that carries a Spanish clip. 270 are in the
table. The other **567 are the `WRONG_` distractors** — the options the lesson
teaches the learner to reject — and they carry no clip of their own, because
the app's own rule is that audio speaking a form the item calls an error
teaches the wrong thing out loud (`B1CurriculumData.kt:39-43`). Declaring a
reading for `Hablaba con él ayer` would declare a specification for audio that
deliberately says `Hablé`. Romanising them would manufacture defects. By
challenge type the 567 are SELECT 125, LISTEN 141, STORY 43, CONJUGATE 114,
FILL_BLANK 113, WORD_BANK 30, ASSIST 1.

**16 rows carry no reading because their text is English.** They are the
English side of a MATCH_PAIRS, the English answer to a STORY comprehension
question, or an English option of a dialogue-comprehension SELECT. The clip
plays when such an option is chosen, but the clip is speaking the Spanish
target, not the English text, so there is no reading of the option to declare.
They are in the table with `romanisation = (not Spanish)` and `confidence =
n/a` so the omission is visible and countable rather than a silent gap.

**A real gap this leaves:** 16 of the 198 clips are reachable *only* through
an English option, so those clips have no option-level specification at all.
They are the six `story_90x.ogg` passages plus the ten clips shared with a
STORY challenge (`compre_el_billete`, `cuando_era_nino`, `el_tren_perdido`,
`la_carta_nunca_enviada`, `la_llamada_que_nunca_hizo`, `dia_perdido_en_la_oficina`,
`el_plan_cambio`, `el_plan_del_domingo`, `la_manana_de_marta`). Their spoken
text is a passage, not an option, so covering them needs a passage-level
specification, which the option-level `romaji` field cannot carry.

## 4. The alphabet, and why it is built the way it is

The target is **Castilian**, because the shipped voice is `ef_dora` and the
app keeps `/θ/`, `/x/` and `/ʎ/`. The **G2P half** of that is verified and still
stands: `g2p_fixes.spa_g2p` is an espeak `es` G2P, and on the live pipeline
`caza → kˈaθa`, `gente → xˈɛnte`, `llave → ʎˈaβe`, `año → ˈaɲo`. The phoneme
string is clean and `/θ/` does reach the acoustic model.

**The audio half was never verified here, and both halves of it are now
unsupported — one disproven, one never measurable.** This section used to end
"No `/ç/`, no seseo, no yeísmo". That conclusion was drawn from the four G2P
outputs above — a check of the
text-to-phoneme step written up as a check of the audio: a string that still
contains `θ` says nothing about whether the decoder produced an interdental.

Measured later, on the shipped renders, against a CTC recogniser calibrated on
espeak-ng's genuine `θ`: `ef_dora` emits `θ` for **6.2%** of the same minimal
pairs against espeak-ng's own **52.1%**, at a **0%** false-alarm rate (Fisher
p = 1.4e-7 over the three Kokoro voices pooled). A forced aligner puts the `θ`
span **5.11 nats** below its own `/s/`, where a genuine interdental sits only
**0.69 nats** below, and a third cue — the paired 2500–4500 Hz band difference
— agrees in magnitude at about a quarter of the anchor. **The voice does not
produce an audible interdental.** The G2P outputs above were right; the
conclusion drawn from them was wrong, and the failure mode is worth naming: a
check of one stage of the pipeline was written up as a check of the next one.

What that does and does not retract, exactly:

* **"no seseo" is withdrawn.** It was a claim about the audio and it is false.
  **40 of the 198 Spanish clips** carry `/θ/` in the intended phoneme string,
  and the learner-visible romanisation writes that phone as `z` in 26 of the
  shipped option rows (38 rows encode `/θ/` or `/ʎ/` together). **Not fixed** —
  which variety the app teaches is still the open decision
  `docs/kokoro-tts.md` §1 describes, and this document does not make it.
* **"no yeísmo" is withdrawn as well.** An earlier correction of this section
  said it survived on a measurement. It does not: the band measure's yeísmo
  sensitivity control is **+0.51 dB**, which cannot separate a contrast that
  certainly exists, so **no number for `/ʎ/` versus `/ʝ/` is admissible in
  either direction**. The lateral is **unmeasured** — not absent, not
  confirmed, unknown, for every voice including the one the app ships.
* **The trill stays confirmed.** `ef_dora` recovers it in 12 of 14 minimal
  pairs (85.7%) against espeak-ng's own 21 of 27 (77.8%), at a comparable
  false-alarm rate, Fisher p = 0.006. That is the one Castilian contrast here
  with a passing control behind it.
* **`/ç/` was not re-examined** in that work at all. The nine assets that were
  once wrongly changed to `/ç/` have not been reintroduced — but that remains a
  G2P-side fact about the strings, and nothing here should be read as a fresh
  acoustic clearance of `/ç/`.

**Both halves of the original "no seseo, no yeísmo" are therefore unsupported,
and they failed differently.** The `θ` claim was a check of the
text-to-phoneme step written up as a check of the audio: a string that still
contains `θ` says nothing about whether the decoder produced an interdental.
The `ʎ` claim was worse, in the sense that it never had a check at all — it
was a summary claim carried across sessions without its control, and a cue
that cannot see a contrast produces no admissible number in either direction.
Neither is a measurement, and §4 of `docs/kokoro-tts.md` carries the full
figures.

So this is a **specific** measured failure — the trill is present and the
interdental is not — which is exactly what made it easy to miss. What the
audio option would need is not available: **no fully-permissive Spanish engine
was established anywhere**, so the defect is documented rather than re-rendered
away. Instruments, controls, licences and the rejected candidate references are
all in `docs/kokoro-tts.md` §11.

One token per phoneme, every token invertible:

| token | phone | token | phone |
|---|---|---|---|
| `z` | /θ/ | `j` | /x/ |
| `ll` | /ʎ/ | `y` | /ʝ/ |
| `ñ` | /ɲ/ | `i` | /i/, incl. the consonantal i |
| `rr` | trill /r/ | `r` | tap /ɾ/ |
| `bh` | /β/ | `dh` | /ð/ |
| `gh` | /ɣ/ | `g` | /ɡ/ |
| `ch` | /tʃ/ | `ku` | /kw/ |
| `ie` | /je/ | `ei` | /ei/ |
| `ue` | /we/ | `ui` | /wi/ |
| `ks` | /ks/ | `ˈ` | primary stress |

`/ŋ/`, `/β/`-versus-`/b/` and word-final vowel quality are deliberately **not**
given letters: they are allophones, and giving them letters would make the
detector compare distinctions the audio pipeline does not make either. The
review notes write them (`/ŋɡ/`); the table writes `n`+`g`.

The three distinctions the task asked to be marked, and how each is marked:

* **`ll` traditional vs `y`.** `ll` is kept as `ll`; `y` is the consonant
  `/ʝ/`. They can never collide, so `ella → ˈellha` and `ayer → aˈʝer` are
  distinguishable at a glance. `pasillo` is `paˈsiyo` (the /ʎ/ reading) with
  the southern `/paˈsiʝo/` recorded in `dialectNote`.
* **`j` vs consonantal `i`.** Written `j` is always `/x/`. A consonantal `i`
  is written `i`, never `y`, so the `/ʝ/` of `yo` and the `/i/` glide of
  `viaje` (`ˈbjaxe`) can never be confused. This is the reason `y` is not used
  for the glide anywhere in the table.
* **trill vs tap.** `rr` is a trill, `r` is a tap. Word-initial `r` and `r`
  after `n`, `l`, `s` are trills: `rojo → ˈrrojo`, `reunión → rreuˈnjon`.
  Everywhere else a tap: `perdido → perˈðiðo`, `madre → ˈmaðɾe`.

**The two cases where a naive doubling is wrong**, both enumerated in
`es_respelling.TAP_RR` rather than left to a rule:

* the written `rr` of `guitarra` is a **single tap** (`ɡiˈtaɾa`) because it
  closes the syllable; the common respelling *git-arra* is wrong;
* the written `rr` of `querría` **is** a trill (`keˈrria`) because it begins
  its own syllable. These two are the most likely rows in the table to be
  marked wrongly, and both carry a `dialectNote` saying which is which.

## 5. How the readings were produced

A rule engine (`es_respelling.py`) does the grapheme work from Spanish
orthography, and a review table (`build_table.REVIEW`) overrides it where the
spelling alone does not settle the reading. Two deliberate choices matter:

1. **The rules are orthographic, not the project's G2P.** If the respelling
   were produced by feeding the text through `spa_g2p`, the detector's "does
   the audio match the spec" question would compare espeak with espeak and
   could never fail. The two sides are derived independently, so a
   disagreement is real information.
2. **Stress is placed by the orthographic accent rule** (esdrújula / llana /
   aguda, plus a listed exception set for `ayer`, the only esdrújula in the
   corpus), not by reading the `ˈ` back out of espeak's output. Stress is
   therefore an independent claim the detector can actually falsify.

Every rule was checked against words where I am confident of the answer, and
that found and fixed nine real defects in the engine itself: an accented weak
vowel wrongly absorbed into a diphthong (`días` as one syllable), the silent
`u` of `gui` collected into a nucleus (`guitarra → guiˈtara`), the same `u`
deleted entirely (`guerra → ˈgra`), `y` claimed as a nucleus when it is the
consonant `/ʝ/` (`ya → i-a`), `g` before `u` treated as soft (`gusta →
ˈghusta`), `cu` treated as two vowels (`cuatro` as two syllables), the stress
mark placed at the nucleus rather than the syllable start (`mañana → mañˈana`),
an onset walk-back that ate the previous syllable's coda, and `/θ/` read where
`c` precedes `h` (`derecha`).

## 6. Alternatives I chose between

Where more than one reading is accepted I picked one and recorded the other:

| word | chosen | alternative | why |
|---|---|---|---|
| `llave` `ella` `llueve` `lluvia` | `/ʎ/` | `/ʝ/` (all Spain, all Latin America) | the app ships `ef_dora` and keeps `/ʎ/` |
| `pasillo` `billete` `silla` | `/ʎ/` | `/ʝ/` (southern Castilian, Canarian) | the northern Castilian default; the variant is recorded in `dialectNote` |
| `agua` | `/ɣ/` | `/ɡ/` | the intervocalic soft g, which is what the voice is expected to produce; `guitar` is `/ɡ/` and the contrast is noted |
| `hasta` | `/s/` | `/h/` (most of Spain) | the careful form; **medium** confidence because the aspiration is the single most common regional difference in this corpus |
| `México` | `/mexiko/` | `/meɣsiko/` — the x may be dropped in Castilian | **medium** |
| `ya` | `/ʝ/` | `/i/`-like weakening between vowels | **medium** |
| `siguió` | `siˈɣio` | a hard `/ɡ/` reading | **medium**; the letters are `s-i-g-u-i-o` and the soft g is correct |

## 7. Things I am unsure about, and one curriculum observation

* **`hasta` at medium.** If `ef_dora` aspirates, the learner will not hear the
  `/s/` I have written, and a naive detector would call that a defect when it
  is a dialect feature. Worth a listen.
* **`México` at medium.** Castilian may weaken `/ks/`.
* **`siguió` at medium.** The spelling is unusual and I want a second reader.
* **Proper nouns.** `Madrid`, `Marta`, `Ana`, `Carlos`, `Abuela`, `Nieto`,
  `Sevilla` appear in story passages rather than in the options; the readings
  are uncontroversial but I have not verified them against a recording.
* **No curriculum error found.** I looked for Spanish text that is not
  Spanish, a misspelling, or an agreement error, and did not find one worth
  changing. The one thing I noticed and did **not** touch: several clip
  filenames drop the tilde (`estudio_espanol_para_viajar_a_espana.ogg` for
  *español* and *España*). That is a filename, not a reading, and the option
  text carries the tilde, so the romanisation follows the option text. Noted
  in `dialectNote` rather than "corrected".

## 8. What this table does NOT establish

* **It does not establish that the readings are correct.** That would need a
  native speaker — see the final bullet for why that will not happen.
* **It does not establish that the audio is correct.** The detector that will
  consume this table compares the audio pipeline's output for the clip text
  against the phonemes of this table. A **passing** detector run proves only
  that the audio matches the authored spec. That is exactly the proof boundary
  the Japanese work established: it detects the app contradicting itself, not
  the app being wrong.
* **It does not cover 16 clips**, which have only English options (section 3).
* **It does not cover stress in the audio.** The comparison is at phoneme
  level; a clip that is phonemically right but stressed on the wrong syllable
  will not be caught. That limit is stated in the detector's own report rather
  than hidden.
* **No native speaker will ever review this table or this audio.** That is a
  permanent property of the project, not a pending task, and every other section
  of this document that says something "needs a native speaker" is describing a
  check that will not occur. Automated instruments can establish mechanical
  facts — which phones are present, how long they last, whether a clip decodes
  at all. They cannot judge naturalness, accent quality, or whether a clip is
  fit to teach a beginner. **This table therefore ships as an authored draft
  that nobody has checked**, and `duo-android/tools/review/` exists for whoever
  may one day open it: it is not a check that has happened, and nothing here
  should be read as one.
* **One known inconsistency, reported and deliberately not fixed: `llave`.** The
  learner-visible reading is `la llˈabhe` — `B1CurriculumData.kt:1535`,
  `spanish_romanisation.tsv` row 501190, and the shipped `data/clips.json` all
  carry the same string, and `build_table.REVIEW` maps `llave` to it. In this
  table's own alphabet `bh` is /β/, and the project's patched G2P agrees on the
  phone: `g2p_fixes.spa_g2p("La llave")` returns `la ʎˈaβe`, which is the
  `phonemes` field `clips.json` already ships. A third form of the row,
  `ˈllabeh`, has also been reported against it; **that string does not occur
  anywhere in the repository or in `.scratch/`, and no word in the 270-row table
  ends in `eh`**, so whatever produces it is not a convention of this table and
  could not be reproduced. Until it is traced, `llave` is a known unresolved
  row: the reading is defensible, but one word's `b` is spelled three ways
  across three copies of the same data with no check between them. **Not
  fixed** — a reviewer's first disagreement with this table should not be on a
  row the project has not reconciled with itself.
* **Nothing was listened to, and no `.ogg` asset was read or modified.**
