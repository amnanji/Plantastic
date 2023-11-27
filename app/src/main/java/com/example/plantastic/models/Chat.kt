package com.example.plantastic.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chat (
    val id: String? = null,
    val name: String? = null,
    val chatType: String? = null,
    val admins: ArrayList<String>? = null,
    val latestMessage: Message? = null
)