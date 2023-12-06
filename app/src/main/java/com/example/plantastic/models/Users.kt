package com.example.plantastic.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Users(
    val id: String? = null,
    val firstName: String? = null,
    val lastName: String? =null,
    val username: String? = null,
    val email: String? = null,
    val friends: HashMap<String, Boolean>? = hashMapOf(),
    val color: Int ?= null,
    val isEmailVerified: Boolean? = false
)
