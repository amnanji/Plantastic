package com.example.plantastic.repository

import android.util.Log
import com.example.plantastic.models.CalendarElement
import com.example.plantastic.models.Events
import com.example.plantastic.models.Groups
import com.example.plantastic.utilities.DateTimeUtils
import com.example.plantastic.utilities.FirebaseNodes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.Query
import com.google.firebase.database.getValue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface EventsCallback {
    fun onEventsLoaded(events: List<Events>)
}

interface CalendarCallback {
    fun onCalendarLoaded(calendarList: List<CalendarElement>)
}

class GroupsRepository {
    private var firebaseDatabase = FirebaseDatabase.getInstance()
    private var groupsReference = firebaseDatabase.getReference(FirebaseNodes.GROUPS_NODE)
    private var usersRepository = UsersRepository()

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
        return groupsReference.orderByChild("${FirebaseNodes.GROUPS_PARTICIPANTS_NODE}/${userId}")
            .equalTo(true)
    }

    // Suspending function to be called inside a coroutine to get all the groups a user is in
    // and add the chat names for the individual group types.
    // Got help from ChatGPT to avoid blocking the main thread
    suspend fun getAllGroupsByUserWithChatNamesAsync(userId: String): List<Groups> {
        return suspendCoroutine { continuation ->
            // Calls the private call-back based function and handles the result
            getAllGroupsByUserWithChatNamesSingleValue(userId) { groups ->
                if (groups != null) {
                    continuation.resume(groups)
                } else {
                    // Handle error or return an empty list
                    continuation.resume(emptyList())
                }
            }
        }
    }

    private fun getAllGroupsByUserWithChatNamesSingleValue(userId: String, callback: (List<Groups>?) -> Unit) {
        val groups = ArrayList<Groups>()

        getAllGroupsQueryForUser(userId).addListenerForSingleValueEvent (object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Create a list to hold deferred tasks for user retrieval
                val deferredList = mutableListOf<Deferred<Unit>>()

                // Looping through data snapshots received and adding the chat name if necessary
                for (groupSnapshot in snapshot.children) {

                    // If any of the snapshots are null, we don't add it to the list of groups and continue the loop
                    val currGroup = groupSnapshot.getValue(Groups::class.java) ?: continue

                    // If it is a group of type "group", we do not need to change the name
                    if (currGroup.groupType == "group") {
                        groups.add(currGroup)
                        continue
                    }

                    // If the group is not of type "group", it must be of type "individual"
                    // then we know it only has one other participant, so the group name will
                    // be named after the other participant

                    // Getting other participant's id
                    val participants = currGroup.participants!!.keys.toList()
                    val otherParticipantId =
                        if (participants[0] == userId) participants[1] else participants[0]

                    // Create a deferred task to get user's name
                    val innerDeferred = CompletableDeferred<Unit>()

                    usersRepository.getUserById(otherParticipantId) { user ->
                        if (user != null) {
                            currGroup.name = "Chat with ${user.firstName} ${user.lastName}"
                        }
                        // Mark inner deferred task as completed
                        innerDeferred.complete(Unit)
                        groups.add(currGroup)
                    }

                    // Add the inner deferred task to the list
                    deferredList.add(innerDeferred)
                }

                // Use async and awaitAll to wait for all innerDeferred to complete
                CoroutineScope(Dispatchers.IO).launch {
                    deferredList.awaitAll()

                    // Return the result using the callback on the main thread
                    withContext(Dispatchers.Main) {
                        callback(groups)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Could not get getAllGroupsByUserWithChatNames()")
                error.toException().printStackTrace()
                callback(null)
            }
        })
    }

    fun getAllGroupsByUserWithChatNames(userId: String, callback: (List<Groups>?) -> Unit) {
        val groups = ArrayList<Groups>()

        getAllGroupsQueryForUser(userId).addValueEventListener (object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Create a list to hold deferred tasks for user retrieval
                val deferredList = mutableListOf<Deferred<Unit>>()

                // Looping through data snapshots received and adding the chat name if necessary
                for (groupSnapshot in snapshot.children) {

                    // If any of the snapshots are null, we don't add it to the list of groups and continue the loop
                    val currGroup = groupSnapshot.getValue(Groups::class.java) ?: continue

                    // If it is a group of type "group", we do not need to change the name
                    if (currGroup.groupType == "group") {
                        groups.add(currGroup)
                        continue
                    }

                    // If the group is not of type "group", it must be of type "individual"
                    // then we know it only has one other participant, so the group name will
                    // be named after the other participant

                    // Getting other participant's id
                    val participants = currGroup.participants!!.keys.toList()
                    val otherParticipantId =
                        if (participants[0] == userId) participants[1] else participants[0]

                    // Create a deferred task to get user's name
                    val innerDeferred = CompletableDeferred<Unit>()

                    usersRepository.getUserById(otherParticipantId) { user ->
                        if (user != null) {
                            currGroup.name = "Chat with ${user.firstName} ${user.lastName}"
                        }
                        // Mark inner deferred task as completed
                        innerDeferred.complete(Unit)
                        groups.add(currGroup)
                    }

                    // Add the inner deferred task to the list
                    deferredList.add(innerDeferred)
                }

                // Use async and awaitAll to wait for all innerDeferred to complete
                CoroutineScope(Dispatchers.IO).launch {
                    deferredList.awaitAll()

                    // Return the result using the callback on the main thread
                    withContext(Dispatchers.Main) {
                        callback(groups)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Could not get getAllGroupsByUserWithChatNames()")
                error.toException().printStackTrace()
                callback(null)
            }
        })
    }

    fun getAllEventsListForUser(userId: String, callback: EventsCallback) {
        getAllGroupsQueryForUser(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eventsList = mutableListOf<Events>()

                for (groupSnapshot in snapshot.children) {
                    val grp = groupSnapshot.getValue(Groups::class.java)
                    if (grp != null) {
                        val events = grp.events?.values?.toList() ?: emptyList()

                        eventsList.addAll(events)
                    }
                }
                val sortedList =  ArrayList(eventsList.sortedWith(compareBy { it.date ?: Long.MAX_VALUE }))
                callback.onEventsLoaded(sortedList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun addEventsItem(eventsItem: Events, groupId: String?) {
        if (groupId != null) {
            groupsReference.child(groupId).child(FirebaseNodes.EVENTS_NODE).push().setValue(eventsItem)
        }
    }

    fun getGroupIdForUsers(userId1: String, userId2: String, callback: (String?) -> Unit){
        getAllGroupsQueryForUser(userId1).addListenerForSingleValueEvent (
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var gId: String? = null
                    if (dataSnapshot.exists()) {
                        dataSnapshot.getValue<HashMap<String, Groups>>()?.forEach { (key, value) ->
                            if (value.participants!!.size == 2 &&
                                value.participants.containsKey(userId2) &&
                                value.participants.containsKey(userId1) &&
                                value.groupType == "individual"
                            ) {
                                gId = key
                            }
                        }
                    }
                    callback(gId)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback(null)
                }
            }
        )
    }


    fun createGroupForUsers(userList: ArrayList<String>, groupName: String?, callback: (String?) -> Unit) {
        val participants: HashMap<String, Boolean> = HashMap()
        val timestampGroupCreated: Long = DateTimeUtils.getCurrentTimeStamp()
        val balances: HashMap<String, HashMap<String, Double>> = HashMap()

        for (user in userList){
            participants[user] = true
            balances[user] = HashMap()
            userList.forEach{
                if (it != user){
                    balances[user]!![it] = 0.0
                }
            }
        }

        //create individual group
        val groupType: String = if (userList.size == 2){
            "individual"
        }
        //create big group
        else {
            "group"
        }

        val reference: DatabaseReference = groupsReference.push()
        val groupId: String? = reference.key

        val group = Groups(
            groupId,
            groupType,
            participants,
            groupName,
            null,
            balances,
            timestampGroupCreated,
            null
        )

        reference.setValue(group)
            .addOnSuccessListener {
                callback(groupId)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    companion object {
        private const val TAG = "Pln GroupsRepository"
    }
}