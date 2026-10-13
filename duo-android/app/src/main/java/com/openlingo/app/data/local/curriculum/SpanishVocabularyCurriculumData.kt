package com.openlingo.app.data.local.curriculum
import com.openlingo.app.data.local.models.ChallengeType

object SpanishVocabularyCurriculumData {
    val spanishVocabularyUnits: List<UnitPayload> = listOf(
        unit(id = 36, courseId = 1, title = "Unit 17: Everyday Life in Spanish", description = "The body, the home, and the table — la salud, la casa y la comida", orderIndex = 16) {
            lesson(id = 900, title = "Lesson 36: The Body and Health", orderIndex = 0)
            lesson(id = 901, title = "Lesson 37: The Home, Room by Room", orderIndex = 1)
            lesson(id = 902, title = "Lesson 38: Food, Shopping and the Restaurant", orderIndex = 2)
            challenge(id = 90000, lessonId = 900, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (6 pairs)", orderIndex = 0) {
                option(id = 900000, text = "el brazo", correct = true)
                option(id = 900001, text = "the arm", correct = true)
                option(id = 900002, text = "la pierna", correct = true)
                option(id = 900003, text = "the leg", correct = true)
                option(id = 900004, text = "el hombro", correct = true)
                option(id = 900005, text = "the shoulder", correct = true)
                option(id = 900006, text = "el codo", correct = true)
                option(id = 900007, text = "the elbow", correct = true)
                option(id = 900008, text = "la rodilla", correct = true)
                option(id = 900009, text = "the knee", correct = true)
                option(id = 900010, text = "la muñeca", correct = true)
                option(id = 900011, text = "the wrist", correct = true)
            }
            challenge(id = 90020, lessonId = 900, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (6 pairs)", orderIndex = 1) {
                option(id = 900400, text = "el tobillo", correct = true)
                option(id = 900401, text = "the ankle", correct = true)
                option(id = 900402, text = "la garganta", correct = true)
                option(id = 900403, text = "the throat", correct = true)
                option(id = 900404, text = "el estómago", correct = true)
                option(id = 900405, text = "the stomach", correct = true)
                option(id = 900406, text = "el corazón", correct = true)
                option(id = 900407, text = "the heart", correct = true)
                option(id = 900408, text = "el pulmón", correct = true)
                option(id = 900409, text = "the lung", correct = true)
                option(id = 900410, text = "el hueso", correct = true)
                option(id = 900411, text = "the bone", correct = true)
            }
            challenge(id = 90040, lessonId = 900, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (6 pairs)", orderIndex = 2) {
                option(id = 900800, text = "la piel", correct = true)
                option(id = 900801, text = "the skin", correct = true)
                option(id = 900802, text = "la barba", correct = true)
                option(id = 900803, text = "the beard", correct = true)
                option(id = 900804, text = "el diente", correct = true)
                option(id = 900805, text = "the tooth", correct = true)
                option(id = 900806, text = "la uña", correct = true)
                option(id = 900807, text = "the fingernail", correct = true)
                option(id = 900808, text = "la enfermera", correct = true)
                option(id = 900809, text = "the nurse", correct = true)
                option(id = 900810, text = "el médico", correct = true)
                option(id = 900811, text = "the doctor", correct = true)
            }
            challenge(id = 90060, lessonId = 900, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (6 pairs)", orderIndex = 3) {
                option(id = 901200, text = "la pastilla", correct = true)
                option(id = 901201, text = "the pill", correct = true)
                option(id = 901202, text = "la herida", correct = true)
                option(id = 901203, text = "the wound", correct = true)
                option(id = 901204, text = "la receta", correct = true)
                option(id = 901205, text = "the prescription", correct = true)
                option(id = 901206, text = "el medicamento", correct = true)
                option(id = 901207, text = "the medicine", correct = true)
                option(id = 901208, text = "la alergia", correct = true)
                option(id = 901209, text = "the allergy", correct = true)
                option(id = 901210, text = "la quemadura", correct = true)
                option(id = 901211, text = "the burn", correct = true)
            }
            challenge(id = 90080, lessonId = 900, type = ChallengeType.SELECT, question = "Which one means 'the eye'?", orderIndex = 4, grammaticalFocus = "es.vocab.everyday_life", ruleText = " el ojo is the answer. la ojo is the wrong person or number for the slot. los ojo is the wrong person or number for the slot. el ojos is the wrong person or number for the slot.") {
                option(id = 901600, text = "el ojo", correct = true)
                option(id = 901601, text = "la ojo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 901602, text = "los ojo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 901603, text = "el ojos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90100, lessonId = 900, type = ChallengeType.SELECT, question = "Which one means 'the ear'?", orderIndex = 5, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la oreja is the answer. el oreja is the wrong person or number for the slot. las oreja is the wrong person or number for the slot. la orejas is the wrong person or number for the slot.") {
                option(id = 902000, text = "la oreja", correct = true)
                option(id = 902001, text = "el oreja", correct = false, errorTag = "WRONG_PERSON")
                option(id = 902002, text = "las oreja", correct = false, errorTag = "WRONG_PERSON")
                option(id = 902003, text = "la orejas", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90120, lessonId = 900, type = ChallengeType.SELECT, question = "Which one means 'the mouth'?", orderIndex = 6, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la boca is the answer. el boca is the wrong person or number for the slot. las boca is the wrong person or number for the slot. la bocas is the wrong person or number for the slot.") {
                option(id = 902400, text = "la boca", correct = true)
                option(id = 902401, text = "el boca", correct = false, errorTag = "WRONG_PERSON")
                option(id = 902402, text = "las boca", correct = false, errorTag = "WRONG_PERSON")
                option(id = 902403, text = "la bocas", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90140, lessonId = 900, type = ChallengeType.SELECT, question = "Which one means 'the nose'?", orderIndex = 7, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la nariz is the answer. el nariz is the wrong person or number for the slot. las nariz is the wrong person or number for the slot. la noses is the wrong person or number for the slot.") {
                option(id = 902800, text = "la nariz", correct = true)
                option(id = 902801, text = "el nariz", correct = false, errorTag = "WRONG_PERSON")
                option(id = 902802, text = "las nariz", correct = false, errorTag = "WRONG_PERSON")
                option(id = 902803, text = "la noses", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90160, lessonId = 900, type = ChallengeType.SELECT, question = "Which one means 'the forehead'?", orderIndex = 8, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la frente is the answer. el frente is the wrong person or number for the slot. las frente is the wrong person or number for the slot. la frentes is the wrong person or number for the slot.") {
                option(id = 903200, text = "la frente", correct = true)
                option(id = 903201, text = "el frente", correct = false, errorTag = "WRONG_PERSON")
                option(id = 903202, text = "las frente", correct = false, errorTag = "WRONG_PERSON")
                option(id = 903203, text = "la frentes", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90180, lessonId = 900, type = ChallengeType.SELECT, question = "Which one means 'the cheek'?", orderIndex = 9, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la mejilla is the answer. el mejilla is the wrong person or number for the slot. las mejilla is the wrong person or number for the slot. la mejillas is the wrong person or number for the slot.") {
                option(id = 903600, text = "la mejilla", correct = true)
                option(id = 903601, text = "el mejilla", correct = false, errorTag = "WRONG_PERSON")
                option(id = 903602, text = "las mejilla", correct = false, errorTag = "WRONG_PERSON")
                option(id = 903603, text = "la mejillas", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90200, lessonId = 900, type = ChallengeType.SELECT, question = "Which one means 'the hip'?", orderIndex = 10, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la cadera is the answer. el cadera is the wrong person or number for the slot. las cadera is the wrong person or number for the slot. la caderas is the wrong person or number for the slot.") {
                option(id = 904000, text = "la cadera", correct = true)
                option(id = 904001, text = "el cadera", correct = false, errorTag = "WRONG_PERSON")
                option(id = 904002, text = "las cadera", correct = false, errorTag = "WRONG_PERSON")
                option(id = 904003, text = "la caderas", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90220, lessonId = 900, type = ChallengeType.SELECT, question = "Which one means 'the calf'?", orderIndex = 11, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la pantorrilla is the answer. el pantorrilla is the wrong person or number for the slot. las pantorrilla is the wrong person or number for the slot. la pantorrillas is the wrong person or number for the slot.") {
                option(id = 904400, text = "la pantorrilla", correct = true)
                option(id = 904401, text = "el pantorrilla", correct = false, errorTag = "WRONG_PERSON")
                option(id = 904402, text = "las pantorrilla", correct = false, errorTag = "WRONG_PERSON")
                option(id = 904403, text = "la pantorrillas", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90240, lessonId = 900, type = ChallengeType.SELECT, question = "Which one means 'the white coat'?", orderIndex = 12, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la bata blanca is the answer. el bata blanca is the wrong person or number for the slot. la bata blancos is a form of the word that cannot fill the slot. la bata blancas is a form of the word that cannot fill the slot.") {
                option(id = 904800, text = "la bata blanca", correct = true)
                option(id = 904801, text = "el bata blanca", correct = false, errorTag = "WRONG_PERSON")
                option(id = 904802, text = "la bata blancos", correct = false, errorTag = "WRONG_FORM")
                option(id = 904803, text = "la bata blancas", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 90260, lessonId = 900, type = ChallengeType.SELECT, question = "Which one means 'a serious wound'?", orderIndex = 13, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la herida grave is the answer. el herida grave is the wrong person or number for the slot. la herida graves is a form of the word that cannot fill the slot. la herida gravísimo is a form of the word that cannot fill the slot.") {
                option(id = 905200, text = "la herida grave", correct = true)
                option(id = 905201, text = "el herida grave", correct = false, errorTag = "WRONG_PERSON")
                option(id = 905202, text = "la herida graves", correct = false, errorTag = "WRONG_FORM")
                option(id = 905203, text = "la herida gravísimo", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 90280, lessonId = 900, type = ChallengeType.SELECT, question = "Which one means 'a high fever'?", orderIndex = 14, grammaticalFocus = "es.vocab.everyday_life", ruleText = " fiebre alta is the answer. fiebre altas is a form of the word that cannot fill the slot. fiebre alto is a form of the word that cannot fill the slot. la fiebre alta is the wrong person or number for the slot.") {
                option(id = 905600, text = "fiebre alta", correct = true)
                option(id = 905601, text = "fiebre altas", correct = false, errorTag = "WRONG_FORM")
                option(id = 905602, text = "fiebre alto", correct = false, errorTag = "WRONG_FORM")
                option(id = 905603, text = "la fiebre alta", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90300, lessonId = 900, type = ChallengeType.SELECT, question = "Which one means 'a stomach ache'?", orderIndex = 15, grammaticalFocus = "es.vocab.everyday_life", ruleText = " dolor de estómago is the answer. dolores de estómago is a form of the word that cannot fill the slot. dolor de estómarago is a form of the word that cannot fill the slot. el dolor de estómago is the wrong person or number for the slot.") {
                option(id = 906000, text = "dolor de estómago", correct = true)
                option(id = 906001, text = "dolores de estómago", correct = false, errorTag = "WRONG_FORM")
                option(id = 906002, text = "dolor de estómarago", correct = false, errorTag = "WRONG_FORM")
                option(id = 906003, text = "el dolor de estómago", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90320, lessonId = 900, type = ChallengeType.SELECT, question = "Which present form of sentirse goes with 'yo'?", orderIndex = 16, grammaticalFocus = "es.vocab.everyday_life", ruleText = "sentirse is reflexive, so the yo form carries the me pronoun with it: me siento. me siento is the answer. te sientas is the wrong person or number for the slot. se sienta is the wrong person or number for the slot. nos sentamos is the wrong person or number for the slot.") {
                option(id = 906400, text = "me siento", correct = true)
                option(id = 906401, text = "te sientas", correct = false, errorTag = "WRONG_PERSON")
                option(id = 906402, text = "se sienta", correct = false, errorTag = "WRONG_PERSON")
                option(id = 906403, text = "nos sentamos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90340, lessonId = 900, type = ChallengeType.CONJUGATE, question = "Which present form of dormir goes with 'tú'?", orderIndex = 17, grammaticalFocus = "es.vocab.everyday_life", ruleText = "dormir changes d- to du- in the present, and the tú ending is -es: duermes. duermes is the answer. duermo is the wrong person or number for the slot. duerme is the wrong person or number for the slot. dormimos is the wrong person or number for the slot.") {
                option(id = 906800, text = "duermes", correct = true)
                option(id = 906801, text = "duermo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 906802, text = "duerme", correct = false, errorTag = "WRONG_PERSON")
                option(id = 906803, text = "dormimos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90360, lessonId = 900, type = ChallengeType.CONJUGATE, question = "Which present form of preferir goes with 'nosotros'?", orderIndex = 18, grammaticalFocus = "es.vocab.everyday_life", ruleText = "preferir changes e- to ie- in the present, but nosotros keeps the plain stem: preferimos. preferimos is the answer. prefiero is the wrong person or number for the slot. prefieres is the wrong person or number for the slot. prefiere is the wrong person or number for the slot.") {
                option(id = 907200, text = "preferimos", correct = true)
                option(id = 907201, text = "prefiero", correct = false, errorTag = "WRONG_PERSON")
                option(id = 907202, text = "prefieres", correct = false, errorTag = "WRONG_PERSON")
                option(id = 907203, text = "prefiere", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90380, lessonId = 900, type = ChallengeType.SELECT, question = "Which present form of vestirse goes with 'ellos'?", orderIndex = 19, grammaticalFocus = "es.vocab.everyday_life", ruleText = "vestirse is reflexive, and ellos takes the -n form of the verb they are doing: se visten. se visten is the answer. se viste is the wrong person or number for the slot. nos vestimos is the wrong person or number for the slot. me visto is the wrong person or number for the slot.") {
                option(id = 907600, text = "se visten", correct = true)
                option(id = 907601, text = "se viste", correct = false, errorTag = "WRONG_PERSON")
                option(id = 907602, text = "nos vestimos", correct = false, errorTag = "WRONG_PERSON")
                option(id = 907603, text = "me visto", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90400, lessonId = 900, type = ChallengeType.FILL_BLANK, question = "Hoy estoy ___ y no puedo ir a trabajar.", orderIndex = 20, grammaticalFocus = "es.vocab.everyday_life", acceptedAnswers = "enfermo|enferma", ruleText = "A person who is ill is estar-enfermo, and the ending agrees with whoever is speaking, so enfermo and enferma are both accepted here. enfermo is the answer. enfermos is the wrong person or number for the slot. enfermarse is a form of the word that cannot fill the slot. enferir is a form of the word that cannot fill the slot.") {
                option(id = 908000, text = "enfermo", correct = true)
                option(id = 908001, text = "enfermos", correct = false, errorTag = "WRONG_PERSON")
                option(id = 908002, text = "enfermarse", correct = false, errorTag = "WRONG_FORM")
                option(id = 908003, text = "enferir", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 90420, lessonId = 900, type = ChallengeType.FILL_BLANK, question = "Me roto el ___ si sigo corriendo.", orderIndex = 21, grammaticalFocus = "es.vocab.everyday_life", acceptedAnswers = "tobillo|brazo", ruleText = "A break is in one part of the body, and Spanish says that part in the singular with its own article: me roto el tobillo. brazo would need its own sentence, not this one. tobillo is the answer. tobillos is the wrong person or number for the slot. tobilar is a form of the word that cannot fill the slot. tobille is a form of the word that cannot fill the slot.") {
                option(id = 908400, text = "tobillo", correct = true)
                option(id = 908401, text = "tobillos", correct = false, errorTag = "WRONG_PERSON")
                option(id = 908402, text = "tobilar", correct = false, errorTag = "WRONG_FORM")
                option(id = 908403, text = "tobille", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 90440, lessonId = 900, type = ChallengeType.FILL_BLANK, question = "¿Qué te ___ más: la cabeza o la espalda?", orderIndex = 22, grammaticalFocus = "es.vocab.everyday_life", acceptedAnswers = "duele|punea", ruleText = "Two verbs can say what hurts you: doler and punear both fill this slot, and the infinitive cannot. duele is the answer. doler is a form of the word that cannot fill the slot. duelan is the wrong person or number for the slot. dolerte is a form of the word that cannot fill the slot.") {
                option(id = 908800, text = "duele", correct = true)
                option(id = 908801, text = "doler", correct = false, errorTag = "WRONG_FORM")
                option(id = 908802, text = "duelan", correct = false, errorTag = "WRONG_PERSON")
                option(id = 908803, text = "dolerte", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 90460, lessonId = 900, type = ChallengeType.FILL_BLANK, question = "La farmacia está ___ de la plaza.", orderIndex = 23, grammaticalFocus = "es.vocab.everyday_life", acceptedAnswers = "cerca|lejos", ruleText = "A place is cerca de or lejos de another place; dentro, encima and debajo do not take de this way. cerca is the answer. dentro is a form of the word that cannot fill the slot. encima is a form of the word that cannot fill the slot. debajo is a form of the word that cannot fill the slot.") {
                option(id = 909200, text = "cerca", correct = true)
                option(id = 909201, text = "dentro", correct = false, errorTag = "WRONG_FORM")
                option(id = 909202, text = "encima", correct = false, errorTag = "WRONG_FORM")
                option(id = 909203, text = "debajo", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 90480, lessonId = 900, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/me_duele_la_garganta.ogg", orderIndex = 24, grammaticalFocus = "es.vocab.everyday_life", ruleText = "The clip says Me duele la garganta. Me duele la garganta. is the answer. Me duelen la garganta is the wrong person or number for the slot. Me duele el garganta is the wrong person or number for the slot. Me duele las garganta is the wrong person or number for the slot.") {
                option(id = 909600, text = "Me duele la garganta.", romaji = "me dˈuele la gargˈanta", correct = true, audioSrc = "asset:///audio/es/me_duele_la_garganta.ogg")
                option(id = 909601, text = "Me duelen la garganta", correct = false, errorTag = "WRONG_PERSON")
                option(id = 909602, text = "Me duele el garganta", correct = false, errorTag = "WRONG_PERSON")
                option(id = 909603, text = "Me duele las garganta", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90500, lessonId = 900, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/la_enfermera_me_dio_una_pastilla.ogg", orderIndex = 25, grammaticalFocus = "es.vocab.everyday_life", ruleText = "The clip says La enfermera me dio una pastilla. La enfermera me dio una pastilla. is the answer. La enfermera me dio una pastillas is the wrong person or number for the slot. La enfermera me dio un pastilla is the wrong person or number for the slot. La enfermera le dio una pastilla is the wrong person or number for the slot.") {
                option(id = 910000, text = "La enfermera me dio una pastilla.", romaji = "la enfermˈera me dio una pastˈiya", correct = true, audioSrc = "asset:///audio/es/la_enfermera_me_dio_una_pastilla.ogg")
                option(id = 910001, text = "La enfermera me dio una pastillas", correct = false, errorTag = "WRONG_PERSON")
                option(id = 910002, text = "La enfermera me dio un pastilla", correct = false, errorTag = "WRONG_PERSON")
                option(id = 910003, text = "La enfermera le dio una pastilla", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 90520, romaji = "la konsˈulta ˈayer me dolˈia mˈucho la gargˈanta y no podhˈia abhlˈar bien mi mˈadhre me yebhˈo al mˈediko en el sˈentro de la siudhˈad la enfermˈera me tomˈo la temperatˈura y me dio una pastˈiya el mˈediko dˈijo kˈe deskansˈara dos dˈias", lessonId = 900, type = ChallengeType.STORY, question = "La consulta\nAyer me dolía mucho la garganta y no podía hablar bien.\nMi madre me llevó al médico en el centro de la ciudad.\nLa enfermera me tomó la temperatura y me dio una pastilla.\nEl médico dijo que descansara dos días.\n\n❓ What did the doctor tell the speaker to do?", audioSrc = "asset:///audio/es/story_900.ogg", orderIndex = 26) {
                option(id = 910400, text = "To rest for two days", correct = true)
                option(id = 910401, text = "To come back the following week", correct = false)
                option(id = 910402, text = "To drink the medicine in one hour", correct = false)
                option(id = 910403, text = "To go to hospital the next morning", correct = false)
            }
            challenge(id = 91000, lessonId = 901, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (6 pairs)", orderIndex = 0, ruleText = "A bedroom is la alcoba in Latin America and el dormitorio in Spain: the same room, so the English side is the same for both. Match each word to its meaning.") {
                option(id = 920000, text = "la sala", correct = true)
                option(id = 920001, text = "the living room", correct = true)
                option(id = 920002, text = "la alcoba", correct = true)
                option(id = 920003, text = "the bedroom", correct = true)
                option(id = 920004, text = "el baño", correct = true)
                option(id = 920005, text = "the bathroom", correct = true)
                option(id = 920006, text = "la oficina", correct = true)
                option(id = 920007, text = "the office", correct = true)
                option(id = 920008, text = "el garaje", correct = true)
                option(id = 920009, text = "the garage", correct = true)
                option(id = 920010, text = "el desván", correct = true)
                option(id = 920011, text = "the attic", correct = true)
            }
            challenge(id = 91020, lessonId = 901, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (6 pairs)", orderIndex = 1) {
                option(id = 920400, text = "la pared", correct = true)
                option(id = 920401, text = "the wall", correct = true)
                option(id = 920402, text = "el techo", correct = true)
                option(id = 920403, text = "the ceiling", correct = true)
                option(id = 920404, text = "el suelo", correct = true)
                option(id = 920405, text = "the floor", correct = true)
                option(id = 920406, text = "la escalera", correct = true)
                option(id = 920407, text = "the staircase", correct = true)
                option(id = 920408, text = "la lámpara", correct = true)
                option(id = 920409, text = "the lamp", correct = true)
                option(id = 920410, text = "la cama", correct = true)
                option(id = 920411, text = "the bed", correct = true)
            }
            challenge(id = 91040, lessonId = 901, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (6 pairs)", orderIndex = 2) {
                option(id = 920800, text = "la almohada", correct = true)
                option(id = 920801, text = "the pillow", correct = true)
                option(id = 920802, text = "la sábana", correct = true)
                option(id = 920803, text = "the sheet", correct = true)
                option(id = 920804, text = "la nevera", correct = true)
                option(id = 920805, text = "the fridge", correct = true)
                option(id = 920806, text = "el horno", correct = true)
                option(id = 920807, text = "the oven", correct = true)
                option(id = 920808, text = "la lavadora", correct = true)
                option(id = 920809, text = "the washing machine", correct = true)
                option(id = 920810, text = "el espejo", correct = true)
                option(id = 920811, text = "the mirror", correct = true)
            }
            challenge(id = 91060, lessonId = 901, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (6 pairs)", orderIndex = 3) {
                option(id = 921200, text = "la toalla", correct = true)
                option(id = 921201, text = "the towel", correct = true)
                option(id = 921202, text = "el armario", correct = true)
                option(id = 921203, text = "the wardrobe", correct = true)
                option(id = 921204, text = "la cómoda", correct = true)
                option(id = 921205, text = "the chest of drawers", correct = true)
                option(id = 921206, text = "el estante", correct = true)
                option(id = 921207, text = "the shelf", correct = true)
                option(id = 921208, text = "el cuadro", correct = true)
                option(id = 921209, text = "the painting", correct = true)
                option(id = 921210, text = "la cortina", correct = true)
                option(id = 921211, text = "the curtain", correct = true)
            }
            challenge(id = 91080, lessonId = 901, type = ChallengeType.SELECT, question = "Which one means 'the garden'?", orderIndex = 4, grammaticalFocus = "es.vocab.everyday_life", ruleText = " el jardín is the answer. la jardín is the wrong person or number for the slot. los jardín is the wrong person or number for the slot. el jardines is the wrong person or number for the slot.") {
                option(id = 921600, text = "el jardín", correct = true)
                option(id = 921601, text = "la jardín", correct = false, errorTag = "WRONG_PERSON")
                option(id = 921602, text = "los jardín", correct = false, errorTag = "WRONG_PERSON")
                option(id = 921603, text = "el jardines", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 91100, lessonId = 901, type = ChallengeType.SELECT, question = "Which one means 'the balcony'?", orderIndex = 5, grammaticalFocus = "es.vocab.everyday_life", ruleText = " el balcón is the answer. la balcón is the wrong person or number for the slot. el balcones is the wrong person or number for the slot. los balcón is the wrong person or number for the slot.") {
                option(id = 922000, text = "el balcón", correct = true)
                option(id = 922001, text = "la balcón", correct = false, errorTag = "WRONG_PERSON")
                option(id = 922002, text = "el balcones", correct = false, errorTag = "WRONG_PERSON")
                option(id = 922003, text = "los balcón", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 91120, lessonId = 901, type = ChallengeType.SELECT, question = "Which one means 'the mailbox'?", orderIndex = 6, grammaticalFocus = "es.vocab.everyday_life", ruleText = " el buzón is the answer. la buzón is the wrong person or number for the slot. los buzón is the wrong person or number for the slot. el buzones is the wrong person or number for the slot.") {
                option(id = 922400, text = "el buzón", correct = true)
                option(id = 922401, text = "la buzón", correct = false, errorTag = "WRONG_PERSON")
                option(id = 922402, text = "los buzón", correct = false, errorTag = "WRONG_PERSON")
                option(id = 922403, text = "el buzones", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 91140, lessonId = 901, type = ChallengeType.SELECT, question = "Which one means 'the door'?", orderIndex = 7, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la puerta is the answer. el puerta is the wrong person or number for the slot. las puertas is the wrong person or number for the slot. los puerta is the wrong person or number for the slot.") {
                option(id = 922800, text = "la puerta", correct = true)
                option(id = 922801, text = "el puerta", correct = false, errorTag = "WRONG_PERSON")
                option(id = 922802, text = "las puertas", correct = false, errorTag = "WRONG_PERSON")
                option(id = 922803, text = "los puerta", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 91160, lessonId = 901, type = ChallengeType.SELECT, question = "Which one means 'the bookshelf'?", orderIndex = 8, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la estantería is the answer. el estantería is the wrong person or number for the slot. la estanterías is the wrong person or number for the slot. los estantería is the wrong person or number for the slot.") {
                option(id = 923200, text = "la estantería", correct = true)
                option(id = 923201, text = "el estantería", correct = false, errorTag = "WRONG_PERSON")
                option(id = 923202, text = "la estanterías", correct = false, errorTag = "WRONG_PERSON")
                option(id = 923203, text = "los estantería", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 91180, lessonId = 901, type = ChallengeType.SELECT, question = "Which one means 'the screen'?", orderIndex = 9, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la pantalla is the answer. el pantalla is the wrong person or number for the slot. la pantallas is the wrong person or number for the slot. los pantalla is the wrong person or number for the slot.") {
                option(id = 923600, text = "la pantalla", correct = true)
                option(id = 923601, text = "el pantalla", correct = false, errorTag = "WRONG_PERSON")
                option(id = 923602, text = "la pantallas", correct = false, errorTag = "WRONG_PERSON")
                option(id = 923603, text = "los pantalla", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 91200, lessonId = 901, type = ChallengeType.SELECT, question = "Which one means 'a small kitchen'?", orderIndex = 10, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la cocina pequeña is the answer. la cocina pequeñas is a form of the word that cannot fill the slot. la cocina pequeño is a form of the word that cannot fill the slot. el cocina pequeña is the wrong person or number for the slot.") {
                option(id = 924000, text = "la cocina pequeña", correct = true)
                option(id = 924001, text = "la cocina pequeñas", correct = false, errorTag = "WRONG_FORM")
                option(id = 924002, text = "la cocina pequeño", correct = false, errorTag = "WRONG_FORM")
                option(id = 924003, text = "el cocina pequeña", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 91220, lessonId = 901, type = ChallengeType.SELECT, question = "Which one means 'a comfortable bed'?", orderIndex = 11, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la cama cómoda is the answer. la cama cómodo is a form of the word that cannot fill the slot. la cama cómodas is a form of the word that cannot fill the slot. el cama cómoda is the wrong person or number for the slot.") {
                option(id = 924400, text = "la cama cómoda", correct = true)
                option(id = 924401, text = "la cama cómodo", correct = false, errorTag = "WRONG_FORM")
                option(id = 924402, text = "la cama cómodas", correct = false, errorTag = "WRONG_FORM")
                option(id = 924403, text = "el cama cómoda", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 91240, lessonId = 901, type = ChallengeType.SELECT, question = "Which one means 'a tiled floor'?", orderIndex = 12, grammaticalFocus = "es.vocab.everyday_life", ruleText = " el suelo de baldosa is the answer. el suelos de baldosa is the wrong person or number for the slot. el suelo de baldosas is a form of the word that cannot fill the slot. la suelo de baldosa is the wrong person or number for the slot.") {
                option(id = 924800, text = "el suelo de baldosa", correct = true)
                option(id = 924801, text = "el suelos de baldosa", correct = false, errorTag = "WRONG_PERSON")
                option(id = 924802, text = "el suelo de baldosas", correct = false, errorTag = "WRONG_FORM")
                option(id = 924803, text = "la suelo de baldosa", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 91260, lessonId = 901, type = ChallengeType.SELECT, question = "Which one means 'a rented flat'?", orderIndex = 13, grammaticalFocus = "es.vocab.everyday_life", ruleText = " un piso alquilado is the answer. un piso alquilados is a form of the word that cannot fill the slot. una piso alquilado is the wrong person or number for the slot. un piso de alquilado is a form of the word that cannot fill the slot.") {
                option(id = 925200, text = "un piso alquilado", correct = true)
                option(id = 925201, text = "un piso alquilados", correct = false, errorTag = "WRONG_FORM")
                option(id = 925202, text = "una piso alquilado", correct = false, errorTag = "WRONG_PERSON")
                option(id = 925203, text = "un piso de alquilado", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 91280, lessonId = 901, type = ChallengeType.SELECT, question = "Which one means 'the landlord'?", orderIndex = 14, grammaticalFocus = "es.vocab.everyday_life", ruleText = " el casero is the answer. la casero is the wrong person or number for the slot. los casero is the wrong person or number for the slot. el caseros is the wrong person or number for the slot.") {
                option(id = 925600, text = "el casero", correct = true)
                option(id = 925601, text = "la casero", correct = false, errorTag = "WRONG_PERSON")
                option(id = 925602, text = "los casero", correct = false, errorTag = "WRONG_PERSON")
                option(id = 925603, text = "el caseros", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 91300, lessonId = 901, type = ChallengeType.SELECT, question = "Which one means 'a central heating system'?", orderIndex = 15, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la calefacción central is the answer. la calefacción centrales is a form of the word that cannot fill the slot. el calefacción central is the wrong person or number for the slot. la caléfacción central is a form of the word that cannot fill the slot.") {
                option(id = 926000, text = "la calefacción central", correct = true)
                option(id = 926001, text = "la calefacción centrales", correct = false, errorTag = "WRONG_FORM")
                option(id = 926002, text = "el calefacción central", correct = false, errorTag = "WRONG_PERSON")
                option(id = 926003, text = "la caléfacción central", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 91320, lessonId = 901, type = ChallengeType.CONJUGATE, question = "Which present form of poner goes with 'tú'?", orderIndex = 16, grammaticalFocus = "es.vocab.everyday_life", ruleText = "poner drops the e in the yo form, which is pongo, and the tú ending is -es: pones. pones is the answer. pongo is the wrong person or number for the slot. pone is the wrong person or number for the slot. ponemos is the wrong person or number for the slot.") {
                option(id = 926400, text = "pones", correct = true)
                option(id = 926401, text = "pongo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 926402, text = "pone", correct = false, errorTag = "WRONG_PERSON")
                option(id = 926403, text = "ponemos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 91340, lessonId = 901, type = ChallengeType.CONJUGATE, question = "Which present form of saber goes with 'yo'?", orderIndex = 17, grammaticalFocus = "es.vocab.everyday_life", ruleText = "saber is one of the few verbs with an irregular yo form, and that form is sé. sé is the answer. sabes is the wrong person or number for the slot. sabe is the wrong person or number for the slot. sabemos is the wrong person or number for the slot.") {
                option(id = 926800, text = "sé", correct = true)
                option(id = 926801, text = "sabes", correct = false, errorTag = "WRONG_PERSON")
                option(id = 926802, text = "sabe", correct = false, errorTag = "WRONG_PERSON")
                option(id = 926803, text = "sabemos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 91360, lessonId = 901, type = ChallengeType.CONJUGATE, question = "Which present form of abrir goes with 'nosotros'?", orderIndex = 18, grammaticalFocus = "es.vocab.everyday_life", ruleText = "abrir takes a regular -ir present, and nosotros keeps the infinitive stem: abrimos. abrimos is the answer. abro is the wrong person or number for the slot. abres is the wrong person or number for the slot. abre is the wrong person or number for the slot.") {
                option(id = 927200, text = "abrimos", correct = true)
                option(id = 927201, text = "abro", correct = false, errorTag = "WRONG_PERSON")
                option(id = 927202, text = "abres", correct = false, errorTag = "WRONG_PERSON")
                option(id = 927203, text = "abre", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 91380, lessonId = 901, type = ChallengeType.CONJUGATE, question = "Which present form of quedar goes with 'ellos'?", orderIndex = 19, grammaticalFocus = "es.vocab.everyday_life", ruleText = "quedar is a regular -ar verb, and ellos takes -n: quedan. quedan is the answer. quedo is the wrong person or number for the slot. quedas is the wrong person or number for the slot. quedamos is the wrong person or number for the slot.") {
                option(id = 927600, text = "quedan", correct = true)
                option(id = 927601, text = "quedo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 927602, text = "quedas", correct = false, errorTag = "WRONG_PERSON")
                option(id = 927603, text = "quedamos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 91400, lessonId = 901, type = ChallengeType.FILL_BLANK, question = "Guardo la leche en la ___ para que no se estropee.", orderIndex = 20, grammaticalFocus = "es.vocab.everyday_life", acceptedAnswers = "nevera|frigorífico", ruleText = "The cold cupboard is la nevera in Spain and el frigorífico in Latin America; el frigorífico is also understood in Spain, so both fill the slot. nevera is the answer. neveras is the wrong person or number for the slot. nevar is a form of the word that cannot fill the slot. estropiar is a form of the word that cannot fill the slot.") {
                option(id = 928000, text = "nevera", correct = true)
                option(id = 928001, text = "neveras", correct = false, errorTag = "WRONG_PERSON")
                option(id = 928002, text = "nevar", correct = false, errorTag = "WRONG_FORM")
                option(id = 928003, text = "estropiar", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 91420, lessonId = 901, type = ChallengeType.FILL_BLANK, question = "Por la ventana entra ___ luz por la mañana.", orderIndex = 21, grammaticalFocus = "es.vocab.everyday_life", acceptedAnswers = "mucha|bastante", ruleText = "luz is feminine, so the word that measures it has to be feminine too: mucha or bastante. mucha is the answer. mucho is the wrong person or number for the slot. muchas is the wrong person or number for the slot. muy is a form of the word that cannot fill the slot.") {
                option(id = 928400, text = "mucha", correct = true)
                option(id = 928401, text = "mucho", correct = false, errorTag = "WRONG_PERSON")
                option(id = 928402, text = "muchas", correct = false, errorTag = "WRONG_PERSON")
                option(id = 928403, text = "muy", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 91440, lessonId = 901, type = ChallengeType.FILL_BLANK, question = "¿Duermes con la ventana ___?", orderIndex = 22, grammaticalFocus = "es.vocab.everyday_life", acceptedAnswers = "abierta|cerrada", ruleText = "The window is one thing, so the adjective beside it is singular: abierta or cerrada. abierta is the answer. abiertas is the wrong person or number for the slot. abierto is the wrong person or number for the slot. abrir is a form of the word that cannot fill the slot.") {
                option(id = 928800, text = "abierta", correct = true)
                option(id = 928801, text = "abiertas", correct = false, errorTag = "WRONG_PERSON")
                option(id = 928802, text = "abierto", correct = false, errorTag = "WRONG_PERSON")
                option(id = 928803, text = "abrir", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 91460, lessonId = 901, type = ChallengeType.FILL_BLANK, question = "El piso tiene tres ___ y un salón.", orderIndex = 23, grammaticalFocus = "es.vocab.everyday_life", acceptedAnswers = "dormitorios|cuartos", ruleText = "The verb tiene says there is more than one, so the noun is plural: tres dormitorios. A room of any kind is un cuarto, and dormir is the verb. dormitorios is the answer. dormitorio is the wrong person or number for the slot. dormidas is a form of the word that cannot fill the slot. dormir is a form of the word that cannot fill the slot.") {
                option(id = 929200, text = "dormitorios", correct = true)
                option(id = 929201, text = "dormitorio", correct = false, errorTag = "WRONG_PERSON")
                option(id = 929202, text = "dormidas", correct = false, errorTag = "WRONG_FORM")
                option(id = 929203, text = "dormir", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 91480, lessonId = 901, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/la_nevera_esta_en_la_cocina.ogg", orderIndex = 24, grammaticalFocus = "es.vocab.everyday_life", ruleText = "The clip says La nevera está en la cocina. La nevera está en la cocina. is the answer. La nevera está en el cocina is the wrong person or number for the slot. Las nevera está en la cocina is the wrong person or number for the slot. La nevera está en las cocina is the wrong person or number for the slot.") {
                option(id = 929600, text = "La nevera está en la cocina.", romaji = "la nebhˈera estˈa en la kosˈina", correct = true, audioSrc = "asset:///audio/es/la_nevera_esta_en_la_cocina.ogg")
                option(id = 929601, text = "La nevera está en el cocina", correct = false, errorTag = "WRONG_PERSON")
                option(id = 929602, text = "Las nevera está en la cocina", correct = false, errorTag = "WRONG_PERSON")
                option(id = 929603, text = "La nevera está en las cocina", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 91500, lessonId = 901, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/voy_a_limpiar_el_bano_y_el_pasillo.ogg", orderIndex = 25, grammaticalFocus = "es.vocab.everyday_life", ruleText = "The clip says Voy a limpiar el baño y el pasillo. Voy a limpiar el baño y el pasillo. is the answer. Voy a limpiar el baño y la pasillo is the wrong person or number for the slot. Voy a limpiar el baño y el pasillos is the wrong person or number for the slot. Voy a limpios el baño y el pasillo is a form of the word that cannot fill the slot.") {
                option(id = 930000, text = "Voy a limpiar el baño y el pasillo.", romaji = "bˈoi a limpˈiar el bˈaño y el pasˈiyo", correct = true, audioSrc = "asset:///audio/es/voy_a_limpiar_el_bano_y_el_pasillo.ogg")
                option(id = 930001, text = "Voy a limpiar el baño y la pasillo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 930002, text = "Voy a limpiar el baño y el pasillos", correct = false, errorTag = "WRONG_PERSON")
                option(id = 930003, text = "Voy a limpios el baño y el pasillo", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 91520, romaji = "los sˈabadhos los sˈabadhos por la mañˈana lˈimpio la kosˈina y el bˈaño despˈues pˈongo la rrˈopa sˈusia en la labhadhˈora tˈiendo las toˈayas en la terrˈasa si ˈase sol el domˈingo por la tˈardhe me sˈiento en la sˈala kon un lˈibhro", lessonId = 901, type = ChallengeType.STORY, question = "Los sábados\nLos sábados por la mañana limpio la cocina y el baño.\nDespués pongo la ropa sucia en la lavadora.\nTiendo las toallas en la terraza si hace sol.\nEl domingo por la tarde me siento en la sala con un libro.\n\n❓ What does the speaker do on Saturday morning?", audioSrc = "asset:///audio/es/story_901.ogg", orderIndex = 26) {
                option(id = 930400, text = "Cleans the kitchen and the bathroom", correct = true)
                option(id = 930401, text = "Reads a book in the living room", correct = false)
                option(id = 930402, text = "Hangs the towels out on the terrace", correct = false)
                option(id = 930403, text = "Cooks lunch for the family", correct = false)
            }
            challenge(id = 92000, lessonId = 902, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (6 pairs)", orderIndex = 0) {
                option(id = 940000, text = "la carne", correct = true)
                option(id = 940001, text = "the meat", correct = true)
                option(id = 940002, text = "el pollo", correct = true)
                option(id = 940003, text = "the chicken", correct = true)
                option(id = 940004, text = "el pescado", correct = true)
                option(id = 940005, text = "the fish", correct = true)
                option(id = 940006, text = "la verdura", correct = true)
                option(id = 940007, text = "the vegetable", correct = true)
                option(id = 940008, text = "la fruta", correct = true)
                option(id = 940009, text = "the fruit", correct = true)
                option(id = 940010, text = "el queso", correct = true)
                option(id = 940011, text = "the cheese", correct = true)
            }
            challenge(id = 92020, lessonId = 902, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (6 pairs)", orderIndex = 1) {
                option(id = 940400, text = "el huevo", correct = true)
                option(id = 940401, text = "the egg", correct = true)
                option(id = 940402, text = "la sopa", correct = true)
                option(id = 940403, text = "the soup", correct = true)
                option(id = 940404, text = "la manzana", correct = true)
                option(id = 940405, text = "the apple", correct = true)
                option(id = 940406, text = "la naranja", correct = true)
                option(id = 940407, text = "the orange", correct = true)
                option(id = 940408, text = "el plátano", correct = true)
                option(id = 940409, text = "the banana", correct = true)
                option(id = 940410, text = "la uva", correct = true)
                option(id = 940411, text = "the grape", correct = true)
            }
            challenge(id = 92040, lessonId = 902, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (6 pairs)", orderIndex = 2) {
                option(id = 940800, text = "la fresa", correct = true)
                option(id = 940801, text = "the strawberry", correct = true)
                option(id = 940802, text = "la cebolla", correct = true)
                option(id = 940803, text = "the onion", correct = true)
                option(id = 940804, text = "el ajo", correct = true)
                option(id = 940805, text = "the garlic", correct = true)
                option(id = 940806, text = "la zanahoria", correct = true)
                option(id = 940807, text = "the carrot", correct = true)
                option(id = 940808, text = "la lechuga", correct = true)
                option(id = 940809, text = "the lettuce", correct = true)
                option(id = 940810, text = "el tomate", correct = true)
                option(id = 940811, text = "the tomato", correct = true)
            }
            challenge(id = 92060, lessonId = 902, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (6 pairs)", orderIndex = 3) {
                option(id = 941200, text = "el restaurante", correct = true)
                option(id = 941201, text = "the restaurant", correct = true)
                option(id = 941202, text = "la cafetería", correct = true)
                option(id = 941203, text = "the café", correct = true)
                option(id = 941204, text = "el mercado", correct = true)
                option(id = 941205, text = "the market", correct = true)
                option(id = 941206, text = "la tienda", correct = true)
                option(id = 941207, text = "the shop", correct = true)
                option(id = 941208, text = "el supermercado", correct = true)
                option(id = 941209, text = "the supermarket", correct = true)
                option(id = 941210, text = "la panadería", correct = true)
                option(id = 941211, text = "the bakery", correct = true)
            }
            challenge(id = 92080, lessonId = 902, type = ChallengeType.SELECT, question = "Which one means 'the waiter'?", orderIndex = 4, grammaticalFocus = "es.vocab.everyday_life", ruleText = " el camarero is the answer. la camarero is the wrong person or number for the slot. los camarero is the wrong person or number for the slot. el camareros is the wrong person or number for the slot.") {
                option(id = 941600, text = "el camarero", correct = true)
                option(id = 941601, text = "la camarero", correct = false, errorTag = "WRONG_PERSON")
                option(id = 941602, text = "los camarero", correct = false, errorTag = "WRONG_PERSON")
                option(id = 941603, text = "el camareros", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 92100, lessonId = 902, type = ChallengeType.SELECT, question = "Which one means 'the menu'?", orderIndex = 5, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la carta is the answer. el carta is the wrong person or number for the slot. las carta is the wrong person or number for the slot. la cartas is the wrong person or number for the slot.") {
                option(id = 942000, text = "la carta", correct = true)
                option(id = 942001, text = "el carta", correct = false, errorTag = "WRONG_PERSON")
                option(id = 942002, text = "las carta", correct = false, errorTag = "WRONG_PERSON")
                option(id = 942003, text = "la cartas", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 92120, lessonId = 902, type = ChallengeType.SELECT, question = "Which one means 'the tip'?", orderIndex = 6, grammaticalFocus = "es.vocab.everyday_life", ruleText = " la propina is the answer. el propina is the wrong person or number for the slot. las propina is the wrong person or number for the slot. la propinas is the wrong person or number for the slot.") {
                option(id = 942400, text = "la propina", correct = true)
                option(id = 942401, text = "el propina", correct = false, errorTag = "WRONG_PERSON")
                option(id = 942402, text = "las propina", correct = false, errorTag = "WRONG_PERSON")
                option(id = 942403, text = "la propinas", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 92140, lessonId = 902, type = ChallengeType.SELECT, question = "Which one means 'the order you ask for'?", orderIndex = 7, grammaticalFocus = "es.vocab.everyday_life", ruleText = " el pedido is the answer. la pedido is the wrong person or number for the slot. los pedido is the wrong person or number for the slot. el pedidos is the wrong person or number for the slot.") {
                option(id = 942800, text = "el pedido", correct = true)
                option(id = 942801, text = "la pedido", correct = false, errorTag = "WRONG_PERSON")
                option(id = 942802, text = "los pedido", correct = false, errorTag = "WRONG_PERSON")
                option(id = 942803, text = "el pedidos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 92160, lessonId = 902, type = ChallengeType.SELECT, question = "Which one means 'a bowl of soup'?", orderIndex = 8, grammaticalFocus = "es.vocab.everyday_life", ruleText = " un plato de sopa is the answer. unos plato de sopa is the wrong person or number for the slot. un plato de sopas is a form of the word that cannot fill the slot. una plato de sopa is the wrong person or number for the slot.") {
                option(id = 943200, text = "un plato de sopa", correct = true)
                option(id = 943201, text = "unos plato de sopa", correct = false, errorTag = "WRONG_PERSON")
                option(id = 943202, text = "un plato de sopas", correct = false, errorTag = "WRONG_FORM")
                option(id = 943203, text = "una plato de sopa", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 92180, lessonId = 902, type = ChallengeType.SELECT, question = "Which one means 'a hot coffee'?", orderIndex = 9, grammaticalFocus = "es.vocab.everyday_life", ruleText = " un café caliente is the answer. un café calientes is a form of the word that cannot fill the slot. un café frío is a form of the word that cannot fill the slot. una café caliente is the wrong person or number for the slot.") {
                option(id = 943600, text = "un café caliente", correct = true)
                option(id = 943601, text = "un café calientes", correct = false, errorTag = "WRONG_FORM")
                option(id = 943602, text = "un café frío", correct = false, errorTag = "WRONG_FORM")
                option(id = 943603, text = "una café caliente", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 92200, lessonId = 902, type = ChallengeType.SELECT, question = "Which one means 'a fresh fish'?", orderIndex = 10, grammaticalFocus = "es.vocab.everyday_life", ruleText = " un pescado fresco is the answer. un pescado frescos is a form of the word that cannot fill the slot. un pescado fresca is a form of the word that cannot fill the slot. una pescado fresco is the wrong person or number for the slot.") {
                option(id = 944000, text = "un pescado fresco", correct = true)
                option(id = 944001, text = "un pescado frescos", correct = false, errorTag = "WRONG_FORM")
                option(id = 944002, text = "un pescado fresca", correct = false, errorTag = "WRONG_FORM")
                option(id = 944003, text = "una pescado fresco", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 92220, lessonId = 902, type = ChallengeType.SELECT, question = "Which one means 'a glass of water'?", orderIndex = 11, grammaticalFocus = "es.vocab.everyday_life", ruleText = " un vaso de agua is the answer. unos vaso de agua is the wrong person or number for the slot. un vaso de aguas is a form of the word that cannot fill the slot. una vaso de agua is the wrong person or number for the slot.") {
                option(id = 944400, text = "un vaso de agua", correct = true)
                option(id = 944401, text = "unos vaso de agua", correct = false, errorTag = "WRONG_PERSON")
                option(id = 944402, text = "un vaso de aguas", correct = false, errorTag = "WRONG_FORM")
                option(id = 944403, text = "una vaso de agua", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 92240, lessonId = 902, type = ChallengeType.SELECT, question = "Which one means 'a delicious dessert'?", orderIndex = 12, grammaticalFocus = "es.vocab.everyday_life", ruleText = " un postre delicioso is the answer. un postre deliciosos is a form of the word that cannot fill the slot. un postre deliciosa is a form of the word that cannot fill the slot. una postre delicioso is the wrong person or number for the slot.") {
                option(id = 944800, text = "un postre delicioso", correct = true)
                option(id = 944801, text = "un postre deliciosos", correct = false, errorTag = "WRONG_FORM")
                option(id = 944802, text = "un postre deliciosa", correct = false, errorTag = "WRONG_FORM")
                option(id = 944803, text = "una postre delicioso", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 92260, lessonId = 902, type = ChallengeType.SELECT, question = "Which one means 'a cheap meal'?", orderIndex = 13, grammaticalFocus = "es.vocab.everyday_life", ruleText = " una comida barata is the answer. una comidas baratas is a form of the word that cannot fill the slot. una comida barato is a form of the word that cannot fill the slot. un comida barata is the wrong person or number for the slot.") {
                option(id = 945200, text = "una comida barata", correct = true)
                option(id = 945201, text = "una comidas baratas", correct = false, errorTag = "WRONG_FORM")
                option(id = 945202, text = "una comida barato", correct = false, errorTag = "WRONG_FORM")
                option(id = 945203, text = "un comida barata", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 92280, lessonId = 902, type = ChallengeType.SELECT, question = "Which one means 'to order' (in a restaurant)?", orderIndex = 14, grammaticalFocus = "es.vocab.everyday_life", ruleText = " pedir is the answer. pides is the wrong person or number for the slot. pidió puts the verb in a tense the sentence does not ask for. pedirse is a form of the word that cannot fill the slot.") {
                option(id = 945600, text = "pedir", correct = true)
                option(id = 945601, text = "pides", correct = false, errorTag = "WRONG_PERSON")
                option(id = 945602, text = "pidió", correct = false, errorTag = "WRONG_TENSE")
                option(id = 945603, text = "pedirse", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 92300, lessonId = 902, type = ChallengeType.SELECT, question = "Which one means 'to try a dish'?", orderIndex = 15, grammaticalFocus = "es.vocab.everyday_life", ruleText = " probar is the answer. pruebo is the wrong person or number for the slot. probó puts the verb in a tense the sentence does not ask for. probarse is a form of the word that cannot fill the slot.") {
                option(id = 946000, text = "probar", correct = true)
                option(id = 946001, text = "pruebo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 946002, text = "probó", correct = false, errorTag = "WRONG_TENSE")
                option(id = 946003, text = "probarse", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 92320, lessonId = 902, type = ChallengeType.CONJUGATE, question = "Which present form of pedir goes with 'yo'?", orderIndex = 16, grammaticalFocus = "es.vocab.everyday_life", ruleText = "pedir changes e- to ie- in the present, and the yo form carries no accent: pido. pido is the answer. pides is the wrong person or number for the slot. pide is the wrong person or number for the slot. pedimos is the wrong person or number for the slot.") {
                option(id = 946400, text = "pido", correct = true)
                option(id = 946401, text = "pides", correct = false, errorTag = "WRONG_PERSON")
                option(id = 946402, text = "pide", correct = false, errorTag = "WRONG_PERSON")
                option(id = 946403, text = "pedimos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 92340, lessonId = 902, type = ChallengeType.CONJUGATE, question = "Which present form of probar goes with 'nosotros'?", orderIndex = 17, grammaticalFocus = "es.vocab.everyday_life", ruleText = "probar changes o- to ue- in the stressed syllable, but nosotros keeps the plain stem: probamos. probamos is the answer. probo is the wrong person or number for the slot. probas is the wrong person or number for the slot. prueba is the wrong person or number for the slot.") {
                option(id = 946800, text = "probamos", correct = true)
                option(id = 946801, text = "probo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 946802, text = "probas", correct = false, errorTag = "WRONG_PERSON")
                option(id = 946803, text = "prueba", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 92360, lessonId = 902, type = ChallengeType.CONJUGATE, question = "Which present form of volver goes with 'tú'?", orderIndex = 18, grammaticalFocus = "es.vocab.everyday_life", ruleText = "volver changes o- to ue- in the present, and the tú ending is -es: vuelves. vuelves is the answer. vuelvo is the wrong person or number for the slot. vuelve is the wrong person or number for the slot. volvemos is the wrong person or number for the slot.") {
                option(id = 947200, text = "vuelves", correct = true)
                option(id = 947201, text = "vuelvo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 947202, text = "vuelve", correct = false, errorTag = "WRONG_PERSON")
                option(id = 947203, text = "volvemos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 92380, lessonId = 902, type = ChallengeType.CONJUGATE, question = "Which present form of servir goes with 'ellos'?", orderIndex = 19, grammaticalFocus = "es.vocab.everyday_life", ruleText = "servir changes e- to ie- in the present, and ellos takes -n: sirven. sirven is the answer. sirvo is the wrong person or number for the slot. sirves is the wrong person or number for the slot. servimos is the wrong person or number for the slot.") {
                option(id = 947600, text = "sirven", correct = true)
                option(id = 947601, text = "sirvo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 947602, text = "sirves", correct = false, errorTag = "WRONG_PERSON")
                option(id = 947603, text = "servimos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 92400, lessonId = 902, type = ChallengeType.FILL_BLANK, question = "¿___ carne, Marta?", orderIndex = 20, grammaticalFocus = "es.vocab.everyday_life", acceptedAnswers = "comes", ruleText = "The question is put to Marta as tú, so the second person singular comes is the answer. como is the yo form, comió is the preterite, and comerse is the infinitive. comes is the answer. como is the wrong person or number for the slot. comió puts the verb in a tense the sentence does not ask for. comerse is a form of the word that cannot fill the slot.") {
                option(id = 948000, text = "comes", correct = true)
                option(id = 948001, text = "como", correct = false, errorTag = "WRONG_PERSON")
                option(id = 948002, text = "comió", correct = false, errorTag = "WRONG_TENSE")
                option(id = 948003, text = "comerse", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 92420, lessonId = 902, type = ChallengeType.FILL_BLANK, question = "¿Me ___ la sal, por favor?", orderIndex = 21, grammaticalFocus = "es.vocab.everyday_life", acceptedAnswers = "pasa|pásame", ruleText = "A request to a waiter is for the salt to be passed, and the verb is pasar. The two accepted spellings are the plain pasa and the attached pásame, which Spanish writes with an accent because the stress moves onto me. pasa is the answer. pase is a form of the word that cannot fill the slot. pasas is the wrong person or number for the slot. pasar is a form of the word that cannot fill the slot.") {
                option(id = 948400, text = "pasa", correct = true)
                option(id = 948401, text = "pase", correct = false, errorTag = "WRONG_FORM")
                option(id = 948402, text = "pasas", correct = false, errorTag = "WRONG_PERSON")
                option(id = 948403, text = "pasar", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 92440, lessonId = 902, type = ChallengeType.FILL_BLANK, question = "Esta sopa está muy ___.", orderIndex = 22, grammaticalFocus = "es.vocab.everyday_life", acceptedAnswers = "sabrosa|deliciosa", ruleText = "Two adjectives say that a soup tastes good: sabrosa and deliciosa. sabroso would be the form for a masculine or a plural noun, and sabrosura is the noun for goodness, not an adjective. sabrosa is the answer. sabrosos is a form of the word that cannot fill the slot. sabroso is a form of the word that cannot fill the slot. sabrosura is a form of the word that cannot fill the slot.") {
                option(id = 948800, text = "sabrosa", correct = true)
                option(id = 948801, text = "sabrosos", correct = false, errorTag = "WRONG_FORM")
                option(id = 948802, text = "sabroso", correct = false, errorTag = "WRONG_FORM")
                option(id = 948803, text = "sabrosura", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 92460, lessonId = 902, type = ChallengeType.FILL_BLANK, question = "Vamos a ___ la mesa en cuanto terminemos.", orderIndex = 23, grammaticalFocus = "es.vocab.everyday_life", acceptedAnswers = "recoger|levantar", ruleText = "Both regions say recoger la mesa to clear the table; levantar la mesa is also heard in Latin America for the same thing. recoge is the tú form and recogida is a noun. recoger is the answer. recoge is a form of the word that cannot fill the slot. recogemos is the wrong person or number for the slot. recogida is a form of the word that cannot fill the slot.") {
                option(id = 949200, text = "recoger", correct = true)
                option(id = 949201, text = "recoge", correct = false, errorTag = "WRONG_FORM")
                option(id = 949202, text = "recogemos", correct = false, errorTag = "WRONG_PERSON")
                option(id = 949203, text = "recogida", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 92480, lessonId = 902, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/quiero_pedir_la_carta.ogg", orderIndex = 24, grammaticalFocus = "es.vocab.everyday_life", ruleText = "The clip says Quiero pedir la carta, por favor. Quiero pedir la carta, por favor. is the answer. Quiero pedir la cartas is the wrong person or number for the slot. Quiero pedir el carta is the wrong person or number for the slot. Quiero pedir la cocina is a form of the word that cannot fill the slot.") {
                option(id = 949600, text = "Quiero pedir la carta, por favor.", romaji = "kˈiero pedhˈir la kˈarta por fabhˈor", correct = true, audioSrc = "asset:///audio/es/quiero_pedir_la_carta.ogg")
                option(id = 949601, text = "Quiero pedir la cartas", correct = false, errorTag = "WRONG_PERSON")
                option(id = 949602, text = "Quiero pedir el carta", correct = false, errorTag = "WRONG_PERSON")
                option(id = 949603, text = "Quiero pedir la cocina", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 92500, lessonId = 902, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/la_propina_esta_incluida.ogg", orderIndex = 25, grammaticalFocus = "es.vocab.everyday_life", ruleText = "The clip says La propina está incluida en la cuenta. La propina está incluida en la cuenta. is the answer. La propinas está incluida is the wrong person or number for the slot. La propina está incluidos is a form of the word that cannot fill the slot. La propina está incluida en el cuenta is the wrong person or number for the slot.") {
                option(id = 950000, text = "La propina está incluida en la cuenta.", romaji = "la propˈina estˈa inklˈuidha en la kwˈenta", correct = true, audioSrc = "asset:///audio/es/la_propina_esta_incluida.ogg")
                option(id = 950001, text = "La propinas está incluida", correct = false, errorTag = "WRONG_PERSON")
                option(id = 950002, text = "La propina está incluidos", correct = false, errorTag = "WRONG_FORM")
                option(id = 950003, text = "La propina está incluida en el cuenta", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 92520, romaji = "en el merkˈadho ˈayer fˈui al merkˈadho kon mi ermˈana komprˈamos frˈuta berdhˈura y pan pˈara la semˈana en la panadherˈia paghˈamos kon tarjˈeta despˈues tomˈamos un kafˈe en la terrˈaza", lessonId = 902, type = ChallengeType.STORY, question = "En el mercado\nAyer fui al mercado con mi hermana.\nCompramos fruta, verdura y pan para la semana.\nEn la panadería pagamos con tarjeta.\nDespués tomamos un café en la terraza.\n\n❓ How did they pay at the bakery?", audioSrc = "asset:///audio/es/story_902.ogg", orderIndex = 26) {
                option(id = 950400, text = "By card", correct = true)
                option(id = 950401, text = "In cash", correct = false)
                option(id = 950402, text = "With a voucher", correct = false)
                option(id = 950403, text = "The speaker does not say", correct = false)
            }
        },
        unit(id = 37, courseId = 1, title = "Unit 18: Out in the World", description = "Getting around, the city, and the working week", orderIndex = 17) {
            lesson(id = 903, title = "Lesson 39: Travel, Transport and Directions", orderIndex = 0)
            lesson(id = 904, title = "Lesson 40: The City, Places and the Weather", orderIndex = 1)
            lesson(id = 905, title = "Lesson 41: Work, Jobs and the Workplace", orderIndex = 2)
            challenge(id = 93000, lessonId = 903, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (4 pairs)", orderIndex = 0) {
                option(id = 960000, text = "el avión", correct = true)
                option(id = 960001, text = "the plane", correct = true)
                option(id = 960002, text = "el autobús", correct = true)
                option(id = 960003, text = "the bus", correct = true)
                option(id = 960004, text = "el metro", correct = true)
                option(id = 960005, text = "the underground", correct = true)
                option(id = 960006, text = "el coche", correct = true)
                option(id = 960007, text = "the car", correct = true)
            }
            challenge(id = 93020, lessonId = 903, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (4 pairs)", orderIndex = 1) {
                option(id = 960400, text = "el barco", correct = true)
                option(id = 960401, text = "the boat", correct = true)
                option(id = 960402, text = "el taxi", correct = true)
                option(id = 960403, text = "the taxi", correct = true)
                option(id = 960404, text = "la bicicleta", correct = true)
                option(id = 960405, text = "the bicycle", correct = true)
                option(id = 960406, text = "el tranvía", correct = true)
                option(id = 960407, text = "the tram", correct = true)
            }
            challenge(id = 93040, lessonId = 903, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (4 pairs)", orderIndex = 2, ruleText = "A ticket is el billete in Spain and el boleto in Latin America: the same noun, used in either region, so the English side is the same for both. Match each word to its meaning.") {
                option(id = 960800, text = "el billete", correct = true)
                option(id = 960801, text = "the ticket", correct = true)
                option(id = 960802, text = "el andén", correct = true)
                option(id = 960803, text = "the platform", correct = true)
                option(id = 960804, text = "la maleta", correct = true)
                option(id = 960805, text = "the suitcase", correct = true)
                option(id = 960806, text = "el pasaporte", correct = true)
                option(id = 960807, text = "the passport", correct = true)
            }
            challenge(id = 93060, lessonId = 903, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (4 pairs)", orderIndex = 3) {
                option(id = 961200, text = "el vuelo", correct = true)
                option(id = 961201, text = "the flight", correct = true)
                option(id = 961202, text = "la llegada", correct = true)
                option(id = 961203, text = "the arrival", correct = true)
                option(id = 961204, text = "la salida", correct = true)
                option(id = 961205, text = "the departure", correct = true)
                option(id = 961206, text = "el retraso", correct = true)
                option(id = 961207, text = "the delay", correct = true)
            }
            challenge(id = 93080, lessonId = 903, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (4 pairs)", orderIndex = 4) {
                option(id = 961600, text = "el conductor", correct = true)
                option(id = 961601, text = "the driver", correct = true)
                option(id = 961602, text = "el pasajero", correct = true)
                option(id = 961603, text = "the passenger", correct = true)
                option(id = 961604, text = "la ventanilla", correct = true)
                option(id = 961605, text = "the ticket window", correct = true)
                option(id = 961606, text = "el trayecto", correct = true)
                option(id = 961607, text = "the journey", correct = true)
            }
            challenge(id = 93100, lessonId = 903, type = ChallengeType.SELECT, question = "Which one means 'the bus stop'?", orderIndex = 5, grammaticalFocus = "es.vocab.travel", ruleText = " la parada is the answer. el parada is the wrong person or number for the slot. las parada is the wrong person or number for the slot. la paradas is the wrong person or number for the slot.") {
                option(id = 962000, text = "la parada", correct = true)
                option(id = 962001, text = "el parada", correct = false, errorTag = "WRONG_PERSON")
                option(id = 962002, text = "las parada", correct = false, errorTag = "WRONG_PERSON")
                option(id = 962003, text = "la paradas", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 93120, lessonId = 903, type = ChallengeType.SELECT, question = "Which one means 'the taxi rank'?", orderIndex = 6, grammaticalFocus = "es.vocab.travel", ruleText = " la parada de taxis is the answer. el parada de taxis is the wrong person or number for the slot. la paradas de taxis is the wrong person or number for the slot. la parada de taxi is a form of the word that cannot fill the slot.") {
                option(id = 962400, text = "la parada de taxis", correct = true)
                option(id = 962401, text = "el parada de taxis", correct = false, errorTag = "WRONG_PERSON")
                option(id = 962402, text = "la paradas de taxis", correct = false, errorTag = "WRONG_PERSON")
                option(id = 962403, text = "la parada de taxi", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 93140, lessonId = 903, type = ChallengeType.SELECT, question = "Which one means 'the flight number'?", orderIndex = 7, grammaticalFocus = "es.vocab.travel", ruleText = " el número de vuelo is the answer. la número de vuelo is the wrong person or number for the slot. el número de vuelos is a form of the word that cannot fill the slot. los número de vuelo is the wrong person or number for the slot.") {
                option(id = 962800, text = "el número de vuelo", correct = true)
                option(id = 962801, text = "la número de vuelo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 962802, text = "el número de vuelos", correct = false, errorTag = "WRONG_FORM")
                option(id = 962803, text = "los número de vuelo", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 93160, lessonId = 903, type = ChallengeType.SELECT, question = "Which one means 'to get off'?", orderIndex = 8, grammaticalFocus = "es.vocab.travel", ruleText = " bajarse is the answer. bajar is a form of the word that cannot fill the slot. bajarse mal is a form of the word that cannot fill the slot. bajamos is the wrong person or number for the slot.") {
                option(id = 963200, text = "bajarse", correct = true)
                option(id = 963201, text = "bajar", correct = false, errorTag = "WRONG_FORM")
                option(id = 963202, text = "bajarse mal", correct = false, errorTag = "WRONG_FORM")
                option(id = 963203, text = "bajamos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 93180, lessonId = 903, type = ChallengeType.SELECT, question = "Which one means 'a one-way ticket'?", orderIndex = 9, grammaticalFocus = "es.vocab.travel", ruleText = " un billete de ida is the answer, and un boleto de ida names the same ticket: el boleto is the everyday word in Latin America, and el billete is the everyday word in Spain.\nun billete de idas is a form of the word that cannot fill the slot. un billete de vuelta is a form of the word that cannot fill the slot. una billete de ida is the wrong person or number for the slot.") {
                option(id = 963600, text = "un billete de ida", correct = true)
                option(id = 963604, text = "un boleto de ida", correct = true)
                option(id = 963601, text = "un billete de idas", correct = false, errorTag = "WRONG_FORM")
                option(id = 963602, text = "un billete de vuelta", correct = false, errorTag = "WRONG_FORM")
                option(id = 963603, text = "una billete de ida", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 93200, lessonId = 903, type = ChallengeType.SELECT, question = "Which one means 'a return ticket'?", orderIndex = 10, grammaticalFocus = "es.vocab.travel", ruleText = " un billete de ida y vuelta is the answer, and un boleto de ida y vuelta names the same return ticket: el boleto is the everyday word in Latin America, and el billete is the everyday word in Spain.\nun billete de ida y ida is a form of the word that cannot fill the slot. una billete de ida y vuelta is the wrong person or number for the slot. un billete de ida y vueltas is a form of the word that cannot fill the slot.") {
                option(id = 964000, text = "un billete de ida y vuelta", correct = true)
                option(id = 964004, text = "un boleto de ida y vuelta", correct = true)
                option(id = 964001, text = "un billete de ida y ida", correct = false, errorTag = "WRONG_FORM")
                option(id = 964002, text = "una billete de ida y vuelta", correct = false, errorTag = "WRONG_PERSON")
                option(id = 964003, text = "un billete de ida y vueltas", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 93220, lessonId = 903, type = ChallengeType.SELECT, question = "Which one means 'to miss the train'?", orderIndex = 11, grammaticalFocus = "es.vocab.travel", ruleText = " perder el tren is the answer. perder el tren is a form of the word that cannot fill the slot. perdieron el tren puts the verb in a tense the sentence does not ask for. pierde el tren is the wrong person or number for the slot.") {
                option(id = 964400, text = "perder el tren", correct = true)
                option(id = 964401, text = "perder el tren", correct = false, errorTag = "WRONG_FORM")
                option(id = 964402, text = "perdieron el tren", correct = false, errorTag = "WRONG_TENSE")
                option(id = 964403, text = "pierde el tren", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 93240, lessonId = 903, type = ChallengeType.SELECT, question = "Which one means 'to change trains'?", orderIndex = 12, grammaticalFocus = "es.vocab.travel", ruleText = " cambiar de tren is the answer. cambiar del tren is a form of the word that cannot fill the slot. cambia de tren is the wrong person or number for the slot. cambiar del tren is a form of the word that cannot fill the slot.") {
                option(id = 964800, text = "cambiar de tren", correct = true)
                option(id = 964801, text = "cambiar del tren", correct = false, errorTag = "WRONG_FORM")
                option(id = 964802, text = "cambia de tren", correct = false, errorTag = "WRONG_PERSON")
                option(id = 964803, text = "cambiar del tren", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 93260, lessonId = 903, type = ChallengeType.SELECT, question = "Which one means 'the journey takes an hour'?", orderIndex = 13, grammaticalFocus = "es.vocab.travel", ruleText = " el viaje dura una hora is the answer. el viaje duran una hora is the wrong person or number for the slot. el viaje dura unas hora is a form of the word that cannot fill the slot. el viajes dura una hora is the wrong person or number for the slot.") {
                option(id = 965200, text = "el viaje dura una hora", correct = true)
                option(id = 965201, text = "el viaje duran una hora", correct = false, errorTag = "WRONG_PERSON")
                option(id = 965202, text = "el viaje dura unas hora", correct = false, errorTag = "WRONG_FORM")
                option(id = 965203, text = "el viajes dura una hora", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 93280, lessonId = 903, type = ChallengeType.SELECT, question = "Which one means 'to check in (at the airport)'?", orderIndex = 14, grammaticalFocus = "es.vocab.travel", ruleText = " facturar el equipaje is the answer. facturar el equipaje is a form of the word that cannot fill the slot. factura el equipaje is the wrong person or number for the slot. facturar la equipaje is the wrong person or number for the slot.") {
                option(id = 965600, text = "facturar el equipaje", correct = true)
                option(id = 965601, text = "facturar el equipaje", correct = false, errorTag = "WRONG_FORM")
                option(id = 965602, text = "factura el equipaje", correct = false, errorTag = "WRONG_PERSON")
                option(id = 965603, text = "facturar la equipaje", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 93300, lessonId = 903, type = ChallengeType.SELECT, question = "Which one means 'the terminal'?", orderIndex = 15, grammaticalFocus = "es.vocab.travel", ruleText = " la terminal is the answer. el terminal is the wrong person or number for the slot. las terminal is the wrong person or number for the slot. la terminales is the wrong person or number for the slot.") {
                option(id = 966000, text = "la terminal", correct = true)
                option(id = 966001, text = "el terminal", correct = false, errorTag = "WRONG_PERSON")
                option(id = 966002, text = "las terminal", correct = false, errorTag = "WRONG_PERSON")
                option(id = 966003, text = "la terminales", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 93320, lessonId = 903, type = ChallengeType.SELECT, question = "Which one means 'a long journey'?", orderIndex = 16, grammaticalFocus = "es.vocab.travel", ruleText = " un viaje largo is the answer. un viaje largos is a form of the word that cannot fill the slot. un viaje larga is a form of the word that cannot fill the slot. una viaje largo is the wrong person or number for the slot.") {
                option(id = 966400, text = "un viaje largo", correct = true)
                option(id = 966401, text = "un viaje largos", correct = false, errorTag = "WRONG_FORM")
                option(id = 966402, text = "un viaje larga", correct = false, errorTag = "WRONG_FORM")
                option(id = 966403, text = "una viaje largo", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 93340, lessonId = 903, type = ChallengeType.CONJUGATE, question = "Which present form of llegar goes with 'yo'?", orderIndex = 17, grammaticalFocus = "es.vocab.travel", ruleText = "llegar is a regular -ar verb, and the yo ending is -o: llego. llego is the answer. llegas is the wrong person or number for the slot. llega is the wrong person or number for the slot. llegamos is the wrong person or number for the slot.") {
                option(id = 966800, text = "llego", correct = true)
                option(id = 966801, text = "llegas", correct = false, errorTag = "WRONG_PERSON")
                option(id = 966802, text = "llega", correct = false, errorTag = "WRONG_PERSON")
                option(id = 966803, text = "llegamos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 93360, lessonId = 903, type = ChallengeType.CONJUGATE, question = "Which present form of salir goes with 'tú'?", orderIndex = 18, grammaticalFocus = "es.vocab.travel", ruleText = "salir changes the stem to salg- in the yo form only, and the tú ending is -es: sales. sales is the answer. salgo is the wrong person or number for the slot. sale is the wrong person or number for the slot. salimos is the wrong person or number for the slot.") {
                option(id = 967200, text = "sales", correct = true)
                option(id = 967201, text = "salgo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 967202, text = "sale", correct = false, errorTag = "WRONG_PERSON")
                option(id = 967203, text = "salimos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 93380, lessonId = 903, type = ChallengeType.CONJUGATE, question = "Which present form of conducir goes with 'nosotros'?", orderIndex = 19, grammaticalFocus = "es.vocab.travel", ruleText = "conducir turns c into z before a stressed o, but nosotros keeps the plain stem: conducimos. conducimos is the answer. conduzco is the wrong person or number for the slot. conduces is the wrong person or number for the slot. conduce is the wrong person or number for the slot.") {
                option(id = 967600, text = "conducimos", correct = true)
                option(id = 967601, text = "conduzco", correct = false, errorTag = "WRONG_PERSON")
                option(id = 967602, text = "conduces", correct = false, errorTag = "WRONG_PERSON")
                option(id = 967603, text = "conduce", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 93400, lessonId = 903, type = ChallengeType.CONJUGATE, question = "Which present form of atravesar goes with 'ellos'?", orderIndex = 20, grammaticalFocus = "es.vocab.travel", ruleText = "atravesar changes e- to ie- in the present, and ellos takes -n: atraviesan. atraviesan is the answer. atravieso is the wrong person or number for the slot. atraviesas is the wrong person or number for the slot. atraviesamos is the wrong person or number for the slot.") {
                option(id = 968000, text = "atraviesan", correct = true)
                option(id = 968001, text = "atravieso", correct = false, errorTag = "WRONG_PERSON")
                option(id = 968002, text = "atraviesas", correct = false, errorTag = "WRONG_PERSON")
                option(id = 968003, text = "atraviesamos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 93420, lessonId = 903, type = ChallengeType.FILL_BLANK, question = "El tren ___ a las ocho en punto.", orderIndex = 21, grammaticalFocus = "es.vocab.travel", acceptedAnswers = "sale|saldrá", ruleText = "A timetable is in the present: el tren sale a las ocho. saldrá would be used for a departure already decided and stated as a future event; neither infinitive nor a subjunctive can fill this slot. sale is the answer. salir is a form of the word that cannot fill the slot. salimos is the wrong person or number for the slot. salga is a form of the word that cannot fill the slot.") {
                option(id = 968400, text = "sale", correct = true)
                option(id = 968401, text = "salir", correct = false, errorTag = "WRONG_FORM")
                option(id = 968402, text = "salimos", correct = false, errorTag = "WRONG_PERSON")
                option(id = 968403, text = "salga", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 93440, lessonId = 903, type = ChallengeType.FILL_BLANK, question = "En el andén ___ mucha gente esperando.", orderIndex = 22, grammaticalFocus = "es.vocab.travel", acceptedAnswers = "hay", ruleText = "There is one place with people in it, so Spanish uses the singular hay, and the crowd is still there in the present. haber and he are forms of the verb rather than of the phrase. hay is the answer. han is the wrong person or number for the slot. haber is a form of the word that cannot fill the slot. he is a form of the word that cannot fill the slot.") {
                option(id = 968800, text = "hay", correct = true)
                option(id = 968801, text = "han", correct = false, errorTag = "WRONG_PERSON")
                option(id = 968802, text = "haber", correct = false, errorTag = "WRONG_FORM")
                option(id = 968803, text = "he", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 93460, lessonId = 903, type = ChallengeType.FILL_BLANK, question = "¿___ el billete en la ventanilla o por internet?", orderIndex = 23, grammaticalFocus = "es.vocab.travel", acceptedAnswers = "compras", ruleText = "The question is put to the listener, so the second person singular of comprar is the answer: compras. comprado would be a participle and comprar the infinitive. compras is the answer. compro is the wrong person or number for the slot. compró puts the verb in a tense the sentence does not ask for. comprar is a form of the word that cannot fill the slot.") {
                option(id = 969200, text = "compras", correct = true)
                option(id = 969201, text = "compro", correct = false, errorTag = "WRONG_PERSON")
                option(id = 969202, text = "compró", correct = false, errorTag = "WRONG_TENSE")
                option(id = 969203, text = "comprar", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 93480, lessonId = 903, type = ChallengeType.FILL_BLANK, question = "Para llegar al puerto, ___ todo recto dos calles.", orderIndex = 24, grammaticalFocus = "es.vocab.travel", acceptedAnswers = "siga|sigan", ruleText = "A polite direction to one person uses the usted command of seguir, which is siga. sigan is the same command for a group, sigo is the yo form, and seguir is the infinitive. siga is the answer. sigo is the wrong person or number for the slot. seguir is a form of the word that cannot fill the slot. siguen is the wrong person or number for the slot.") {
                option(id = 969600, text = "siga", correct = true)
                option(id = 969601, text = "sigo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 969602, text = "seguir", correct = false, errorTag = "WRONG_FORM")
                option(id = 969603, text = "siguen", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 93500, lessonId = 903, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/el_avion_llega_tarde.ogg", orderIndex = 25, grammaticalFocus = "es.vocab.travel", ruleText = "The clip says El avión llega tarde por la niebla. El avión llega tarde por la niebla. is the answer. El avión llegan tarde is the wrong person or number for the slot. El avión llega tardes is a form of the word that cannot fill the slot. El avión llega temprano is a form of the word that cannot fill the slot.") {
                option(id = 970000, text = "El avión llega tarde por la niebla.", romaji = "el abhˈion yˈegha tˈardhe por la nˈiebhla", correct = true, audioSrc = "asset:///audio/es/el_avion_llega_tarde.ogg")
                option(id = 970001, text = "El avión llegan tarde", correct = false, errorTag = "WRONG_PERSON")
                option(id = 970002, text = "El avión llega tardes", correct = false, errorTag = "WRONG_FORM")
                option(id = 970003, text = "El avión llega temprano", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 93520, lessonId = 903, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/gire_a_la_derecha_en_la_esquina.ogg", orderIndex = 26, grammaticalFocus = "es.vocab.travel", ruleText = "The clip says Gire a la derecha en la esquina. Gire a la derecha en la esquina. is the answer. Gire a la derecha en las esquinas is a form of the word that cannot fill the slot. Gire a la derecha en la esquino is the wrong person or number for the slot. Gire a la izquierda en la esquina is a form of the word that cannot fill the slot.") {
                option(id = 970400, text = "Gire a la derecha en la esquina.", romaji = "jˈire a la derˈecha en la eskˈina", correct = true, audioSrc = "asset:///audio/es/gire_a_la_derecha_en_la_esquina.ogg")
                option(id = 970401, text = "Gire a la derecha en las esquinas", correct = false, errorTag = "WRONG_FORM")
                option(id = 970402, text = "Gire a la derecha en la esquino", correct = false, errorTag = "WRONG_PERSON")
                option(id = 970403, text = "Gire a la izquierda en la esquina", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 93540, romaji = "el tren perdhˈidho ˈayer yeghˈe a la estasˈion bˈeinte minˈutos tˈardhe el tren a madhrˈid yˈa abhˈia salˈidho del andˈen la señˈora de la bentanˈiya me dˈijo kˈe abhˈia ˈotro tren en una ˈora al finˈal yeghˈe a kˈasa a las ˈonse de la nˈoche", lessonId = 903, type = ChallengeType.STORY, question = "El tren perdido\nAyer llegué a la estación veinte minutos tarde.\nEl tren a Madrid ya había salido del andén.\nLa señora de la ventanilla me dijo que había otro tren en una hora.\nAl final llegué a casa a las once de la noche.\n\n❓ Why did the speaker miss the train?", audioSrc = "asset:///audio/es/story_903.ogg", orderIndex = 27) {
                option(id = 970800, text = "It had already left the platform", correct = true)
                option(id = 970801, text = "It was cancelled because of the fog", correct = false)
                option(id = 970802, text = "They arrived at the wrong station", correct = false)
                option(id = 970803, text = "They had bought the wrong ticket", correct = false)
            }
            challenge(id = 94000, lessonId = 904, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (4 pairs)", orderIndex = 0) {
                option(id = 980000, text = "la ciudad", correct = true)
                option(id = 980001, text = "the city", correct = true)
                option(id = 980002, text = "la calle", correct = true)
                option(id = 980003, text = "the street", correct = true)
                option(id = 980004, text = "la plaza", correct = true)
                option(id = 980005, text = "the square", correct = true)
                option(id = 980006, text = "el parque", correct = true)
                option(id = 980007, text = "the park", correct = true)
            }
            challenge(id = 94020, lessonId = 904, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (4 pairs)", orderIndex = 1) {
                option(id = 980400, text = "el museo", correct = true)
                option(id = 980401, text = "the museum", correct = true)
                option(id = 980402, text = "la biblioteca", correct = true)
                option(id = 980403, text = "the library", correct = true)
                option(id = 980404, text = "el teatro", correct = true)
                option(id = 980405, text = "the theatre", correct = true)
                option(id = 980406, text = "la iglesia", correct = true)
                option(id = 980407, text = "the church", correct = true)
            }
            challenge(id = 94040, lessonId = 904, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (4 pairs)", orderIndex = 2) {
                option(id = 980800, text = "el puente", correct = true)
                option(id = 980801, text = "the bridge", correct = true)
                option(id = 980802, text = "la avenida", correct = true)
                option(id = 980803, text = "the avenue", correct = true)
                option(id = 980804, text = "el edificio", correct = true)
                option(id = 980805, text = "the building", correct = true)
                option(id = 980806, text = "la orilla", correct = true)
                option(id = 980807, text = "the river bank", correct = true)
            }
            challenge(id = 94060, lessonId = 904, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (4 pairs)", orderIndex = 3) {
                option(id = 981200, text = "el banco", correct = true)
                option(id = 981201, text = "the bank", correct = true)
                option(id = 981202, text = "el hospital", correct = true)
                option(id = 981203, text = "the hospital", correct = true)
                option(id = 981204, text = "el ayuntamiento", correct = true)
                option(id = 981205, text = "the town hall", correct = true)
                option(id = 981206, text = "la pizzería", correct = true)
                option(id = 981207, text = "the pizzeria", correct = true)
            }
            challenge(id = 94080, lessonId = 904, type = ChallengeType.SELECT, question = "Which one means 'the public garden'?", orderIndex = 4, grammaticalFocus = "es.vocab.travel", ruleText = " el jardín público is the answer. la jardín público is the wrong person or number for the slot. el jardín pública is a form of the word that cannot fill the slot. el jardín públicos is a form of the word that cannot fill the slot.") {
                option(id = 981600, text = "el jardín público", correct = true)
                option(id = 981601, text = "la jardín público", correct = false, errorTag = "WRONG_PERSON")
                option(id = 981602, text = "el jardín pública", correct = false, errorTag = "WRONG_FORM")
                option(id = 981603, text = "el jardín públicos", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 94100, lessonId = 904, type = ChallengeType.SELECT, question = "Which one means 'the old town'?", orderIndex = 5, grammaticalFocus = "es.vocab.travel", ruleText = " el casco antiguo is the answer. la casco antiguo is the wrong person or number for the slot. el casco antiguos is a form of the word that cannot fill the slot. el casco antigua is a form of the word that cannot fill the slot.") {
                option(id = 982000, text = "el casco antiguo", correct = true)
                option(id = 982001, text = "la casco antiguo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 982002, text = "el casco antiguos", correct = false, errorTag = "WRONG_FORM")
                option(id = 982003, text = "el casco antigua", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 94120, lessonId = 904, type = ChallengeType.SELECT, question = "Which one means 'it is cloudy'?", orderIndex = 6, grammaticalFocus = "es.vocab.travel", ruleText = " está nublado is the answer. están nublados is the wrong person or number for the slot. está nublada is a form of the word that cannot fill the slot. está nublados is a form of the word that cannot fill the slot.") {
                option(id = 982400, text = "está nublado", correct = true)
                option(id = 982401, text = "están nublados", correct = false, errorTag = "WRONG_PERSON")
                option(id = 982402, text = "está nublada", correct = false, errorTag = "WRONG_FORM")
                option(id = 982403, text = "está nublados", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 94140, lessonId = 904, type = ChallengeType.SELECT, question = "Which one means 'it is sunny'?", orderIndex = 7, grammaticalFocus = "es.vocab.travel", ruleText = " hace sol is the answer. hacen sol is the wrong person or number for the slot. hace soles is a form of the word that cannot fill the slot. hace soleado is a form of the word that cannot fill the slot.") {
                option(id = 982800, text = "hace sol", correct = true)
                option(id = 982801, text = "hacen sol", correct = false, errorTag = "WRONG_PERSON")
                option(id = 982802, text = "hace soles", correct = false, errorTag = "WRONG_FORM")
                option(id = 982803, text = "hace soleado", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 94160, lessonId = 904, type = ChallengeType.SELECT, question = "Which one means 'it is snowing'?", orderIndex = 8, grammaticalFocus = "es.vocab.travel", ruleText = " está nevando is the answer. están nevando is the wrong person or number for the slot. está nevadas is a form of the word that cannot fill the slot. están nevadas is a form of the word that cannot fill the slot.") {
                option(id = 983200, text = "está nevando", correct = true)
                option(id = 983201, text = "están nevando", correct = false, errorTag = "WRONG_PERSON")
                option(id = 983202, text = "está nevadas", correct = false, errorTag = "WRONG_FORM")
                option(id = 983203, text = "están nevadas", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 94180, lessonId = 904, type = ChallengeType.SELECT, question = "Which one means 'the weather forecast'?", orderIndex = 9, grammaticalFocus = "es.vocab.travel", ruleText = " el pronóstico del tiempo is the answer. el pronóstico del tiempos is a form of the word that cannot fill the slot. la pronóstico del tiempo is the wrong person or number for the slot. el pronósitico del tiempo is a form of the word that cannot fill the slot.") {
                option(id = 983600, text = "el pronóstico del tiempo", correct = true)
                option(id = 983601, text = "el pronóstico del tiempos", correct = false, errorTag = "WRONG_FORM")
                option(id = 983602, text = "la pronóstico del tiempo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 983603, text = "el pronósitico del tiempo", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 94200, lessonId = 904, type = ChallengeType.SELECT, question = "Which one means 'a crowded street'?", orderIndex = 10, grammaticalFocus = "es.vocab.travel", ruleText = " una calle llena de gente is the answer. una calle lleno de gente is a form of the word that cannot fill the slot. una calles llena de gente is the wrong person or number for the slot. una calle llen de gente is a form of the word that cannot fill the slot.") {
                option(id = 984000, text = "una calle llena de gente", correct = true)
                option(id = 984001, text = "una calle lleno de gente", correct = false, errorTag = "WRONG_FORM")
                option(id = 984002, text = "una calles llena de gente", correct = false, errorTag = "WRONG_PERSON")
                option(id = 984003, text = "una calle llen de gente", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 94220, lessonId = 904, type = ChallengeType.SELECT, question = "Which one means 'a wide avenue'?", orderIndex = 11, grammaticalFocus = "es.vocab.travel", ruleText = " una avenida ancha is the answer. una avenida anchas is a form of the word that cannot fill the slot. una avenida ancho is a form of the word that cannot fill the slot. un avenida ancha is the wrong person or number for the slot.") {
                option(id = 984400, text = "una avenida ancha", correct = true)
                option(id = 984401, text = "una avenida anchas", correct = false, errorTag = "WRONG_FORM")
                option(id = 984402, text = "una avenida ancho", correct = false, errorTag = "WRONG_FORM")
                option(id = 984403, text = "un avenida ancha", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 94240, lessonId = 904, type = ChallengeType.SELECT, question = "Which one means 'the launderette'?", orderIndex = 12, grammaticalFocus = "es.vocab.travel", ruleText = " la lavandería is the answer. el lavandería is the wrong person or number for the slot. la lavanderías is the wrong person or number for the slot. la lavanderes is a form of the word that cannot fill the slot.") {
                option(id = 984800, text = "la lavandería", correct = true)
                option(id = 984801, text = "el lavandería", correct = false, errorTag = "WRONG_PERSON")
                option(id = 984802, text = "la lavanderías", correct = false, errorTag = "WRONG_PERSON")
                option(id = 984803, text = "la lavanderes", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 94260, lessonId = 904, type = ChallengeType.SELECT, question = "Which one means 'a ticket machine'?", orderIndex = 13, grammaticalFocus = "es.vocab.travel", ruleText = " la máquina de billetes is the answer. la máquinas de billetes is the wrong person or number for the slot. la máquina de billete is a form of the word that cannot fill the slot. el máquina de billetes is the wrong person or number for the slot.") {
                option(id = 985200, text = "la máquina de billetes", correct = true)
                option(id = 985201, text = "la máquinas de billetes", correct = false, errorTag = "WRONG_PERSON")
                option(id = 985202, text = "la máquina de billete", correct = false, errorTag = "WRONG_FORM")
                option(id = 985203, text = "el máquina de billetes", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 94280, lessonId = 904, type = ChallengeType.SELECT, question = "Which one means 'to get a cold'?", orderIndex = 14, grammaticalFocus = "es.vocab.travel", ruleText = " constiparse is the answer. constipamos is the wrong person or number for the slot. constiparse bien is a form of the word that cannot fill the slot. constipada is a form of the word that cannot fill the slot.") {
                option(id = 985600, text = "constiparse", correct = true)
                option(id = 985601, text = "constipamos", correct = false, errorTag = "WRONG_PERSON")
                option(id = 985602, text = "constiparse bien", correct = false, errorTag = "WRONG_FORM")
                option(id = 985603, text = "constipada", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 94300, lessonId = 904, type = ChallengeType.SELECT, question = "Which one means 'it is windy'?", orderIndex = 15, grammaticalFocus = "es.vocab.travel", ruleText = " hace viento is the answer. hacen viento is the wrong person or number for the slot. hace vientos is a form of the word that cannot fill the slot. hace ventoso is a form of the word that cannot fill the slot.") {
                option(id = 986000, text = "hace viento", correct = true)
                option(id = 986001, text = "hacen viento", correct = false, errorTag = "WRONG_PERSON")
                option(id = 986002, text = "hace vientos", correct = false, errorTag = "WRONG_FORM")
                option(id = 986003, text = "hace ventoso", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 94320, lessonId = 904, type = ChallengeType.CONJUGATE, question = "Which present form of llover goes with 'yo'?", orderIndex = 16, grammaticalFocus = "es.vocab.travel", ruleText = "llover changes o- to ue- in the present, and the yo form is lluevo. lluevo is the answer. lloves is the wrong person or number for the slot. llueve is the wrong person or number for the slot. llovemos is the wrong person or number for the slot.") {
                option(id = 986400, text = "lluevo", correct = true)
                option(id = 986401, text = "lloves", correct = false, errorTag = "WRONG_PERSON")
                option(id = 986402, text = "llueve", correct = false, errorTag = "WRONG_PERSON")
                option(id = 986403, text = "llovemos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 94340, lessonId = 904, type = ChallengeType.CONJUGATE, question = "Which present form of hacer goes with 'tú'?", orderIndex = 17, grammaticalFocus = "es.vocab.travel", ruleText = "hacer has an irregular yo form, hago, and the tú ending is -es: haces. haces is the answer. hago is the wrong person or number for the slot. hace is the wrong person or number for the slot. hacemos is the wrong person or number for the slot.") {
                option(id = 986800, text = "haces", correct = true)
                option(id = 986801, text = "hago", correct = false, errorTag = "WRONG_PERSON")
                option(id = 986802, text = "hace", correct = false, errorTag = "WRONG_PERSON")
                option(id = 986803, text = "hacemos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 94360, lessonId = 904, type = ChallengeType.CONJUGATE, question = "Which present form of soler goes with 'ellos'?", orderIndex = 18, grammaticalFocus = "es.vocab.travel", ruleText = "soler changes o- to ue- in the present, and ellos takes -n: suelen. suelen is the answer. suelo is the wrong person or number for the slot. sueles is the wrong person or number for the slot. suélense is a form of the word that cannot fill the slot.") {
                option(id = 987200, text = "suelen", correct = true)
                option(id = 987201, text = "suelo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 987202, text = "sueles", correct = false, errorTag = "WRONG_PERSON")
                option(id = 987203, text = "suélense", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 94380, lessonId = 904, type = ChallengeType.CONJUGATE, question = "Which present form of nublarse goes with 'nosotros'?", orderIndex = 19, grammaticalFocus = "es.vocab.travel", ruleText = "nublarse is a regular -ar verb used impersonally for the weather, and nosotros keeps the plain stem: nublamos. nublamos is the answer. nublémonos is a form of the word that cannot fill the slot. nublo is the wrong person or number for the slot. nublas is the wrong person or number for the slot.") {
                option(id = 987600, text = "nublamos", correct = true)
                option(id = 987601, text = "nublémonos", correct = false, errorTag = "WRONG_FORM")
                option(id = 987602, text = "nublo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 987603, text = "nublas", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 94400, lessonId = 904, type = ChallengeType.FILL_BLANK, question = "En el norte, en invierno, ___ mucho frío.", orderIndex = 20, grammaticalFocus = "es.vocab.travel", acceptedAnswers = "hace", ruleText = "In Spain and Latin America the weather takes the third person singular of hacer, and a habit described for every winter is present: hace. hace is the answer. hacen is the wrong person or number for the slot. hacerse is a form of the word that cannot fill the slot. hacéis is the wrong person or number for the slot.") {
                option(id = 988000, text = "hace", correct = true)
                option(id = 988001, text = "hacen", correct = false, errorTag = "WRONG_PERSON")
                option(id = 988002, text = "hacerse", correct = false, errorTag = "WRONG_FORM")
                option(id = 988003, text = "hacéis", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 94420, lessonId = 904, type = ChallengeType.FILL_BLANK, question = "Ayer ___ todo el día y no salimos.", orderIndex = 21, grammaticalFocus = "es.vocab.travel", acceptedAnswers = "llovió|llovía", ruleText = "Ayer pins the rain in the past and the sentence has one completed event, so the preterite is the form: llovió. The imperfect llovía would describe the whole day as background rather than one event. llovió is the answer. llueve puts the verb in a tense the sentence does not ask for. llueven puts the verb in a tense the sentence does not ask for. llover is a form of the word that cannot fill the slot.") {
                option(id = 988400, text = "llovió", correct = true)
                option(id = 988401, text = "llueve", correct = false, errorTag = "WRONG_TENSE")
                option(id = 988402, text = "llueven", correct = false, errorTag = "WRONG_TENSE")
                option(id = 988403, text = "llover", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 94440, lessonId = 904, type = ChallengeType.FILL_BLANK, question = "El museo ___ los lunes por la tarde.", orderIndex = 22, grammaticalFocus = "es.vocab.travel", acceptedAnswers = "cierra|cerraba", ruleText = "El museo is singular, so cerrar gives cierra in the third person. A timetable that still holds is present, and cerraba would describe a museum that no longer keeps those hours. cierra is the answer. cierran is the wrong person or number for the slot. cerrar is a form of the word that cannot fill the slot. cerramos is the wrong person or number for the slot.") {
                option(id = 988800, text = "cierra", correct = true)
                option(id = 988801, text = "cierran", correct = false, errorTag = "WRONG_PERSON")
                option(id = 988802, text = "cerrar", correct = false, errorTag = "WRONG_FORM")
                option(id = 988803, text = "cerramos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 94460, lessonId = 904, type = ChallengeType.FILL_BLANK, question = "La plaza se ___ de mucha gente el fin de semana.", orderIndex = 23, grammaticalFocus = "es.vocab.travel", acceptedAnswers = "llena|llenaba", ruleText = "La plaza is singular, and the reflexive verb agrees with it: se llena. La gente is a collective noun and does not make the verb plural. llena is the answer. llenan is the wrong person or number for the slot. llenar is a form of the word that cannot fill the slot. llenaron puts the verb in a tense the sentence does not ask for.") {
                option(id = 989200, text = "llena", correct = true)
                option(id = 989201, text = "llenan", correct = false, errorTag = "WRONG_PERSON")
                option(id = 989202, text = "llenar", correct = false, errorTag = "WRONG_FORM")
                option(id = 989203, text = "llenaron", correct = false, errorTag = "WRONG_TENSE")
            }
            challenge(id = 94480, lessonId = 904, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/hoy_llueve_en_toda_la_ciudad.ogg", orderIndex = 24, grammaticalFocus = "es.vocab.travel", ruleText = "The clip says Hoy llueve en toda la ciudad. Hoy llueve en toda la ciudad. is the answer. Hoy llueven en toda la ciudad is the wrong person or number for the slot. Hoy llueve en toda la ciudades is a form of the word that cannot fill the slot. Hoy nieva en toda la ciudad is a form of the word that cannot fill the slot.") {
                option(id = 989600, text = "Hoy llueve en toda la ciudad.", romaji = "oi yˈuebe en tˈodha la siudhˈad", correct = true, audioSrc = "asset:///audio/es/hoy_llueve_en_toda_la_ciudad.ogg")
                option(id = 989601, text = "Hoy llueven en toda la ciudad", correct = false, errorTag = "WRONG_PERSON")
                option(id = 989602, text = "Hoy llueve en toda la ciudades", correct = false, errorTag = "WRONG_FORM")
                option(id = 989603, text = "Hoy nieva en toda la ciudad", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 94500, lessonId = 904, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/el_puente_esta_cerrado_por_lluvia.ogg", orderIndex = 25, grammaticalFocus = "es.vocab.travel", ruleText = "The clip says El puente está cerrado por la lluvia. El puente está cerrado por la lluvia. is the answer. El puentes está cerrado por la lluvia is the wrong person or number for the slot. El puente están cerrados por la lluvia is the wrong person or number for the slot. El puente está cerrado por las lluvia is the wrong person or number for the slot.") {
                option(id = 990000, text = "El puente está cerrado por la lluvia.", romaji = "el pˈuente estˈa serrˈadho por la yˈubia", correct = true, audioSrc = "asset:///audio/es/el_puente_esta_cerrado_por_lluvia.ogg")
                option(id = 990001, text = "El puentes está cerrado por la lluvia", correct = false, errorTag = "WRONG_PERSON")
                option(id = 990002, text = "El puente están cerrados por la lluvia", correct = false, errorTag = "WRONG_PERSON")
                option(id = 990003, text = "El puente está cerrado por las lluvia", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 94520, romaji = "un domˈingo en la siudhˈad el domˈingo por la mañˈana fˈui al sˈentro kon mi amˈigha asˈia buen tˈiempo y la plˈasa estˈabha yˈena de jˈente komprˈemos kafˈe y bˈoyos en una panadherˈia de la eskˈina por la tˈardhe bisitˈamos el musˈeo kˈe es grˈatis el primˈer domˈingo del mes", lessonId = 904, type = ChallengeType.STORY, question = "Un domingo en la ciudad\nEl domingo por la mañana fui al centro con mi amiga.\nHacía buen tiempo y la plaza estaba llena de gente.\nCompremos café y bollos en una panadería de la esquina.\nPor la tarde visitamos el museo, que es gratis el primer domingo del mes.\n\n❓ How much did the visit to the museum cost?", audioSrc = "asset:///audio/es/story_904.ogg", orderIndex = 26) {
                option(id = 990400, text = "Nothing, it was free", correct = true)
                option(id = 990401, text = "Five euros each", correct = false)
                option(id = 990402, text = "The price of a coffee", correct = false)
                option(id = 990403, text = "The speaker does not say", correct = false)
            }
            challenge(id = 95000, lessonId = 905, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (4 pairs)", orderIndex = 0) {
                option(id = 1000000, text = "el trabajo", correct = true)
                option(id = 1000001, text = "the work", correct = true)
                option(id = 1000002, text = "el jefe", correct = true)
                option(id = 1000003, text = "the boss", correct = true)
                option(id = 1000004, text = "la empresa", correct = true)
                option(id = 1000005, text = "the company", correct = true)
                option(id = 1000006, text = "el sueldo", correct = true)
                option(id = 1000007, text = "the salary", correct = true)
            }
            challenge(id = 95020, lessonId = 905, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (4 pairs)", orderIndex = 1) {
                option(id = 1000400, text = "el horario", correct = true)
                option(id = 1000401, text = "the timetable", correct = true)
                option(id = 1000402, text = "el permiso", correct = true)
                option(id = 1000403, text = "the leave", correct = true)
                option(id = 1000404, text = "la entrevista", correct = true)
                option(id = 1000405, text = "the interview", correct = true)
                option(id = 1000406, text = "el teclado", correct = true)
                option(id = 1000407, text = "the keyboard", correct = true)
            }
            challenge(id = 95040, lessonId = 905, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (4 pairs)", orderIndex = 2) {
                option(id = 1000800, text = "el abogado", correct = true)
                option(id = 1000801, text = "the lawyer", correct = true)
                option(id = 1000802, text = "el maestro", correct = true)
                option(id = 1000803, text = "the teacher", correct = true)
                option(id = 1000804, text = "el ingeniero", correct = true)
                option(id = 1000805, text = "the engineer", correct = true)
                option(id = 1000806, text = "el cocinero", correct = true)
                option(id = 1000807, text = "the cook", correct = true)
            }
            challenge(id = 95060, lessonId = 905, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (4 pairs)", orderIndex = 3) {
                option(id = 1001200, text = "el panadero", correct = true)
                option(id = 1001201, text = "the baker", correct = true)
                option(id = 1001202, text = "el cartero", correct = true)
                option(id = 1001203, text = "the postman", correct = true)
                option(id = 1001204, text = "el becario", correct = true)
                option(id = 1001205, text = "the intern", correct = true)
                option(id = 1001206, text = "el cliente", correct = true)
                option(id = 1001207, text = "the customer", correct = true)
            }
            challenge(id = 95080, lessonId = 905, type = ChallengeType.MATCH_PAIRS, question = "Match each word to its meaning (4 pairs)", orderIndex = 4) {
                option(id = 1001600, text = "el ordenador", correct = true)
                option(id = 1001601, text = "the computer", correct = true)
                option(id = 1001602, text = "el archivo", correct = true)
                option(id = 1001603, text = "the filing cabinet", correct = true)
                option(id = 1001604, text = "la carpeta", correct = true)
                option(id = 1001605, text = "the folder", correct = true)
                option(id = 1001606, text = "el informe", correct = true)
                option(id = 1001607, text = "the report", correct = true)
            }
            challenge(id = 95100, lessonId = 905, type = ChallengeType.SELECT, question = "Which one means 'the staff'?", orderIndex = 5, grammaticalFocus = "es.vocab.travel", ruleText = " el personal is the answer. la personal is the wrong person or number for the slot. los personal is the wrong person or number for the slot. el personals is the wrong person or number for the slot.") {
                option(id = 1002000, text = "el personal", correct = true)
                option(id = 1002001, text = "la personal", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1002002, text = "los personal", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1002003, text = "el personals", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 95120, lessonId = 905, type = ChallengeType.SELECT, question = "Which one means 'the deadline'?", orderIndex = 6, grammaticalFocus = "es.vocab.travel", ruleText = " la fecha límite is the answer. el fecha límite is the wrong person or number for the slot. la fecha límites is a form of the word that cannot fill the slot. la fecha limite is a form of the word that cannot fill the slot.") {
                option(id = 1002400, text = "la fecha límite", correct = true)
                option(id = 1002401, text = "el fecha límite", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1002402, text = "la fecha límites", correct = false, errorTag = "WRONG_FORM")
                option(id = 1002403, text = "la fecha limite", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 95140, lessonId = 905, type = ChallengeType.SELECT, question = "Which one means 'to hand in a report'?", orderIndex = 7, grammaticalFocus = "es.vocab.travel", ruleText = " entregar un informe is the answer. entregar un informes is a form of the word that cannot fill the slot. entrega un informe is the wrong person or number for the slot. entregar la informe is a form of the word that cannot fill the slot.") {
                option(id = 1002800, text = "entregar un informe", correct = true)
                option(id = 1002801, text = "entregar un informes", correct = false, errorTag = "WRONG_FORM")
                option(id = 1002802, text = "entrega un informe", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1002803, text = "entregar la informe", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 95160, lessonId = 905, type = ChallengeType.SELECT, question = "Which one means 'to take a day off'?", orderIndex = 8, grammaticalFocus = "es.vocab.travel", ruleText = " pedir un día libre is the answer. pedir un día libres is a form of the word that cannot fill the slot. pide un día libre is the wrong person or number for the slot. pedir un día de libre is a form of the word that cannot fill the slot.") {
                option(id = 1003200, text = "pedir un día libre", correct = true)
                option(id = 1003201, text = "pedir un día libres", correct = false, errorTag = "WRONG_FORM")
                option(id = 1003202, text = "pide un día libre", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1003203, text = "pedir un día de libre", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 95180, lessonId = 905, type = ChallengeType.SELECT, question = "Which one means 'a fixed-term contract'?", orderIndex = 9, grammaticalFocus = "es.vocab.travel", ruleText = " un contrato temporal is the answer. un contrato temporales is a form of the word that cannot fill the slot. una contrato temporal is the wrong person or number for the slot. un contrato de temporal is a form of the word that cannot fill the slot.") {
                option(id = 1003600, text = "un contrato temporal", correct = true)
                option(id = 1003601, text = "un contrato temporales", correct = false, errorTag = "WRONG_FORM")
                option(id = 1003602, text = "una contrato temporal", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1003603, text = "un contrato de temporal", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 95200, lessonId = 905, type = ChallengeType.SELECT, question = "Which one means 'to get promoted'?", orderIndex = 10, grammaticalFocus = "es.vocab.travel", ruleText = " ascender de puesto is the answer. ascender del puesto is a form of the word that cannot fill the slot. asciende de puesto is the wrong person or number for the slot. ascender de puestos is a form of the word that cannot fill the slot.") {
                option(id = 1004000, text = "ascender de puesto", correct = true)
                option(id = 1004001, text = "ascender del puesto", correct = false, errorTag = "WRONG_FORM")
                option(id = 1004002, text = "asciende de puesto", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1004003, text = "ascender de puestos", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 95220, lessonId = 905, type = ChallengeType.SELECT, question = "Which one means 'to work overtime'?", orderIndex = 11, grammaticalFocus = "es.vocab.travel", ruleText = " hacer horas extra is the answer. hacer horas extras is a form of the word that cannot fill the slot. hace horas extra is the wrong person or number for the slot. hacer la hora extra is a form of the word that cannot fill the slot.") {
                option(id = 1004400, text = "hacer horas extra", correct = true)
                option(id = 1004401, text = "hacer horas extras", correct = false, errorTag = "WRONG_FORM")
                option(id = 1004402, text = "hace horas extra", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1004403, text = "hacer la hora extra", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 95240, lessonId = 905, type = ChallengeType.SELECT, question = "Which one means 'a full-time job'?", orderIndex = 12, grammaticalFocus = "es.vocab.travel", ruleText = " un trabajo a tiempo completo is the answer. un trabajo a tiempo completos is a form of the word that cannot fill the slot. una trabajo a tiempo completo is the wrong person or number for the slot. un trabajo a tiempo incompletos is a form of the word that cannot fill the slot.") {
                option(id = 1004800, text = "un trabajo a tiempo completo", correct = true)
                option(id = 1004801, text = "un trabajo a tiempo completos", correct = false, errorTag = "WRONG_FORM")
                option(id = 1004802, text = "una trabajo a tiempo completo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1004803, text = "un trabajo a tiempo incompletos", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 95260, lessonId = 905, type = ChallengeType.SELECT, question = "Which one means 'to apply for a job'?", orderIndex = 13, grammaticalFocus = "es.vocab.travel", ruleText = " solicitar un empleo is the answer. solicitar un empleos is a form of the word that cannot fill the slot. solicita un empleo is the wrong person or number for the slot. solicitar una empleo is the wrong person or number for the slot.") {
                option(id = 1005200, text = "solicitar un empleo", correct = true)
                option(id = 1005201, text = "solicitar un empleos", correct = false, errorTag = "WRONG_FORM")
                option(id = 1005202, text = "solicita un empleo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1005203, text = "solicitar una empleo", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 95280, lessonId = 905, type = ChallengeType.SELECT, question = "Which one means 'the work experience'?", orderIndex = 14, grammaticalFocus = "es.vocab.travel", ruleText = " la experiencia laboral is the answer. el experiencia laboral is the wrong person or number for the slot. la experiencia laborables is a form of the word that cannot fill the slot. la experiencias laboral is the wrong person or number for the slot.") {
                option(id = 1005600, text = "la experiencia laboral", correct = true)
                option(id = 1005601, text = "el experiencia laboral", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1005602, text = "la experiencia laborables", correct = false, errorTag = "WRONG_FORM")
                option(id = 1005603, text = "la experiencias laboral", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 95300, lessonId = 905, type = ChallengeType.SELECT, question = "Which one means 'to hand in one's notice'?", orderIndex = 15, grammaticalFocus = "es.vocab.travel", ruleText = " presentar la dimisión is the answer. presentar la dimisiones is a form of the word that cannot fill the slot. presentar el dimisión is the wrong person or number for the slot. presentar la dimisiónes is a form of the word that cannot fill the slot.") {
                option(id = 1006000, text = "presentar la dimisión", correct = true)
                option(id = 1006001, text = "presentar la dimisiones", correct = false, errorTag = "WRONG_FORM")
                option(id = 1006002, text = "presentar el dimisión", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1006003, text = "presentar la dimisiónes", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 95320, lessonId = 905, type = ChallengeType.SELECT, question = "Which one means 'a colleague'?", orderIndex = 16, grammaticalFocus = "es.vocab.travel", ruleText = " un compañero de trabajo is the answer. un compañero de trabajos is a form of the word that cannot fill the slot. una compañero de trabajo is the wrong person or number for the slot. un compañero del trabajo is a form of the word that cannot fill the slot.") {
                option(id = 1006400, text = "un compañero de trabajo", correct = true)
                option(id = 1006401, text = "un compañero de trabajos", correct = false, errorTag = "WRONG_FORM")
                option(id = 1006402, text = "una compañero de trabajo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1006403, text = "un compañero del trabajo", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 95340, lessonId = 905, type = ChallengeType.CONJUGATE, question = "Which present form of entregar goes with 'yo'?", orderIndex = 17, grammaticalFocus = "es.vocab.travel", ruleText = "entregar is a regular -ar verb, and the yo ending is -o: entrego. entrego is the answer. entregas is the wrong person or number for the slot. entrega is the wrong person or number for the slot. entregamos is the wrong person or number for the slot.") {
                option(id = 1006800, text = "entrego", correct = true)
                option(id = 1006801, text = "entregas", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1006802, text = "entrega", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1006803, text = "entregamos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 95360, lessonId = 905, type = ChallengeType.CONJUGATE, question = "Which present form of soler goes with 'yo'?", orderIndex = 18, grammaticalFocus = "es.vocab.travel", ruleText = "soler changes o- to ue- in the present, so the yo form is suelo. suelo is the answer. sueles is the wrong person or number for the slot. suele is the wrong person or number for the slot. solemos is the wrong person or number for the slot.") {
                option(id = 1007200, text = "suelo", correct = true)
                option(id = 1007201, text = "sueles", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1007202, text = "suele", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1007203, text = "solemos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 95380, lessonId = 905, type = ChallengeType.CONJUGATE, question = "Which present form of seguir goes with 'tú'?", orderIndex = 19, grammaticalFocus = "es.vocab.travel", ruleText = "seguir changes e- to ie- in the present, and the tú ending is -es: sigues. sigues is the answer. sigo is the wrong person or number for the slot. sigue is the wrong person or number for the slot. seguimos is the wrong person or number for the slot.") {
                option(id = 1007600, text = "sigues", correct = true)
                option(id = 1007601, text = "sigo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1007602, text = "sigue", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1007603, text = "seguimos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 95400, lessonId = 905, type = ChallengeType.SELECT, question = "Which present form of reunirse goes with 'ellos'?", orderIndex = 20, grammaticalFocus = "es.vocab.travel", ruleText = "reunirse is reflexive, and reunir is irregular in the stressed syllable in the present, so ellos is se reúnen. se reúnen is the answer. se reúne is the wrong person or number for the slot. se reúno is the wrong person or number for the slot. nos reunimos is the wrong person or number for the slot.") {
                option(id = 1008000, text = "se reúnen", correct = true)
                option(id = 1008001, text = "se reúne", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1008002, text = "se reúno", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1008003, text = "nos reunimos", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 95420, lessonId = 905, type = ChallengeType.FILL_BLANK, question = "Mi jefe ___ en una oficina de Madrid.", orderIndex = 21, grammaticalFocus = "es.vocab.travel", acceptedAnswers = "trabaja", ruleText = "Mi jefe is third person singular, so the present of trabajar is the answer: trabaja, and neither yo nor a plural can be right for the subject given. trabaja is the answer. trabajo is the wrong person or number for the slot. trabajan is the wrong person or number for the slot. trabajar is a form of the word that cannot fill the slot.") {
                option(id = 1008400, text = "trabaja", correct = true)
                option(id = 1008401, text = "trabajo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1008402, text = "trabajan", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1008403, text = "trabajar", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 95440, lessonId = 905, type = ChallengeType.FILL_BLANK, question = "¿Cuánto tiempo ___ en esta empresa?", orderIndex = 22, grammaticalFocus = "es.vocab.travel", acceptedAnswers = "llevas|estás", ruleText = "The question is put to one person, so the second person singular is the answer. You can say how long you have been there with llevar or with estar, and both are correct here. llevas is the answer. lleva is the wrong person or number for the slot. llevamos is the wrong person or number for the slot. llevar is a form of the word that cannot fill the slot.") {
                option(id = 1008800, text = "llevas", correct = true)
                option(id = 1008801, text = "lleva", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1008802, text = "llevamos", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1008803, text = "llevar", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 95460, lessonId = 905, type = ChallengeType.FILL_BLANK, question = "Tengo que ___ un informe antes de las cinco.", orderIndex = 23, grammaticalFocus = "es.vocab.travel", acceptedAnswers = "enviar|mandar", ruleText = "After tengo que the verb stays in the infinitive, because que already marks the whole obligation: tengo que enviar. enviar and mandar are both ways to say to send. enviar is the answer. envías is the wrong person or number for the slot. enviaré is a form of the word that cannot fill the slot. enviarlo is a form of the word that cannot fill the slot.") {
                option(id = 1009200, text = "enviar", correct = true)
                option(id = 1009201, text = "envías", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1009202, text = "enviaré", correct = false, errorTag = "WRONG_FORM")
                option(id = 1009203, text = "enviarlo", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 95480, lessonId = 905, type = ChallengeType.FILL_BLANK, question = "Los clientes ___ su cita en recepción.", orderIndex = 24, grammaticalFocus = "es.vocab.travel", acceptedAnswers = "tienen|tenían", ruleText = "Los clientes is plural, so the third person plural of tener is the answer: tienen. Tenían would put the booking in the past, and neither singular nor nosotros agrees with a group of customers. tienen is the answer. tiene is the wrong person or number for the slot. tenemos is the wrong person or number for the slot. tener is a form of the word that cannot fill the slot.") {
                option(id = 1009600, text = "tienen", correct = true)
                option(id = 1009601, text = "tiene", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1009602, text = "tenemos", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1009603, text = "tener", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 95500, lessonId = 905, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/mi_jefe_trabaja_desde_casa.ogg", orderIndex = 25, grammaticalFocus = "es.vocab.travel", ruleText = "The clip says Mi jefe trabaja desde casa desde marzo. Mi jefe trabaja desde casa desde marzo. is the answer. Mi jefes trabaja desde casa desde marzo is the wrong person or number for the slot. Mi jefe trabajan desde casa desde marzo is the wrong person or number for the slot. Mi jefe trabaja desde casa desde mayo is a form of the word that cannot fill the slot.") {
                option(id = 1010000, text = "Mi jefe trabaja desde casa desde marzo.", romaji = "mi jˈefe trabhˈaja dˈesde kˈasa dˈesde mˈarso", correct = true, audioSrc = "asset:///audio/es/mi_jefe_trabaja_desde_casa.ogg")
                option(id = 1010001, text = "Mi jefes trabaja desde casa desde marzo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1010002, text = "Mi jefe trabajan desde casa desde marzo", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1010003, text = "Mi jefe trabaja desde casa desde mayo", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 95520, lessonId = 905, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/la_reunion_se_ha_aplazado.ogg", orderIndex = 26, grammaticalFocus = "es.vocab.travel", ruleText = "The clip says La reunión se ha aplazado hasta el viernes. La reunión se ha aplazado hasta el viernes. is the answer. La reunión se han aplazado is the wrong person or number for the slot. La reunión se ha aplazada is a form of the word that cannot fill the slot. La reunión se han aplazada is a form of the word that cannot fill the slot.") {
                option(id = 1010400, text = "La reunión se ha aplazado hasta el viernes.", romaji = "la rreunˈion se a aplazˈadho ˈasta el bˈiernes", correct = true, audioSrc = "asset:///audio/es/la_reunion_se_ha_aplazado.ogg")
                option(id = 1010401, text = "La reunión se han aplazado", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1010402, text = "La reunión se ha aplazada", correct = false, errorTag = "WRONG_FORM")
                option(id = 1010403, text = "La reunión se han aplazada", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 95540, romaji = "el primˈer dˈia el lˈunes fue mi primˈer dˈia en la emprˈesa mi jˈefe me enseñˈo la ofisˈina y me presentˈo al rrˈesto del ekˈipo a las dˈose tubhˈimos nˈuestra primˈera rreunˈion de la semˈana terminˈe el dˈia kon trˈes korrˈeos sin rrespondˈer", lessonId = 905, type = ChallengeType.STORY, question = "El primer día\nEl lunes fue mi primer día en la empresa.\nMi jefe me enseñó la oficina y me presentó al resto del equipo.\nA las doce tuvimos nuestra primera reunión de la semana.\nTerminé el día con tres correos sin responder.\n\n❓ What did the speaker do with the rest of the team?", audioSrc = "asset:///audio/es/story_905.ogg", orderIndex = 27) {
                option(id = 1010800, text = "Their boss introduced them", correct = true)
                option(id = 1010801, text = "They answered three emails", correct = false)
                option(id = 1010802, text = "They were trained in the office", correct = false)
                option(id = 1010803, text = "They did not meet anyone", correct = false)
            }
        },
    )
}
