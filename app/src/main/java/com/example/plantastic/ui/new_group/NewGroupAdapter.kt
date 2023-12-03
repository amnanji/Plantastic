package com.example.plantastic.ui.new_group

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Users
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.ui.conversation.ConversationActivity
import com.example.plantastic.ui.new_friend_chat.FriendsChatAdapter

class NewGroupAdapter (
    private var dataList: List<Users>,
    private val userId: String,
    private val newGroupViewModel: NewGroupViewModel
) :
    RecyclerView.Adapter<NewGroupAdapter.NewGroupViewHolder>() {

    private val groupsRepository = GroupsRepository()

    inner class NewGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.searchUsernameTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.searchNameTextView)
        val searchContainer: RelativeLayout = itemView.findViewById(R.id.searchContainer)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxSearchUsers)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewGroupViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_search_users_layout, parent, false)
        return NewGroupViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: NewGroupViewHolder, position: Int) {
        val model = dataList[position]
        holder.usernameTextView.text = model.username
        "${model.firstName} ${model.lastName}".also { holder.nameTextView.text = it }
        holder.checkBox.visibility = View.VISIBLE
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                newGroupViewModel.addToMembersList(model)
            }
            else{
                newGroupViewModel.removeFromMembersList(model)
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