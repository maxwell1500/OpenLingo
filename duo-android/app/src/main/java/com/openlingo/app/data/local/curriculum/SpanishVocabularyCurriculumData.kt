package com.openlingo.app.data.local.curriculum

import com.openlingo.app.data.local.entities.ChallengeEntity
import com.openlingo.app.data.local.entities.ChallengeOptionEntity
import com.openlingo.app.data.local.entities.LessonEntity
import com.openlingo.app.data.local.entities.UnitEntity
import com.openlingo.app.data.local.models.ChallengeType

/**
 * The themed everyday vocabulary units, Spanish units 17-18 (unit ids 36-37).
 *
 * Unit 36 (lessons 900-902) teaches the body and health, the home room by room,
 * and food, shopping and the restaurant. Unit 37 (lessons 903-905) teaches
 * travel, transport and directions, the city, its places and the weather, and
 * work, jobs and the workplace. Both drill `es.vocab.everyday_life` and
 * `es.vocab.travel`: a themed unit puts the sentence frame right and the wrong
 * word in the slot, so a distractor is a wrong form (a word that cannot carry
 * the article, number or ending the slot needs), a wrong person or number, or
 * occasionally a wrong tense (pidió for pide). `UNRELATED` is deliberately
 * absent: a challenge that names a grammaticalFocus has told the learner the
 * grammar is what is being tested, and "a different noun" would contradict that.
 *
 * These live in their own object rather than at the tail of
 * `B1CurriculumData.spanishB1Units` for a mechanical reason: a Kotlin `object`
 * compiles its property initialisers into one static `<clinit>`, and the JVM caps
 * a method at 64 KB of bytecode. With units 36-37 folded in, the Spanish list
 * overflowed that cap and `B1CurriculumData` stopped compiling at all. The units
 * are unchanged; only the file they are declared in is different;
 * `spanishB1Units` ends with `+ SpanishVocabularyCurriculumData.spanishVocabularyUnits`.
 *
 * Id layout inside this file:
 *   - `90000`-`90999`   the challenges of unit 36, and `900000` up the options
 *   - `91000`-`91999`   the challenges of unit 37, and `910000` up the options
 *   Nothing here is held out: the B1 pool stays fixed at 8, drawn from units
 *   30-31, so a held-out item in unit 36 or 37 would be reachable by nothing.
 * Audio: 18 Kokoro clips, all new — 12 LISTEN sentence clips and 6 STORY
 * passages (`story_900`-`story_905`, one per lesson). A `WRONG_*` distractor
 * never carries one anywhere in this file.
 */
object SpanishVocabularyCurriculumData {

    val spanishVocabularyUnits: List<UnitPayload> = listOf(
        // ---------------------------------------------------------------------
        UnitPayload(
            unit = UnitEntity(
                id = 36,
                courseId = 1,
                title = "Unit 17: Everyday Life in Spanish",
                description = "The body, the home, and the table — la salud, la casa y la comida",
                orderIndex = 16,
            ),
            lessons = listOf(
                LessonEntity(id = 900, unitId = 36, title = "Lesson 36: The Body and Health", orderIndex = 0),
                LessonEntity(id = 901, unitId = 36, title = "Lesson 37: The Home, Room by Room", orderIndex = 1),
                LessonEntity(id = 902, unitId = 36, title = "Lesson 38: Food, Shopping and the Restaurant", orderIndex = 2),
            ),
            challenges = listOf(

                ChallengeEntity(
                    id = 90000, lessonId = 900, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (6 pairs)",
                    orderIndex = 0,
                ),

                ChallengeEntity(
                    id = 90020, lessonId = 900, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (6 pairs)",
                    orderIndex = 1,
                ),

                ChallengeEntity(
                    id = 90040, lessonId = 900, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (6 pairs)",
                    orderIndex = 2,
                ),

                ChallengeEntity(
                    id = 90060, lessonId = 900, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (6 pairs)",
                    orderIndex = 3,
                ),

                ChallengeEntity(
                    id = 90080, lessonId = 900, type = ChallengeType.SELECT,
                    question = "Which one means 'the eye'?",
                    orderIndex = 4,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " el ojo is the answer. la ojo is the wrong person or number for the slot. los ojo is the wrong person or number for the slot. el ojos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90100, lessonId = 900, type = ChallengeType.SELECT,
                    question = "Which one means 'the ear'?",
                    orderIndex = 5,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la oreja is the answer. el oreja is the wrong person or number for the slot. las oreja is the wrong person or number for the slot. la orejas is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90120, lessonId = 900, type = ChallengeType.SELECT,
                    question = "Which one means 'the mouth'?",
                    orderIndex = 6,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la boca is the answer. el boca is the wrong person or number for the slot. las boca is the wrong person or number for the slot. la bocas is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90140, lessonId = 900, type = ChallengeType.SELECT,
                    question = "Which one means 'the nose'?",
                    orderIndex = 7,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la nariz is the answer. el nariz is the wrong person or number for the slot. las nariz is the wrong person or number for the slot. la noses is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90160, lessonId = 900, type = ChallengeType.SELECT,
                    question = "Which one means 'the forehead'?",
                    orderIndex = 8,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la frente is the answer. el frente is the wrong person or number for the slot. las frente is the wrong person or number for the slot. la frentes is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90180, lessonId = 900, type = ChallengeType.SELECT,
                    question = "Which one means 'the cheek'?",
                    orderIndex = 9,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la mejilla is the answer. el mejilla is the wrong person or number for the slot. las mejilla is the wrong person or number for the slot. la mejillas is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90200, lessonId = 900, type = ChallengeType.SELECT,
                    question = "Which one means 'the hip'?",
                    orderIndex = 10,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la cadera is the answer. el cadera is the wrong person or number for the slot. las cadera is the wrong person or number for the slot. la caderas is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90220, lessonId = 900, type = ChallengeType.SELECT,
                    question = "Which one means 'the calf'?",
                    orderIndex = 11,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la pantorrilla is the answer. el pantorrilla is the wrong person or number for the slot. las pantorrilla is the wrong person or number for the slot. la pantorrillas is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90240, lessonId = 900, type = ChallengeType.SELECT,
                    question = "Which one means 'the white coat'?",
                    orderIndex = 12,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la bata blanca is the answer. el bata blanca is the wrong person or number for the slot. la bata blancos is a form of the word that cannot fill the slot. la bata blancas is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 90260, lessonId = 900, type = ChallengeType.SELECT,
                    question = "Which one means 'a serious wound'?",
                    orderIndex = 13,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la herida grave is the answer. el herida grave is the wrong person or number for the slot. la herida graves is a form of the word that cannot fill the slot. la herida gravísimo is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 90280, lessonId = 900, type = ChallengeType.SELECT,
                    question = "Which one means 'a high fever'?",
                    orderIndex = 14,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " fiebre alta is the answer. fiebre altas is a form of the word that cannot fill the slot. fiebre alto is a form of the word that cannot fill the slot. la fiebre alta is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90300, lessonId = 900, type = ChallengeType.SELECT,
                    question = "Which one means 'a stomach ache'?",
                    orderIndex = 15,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " dolor de estómago is the answer. dolores de estómago is a form of the word that cannot fill the slot. dolor de estómarago is a form of the word that cannot fill the slot. el dolor de estómago is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90320, lessonId = 900, type = ChallengeType.SELECT,
                    question = "Which present form of sentirse goes with 'yo'?",
                    orderIndex = 16,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "sentirse is reflexive, so the yo form carries the me pronoun with it: me siento. me siento is the answer. te sientas is the wrong person or number for the slot. se sienta is the wrong person or number for the slot. nos sentamos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90340, lessonId = 900, type = ChallengeType.CONJUGATE,
                    question = "Which present form of dormir goes with 'tú'?",
                    orderIndex = 17,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "dormir changes d- to du- in the present, and the tú ending is -es: duermes. duermes is the answer. duermo is the wrong person or number for the slot. duerme is the wrong person or number for the slot. dormimos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90360, lessonId = 900, type = ChallengeType.CONJUGATE,
                    question = "Which present form of preferir goes with 'nosotros'?",
                    orderIndex = 18,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "preferir changes e- to ie- in the present, but nosotros keeps the plain stem: preferimos. preferimos is the answer. prefiero is the wrong person or number for the slot. prefieres is the wrong person or number for the slot. prefiere is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90380, lessonId = 900, type = ChallengeType.SELECT,
                    question = "Which present form of vestirse goes with 'ellos'?",
                    orderIndex = 19,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "vestirse is reflexive, and ellos takes the -n form of the verb they are doing: se visten. se visten is the answer. se viste is the wrong person or number for the slot. nos vestimos is the wrong person or number for the slot. me visto is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90400, lessonId = 900, type = ChallengeType.FILL_BLANK,
                    question = "Hoy estoy ___ y no puedo ir a trabajar.",
                    orderIndex = 20,
                    grammaticalFocus = "es.vocab.everyday_life",
                    acceptedAnswers = "enfermo|enferma",
                    ruleText = "A person who is ill is estar-enfermo, and the ending agrees with whoever is speaking, so enfermo and enferma are both accepted here. enfermo is the answer. enfermos is the wrong person or number for the slot. enfermarse is a form of the word that cannot fill the slot. enferir is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 90420, lessonId = 900, type = ChallengeType.FILL_BLANK,
                    question = "Me roto el ___ si sigo corriendo.",
                    orderIndex = 21,
                    grammaticalFocus = "es.vocab.everyday_life",
                    acceptedAnswers = "tobillo|brazo",
                    ruleText = "A break is in one part of the body, and Spanish says that part in the singular with its own article: me roto el tobillo. brazo would need its own sentence, not this one. tobillo is the answer. tobillos is the wrong person or number for the slot. tobilar is a form of the word that cannot fill the slot. tobille is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 90440, lessonId = 900, type = ChallengeType.FILL_BLANK,
                    question = "¿Qué te ___ más: la cabeza o la espalda?",
                    orderIndex = 22,
                    grammaticalFocus = "es.vocab.everyday_life",
                    acceptedAnswers = "duele|punea",
                    ruleText = "Two verbs can say what hurts you: doler and punear both fill this slot, and the infinitive cannot. duele is the answer. doler is a form of the word that cannot fill the slot. duelan is the wrong person or number for the slot. dolerte is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 90460, lessonId = 900, type = ChallengeType.FILL_BLANK,
                    question = "La farmacia está ___ de la plaza.",
                    orderIndex = 23,
                    grammaticalFocus = "es.vocab.everyday_life",
                    acceptedAnswers = "cerca|lejos",
                    ruleText = "A place is cerca de or lejos de another place; dentro, encima and debajo do not take de this way. cerca is the answer. dentro is a form of the word that cannot fill the slot. encima is a form of the word that cannot fill the slot. debajo is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 90480, lessonId = 900, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/me_duele_la_garganta.ogg",
                    orderIndex = 24,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "The clip says Me duele la garganta. Me duele la garganta. is the answer. Me duelen la garganta is the wrong person or number for the slot. Me duele el garganta is the wrong person or number for the slot. Me duele las garganta is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90500, lessonId = 900, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/la_enfermera_me_dio_una_pastilla.ogg",
                    orderIndex = 25,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "The clip says La enfermera me dio una pastilla. La enfermera me dio una pastilla. is the answer. La enfermera me dio una pastillas is the wrong person or number for the slot. La enfermera me dio un pastilla is the wrong person or number for the slot. La enfermera le dio una pastilla is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 90520,
                                  romaji = "la konsˈulta ˈayer me dolˈia mˈucho la gargˈanta y no podhˈia abhlˈar bien mi mˈadhre me yebhˈo al mˈediko en el sˈentro de la siudhˈad la enfermˈera me tomˈo la temperatˈura y me dio una pastˈiya el mˈediko dˈijo kˈe deskansˈara dos dˈias", lessonId = 900, type = ChallengeType.STORY,
                    question = "La consulta\nAyer me dolía mucho la garganta y no podía hablar bien.\nMi madre me llevó al médico en el centro de la ciudad.\nLa enfermera me tomó la temperatura y me dio una pastilla.\nEl médico dijo que descansara dos días.\n\n❓ What did the doctor tell the speaker to do?",
                    audioSrc = "asset:///audio/es/story_900.ogg",
                    orderIndex = 26,
                ),

                ChallengeEntity(
                    id = 91000, lessonId = 901, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (6 pairs)",
                    orderIndex = 0,
                    ruleText = "A bedroom is la alcoba in Latin America and el dormitorio in Spain: the same room, so the English side is the same for both. Match each word to its meaning.",
                ),

                ChallengeEntity(
                    id = 91020, lessonId = 901, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (6 pairs)",
                    orderIndex = 1,
                ),

                ChallengeEntity(
                    id = 91040, lessonId = 901, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (6 pairs)",
                    orderIndex = 2,
                ),

                ChallengeEntity(
                    id = 91060, lessonId = 901, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (6 pairs)",
                    orderIndex = 3,
                ),

                ChallengeEntity(
                    id = 91080, lessonId = 901, type = ChallengeType.SELECT,
                    question = "Which one means 'the garden'?",
                    orderIndex = 4,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " el jardín is the answer. la jardín is the wrong person or number for the slot. los jardín is the wrong person or number for the slot. el jardines is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 91100, lessonId = 901, type = ChallengeType.SELECT,
                    question = "Which one means 'the balcony'?",
                    orderIndex = 5,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " el balcón is the answer. la balcón is the wrong person or number for the slot. el balcones is the wrong person or number for the slot. los balcón is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 91120, lessonId = 901, type = ChallengeType.SELECT,
                    question = "Which one means 'the mailbox'?",
                    orderIndex = 6,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " el buzón is the answer. la buzón is the wrong person or number for the slot. los buzón is the wrong person or number for the slot. el buzones is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 91140, lessonId = 901, type = ChallengeType.SELECT,
                    question = "Which one means 'the door'?",
                    orderIndex = 7,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la puerta is the answer. el puerta is the wrong person or number for the slot. las puertas is the wrong person or number for the slot. los puerta is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 91160, lessonId = 901, type = ChallengeType.SELECT,
                    question = "Which one means 'the bookshelf'?",
                    orderIndex = 8,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la estantería is the answer. el estantería is the wrong person or number for the slot. la estanterías is the wrong person or number for the slot. los estantería is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 91180, lessonId = 901, type = ChallengeType.SELECT,
                    question = "Which one means 'the screen'?",
                    orderIndex = 9,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la pantalla is the answer. el pantalla is the wrong person or number for the slot. la pantallas is the wrong person or number for the slot. los pantalla is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 91200, lessonId = 901, type = ChallengeType.SELECT,
                    question = "Which one means 'a small kitchen'?",
                    orderIndex = 10,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la cocina pequeña is the answer. la cocina pequeñas is a form of the word that cannot fill the slot. la cocina pequeño is a form of the word that cannot fill the slot. el cocina pequeña is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 91220, lessonId = 901, type = ChallengeType.SELECT,
                    question = "Which one means 'a comfortable bed'?",
                    orderIndex = 11,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la cama cómoda is the answer. la cama cómodo is a form of the word that cannot fill the slot. la cama cómodas is a form of the word that cannot fill the slot. el cama cómoda is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 91240, lessonId = 901, type = ChallengeType.SELECT,
                    question = "Which one means 'a tiled floor'?",
                    orderIndex = 12,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " el suelo de baldosa is the answer. el suelos de baldosa is the wrong person or number for the slot. el suelo de baldosas is a form of the word that cannot fill the slot. la suelo de baldosa is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 91260, lessonId = 901, type = ChallengeType.SELECT,
                    question = "Which one means 'a rented flat'?",
                    orderIndex = 13,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " un piso alquilado is the answer. un piso alquilados is a form of the word that cannot fill the slot. una piso alquilado is the wrong person or number for the slot. un piso de alquilado is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 91280, lessonId = 901, type = ChallengeType.SELECT,
                    question = "Which one means 'the landlord'?",
                    orderIndex = 14,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " el casero is the answer. la casero is the wrong person or number for the slot. los casero is the wrong person or number for the slot. el caseros is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 91300, lessonId = 901, type = ChallengeType.SELECT,
                    question = "Which one means 'a central heating system'?",
                    orderIndex = 15,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la calefacción central is the answer. la calefacción centrales is a form of the word that cannot fill the slot. el calefacción central is the wrong person or number for the slot. la caléfacción central is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 91320, lessonId = 901, type = ChallengeType.CONJUGATE,
                    question = "Which present form of poner goes with 'tú'?",
                    orderIndex = 16,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "poner drops the e in the yo form, which is pongo, and the tú ending is -es: pones. pones is the answer. pongo is the wrong person or number for the slot. pone is the wrong person or number for the slot. ponemos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 91340, lessonId = 901, type = ChallengeType.CONJUGATE,
                    question = "Which present form of saber goes with 'yo'?",
                    orderIndex = 17,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "saber is one of the few verbs with an irregular yo form, and that form is sé. sé is the answer. sabes is the wrong person or number for the slot. sabe is the wrong person or number for the slot. sabemos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 91360, lessonId = 901, type = ChallengeType.CONJUGATE,
                    question = "Which present form of abrir goes with 'nosotros'?",
                    orderIndex = 18,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "abrir takes a regular -ir present, and nosotros keeps the infinitive stem: abrimos. abrimos is the answer. abro is the wrong person or number for the slot. abres is the wrong person or number for the slot. abre is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 91380, lessonId = 901, type = ChallengeType.CONJUGATE,
                    question = "Which present form of quedar goes with 'ellos'?",
                    orderIndex = 19,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "quedar is a regular -ar verb, and ellos takes -n: quedan. quedan is the answer. quedo is the wrong person or number for the slot. quedas is the wrong person or number for the slot. quedamos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 91400, lessonId = 901, type = ChallengeType.FILL_BLANK,
                    question = "Guardo la leche en la ___ para que no se estropee.",
                    orderIndex = 20,
                    grammaticalFocus = "es.vocab.everyday_life",
                    acceptedAnswers = "nevera|frigorífico",
                    // "also understood in Spain" is deliberate: el frigorífico is an ordinary
                    // DRAE-listed Spanish word too, so this is a preference, not a split.
                    // Do not remove the overlap clause.
                    ruleText = "The cold cupboard is la nevera in Spain and el frigorífico in Latin America; el frigorífico is also understood in Spain, so both fill the slot. nevera is the answer. neveras is the wrong person or number for the slot. nevar is a form of the word that cannot fill the slot. estropiar is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 91420, lessonId = 901, type = ChallengeType.FILL_BLANK,
                    question = "Por la ventana entra ___ luz por la mañana.",
                    orderIndex = 21,
                    grammaticalFocus = "es.vocab.everyday_life",
                    acceptedAnswers = "mucha|bastante",
                    ruleText = "luz is feminine, so the word that measures it has to be feminine too: mucha or bastante. mucha is the answer. mucho is the wrong person or number for the slot. muchas is the wrong person or number for the slot. muy is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 91440, lessonId = 901, type = ChallengeType.FILL_BLANK,
                    question = "¿Duermes con la ventana ___?",
                    orderIndex = 22,
                    grammaticalFocus = "es.vocab.everyday_life",
                    acceptedAnswers = "abierta|cerrada",
                    ruleText = "The window is one thing, so the adjective beside it is singular: abierta or cerrada. abierta is the answer. abiertas is the wrong person or number for the slot. abierto is the wrong person or number for the slot. abrir is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 91460, lessonId = 901, type = ChallengeType.FILL_BLANK,
                    question = "El piso tiene tres ___ y un salón.",
                    orderIndex = 23,
                    grammaticalFocus = "es.vocab.everyday_life",
                    acceptedAnswers = "dormitorios|cuartos",
                    ruleText = "The verb tiene says there is more than one, so the noun is plural: tres dormitorios. A room of any kind is un cuarto, and dormir is the verb. dormitorios is the answer. dormitorio is the wrong person or number for the slot. dormidas is a form of the word that cannot fill the slot. dormir is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 91480, lessonId = 901, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/la_nevera_esta_en_la_cocina.ogg",
                    orderIndex = 24,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "The clip says La nevera está en la cocina. La nevera está en la cocina. is the answer. La nevera está en el cocina is the wrong person or number for the slot. Las nevera está en la cocina is the wrong person or number for the slot. La nevera está en las cocina is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 91500, lessonId = 901, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/voy_a_limpiar_el_bano_y_el_pasillo.ogg",
                    orderIndex = 25,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "The clip says Voy a limpiar el baño y el pasillo. Voy a limpiar el baño y el pasillo. is the answer. Voy a limpiar el baño y la pasillo is the wrong person or number for the slot. Voy a limpiar el baño y el pasillos is the wrong person or number for the slot. Voy a limpios el baño y el pasillo is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 91520,
                                  romaji = "los sˈabadhos los sˈabadhos por la mañˈana lˈimpio la kosˈina y el bˈaño despˈues pˈongo la rrˈopa sˈusia en la labhadhˈora tˈiendo las toˈayas en la terrˈasa si ˈase sol el domˈingo por la tˈardhe me sˈiento en la sˈala kon un lˈibhro", lessonId = 901, type = ChallengeType.STORY,
                    question = "Los sábados\nLos sábados por la mañana limpio la cocina y el baño.\nDespués pongo la ropa sucia en la lavadora.\nTiendo las toallas en la terraza si hace sol.\nEl domingo por la tarde me siento en la sala con un libro.\n\n❓ What does the speaker do on Saturday morning?",
                    audioSrc = "asset:///audio/es/story_901.ogg",
                    orderIndex = 26,
                ),

                ChallengeEntity(
                    id = 92000, lessonId = 902, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (6 pairs)",
                    orderIndex = 0,
                ),

                ChallengeEntity(
                    id = 92020, lessonId = 902, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (6 pairs)",
                    orderIndex = 1,
                ),

                ChallengeEntity(
                    id = 92040, lessonId = 902, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (6 pairs)",
                    orderIndex = 2,
                ),

                ChallengeEntity(
                    id = 92060, lessonId = 902, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (6 pairs)",
                    orderIndex = 3,
                ),

                ChallengeEntity(
                    id = 92080, lessonId = 902, type = ChallengeType.SELECT,
                    question = "Which one means 'the waiter'?",
                    orderIndex = 4,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " el camarero is the answer. la camarero is the wrong person or number for the slot. los camarero is the wrong person or number for the slot. el camareros is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 92100, lessonId = 902, type = ChallengeType.SELECT,
                    question = "Which one means 'the menu'?",
                    orderIndex = 5,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la carta is the answer. el carta is the wrong person or number for the slot. las carta is the wrong person or number for the slot. la cartas is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 92120, lessonId = 902, type = ChallengeType.SELECT,
                    question = "Which one means 'the tip'?",
                    orderIndex = 6,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " la propina is the answer. el propina is the wrong person or number for the slot. las propina is the wrong person or number for the slot. la propinas is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 92140, lessonId = 902, type = ChallengeType.SELECT,
                    question = "Which one means 'the order you ask for'?",
                    orderIndex = 7,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " el pedido is the answer. la pedido is the wrong person or number for the slot. los pedido is the wrong person or number for the slot. el pedidos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 92160, lessonId = 902, type = ChallengeType.SELECT,
                    question = "Which one means 'a bowl of soup'?",
                    orderIndex = 8,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " un plato de sopa is the answer. unos plato de sopa is the wrong person or number for the slot. un plato de sopas is a form of the word that cannot fill the slot. una plato de sopa is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 92180, lessonId = 902, type = ChallengeType.SELECT,
                    question = "Which one means 'a hot coffee'?",
                    orderIndex = 9,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " un café caliente is the answer. un café calientes is a form of the word that cannot fill the slot. un café frío is a form of the word that cannot fill the slot. una café caliente is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 92200, lessonId = 902, type = ChallengeType.SELECT,
                    question = "Which one means 'a fresh fish'?",
                    orderIndex = 10,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " un pescado fresco is the answer. un pescado frescos is a form of the word that cannot fill the slot. un pescado fresca is a form of the word that cannot fill the slot. una pescado fresco is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 92220, lessonId = 902, type = ChallengeType.SELECT,
                    question = "Which one means 'a glass of water'?",
                    orderIndex = 11,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " un vaso de agua is the answer. unos vaso de agua is the wrong person or number for the slot. un vaso de aguas is a form of the word that cannot fill the slot. una vaso de agua is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 92240, lessonId = 902, type = ChallengeType.SELECT,
                    question = "Which one means 'a delicious dessert'?",
                    orderIndex = 12,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " un postre delicioso is the answer. un postre deliciosos is a form of the word that cannot fill the slot. un postre deliciosa is a form of the word that cannot fill the slot. una postre delicioso is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 92260, lessonId = 902, type = ChallengeType.SELECT,
                    question = "Which one means 'a cheap meal'?",
                    orderIndex = 13,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " una comida barata is the answer. una comidas baratas is a form of the word that cannot fill the slot. una comida barato is a form of the word that cannot fill the slot. un comida barata is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 92280, lessonId = 902, type = ChallengeType.SELECT,
                    question = "Which one means 'to order' (in a restaurant)?",
                    orderIndex = 14,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " pedir is the answer. pides is the wrong person or number for the slot. pidió puts the verb in a tense the sentence does not ask for. pedirse is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 92300, lessonId = 902, type = ChallengeType.SELECT,
                    question = "Which one means 'to try a dish'?",
                    orderIndex = 15,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = " probar is the answer. pruebo is the wrong person or number for the slot. probó puts the verb in a tense the sentence does not ask for. probarse is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 92320, lessonId = 902, type = ChallengeType.CONJUGATE,
                    question = "Which present form of pedir goes with 'yo'?",
                    orderIndex = 16,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "pedir changes e- to ie- in the present, and the yo form carries no accent: pido. pido is the answer. pides is the wrong person or number for the slot. pide is the wrong person or number for the slot. pedimos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 92340, lessonId = 902, type = ChallengeType.CONJUGATE,
                    question = "Which present form of probar goes with 'nosotros'?",
                    orderIndex = 17,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "probar changes o- to ue- in the stressed syllable, but nosotros keeps the plain stem: probamos. probamos is the answer. probo is the wrong person or number for the slot. probas is the wrong person or number for the slot. prueba is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 92360, lessonId = 902, type = ChallengeType.CONJUGATE,
                    question = "Which present form of volver goes with 'tú'?",
                    orderIndex = 18,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "volver changes o- to ue- in the present, and the tú ending is -es: vuelves. vuelves is the answer. vuelvo is the wrong person or number for the slot. vuelve is the wrong person or number for the slot. volvemos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 92380, lessonId = 902, type = ChallengeType.CONJUGATE,
                    question = "Which present form of servir goes with 'ellos'?",
                    orderIndex = 19,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "servir changes e- to ie- in the present, and ellos takes -n: sirven. sirven is the answer. sirvo is the wrong person or number for the slot. sirves is the wrong person or number for the slot. servimos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 92400, lessonId = 902, type = ChallengeType.FILL_BLANK,
                    question = "¿___ carne, Marta?",
                    orderIndex = 20,
                    grammaticalFocus = "es.vocab.everyday_life",
                    acceptedAnswers = "comes",
                    ruleText = "The question is put to Marta as tú, so the second person singular comes is the answer. como is the yo form, comió is the preterite, and comerse is the infinitive. comes is the answer. como is the wrong person or number for the slot. comió puts the verb in a tense the sentence does not ask for. comerse is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 92420, lessonId = 902, type = ChallengeType.FILL_BLANK,
                    question = "¿Me ___ la sal, por favor?",
                    orderIndex = 21,
                    grammaticalFocus = "es.vocab.everyday_life",
                    acceptedAnswers = "pasa|pásame",
                    ruleText = "A request to a waiter is for the salt to be passed, and the verb is pasar. The two accepted spellings are the plain pasa and the attached pásame, which Spanish writes with an accent because the stress moves onto me. pasa is the answer. pase is a form of the word that cannot fill the slot. pasas is the wrong person or number for the slot. pasar is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 92440, lessonId = 902, type = ChallengeType.FILL_BLANK,
                    question = "Esta sopa está muy ___.",
                    orderIndex = 22,
                    grammaticalFocus = "es.vocab.everyday_life",
                    acceptedAnswers = "sabrosa|deliciosa",
                    ruleText = "Two adjectives say that a soup tastes good: sabrosa and deliciosa. sabroso would be the form for a masculine or a plural noun, and sabrosura is the noun for goodness, not an adjective. sabrosa is the answer. sabrosos is a form of the word that cannot fill the slot. sabroso is a form of the word that cannot fill the slot. sabrosura is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 92460, lessonId = 902, type = ChallengeType.FILL_BLANK,
                    question = "Vamos a ___ la mesa en cuanto terminemos.",
                    orderIndex = 23,
                    grammaticalFocus = "es.vocab.everyday_life",
                    acceptedAnswers = "recoger|levantar",
                    // Deliberately states a shared default, not a two-way split: no native
                    // speaker will ever check this (docs/kokoro-tts.md §11.1), and "recoger la
                    // mesa" is not Spain-specific, so a contrast here could be false and
                    // unverifiable forever. Do not "tighten" this into a regional split.
                    ruleText = "Both regions say recoger la mesa to clear the table; levantar la mesa is also heard in Latin America for the same thing. recoge is the tú form and recogida is a noun. recoger is the answer. recoge is a form of the word that cannot fill the slot. recogemos is the wrong person or number for the slot. recogida is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 92480, lessonId = 902, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/quiero_pedir_la_carta.ogg",
                    orderIndex = 24,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "The clip says Quiero pedir la carta, por favor. Quiero pedir la carta, por favor. is the answer. Quiero pedir la cartas is the wrong person or number for the slot. Quiero pedir el carta is the wrong person or number for the slot. Quiero pedir la cocina is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 92500, lessonId = 902, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/la_propina_esta_incluida.ogg",
                    orderIndex = 25,
                    grammaticalFocus = "es.vocab.everyday_life",
                    ruleText = "The clip says La propina está incluida en la cuenta. La propina está incluida en la cuenta. is the answer. La propinas está incluida is the wrong person or number for the slot. La propina está incluidos is a form of the word that cannot fill the slot. La propina está incluida en el cuenta is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 92520,
                                  romaji = "en el merkˈadho ˈayer fˈui al merkˈadho kon mi ermˈana komprˈamos frˈuta berdhˈura y pan pˈara la semˈana en la panadherˈia paghˈamos kon tarjˈeta despˈues tomˈamos un kafˈe en la terrˈaza", lessonId = 902, type = ChallengeType.STORY,
                    question = "En el mercado\nAyer fui al mercado con mi hermana.\nCompramos fruta, verdura y pan para la semana.\nEn la panadería pagamos con tarjeta.\nDespués tomamos un café en la terraza.\n\n❓ How did they pay at the bakery?",
                    audioSrc = "asset:///audio/es/story_902.ogg",
                    orderIndex = 26,
                ),
            ),
            options = listOf(

                ChallengeOptionEntity(id = 900000, challengeId = 90000, text = "el brazo", correct = true),
                ChallengeOptionEntity(id = 900001, challengeId = 90000, text = "the arm", correct = true),
                ChallengeOptionEntity(id = 900002, challengeId = 90000, text = "la pierna", correct = true),
                ChallengeOptionEntity(id = 900003, challengeId = 90000, text = "the leg", correct = true),
                ChallengeOptionEntity(id = 900004, challengeId = 90000, text = "el hombro", correct = true),
                ChallengeOptionEntity(id = 900005, challengeId = 90000, text = "the shoulder", correct = true),
                ChallengeOptionEntity(id = 900006, challengeId = 90000, text = "el codo", correct = true),
                ChallengeOptionEntity(id = 900007, challengeId = 90000, text = "the elbow", correct = true),
                ChallengeOptionEntity(id = 900008, challengeId = 90000, text = "la rodilla", correct = true),
                ChallengeOptionEntity(id = 900009, challengeId = 90000, text = "the knee", correct = true),
                ChallengeOptionEntity(id = 900010, challengeId = 90000, text = "la muñeca", correct = true),
                ChallengeOptionEntity(id = 900011, challengeId = 90000, text = "the wrist", correct = true),

                ChallengeOptionEntity(id = 900400, challengeId = 90020, text = "el tobillo", correct = true),
                ChallengeOptionEntity(id = 900401, challengeId = 90020, text = "the ankle", correct = true),
                ChallengeOptionEntity(id = 900402, challengeId = 90020, text = "la garganta", correct = true),
                ChallengeOptionEntity(id = 900403, challengeId = 90020, text = "the throat", correct = true),
                ChallengeOptionEntity(id = 900404, challengeId = 90020, text = "el estómago", correct = true),
                ChallengeOptionEntity(id = 900405, challengeId = 90020, text = "the stomach", correct = true),
                ChallengeOptionEntity(id = 900406, challengeId = 90020, text = "el corazón", correct = true),
                ChallengeOptionEntity(id = 900407, challengeId = 90020, text = "the heart", correct = true),
                ChallengeOptionEntity(id = 900408, challengeId = 90020, text = "el pulmón", correct = true),
                ChallengeOptionEntity(id = 900409, challengeId = 90020, text = "the lung", correct = true),
                ChallengeOptionEntity(id = 900410, challengeId = 90020, text = "el hueso", correct = true),
                ChallengeOptionEntity(id = 900411, challengeId = 90020, text = "the bone", correct = true),

                ChallengeOptionEntity(id = 900800, challengeId = 90040, text = "la piel", correct = true),
                ChallengeOptionEntity(id = 900801, challengeId = 90040, text = "the skin", correct = true),
                ChallengeOptionEntity(id = 900802, challengeId = 90040, text = "la barba", correct = true),
                ChallengeOptionEntity(id = 900803, challengeId = 90040, text = "the beard", correct = true),
                ChallengeOptionEntity(id = 900804, challengeId = 90040, text = "el diente", correct = true),
                ChallengeOptionEntity(id = 900805, challengeId = 90040, text = "the tooth", correct = true),
                ChallengeOptionEntity(id = 900806, challengeId = 90040, text = "la uña", correct = true),
                ChallengeOptionEntity(id = 900807, challengeId = 90040, text = "the fingernail", correct = true),
                ChallengeOptionEntity(id = 900808, challengeId = 90040, text = "la enfermera", correct = true),
                ChallengeOptionEntity(id = 900809, challengeId = 90040, text = "the nurse", correct = true),
                ChallengeOptionEntity(id = 900810, challengeId = 90040, text = "el médico", correct = true),
                ChallengeOptionEntity(id = 900811, challengeId = 90040, text = "the doctor", correct = true),

                ChallengeOptionEntity(id = 901200, challengeId = 90060, text = "la pastilla", correct = true),
                ChallengeOptionEntity(id = 901201, challengeId = 90060, text = "the pill", correct = true),
                ChallengeOptionEntity(id = 901202, challengeId = 90060, text = "la herida", correct = true),
                ChallengeOptionEntity(id = 901203, challengeId = 90060, text = "the wound", correct = true),
                ChallengeOptionEntity(id = 901204, challengeId = 90060, text = "la receta", correct = true),
                ChallengeOptionEntity(id = 901205, challengeId = 90060, text = "the prescription", correct = true),
                ChallengeOptionEntity(id = 901206, challengeId = 90060, text = "el medicamento", correct = true),
                ChallengeOptionEntity(id = 901207, challengeId = 90060, text = "the medicine", correct = true),
                ChallengeOptionEntity(id = 901208, challengeId = 90060, text = "la alergia", correct = true),
                ChallengeOptionEntity(id = 901209, challengeId = 90060, text = "the allergy", correct = true),
                ChallengeOptionEntity(id = 901210, challengeId = 90060, text = "la quemadura", correct = true),
                ChallengeOptionEntity(id = 901211, challengeId = 90060, text = "the burn", correct = true),

                ChallengeOptionEntity(id = 901600, challengeId = 90080, text = "el ojo", correct = true),
                ChallengeOptionEntity(id = 901601, challengeId = 90080, text = "la ojo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 901602, challengeId = 90080, text = "los ojo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 901603, challengeId = 90080, text = "el ojos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 902000, challengeId = 90100, text = "la oreja", correct = true),
                ChallengeOptionEntity(id = 902001, challengeId = 90100, text = "el oreja", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 902002, challengeId = 90100, text = "las oreja", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 902003, challengeId = 90100, text = "la orejas", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 902400, challengeId = 90120, text = "la boca", correct = true),
                ChallengeOptionEntity(id = 902401, challengeId = 90120, text = "el boca", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 902402, challengeId = 90120, text = "las boca", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 902403, challengeId = 90120, text = "la bocas", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 902800, challengeId = 90140, text = "la nariz", correct = true),
                ChallengeOptionEntity(id = 902801, challengeId = 90140, text = "el nariz", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 902802, challengeId = 90140, text = "las nariz", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 902803, challengeId = 90140, text = "la noses", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 903200, challengeId = 90160, text = "la frente", correct = true),
                ChallengeOptionEntity(id = 903201, challengeId = 90160, text = "el frente", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 903202, challengeId = 90160, text = "las frente", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 903203, challengeId = 90160, text = "la frentes", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 903600, challengeId = 90180, text = "la mejilla", correct = true),
                ChallengeOptionEntity(id = 903601, challengeId = 90180, text = "el mejilla", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 903602, challengeId = 90180, text = "las mejilla", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 903603, challengeId = 90180, text = "la mejillas", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 904000, challengeId = 90200, text = "la cadera", correct = true),
                ChallengeOptionEntity(id = 904001, challengeId = 90200, text = "el cadera", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 904002, challengeId = 90200, text = "las cadera", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 904003, challengeId = 90200, text = "la caderas", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 904400, challengeId = 90220, text = "la pantorrilla", correct = true),
                ChallengeOptionEntity(id = 904401, challengeId = 90220, text = "el pantorrilla", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 904402, challengeId = 90220, text = "las pantorrilla", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 904403, challengeId = 90220, text = "la pantorrillas", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 904800, challengeId = 90240, text = "la bata blanca", correct = true),
                ChallengeOptionEntity(id = 904801, challengeId = 90240, text = "el bata blanca", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 904802, challengeId = 90240, text = "la bata blancos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 904803, challengeId = 90240, text = "la bata blancas", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 905200, challengeId = 90260, text = "la herida grave", correct = true),
                ChallengeOptionEntity(id = 905201, challengeId = 90260, text = "el herida grave", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 905202, challengeId = 90260, text = "la herida graves", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 905203, challengeId = 90260, text = "la herida gravísimo", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 905600, challengeId = 90280, text = "fiebre alta", correct = true),
                ChallengeOptionEntity(id = 905601, challengeId = 90280, text = "fiebre altas", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 905602, challengeId = 90280, text = "fiebre alto", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 905603, challengeId = 90280, text = "la fiebre alta", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 906000, challengeId = 90300, text = "dolor de estómago", correct = true),
                ChallengeOptionEntity(id = 906001, challengeId = 90300, text = "dolores de estómago", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 906002, challengeId = 90300, text = "dolor de estómarago", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 906003, challengeId = 90300, text = "el dolor de estómago", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 906400, challengeId = 90320, text = "me siento", correct = true),
                ChallengeOptionEntity(id = 906401, challengeId = 90320, text = "te sientas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 906402, challengeId = 90320, text = "se sienta", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 906403, challengeId = 90320, text = "nos sentamos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 906800, challengeId = 90340, text = "duermes", correct = true),
                ChallengeOptionEntity(id = 906801, challengeId = 90340, text = "duermo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 906802, challengeId = 90340, text = "duerme", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 906803, challengeId = 90340, text = "dormimos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 907200, challengeId = 90360, text = "preferimos", correct = true),
                ChallengeOptionEntity(id = 907201, challengeId = 90360, text = "prefiero", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 907202, challengeId = 90360, text = "prefieres", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 907203, challengeId = 90360, text = "prefiere", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 907600, challengeId = 90380, text = "se visten", correct = true),
                ChallengeOptionEntity(id = 907601, challengeId = 90380, text = "se viste", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 907602, challengeId = 90380, text = "nos vestimos", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 907603, challengeId = 90380, text = "me visto", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 908000, challengeId = 90400, text = "enfermo", correct = true),
                ChallengeOptionEntity(id = 908001, challengeId = 90400, text = "enfermos", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 908002, challengeId = 90400, text = "enfermarse", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 908003, challengeId = 90400, text = "enferir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 908400, challengeId = 90420, text = "tobillo", correct = true),
                ChallengeOptionEntity(id = 908401, challengeId = 90420, text = "tobillos", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 908402, challengeId = 90420, text = "tobilar", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 908403, challengeId = 90420, text = "tobille", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 908800, challengeId = 90440, text = "duele", correct = true),
                ChallengeOptionEntity(id = 908801, challengeId = 90440, text = "doler", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 908802, challengeId = 90440, text = "duelan", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 908803, challengeId = 90440, text = "dolerte", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 909200, challengeId = 90460, text = "cerca", correct = true),
                ChallengeOptionEntity(id = 909201, challengeId = 90460, text = "dentro", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 909202, challengeId = 90460, text = "encima", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 909203, challengeId = 90460, text = "debajo", correct = false, errorTag = "WRONG_FORM"),

ChallengeOptionEntity(id = 909600, challengeId = 90480, text = "Me duele la garganta.", romaji = "me dˈuele la gargˈanta", correct = true, audioSrc = "asset:///audio/es/me_duele_la_garganta.ogg"),
                ChallengeOptionEntity(id = 909601, challengeId = 90480, text = "Me duelen la garganta", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 909602, challengeId = 90480, text = "Me duele el garganta", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 909603, challengeId = 90480, text = "Me duele las garganta", correct = false, errorTag = "WRONG_PERSON"),

ChallengeOptionEntity(id = 910000, challengeId = 90500, text = "La enfermera me dio una pastilla.", romaji = "la enfermˈera me dio una pastˈiya", correct = true, audioSrc = "asset:///audio/es/la_enfermera_me_dio_una_pastilla.ogg"),
                ChallengeOptionEntity(id = 910001, challengeId = 90500, text = "La enfermera me dio una pastillas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 910002, challengeId = 90500, text = "La enfermera me dio un pastilla", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 910003, challengeId = 90500, text = "La enfermera le dio una pastilla", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 910400, challengeId = 90520, text = "To rest for two days", correct = true),
                ChallengeOptionEntity(id = 910401, challengeId = 90520, text = "To come back the following week", correct = false),
                ChallengeOptionEntity(id = 910402, challengeId = 90520, text = "To drink the medicine in one hour", correct = false),
                ChallengeOptionEntity(id = 910403, challengeId = 90520, text = "To go to hospital the next morning", correct = false),

                ChallengeOptionEntity(id = 920000, challengeId = 91000, text = "la sala", correct = true),
                ChallengeOptionEntity(id = 920001, challengeId = 91000, text = "the living room", correct = true),
                ChallengeOptionEntity(id = 920002, challengeId = 91000, text = "la alcoba", correct = true),
                ChallengeOptionEntity(id = 920003, challengeId = 91000, text = "the bedroom", correct = true),
                ChallengeOptionEntity(id = 920004, challengeId = 91000, text = "el baño", correct = true),
                ChallengeOptionEntity(id = 920005, challengeId = 91000, text = "the bathroom", correct = true),
                ChallengeOptionEntity(id = 920006, challengeId = 91000, text = "la oficina", correct = true),
                ChallengeOptionEntity(id = 920007, challengeId = 91000, text = "the office", correct = true),
                ChallengeOptionEntity(id = 920008, challengeId = 91000, text = "el garaje", correct = true),
                ChallengeOptionEntity(id = 920009, challengeId = 91000, text = "the garage", correct = true),
                ChallengeOptionEntity(id = 920010, challengeId = 91000, text = "el desván", correct = true),
                ChallengeOptionEntity(id = 920011, challengeId = 91000, text = "the attic", correct = true),

                ChallengeOptionEntity(id = 920400, challengeId = 91020, text = "la pared", correct = true),
                ChallengeOptionEntity(id = 920401, challengeId = 91020, text = "the wall", correct = true),
                ChallengeOptionEntity(id = 920402, challengeId = 91020, text = "el techo", correct = true),
                ChallengeOptionEntity(id = 920403, challengeId = 91020, text = "the ceiling", correct = true),
                ChallengeOptionEntity(id = 920404, challengeId = 91020, text = "el suelo", correct = true),
                ChallengeOptionEntity(id = 920405, challengeId = 91020, text = "the floor", correct = true),
                ChallengeOptionEntity(id = 920406, challengeId = 91020, text = "la escalera", correct = true),
                ChallengeOptionEntity(id = 920407, challengeId = 91020, text = "the staircase", correct = true),
                ChallengeOptionEntity(id = 920408, challengeId = 91020, text = "la lámpara", correct = true),
                ChallengeOptionEntity(id = 920409, challengeId = 91020, text = "the lamp", correct = true),
                ChallengeOptionEntity(id = 920410, challengeId = 91020, text = "la cama", correct = true),
                ChallengeOptionEntity(id = 920411, challengeId = 91020, text = "the bed", correct = true),

                ChallengeOptionEntity(id = 920800, challengeId = 91040, text = "la almohada", correct = true),
                ChallengeOptionEntity(id = 920801, challengeId = 91040, text = "the pillow", correct = true),
                ChallengeOptionEntity(id = 920802, challengeId = 91040, text = "la sábana", correct = true),
                ChallengeOptionEntity(id = 920803, challengeId = 91040, text = "the sheet", correct = true),
                ChallengeOptionEntity(id = 920804, challengeId = 91040, text = "la nevera", correct = true),
                ChallengeOptionEntity(id = 920805, challengeId = 91040, text = "the fridge", correct = true),
                ChallengeOptionEntity(id = 920806, challengeId = 91040, text = "el horno", correct = true),
                ChallengeOptionEntity(id = 920807, challengeId = 91040, text = "the oven", correct = true),
                ChallengeOptionEntity(id = 920808, challengeId = 91040, text = "la lavadora", correct = true),
                ChallengeOptionEntity(id = 920809, challengeId = 91040, text = "the washing machine", correct = true),
                ChallengeOptionEntity(id = 920810, challengeId = 91040, text = "el espejo", correct = true),
                ChallengeOptionEntity(id = 920811, challengeId = 91040, text = "the mirror", correct = true),

                ChallengeOptionEntity(id = 921200, challengeId = 91060, text = "la toalla", correct = true),
                ChallengeOptionEntity(id = 921201, challengeId = 91060, text = "the towel", correct = true),
                ChallengeOptionEntity(id = 921202, challengeId = 91060, text = "el armario", correct = true),
                ChallengeOptionEntity(id = 921203, challengeId = 91060, text = "the wardrobe", correct = true),
                ChallengeOptionEntity(id = 921204, challengeId = 91060, text = "la cómoda", correct = true),
                ChallengeOptionEntity(id = 921205, challengeId = 91060, text = "the chest of drawers", correct = true),
                ChallengeOptionEntity(id = 921206, challengeId = 91060, text = "el estante", correct = true),
                ChallengeOptionEntity(id = 921207, challengeId = 91060, text = "the shelf", correct = true),
                ChallengeOptionEntity(id = 921208, challengeId = 91060, text = "el cuadro", correct = true),
                ChallengeOptionEntity(id = 921209, challengeId = 91060, text = "the painting", correct = true),
                ChallengeOptionEntity(id = 921210, challengeId = 91060, text = "la cortina", correct = true),
                ChallengeOptionEntity(id = 921211, challengeId = 91060, text = "the curtain", correct = true),

                ChallengeOptionEntity(id = 921600, challengeId = 91080, text = "el jardín", correct = true),
                ChallengeOptionEntity(id = 921601, challengeId = 91080, text = "la jardín", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 921602, challengeId = 91080, text = "los jardín", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 921603, challengeId = 91080, text = "el jardines", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 922000, challengeId = 91100, text = "el balcón", correct = true),
                ChallengeOptionEntity(id = 922001, challengeId = 91100, text = "la balcón", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 922002, challengeId = 91100, text = "el balcones", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 922003, challengeId = 91100, text = "los balcón", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 922400, challengeId = 91120, text = "el buzón", correct = true),
                ChallengeOptionEntity(id = 922401, challengeId = 91120, text = "la buzón", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 922402, challengeId = 91120, text = "los buzón", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 922403, challengeId = 91120, text = "el buzones", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 922800, challengeId = 91140, text = "la puerta", correct = true),
                ChallengeOptionEntity(id = 922801, challengeId = 91140, text = "el puerta", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 922802, challengeId = 91140, text = "las puertas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 922803, challengeId = 91140, text = "los puerta", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 923200, challengeId = 91160, text = "la estantería", correct = true),
                ChallengeOptionEntity(id = 923201, challengeId = 91160, text = "el estantería", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 923202, challengeId = 91160, text = "la estanterías", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 923203, challengeId = 91160, text = "los estantería", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 923600, challengeId = 91180, text = "la pantalla", correct = true),
                ChallengeOptionEntity(id = 923601, challengeId = 91180, text = "el pantalla", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 923602, challengeId = 91180, text = "la pantallas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 923603, challengeId = 91180, text = "los pantalla", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 924000, challengeId = 91200, text = "la cocina pequeña", correct = true),
                ChallengeOptionEntity(id = 924001, challengeId = 91200, text = "la cocina pequeñas", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 924002, challengeId = 91200, text = "la cocina pequeño", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 924003, challengeId = 91200, text = "el cocina pequeña", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 924400, challengeId = 91220, text = "la cama cómoda", correct = true),
                ChallengeOptionEntity(id = 924401, challengeId = 91220, text = "la cama cómodo", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 924402, challengeId = 91220, text = "la cama cómodas", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 924403, challengeId = 91220, text = "el cama cómoda", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 924800, challengeId = 91240, text = "el suelo de baldosa", correct = true),
                ChallengeOptionEntity(id = 924801, challengeId = 91240, text = "el suelos de baldosa", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 924802, challengeId = 91240, text = "el suelo de baldosas", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 924803, challengeId = 91240, text = "la suelo de baldosa", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 925200, challengeId = 91260, text = "un piso alquilado", correct = true),
                ChallengeOptionEntity(id = 925201, challengeId = 91260, text = "un piso alquilados", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 925202, challengeId = 91260, text = "una piso alquilado", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 925203, challengeId = 91260, text = "un piso de alquilado", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 925600, challengeId = 91280, text = "el casero", correct = true),
                ChallengeOptionEntity(id = 925601, challengeId = 91280, text = "la casero", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 925602, challengeId = 91280, text = "los casero", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 925603, challengeId = 91280, text = "el caseros", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 926000, challengeId = 91300, text = "la calefacción central", correct = true),
                ChallengeOptionEntity(id = 926001, challengeId = 91300, text = "la calefacción centrales", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 926002, challengeId = 91300, text = "el calefacción central", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 926003, challengeId = 91300, text = "la caléfacción central", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 926400, challengeId = 91320, text = "pones", correct = true),
                ChallengeOptionEntity(id = 926401, challengeId = 91320, text = "pongo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 926402, challengeId = 91320, text = "pone", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 926403, challengeId = 91320, text = "ponemos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 926800, challengeId = 91340, text = "sé", correct = true),
                ChallengeOptionEntity(id = 926801, challengeId = 91340, text = "sabes", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 926802, challengeId = 91340, text = "sabe", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 926803, challengeId = 91340, text = "sabemos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 927200, challengeId = 91360, text = "abrimos", correct = true),
                ChallengeOptionEntity(id = 927201, challengeId = 91360, text = "abro", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 927202, challengeId = 91360, text = "abres", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 927203, challengeId = 91360, text = "abre", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 927600, challengeId = 91380, text = "quedan", correct = true),
                ChallengeOptionEntity(id = 927601, challengeId = 91380, text = "quedo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 927602, challengeId = 91380, text = "quedas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 927603, challengeId = 91380, text = "quedamos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 928000, challengeId = 91400, text = "nevera", correct = true),
                ChallengeOptionEntity(id = 928001, challengeId = 91400, text = "neveras", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 928002, challengeId = 91400, text = "nevar", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 928003, challengeId = 91400, text = "estropiar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 928400, challengeId = 91420, text = "mucha", correct = true),
                ChallengeOptionEntity(id = 928401, challengeId = 91420, text = "mucho", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 928402, challengeId = 91420, text = "muchas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 928403, challengeId = 91420, text = "muy", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 928800, challengeId = 91440, text = "abierta", correct = true),
                ChallengeOptionEntity(id = 928801, challengeId = 91440, text = "abiertas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 928802, challengeId = 91440, text = "abierto", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 928803, challengeId = 91440, text = "abrir", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 929200, challengeId = 91460, text = "dormitorios", correct = true),
                ChallengeOptionEntity(id = 929201, challengeId = 91460, text = "dormitorio", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 929202, challengeId = 91460, text = "dormidas", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 929203, challengeId = 91460, text = "dormir", correct = false, errorTag = "WRONG_FORM"),

ChallengeOptionEntity(id = 929600, challengeId = 91480, text = "La nevera está en la cocina.", romaji = "la nebhˈera estˈa en la kosˈina", correct = true, audioSrc = "asset:///audio/es/la_nevera_esta_en_la_cocina.ogg"),
                ChallengeOptionEntity(id = 929601, challengeId = 91480, text = "La nevera está en el cocina", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 929602, challengeId = 91480, text = "Las nevera está en la cocina", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 929603, challengeId = 91480, text = "La nevera está en las cocina", correct = false, errorTag = "WRONG_PERSON"),

ChallengeOptionEntity(id = 930000, challengeId = 91500, text = "Voy a limpiar el baño y el pasillo.", romaji = "bˈoi a limpˈiar el bˈaño y el pasˈiyo", correct = true, audioSrc = "asset:///audio/es/voy_a_limpiar_el_bano_y_el_pasillo.ogg"),
                ChallengeOptionEntity(id = 930001, challengeId = 91500, text = "Voy a limpiar el baño y la pasillo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 930002, challengeId = 91500, text = "Voy a limpiar el baño y el pasillos", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 930003, challengeId = 91500, text = "Voy a limpios el baño y el pasillo", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 930400, challengeId = 91520, text = "Cleans the kitchen and the bathroom", correct = true),
                ChallengeOptionEntity(id = 930401, challengeId = 91520, text = "Reads a book in the living room", correct = false),
                ChallengeOptionEntity(id = 930402, challengeId = 91520, text = "Hangs the towels out on the terrace", correct = false),
                ChallengeOptionEntity(id = 930403, challengeId = 91520, text = "Cooks lunch for the family", correct = false),

                ChallengeOptionEntity(id = 940000, challengeId = 92000, text = "la carne", correct = true),
                ChallengeOptionEntity(id = 940001, challengeId = 92000, text = "the meat", correct = true),
                ChallengeOptionEntity(id = 940002, challengeId = 92000, text = "el pollo", correct = true),
                ChallengeOptionEntity(id = 940003, challengeId = 92000, text = "the chicken", correct = true),
                ChallengeOptionEntity(id = 940004, challengeId = 92000, text = "el pescado", correct = true),
                ChallengeOptionEntity(id = 940005, challengeId = 92000, text = "the fish", correct = true),
                ChallengeOptionEntity(id = 940006, challengeId = 92000, text = "la verdura", correct = true),
                ChallengeOptionEntity(id = 940007, challengeId = 92000, text = "the vegetable", correct = true),
                ChallengeOptionEntity(id = 940008, challengeId = 92000, text = "la fruta", correct = true),
                ChallengeOptionEntity(id = 940009, challengeId = 92000, text = "the fruit", correct = true),
                ChallengeOptionEntity(id = 940010, challengeId = 92000, text = "el queso", correct = true),
                ChallengeOptionEntity(id = 940011, challengeId = 92000, text = "the cheese", correct = true),

                ChallengeOptionEntity(id = 940400, challengeId = 92020, text = "el huevo", correct = true),
                ChallengeOptionEntity(id = 940401, challengeId = 92020, text = "the egg", correct = true),
                ChallengeOptionEntity(id = 940402, challengeId = 92020, text = "la sopa", correct = true),
                ChallengeOptionEntity(id = 940403, challengeId = 92020, text = "the soup", correct = true),
                ChallengeOptionEntity(id = 940404, challengeId = 92020, text = "la manzana", correct = true),
                ChallengeOptionEntity(id = 940405, challengeId = 92020, text = "the apple", correct = true),
                ChallengeOptionEntity(id = 940406, challengeId = 92020, text = "la naranja", correct = true),
                ChallengeOptionEntity(id = 940407, challengeId = 92020, text = "the orange", correct = true),
                ChallengeOptionEntity(id = 940408, challengeId = 92020, text = "el plátano", correct = true),
                ChallengeOptionEntity(id = 940409, challengeId = 92020, text = "the banana", correct = true),
                ChallengeOptionEntity(id = 940410, challengeId = 92020, text = "la uva", correct = true),
                ChallengeOptionEntity(id = 940411, challengeId = 92020, text = "the grape", correct = true),

                ChallengeOptionEntity(id = 940800, challengeId = 92040, text = "la fresa", correct = true),
                ChallengeOptionEntity(id = 940801, challengeId = 92040, text = "the strawberry", correct = true),
                ChallengeOptionEntity(id = 940802, challengeId = 92040, text = "la cebolla", correct = true),
                ChallengeOptionEntity(id = 940803, challengeId = 92040, text = "the onion", correct = true),
                ChallengeOptionEntity(id = 940804, challengeId = 92040, text = "el ajo", correct = true),
                ChallengeOptionEntity(id = 940805, challengeId = 92040, text = "the garlic", correct = true),
                ChallengeOptionEntity(id = 940806, challengeId = 92040, text = "la zanahoria", correct = true),
                ChallengeOptionEntity(id = 940807, challengeId = 92040, text = "the carrot", correct = true),
                ChallengeOptionEntity(id = 940808, challengeId = 92040, text = "la lechuga", correct = true),
                ChallengeOptionEntity(id = 940809, challengeId = 92040, text = "the lettuce", correct = true),
                ChallengeOptionEntity(id = 940810, challengeId = 92040, text = "el tomate", correct = true),
                ChallengeOptionEntity(id = 940811, challengeId = 92040, text = "the tomato", correct = true),

                ChallengeOptionEntity(id = 941200, challengeId = 92060, text = "el restaurante", correct = true),
                ChallengeOptionEntity(id = 941201, challengeId = 92060, text = "the restaurant", correct = true),
                ChallengeOptionEntity(id = 941202, challengeId = 92060, text = "la cafetería", correct = true),
                ChallengeOptionEntity(id = 941203, challengeId = 92060, text = "the café", correct = true),
                ChallengeOptionEntity(id = 941204, challengeId = 92060, text = "el mercado", correct = true),
                ChallengeOptionEntity(id = 941205, challengeId = 92060, text = "the market", correct = true),
                ChallengeOptionEntity(id = 941206, challengeId = 92060, text = "la tienda", correct = true),
                ChallengeOptionEntity(id = 941207, challengeId = 92060, text = "the shop", correct = true),
                ChallengeOptionEntity(id = 941208, challengeId = 92060, text = "el supermercado", correct = true),
                ChallengeOptionEntity(id = 941209, challengeId = 92060, text = "the supermarket", correct = true),
                ChallengeOptionEntity(id = 941210, challengeId = 92060, text = "la panadería", correct = true),
                ChallengeOptionEntity(id = 941211, challengeId = 92060, text = "the bakery", correct = true),

                ChallengeOptionEntity(id = 941600, challengeId = 92080, text = "el camarero", correct = true),
                ChallengeOptionEntity(id = 941601, challengeId = 92080, text = "la camarero", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 941602, challengeId = 92080, text = "los camarero", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 941603, challengeId = 92080, text = "el camareros", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 942000, challengeId = 92100, text = "la carta", correct = true),
                ChallengeOptionEntity(id = 942001, challengeId = 92100, text = "el carta", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 942002, challengeId = 92100, text = "las carta", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 942003, challengeId = 92100, text = "la cartas", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 942400, challengeId = 92120, text = "la propina", correct = true),
                ChallengeOptionEntity(id = 942401, challengeId = 92120, text = "el propina", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 942402, challengeId = 92120, text = "las propina", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 942403, challengeId = 92120, text = "la propinas", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 942800, challengeId = 92140, text = "el pedido", correct = true),
                ChallengeOptionEntity(id = 942801, challengeId = 92140, text = "la pedido", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 942802, challengeId = 92140, text = "los pedido", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 942803, challengeId = 92140, text = "el pedidos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 943200, challengeId = 92160, text = "un plato de sopa", correct = true),
                ChallengeOptionEntity(id = 943201, challengeId = 92160, text = "unos plato de sopa", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 943202, challengeId = 92160, text = "un plato de sopas", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 943203, challengeId = 92160, text = "una plato de sopa", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 943600, challengeId = 92180, text = "un café caliente", correct = true),
                ChallengeOptionEntity(id = 943601, challengeId = 92180, text = "un café calientes", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 943602, challengeId = 92180, text = "un café frío", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 943603, challengeId = 92180, text = "una café caliente", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 944000, challengeId = 92200, text = "un pescado fresco", correct = true),
                ChallengeOptionEntity(id = 944001, challengeId = 92200, text = "un pescado frescos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 944002, challengeId = 92200, text = "un pescado fresca", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 944003, challengeId = 92200, text = "una pescado fresco", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 944400, challengeId = 92220, text = "un vaso de agua", correct = true),
                ChallengeOptionEntity(id = 944401, challengeId = 92220, text = "unos vaso de agua", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 944402, challengeId = 92220, text = "un vaso de aguas", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 944403, challengeId = 92220, text = "una vaso de agua", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 944800, challengeId = 92240, text = "un postre delicioso", correct = true),
                ChallengeOptionEntity(id = 944801, challengeId = 92240, text = "un postre deliciosos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 944802, challengeId = 92240, text = "un postre deliciosa", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 944803, challengeId = 92240, text = "una postre delicioso", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 945200, challengeId = 92260, text = "una comida barata", correct = true),
                ChallengeOptionEntity(id = 945201, challengeId = 92260, text = "una comidas baratas", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 945202, challengeId = 92260, text = "una comida barato", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 945203, challengeId = 92260, text = "un comida barata", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 945600, challengeId = 92280, text = "pedir", correct = true),
                ChallengeOptionEntity(id = 945601, challengeId = 92280, text = "pides", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 945602, challengeId = 92280, text = "pidió", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 945603, challengeId = 92280, text = "pedirse", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 946000, challengeId = 92300, text = "probar", correct = true),
                ChallengeOptionEntity(id = 946001, challengeId = 92300, text = "pruebo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 946002, challengeId = 92300, text = "probó", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 946003, challengeId = 92300, text = "probarse", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 946400, challengeId = 92320, text = "pido", correct = true),
                ChallengeOptionEntity(id = 946401, challengeId = 92320, text = "pides", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 946402, challengeId = 92320, text = "pide", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 946403, challengeId = 92320, text = "pedimos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 946800, challengeId = 92340, text = "probamos", correct = true),
                ChallengeOptionEntity(id = 946801, challengeId = 92340, text = "probo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 946802, challengeId = 92340, text = "probas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 946803, challengeId = 92340, text = "prueba", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 947200, challengeId = 92360, text = "vuelves", correct = true),
                ChallengeOptionEntity(id = 947201, challengeId = 92360, text = "vuelvo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 947202, challengeId = 92360, text = "vuelve", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 947203, challengeId = 92360, text = "volvemos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 947600, challengeId = 92380, text = "sirven", correct = true),
                ChallengeOptionEntity(id = 947601, challengeId = 92380, text = "sirvo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 947602, challengeId = 92380, text = "sirves", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 947603, challengeId = 92380, text = "servimos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 948000, challengeId = 92400, text = "comes", correct = true),
                ChallengeOptionEntity(id = 948001, challengeId = 92400, text = "como", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 948002, challengeId = 92400, text = "comió", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 948003, challengeId = 92400, text = "comerse", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 948400, challengeId = 92420, text = "pasa", correct = true),
                ChallengeOptionEntity(id = 948401, challengeId = 92420, text = "pase", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 948402, challengeId = 92420, text = "pasas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 948403, challengeId = 92420, text = "pasar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 948800, challengeId = 92440, text = "sabrosa", correct = true),
                ChallengeOptionEntity(id = 948801, challengeId = 92440, text = "sabrosos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 948802, challengeId = 92440, text = "sabroso", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 948803, challengeId = 92440, text = "sabrosura", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 949200, challengeId = 92460, text = "recoger", correct = true),
                ChallengeOptionEntity(id = 949201, challengeId = 92460, text = "recoge", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 949202, challengeId = 92460, text = "recogemos", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 949203, challengeId = 92460, text = "recogida", correct = false, errorTag = "WRONG_FORM"),

ChallengeOptionEntity(id = 949600, challengeId = 92480, text = "Quiero pedir la carta, por favor.", romaji = "kˈiero pedhˈir la kˈarta por fabhˈor", correct = true, audioSrc = "asset:///audio/es/quiero_pedir_la_carta.ogg"),
                ChallengeOptionEntity(id = 949601, challengeId = 92480, text = "Quiero pedir la cartas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 949602, challengeId = 92480, text = "Quiero pedir el carta", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 949603, challengeId = 92480, text = "Quiero pedir la cocina", correct = false, errorTag = "WRONG_FORM"),

ChallengeOptionEntity(id = 950000, challengeId = 92500, text = "La propina está incluida en la cuenta.", romaji = "la propˈina estˈa inklˈuidha en la kwˈenta", correct = true, audioSrc = "asset:///audio/es/la_propina_esta_incluida.ogg"),
                ChallengeOptionEntity(id = 950001, challengeId = 92500, text = "La propinas está incluida", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 950002, challengeId = 92500, text = "La propina está incluidos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 950003, challengeId = 92500, text = "La propina está incluida en el cuenta", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 950400, challengeId = 92520, text = "By card", correct = true),
                ChallengeOptionEntity(id = 950401, challengeId = 92520, text = "In cash", correct = false),
                ChallengeOptionEntity(id = 950402, challengeId = 92520, text = "With a voucher", correct = false),
                ChallengeOptionEntity(id = 950403, challengeId = 92520, text = "The speaker does not say", correct = false),
            ),
        ),
        // ---------------------------------------------------------------------
        UnitPayload(
            unit = UnitEntity(
                id = 37,
                courseId = 1,
                title = "Unit 18: Out in the World",
                description = "Getting around, the city, and the working week",
                orderIndex = 17,
            ),
            lessons = listOf(
                LessonEntity(id = 903, unitId = 37, title = "Lesson 39: Travel, Transport and Directions", orderIndex = 0),
                LessonEntity(id = 904, unitId = 37, title = "Lesson 40: The City, Places and the Weather", orderIndex = 1),
                LessonEntity(id = 905, unitId = 37, title = "Lesson 41: Work, Jobs and the Workplace", orderIndex = 2),
            ),
            challenges = listOf(

                ChallengeEntity(
                    id = 93000, lessonId = 903, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (4 pairs)",
                    orderIndex = 0,
                ),

                ChallengeEntity(
                    id = 93020, lessonId = 903, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (4 pairs)",
                    orderIndex = 1,
                ),

                ChallengeEntity(
                    id = 93040, lessonId = 903, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (4 pairs)",
                    orderIndex = 2,
                    ruleText = "A ticket is el billete in Spain and el boleto in Latin America: the same noun, used in either region, so the English side is the same for both. Match each word to its meaning.",
                ),

                ChallengeEntity(
                    id = 93060, lessonId = 903, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (4 pairs)",
                    orderIndex = 3,
                ),

                ChallengeEntity(
                    id = 93080, lessonId = 903, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (4 pairs)",
                    orderIndex = 4,
                ),

                ChallengeEntity(
                    id = 93100, lessonId = 903, type = ChallengeType.SELECT,
                    question = "Which one means 'the bus stop'?",
                    orderIndex = 5,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " la parada is the answer. el parada is the wrong person or number for the slot. las parada is the wrong person or number for the slot. la paradas is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 93120, lessonId = 903, type = ChallengeType.SELECT,
                    question = "Which one means 'the taxi rank'?",
                    orderIndex = 6,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " la parada de taxis is the answer. el parada de taxis is the wrong person or number for the slot. la paradas de taxis is the wrong person or number for the slot. la parada de taxi is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 93140, lessonId = 903, type = ChallengeType.SELECT,
                    question = "Which one means 'the flight number'?",
                    orderIndex = 7,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " el número de vuelo is the answer. la número de vuelo is the wrong person or number for the slot. el número de vuelos is a form of the word that cannot fill the slot. los número de vuelo is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 93160, lessonId = 903, type = ChallengeType.SELECT,
                    question = "Which one means 'to get off'?",
                    orderIndex = 8,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " bajarse is the answer. bajar is a form of the word that cannot fill the slot. bajarse mal is a form of the word that cannot fill the slot. bajamos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 93180, lessonId = 903, type = ChallengeType.SELECT,
                    question = "Which one means 'a one-way ticket'?",
                    orderIndex = 9,
                    grammaticalFocus = "es.vocab.travel",
                    // "everyday word in" states a preference, not ownership: el boleto is a
                    // normal Spanish word too. Do not rewrite this as "the Latin American form",
                    // which reads as excluding Spain.
                    ruleText = " un billete de ida is the answer, and un boleto de ida names the same ticket: el boleto is the everyday word in Latin America, and el billete is the everyday word in Spain.\nun billete de idas is a form of the word that cannot fill the slot. un billete de vuelta is a form of the word that cannot fill the slot. una billete de ida is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 93200, lessonId = 903, type = ChallengeType.SELECT,
                    question = "Which one means 'a return ticket'?",
                    orderIndex = 10,
                    grammaticalFocus = "es.vocab.travel",
                    // Same reasoning as challenge 93180: a preference, not an exclusion.
                    ruleText = " un billete de ida y vuelta is the answer, and un boleto de ida y vuelta names the same return ticket: el boleto is the everyday word in Latin America, and el billete is the everyday word in Spain.\nun billete de ida y ida is a form of the word that cannot fill the slot. una billete de ida y vuelta is the wrong person or number for the slot. un billete de ida y vueltas is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 93220, lessonId = 903, type = ChallengeType.SELECT,
                    question = "Which one means 'to miss the train'?",
                    orderIndex = 11,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " perder el tren is the answer. perder el tren is a form of the word that cannot fill the slot. perdieron el tren puts the verb in a tense the sentence does not ask for. pierde el tren is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 93240, lessonId = 903, type = ChallengeType.SELECT,
                    question = "Which one means 'to change trains'?",
                    orderIndex = 12,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " cambiar de tren is the answer. cambiar del tren is a form of the word that cannot fill the slot. cambia de tren is the wrong person or number for the slot. cambiar del tren is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 93260, lessonId = 903, type = ChallengeType.SELECT,
                    question = "Which one means 'the journey takes an hour'?",
                    orderIndex = 13,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " el viaje dura una hora is the answer. el viaje duran una hora is the wrong person or number for the slot. el viaje dura unas hora is a form of the word that cannot fill the slot. el viajes dura una hora is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 93280, lessonId = 903, type = ChallengeType.SELECT,
                    question = "Which one means 'to check in (at the airport)'?",
                    orderIndex = 14,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " facturar el equipaje is the answer. facturar el equipaje is a form of the word that cannot fill the slot. factura el equipaje is the wrong person or number for the slot. facturar la equipaje is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 93300, lessonId = 903, type = ChallengeType.SELECT,
                    question = "Which one means 'the terminal'?",
                    orderIndex = 15,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " la terminal is the answer. el terminal is the wrong person or number for the slot. las terminal is the wrong person or number for the slot. la terminales is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 93320, lessonId = 903, type = ChallengeType.SELECT,
                    question = "Which one means 'a long journey'?",
                    orderIndex = 16,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " un viaje largo is the answer. un viaje largos is a form of the word that cannot fill the slot. un viaje larga is a form of the word that cannot fill the slot. una viaje largo is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 93340, lessonId = 903, type = ChallengeType.CONJUGATE,
                    question = "Which present form of llegar goes with 'yo'?",
                    orderIndex = 17,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "llegar is a regular -ar verb, and the yo ending is -o: llego. llego is the answer. llegas is the wrong person or number for the slot. llega is the wrong person or number for the slot. llegamos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 93360, lessonId = 903, type = ChallengeType.CONJUGATE,
                    question = "Which present form of salir goes with 'tú'?",
                    orderIndex = 18,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "salir changes the stem to salg- in the yo form only, and the tú ending is -es: sales. sales is the answer. salgo is the wrong person or number for the slot. sale is the wrong person or number for the slot. salimos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 93380, lessonId = 903, type = ChallengeType.CONJUGATE,
                    question = "Which present form of conducir goes with 'nosotros'?",
                    orderIndex = 19,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "conducir turns c into z before a stressed o, but nosotros keeps the plain stem: conducimos. conducimos is the answer. conduzco is the wrong person or number for the slot. conduces is the wrong person or number for the slot. conduce is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 93400, lessonId = 903, type = ChallengeType.CONJUGATE,
                    question = "Which present form of atravesar goes with 'ellos'?",
                    orderIndex = 20,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "atravesar changes e- to ie- in the present, and ellos takes -n: atraviesan. atraviesan is the answer. atravieso is the wrong person or number for the slot. atraviesas is the wrong person or number for the slot. atraviesamos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 93420, lessonId = 903, type = ChallengeType.FILL_BLANK,
                    question = "El tren ___ a las ocho en punto.",
                    orderIndex = 21,
                    grammaticalFocus = "es.vocab.travel",
                    acceptedAnswers = "sale|saldrá",
                    ruleText = "A timetable is in the present: el tren sale a las ocho. saldrá would be used for a departure already decided and stated as a future event; neither infinitive nor a subjunctive can fill this slot. sale is the answer. salir is a form of the word that cannot fill the slot. salimos is the wrong person or number for the slot. salga is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 93440, lessonId = 903, type = ChallengeType.FILL_BLANK,
                    question = "En el andén ___ mucha gente esperando.",
                    orderIndex = 22,
                    grammaticalFocus = "es.vocab.travel",
                    acceptedAnswers = "hay",
                    ruleText = "There is one place with people in it, so Spanish uses the singular hay, and the crowd is still there in the present. haber and he are forms of the verb rather than of the phrase. hay is the answer. han is the wrong person or number for the slot. haber is a form of the word that cannot fill the slot. he is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 93460, lessonId = 903, type = ChallengeType.FILL_BLANK,
                    question = "¿___ el billete en la ventanilla o por internet?",
                    orderIndex = 23,
                    grammaticalFocus = "es.vocab.travel",
                    acceptedAnswers = "compras",
                    ruleText = "The question is put to the listener, so the second person singular of comprar is the answer: compras. comprado would be a participle and comprar the infinitive. compras is the answer. compro is the wrong person or number for the slot. compró puts the verb in a tense the sentence does not ask for. comprar is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 93480, lessonId = 903, type = ChallengeType.FILL_BLANK,
                    question = "Para llegar al puerto, ___ todo recto dos calles.",
                    orderIndex = 24,
                    grammaticalFocus = "es.vocab.travel",
                    acceptedAnswers = "siga|sigan",
                    ruleText = "A polite direction to one person uses the usted command of seguir, which is siga. sigan is the same command for a group, sigo is the yo form, and seguir is the infinitive. siga is the answer. sigo is the wrong person or number for the slot. seguir is a form of the word that cannot fill the slot. siguen is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 93500, lessonId = 903, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/el_avion_llega_tarde.ogg",
                    orderIndex = 25,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "The clip says El avión llega tarde por la niebla. El avión llega tarde por la niebla. is the answer. El avión llegan tarde is the wrong person or number for the slot. El avión llega tardes is a form of the word that cannot fill the slot. El avión llega temprano is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 93520, lessonId = 903, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/gire_a_la_derecha_en_la_esquina.ogg",
                    orderIndex = 26,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "The clip says Gire a la derecha en la esquina. Gire a la derecha en la esquina. is the answer. Gire a la derecha en las esquinas is a form of the word that cannot fill the slot. Gire a la derecha en la esquino is the wrong person or number for the slot. Gire a la izquierda en la esquina is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 93540,
                                  romaji = "el tren perdhˈidho ˈayer yeghˈe a la estasˈion bˈeinte minˈutos tˈardhe el tren a madhrˈid yˈa abhˈia salˈidho del andˈen la señˈora de la bentanˈiya me dˈijo kˈe abhˈia ˈotro tren en una ˈora al finˈal yeghˈe a kˈasa a las ˈonse de la nˈoche", lessonId = 903, type = ChallengeType.STORY,
                    question = "El tren perdido\nAyer llegué a la estación veinte minutos tarde.\nEl tren a Madrid ya había salido del andén.\nLa señora de la ventanilla me dijo que había otro tren en una hora.\nAl final llegué a casa a las once de la noche.\n\n❓ Why did the speaker miss the train?",
                    audioSrc = "asset:///audio/es/story_903.ogg",
                    orderIndex = 27,
                ),

                ChallengeEntity(
                    id = 94000, lessonId = 904, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (4 pairs)",
                    orderIndex = 0,
                ),

                ChallengeEntity(
                    id = 94020, lessonId = 904, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (4 pairs)",
                    orderIndex = 1,
                ),

                ChallengeEntity(
                    id = 94040, lessonId = 904, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (4 pairs)",
                    orderIndex = 2,
                ),

                ChallengeEntity(
                    id = 94060, lessonId = 904, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (4 pairs)",
                    orderIndex = 3,
                ),

                ChallengeEntity(
                    id = 94080, lessonId = 904, type = ChallengeType.SELECT,
                    question = "Which one means 'the public garden'?",
                    orderIndex = 4,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " el jardín público is the answer. la jardín público is the wrong person or number for the slot. el jardín pública is a form of the word that cannot fill the slot. el jardín públicos is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 94100, lessonId = 904, type = ChallengeType.SELECT,
                    question = "Which one means 'the old town'?",
                    orderIndex = 5,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " el casco antiguo is the answer. la casco antiguo is the wrong person or number for the slot. el casco antiguos is a form of the word that cannot fill the slot. el casco antigua is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 94120, lessonId = 904, type = ChallengeType.SELECT,
                    question = "Which one means 'it is cloudy'?",
                    orderIndex = 6,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " está nublado is the answer. están nublados is the wrong person or number for the slot. está nublada is a form of the word that cannot fill the slot. está nublados is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 94140, lessonId = 904, type = ChallengeType.SELECT,
                    question = "Which one means 'it is sunny'?",
                    orderIndex = 7,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " hace sol is the answer. hacen sol is the wrong person or number for the slot. hace soles is a form of the word that cannot fill the slot. hace soleado is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 94160, lessonId = 904, type = ChallengeType.SELECT,
                    question = "Which one means 'it is snowing'?",
                    orderIndex = 8,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " está nevando is the answer. están nevando is the wrong person or number for the slot. está nevadas is a form of the word that cannot fill the slot. están nevadas is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 94180, lessonId = 904, type = ChallengeType.SELECT,
                    question = "Which one means 'the weather forecast'?",
                    orderIndex = 9,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " el pronóstico del tiempo is the answer. el pronóstico del tiempos is a form of the word that cannot fill the slot. la pronóstico del tiempo is the wrong person or number for the slot. el pronósitico del tiempo is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 94200, lessonId = 904, type = ChallengeType.SELECT,
                    question = "Which one means 'a crowded street'?",
                    orderIndex = 10,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " una calle llena de gente is the answer. una calle lleno de gente is a form of the word that cannot fill the slot. una calles llena de gente is the wrong person or number for the slot. una calle llen de gente is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 94220, lessonId = 904, type = ChallengeType.SELECT,
                    question = "Which one means 'a wide avenue'?",
                    orderIndex = 11,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " una avenida ancha is the answer. una avenida anchas is a form of the word that cannot fill the slot. una avenida ancho is a form of the word that cannot fill the slot. un avenida ancha is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 94240, lessonId = 904, type = ChallengeType.SELECT,
                    question = "Which one means 'the launderette'?",
                    orderIndex = 12,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " la lavandería is the answer. el lavandería is the wrong person or number for the slot. la lavanderías is the wrong person or number for the slot. la lavanderes is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 94260, lessonId = 904, type = ChallengeType.SELECT,
                    question = "Which one means 'a ticket machine'?",
                    orderIndex = 13,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " la máquina de billetes is the answer. la máquinas de billetes is the wrong person or number for the slot. la máquina de billete is a form of the word that cannot fill the slot. el máquina de billetes is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 94280, lessonId = 904, type = ChallengeType.SELECT,
                    question = "Which one means 'to get a cold'?",
                    orderIndex = 14,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " constiparse is the answer. constipamos is the wrong person or number for the slot. constiparse bien is a form of the word that cannot fill the slot. constipada is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 94300, lessonId = 904, type = ChallengeType.SELECT,
                    question = "Which one means 'it is windy'?",
                    orderIndex = 15,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " hace viento is the answer. hacen viento is the wrong person or number for the slot. hace vientos is a form of the word that cannot fill the slot. hace ventoso is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 94320, lessonId = 904, type = ChallengeType.CONJUGATE,
                    question = "Which present form of llover goes with 'yo'?",
                    orderIndex = 16,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "llover changes o- to ue- in the present, and the yo form is lluevo. lluevo is the answer. lloves is the wrong person or number for the slot. llueve is the wrong person or number for the slot. llovemos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 94340, lessonId = 904, type = ChallengeType.CONJUGATE,
                    question = "Which present form of hacer goes with 'tú'?",
                    orderIndex = 17,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "hacer has an irregular yo form, hago, and the tú ending is -es: haces. haces is the answer. hago is the wrong person or number for the slot. hace is the wrong person or number for the slot. hacemos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 94360, lessonId = 904, type = ChallengeType.CONJUGATE,
                    question = "Which present form of soler goes with 'ellos'?",
                    orderIndex = 18,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "soler changes o- to ue- in the present, and ellos takes -n: suelen. suelen is the answer. suelo is the wrong person or number for the slot. sueles is the wrong person or number for the slot. suélense is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 94380, lessonId = 904, type = ChallengeType.CONJUGATE,
                    question = "Which present form of nublarse goes with 'nosotros'?",
                    orderIndex = 19,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "nublarse is a regular -ar verb used impersonally for the weather, and nosotros keeps the plain stem: nublamos. nublamos is the answer. nublémonos is a form of the word that cannot fill the slot. nublo is the wrong person or number for the slot. nublas is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 94400, lessonId = 904, type = ChallengeType.FILL_BLANK,
                    question = "En el norte, en invierno, ___ mucho frío.",
                    orderIndex = 20,
                    grammaticalFocus = "es.vocab.travel",
                    acceptedAnswers = "hace",
                    ruleText = "In Spain and Latin America the weather takes the third person singular of hacer, and a habit described for every winter is present: hace. hace is the answer. hacen is the wrong person or number for the slot. hacerse is a form of the word that cannot fill the slot. hacéis is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 94420, lessonId = 904, type = ChallengeType.FILL_BLANK,
                    question = "Ayer ___ todo el día y no salimos.",
                    orderIndex = 21,
                    grammaticalFocus = "es.vocab.travel",
                    acceptedAnswers = "llovió|llovía",
                    ruleText = "Ayer pins the rain in the past and the sentence has one completed event, so the preterite is the form: llovió. The imperfect llovía would describe the whole day as background rather than one event. llovió is the answer. llueve puts the verb in a tense the sentence does not ask for. llueven puts the verb in a tense the sentence does not ask for. llover is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 94440, lessonId = 904, type = ChallengeType.FILL_BLANK,
                    question = "El museo ___ los lunes por la tarde.",
                    orderIndex = 22,
                    grammaticalFocus = "es.vocab.travel",
                    acceptedAnswers = "cierra|cerraba",
                    ruleText = "El museo is singular, so cerrar gives cierra in the third person. A timetable that still holds is present, and cerraba would describe a museum that no longer keeps those hours. cierra is the answer. cierran is the wrong person or number for the slot. cerrar is a form of the word that cannot fill the slot. cerramos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 94460, lessonId = 904, type = ChallengeType.FILL_BLANK,
                    question = "La plaza se ___ de mucha gente el fin de semana.",
                    orderIndex = 23,
                    grammaticalFocus = "es.vocab.travel",
                    acceptedAnswers = "llena|llenaba",
                    ruleText = "La plaza is singular, and the reflexive verb agrees with it: se llena. La gente is a collective noun and does not make the verb plural. llena is the answer. llenan is the wrong person or number for the slot. llenar is a form of the word that cannot fill the slot. llenaron puts the verb in a tense the sentence does not ask for.",
                ),

                ChallengeEntity(
                    id = 94480, lessonId = 904, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/hoy_llueve_en_toda_la_ciudad.ogg",
                    orderIndex = 24,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "The clip says Hoy llueve en toda la ciudad. Hoy llueve en toda la ciudad. is the answer. Hoy llueven en toda la ciudad is the wrong person or number for the slot. Hoy llueve en toda la ciudades is a form of the word that cannot fill the slot. Hoy nieva en toda la ciudad is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 94500, lessonId = 904, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/el_puente_esta_cerrado_por_lluvia.ogg",
                    orderIndex = 25,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "The clip says El puente está cerrado por la lluvia. El puente está cerrado por la lluvia. is the answer. El puentes está cerrado por la lluvia is the wrong person or number for the slot. El puente están cerrados por la lluvia is the wrong person or number for the slot. El puente está cerrado por las lluvia is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 94520,
                                  romaji = "un domˈingo en la siudhˈad el domˈingo por la mañˈana fˈui al sˈentro kon mi amˈigha asˈia buen tˈiempo y la plˈasa estˈabha yˈena de jˈente komprˈemos kafˈe y bˈoyos en una panadherˈia de la eskˈina por la tˈardhe bisitˈamos el musˈeo kˈe es grˈatis el primˈer domˈingo del mes", lessonId = 904, type = ChallengeType.STORY,
                    question = "Un domingo en la ciudad\nEl domingo por la mañana fui al centro con mi amiga.\nHacía buen tiempo y la plaza estaba llena de gente.\nCompremos café y bollos en una panadería de la esquina.\nPor la tarde visitamos el museo, que es gratis el primer domingo del mes.\n\n❓ How much did the visit to the museum cost?",
                    audioSrc = "asset:///audio/es/story_904.ogg",
                    orderIndex = 26,
                ),

                ChallengeEntity(
                    id = 95000, lessonId = 905, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (4 pairs)",
                    orderIndex = 0,
                ),

                ChallengeEntity(
                    id = 95020, lessonId = 905, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (4 pairs)",
                    orderIndex = 1,
                ),

                ChallengeEntity(
                    id = 95040, lessonId = 905, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (4 pairs)",
                    orderIndex = 2,
                ),

                ChallengeEntity(
                    id = 95060, lessonId = 905, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (4 pairs)",
                    orderIndex = 3,
                ),

                ChallengeEntity(
                    id = 95080, lessonId = 905, type = ChallengeType.MATCH_PAIRS,
                    question = "Match each word to its meaning (4 pairs)",
                    orderIndex = 4,
                ),

                ChallengeEntity(
                    id = 95100, lessonId = 905, type = ChallengeType.SELECT,
                    question = "Which one means 'the staff'?",
                    orderIndex = 5,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " el personal is the answer. la personal is the wrong person or number for the slot. los personal is the wrong person or number for the slot. el personals is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 95120, lessonId = 905, type = ChallengeType.SELECT,
                    question = "Which one means 'the deadline'?",
                    orderIndex = 6,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " la fecha límite is the answer. el fecha límite is the wrong person or number for the slot. la fecha límites is a form of the word that cannot fill the slot. la fecha limite is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 95140, lessonId = 905, type = ChallengeType.SELECT,
                    question = "Which one means 'to hand in a report'?",
                    orderIndex = 7,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " entregar un informe is the answer. entregar un informes is a form of the word that cannot fill the slot. entrega un informe is the wrong person or number for the slot. entregar la informe is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 95160, lessonId = 905, type = ChallengeType.SELECT,
                    question = "Which one means 'to take a day off'?",
                    orderIndex = 8,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " pedir un día libre is the answer. pedir un día libres is a form of the word that cannot fill the slot. pide un día libre is the wrong person or number for the slot. pedir un día de libre is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 95180, lessonId = 905, type = ChallengeType.SELECT,
                    question = "Which one means 'a fixed-term contract'?",
                    orderIndex = 9,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " un contrato temporal is the answer. un contrato temporales is a form of the word that cannot fill the slot. una contrato temporal is the wrong person or number for the slot. un contrato de temporal is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 95200, lessonId = 905, type = ChallengeType.SELECT,
                    question = "Which one means 'to get promoted'?",
                    orderIndex = 10,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " ascender de puesto is the answer. ascender del puesto is a form of the word that cannot fill the slot. asciende de puesto is the wrong person or number for the slot. ascender de puestos is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 95220, lessonId = 905, type = ChallengeType.SELECT,
                    question = "Which one means 'to work overtime'?",
                    orderIndex = 11,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " hacer horas extra is the answer. hacer horas extras is a form of the word that cannot fill the slot. hace horas extra is the wrong person or number for the slot. hacer la hora extra is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 95240, lessonId = 905, type = ChallengeType.SELECT,
                    question = "Which one means 'a full-time job'?",
                    orderIndex = 12,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " un trabajo a tiempo completo is the answer. un trabajo a tiempo completos is a form of the word that cannot fill the slot. una trabajo a tiempo completo is the wrong person or number for the slot. un trabajo a tiempo incompletos is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 95260, lessonId = 905, type = ChallengeType.SELECT,
                    question = "Which one means 'to apply for a job'?",
                    orderIndex = 13,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " solicitar un empleo is the answer. solicitar un empleos is a form of the word that cannot fill the slot. solicita un empleo is the wrong person or number for the slot. solicitar una empleo is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 95280, lessonId = 905, type = ChallengeType.SELECT,
                    question = "Which one means 'the work experience'?",
                    orderIndex = 14,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " la experiencia laboral is the answer. el experiencia laboral is the wrong person or number for the slot. la experiencia laborables is a form of the word that cannot fill the slot. la experiencias laboral is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 95300, lessonId = 905, type = ChallengeType.SELECT,
                    question = "Which one means 'to hand in one's notice'?",
                    orderIndex = 15,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " presentar la dimisión is the answer. presentar la dimisiones is a form of the word that cannot fill the slot. presentar el dimisión is the wrong person or number for the slot. presentar la dimisiónes is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 95320, lessonId = 905, type = ChallengeType.SELECT,
                    question = "Which one means 'a colleague'?",
                    orderIndex = 16,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = " un compañero de trabajo is the answer. un compañero de trabajos is a form of the word that cannot fill the slot. una compañero de trabajo is the wrong person or number for the slot. un compañero del trabajo is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 95340, lessonId = 905, type = ChallengeType.CONJUGATE,
                    question = "Which present form of entregar goes with 'yo'?",
                    orderIndex = 17,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "entregar is a regular -ar verb, and the yo ending is -o: entrego. entrego is the answer. entregas is the wrong person or number for the slot. entrega is the wrong person or number for the slot. entregamos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 95360, lessonId = 905, type = ChallengeType.CONJUGATE,
                    question = "Which present form of soler goes with 'yo'?",
                    orderIndex = 18,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "soler changes o- to ue- in the present, so the yo form is suelo. suelo is the answer. sueles is the wrong person or number for the slot. suele is the wrong person or number for the slot. solemos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 95380, lessonId = 905, type = ChallengeType.CONJUGATE,
                    question = "Which present form of seguir goes with 'tú'?",
                    orderIndex = 19,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "seguir changes e- to ie- in the present, and the tú ending is -es: sigues. sigues is the answer. sigo is the wrong person or number for the slot. sigue is the wrong person or number for the slot. seguimos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 95400, lessonId = 905, type = ChallengeType.SELECT,
                    question = "Which present form of reunirse goes with 'ellos'?",
                    orderIndex = 20,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "reunirse is reflexive, and reunir is irregular in the stressed syllable in the present, so ellos is se reúnen. se reúnen is the answer. se reúne is the wrong person or number for the slot. se reúno is the wrong person or number for the slot. nos reunimos is the wrong person or number for the slot.",
                ),

                ChallengeEntity(
                    id = 95420, lessonId = 905, type = ChallengeType.FILL_BLANK,
                    question = "Mi jefe ___ en una oficina de Madrid.",
                    orderIndex = 21,
                    grammaticalFocus = "es.vocab.travel",
                    acceptedAnswers = "trabaja",
                    ruleText = "Mi jefe is third person singular, so the present of trabajar is the answer: trabaja, and neither yo nor a plural can be right for the subject given. trabaja is the answer. trabajo is the wrong person or number for the slot. trabajan is the wrong person or number for the slot. trabajar is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 95440, lessonId = 905, type = ChallengeType.FILL_BLANK,
                    question = "¿Cuánto tiempo ___ en esta empresa?",
                    orderIndex = 22,
                    grammaticalFocus = "es.vocab.travel",
                    acceptedAnswers = "llevas|estás",
                    ruleText = "The question is put to one person, so the second person singular is the answer. You can say how long you have been there with llevar or with estar, and both are correct here. llevas is the answer. lleva is the wrong person or number for the slot. llevamos is the wrong person or number for the slot. llevar is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 95460, lessonId = 905, type = ChallengeType.FILL_BLANK,
                    question = "Tengo que ___ un informe antes de las cinco.",
                    orderIndex = 23,
                    grammaticalFocus = "es.vocab.travel",
                    acceptedAnswers = "enviar|mandar",
                    ruleText = "After tengo que the verb stays in the infinitive, because que already marks the whole obligation: tengo que enviar. enviar and mandar are both ways to say to send. enviar is the answer. envías is the wrong person or number for the slot. enviaré is a form of the word that cannot fill the slot. enviarlo is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 95480, lessonId = 905, type = ChallengeType.FILL_BLANK,
                    question = "Los clientes ___ su cita en recepción.",
                    orderIndex = 24,
                    grammaticalFocus = "es.vocab.travel",
                    acceptedAnswers = "tienen|tenían",
                    ruleText = "Los clientes is plural, so the third person plural of tener is the answer: tienen. Tenían would put the booking in the past, and neither singular nor nosotros agrees with a group of customers. tienen is the answer. tiene is the wrong person or number for the slot. tenemos is the wrong person or number for the slot. tener is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 95500, lessonId = 905, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/mi_jefe_trabaja_desde_casa.ogg",
                    orderIndex = 25,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "The clip says Mi jefe trabaja desde casa desde marzo. Mi jefe trabaja desde casa desde marzo. is the answer. Mi jefes trabaja desde casa desde marzo is the wrong person or number for the slot. Mi jefe trabajan desde casa desde marzo is the wrong person or number for the slot. Mi jefe trabaja desde casa desde mayo is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 95520, lessonId = 905, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/es/la_reunion_se_ha_aplazado.ogg",
                    orderIndex = 26,
                    grammaticalFocus = "es.vocab.travel",
                    ruleText = "The clip says La reunión se ha aplazado hasta el viernes. La reunión se ha aplazado hasta el viernes. is the answer. La reunión se han aplazado is the wrong person or number for the slot. La reunión se ha aplazada is a form of the word that cannot fill the slot. La reunión se han aplazada is a form of the word that cannot fill the slot.",
                ),

                ChallengeEntity(
                    id = 95540,
                                  romaji = "el primˈer dˈia el lˈunes fue mi primˈer dˈia en la emprˈesa mi jˈefe me enseñˈo la ofisˈina y me presentˈo al rrˈesto del ekˈipo a las dˈose tubhˈimos nˈuestra primˈera rreunˈion de la semˈana terminˈe el dˈia kon trˈes korrˈeos sin rrespondˈer", lessonId = 905, type = ChallengeType.STORY,
                    question = "El primer día\nEl lunes fue mi primer día en la empresa.\nMi jefe me enseñó la oficina y me presentó al resto del equipo.\nA las doce tuvimos nuestra primera reunión de la semana.\nTerminé el día con tres correos sin responder.\n\n❓ What did the speaker do with the rest of the team?",
                    audioSrc = "asset:///audio/es/story_905.ogg",
                    orderIndex = 27,
                ),
            ),
            options = listOf(

                ChallengeOptionEntity(id = 960000, challengeId = 93000, text = "el avión", correct = true),
                ChallengeOptionEntity(id = 960001, challengeId = 93000, text = "the plane", correct = true),
                ChallengeOptionEntity(id = 960002, challengeId = 93000, text = "el autobús", correct = true),
                ChallengeOptionEntity(id = 960003, challengeId = 93000, text = "the bus", correct = true),
                ChallengeOptionEntity(id = 960004, challengeId = 93000, text = "el metro", correct = true),
                ChallengeOptionEntity(id = 960005, challengeId = 93000, text = "the underground", correct = true),
                ChallengeOptionEntity(id = 960006, challengeId = 93000, text = "el coche", correct = true),
                ChallengeOptionEntity(id = 960007, challengeId = 93000, text = "the car", correct = true),

                ChallengeOptionEntity(id = 960400, challengeId = 93020, text = "el barco", correct = true),
                ChallengeOptionEntity(id = 960401, challengeId = 93020, text = "the boat", correct = true),
                ChallengeOptionEntity(id = 960402, challengeId = 93020, text = "el taxi", correct = true),
                ChallengeOptionEntity(id = 960403, challengeId = 93020, text = "the taxi", correct = true),
                ChallengeOptionEntity(id = 960404, challengeId = 93020, text = "la bicicleta", correct = true),
                ChallengeOptionEntity(id = 960405, challengeId = 93020, text = "the bicycle", correct = true),
                ChallengeOptionEntity(id = 960406, challengeId = 93020, text = "el tranvía", correct = true),
                ChallengeOptionEntity(id = 960407, challengeId = 93020, text = "the tram", correct = true),

                ChallengeOptionEntity(id = 960800, challengeId = 93040, text = "el billete", correct = true),
                ChallengeOptionEntity(id = 960801, challengeId = 93040, text = "the ticket", correct = true),
                ChallengeOptionEntity(id = 960802, challengeId = 93040, text = "el andén", correct = true),
                ChallengeOptionEntity(id = 960803, challengeId = 93040, text = "the platform", correct = true),
                ChallengeOptionEntity(id = 960804, challengeId = 93040, text = "la maleta", correct = true),
                ChallengeOptionEntity(id = 960805, challengeId = 93040, text = "the suitcase", correct = true),
                ChallengeOptionEntity(id = 960806, challengeId = 93040, text = "el pasaporte", correct = true),
                ChallengeOptionEntity(id = 960807, challengeId = 93040, text = "the passport", correct = true),

                ChallengeOptionEntity(id = 961200, challengeId = 93060, text = "el vuelo", correct = true),
                ChallengeOptionEntity(id = 961201, challengeId = 93060, text = "the flight", correct = true),
                ChallengeOptionEntity(id = 961202, challengeId = 93060, text = "la llegada", correct = true),
                ChallengeOptionEntity(id = 961203, challengeId = 93060, text = "the arrival", correct = true),
                ChallengeOptionEntity(id = 961204, challengeId = 93060, text = "la salida", correct = true),
                ChallengeOptionEntity(id = 961205, challengeId = 93060, text = "the departure", correct = true),
                ChallengeOptionEntity(id = 961206, challengeId = 93060, text = "el retraso", correct = true),
                ChallengeOptionEntity(id = 961207, challengeId = 93060, text = "the delay", correct = true),

                ChallengeOptionEntity(id = 961600, challengeId = 93080, text = "el conductor", correct = true),
                ChallengeOptionEntity(id = 961601, challengeId = 93080, text = "the driver", correct = true),
                ChallengeOptionEntity(id = 961602, challengeId = 93080, text = "el pasajero", correct = true),
                ChallengeOptionEntity(id = 961603, challengeId = 93080, text = "the passenger", correct = true),
                ChallengeOptionEntity(id = 961604, challengeId = 93080, text = "la ventanilla", correct = true),
                ChallengeOptionEntity(id = 961605, challengeId = 93080, text = "the ticket window", correct = true),
                ChallengeOptionEntity(id = 961606, challengeId = 93080, text = "el trayecto", correct = true),
                ChallengeOptionEntity(id = 961607, challengeId = 93080, text = "the journey", correct = true),

                ChallengeOptionEntity(id = 962000, challengeId = 93100, text = "la parada", correct = true),
                ChallengeOptionEntity(id = 962001, challengeId = 93100, text = "el parada", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 962002, challengeId = 93100, text = "las parada", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 962003, challengeId = 93100, text = "la paradas", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 962400, challengeId = 93120, text = "la parada de taxis", correct = true),
                ChallengeOptionEntity(id = 962401, challengeId = 93120, text = "el parada de taxis", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 962402, challengeId = 93120, text = "la paradas de taxis", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 962403, challengeId = 93120, text = "la parada de taxi", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 962800, challengeId = 93140, text = "el número de vuelo", correct = true),
                ChallengeOptionEntity(id = 962801, challengeId = 93140, text = "la número de vuelo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 962802, challengeId = 93140, text = "el número de vuelos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 962803, challengeId = 93140, text = "los número de vuelo", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 963200, challengeId = 93160, text = "bajarse", correct = true),
                ChallengeOptionEntity(id = 963201, challengeId = 93160, text = "bajar", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 963202, challengeId = 93160, text = "bajarse mal", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 963203, challengeId = 93160, text = "bajamos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 963600, challengeId = 93180, text = "un billete de ida", correct = true),
                ChallengeOptionEntity(id = 963604, challengeId = 93180, text = "un boleto de ida", correct = true),
                ChallengeOptionEntity(id = 963601, challengeId = 93180, text = "un billete de idas", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 963602, challengeId = 93180, text = "un billete de vuelta", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 963603, challengeId = 93180, text = "una billete de ida", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 964000, challengeId = 93200, text = "un billete de ida y vuelta", correct = true),
                ChallengeOptionEntity(id = 964004, challengeId = 93200, text = "un boleto de ida y vuelta", correct = true),
                ChallengeOptionEntity(id = 964001, challengeId = 93200, text = "un billete de ida y ida", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 964002, challengeId = 93200, text = "una billete de ida y vuelta", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 964003, challengeId = 93200, text = "un billete de ida y vueltas", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 964400, challengeId = 93220, text = "perder el tren", correct = true),
                ChallengeOptionEntity(id = 964401, challengeId = 93220, text = "perder el tren", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 964402, challengeId = 93220, text = "perdieron el tren", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 964403, challengeId = 93220, text = "pierde el tren", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 964800, challengeId = 93240, text = "cambiar de tren", correct = true),
                ChallengeOptionEntity(id = 964801, challengeId = 93240, text = "cambiar del tren", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 964802, challengeId = 93240, text = "cambia de tren", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 964803, challengeId = 93240, text = "cambiar del tren", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 965200, challengeId = 93260, text = "el viaje dura una hora", correct = true),
                ChallengeOptionEntity(id = 965201, challengeId = 93260, text = "el viaje duran una hora", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 965202, challengeId = 93260, text = "el viaje dura unas hora", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 965203, challengeId = 93260, text = "el viajes dura una hora", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 965600, challengeId = 93280, text = "facturar el equipaje", correct = true),
                ChallengeOptionEntity(id = 965601, challengeId = 93280, text = "facturar el equipaje", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 965602, challengeId = 93280, text = "factura el equipaje", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 965603, challengeId = 93280, text = "facturar la equipaje", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 966000, challengeId = 93300, text = "la terminal", correct = true),
                ChallengeOptionEntity(id = 966001, challengeId = 93300, text = "el terminal", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 966002, challengeId = 93300, text = "las terminal", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 966003, challengeId = 93300, text = "la terminales", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 966400, challengeId = 93320, text = "un viaje largo", correct = true),
                ChallengeOptionEntity(id = 966401, challengeId = 93320, text = "un viaje largos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 966402, challengeId = 93320, text = "un viaje larga", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 966403, challengeId = 93320, text = "una viaje largo", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 966800, challengeId = 93340, text = "llego", correct = true),
                ChallengeOptionEntity(id = 966801, challengeId = 93340, text = "llegas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 966802, challengeId = 93340, text = "llega", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 966803, challengeId = 93340, text = "llegamos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 967200, challengeId = 93360, text = "sales", correct = true),
                ChallengeOptionEntity(id = 967201, challengeId = 93360, text = "salgo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 967202, challengeId = 93360, text = "sale", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 967203, challengeId = 93360, text = "salimos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 967600, challengeId = 93380, text = "conducimos", correct = true),
                ChallengeOptionEntity(id = 967601, challengeId = 93380, text = "conduzco", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 967602, challengeId = 93380, text = "conduces", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 967603, challengeId = 93380, text = "conduce", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 968000, challengeId = 93400, text = "atraviesan", correct = true),
                ChallengeOptionEntity(id = 968001, challengeId = 93400, text = "atravieso", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 968002, challengeId = 93400, text = "atraviesas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 968003, challengeId = 93400, text = "atraviesamos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 968400, challengeId = 93420, text = "sale", correct = true),
                ChallengeOptionEntity(id = 968401, challengeId = 93420, text = "salir", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 968402, challengeId = 93420, text = "salimos", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 968403, challengeId = 93420, text = "salga", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 968800, challengeId = 93440, text = "hay", correct = true),
                ChallengeOptionEntity(id = 968801, challengeId = 93440, text = "han", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 968802, challengeId = 93440, text = "haber", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 968803, challengeId = 93440, text = "he", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 969200, challengeId = 93460, text = "compras", correct = true),
                ChallengeOptionEntity(id = 969201, challengeId = 93460, text = "compro", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 969202, challengeId = 93460, text = "compró", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 969203, challengeId = 93460, text = "comprar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 969600, challengeId = 93480, text = "siga", correct = true),
                ChallengeOptionEntity(id = 969601, challengeId = 93480, text = "sigo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 969602, challengeId = 93480, text = "seguir", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 969603, challengeId = 93480, text = "siguen", correct = false, errorTag = "WRONG_PERSON"),

ChallengeOptionEntity(id = 970000, challengeId = 93500, text = "El avión llega tarde por la niebla.", romaji = "el abhˈion yˈegha tˈardhe por la nˈiebhla", correct = true, audioSrc = "asset:///audio/es/el_avion_llega_tarde.ogg"),
                ChallengeOptionEntity(id = 970001, challengeId = 93500, text = "El avión llegan tarde", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 970002, challengeId = 93500, text = "El avión llega tardes", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 970003, challengeId = 93500, text = "El avión llega temprano", correct = false, errorTag = "WRONG_FORM"),

ChallengeOptionEntity(id = 970400, challengeId = 93520, text = "Gire a la derecha en la esquina.", romaji = "jˈire a la derˈecha en la eskˈina", correct = true, audioSrc = "asset:///audio/es/gire_a_la_derecha_en_la_esquina.ogg"),
                ChallengeOptionEntity(id = 970401, challengeId = 93520, text = "Gire a la derecha en las esquinas", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 970402, challengeId = 93520, text = "Gire a la derecha en la esquino", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 970403, challengeId = 93520, text = "Gire a la izquierda en la esquina", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 970800, challengeId = 93540, text = "It had already left the platform", correct = true),
                ChallengeOptionEntity(id = 970801, challengeId = 93540, text = "It was cancelled because of the fog", correct = false),
                ChallengeOptionEntity(id = 970802, challengeId = 93540, text = "They arrived at the wrong station", correct = false),
                ChallengeOptionEntity(id = 970803, challengeId = 93540, text = "They had bought the wrong ticket", correct = false),

                ChallengeOptionEntity(id = 980000, challengeId = 94000, text = "la ciudad", correct = true),
                ChallengeOptionEntity(id = 980001, challengeId = 94000, text = "the city", correct = true),
                ChallengeOptionEntity(id = 980002, challengeId = 94000, text = "la calle", correct = true),
                ChallengeOptionEntity(id = 980003, challengeId = 94000, text = "the street", correct = true),
                ChallengeOptionEntity(id = 980004, challengeId = 94000, text = "la plaza", correct = true),
                ChallengeOptionEntity(id = 980005, challengeId = 94000, text = "the square", correct = true),
                ChallengeOptionEntity(id = 980006, challengeId = 94000, text = "el parque", correct = true),
                ChallengeOptionEntity(id = 980007, challengeId = 94000, text = "the park", correct = true),

                ChallengeOptionEntity(id = 980400, challengeId = 94020, text = "el museo", correct = true),
                ChallengeOptionEntity(id = 980401, challengeId = 94020, text = "the museum", correct = true),
                ChallengeOptionEntity(id = 980402, challengeId = 94020, text = "la biblioteca", correct = true),
                ChallengeOptionEntity(id = 980403, challengeId = 94020, text = "the library", correct = true),
                ChallengeOptionEntity(id = 980404, challengeId = 94020, text = "el teatro", correct = true),
                ChallengeOptionEntity(id = 980405, challengeId = 94020, text = "the theatre", correct = true),
                ChallengeOptionEntity(id = 980406, challengeId = 94020, text = "la iglesia", correct = true),
                ChallengeOptionEntity(id = 980407, challengeId = 94020, text = "the church", correct = true),

                ChallengeOptionEntity(id = 980800, challengeId = 94040, text = "el puente", correct = true),
                ChallengeOptionEntity(id = 980801, challengeId = 94040, text = "the bridge", correct = true),
                ChallengeOptionEntity(id = 980802, challengeId = 94040, text = "la avenida", correct = true),
                ChallengeOptionEntity(id = 980803, challengeId = 94040, text = "the avenue", correct = true),
                ChallengeOptionEntity(id = 980804, challengeId = 94040, text = "el edificio", correct = true),
                ChallengeOptionEntity(id = 980805, challengeId = 94040, text = "the building", correct = true),
                ChallengeOptionEntity(id = 980806, challengeId = 94040, text = "la orilla", correct = true),
                ChallengeOptionEntity(id = 980807, challengeId = 94040, text = "the river bank", correct = true),

                ChallengeOptionEntity(id = 981200, challengeId = 94060, text = "el banco", correct = true),
                ChallengeOptionEntity(id = 981201, challengeId = 94060, text = "the bank", correct = true),
                ChallengeOptionEntity(id = 981202, challengeId = 94060, text = "el hospital", correct = true),
                ChallengeOptionEntity(id = 981203, challengeId = 94060, text = "the hospital", correct = true),
                ChallengeOptionEntity(id = 981204, challengeId = 94060, text = "el ayuntamiento", correct = true),
                ChallengeOptionEntity(id = 981205, challengeId = 94060, text = "the town hall", correct = true),
                ChallengeOptionEntity(id = 981206, challengeId = 94060, text = "la pizzería", correct = true),
                ChallengeOptionEntity(id = 981207, challengeId = 94060, text = "the pizzeria", correct = true),

                ChallengeOptionEntity(id = 981600, challengeId = 94080, text = "el jardín público", correct = true),
                ChallengeOptionEntity(id = 981601, challengeId = 94080, text = "la jardín público", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 981602, challengeId = 94080, text = "el jardín pública", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 981603, challengeId = 94080, text = "el jardín públicos", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 982000, challengeId = 94100, text = "el casco antiguo", correct = true),
                ChallengeOptionEntity(id = 982001, challengeId = 94100, text = "la casco antiguo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 982002, challengeId = 94100, text = "el casco antiguos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 982003, challengeId = 94100, text = "el casco antigua", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 982400, challengeId = 94120, text = "está nublado", correct = true),
                ChallengeOptionEntity(id = 982401, challengeId = 94120, text = "están nublados", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 982402, challengeId = 94120, text = "está nublada", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 982403, challengeId = 94120, text = "está nublados", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 982800, challengeId = 94140, text = "hace sol", correct = true),
                ChallengeOptionEntity(id = 982801, challengeId = 94140, text = "hacen sol", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 982802, challengeId = 94140, text = "hace soles", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 982803, challengeId = 94140, text = "hace soleado", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 983200, challengeId = 94160, text = "está nevando", correct = true),
                ChallengeOptionEntity(id = 983201, challengeId = 94160, text = "están nevando", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 983202, challengeId = 94160, text = "está nevadas", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 983203, challengeId = 94160, text = "están nevadas", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 983600, challengeId = 94180, text = "el pronóstico del tiempo", correct = true),
                ChallengeOptionEntity(id = 983601, challengeId = 94180, text = "el pronóstico del tiempos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 983602, challengeId = 94180, text = "la pronóstico del tiempo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 983603, challengeId = 94180, text = "el pronósitico del tiempo", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 984000, challengeId = 94200, text = "una calle llena de gente", correct = true),
                ChallengeOptionEntity(id = 984001, challengeId = 94200, text = "una calle lleno de gente", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 984002, challengeId = 94200, text = "una calles llena de gente", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 984003, challengeId = 94200, text = "una calle llen de gente", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 984400, challengeId = 94220, text = "una avenida ancha", correct = true),
                ChallengeOptionEntity(id = 984401, challengeId = 94220, text = "una avenida anchas", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 984402, challengeId = 94220, text = "una avenida ancho", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 984403, challengeId = 94220, text = "un avenida ancha", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 984800, challengeId = 94240, text = "la lavandería", correct = true),
                ChallengeOptionEntity(id = 984801, challengeId = 94240, text = "el lavandería", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 984802, challengeId = 94240, text = "la lavanderías", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 984803, challengeId = 94240, text = "la lavanderes", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 985200, challengeId = 94260, text = "la máquina de billetes", correct = true),
                ChallengeOptionEntity(id = 985201, challengeId = 94260, text = "la máquinas de billetes", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 985202, challengeId = 94260, text = "la máquina de billete", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 985203, challengeId = 94260, text = "el máquina de billetes", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 985600, challengeId = 94280, text = "constiparse", correct = true),
                ChallengeOptionEntity(id = 985601, challengeId = 94280, text = "constipamos", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 985602, challengeId = 94280, text = "constiparse bien", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 985603, challengeId = 94280, text = "constipada", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 986000, challengeId = 94300, text = "hace viento", correct = true),
                ChallengeOptionEntity(id = 986001, challengeId = 94300, text = "hacen viento", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 986002, challengeId = 94300, text = "hace vientos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 986003, challengeId = 94300, text = "hace ventoso", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 986400, challengeId = 94320, text = "lluevo", correct = true),
                ChallengeOptionEntity(id = 986401, challengeId = 94320, text = "lloves", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 986402, challengeId = 94320, text = "llueve", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 986403, challengeId = 94320, text = "llovemos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 986800, challengeId = 94340, text = "haces", correct = true),
                ChallengeOptionEntity(id = 986801, challengeId = 94340, text = "hago", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 986802, challengeId = 94340, text = "hace", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 986803, challengeId = 94340, text = "hacemos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 987200, challengeId = 94360, text = "suelen", correct = true),
                ChallengeOptionEntity(id = 987201, challengeId = 94360, text = "suelo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 987202, challengeId = 94360, text = "sueles", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 987203, challengeId = 94360, text = "suélense", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 987600, challengeId = 94380, text = "nublamos", correct = true),
                ChallengeOptionEntity(id = 987601, challengeId = 94380, text = "nublémonos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 987602, challengeId = 94380, text = "nublo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 987603, challengeId = 94380, text = "nublas", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 988000, challengeId = 94400, text = "hace", correct = true),
                ChallengeOptionEntity(id = 988001, challengeId = 94400, text = "hacen", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 988002, challengeId = 94400, text = "hacerse", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 988003, challengeId = 94400, text = "hacéis", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 988400, challengeId = 94420, text = "llovió", correct = true),
                ChallengeOptionEntity(id = 988401, challengeId = 94420, text = "llueve", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 988402, challengeId = 94420, text = "llueven", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 988403, challengeId = 94420, text = "llover", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 988800, challengeId = 94440, text = "cierra", correct = true),
                ChallengeOptionEntity(id = 988801, challengeId = 94440, text = "cierran", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 988802, challengeId = 94440, text = "cerrar", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 988803, challengeId = 94440, text = "cerramos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 989200, challengeId = 94460, text = "llena", correct = true),
                ChallengeOptionEntity(id = 989201, challengeId = 94460, text = "llenan", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 989202, challengeId = 94460, text = "llenar", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 989203, challengeId = 94460, text = "llenaron", correct = false, errorTag = "WRONG_TENSE"),

ChallengeOptionEntity(id = 989600, challengeId = 94480, text = "Hoy llueve en toda la ciudad.", romaji = "oi yˈuebe en tˈodha la siudhˈad", correct = true, audioSrc = "asset:///audio/es/hoy_llueve_en_toda_la_ciudad.ogg"),
                ChallengeOptionEntity(id = 989601, challengeId = 94480, text = "Hoy llueven en toda la ciudad", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 989602, challengeId = 94480, text = "Hoy llueve en toda la ciudades", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 989603, challengeId = 94480, text = "Hoy nieva en toda la ciudad", correct = false, errorTag = "WRONG_FORM"),

ChallengeOptionEntity(id = 990000, challengeId = 94500, text = "El puente está cerrado por la lluvia.", romaji = "el pˈuente estˈa serrˈadho por la yˈubia", correct = true, audioSrc = "asset:///audio/es/el_puente_esta_cerrado_por_lluvia.ogg"),
                ChallengeOptionEntity(id = 990001, challengeId = 94500, text = "El puentes está cerrado por la lluvia", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 990002, challengeId = 94500, text = "El puente están cerrados por la lluvia", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 990003, challengeId = 94500, text = "El puente está cerrado por las lluvia", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 990400, challengeId = 94520, text = "Nothing, it was free", correct = true),
                ChallengeOptionEntity(id = 990401, challengeId = 94520, text = "Five euros each", correct = false),
                ChallengeOptionEntity(id = 990402, challengeId = 94520, text = "The price of a coffee", correct = false),
                ChallengeOptionEntity(id = 990403, challengeId = 94520, text = "The speaker does not say", correct = false),

                ChallengeOptionEntity(id = 1000000, challengeId = 95000, text = "el trabajo", correct = true),
                ChallengeOptionEntity(id = 1000001, challengeId = 95000, text = "the work", correct = true),
                ChallengeOptionEntity(id = 1000002, challengeId = 95000, text = "el jefe", correct = true),
                ChallengeOptionEntity(id = 1000003, challengeId = 95000, text = "the boss", correct = true),
                ChallengeOptionEntity(id = 1000004, challengeId = 95000, text = "la empresa", correct = true),
                ChallengeOptionEntity(id = 1000005, challengeId = 95000, text = "the company", correct = true),
                ChallengeOptionEntity(id = 1000006, challengeId = 95000, text = "el sueldo", correct = true),
                ChallengeOptionEntity(id = 1000007, challengeId = 95000, text = "the salary", correct = true),

                ChallengeOptionEntity(id = 1000400, challengeId = 95020, text = "el horario", correct = true),
                ChallengeOptionEntity(id = 1000401, challengeId = 95020, text = "the timetable", correct = true),
                ChallengeOptionEntity(id = 1000402, challengeId = 95020, text = "el permiso", correct = true),
                ChallengeOptionEntity(id = 1000403, challengeId = 95020, text = "the leave", correct = true),
                ChallengeOptionEntity(id = 1000404, challengeId = 95020, text = "la entrevista", correct = true),
                ChallengeOptionEntity(id = 1000405, challengeId = 95020, text = "the interview", correct = true),
                ChallengeOptionEntity(id = 1000406, challengeId = 95020, text = "el teclado", correct = true),
                ChallengeOptionEntity(id = 1000407, challengeId = 95020, text = "the keyboard", correct = true),

                ChallengeOptionEntity(id = 1000800, challengeId = 95040, text = "el abogado", correct = true),
                ChallengeOptionEntity(id = 1000801, challengeId = 95040, text = "the lawyer", correct = true),
                ChallengeOptionEntity(id = 1000802, challengeId = 95040, text = "el maestro", correct = true),
                ChallengeOptionEntity(id = 1000803, challengeId = 95040, text = "the teacher", correct = true),
                ChallengeOptionEntity(id = 1000804, challengeId = 95040, text = "el ingeniero", correct = true),
                ChallengeOptionEntity(id = 1000805, challengeId = 95040, text = "the engineer", correct = true),
                ChallengeOptionEntity(id = 1000806, challengeId = 95040, text = "el cocinero", correct = true),
                ChallengeOptionEntity(id = 1000807, challengeId = 95040, text = "the cook", correct = true),

                ChallengeOptionEntity(id = 1001200, challengeId = 95060, text = "el panadero", correct = true),
                ChallengeOptionEntity(id = 1001201, challengeId = 95060, text = "the baker", correct = true),
                ChallengeOptionEntity(id = 1001202, challengeId = 95060, text = "el cartero", correct = true),
                ChallengeOptionEntity(id = 1001203, challengeId = 95060, text = "the postman", correct = true),
                ChallengeOptionEntity(id = 1001204, challengeId = 95060, text = "el becario", correct = true),
                ChallengeOptionEntity(id = 1001205, challengeId = 95060, text = "the intern", correct = true),
                ChallengeOptionEntity(id = 1001206, challengeId = 95060, text = "el cliente", correct = true),
                ChallengeOptionEntity(id = 1001207, challengeId = 95060, text = "the customer", correct = true),

                ChallengeOptionEntity(id = 1001600, challengeId = 95080, text = "el ordenador", correct = true),
                ChallengeOptionEntity(id = 1001601, challengeId = 95080, text = "the computer", correct = true),
                ChallengeOptionEntity(id = 1001602, challengeId = 95080, text = "el archivo", correct = true),
                ChallengeOptionEntity(id = 1001603, challengeId = 95080, text = "the filing cabinet", correct = true),
                ChallengeOptionEntity(id = 1001604, challengeId = 95080, text = "la carpeta", correct = true),
                ChallengeOptionEntity(id = 1001605, challengeId = 95080, text = "the folder", correct = true),
                ChallengeOptionEntity(id = 1001606, challengeId = 95080, text = "el informe", correct = true),
                ChallengeOptionEntity(id = 1001607, challengeId = 95080, text = "the report", correct = true),

                ChallengeOptionEntity(id = 1002000, challengeId = 95100, text = "el personal", correct = true),
                ChallengeOptionEntity(id = 1002001, challengeId = 95100, text = "la personal", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1002002, challengeId = 95100, text = "los personal", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1002003, challengeId = 95100, text = "el personals", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 1002400, challengeId = 95120, text = "la fecha límite", correct = true),
                ChallengeOptionEntity(id = 1002401, challengeId = 95120, text = "el fecha límite", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1002402, challengeId = 95120, text = "la fecha límites", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 1002403, challengeId = 95120, text = "la fecha limite", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 1002800, challengeId = 95140, text = "entregar un informe", correct = true),
                ChallengeOptionEntity(id = 1002801, challengeId = 95140, text = "entregar un informes", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 1002802, challengeId = 95140, text = "entrega un informe", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1002803, challengeId = 95140, text = "entregar la informe", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 1003200, challengeId = 95160, text = "pedir un día libre", correct = true),
                ChallengeOptionEntity(id = 1003201, challengeId = 95160, text = "pedir un día libres", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 1003202, challengeId = 95160, text = "pide un día libre", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1003203, challengeId = 95160, text = "pedir un día de libre", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 1003600, challengeId = 95180, text = "un contrato temporal", correct = true),
                ChallengeOptionEntity(id = 1003601, challengeId = 95180, text = "un contrato temporales", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 1003602, challengeId = 95180, text = "una contrato temporal", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1003603, challengeId = 95180, text = "un contrato de temporal", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 1004000, challengeId = 95200, text = "ascender de puesto", correct = true),
                ChallengeOptionEntity(id = 1004001, challengeId = 95200, text = "ascender del puesto", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 1004002, challengeId = 95200, text = "asciende de puesto", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1004003, challengeId = 95200, text = "ascender de puestos", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 1004400, challengeId = 95220, text = "hacer horas extra", correct = true),
                ChallengeOptionEntity(id = 1004401, challengeId = 95220, text = "hacer horas extras", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 1004402, challengeId = 95220, text = "hace horas extra", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1004403, challengeId = 95220, text = "hacer la hora extra", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 1004800, challengeId = 95240, text = "un trabajo a tiempo completo", correct = true),
                ChallengeOptionEntity(id = 1004801, challengeId = 95240, text = "un trabajo a tiempo completos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 1004802, challengeId = 95240, text = "una trabajo a tiempo completo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1004803, challengeId = 95240, text = "un trabajo a tiempo incompletos", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 1005200, challengeId = 95260, text = "solicitar un empleo", correct = true),
                ChallengeOptionEntity(id = 1005201, challengeId = 95260, text = "solicitar un empleos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 1005202, challengeId = 95260, text = "solicita un empleo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1005203, challengeId = 95260, text = "solicitar una empleo", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 1005600, challengeId = 95280, text = "la experiencia laboral", correct = true),
                ChallengeOptionEntity(id = 1005601, challengeId = 95280, text = "el experiencia laboral", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1005602, challengeId = 95280, text = "la experiencia laborables", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 1005603, challengeId = 95280, text = "la experiencias laboral", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 1006000, challengeId = 95300, text = "presentar la dimisión", correct = true),
                ChallengeOptionEntity(id = 1006001, challengeId = 95300, text = "presentar la dimisiones", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 1006002, challengeId = 95300, text = "presentar el dimisión", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1006003, challengeId = 95300, text = "presentar la dimisiónes", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 1006400, challengeId = 95320, text = "un compañero de trabajo", correct = true),
                ChallengeOptionEntity(id = 1006401, challengeId = 95320, text = "un compañero de trabajos", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 1006402, challengeId = 95320, text = "una compañero de trabajo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1006403, challengeId = 95320, text = "un compañero del trabajo", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 1006800, challengeId = 95340, text = "entrego", correct = true),
                ChallengeOptionEntity(id = 1006801, challengeId = 95340, text = "entregas", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1006802, challengeId = 95340, text = "entrega", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1006803, challengeId = 95340, text = "entregamos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 1007200, challengeId = 95360, text = "suelo", correct = true),
                ChallengeOptionEntity(id = 1007201, challengeId = 95360, text = "sueles", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1007202, challengeId = 95360, text = "suele", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1007203, challengeId = 95360, text = "solemos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 1007600, challengeId = 95380, text = "sigues", correct = true),
                ChallengeOptionEntity(id = 1007601, challengeId = 95380, text = "sigo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1007602, challengeId = 95380, text = "sigue", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1007603, challengeId = 95380, text = "seguimos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 1008000, challengeId = 95400, text = "se reúnen", correct = true),
                ChallengeOptionEntity(id = 1008001, challengeId = 95400, text = "se reúne", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1008002, challengeId = 95400, text = "se reúno", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1008003, challengeId = 95400, text = "nos reunimos", correct = false, errorTag = "WRONG_PERSON"),

                ChallengeOptionEntity(id = 1008400, challengeId = 95420, text = "trabaja", correct = true),
                ChallengeOptionEntity(id = 1008401, challengeId = 95420, text = "trabajo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1008402, challengeId = 95420, text = "trabajan", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1008403, challengeId = 95420, text = "trabajar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 1008800, challengeId = 95440, text = "llevas", correct = true),
                ChallengeOptionEntity(id = 1008801, challengeId = 95440, text = "lleva", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1008802, challengeId = 95440, text = "llevamos", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1008803, challengeId = 95440, text = "llevar", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 1009200, challengeId = 95460, text = "enviar", correct = true),
                ChallengeOptionEntity(id = 1009201, challengeId = 95460, text = "envías", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1009202, challengeId = 95460, text = "enviaré", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 1009203, challengeId = 95460, text = "enviarlo", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 1009600, challengeId = 95480, text = "tienen", correct = true),
                ChallengeOptionEntity(id = 1009601, challengeId = 95480, text = "tiene", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1009602, challengeId = 95480, text = "tenemos", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1009603, challengeId = 95480, text = "tener", correct = false, errorTag = "WRONG_FORM"),

ChallengeOptionEntity(id = 1010000, challengeId = 95500, text = "Mi jefe trabaja desde casa desde marzo.", romaji = "mi jˈefe trabhˈaja dˈesde kˈasa dˈesde mˈarso", correct = true, audioSrc = "asset:///audio/es/mi_jefe_trabaja_desde_casa.ogg"),
                ChallengeOptionEntity(id = 1010001, challengeId = 95500, text = "Mi jefes trabaja desde casa desde marzo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1010002, challengeId = 95500, text = "Mi jefe trabajan desde casa desde marzo", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1010003, challengeId = 95500, text = "Mi jefe trabaja desde casa desde mayo", correct = false, errorTag = "WRONG_FORM"),

ChallengeOptionEntity(id = 1010400, challengeId = 95520, text = "La reunión se ha aplazado hasta el viernes.", romaji = "la rreunˈion se a aplazˈadho ˈasta el bˈiernes", correct = true, audioSrc = "asset:///audio/es/la_reunion_se_ha_aplazado.ogg"),
                ChallengeOptionEntity(id = 1010401, challengeId = 95520, text = "La reunión se han aplazado", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1010402, challengeId = 95520, text = "La reunión se ha aplazada", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 1010403, challengeId = 95520, text = "La reunión se han aplazada", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 1010800, challengeId = 95540, text = "Their boss introduced them", correct = true),
                ChallengeOptionEntity(id = 1010801, challengeId = 95540, text = "They answered three emails", correct = false),
                ChallengeOptionEntity(id = 1010802, challengeId = 95540, text = "They were trained in the office", correct = false),
                ChallengeOptionEntity(id = 1010803, challengeId = 95540, text = "They did not meet anyone", correct = false),
            ),
        ),
    )
}
