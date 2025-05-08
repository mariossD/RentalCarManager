package com.example.rentalcarmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.rentalcarmanager.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Apply dynamic theming using Material You (available from Android 12+)
    com.google.android.material.color.DynamicColors.applyToActivitiesIfAvailable(application)

    // Create notification channel for showing rental alerts
    createNotificationChannel()

    // Request notification permission (required on Android 13+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
      ) {
        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
      }
    }

    // Initialize Room database and repositories
    val db = com.example.rentalcarmanager.data.local.DatabaseProvider.getDatabase(applicationContext)
    com.example.rentalcarmanager.data.local.repository.CarsRepo.init(db.daoCars())
    com.example.rentalcarmanager.data.local.repository.CustomersRepo.init(db.daoCustomers())
    com.example.rentalcarmanager.data.local.repository.BranchesRepo.init(db.daoBranches())

    // Set up ViewBinding and set the main content view
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setSupportActionBar(binding.appBarMain.toolbar)

    // Set up Navigation Drawer and NavController
    val drawerLayout: DrawerLayout = binding.drawerLayout
    val navView: NavigationView = binding.navView
    val navController = findNavController(R.id.nav_host_fragment_content_main)

    appBarConfiguration = AppBarConfiguration(
      setOf(
        R.id.nav_cars,
        R.id.nav_customers,
        R.id.nav_rentals,
        R.id.nav_branches,
        R.id.nav_queries,
        R.id.nav_home
      ), drawerLayout
    )

    setupActionBarWithNavController(navController, appBarConfiguration)
    navView.setupWithNavController(navController)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the top-right menu (Settings icon)
    menuInflater.inflate(R.menu.main, menu)
    return true
  }

  // Handle toolbar menu item selections
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_settings -> {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.navigate(R.id.nav_settings)
        true
      }

      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onSupportNavigateUp(): Boolean {
    // Handle navigation "up" button
    val navController = findNavController(R.id.nav_host_fragment_content_main)
    return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
  }

  // Create notification channel (required for Android 8+)
  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        "rentals_channel",
        "Rentals Notifications",
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Notifies when a new rental is created"
      }

      val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      manager.createNotificationChannel(channel)
    }
  }
}
