package com.example.plantastic.ui.toDo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantastic.models.Groups
import com.example.plantastic.models.ToDoItemForDisplay
import com.example.plantastic.repository.ToDoRepository
import com.example.plantastic.repository.UsersAuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoViewModel : ViewModel() {

    private val _toDoItems = MutableLiveData<ArrayList<ToDoItemForDisplay>>()
    val toDoItems: LiveData<ArrayList<ToDoItemForDisplay>> = _toDoItems

    var groupsList: List<Groups?> = ArrayList()

    init {
        val currUser = UsersAuthRepository().getCurrentUser()
        val userId = currUser!!.uid

        CoroutineScope(Dispatchers.IO).launch {
            ToDoRepository().getToDoItemsByUserId(userId) { todoListsHashmap, groupsHashmap ->
                // Getting all todoItems assigned to the user and using that list to create a new list of
                // ToDoItemForDisplay which collects all the data needed in addition to todoItem to display the todoItem
                val data = ArrayList<ToDoItemForDisplay>()
                groupsList = groupsHashmap.values.toList()
                for ((groupId, toDoItems) in todoListsHashmap) {
                    if (!groupsHashmap.containsKey(groupId)) continue
                    Log.d(TAG, "Got past this")
                    val groupName = groupsHashmap[groupId]?.name
                    val isGroup = groupsHashmap[groupId]?.groupType == "group"

                    for (toDoItem in toDoItems) {
                        if (toDoItem.id != null) {
                            val toDoItemForDisplay = ToDoItemForDisplay(
                                toDoItem,
                                groupId,
                                isGroup,
                                groupName
                            )
                            data.add(toDoItemForDisplay)
                        }
                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    Log.d(TAG, "Data is --> $data")
                    // Sorting list by due date
                    _toDoItems.value =
                        ArrayList(data.sortedWith(compareBy { it.dueDate ?: Long.MAX_VALUE }))
                }
            }
        }
    }

    companion object {
        private const val TAG = "Pln ToDoViewModel"
    }
}