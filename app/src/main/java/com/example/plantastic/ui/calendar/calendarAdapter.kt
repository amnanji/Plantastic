package com.example.plantastic.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.CalendarElement
import com.example.plantastic.repository.GroupsRepository

class calendarAdapter(private var calendarElementList: List<CalendarElement>) :
    RecyclerView.Adapter<calendarAdapter.CalendarViewHolder>() {
    private val groupsRepository = GroupsRepository()


    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val calType: TextView = itemView.findViewById(R.id.calendarEventOrTodo)
        val calTime: TextView = itemView.findViewById(R.id.calGroupTimeText)
        val calTitle: TextView = itemView.findViewById(R.id.calTitleText)
        val calGroupName: TextView = itemView.findViewById(R.id.calGroupNameText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.custom_calendar_layout, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val calendar = calendarElementList[position]
        holder.calType.text = calendar.type
        holder.calTime.text = calendar.date.toString()
        holder.calTitle.text = calendar.title
        groupsRepository.getGroupById(calendar.GID!!){
            if(it!=null)
            {
                holder.calGroupName.text = it.name
            }
        }
    }

    override fun getItemCount(): Int {
        return calendarElementList.size
    }
    fun updateCalendarElements(newCalendarElements: List<CalendarElement>) {
        calendarElementList = newCalendarElements
        notifyDataSetChanged()
    }
}
