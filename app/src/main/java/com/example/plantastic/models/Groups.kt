package com.example.plantastic.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Groups(
    var id: String? = null,
    var participants: HashMap<String, Boolean>? = null,
    var eventIds:HashMap<String, Boolean>? = null,
    var balances:HashMap<String, HashMap<String, Int>>? = null
)