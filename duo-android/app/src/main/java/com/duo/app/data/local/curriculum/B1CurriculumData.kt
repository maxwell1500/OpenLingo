package com.duo.app.data.local.curriculum

import com.duo.app.data.local.entities.ChallengeEntity
import com.duo.app.data.local.entities.ChallengeOptionEntity
import com.duo.app.data.local.entities.LessonEntity
import com.duo.app.data.local.entities.UnitEntity
import com.duo.app.data.local.models.ChallengeType

/**
 * The intermediate stage of the curriculum. The `B1` in this file's name is the
 * *roadmap workstream* — `docs/CURRICULUM_B1_N4_ROADMAP.md` — and not a claim about
 * the level of everything authored here. What has actually shipped is:
 *   Spanish: Units 9-10 (unit ids 18-19) — Pretérito Indefinido and Imperfecto,
 *     **regular only**, which is **CEFR A2**, so these units are labelled A2
 *     everywhere a learner can see them.
 *   Spanish: Units 11-12 (unit ids 30-31) — the irregular and stem-changing
 *     preterite, the past perfect frame, the regular and irregular conditional,
 *     the polite periphrasis and the connectives of purpose, cause, result and
 *     concession (`spanishB1Units`).
 *   Spanish: Units 13-14 (unit ids 32-33) — the subjunctive after querer and
 *     after emotion/doubt/negation and after para que / a menos que, the
 *     imperative in its affirmative tú, affirmative usted and negative shapes, the
 *     direct and indirect object pronouns, gustar, the reflexive pronouns and
 *     impersonal se. These four units are the B1 material the roadmap asked for,
 *     so the whole of 30-33 is what a B1 checkpoint draws on.
 *   Japanese: Units 9-10 (unit ids 28-29) — Te-form & Requests, Potential & Ability.
 *     These are genuinely JLPT N4 grammar points, so N4 is the honest label.
 *
 * All audio assets are bundled Kokoro-82M Ogg files. `spanishB1Units` carries
 * 110 distinct clips over its 123 challenges, covering SELECT, CONJUGATE,
 * FILL_BLANK, WORD_BANK, LISTEN and STORY alike. A SELECT challenge and its
 * correct option share the same clip; a CONJUGATE carries the target form on the
 * challenge alone, so the option grid never plays the answer for the learner. No
 * `WRONG_*` distractor anywhere in units 32-33 carries a clip: audio that speaks a
 * form the item calls an error teaches the wrong thing out loud.
 *
 * Id layout inside this file:
 *   - `1xxxx` / `2xxxx`  the originally authored taught items
 *   - `3xxxx`           the WI-03/04/05/07 grammar items authored with the rule cards
 *   - `31xxx`           the WI-08 held-out checkpoint pool: same structures, unseen sentences.
 *                       These are seeded with everything else and carry `heldOut = true`, so
 *                       the lesson-path queries skip them and only a checkpoint can reach them.
 *   - `30xxx` / `31xxx`  units 30-31 and lessons 300-305 (Spanish units 11-12)
 *   - `50xxx`           the challenges of units 30-31; `5014x` / `5024x` are their
 *                       held-out checkpoint pools
 *   - `5xxxxx`          the options of units 30-31, at 500000 + (challengeId - 50100) * 10
 *   - `60xxx`           units 32-33 and lessons 600-606 (Spanish units 13-14)
 *   - `70xxx`           the challenges of units 32-33, `70000`-`70066`. Nothing
 *                       here is held out: the B1 pool stays fixed at 8, drawn
 *                       from units 30-31, so a held-out item in unit 32 would be
 *                       reachable by nothing.
 *   - `70xxxx`          the options of units 32-33, from `700000` up
 */
object B1CurriculumData {

    // =========================================================================
    // SPANISH CEFR A2 (Units 9 - 10)
    // =========================================================================
    val spanishA2Units: List<UnitPayload> = listOf(
        // Unit 9: Past Tense — Pretérito Indefinido
        UnitPayload(
            unit = UnitEntity(
                id = 18,
                courseId = 1,
                title = "Unit 9: Past Tense — Pretérito",
                description = "Completed past actions: hablé, comí, viví, llegamos",
                orderIndex = 8,
            ),
            lessons = listOf(
                LessonEntity(id = 116, unitId = 18, title = "Lesson 16: I Did It", orderIndex = 0),
                LessonEntity(id = 117, unitId = 18, title = "Lesson 17: A Trip to Sevilla", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(
                    id = 1082, lessonId = 116, type = ChallengeType.SELECT,
                    question = "Which one means 'I spoke with him yesterday'?",
                    audioSrc = "asset:///audio/es/hable_con_el.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.preterito.regular",
                    ruleText = "The preterite marks one completed event: 1st person singular ends in -é, so hablé.\nhablaba is the imperfecto (an ongoing habit) and hablaré is the future. The ending decides which one you said.",
                ),
                ChallengeEntity(
                    id = 1083, lessonId = 116, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'We arrived on time'",
                    orderIndex = 1,
                    grammaticalFocus = "es.preterito.regular",
                    ruleText = "nosotros in the preterite takes -amos, so llegamos.\nThe present is also llegamos, and the imperfecto is llegábamos. llegué belongs to 'yo' and llegáis to vosotros — neither can finish 'we'.",
                ),
                ChallengeEntity(
                    id = 1084, lessonId = 116, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/llegamos_a_tiempo.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "es.preterito.regular",
                    ruleText = "nosotros in the preterite takes -amos: llegamos a tiempo.\nllegué is 'I arrived' and llegaba is the imperfecto 'we/they used to arrive'. Same three words, different ending.",
                ),

                ChallengeEntity(
                    id = 30001, lessonId = 116, type = ChallengeType.CONJUGATE,
                    question = "Which present form of hablar goes with 'yo'?",
                    orderIndex = 3,
                    grammaticalFocus = "es.present_person",
                    ruleText = "Present -ar verbs drop the -ar and add the person ending: -o, -as, -a, -amos, -an.\nAfter 'yo' only hablo fits — hablas is tú, hablan is ellos, and hablé is a different tense entirely.",
                ),
                ChallengeEntity(
                    id = 30002, lessonId = 116, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'Yo ___ español' — one subject, one tense, one ending",
                    orderIndex = 4,
                    grammaticalFocus = "es.present_person",
                    ruleText = "Present + yo → hablo.\nEvery wrong tile here is the right frame with the wrong morpheme: hablas and hablan break person agreement, comí and hablaré break the tense. No amount of vocabulary knowledge gets you through this one.",
                ),
                ChallengeEntity(
                    id = 30003, lessonId = 116, type = ChallengeType.FILL_BLANK,
                    question = "Cuando era niño, yo ___ en Madrid.",
                    orderIndex = 5,
                    grammaticalFocus = "es.imperfecto",
                    acceptedAnswers = "vivía|vivia",
                    ruleText = "The imperfecto sets the scene rather than counting events: yo vivía 'I used to live'.\n-ar/-er take -aba, -ir takes -ía. viví is the preterite (one completed move) and vivo is the present.",
                ),
                ChallengeEntity(
                    id = 30004, lessonId = 116, type = ChallengeType.CONJUGATE,
                    question = "Which preterite form of hablar goes with 'nosotros'?",
                    orderIndex = 6,
                    grammaticalFocus = "es.preterito.regular",
                    ruleText = "Regular preterite endings: -é, -aste, -ó, -amos, -asteis, -aron.\n-nosotros takes -amos, so hablamos. hablábamos is the imperfecto — a habit, not a completed event.",
                ),
                ChallengeEntity(
                    id = 30005, lessonId = 116, type = ChallengeType.FILL_BLANK,
                    question = "Ayer nosotros ___ a Sevilla.",
                    orderIndex = 7,
                    grammaticalFocus = "es.preterito.regular",
                    acceptedAnswers = "viajamos",
                    ruleText = "'Ayer' signals a completed event, so the preterite: nosotros + viajar → viajamos.\nviajábamos would be 'we used to travel' and viaja is 3rd person — both are the wrong shape for 'nosotros'.",
                ),

                ChallengeEntity(
                    id = 1085, lessonId = 117, type = ChallengeType.SELECT,
                    question = "Which one means 'I bought the train ticket'?",
                    audioSrc = "asset:///audio/es/compre_el_billete.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.preterito.regular",
                    ruleText = "1st person singular preterite: the infinitive loses -er/-ir and gains an accented -é — comprar → compré.\nCompramos is 'we bought' and compro is the present 'I buy'.",
                ),
                ChallengeEntity(
                    id = 1086, lessonId = 117, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'I spoke with him yesterday'",
                    orderIndex = 1,
                    grammaticalFocus = "es.preterito.regular",
                    ruleText = "1st person singular preterite of hablar is hablé.\nhablaba is the imperfecto ('I used to speak') and hablamos is the preterite of 'we'. Neither can go with 'yo' + 'ayer'.",
                ),
                ChallengeEntity(
                    id = 1087, lessonId = 117, type = ChallengeType.STORY,
                    question = "📖 Un Viaje en Tren\n\nAna: \"¡Hola Carlos! ¿A dónde vas?\"\nCarlos: \"Voy a Sevilla. Compré el billete ayer en la estación.\"\nAna: \"¡Qué bien! Yo viajé a Sevilla el año pasado. Llegamos a tiempo y la comida fue fantástica.\"\nCarlos: \"Espero tener suerte también.\"\n\n❓ What is Carlos planning to do?",
                    audioSrc = "asset:///audio/es/compre_el_billete.ogg",
                    orderIndex = 2,
                ),
                ChallengeEntity(
                    id = 30006, lessonId = 117, type = ChallengeType.CONJUGATE,
                    question = "Which preterite form of llegar goes with 'ellos'?",
                    orderIndex = 3,
                    grammaticalFocus = "es.preterito.regular",
                    ruleText = "The 3rd person plural preterite ends in -aron: llegaron.\nllegué is 'I arrived' (1st person singular) and llegaba is the imperfecto.",
                ),
                ChallengeEntity(
                    id = 30007, lessonId = 117, type = ChallengeType.FILL_BLANK,
                    question = "Mi amigo ___ el billete ayer.",
                    orderIndex = 4,
                    grammaticalFocus = "es.preterito.regular",
                    acceptedAnswers = "compró",
                    ruleText = "A single completed purchase in the past is preterite: él compró.\nThe 3rd person preterite of -ar/-er is an accented -ó; the present of the same verb is compra, with no accent.",
                ),
                ChallengeEntity(
                    id = 30008, lessonId = 117, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'Nosotros ___ a tiempo' — the preterite ending set",
                    orderIndex = 5,
                    grammaticalFocus = "es.preterito.regular",
                    ruleText = "nosotros + llegar in the preterite → llegamos.\nllegué belongs to 'yo', llegáis to vosotros, and llegaba is the imperfecto. The subject fixes the ending.",
                ),
                ChallengeEntity(
                    id = 31002, lessonId = 117, type = ChallengeType.CONJUGATE,
                    question = "Which preterite form of escribir goes with 'yo'?",
                    orderIndex = 6,
                    heldOut = true,
                    grammaticalFocus = "es.preterito.regular",
                    ruleText = "1st person singular preterite of an -ir verb: escribir → escribí (accented -í).\nescribes is the present and escribía the imperfecto; escribimos is the preterite of 'we'.",
                ),
                ChallengeEntity(
                    id = 31003, lessonId = 117, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'Ayer yo ___ una carta'",
                    orderIndex = 7,
                    heldOut = true,
                    grammaticalFocus = "es.preterito.regular",
                    ruleText = "1st person singular preterite of an -ir verb ends in -í: yo escribí.\nescribo is the present, escribíamos the imperfecto, and escribimos needs 'nosotros'.",
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 10309, challengeId = 1082, text = "Hablé con él ayer", correct = true, audioSrc = "asset:///audio/es/hable_con_el.ogg"),
                ChallengeOptionEntity(id = 10310, challengeId = 1082, text = "Hablaba con él ayer", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 10311, challengeId = 1082, text = "Hablé con ella ayer", correct = false),

                ChallengeOptionEntity(id = 10312, challengeId = 1083, text = "Llegamos", correct = true),
                ChallengeOptionEntity(id = 10313, challengeId = 1083, text = "a", correct = true),
                ChallengeOptionEntity(id = 10314, challengeId = 1083, text = "tiempo", correct = true),
                ChallengeOptionEntity(id = 10315, challengeId = 1083, text = "llegué", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 300006, challengeId = 1083, text = "llegaba", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 10316, challengeId = 1084, text = "Llegamos a tiempo", correct = true),
                ChallengeOptionEntity(id = 10317, challengeId = 1084, text = "Llegué a tiempo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 10318, challengeId = 1084, text = "Llegaba a tiempo", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 300100, challengeId = 30001, text = "hablo", correct = true),
                ChallengeOptionEntity(id = 300101, challengeId = 30001, text = "hablas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 300102, challengeId = 30001, text = "hablan", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 300103, challengeId = 30001, text = "hablé", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 300104, challengeId = 30002, text = "Yo", correct = true),
                ChallengeOptionEntity(id = 300105, challengeId = 30002, text = "hablo", correct = true),
                ChallengeOptionEntity(id = 300106, challengeId = 30002, text = "español", correct = true),
                ChallengeOptionEntity(id = 300107, challengeId = 30002, text = "hablas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 300108, challengeId = 30002, text = "hablan", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 300109, challengeId = 30002, text = "comí", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300110, challengeId = 30002, text = "hablaré", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 300111, challengeId = 30003, text = "vivía", correct = true),
                ChallengeOptionEntity(id = 300112, challengeId = 30003, text = "viví", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300113, challengeId = 30003, text = "vivo", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 300114, challengeId = 30004, text = "hablamos", correct = true),
                ChallengeOptionEntity(id = 300115, challengeId = 30004, text = "habláis", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 300116, challengeId = 30004, text = "hablaron", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 300117, challengeId = 30004, text = "hablábamos", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 300118, challengeId = 30005, text = "viajamos", correct = true),
                ChallengeOptionEntity(id = 300119, challengeId = 30005, text = "viajábamos", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300120, challengeId = 30005, text = "viaja", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 10319, challengeId = 1085, text = "Compré el billete de tren", correct = true, audioSrc = "asset:///audio/es/compre_el_billete.ogg"),
                ChallengeOptionEntity(id = 10320, challengeId = 1085, text = "Compramos el billete de tren", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 10321, challengeId = 1085, text = "Compro el billete de tren", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 10322, challengeId = 1086, text = "Hablé", correct = true),
                ChallengeOptionEntity(id = 10323, challengeId = 1086, text = "con", correct = true),
                ChallengeOptionEntity(id = 10324, challengeId = 1086, text = "él", correct = true),
                ChallengeOptionEntity(id = 10325, challengeId = 1086, text = "ayer", correct = true),
                ChallengeOptionEntity(id = 10326, challengeId = 1086, text = "hablaba", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300007, challengeId = 1086, text = "hablamos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 10327, challengeId = 1087, text = "Bought the train ticket", correct = true),
                ChallengeOptionEntity(id = 10328, challengeId = 1087, text = "Arrived on time", correct = false),
                ChallengeOptionEntity(id = 10329, challengeId = 1087, text = "Spoke with him", correct = false),

                ChallengeOptionEntity(id = 300121, challengeId = 30006, text = "llegaron", correct = true),
                ChallengeOptionEntity(id = 300122, challengeId = 30006, text = "llegué", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 300123, challengeId = 30006, text = "llegaba", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300124, challengeId = 30006, text = "llega", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 300125, challengeId = 30007, text = "compró", correct = true),
                ChallengeOptionEntity(id = 300126, challengeId = 30007, text = "compra", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300127, challengeId = 30007, text = "compraba", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 300128, challengeId = 30008, text = "Nosotros", correct = true),
                ChallengeOptionEntity(id = 300129, challengeId = 30008, text = "llegamos", correct = true),
                ChallengeOptionEntity(id = 300130, challengeId = 30008, text = "a", correct = true),
                ChallengeOptionEntity(id = 300131, challengeId = 30008, text = "tiempo", correct = true),
                ChallengeOptionEntity(id = 300132, challengeId = 30008, text = "llegué", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 300133, challengeId = 30008, text = "llegáis", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 300134, challengeId = 30008, text = "llegaba", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 310000, challengeId = 31002, text = "escribí", correct = true),
                ChallengeOptionEntity(id = 310001, challengeId = 31002, text = "escribes", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 310002, challengeId = 31002, text = "escribía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 310003, challengeId = 31002, text = "escribimos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 310004, challengeId = 31003, text = "Ayer", correct = true),
                ChallengeOptionEntity(id = 310005, challengeId = 31003, text = "yo", correct = true),
                ChallengeOptionEntity(id = 310006, challengeId = 31003, text = "escribí", correct = true),
                ChallengeOptionEntity(id = 310007, challengeId = 31003, text = "una", correct = true),
                ChallengeOptionEntity(id = 310008, challengeId = 31003, text = "carta", correct = true),
                ChallengeOptionEntity(id = 310009, challengeId = 31003, text = "escribía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 310010, challengeId = 31003, text = "escribimos", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 310011, challengeId = 31003, text = "escribo", correct = false, errorTag = "WRONG_TENSE"),
            ),
        ),

        // Unit 10: Past Tense — Imperfecto
        UnitPayload(
            unit = UnitEntity(
                id = 19,
                courseId = 1,
                title = "Unit 10: Past Tense — Imperfecto",
                description = "Descriptions, habits, and states in the past: era, tenía, vivía",
                orderIndex = 9,
            ),
            lessons = listOf(
                LessonEntity(id = 118, unitId = 19, title = "Lesson 18: When I Was a Child", orderIndex = 0),
                LessonEntity(id = 119, unitId = 19, title = "Lesson 19: Past Habits", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(
                    id = 1088, lessonId = 118, type = ChallengeType.SELECT,
                    question = "Which one means 'When I was a child I lived in Madrid'?",
                    audioSrc = "asset:///audio/es/cuando_era_nino.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.imperfecto",
                    ruleText = "era and vivía are both imperfecto: they describe how things were over a stretch of time.\nfue and viví are the preterite, which pins the sentence to a single completed event instead.",
                ),
                ChallengeEntity(
                    id = 1089, lessonId = 118, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'When I was a child I lived in Madrid'",
                    orderIndex = 1,
                    grammaticalFocus = "es.imperfecto",
                    ruleText = "Cuando era niño vivía en Madrid: era (he was) and vivía (I lived) are both imperfecto, so the whole scene stays open.\nvivió is the preterite — one move, not a childhood — and viven cannot agree with 'yo'.",
                ),
                ChallengeEntity(
                    id = 1090, lessonId = 118, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/cuando_era_nino.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "es.imperfecto",
                    ruleText = "era, vivía, hacía — the -ía/-ía/-ía forms are the imperfecto.\nfue and viví are the preterite. The audio differs from the near-misses by one ending, so listen for it.",
                ),

                ChallengeEntity(
                    id = 30009, lessonId = 118, type = ChallengeType.CONJUGATE,
                    question = "Which imperfect form of vivir goes with 'yo'?",
                    orderIndex = 3,
                    grammaticalFocus = "es.imperfecto",
                    ruleText = "-ir verbs take -ía / -ías / -ía / -íamos / -ían in the imperfecto.\nAfter 'yo' that is vivía. viví is the preterite and vivimos/viven are the nosotros/ellos forms.",
                ),
                ChallengeEntity(
                    id = 30010, lessonId = 118, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'Cuando era niño yo ___ en Madrid' — the imperfect ending set",
                    orderIndex = 4,
                    grammaticalFocus = "es.imperfecto",
                    ruleText = "A childhood that lasted needs the imperfecto: yo vivía.\nviví is the preterite (one completed move), viviré the future, and viven cannot agree with 'yo'.",
                ),
                ChallengeEntity(
                    id = 30011, lessonId = 118, type = ChallengeType.FILL_BLANK,
                    question = "De niño, todos los días ___ buen tiempo.",
                    orderIndex = 5,
                    grammaticalFocus = "es.imperfecto",
                    acceptedAnswers = "hacía|hacia",
                    ruleText = "'Todos los días' describes a repetition, so the imperfecto: hacía.\nHizo is the preterite and hará the future — both describe a single point, not every day of a season.",
                ),

                ChallengeEntity(
                    id = 1091, lessonId = 119, type = ChallengeType.SELECT,
                    question = "Which one means 'The weather was nice every day'?",
                    audioSrc = "asset:///audio/es/hacia_buen_tiempo.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.imperfecto",
                    ruleText = "'Todos los días' describes a repetition, so the imperfecto: hacía buen tiempo.\nHizo is one finished afternoon and hará is a forecast, not a description of the whole summer.",
                ),
                ChallengeEntity(
                    id = 1092, lessonId = 119, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'The weather was nice every day'",
                    orderIndex = 1,
                    grammaticalFocus = "es.imperfecto",
                    ruleText = "hacer in the imperfecto is regular -er: yo hacía, tú hacías, él hacía, nosotros hacíamos, ellos hacían.\nHizo is the preterite and hará the future. Only hacía describes the weather day after day.",
                ),
                ChallengeEntity(
                    id = 1093, lessonId = 119, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/hacia_buen_tiempo.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "es.imperfecto",
                    ruleText = "The imperfecto of hacer is hacía; Hizo and hará are the preterite and the future.\nAll three near-misses differ from the correct sentence by that one verb form — the rest of the words are identical.",
                ),
                ChallengeEntity(
                    id = 1094, lessonId = 119, type = ChallengeType.STORY,
                    question = "📖 Recuerdos de Infancia\n\nAbuela: \"Cuando era niña, vivía en Madrid con mis padres y hermanos.\"\nNieto: \"¿Y hacía buen tiempo?\"\nAbuela: \"Hacía buen tiempo todos los días en verano. Jugábamos en el parque hasta la noche.\"\nNieto: \"¡Sonaba maravilloso!\"\n\n❓ Where did the grandmother live as a child?",
                    audioSrc = "asset:///audio/es/cuando_era_nino.ogg",
                    orderIndex = 3,
                ),
                ChallengeEntity(
                    id = 30012, lessonId = 119, type = ChallengeType.CONJUGATE,
                    question = "Which imperfect form of hacer goes with 'nosotros'?",
                    orderIndex = 4,
                    grammaticalFocus = "es.imperfecto",
                    ruleText = "-er verbs take -ía / -ías / -ía / -íamos / -ían in the imperfecto.\nAfter 'nosotros' that is hacíamos. hacían is ellos/ellas, and hicimos/hacemos belong to a different tense.",
                ),
                ChallengeEntity(
                    id = 30013, lessonId = 119, type = ChallengeType.FILL_BLANK,
                    question = "Los domingos ___ en el parque con mis amigos.",
                    orderIndex = 5,
                    grammaticalFocus = "es.imperfecto",
                    acceptedAnswers = "jugábamos|jugabamos",
                    ruleText = "A repeated Sunday habit is imperfecto: nosotros jugábamos.\nJugamos is the preterite ('they played once') and jugaba is the él/ella form — neither fits 'los domingos' with 'nosotros'.",
                ),
                ChallengeEntity(
                    id = 30014, lessonId = 119, type = ChallengeType.CONJUGATE,
                    question = "Which preterite form of hablar fits 'Ellos ___ con ella ayer'?",
                    orderIndex = 6,
                    grammaticalFocus = "es.preterito.regular",
                    ruleText = "3rd person plural preterite ends in -aron: hablaron.\nhablaban is the imperfecto and hablan the present — both describe an ongoing situation, not a finished conversation with 'ayer'.",
                ),
                ChallengeEntity(
                    id = 31000, lessonId = 119, type = ChallengeType.CONJUGATE,
                    question = "Which imperfect form of trabajar goes with 'nosotros'?",
                    orderIndex = 7,
                    heldOut = true,
                    grammaticalFocus = "es.imperfecto",
                    ruleText = "-ar verbs take -aba / -abas / -aba / -ábamos / -aban in the imperfecto.\nAfter 'nosotros' that is trabajábamos. trabajaban is ellos/ellas, and trabajamos/trabajaron belong to a different tense.",
                ),
                ChallengeEntity(
                    id = 31001, lessonId = 119, type = ChallengeType.FILL_BLANK,
                    question = "Cuando era pequeño, ___ en Barcelona.",
                    orderIndex = 8,
                    heldOut = true,
                    grammaticalFocus = "es.imperfecto",
                    acceptedAnswers = "vivía|vivia",
                    ruleText = "A childhood that lasted is imperfecto: yo vivía.\nviví is the preterite (one completed move) and vivo is the present.",
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 10330, challengeId = 1088, text = "Cuando era niño vivía en Madrid", correct = true, audioSrc = "asset:///audio/es/cuando_era_nino.ogg"),
                ChallengeOptionEntity(id = 10331, challengeId = 1088, text = "Cuando fue niño vivía en Madrid", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 10332, challengeId = 1088, text = "Cuando era niña vivía en Madrid", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 10333, challengeId = 1089, text = "Cuando", correct = true),
                ChallengeOptionEntity(id = 10334, challengeId = 1089, text = "era", correct = true),
                ChallengeOptionEntity(id = 10335, challengeId = 1089, text = "niño", correct = true),
                ChallengeOptionEntity(id = 10336, challengeId = 1089, text = "vivía", correct = true),
                ChallengeOptionEntity(id = 10337, challengeId = 1089, text = "en", correct = true),
                ChallengeOptionEntity(id = 10338, challengeId = 1089, text = "Madrid", correct = true),
                ChallengeOptionEntity(id = 10339, challengeId = 1089, text = "vivió", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300008, challengeId = 1089, text = "viven", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 10340, challengeId = 1090, text = "Cuando era niño vivía en Madrid", correct = true),
                ChallengeOptionEntity(id = 10341, challengeId = 1090, text = "Cuando fue niño vivía en Madrid", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 10342, challengeId = 1090, text = "Cuando era niño viví en Madrid", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 300135, challengeId = 30009, text = "vivía", correct = true),
                ChallengeOptionEntity(id = 300136, challengeId = 30009, text = "viví", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300137, challengeId = 30009, text = "vivimos", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 300138, challengeId = 30009, text = "viven", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 300139, challengeId = 30010, text = "Cuando", correct = true),
                ChallengeOptionEntity(id = 300140, challengeId = 30010, text = "era", correct = true),
                ChallengeOptionEntity(id = 300141, challengeId = 30010, text = "niño", correct = true),
                ChallengeOptionEntity(id = 300142, challengeId = 30010, text = "yo", correct = true),
                ChallengeOptionEntity(id = 300143, challengeId = 30010, text = "vivía", correct = true),
                ChallengeOptionEntity(id = 300144, challengeId = 30010, text = "en", correct = true),
                ChallengeOptionEntity(id = 300145, challengeId = 30010, text = "Madrid", correct = true),
                ChallengeOptionEntity(id = 300146, challengeId = 30010, text = "viví", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300147, challengeId = 30010, text = "viven", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 300148, challengeId = 30010, text = "viviré", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 300149, challengeId = 30011, text = "hacía", correct = true),
                ChallengeOptionEntity(id = 300150, challengeId = 30011, text = "hizo", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300151, challengeId = 30011, text = "hará", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 10343, challengeId = 1091, text = "Hacía buen tiempo todos los días", correct = true, audioSrc = "asset:///audio/es/hacia_buen_tiempo.ogg"),
                ChallengeOptionEntity(id = 10344, challengeId = 1091, text = "Hizo buen tiempo todos los días", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 10345, challengeId = 1091, text = "Hará buen tiempo todos los días", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 10346, challengeId = 1092, text = "Hacía", correct = true),
                ChallengeOptionEntity(id = 10347, challengeId = 1092, text = "buen", correct = true),
                ChallengeOptionEntity(id = 10348, challengeId = 1092, text = "tiempo", correct = true),
                ChallengeOptionEntity(id = 10349, challengeId = 1092, text = "todos", correct = true),
                ChallengeOptionEntity(id = 10350, challengeId = 1092, text = "los", correct = true),
                ChallengeOptionEntity(id = 10351, challengeId = 1092, text = "días", correct = true),
                ChallengeOptionEntity(id = 10352, challengeId = 1092, text = "Hizo", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300009, challengeId = 1092, text = "Hará", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 10353, challengeId = 1093, text = "Hacía buen tiempo todos los días", correct = true),
                ChallengeOptionEntity(id = 10354, challengeId = 1093, text = "Hizo buen tiempo todos los días", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 10355, challengeId = 1093, text = "Hará buen tiempo todos los días", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 10356, challengeId = 1094, text = "In Madrid with her family", correct = true),
                ChallengeOptionEntity(id = 10357, challengeId = 1094, text = "In Sevilla alone", correct = false),
                ChallengeOptionEntity(id = 10358, challengeId = 1094, text = "In a train station", correct = false),

                ChallengeOptionEntity(id = 300152, challengeId = 30012, text = "hacíamos", correct = true),
                ChallengeOptionEntity(id = 300153, challengeId = 30012, text = "hacían", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 300154, challengeId = 30012, text = "hicimos", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300155, challengeId = 30012, text = "hacen", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 300156, challengeId = 30013, text = "jugábamos", correct = true),
                ChallengeOptionEntity(id = 300157, challengeId = 30013, text = "jugamos", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300158, challengeId = 30013, text = "jugaba", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 300159, challengeId = 30014, text = "hablaron", correct = true),
                ChallengeOptionEntity(id = 300160, challengeId = 30014, text = "hablaban", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300161, challengeId = 30014, text = "hablan", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300162, challengeId = 30014, text = "hablé", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 310012, challengeId = 31000, text = "trabajábamos", correct = true),
                ChallengeOptionEntity(id = 310013, challengeId = 31000, text = "trabajaban", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 310014, challengeId = 31000, text = "trabajamos", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 310015, challengeId = 31000, text = "trabajaron", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 310016, challengeId = 31001, text = "vivía", correct = true),
                ChallengeOptionEntity(id = 310017, challengeId = 31001, text = "viví", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 310018, challengeId = 31001, text = "vivo", correct = false, errorTag = "WRONG_TENSE"),
            ),
        ),
    )
    // =========================================================================
    // SPANISH CEFR B1 (Units 11 - 12)
    // =========================================================================
    // This block is what units 9-10 above were not. The preterite taught there is
    // regular-only, which leaves the most frequent verbs in the language — tener,
    // poder, hacer, decir, estar, querer, venir, poner — with no preterite at all,
    // and leaves the conditional, the polite periphrasis and the connective layer
    // untaught. Units 11-12 close that: an irregular preterite (unit 11) plus the
    // past perfect that lets a learner say what came *before*, and the conditional
    // with its periphrasis and its connectives of purpose, cause, result and
    // concession (unit 12).
    //
    // Id layout for this block:
    //   - units 30-31, lessons 300-305
    //   - challenges 501xx (unit 11) / 502xx (unit 12); the trailing 5014x / 5024x
    //     are the held-out checkpoint pool for each unit
    //   - options 500000 + (challengeId - 50100) * 10, so every option is unique
    //     and still traceable to the challenge that owns it.
    //
    // Audio: 56 bundled Kokoro clips, one per challenge, and every one of them was
    // checked against the text it hangs off before it was wired. A SELECT challenge
    // speaks its correct option, so the correct option carries the same clip; a
    // CONJUGATE speaks the target form on the challenge only, because an option that
    // played itself would give the answer away. FILL_BLANK and WORD_BANK speak the
    // full target sentence (the blank filled, the tiles assembled) and STORY speaks
    // the story body with its paragraph breaks rendered as full stops. Every value
    // has a file behind it, which is what CurriculumIntegrityTest asserts.
    val spanishB1Units: List<UnitPayload> = listOf(
        // Unit 11: Irregular Past — Pretérito Indefinido irregular + Pasado Perfecto
        UnitPayload(
            unit = UnitEntity(
                id = 30,
                courseId = 1,
                title = "Unit 11: Irregular Past — Pretérito",
                description = "tuve, pude, hice, vine — and what had happened before",
                orderIndex = 10,
            ),
            lessons = listOf(
                LessonEntity(id = 300, unitId = 30, title = "Lesson 20: I Had It", orderIndex = 0),
                LessonEntity(id = 301, unitId = 30, title = "Lesson 21: How the Day Went", orderIndex = 1),
                LessonEntity(id = 302, unitId = 30, title = "Lesson 22: Before All That", orderIndex = 2),
            ),
            challenges = listOf(
                ChallengeEntity(
                    id = 50100, lessonId = 300, type = ChallengeType.SELECT,
                    question = "Which one means 'I had a terrible day'?",
                    audioSrc = "asset:///audio/es/tuve_un_dia_malo.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.preterito.irregular",
                    ruleText = "tener changes stem in the preterite: tuv- + e/iste/o/imos/ieron, so yo tuve.\nTener un día malo leaves the infinitive in the slot and tuviste is tú — the sentence says nothing about anyone but me.",
                ),
                ChallengeEntity(
                    id = 50101, lessonId = 300, type = ChallengeType.CONJUGATE,
                    question = "Which preterite form of tener goes with 'yo'?",
                    audioSrc = "asset:///audio/es/tuve.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "es.preterito.irregular",
                    ruleText = "The irregular preterite of tener uses the stem tuv- in every person: tuve, tuviste, tuvo, tuvimos, tuvieron.\ntuviste belongs to tú and tenías is the imperfecto — neither can stand for one finished past action.",
                ),
                ChallengeEntity(
                    id = 50102, lessonId = 300, type = ChallengeType.FILL_BLANK,
                    question = "Ayer yo ___ mucha hambre.",
                    audioSrc = "asset:///audio/es/ayer_yo_tuve_mucha_hambre.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "es.preterito.irregular",
                    acceptedAnswers = "tuve",
                    ruleText = "One completed stretch of hunger is the preterite, and the irregular stem of tener is tuv-: yo tuve.\nTenía is the imperfecto, a hunger that lasted, and Tener is the infinitive, which can never fill the slot.",
                ),
                ChallengeEntity(
                    id = 50103, lessonId = 300, type = ChallengeType.CONJUGATE,
                    question = "Which preterite form of poder goes with 'yo'?",
                    audioSrc = "asset:///audio/es/pude.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "es.preterito.irregular",
                    ruleText = "poder is irregular in the preterite: pud- + e/iste/o/imos/ieron, so yo pude and él pudo.\nPodía is the imperfecto, an ability that lasted all evening, and Poder is the infinitive.",
                ),
                ChallengeEntity(
                    id = 50104, lessonId = 300, type = ChallengeType.FILL_BLANK,
                    question = "No ___ contestar el teléfono.",
                    audioSrc = "asset:///audio/es/no_pude_contestar_el_telefono.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "es.preterito.irregular",
                    acceptedAnswers = "pude",
                    ruleText = "One finished attempt is the preterite: no pude.\nPodía is the imperfecto and Puedo is the present — both say the ability is still there instead of reporting that it was used up.",
                ),
                ChallengeEntity(
                    id = 50105, lessonId = 300, type = ChallengeType.CONJUGATE,
                    question = "Which preterite form of hacer goes with 'yo'?",
                    audioSrc = "asset:///audio/es/hice.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "es.preterito.irregular",
                    ruleText = "hacer is irregular in the preterite: hic- + e/iste/o/imos/ieron, so yo hice and él hizo.\nHacía is the imperfecto, a habit, and Hacer is the infinitive; neither can report the one cake that was actually made.",
                ),

                ChallengeEntity(
                    id = 50300, lessonId = 300, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/ayer_hice_un_bizcocho_para_la_reunion.ogg",
                    orderIndex = 6,
                    grammaticalFocus = "es.preterito.irregular",
                    ruleText = "hacer is irregular in the preterite: hic- + e/iste/o/imos/ieron, so the one bake that was finished is the preterite.\n'Ayer hacía un bizcocho para la reunión' is the imperfecto, a baking that went on all day; 'Ayer hizo un bizcocho para la reunión' is él/ella, not the speaker; 'Ayer hacer un bizcocho para la reunión' leaves the infinitive in the slot.",
                ),
                ChallengeEntity(
                    id = 50106, lessonId = 300, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'I made the cake yesterday'",
                    orderIndex = 7,
                    grammaticalFocus = "es.preterito.irregular",
                    ruleText = "The first person singular of the irregular hacer is hice, so the sentence is Ayer yo hice un bizcocho.\nHacía is the imperfecto and Hizo is él/ella, so neither can follow yo.",
                ),
                ChallengeEntity(
                    id = 50107, lessonId = 300, type = ChallengeType.CONJUGATE,
                    question = "Which preterite form of decir goes with 'nosotros'?",
                    audioSrc = "asset:///audio/es/dijimos.ogg",
                    orderIndex = 8,
                    grammaticalFocus = "es.preterito.irregular",
                    ruleText = "decir is irregular in the preterite: dij- + o/imos/ieron, so nosotros dijimos and ellos dijeron.\nDecíamos is the imperfecto and Decir is the infinitive; neither is the one time the thing was said.",
                ),
                ChallengeEntity(
                    id = 50108, lessonId = 300, type = ChallengeType.SELECT,
                    question = "Which one means 'the meeting'?",
                    audioSrc = "asset:///audio/es/la_reunion.ogg",
                    orderIndex = 9,
                    ruleText = "A noun taught on its own, with no grammar to miss: la reunión is the meeting you are late to.\nLa habitación, la farmacia and la iglesia are other places, and only one of the four is a gathering of people.",
                ),
                ChallengeEntity(
                    id = 50109, lessonId = 300, type = ChallengeType.SELECT,
                    question = "Which one means 'I was very tired'?",
                    audioSrc = "asset:///audio/es/estuve_muy_cansado.ogg",
                    orderIndex = 10,
                    grammaticalFocus = "es.preterito.irregular",
                    ruleText = "A completed state of tiredness is the preterite: estuve muy cansado.\nEstaba is the imperfecto, a tiredness that ran on, and Estaré is the future; Estar is the infinitive and fills no slot.",
                ),

                ChallengeEntity(
                    id = 50301, lessonId = 300, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/estuve_en_casa_todo_el_dia.ogg",
                    orderIndex = 11,
                    grammaticalFocus = "es.preterito.irregular",
                    ruleText = "estar is irregular in the preterite: estuv- + e/iste/o/imos/ieron, so a whole day that ended is the preterite.\n'Estaba en casa todo el día' is the imperfecto, a day with no end yet; 'Estuviste en casa todo el día' is tú, not me; 'Estar en casa todo el día' leaves the infinitive in the slot.",
                ),

                ChallengeEntity(
                    id = 50110, lessonId = 301, type = ChallengeType.SELECT,
                    question = "Which one means 'I did not want to go'?",
                    audioSrc = "asset:///audio/es/no_quise_ir.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.preterito.irregular",
                    ruleText = "querer is irregular in the preterite: quis- + e/iste/o/imos/ieron, so no quise.\nQuería is the imperfecto, an inclination that lasted, and Quisiste is tú, not me.",
                ),
                ChallengeEntity(
                    id = 50111, lessonId = 301, type = ChallengeType.CONJUGATE,
                    question = "Which preterite form of venir goes with 'ellos'?",
                    audioSrc = "asset:///audio/es/vinieron.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "es.preterito.irregular",
                    ruleText = "venir is irregular in the preterite: vin- + ieron, so ellos vinieron and ella vino.\nVenían is the imperfecto of a repeated coming and Venir is the infinitive; neither describes one arrival.",
                ),
                ChallengeEntity(
                    id = 50112, lessonId = 301, type = ChallengeType.FILL_BLANK,
                    question = "Ana ___ a Madrid en tren.",
                    audioSrc = "asset:///audio/es/ana_vino_a_madrid_en_tren.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "es.preterito.irregular",
                    acceptedAnswers = "vino",
                    ruleText = "One arrival by someone else: Ana + venir → vino.\nVenía is the imperfecto and Viene is the present, so both leave the trip still under way instead of finished.",
                ),
                ChallengeEntity(
                    id = 50113, lessonId = 301, type = ChallengeType.CONJUGATE,
                    question = "Which preterite form of estar goes with 'yo'?",
                    audioSrc = "asset:///audio/es/estuve.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "es.preterito.irregular",
                    ruleText = "estar is irregular in the preterite: estuv- + e/iste/o/imos/ieron, so yo estuve and él estuvo.\nEstaba is the imperfecto, a stay that had no end yet, and Estar is the infinitive.",
                ),
                ChallengeEntity(
                    id = 50114, lessonId = 301, type = ChallengeType.CONJUGATE,
                    question = "Which preterite form of poner goes with 'nosotros'?",
                    audioSrc = "asset:///audio/es/pusimos.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "es.preterito.irregular",
                    ruleText = "poner is irregular in the preterite: pus- + imos/eron, so nosotros pusimos and ellos pusieron.\nPoníamos is the imperfecto and Poner is the infinitive; neither is the one time the table was set.",
                ),
                ChallengeEntity(
                    id = 50115, lessonId = 301, type = ChallengeType.CONJUGATE,
                    question = "Which preterite form of dormir goes with 'yo'?",
                    audioSrc = "asset:///audio/es/dormi.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "es.preterito.stem_changing",
                    ruleText = "dormir changes stem in the present (dorm- → duerm-) but the preterite keeps the plain stem: dormí, dormiste, durmió.\nDurmió is él/ella, Dormía is the imperfecto, and Duermo belongs to the present tense.",
                ),
                ChallengeEntity(
                    id = 50116, lessonId = 301, type = ChallengeType.FILL_BLANK,
                    question = "Anoche ___ ocho horas.",
                    audioSrc = "asset:///audio/es/anoche_dormi_ocho_horas.ogg",
                    orderIndex = 6,
                    grammaticalFocus = "es.preterito.stem_changing",
                    acceptedAnswers = "dormí|dormi",
                    ruleText = "One night of sleep is the preterite, and the preterite stem of dormir is dorm-: yo dormí.\nDormía is the imperfecto, how you slept every night, and Duermo is the present — neither is the single finished night.",
                ),

                ChallengeEntity(
                    id = 50302, lessonId = 301, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/anoche_dormimos_ocho_horas.ogg",
                    orderIndex = 7,
                    grammaticalFocus = "es.preterito.stem_changing",
                    ruleText = "The preterite of a stem-changing -ir verb keeps the plain stem: nosotros dormimos.\n'Anoche durmió ocho horas' is él/ella, not nosotros; 'Anoche dormíamos ocho horas' is the imperfecto, a sleep that repeated every night; 'Anoche dormir ocho horas' leaves the infinitive in the slot.",
                ),
                ChallengeEntity(
                    id = 50117, lessonId = 301, type = ChallengeType.CONJUGATE,
                    question = "Which preterite form of pedir goes with 'él'?",
                    audioSrc = "asset:///audio/es/pidio.ogg",
                    orderIndex = 8,
                    grammaticalFocus = "es.preterito.stem_changing",
                    ruleText = "pedir changes stem in the present (pid- → pide) but the preterite is ped-: pedí, pediste, pidió.\nPedí is yo and Pedía is the imperfecto, so only Pidió can answer for him.",
                ),
                ChallengeEntity(
                    id = 50118, lessonId = 301, type = ChallengeType.CONJUGATE,
                    question = "Which preterite form of descubrir goes with 'nosotros'?",
                    audioSrc = "asset:///audio/es/descubrimos.ogg",
                    orderIndex = 9,
                    grammaticalFocus = "es.preterito.stem_changing",
                    ruleText = "descubrir is a weak stem-changing -ir verb: descubrí, descubriste, descubrió, descubrimos.\nDescubrieron is ellos/ellas and Descubría is the imperfecto; Descubre is the present.",
                ),
                ChallengeEntity(
                    id = 50119, lessonId = 301, type = ChallengeType.SELECT,
                    question = "Which one means 'the station'?",
                    audioSrc = "asset:///audio/es/la_estacion.ogg",
                    orderIndex = 10,
                    ruleText = "la estación is the station; it is a feminine noun, like la reunión and la habitación.\nLa piscina, la torre and el mercado are other places, and only one of them is where a train stops.",
                ),
                ChallengeEntity(
                    id = 50140, lessonId = 301, type = ChallengeType.FILL_BLANK,
                    question = "El profesor ___ toda la mañana.",
                    audioSrc = "asset:///audio/es/el_profesor_siguio_toda_la_manana.ogg",
                    orderIndex = 11,
                    heldOut = true,
                    grammaticalFocus = "es.preterito.stem_changing",
                    acceptedAnswers = "siguió|siguio",
                    ruleText = "seguir is a strong stem-changing -ir verb: the present is sigo, but the preterite keeps the soft g and takes ió — the one completed stretch of talking is Siguió.\nSeguía is the imperfecto, Sigue is the present, and Seguir is the infinitive; none of the three is a finished action.",
                ),
                ChallengeEntity(
                    id = 50141, lessonId = 301, type = ChallengeType.FILL_BLANK,
                    question = "El tren ___ a la estación a las tres.",
                    audioSrc = "asset:///audio/es/el_tren_volvio_a_la_estacion_a_las_tres.ogg",
                    orderIndex = 12,
                    heldOut = true,
                    grammaticalFocus = "es.preterito.irregular",
                    acceptedAnswers = "volvió|volvio",
                    ruleText = "volver is irregular in the preterite: volv- + ió, so the one completed return the sentence asks for is Volvió.\nVolvía is the imperfecto of a train that came back every day, Vuelve is the present, and Volver is the infinitive.",
                ),

                ChallengeEntity(
                    id = 50303, lessonId = 301, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/los_ninos_pidieron_permiso_para_salir.ogg",
                    orderIndex = 13,
                    grammaticalFocus = "es.preterito.stem_changing",
                    ruleText = "pedir changes stem in the present (pid- → pide) but the preterite is ped-, and ellos take -ieron: pidieron.\n'Los niños piden permiso para salir' is the present, an ask that is still going on; 'Los niños pidió permiso para salir' is singular against a plural subject; 'Los niños pedir permiso para salir' leaves the infinitive in the slot.",
                ),

                ChallengeEntity(
                    id = 50120, lessonId = 302, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'When I arrived the film had already started'",
                    audioSrc = "asset:///audio/es/cuando_llegue_la_pelicula_ya_habia_empezado.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.past_perfect",
                    ruleText = "The past perfect is había + participle, and empezar builds its participle as empez- + ado: ya había empezado.\nEmpezó is the plain preterite, a start with no earlier point to measure it from, and Empezaba is the imperfecto.",
                ),
                ChallengeEntity(
                    id = 50121, lessonId = 302, type = ChallengeType.FILL_BLANK,
                    question = "___ comido antes de las dos.",
                    audioSrc = "asset:///audio/es/habia_comido_antes_de_las_dos.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "es.past_perfect",
                    acceptedAnswers = "había|habia",
                    ruleText = "The past perfect reports what was already true before another past event, so the auxiliary is había and the participle comido follows it.\nHe is the present perfect, a result that still stands now, and Haber is the infinitive — neither can sit in front of a participle in a past-perfect frame.",
                ),
                ChallengeEntity(
                    id = 50122, lessonId = 302, type = ChallengeType.FILL_BLANK,
                    question = "Cuando volvimos, ya ___ de casa.",
                    orderIndex = 2,
                    grammaticalFocus = "es.past_perfect",
                    acceptedAnswers = "salido",
                    ruleText = "The participle of salir is salido, and it takes haber in the past perfect: ya había salido.\nSalió is the plain preterite, one exit, and Salía is the imperfecto; neither can follow ya había.",
                ),
                ChallengeEntity(
                    id = 50123, lessonId = 302, type = ChallengeType.CONJUGATE,
                    question = "Which participle does escribir use after había?",
                    orderIndex = 3,
                    grammaticalFocus = "es.past_perfect",
                    ruleText = "escribir builds its participle on the short stem escrib- + ido, so había escrito.\nEscribió is the preterite of the whole verb and Escribiendo is the -ndo form, which never follows había.",
                ),
                ChallengeEntity(
                    id = 50124, lessonId = 302, type = ChallengeType.CONJUGATE,
                    question = "Which participle does hacer use after había?",
                    orderIndex = 4,
                    grammaticalFocus = "es.past_perfect",
                    ruleText = "hacer has an irregular participle, hecho: había hecho, never había hacido.\nHicimos is the preterite of the whole verb, and Haciendo is the -ndo form — the slot after había takes a participle and nothing else.",
                ),
                ChallengeEntity(
                    id = 50125, lessonId = 302, type = ChallengeType.FILL_BLANK,
                    question = "No ___ dicho la verdad.",
                    orderIndex = 5,
                    grammaticalFocus = "es.past_perfect",
                    acceptedAnswers = "dicho",
                    ruleText = "decir has an irregular participle, dicho, so no había dicho.\nDijo is the plain preterite, one telling, and Diciendo is the -ndo form, which cannot follow había.",
                ),

                ChallengeEntity(
                    id = 50304, lessonId = 302, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/el_tren_ya_habia_salido_cuando_llegue.ogg",
                    orderIndex = 6,
                    grammaticalFocus = "es.past_perfect",
                    ruleText = "The past perfect is había + participle, and the participle of salir is salido: ya había salido.\n'El tren ya salió cuando llegué' is the preterite of the whole verb, with no earlier point measured from; 'El tren ya había salir cuando llegué' puts the infinitive in the slot where the participle belongs; 'El tren ya había salido cuando llegó' is a different person, not the speaker.",
                ),
                ChallengeEntity(
                    id = 50126, lessonId = 302, type = ChallengeType.STORY,
                    question = "Día perdido en la oficina\n\nLlegué tarde a la reunión.\nMi jefe ya había salido.\nDespués supe que yo había dicho la verdad demasiado tarde.",
                    audioSrc = "asset:///audio/es/dia_perdido_en_la_oficina.ogg",
                    orderIndex = 7,
                ),
                ChallengeEntity(
                    id = 50127, lessonId = 302, type = ChallengeType.CONJUGATE,
                    question = "Which participle does poner use after había?",
                    orderIndex = 8,
                    grammaticalFocus = "es.past_perfect",
                    ruleText = "poner has an irregular participle, puesto: había puesto la mesa.\nPusimos is the preterite of the whole verb, Ponido is the participle regular verbs would take, and Poniendo is the -ndo form.",
                ),
                ChallengeEntity(
                    id = 50128, lessonId = 302, type = ChallengeType.CONJUGATE,
                    question = "Which participle does ir use after había?",
                    orderIndex = 9,
                    grammaticalFocus = "es.past_perfect",
                    ruleText = "ir has an irregular participle, ido: había ido.\nFui is the preterite of the whole verb and Yendo is the -ndo form; the slot after había never takes a tensed or an -ndo verb.",
                ),
                ChallengeEntity(
                    id = 50129, lessonId = 302, type = ChallengeType.FILL_BLANK,
                    question = "Cuando me llamaron yo ___ en el tren.",
                    orderIndex = 10,
                    grammaticalFocus = "es.past_perfect",
                    acceptedAnswers = "dormido",
                    ruleText = "dormir's participle is dormido, and it needs haber in the past perfect: yo había dormido.\nDurmió is a different person, Durmiendo is the -ndo form, and Dormir is the infinitive — none of the three can follow yo había.",
                ),
                ChallengeEntity(
                    id = 50142, lessonId = 302, type = ChallengeType.FILL_BLANK,
                    question = "Cuando llegué, mi vecino ya lo había ___.",
                    audioSrc = "asset:///audio/es/cuando_llegue_mi_vecino_ya_lo_habia_comprado.ogg",
                    orderIndex = 11,
                    heldOut = true,
                    grammaticalFocus = "es.past_perfect",
                    acceptedAnswers = "comprado",
                    ruleText = "The past perfect is había + participle, and the participle of comprar is comprado: ya lo había comprado.\ncompraba is the imperfecto of a habit that repeated, comprando is the -ndo form, and comprar is the infinitive — none of them can follow había.",
                ),
                ChallengeEntity(
                    id = 50143, lessonId = 302, type = ChallengeType.CONJUGATE,
                    question = "Which preterite form of abrir goes with 'yo'?",
                    audioSrc = "asset:///audio/es/abri.ogg",
                    orderIndex = 12,
                    heldOut = true,
                    grammaticalFocus = "es.preterito.irregular",
                    ruleText = "abrir is one of the -ir verbs that stay regular in the preterite: abr- + í, so the window opened in one finished act is abrió.\nabría is the imperfecto of a window left open all afternoon, and abrir is the infinitive, which fills no slot.",
                ),

                ChallengeEntity(
                    id = 50305, lessonId = 302, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/ya_habiamos_terminado_el_trabajo.ogg",
                    orderIndex = 13,
                    grammaticalFocus = "es.past_perfect",
                    ruleText = "The past perfect is había + participle, and nosotros take hab- + íamos: habíamos terminado.\n'Ya terminamos el trabajo' is the preterite of the whole verb, one finish with nothing before it; 'Ya haber terminado el trabajo' puts the infinitive where the participle belongs; 'Ya habías terminado el trabajo' is tú, not nosotros.",
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 500000, challengeId = 50100, text = "Tuve un día malo", correct = true, audioSrc = "asset:///audio/es/tuve_un_dia_malo.ogg"),
                ChallengeOptionEntity(id = 500001, challengeId = 50100, text = "Tengo un día malo", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500002, challengeId = 50100, text = "Tener un día malo", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 500003, challengeId = 50100, text = "Tuviste un día malo", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 500010, challengeId = 50101, text = "tuve", correct = true),
                ChallengeOptionEntity(id = 500011, challengeId = 50101, text = "tuviste", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 500012, challengeId = 50101, text = "tenía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500013, challengeId = 50101, text = "tener", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500020, challengeId = 50102, text = "Tuve", correct = true),
                ChallengeOptionEntity(id = 500021, challengeId = 50102, text = "Tenía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500022, challengeId = 50102, text = "Tener", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 500023, challengeId = 50102, text = "Tuviste", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 500030, challengeId = 50103, text = "pude", correct = true),
                ChallengeOptionEntity(id = 500031, challengeId = 50103, text = "pudo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 500032, challengeId = 50103, text = "podía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500033, challengeId = 50103, text = "poder", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500040, challengeId = 50104, text = "Pude", correct = true),
                ChallengeOptionEntity(id = 500041, challengeId = 50104, text = "Podía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500042, challengeId = 50104, text = "Puedo", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500043, challengeId = 50104, text = "Poder", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500050, challengeId = 50105, text = "hice", correct = true),
                ChallengeOptionEntity(id = 500051, challengeId = 50105, text = "hizo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 500052, challengeId = 50105, text = "hacía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500053, challengeId = 50105, text = "hacer", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500060, challengeId = 50106, text = "Ayer", correct = true),
                ChallengeOptionEntity(id = 500061, challengeId = 50106, text = "yo", correct = true),
                ChallengeOptionEntity(id = 500062, challengeId = 50106, text = "hice", correct = true),
                ChallengeOptionEntity(id = 500064, challengeId = 50106, text = "bizcocho", correct = true),
                ChallengeOptionEntity(id = 500065, challengeId = 50106, text = "hacía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500066, challengeId = 50106, text = "hizo", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 500070, challengeId = 50107, text = "dijimos", correct = true),
                ChallengeOptionEntity(id = 500071, challengeId = 50107, text = "dijeron", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 500072, challengeId = 50107, text = "decíamos", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500073, challengeId = 50107, text = "decir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500080, challengeId = 50108, text = "La reunión", correct = true, audioSrc = "asset:///audio/es/la_reunion.ogg"),
                ChallengeOptionEntity(id = 500081, challengeId = 50108, text = "La habitación", correct = false),
                ChallengeOptionEntity(id = 500082, challengeId = 50108, text = "La farmacia", correct = false),
                ChallengeOptionEntity(id = 500083, challengeId = 50108, text = "La iglesia", correct = false),

                ChallengeOptionEntity(id = 500090, challengeId = 50109, text = "Estuve muy cansado", correct = true, audioSrc = "asset:///audio/es/estuve_muy_cansado.ogg"),
                ChallengeOptionEntity(id = 500091, challengeId = 50109, text = "Estaba muy cansado", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500092, challengeId = 50109, text = "Estaré muy cansado", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500093, challengeId = 50109, text = "Estar muy cansado", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500100, challengeId = 50110, text = "No quise ir", correct = true, audioSrc = "asset:///audio/es/no_quise_ir.ogg"),
                ChallengeOptionEntity(id = 500101, challengeId = 50110, text = "No quería ir", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500102, challengeId = 50110, text = "No quisiste ir", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 500103, challengeId = 50110, text = "No querer ir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500110, challengeId = 50111, text = "vinieron", correct = true),
                ChallengeOptionEntity(id = 500111, challengeId = 50111, text = "vino", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 500112, challengeId = 50111, text = "venían", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500113, challengeId = 50111, text = "venir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500120, challengeId = 50112, text = "Vino", correct = true),
                ChallengeOptionEntity(id = 500121, challengeId = 50112, text = "Venía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500122, challengeId = 50112, text = "Viene", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500123, challengeId = 50112, text = "Venir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500130, challengeId = 50113, text = "estuve", correct = true),
                ChallengeOptionEntity(id = 500131, challengeId = 50113, text = "estuvo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 500132, challengeId = 50113, text = "estaba", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500133, challengeId = 50113, text = "estar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500140, challengeId = 50114, text = "pusimos", correct = true),
                ChallengeOptionEntity(id = 500141, challengeId = 50114, text = "pusieron", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 500142, challengeId = 50114, text = "poníamos", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500143, challengeId = 50114, text = "poner", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500150, challengeId = 50115, text = "dormí", correct = true),
                ChallengeOptionEntity(id = 500151, challengeId = 50115, text = "durmió", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 500152, challengeId = 50115, text = "dormía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500153, challengeId = 50115, text = "duermo", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 500160, challengeId = 50116, text = "Dormí", correct = true),
                ChallengeOptionEntity(id = 500161, challengeId = 50116, text = "Dormía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500162, challengeId = 50116, text = "Duermo", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500163, challengeId = 50116, text = "Dormir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500170, challengeId = 50117, text = "pidió", correct = true),
                ChallengeOptionEntity(id = 500171, challengeId = 50117, text = "pedí", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 500172, challengeId = 50117, text = "pedía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500173, challengeId = 50117, text = "pido", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 500180, challengeId = 50118, text = "descubrimos", correct = true),
                ChallengeOptionEntity(id = 500181, challengeId = 50118, text = "descubrieron", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 500182, challengeId = 50118, text = "descubría", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500183, challengeId = 50118, text = "descubro", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 500190, challengeId = 50119, text = "La estación", correct = true, audioSrc = "asset:///audio/es/la_estacion.ogg"),
                ChallengeOptionEntity(id = 500191, challengeId = 50119, text = "La piscina", correct = false),
                ChallengeOptionEntity(id = 500192, challengeId = 50119, text = "La torre", correct = false),
                ChallengeOptionEntity(id = 500193, challengeId = 50119, text = "El mercado", correct = false),

                ChallengeOptionEntity(id = 500400, challengeId = 50140, text = "Siguió", correct = true),
                ChallengeOptionEntity(id = 500401, challengeId = 50140, text = "Seguía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500402, challengeId = 50140, text = "Sigue", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500403, challengeId = 50140, text = "Seguir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500410, challengeId = 50141, text = "Volvió", correct = true),
                ChallengeOptionEntity(id = 500411, challengeId = 50141, text = "Volvía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500412, challengeId = 50141, text = "Vuelve", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500413, challengeId = 50141, text = "Volver", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500200, challengeId = 50120, text = "Cuando", correct = true),
                ChallengeOptionEntity(id = 500201, challengeId = 50120, text = "llegué", correct = true),
                ChallengeOptionEntity(id = 500202, challengeId = 50120, text = "la", correct = true),
                ChallengeOptionEntity(id = 500203, challengeId = 50120, text = "película", correct = true),
                ChallengeOptionEntity(id = 500204, challengeId = 50120, text = "ya", correct = true),
                ChallengeOptionEntity(id = 500205, challengeId = 50120, text = "había", correct = true),
                ChallengeOptionEntity(id = 500206, challengeId = 50120, text = "empezado", correct = true),
                ChallengeOptionEntity(id = 500207, challengeId = 50120, text = "empezó", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500208, challengeId = 50120, text = "empezaba", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 500210, challengeId = 50121, text = "Había", correct = true),
                ChallengeOptionEntity(id = 500211, challengeId = 50121, text = "He", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500212, challengeId = 50121, text = "Haber", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500220, challengeId = 50122, text = "Salido", correct = true),
                ChallengeOptionEntity(id = 500221, challengeId = 50122, text = "Salió", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500222, challengeId = 50122, text = "Salía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500223, challengeId = 50122, text = "Salir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500230, challengeId = 50123, text = "escrito", correct = true),
                ChallengeOptionEntity(id = 500231, challengeId = 50123, text = "escribió", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500232, challengeId = 50123, text = "escribiendo", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 500233, challengeId = 50123, text = "escribir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500240, challengeId = 50124, text = "hecho", correct = true),
                ChallengeOptionEntity(id = 500241, challengeId = 50124, text = "hicimos", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500242, challengeId = 50124, text = "hacido", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 500243, challengeId = 50124, text = "haciendo", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500250, challengeId = 50125, text = "dicho", correct = true),
                ChallengeOptionEntity(id = 500251, challengeId = 50125, text = "dijo", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500252, challengeId = 50125, text = "diciendo", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 500253, challengeId = 50125, text = "decir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500260, challengeId = 50126, text = "In a lost day", correct = true),
                ChallengeOptionEntity(id = 500261, challengeId = 50126, text = "At a long meeting", correct = false),
                ChallengeOptionEntity(id = 500262, challengeId = 50126, text = "In a train station", correct = false),
                ChallengeOptionEntity(id = 500263, challengeId = 50126, text = "At the beach", correct = false),

                ChallengeOptionEntity(id = 500270, challengeId = 50127, text = "puesto", correct = true),
                ChallengeOptionEntity(id = 500271, challengeId = 50127, text = "pusimos", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500272, challengeId = 50127, text = "ponido", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 500273, challengeId = 50127, text = "poniendo", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500280, challengeId = 50128, text = "ido", correct = true),
                ChallengeOptionEntity(id = 500281, challengeId = 50128, text = "fui", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500282, challengeId = 50128, text = "yendo", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 500283, challengeId = 50128, text = "ir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500290, challengeId = 50129, text = "Dormido", correct = true),
                ChallengeOptionEntity(id = 500291, challengeId = 50129, text = "Durmió", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500292, challengeId = 50129, text = "Durmiendo", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 500293, challengeId = 50129, text = "Dormir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500420, challengeId = 50142, text = "comprado", correct = true),
                ChallengeOptionEntity(id = 500421, challengeId = 50142, text = "compraba", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500422, challengeId = 50142, text = "comprando", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 500423, challengeId = 50142, text = "comprar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 500430, challengeId = 50143, text = "abrí", correct = true),
                ChallengeOptionEntity(id = 500431, challengeId = 50143, text = "abrió", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 500432, challengeId = 50143, text = "abría", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 500433, challengeId = 50143, text = "abrir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 503000, challengeId = 50300, text = "Ayer hice un bizcocho para la reunión", correct = true),
                ChallengeOptionEntity(id = 503001, challengeId = 50300, text = "Ayer hacía un bizcocho para la reunión", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 503002, challengeId = 50300, text = "Ayer hizo un bizcocho para la reunión", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 503003, challengeId = 50300, text = "Ayer hacer un bizcocho para la reunión", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 503010, challengeId = 50301, text = "Estuve en casa todo el día", correct = true),
                ChallengeOptionEntity(id = 503011, challengeId = 50301, text = "Estaba en casa todo el día", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 503012, challengeId = 50301, text = "Estuviste en casa todo el día", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 503013, challengeId = 50301, text = "Estar en casa todo el día", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 503020, challengeId = 50302, text = "Anoche dormimos ocho horas", correct = true),
                ChallengeOptionEntity(id = 503021, challengeId = 50302, text = "Anoche durmió ocho horas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 503022, challengeId = 50302, text = "Anoche dormíamos ocho horas", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 503023, challengeId = 50302, text = "Anoche dormir ocho horas", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 503030, challengeId = 50303, text = "Los niños pidieron permiso para salir", correct = true),
                ChallengeOptionEntity(id = 503031, challengeId = 50303, text = "Los niños piden permiso para salir", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 503032, challengeId = 50303, text = "Los niños pidió permiso para salir", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 503033, challengeId = 50303, text = "Los niños pedir permiso para salir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 503040, challengeId = 50304, text = "El tren ya había salido cuando llegué", correct = true),
                ChallengeOptionEntity(id = 503041, challengeId = 50304, text = "El tren ya salió cuando llegué", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 503042, challengeId = 50304, text = "El tren ya había salir cuando llegué", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 503043, challengeId = 50304, text = "El tren ya había salido cuando llegó", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 503050, challengeId = 50305, text = "Ya habíamos terminado el trabajo", correct = true),
                ChallengeOptionEntity(id = 503051, challengeId = 50305, text = "Ya terminamos el trabajo", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 503052, challengeId = 50305, text = "Ya haber terminado el trabajo", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 503053, challengeId = 50305, text = "Ya habías terminado el trabajo", correct = false, errorTag = "WRONG_PERSON"),
            ),
        ),

        // Unit 12: Condicional, Perífrasis y Conectores
        UnitPayload(
            unit = UnitEntity(
                id = 31,
                courseId = 1,
                title = "Unit 12: Conditional & Connectives",
                description = "hablaría, me gustaría — para, porque, así que, aunque",
                orderIndex = 11,
            ),
            lessons = listOf(
                LessonEntity(id = 303, unitId = 31, title = "Lesson 23: What Would You Do?", orderIndex = 0),
                LessonEntity(id = 304, unitId = 31, title = "Lesson 24: I Would Like, Please", orderIndex = 1),
                LessonEntity(id = 305, unitId = 31, title = "Lesson 25: Why, So, Although", orderIndex = 2),
            ),
            challenges = listOf(
                ChallengeEntity(
                    id = 50200, lessonId = 303, type = ChallengeType.CONJUGATE,
                    question = "Which conditional form of hablar goes with 'yo'?",
                    audioSrc = "asset:///audio/es/hablaria.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.conditional.regular",
                    ruleText = "The regular conditional keeps the whole infinitive: hablar + ía/ías/ía/íamos/ían, so yo hablaría.\nHablarías is tú and Hablaré is the future, which promises the speech instead of supposing it.",
                ),
                ChallengeEntity(
                    id = 50201, lessonId = 303, type = ChallengeType.FILL_BLANK,
                    question = "En tu lugar, yo ___ con el jefe.",
                    audioSrc = "asset:///audio/es/en_tu_lugar_yo_hablaria_con_el_jefe.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "es.conditional.regular",
                    acceptedAnswers = "hablaría|hablaria",
                    ruleText = "A supposition about someone else's action is the conditional: yo hablaría.\nHablaba is the imperfecto, a habit in the past, and Hablaré is the future, which commits to the conversation instead of supposing it.",
                ),
                ChallengeEntity(
                    id = 50202, lessonId = 303, type = ChallengeType.CONJUGATE,
                    question = "Which conditional form of vivir goes with 'nosotros'?",
                    audioSrc = "asset:///audio/es/viviriamos.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "es.conditional.regular",
                    ruleText = "The regular conditional keeps the infinitive whole: vivir + iríamos, so nosotros viviríamos.\nViviríais is vosotros and Viviremos is the future, a plan rather than a supposition; Vivíamos is the imperfecto.",
                ),
                ChallengeEntity(
                    id = 50203, lessonId = 303, type = ChallengeType.CONJUGATE,
                    question = "Which conditional form of tener goes with 'yo'?",
                    audioSrc = "asset:///audio/es/tendria.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "es.conditional.irregular",
                    ruleText = "The irregular conditional is built on the infinitive stem plus -ría, so tener → tendría.\nTendrías is tú and Tendré is the future; Tenía is the imperfecto and belongs to a different tense entirely.",
                ),
                ChallengeEntity(
                    id = 50204, lessonId = 303, type = ChallengeType.CONJUGATE,
                    question = "Which conditional form of poder goes with 'nosotros'?",
                    audioSrc = "asset:///audio/es/podriamos.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "es.conditional.irregular",
                    ruleText = "poder drops the -d- before -ría: poder → podría, so nosotros podríamos.\nPodríamos answers only for nosotros; Podré is the future and Podemos is the present.",
                ),
                ChallengeEntity(
                    id = 50205, lessonId = 303, type = ChallengeType.CONJUGATE,
                    question = "Which conditional form of decir goes with 'ella'?",
                    audioSrc = "asset:///audio/es/diria.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "es.conditional.irregular",
                    ruleText = "decir inserts a -d- in front of -ría: decir → diría, so ella diría.\nDirían is ellos/ellas and Dirá is the future; the present would be Dice, which states a fact rather than supposing one.",
                ),
                ChallengeEntity(
                    id = 50206, lessonId = 303, type = ChallengeType.CONJUGATE,
                    question = "Which conditional form of salir goes with 'yo'?",
                    audioSrc = "asset:///audio/es/saldria.ogg",
                    orderIndex = 6,
                    grammaticalFocus = "es.conditional.irregular",
                    ruleText = "salir → saldría, the -ir- of the infinitive before -ría.\nSaldrías is tú and Saldré is the future; Salía is the imperfecto of a leaving that repeated.",
                ),
                ChallengeEntity(
                    id = 50207, lessonId = 303, type = ChallengeType.FILL_BLANK,
                    question = "El médico cree que yo ___ bien mañana.",
                    audioSrc = "asset:///audio/es/el_medico_cree_que_yo_estaria_bien_manana.ogg",
                    orderIndex = 7,
                    grammaticalFocus = "es.conditional.irregular",
                    acceptedAnswers = "estaría|estaria",
                    ruleText = "Cree que plus a supposition is the conditional, and estar → estaría.\nEstará is the future, which states the recovery as a fact, and Estaba is the imperfecto, which puts the claim in a past scene.",
                ),

                ChallengeEntity(
                    id = 50306, lessonId = 303, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/yo_compraria_una_casa_mas_grande.ogg",
                    orderIndex = 8,
                    grammaticalFocus = "es.conditional.regular",
                    ruleText = "A regular -ar verb takes -ía / -ías / -ía / -íamos / -ían in the conditional, so yo compraría.\n'Yo compraré una casa más grande' is the future, a promise about the house; 'Vosotros compraríais una casa más grande' is not the speaker; 'Yo comprar una casa más grande' leaves the infinitive in the slot.",
                ),
                ChallengeEntity(
                    id = 50208, lessonId = 303, type = ChallengeType.CONJUGATE,
                    question = "Which conditional form of venir goes with 'nosotros'?",
                    audioSrc = "asset:///audio/es/vendriamos.ogg",
                    orderIndex = 9,
                    grammaticalFocus = "es.conditional.irregular",
                    ruleText = "venir → vendría, the -n- of the infinitive before -ría.\nVendremos is the future and Venían is the imperfecto of a repeated coming; neither supposes anything.",
                ),
                ChallengeEntity(
                    id = 50209, lessonId = 303, type = ChallengeType.SELECT,
                    question = "Which one means 'the appointment'?",
                    audioSrc = "asset:///audio/es/la_cita.ogg",
                    orderIndex = 10,
                    ruleText = "la cita is the appointment you arrange to meet; la factura, la receta and la entrada are other papers or places.\nIt is a feminine noun, and only one of the four names a time you have agreed to keep.",
                ),
                ChallengeEntity(
                    id = 50240, lessonId = 303, type = ChallengeType.FILL_BLANK,
                    question = "¿___ tú con el proyecto?",
                    audioSrc = "asset:///audio/es/harias_tu_con_el_proyecto.ogg",
                    orderIndex = 11,
                    heldOut = true,
                    grammaticalFocus = "es.conditional.irregular",
                    acceptedAnswers = "harías|harías",
                    ruleText = "The conditional offers an action without committing to it, and second person is the one being asked, so the answer is Harías; Haría would be yo.\nHarás and Haré are the future, which promise the work, and Hacer is the infinitive, which cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 50307, lessonId = 303, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/yo_diria_que_si_pero_no_se.ogg",
                    orderIndex = 12,
                    grammaticalFocus = "es.conditional.irregular",
                    ruleText = "decir → diría, the -d- before -ría, and the conditional offers a view without committing to it.\n'Yo diré que sí, pero no sé' is the future, which commits; 'Yo decía que sí, pero no sé' is the imperfecto, which puts the remark in a past scene; 'Yo decir que sí, pero no sé' leaves the infinitive in the slot.",
                ),

                ChallengeEntity(
                    id = 50210, lessonId = 304, type = ChallengeType.FILL_BLANK,
                    question = "Me ___ un café, por favor.",
                    audioSrc = "asset:///audio/es/me_gustaria_un_cafe_por_favor.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.conditional.periphrasis",
                    acceptedAnswers = "gustaría|gustaria",
                    ruleText = "The polite request is me gustaría + infinitive, a softened 'I would like'.\nGusta is a plain liking with nothing softened about it, Quiero is a blunt demand, and Gustó is a preterite liking.",
                ),
                ChallengeEntity(
                    id = 50211, lessonId = 304, type = ChallengeType.FILL_BLANK,
                    question = "En tu lugar yo ___ viajar en tren.",
                    audioSrc = "asset:///audio/es/en_tu_lugar_yo_querria_viajar_en_tren.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "es.conditional.periphrasis",
                    acceptedAnswers = "querría|querria",
                    ruleText = "querría + infinitive is a polite supposition: 'I would like to travel by train'.\nQuerer is the infinitive and fills no slot, Quería is the imperfecto of a wish that lived in the past, and Querrás is the future.",
                ),
                ChallengeEntity(
                    id = 50212, lessonId = 304, type = ChallengeType.FILL_BLANK,
                    question = "¿___ ayudarme con la maleta?",
                    audioSrc = "asset:///audio/es/podria_ayudarme_con_la_maleta.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "es.conditional.periphrasis",
                    acceptedAnswers = "podría|podria",
                    ruleText = "podría + infinitive is the polite, uncertain offer: 'could you help me?'\nPuedes is a flat present, Poder is the infinitive, and Podré is the future, which commits to the help instead of offering it.",
                ),
                ChallengeEntity(
                    id = 50213, lessonId = 304, type = ChallengeType.SELECT,
                    question = "Which one asks for the bill politely?",
                    audioSrc = "asset:///audio/es/me_trae_la_cuenta_por_favor.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "es.conditional.periphrasis",
                    ruleText = "Me trae la cuenta, por favor is the polite conditional request: me + trae + la cuenta.\nQuiero la cuenta demands, Trae la cuenta is a bare order, and Me da la cuenta is a statement that never asks for anything.",
                ),
                ChallengeEntity(
                    id = 50214, lessonId = 304, type = ChallengeType.CONJUGATE,
                    question = "Which form says 'I would have time' rather than promising it?",
                    orderIndex = 4,
                    grammaticalFocus = "es.conditional.irregular",
                    ruleText = "The conditional is a polite maybe and the future is a promise: yo tendría 'I would have time', yo tendré 'I will have time'.\nTendríamos is nosotros and Tenemos is the present; neither is a future form at all.",
                ),
                ChallengeEntity(
                    id = 50215, lessonId = 304, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'I would like to book a table'",
                    audioSrc = "asset:///audio/es/me_gustaria_reservar_una_mesa.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "es.conditional.periphrasis",
                    ruleText = "The polite frame is me gustaría + infinitive, so Me gustaría reservar una mesa.\nQuiero is the blunt version, Gustaba is the imperfecto of a liking that belonged to the past, and Reservó is the preterite of the whole verb.",
                ),
                ChallengeEntity(
                    id = 50216, lessonId = 304, type = ChallengeType.FILL_BLANK,
                    question = "En casa siempre ___ la comida a las dos.",
                    audioSrc = "asset:///audio/es/en_casa_siempre_comeria_la_comida_a_las_dos.ogg",
                    orderIndex = 6,
                    grammaticalFocus = "es.conditional.regular",
                    acceptedAnswers = "comería|comeria",
                    ruleText = "The regular conditional is infinitive + ría, so comer → comería: 'we would eat at two'.\nComer is the infinitive, Comió is one finished preterite meal, and Comeremos is the future — none of the three is a supposition.",
                ),

                ChallengeEntity(
                    id = 50308, lessonId = 304, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/me_gustaria_reservar_una_mesa_para_cuatro.ogg",
                    orderIndex = 7,
                    grammaticalFocus = "es.conditional.periphrasis",
                    ruleText = "The polite periphrasis is me gustaría + infinitive, a softened want rather than a flat statement of appetite.\n'Me gusta reservar una mesa para cuatro' is the gustar construction, which says what I like rather than what I would like asked for; 'Quiero reservar una mesa para cuatro' is the blunt present, which asks without softening it; 'Me gustaba reservar una mesa para cuatro' is the imperfecto, a liking that lasted in a past scene.",
                ),
                ChallengeEntity(
                    id = 50219, lessonId = 304, type = ChallengeType.SELECT,
                    question = "Which one means 'the key'?",
                    audioSrc = "asset:///audio/es/la_llave.ogg",
                    orderIndex = 8,
                    ruleText = "la llave is the key that opens a door or a hotel room; la torre, la playa and la ventana are other things.\nIt is a feminine noun, and only one of the four is something you turn in a lock.",
                ),
                ChallengeEntity(
                    id = 50241, lessonId = 304, type = ChallengeType.CONJUGATE,
                    question = "Which conditional form of comer goes with 'yo'?",
                    audioSrc = "asset:///audio/es/comeria.ogg",
                    orderIndex = 9,
                    heldOut = true,
                    grammaticalFocus = "es.conditional.regular",
                    ruleText = "The regular conditional is infinitive + ría, so comer → comería, and the answer for yo is Comería.\nComerías is tú, Comeré is the future, Comía is the imperfecto of a meal that repeated in the past, and Comer is the infinitive.",
                ),
                ChallengeEntity(
                    id = 50242, lessonId = 304, type = ChallengeType.FILL_BLANK,
                    question = "En ese caso, yo no ___ la verdad.",
                    audioSrc = "asset:///audio/es/en_ese_caso_yo_no_diria_la_verdad.ogg",
                    orderIndex = 10,
                    heldOut = true,
                    grammaticalFocus = "es.conditional.irregular",
                    acceptedAnswers = "diría|diria",
                    ruleText = "A supposition about someone else's action is the conditional, so the answer is diría.\ndigo is the present, decía is the imperfecto of a habit in the past, and decir is the infinitive — none of the three supposes anything about the future.",
                ),

                ChallengeEntity(
                    id = 50309, lessonId = 304, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/yo_querria_un_vaso_de_agua_por_favor.ogg",
                    orderIndex = 11,
                    grammaticalFocus = "es.conditional.periphrasis",
                    ruleText = "querer → querría is the polite periphrasis: an offer softened into a question, not a demand.\n'Yo querré un vaso de agua, por favor' is the future, which promises the glass; 'Yo quería un vaso de agua, por favor' is the imperfecto, a wish that lasted in a past scene; 'Yo querer un vaso de agua, por favor' leaves the infinitive in the slot.",
                ),

                ChallengeEntity(
                    id = 50220, lessonId = 305, type = ChallengeType.FILL_BLANK,
                    question = "Estudié mucho ___ quería aprobar.",
                    audioSrc = "asset:///audio/es/estudie_mucho_porque_querria_aprobar.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.connectives",
                    acceptedAnswers = "porque",
                    ruleText = "porque states the reason of a past action and opens a whole clause: estudié mucho porque quería aprobar.\nEntonces states a result, Además adds extra information and También stacks another item; none of the three opens a clause of cause.",
                ),

                ChallengeEntity(
                    id = 50310, lessonId = 305, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/no_fui_porque_estaba_cansado.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "es.connectives",
                    ruleText = "porque opens a clause of cause and states why the main clause is as it is.\n'No fui aunque estaba cansado' is a concession, which would admit the tiredness and still go; 'No fui sin embargo estaba cansado' is a contrast that puts two facts side by side instead of making either the reason for the other; 'No fui para estaba cansado' is a purpose, and a purpose cannot open a clause in that shape.",
                ),
                ChallengeEntity(
                    id = 50221, lessonId = 305, type = ChallengeType.FILL_BLANK,
                    question = "No dormí temprano, ___ no me levanté.",
                    audioSrc = "asset:///audio/es/no_dormi_temprano_entonces_no_me_levante.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "es.connectives",
                    acceptedAnswers = "entonces",
                    ruleText = "entonces states a result, so the sentence runs cause and then effect: no dormí temprano, entonces no me levanté.\nTambién, Sin embargo and Además all add or contrast a second fact; none of the three carries a consequence.",
                ),
                ChallengeEntity(
                    id = 50222, lessonId = 305, type = ChallengeType.FILL_BLANK,
                    question = "___ hacía frío, salimos a pasear.",
                    audioSrc = "asset:///audio/es/aunque_hacia_frio_salimos_a_pasear.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "es.connectives",
                    acceptedAnswers = "aunque",
                    ruleText = "aunque states a concession: the second half happens in spite of the first, not because of it.\nTambién, Sin embargo and Además cannot open a clause; they only join something that already stands on its own.",
                ),
                ChallengeEntity(
                    id = 50223, lessonId = 305, type = ChallengeType.FILL_BLANK,
                    question = "Estudio español ___ viajar a España.",
                    orderIndex = 4,
                    grammaticalFocus = "es.connectives",
                    acceptedAnswers = "para",
                    ruleText = "para states the aim of an action: estudio español para viajar a España.\nTambién, Sin embargo and Además express no aim at all; none of them opens a clause that states a purpose.",
                ),
                ChallengeEntity(
                    id = 50224, lessonId = 305, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'It was late, so we went home'",
                    audioSrc = "asset:///audio/es/era_tarde_asi_que_nos_fuimos_a_casa.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "es.connectives",
                    ruleText = "así que joins two facts as cause and result: era tarde, así que nos fuimos a casa.\nTambién and Además only add a second fact to one that already stands; neither carries the result.",
                ),
                ChallengeEntity(
                    id = 50225, lessonId = 305, type = ChallengeType.SELECT,
                    question = "Which one means 'I called her because I needed money'?",
                    audioSrc = "asset:///audio/es/la_llame_porque_necesitaba_dinero.ogg",
                    orderIndex = 6,
                    grammaticalFocus = "es.connectives",
                    ruleText = "porque states the reason of a past action: la llamé porque necesitaba dinero.\nTambién and Sin embargo can sit in the middle of a sentence, but they open no clause and carry no reason, so neither fits this slot.",
                ),
                ChallengeEntity(
                    id = 50226, lessonId = 305, type = ChallengeType.SELECT,
                    question = "Which one states a purpose?",
                    audioSrc = "asset:///audio/es/estudio_espanol_para_viajar_a_espana.ogg",
                    orderIndex = 7,
                    grammaticalFocus = "es.connectives",
                    ruleText = "para states the aim of an action: estudio español para viajar a España.\nSin embargo and Además join clauses that already stand on their own; neither of the two states an aim.",
                ),
                ChallengeEntity(
                    id = 50227, lessonId = 305, type = ChallengeType.STORY,
                    question = "El plan cambió\n\nQuería ir a la playa.\nAun así, hacía frío.\nEntonces me quedé en casa y leí un libro.",
                    audioSrc = "asset:///audio/es/el_plan_cambio.ogg",
                    orderIndex = 8,
                ),
                ChallengeEntity(
                    id = 50228, lessonId = 305, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'Although it was expensive, we bought two bikes'",
                    audioSrc = "asset:///audio/es/aunque_costaba_mucho_compramos_dos_bicicletas.ogg",
                    orderIndex = 9,
                    grammaticalFocus = "es.connectives",
                    ruleText = "aunque opens a concession and the main clause carries the surprise: aunque costaba mucho, compramos dos bicicletas.\nEntonces states a result and para states an aim; neither opens a clause that is being conceded.",
                ),
                ChallengeEntity(
                    id = 50229, lessonId = 305, type = ChallengeType.SELECT,
                    question = "Which one means 'the weekend'?",
                    audioSrc = "asset:///audio/es/el_fin_de_semana.ogg",
                    orderIndex = 10,
                    ruleText = "el fin de semana is a masculine noun, and both halves of it stay as they are.\nLa fin de semana takes the wrong article, El fin de mes is the end of the month, and El fin de año is the end of the year.",
                ),
                ChallengeEntity(
                    id = 50243, lessonId = 305, type = ChallengeType.FILL_BLANK,
                    question = "Era tarde, ___ seguimos trabajando.",
                    audioSrc = "asset:///audio/es/era_tarde_pero_seguimos_trabajando.ogg",
                    orderIndex = 11,
                    heldOut = true,
                    grammaticalFocus = "es.connectives",
                    acceptedAnswers = "pero",
                    ruleText = "pero contrasts two facts that are both true, with no result and no aim: era tarde, pero seguimos trabajando.\ntambién only adds a fact, sin embargo sets the contrast down as an aside instead of joining two clauses, and además adds one more; none of them sets one clause against another.",
                ),

                ChallengeEntity(
                    id = 50311, lessonId = 305, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/llegue_tarde_asi_que_no_pude_entrar.ogg",
                    orderIndex = 12,
                    grammaticalFocus = "es.connectives",
                    ruleText = "así que opens a clause of result: the lateness is what caused the missed entry.\n'Llegué tarde, porque no pude entrar' makes the locked door the cause and the lateness the effect, which reverses the two; 'Llegué tarde, aunque no pude entrar' is a concession, which sets the locked door against the lateness instead of letting it follow; 'Llegué tarde, para no pude entrar' is a purpose, and a purpose cannot open a clause in that shape.",
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 501000, challengeId = 50200, text = "hablaría", correct = true),
                ChallengeOptionEntity(id = 501001, challengeId = 50200, text = "hablarías", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 501002, challengeId = 50200, text = "hablaré", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501003, challengeId = 50200, text = "hablaba", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 501010, challengeId = 50201, text = "Hablaría", correct = true),
                ChallengeOptionEntity(id = 501011, challengeId = 50201, text = "Hablaba", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501012, challengeId = 50201, text = "Hablaré", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501013, challengeId = 50201, text = "Hablar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 501020, challengeId = 50202, text = "viviríamos", correct = true),
                ChallengeOptionEntity(id = 501021, challengeId = 50202, text = "viviríais", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 501022, challengeId = 50202, text = "viviremos", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501023, challengeId = 50202, text = "vivíamos", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 501030, challengeId = 50203, text = "tendría", correct = true),
                ChallengeOptionEntity(id = 501031, challengeId = 50203, text = "tendrías", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 501032, challengeId = 50203, text = "tendré", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501033, challengeId = 50203, text = "tenía", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 501040, challengeId = 50204, text = "podríamos", correct = true),
                ChallengeOptionEntity(id = 501041, challengeId = 50204, text = "podré", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501042, challengeId = 50204, text = "podían", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 501043, challengeId = 50204, text = "podemos", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 501050, challengeId = 50205, text = "diría", correct = true),
                ChallengeOptionEntity(id = 501051, challengeId = 50205, text = "dirían", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 501052, challengeId = 50205, text = "dirá", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501053, challengeId = 50205, text = "dice", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 501060, challengeId = 50206, text = "saldría", correct = true),
                ChallengeOptionEntity(id = 501061, challengeId = 50206, text = "saldrías", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 501062, challengeId = 50206, text = "saldré", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501063, challengeId = 50206, text = "salía", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 501070, challengeId = 50207, text = "Estaría", correct = true),
                ChallengeOptionEntity(id = 501071, challengeId = 50207, text = "Estará", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501072, challengeId = 50207, text = "Estaba", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501073, challengeId = 50207, text = "Estar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 501080, challengeId = 50208, text = "vendríamos", correct = true),
                ChallengeOptionEntity(id = 501081, challengeId = 50208, text = "vendremos", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501082, challengeId = 50208, text = "venían", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 501083, challengeId = 50208, text = "venir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 501090, challengeId = 50209, text = "La cita", correct = true, audioSrc = "asset:///audio/es/la_cita.ogg"),
                ChallengeOptionEntity(id = 501091, challengeId = 50209, text = "La factura", correct = false),
                ChallengeOptionEntity(id = 501092, challengeId = 50209, text = "La receta", correct = false),
                ChallengeOptionEntity(id = 501093, challengeId = 50209, text = "La entrada", correct = false),

                ChallengeOptionEntity(id = 501400, challengeId = 50240, text = "Harías", correct = true),
                ChallengeOptionEntity(id = 501401, challengeId = 50240, text = "Harás", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501402, challengeId = 50240, text = "Haré", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501403, challengeId = 50240, text = "Hacer", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 501100, challengeId = 50210, text = "gustaría", correct = true),
                ChallengeOptionEntity(id = 501101, challengeId = 50210, text = "gusta", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501102, challengeId = 50210, text = "quiero", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 501103, challengeId = 50210, text = "gustó", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 501110, challengeId = 50211, text = "querría", correct = true),
                ChallengeOptionEntity(id = 501111, challengeId = 50211, text = "querrás", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501112, challengeId = 50211, text = "quería", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501113, challengeId = 50211, text = "querer", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 501120, challengeId = 50212, text = "podría", correct = true),
                ChallengeOptionEntity(id = 501121, challengeId = 50212, text = "puedes", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501122, challengeId = 50212, text = "poder", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501123, challengeId = 50212, text = "podré", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 501130, challengeId = 50213, text = "¿Me trae la cuenta, por favor?", correct = true, audioSrc = "asset:///audio/es/me_trae_la_cuenta_por_favor.ogg"),
                ChallengeOptionEntity(id = 501131, challengeId = 50213, text = "Quiero la cuenta.", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 501132, challengeId = 50213, text = "Trae la cuenta.", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 501133, challengeId = 50213, text = "Me da la cuenta.", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 501140, challengeId = 50214, text = "tendría", correct = true),
                ChallengeOptionEntity(id = 501141, challengeId = 50214, text = "tendríamos", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 501142, challengeId = 50214, text = "tendré", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501143, challengeId = 50214, text = "tenemos", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 501150, challengeId = 50215, text = "Me", correct = true),
                ChallengeOptionEntity(id = 501151, challengeId = 50215, text = "gustaría", correct = true),
                ChallengeOptionEntity(id = 501152, challengeId = 50215, text = "reservar", correct = true),
                ChallengeOptionEntity(id = 501153, challengeId = 50215, text = "una", correct = true),
                ChallengeOptionEntity(id = 501154, challengeId = 50215, text = "mesa", correct = true),
                ChallengeOptionEntity(id = 501155, challengeId = 50215, text = "quiero", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 501156, challengeId = 50215, text = "gustaba", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501157, challengeId = 50215, text = "reservó", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 501160, challengeId = 50216, text = "Comería", correct = true),
                ChallengeOptionEntity(id = 501161, challengeId = 50216, text = "Comer", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501162, challengeId = 50216, text = "Comió", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501163, challengeId = 50216, text = "Comeremos", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 501190, challengeId = 50219, text = "La llave", correct = true, audioSrc = "asset:///audio/es/la_llave.ogg"),
                ChallengeOptionEntity(id = 501191, challengeId = 50219, text = "La torre", correct = false),
                ChallengeOptionEntity(id = 501192, challengeId = 50219, text = "La playa", correct = false),
                ChallengeOptionEntity(id = 501193, challengeId = 50219, text = "La ventana", correct = false),

                ChallengeOptionEntity(id = 501410, challengeId = 50241, text = "Comería", correct = true),
                ChallengeOptionEntity(id = 501411, challengeId = 50241, text = "Comerías", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 501412, challengeId = 50241, text = "Comeré", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501413, challengeId = 50241, text = "Comía", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 501420, challengeId = 50242, text = "diría", correct = true),
                ChallengeOptionEntity(id = 501421, challengeId = 50242, text = "digo", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501422, challengeId = 50242, text = "decía", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 501423, challengeId = 50242, text = "decir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 501200, challengeId = 50220, text = "porque", correct = true),
                ChallengeOptionEntity(id = 501201, challengeId = 50220, text = "entonces", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501202, challengeId = 50220, text = "además", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501203, challengeId = 50220, text = "también", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 501210, challengeId = 50221, text = "entonces", correct = true),
                ChallengeOptionEntity(id = 501211, challengeId = 50221, text = "sin embargo", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501212, challengeId = 50221, text = "también", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501213, challengeId = 50221, text = "además", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 501220, challengeId = 50222, text = "aunque", correct = true),
                ChallengeOptionEntity(id = 501221, challengeId = 50222, text = "también", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501222, challengeId = 50222, text = "sin embargo", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501223, challengeId = 50222, text = "además", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 501230, challengeId = 50223, text = "para", correct = true),
                ChallengeOptionEntity(id = 501231, challengeId = 50223, text = "también", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501232, challengeId = 50223, text = "sin embargo", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501233, challengeId = 50223, text = "además", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 501240, challengeId = 50224, text = "Era", correct = true),
                ChallengeOptionEntity(id = 501241, challengeId = 50224, text = "tarde", correct = true),
                ChallengeOptionEntity(id = 501242, challengeId = 50224, text = "así", correct = true),
                ChallengeOptionEntity(id = 501243, challengeId = 50224, text = "que", correct = true),
                ChallengeOptionEntity(id = 501244, challengeId = 50224, text = "nos", correct = true),
                ChallengeOptionEntity(id = 501245, challengeId = 50224, text = "fuimos", correct = true),
                ChallengeOptionEntity(id = 501246, challengeId = 50224, text = "a", correct = true),
                ChallengeOptionEntity(id = 501247, challengeId = 50224, text = "casa", correct = true),
                ChallengeOptionEntity(id = 501248, challengeId = 50224, text = "también", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501249, challengeId = 50224, text = "además", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 501250, challengeId = 50225, text = "La llamé porque necesitaba dinero", correct = true, audioSrc = "asset:///audio/es/la_llame_porque_necesitaba_dinero.ogg"),
                ChallengeOptionEntity(id = 501251, challengeId = 50225, text = "La llamé también necesitaba dinero", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501252, challengeId = 50225, text = "La llamé sin embargo necesitaba dinero", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 501260, challengeId = 50226, text = "Estudio español para viajar a España", correct = true, audioSrc = "asset:///audio/es/estudio_espanol_para_viajar_a_espana.ogg"),
                ChallengeOptionEntity(id = 501261, challengeId = 50226, text = "Estudio español sin embargo viajar a España", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501262, challengeId = 50226, text = "Estudio español además viajar a España", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 501270, challengeId = 50227, text = "A plan that changed", correct = true),
                ChallengeOptionEntity(id = 501271, challengeId = 50227, text = "A day at the beach", correct = false),
                ChallengeOptionEntity(id = 501272, challengeId = 50227, text = "A week in the mountains", correct = false),
                ChallengeOptionEntity(id = 501273, challengeId = 50227, text = "A long train ride", correct = false),

                ChallengeOptionEntity(id = 501280, challengeId = 50228, text = "Aunque", correct = true),
                ChallengeOptionEntity(id = 501281, challengeId = 50228, text = "costaba", correct = true),
                ChallengeOptionEntity(id = 501282, challengeId = 50228, text = "mucho", correct = true),
                ChallengeOptionEntity(id = 501284, challengeId = 50228, text = "dos", correct = true),
                ChallengeOptionEntity(id = 501285, challengeId = 50228, text = "bicicletas", correct = true),
                ChallengeOptionEntity(id = 501286, challengeId = 50228, text = "también", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501283, challengeId = 50228, text = "compramos", correct = true),
                ChallengeOptionEntity(id = 501287, challengeId = 50228, text = "además", correct = false, errorTag = "WRONG_FORM"),


                ChallengeOptionEntity(id = 501290, challengeId = 50229, text = "El fin de semana", correct = true, audioSrc = "asset:///audio/es/el_fin_de_semana.ogg"),
                ChallengeOptionEntity(id = 501291, challengeId = 50229, text = "La fin de semana", correct = false),
                ChallengeOptionEntity(id = 501292, challengeId = 50229, text = "El fin de mes", correct = false),
                ChallengeOptionEntity(id = 501293, challengeId = 50229, text = "El fin de año", correct = false),

                ChallengeOptionEntity(id = 501430, challengeId = 50243, text = "Pero", correct = true),
                ChallengeOptionEntity(id = 501431, challengeId = 50243, text = "también", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501432, challengeId = 50243, text = "sin embargo", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 501433, challengeId = 50243, text = "además", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 503060, challengeId = 50306, text = "Yo compraría una casa más grande", correct = true),
                ChallengeOptionEntity(id = 503061, challengeId = 50306, text = "Yo compraré una casa más grande", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 503062, challengeId = 50306, text = "Vosotros compraríais una casa más grande", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 503063, challengeId = 50306, text = "Yo comprar una casa más grande", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 503070, challengeId = 50307, text = "Yo diría que sí, pero no sé", correct = true),
                ChallengeOptionEntity(id = 503071, challengeId = 50307, text = "Yo diré que sí, pero no sé", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 503072, challengeId = 50307, text = "Yo decía que sí, pero no sé", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 503073, challengeId = 50307, text = "Yo decir que sí, pero no sé", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 503080, challengeId = 50308, text = "Me gustaría reservar una mesa para cuatro", correct = true),
                ChallengeOptionEntity(id = 503081, challengeId = 50308, text = "Me gusta reservar una mesa para cuatro", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 503082, challengeId = 50308, text = "Quiero reservar una mesa para cuatro", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 503083, challengeId = 50308, text = "Me gustaba reservar una mesa para cuatro", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 503090, challengeId = 50309, text = "Yo querría un vaso de agua, por favor", correct = true),
                ChallengeOptionEntity(id = 503091, challengeId = 50309, text = "Yo querré un vaso de agua, por favor", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 503092, challengeId = 50309, text = "Yo quería un vaso de agua, por favor", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 503093, challengeId = 50309, text = "Yo querer un vaso de agua, por favor", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 503100, challengeId = 50310, text = "No fui porque estaba cansado", correct = true),
                ChallengeOptionEntity(id = 503101, challengeId = 50310, text = "No fui aunque estaba cansado", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 503102, challengeId = 50310, text = "No fui sin embargo estaba cansado", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 503103, challengeId = 50310, text = "No fui para estaba cansado", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 503110, challengeId = 50311, text = "Llegué tarde, así que no pude entrar", correct = true),
                ChallengeOptionEntity(id = 503111, challengeId = 50311, text = "Llegué tarde, porque no pude entrar", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 503112, challengeId = 50311, text = "Llegué tarde, aunque no pude entrar", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 503113, challengeId = 50311, text = "Llegué tarde, para no pude entrar", correct = false, errorTag = "WRONG_FORM"),
            ),
        ),

        // ---------------------------------------------------------------------
        // Unit 13: El Subjuntivo
        //
        // The subjunctive is a MOOD, not a tense, so every distractor below that
        // puts an indicative in the slot is tagged WRONG_FORM: what the learner
        // got wrong is the mood they chose, not when the action happens. There is
        // no tag for mood in the fixed vocabulary, and inventing one is worse
        // than naming the form.
        // ---------------------------------------------------------------------
        UnitPayload(
            unit = UnitEntity(
                id = 32,
                courseId = 1,
                title = "Unit 13: The Subjunctive",
                description = "quiero que vengas — no creo que sea, para que, a menos que",
                orderIndex = 12,
            ),
            lessons = listOf(
                LessonEntity(id = 600, unitId = 32, title = "Lesson 26: What I Want You To Do", orderIndex = 0),
                LessonEntity(id = 601, unitId = 32, title = "Lesson 27: Feelings, Doubts, Denials", orderIndex = 1),
                LessonEntity(id = 602, unitId = 32, title = "Lesson 28: So That, Unless", orderIndex = 2),
            ),
            challenges = listOf(
                ChallengeEntity(
                    id = 70000, lessonId = 600, type = ChallengeType.CONJUGATE,
                    question = "Which form of venir goes with 'tú' in 'Quiero que ___ a mi casa'?",
                    audioSrc = "asset:///audio/es/vengas.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.subjunctive.wants",
                    ruleText = "Querer, esperar, necesitar and buscar + que put the second verb in the subjunctive, and against tú that is vengas.\nvienes is the present indicative, which states a fact instead of naming a want, and venir is the infinitive, which cannot follow que at all; venga is the subjunctive but the wrong person, because it is él/ella/usted.",
                ),
                ChallengeEntity(
                    id = 70001, lessonId = 600, type = ChallengeType.FILL_BLANK,
                    question = "Quiero que ___ el informe antes de las cinco.",
                    audioSrc = "asset:///audio/es/quiero_que_envies_el_informe_antes_de_las_cinco.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "es.subjunctive.wants",
                    acceptedAnswers = "envíes|envies",
                    ruleText = "The verb after querer que is the one being asked for, so it goes in the subjunctive: quiero que envíes el informe.\nenvías is the present indicative, which reports what you do rather than what you are asking for, and enviar is the infinitive; envíe is the subjunctive but the wrong person, because the report is addressed to tú.",
                ),
                ChallengeEntity(
                    id = 70002, lessonId = 600, type = ChallengeType.SELECT,
                    question = "Which one means 'I need you to sign this' (said to one person)?",
                    audioSrc = "asset:///audio/es/necesito_que_firmes_este_formulario.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "es.subjunctive.wants",
                    ruleText = "Necesitar que also takes the subjunctive, and the form for tú is firmes.\nfirma is the present indicative, which says what he does instead of what you need him to do, and firmar is the infinitive; firmen is the subjunctive but the wrong person, because the request is aimed at one person.",
                ),
                ChallengeEntity(
                    id = 70003, lessonId = 600, type = ChallengeType.CONJUGATE,
                    question = "Which form of hacer goes with 'tú' in 'Espero que ___ la cena' (said to one person)?",
                    audioSrc = "asset:///audio/es/hagas.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "es.subjunctive.wants",
                    ruleText = "The subjunctive tú of hacer drops the c and adds -as, so hagas.\nhaces is the present indicative, which states the habit instead of the expectation, and hacer is the infinitive; haga is the subjunctive but the wrong person, because he is the one being asked, not the one being spoken to.",
                ),
                ChallengeEntity(
                    id = 70004, lessonId = 600, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'I want you to come tomorrow'",
                    audioSrc = "asset:///audio/es/quiero_que_vengas_manana.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "es.subjunctive.wants",
                    ruleText = "Querer que is one of the frames that obliges the subjunctive, so the tiles assemble into quiero que vengas mañana.\nvienes is the present indicative, which states a habit instead of a want, and venir is the infinitive, which cannot fill the slot; quiera is the subjunctive but the wrong person, because the sentence is addressed to tú.",
                ),
                ChallengeEntity(
                    id = 70005, lessonId = 600, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each trigger to the frame it opens",
                    orderIndex = 5,
                ),

                ChallengeEntity(
                    id = 70010, lessonId = 601, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/no_creo_que_sea_la_verdad.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.subjunctive.emotion_doubt",
                    ruleText = "No creo que is a negation of belief, and anything doubted takes the subjunctive: no creo que sea la verdad.\nes is the present indicative, which would assert the opposite of what the speaker means to doubt, and ser is the infinitive; sean is the subjunctive but the wrong person, because the speaker is talking about one thing, not many.",
                ),
                ChallengeEntity(
                    id = 70011, lessonId = 601, type = ChallengeType.CONJUGATE,
                    question = "Which form of ser goes with 'tú' in 'No creo que ___ un buen médico' (said to one person)?",
                    audioSrc = "asset:///audio/es/seas.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "es.subjunctive.emotion_doubt",
                    ruleText = "The subjunctive tú of ser is seas, and that is the form no creo que asks for.\neres is the present indicative, which believes the opposite of what the speaker doubts, and ser is the infinitive; sea is the subjunctive but the wrong person, because the doctor is third person.",
                ),
                ChallengeEntity(
                    id = 70012, lessonId = 601, type = ChallengeType.FILL_BLANK,
                    question = "Dudo que ___ llegar a tiempo.",
                    audioSrc = "asset:///audio/es/dudo_que_pueda_llegar_a_tiempo.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "es.subjunctive.emotion_doubt",
                    acceptedAnswers = "pueda",
                    ruleText = "Dudar que, like no creer que and no saber que, puts the second verb in the subjunctive: dudo que pueda llegar a tiempo.\npuedo is the present indicative, which is a claim rather than a doubt, and poder is the infinitive; puedan is the subjunctive but the wrong person, because the speaker is talking about himself.",
                ),
                ChallengeEntity(
                    id = 70013, lessonId = 601, type = ChallengeType.SELECT,
                    question = "Which one means 'It's a shame he didn't come' (said about one man)?",
                    audioSrc = "asset:///audio/es/es_una_pena_que_no_haya_venido.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "es.subjunctive.emotion_doubt",
                    ruleText = "Es una pena que states a feeling, and a feeling is not a fact, so the second verb is the subjunctive: es una pena que no haya venido.\nha is the present indicative, which would put the coming back inside the present, and haber is the infinitive; venga is the subjunctive but the wrong person, because the sentence is about him, not about tú.",
                ),
                ChallengeEntity(
                    id = 70014, lessonId = 601, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/me_alegro_de_que_esten_listos.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "es.subjunctive.emotion_doubt",
                    ruleText = "Alegrarse de que is a feeling, so the second verb is the subjunctive: me alegro de que estén listos.\nestán is the present indicative, which would be a statement of fact rather than a reaction to one, and estar is the infinitive; esté is the subjunctive but the wrong person, because the people are plural.",
                ),
                ChallengeEntity(
                    id = 70015, lessonId = 601, type = ChallengeType.CONJUGATE,
                    question = "Which form of estar goes with 'vosotros' in 'Espero que ___ bien' (said to two or more people)?",
                    audioSrc = "asset:///audio/es/esteis.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "es.subjunctive.emotion_doubt",
                    ruleText = "The subjunctive vosotros of estar is estéis: the -éis ending takes é where the present estáis takes í.\nestáis is the present indicative, which is a fact rather than a hope, and estar is the infinitive; esté is the subjunctive but the wrong person, because that form is for él/ella/usted.",
                ),

                ChallengeEntity(
                    id = 70020, lessonId = 602, type = ChallengeType.FILL_BLANK,
                    question = "A menos que ___ el tren, no llego.",
                    audioSrc = "asset:///audio/es/a_menos_que_salga_el_tren_no_llego.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.subjunctive.purpose_concession",
                    acceptedAnswers = "salga",
                    ruleText = "A menos que states the one thing that would undo the plan, so its verb is the subjunctive: a menos que salga el tren, no llego.\nsale is the present indicative, which would name a fact instead of a condition, and salir is the infinitive; salgan is the subjunctive but the wrong person, because the train is one thing.",
                ),
                ChallengeEntity(
                    id = 70021, lessonId = 602, type = ChallengeType.SELECT,
                    question = "Which one means 'I am telling you so that you know' (said to one person)?",
                    audioSrc = "asset:///audio/es/te_lo_digo_para_que_lo_sepas.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "es.subjunctive.purpose_concession",
                    ruleText = "Para que states a purpose, and a purpose is something still wanted, so the second verb is the subjunctive: te lo digo para que lo sepas.\nsabes is the present indicative, which would assume the knowing rather than arrange for it, and saber is the infinitive; sepa is the subjunctive but the wrong person, because the person who has to know is tú.",
                ),
                ChallengeEntity(
                    id = 70022, lessonId = 602, type = ChallengeType.CONJUGATE,
                    question = "Which form of poder goes with 'nosotros' in 'Vamos para que ___ verlo' (said about two or more people)?",
                    audioSrc = "asset:///audio/es/podamos.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "es.subjunctive.purpose_concession",
                    ruleText = "The subjunctive nosotros of poder drops the d, so podamos, and that is what para que asks for.\npodemos is the present indicative, which states a current ability rather than arranging for one, and poder is the infinitive; pueda is the subjunctive but the wrong person, because it is for él/ella/usted.",
                ),
                ChallengeEntity(
                    id = 70023, lessonId = 602, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/te_lo_compro_para_que_puedas_usarlo_manana.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "es.subjunctive.purpose_concession",
                    ruleText = "Para que states a purpose, so the second verb is the subjunctive: te lo compro para que puedas usarlo mañana.\npuedes is the present indicative, which would leave the ability to chance, and poder is the infinitive; pueda is the subjunctive but the wrong person, because it is tú who has to be able to use it.",
                ),
                ChallengeEntity(
                    id = 70024, lessonId = 602, type = ChallengeType.FILL_BLANK,
                    question = "Con tal de que ___ bien, te vas a casa.",
                    audioSrc = "asset:///audio/es/con_tal_de_que_termines_bien.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "es.subjunctive.purpose_concession",
                    acceptedAnswers = "termines",
                    ruleText = "Con tal de que is a purpose clause, so the second verb is the subjunctive: con tal de que termines bien, te vas a casa.\ntermina is the present indicative, which states a habit instead of a condition, and terminar is the infinitive; terminen is the subjunctive but the wrong person, because the condition is about tú.",
                ),
                ChallengeEntity(
                    id = 70025, lessonId = 602, type = ChallengeType.STORY,
                    question = "El plan del domingo\n\nQuiero que vengas a la piscina.\nDicen que lloverá por la tarde.\nA menos que llueva, nos vemos a las cuatro.\n\n❓ Under what condition will they meet?",
                    audioSrc = "asset:///audio/es/el_plan_del_domingo.ogg",
                    orderIndex = 5,
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 700000, challengeId = 70000, text = "vengas", correct = true),
                ChallengeOptionEntity(id = 700001, challengeId = 70000, text = "vienes", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700002, challengeId = 70000, text = "venga", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700003, challengeId = 70000, text = "venir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700010, challengeId = 70001, text = "envíes", correct = true),
                ChallengeOptionEntity(id = 700011, challengeId = 70001, text = "envías", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700012, challengeId = 70001, text = "envíe", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700013, challengeId = 70001, text = "enviar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700020, challengeId = 70002, text = "Necesito que firmes este formulario", correct = true, audioSrc = "asset:///audio/es/necesito_que_firmes_este_formulario.ogg"),
                ChallengeOptionEntity(id = 700021, challengeId = 70002, text = "Necesito que firma este formulario", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700022, challengeId = 70002, text = "Necesito que firmen este formulario", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700023, challengeId = 70002, text = "Necesito que firmar este formulario", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700030, challengeId = 70003, text = "hagas", correct = true),
                ChallengeOptionEntity(id = 700031, challengeId = 70003, text = "haces", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700032, challengeId = 70003, text = "haga", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700033, challengeId = 70003, text = "hacer", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700040, challengeId = 70004, text = "Quiero", correct = true),
                ChallengeOptionEntity(id = 700041, challengeId = 70004, text = "que", correct = true),
                ChallengeOptionEntity(id = 700042, challengeId = 70004, text = "vengas", correct = true),
                ChallengeOptionEntity(id = 700043, challengeId = 70004, text = "mañana", correct = true),
                ChallengeOptionEntity(id = 700044, challengeId = 70004, text = "vienes", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700045, challengeId = 70004, text = "venir", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700046, challengeId = 70004, text = "quiera", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 700050, challengeId = 70005, text = "Quiero que", correct = true),
                ChallengeOptionEntity(id = 700051, challengeId = 70005, text = "I want / I hope", correct = true),
                ChallengeOptionEntity(id = 700052, challengeId = 70005, text = "No creo que", correct = true),
                ChallengeOptionEntity(id = 700053, challengeId = 70005, text = "I don't think", correct = true),
                ChallengeOptionEntity(id = 700054, challengeId = 70005, text = "Para que", correct = true),
                ChallengeOptionEntity(id = 700055, challengeId = 70005, text = "So that", correct = true),

                ChallengeOptionEntity(id = 700100, challengeId = 70010, text = "No creo que sea la verdad", correct = true),
                ChallengeOptionEntity(id = 700101, challengeId = 70010, text = "No creo que es la verdad", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700102, challengeId = 70010, text = "No creo que sean la verdad", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700103, challengeId = 70010, text = "No creo que ser la verdad", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700110, challengeId = 70011, text = "seas", correct = true),
                ChallengeOptionEntity(id = 700111, challengeId = 70011, text = "eres", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700112, challengeId = 70011, text = "sea", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700113, challengeId = 70011, text = "ser", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700120, challengeId = 70012, text = "pueda", correct = true),
                ChallengeOptionEntity(id = 700121, challengeId = 70012, text = "puedo", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700122, challengeId = 70012, text = "puedan", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700123, challengeId = 70012, text = "poder", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700130, challengeId = 70013, text = "Es una pena que no haya venido", correct = true, audioSrc = "asset:///audio/es/es_una_pena_que_no_haya_venido.ogg"),
                ChallengeOptionEntity(id = 700131, challengeId = 70013, text = "Es una pena que no ha venido", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700132, challengeId = 70013, text = "Es una pena que no venga", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700133, challengeId = 70013, text = "Es una pena que no haber venido", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700140, challengeId = 70014, text = "Me alegro de que estén listos", correct = true),
                ChallengeOptionEntity(id = 700141, challengeId = 70014, text = "Me alegro de que están listos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700142, challengeId = 70014, text = "Me alegro de que esté listos", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700143, challengeId = 70014, text = "Me alegro de que estar listos", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700150, challengeId = 70015, text = "estéis", correct = true),
                ChallengeOptionEntity(id = 700151, challengeId = 70015, text = "estáis", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700152, challengeId = 70015, text = "esté", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700153, challengeId = 70015, text = "estar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700200, challengeId = 70020, text = "salga", correct = true),
                ChallengeOptionEntity(id = 700201, challengeId = 70020, text = "sale", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700202, challengeId = 70020, text = "salgan", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700203, challengeId = 70020, text = "salir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700210, challengeId = 70021, text = "Te lo digo para que lo sepas", correct = true, audioSrc = "asset:///audio/es/te_lo_digo_para_que_lo_sepas.ogg"),
                ChallengeOptionEntity(id = 700211, challengeId = 70021, text = "Te lo digo para que lo sabes", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700212, challengeId = 70021, text = "Te lo digo para que lo sepa", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700213, challengeId = 70021, text = "Te lo digo para lo saber", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700220, challengeId = 70022, text = "podamos", correct = true),
                ChallengeOptionEntity(id = 700221, challengeId = 70022, text = "podemos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700222, challengeId = 70022, text = "pueda", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700223, challengeId = 70022, text = "poder", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700230, challengeId = 70023, text = "Te lo compro para que puedas usarlo mañana", correct = true),
                ChallengeOptionEntity(id = 700231, challengeId = 70023, text = "Te lo compro para que puedes usarlo mañana", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700232, challengeId = 70023, text = "Te lo compro para que pueda usarlo mañana", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700233, challengeId = 70023, text = "Te lo compro para poder usarlo mañana", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700240, challengeId = 70024, text = "termines", correct = true),
                ChallengeOptionEntity(id = 700241, challengeId = 70024, text = "termina", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700242, challengeId = 70024, text = "terminen", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700243, challengeId = 70024, text = "terminar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700250, challengeId = 70025, text = "Unless it rains", correct = true),
                ChallengeOptionEntity(id = 700251, challengeId = 70025, text = "If the weather is good", correct = false),
                ChallengeOptionEntity(id = 700252, challengeId = 70025, text = "As soon as the news arrives", correct = false),
                ChallengeOptionEntity(id = 700253, challengeId = 70025, text = "Because they like the water", correct = false),
            ),
        ),

        // ---------------------------------------------------------------------
        // Unit 14: Pronombres y Reflejos
        //
        // Direct object pronouns (lo/la/los/las), the indirect object trio
        // (me/te/nos + le/les) with gustar, reflexive verbs with their own
        // pronouns, impersonal se, and the imperative for tú and usted.
        // ---------------------------------------------------------------------
        UnitPayload(
            unit = UnitEntity(
                id = 33,
                courseId = 1,
                title = "Unit 14: Object Pronouns & Reflexive Verbs",
                description = "lo veo, me gusta, me levanto — habla, no hables",
                orderIndex = 13,
            ),
            lessons = listOf(
                LessonEntity(id = 603, unitId = 33, title = "Lesson 29: Him, Her, It", orderIndex = 0),
                LessonEntity(id = 604, unitId = 33, title = "Lesson 30: To Me, To You", orderIndex = 1),
                LessonEntity(id = 605, unitId = 33, title = "Lesson 31: Doing It Yourself", orderIndex = 2),
                LessonEntity(id = 606, unitId = 33, title = "Lesson 32: Give Orders", orderIndex = 3),
            ),
            challenges = listOf(
                ChallengeEntity(
                    id = 70030, lessonId = 603, type = ChallengeType.CONJUGATE,
                    question = "Which pronoun replaces 'el coche' in 'Ayer vi el coche'?",
                    audioSrc = "asset:///audio/es/lo.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.object_pronoun.direct",
                    ruleText = "A direct object takes lo, la, los or las, agreeing with the thing in gender and number: vi el coche → lo vi.\nla is the feminine, which the car is not; me is the reflexive pronoun, which can never point at a third thing; el is the noun itself, which the pronoun exists to replace.",
                ),
                ChallengeEntity(
                    id = 70031, lessonId = 603, type = ChallengeType.FILL_BLANK,
                    question = "No ___ veo nunca.",
                    audioSrc = "asset:///audio/es/no_lo_veo_nunca.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "es.object_pronoun.direct",
                    acceptedAnswers = "lo",
                    ruleText = "The direct object pronoun stands in for the thing and agrees with it: no lo veo nunca.\nla is the feminine and los the plural, so neither can stand for a masculine singular thing; ver is the infinitive, which fills no slot at all.",
                ),
                ChallengeEntity(
                    id = 70032, lessonId = 603, type = ChallengeType.SELECT,
                    question = "Which one means 'I see her at the door' (one woman)?",
                    audioSrc = "asset:///audio/es/la_veo_en_la_puerta.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "es.object_pronoun.direct",
                    ruleText = "A direct object pronoun agrees with the noun it replaces, and a woman is feminine singular: la veo en la puerta.\nlo is the masculine, which would name a different person or thing; las veo is the plural, and the sentence is about one woman; la ver en la puerta is an infinitive with the pronoun attached, which is not a sentence.",
                ),
                ChallengeEntity(
                    id = 70033, lessonId = 603, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/los_veo_en_el_parque.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "es.object_pronoun.direct",
                    ruleText = "The direct object pronoun keeps the number of the noun: los veo en el parque.\nveo los en el parque puts the pronoun behind the verb, which Spanish only allows with an infinitive or a gerund; la veo en el parque is singular, and there is a group of people; nos vemos en el parque turns the object into a reflexive, which would mean the speaker is looking at himself.",
                ),
                ChallengeEntity(
                    id = 70034, lessonId = 603, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'I buy the tickets'",
                    audioSrc = "asset:///audio/es/compro_los_boletos.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "es.object_pronoun.direct",
                    ruleText = "The pronoun has to agree with the noun it stands in for, and los boletos is masculine plural, so the tiles assemble into compro los boletos.\nla is the feminine, so it cannot stand in for a masculine noun; lo is the singular, so it cannot stand in for a plural one; veo is the first person present, and the sentence is about the speaker buying rather than about the speaker seeing.",
                ),
                ChallengeEntity(
                    id = 70035, lessonId = 603, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each pronoun to the noun it can stand for",
                    orderIndex = 5,
                ),

                ChallengeEntity(
                    id = 70040, lessonId = 604, type = ChallengeType.CONJUGATE,
                    question = "Which pronoun replaces 'a mi hermana' in 'Doy el libro a mi hermana'?",
                    audioSrc = "asset:///audio/es/le.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.object_pronoun.indirect",
                    ruleText = "A recipient takes the indirect object pronoun le, and for one person that is le: doy el libro a mi hermana → le doy el libro.\nlo is the direct object pronoun, which would make the book the receiver; la agrees with the book in gender but is still direct, so it cannot be the person; les is the plural receiver, and the sentence names one sister.",
                ),
                ChallengeEntity(
                    id = 70041, lessonId = 604, type = ChallengeType.FILL_BLANK,
                    question = "___ doy el libro a mi hermana.",
                    audioSrc = "asset:///audio/es/le_doy_el_libro_a_mi_hermana.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "es.object_pronoun.indirect",
                    acceptedAnswers = "le",
                    ruleText = "The indirect object pronoun names the receiver and comes before the verb: le doy el libro a mi hermana.\nlos doy el libro a mi hermana would make the books the receiver; lo doy el libro a mi hermana would use the direct pronoun for a person; me doy el libro a mi hermana would make the speaker give the book to himself.",
                ),
                ChallengeEntity(
                    id = 70042, lessonId = 604, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/a_marta_le_gusta_el_cafe.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "es.object_pronoun.indirect",
                    ruleText = "Gustar works backwards: the thing liked is the subject and the person who likes it is the indirect object, so a Marta le gusta el café.\na Marta gusta el café leaves the person out entirely, which no Spanish gustar sentence can do; a Marta le gustan el café puts a plural verb on a singular thing; a Marta le gusta los cafés puts a singular verb on a plural thing, and the café is one thing.",
                ),
                ChallengeEntity(
                    id = 70043, lessonId = 604, type = ChallengeType.CONJUGATE,
                    question = "Which form of gustar goes with 'A mis hermanas ___ las fresas'?",
                    audioSrc = "asset:///audio/es/gustan.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "es.gustar",
                    ruleText = "With gustar the verb agrees with the thing that is liked, and las fresas is plural, so gustan.\ngusta is singular, which only fits one thing; gusto is the first person, which would make the sisters the ones doing the liking; gustar is the infinitive, which nothing here can put in front of a name.",
                ),
                ChallengeEntity(
                    id = 70044, lessonId = 604, type = ChallengeType.FILL_BLANK,
                    question = "Me ___ mucho tus dibujos.",
                    audioSrc = "asset:///audio/es/me_gustan_mucho_tus_dibujos.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "es.gustar",
                    acceptedAnswers = "gustan",
                    ruleText = "The verb agrees with the thing liked, and tus dibujos is plural, so gustan: me gustan mucho tus dibujos.\ngusta is singular and only fits one drawing; gustar is the infinitive, which me cannot put in front of; gusto is the first person, which would say the drawings like the speaker.",
                ),
                ChallengeEntity(
                    id = 70045, lessonId = 604, type = ChallengeType.SELECT,
                    question = "Which one means 'We like the sea' (said to friends)?",
                    audioSrc = "asset:///audio/es/nos_gusta_el_mar.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "es.gustar",
                    ruleText = "Nosotros liking something is nos gusta, singular, because el mar is one thing.\nnos gustan is the plural verb, which would only fit a plural thing; nos gustamos cannot exist, because gustar never conjugates for the person doing the liking; nos gustar el mar leaves the verb as an infinitive.",
                ),
                ChallengeEntity(
                    id = 70046, lessonId = 604, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each sentence to what it means",
                    orderIndex = 6,
                ),

                ChallengeEntity(
                    id = 70050, lessonId = 605, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/me_levanto_todos_los_dias.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.reflexive.pronoun",
                    ruleText = "A reflexive verb sends the action back at the subject, so it needs the reflexive pronoun: me levanto todos los días.\nte levanto would mean the speaker gets you up, not himself; me levanta is third person, so the me has nothing to agree with; me levantar is the infinitive, which fills no slot at all.",
                ),
                ChallengeEntity(
                    id = 70051, lessonId = 605, type = ChallengeType.CONJUGATE,
                    question = "Which reflexive pronoun goes with 'ducharse' when the subject is 'yo'?",
                    audioSrc = "asset:///audio/es/me.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "es.reflexive.pronoun",
                    ruleText = "Each subject has its own reflexive pronoun, and against yo that is me: yo me ducho.\nte is tú, so te ducho would say the speaker washes you; se is él/ella/usted; nos is nosotros, so nos ducho would put a plural speaker in a first person sentence.",
                ),
                ChallengeEntity(
                    id = 70052, lessonId = 605, type = ChallengeType.FILL_BLANK,
                    question = "Mi hermana ___ peina todas las mañanas.",
                    audioSrc = "asset:///audio/es/mi_hermana_se_peina_todas_las_mananas.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "es.reflexive.pronoun",
                    acceptedAnswers = "se",
                    ruleText = "When the subject and the object are the same person, the object is a reflexive pronoun, and a third person subject takes se: mi hermana se peina.\nme would make the speaker do the combing; le would make somebody else the object; peina without a pronoun leaves the action with no object at all.",
                ),
                ChallengeEntity(
                    id = 70053, lessonId = 605, type = ChallengeType.SELECT,
                    question = "Which one means 'She dressed herself' (one woman)?",
                    audioSrc = "asset:///audio/es/ella_se_vistio.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "es.reflexive.pronoun",
                    ruleText = "Vestirse is reflexive, so with a third person subject the pronoun is se: ella se vistió.\nla vistió would make the dress the object of a different verb; ella vistió has no reflexive pronoun, so the action has no object; ella se vistieron is plural, and the sentence is about one woman.",
                ),
                ChallengeEntity(
                    id = 70054, lessonId = 605, type = ChallengeType.CONJUGATE,
                    question = "Which form goes in 'Se ___ español en todo el mundo' (impersonal se)?",
                    audioSrc = "asset:///audio/es/habla.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "es.reflexive.impersonal_se",
                    ruleText = "Impersonal se stands for 'la gente' in the third person singular, so the verb is habla and never carries a subject of its own.\nhablan is the plural, and impersonal se is singular unless the object it names is plural; hablo is the first person, which would put a speaker inside an impersonal sentence; hablar is the infinitive, which nothing here governs.",
                ),
                ChallengeEntity(
                    id = 70055, lessonId = 605, type = ChallengeType.FILL_BLANK,
                    question = "En este país se ___ muchos idiomas.",
                    audioSrc = "asset:///audio/es/en_este_pais_se_hablan_muchos_idiomas.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "es.reflexive.impersonal_se",
                    acceptedAnswers = "hablan",
                    ruleText = "When impersonal se carries a plural object, the verb agrees with that object rather than with se: en este país se hablan muchos idiomas.\nhabla is singular, which only fits a single language; hablamos is the first person plural, and impersonal sentences have no speaker; hablar is the infinitive, which se cannot govern.",
                ),
                ChallengeEntity(
                    id = 70056, lessonId = 605, type = ChallengeType.STORY,
                    question = "La mañana de Marta\n\nMarta se levanta a las siete.\nSe ducha enseguida y se viste deprisa.\nSale de casa a las ocho menos cuarto.\n\n❓ What does Marta do first?",
                    audioSrc = "asset:///audio/es/la_manana_de_marta.ogg",
                    orderIndex = 6,
                ),

                ChallengeEntity(
                    id = 70060, lessonId = 606, type = ChallengeType.CONJUGATE,
                    question = "Which form of escuchar do you use to tell one person 'Listen' (affirmative, tú)?",
                    audioSrc = "asset:///audio/es/escucha.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "es.imperative.affirmative",
                    ruleText = "The affirmative tú imperative is the present form with the final -s dropped, so escuchar → escucha.\nescuchas is the present indicative, which states a habit rather than giving an order; escuchar is the infinitive, which commands nobody; escuchad is the vosotros command, and the sentence is addressed to one person.",
                ),
                ChallengeEntity(
                    id = 70061, lessonId = 606, type = ChallengeType.CONJUGATE,
                    question = "Which form do you use to tell one person 'Come here' (affirmative, tú)?",
                    audioSrc = "asset:///audio/es/ven.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "es.imperative.irregular",
                    ruleText = "Irregular affirmative tú imperatives are ven, pon, sal, ten, haz, di and ve: venir loses its -ir and keeps ven.\nvienes is the present indicative, which reports a habit instead of ordering; venir is the infinitive, which commands nobody; venid is the vosotros command, and the sentence is addressed to one person.",
                ),
                ChallengeEntity(
                    id = 70062, lessonId = 606, type = ChallengeType.FILL_BLANK,
                    question = "No ___ con prisa.",
                    audioSrc = "asset:///audio/es/no_corras_con_prisa.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "es.imperative.negative",
                    acceptedAnswers = "corras",
                    ruleText = "The negative tú imperative is no + the subjunctive, so no corras con prisa.\ncorres is the present indicative, which denies a fact instead of forbidding an action; correr is the infinitive, which no cannot govern; corráis is the subjunctive of vosotros, and the sentence is addressed to one person.",
                ),
                ChallengeEntity(
                    id = 70063, lessonId = 606, type = ChallengeType.SELECT,
                    question = "Which one means 'Sit down, please' (to a person you address as usted)?",
                    audioSrc = "asset:///audio/es/sientese_por_favor.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "es.imperative.affirmative",
                    ruleText = "The usted command is the third person present with the -s dropped, and a reflexive pronoun turns into -se: siéntese, por favor.\nsiéntate is the tú form, which is grammatically a command but at the wrong level of politeness for a person addressed as usted; siéntense is the ustedes form, and there is one person; sentarse is the infinitive, which commands nobody.",
                ),
                ChallengeEntity(
                    id = 70064, lessonId = 606, type = ChallengeType.FILL_BLANK,
                    question = "Por favor, ___ aquí.",
                    audioSrc = "asset:///audio/es/por_favor_sientate_aqui.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "es.imperative.affirmative",
                    acceptedAnswers = "siéntate",
                    ruleText = "Sentarse is reflexive, and the affirmative tú command attaches the reflexive pronoun to the verb as -ate, so the whole word is siéntate: por favor, siéntate aquí.\nsiéntese is the usted form, which is grammatically a command but at the wrong level of politeness for a friend; siéntense is the ustedes form, and there is one person; sentarse is the infinitive, which commands nobody.",
                ),
                ChallengeEntity(
                    id = 70065, lessonId = 606, type = ChallengeType.CONJUGATE,
                    question = "Which form of dormir do you use to tell one person 'Sleep well' (affirmative, tú)?",
                    audioSrc = "asset:///audio/es/duerme.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "es.imperative.affirmative",
                    ruleText = "The affirmative tú imperative of dormir is the present form without the -s: duerme.\nduermes is the present indicative, which states a habit instead of giving an order; dormir is the infinitive, which commands nobody; duerman is the ustedes command, and the sentence is addressed to one person.",
                ),
                ChallengeEntity(
                    id = 70066, lessonId = 606, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/no_digas_eso_por_favor.ogg",
                    orderIndex = 6,
                    grammaticalFocus = "es.imperative.negative",
                    ruleText = "The negative tú imperative is no + the subjunctive, and the tú form of decir is digas: no digas eso, por favor.\nno dices is the present indicative, which denies a fact instead of forbidding the action; decir is the infinitive, which no cannot govern; no digo is the first person, which forbids the speaker from speaking to himself.",
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 700300, challengeId = 70030, text = "lo", correct = true),
                ChallengeOptionEntity(id = 700301, challengeId = 70030, text = "la", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700302, challengeId = 70030, text = "me", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700303, challengeId = 70030, text = "el", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700310, challengeId = 70031, text = "Lo", correct = true),
                ChallengeOptionEntity(id = 700311, challengeId = 70031, text = "La", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700312, challengeId = 70031, text = "Los", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700313, challengeId = 70031, text = "Ver", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700320, challengeId = 70032, text = "La veo en la puerta", correct = true, audioSrc = "asset:///audio/es/la_veo_en_la_puerta.ogg"),
                ChallengeOptionEntity(id = 700321, challengeId = 70032, text = "Lo veo en la puerta", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700322, challengeId = 70032, text = "Las veo en la puerta", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700323, challengeId = 70032, text = "La ver en la puerta", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700330, challengeId = 70033, text = "Los veo en el parque", correct = true),
                ChallengeOptionEntity(id = 700331, challengeId = 70033, text = "Veo los en el parque", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700332, challengeId = 70033, text = "La veo en el parque", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700333, challengeId = 70033, text = "Nos vemos en el parque", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700340, challengeId = 70034, text = "Compro", correct = true),
                ChallengeOptionEntity(id = 700341, challengeId = 70034, text = "los", correct = true),
                ChallengeOptionEntity(id = 700342, challengeId = 70034, text = "boletos", correct = true),
                ChallengeOptionEntity(id = 700343, challengeId = 70034, text = "la", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700344, challengeId = 70034, text = "lo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700345, challengeId = 70034, text = "veo", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 700350, challengeId = 70035, text = "lo", correct = true),
                ChallengeOptionEntity(id = 700351, challengeId = 70035, text = "el libro", correct = true),
                ChallengeOptionEntity(id = 700352, challengeId = 70035, text = "la", correct = true),
                ChallengeOptionEntity(id = 700353, challengeId = 70035, text = "la casa", correct = true),
                ChallengeOptionEntity(id = 700354, challengeId = 70035, text = "los", correct = true),
                ChallengeOptionEntity(id = 700355, challengeId = 70035, text = "los zapatos", correct = true),

                ChallengeOptionEntity(id = 700400, challengeId = 70040, text = "le", correct = true),
                ChallengeOptionEntity(id = 700401, challengeId = 70040, text = "lo", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700402, challengeId = 70040, text = "la", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700403, challengeId = 70040, text = "les", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700410, challengeId = 70041, text = "Le", correct = true),
                ChallengeOptionEntity(id = 700411, challengeId = 70041, text = "Los", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700412, challengeId = 70041, text = "Lo", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700413, challengeId = 70041, text = "Me", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 700420, challengeId = 70042, text = "A Marta le gusta el café", correct = true),
                ChallengeOptionEntity(id = 700421, challengeId = 70042, text = "A Marta gusta el café", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700422, challengeId = 70042, text = "A Marta le gustan el café", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700423, challengeId = 70042, text = "A Marta le gusta los cafés", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 700430, challengeId = 70043, text = "gustan", correct = true),
                ChallengeOptionEntity(id = 700431, challengeId = 70043, text = "gusta", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700432, challengeId = 70043, text = "gusto", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700433, challengeId = 70043, text = "gustar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700440, challengeId = 70044, text = "gustan", correct = true),
                ChallengeOptionEntity(id = 700441, challengeId = 70044, text = "gusta", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700442, challengeId = 70044, text = "gustar", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700443, challengeId = 70044, text = "gusto", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 700450, challengeId = 70045, text = "Nos gusta el mar", correct = true, audioSrc = "asset:///audio/es/nos_gusta_el_mar.ogg"),
                ChallengeOptionEntity(id = 700451, challengeId = 70045, text = "Nos gustan el mar", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700452, challengeId = 70045, text = "Nos gustamos el mar", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700453, challengeId = 70045, text = "Nos gustar el mar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700460, challengeId = 70046, text = "A Marta le gusta el café", correct = true),
                ChallengeOptionEntity(id = 700461, challengeId = 70046, text = "Marta likes coffee", correct = true),
                ChallengeOptionEntity(id = 700462, challengeId = 70046, text = "Nos gusta el mar", correct = true),
                ChallengeOptionEntity(id = 700463, challengeId = 70046, text = "We like the sea", correct = true),
                ChallengeOptionEntity(id = 700464, challengeId = 70046, text = "Me levanto todos los días", correct = true),
                ChallengeOptionEntity(id = 700465, challengeId = 70046, text = "I get up every day", correct = true),

                ChallengeOptionEntity(id = 700500, challengeId = 70050, text = "Me levanto todos los días", correct = true),
                ChallengeOptionEntity(id = 700501, challengeId = 70050, text = "Te levanto todos los días", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700502, challengeId = 70050, text = "Me levanta todos los días", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700503, challengeId = 70050, text = "Me levantar todos los días", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700510, challengeId = 70051, text = "me", correct = true),
                ChallengeOptionEntity(id = 700511, challengeId = 70051, text = "te", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700512, challengeId = 70051, text = "se", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700513, challengeId = 70051, text = "nos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 700520, challengeId = 70052, text = "se", correct = true),
                ChallengeOptionEntity(id = 700521, challengeId = 70052, text = "me", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700522, challengeId = 70052, text = "le", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700523, challengeId = 70052, text = "Peina", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700530, challengeId = 70053, text = "Ella se vistió", correct = true, audioSrc = "asset:///audio/es/ella_se_vistio.ogg"),
                ChallengeOptionEntity(id = 700531, challengeId = 70053, text = "La vistió", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700532, challengeId = 70053, text = "Ella vistió", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700533, challengeId = 70053, text = "Ella se vistieron", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 700540, challengeId = 70054, text = "habla", correct = true),
                ChallengeOptionEntity(id = 700541, challengeId = 70054, text = "hablan", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700542, challengeId = 70054, text = "hablo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700543, challengeId = 70054, text = "hablar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700550, challengeId = 70055, text = "hablan", correct = true),
                ChallengeOptionEntity(id = 700551, challengeId = 70055, text = "habla", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700552, challengeId = 70055, text = "hablamos", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700553, challengeId = 70055, text = "hablar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700560, challengeId = 70056, text = "She gets up", correct = true),
                ChallengeOptionEntity(id = 700561, challengeId = 70056, text = "She takes the bus", correct = false),
                ChallengeOptionEntity(id = 700562, challengeId = 70056, text = "She has breakfast", correct = false),
                ChallengeOptionEntity(id = 700563, challengeId = 70056, text = "She goes to bed early", correct = false),

                ChallengeOptionEntity(id = 700600, challengeId = 70060, text = "escucha", correct = true),
                ChallengeOptionEntity(id = 700601, challengeId = 70060, text = "escuchas", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700602, challengeId = 70060, text = "escuchad", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700603, challengeId = 70060, text = "escuchar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700610, challengeId = 70061, text = "ven", correct = true),
                ChallengeOptionEntity(id = 700611, challengeId = 70061, text = "vienes", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700612, challengeId = 70061, text = "venid", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700613, challengeId = 70061, text = "venir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700620, challengeId = 70062, text = "corras", correct = true),
                ChallengeOptionEntity(id = 700621, challengeId = 70062, text = "corres", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700622, challengeId = 70062, text = "correr", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700623, challengeId = 70062, text = "corráis", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 700630, challengeId = 70063, text = "Siéntese, por favor", correct = true, audioSrc = "asset:///audio/es/sientese_por_favor.ogg"),
                ChallengeOptionEntity(id = 700631, challengeId = 70063, text = "Siéntate, por favor", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 700632, challengeId = 70063, text = "Siéntense, por favor", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700633, challengeId = 70063, text = "Sentarse, por favor", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700640, challengeId = 70064, text = "siéntate", correct = true),
                ChallengeOptionEntity(id = 700641, challengeId = 70064, text = "siéntese", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 700642, challengeId = 70064, text = "siéntense", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 700643, challengeId = 70064, text = "sentarse", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 700650, challengeId = 70065, text = "duerme", correct = true),
                ChallengeOptionEntity(id = 700651, challengeId = 70065, text = "duermes", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700652, challengeId = 70065, text = "dormir", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700653, challengeId = 70065, text = "duerman", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 700660, challengeId = 70066, text = "No digas eso, por favor", correct = true),
                ChallengeOptionEntity(id = 700661, challengeId = 70066, text = "No dices eso, por favor", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700662, challengeId = 70066, text = "No decir eso, por favor", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 700663, challengeId = 70066, text = "No digo eso, por favor", correct = false, errorTag = "WRONG_PERSON"),
            ),
        ),
    )

    // =========================================================================
    // JAPANESE JLPT N4 (Units 9 - 10)
    // =========================================================================
    val japaneseN4Units: List<UnitPayload> = listOf(
        // Unit 9: Te-form & Requests (~てください / ~ています)
        UnitPayload(
            unit = UnitEntity(
                id = 28,
                courseId = 2,
                title = "Unit 9: Te-form & Requests",
                description = "Connect verbs, describe ongoing actions, and make polite requests",
                orderIndex = 8,
            ),
            lessons = listOf(
                LessonEntity(id = 215, unitId = 28, title = "Lesson 16: Wait, Please", orderIndex = 0),
                LessonEntity(id = 216, unitId = 28, title = "Lesson 17: Asking Directions", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(
                    id = 2081, lessonId = 215, type = ChallengeType.SELECT,
                    question = "How do you say 'Wait a moment, please'?",
                    audioSrc = "asset:///audio/ja/chotto_matte.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.request_polite",
                    ruleText = "A polite request is the te-form of the verb plus ください: 待ちます → 待って + ください.\n待て is the plain imperative and 待とう is 'let me wait' — neither is a request in the polite てください form.",
                ),
                ChallengeEntity(
                    id = 2082, lessonId = 215, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'Wait a moment, please'",
                    orderIndex = 1,
                    grammaticalFocus = "ja.request_polite",
                    ruleText = "A polite request is the te-form plus ください: ちょっと待ってください.\nDropping ください leaves the bare te-form 待って, and まってます is the polite progressive 'I am waiting' — a statement, not a request.",
                ),
                ChallengeEntity(
                    id = 2083, lessonId = 215, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/chotto_matte.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.request_polite",
                    ruleText = "Look for the -って of the te-form and the ください after it.\n待っています is 'I am waiting' and 待て is the plain imperative: same verb, different ending, different meaning.",
                ),
                ChallengeEntity(
                    id = 30020, lessonId = 215, type = ChallengeType.CONJUGATE,
                    question = "Which te-form (て形) of 待つ goes with ください in 「ちょっと待ってください」?",
                    orderIndex = 3,
                    grammaticalFocus = "ja.request_polite",
                    ruleText = "A godan verb forms the te-form by changing the final -う: 待つ → 待って.\nThat て is what ください attaches to. 待て is the plain imperative, 待とう the volitional, and 待っている the progressive.",
                ),
                ChallengeEntity(
                    id = 30021, lessonId = 215, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'ちょっと ___ ください' — the polite request frame",
                    orderIndex = 4,
                    grammaticalFocus = "ja.request_polite",
                    ruleText = "The てください slot takes the te-form, not the polite ます form: ちょっと待ってください.\n待ちます would be a statement ('I will wait'), and 待て/待とう are the plain imperative and volitional.",
                ),
                ChallengeEntity(
                    id = 30022, lessonId = 215, type = ChallengeType.FILL_BLANK,
                    question = "ちょっと待って___。",
                    orderIndex = 5,
                    grammaticalFocus = "ja.request_polite",
                    acceptedAnswers = "ください",
                    ruleText = "The polite request ending is ください, attached straight onto the te-form: 待ってください.\nます would leave the polite ending on its own, and ました is the polite past ('I did wait').",
                ),

                ChallengeEntity(
                    id = 2084, lessonId = 216, type = ChallengeType.SELECT,
                    question = "How do you say 'Turn right, please'?",
                    audioSrc = "asset:///audio/ja/migi_ni_magatte.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.request_polite",
                    ruleText = "曲がります → 曲がって + ください = 右に曲がってください.\n曲がっています is 'I am turning' and 曲がって on its own is the plain te-form — the request needs ください.",
                ),
                ChallengeEntity(
                    id = 2085, lessonId = 216, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'I'm reading a book right now'",
                    orderIndex = 1,
                    grammaticalFocus = "ja.te_form",
                    ruleText = "～ています is the polite progressive: it needs the te-form of the verb plus います. 読む → 読んで + います.\n読んでます is the contracted casual form, and 読んでいました is the polite past progressive.",
                ),
                ChallengeEntity(
                    id = 2086, lessonId = 216, type = ChallengeType.SELECT,
                    question = "Which one means 'I'm reading a book right now'?",
                    audioSrc = "asset:///audio/ja/ima_tabete_imasu.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.te_form",
                    ruleText = "～ています is built on the te-form: 読んで + います = 読んでいます.\n読む drops the te-form entirely and 読んでいた is the plain past progressive.",
                ),
                ChallengeEntity(
                    id = 2087, lessonId = 216, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the te-form phrases",
                    orderIndex = 3,
                ),
                ChallengeEntity(
                    id = 30023, lessonId = 216, type = ChallengeType.CONJUGATE,
                    question = "Which te-form (て形) of 曲がる goes with ください in 「右に曲がってください」?",
                    orderIndex = 4,
                    grammaticalFocus = "ja.request_polite",
                    ruleText = "An ichidan verb forms the te-form by dropping る: 曲がる → 曲がって.\n曲がる is the dictionary form, 曲がら is not a form of this verb, and 曲がっています is the progressive.",
                ),
                ChallengeEntity(
                    id = 30024, lessonId = 216, type = ChallengeType.FILL_BLANK,
                    question = "まっすぐ___ください。",
                    orderIndex = 5,
                    grammaticalFocus = "ja.request_polite",
                    acceptedAnswers = "行って|いって",
                    ruleText = "行く is an irregular: its te-form is 行って, not 行いて.\n行く is the dictionary form and 行きます the plain polite — neither can attach to ください.",
                ),
                ChallengeEntity(
                    id = 31010, lessonId = 216, type = ChallengeType.CONJUGATE,
                    question = "Which te-form (て形) of 聞く goes with ください?",
                    orderIndex = 6,
                    heldOut = true,
                    grammaticalFocus = "ja.request_polite",
                    ruleText = "A godan verb forms the te-form by changing the final -く to いて: 聞く → 聞いて.\n聞く is the dictionary form, 聞き the stem, and 聞いています the progressive.",
                ),
                ChallengeEntity(
                    id = 31011, lessonId = 216, type = ChallengeType.FILL_BLANK,
                    question = "もう一度___ください。",
                    orderIndex = 7,
                    heldOut = true,
                    grammaticalFocus = "ja.request_polite",
                    acceptedAnswers = "言って",
                    ruleText = "言う is an irregular: its te-form is 言って, not 言いて.\n言う is the dictionary form and 言います the plain polite — neither attaches to ください.",
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 20306, challengeId = 2081, text = "ちょっと待ってください", romaji = "chotto matte kudasai", correct = true, audioSrc = "asset:///audio/ja/chotto_matte.ogg"),
                ChallengeOptionEntity(id = 20307, challengeId = 2081, text = "ちょっと待っています", romaji = "chotto matte imasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 20308, challengeId = 2081, text = "ちょっと待て", romaji = "chotto matte", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 20309, challengeId = 2082, text = "ちょっと", romaji = "chotto", correct = true),
                ChallengeOptionEntity(id = 20310, challengeId = 2082, text = "まって", romaji = "matte", correct = true),
                ChallengeOptionEntity(id = 20311, challengeId = 2082, text = "ください", romaji = "kudasai", correct = true),
                ChallengeOptionEntity(id = 20312, challengeId = 2082, text = "まってます", romaji = "mattemasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 300010, challengeId = 2082, text = "まちます", romaji = "machimasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 20313, challengeId = 2083, text = "ちょっと待ってください", romaji = "chotto matte kudasai", correct = true),
                ChallengeOptionEntity(id = 20314, challengeId = 2083, text = "ちょっと待っています", romaji = "chotto matte imasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 20315, challengeId = 2083, text = "ちょっと待て", romaji = "chotto matte", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 300200, challengeId = 30020, text = "待って", romaji = "matte", correct = true),
                ChallengeOptionEntity(id = 300201, challengeId = 30020, text = "待て", romaji = "mate", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 300202, challengeId = 30020, text = "待とう", romaji = "matou", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 300203, challengeId = 30020, text = "待っています", romaji = "matte imasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 300204, challengeId = 30021, text = "ちょっと", romaji = "chotto", correct = true),
                ChallengeOptionEntity(id = 300205, challengeId = 30021, text = "待って", romaji = "matte", correct = true),
                ChallengeOptionEntity(id = 300206, challengeId = 30021, text = "ください", romaji = "kudasai", correct = true),
                ChallengeOptionEntity(id = 300207, challengeId = 30021, text = "待て", romaji = "mate", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 300208, challengeId = 30021, text = "待とう", romaji = "matou", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 300209, challengeId = 30021, text = "待ちます", romaji = "machimasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 300210, challengeId = 30022, text = "ください", romaji = "kudasai", correct = true),
                ChallengeOptionEntity(id = 300211, challengeId = 30022, text = "ます", romaji = "masu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 300212, challengeId = 30022, text = "ました", romaji = "mashita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 20316, challengeId = 2084, text = "右に曲がってください", romaji = "migi ni magatte kudasai", correct = true, audioSrc = "asset:///audio/ja/migi_ni_magatte.ogg"),
                ChallengeOptionEntity(id = 20317, challengeId = 2084, text = "右に曲がっています", romaji = "migi ni magatte imasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 20318, challengeId = 2084, text = "右に曲がって", romaji = "migi ni magatte", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 20319, challengeId = 2085, text = "今", romaji = "ima", correct = true),
                ChallengeOptionEntity(id = 20320, challengeId = 2085, text = "本を", romaji = "hon o", correct = true),
                ChallengeOptionEntity(id = 20321, challengeId = 2085, text = "読んで", romaji = "yonde", correct = true),
                ChallengeOptionEntity(id = 20322, challengeId = 2085, text = "います", romaji = "imasu", correct = true),
                ChallengeOptionEntity(id = 20323, challengeId = 2085, text = "読んでます", romaji = "yondemasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 300011, challengeId = 2085, text = "読んでいました", romaji = "yonde imashita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 20324, challengeId = 2086, text = "今、本を読んでいます", romaji = "ima hon o yonde imasu", correct = true, audioSrc = "asset:///audio/ja/ima_tabete_imasu.ogg"),
                ChallengeOptionEntity(id = 20325, challengeId = 2086, text = "今、本を読む", romaji = "ima hon o yomu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 20326, challengeId = 2086, text = "今、本を読んでいた", romaji = "ima hon o yonde ita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 20327, challengeId = 2087, text = "待ってください", romaji = "matte kudasai", correct = true),
                ChallengeOptionEntity(id = 20328, challengeId = 2087, text = "Wait, please", correct = true),
                ChallengeOptionEntity(id = 20329, challengeId = 2087, text = "曲がってください", romaji = "magatte kudasai", correct = true),
                ChallengeOptionEntity(id = 20330, challengeId = 2087, text = "Turn, please", correct = true),
                ChallengeOptionEntity(id = 20331, challengeId = 2087, text = "読んでいます", romaji = "yonde imasu", correct = true),
                ChallengeOptionEntity(id = 20332, challengeId = 2087, text = "I'm reading", correct = true),

                ChallengeOptionEntity(id = 300213, challengeId = 30023, text = "曲がって", romaji = "magatte", correct = true),
                ChallengeOptionEntity(id = 300214, challengeId = 30023, text = "曲がる", romaji = "magaru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 300215, challengeId = 30023, text = "曲がら", romaji = "magara", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 300216, challengeId = 30023, text = "曲がっています", romaji = "magatte imasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 300217, challengeId = 30024, text = "行って", romaji = "itte", correct = true),
                ChallengeOptionEntity(id = 300218, challengeId = 30024, text = "行く", romaji = "iku", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 300219, challengeId = 30024, text = "行きます", romaji = "ikimasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 310100, challengeId = 31010, text = "聞いて", romaji = "kite", correct = true),
                ChallengeOptionEntity(id = 310101, challengeId = 31010, text = "聞く", romaji = "kiku", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 310102, challengeId = 31010, text = "聞き", romaji = "kiki", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 310103, challengeId = 31010, text = "聞いています", romaji = "kite imasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 310104, challengeId = 31011, text = "言って", romaji = "iitte", correct = true),
                ChallengeOptionEntity(id = 310105, challengeId = 31011, text = "言う", romaji = "iu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 310106, challengeId = 31011, text = "言います", romaji = "iimasu", correct = false, errorTag = "WRONG_FORM"),
            ),
        ),

        // Unit 10: Potential & Ability (~ことができる / ~れる)
        UnitPayload(
            unit = UnitEntity(
                id = 29,
                courseId = 2,
                title = "Unit 10: Potential & Ability",
                description = "Can/can't do: languages, skills (日本語が話せます, 書くことができます)",
                orderIndex = 9,
            ),
            lessons = listOf(
                LessonEntity(id = 217, unitId = 29, title = "Lesson 18: I Can Speak", orderIndex = 0),
                LessonEntity(id = 218, unitId = 29, title = "Lesson 19: I Can Write", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(
                    id = 2088, lessonId = 217, type = ChallengeType.SELECT,
                    question = "How do you say 'I can speak a little Japanese'?",
                    audioSrc = "asset:///audio/ja/nihongo_ga_hanasemasu.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.potential",
                    ruleText = "話す → 話せます: an ichidan verb drops る and takes the ます-stem plus える. The potential is what says 'can'.\n話します is 'I speak' and 話せました is 'I was able to speak' — right ability, wrong tense.",
                ),
                ChallengeEntity(
                    id = 2089, lessonId = 217, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'I can speak a little Japanese'",
                    orderIndex = 1,
                    grammaticalFocus = "ja.potential",
                    ruleText = "話す → 話せます: an ichidan verb drops る and takes the ます-stem plus える.\n話します is the plain 'I speak' and 話せました is the polite past 'I was able to speak'. Only 話せます says 'I can'.",
                ),
                ChallengeEntity(
                    id = 2090, lessonId = 217, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/nihongo_ga_hanasemasu.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.potential",
                    ruleText = "The particle after 日本語 is が (the thing you can do), and the verb ends in せます.\n日本語を would mark something you do to the language; 話します drops the potential suffix entirely.",
                ),
                ChallengeEntity(
                    id = 30025, lessonId = 217, type = ChallengeType.CONJUGATE,
                    question = "Which form of 話す means 'I can speak'?",
                    orderIndex = 3,
                    grammaticalFocus = "ja.potential",
                    ruleText = "話す → 話せます: an ichidan verb drops る and takes the ます-stem plus える.\n話します is the plain present, 話せました the polite past, 話した the plain past — none of them says 'can'.",
                ),
                ChallengeEntity(
                    id = 30026, lessonId = 217, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 「今、本を___います。」— the polite progressive frame",
                    orderIndex = 4,
                    grammaticalFocus = "ja.polite_verb",
                    ruleText = "～ています takes the te-form of ます: 読んで + います.\n読んでます is the contracted casual form, 読んでいた the plain past progressive, and 読ん is a stem with the okurigana missing.",
                ),
                ChallengeEntity(
                    id = 30027, lessonId = 217, type = ChallengeType.FILL_BLANK,
                    question = "私は日本語が___。",
                    orderIndex = 5,
                    grammaticalFocus = "ja.potential",
                    acceptedAnswers = "話せます|はなせます",
                    ruleText = "話す → 話せます: drop る, keep the ます-stem はな, add せる.\n話します is the plain present and 話せました the polite past — neither carries the potential.",
                ),

                ChallengeEntity(
                    id = 2091, lessonId = 218, type = ChallengeType.SELECT,
                    question = "How do you say 'I can write kanji'?",
                    audioSrc = "asset:///audio/ja/kanji_o_kaku_koto_ga_dekimasu.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.potential_nominal",
                    ruleText = "The noun + を + verb-stem + ことが できます frame names an ability: 書くことができます 'I can write'.\n書きます is the plain 'I write' and 書けます is the short potential — neither fills the ことが できます slot.",
                ),
                ChallengeEntity(
                    id = 2092, lessonId = 218, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'I can write kanji'",
                    orderIndex = 1,
                    grammaticalFocus = "ja.potential_nominal",
                    ruleText = "The ability frame is 書く + ことが + できます: the verb loses る and becomes a noun phrase.\n書きます is a plain sentence and 書きました is its past — neither fits before ことができます.",
                ),
                ChallengeEntity(
                    id = 2093, lessonId = 218, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the ability words",
                    orderIndex = 2,
                ),
                ChallengeEntity(
                    id = 30028, lessonId = 218, type = ChallengeType.CONJUGATE,
                    question = "Which potential form of 書きます means 'I can write'?",
                    orderIndex = 3,
                    grammaticalFocus = "ja.potential",
                    ruleText = "書きます → 書けます: an ichidan verb drops る and takes the ます-stem plus える.\n書きます is the plain present, 書けました the polite past, 書きたい 'I want to write' — only 書けます says 'I can'.",
                ),
                ChallengeEntity(
                    id = 30029, lessonId = 218, type = ChallengeType.FILL_BLANK,
                    question = "漢字を___ことができます。",
                    orderIndex = 4,
                    grammaticalFocus = "ja.potential_nominal",
                    acceptedAnswers = "書く|かく",
                    ruleText = "Before ことができます the verb becomes a noun: 書く (from 書きます, drop ます and add る).\n書きます is the plain verb form and 書きたい is 'I want to write' — neither can head the ことができます frame.",
                ),
                ChallengeEntity(
                    id = 31012, lessonId = 218, type = ChallengeType.CONJUGATE,
                    question = "Which potential form of 読む means 'I can read'?",
                    orderIndex = 5,
                    heldOut = true,
                    grammaticalFocus = "ja.potential",
                    ruleText = "読む → 読めます: an ichidan verb drops る and takes the ます-stem plus える.\n読みます is the plain present, 読めませんでした the polite past negative, 読む the dictionary form.",
                ),
                ChallengeEntity(
                    id = 31013, lessonId = 218, type = ChallengeType.FILL_BLANK,
                    question = "ひらがなを___ことができます。",
                    orderIndex = 6,
                    heldOut = true,
                    grammaticalFocus = "ja.potential_nominal",
                    acceptedAnswers = "読める|よめる",
                    ruleText = "Before ことができます the verb becomes a noun, here the potential stem 読める (from 読めます).\n読みます is the plain verb form and 読む the dictionary form — neither can head the ことができます frame.",
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 20333, challengeId = 2088, text = "少し日本語が話せます", romaji = "sukoshi nihongo ga hanasemasu", correct = true, audioSrc = "asset:///audio/ja/nihongo_ga_hanasemasu.ogg"),
                ChallengeOptionEntity(id = 20334, challengeId = 2088, text = "少し日本語が話します", romaji = "sukoshi nihongo ga hanashimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 20335, challengeId = 2088, text = "少し日本語が話せませんでした", romaji = "sukoshi nihongo ga hanasemasen deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 20336, challengeId = 2089, text = "少し", romaji = "sukoshi", correct = true),
                ChallengeOptionEntity(id = 20337, challengeId = 2089, text = "日本語が", romaji = "nihongo ga", correct = true),
                ChallengeOptionEntity(id = 20338, challengeId = 2089, text = "話せます", romaji = "hanasemasu", correct = true),
                ChallengeOptionEntity(id = 20339, challengeId = 2089, text = "話せました", romaji = "hanasemashita", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300012, challengeId = 2089, text = "話します", romaji = "hanashimasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 20340, challengeId = 2090, text = "日本語が話せます", romaji = "nihongo ga hanasemasu", correct = true),
                ChallengeOptionEntity(id = 20341, challengeId = 2090, text = "日本語を話せます", romaji = "nihongo o hanasemasu", correct = false),
                ChallengeOptionEntity(id = 20342, challengeId = 2090, text = "日本語が話します", romaji = "nihongo ga hanashimasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 300220, challengeId = 30025, text = "話せます", romaji = "hanasemasu", correct = true),
                ChallengeOptionEntity(id = 300221, challengeId = 30025, text = "話します", romaji = "hanashimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 300222, challengeId = 30025, text = "話せました", romaji = "hanasemashita", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300223, challengeId = 30025, text = "話した", romaji = "hanashita", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 300224, challengeId = 30026, text = "今", romaji = "ima", correct = true),
                ChallengeOptionEntity(id = 300225, challengeId = 30026, text = "本を", romaji = "hon o", correct = true),
                ChallengeOptionEntity(id = 300226, challengeId = 30026, text = "読んで", romaji = "yonde", correct = true),
                ChallengeOptionEntity(id = 300227, challengeId = 30026, text = "います", romaji = "imasu", correct = true),
                ChallengeOptionEntity(id = 300228, challengeId = 30026, text = "読んでます", romaji = "yondemasu", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 300229, challengeId = 30026, text = "読んでいた", romaji = "yonde ita", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300230, challengeId = 30026, text = "読ん", romaji = "yon", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 300231, challengeId = 30027, text = "話せます", romaji = "hanasemasu", correct = true),
                ChallengeOptionEntity(id = 300232, challengeId = 30027, text = "話します", romaji = "hanashimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 300233, challengeId = 30027, text = "話せました", romaji = "hanasemashita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 20343, challengeId = 2091, text = "漢字を書くことができます", romaji = "kanji o kaku koto ga dekimasu", correct = true, audioSrc = "asset:///audio/ja/kanji_o_kaku_koto_ga_dekimasu.ogg"),
                ChallengeOptionEntity(id = 20344, challengeId = 2091, text = "漢字を書きます", romaji = "kanji o kakimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 20345, challengeId = 2091, text = "漢字が書くことができます", romaji = "kanji ga kaku koto ga dekimasu", correct = false),

                ChallengeOptionEntity(id = 20346, challengeId = 2092, text = "漢字", romaji = "kanji", correct = true),
                ChallengeOptionEntity(id = 20347, challengeId = 2092, text = "を", romaji = "o", correct = true),
                ChallengeOptionEntity(id = 20348, challengeId = 2092, text = "書くことが", romaji = "kaku koto ga", correct = true),
                ChallengeOptionEntity(id = 20349, challengeId = 2092, text = "できます", romaji = "dekimasu", correct = true),
                ChallengeOptionEntity(id = 20350, challengeId = 2092, text = "書きます", romaji = "kakimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 300013, challengeId = 2092, text = "書きました", romaji = "kakimashita", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 20351, challengeId = 2093, text = "話せます", romaji = "hanasemasu", correct = true),
                ChallengeOptionEntity(id = 20352, challengeId = 2093, text = "Can speak", correct = true),
                ChallengeOptionEntity(id = 20353, challengeId = 2093, text = "書くことができます", romaji = "kaku koto ga dekimasu", correct = true),
                ChallengeOptionEntity(id = 20354, challengeId = 2093, text = "Can write", correct = true),
                ChallengeOptionEntity(id = 20355, challengeId = 2093, text = "待ってください", romaji = "matte kudasai", correct = true),
                ChallengeOptionEntity(id = 20356, challengeId = 2093, text = "Wait, please", correct = true),

                ChallengeOptionEntity(id = 300234, challengeId = 30028, text = "書けます", romaji = "kakemasu", correct = true),
                ChallengeOptionEntity(id = 300235, challengeId = 30028, text = "書きます", romaji = "kakimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 300236, challengeId = 30028, text = "書けました", romaji = "kakemashita", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 300237, challengeId = 30028, text = "書きたい", romaji = "kakaitai", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 300238, challengeId = 30029, text = "書く", romaji = "kaku", correct = true),
                ChallengeOptionEntity(id = 300239, challengeId = 30029, text = "書きます", romaji = "kakimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 300240, challengeId = 30029, text = "書きたい", romaji = "kakaitai", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 310200, challengeId = 31012, text = "読めます", romaji = "yomemasu", correct = true),
                ChallengeOptionEntity(id = 310201, challengeId = 31012, text = "読みます", romaji = "yomimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 310202, challengeId = 31012, text = "読めませんでした", romaji = "yomemasen deshita", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 310203, challengeId = 31012, text = "読む", romaji = "yomu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 310204, challengeId = 31013, text = "読める", romaji = "yomeru", correct = true),
                ChallengeOptionEntity(id = 310205, challengeId = 31013, text = "読みます", romaji = "yomimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 310206, challengeId = 31013, text = "読む", romaji = "yomu", correct = false, errorTag = "WRONG_FORM"),
            ),
        ),
    )
}
