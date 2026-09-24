package com.printxpress.app.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.printxpress.app.R
import com.printxpress.app.databinding.ActivityMainBinding
import com.printxpress.app.ui.home.HomeFragment
import com.printxpress.app.ui.notifications.NotificationsFragment
import com.printxpress.app.ui.orders.OrdersFragment
import com.printxpress.app.ui.profile.ProfileFragment

/**
 * Single Activity hosting the four bottom-navigation destinations as
 * Fragments (Home, Orders, Notifications, Profile) - matching the four
 * icons shown in the Task C bottom nav wireframe. Fragments are swapped
 * with plain FragmentTransactions rather than the Navigation Component,
 * keeping the navigation graph explicit and easy to follow in one file.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val homeFragment by lazy { HomeFragment() }
    private val ordersFragment by lazy { OrdersFragment() }
    private val notificationsFragment by lazy { NotificationsFragment() }
    private val profileFragment by lazy { ProfileFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            showFragment(homeFragment)
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { showFragment(homeFragment); true }
                R.id.nav_orders -> { showFragment(ordersFragment); true }
                R.id.nav_notifications -> { showFragment(notificationsFragment); true }
                R.id.nav_profile -> { showFragment(profileFragment); true }
                else -> false
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
