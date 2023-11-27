package com.example.plantastic.ui.chats

import android.text.format.DateFormat
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.databinding.ChatGroupBinding
import com.example.plantastic.models.Chat
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import java.util.Calendar

class ChatsAdapter (private val options: FirebaseRecyclerOptions<Chat>,
                    private val currentUserName: String?
) : FirebaseRecyclerAdapter<Chat, RecyclerView.ViewHolder>(options){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.chat_group, parent, false)
        val binding = ChatGroupBinding.bind(view)
        return GroupChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Chat) {
        (holder as GroupChatViewHolder).bind(model, getRef(position))
    }

    inner class GroupChatViewHolder(private val binding: ChatGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Chat, ref: DatabaseReference) {
            Log.i(TAG,"Curr chat item ref --> $ref")
            binding.chatName.text = item.name
            binding.lastMsgContent.text = item.latestMessage?.content ?: "No Messages"
            binding.lastMsgSender.text = item.latestMessage?.senderId ?: "Anonymous"

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = item.latestMessage!!.timestamp!!
            val date: String = DateFormat.format("MMM dd, yyyy", calendar).toString()
            binding.lastMsgTimestamp.text = date
        }
    }

    companion object {
        private const val TAG = "Pln ChatsAdapter"
    }
}