# Curriculum Roadmap: Beyond A1 / N5

This document outlines the pedagogical scope, unit structure, and audio
production requirements for expanding Duo into intermediate levels.

**Status summary.** Units 9–18 of the Spanish course and 9–18 of the Japanese course
have shipped. The Japanese units are genuinely at the level this roadmap names
(JLPT N4: te-form, polite requests and potential forms, then the past, plain-vs-polite
register, the い/な adjective classes, ability, opinion and the giving/receiving trio,
then the passive, the causative and the relative clause, then the three conditionals, the polite volitional, keigo and the three readings of られる). The Spanish course spans two
labelled levels: **units 9–10 are CEFR A2** — the regular preterite and the imperfecto
only, no irregular stem anywhere — and **units 11–16 are the B1 material**:
the irregular and stem-changing preterite (`tuve`, `pude`, `hice`, `dije`, `estuve`,
`quise`, `vino`, `dormí`, `pidió`), the past perfect frame (`había salido`,
`había hecho`, `había dicho`), the regular and irregular conditional (`hablaría`,
`tendría`, `haría`, `podría`, `diría`, `vendría`, `saldría`), the polite periphrasis
(`me gustaría`, `querría`, `podría` + infinitive) and the connectives of purpose,
cause, result and concession (`para`, `porque`, `así que`, `entonces`, `aunque`,
`pero`) in units 11–12; then the subjunctive (`quiero que vengas`, `no creo que sea`,
`para que`, `a menos que`), the imperative in all three shapes (affirmative `tú`,
affirmative `usted`, negative), the direct and indirect object pronouns, `gustar` and
the reflexive verbs including impersonal `se` in units 13–14, the subjunctive perfect, the unreal past, reported speech and `por`/`para` in units 15–16. The A2 checkpoint draws
units 18–19 and the **B1** checkpoint draws units 30–37, which now includes the two
themed vocabulary units 17–18 (ids 36–37).

**Vocabulary coverage.** The corpus teaches **690 distinct Spanish and 592 distinct
Japanese headwords**; units 1–8 contributed 76 / 62 of them, units 9–16 added the
grammar-driven rest, and the themed units 17–18 roughly doubled both totals. A
"headword" here is exactly what the app treats as one: a string that at least one
challenge marks as a **correct answer**, keyed `trim().lowercase()` — the same key `DictionaryIndex` indexes and
`UnitVocabularyIndex` compares. Wrong answers are excluded, so a distractor never
counts, and 9 Spanish / 10 Japanese headwords appear only in the held-out checkpoint
pool, which tests rather than teaches. This is **not** a row count: the FSRS
`vocab_schedule` table is a hand-seeded set of 14 review cards (6 `es`, 8 `ja`) that
does not grow with the corpus, and it decides what is *due for review*, not what a unit
may *list*. The per-unit vocabulary list therefore reads the corpus directly — 681
Spanish and 582 Japanese headwords are on a lesson path and are all browsable — and
excludes the held-out-only ones, because printing a checkpoint's unseen answer in a
study list would hand over what the checkpoint exists to test.
`docs/android-architecture.md` §2.1 tabulates all three surfaces.

**Still not authored in Spanish.** Units 15–16 closed the three gaps this section used to
name: the subjunctive *perfect* (`hubiera`/`hubiese` + participle) is taught across
`ojalá`, `como si` and `si`, reported speech has its own `dijo que` frame with and
without tense backshift, and `por` is now taught against `para` by cause, exchange,
result, purpose, direction and recipient rather than appearing only inside fixed phrases.
The honest label for the Spanish course is still **A2 → B1**, and what remains missing
is **breadth, not grammar** — verified against the corpus:

- **Reporting is modelled on a single verb.** `dijo que` is the whole frame; there is no
  `cuenta que`, `explica que` or `asegura que`, and so no lesson that makes the learner
  notice the backshift does not depend on the reporting verb at all.
- **No indirect command, and no second backshift.** The corpus reports speech that was
  present, future or preterite, but never reports a command (`dijo que viniera`) and
  never reports a form that had already moved back once.
- **The future and the imperfect subjunctive are untaught.** The subjunctive appears
  only where the corpus needs it; there is no `sea`, `tenga` or `vaya` paradigm, so the
  irregular subjunctive is entirely absent.
- **Vocabulary is the remaining shortfall, and it is now a matter of degree rather
  than of absence.** The travel, work, health, home and food themes the roadmap
  sketched for units 11–12 are now taught as units 17–18 in their own right, taking
  Spanish from 305 to 690 headwords. That is a working starter set, not a broad B1
  vocabulary: a course at this level wants roughly **2,000** Spanish headwords, so the
  course still carries about a third of the breadth it claims by level.

**Still absent at N4.** Units 15–16 closed the four points this section used to name:
the three conditionals (`〜たら`, `〜なら`, `〜ば`) are taught as three, the polite
volitional (`〜ましょう`) is a form with its own focus, 尊敬語 and 謙譲語 are introduced
with their everyday substitutions, and the られる disambiguation is now a rule with its
own focus rather than four confrontations. What is still missing is **breadth**, and the
following was checked by searching the corpus rather than assumed:

- **〜なければなりません, 〜てもいい and 〜たことがあります now ship**, in units 17–18
  with the themed vocabulary, each as a focus of its own (`ja.necessity`,
  `ja.permission`, `ja.experience`) rather than as an incidental ending. What is still
  thin is that each has one lesson's worth of material rather than a paradigm.
- **〜ようにする / 〜ことにする** still appear zero times, so the corpus never contrasts
  intention with decision. This was skipped deliberately rather than overlooked: the
  frames belong to a grammar unit, not to a vocabulary one, and shipping them with a
  single example each would claim coverage the corpus does not have.
- **〜ましょうか** appears zero times, so the polite invitation with rising intonation
  is absent while the plain proposal is taught.
- **Keigo is an introduction, not a system.** 尊敬語 here is 召し上がる / なさる /
  いらっしゃる / おっしゃる / 申し上げる, and 謙譲語 is 伺う / 拝見する / いたす — the
  everyday substitutions, drilled and nothing more. There is no お〜になる pattern beyond
  a single item, no double honourific, and none of the 謙譲語I / 謙譲語II split a business
  course would need.

The `LISTEN` mechanic is no longer a gap: the corpus files carry 70 `LISTEN` challenges
(100 counting the ones seeded with the A1 and N5 units), sitting in Japanese units 9–18
(1, 1, 5, 4, 3, 4, 3, 3, 6 and 4) and Spanish units 11–18 (6, 6, 3, 4, 3, 3, 6 and 6),
not only in the A1 and A2 units.

## Spanish (CEFR B1 Threshold)

Target: Units 9–16. Shipped: 23 lessons and 195 challenges.

Status: Units 9–10 have shipped in `B1CurriculumData.kt` (`spanishA2Units`, unit ids
18–19) — 4 lessons and 31 challenges, and they are **A2-level content, not B1**
(regular preterite and imperfecto only; see the summary above). Units 11–12 shipped
first (`spanishB1Units`, unit ids 30–31) — 6 lessons and 78 challenges. Units 13–14
followed in the same list (unit ids 32–33, lessons 600–606, challenges `70000`-`70066`,
options from `700000`) — 7 lessons and 45 challenges, and all four *are* the B1 material
this roadmap asked for. Note that the unit themes the roadmap originally sketched for
11–12 (travel complaints, future plans) were not what the grammar gap needed: the
shipped 11–12 teach the irregular preterite, the past perfect, the conditional and the
connective layer instead, and travel and plans vocabulary rides along inside them. The
same held true for 13–14, which the roadmap had left as the open B1 gap.

### Unit 9: Past Tense — Pretérito Indefinido
• Focus: Regular `-ar`, `-er`, `-ir` past actions (hablé, comí, viví)
• Dialogue / Story: *Un viaje a Sevilla* (A trip to Seville — train tickets, hotel check-in)
• Audio needed (ef_dora):
  - `hable_con_el.ogg` ("Hablé con él ayer")
  - `llegamos_a_tiempo.ogg` ("Llegamos a tiempo")
  - `compre_el_billete.ogg` ("Compré el billete de tren")

### Unit 10: Past Tense — Imperfecto
• Focus: Descriptions, habits, states (era, tenía, vivía cuando era niño)
• Dialogue: *Cuando era pequeño* (childhood habits)
• Audio needed (ef_dora):
  - `cuando_era_nino.ogg` ("Cuando era niño vivía en Madrid")
  - `hacia_buen_tiempo.ogg` ("Hacía buen tiempo todos los días")

### Unit 11: Irregular Past — Pretérito irregular + Pasado Perfecto *(shipped, `spanishB1Units` unit 30)*
• Focus: the high-frequency irregular preterite (tener→tuve/tuviste/tuvo, poder→pude/pudo,
  hacer→hice/hizo, decir→dije/dijimos, estar→estuve, querer→quise, venir→vino, poner→pusimos),
  the -ar/-er/-ir stem changers (dormir→dormí, pedir→pedió, descubrir→descubrimos,
  seguir→siguió) and the past perfect frame (había + participle, including the irregular
  participle hecho, dicho, puesto, ido, visto)
• Lessons: *I Had It* / *How the Day Went* / *Before All That*
• Held out for the B1 checkpoint: siguió, volvió, comprado, abrí
• Audio: recorded — 32 clips, one per challenge, wired after each was checked against the text it
  attaches to. SELECT speaks its correct option (and the correct option carries the same clip),
  CONJUGATE speaks the target form on the challenge only, FILL_BLANK and WORD_BANK speak the full
  target sentence, STORY speaks the story body. Terminal full stops were added where the option
  text has none, and the two STORY clips render `\n\n` paragraph breaks as full stops; no other
  character differs.

### Unit 12: Conditional, Periphrasis & Connectives *(shipped, `spanishB1Units` unit 31)*
• Focus: the regular conditional (hablaría, comería, viviríamos) and the irregular one
  (tendría, haríamos→haría, podríamos, diría, vendríamos, saldría, estaría), the future
  against the conditional (tendré vs tendría), the polite periphrasis (me gustaría,
  querría, podría + infinitive) and the connectives (para, porque, así que, entonces,
  aunque, pero)
• Lessons: *What Would You Do?* / *I Would Like, Please* / *Why, So, Although*
• Held out for the B1 checkpoint: harías, comería, diría, pero
• Audio: recorded — 36 clips on the same rules as unit 11. `estudio_espanol_para_viajar_a_espana`
  hangs off the SELECT item 50226 rather than the FILL_BLANK 50223, because the clip speaks the
  whole sentence and 50223's blank takes only the connective `para`; `tendria` hangs off the
  direct conjugation item 50203, not the nuance item 50214.

### Unit 13: The Subjunctive *(shipped, `spanishB1Units` unit 32)*
• Focus: the subjunctive after querer / esperar / necesitar / buscar + que (quiero que
  vengas), after emotion, doubt and negation (no creo que sea, me alegro de que estén
  listos), and after para que and a menos que (para que lo sepas, a menos que llueva)
• Lessons: *What I Want You To Do* / *Feelings, Doubts, Denials* / *So That, Unless*
• Grammar slugs: `es.subjunctive.wants`, `es.subjunctive.emotion_doubt`,
  `es.subjunctive.purpose_concession`
• Held out for the B1 checkpoint: nothing — this unit adds no held-out items, and the
  B1 pool stays fixed at 8, drawn from units 30–31
• Audio: recorded — 42 distinct clips across units 13–14, every one wired and every one
  referenced, on the same rules as units 11–12: a SELECT speaks its correct option and
  the correct option carries the same clip, a CONJUGATE speaks the target form on the
  challenge alone so the option grid never plays the answer, and no `WRONG_*` distractor
  in either unit carries a clip.

### Unit 14: Object Pronouns & Reflexive Verbs *(shipped, `spanishB1Units` unit 33)*
• Focus: the direct object pronoun in gender and number (lo, la, los, las), the indirect
  object pronoun (le, les) and the way `gustar` runs backwards so the liked thing is the
  subject, the reflexive pronouns that agree with the subject (me levanto, se peina) and
  impersonal `se` (se habla español / se hablan muchos idiomas)
• It closes with the imperative in all three shapes: the affirmative `tú` command with
  the final -s dropped (habla, ven), the affirmative `usted` command as the third person
  (hable, siéntese) and the negative command as *no* + subjunctive (no corras, no digas
  eso)
• Lessons: *Him, Her, It* / *To Me, To You* / *Doing It Yourself* / *Give Orders*
• Grammar slugs: `es.object_pronoun.direct`, `es.object_pronoun.indirect`, `es.gustar`,
  `es.reflexive.pronoun`, `es.reflexive.impersonal_se`, `es.imperative.affirmative`,
  `es.imperative.irregular`, `es.imperative.negative`
• Audio: the 42 clips counted under unit 13 cover this unit too — the two units share one
  clip set, so nothing here adds a clip of its own.

### Unit 15: El Pluscuamperfecto *(shipped, `spanishB1Units` unit 34)*
• Focus: the subjunctive perfect (hubiera / hubiese + participle) in the three frames
  that need it — `ojalá` for a wish about a past that did not happen, `como si` for a
  comparison held to be untrue, and `si` for the unreal past condition, which answers
  with the conditional on the open side (si hubiera sabido, habría venido)
• It teaches the contrast the frame turns on: the same past is indicative where it
  really happened (no vinieron porque ya **habían** salido) and subjunctive where it did
  not (no vinieron porque ya **hubieran** salido)
• Lessons: *If I Had Known* / *As If It Were So* / *Two Had Clauses*
• Grammar slugs: `es.subjunctive.past_perfect`, `es.unreal_past`,
  `es.conditional.unreal_past`
• Held out for the B1 checkpoint: nothing — the B1 pool stays fixed at 8, drawn from
  units 30–31
• Audio: recorded — 19 clips in this unit, every one wired and referenced

### Unit 16: Reported Speech and por / para *(shipped, `spanishB1Units` unit 35)*
• Focus: the `dijo que` frame with and without backshift. The verb steps back one step
  (present → imperfect, future → conditional), and the unit says plainly that Spanish
  also accepts the unshifted form, so a learner is not told a correct sentence is wrong.
  A verb already in the past keeps its past form, and a reported question takes `si`
  rather than `que`
• It closes with `por` against `para` by relation, not by dictionary gloss: `por` for a
  cause, an exchange and a result, `para` for a purpose, a direction and a recipient
• Lessons: *What He Told Me* / *When the Tense Stays Put* / *por and para*
• Grammar slugs: `es.reported_speech.backshift`, `es.reported_speech.no_backshift`,
  `es.por_para`
• Held out for the B1 checkpoint: nothing
• Audio: recorded — 19 clips in this unit, every one wired and referenced

---

## Japanese (JLPT N4 Elementary Intermediate)

Target: Units 9–16. Shipped: 23 lessons and 156 challenges.

Status: **Units 9–16 have shipped.** Units 9–10 are in `B1CurriculumData.kt` (`japaneseN4Units`) — 4 lessons and 27 challenges. Units 11–12 are in `JapaneseN4CurriculumData.kt` (`japaneseN4ExtensionUnits`, unit ids 40–41, lessons 400–405, challenges `60000`-`60043` plus the `LISTEN` block `61000`-`61008`) — 6 lessons and 53 challenges, 6 of them held out. Units 13–14 follow in the same list (unit ids 42–43, lessons 406–412, challenges `62000`-`62040` plus the `LISTEN` block `61100`-`61106`, options from `6300001`) — 7 lessons and 43 challenges and nothing held out, so the N4 checkpoint still holds 10 held-out items, all in units 28–29 and 40–41. As with Spanish 11–12, the themes this roadmap originally sketched for units 11–12 (past experience 〜たことがある, plans 〜つもり, reasons 〜から, comparisons 〜より) were not what the grammar gap needed, and the shipped units teach different points: without a past tense there is no tense to conjugate, and every verb in units 1–10 was stuck in the present.

### Unit 9: Te-form & Requests (〜てください / 〜ています)
• Focus: Connecting verbs, ongoing actions, polite requests
• Dialogue / Story: *道案内* (Asking directions in Shibuya)
• Audio needed (jf_alpha):
  - `chotto_matte.ogg` ("ちょっと待ってください")
  - `ima_tabete_imasu.ogg` ("今、本を読んでいます")
  - `migi_ni_magatte.ogg` ("右に曲がってください")

### Unit 10: Potential & Ability (〜ことができる / 〜れる)
• Focus: Can/cannot do, languages, skills (日本語が話せます)
• Dialogue: *趣味と特技* (Hobbies and skills)
• Audio needed (jf_alpha):
  - `nihongo_ga_hanasemasu.ogg` ("少し日本語が話せます")
  - `kanji_o_kaku_koto_ga_dekimasu.ogg` ("漢字を書くことができます")

### Unit 11: Past Tense & Adjectives *(shipped, `japaneseN4ExtensionUnits` unit 40)*
• Focus: the polite past (ました) across godan and ichidan verbs, the plain past (断定形 た) against the
  polite one, the ない-form negative in both registers, and the い/な adjective class distinction
• Lessons: *Yesterday* / *Plain and Polite* / *How Was It?*
• Grammar slugs: `ja.past_polite`, `ja.plain_vs_polite`, `ja.negative`, `ja.i_adjective`,
  `ja.na_adjective`
• Held out for the N4 checkpoint: 帰りました, 飲まない, ビール
• Audio: recorded — 24 clips. SELECT speaks its correct option (and the correct option carries the
  same clip), CONJUGATE speaks the target form on the challenge only, FILL_BLANK speaks the whole
  scaffold with the blank filled. One exception: `atsui` speaks the bare word, not the sentence the
  held-out FILL_BLANK 60036 assembles, so it is wired to that item's correct option instead of the
  challenge.

### Unit 12: Ability, Opinion & Giving *(shipped, `japaneseN4ExtensionUnits` unit 41)*
• Focus: ability, opinion as 〜と思います (which drills the plain-form endings unit 11 taught), and the
  あげる / くれる / もらう trio with から and に
• Ability has two routes and lesson 403 teaches both: the potential on its own (乗れます, 作れます,
  食べられません, 歌えます) and the plain verb + ことができます. ことができます is 「〜こと が できます」
  and こと is the する→す nominaliser, so the verb keeps its **plain** form — 書く ことが できます,
  運転する ことが できます — and a ます form can never head it, because 泳ぎますこと is not a word.
  Unit 10 already drilled the こと route under `ja.potential_nominal`; lesson 23 puts both under
  `ja.ability_polite`, so every ability rule text there names the potential as a separate way of
  saying 'can'.
• Lessons: *I Can Do That* / *I Think* / *Gifts and Favours*
• Grammar slugs: `ja.ability_polite`, `ja.think`, `ja.giving_receiving`
• Held out for the N4 checkpoint: 歌えます, あつい, もらった
• Audio: recorded — all 22 clips wired, 18 on the challenge and 12 on the option that speaks the
  same text. Two of those wirings were corrected with the grammar they speak:
  - `kuruma_wo_unten_suru_koto_ga_dekimasu` speaks 車を運転することができます and the FILL_BLANK 60025
    scaffold reads 車を___ことができます, so the clip sits on the challenge: the item keys 運転する
    (plus うんてんする) and composes exactly the sentence the clip speaks. It was the one clip left
    unwired until the item was corrected — the old key took 運転 alone, which composes into
    車を運転ことができます: a noun in a verb slot.
  - `oyogu.ogg` speaks the bare 泳ぐ that CONJUGATE 60024 now keys. The item used to key 泳ぎます and
    carry `oyogimasu.ogg`, which cannot appear before ことができます at all; a CONJUGATE speaks the
    target form on the challenge alone, like every other one in these units, so the bare form is the
    clip. `oyogimasu.ogg` was recorded only for that wrong answer and has been removed.
  `CurriculumIntegrityTest` now fails any 「〜___ことができます。」 key that is not a verb, so the
  class that produced both corrections cannot ship again. The seven MATCH_PAIRS clips hang off their
  paired options: those challenges' prompts are the instruction "Match the ...", not a sentence, so
  there is nothing for a challenge-level clip to speak.

### Unit 13: The Passive (受身) *(shipped, `japaneseN4ExtensionUnits` unit 42)*
• Focus: the plain passive (読む → 読まれる, 書く → 書かれる, 見る → 見られる), the
  particle layer that goes with it (the を-object becomes the topic, the agent takes に —
  母に叱られました — and 雨で names a non-agent cause), and the progressive
  受け身 + ている (使われている, 汚染されています)
• The unit is explicit about what the passive cannot do: only a 他動詞, a verb that took a
  を-object, takes one, and 62010's and 62012's rule texts say so rather than implying every
  verb can. It is equally explicit that られる is three words — 書かれる is the passive,
  書ける the potential, and られる also means 得る ('can get') — and 62000, 62001, 62006 and
  62025 each pit two of them against each other rather than asking the learner to memorise
  an ending.
• Lessons: *Passive Forms* / *Who Did It* / *Right Now*
• Grammar slugs: `ja.passive_formation`, `ja.passive_particles`, `ja.passive_teiru`
• Held out for the N4 checkpoint: nothing
• Audio: recorded — 43 clips are shared with unit 14 and 34 hang off a challenge here, on
  the same rules as units 11–12. No `LISTEN` item is built on the られる/られる pair,
  because 書かれる and 書ける sit one mora apart and neither would be answerable by ear.

### Unit 14: The Causative (使役) and Relative Clauses (修飾節) *(shipped, `japaneseN4ExtensionUnits` unit 43)*
• Focus: the causative stem in せ (食べる → 食べさせる, 飲む → 飲ませる) with its two
  irregulars する → させる and 来る → 来させる, its progressive and polite forms
  (食べさせている, 走らせます) and the に that names who is made to act; then the relative
  clause — a plain-form clause bound to the noun it modifies with の, across the た-form
  (京都に行った友達), the ている-form (今閉まっている店), the potential (話せる人) and the
  ない-form (読めない人)
• The one-mora contrast with the passive is drilled head-on: させる adds せ where 受身 adds
  れ, and 62014 and 62029 are MATCH_PAIRS built on exactly that difference. The unit also
  rules out the mistake learners actually make — a は cannot head a relative clause, and
  の belongs to the clause while は belongs to the sentence.
• Lessons: *Making Someone Do* / *Being Made To Do It* / *The Noun With a Story* /
  *Who's In The Clause*
• Grammar slugs: `ja.causative_formation`, `ja.causative_teiru`, `ja.relative_clause`
• Held out for the N4 checkpoint: nothing
• Audio: recorded — 43 distinct clips across units 13–14, 34 on a challenge and 36 on the
  option that speaks the same text, and the two are not disjoint (27 clips are wired to
  both). Every `LISTEN` challenge carries a clip, none of its options does, and no
  `WRONG_*` distractor in either unit carries one.

### Unit 15: Conditionals & Proposals *(shipped, `japaneseN4ExtensionUnits` unit 44)*
• Focus: the three conditionals as three shapes rather than one idea — 〜たら on the
  plain past (降った → 降ったら), 〜なら on the 辞書形 (行くなら) with no tense of its
  own, and 〜ば on the e-row (買えば) and the い-adjective stem (忙しい → 忙しければ).
  〜ば is stated as the written and formal register and paired with ない / ありません,
  and the rule text says outright that in speech 忙しかったら is what people actually
  use: 〜と states a general outcome and never takes a 依頼, where 〜たら is free to
  carry one.
• Then the polite volitional as a form: the ます-stem plus ましょう (行きましょう), a
  proposal the other person is free to refuse, kept apart from the plain 意向形
  (行こう) and from 〜でしょう, which is the speaker's guess rather than a proposal
• Lessons: *If It Happens* / *If That's The Case* / *Let's*
• Grammar slugs: `ja.conditional_tara`, `ja.conditional_nara`, `ja.conditional_ba`,
  `ja.volition_polite`
• Held out for the N4 checkpoint: nothing — the N4 pool stays fixed at 10
• Audio: recorded — 20 clips in this unit, every one wired and referenced

### Unit 16: Keigo & Three られる *(shipped, `japaneseN4ExtensionUnits` unit 45)*
• Focus: 尊敬語 and 謙譲語 as a minimal introduction — 尊敬語 lifts the other
  person (召し上がる, なさる, いらっしゃる, おっしゃる, 申し上げる) and 謙譲語 lowers the
  speaker's own action (伺う, 拝見する, いたす). The unit says plainly that this is an
  introduction and not a system
• Then the three readings of られる — the potential, the plain 受身, and 得る ('can get',
  a small closed set: 得る, 求める, 採る) — with the rule text repeating what unit 13
  already said: られる by itself never says which reading it is, and what settles it is
  who stands in the subject slot
• Lessons: *Lifting the Other Person* / *Lowering Yourself* / *られる, Three Times Over*
• Grammar slugs: `ja.keigo_honorific`, `ja.keigo_humble`, `ja.rareru_readings`
• Held out for the N4 checkpoint: nothing
• Audio: recorded — 23 clips in this unit, every one wired and referenced

## Technical Prerequisites for Ingest
1. Python Kokoro pipeline, documented in `docs/kokoro-tts.md` at the repository root (ef_dora / jf_alpha @ 24kHz mono)
2. `UnitPayload` additions (already exists; Spanish units 9–10 are in `spanishA2Units`, Spanish units 11–16 in `spanishB1Units`, Japanese units 9–10 in `japaneseN4Units`, Japanese units 11–16 in `japaneseN4ExtensionUnits` in the separate `JapaneseN4CurriculumData.kt`)
3. Referential integrity tests in `CurriculumIntegrityTest.kt` ensure zero FK / audio regressions
