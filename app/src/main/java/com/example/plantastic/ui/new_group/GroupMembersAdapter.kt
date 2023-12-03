package com.example.plantastic.ui.new_group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R

class GroupMembersAdapter(private val dateList: List<String>) : RecyclerView.Adapter<GroupMembersAdapter.DateViewHolder>() {

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.testTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_new_group_members_layout, parent, false)
        return DateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = dateList[position]
        holder.dateTextView.text = date
    }

    override fun getItemCount(): Int {
        return dateList.size
    }
}
