package com.example.plantastic.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.plantastic.databinding.FragmentCalendarBinding
import java.util.*

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var editTextEventName: EditText
    private lateinit var buttonSaveEvent: Button
    private lateinit var calendarView: CalendarView
    private lateinit var listViewEvents: ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        calendarViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)

        editTextEventName = binding.editTextEventName
        buttonSaveEvent = binding.buttonSaveEvent
        calendarView = binding.calendarView
        listViewEvents = binding.listViewEvents

        val eventsAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf<String>())
        listViewEvents.adapter = eventsAdapter

        // REFERENCED: https://developer.android.com/reference/android/widget/CalendarView.OnDateChangeListener
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDay = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time
            calendarViewModel.loadEventsForSelectedDay(selectedDay)
        }

        // Observe changes
        calendarViewModel.eventsForSelectedDay.observe(viewLifecycleOwner) { events ->
            val eventNames = events.map { it.title }
            eventsAdapter.clear()
            eventsAdapter.addAll(eventNames)
        }

        // Button click listener for saving events
        buttonSaveEvent.setOnClickListener {
            val eventName = editTextEventName.text.toString()
            val currentDate = Calendar.getInstance().time
            val newEvent = CalendarViewModel.CalendarEvent(eventName, currentDate, currentDate)
            calendarViewModel.addEvent(newEvent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
