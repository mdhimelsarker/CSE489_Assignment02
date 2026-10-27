package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityBatteryReceiverBinding

/**
 * Activity that receives and displays system battery status notifications.
 *
 * NOTE FOR VIVA / EXPLANATION:
 * Intent.ACTION_BATTERY_CHANGED is a sticky system broadcast.
 * Sticky broadcasts are cached by the Android OS after being sent, allowing applications to:
 * 1. Query the current battery state immediately when registering a receiver without waiting
 *    for a state change event.
 * 2. Receive live updates as the battery level or charging state changes.
 *
 * For API level compatibility (Android 14+ / API 34+), system-wide broadcasts like
 * ACTION_BATTERY_CHANGED must be registered using ContextCompat.RECEIVER_EXPORTED flag.
 */
class BatteryReceiverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBatteryReceiverBinding

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { updateBatteryStatus(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBatteryReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val stickyIntent = ContextCompat.registerReceiver(
            this,
            batteryReceiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED,
        )

        stickyIntent?.let { updateBatteryStatus(it) }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(batteryReceiver)
    }

    private fun updateBatteryStatus(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        if ((level != -1) && (scale != -1)) {
            val batteryPercentage = ((level * 100) / scale.toFloat()).toInt()
            binding.tvBatteryPercentage.text = getString(R.string.battery_percentage_format, batteryPercentage)

            val isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING) ||
                    (status == BatteryManager.BATTERY_STATUS_FULL)

            val statusText = if (isCharging) {
                "Status: Charging ($level / $scale)"
            } else {
                "Status: Discharging ($level / $scale)"
            }
            binding.tvBatteryDetails.text = statusText
        } else {
            binding.tvBatteryPercentage.text = getString(R.string.battery_unknown)
            binding.tvBatteryDetails.text = getString(R.string.battery_unknown)
        }
    }
}