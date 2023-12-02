package com.example.plantastic.repository

import android.util.Log
import com.example.plantastic.models.Events
import com.example.plantastic.models.Groups
import com.example.plantastic.utilities.FirebaseNodes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.Query
interface EventsCallback {
    fun onEventsLoaded(events: List<Events>)
}
class GroupsRepository {
    private var firebaseDatabase: FirebaseDatabase =  FirebaseDatabase.getInstance()
    private var groupsReference: DatabaseReference = firebaseDatabase.getReference(FirebaseNodes.GROUPS_NODE)

    fun getGroupById(id: String, callback: (Groups?) -> Unit) {
        val reference = groupsReference.child(id)

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val group = dataSnapshot.getValue(Groups::class.java)
                callback(group)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                callback(null)
            }
        })
    }

    fun getAllGroupsQueryForUser(userId: String): Query {
        return  groupsReference.orderByChild("${FirebaseNodes.GROUPS_PARTICIPANTS_NODE}/${userId}").equalTo(true)
    }

    fun getAllEventsQueryForUser(userId: String, callback: EventsCallback) {
        val eventsReference = FirebaseDatabase.getInstance().getReference(FirebaseNodes.GROUPS_NODE)
        val query = eventsReference.orderByChild("${FirebaseNodes.GROUPS_PARTICIPANTS_NODE}/$userId").equalTo(true)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eventsList = mutableListOf<Events>()

                for (groupSnapshot in snapshot.children) {
                    val grp = groupSnapshot.getValue(Groups::class.java)
                    if (grp != null) {
                        val events = grp.events?.values?.toList() ?: emptyList()
                        eventsList.addAll(events)
                    }
                }

                callback.onEventsLoaded(eventsList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

}