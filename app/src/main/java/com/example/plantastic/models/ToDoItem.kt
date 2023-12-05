package com.example.plantastic.models
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ToDoItem (
    var id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val dueDate: Long? = null,
    val completedDate: Long? = null,
    var completed: Boolean? = null,
    val assignedTo: String? = null
)