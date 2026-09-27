# Curriculum Roadmap: Beyond A1 / N5

This document outlines the pedagogical scope, unit structure, and audio
production requirements for expanding Duo into intermediate levels.

**Status summary.** Units 9–12 of the Spanish course and 9–10 of the Japanese course
have shipped. The Japanese units are genuinely at the level this roadmap names
(JLPT N4: te-form, polite requests and potential forms). The Spanish course now
spans two labelled levels: **units 9–10 are CEFR A2** — the regular preterite and the
imperfecto only, no irregular stem anywhere — and **units 11–12 are the B1 material**:
the irregular and stem-changing preterite (`tuve`, `pude`, `hice`, `dije`, `estuve`,
`quise`, `vino`, `dormí`, `pidió`), the past perfect frame (`había salido`,
`había hecho`, `había dicho`), the regular and irregular conditional (`hablaría`,
`tendría`, `haría`, `podría`, `diría`, `vendría`, `saldría`), the polite periphrasis
(`me gustaría`, `querría`, `podría` + infinitive) and the connectives of purpose,
cause, result and concession (`para`, `porque`, `así que`, `entonces`, `aunque`,
`pero`). The A2 checkpoint draws units 18–19 and the **B1** checkpoint draws units
30–31. Still not authored: the subjunctive, the imperative, direct and indirect
object pronouns, and reflexive verbs — those remain the next B1 gap, and the honest
label for the Spanish course is now **A2 → B1**, not B1 throughout.

## Spanish (CEFR B1 Threshold)

Target: Units 9–12. Shipped: 10 lessons and 97 challenges.

Status: Units 9–10 have shipped in `B1CurriculumData.kt` (`spanishA2Units`, unit ids
18–19) — 4 lessons and 31 challenges, and they are **A2-level content, not B1**
(regular preterite and imperfecto only; see the summary above). Units 11–12 have
also shipped (`spanishB1Units`, unit ids 30–31) — 6 lessons and 66 challenges, and
they *are* the B1 material this roadmap asked for. Note that the unit themes the
roadmap originally sketched for 11–12 (travel complaints, future plans) were not
what the grammar gap needed: the shipped 11–12 teach the irregular preterite, the
past perfect, the conditional and the connective layer instead, and travel and
plans vocabulary rides along inside them.

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
• Audio: none — the shipped clips cover A1–A2 only, and no `audioSrc` was invented

### Unit 12: Conditional, Periphrasis & Connectives *(shipped, `spanishB1Units` unit 31)*
• Focus: the regular conditional (hablaría, comería, viviríamos) and the irregular one
  (tendría, haríamos→haría, podríamos, diría, vendríamos, saldría, estaría), the future
  against the conditional (tendré vs tendría), the polite periphrasis (me gustaría,
  querría, podría + infinitive) and the connectives (para, porque, así que, entonces,
  aunque, pero)
• Lessons: *What Would You Do?* / *I Would Like, Please* / *Why, So, Although*
• Held out for the B1 checkpoint: harías, comería, diría, pero
• Audio: none, for the same reason

---

## Japanese (JLPT N4 Elementary Intermediate)

Target: Units 9–12 (10 lessons, 71 challenges).

Status: **Units 9–12 have shipped.** Units 9–10 are in `B1CurriculumData.kt` (`japaneseN4Units`) — 4 lessons and 27 challenges. Units 11–12 are in `JapaneseN4CurriculumData.kt` (`japaneseN4ExtensionUnits`, unit ids 40–41, lessons 400–405, challenges 60000–60043) — 6 lessons and 44 challenges, 6 of them held out, so the N4 checkpoint now holds 10 held-out items. As with Spanish 11–12, the themes this roadmap originally sketched for units 11–12 (past experience 〜たことがある, plans 〜つもり, reasons 〜から, comparisons 〜より) were not what the grammar gap needed, and the shipped units teach different points: without a past tense there is no tense to conjugate, and every verb in units 1–10 was stuck in the present.

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
• Audio: none — the shipped clips cover A1–A2 only, and no `audioSrc` was invented

### Unit 12: Ability, Opinion & Giving *(shipped, `japaneseN4ExtensionUnits` unit 41)*
• Focus: ability as ます → せます and ます + ことができます, opinion as 〜と思います (which drills the
  plain-form endings unit 11 taught), and the あげる / くれる / もらう trio with から and に
• Lessons: *I Can Do That* / *I Think* / *Gifts and Favours*
• Grammar slugs: `ja.ability_polite`, `ja.think`, `ja.giving_receiving`
• Held out for the N4 checkpoint: 歌えます, あつい, もらった
• Audio: none, for the same reason

---

### Still absent at N4
The passive (受身) and the causative (使役) are not taught anywhere in the Japanese corpus, and neither
are relative clauses. They are real JLPT N4 grammar and remain open work — units 13 and 14 would be the
natural home. The audio for every unit from 9 onwards is also still unrecorded, so the `LISTEN` mechanic
is not available on any of them.

## Technical Prerequisites for Ingest
1. Python Kokoro pipeline, documented in `docs/kokoro-tts.md` at the repository root (ef_dora / jf_alpha @ 24kHz mono)
2. `UnitPayload` additions (already exists; Spanish units 9–10 are in `spanishA2Units`, Spanish units 11–12 in `spanishB1Units`, Japanese units 9–10 in `japaneseN4Units`, Japanese units 11–12 in `japaneseN4ExtensionUnits` in the separate `JapaneseN4CurriculumData.kt`)
3. Referential integrity tests in `CurriculumIntegrityTest.kt` ensure zero FK / audio regressions
