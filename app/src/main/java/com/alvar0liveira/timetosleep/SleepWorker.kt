package com.alvar0liveira.timetosleep

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters


class SleepWorker(
    private var context: Context,
    workerParameters: WorkerParameters
): Worker(context, workerParameters) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        stopAudio()
        context.cancelNotification()
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun stopAudio(){
        val am: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(attributes)
            .setOnAudioFocusChangeListener {}.build()

        val currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)

        am.requestAudioFocus(focusRequest)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
        am.abandonAudioFocusRequest(focusRequest)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0)
    }
}