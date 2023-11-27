package com.example.plantastic.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Groups(
    val id: String? = null,
    val participants: HashMap<String, Boolean>? = null,
    val eventIds:HashMap<String, Boolean>? = null
)
