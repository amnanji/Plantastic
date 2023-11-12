package com.example.plantastic.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Users(
    val usrId: String? = null,
    val username: String? = null,
    val email: String? = null,
    val password: String? = null
)