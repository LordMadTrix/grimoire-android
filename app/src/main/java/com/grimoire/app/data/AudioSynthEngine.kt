package com.grimoire.app.data

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

object AudioSynthEngine {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var ambientJob: Job? = null
    private var activeAmbientTrack: AudioTrack? = null
    var isPlayingAmbient = false
        private set

    fun playSfx(sfxType: String) {
        scope.launch {
            try {
                val sampleRate = 22050
                val durationMs = when (sfxType) {
                    "dice" -> 400
                    "sword" -> 350
                    "magic" -> 600
                    "monster" -> 500
                    "bell" -> 700
                    else -> 300
                }
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                when (sfxType) {
                    "dice" -> {
                        // Multi-click dice rattle
                        var clickPhase = 0
                        for (i in 0 until numSamples) {
                            val decay = (1.0 - i.toDouble() / numSamples)
                            val click = if (i % (sampleRate / 25) < 30) (Random.nextDouble(-1.0, 1.0) * 20000 * decay).toInt() else 0
                            val noise = (Random.nextDouble(-0.3, 0.3) * 6000 * decay).toInt()
                            buffer[i] = (click + noise).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                    "sword" -> {
                        // High metallic ping with fast decay + noise
                        for (i in 0 until numSamples) {
                            val t = i.toDouble() / sampleRate
                            val decay = Math.exp(-t * 12.0)
                            val ring1 = sin(2 * PI * 1850.0 * t)
                            val ring2 = sin(2 * PI * 2420.0 * t) * 0.7
                            val impact = if (i < 300) Random.nextDouble(-0.8, 0.8) else 0.0
                            val sample = ((ring1 + ring2 + impact) * 26000 * decay).toInt()
                            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                    "magic" -> {
                        // Ethereal rising chime arpeggio
                        for (i in 0 until numSamples) {
                            val t = i.toDouble() / sampleRate
                            val freq = 523.25 + 300.0 * (t / (durationMs / 1000.0))
                            val decay = Math.exp(-t * 3.5)
                            val chime = sin(2 * PI * freq * t) + 0.5 * sin(2 * PI * freq * 2.0 * t)
                            val sample = (chime * 20000 * decay).toInt()
                            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                    "monster" -> {
                        // Low aggressive growl
                        for (i in 0 until numSamples) {
                            val t = i.toDouble() / sampleRate
                            val freq = 90.0 + sin(2 * PI * 15.0 * t) * 25.0
                            val grit = Random.nextDouble(-0.4, 0.4)
                            val growl = sin(2 * PI * freq * t) + grit
                            val envelope = sin(PI * (i.toDouble() / numSamples))
                            val sample = (growl * 24000 * envelope).toInt()
                            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                    "bell" -> {
                        // Sacred chapel bell / heal chime
                        for (i in 0 until numSamples) {
                            val t = i.toDouble() / sampleRate
                            val decay = Math.exp(-t * 4.0)
                            val tone = sin(2 * PI * 880.0 * t) + 0.4 * sin(2 * PI * 1760.0 * t) + 0.2 * sin(2 * PI * 2640.0 * t)
                            val sample = (tone * 22000 * decay).toInt()
                            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_GAME)
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

                track.write(buffer, 0, buffer.size)
                track.play()
                delay(durationMs + 100L)
                track.release()
            } catch (_: Exception) {}
        }
    }

    fun startAmbientSynthesis(activeTrackNames: List<String>, masterVolume: Float = 0.7f) {
        stopAmbient()
        if (activeTrackNames.isEmpty()) return

        isPlayingAmbient = true
        ambientJob = scope.launch {
            try {
                val sampleRate = 16000
                val bufferSize = sampleRate / 4 // 250ms chunks
                val buffer = ShortArray(bufferSize)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize * 4)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                activeAmbientTrack = track
                track.play()

                var phase = 0.0
                val hasRain = activeTrackNames.any { it.contains("Pluie", ignoreCase = true) || it.contains("Tempête", ignoreCase = true) }
                val hasFire = activeTrackNames.any { it.contains("Feu", ignoreCase = true) || it.contains("Bivouac", ignoreCase = true) }
                val hasDungeon = activeTrackNames.any { it.contains("Donjon", ignoreCase = true) || it.contains("Crypte", ignoreCase = true) }
                val hasWind = activeTrackNames.any { it.contains("Vent", ignoreCase = true) }

                while (isActive) {
                    for (i in 0 until bufferSize) {
                        var sum = 0.0
                        if (hasRain) {
                            // Pink-ish filtered noise
                            sum += Random.nextDouble(-0.35, 0.35)
                        }
                        if (hasFire) {
                            // Crackling pops
                            val crackle = if (Random.nextDouble() < 0.008) Random.nextDouble(-0.8, 0.8) else 0.0
                            sum += crackle
                        }
                        if (hasDungeon) {
                            // Deep mysterious drone
                            phase += (2 * PI * 65.0) / sampleRate
                            sum += sin(phase) * 0.45 + sin(phase * 1.5) * 0.2
                        }
                        if (hasWind) {
                            phase += (2 * PI * 110.0) / sampleRate
                            val mod = (sin(phase * 0.05) + 1.0) * 0.5
                            sum += (Random.nextDouble(-0.3, 0.3) * mod)
                        }

                        val sample = (sum * 20000 * masterVolume).toInt()
                        buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }
                    track.write(buffer, 0, buffer.size)
                }
            } catch (_: Exception) {
            } finally {
                activeAmbientTrack?.stop()
                activeAmbientTrack?.release()
                activeAmbientTrack = null
                isPlayingAmbient = false
            }
        }
    }

    fun stopAmbient() {
        ambientJob?.cancel()
        ambientJob = null
        try {
            activeAmbientTrack?.stop()
            activeAmbientTrack?.release()
        } catch (_: Exception) {}
        activeAmbientTrack = null
        isPlayingAmbient = false
    }
}
