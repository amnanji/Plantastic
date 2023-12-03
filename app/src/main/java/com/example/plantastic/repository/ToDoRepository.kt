package com.example.plantastic.repository

import com.example.plantastic.models.Groups
import com.example.plantastic.models.ToDoItem
import com.example.plantastic.utilities.FirebaseNodes
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class ToDoRepository {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val groupsRepository = GroupsRepository()

    //    private val usersRepository = UsersRepository()
    private val toDoListsReference: DatabaseReference =
        firebaseDatabase.getReference(FirebaseNodes.TODO_LISTS_NODE)

    suspend fun getToDoItemsByUserId(
        userId: String,
        callback: (HashMap<String, ArrayList<ToDoItem>>, HashMap<String, Groups?>) -> Unit
    ) {
        val groupsHashmap = HashMap<String, Groups?>()
        val todoListsHashmap = HashMap<String, ArrayList<ToDoItem>>()

        // Getting all groups the user is a participant of
        val groups = groupsRepository.getAllGroupsByUserWithChatNames(userId)

        // Create a list of deferred tasks to await all group todoLists
        val deferredList = mutableListOf<Deferred<Unit>>()

        for (group in groups) {
            val groupId = group.id!!
            groupsHashmap[groupId] = group

            // Create a coroutine scope for each group's todoList retrieval
            val coroutineScope = CoroutineScope(Dispatchers.IO)

            // Use async to fetch each group's todoList
            val deferred = coroutineScope.async {
                val data: ArrayList<ToDoItem> = ArrayList()

                // Using group id to get all the todoItems of each group
                val toDoListsQuery = toDoListsReference.child(groupId)
                val snapshot = toDoListsQuery.get().await()

                // Loop through all todoItems of the group
                for (toDoItemSnapshot in snapshot.children) {
                    val toDoItem = toDoItemSnapshot.getValue(ToDoItem::class.java)

                    // We only keep todoItems that are assigned to the current user
                    if (toDoItem != null && toDoItem.assignedTo == userId) {
                        toDoItem.id = toDoItemSnapshot.key
                        data.add(toDoItem)
                    }
                }

                todoListsHashmap[groupId] = data
            }

            deferredList.add(deferred)
        }

        // Use awaitAll to wait for all deferred tasks to complete
        awaitAll(*deferredList.toTypedArray())

        // Return the result using the callback
        callback(todoListsHashmap, groupsHashmap)
    }

    // Function to update the todoItem based if the user checks or un-checks the todoItem
    fun updateTodoListItem(id: String?, groupId: String?, isChecked: Boolean) {
        if (id != null && groupId != null) {
            toDoListsReference.child(groupId).child(id)
                .child(FirebaseNodes.TODO_LISTS_IS_COMPLETED_NODE).setValue(isChecked)

            if (isChecked) {
                toDoListsReference.child(groupId).child(id)
                    .child(FirebaseNodes.TODO_LISTS_COMPLETED_DATE_NODE)
                    .setValue(Calendar.getInstance().timeInMillis)
            } else {
                toDoListsReference.child(groupId).child(id)
                    .child(FirebaseNodes.TODO_LISTS_COMPLETED_DATE_NODE).setValue(null)
            }
        }
    }

    // Function to update the todoItem based if the user checks or un-checks the todoItem
    fun addTodoListItem(todoItem: ToDoItem, groupId: String?) {
        if (groupId != null) {
            toDoListsReference.child(groupId).push().setValue(todoItem)
        }
    }

    companion object {
        private const val TAG = "Pln ToDoRepository"
    }
}