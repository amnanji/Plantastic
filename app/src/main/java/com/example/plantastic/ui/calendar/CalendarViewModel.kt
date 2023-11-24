package com.example.plantastic.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val _calendarEvents = MutableLiveData<List<CalendarEvent>>()

    // New property to hold a list of events for the selected day
    private val _eventsForSelectedDay = MutableLiveData<List<CalendarEvent>>()
    val eventsForSelectedDay: LiveData<List<CalendarEvent>> = _eventsForSelectedDay



    init {
        val events = mutableListOf<CalendarEvent>()
        // sample events
        events.add(CalendarEvent("Event 1", Date(), Date()))
        events.add(CalendarEvent("Event 2", Date(), Date()))
        _calendarEvents.value = events
    }

    // load events for the selected day
    fun loadEventsForSelectedDay(selectedDay: Date) {
        val events = _calendarEvents.value ?: emptyList()
        val eventsOnSelectedDay = events.filter { it.startDate.day == selectedDay.day }
        _eventsForSelectedDay.value = eventsOnSelectedDay
    }
    fun addEvent(event: CalendarEvent) {
        val currentEvents = _calendarEvents.value?.toMutableList() ?: mutableListOf()
        currentEvents.add(event)
        _calendarEvents.value = currentEvents
    }

    data class CalendarEvent(val title: String, val startDate: Date, val endDate: Date)

}