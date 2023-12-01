package com.example.plantastic.models

data class ToDoItem (
    var id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val dueDate: Long? = null,
    val completedDate: Long? = null,
    val isCompleted: Boolean? = null,
    val assignedTo: String? = null
){
    // Getter for isCompleted
    fun getIsCompleted(): Boolean? {
        return isCompleted
    }

    // Setter for isCompleted
    fun setIsCompleted(completed: Boolean?): ToDoItem {
        return copy(isCompleted = completed)
    }
}