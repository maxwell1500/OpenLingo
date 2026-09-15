package com.duo.app.feedback

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Tiny haptic vocabulary for dopamine feedback. minSdk 26 → VibrationEffect is always available. */
object Haptics {

    private fun vibrator(context: Context): Vibrator? {
        return try {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
                ?: @Suppress("DEPRECATION") context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } catch (e: Exception) {
            null
        }
    }

    private fun buzz(context: Context, effect: VibrationEffect) {
        try {
            vibrator(context)?.takeIf { it.hasVibrator() }?.vibrate(effect)
        } catch (e: Exception) {
            // Haptics are garnish — never crash for a buzz.
        }
    }
    fun tick(context: Context) =
        buzz(context, VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))

    /** Correct answer: rising double-tap. */
    fun correct(context: Context) = buzz(
        context,
        VibrationEffect.createWaveform(longArrayOf(0, 30, 60, 50), intArrayOf(0, 180, 0, 255), -1),
    )

    /** Wrong answer: low double-thud. */
    fun incorrect(context: Context) = buzz(
        context,
        VibrationEffect.createWaveform(longArrayOf(0, 80, 60, 120), intArrayOf(0, 200, 0, 255), -1),
    )

    /** Big moments: lesson complete, character mastered, streak extended. */
    fun celebrate(context: Context) = buzz(
        context,
        VibrationEffect.createWaveform(
            longArrayOf(0, 40, 70, 40, 70, 40, 70, 120),
            intArrayOf(0, 200, 0, 220, 0, 240, 0, 255),
            -1,
        ),
    )
}
