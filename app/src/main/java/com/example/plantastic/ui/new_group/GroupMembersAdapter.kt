package com.example.plantastic.ui.new_group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Users

class GroupMembersAdapter(private val memberList: ArrayList<Users>) : RecyclerView.Adapter<GroupMembersAdapter.GroupMemberViewHolder>() {

    inner class GroupMemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.groupMemberUsernameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMemberViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_new_group_members_layout, parent, false)
        return GroupMemberViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupMemberViewHolder, position: Int) {
        val model = memberList[position]
        holder.usernameTextView.text = model.username
    }

    override fun getItemCount(): Int {
        return memberList.size
    }
}