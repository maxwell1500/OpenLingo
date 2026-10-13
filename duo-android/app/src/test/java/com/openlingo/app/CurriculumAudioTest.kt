package com.openlingo.app

import com.openlingo.app.data.local.curriculum.UnitPayload
import com.openlingo.app.data.local.models.ChallengeType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Audio integrity: every referenced clip exists in the bundle, LISTEN
 * challenges carry a playable clip, and declared readings match the audio.
 */
class CurriculumAudioTest {

    private val allPayloads: List<UnitPayload> = Corpus.allPayloads

    private val auditedJapaneseReadings = listOf(
        Triple(6300086, "見られる", "mirareru"),
        Triple(6300121, "見られる", "mirareru"),
        Triple(6300285, "見られる", "mirareru"),
        Triple(6300584, "待たせる", "mataseru"),
        Triple(6300586, "飲ませる", "nomaseru"),
        Triple(6400001, "雨が降ったら、家にいてください。", "ame ga futtara, ie ni ite kudasai"),
        Triple(6400002, "雨が降ると、家にいてください。", "ame ga furuto, ie ni ite kudasai"),
        Triple(6400003, "雨が降るたら、家にいてください。", "ame ga furutara, ie ni ite kudasai"),
        Triple(6400004, "雨が降りましたら、家にいてください。", "ame ga furimashitara, ie ni ite kudasai"),
        Triple(6400013, "仕事が終わったら、帰りましょう。", "shigoto ga owattara, kaerimashou"),
        Triple(6400014, "仕事が変わるなら、帰りましょう。", "shigoto ga kawarunara, kaerimashou"),
        Triple(6400015, "仕事が終われば、帰りましょう。", "shigoto ga owareba, kaerimashou"),
        Triple(6400016, "仕事が終わって、帰りましょう。", "shigoto ga owatte, kaerimashou"),
        Triple(6400065, "社長がお昼を召し上がります。", "shachou ga ohiru o meshimasu"),
        Triple(6400066, "お昼をいただきます。", "ohiru o itadakimasu"),
        Triple(6400067, "社長がお昼を食べます。", "shachou ga ohiru o tabemasu"),
        Triple(6400068, "社長がお昼を食べなさいます。", "shachou ga ohiru o tabenasaimasu"),
        Triple(6400069, "部長が契約書を お読みになります。", "bucho ga keiyakusho o oyomi ni narimasu"),
        Triple(6400071, "部長が契約書をお読みます。", "bucho ga keiyakusho o oyomimasu"),
        Triple(6400076, "ご覧になる", "goran ni naru"),
        Triple(6400109, "私はその映画を見られます。", "watashi wa sono eiga o miraremasu"),
        Triple(6500061, "定休日", "teikyuubi"),
        Triple(6500072, "現金で払わなければなりません。", "genkin de harawanakereba narimasen"),
        Triple(6500074, "現金で払わなくてもいいです。", "genkin de harawanakutemo ii desu"),
        Triple(6500075, "現金で払わなかった。", "genkin de harawanakatta"),
        Triple(6500113, "領収書", "ryoushuusho"),
        Triple(6500153, "領収書", "ryoushuusho"),
        Triple(6500172, "領収書", "ryoushuusho"),
        Triple(7500013, "停留所", "teiryuujyo"),
        Triple(7500053, "停留所", "teiryuujyo"),
        Triple(7500073, "停留所", "teiryuujyo"),
        Triple(7500220, "今日は風が強いです。", "kyou wa kaze ga tsuyoi desu"),
        Triple(7500221, "今日は風が強くです。", "kyou wa kaze ga tsuyoku desu"),
        Triple(7500222, "今日は風が強いでした。", "kyou wa kaze ga tsuyoi deshita"),
        Triple(20102, "千円", "Sennen"),
        Triple(20104, "千円", "Sennen"),
    )

    @Test
    fun `every referenced audio file exists in assets`() {
        val referenced = allPayloads.flatMap { it.challenges }.mapNotNull { it.audioSrc } +
            allPayloads.flatMap { it.options }.mapNotNull { it.audioSrc }
        AudioAssets.assertAllExist(referenced)
    }

    @Test
    fun `every LISTEN challenge carries a clip that exists in assets`() {
        val available = AudioAssets.available
        val dead = allPayloads.flatMap { it.challenges }
            .filter { it.type == ChallengeType.LISTEN }
            .mapNotNull { challenge ->
                val src = challenge.audioSrc
                when {
                    src.isNullOrBlank() ->
                        "LISTEN challenge ${challenge.id} has no audioSrc, so the learner is told to tap what they hear and hears nothing"
                    src.substringAfterLast("/") !in available ->
                        "LISTEN challenge ${challenge.id} points at $src, which is not in assets"
                    else -> null
                }
            }
        assertEquals(
            "LISTEN challenges with no playable clip: $dead",
            emptyList<String>(),
            dead,
        )
    }

    @Test
    fun `a challenge with multiple correct options carries no challenge-level audio`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        val failures = mutableListOf<String>()

        allPayloads.flatMap { it.challenges }.forEach { challenge ->
            if (challenge.type == ChallengeType.WORD_BANK) return@forEach
            if (challenge.audioSrc == null) return@forEach
            val correctCount = optionsByChallenge[challenge.id].orEmpty().count { it.correct }
            if (correctCount > 1) {
                failures += "challenge ${challenge.id} (${challenge.type.rawValue}) has $correctCount correct options " +
                    "but carries audioSrc '${challenge.audioSrc}', which speaks one form as the answer"
            }
        }

        assertEquals(
            "multi-answer challenges carrying challenge-level audio: $failures",
            emptyList<String>(),
            failures,
        )
    }

    @Test
    fun `a japanese reading that the audio pronounces differently is declared as the audio says it`() {
        val optionsById = allPayloads.flatMap { it.options }.associateBy { it.id }
        val drifted = auditedJapaneseReadings.mapNotNull { (id, text, romaji) ->
            val option = optionsById[id] ?: return@mapNotNull "option $id is gone"
            if (option.text == text && option.romaji == romaji) {
                null
            } else {
                "option $id reads ${option.text} as '${option.romaji}', " +
                    "expected $text as '$romaji'"
            }
        }
        assertEquals(
            "declared readings that contradict the shipped audio",
            emptyList<String>(),
            drifted,
        )
    }

    @Test
    fun `the option that plays an audited clip declares the reading that clip pronounces`() {
        val audited = auditedJapaneseReadings
            .mapNotNull { (id, _, romaji) ->
                allPayloads.flatMap { it.options }
                    .firstOrNull { it.id == id && it.audioSrc != null }
                    ?.let { it.audioSrc!! to romaji }
            }
            .toMap()
        assertTrue("no audited clip-carrying option was found", audited.isNotEmpty())

        val optionsByAudio = allPayloads.flatMap { it.options }
            .filter { it.audioSrc != null }
            .groupBy { it.audioSrc }
        val mismatched = audited.filter { (audio, romaji) ->
            val declared = optionsByAudio[audio].orEmpty().mapNotNull { it.romaji }.distinct()
            declared.isNotEmpty() && romaji !in declared
        }
        assertEquals(
            "clips whose declaring option no longer states the audited reading",
            emptyMap<String, String>(),
            mismatched.toMap(),
        )
    }

}
