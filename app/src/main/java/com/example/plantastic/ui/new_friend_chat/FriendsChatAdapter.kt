package com.example.plantastic.ui.new_friend_chat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Users
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.ui.conversation.ConversationActivity

class FriendsChatAdapter(
    private var dataList: List<Users>,
    private val userId: String
) :
    RecyclerView.Adapter<FriendsChatAdapter.SearchFriendsViewHolder>() {

    private val groupsRepository = GroupsRepository()

    inner class SearchFriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.searchUsernameTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.searchNameTextView)
        val searchContainer: RelativeLayout = itemView.findViewById(R.id.searchContainer)
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
        holder.searchContainer.setOnClickListener {
            groupsRepository.getGroupIdForUsers(userId, model.id!!){
                if (it == null){
                    groupsRepository.createGroupForUsers(arrayListOf(userId, model.id), null){ groupId ->
                        if (groupId != null){
                            navigateToConversationsActivity(holder.itemView.context, groupId, model.username!!)
                        }
                    }
                }
                else{
                    navigateToConversationsActivity(holder.itemView.context, it, model.username!!)
                }
            }
        }
    }

    private fun navigateToConversationsActivity(context: Context, id: String, username: String){
        val intent = Intent(context, ConversationActivity::class.java)
        intent.putExtra(ConversationActivity.KEY_GROUP_ID, id)
        intent.putExtra(ConversationActivity.KEY_GROUP_NAME, username)
        context.startActivity(intent)
    }
}