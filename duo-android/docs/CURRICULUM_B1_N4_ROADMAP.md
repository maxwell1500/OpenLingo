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

Target: Units 9–12. Shipped: 10 lessons and 66 challenges.

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

Target: Units 9–12 (8 lessons, ~28 challenges).

Status: Units 9–10 have shipped in `B1CurriculumData.kt` (`japaneseN4Units`) — 4 lessons and 27 challenges, at the JLPT N4 level this section names. Units 11–12 are not authored yet.

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

### Unit 11: Past Experience & Plans (〜たことがある / 〜つもり)
• Focus: "I have been to...", future intentions
• Dialogue: *京都旅行* (Trip to Kyoto)
• Audio needed (jf_alpha):
  - `kyouto_ni_itta_koto_ga_arimasu.ogg` ("京都に行ったことがあります")
  - `ashita_iku_tsumori_desu.ogg` ("明日行くつもりです")

### Unit 12: Reasons & Comparisons (〜から / 〜より〜のほうが)
• Focus: Explaining reasons, comparative statements (電車のほうが速いです)
• Dialogue: *レストラン選び* (Picking a restaurant)

---

## Technical Prerequisites for Ingest
1. Python Kokoro pipeline, documented in `docs/kokoro-tts.md` at the repository root (ef_dora / jf_alpha @ 24kHz mono)
2. `UnitPayload` additions in `B1CurriculumData.kt` (already exists; Spanish units 9–10 are in `spanishA2Units`, Spanish units 11–12 in `spanishB1Units`, Japanese units 9–10 in `japaneseN4Units`)
3. Referential integrity tests in `CurriculumIntegrityTest.kt` ensure zero FK / audio regressions
