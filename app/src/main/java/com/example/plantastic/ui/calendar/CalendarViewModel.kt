package com.example.plantastic.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantastic.models.CalendarElement
import com.example.plantastic.models.Groups
import com.example.plantastic.repository.ToDoRepository
import com.example.plantastic.repository.UsersAuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class CalendarViewModel() : ViewModel() {

    private val _calendarEvents = MutableLiveData<List<CalendarElement>>()
    val calEvents: LiveData<List<CalendarElement>> = _calendarEvents
    private var groupsList: List<Groups?> = ArrayList()

    init {
        val currUser = UsersAuthRepository().getCurrentUser()
        val userId = currUser!!.uid

        CoroutineScope(Dispatchers.IO).launch {
            ToDoRepository().getToDoItemsByUserId(userId) { todoListsHashmap, groupsHashmap ->
                // Getting all todoItems assigned to the user and using that list to create a new list of
                // ToDoItemForDisplay which collects all the data needed in addition to todoItem to display the todoItem
                val data = ArrayList<CalendarElement>()
                groupsList = groupsHashmap.values.toList()
                for ((groupId, toDoItems) in todoListsHashmap) {
                    if (!groupsHashmap.containsKey(groupId)) continue
                    val groupName = groupsHashmap[groupId]?.name
                    val groupColor = groupsHashmap[groupId]?.color

                    val events = groupsHashmap[groupId]?.events ?: emptyMap()

                    for ((_, event) in events) {
                            val calendarEvent = CalendarElement(
                                title = event.name,
                                type = "Event", // will switch up in TO-DO
                                date = event.date,
                                GID = event.GID,
                                groupName = groupName,
                                location = event.location,
                                color = groupColor
                            )
                            data.add(calendarEvent)
                    }

                    for (toDoItem in toDoItems) {
                        if (toDoItem.id != null) {
                            val calendarEvent = CalendarElement(
                                title = toDoItem.title,
                                type = "Todo", // will switch up in TO-DO
                                date = toDoItem.dueDate,
                                GID = groupId,
                                groupName = groupName,
                                color = groupColor
                            )
                            data.add(calendarEvent)
                        }
                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    // Sorting list by due date
                    _calendarEvents.value =
                        data.sortedWith(compareBy { it.date ?: Long.MAX_VALUE })
                }
            }
        }
    }

    // load events for the selected day
    fun loadEventsForSelectedDay(selectedDay:Long): List<CalendarElement> {
        val events = _calendarEvents.value ?: emptyList()
        return events.filter { isSameDate(it.date, selectedDay) }
    }

    // Function to check if two dates are the same (ignoring time)
    private fun isSameDate(eventDate: Long?, targetDate: Long): Boolean {
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