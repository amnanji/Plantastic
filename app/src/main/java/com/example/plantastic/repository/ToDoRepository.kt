package com.example.plantastic.repository

import android.util.Log
import com.example.plantastic.models.Groups
import com.example.plantastic.models.ToDoItem
import com.example.plantastic.utilities.FirebaseNodes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class ToDoRepository {
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var groupsRepository = GroupsRepository()
    private val toDoListsReference: DatabaseReference =
        firebaseDatabase.getReference(FirebaseNodes.TODO_LISTS_NODE)

    fun getToDoItemsByUserId(userId: String, callback: (HashMap<String, ArrayList<ToDoItem>>, HashMap<String, Groups?>) -> Unit) {
        val groupsQuery = groupsRepository.getAllGroupsQueryForUser(userId)

        // Getting all groups the user is a participant of
        groupsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Hashmap of <groupId, Groups object>
                val groupsHashmap = HashMap<String, Groups?>()

                // Hashmap of <groupId, List of todoItems>
                val todoListsHashmap = HashMap<String, ArrayList<ToDoItem>>()

                // Looping through all the groups the user is part of
                for (groupSnapshot in dataSnapshot.children){
                    val group = groupSnapshot.getValue(Groups::class.java)
                    val groupId = group?.id

                    if (groupId != null){
                        groupsHashmap[groupId] = group
                        val toDoListsQuery = toDoListsReference.child(groupId)

                        // Using group id to get all the todoItems of each group
                        toDoListsQuery.addValueEventListener(object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val data: ArrayList<ToDoItem> = ArrayList()

                                // looping through all todoItems of the group
                                for (toDoItemSnapshot in snapshot.children){
                                    val toDoItem = toDoItemSnapshot.getValue(ToDoItem::class.java)

                                    // We only keep todoItems that are assigned to the current user
                                    if(toDoItem != null && toDoItem.assignedTo == userId){
                                        toDoItem.id = toDoItemSnapshot.key
                                        data.add(toDoItem)
                                    }
                                }
                                todoListsHashmap[groupId] = data
                                callback(todoListsHashmap, groupsHashmap)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e(TAG, "Could not get todo list for group --> $groupId")
                                error.toException().printStackTrace()
                            }
                        })
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Could not get todo items for user --> $userId")
                databaseError.toException().printStackTrace()
            }
        })
    }

    // Function to update the todoItem based if the user checks or un-checks the todoItem
    fun updateTodoListItem(id: String?, groupId:String?, isChecked: Boolean){
        if(id != null && groupId != null) {
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

    companion object {
        private const val TAG = "Pln ToDoRepository"
    }
}