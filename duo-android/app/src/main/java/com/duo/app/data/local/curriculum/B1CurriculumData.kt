package com.duo.app.data.local.curriculum

import com.duo.app.data.local.entities.ChallengeEntity
import com.duo.app.data.local.entities.ChallengeOptionEntity
import com.duo.app.data.local.entities.LessonEntity
import com.duo.app.data.local.entities.UnitEntity
import com.duo.app.data.local.models.ChallengeType

/**
 * The intermediate stage of the curriculum. The `B1` in this file's name is the
 * *roadmap workstream* — `docs/CURRICULUM_B1_N4_ROADMAP.md` — and not a claim about
 * the level of what is authored here. What has actually shipped is:
 *   Spanish: Units 9-10 (unit ids 18-19) — Pretérito Indefinido and Imperfecto, both
 *     **regular only**. There is no irregular preterite (tuve, pude, hice, dije,
 *     estuve, quise), no conditional, no subjunctive, no imperative, no object or
 *     reflexive pronouns and no por/para. That is **CEFR A2**, so these units are
 *     labelled A2 everywhere a learner can see them; the B1 material the roadmap
 *     wants is Units 11-12 and it is not authored.
 *   Japanese: Units 9-10 (unit ids 28-29) — Te-form & Requests, Potential & Ability.
 *     These are genuinely JLPT N4 grammar points, so N4 is the honest label.
 *
 * All audio assets are bundled Kokoro-82M Ogg files.
 *
 * Id layout inside this file:
 *   - `1xxxx` / `2xxxx`  the originally authored taught items
 *   - `3xxxx`           the WI-03/04/05/07 grammar items authored with the rule cards
 *   - `31xxx`           the WI-08 held-out checkpoint pool: same structures, unseen sentences.
 *                       These are seeded with everything else and carry `heldOut = true`, so
 *                       the lesson-path queries skip them and only a checkpoint can reach them.
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
