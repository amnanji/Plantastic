package com.example.plantastic.ui.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Events Fragment"
    }
    val text: LiveData<String> = _text

    // You can create additional LiveData for your event data here
    // For example:
//    private val _eventsList = MutableLiveData<List<Event>>()
//    val eventsList: LiveData<List<Event>> = _eventsList

    init {
        // Initialize your event data here, or load it from a repository
        // For example:
//        val sampleEventList = listOf(
//            Event("Event 1", "2023-01-01", "Location 1"),
//            Event("Event 2", "2023-02-01", "Location 2"),
//            // Add more events as needed
//        )
//        _eventsList.value = sampleEventList
    }
}

