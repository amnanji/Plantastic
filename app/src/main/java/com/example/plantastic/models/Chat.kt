package com.example.plantastic.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chat (
    val id: String? = null,
    val chatType: String? = null,
    val participants: ArrayList<String>? = null,
    val admins: ArrayList<String>? = null,
    val toDoListId: String? = null,
    val events: ArrayList<String>? = null,
    val expenses: ArrayList<String>? = null,
    val latestMessage: Message? = null
)