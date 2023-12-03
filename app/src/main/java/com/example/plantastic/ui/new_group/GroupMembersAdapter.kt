package com.example.plantastic.ui.new_group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Users

class GroupMembersAdapter(private val memberList: ArrayList<Users>) : RecyclerView.Adapter<GroupMembersAdapter.DateViewHolder>() {

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.testTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_new_group_members_layout, parent, false)
        return DateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val model = memberList[position]
        holder.dateTextView.text = model.username
    }

    override fun getItemCount(): Int {
        return memberList.size
    }
}
