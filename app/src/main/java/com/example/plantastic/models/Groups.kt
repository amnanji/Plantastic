package com.example.plantastic.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Groups(
    val id: String? = null,
    val groupType: String? = null, // Group, Individual, Event
    val participants: HashMap<String, Boolean>? = null,
    var name: String? = null,
    val latestMessage: Message? = null,
    var balances:HashMap<String, HashMap<String, Double>>? = null,
    val timestampGroupCreated: Long? = null,
    val events:HashMap<String,Events> ?= null,
    val color: Int ?= null
)