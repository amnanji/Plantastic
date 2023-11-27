package com.example.plantastic.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message(
    val messageType: String? = null,
    val senderId: String? = null,
    val content: String? = null,
    val timestamp: Long? = null
)