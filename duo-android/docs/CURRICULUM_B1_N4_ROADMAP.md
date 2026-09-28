# Curriculum Roadmap: Beyond A1 / N5

This document outlines the pedagogical scope, unit structure, and audio
production requirements for expanding Duo into intermediate levels.

**Status summary.** Units 9–14 of the Spanish course and 9–14 of the Japanese course
have shipped. The Japanese units are genuinely at the level this roadmap names
(JLPT N4: te-form, polite requests and potential forms, then the past, plain-vs-polite
register, the い/な adjective classes, ability, opinion and the giving/receiving trio,
then the passive, the causative and the relative clause). The Spanish course spans two
labelled levels: **units 9–10 are CEFR A2** — the regular preterite and the imperfecto
only, no irregular stem anywhere — and **units 11–14 are the B1 material**:
the irregular and stem-changing preterite (`tuve`, `pude`, `hice`, `dije`, `estuve`,
`quise`, `vino`, `dormí`, `pidió`), the past perfect frame (`había salido`,
`había hecho`, `había dicho`), the regular and irregular conditional (`hablaría`,
`tendría`, `haría`, `podría`, `diría`, `vendría`, `saldría`), the polite periphrasis
(`me gustaría`, `querría`, `podría` + infinitive) and the connectives of purpose,
cause, result and concession (`para`, `porque`, `así que`, `entonces`, `aunque`,
`pero`) in units 11–12; then the subjunctive (`quiero que vengas`, `no creo que sea`,
`para que`, `a menos que`), the imperative in all three shapes (affirmative `tú`,
affirmative `usted`, negative), the direct and indirect object pronouns, `gustar` and
the reflexive verbs including impersonal `se` in units 13–14. The A2 checkpoint draws
units 18–19 and the **B1** checkpoint draws units 30–33.

**Still not authored in Spanish.** The subjunctive *perfect* (`hubiera`/`hubiese` +
participle) appears nowhere in the corpus; only the indicative past perfect
(`había comido`) is taught. Reported speech is likewise absent as a system — no
`dijo que` frame, no tense or person backshift, and no `es.subjunctive`-focused
indirect-speech lesson. `por` versus `para` is never taught as a system either: the
corpus teaches `para` as a purpose connective in unit 12 and `para que` in unit 13,
while `por` appears only inside fixed phrases (`por favor`, `por la tarde`) and in no
contrastive item. The honest label for the Spanish course is **A2 → B1**; that label
describes what ships, and B1 in this corpus stops at the four grammar areas above.

## Spanish (CEFR B1 Threshold)

Target: Units 9–14. Shipped: 17 lessons and 154 challenges.

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

---

## Japanese (JLPT N4 Elementary Intermediate)

Target: Units 9–14. Shipped: 17 lessons and 123 challenges.

Status: **Units 9–14 have shipped.** Units 9–10 are in `B1CurriculumData.kt` (`japaneseN4Units`) — 4 lessons and 27 challenges. Units 11–12 are in `JapaneseN4CurriculumData.kt` (`japaneseN4ExtensionUnits`, unit ids 40–41, lessons 400–405, challenges `60000`-`60043` plus the `LISTEN` block `61000`-`61008`) — 6 lessons and 53 challenges, 6 of them held out. Units 13–14 follow in the same list (unit ids 42–43, lessons 406–412, challenges `62000`-`62040` plus the `LISTEN` block `61100`-`61106`, options from `6300001`) — 7 lessons and 43 challenges and nothing held out, so the N4 checkpoint still holds 10 held-out items, all in units 28–29 and 40–41. As with Spanish 11–12, the themes this roadmap originally sketched for units 11–12 (past experience 〜たことがある, plans 〜つもり, reasons 〜から, comparisons 〜より) were not what the grammar gap needed, and the shipped units teach different points: without a past tense there is no tense to conjugate, and every verb in units 1–10 was stuck in the present.

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

---

### Still absent at N4
Units 13–14 closed the three grammar areas this section used to name as open — the passive
(受身), the causative (使役) and the relative clause (修飾節) are all taught now. Four
real N4 points are still not taught anywhere in the corpus, and none of them is claimed
anywhere in the app:

- **Conditionals.** There is no conditional focus at all. 〜たら and 〜なら appear zero
  times in the corpus, and the single 〜れば form (乗らなければ) exists only as a
  `WRONG_FORM` distractor on an ability item, so a learner is never asked to build one.
- **Volition (意向形 / 〜ましょう).** Never taught as a form; 〜ましょう does not appear in
  any challenge or rule text.
- **Keigo.** Nothing above the basic ます register: no 尊敬語 and no 謙譲語, and no
  ascript such as お〜になる or いたす. The corpus is 丁寧 throughout, which it says it is.
- **The られる disambiguation as a system.** Units 13–14 make the collision explicit in
  four items (62000, 62001, 62006, 62025) and the `ErrorHint.FocusProfile` for
  `ja.passive_formation` says outright that られる by itself never says which of the passive
  or the potential it is — but that is four confrontations, not a rule with its own focus,
  and the third reading (得る, 'can get') is mentioned in a file header only.

The `LISTEN` mechanic is no longer a gap: the corpus files carry 58 `LISTEN` challenges
(66 counting the eight seeded with the A1 and N5 units), sitting in Japanese units 9–14
(1, 1, 5, 4, 3 and 4) and Spanish units 11–14 (6, 6, 3 and 4), not only in the A1 and A2
units.


## Technical Prerequisites for Ingest
1. Python Kokoro pipeline, documented in `docs/kokoro-tts.md` at the repository root (ef_dora / jf_alpha @ 24kHz mono)
2. `UnitPayload` additions (already exists; Spanish units 9–10 are in `spanishA2Units`, Spanish units 11–14 in `spanishB1Units`, Japanese units 9–10 in `japaneseN4Units`, Japanese units 11–14 in `japaneseN4ExtensionUnits` in the separate `JapaneseN4CurriculumData.kt`)
3. Referential integrity tests in `CurriculumIntegrityTest.kt` ensure zero FK / audio regressions
