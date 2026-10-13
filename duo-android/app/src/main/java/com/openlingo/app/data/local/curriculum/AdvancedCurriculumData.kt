package com.openlingo.app.data.local.curriculum
import com.openlingo.app.data.local.models.ChallengeType

object AdvancedCurriculumData {
    val spanishAdvancedUnits: List<UnitPayload> = listOf(
        unit(id = 15, courseId = 1, title = "Unit 6: Home & Furniture", description = "Describe your home: rooms, furniture, and sizes", orderIndex = 5) {
            lesson(id = 110, title = "Lesson 10: Rooms of the House", orderIndex = 0)
            lesson(id = 111, title = "Lesson 11: Furniture", orderIndex = 1)
            challenge(id = 1030, lessonId = 110, type = ChallengeType.SELECT, question = "Which one means 'The bedroom'?", audioSrc = "asset:///audio/es/el_dormitorio.ogg", orderIndex = 0, ruleText = "A bedroom is el dormitorio in Spain and la alcoba in Latin America: the same room, named with either word, so both are answered by the same English side. The clip says el dormitorio, the form offered here.") {
                option(id = 10101, text = "El dormitorio", romaji = "el dormitˈorio", correct = true, audioSrc = "asset:///audio/es/el_dormitorio.ogg")
                option(id = 10102, text = "La cocina", correct = false)
                option(id = 10103, text = "El baño", correct = false)
                option(id = 10104, text = "La sala", correct = false)
            }
            challenge(id = 1031, lessonId = 110, type = ChallengeType.WORD_BANK, question = "Assemble: 'The kitchen is clean'", orderIndex = 1, grammaticalFocus = "es.ser_estar", ruleText = "estar marks a state that holds right now: La cocina está limpia 'the kitchen is clean (now)'.\nser marks what something is or a lasting quality: La cocina es grande. es would claim the kitchen is a size, not a state; están would not agree with the singular 'la cocina'.") {
                option(id = 10105, text = "La", correct = true)
                option(id = 10106, text = "cocina", romaji = "kosˈina", correct = true, audioSrc = "asset:///audio/es/la_cocina.ogg")
                option(id = 10107, text = "está", correct = true)
                option(id = 10108, text = "limpia", correct = true)
                option(id = 10109, text = "es", correct = false, errorTag = "WRONG_COPULA")
                option(id = 10110, text = "están", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 1032, lessonId = 110, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/la_cocina.ogg", orderIndex = 2) {
                option(id = 10111, text = "La cocina", romaji = "la kosˈina", correct = true)
                option(id = 10112, text = "El cocina", correct = false)
                option(id = 10113, text = "La cosa", correct = false)
            }
            challenge(id = 30031, lessonId = 110, type = ChallengeType.CONJUGATE, question = "Which present form of the verb ser belongs with 'yo'?", orderIndex = 3, grammaticalFocus = "es.ser_present", ruleText = "ser is irregular in the present: yo soy, tú eres, él/ella es, nosotros somos, ellos/ellas son.\nAfter 'yo' only soy fits. son belongs to ellos/ellas and estás belongs to the other verb, estar.") {
                option(id = 300080, text = "soy", correct = true)
                option(id = 300081, text = "son", correct = false, errorTag = "WRONG_PERSON")
                option(id = 300082, text = "eres", correct = false, errorTag = "WRONG_PERSON")
                option(id = 300083, text = "estás", correct = false, errorTag = "WRONG_COPULA")
            }
            challenge(id = 1033, lessonId = 111, type = ChallengeType.SELECT, question = "Which one means 'The table'?", audioSrc = "asset:///audio/es/la_mesa.ogg", orderIndex = 0) {
                option(id = 10114, text = "La mesa", romaji = "la mˈesa", correct = true, audioSrc = "asset:///audio/es/la_mesa.ogg")
                option(id = 10115, text = "La silla", correct = false)
                option(id = 10116, text = "La cama", correct = false)
                option(id = 10117, text = "El sofá", correct = false)
            }
            challenge(id = 1034, lessonId = 111, type = ChallengeType.WORD_BANK, question = "Assemble: 'The chair is big'", orderIndex = 1, grammaticalFocus = "es.ser_estar", ruleText = "ser marks identity and lasting qualities: La silla es grande 'the chair is big'.\nestar marks location and temporary states: La silla está cerca 'the chair is nearby'. Here the size is a property of the chair, so es; son would not agree with the singular 'la silla'.") {
                option(id = 10118, text = "La silla", correct = true)
                option(id = 10119, text = "es", correct = true)
                option(id = 10120, text = "grande", correct = true)
                option(id = 10122, text = "está", correct = false, errorTag = "WRONG_COPULA")
                option(id = 300005, text = "son", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 1035, lessonId = 111, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/la_ventana.ogg", orderIndex = 2) {
                option(id = 10123, text = "La ventana", romaji = "la bentˈana", correct = true)
                option(id = 10124, text = "Las ventana", correct = false)
                option(id = 10125, text = "La verano", correct = false)
            }
        },
        unit(id = 16, courseId = 1, title = "Unit 7: Shopping & Money", description = "Buy clothes and ask prices: cuánto cuesta, barato, caro", orderIndex = 6) {
            lesson(id = 112, title = "Lesson 12: At the Store", orderIndex = 0)
            lesson(id = 113, title = "Lesson 13: Prices & Clothing", orderIndex = 1)
            challenge(id = 1036, lessonId = 112, type = ChallengeType.SELECT, question = "How do you ask 'How much does it cost'?", audioSrc = "asset:///audio/es/cuanto_cuesta.ogg", orderIndex = 0) {
                option(id = 10126, text = "¿Cuánto cuesta?", romaji = "kuuanto kˈuesta", correct = true, audioSrc = "asset:///audio/es/cuanto_cuesta.ogg")
                option(id = 10127, text = "¿Dónde está?", correct = false)
                option(id = 10128, text = "¿Qué hora es?", correct = false)
                option(id = 10129, text = "¿Cómo estás?", correct = false)
            }
            challenge(id = 1037, lessonId = 112, type = ChallengeType.WORD_BANK, question = "Assemble: 'This shirt is expensive'", orderIndex = 1, grammaticalFocus = "es.ser_estar", ruleText = "A price is a lasting property of the shirt, so ser: Esta camisa es cara.\nestar would describe a temporary state instead: Esta camisa está mojada 'this shirt is wet'. están does not agree with the singular 'esta camisa'.") {
                option(id = 10130, text = "Esta", correct = true)
                option(id = 10131, text = "camisa", romaji = "kamˈisa", correct = true, audioSrc = "asset:///audio/es/la_camisa.ogg")
                option(id = 10132, text = "es", correct = true)
                option(id = 10133, text = "cara", correct = true)
                option(id = 10134, text = "está", correct = false, errorTag = "WRONG_COPULA")
                option(id = 10135, text = "están", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 1038, lessonId = 112, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/la_camisa.ogg", orderIndex = 2) {
                option(id = 10136, text = "La camisa", romaji = "la kamˈisa", correct = true)
                option(id = 10137, text = "La camino", correct = false)
                option(id = 10138, text = "Las camisa", correct = false)
            }
            challenge(id = 1039, lessonId = 113, type = ChallengeType.SELECT, question = "Which one means 'Cheap'?", audioSrc = "asset:///audio/es/barato.ogg", orderIndex = 0) {
                option(id = 10139, text = "Barato", romaji = "barˈato", correct = true, audioSrc = "asset:///audio/es/barato.ogg")
                option(id = 10140, text = "Caro", correct = false)
                option(id = 10141, text = "Nuevo", correct = false)
                option(id = 10142, text = "Bonito", correct = false)
            }
            challenge(id = 1040, lessonId = 113, type = ChallengeType.WORD_BANK, question = "Assemble: 'I buy shoes'", orderIndex = 1, grammaticalFocus = "es.present_person", ruleText = "Present -ar: yo compro, tú compras, él compra, nosotros compramos, ellos compran.\nAfter 'yo' the ending is always -o, so neither compramos nor compra can follow it.") {
                option(id = 10143, text = "Yo", correct = true)
                option(id = 10144, text = "compro", correct = true)
                option(id = 10146, text = "compramos", correct = false, errorTag = "WRONG_PERSON")
                option(id = 10147, text = "compra", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 1041, lessonId = 113, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/los_zapatos.ogg", orderIndex = 2) {
                option(id = 10148, text = "Los zapatos", romaji = "los zapˈatos", correct = true)
                option(id = 10149, text = "Los zapato", correct = false)
                option(id = 10150, text = "La zapatos", correct = false)
            }
        },
        unit(id = 17, courseId = 1, title = "Unit 8: Health & Body", description = "Talk about your body and when you feel unwell", orderIndex = 7) {
            lesson(id = 114, title = "Lesson 14: My Body", orderIndex = 0)
            lesson(id = 115, title = "Lesson 15: When You Feel Unwell", orderIndex = 1)
            challenge(id = 1042, lessonId = 114, type = ChallengeType.SELECT, question = "Which one means 'The head'?", audioSrc = "asset:///audio/es/la_cabeza.ogg", orderIndex = 0) {
                option(id = 10151, text = "La cabeza", romaji = "la kabhˈeza", correct = true, audioSrc = "asset:///audio/es/la_cabeza.ogg")
                option(id = 10152, text = "El ojo", correct = false)
                option(id = 10153, text = "La mano", correct = false)
                option(id = 10154, text = "La boca", correct = false)
            }
            challenge(id = 1043, lessonId = 114, type = ChallengeType.WORD_BANK, question = "Assemble: 'I have a headache'", orderIndex = 1, grammaticalFocus = "es.tener_present", ruleText = "tener is irregular in the present: yo tengo, tú tienes, él tiene, nosotros tenemos, ellos tienen.\nIt is only 'yo' that says tengo — tiene and tienen belong to él/ella and ellos/ellas.") {
                option(id = 10155, text = "Tengo", correct = true)
                option(id = 10156, text = "dolor", correct = true)
                option(id = 10157, text = "de", correct = true)
                option(id = 10158, text = "cabeza", correct = true)
                option(id = 10159, text = "Tiene", correct = false, errorTag = "WRONG_PERSON")
                option(id = 10160, text = "Tienen", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 1044, lessonId = 114, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/la_mano.ogg", orderIndex = 2) {
                option(id = 10161, text = "La mano", romaji = "la mˈano", correct = true)
                option(id = 10162, text = "La manos", correct = false)
                option(id = 10163, text = "Las mano", correct = false)
            }
            challenge(id = 1045, lessonId = 115, type = ChallengeType.SELECT, question = "How do you say 'I have a fever'?", audioSrc = "asset:///audio/es/tengo_fiebre.ogg", orderIndex = 0) {
                option(id = 10164, text = "Tengo fiebre", romaji = "tˈenggo fˈiebhre", correct = true, audioSrc = "asset:///audio/es/tengo_fiebre.ogg")
                option(id = 10165, text = "Me duele", correct = false)
                option(id = 10166, text = "Descansar", correct = false)
                option(id = 10167, text = "Tomar medicina", correct = false)
            }
            challenge(id = 1046, lessonId = 115, type = ChallengeType.WORD_BANK, question = "Assemble: 'I take the medicine'", orderIndex = 1, grammaticalFocus = "es.present_person", ruleText = "Present -er/-ir: yo tomo, tú tomas, él toma, nosotros tomamos, ellos toman.\nAfter 'yo' the ending is always -o, so neither tomamos nor toman can follow it.") {
                option(id = 10168, text = "Yo", correct = true)
                option(id = 10169, text = "tomo", correct = true)
                option(id = 10170, text = "la", correct = true)
                option(id = 10171, text = "medicina", correct = true)
                option(id = 10172, text = "tomamos", correct = false, errorTag = "WRONG_PERSON")
                option(id = 10173, text = "toman", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 1047, lessonId = 115, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/el_doctor.ogg", orderIndex = 2) {
                option(id = 10174, text = "El doctor", romaji = "el doktˈor", correct = true)
                option(id = 10175, text = "La doctor", correct = false)
                option(id = 10176, text = "Los doctor", correct = false)
            }
        },
    )
    val japaneseAdvancedUnits: List<UnitPayload> = listOf(
        unit(id = 24, courseId = 2, title = "Unit 5: Numbers & Counters", description = "Count people, books, and money: 人, 冊, 円", orderIndex = 4) {
            lesson(id = 207, title = "Lesson 8: Counting People", orderIndex = 0)
            lesson(id = 208, title = "Lesson 9: Counting Things & Money", orderIndex = 1)
            challenge(id = 20251, lessonId = 207, type = ChallengeType.INTRO, question = "One person", orderIndex = 0) {
                option(id = 300241, text = "一人", romaji = "Hitori", correct = true)
            }
            challenge(id = 20252, lessonId = 207, type = ChallengeType.INTRO, question = "Two people", audioSrc = "asset:///audio/ja/futari.ogg", orderIndex = 1) {
                option(id = 300242, text = "二人", romaji = "Futari", correct = true, audioSrc = "asset:///audio/ja/futari.ogg")
            }
            challenge(id = 20253, lessonId = 207, type = ChallengeType.INTRO, question = "Three people", orderIndex = 2) {
                option(id = 300243, text = "三人", romaji = "Sannin", correct = true)
            }
            challenge(id = 20254, lessonId = 207, type = ChallengeType.INTRO, question = "Here", orderIndex = 3) {
                option(id = 300244, text = "ここに", romaji = "koko ni", correct = true)
            }
            challenge(id = 20255, lessonId = 207, type = ChallengeType.INTRO, question = "There is / are (living things)", orderIndex = 4) {
                option(id = 300245, text = "います", romaji = "imasu", correct = true)
            }
            challenge(id = 2025, lessonId = 207, type = ChallengeType.SELECT, question = "How do you say 'Two people'?", audioSrc = "asset:///audio/ja/futari.ogg", orderIndex = 5, grammaticalFocus = "ja.counter_people", ruleText = "People are counted with 人 (にん), and 1 and 2 are irregular: 一人 ひとり, 二人 ふたり, then 三人 さんにん.\nThe numeral before the counter is not enough on its own: 二本 counts long thin things and 二冊 counts books, so neither can stand in for 二人 here.") {
                option(id = 20084, text = "二人", romaji = "Futari", correct = true, audioSrc = "asset:///audio/ja/futari.ogg")
                option(id = 20085, text = "二本", romaji = "Nihon", correct = false, errorTag = "WRONG_CLASSIFIER")
                option(id = 20086, text = "二冊", romaji = "Nisatsu", correct = false, errorTag = "WRONG_CLASSIFIER")
                option(id = 20087, text = "三人", romaji = "Sannin", correct = false)
            }
            challenge(id = 2026, lessonId = 207, type = ChallengeType.WORD_BANK, question = "Assemble: 'There are three people here'", orderIndex = 6, grammaticalFocus = "ja.counter_people", ruleText = "People are counted with 人 (にん), and 1 and 2 are irregular: 一人 ひとり, 二人 ふたり, then 三人 さんにん.\n三冊 counts books and 三本 counts long thin things, so the classifier is the whole answer — the numeral alone cannot carry it.") {
                option(id = 20088, text = "ここに", romaji = "koko ni", correct = true)
                option(id = 20089, text = "三人が", romaji = "Sannin ga", correct = true)
                option(id = 20090, text = "います", romaji = "imasu", correct = true)
                option(id = 20091, text = "三冊が", romaji = "sanpaku ga", correct = false, errorTag = "WRONG_CLASSIFIER")
                option(id = 20092, text = "三本が", romaji = "sanbon ga", correct = false, errorTag = "WRONG_CLASSIFIER")
            }
            challenge(id = 2027, lessonId = 207, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/ninnin.ogg", orderIndex = 7) {
                option(id = 20093, text = "三人", romaji = "Sannin", correct = true)
                option(id = 20094, text = "二人", romaji = "Futari", correct = false)
                option(id = 20095, text = "三入", romaji = "San-nyuu", correct = false)
            }
            challenge(id = 20281, lessonId = 208, type = ChallengeType.INTRO, question = "One book (books classifier)", audioSrc = "asset:///audio/ja/issatsu.ogg", orderIndex = 0) {
                option(id = 300246, text = "一冊", romaji = "Issatsu", correct = true, audioSrc = "asset:///audio/ja/issatsu.ogg")
            }
            challenge(id = 20282, lessonId = 208, type = ChallengeType.INTRO, question = "Yen (currency)", orderIndex = 1) {
                option(id = 300247, text = "円", romaji = "en", correct = true)
            }
            challenge(id = 20283, lessonId = 208, type = ChallengeType.INTRO, question = "Is / are (copula)", orderIndex = 2) {
                option(id = 300248, text = "です", romaji = "desu", correct = true)
            }
            challenge(id = 20284, lessonId = 208, type = ChallengeType.INTRO, question = "500 yen", orderIndex = 3) {
                option(id = 300249, text = "五百円", romaji = "Gohyakuen", correct = true)
            }
            challenge(id = 20285, lessonId = 208, type = ChallengeType.INTRO, question = "1,000 yen", audioSrc = "asset:///audio/ja/senen.ogg", orderIndex = 4) {
                option(id = 300250, text = "千円", romaji = "Sennen", correct = true, audioSrc = "asset:///audio/ja/senen.ogg")
            }
            challenge(id = 2028, lessonId = 208, type = ChallengeType.SELECT, question = "How do you say 'One book'?", audioSrc = "asset:///audio/ja/issatsu.ogg", orderIndex = 5, grammaticalFocus = "ja.counter_classifier", ruleText = "Japanese counts with classifiers, and the noun alone never tells you which one. Books take 冊 (いっさつ), long thin things take 本 (いっぽん), small round things take 個 (いっこ), people take 人 (ひとり).\nAll four answers start with 一 — the classifier is the entire difference.") {
                option(id = 20096, text = "一冊", romaji = "Issatsu", correct = true, audioSrc = "asset:///audio/ja/issatsu.ogg")
                option(id = 20097, text = "一個", romaji = "Ikko", correct = false, errorTag = "WRONG_CLASSIFIER")
                option(id = 20098, text = "一本", romaji = "Ippon", correct = false, errorTag = "WRONG_CLASSIFIER")
                option(id = 20099, text = "一人", romaji = "Hitori", correct = false, errorTag = "WRONG_CLASSIFIER")
            }
            challenge(id = 2029, lessonId = 208, type = ChallengeType.WORD_BANK, question = "Assemble: 'It's 500 yen'", orderIndex = 6) {
                option(id = 20100, text = "五百円", romaji = "Gohyakuen", correct = true)
                option(id = 20101, text = "です", romaji = "Desu", correct = true)
                option(id = 20102, text = "千円", romaji = "Sennen", correct = false)
                option(id = 20103, text = "二人", romaji = "Futari", correct = false)
            }
            challenge(id = 2030, lessonId = 208, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/senen.ogg", orderIndex = 7) {
                option(id = 20104, text = "千円", romaji = "Sennen", correct = true)
                option(id = 20105, text = "百円", romaji = "Hyakuen", correct = false)
                option(id = 20106, text = "五百円", romaji = "Gohyakuen", correct = false)
            }
        },
        unit(id = 25, courseId = 2, title = "Unit 6: Katakana Travel & Food", description = "Travel and restaurant Katakana: 駅, 空港, 寿司, ピザ", orderIndex = 5) {
            lesson(id = 209, title = "Lesson 10: Travel in Katakana", orderIndex = 0)
            lesson(id = 210, title = "Lesson 11: Food in Katakana", orderIndex = 1)
            challenge(id = 2031, lessonId = 209, type = ChallengeType.SELECT, question = "What is 'Station' in Katakana?", audioSrc = "asset:///audio/ja/eki.ogg", orderIndex = 0) {
                option(id = 20107, text = "駅", romaji = "Eki", correct = true, audioSrc = "asset:///audio/ja/eki.ogg")
                option(id = 20108, text = "バス", romaji = "Busu", correct = false)
                option(id = 20109, text = "空港", romaji = "Kuukou", correct = false)
                option(id = 20110, text = "タクシー", romaji = "Takushii", correct = false)
            }
            challenge(id = 2032, lessonId = 209, type = ChallengeType.WORD_BANK, question = "Assemble: 'The airport is far'", orderIndex = 1) {
                option(id = 20111, text = "空港", romaji = "Kuukou", correct = true, audioSrc = "asset:///audio/ja/kuukou.ogg")
                option(id = 20112, text = "は", romaji = "ha", correct = true)
                option(id = 20113, text = "遠い", romaji = "Tōi", correct = true)
                option(id = 20114, text = "駅", romaji = "Eki", correct = false)
                option(id = 20115, text = "バス", romaji = "Busu", correct = false)
            }
            challenge(id = 2033, lessonId = 209, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/kuukou.ogg", orderIndex = 2) {
                option(id = 20116, text = "空港", romaji = "Kuukou", correct = true)
                option(id = 20117, text = "空母", romaji = "Kubo", correct = false)
                option(id = 20118, text = "港空", romaji = "Minamikuu", correct = false)
            }
            challenge(id = 2034, lessonId = 210, type = ChallengeType.SELECT, question = "What is 'Sushi' in Katakana?", audioSrc = "asset:///audio/ja/sushi.ogg", orderIndex = 0) {
                option(id = 20119, text = "寿司", romaji = "Sushi", correct = true, audioSrc = "asset:///audio/ja/sushi.ogg")
                option(id = 20120, text = "ピザ", romaji = "Piza", correct = false)
                option(id = 20121, text = "餃子", romaji = "Gyōza", correct = false)
                option(id = 20122, text = "レストラン", romaji = "Resutoran", correct = false)
            }
            challenge(id = 2035, lessonId = 210, type = ChallengeType.WORD_BANK, question = "Assemble: 'I eat sushi'", orderIndex = 1, grammaticalFocus = "ja.polite_verb", ruleText = "The polite (です/ます) form of 食べる is 食べます: drop the る and add ます.\n寿司を食べます 'I eat sushi' is the polite sentence; 寿司を食べる stops at the dictionary form, which is what you use before a particle, not at the end of a polite sentence.") {
                option(id = 20123, text = "寿司", romaji = "sushi", correct = true, audioSrc = "asset:///audio/ja/sushi.ogg")
                option(id = 20124, text = "を", romaji = "o", correct = true)
                option(id = 20125, text = "食べます", romaji = "tabemasu", correct = true)
                option(id = 20126, text = "食べる", romaji = "taberu", correct = false, errorTag = "WRONG_REGISTER")
                option(id = 20127, text = "食べない", romaji = "tabenai", correct = false)
            }
            challenge(id = 2036, lessonId = 210, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/piza.ogg", orderIndex = 2) {
                option(id = 20128, text = "ピザ", romaji = "Piza", correct = true)
                option(id = 20129, text = "ピサ", romaji = "Pisa", correct = false)
                option(id = 20130, text = "びざ", romaji = "biza", correct = false)
            }
        },
        unit(id = 26, courseId = 2, title = "Unit 7: Kanji Nature & Time", description = "First Kanji: 山, 川, 雨, 火, 日, 月, 時間", orderIndex = 6) {
            lesson(id = 211, title = "Lesson 12: Nature Kanji", orderIndex = 0)
            lesson(id = 212, title = "Lesson 13: Time Kanji", orderIndex = 1)
            challenge(id = 2037, lessonId = 211, type = ChallengeType.SELECT, question = "What does 山 (yama) mean?", audioSrc = "asset:///audio/ja/yama.ogg", orderIndex = 0) {
                option(id = 20131, text = "Mountain", correct = true)
                option(id = 20132, text = "River", correct = false)
                option(id = 20133, text = "Tree", correct = false)
                option(id = 20134, text = "Rain", correct = false)
            }
            challenge(id = 2038, lessonId = 211, type = ChallengeType.WORD_BANK, question = "Assemble: 'I can see a mountain'", orderIndex = 1, grammaticalFocus = "ja.potential", ruleText = "A potential form says what you are able to do. 見ます 'I see' becomes 見えます 'I can see': an ichidan verb drops る and takes the ます-stem plus える.\n見ます is the plain 'I see' and 見ました is 'I saw' — neither says anything about ability.") {
                option(id = 20135, text = "山", romaji = "Yama", correct = true, audioSrc = "asset:///audio/ja/yama.ogg")
                option(id = 20136, text = "が", romaji = "ga", correct = true)
                option(id = 20137, text = "見えます", romaji = "miemasu", correct = true)
                option(id = 20138, text = "見ます", romaji = "mimasu", correct = false, errorTag = "WRONG_FORM")
                option(id = 20139, text = "見ました", romaji = "mimashita", correct = false, errorTag = "WRONG_FORM")
            }
            challenge(id = 2039, lessonId = 211, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/ame.ogg", orderIndex = 2) {
                option(id = 20140, text = "雨", romaji = "Ame", correct = true)
                option(id = 20141, text = "雪", romaji = "Yuki", correct = false)
                option(id = 20142, text = "雲", romaji = "Kumo", correct = false)
            }
            challenge(id = 2040, lessonId = 212, type = ChallengeType.SELECT, question = "What does 月 (tsuki) mean?", audioSrc = "asset:///audio/ja/tsuki.ogg", orderIndex = 0) {
                option(id = 20143, text = "Moon / Month", correct = true)
                option(id = 20144, text = "Day", correct = false)
                option(id = 20145, text = "Time", correct = false)
                option(id = 20146, text = "Water", correct = false)
            }
            challenge(id = 2041, lessonId = 212, type = ChallengeType.WORD_BANK, question = "Assemble: 'It's one year'", orderIndex = 1, grammaticalFocus = "ja.counter_time", ruleText = "Time counters change what 一 counts, not how much: 一年 (one year), 一月 (January / one month), 一日 (one day), 一時 (one o'clock).\nThe numeral 一 is identical in all of them — the kanji after it is the whole answer.") {
                option(id = 20147, text = "一年", romaji = "Ichinen", correct = true)
                option(id = 20148, text = "です", romaji = "desu", correct = true)
                option(id = 20149, text = "一日", romaji = "Ichinichi", correct = false, errorTag = "WRONG_CLASSIFIER")
                option(id = 20150, text = "一月", romaji = "Ichigatsu", correct = false, errorTag = "WRONG_CLASSIFIER")
            }
            challenge(id = 2042, lessonId = 212, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/jikan.ogg", orderIndex = 2) {
                option(id = 20151, text = "時間", romaji = "Jikan", correct = true)
                option(id = 20152, text = "人間", romaji = "Ningen", correct = false)
                option(id = 20153, text = "時", romaji = "Toki", correct = false)
            }
        },
        unit(id = 27, courseId = 2, title = "Unit 8: N5 Politeness", description = "Polite conversation: names, gratitude, requests, 大丈夫, どうも", orderIndex = 7) {
            lesson(id = 213, title = "Lesson 14: Polite Questions", orderIndex = 0)
            lesson(id = 214, title = "Lesson 15: Everyday Politeness", orderIndex = 1)
            challenge(id = 2043, lessonId = 213, type = ChallengeType.SELECT, question = "How do you ask 'What is your name'?", audioSrc = "asset:///audio/ja/onamae.ogg", orderIndex = 0) {
                option(id = 20154, text = "お名前は何ですか", romaji = "Onamae wa nan desu ka", correct = true, audioSrc = "asset:///audio/ja/onamae.ogg")
                option(id = 20155, text = "何人ですか", romaji = "Nannin desu ka", correct = false)
                option(id = 20156, text = "どちらですか", romaji = "Dochira desu ka", correct = false)
                option(id = 20157, text = "大丈夫ですか", romaji = "Daijoubu desu ka", correct = false)
            }
            challenge(id = 2044, lessonId = 213, type = ChallengeType.WORD_BANK, question = "Assemble: 'I'm fine, thank you'", orderIndex = 1, grammaticalFocus = "ja.copula_polite", ruleText = "です is the polite copula and でした is its polite past. だ is the plain, casual copula you use with friends, and ではありません is the polite negative.\nA learner who answers でした has said 'I was fine' — right copula, wrong tense.") {
                option(id = 20158, text = "大丈夫", romaji = "daijoubu", correct = true)
                option(id = 20159, text = "です", romaji = "desu", correct = true)
                option(id = 20160, text = "ありがとう", romaji = "arigatou", correct = true)
                option(id = 20161, text = "でした", romaji = "deshita", correct = false, errorTag = "WRONG_TENSE")
                option(id = 20162, text = "だ", romaji = "da", correct = false, errorTag = "WRONG_REGISTER")
            }
            challenge(id = 2045, lessonId = 213, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/docha.ogg", orderIndex = 2) {
                option(id = 20163, text = "どちら", romaji = "Dochira", correct = true)
                option(id = 20164, text = "どうち", romaji = "Douchi", correct = false)
                option(id = 20165, text = "どち", romaji = "Dochi", correct = false)
            }
            challenge(id = 2046, lessonId = 214, type = ChallengeType.SELECT, question = "What do you say after someone helps you?", audioSrc = "asset:///audio/ja/doumo_arigatou.ogg", orderIndex = 0) {
                option(id = 20166, text = "どうもありがとうございます", romaji = "Dōmo arigatō gozaimasu", correct = true, audioSrc = "asset:///audio/ja/doumo_arigatou.ogg")
                option(id = 20167, text = "結構です", romaji = "Kekkou desu", correct = false)
                option(id = 20168, text = "お願いします", romaji = "Onegaishimasu", correct = false)
                option(id = 20169, text = "大丈夫です", romaji = "Daijoubu desu", correct = false)
            }
            challenge(id = 2047, lessonId = 214, type = ChallengeType.WORD_BANK, question = "Assemble: 'Please' (polite)", orderIndex = 1) {
                option(id = 20170, text = "お", romaji = "o", correct = true)
                option(id = 20171, text = "願い", romaji = "negai", correct = true)
                option(id = 20172, text = "します", romaji = "shimasu", correct = true)
                option(id = 20173, text = "ありがとうございます", romaji = "arigatou gozaimasu", correct = false)
                option(id = 20174, text = "どうも", romaji = "doumo", correct = false)
            }
            challenge(id = 30030, lessonId = 214, type = ChallengeType.WORD_BANK, question = "Assemble: 'Tanaka-san is a teacher' — the polite copula", orderIndex = 4, grammaticalFocus = "ja.copula_polite", ruleText = "です is the polite copula. だ is the plain form you use with friends, and じゃ / で are the spoken contractions of the same thing.\nA polite sentence never ends in だ: 田中さんは先生です, never 田中さんは先生だ.") {
                option(id = 300084, text = "田中さんは", romaji = "Tanaka-san wa", correct = true)
                option(id = 300085, text = "先生です", romaji = "sensei desu", correct = true)
                option(id = 300086, text = "先生だ", romaji = "sensei da", correct = false, errorTag = "WRONG_REGISTER")
                option(id = 300087, text = "先生じゃ", romaji = "sensei ja", correct = false, errorTag = "WRONG_REGISTER")
                option(id = 300088, text = "先生で", romaji = "sensei de", correct = false, errorTag = "WRONG_REGISTER")
            }
            challenge(id = 2048, lessonId = 214, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/onegai.ogg", orderIndex = 2) {
                option(id = 20175, text = "お願いします", romaji = "Onegaishimasu", correct = true)
                option(id = 20176, text = "おねがい", romaji = "onegai", correct = false)
                option(id = 20177, text = "お願いす", romaji = "onegaisu", correct = false)
            }
            challenge(id = 2049, lessonId = 214, type = ChallengeType.SELECT, question = "Story: Tanaka meets a visitor.\nTanaka: \"お名前は何ですか\"\nVisitor: \"Smithです。よろしくお願いします\"\nWhat is the visitor's name?", audioSrc = "asset:///audio/ja/onamae.ogg", orderIndex = 3) {
                option(id = 20178, text = "Smith", romaji = "Sumisu", correct = true)
                option(id = 20179, text = "Tanaka", romaji = "Tanaka", correct = false)
                option(id = 20186, text = "Yamada", romaji = "Yamada", correct = false)
            }
        },
    )
}
