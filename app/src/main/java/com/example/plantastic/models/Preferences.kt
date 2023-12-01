package com.example.plantastic.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Preferences (
    var foodPreferences: String? = null,
    var dietaryRestrictionIndex: Int? = null,
    var activityPreferences: String? = null,
    var availability: MutableList<Int>? = null
)