package com.alvar0liveira.timetosleep

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
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
import com.alvar0liveira.timetosleep.ui.theme.TimeToSleepTheme

private const val CHANNEL_ID = "Time_To_Sleep_Channel"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askForStoppingBatteryOptimization()
        createNotificationChannel()
        setContent {
            var minutes by remember {
                mutableStateOf("60")
            }
            TimeToSleepTheme {
                // A surface container using the 'background' color from the theme
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
                                startService(Intent(this@MainActivity, SleepService::class.java).putExtra("minutes", minutes))
                                Toast.makeText(this@MainActivity, "Good Night!", Toast.LENGTH_LONG).show()
                            }

                            ActionButton("Stop") {
                                stopService(Intent(this@MainActivity, SleepService::class.java))
                                Toast.makeText(this@MainActivity, "Stopped the service", Toast.LENGTH_LONG).show()
                            }
                        }

                    }
                }
            }
        }
    }
    private fun askForStoppingBatteryOptimization(){
        val intent = Intent()
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)){
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TimeToSleep"
            val description = "A notification channel for TimeToSleep"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                this.description = description
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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
