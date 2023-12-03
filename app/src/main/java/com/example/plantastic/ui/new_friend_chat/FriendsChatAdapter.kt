package com.example.plantastic.ui.new_friend_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Users
import com.example.plantastic.repository.UsersRepository

class FriendsChatAdapter(
    private var dataList: List<Users>,
    private val userId: String
) :
    RecyclerView.Adapter<FriendsChatAdapter.SearchFriendsViewHolder>() {

    private var usersRepository = UsersRepository()

    inner class SearchFriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.searchUsernameTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.searchNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFriendsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_search_users_layout, parent, false)
        return SearchFriendsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: SearchFriendsViewHolder, position: Int) {
        val model = dataList[position]
        holder.usernameTextView.text = model.username
        "${model.firstName} ${model.lastName}".also { holder.nameTextView.text = it }
    }
}