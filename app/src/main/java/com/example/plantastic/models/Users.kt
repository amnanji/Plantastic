package com.example.plantastic.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Users(
    val firstName: String? = null,
    val lastName: String? =null,
    val username: String? = null,
    val email: String? = null,
    val password: String? = null
)