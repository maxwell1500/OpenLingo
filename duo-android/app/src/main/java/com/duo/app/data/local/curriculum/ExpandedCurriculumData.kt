package com.duo.app.data.local.curriculum

import com.duo.app.data.local.entities.ChallengeEntity
import com.duo.app.data.local.entities.ChallengeOptionEntity
import com.duo.app.data.local.entities.LessonEntity
import com.duo.app.data.local.entities.UnitEntity

data class UnitPayload(
    val unit: UnitEntity,
    val lessons: List<LessonEntity>,
    val challenges: List<ChallengeEntity>,
    val options: List<ChallengeOptionEntity>,
)

object ExpandedCurriculumData {

    // =========================================================================
    // SPANISH A1 (Units 3 - 8)
    // =========================================================================
    val spanishExpandedUnits: List<UnitPayload> = listOf(
        // Unit 3: Food & Dining
        UnitPayload(
            unit = UnitEntity(
                id = 12,
                courseId = 1,
                title = "Unit 3: Food & Dining",
                description = "Order food, beverages, and ask for the bill at restaurants",
                orderIndex = 2,
            ),
            lessons = listOf(
                LessonEntity(id = 105, unitId = 12, title = "Lesson 5: Ordering at a Café", orderIndex = 0),
                LessonEntity(id = 106, unitId = 12, title = "Lesson 6: Restaurant Phrases", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(id = 1017, lessonId = 105, type = "SELECT", question = "Which one means 'A coffee, please'?", audioSrc = "asset:///audio/es/un_cafe_por_favor.ogg", orderIndex = 0),
                ChallengeEntity(id = 1018, lessonId = 105, type = "WORD_BANK", question = "Assemble: 'Water and bread'", orderIndex = 1),
                ChallengeEntity(id = 1019, lessonId = 105, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/es/la_cuenta.ogg", orderIndex = 2),

                ChallengeEntity(id = 1020, lessonId = 106, type = "SELECT", question = "How do you ask 'Where is the restaurant?'", audioSrc = "asset:///audio/es/donde_esta_el_restaurante.ogg", orderIndex = 0),
                ChallengeEntity(id = 1021, lessonId = 106, type = "WORD_BANK", question = "Assemble: 'The bill, please'", orderIndex = 1),
                ChallengeEntity(id = 1022, lessonId = 106, type = "SELECT", question = "Which one means 'Delicious'?", orderIndex = 2),
                ChallengeEntity(id = 1080, lessonId = 106, type = "MATCH_PAIRS", question = "Match the café words", orderIndex = 3),
                ChallengeEntity(id = 1081, lessonId = 106, type = "SELECT", question = "Dialogue at the café:\nCamarero: \"¿Qué desea tomar?\"\nCliente: \"Un café, por favor.\"\nWhat did the customer order?", audioSrc = "asset:///audio/es/un_cafe_por_favor.ogg", orderIndex = 4),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 10057, challengeId = 1017, text = "Un café, por favor", correct = true, audioSrc = "asset:///audio/es/un_cafe_por_favor.ogg"),
                ChallengeOptionEntity(id = 10058, challengeId = 1017, text = "Una cerveza fría", correct = false),
                ChallengeOptionEntity(id = 10059, challengeId = 1017, text = "Un vaso de leche", correct = false),

                ChallengeOptionEntity(id = 10060, challengeId = 1018, text = "Agua", correct = true),
                ChallengeOptionEntity(id = 10061, challengeId = 1018, text = "y", correct = true),
                ChallengeOptionEntity(id = 10062, challengeId = 1018, text = "pan", correct = true),
                ChallengeOptionEntity(id = 10063, challengeId = 1018, text = "café", correct = false),
                ChallengeOptionEntity(id = 10064, challengeId = 1018, text = "por favor", correct = false),

                ChallengeOptionEntity(id = 10065, challengeId = 1019, text = "The bill / check", correct = true),
                ChallengeOptionEntity(id = 10066, challengeId = 1019, text = "The kitchen", correct = false),
                ChallengeOptionEntity(id = 10067, challengeId = 1019, text = "The table", correct = false),

                ChallengeOptionEntity(id = 10068, challengeId = 1020, text = "¿Dónde está el restaurante?", correct = true, audioSrc = "asset:///audio/es/donde_esta_el_restaurante.ogg"),
                ChallengeOptionEntity(id = 10069, challengeId = 1020, text = "¿Dónde está la estación?", correct = false),
                ChallengeOptionEntity(id = 10070, challengeId = 1020, text = "¿Qué hora es?", correct = false),

                ChallengeOptionEntity(id = 10071, challengeId = 1021, text = "La cuenta", correct = true, audioSrc = "asset:///audio/es/la_cuenta.ogg"),
                ChallengeOptionEntity(id = 10072, challengeId = 1021, text = "por favor", correct = true),
                ChallengeOptionEntity(id = 10073, challengeId = 1021, text = "gracias", correct = false),
                ChallengeOptionEntity(id = 10074, challengeId = 1021, text = "buenos días", correct = false),

                ChallengeOptionEntity(id = 10075, challengeId = 1022, text = "Delicioso", correct = true),
                ChallengeOptionEntity(id = 10076, challengeId = 1022, text = "Caliente", correct = false),
                ChallengeOptionEntity(id = 10077, challengeId = 1022, text = "Frío", correct = false),

                // Pair 1: café <-> coffee
                ChallengeOptionEntity(id = 10300, challengeId = 1080, text = "Café", correct = true, audioSrc = "asset:///audio/es/un_cafe_por_favor.ogg"),
                ChallengeOptionEntity(id = 10301, challengeId = 1080, text = "Coffee", correct = true),
                // Pair 2: cuenta <-> bill
                ChallengeOptionEntity(id = 10302, challengeId = 1080, text = "Cuenta", correct = true, audioSrc = "asset:///audio/es/la_cuenta.ogg"),
                ChallengeOptionEntity(id = 10303, challengeId = 1080, text = "Bill", correct = true),
                // Pair 3: agua <-> water
                ChallengeOptionEntity(id = 10304, challengeId = 1080, text = "Agua", correct = true),
                ChallengeOptionEntity(id = 10305, challengeId = 1080, text = "Water", correct = true),

                // Dialogue comprehension
                ChallengeOptionEntity(id = 10306, challengeId = 1081, text = "A coffee", correct = true, audioSrc = "asset:///audio/es/un_cafe_por_favor.ogg"),
                ChallengeOptionEntity(id = 10307, challengeId = 1081, text = "A beer", correct = false),
                ChallengeOptionEntity(id = 10308, challengeId = 1081, text = "A glass of water", correct = false),
            )
        ),

        // Unit 4: Daily Routine & Verbs
        UnitPayload(
            unit = UnitEntity(
                id = 13,
                courseId = 1,
                title = "Unit 4: Daily Routine & Action Verbs",
                description = "Learn essential -ar, -er, and -ir verbs: hablar, comer, vivir",
                orderIndex = 3,
            ),
            lessons = listOf(
                LessonEntity(id = 107, unitId = 13, title = "Lesson 7: Everyday Actions", orderIndex = 0),
                LessonEntity(id = 108, unitId = 13, title = "Lesson 8: Habits & Home", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(id = 1023, lessonId = 107, type = "SELECT", question = "What is 'I speak Spanish'?", audioSrc = "asset:///audio/es/yo_hablo_espanol.ogg", orderIndex = 0),
                ChallengeEntity(id = 1024, lessonId = 107, type = "WORD_BANK", question = "Assemble: 'I live in Madrid'", orderIndex = 1),
                ChallengeEntity(id = 1025, lessonId = 107, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/es/yo_como_manzanas.ogg", orderIndex = 2),

                ChallengeEntity(id = 1026, lessonId = 108, type = "SELECT", question = "Translate: 'We study Spanish'", orderIndex = 0),
                ChallengeEntity(id = 1027, lessonId = 108, type = "WORD_BANK", question = "Assemble: 'He eats bread'", orderIndex = 1),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 10078, challengeId = 1023, text = "Yo hablo español", correct = true, audioSrc = "asset:///audio/es/yo_hablo_espanol.ogg"),
                ChallengeOptionEntity(id = 10079, challengeId = 1023, text = "Yo como manzana", correct = false),
                ChallengeOptionEntity(id = 10080, challengeId = 1023, text = "Yo vivo aquí", correct = false),

                ChallengeOptionEntity(id = 10081, challengeId = 1024, text = "Yo vivo", correct = true),
                ChallengeOptionEntity(id = 10082, challengeId = 1024, text = "en", correct = true),
                ChallengeOptionEntity(id = 10083, challengeId = 1024, text = "Madrid", correct = true),
                ChallengeOptionEntity(id = 10084, challengeId = 1024, text = "hablo", correct = false),

                ChallengeOptionEntity(id = 10085, challengeId = 1025, text = "I eat apples", correct = true),
                ChallengeOptionEntity(id = 10086, challengeId = 1025, text = "I drink water", correct = false),
                ChallengeOptionEntity(id = 10087, challengeId = 1025, text = "I speak English", correct = false),

                ChallengeOptionEntity(id = 10088, challengeId = 1026, text = "Nosotros estudiamos español", correct = true),
                ChallengeOptionEntity(id = 10089, challengeId = 1026, text = "Ellos estudian inglés", correct = false),

                ChallengeOptionEntity(id = 10090, challengeId = 1027, text = "Él", correct = true),
                ChallengeOptionEntity(id = 10091, challengeId = 1027, text = "come", correct = true),
                ChallengeOptionEntity(id = 10092, challengeId = 1027, text = "pan", correct = true),
                ChallengeOptionEntity(id = 10093, challengeId = 1027, text = "bebe", correct = false),
            )
        ),

        // Unit 5: City & Navigation
        UnitPayload(
            unit = UnitEntity(
                id = 14,
                courseId = 1,
                title = "Unit 5: City & Navigation",
                description = "Ask for directions: izquierda, derecha, tren, calle, hotel",
                orderIndex = 4,
            ),
            lessons = listOf(
                LessonEntity(id = 109, unitId = 14, title = "Lesson 9: Finding Places", orderIndex = 0),
            ),
            challenges = listOf(
                ChallengeEntity(id = 1028, lessonId = 109, type = "SELECT", question = "Which phrase means 'To the right'?", orderIndex = 0),
                ChallengeEntity(id = 1029, lessonId = 109, type = "WORD_BANK", question = "Assemble: 'The hotel is here'", orderIndex = 1),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 10094, challengeId = 1028, text = "A la derecha", correct = true),
                ChallengeOptionEntity(id = 10095, challengeId = 1028, text = "A la izquierda", correct = false),
                ChallengeOptionEntity(id = 10096, challengeId = 1028, text = "Todo recto", correct = false),

                ChallengeOptionEntity(id = 10097, challengeId = 1029, text = "El hotel", correct = true),
                ChallengeOptionEntity(id = 10098, challengeId = 1029, text = "está", correct = true),
                ChallengeOptionEntity(id = 10099, challengeId = 1029, text = "aquí", correct = true),
                ChallengeOptionEntity(id = 10100, challengeId = 1029, text = "lejos", correct = false),
            )
        )
    )

    // =========================================================================
    // JAPANESE N5 (Units 3 - 6)
    // =========================================================================
    val japaneseExpandedUnits: List<UnitPayload> = listOf(
        // Unit 3: Loanwords & Katakana
        UnitPayload(
            unit = UnitEntity(
                id = 22,
                courseId = 2,
                title = "Unit 3: Loanwords & Katakana",
                description = "Learn Katakana vocabulary: コーヒー, パン, テレビ, ホテル",
                orderIndex = 2,
            ),
            lessons = listOf(
                LessonEntity(id = 204, unitId = 22, title = "Lesson 5: Café Katakana", orderIndex = 0),
                LessonEntity(id = 205, unitId = 22, title = "Lesson 6: Modern Items", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(id = 2017, lessonId = 204, type = "SELECT", question = "Which one means 'Coffee' in Japanese?", audioSrc = "asset:///audio/ja/koohii.ogg", orderIndex = 0),
                ChallengeEntity(id = 2018, lessonId = 204, type = "WORD_BANK", question = "Assemble: 'Coffee, please'", orderIndex = 1),
                ChallengeEntity(id = 2019, lessonId = 204, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/pan.ogg", orderIndex = 2),

                ChallengeEntity(id = 2020, lessonId = 205, type = "SELECT", question = "What is 'Hotel' in Katakana?", orderIndex = 0),
                ChallengeEntity(id = 2021, lessonId = 205, type = "WORD_BANK", question = "Assemble: 'Bread and coffee'", orderIndex = 1),
                ChallengeEntity(id = 2080, lessonId = 205, type = "MATCH_PAIRS", question = "Match the Katakana words", orderIndex = 2),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 20057, challengeId = 2017, text = "コーヒー", romaji = "Koohii", correct = true, audioSrc = "asset:///audio/ja/koohii.ogg"),
                ChallengeOptionEntity(id = 20058, challengeId = 2017, text = "お茶", romaji = "Ocha", correct = false),
                ChallengeOptionEntity(id = 20059, challengeId = 2017, text = "パン", romaji = "Pan", correct = false),

                ChallengeOptionEntity(id = 20060, challengeId = 2018, text = "コーヒー", romaji = "koohii", correct = true, audioSrc = "asset:///audio/ja/koohii.ogg"),
                ChallengeOptionEntity(id = 20061, challengeId = 2018, text = "を", romaji = "o", correct = true),
                ChallengeOptionEntity(id = 20062, challengeId = 2018, text = "ください", romaji = "kudasai", correct = true),
                ChallengeOptionEntity(id = 20063, challengeId = 2018, text = "お水", romaji = "omizu", correct = false),

                ChallengeOptionEntity(id = 20064, challengeId = 2019, text = "Bread (パン)", correct = true),
                ChallengeOptionEntity(id = 20065, challengeId = 2019, text = "Rice (ごはん)", correct = false),
                ChallengeOptionEntity(id = 20066, challengeId = 2019, text = "Tea (お茶)", correct = false),

                ChallengeOptionEntity(id = 20067, challengeId = 2020, text = "ホテル", romaji = "Hoteru", correct = true),
                ChallengeOptionEntity(id = 20068, challengeId = 2020, text = "テレビ", romaji = "Terebi", correct = false),
                ChallengeOptionEntity(id = 20069, challengeId = 2020, text = "カメラ", romaji = "Kamera", correct = false),

                ChallengeOptionEntity(id = 20070, challengeId = 2021, text = "パン", romaji = "pan", correct = true, audioSrc = "asset:///audio/ja/pan.ogg"),
                ChallengeOptionEntity(id = 20071, challengeId = 2021, text = "と", romaji = "to", correct = true),
                ChallengeOptionEntity(id = 20072, challengeId = 2021, text = "コーヒー", romaji = "koohii", correct = true, audioSrc = "asset:///audio/ja/koohii.ogg"),
                ChallengeOptionEntity(id = 20073, challengeId = 2021, text = "お茶", romaji = "ocha", correct = false),

                // Pair 1: コーヒー <-> Coffee
                ChallengeOptionEntity(id = 20300, challengeId = 2080, text = "コーヒー", romaji = "koohii", correct = true, audioSrc = "asset:///audio/ja/koohii.ogg"),
                ChallengeOptionEntity(id = 20301, challengeId = 2080, text = "Coffee", correct = true),
                // Pair 2: パン <-> Bread
                ChallengeOptionEntity(id = 20302, challengeId = 2080, text = "パン", romaji = "pan", correct = true, audioSrc = "asset:///audio/ja/pan.ogg"),
                ChallengeOptionEntity(id = 20303, challengeId = 2080, text = "Bread", correct = true),
                // Pair 3: ホテル <-> Hotel
                ChallengeOptionEntity(id = 20304, challengeId = 2080, text = "ホテル", romaji = "hoteru", correct = true),
                ChallengeOptionEntity(id = 20305, challengeId = 2080, text = "Hotel", correct = true),
            )
        ),

        // Unit 4: Daily Actions & Verbs (Tabemasu, Nomimasu)
        UnitPayload(
            unit = UnitEntity(
                id = 23,
                courseId = 2,
                title = "Unit 4: Daily Verbs & Actions",
                description = "Master polite present verbs: たべます, のみます, いきます",
                orderIndex = 3,
            ),
            lessons = listOf(
                LessonEntity(id = 206, unitId = 23, title = "Lesson 7: Eating & Drinking Verbs", orderIndex = 0),
            ),
            challenges = listOf(
                ChallengeEntity(id = 2022, lessonId = 206, type = "SELECT", question = "Which verb means 'To eat' (polite)?", audioSrc = "asset:///audio/ja/tabemasu.ogg", orderIndex = 0),
                ChallengeEntity(id = 2023, lessonId = 206, type = "WORD_BANK", question = "Assemble: 'I eat bread'", orderIndex = 1),
                ChallengeEntity(id = 2024, lessonId = 206, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/nomimasu.ogg", orderIndex = 2),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 20074, challengeId = 2022, text = "たべます", romaji = "Tabemasu", correct = true, audioSrc = "asset:///audio/ja/tabemasu.ogg"),
                ChallengeOptionEntity(id = 20075, challengeId = 2022, text = "のみます", romaji = "Nomimasu", correct = false, audioSrc = "asset:///audio/ja/nomimasu.ogg"),
                ChallengeOptionEntity(id = 20076, challengeId = 2022, text = "いきます", romaji = "Ikimasu", correct = false),

                ChallengeOptionEntity(id = 20077, challengeId = 2023, text = "パン", romaji = "pan", correct = true),
                ChallengeOptionEntity(id = 20078, challengeId = 2023, text = "を", romaji = "o", correct = true),
                ChallengeOptionEntity(id = 20079, challengeId = 2023, text = "たべます", romaji = "tabemasu", correct = true, audioSrc = "asset:///audio/ja/tabemasu.ogg"),
                ChallengeOptionEntity(id = 20080, challengeId = 2023, text = "ごはん", romaji = "gohan", correct = false),

                ChallengeOptionEntity(id = 20081, challengeId = 2024, text = "To drink (のみます)", correct = true),
                ChallengeOptionEntity(id = 20082, challengeId = 2024, text = "To eat (たべます)", correct = false),
                ChallengeOptionEntity(id = 20083, challengeId = 2024, text = "To go (いきます)", correct = false),
            )
        )
    )
}
