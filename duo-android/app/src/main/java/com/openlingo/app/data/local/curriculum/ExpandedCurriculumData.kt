package com.openlingo.app.data.local.curriculum
import com.openlingo.app.data.local.entities.ChallengeEntity
import com.openlingo.app.data.local.entities.ChallengeOptionEntity
import com.openlingo.app.data.local.entities.LessonEntity
import com.openlingo.app.data.local.entities.UnitEntity
import com.openlingo.app.data.local.models.ChallengeType
data class UnitPayload(
    val unit: UnitEntity,
    val lessons: List<LessonEntity>,
    val challenges: List<ChallengeEntity>,
    val options: List<ChallengeOptionEntity>,
)

object ExpandedCurriculumData {
    val spanishExpandedUnits: List<UnitPayload> = listOf(
        unit(id = 12, courseId = 1, title = "Unit 3: Food & Dining", description = "Order food, beverages, and ask for the bill at restaurants", orderIndex = 2) {
            lesson(id = 105, title = "Lesson 5: Ordering at a Café", orderIndex = 0)
            lesson(id = 106, title = "Lesson 6: Restaurant Phrases", orderIndex = 1)
            challenge(id = 1017, lessonId = 105, type = ChallengeType.SELECT, question = "Which one means 'A coffee, please'?", audioSrc = "asset:///audio/es/un_cafe_por_favor.ogg", orderIndex = 0) {
                option(id = 10057, text = "Un café, por favor", romaji = "un kafˈe por fabhˈor", correct = true, audioSrc = "asset:///audio/es/un_cafe_por_favor.ogg")
                option(id = 10058, text = "Una cerveza fría", correct = false)
                option(id = 10059, text = "Un vaso de leche", correct = false)
            }
            challenge(id = 1018, lessonId = 105, type = ChallengeType.WORD_BANK, question = "Assemble: 'Water and bread'", orderIndex = 1) {
                option(id = 10060, text = "Agua", correct = true)
                option(id = 10061, text = "y", correct = true)
                option(id = 10062, text = "pan", correct = true)
                option(id = 10063, text = "café", correct = false)
                option(id = 10064, text = "por favor", correct = false)
            }
            challenge(id = 1019, lessonId = 105, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/la_cuenta.ogg", orderIndex = 2) {
                option(id = 10065, text = "La cuenta", romaji = "la kwˈenta", correct = true)
                option(id = 10066, text = "El cuenta", correct = false)
                option(id = 10067, text = "Las cuentas", correct = false)
            }
            challenge(id = 1020, lessonId = 106, type = ChallengeType.SELECT, question = "How do you ask 'Where is the restaurant?'", audioSrc = "asset:///audio/es/donde_esta_el_restaurante.ogg", orderIndex = 0) {
                option(id = 10068, text = "¿Dónde está el restaurante?", romaji = "dˈonde estˈa el rrestaurˈante", correct = true, audioSrc = "asset:///audio/es/donde_esta_el_restaurante.ogg")
                option(id = 10069, text = "¿Dónde está la estación?", correct = false)
                option(id = 10070, text = "¿Qué hora es?", correct = false)
            }
            challenge(id = 1021, lessonId = 106, type = ChallengeType.WORD_BANK, question = "Assemble: 'The bill, please'", orderIndex = 1) {
                option(id = 10071, text = "La cuenta", romaji = "la kwˈenta", correct = true, audioSrc = "asset:///audio/es/la_cuenta.ogg")
                option(id = 10072, text = "por favor", correct = true)
                option(id = 10073, text = "gracias", correct = false)
                option(id = 10074, text = "buenos días", correct = false)
            }
            challenge(id = 1022, lessonId = 106, type = ChallengeType.SELECT, question = "Which one means 'Delicious'?", orderIndex = 2) {
                option(id = 10075, text = "Delicioso", correct = true)
                option(id = 10076, text = "Caliente", correct = false)
                option(id = 10077, text = "Frío", correct = false)
            }
            challenge(id = 1080, lessonId = 106, type = ChallengeType.MATCH_PAIRS, question = "Match the café words", orderIndex = 3) {
                option(id = 10300, text = "Café", romaji = "kafˈe", correct = true, audioSrc = "asset:///audio/es/un_cafe_por_favor.ogg")
                option(id = 10301, text = "Coffee", correct = true)
                option(id = 10302, text = "Cuenta", romaji = "kwˈenta", correct = true, audioSrc = "asset:///audio/es/la_cuenta.ogg")
                option(id = 10303, text = "Bill", correct = true)
                option(id = 10304, text = "Agua", correct = true)
                option(id = 10305, text = "Water", correct = true)
            }
            challenge(id = 1081, lessonId = 106, type = ChallengeType.SELECT, question = "Dialogue at the café:\nCamarero: \"¿Qué desea tomar?\"\nCliente: \"Un café, por favor.\"\nWhat did the customer order?", audioSrc = "asset:///audio/es/un_cafe_por_favor.ogg", orderIndex = 4) {
                option(id = 10306, text = "A coffee", correct = true, audioSrc = "asset:///audio/es/un_cafe_por_favor.ogg")
                option(id = 10307, text = "A beer", correct = false)
                option(id = 10308, text = "A glass of water", correct = false)
            }
        },
        unit(id = 13, courseId = 1, title = "Unit 4: Daily Routine & Action Verbs", description = "Learn essential -ar, -er, and -ir verbs: hablar, comer, vivir", orderIndex = 3) {
            lesson(id = 107, title = "Lesson 7: Everyday Actions", orderIndex = 0)
            lesson(id = 108, title = "Lesson 8: Habits & Home", orderIndex = 1)
            challenge(id = 10270, lessonId = 107, type = ChallengeType.INTRO, question = "I speak (-ar verb)", orderIndex = 0) {
                option(id = 300260, text = "Hablo", correct = true)
            }
            challenge(id = 10271, lessonId = 107, type = ChallengeType.INTRO, question = "I eat (-er verb)", orderIndex = 1) {
                option(id = 300261, text = "Como", correct = true)
            }
            challenge(id = 10272, lessonId = 107, type = ChallengeType.INTRO, question = "I live (-ir verb)", orderIndex = 2) {
                option(id = 300262, text = "Vivo", correct = true)
            }
            challenge(id = 10273, lessonId = 107, type = ChallengeType.INTRO, question = "Madrid", orderIndex = 3) {
                option(id = 300263, text = "Madrid", correct = true)
            }
            challenge(id = 1023, lessonId = 107, type = ChallengeType.SELECT, question = "What is 'I speak Spanish'?", audioSrc = "asset:///audio/es/yo_hablo_espanol.ogg", orderIndex = 4, grammaticalFocus = "es.present_person", ruleText = "Regular -ar verbs in the present drop -ar and add -o for 'yo': yo hablo 'I speak'. Neither comemos nor vivo uses the -ar root hablar. español is the word for Spanish.") {
                option(id = 10078, text = "Yo hablo español", romaji = "yˈo ˈabhlo españˈol", correct = true, audioSrc = "asset:///audio/es/yo_hablo_espanol.ogg")
                option(id = 10079, text = "Yo vivo en Madrid", correct = false, errorTag = "WRONG_PERSON")
                option(id = 10080, text = "Yo como manzana", correct = false)
            }
            challenge(id = 10274, lessonId = 107, type = ChallengeType.SELECT, question = "Translate: 'I eat apples'", orderIndex = 5, grammaticalFocus = "es.present_person", ruleText = "Regular -er verbs in the present drop -er and add -o for 'yo': yo como 'I eat'. manzanas means apples.") {
                option(id = 300264, text = "Yo como manzanas", correct = true)
                option(id = 300265, text = "Yo hablo manzanas", correct = false, errorTag = "WRONG_PERSON")
                option(id = 300266, text = "Yo vivo en manzanas", correct = false)
            }
            challenge(id = 1024, lessonId = 107, type = ChallengeType.WORD_BANK, question = "Assemble: 'I live in Madrid'", orderIndex = 6, grammaticalFocus = "es.present_person", ruleText = "Regular -ir verbs in the present drop the -ir and add the person ending: yo vivo, tú vives, él vive, nosotros vivimos, ellos viven.\nAfter 'yo' the ending is always -o: neither vives nor viven can follow it.") {
                option(id = 10081, text = "Yo vivo", correct = true)
                option(id = 10082, text = "en", correct = true)
                option(id = 10083, text = "Madrid", correct = true)
                option(id = 10084, text = "viven", correct = false, errorTag = "WRONG_PERSON")
                option(id = 300000, text = "vive", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 1025, lessonId = 107, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/yo_como_manzanas.ogg", orderIndex = 7) {
                option(id = 10085, text = "Yo como manzanas", romaji = "yˈo komo manzˈanas", correct = true)
                option(id = 10086, text = "Yo como mañana", correct = false)
                option(id = 10087, text = "Yo cómo manzanas", correct = false)
            }
            challenge(id = 10260, lessonId = 108, type = ChallengeType.INTRO, question = "We study", orderIndex = 0) {
                option(id = 300184, text = "Estudiamos", romaji = "estudjˈamos", correct = true)
            }
            challenge(id = 10261, lessonId = 108, type = ChallengeType.INTRO, question = "Spanish", orderIndex = 1) {
                option(id = 300185, text = "El español", romaji = "el espaˈɲol", correct = true)
            }
            challenge(id = 10262, lessonId = 108, type = ChallengeType.INTRO, question = "He eats", orderIndex = 2) {
                option(id = 300186, text = "Él come", romaji = "el ˈkome", correct = true)
            }
            challenge(id = 10263, lessonId = 108, type = ChallengeType.INTRO, question = "Bread", orderIndex = 3) {
                option(id = 300187, text = "El pan", romaji = "el ˈpan", correct = true)
            }
            challenge(id = 1026, lessonId = 108, type = ChallengeType.SELECT, question = "Translate: 'We study Spanish'", orderIndex = 4) {
                option(id = 10088, text = "Nosotros estudiamos español", correct = true)
                option(id = 300188, text = "Nosotros hablamos español", correct = false)
                option(id = 300189, text = "Nosotros comemos pan", correct = false)
            }
            challenge(id = 10264, lessonId = 108, type = ChallengeType.SELECT, question = "Translate: 'He eats bread'", orderIndex = 5) {
                option(id = 300190, text = "Él come pan", correct = true)
                option(id = 300191, text = "Él come español", correct = false)
                option(id = 300192, text = "Él estudia pan", correct = false)
            }
            challenge(id = 1027, lessonId = 108, type = ChallengeType.WORD_BANK, question = "Assemble: 'He eats bread'", orderIndex = 6, grammaticalFocus = "es.present_person", ruleText = "Regular -er verbs in the present: yo como, tú comes, él come, nosotros comemos, ellos comen.\nÉl takes the third person ending, so él come. como is also the word 'as' — in this frame only come fits.") {
                option(id = 10090, text = "Él", correct = true)
                option(id = 10091, text = "come", correct = true)
                option(id = 10092, text = "pan", correct = true)
                option(id = 10093, text = "como", correct = false, errorTag = "WRONG_PERSON")
                option(id = 300001, text = "comen", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 10265, lessonId = 108, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/estudio_espanol_para_viajar_a_espana.ogg", orderIndex = 7, grammaticalFocus = "es.present_person", ruleText = "An -ar verb in the present drops the -ar and adds the person ending: yo estudio, tú estudias, él estudia, nosotros estudiamos, ellos estudian.\nAfter 'yo' the ending is always -o: neither estudiamos nor estudió can follow it.") {
                option(id = 300193, text = "Estudio español", romaji = "estˈudjo espaˈɲol", correct = true, audioSrc = "asset:///audio/es/estudio_espanol_para_viajar_a_espana.ogg")
                option(id = 300194, text = "Estudiamos español", correct = false, errorTag = "WRONG_PERSON")
                option(id = 300195, text = "Estudió español", correct = false, errorTag = "WRONG_TENSE")
            }
        },
        unit(id = 14, courseId = 1, title = "Unit 5: City & Navigation", description = "Ask for directions: izquierda, derecha, tren, calle, hotel", orderIndex = 4) {
            lesson(id = 109, title = "Lesson 9: Finding Places", orderIndex = 0)
            challenge(id = 10280, lessonId = 109, type = ChallengeType.INTRO, question = "To the right", orderIndex = 0) {
                option(id = 300170, text = "A la derecha", correct = true)
                option(id = 300180, text = "To the right", correct = false)
            }
            challenge(id = 10281, lessonId = 109, type = ChallengeType.INTRO, question = "To the left", orderIndex = 1) {
                option(id = 300171, text = "A la izquierda", correct = true)
                option(id = 300181, text = "To the left", correct = false)
            }
            challenge(id = 10282, lessonId = 109, type = ChallengeType.INTRO, question = "Straight ahead", orderIndex = 2) {
                option(id = 300172, text = "Todo recto", correct = true)
                option(id = 300182, text = "Straight ahead", correct = false)
            }
            challenge(id = 10283, lessonId = 109, type = ChallengeType.INTRO, question = "The station", audioSrc = "asset:///audio/es/la_estacion.ogg", orderIndex = 3) {
                option(id = 300173, text = "La estación", correct = true, audioSrc = "asset:///audio/es/la_estacion.ogg")
                option(id = 300183, text = "The station", correct = false)
            }
            challenge(id = 1028, lessonId = 109, type = ChallengeType.SELECT, question = "Which phrase means 'To the right'?", orderIndex = 4) {
                option(id = 10094, text = "A la derecha", correct = true)
                option(id = 10095, text = "A la izquierda", correct = false)
                option(id = 10096, text = "Todo recto", correct = false)
            }
            challenge(id = 10284, lessonId = 109, type = ChallengeType.SELECT, question = "Which phrase means 'To the left'?", orderIndex = 5) {
                option(id = 300174, text = "A la izquierda", correct = true)
                option(id = 300175, text = "A la derecha", correct = false)
                option(id = 300176, text = "Todo recto", correct = false)
            }
            challenge(id = 1029, lessonId = 109, type = ChallengeType.WORD_BANK, question = "Assemble: 'The hotel is here'", orderIndex = 6, grammaticalFocus = "es.ser_estar", ruleText = "estar marks location and temporary states: El hotel está aquí.\nser marks identity and lasting qualities: El hotel es grande, La casa es mi casa. With a place word you need está; están would not agree with the singular 'el hotel'.") {
                option(id = 10097, text = "El hotel", correct = true)
                option(id = 10098, text = "está", correct = true)
                option(id = 10099, text = "aquí", correct = true)
                option(id = 10100, text = "es", correct = false, errorTag = "WRONG_COPULA")
                option(id = 300002, text = "están", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 10285, lessonId = 109, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/la_estacion.ogg", orderIndex = 7) {
                option(id = 300177, text = "La estación", correct = true)
                option(id = 300178, text = "El hotel", correct = false)
                option(id = 300179, text = "La calle", correct = false)
            }
        },
    )
    val japaneseExpandedUnits: List<UnitPayload> = listOf(
        unit(id = 22, courseId = 2, title = "Unit 3: Loanwords & Katakana", description = "Learn Katakana vocabulary: コーヒー, パン, テレビ, ホテル", orderIndex = 2) {
            lesson(id = 204, title = "Lesson 5: Café Katakana", orderIndex = 0)
            lesson(id = 205, title = "Lesson 6: Modern Items", orderIndex = 1)
            challenge(id = 2017, lessonId = 204, type = ChallengeType.SELECT, question = "Which one means 'Coffee' in Japanese?", audioSrc = "asset:///audio/ja/koohii.ogg", orderIndex = 0) {
                option(id = 20057, text = "コーヒー", romaji = "Koohii", correct = true, audioSrc = "asset:///audio/ja/koohii.ogg")
                option(id = 20058, text = "お茶", romaji = "Ocha", correct = false)
                option(id = 20059, text = "パン", romaji = "Pan", correct = false)
            }
            challenge(id = 2018, lessonId = 204, type = ChallengeType.WORD_BANK, question = "Assemble: 'Coffee, please'", orderIndex = 1, grammaticalFocus = "ja.request_polite", ruleText = "~てください is the polite way to ask someone to do something: コーヒーをください 'a coffee, please'.\nDropping ください leaves the plain request くれ, which sounds abrupt to a stranger. おねがいします is the softer version of the same request.") {
                option(id = 20060, text = "コーヒー", romaji = "koohii", correct = true, audioSrc = "asset:///audio/ja/koohii.ogg")
                option(id = 20061, text = "を", romaji = "o", correct = true)
                option(id = 20062, text = "ください", romaji = "kudasai", correct = true)
                option(id = 20063, text = "くれ", romaji = "kure", correct = false, errorTag = "WRONG_REGISTER")
                option(id = 300003, text = "おねがいします", romaji = "onegai shimasu", correct = false, errorTag = "WRONG_REGISTER")
            }
            challenge(id = 2019, lessonId = 204, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/pan.ogg", orderIndex = 2) {
                option(id = 20064, text = "パン", romaji = "Pan", correct = true)
                option(id = 20065, text = "ばん", romaji = "ban", correct = false)
                option(id = 20066, text = "ハン", romaji = "han", correct = false)
            }
            challenge(id = 2020, lessonId = 205, type = ChallengeType.SELECT, question = "What is 'Hotel' in Katakana?", orderIndex = 0) {
                option(id = 20067, text = "ホテル", romaji = "Hoteru", correct = true)
                option(id = 20068, text = "テレビ", romaji = "Terebi", correct = false)
                option(id = 20069, text = "カメラ", romaji = "Kamera", correct = false)
            }
            challenge(id = 2021, lessonId = 205, type = ChallengeType.WORD_BANK, question = "Assemble: 'Bread and coffee'", orderIndex = 1) {
                option(id = 20070, text = "パン", romaji = "pan", correct = true, audioSrc = "asset:///audio/ja/pan.ogg")
                option(id = 20071, text = "と", romaji = "to", correct = true)
                option(id = 20072, text = "コーヒー", romaji = "koohii", correct = true, audioSrc = "asset:///audio/ja/koohii.ogg")
                option(id = 20073, text = "お茶", romaji = "ocha", correct = false)
            }
            challenge(id = 2080, lessonId = 205, type = ChallengeType.MATCH_PAIRS, question = "Match the Katakana words", orderIndex = 2) {
                option(id = 20300, text = "コーヒー", romaji = "koohii", correct = true, audioSrc = "asset:///audio/ja/koohii.ogg")
                option(id = 20301, text = "Coffee", correct = true)
                option(id = 20302, text = "パン", romaji = "pan", correct = true, audioSrc = "asset:///audio/ja/pan.ogg")
                option(id = 20303, text = "Bread", correct = true)
                option(id = 20304, text = "ホテル", romaji = "hoteru", correct = true)
                option(id = 20305, text = "Hotel", correct = true)
            }
        },
        unit(id = 23, courseId = 2, title = "Unit 4: Daily Verbs & Actions", description = "Master polite present verbs: たべます, のみます, いきます", orderIndex = 3) {
            lesson(id = 206, title = "Lesson 7: Eating & Drinking Verbs", orderIndex = 0)
            challenge(id = 2022, lessonId = 206, type = ChallengeType.SELECT, question = "Which verb means 'To eat' (polite)?", audioSrc = "asset:///audio/ja/tabemasu.ogg", orderIndex = 0) {
                option(id = 20074, text = "たべます", romaji = "Tabemasu", correct = true, audioSrc = "asset:///audio/ja/tabemasu.ogg")
                option(id = 20075, text = "のみます", romaji = "Nomimasu", correct = false, audioSrc = "asset:///audio/ja/nomimasu.ogg")
                option(id = 20076, text = "いきます", romaji = "Ikimasu", correct = false)
            }
            challenge(id = 2023, lessonId = 206, type = ChallengeType.WORD_BANK, question = "Assemble: 'I eat bread'", orderIndex = 1, grammaticalFocus = "ja.polite_verb", ruleText = "The polite (です/ます) form of 食べる is たべます: drop the る and add ます.\nThe bare dictionary form 食べる is what you use before particles, never as a polite sentence ending.") {
                option(id = 20077, text = "パン", romaji = "pan", correct = true)
                option(id = 20078, text = "を", romaji = "o", correct = true)
                option(id = 20079, text = "たべます", romaji = "tabemasu", correct = true, audioSrc = "asset:///audio/ja/tabemasu.ogg")
                option(id = 20080, text = "たべる", romaji = "taberu", correct = false, errorTag = "WRONG_REGISTER")
                option(id = 300004, text = "たべて", romaji = "tabete", correct = false)
            }
            challenge(id = 2024, lessonId = 206, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/nomimasu.ogg", orderIndex = 2) {
                option(id = 20081, text = "のみます", romaji = "Nomimasu", correct = true)
                option(id = 20082, text = "のます", romaji = "nomasu", correct = false)
                option(id = 20083, text = "のみまず", romaji = "nomimazu", correct = false)
            }
        },
    )
}
