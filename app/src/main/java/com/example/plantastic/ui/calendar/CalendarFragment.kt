package com.example.plantastic.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantastic.databinding.FragmentCalendarBinding
import com.example.plantastic.models.CalendarElement
import com.example.plantastic.repository.CalendarCallback
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import java.util.Calendar

class CalendarFragment : Fragment(), CalendarCallback {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var calendarView: CalendarView
    private lateinit var groupsRepository: GroupsRepository
    private lateinit var usersAuthRepository: UsersAuthRepository
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var calendarViewModel: CalendarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root
        calendarViewModel = ViewModelProvider(this)[CalendarViewModel::class.java]

        calendarView = binding.calendarView
        usersAuthRepository = UsersAuthRepository()
        groupsRepository = GroupsRepository()
        calendarAdapter = CalendarAdapter(emptyList())

        // REFERENCED: https://developer.android.com/reference/android/widget/CalendarView.OnDateChangeListener
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDay = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.timeInMillis
            calendarAdapter =
                CalendarAdapter(calendarViewModel.loadEventsForSelectedDay(selectedDay))
            binding.calendarRecyclerView.adapter = calendarAdapter
            binding.calendarRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCalendarLoaded(calendarList: List<CalendarElement>) {
        calendarAdapter = CalendarAdapter(calendarList)
        binding.calendarRecyclerView.adapter = calendarAdapter
        binding.calendarRecyclerView.layoutManager = LinearLayoutManager(requireContext())

    }
}
