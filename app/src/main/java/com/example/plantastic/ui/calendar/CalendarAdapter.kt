package com.example.plantastic.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.CalendarElement
import com.example.plantastic.utilities.DateTimeUtils


class CalendarAdapter(private var calendarElementList: List<CalendarElement>) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val calType: TextView = itemView.findViewById(R.id.calendarEventOrTodo)
        val calTime: TextView = itemView.findViewById(R.id.calGroupTimeText)
        val calTitle: TextView = itemView.findViewById(R.id.calTitleText)
        val calGroupName: TextView = itemView.findViewById(R.id.calGroupNameText)
        val timeLayout: LinearLayout = itemView.findViewById(R.id.calGroupTimeLayout)
        val locationLayout: LinearLayout = itemView.findViewById(R.id.calEventLocationLayout)
        val calLocation: TextView = itemView.findViewById(R.id.calEventLocationName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.custom_calendar_layout, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val calendar = calendarElementList[position]
        holder.calType.text = calendar.type
        if (calendar.type == "Todo") {
            holder.timeLayout.visibility = View.GONE
            holder.locationLayout.visibility = View.GONE
        } else {
            holder.calTime.text = DateTimeUtils.getTimeString(calendar.date!!)
            holder.calLocation.text = calendar.location
        }
        holder.calTitle.text = calendar.title
        holder.calGroupName.text = calendar.groupName
    }

    override fun getItemCount(): Int {
        return calendarElementList.size
    }

}
