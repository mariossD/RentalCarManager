package com.example.rentalcarmanager

import android.os.Bundle
import android.view.Menu
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
    val db = com.example.rentalcarmanager.data.local.DatabaseProvider.getDatabase(applicationContext)
    com.example.rentalcarmanager.data.local.repository.CarsRepo.init(db.daoCars())
    com.example.rentalcarmanager.data.local.repository.CustomersRepo.init(db.daoCustomers())
    com.example.rentalcarmanager.data.local.repository.BranchesRepo.init(db.daoBranches())

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setSupportActionBar(binding.appBarMain.toolbar)

    val drawerLayout: DrawerLayout = binding.drawerLayout
    val navView: NavigationView = binding.navView
    val navController = findNavController(R.id.nav_host_fragment_content_main)

    appBarConfiguration = AppBarConfiguration(
      setOf(
        R.id.nav_cars, R.id.nav_customers, R.id.nav_rentals,R.id.nav_branches,R.id.nav_queries,R.id.nav_home
      ), drawerLayout
    )
    setupActionBarWithNavController(navController, appBarConfiguration)
    navView.setupWithNavController(navController)

  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main, menu)
    return true
  }

  override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment_content_main)
    return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
  }


}