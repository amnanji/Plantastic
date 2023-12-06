package com.example.plantastic

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.plantastic.databinding.ActivityMainBinding
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.ui.login.LoginActivity
import com.example.plantastic.utilities.IconUtil

class MainActivity : AppCompatActivity() {
    companion object {
        const val MY_PERMISSIONS_REQUEST_CALENDAR = 123
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var usersAuthRepository: UsersAuthRepository = UsersAuthRepository()
    private var usersRepository: UsersRepository = UsersRepository()

    lateinit var navController: NavController

    var newChatsFragmentId: Int = -1
    private var newGroupChatFragmentId: Int = -1
    private var newIndividualChatFragmentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currUser = usersAuthRepository.getCurrentUser()
        if (currUser == null) {
            navigateToLoginActivity()
        }

        if (!currUser!!.isEmailVerified) {
            Toast.makeText(
                this,
                getString(R.string.a_verification_email_has_been_sent_please_verify_your_email),
                Toast.LENGTH_SHORT
            ).show()
            usersAuthRepository.logOutUser()
            navigateToLoginActivity()
            finish()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout = binding.drawerLayout
        val navDrawer = binding.navDrawer
        val navBottomBar = binding.appBarMain.bottomNavigationView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        val headerView = navDrawer.getHeaderView(0)
        val navHeaderLinearLayout = headerView.findViewById<LinearLayout>(R.id.navViewHeader)
        val profileImageView = headerView.findViewById<ImageView>(R.id.profileImageView)
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
            if (
                destination.id == R.id.nav_calendar ||
                destination.id == R.id.nav_settings ||
                destination.id == R.id.nav_profile ||
                destination.id == R.id.nav_add_friends ||
                destination.id == R.id.nav_new_chat ||
                destination.id == R.id.nav_new_chat_individual ||
                destination.id == R.id.nav_new_chat_group
            ) {
                navBottomBar.visibility = View.GONE
            } else {
                navBottomBar.visibility = View.VISIBLE
            }
        }

        currUserEmail.text = currUser.email

        val context = this

        usersRepository.getUserById(currUser.uid) {
            if (it != null) {
                currUserName.text = buildString {
                    append(it.firstName)
                    append(" ")
                    append(it.lastName)

                    val iconUtils = IconUtil(context)
                    val color: Int
                    if (it.color == null) {
                        color = iconUtils.getRandomColour()
                        usersRepository.setColor(currUser.uid, color)
                    } else {
                        color = it.color
                    }
                    val icon = iconUtils.getIcon(it.firstName!!, it.lastName!!, color)
                    profileImageView.setImageDrawable(icon)
                }
            }
        }

        navHeaderLinearLayout.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            navController.navigate(R.id.nav_profile)
        }

        newChatsFragmentId = R.id.nav_new_chat
        newIndividualChatFragmentId = R.id.nav_new_chat_individual
        newGroupChatFragmentId = R.id.nav_new_chat_group
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