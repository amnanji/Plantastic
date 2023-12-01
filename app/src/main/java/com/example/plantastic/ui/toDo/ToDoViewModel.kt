package com.example.plantastic.ui.toDo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantastic.models.ToDoItemForDisplay
import com.example.plantastic.repository.ToDoRepository
import com.example.plantastic.repository.UsersAuthRepository

class ToDoViewModel : ViewModel() {

    private val _toDoItems = MutableLiveData<ArrayList<ToDoItemForDisplay>>()
    val toDoItems: LiveData<ArrayList<ToDoItemForDisplay>> = _toDoItems

    init{
        val currUser = UsersAuthRepository().getCurrentUser()
        val userId = currUser!!.uid

        // Getting all todoItems assigned to the user and using that list to create a new list of
        // ToDoItemForDisplay which collects all the data needed in addition to todoItem to display the todoItem
        ToDoRepository().getToDoItemsByUserId(userId){ todoListsHashmap, groupsHashmap ->
            val data = ArrayList<ToDoItemForDisplay>()
            for ((groupId, toDoItems) in todoListsHashmap){
                val groupName = groupsHashmap[groupId]?.name
                val isGroup = groupsHashmap[groupId]?.groupType == "group"
                val participants = groupsHashmap[groupId]?.participants!!.keys.toList()
                val otherParticipantId = if (isGroup) null else {
                    if (participants[0] == userId) participants[1] else participants[0]
                }

                for (toDoItem in toDoItems){
                    if (toDoItem.id != null) {
                        val toDoItemForDisplay = ToDoItemForDisplay(toDoItem, groupId, isGroup, groupName, otherParticipantId)
                        data.add(toDoItemForDisplay)
                    }
                }
            }

            // Sorting list by due date
            _toDoItems.value = ArrayList(data.sortedWith(compareBy { it.dueDate ?: Long.MAX_VALUE }))
        }
    }
}