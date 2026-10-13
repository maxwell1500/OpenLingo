package com.openlingo.app

import com.openlingo.app.data.local.curriculum.AdvancedCurriculumData
import com.openlingo.app.data.local.curriculum.B1CurriculumData
import com.openlingo.app.data.local.curriculum.ExpandedCurriculumData
import com.openlingo.app.data.local.curriculum.JapaneseN4CurriculumData
import com.openlingo.app.data.local.curriculum.UnitPayload
import java.io.File
import org.junit.Assert.assertTrue

/**
 * The bundled curriculum, in one place.
 *
 * Every corpus-walking test used to re-list the eight payload properties itself.
 * Adding or dropping a curriculum file meant editing five files, and forgetting
 * one left that file's tests silently skipping the new content — the exact
 * failure class the suite exists to hunt. One list, imported everywhere.
 */
object Corpus {
    val allPayloads: List<UnitPayload> =
        ExpandedCurriculumData.spanishExpandedUnits +
            ExpandedCurriculumData.japaneseExpandedUnits +
            AdvancedCurriculumData.spanishAdvancedUnits +
            AdvancedCurriculumData.japaneseAdvancedUnits +
            B1CurriculumData.spanishA2Units +
            B1CurriculumData.spanishB1Units +
            B1CurriculumData.japaneseN4Units +
            JapaneseN4CurriculumData.japaneseN4ExtensionUnits
}

/**
 * The audio files the APK actually bundles, in one place.
 *
 * Walking `src/main/assets/audio` was copy-pasted into four tests; a new
 * audio-referencing surface had to re-add the check by hand. Walked once, lazily.
 */
object AudioAssets {
    val available: Set<String> by lazy {
        File("src/main/assets/audio")
            .walkTopDown()
            .filter { it.isFile }
            .map { it.name }
            .toSet()
    }

    /** Fails on any referenced clip whose file name is not in the bundle. */
    fun assertAllExist(referenced: List<String>) {
        assertTrue("no audio referenced at all", referenced.isNotEmpty())
        referenced.forEach { src ->
            val file = src.substringAfterLast("/")
            assertTrue("missing audio asset for $src", file in available)
        }
    }
}
