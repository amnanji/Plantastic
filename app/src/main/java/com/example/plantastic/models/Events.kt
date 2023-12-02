package com.example.plantastic.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Events(
    val name: String? = null,
    val location: String? = null,
    val date: String? = null
)