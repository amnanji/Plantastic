package com.example.plantastic.ui.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Users
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class AddFriendsAdapter(
    options: FirebaseRecyclerOptions<Users>,
    userId: String
) :
    FirebaseRecyclerAdapter<Users, AddFriendsAdapter.SearchUsersViewHolder>(options) {

    inner class SearchUsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.searchUsernameTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.searchNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUsersViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_search_users_layout, parent, false)
        return SearchUsersViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchUsersViewHolder, position: Int, model: Users) {
        holder.usernameTextView.text = model.username
        "${model.firstName} ${model.lastName}".also { holder.nameTextView.text = it }
    }
}
