package com.example.plantastic.ui.chats

import android.annotation.SuppressLint
import android.text.format.DateFormat
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.databinding.ChatGroupBinding
import com.example.plantastic.databinding.ChatIndividualBinding
import com.example.plantastic.models.Groups
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import java.util.Calendar

class ChatsAdapter (
    private val options: FirebaseRecyclerOptions<Groups>,
    private val userId: String
) : FirebaseRecyclerAdapter<Groups, RecyclerView.ViewHolder>(options){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_GROUP){
            val view = inflater.inflate(R.layout.chat_group, parent, false)
            val binding = ChatGroupBinding.bind(view)
            GroupChatViewHolder(binding)
        } else {
            val view = inflater.inflate(R.layout.chat_individual, parent, false)
            val binding = ChatIndividualBinding.bind(view)
            IndividualChatViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Groups) {
        if (model.groupType == "individual"){
            (holder as IndividualChatViewHolder).bind(model, getRef(position))
        } else {
            (holder as GroupChatViewHolder).bind(model, getRef(position))
        }
    }

    inner class GroupChatViewHolder(private val binding: ChatGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Groups, ref: DatabaseReference) {
            Log.i(TAG,"Curr chat item ref --> $ref")
            binding.chatName.text = item.name
            if (item.latestMessage != null){
                binding.lastMsgContent.text = item.latestMessage.content
                binding.lastMsgSender.text = "${item.latestMessage.senderId}: "

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = item.latestMessage.timestamp!!
                val date: String = DateFormat.format("MMM dd, yyyy", calendar).toString()
                binding.lastMsgTimestamp.text = date
            }
        }
    }

    inner class IndividualChatViewHolder(private val binding: ChatIndividualBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Groups, ref: DatabaseReference) {
            Log.i(TAG,"Curr chat item ref --> $ref")
            val participants = item.participants!!.keys.toList()
            val otherParticipantId = if (participants[0] == userId) participants[1] else participants[0]
//            val otherParticipant = UsersRepository().getUserById(otherParticipantId)
//            binding.chatName.text = "${otherParticipant?.firstName} ${otherParticipant?.lastName}"

            if (item.latestMessage != null) {
                binding.lastMsgContent.text = item.latestMessage.content

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = item.latestMessage.timestamp!!
                val date: String = DateFormat.format("MMM dd, yyyy", calendar).toString()
                binding.lastMsgTimestamp.text = date
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (options.snapshots[position].groupType == "group") VIEW_TYPE_GROUP else VIEW_TYPE_INDIVIDUAL
    }

    companion object {
        private const val TAG = "Pln ChatsAdapter"
        const val VIEW_TYPE_GROUP = 1
        const val VIEW_TYPE_INDIVIDUAL = 2
    }
}