package com.example.plantastic.models

// Data class used only when passing data from the ToDoViewModel to the ToDoAdapter. We use this
// because we need a few more pieces of data to display the todoItem.
data class ToDoItemForDisplay(
    val id: String? = null,
    val groupId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val dueDate: Long? = null,
    val isCompleted: Boolean? = null,
    val isGroup: Boolean? = null,
    val groupName: String? = null
) {
    // Constructor that takes a ToDoItem to populate values common between ToDoItem and ToDoItemForDisplay,
    // and also takes the additional pieces of data as parameters
    constructor(toDoItem: ToDoItem, groupId: String, isGroup: Boolean, groupName: String?) : this(
        toDoItem.id,
        groupId,
        toDoItem.title,
        toDoItem.description,
        toDoItem.dueDate,
        toDoItem.isCompleted,
        isGroup,
        groupName
    )
}
