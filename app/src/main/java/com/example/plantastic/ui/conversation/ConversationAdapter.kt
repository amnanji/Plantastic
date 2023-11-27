package com.example.plantastic.ui.conversation

import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.databinding.MessageGroupBinding
import com.example.plantastic.databinding.MessageIndividualBinding
import com.example.plantastic.models.Groups
import com.example.plantastic.models.Message
import com.example.plantastic.repository.UsersRepository
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import java.util.Calendar

class ConversationAdapter(
    private val options: FirebaseRecyclerOptions<Message>,
    private val userId: String,
    private val isGroup: Boolean
) : FirebaseRecyclerAdapter<Message, RecyclerView.ViewHolder>(options){
    val usersRepository = UsersRepository()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (isGroup){
            val view = inflater.inflate(R.layout.message_group, parent, false)
            val binding = MessageGroupBinding.bind(view)
            GroupChatMessagesViewHolder(binding)
        } else {
            val view = inflater.inflate(R.layout.message_individual, parent, false)
            val binding = MessageIndividualBinding.bind(view)
            IndividualChatMessagesViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Message) {
        if (isGroup){
            (holder as GroupChatMessagesViewHolder).bind(model, getRef(position))
        } else {
            (holder as IndividualChatMessagesViewHolder).bind(model, getRef(position))
        }
    }

    inner class GroupChatMessagesViewHolder(private val binding: MessageGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message, ref: DatabaseReference) {
            Log.i(TAG,"Curr msg item --> $item")
            usersRepository.getUserById(item.senderId!!){
                if (it != null) {
                    binding.messageSender.text = "${it.firstName} ${it.lastName}: "
                }
            }
            binding.messageText.text = item.content

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = item.timestamp!!
            val date: String = DateFormat.format("hh:mm a", calendar).toString()
            binding.messageTimestamp.text = date

//            if (item.senderId == userId){
//                val params = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//                params.gravity = Gravity.END
//                binding.outerLayout.layoutParams = params
//            }
        }
    }

    inner class IndividualChatMessagesViewHolder(private val binding: MessageIndividualBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message, ref: DatabaseReference) {
            Log.i(TAG,"Curr msg item --> $item")
            binding.messageText.text = item.content

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = item.timestamp!!
            val date: String = DateFormat.format("hh:mm a", calendar).toString()
            binding.messageTimestamp.text = date
        }
    }

    companion object {
        private const val TAG = "Pln MessagesAdapter"
    }
}