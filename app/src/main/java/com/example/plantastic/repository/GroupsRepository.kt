package com.example.plantastic.repository

import com.example.plantastic.models.CalendarElement
import com.example.plantastic.models.Events
import com.example.plantastic.models.Groups
import com.example.plantastic.utilities.FirebaseNodes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.Query
import java.util.Date
import java.util.Calendar

interface EventsCallback {
    fun onEventsLoaded(events: List<Events>)
}
interface CalendarCallback {
    fun onCalendarLoaded(calendarList: List<CalendarElement>)
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

    fun getCalendarForUserAndDate(userId: String, targetDate: Long, callback: CalendarCallback) {
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
                                    type = "Event", // will switch up in TO-DO
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

    fun isSameDate(eventDate: Long?, targetDate: Long): Boolean {
        if (eventDate == null) {
            return false
        }

        val eventCalendar = Calendar.getInstance().apply {
            timeInMillis = eventDate
        }

        val targetCalendar = Calendar.getInstance().apply {
            timeInMillis = targetDate
        }

        return (eventCalendar.get(Calendar.YEAR) == targetCalendar.get(Calendar.YEAR) &&
                eventCalendar.get(Calendar.MONTH) == targetCalendar.get(Calendar.MONTH) &&
                eventCalendar.get(Calendar.DAY_OF_MONTH) == targetCalendar.get(Calendar.DAY_OF_MONTH))
    }




}