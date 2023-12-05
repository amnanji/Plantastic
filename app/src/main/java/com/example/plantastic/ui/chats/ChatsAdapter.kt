package com.example.plantastic.ui.chats

import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.databinding.ChatGroupBinding
import com.example.plantastic.databinding.ChatIndividualBinding
import com.example.plantastic.models.Groups
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.ui.conversation.ConversationActivity
import com.example.plantastic.utilities.IconUtil
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import java.util.Calendar

class ChatsAdapter(
    private val options: FirebaseRecyclerOptions<Groups>,
    private val userId: String
) : FirebaseRecyclerAdapter<Groups, RecyclerView.ViewHolder>(options) {

    val usersRepository = UsersRepository()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_GROUP) {
            val view = inflater.inflate(R.layout.chat_group, parent, false)
            val binding = ChatGroupBinding.bind(view)
            GroupChatViewHolder(view.context, binding)
        } else {
            val view = inflater.inflate(R.layout.chat_individual, parent, false)
            val binding = ChatIndividualBinding.bind(view)
            IndividualChatViewHolder(view.context, binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Groups) {
        if (model.groupType == "individual") {
            (holder as IndividualChatViewHolder).bind(model)
        } else {
            (holder as GroupChatViewHolder).bind(model)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (options.snapshots[position].groupType == "group") VIEW_TYPE_GROUP else VIEW_TYPE_INDIVIDUAL
    }

    private fun getDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return DateFormat.format("MMM dd, yyyy", calendar).toString()
    }

    inner class GroupChatViewHolder(private val context: Context, private val binding: ChatGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Groups) {
            binding.chatName.text = item.name
            if (item.latestMessage != null) {
                binding.lastMsgContent.text = item.latestMessage.content
                binding.lastMsgTimestamp.text = getDate(item.latestMessage.timestamp!!)

                if (item.latestMessage.senderId != null) {
                    if (item.latestMessage.senderId == userId) {
                        binding.lastMsgSender.text = context.getString(R.string.you)
                    } else {
                        usersRepository.getUserById(item.latestMessage.senderId) {
                            if (it != null) {
                                binding.lastMsgSender.text = context.getString(
                                    R.string.name_placeholder_with_colon,
                                    it.firstName,
                                    it.lastName
                                )
                            }
                        }
                    }
                }
            } else {
                binding.lastMsgTimestamp.text = item.timestampGroupCreated?.let { getDate(it) }
            }

            val iconUtil = IconUtil(itemView.context)
            val drawable = iconUtil.getIcon(item.name!!, "", item.color!!)
            binding.messengerImageView.setImageDrawable(drawable)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ConversationActivity::class.java)
                intent.putExtra(ConversationActivity.KEY_GROUP_ID, item.id)
                intent.putExtra(ConversationActivity.KEY_GROUP_NAME, item.name)
                itemView.context.startActivity(intent)
            }
        }
    }

    inner class IndividualChatViewHolder(private val context: Context, private val binding: ChatIndividualBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Groups) {
            val participants = item.participants!!.keys.toList()
            val otherParticipantId =
                if (participants[0] == userId) participants[1] else participants[0]
            var chatName = "Plantastic"
            usersRepository.getUserById(otherParticipantId) {
                if (it != null) {
                    chatName = context.getString(
                        R.string.name_placeholder,
                        it.firstName,
                        it.lastName
                    )
                    binding.chatName.text = chatName

                    val iconUtil= IconUtil(itemView.context)
                    val drawable = iconUtil.getIcon(it.firstName!!, it.lastName!!, it.color!!)
                    binding.messengerImageView.setImageDrawable(drawable)
                }

            }

            if (item.latestMessage != null) {
                binding.lastMsgContent.text = item.latestMessage.content
                binding.lastMsgTimestamp.text = getDate(item.latestMessage.timestamp!!)
            } else {
                binding.lastMsgTimestamp.text = item.timestampGroupCreated?.let { getDate(it) }
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ConversationActivity::class.java)
                intent.putExtra(ConversationActivity.KEY_GROUP_ID, item.id)
                intent.putExtra(ConversationActivity.KEY_GROUP_NAME, chatName)
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        private const val TAG = "Pln ChatsAdapter"
        const val VIEW_TYPE_GROUP = 1
        const val VIEW_TYPE_INDIVIDUAL = 2
    }
}