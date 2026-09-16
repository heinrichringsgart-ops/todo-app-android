package com.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.todo.databinding.ActivityMainBinding

/**
 * Main Activity for the TODO application.
 * Serves as the host for navigation and fragment management.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the action bar
        setSupportActionBar(binding.toolbar)

        // Set up navigation
        setupNavigation()
    }

    /**
     * Set up navigation controller and action bar configuration.
     */
    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment)

        // Configure top-level destinations (those that don't show back button)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.taskListFragment)
        )

        // Connect the action bar with the navigation controller
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    /**
     * Handle navigation back button in the action bar.
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
