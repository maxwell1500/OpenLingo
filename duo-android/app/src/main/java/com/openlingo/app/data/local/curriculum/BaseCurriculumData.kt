package com.openlingo.app.data.local.curriculum
import com.openlingo.app.data.local.models.ChallengeType

/**
 * The two base courses, authored on the curriculum DSL. Migrated out of
 * `LocalProgressRepository.seedSpanishCourse` / `seedJapaneseCourse`, which
 * used to list the units, lessons, challenges and options as four separate
 * insert arguments; the repository now seeds them with the same generic
 * payload loop as every other curriculum.
 * An INTRO card's question is the ENGLISH it introduces; the word itself lives
 * on the option. Filing the Spanish word into `question` made the card show its
 * own word as its own meaning line, and made DictionaryIndex file "De nada" as
 * its own gloss -- a tautology that beat the honest "You're welcome" taught
 * later in the same unit.
 */
object BaseCurriculumData {

    val spanishBaseUnits: List<UnitPayload> = listOf(
        unit(id = 10, courseId = 1, title = "Unit 1: Spanish Essentials", description = "Learn foundational Spanish greetings, nouns, and phrases", orderIndex = 0) {
            lesson(id = 100, title = "Lesson 1: Greetings & Essentials", orderIndex = 0)
            challenge(id = 1001, lessonId = 100, type = ChallengeType.INTRO, question = "Hello", romaji = "ˈola", audioSrc = "asset:///audio/es/hola.ogg", orderIndex = 0) {
                option(id = 1000100, text = "¡Hola!", romaji = "ˈola", correct = true, audioSrc = "asset:///audio/es/hola.ogg")
            }
            challenge(id = 1002, lessonId = 100, type = ChallengeType.INTRO, question = "Good morning", romaji = "bˈuenos dˈias", audioSrc = "asset:///audio/es/buenos_dias.ogg", orderIndex = 1) {
                option(id = 1000101, text = "Buenos días", romaji = "bˈuenos dˈias", correct = true, audioSrc = "asset:///audio/es/buenos_dias.ogg")
            }
            challenge(id = 1003, lessonId = 100, type = ChallengeType.INTRO, question = "Goodbye", orderIndex = 2) {
                option(id = 1000102, text = "Adiós", correct = true)
            }
            challenge(id = 1004, lessonId = 100, type = ChallengeType.SELECT, question = "Which one means 'Hello'?", orderIndex = 3) {
                option(id = 1000103, text = "¡Hola!", romaji = "ˈola", correct = true)
                option(id = 1000104, text = "Buenos días", romaji = "bˈuenos dˈias", correct = false)
                option(id = 1000105, text = "Adiós", correct = false)
            }
            challenge(id = 1005, lessonId = 100, type = ChallengeType.SELECT, question = "Which phrase means 'Good morning'?", orderIndex = 4) {
                option(id = 1000106, text = "Buenos días", romaji = "bˈuenos dˈias", correct = true)
                option(id = 1000107, text = "Adiós", correct = false)
                option(id = 1000108, text = "¡Hola!", romaji = "ˈola", correct = false)
            }
            challenge(id = 1006, lessonId = 100, type = ChallengeType.ASSIST, question = "___ días", orderIndex = 5) {
                option(id = 1000109, text = "Buenos", correct = true)
                option(id = 1000110, text = "Adiós", correct = false)
                option(id = 1000111, text = "Hola", correct = false)
            }
            challenge(id = 1007, lessonId = 100, type = ChallengeType.WORD_BANK, question = "Translate: 'Hello, good morning'", orderIndex = 6) {
                option(id = 1000112, text = "¡Hola!", correct = true)
                option(id = 1000113, text = "buenos", correct = true)
                option(id = 1000114, text = "días", correct = true)
                option(id = 1000115, text = "adiós", correct = false)
            }
            challenge(id = 1008, lessonId = 100, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/buenos_dias.ogg", orderIndex = 7) {
                option(id = 1000116, text = "Buenos días", romaji = "bˈuenos dˈias", correct = true)
                option(id = 1000117, text = "Buenas noches", correct = false)
                option(id = 1000118, text = "¡Hola!", romaji = "ˈola", correct = false)
            }
            lesson(id = 101, title = "Lesson 2: Common & Polite Phrases", orderIndex = 1)
            challenge(id = 1009, lessonId = 101, type = ChallengeType.INTRO, question = "Please", romaji = "por fabhˈor", audioSrc = "asset:///audio/es/por_favor.ogg", orderIndex = 0) {
                option(id = 1000119, text = "Por favor", romaji = "por fabhˈor", correct = true, audioSrc = "asset:///audio/es/por_favor.ogg")
            }
            challenge(id = 1010, lessonId = 101, type = ChallengeType.INTRO, question = "Thank you", romaji = "grˈasias", audioSrc = "asset:///audio/es/gracias.ogg", orderIndex = 1) {
                option(id = 1000120, text = "Gracias", romaji = "grˈasias", correct = true, audioSrc = "asset:///audio/es/gracias.ogg")
            }
            challenge(id = 1011, lessonId = 101, type = ChallengeType.INTRO, question = "You're welcome", romaji = "de nˈadha", audioSrc = "asset:///audio/es/de_nada.ogg", orderIndex = 2) {
                option(id = 1000121, text = "De nada", romaji = "de nˈadha", correct = true, audioSrc = "asset:///audio/es/de_nada.ogg")
            }
            challenge(id = 1012, lessonId = 101, type = ChallengeType.INTRO, question = "Yes", orderIndex = 3) {
                option(id = 1000122, text = "Sí", correct = true)
            }
            challenge(id = 1013, lessonId = 101, type = ChallengeType.SELECT, question = "How do you say 'Thank you'?", audioSrc = "asset:///audio/es/gracias.ogg", orderIndex = 4) {
                option(id = 1000123, text = "Muchas gracias", romaji = "mˈuchas grˈasias", correct = true)
                option(id = 1000124, text = "De nada", romaji = "de nˈadha", correct = false)
                option(id = 1000125, text = "Por favor", romaji = "por fabhˈor", correct = false)
            }
            challenge(id = 1014, lessonId = 101, type = ChallengeType.SELECT, question = "What is 'You're welcome'?", audioSrc = "asset:///audio/es/de_nada.ogg", orderIndex = 5) {
                option(id = 1000126, text = "De nada", romaji = "de nˈadha", correct = true)
                option(id = 1000127, text = "Gracias", romaji = "grˈasias", correct = false)
                option(id = 1000128, text = "Por favor", romaji = "por fabhˈor", correct = false)
            }
            challenge(id = 1015, lessonId = 101, type = ChallengeType.ASSIST, question = "Sí, por ___", orderIndex = 6) {
                option(id = 1000129, text = "favor", correct = true)
                option(id = 1000130, text = "gracias", correct = false)
                option(id = 1000131, text = "nada", correct = false)
            }
            challenge(id = 100010, lessonId = 101, type = ChallengeType.WORD_BANK, question = "Translate: 'Yes, please'", orderIndex = 7) {
                option(id = 1000132, text = "Sí", correct = true)
                option(id = 1000133, text = "por", correct = true)
                option(id = 1000134, text = "favor", correct = true)
                option(id = 1000135, text = "gracias", correct = false)
                option(id = 1000136, text = "de", correct = false)
            }
            challenge(id = 100011, lessonId = 101, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/por_favor.ogg", orderIndex = 8) {
                option(id = 1000137, text = "Por favor", romaji = "por fabhˈor", correct = true)
                option(id = 1000138, text = "Gracias", romaji = "grˈasias", correct = false)
                option(id = 1000139, text = "De nada", romaji = "de nˈadha", correct = false)
            }
        },
        unit(id = 11, courseId = 1, title = "Unit 2: People & Family", description = "Talk about family members, people, and simple descriptions", orderIndex = 1) {
            lesson(id = 102, title = "Lesson 3: People & Family", orderIndex = 0)
            challenge(id = 100012, lessonId = 102, type = ChallengeType.INTRO, question = "The man", romaji = "el ˈombre", orderIndex = 0) {
                option(id = 1000140, text = "El hombre", romaji = "el ˈombre", correct = true)
            }
            challenge(id = 100013, lessonId = 102, type = ChallengeType.INTRO, question = "The woman", romaji = "la mujˈer", orderIndex = 1) {
                option(id = 1000141, text = "La mujer", romaji = "la mujˈer", correct = true)
            }
            challenge(id = 100014, lessonId = 102, type = ChallengeType.INTRO, question = "The father", romaji = "el pˈadhre", audioSrc = "asset:///audio/es/el_padre.ogg", orderIndex = 2) {
                option(id = 1000142, text = "El padre", romaji = "el pˈadhre", correct = true, audioSrc = "asset:///audio/es/el_padre.ogg")
            }
            challenge(id = 100015, lessonId = 102, type = ChallengeType.INTRO, question = "The mother", romaji = "la mˈadhre", audioSrc = "asset:///audio/es/la_madre.ogg", orderIndex = 3) {
                option(id = 1000143, text = "La madre", romaji = "la mˈadhre", correct = true, audioSrc = "asset:///audio/es/la_madre.ogg")
            }
            challenge(id = 100016, lessonId = 102, type = ChallengeType.INTRO, question = "The family", romaji = "la famˈilia", audioSrc = "asset:///audio/es/la_familia.ogg", orderIndex = 4) {
                option(id = 1000144, text = "La familia", romaji = "la famˈilia", correct = true, audioSrc = "asset:///audio/es/la_familia.ogg")
            }
            challenge(id = 100017, lessonId = 102, type = ChallengeType.SELECT, question = "Translate: 'The father'", orderIndex = 5) {
                option(id = 1000145, text = "El padre", romaji = "el pˈadhre", correct = true)
                option(id = 1000146, text = "La madre", romaji = "la mˈadhre", correct = false)
                option(id = 1000147, text = "El hombre", romaji = "el ˈombre", correct = false)
            }
            challenge(id = 100018, lessonId = 102, type = ChallengeType.SELECT, question = "Translate: 'The mother'", orderIndex = 6) {
                option(id = 1000148, text = "La madre", romaji = "la mˈadhre", correct = true)
                option(id = 1000149, text = "La mujer", romaji = "la mujˈer", correct = false)
                option(id = 1000150, text = "El padre", romaji = "el pˈadhre", correct = false)
            }
            challenge(id = 100019, lessonId = 102, type = ChallengeType.SELECT, question = "Translate: 'The woman'", orderIndex = 7) {
                option(id = 1000151, text = "La mujer", romaji = "la mujˈer", correct = true)
                option(id = 1000152, text = "El hombre", romaji = "el ˈombre", correct = false)
                option(id = 1000153, text = "La madre", romaji = "la mˈadhre", correct = false)
            }
            challenge(id = 100020, lessonId = 102, type = ChallengeType.MATCH_PAIRS, question = "Match each person to their meaning", orderIndex = 8) {
                option(id = 1000154, text = "El hombre", romaji = "el ˈombre", correct = true)
                option(id = 1000155, text = "The man", correct = true)
                option(id = 1000156, text = "La mujer", romaji = "la mujˈer", correct = true)
                option(id = 1000157, text = "The woman", correct = true)
                option(id = 1000158, text = "El padre", romaji = "el pˈadhre", correct = true)
                option(id = 1000159, text = "The father", correct = true)
                option(id = 1000160, text = "La madre", romaji = "la mˈadhre", correct = true)
                option(id = 1000161, text = "The mother", correct = true)
            }
            challenge(id = 100021, lessonId = 102, type = ChallengeType.WORD_BANK, question = "Translate: 'The father and the mother'", orderIndex = 9) {
                option(id = 1000162, text = "El", correct = true)
                option(id = 1000163, text = "padre", correct = true)
                option(id = 1000164, text = "y", correct = true)
                option(id = 1000165, text = "la", correct = true)
                option(id = 1000166, text = "madre", correct = true)
                option(id = 1000167, text = "mujer", correct = false)
            }
            challenge(id = 100022, lessonId = 102, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/la_familia.ogg", orderIndex = 10) {
                option(id = 1000168, text = "La familia", romaji = "la famˈilia", correct = true)
                option(id = 1000169, text = "El padre", romaji = "el pˈadhre", correct = false)
                option(id = 1000170, text = "La madre", romaji = "la mˈadhre", correct = false)
            }
            lesson(id = 103, title = "Lesson 4: Introductions & Identity", orderIndex = 1)
            challenge(id = 100023, lessonId = 103, type = ChallengeType.INTRO, question = "Nice to meet you", romaji = "mˈucho gˈusto", audioSrc = "asset:///audio/es/mucho_gusto.ogg", orderIndex = 0) {
                option(id = 1000171, text = "¡Mucho gusto!", romaji = "mˈucho gˈusto", correct = true, audioSrc = "asset:///audio/es/mucho_gusto.ogg")
            }
            challenge(id = 100024, lessonId = 103, type = ChallengeType.INTRO, question = "I am", grammaticalFocus = "es.ser_present", ruleText = "ser (to be, identity): yo soy = I am", orderIndex = 1) {
                option(id = 1000172, text = "Yo soy", correct = true)
            }
            challenge(id = 100025, lessonId = 103, type = ChallengeType.INTRO, question = "A boy", romaji = "un nˈiño", orderIndex = 2) {
                option(id = 1000173, text = "Un niño", romaji = "un nˈiño", correct = true)
            }
            challenge(id = 100026, lessonId = 103, type = ChallengeType.INTRO, question = "A girl", romaji = "ˈuna nˈiña", orderIndex = 3) {
                option(id = 1000174, text = "Una niña", romaji = "ˈuna nˈiña", correct = true)
            }
            challenge(id = 100027, lessonId = 103, type = ChallengeType.SELECT, question = "How do you say 'Nice to meet you'?", orderIndex = 4) {
                option(id = 1000175, text = "¡Mucho gusto!", romaji = "mˈucho gˈusto", correct = true)
                option(id = 1000176, text = "Buenos días", romaji = "bˈuenos dˈias", correct = false)
                option(id = 1000177, text = "Adiós", correct = false)
            }
            challenge(id = 100028, lessonId = 103, type = ChallengeType.ASSIST, question = "Yo ___ un niño", grammaticalFocus = "es.ser_present", ruleText = "ser (to be, identity): yo soy = I am. Use soy with yo.", orderIndex = 5) {
                option(id = 1000178, text = "soy", correct = true)
                option(id = 1000179, text = "es", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1000180, text = "son", correct = false, errorTag = "WRONG_PERSON")
            }
            challenge(id = 100029, lessonId = 103, type = ChallengeType.WORD_BANK, question = "Assemble: 'I am a boy'", grammaticalFocus = "es.ser_present", ruleText = "ser (to be, identity): yo soy = I am. Use soy with yo.", orderIndex = 6) {
                option(id = 1000181, text = "Yo", correct = true)
                option(id = 1000182, text = "soy", correct = true)
                option(id = 1000183, text = "un", correct = true)
                option(id = 1000184, text = "niño", correct = true)
                option(id = 1000185, text = "son", correct = false, errorTag = "WRONG_PERSON")
                option(id = 1000186, text = "estoy", correct = false, errorTag = "WRONG_COPULA")
            }
            challenge(id = 100030, lessonId = 103, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/yo_soy_un_nino.ogg", grammaticalFocus = "es.ser_present", ruleText = "ser (to be, identity): yo soy = I am. Use soy with yo.", orderIndex = 7) {
                option(id = 1000187, text = "Yo soy un niño", romaji = "yˈo sˈoi un nˈiño", correct = true)
                option(id = 1000188, text = "Yo estoy un niño", correct = false, errorTag = "WRONG_COPULA")
                option(id = 1000189, text = "Tú eres un niño", correct = false)
            }
        },
    )

    val japaneseBaseUnits: List<UnitPayload> = listOf(
        unit(id = 20, courseId = 2, title = "Unit 1: Hiragana & Greetings", description = "Learn essential Japanese sounds, daily greetings, and numbers", orderIndex = 0) {
            lesson(id = 200, title = "Lesson 1: Vowels あ–お", orderIndex = 0)
            challenge(id = 2001, lessonId = 200, type = ChallengeType.INTRO, question = "あ", orderIndex = 0) {
                option(id = 200000, text = "あ", romaji = "a", correct = true)
            }
            challenge(id = 2002, lessonId = 200, type = ChallengeType.INTRO, question = "い", orderIndex = 1) {
                option(id = 200001, text = "い", romaji = "i", correct = true)
            }
            challenge(id = 20000, lessonId = 200, type = ChallengeType.INTRO, question = "う", orderIndex = 2) {
                option(id = 200002, text = "う", romaji = "u", correct = true)
            }
            challenge(id = 20001, lessonId = 200, type = ChallengeType.INTRO, question = "え", orderIndex = 3) {
                option(id = 200003, text = "え", romaji = "e", correct = true)
            }
            challenge(id = 20002, lessonId = 200, type = ChallengeType.INTRO, question = "お", orderIndex = 4) {
                option(id = 200004, text = "お", romaji = "o", correct = true)
            }
            challenge(id = 20003, lessonId = 200, type = ChallengeType.SELECT, question = "Which kana is 'a'?", orderIndex = 5) {
                option(id = 200005, text = "あ", romaji = "a", correct = true)
                option(id = 200006, text = "い", romaji = "i", correct = false)
                option(id = 200007, text = "う", romaji = "u", correct = false)
            }
            challenge(id = 20004, lessonId = 200, type = ChallengeType.SELECT, question = "Which kana is 'u'?", orderIndex = 6) {
                option(id = 200008, text = "う", romaji = "u", correct = true)
                option(id = 200009, text = "え", romaji = "e", correct = false)
                option(id = 200010, text = "お", romaji = "o", correct = false)
            }
            challenge(id = 20005, lessonId = 200, type = ChallengeType.MATCH_PAIRS, question = "Match each kana to its sound", orderIndex = 7) {
                option(id = 200011, text = "あ", romaji = "a", correct = true)
                option(id = 200012, text = "a", correct = true)
                option(id = 200013, text = "い", romaji = "i", correct = true)
                option(id = 200014, text = "i", correct = true)
                option(id = 200015, text = "う", romaji = "u", correct = true)
                option(id = 200016, text = "u", correct = true)
                option(id = 200017, text = "え", romaji = "e", correct = true)
                option(id = 200018, text = "e", correct = true)
                option(id = 200019, text = "お", romaji = "o", correct = true)
                option(id = 200020, text = "o", correct = true)
            }
            challenge(id = 20006, lessonId = 200, type = ChallengeType.LISTEN, question = "Tap what you hear", orderIndex = 8) {
                option(id = 200021, text = "あ", romaji = "a", correct = true)
                option(id = 200022, text = "い", romaji = "i", correct = false)
                option(id = 200023, text = "う", romaji = "u", correct = false)
            }
            lesson(id = 201, title = "Lesson 2: Consonants か & さ Rows", orderIndex = 1)
            challenge(id = 20007, lessonId = 201, type = ChallengeType.INTRO, question = "か", orderIndex = 0) {
                option(id = 200024, text = "か", romaji = "ka", correct = true)
            }
            challenge(id = 20008, lessonId = 201, type = ChallengeType.INTRO, question = "き", orderIndex = 1) {
                option(id = 200025, text = "き", romaji = "ki", correct = true)
            }
            challenge(id = 20009, lessonId = 201, type = ChallengeType.INTRO, question = "く", orderIndex = 2) {
                option(id = 200026, text = "く", romaji = "ku", correct = true)
            }
            challenge(id = 20010, lessonId = 201, type = ChallengeType.INTRO, question = "け", orderIndex = 3) {
                option(id = 200027, text = "け", romaji = "ke", correct = true)
            }
            challenge(id = 20011, lessonId = 201, type = ChallengeType.INTRO, question = "こ", orderIndex = 4) {
                option(id = 200028, text = "こ", romaji = "ko", correct = true)
            }
            challenge(id = 20012, lessonId = 201, type = ChallengeType.INTRO, question = "さ", orderIndex = 5) {
                option(id = 200029, text = "さ", romaji = "sa", correct = true)
            }
            challenge(id = 20013, lessonId = 201, type = ChallengeType.INTRO, question = "し", orderIndex = 6) {
                option(id = 200030, text = "し", romaji = "shi", correct = true)
            }
            challenge(id = 20014, lessonId = 201, type = ChallengeType.INTRO, question = "す", orderIndex = 7) {
                option(id = 200031, text = "す", romaji = "su", correct = true)
            }
            challenge(id = 20015, lessonId = 201, type = ChallengeType.INTRO, question = "せ", orderIndex = 8) {
                option(id = 200032, text = "せ", romaji = "se", correct = true)
            }
            challenge(id = 20016, lessonId = 201, type = ChallengeType.INTRO, question = "そ", orderIndex = 9) {
                option(id = 200033, text = "そ", romaji = "so", correct = true)
            }
            challenge(id = 20017, lessonId = 201, type = ChallengeType.SELECT, question = "Which kana is 'ka'?", orderIndex = 10) {
                option(id = 200034, text = "か", romaji = "ka", correct = true)
                option(id = 200035, text = "き", romaji = "ki", correct = false)
                option(id = 200036, text = "く", romaji = "ku", correct = false)
            }
            challenge(id = 20018, lessonId = 201, type = ChallengeType.SELECT, question = "Which kana is 'so'?", orderIndex = 11) {
                option(id = 200037, text = "そ", romaji = "so", correct = true)
                option(id = 200038, text = "さ", romaji = "sa", correct = false)
                option(id = 200039, text = "し", romaji = "shi", correct = false)
            }
            challenge(id = 20019, lessonId = 201, type = ChallengeType.MATCH_PAIRS, question = "Match each kana to its sound", orderIndex = 12) {
                option(id = 200040, text = "か", romaji = "ka", correct = true)
                option(id = 200041, text = "ka", correct = true)
                option(id = 200042, text = "き", romaji = "ki", correct = true)
                option(id = 200043, text = "ki", correct = true)
                option(id = 200044, text = "く", romaji = "ku", correct = true)
                option(id = 200045, text = "ku", correct = true)
                option(id = 200046, text = "け", romaji = "ke", correct = true)
                option(id = 200047, text = "ke", correct = true)
                option(id = 200048, text = "こ", romaji = "ko", correct = true)
                option(id = 200049, text = "ko", correct = true)
            }
            challenge(id = 20020, lessonId = 201, type = ChallengeType.LISTEN, question = "Tap what you hear", orderIndex = 13) {
                option(id = 200050, text = "か", romaji = "ka", correct = true)
                option(id = 200051, text = "き", romaji = "ki", correct = false)
                option(id = 200052, text = "さ", romaji = "sa", correct = false)
            }
            lesson(id = 20001, title = "Lesson 3: First Words", orderIndex = 2)
            challenge(id = 20021, lessonId = 20001, type = ChallengeType.INTRO, question = "あい — love", orderIndex = 0) {
                option(id = 200053, text = "あい", romaji = "ai", correct = true)
            }
            challenge(id = 20022, lessonId = 20001, type = ChallengeType.INTRO, question = "いえ — house", orderIndex = 1) {
                option(id = 200054, text = "いえ", romaji = "ie", correct = true)
            }
            challenge(id = 20023, lessonId = 20001, type = ChallengeType.INTRO, question = "うえ — up / above", orderIndex = 2) {
                option(id = 200055, text = "うえ", romaji = "ue", correct = true)
            }
            challenge(id = 20024, lessonId = 20001, type = ChallengeType.INTRO, question = "あお — blue", orderIndex = 3) {
                option(id = 200056, text = "あお", romaji = "ao", correct = true)
            }
            challenge(id = 20025, lessonId = 20001, type = ChallengeType.INTRO, question = "あさ — morning", orderIndex = 4) {
                option(id = 200057, text = "あさ", romaji = "asa", correct = true)
            }
            challenge(id = 20026, lessonId = 20001, type = ChallengeType.SELECT, question = "Which word means 'love'?", orderIndex = 5) {
                option(id = 200058, text = "あい", romaji = "ai", correct = true)
                option(id = 200059, text = "いえ", romaji = "ie", correct = false)
                option(id = 200060, text = "あお", romaji = "ao", correct = false)
            }
            challenge(id = 20027, lessonId = 20001, type = ChallengeType.SELECT, question = "Which word means 'morning'?", orderIndex = 6) {
                option(id = 200061, text = "あさ", romaji = "asa", correct = true)
                option(id = 200062, text = "うえ", romaji = "ue", correct = false)
                option(id = 200063, text = "あい", romaji = "ai", correct = false)
            }
            challenge(id = 20028, lessonId = 20001, type = ChallengeType.WORD_BANK, question = "Assemble: 'house'", orderIndex = 7) {
                option(id = 200064, text = "い", romaji = "i", correct = true)
                option(id = 200065, text = "え", romaji = "e", correct = true)
                option(id = 200066, text = "あ", romaji = "a", correct = false)
                option(id = 200067, text = "う", romaji = "u", correct = false)
            }
            challenge(id = 20029, lessonId = 20001, type = ChallengeType.LISTEN, question = "Tap what you hear", orderIndex = 8) {
                option(id = 200068, text = "あい", romaji = "ai", correct = true)
                option(id = 200069, text = "あお", romaji = "ao", correct = false)
                option(id = 200070, text = "あさ", romaji = "asa", correct = false)
            }
            lesson(id = 20002, title = "Lesson 4: Daily Greetings", orderIndex = 3)
            challenge(id = 20030, lessonId = 20002, type = ChallengeType.INTRO, question = "こんにちは — Hello", audioSrc = "asset:///audio/ja/konnichiwa.ogg", orderIndex = 0) {
                option(id = 200071, text = "こんにちは", romaji = "Konnichiwa", correct = true, audioSrc = "asset:///audio/ja/konnichiwa.ogg")
            }
            challenge(id = 20031, lessonId = 20002, type = ChallengeType.INTRO, question = "ありがとう — Thank you", audioSrc = "asset:///audio/ja/arigatou.ogg", orderIndex = 1) {
                option(id = 200072, text = "ありがとう", romaji = "Arigatou", correct = true, audioSrc = "asset:///audio/ja/arigatou.ogg")
            }
            challenge(id = 20032, lessonId = 20002, type = ChallengeType.INTRO, question = "はい — Yes", audioSrc = "asset:///audio/ja/hai.ogg", orderIndex = 2) {
                option(id = 200073, text = "はい", romaji = "Hai", correct = true, audioSrc = "asset:///audio/ja/hai.ogg")
            }
            challenge(id = 20033, lessonId = 20002, type = ChallengeType.SELECT, question = "Which one means 'Hello'?", audioSrc = "asset:///audio/ja/konnichiwa.ogg", orderIndex = 3) {
                option(id = 200074, text = "こんにちは", romaji = "Konnichiwa", correct = true, audioSrc = "asset:///audio/ja/konnichiwa.ogg")
                option(id = 200075, text = "ありがとう", romaji = "Arigatou", correct = false)
                option(id = 200076, text = "はい", romaji = "Hai", correct = false)
            }
            challenge(id = 20034, lessonId = 20002, type = ChallengeType.SELECT, question = "How do you say 'Thank you'?", audioSrc = "asset:///audio/ja/arigatou.ogg", orderIndex = 4) {
                option(id = 200077, text = "ありがとう", romaji = "Arigatou", correct = true, audioSrc = "asset:///audio/ja/arigatou.ogg")
                option(id = 200078, text = "はい", romaji = "Hai", correct = false)
                option(id = 200079, text = "こんにちは", romaji = "Konnichiwa", correct = false)
            }
            challenge(id = 20035, lessonId = 20002, type = ChallengeType.ASSIST, question = "Complete the greeting: 'Thank you' = ___", orderIndex = 5) {
                option(id = 200080, text = "ありがとう", romaji = "Arigatou", correct = true)
                option(id = 200081, text = "こんにちは", romaji = "Konnichiwa", correct = false)
                option(id = 200082, text = "はい", romaji = "Hai", correct = false)
            }
            challenge(id = 20036, lessonId = 20002, type = ChallengeType.WORD_BANK, question = "Assemble: 'Yes, thank you'", orderIndex = 6) {
                option(id = 200083, text = "はい", romaji = "hai", correct = true)
                option(id = 200084, text = "ありがとう", romaji = "arigatou", correct = true)
                option(id = 200085, text = "こんにちは", romaji = "konnichiwa", correct = false)
                option(id = 200086, text = "あい", romaji = "ai", correct = false)
            }
            challenge(id = 20037, lessonId = 20002, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/konnichiwa.ogg", orderIndex = 7) {
                option(id = 200087, text = "こんにちは", romaji = "Konnichiwa", correct = true)
                option(id = 200088, text = "ありがとう", romaji = "Arigatou", correct = false)
                option(id = 200089, text = "はい", romaji = "Hai", correct = false)
            }
            lesson(id = 20003, title = "Lesson 5: Numbers 1–3", orderIndex = 4)
            challenge(id = 20038, lessonId = 20003, type = ChallengeType.INTRO, question = "いち (一) — One (1)", orderIndex = 0) {
                option(id = 200090, text = "いち", romaji = "ichi", correct = true)
            }
            challenge(id = 20039, lessonId = 20003, type = ChallengeType.INTRO, question = "に (二) — Two (2)", orderIndex = 1) {
                option(id = 200091, text = "に", romaji = "ni", correct = true)
            }
            challenge(id = 20040, lessonId = 20003, type = ChallengeType.INTRO, question = "さん (三) — Three (3)", orderIndex = 2) {
                option(id = 200092, text = "さん", romaji = "san", correct = true)
            }
            challenge(id = 20041, lessonId = 20003, type = ChallengeType.SELECT, question = "Which kanji is 'One (1)'?", orderIndex = 3) {
                option(id = 200093, text = "一", romaji = "ichi", correct = true)
                option(id = 200094, text = "二", romaji = "ni", correct = false)
                option(id = 200095, text = "三", romaji = "san", correct = false)
            }
            challenge(id = 20042, lessonId = 20003, type = ChallengeType.SELECT, question = "Which kanji is 'Two (2)'?", orderIndex = 4) {
                option(id = 200096, text = "二", romaji = "ni", correct = true)
                option(id = 200097, text = "一", romaji = "ichi", correct = false)
                option(id = 200098, text = "三", romaji = "san", correct = false)
            }
            challenge(id = 20043, lessonId = 20003, type = ChallengeType.SELECT, question = "Which kanji is 'Three (3)'?", orderIndex = 5) {
                option(id = 200099, text = "三", romaji = "san", correct = true)
                option(id = 200100, text = "一", romaji = "ichi", correct = false)
                option(id = 200101, text = "二", romaji = "ni", correct = false)
            }
            challenge(id = 20044, lessonId = 20003, type = ChallengeType.WORD_BANK, question = "Assemble: 'One, two, three'", orderIndex = 6) {
                option(id = 200102, text = "いち", romaji = "ichi", correct = true)
                option(id = 200103, text = "に", romaji = "ni", correct = true)
                option(id = 200104, text = "さん", romaji = "san", correct = true)
                option(id = 200105, text = "あい", romaji = "ai", correct = false)
                option(id = 200106, text = "あさ", romaji = "asa", correct = false)
            }
            challenge(id = 20045, lessonId = 20003, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/ichi_ni_san.ogg", orderIndex = 7) {
                option(id = 200107, text = "いち に さん", romaji = "ichi ni san", correct = true)
                option(id = 200108, text = "いち に あさ", romaji = "ichi ni asa", correct = false)
                option(id = 200109, text = "あい に さん", romaji = "ai ni san", correct = false)
            }
        },
        unit(id = 21, courseId = 2, title = "Unit 2: Daily Life & Food", description = "Common phrases for dining, polite interactions, and refreshments", orderIndex = 1) {
            lesson(id = 202, title = "Lesson 3: Eating & Drinking", orderIndex = 0)
            challenge(id = 2009, lessonId = 202, type = ChallengeType.SELECT, question = "What is 'Water' in Japanese?", audioSrc = "asset:///audio/ja/mizu.ogg", orderIndex = 0) {
                option(id = 20029, text = "お水", romaji = "Mizu", correct = true, audioSrc = "asset:///audio/ja/mizu.ogg")
                option(id = 20030, text = "お茶", romaji = "Ocha", correct = false, audioSrc = "asset:///audio/ja/ocha.ogg")
                option(id = 20031, text = "ごはん", romaji = "Gohan", correct = false)
            }
            challenge(id = 2010, lessonId = 202, type = ChallengeType.SELECT, question = "What is 'Green Tea'?", audioSrc = "asset:///audio/ja/ocha.ogg", orderIndex = 1) {
                option(id = 20032, text = "お茶", romaji = "Ocha", correct = true, audioSrc = "asset:///audio/ja/ocha.ogg")
                option(id = 20033, text = "お水", romaji = "Mizu", correct = false, audioSrc = "asset:///audio/ja/mizu.ogg")
                option(id = 20034, text = "パン", romaji = "Pan", correct = false)
            }
            challenge(id = 2011, lessonId = 202, type = ChallengeType.WORD_BANK, question = "Assemble: 'Water, please'", orderIndex = 2) {
                option(id = 20035, text = "お水", romaji = "omizu", correct = true, audioSrc = "asset:///audio/ja/mizu.ogg")
                option(id = 20036, text = "を", romaji = "o", correct = true)
                option(id = 20037, text = "ください", romaji = "kudasai", correct = true)
                option(id = 20038, text = "お茶", romaji = "ocha", correct = false)
                option(id = 20039, text = "はい", romaji = "hai", correct = false)
            }
            challenge(id = 2012, lessonId = 202, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/itadakimasu.ogg", orderIndex = 3) {
                option(id = 20040, text = "いただきます", romaji = "Itadakimasu", correct = true)
                option(id = 20041, text = "いただきません", romaji = "Itadakimasen", correct = false)
                option(id = 20042, text = "いだきます", romaji = "Idakimasu", correct = false)
            }
            lesson(id = 203, title = "Lesson 4: Politeness & Gratitude", orderIndex = 1)
            challenge(id = 2013, lessonId = 203, type = ChallengeType.SELECT, question = "How do you say 'Yes' in Japanese?", audioSrc = "asset:///audio/ja/hai.ogg", orderIndex = 0) {
                option(id = 20043, text = "はい", romaji = "Hai", correct = true, audioSrc = "asset:///audio/ja/hai.ogg")
                option(id = 20044, text = "いいえ", romaji = "Iie", correct = false)
                option(id = 20045, text = "さようなら", romaji = "Sayounara", correct = false)
            }
            challenge(id = 2014, lessonId = 203, type = ChallengeType.SELECT, question = "How do you say 'Excuse me / Sorry'?", audioSrc = "asset:///audio/ja/sumimasen.ogg", orderIndex = 1) {
                option(id = 20046, text = "すみません", romaji = "Sumimasen", correct = true, audioSrc = "asset:///audio/ja/sumimasen.ogg")
                option(id = 20047, text = "ありがとう", romaji = "Arigatou", correct = false, audioSrc = "asset:///audio/ja/arigatou.ogg")
                option(id = 20048, text = "おはよう", romaji = "Ohayou", correct = false)
            }
            challenge(id = 2015, lessonId = 203, type = ChallengeType.WORD_BANK, question = "Assemble: 'Yes, thank you'", orderIndex = 2, grammaticalFocus = "ja.polite_register", ruleText = "ございます is the polite form of あります. Use ある with friends and family, ございます in polite speech.\nSo the polite thank-you is ありがとうございます, never あります.") {
                option(id = 20049, text = "はい", romaji = "hai", correct = true, audioSrc = "asset:///audio/ja/hai.ogg")
                option(id = 20050, text = "ありがとう", romaji = "arigatou", correct = true, audioSrc = "asset:///audio/ja/arigatou.ogg")
                option(id = 20051, text = "ございます", romaji = "gozaimasu", correct = true)
                option(id = 20052, text = "あります", romaji = "arimasu", correct = false, errorTag = "WRONG_REGISTER")
                option(id = 20053, text = "すみません", romaji = "sumimasen", correct = false)
            }
            challenge(id = 2016, lessonId = 203, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/sumimasen.ogg", orderIndex = 3) {
                option(id = 20054, text = "すみません", romaji = "Sumimasen", correct = true)
                option(id = 20055, text = "すいせん", romaji = "Suinsen", correct = false)
                option(id = 20056, text = "みません", romaji = "Mimasen", correct = false)
            }
        },
    )
}
