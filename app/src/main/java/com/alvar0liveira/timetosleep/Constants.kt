package com.alvar0liveira.timetosleep

import android.app.NotificationManager
import android.content.Context

const val NOTIFICATION_CHANNEL_ID: String = "Time_To_Sleep_Channel"
const val NOTIFICATION_CHANNEL_NAME: String = "TimeToSleep"
const val NOTIFICATION_CHANNEL_DESCRIPTION: String = "A notification channel for TimeToSleep"
const val NOTIFICATION_ID: Int = 1
const val NOTIFICATION_TITLE: String = "TimeToSleep"
const val NOTIFICATION_TEXT: String = "The timer is running"
const val SLEEP_WORKER_TAG: String = "SLEEP_WORKER"

fun Context.cancelNotification(){
    val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(NOTIFICATION_ID)
}