package com.duo.app.data.local.curriculum

import com.duo.app.data.local.entities.ChallengeEntity
import com.duo.app.data.local.entities.ChallengeOptionEntity
import com.duo.app.data.local.entities.LessonEntity
import com.duo.app.data.local.entities.UnitEntity

/**
 * A1 expansion, phase 2:
 *   Spanish (CEFR A1): Units 6-8 (Home & Furniture, Shopping & Money, Health & Body)
 *   Japanese (JLPT N5): Units 5-8 (Numbers & Counters, Katakana Travel & Food, Kanji Nature & Time, N5 Politeness)
 */
object AdvancedCurriculumData {

    // =========================================================================
    // SPANISH CEFR A1 (Units 6 - 8)
    // =========================================================================
    val spanishAdvancedUnits: List<UnitPayload> = listOf(
        // Unit 6: Home & Furniture
        UnitPayload(
            unit = UnitEntity(
                id = 15,
                courseId = 1,
                title = "Unit 6: Home & Furniture",
                description = "Describe your home: rooms, furniture, and sizes",
                orderIndex = 5,
            ),
            lessons = listOf(
                LessonEntity(id = 110, unitId = 15, title = "Lesson 10: Rooms of the House", orderIndex = 0),
                LessonEntity(id = 111, unitId = 15, title = "Lesson 11: Furniture", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(id = 1030, lessonId = 110, type = "SELECT", question = "Which one means 'The bedroom'?", audioSrc = "asset:///audio/es/el_dormitorio.ogg", orderIndex = 0),
                ChallengeEntity(id = 1031, lessonId = 110, type = "WORD_BANK", question = "Assemble: 'The kitchen is clean'", orderIndex = 1),
                ChallengeEntity(id = 1032, lessonId = 110, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/es/la_cocina.ogg", orderIndex = 2),

                ChallengeEntity(id = 1033, lessonId = 111, type = "SELECT", question = "Which one means 'The table'?", audioSrc = "asset:///audio/es/la_mesa.ogg", orderIndex = 0),
                ChallengeEntity(id = 1034, lessonId = 111, type = "WORD_BANK", question = "Assemble: 'The chair is big'", orderIndex = 1),
                ChallengeEntity(id = 1035, lessonId = 111, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/es/la_ventana.ogg", orderIndex = 2),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 10101, challengeId = 1030, text = "El dormitorio", correct = true, audioSrc = "asset:///audio/es/el_dormitorio.ogg"),
                ChallengeOptionEntity(id = 10102, challengeId = 1030, text = "La cocina", correct = false),
                ChallengeOptionEntity(id = 10103, challengeId = 1030, text = "El baño", correct = false),
                ChallengeOptionEntity(id = 10104, challengeId = 1030, text = "La sala", correct = false),

                ChallengeOptionEntity(id = 10105, challengeId = 1031, text = "La", correct = true),
                ChallengeOptionEntity(id = 10106, challengeId = 1031, text = "cocina", correct = true, audioSrc = "asset:///audio/es/la_cocina.ogg"),
                ChallengeOptionEntity(id = 10107, challengeId = 1031, text = "está", correct = true),
                ChallengeOptionEntity(id = 10108, challengeId = 1031, text = "limpia", correct = true),
                ChallengeOptionEntity(id = 10109, challengeId = 1031, text = "mesa", correct = false),
                ChallengeOptionEntity(id = 10110, challengeId = 1031, text = "sofá", correct = false),

                ChallengeOptionEntity(id = 10111, challengeId = 1032, text = "The kitchen", correct = true),
                ChallengeOptionEntity(id = 10112, challengeId = 1032, text = "The bedroom", correct = false),
                ChallengeOptionEntity(id = 10113, challengeId = 1032, text = "The bathroom", correct = false),

                ChallengeOptionEntity(id = 10114, challengeId = 1033, text = "La mesa", correct = true, audioSrc = "asset:///audio/es/la_mesa.ogg"),
                ChallengeOptionEntity(id = 10115, challengeId = 1033, text = "La silla", correct = false),
                ChallengeOptionEntity(id = 10116, challengeId = 1033, text = "La cama", correct = false),
                ChallengeOptionEntity(id = 10117, challengeId = 1033, text = "El sofá", correct = false),

                ChallengeOptionEntity(id = 10118, challengeId = 1034, text = "La silla", correct = true),
                ChallengeOptionEntity(id = 10119, challengeId = 1034, text = "es", correct = true),
                ChallengeOptionEntity(id = 10120, challengeId = 1034, text = "grande", correct = true),
                ChallengeOptionEntity(id = 10121, challengeId = 1034, text = "cama", correct = false),
                ChallengeOptionEntity(id = 10122, challengeId = 1034, text = "pequeño", correct = false),

                ChallengeOptionEntity(id = 10123, challengeId = 1035, text = "The window", correct = true),
                ChallengeOptionEntity(id = 10124, challengeId = 1035, text = "The door", correct = false),
                ChallengeOptionEntity(id = 10125, challengeId = 1035, text = "The table", correct = false),
            )
        ),

        // Unit 7: Shopping & Money
        UnitPayload(
            unit = UnitEntity(
                id = 16,
                courseId = 1,
                title = "Unit 7: Shopping & Money",
                description = "Buy clothes and ask prices: cuánto cuesta, barato, caro",
                orderIndex = 6,
            ),
            lessons = listOf(
                LessonEntity(id = 112, unitId = 16, title = "Lesson 12: At the Store", orderIndex = 0),
                LessonEntity(id = 113, unitId = 16, title = "Lesson 13: Prices & Clothing", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(id = 1036, lessonId = 112, type = "SELECT", question = "How do you ask 'How much does it cost'?", audioSrc = "asset:///audio/es/cuanto_cuesta.ogg", orderIndex = 0),
                ChallengeEntity(id = 1037, lessonId = 112, type = "WORD_BANK", question = "Assemble: 'This shirt is expensive'", orderIndex = 1),
                ChallengeEntity(id = 1038, lessonId = 112, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/es/la_camisa.ogg", orderIndex = 2),

                ChallengeEntity(id = 1039, lessonId = 113, type = "SELECT", question = "Which one means 'Cheap'?", audioSrc = "asset:///audio/es/barato.ogg", orderIndex = 0),
                ChallengeEntity(id = 1040, lessonId = 113, type = "WORD_BANK", question = "Assemble: 'I buy shoes'", orderIndex = 1),
                ChallengeEntity(id = 1041, lessonId = 113, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/es/los_zapatos.ogg", orderIndex = 2),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 10126, challengeId = 1036, text = "¿Cuánto cuesta?", correct = true, audioSrc = "asset:///audio/es/cuanto_cuesta.ogg"),
                ChallengeOptionEntity(id = 10127, challengeId = 1036, text = "¿Dónde está?", correct = false),
                ChallengeOptionEntity(id = 10128, challengeId = 1036, text = "¿Qué hora es?", correct = false),
                ChallengeOptionEntity(id = 10129, challengeId = 1036, text = "¿Cómo estás?", correct = false),

                ChallengeOptionEntity(id = 10130, challengeId = 1037, text = "Esta", correct = true),
                ChallengeOptionEntity(id = 10131, challengeId = 1037, text = "camisa", correct = true, audioSrc = "asset:///audio/es/la_camisa.ogg"),
                ChallengeOptionEntity(id = 10132, challengeId = 1037, text = "es", correct = true),
                ChallengeOptionEntity(id = 10133, challengeId = 1037, text = "cara", correct = true),
                ChallengeOptionEntity(id = 10134, challengeId = 1037, text = "pantalones", correct = false),
                ChallengeOptionEntity(id = 10135, challengeId = 1037, text = "barato", correct = false),

                ChallengeOptionEntity(id = 10136, challengeId = 1038, text = "The shirt", correct = true),
                ChallengeOptionEntity(id = 10137, challengeId = 1038, text = "The pants", correct = false),
                ChallengeOptionEntity(id = 10138, challengeId = 1038, text = "The coat", correct = false),

                ChallengeOptionEntity(id = 10139, challengeId = 1039, text = "Barato", correct = true, audioSrc = "asset:///audio/es/barato.ogg"),
                ChallengeOptionEntity(id = 10140, challengeId = 1039, text = "Caro", correct = false),
                ChallengeOptionEntity(id = 10141, challengeId = 1039, text = "Nuevo", correct = false),
                ChallengeOptionEntity(id = 10142, challengeId = 1039, text = "Bonito", correct = false),

                ChallengeOptionEntity(id = 10143, challengeId = 1040, text = "Yo", correct = true),
                ChallengeOptionEntity(id = 10144, challengeId = 1040, text = "compro", correct = true),
                ChallengeOptionEntity(id = 10145, challengeId = 1040, text = "zapatos", correct = true, audioSrc = "asset:///audio/es/los_zapatos.ogg"),
                ChallengeOptionEntity(id = 10146, challengeId = 1040, text = "vendo", correct = false),
                ChallengeOptionEntity(id = 10147, challengeId = 1040, text = "camisa", correct = false),

                ChallengeOptionEntity(id = 10148, challengeId = 1041, text = "The shoes", correct = true),
                ChallengeOptionEntity(id = 10149, challengeId = 1041, text = "The coat", correct = false),
                ChallengeOptionEntity(id = 10150, challengeId = 1041, text = "The money", correct = false),
            )
        ),

        // Unit 8: Health & Body
        UnitPayload(
            unit = UnitEntity(
                id = 17,
                courseId = 1,
                title = "Unit 8: Health & Body",
                description = "Talk about your body and when you feel unwell",
                orderIndex = 7,
            ),
            lessons = listOf(
                LessonEntity(id = 114, unitId = 17, title = "Lesson 14: My Body", orderIndex = 0),
                LessonEntity(id = 115, unitId = 17, title = "Lesson 15: When You Feel Unwell", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(id = 1042, lessonId = 114, type = "SELECT", question = "Which one means 'The head'?", audioSrc = "asset:///audio/es/la_cabeza.ogg", orderIndex = 0),
                ChallengeEntity(id = 1043, lessonId = 114, type = "WORD_BANK", question = "Assemble: 'I have a headache'", orderIndex = 1),
                ChallengeEntity(id = 1044, lessonId = 114, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/es/la_mano.ogg", orderIndex = 2),

                ChallengeEntity(id = 1045, lessonId = 115, type = "SELECT", question = "How do you say 'I have a fever'?", audioSrc = "asset:///audio/es/tengo_fiebre.ogg", orderIndex = 0),
                ChallengeEntity(id = 1046, lessonId = 115, type = "WORD_BANK", question = "Assemble: 'I take the medicine'", orderIndex = 1),
                ChallengeEntity(id = 1047, lessonId = 115, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/es/el_doctor.ogg", orderIndex = 2),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 10151, challengeId = 1042, text = "La cabeza", correct = true, audioSrc = "asset:///audio/es/la_cabeza.ogg"),
                ChallengeOptionEntity(id = 10152, challengeId = 1042, text = "El ojo", correct = false),
                ChallengeOptionEntity(id = 10153, challengeId = 1042, text = "La mano", correct = false),
                ChallengeOptionEntity(id = 10154, challengeId = 1042, text = "La boca", correct = false),

                ChallengeOptionEntity(id = 10155, challengeId = 1043, text = "Tengo", correct = true),
                ChallengeOptionEntity(id = 10156, challengeId = 1043, text = "dolor", correct = true),
                ChallengeOptionEntity(id = 10157, challengeId = 1043, text = "de", correct = true),
                ChallengeOptionEntity(id = 10158, challengeId = 1043, text = "cabeza", correct = true),
                ChallengeOptionEntity(id = 10159, challengeId = 1043, text = "fiebre", correct = false),
                ChallengeOptionEntity(id = 10160, challengeId = 1043, text = "frío", correct = false),

                ChallengeOptionEntity(id = 10161, challengeId = 1044, text = "The hand", correct = true),
                ChallengeOptionEntity(id = 10162, challengeId = 1044, text = "The mouth", correct = false),
                ChallengeOptionEntity(id = 10163, challengeId = 1044, text = "The eye", correct = false),

                ChallengeOptionEntity(id = 10164, challengeId = 1045, text = "Tengo fiebre", correct = true, audioSrc = "asset:///audio/es/tengo_fiebre.ogg"),
                ChallengeOptionEntity(id = 10165, challengeId = 1045, text = "Me duele", correct = false),
                ChallengeOptionEntity(id = 10166, challengeId = 1045, text = "Descansar", correct = false),
                ChallengeOptionEntity(id = 10167, challengeId = 1045, text = "Tomar medicina", correct = false),

                ChallengeOptionEntity(id = 10168, challengeId = 1046, text = "Yo", correct = true),
                ChallengeOptionEntity(id = 10169, challengeId = 1046, text = "tomo", correct = true),
                ChallengeOptionEntity(id = 10170, challengeId = 1046, text = "la", correct = true),
                ChallengeOptionEntity(id = 10171, challengeId = 1046, text = "medicina", correct = true),
                ChallengeOptionEntity(id = 10172, challengeId = 1046, text = "descanso", correct = false),
                ChallengeOptionEntity(id = 10173, challengeId = 1046, text = "fiebre", correct = false),

                ChallengeOptionEntity(id = 10174, challengeId = 1047, text = "The doctor", correct = true),
                ChallengeOptionEntity(id = 10175, challengeId = 1047, text = "The medicine", correct = false),
                ChallengeOptionEntity(id = 10176, challengeId = 1047, text = "The rest", correct = false),
            )
        )
    )

    // =========================================================================
    // JAPANESE N5 (Units 5 - 8)
    // =========================================================================
    val japaneseAdvancedUnits: List<UnitPayload> = listOf(
        // Unit 5: Numbers & Counters
        UnitPayload(
            unit = UnitEntity(
                id = 24,
                courseId = 2,
                title = "Unit 5: Numbers & Counters",
                description = "Count people, books, and money: 人, 冊, 円",
                orderIndex = 4,
            ),
            lessons = listOf(
                LessonEntity(id = 207, unitId = 24, title = "Lesson 8: Counting People", orderIndex = 0),
                LessonEntity(id = 208, unitId = 24, title = "Lesson 9: Counting Things & Money", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(id = 2025, lessonId = 207, type = "SELECT", question = "How do you say 'Two people'?", audioSrc = "asset:///audio/ja/futari.ogg", orderIndex = 0),
                ChallengeEntity(id = 2026, lessonId = 207, type = "WORD_BANK", question = "Assemble: 'There are three people here'", orderIndex = 1),
                ChallengeEntity(id = 2027, lessonId = 207, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/ninnin.ogg", orderIndex = 2),

                ChallengeEntity(id = 2028, lessonId = 208, type = "SELECT", question = "How do you say 'One book'?", audioSrc = "asset:///audio/ja/issatsu.ogg", orderIndex = 0),
                ChallengeEntity(id = 2029, lessonId = 208, type = "WORD_BANK", question = "Assemble: 'It's 500 yen'", orderIndex = 1),
                ChallengeEntity(id = 2030, lessonId = 208, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/senen.ogg", orderIndex = 2),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 20084, challengeId = 2025, text = "二人", romaji = "Futari", correct = true, audioSrc = "asset:///audio/ja/futari.ogg"),
                ChallengeOptionEntity(id = 20085, challengeId = 2025, text = "三冊", romaji = "Sansatsu", correct = false),
                ChallengeOptionEntity(id = 20086, challengeId = 2025, text = "一本", romaji = "Ippon", correct = false),
                ChallengeOptionEntity(id = 20087, challengeId = 2025, text = "五円", romaji = "Gon", correct = false),

                ChallengeOptionEntity(id = 20088, challengeId = 2026, text = "ここに", romaji = "Koni", correct = true),
                ChallengeOptionEntity(id = 20089, challengeId = 2026, text = "三人が", romaji = "Sannin ga", correct = true),
                ChallengeOptionEntity(id = 20090, challengeId = 2026, text = "います", romaji = "Imasu", correct = true),
                ChallengeOptionEntity(id = 20091, challengeId = 2026, text = "何人", romaji = "Nannin", correct = false),
                ChallengeOptionEntity(id = 20092, challengeId = 2026, text = "本", romaji = "Hon", correct = false),

                ChallengeOptionEntity(id = 20093, challengeId = 2027, text = "How many people? (何人)", correct = true),
                ChallengeOptionEntity(id = 20094, challengeId = 2027, text = "How many books? (何冊)", correct = false),
                ChallengeOptionEntity(id = 20095, challengeId = 2027, text = "How much? (いくら)", correct = false),

                ChallengeOptionEntity(id = 20096, challengeId = 2028, text = "一冊", romaji = "Issatsu", correct = true, audioSrc = "asset:///audio/ja/issatsu.ogg"),
                ChallengeOptionEntity(id = 20097, challengeId = 2028, text = "一個", romaji = "Ichiko", correct = false),
                ChallengeOptionEntity(id = 20098, challengeId = 2028, text = "一本", romaji = "Ippon", correct = false),
                ChallengeOptionEntity(id = 20099, challengeId = 2028, text = "一人", romaji = "Hitori", correct = false),

                ChallengeOptionEntity(id = 20100, challengeId = 2029, text = "五百円", romaji = "Gohyakuen", correct = true),
                ChallengeOptionEntity(id = 20101, challengeId = 2029, text = "です", romaji = "Desu", correct = true),
                ChallengeOptionEntity(id = 20102, challengeId = 2029, text = "千円", romaji = "Senen", correct = false),
                ChallengeOptionEntity(id = 20103, challengeId = 2029, text = "二人", romaji = "Futari", correct = false),

                ChallengeOptionEntity(id = 20104, challengeId = 2030, text = "1000 yen (千円)", correct = true),
                ChallengeOptionEntity(id = 20105, challengeId = 2030, text = "500 yen (五百円)", correct = false),
                ChallengeOptionEntity(id = 20106, challengeId = 2030, text = "2 people (二人)", correct = false),
            )
        ),

        // Unit 6: Katakana - Travel & Food
        UnitPayload(
            unit = UnitEntity(
                id = 25,
                courseId = 2,
                title = "Unit 6: Katakana Travel & Food",
                description = "Travel and restaurant Katakana: 駅, 空港, 寿司, ピザ",
                orderIndex = 5,
            ),
            lessons = listOf(
                LessonEntity(id = 209, unitId = 25, title = "Lesson 10: Travel in Katakana", orderIndex = 0),
                LessonEntity(id = 210, unitId = 25, title = "Lesson 11: Food in Katakana", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(id = 2031, lessonId = 209, type = "SELECT", question = "What is 'Station' in Katakana?", audioSrc = "asset:///audio/ja/eki.ogg", orderIndex = 0),
                ChallengeEntity(id = 2032, lessonId = 209, type = "WORD_BANK", question = "Assemble: 'The airport is far'", orderIndex = 1),
                ChallengeEntity(id = 2033, lessonId = 209, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/kuukou.ogg", orderIndex = 2),

                ChallengeEntity(id = 2034, lessonId = 210, type = "SELECT", question = "What is 'Sushi' in Katakana?", audioSrc = "asset:///audio/ja/sushi.ogg", orderIndex = 0),
                ChallengeEntity(id = 2035, lessonId = 210, type = "WORD_BANK", question = "Assemble: 'I eat sushi'", orderIndex = 1),
                ChallengeEntity(id = 2036, lessonId = 210, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/piza.ogg", orderIndex = 2),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 20107, challengeId = 2031, text = "駅", romaji = "Eki", correct = true, audioSrc = "asset:///audio/ja/eki.ogg"),
                ChallengeOptionEntity(id = 20108, challengeId = 2031, text = "バス", romaji = "Busu", correct = false),
                ChallengeOptionEntity(id = 20109, challengeId = 2031, text = "空港", romaji = "Kuukou", correct = false),
                ChallengeOptionEntity(id = 20110, challengeId = 2031, text = "タクシー", romaji = "Takushii", correct = false),

                ChallengeOptionEntity(id = 20111, challengeId = 2032, text = "空港", romaji = "Kuukou", correct = true, audioSrc = "asset:///audio/ja/kuukou.ogg"),
                ChallengeOptionEntity(id = 20112, challengeId = 2032, text = "は", romaji = "ha", correct = true),
                ChallengeOptionEntity(id = 20113, challengeId = 2032, text = "遠い", romaji = "Tōi", correct = true),
                ChallengeOptionEntity(id = 20114, challengeId = 2032, text = "駅", romaji = "Eki", correct = false),
                ChallengeOptionEntity(id = 20115, challengeId = 2032, text = "バス", romaji = "Busu", correct = false),

                ChallengeOptionEntity(id = 20116, challengeId = 2033, text = "Airport (空港)", correct = true),
                ChallengeOptionEntity(id = 20117, challengeId = 2033, text = "Bus (バス)", correct = false),
                ChallengeOptionEntity(id = 20118, challengeId = 2033, text = "Taxi (タクシー)", correct = false),

                ChallengeOptionEntity(id = 20119, challengeId = 2034, text = "寿司", romaji = "Sushi", correct = true, audioSrc = "asset:///audio/ja/sushi.ogg"),
                ChallengeOptionEntity(id = 20120, challengeId = 2034, text = "ピザ", romaji = "Piza", correct = false),
                ChallengeOptionEntity(id = 20121, challengeId = 2034, text = "餃子", romaji = "Gyōza", correct = false),
                ChallengeOptionEntity(id = 20122, challengeId = 2034, text = "レストラン", romaji = "Resutoran", correct = false),

                ChallengeOptionEntity(id = 20123, challengeId = 2035, text = "寿司", romaji = "sushi", correct = true, audioSrc = "asset:///audio/ja/sushi.ogg"),
                ChallengeOptionEntity(id = 20124, challengeId = 2035, text = "を", romaji = "o", correct = true),
                ChallengeOptionEntity(id = 20125, challengeId = 2035, text = "食べます", romaji = "tabemasu", correct = true),
                ChallengeOptionEntity(id = 20126, challengeId = 2035, text = "ピザ", romaji = "piza", correct = false),
                ChallengeOptionEntity(id = 20127, challengeId = 2035, text = "飲みます", romaji = "nomimasu", correct = false),

                ChallengeOptionEntity(id = 20128, challengeId = 2036, text = "Pizza (ピザ)", correct = true),
                ChallengeOptionEntity(id = 20129, challengeId = 2036, text = "Gyoza (餃子)", correct = false),
                ChallengeOptionEntity(id = 20130, challengeId = 2036, text = "Restaurant (レストラン)", correct = false),
            )
        ),

        // Unit 7: Kanji Basics - Nature & Time
        UnitPayload(
            unit = UnitEntity(
                id = 26,
                courseId = 2,
                title = "Unit 7: Kanji Nature & Time",
                description = "First Kanji: 山, 川, 雨, 火, 日, 月, 時間",
                orderIndex = 6,
            ),
            lessons = listOf(
                LessonEntity(id = 211, unitId = 26, title = "Lesson 12: Nature Kanji", orderIndex = 0),
                LessonEntity(id = 212, unitId = 26, title = "Lesson 13: Time Kanji", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(id = 2037, lessonId = 211, type = "SELECT", question = "What does 山 (yama) mean?", audioSrc = "asset:///audio/ja/yama.ogg", orderIndex = 0),
                ChallengeEntity(id = 2038, lessonId = 211, type = "WORD_BANK", question = "Assemble: 'I see a mountain'", orderIndex = 1),
                ChallengeEntity(id = 2039, lessonId = 211, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/ame.ogg", orderIndex = 2),

                ChallengeEntity(id = 2040, lessonId = 212, type = "SELECT", question = "What does 月 (tsuki) mean?", audioSrc = "asset:///audio/ja/tsuki.ogg", orderIndex = 0),
                ChallengeEntity(id = 2041, lessonId = 212, type = "WORD_BANK", question = "Assemble: 'It's one year'", orderIndex = 1),
                ChallengeEntity(id = 2042, lessonId = 212, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/jikan.ogg", orderIndex = 2),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 20131, challengeId = 2037, text = "Mountain", correct = true),
                ChallengeOptionEntity(id = 20132, challengeId = 2037, text = "River", correct = false),
                ChallengeOptionEntity(id = 20133, challengeId = 2037, text = "Tree", correct = false),
                ChallengeOptionEntity(id = 20134, challengeId = 2037, text = "Rain", correct = false),

                ChallengeOptionEntity(id = 20135, challengeId = 2038, text = "山", romaji = "Yama", correct = true, audioSrc = "asset:///audio/ja/yama.ogg"),
                ChallengeOptionEntity(id = 20136, challengeId = 2038, text = "が", romaji = "ga", correct = true),
                ChallengeOptionEntity(id = 20137, challengeId = 2038, text = "見えます", romaji = "miemasu", correct = true),
                ChallengeOptionEntity(id = 20138, challengeId = 2038, text = "川", romaji = "Kawa", correct = false),
                ChallengeOptionEntity(id = 20139, challengeId = 2038, text = "木", romaji = "Ki", correct = false),

                ChallengeOptionEntity(id = 20140, challengeId = 2039, text = "Rain (雨)", correct = true),
                ChallengeOptionEntity(id = 20141, challengeId = 2039, text = "Fire (火)", correct = false),
                ChallengeOptionEntity(id = 20142, challengeId = 2039, text = "Water (水)", correct = false),

                ChallengeOptionEntity(id = 20143, challengeId = 2040, text = "Moon / Month", correct = true),
                ChallengeOptionEntity(id = 20144, challengeId = 2040, text = "Day", correct = false),
                ChallengeOptionEntity(id = 20145, challengeId = 2040, text = "Time", correct = false),
                ChallengeOptionEntity(id = 20146, challengeId = 2040, text = "Water", correct = false),

                ChallengeOptionEntity(id = 20147, challengeId = 2041, text = "一年", romaji = "Ichinen", correct = true),
                ChallengeOptionEntity(id = 20148, challengeId = 2041, text = "です", romaji = "desu", correct = true),
                ChallengeOptionEntity(id = 20149, challengeId = 2041, text = "一日", romaji = "Ichinichi", correct = false),
                ChallengeOptionEntity(id = 20150, challengeId = 2041, text = "一月", romaji = "Ichigatsu", correct = false),

                ChallengeOptionEntity(id = 20151, challengeId = 2042, text = "Time (時間)", correct = true),
                ChallengeOptionEntity(id = 20152, challengeId = 2042, text = "Month (月)", correct = false),
                ChallengeOptionEntity(id = 20153, challengeId = 2042, text = "Day (日)", correct = false),
            )
        ),

        // Unit 8: N5 Politeness & Conversation
        UnitPayload(
            unit = UnitEntity(
                id = 27,
                courseId = 2,
                title = "Unit 8: N5 Politeness",
                description = "Polite conversation: names, gratitude, requests, 大丈夫, どうも",
                orderIndex = 7,
            ),
            lessons = listOf(
                LessonEntity(id = 213, unitId = 27, title = "Lesson 14: Polite Questions", orderIndex = 0),
                LessonEntity(id = 214, unitId = 27, title = "Lesson 15: Everyday Politeness", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(id = 2043, lessonId = 213, type = "SELECT", question = "How do you ask 'What is your name'?", audioSrc = "asset:///audio/ja/onamae.ogg", orderIndex = 0),
                ChallengeEntity(id = 2044, lessonId = 213, type = "WORD_BANK", question = "Assemble: 'I'm fine, thank you'", orderIndex = 1),
                ChallengeEntity(id = 2045, lessonId = 213, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/docha.ogg", orderIndex = 2),

                ChallengeEntity(id = 2046, lessonId = 214, type = "SELECT", question = "What do you say after someone helps you?", audioSrc = "asset:///audio/ja/doumo_arigatou.ogg", orderIndex = 0),
                ChallengeEntity(id = 2047, lessonId = 214, type = "WORD_BANK", question = "Assemble: 'Please' (polite)", orderIndex = 1),
                ChallengeEntity(id = 2048, lessonId = 214, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/onegai.ogg", orderIndex = 2),
                ChallengeEntity(id = 2049, lessonId = 214, type = "SELECT", question = "Story: Tanaka meets a visitor.\nTanaka: \"お名前は何ですか\"\nVisitor: \"Smithです。よろしくお願いします\"\nWhat is the visitor's name?", audioSrc = "asset:///audio/ja/onamae.ogg", orderIndex = 3),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 20154, challengeId = 2043, text = "お名前は何ですか", romaji = "Onamae wa nan desu ka", correct = true, audioSrc = "asset:///audio/ja/onamae.ogg"),
                ChallengeOptionEntity(id = 20155, challengeId = 2043, text = "何人ですか", romaji = "Nannin desu ka", correct = false),
                ChallengeOptionEntity(id = 20156, challengeId = 2043, text = "どちらですか", romaji = "Dochira desu ka", correct = false),
                ChallengeOptionEntity(id = 20157, challengeId = 2043, text = "大丈夫ですか", romaji = "Daijoubu desu ka", correct = false),

                ChallengeOptionEntity(id = 20158, challengeId = 2044, text = "大丈夫", romaji = "daijoubu", correct = true),
                ChallengeOptionEntity(id = 20159, challengeId = 2044, text = "です", romaji = "desu", correct = true),
                ChallengeOptionEntity(id = 20160, challengeId = 2044, text = "ありがとう", romaji = "arigatou", correct = true),
                ChallengeOptionEntity(id = 20161, challengeId = 2044, text = "どうも", romaji = "doumo", correct = false),
                ChallengeOptionEntity(id = 20162, challengeId = 2044, text = "結構", romaji = "kekkou", correct = false),

                ChallengeOptionEntity(id = 20163, challengeId = 2045, text = "Which way? (どちら)", correct = true),
                ChallengeOptionEntity(id = 20164, challengeId = 2045, text = "How are you? (どうですか)", correct = false),
                ChallengeOptionEntity(id = 20165, challengeId = 2045, text = "What time is it? (何時ですか)", correct = false),

                ChallengeOptionEntity(id = 20166, challengeId = 2046, text = "どうもありがとうございます", romaji = "Dōmo arigatō gozaimasu", correct = true, audioSrc = "asset:///audio/ja/doumo_arigatou.ogg"),
                ChallengeOptionEntity(id = 20167, challengeId = 2046, text = "結構です", romaji = "Kekkou desu", correct = false),
                ChallengeOptionEntity(id = 20168, challengeId = 2046, text = "お願いします", romaji = "Onegaishimasu", correct = false),
                ChallengeOptionEntity(id = 20169, challengeId = 2046, text = "大丈夫です", romaji = "Daijoubu desu", correct = false),

                ChallengeOptionEntity(id = 20170, challengeId = 2047, text = "お", romaji = "o", correct = true),
                ChallengeOptionEntity(id = 20171, challengeId = 2047, text = "願い", romaji = "negai", correct = true),
                ChallengeOptionEntity(id = 20172, challengeId = 2047, text = "します", romaji = "shimasu", correct = true),
                ChallengeOptionEntity(id = 20173, challengeId = 2047, text = "ありがとうございます", romaji = "arigatou gozaimasu", correct = false),
                ChallengeOptionEntity(id = 20174, challengeId = 2047, text = "どうも", romaji = "doumo", correct = false),

                ChallengeOptionEntity(id = 20175, challengeId = 2048, text = "Please (お願いします)", correct = true),
                ChallengeOptionEntity(id = 20176, challengeId = 2048, text = "Excuse me (すみません)", correct = false),
                ChallengeOptionEntity(id = 20177, challengeId = 2048, text = "Thank you (ありがとうございます)", correct = false),

                // Story comprehension options
                ChallengeOptionEntity(id = 20178, challengeId = 2049, text = "Smith", romaji = "Sumisu", correct = true),
                ChallengeOptionEntity(id = 20179, challengeId = 2049, text = "Tanaka", romaji = "Tanaka", correct = false),
                ChallengeOptionEntity(id = 20186, challengeId = 2049, text = "Yamada", romaji = "Yamada", correct = false),
            )
        )
    )
}
