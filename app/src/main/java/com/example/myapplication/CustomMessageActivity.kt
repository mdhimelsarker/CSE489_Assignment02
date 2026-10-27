package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityCustomMessageBinding

/**
 * Activity for entering a plain-text message.
 *
 * Validates that the input message is not empty, then passes it as an
 * explicit Intent extra to CustomBroadcastActivity.
 */
class CustomMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnNext.setOnClickListener {
            val messageText = binding.etMessage.text?.toString()?.trim().orEmpty()

            if (messageText.isEmpty()) {
                binding.tilMessage.error = getString(R.string.err_empty_message)
                Toast.makeText(this, getString(R.string.err_empty_message), Toast.LENGTH_SHORT).show()
            } else {
                binding.tilMessage.error = null
                val intent = Intent(this, CustomBroadcastActivity::class.java).apply {
                    putExtra(CustomBroadcastActivity.EXTRA_PASSED_MESSAGE, messageText)
                }
                startActivity(intent)
            }
        }
    }
}