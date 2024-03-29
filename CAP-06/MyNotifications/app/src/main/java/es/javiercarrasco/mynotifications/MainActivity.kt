package es.javiercarrasco.mynotifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import es.javiercarrasco.mynotifications.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val CHANNELID1 = "es.javiercarrasco.mynotifications"
    private val notificationId1 = 123456

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Se crea el intent que deberá abrirse al pulsarse la notificación.
        val intent = Intent(this, RequestNotification::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // Se crea una notificación utilizando el builder de NotificationCompat.
        val builder = NotificationCompat.Builder(this, CHANNELID1).apply {
            setSmallIcon(R.drawable.notificacion)
            setContentTitle("Mi primera notificación")
            setContentText("Esta será la primera notificación creada.")
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }

        binding.btnFirstNotification.setOnClickListener {
            // Se registra el canal de notificación en el sistema.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                createNotificationChannel(
                    CHANNELID1,
                    R.string.txt_channel1_name,
                    R.string.txt_channel1_desc,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            }

            with(NotificationManagerCompat.from(this)) {
                notify(notificationId1, builder.build())
                vibrate()
            }

            val mediaPlayer = MediaPlayer.create(this, R.raw.mario)
            mediaPlayer.start()
        }
    }

    private fun createNotificationChannel(
        channel: String, name: Int,
        desc: Int, importance: Int
    ) {
        // Se crea el canal de notificación únicamente para API 26+.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(name)
            val descriptionText = getString(desc)
            val channel = NotificationChannel(channel, name, importance).apply {
                description = descriptionText
            }
            // Se registra el canal en el sistema.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun vibrate() {
        // Se instancia el objeto Vibrator.
        val vibrator: Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager.defaultVibrator
        } else {
            vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        }


        // Se comprueba la existencia de vibrador.
        if (!vibrator.hasVibrator()) {
            Toast.makeText(this, "No tienes vibrador!!", Toast.LENGTH_SHORT).show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect =
                    VibrationEffect.createOneShot(
                        1000,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                vibrator.vibrate(vibrationEffect)
            } else {
                vibrator.vibrate(1000) // Deprecated API 26
                Toast.makeText(this, "Deprecated version", Toast.LENGTH_SHORT).show()
            }
        }
    }
}