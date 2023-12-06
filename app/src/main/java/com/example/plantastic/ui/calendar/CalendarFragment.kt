package com.example.plantastic.ui.calendar

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantastic.R
import com.example.plantastic.databinding.FragmentCalendarBinding
import com.example.plantastic.models.CalendarElement
import com.example.plantastic.repository.CalendarCallback
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.Locale

class CalendarFragment : Fragment(), CalendarCallback {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var calendarView: MaterialCalendarView
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
        calendarView= root.findViewById(R.id.calendarView) as MaterialCalendarView
        calendarViewModel.calEvents.observe(requireActivity()){
            for(calendarelem in it)
            {
                //Referenced from https://github.com/prolificinteractive/material-calendarview/wiki/Decorators
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = calendarelem.date!!
                val myDate= CalendarDay.from(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH))
                calendarView.addDecorators(CurrentDayDecorator( myDate))
            }
        }
        usersAuthRepository = UsersAuthRepository()
        groupsRepository = GroupsRepository()
        calendarAdapter = CalendarAdapter(emptyList())
        calendarView.setOnDateChangedListener { _, date, _ ->
            calendarAdapter =
                CalendarAdapter(calendarViewModel.loadEventsForSelectedDay(parseDate(date)))
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
    private fun parseDate(date:CalendarDay): Long {
        val stringDate = getString(R.string.date_placeholder, date.year, (date.month), date.day)
        Log.d("revs","date $stringDate")
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.CANADA)
        return sdf.parse(stringDate).time

    }
}
