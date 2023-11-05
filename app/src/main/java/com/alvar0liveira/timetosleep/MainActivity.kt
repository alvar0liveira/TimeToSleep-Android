package com.alvar0liveira.timetosleep

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.alvar0liveira.timetosleep.ui.theme.TimeToSleepTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContent {
            var minutes by remember {
                mutableStateOf("60")
            }
            TimeToSleepTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 20.dp),
                            text = "TimeToSleep",
                            style = MaterialTheme.typography.h3
                        )
                        Column(
                            modifier = Modifier
                                .weight(2f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            OutlinedTextField(
                                value = minutes,
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                onValueChange = {
                                    minutes = it
                                },
                                label = {
                                    Text(
                                        text = "Minutes",
                                        style = MaterialTheme.typography.h6
                                    )
                                        },
                                modifier = Modifier.padding(8.dp).fillMaxWidth()
                            )

                            ActionButton("Start") {
                                enqueueLongWorker(minutes.toLongOrNull())
                                sendNotification(this@MainActivity)
                                Toast.makeText(this@MainActivity, "Good Night!", Toast.LENGTH_LONG).show()
                            }

                            ActionButton("Stop") {
                                this@MainActivity.cancelNotification()
                                cancelWorker()
                                Toast.makeText(this@MainActivity, "Stopped the service", Toast.LENGTH_LONG).show()
                            }
                        }

                    }
                }
            }
        }
    }

    private fun enqueueLongWorker(delay: Long?){
        delay?.let {
            val workManager: WorkManager = WorkManager
                .getInstance(this)
            val workRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<SleepWorker>()
                .setInitialDelay(it, TimeUnit.MINUTES)
                .addTag(SLEEP_WORKER_TAG)
                .build()
            workManager
                .enqueue(workRequest)
        }
    }

    private fun cancelWorker(){
        WorkManager
            .getInstance(this)
            .cancelAllWorkByTag(SLEEP_WORKER_TAG)
        this@MainActivity.cancelNotification()
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = NOTIFICATION_CHANNEL_NAME
            val description = NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                this.description = description
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(context: Context){
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_time_to_sleep)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(NOTIFICATION_TEXT)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}

@Composable
fun ActionButton(
    label: String = "",
    onAction: () -> Unit
) {
    Button(
        onClick = onAction,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.h6
        )
    }
}
