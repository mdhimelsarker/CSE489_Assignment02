package com.example.myapplication

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityCustomBroadcastBinding

/**
 * Third Activity in the Custom Broadcast Flow.
 *
 * It dynamically registers [MyCustomReceiver] using ContextCompat for safe, lifecycle-aware,
 * restricted app-level broadcasts. When the user taps 'Send Broadcast', an Intent is dispatched
 * via system broadcast. Upon reception by [MyCustomReceiver], the received message is updated on screen.
 */
class CustomBroadcastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomBroadcastBinding
    private var passedMessage: String = ""

    private val customReceiver = MyCustomReceiver { receivedMessage ->
        binding.tvReceivedMessage.text = receivedMessage
        binding.tvReceivedMessage.setTypeface(null, Typeface.BOLD)
        binding.tvReceivedMessage.setTextColor(ContextCompat.getColor(this, R.color.primary))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomBroadcastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        passedMessage = intent.getStringExtra(EXTRA_PASSED_MESSAGE) ?: "Default Message"
        binding.tvEnteredMessage.text = passedMessage

        binding.btnSendBroadcast.setOnClickListener {
            val broadcastIntent = Intent(MyCustomReceiver.ACTION_CUSTOM_BROADCAST).apply {
                putExtra(MyCustomReceiver.EXTRA_BROADCAST_MESSAGE, passedMessage)
                setPackage(packageName)
            }
            sendBroadcast(broadcastIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(MyCustomReceiver.ACTION_CUSTOM_BROADCAST)
        ContextCompat.registerReceiver(
            this,
            customReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(customReceiver)
    }

    companion object {
        const val EXTRA_PASSED_MESSAGE = "extra_passed_message"
    }
}