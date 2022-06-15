package com.niiadotei.storehouse

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.AppBarConfiguration.Builder
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController

import com.niiadotei.storehouse.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val appBarConfiguration: AppBarConfiguration = Builder(R.id.navigation_supply_chain, R.id.navigation_finance, R.id.navigation_stock, R.id.navigation_customers, R.id.navigation_apprentice).build()

        val navController = findNavController(this, R.id.nav_host_fragment_activity_main)

        setupActionBarWithNavController(this, navController, appBarConfiguration)
        setupWithNavController(binding.navView, navController)
    }

}