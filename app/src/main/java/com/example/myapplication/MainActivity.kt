package com.example.myapplication

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentSelectedItemId: Int = R.id.nav_broadcast_receiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_broadcast_receiver -> {
                    selectFragment(
                        BroadcastReceiverFragment(),
                        getString(R.string.title_broadcast_receiver),
                        menuItem.itemId
                    )
                }
                R.id.nav_image_scale -> {
                    selectFragment(
                        ImageScaleFragment(),
                        getString(R.string.title_image_scale),
                        menuItem.itemId
                    )
                }
                R.id.nav_video -> {
                    selectFragment(
                        VideoFragment(),
                        getString(R.string.title_video),
                        menuItem.itemId
                    )
                }
                R.id.nav_audio -> {
                    selectFragment(
                        AudioFragment(),
                        getString(R.string.title_audio),
                        menuItem.itemId
                    )
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        if (savedInstanceState == null) {
            selectFragment(
                BroadcastReceiverFragment(),
                getString(R.string.title_broadcast_receiver),
                R.id.nav_broadcast_receiver
            )
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else if (currentSelectedItemId != R.id.nav_broadcast_receiver) {
                    selectFragment(
                        BroadcastReceiverFragment(),
                        getString(R.string.title_broadcast_receiver),
                        R.id.nav_broadcast_receiver
                    )
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun selectFragment(fragment: Fragment, title: String, itemId: Int) {
        currentSelectedItemId = itemId
        supportActionBar?.title = title
        binding.navView.setCheckedItem(itemId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}