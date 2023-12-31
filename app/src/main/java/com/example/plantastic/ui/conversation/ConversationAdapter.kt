package com.example.plantastic.ui.conversation

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.databinding.MessageCurrUserBinding
import com.example.plantastic.databinding.MessageGroupBinding
import com.example.plantastic.databinding.MessageIndividualBinding
import com.example.plantastic.models.Message
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.utilities.DateTimeUtils
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class ConversationAdapter(
    private val options: FirebaseRecyclerOptions<Message>,
    private val userId: String,
    private val isGroup: Boolean
) : FirebaseRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {
    val usersRepository = UsersRepository()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_CURR_USER) {
            val view = inflater.inflate(R.layout.message_curr_user, parent, false)
            val binding = MessageCurrUserBinding.bind(view)
            CurrUserMessageViewHolder(binding)
        } else if (isGroup || viewType == VIEW_TYPE_AI) {
            val view = inflater.inflate(R.layout.message_group, parent, false)
            val binding = MessageGroupBinding.bind(view)
            GroupChatMessagesViewHolder(view.context, binding)
        } else {
            val view = inflater.inflate(R.layout.message_individual, parent, false)
            val binding = MessageIndividualBinding.bind(view)
            IndividualChatMessagesViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Message) {
        if (model.senderId == userId) {
            (holder as CurrUserMessageViewHolder).bind(model)
        } else if (isGroup || model.messageType == "AI Message") {
            (holder as GroupChatMessagesViewHolder).bind(model)
        } else {
            (holder as IndividualChatMessagesViewHolder).bind(model)
        }
    }

    inner class GroupChatMessagesViewHolder(
        private val context: Context, private val binding: MessageGroupBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            if (item.messageType == "AI Message") {
                binding.messageSender.text = context.getString(R.string.msg_sender_ai_with_colon)
                binding.root.backgroundTintList =
                    ColorStateList.valueOf(context.getColor(R.color.ai_msg))
            } else {
                usersRepository.getUserById(item.senderId!!) {
                    if (it != null) {
                        binding.messageSender.text = context.getString(
                            R.string.name_placeholder_with_colon, it.firstName, it.lastName
                        )
                    }
                }
            }
            binding.messageText.text = item.content
            binding.messageTimestamp.text = item.timestamp?.let { DateTimeUtils.getDateString(it) }
        }
    }

    inner class IndividualChatMessagesViewHolder(private val binding: MessageIndividualBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.messageText.text = item.content
            binding.messageTimestamp.text = item.timestamp?.let { DateTimeUtils.getDateString(it) }
        }
    }

    inner class CurrUserMessageViewHolder(private val binding: MessageCurrUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.messageText.text = item.content
            binding.messageTimestamp.text = item.timestamp?.let { DateTimeUtils.getDateString(it) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (options.snapshots[position].messageType == "AI Message") {
            VIEW_TYPE_AI
        } else if (options.snapshots[position].senderId == userId) {
            VIEW_TYPE_CURR_USER
        } else {
            VIEW_TYPE_NOT_CURR_USER
        }
    }

    companion object {
        private const val TAG = "Pln MessagesAdapter"
        private const val VIEW_TYPE_CURR_USER = 1
        private const val VIEW_TYPE_NOT_CURR_USER = 0
        private const val VIEW_TYPE_AI = 2
    }
}