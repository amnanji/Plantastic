package com.example.plantastic.models

data class Message(
    val id: String? = null,
    val chatId: String? = null,
    val messageType: String? = null,
    val senderId: String? = null,
    val content: String? = null,
    val timestamp: Int? = null
)
