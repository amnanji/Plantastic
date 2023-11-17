package com.example.plantastic.models

import android.view.MenuItem
import com.example.plantastic.R
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Users(
    val firstName: String? = null,
    val lastName: String? =null,
    val username: String? = null,
    val email: String? = null
)


