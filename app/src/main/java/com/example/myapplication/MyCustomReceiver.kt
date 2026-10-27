package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Custom BroadcastReceiver class.
 *
 * It listens for the custom action `ACTION_CUSTOM_BROADCAST`.
 * When triggered, it extracts the message payload passed inside the Intent
 * and passes it to the `onMessageReceived` callback function.
 */
class MyCustomReceiver(
    private val onMessageReceived: (String) -> Unit,
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_CUSTOM_BROADCAST) {
            val message = intent.getStringExtra(EXTRA_BROADCAST_MESSAGE) ?: "No message received"
            onMessageReceived(message)
        }
    }

    companion object {
        const val ACTION_CUSTOM_BROADCAST = "com.example.myapplication.ACTION_CUSTOM_BROADCAST"
        const val EXTRA_BROADCAST_MESSAGE = "extra_broadcast_message"
    }
}