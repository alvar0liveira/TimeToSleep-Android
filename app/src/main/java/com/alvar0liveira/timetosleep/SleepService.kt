package com.alvar0liveira.timetosleep

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.suspendCoroutine

private const val CHANNEL_ID = "Time_To_Sleep_Channel"
private const val NOTIFICATION_ID = 1
class SleepService: Service() {

    private lateinit var job: Job
    private var coroutineScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        buildNotification(context = applicationContext)
        intent?.getStringExtra("minutes")?.toLong()?.let {
            val milliseconds =  it * 60000
            job = coroutineScope.launch {
                delay(milliseconds)
                stopAudio()
            }
        }
        return START_STICKY
    }
    override fun onDestroy() {
        job.cancel()
        cancelNotification(context = applicationContext)
        super.onDestroy()
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun stopAudio(){
        val am: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(attributes)
            .setOnAudioFocusChangeListener {
            }.build()

        val currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)

        am.requestAudioFocus(focusRequest)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
        am.abandonAudioFocusRequest(focusRequest)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0)
    }

    private fun buildNotification(context: Context){
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_time_to_sleep)
            .setContentTitle("TimeToSleep")
            .setContentText("The timer is running")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun cancelNotification(context: Context){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}