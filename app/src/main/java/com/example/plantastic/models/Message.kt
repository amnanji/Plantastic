package com.example.plantastic.models

data class Message(
    val messageType: String? = null,
    val senderId: String? = null,
    val content: String? = null,
    val timestamp: Long? = null
)
