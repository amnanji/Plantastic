package com.example.plantastic

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.plantastic.databinding.ActivityMainBinding
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var usersAuthRepository: UsersAuthRepository = UsersAuthRepository()

    val x = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout = binding.drawerLayout
        val navDrawer = binding.navDrawer
        val navBottomBar = binding.appBarMain.bottomNavigationView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_calendar, R.id.nav_settings, R.id.nav_chats, R.id.nav_events, R.id.nav_to_do, R.id.nav_balance
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Setup the navigation views with navController
        navDrawer.setupWithNavController(navController)
        navBottomBar.setupWithNavController(navController)

        // Help from - https://stackoverflow.com/questions/31265530/how-can-i-get-menu-item-in-navigationview
        // Help from - https://developer.android.com/reference/android/view/MenuItem.OnMenuItemClickListener
        val logOutMenuItem: MenuItem = navDrawer.menu.findItem(R.id.nav_logout)
        logOutMenuItem.setOnMenuItemClickListener {
            usersAuthRepository.logOutUser()
            navigateToLoginActivity()
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.nav_calendar || destination.id == R.id.nav_settings) {
                navBottomBar.visibility = View.GONE
            } else {
                navBottomBar.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}