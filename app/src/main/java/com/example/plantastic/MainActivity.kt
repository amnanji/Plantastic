package com.example.plantastic

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.core.view.GravityCompat
import com.example.plantastic.databinding.ActivityMainBinding
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.ui.login.LoginActivity
import android.Manifest

class MainActivity : AppCompatActivity() {
    companion object {
        const val MY_PERMISSIONS_REQUEST_CALENDAR = 123
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var usersAuthRepository: UsersAuthRepository = UsersAuthRepository()
    private var usersRepository: UsersRepository = UsersRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout = binding.drawerLayout
        val navDrawer = binding.navDrawer
        val navBottomBar = binding.appBarMain.bottomNavigationView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val headerView = navDrawer.getHeaderView(0)
        val navHeaderLinearLayout = headerView.findViewById<LinearLayout>(R.id.navViewHeader)
        val currUserEmail = headerView.findViewById<TextView>(R.id.currUserEmail_textView)
        val currUserName = headerView.findViewById<TextView>(R.id.currUserName_textView)

        //checkingPermissions
        if (!areCalendarPermissionsGranted()) {
            // Request calendar permissions
            requestCalendarPermissions()
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_chats, R.id.nav_events, R.id.nav_to_do, R.id.nav_balance
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
            if(
                destination.id == R.id.nav_calendar ||
                destination.id == R.id.nav_settings ||
                destination.id == R.id.nav_profile ||
                destination.id == R.id.nav_add_friends
            ) {
                navBottomBar.visibility = View.GONE
            } else {
                navBottomBar.visibility = View.VISIBLE
            }
        }

        val currUser = usersAuthRepository.getCurrentUser()
        if (currUser == null){
            navigateToLoginActivity()
        }
        currUserEmail.text = currUser!!.email

        usersRepository.getUserById(currUser.uid) {
            if (it != null) {
                currUserName.text = buildString {
                    append(it.firstName)
                    append(" ")
                    append(it.lastName)
                }
            }
        }

        navHeaderLinearLayout.setOnClickListener{
            drawerLayout.closeDrawer(GravityCompat.START)
            navController.navigate(R.id.nav_profile)
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
    private fun areCalendarPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_CALENDAR
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCalendarPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
            ),
            MY_PERMISSIONS_REQUEST_CALENDAR
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}