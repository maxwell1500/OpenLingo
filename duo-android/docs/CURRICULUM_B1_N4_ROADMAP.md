# Curriculum Roadmap: Beyond A1 / N5

This document outlines the pedagogical scope, unit structure, and audio
production requirements for expanding Duo into intermediate levels.

**Status summary.** Units 9–10 of both courses have shipped. The Japanese units are
genuinely at the level this roadmap names (JLPT N4: te-form, polite requests and
potential forms). The Spanish units 9–10 are **not** at the level this roadmap names:
they teach the regular preterite and the imperfecto only — no irregular preterite
(`tuve`, `pude`, `hice`, `dije`, `estuve`, `quise`), no conditional, no subjunctive, no
imperative, no object or reflexive pronouns, no `por`/`para` — which is **CEFR A2**. The
app therefore labels the Spanish checkpoint that draws those units **A2**, not B1. The
actual B1 material is Units 11–12 and it is **not authored**. Nothing in this roadmap
has been deleted; the target is unchanged, the labels now describe what exists.

## Spanish (CEFR B1 Threshold)

Target: Units 9–12 (8 lessons, ~28 challenges).

Status: Units 9–10 have shipped in `B1CurriculumData.kt` (`spanishA2Units`) — 4 lessons and 31 challenges, and they are **A2-level content, not B1** (regular preterite and imperfecto only; see the summary above). Units 11–12 — the actual B1 material — are not authored yet.

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

### Unit 11: Travel & Navigation (Intermediate)
• Focus: Lost luggage, hotel complaints, train delays
• Dialogue: *En el aeropuerto*
• Audio needed (ef_dora):
  - `mi_maleta_se_perdio.ogg` ("Mi maleta no ha llegado")
  - `a_que_hora_sale_el_vuelo.ogg` ("¿A qué hora sale el próximo vuelo?")

### Unit 12: Future & Plans
• Focus: Simple future + `ir a` + infinitive (viajaré, voy a aprender)
• Dialogue: *Planes para el verano*

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
2. `UnitPayload` additions in `B1CurriculumData.kt` (already exists; Units 9–10 are in `spanishA2Units` / `japaneseN4Units`)
3. Referential integrity tests in `CurriculumIntegrityTest.kt` ensure zero FK / audio regressions
