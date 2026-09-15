package com.duo.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class AudioPlayer(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context).build()
    }

    /**
     * Plays a speech/voice audio clip from a URL, local file path, or asset URI.
     */
    @OptIn(UnstableApi::class)
    fun playVoice(audioSource: String, speed: Float = 1.0f) {
        if (audioSource.isBlank()) return
        // Fail fast on missing bundled assets: ExoPlayer reports these
        // asynchronously, so pre-check to avoid player-state churn.
        if (audioSource.startsWith("asset:///")) {
            val assetPath = audioSource.removePrefix("asset:///")
            val exists = runCatching {
                context.assets.open(assetPath).close()
                true
            }.getOrDefault(false)
            if (!exists) return
        }
        try {
            val uri = when {
                audioSource.startsWith("http://") ||
                audioSource.startsWith("https://") ||
                audioSource.startsWith("content://") ||
                audioSource.startsWith("file://") ||
                audioSource.startsWith("asset:///") -> android.net.Uri.parse(audioSource)
                else -> android.net.Uri.parse("file://$audioSource")
            }

            val mediaItem = MediaItem.fromUri(uri)
            exoPlayer.setPlaybackParameters(androidx.media3.common.PlaybackParameters(speed))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopVoice() {
        try {
            exoPlayer.stop()
        } catch (_: Exception) {}
    }

    /**
     * Plays an upbeat, 3-note Duolingo-style chime (C5 -> E5 -> G5) using zero-latency AudioTrack.
     */
    fun playCorrectSound() {
        scope.launch {
            try {
                val sampleRate = 44100
                val note1 = generateTone(523.25, 0.08, sampleRate, volume = 0.6)
                val note2 = generateTone(659.25, 0.08, sampleRate, volume = 0.65)
                val note3 = generateTone(783.99, 0.22, sampleRate, volume = 0.7)
                val combined = note1 + note2 + note3
                playPcm(combined, sampleRate)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Plays a low, 2-note incorrect buzz/thud (220 Hz -> 174 Hz) using zero-latency AudioTrack.
     */
    fun playIncorrectSound() {
        scope.launch {
            try {
                val sampleRate = 44100
                val tone1 = generateTone(220.0, 0.12, sampleRate, volume = 0.5)
                val tone2 = generateTone(174.6, 0.22, sampleRate, volume = 0.45)
                val combined = tone1 + tone2
                playPcm(combined, sampleRate)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Lesson-complete fanfare: triumphant C5 -> E5 -> G5 -> C6 (longer final note).
     */
    fun playFanfare() {
        scope.launch {
            try {
                val sampleRate = 44100
                val c5 = generateTone(523.25, 0.10, sampleRate, volume = 0.6)
                val e5 = generateTone(659.25, 0.10, sampleRate, volume = 0.65)
                val g5 = generateTone(783.99, 0.10, sampleRate, volume = 0.7)
                val c6 = generateTone(1046.50, 0.45, sampleRate, volume = 0.75)
                playPcm(c5 + e5 + g5 + c6, sampleRate)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Character-mastery sparkle: 5 quick ascending chimes.
     */
    fun playSparkle() {
        scope.launch {
            try {
                val sampleRate = 44100
                val notes = listOf(880.0, 987.77, 1174.66, 1318.51, 1567.98)
                    .map { generateTone(it, 0.07, sampleRate, volume = 0.55) }
                playPcm(notes.reduce { acc, tone -> acc + tone }, sampleRate)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun generateTone(freq: Double, durationSec: Double, sampleRate: Int, volume: Double = 0.6): ShortArray {
        val numSamples = (durationSec * sampleRate).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            // Apply linear envelope (fade in and fade out) to prevent audio clicks
            val envelope = when {
                i < 200 -> i.toDouble() / 200.0
                i > numSamples - 400 -> (numSamples - i).toDouble() / 400.0
                else -> 1.0
            }
            val sample = sin(2.0 * Math.PI * i * freq / sampleRate) * Short.MAX_VALUE * volume * envelope
            buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun playPcm(buffer: ShortArray, sampleRate: Int) {
        // AudioTrack construction/playback throws on devices with no audio
        // output (some emulators, broken HALs) — chimes must never crash.
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
        val durationMs = (buffer.size * 1000L) / sampleRate + 50
        Thread.sleep(durationMs)
            audioTrack.release()
        } catch (_: Exception) {
            // Chimes are garnish — never crash for a beep.
        }
    }

    fun release() {
        try {
            exoPlayer.release()
        } catch (_: Exception) {}
    }
}
