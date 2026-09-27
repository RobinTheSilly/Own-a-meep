package com.example.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.math.sin

class MeepVoiceManager(context: Context) {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setPitch(1.95f) // Cute ultra-high squeaky voice
                tts?.setSpeechRate(1.22f)
                isTtsReady = true
            }
        }
    }

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        playMeepChirp(frequency = 1050f, durationMs = 80)
        if (isTtsReady && tts != null) {
            tts?.setPitch(1.95f)
            tts?.setSpeechRate(1.22f)
            val utteranceId = "meep_${System.currentTimeMillis()}"
            if (onComplete != null) {
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(id: String?) {}
                    override fun onDone(id: String?) {
                        if (id == utteranceId) onComplete()
                    }
                    override fun onError(id: String?) {
                        if (id == utteranceId) onComplete()
                    }
                })
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } else {
            onComplete?.invoke()
        }
    }

    fun sayWelcome(meepName: String) {
        speak("Meep meep! Welcome back! I missed you so much, friend!")
    }

    fun sayGoodbye(onDone: () -> Unit) {
        speak("Meep meep! Goodbye friend, see you soon!") {
            onDone()
        }
    }

    fun playMeepChirp(frequency: Float = 1100f, durationMs: Int = 110) {
        thread {
            try {
                val sampleRate = 22050
                val numSamples = (durationMs * sampleRate) / 1000
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    // Cute chirp with slight pitch bend upward
                    val currentFreq = frequency + (i.toDouble() / numSamples) * 200.0
                    val angle = 2.0 * Math.PI * currentFreq * time
                    val envelope = sin((Math.PI * i) / numSamples) // Smooth fade in and fade out
                    buffer[i] = (sin(angle) * 30000 * envelope).toInt().toShort()
                }

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
                Thread.sleep(durationMs.toLong() + 30)
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                Log.d("MeepVoice", "AudioTrack chirp playback skipped: ${e.message}")
            }
        }
    }

    fun playHappyFanfare() {
        thread {
            val notes = listOf(880f, 1100f, 1320f, 1760f)
            for (note in notes) {
                playMeepChirp(frequency = note, durationMs = 90)
                Thread.sleep(100)
            }
        }
    }

    fun playMunchSound() {
        thread {
            for (i in 0..2) {
                playMeepChirp(frequency = 750f + i * 80f, durationMs = 70)
                Thread.sleep(85)
            }
        }
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
    }
}
