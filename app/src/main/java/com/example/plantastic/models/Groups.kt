package com.example.plantastic.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Groups(
    val id: String? = null,
    val groupType: String? = null, // Group, Individual, Event
    val participants: HashMap<String, Boolean>? = null,
    val eventIds:HashMap<String, Boolean>? = null,
    val name: String? = null,
    val admins: ArrayList<String>? = null,
    val latestMessage: Message? = null,
    var balances:HashMap<String, HashMap<String, Double>>? = null,
    val timestampGroupCreated: Long? = null
)