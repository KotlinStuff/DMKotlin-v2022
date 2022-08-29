package es.javiercarrasco.mynotifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import es.javiercarrasco.mynotifications.databinding.ActivityRequestNotificationBinding

class RequestNotification : AppCompatActivity() {
    private lateinit var binding: ActivityRequestNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}