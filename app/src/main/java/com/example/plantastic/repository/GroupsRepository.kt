package com.example.plantastic.repository

import android.util.Log
import com.example.plantastic.models.CalendarElement
import com.example.plantastic.models.Events
import com.example.plantastic.models.Groups
import com.example.plantastic.utilities.FirebaseNodes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.Query
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import java.util.Date
import java.util.Calendar

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
    suspend fun getAllGroupsByUserWithChatNames(userId: String): List<Groups> {
        return suspendCoroutine { continuation ->
            // Calls the private call-back based function and handles the result
            getAllGroupsByUserWithChatNames(userId) { groups ->
                if (groups != null) {
                    continuation.resume(groups)
                } else {
                    // Handle error or return an empty list
                    continuation.resume(emptyList())
                }
            }
        }
    }

    // Your existing function with a callback
    private fun getAllGroupsByUserWithChatNames(userId: String, callback: (List<Groups>?) -> Unit) {
        val groups = ArrayList<Groups>()

        getAllGroupsQueryForUser(userId).addValueEventListener(object : ValueEventListener {
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

    fun getCalendarForUserAndDate(userId: String, targetDate: Date, callback: CalendarCallback) {
        val eventsReference = FirebaseDatabase.getInstance().getReference(FirebaseNodes.GROUPS_NODE)
        val query = eventsReference.orderByChild("${FirebaseNodes.GROUPS_PARTICIPANTS_NODE}/$userId").equalTo(true)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val calendarList = mutableListOf<CalendarElement>()

                for (groupSnapshot in snapshot.children) {
                    val grp = groupSnapshot.getValue(Groups::class.java)
                    if (grp != null) {
                        val events = grp.events ?: emptyMap()

                        for ((eventId, event) in events) {
                            // Check if the event date matches the target date
                            if (isSameDate(event.date, targetDate)) {
                                val calendarEvent = CalendarElement(
                                    title = event.name,
                                    type = "Event", // or any other type you want
                                    date = event.date,
                                    GID = event.GID
                                )
                                calendarList.add(calendarEvent)
                            }
                        }
                    }
                }

                callback.onCalendarLoaded(calendarList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


    // Function to check if two dates are the same (ignoring time)

    fun isSameDate(eventDate: Long?, targetDate: Date): Boolean {
        if (eventDate == null) {
            return false
        }

        val eventCalendar = Calendar.getInstance().apply {
            timeInMillis = eventDate
        }

        val targetCalendar = Calendar.getInstance().apply {
            time = targetDate
        }

        return (eventCalendar.get(Calendar.YEAR) == targetCalendar.get(Calendar.YEAR) &&
                eventCalendar.get(Calendar.MONTH) == targetCalendar.get(Calendar.MONTH) &&
                eventCalendar.get(Calendar.DAY_OF_MONTH) == targetCalendar.get(Calendar.DAY_OF_MONTH))
    }

    companion object {
        private const val TAG = "Pln GroupsRepository"
    }
}