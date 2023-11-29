package com.example.plantastic.ui.conversation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Groups
import com.example.plantastic.models.Message
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.utilities.FirebaseNodes
import com.example.plantastic.utilities.WrapContentLinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class ConversationActivity : AppCompatActivity() {
    private lateinit var group: Groups
    private var adapter: ConversationAdapter? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var btnAdd: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        recyclerView = findViewById(R.id.conversationsRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        btnSend = findViewById(R.id.btnSend)
        btnAdd = findViewById(R.id.btnAdd)

        val chatName = intent.getStringExtra(KEY_GROUP_NAME)
        Log.d(TAG, "chatName --> $chatName")
        supportActionBar?.title = if (chatName == null) chatName else getString(R.string.app_name)
        actionBar?.title = if (chatName == null) chatName else getString(R.string.app_name)


        btnSend.isEnabled = false

        val groupId = intent.getStringExtra(KEY_GROUP_ID)

        val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
        val groupsRepository = GroupsRepository()
        val groupsReference: DatabaseReference =
            firebaseDatabase.getReference(FirebaseNodes.GROUPS_NODE)
        val messagesReference: DatabaseReference =
            firebaseDatabase.getReference(FirebaseNodes.MESSAGES_NODE)
        val messagesQuery = messagesReference.child(groupId!!)
        val orderedMessagesQuery =
            messagesReference.child(groupId).orderByChild(FirebaseNodes.MESSAGES_TIMESTAMP_NODE)

        orderedMessagesQuery.get().addOnSuccessListener {
            Log.d(TAG, "messages --> $it")
        }

        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(orderedMessagesQuery, Message::class.java).build()

        val currUser = UsersAuthRepository().getCurrentUser()
        val userId = currUser!!.uid

        groupsRepository.getGroupById(groupId) {
            if (it != null) {
                group = it
                adapter = ConversationAdapter(options, userId, group.groupType == "group")
                recyclerView.adapter = adapter
                val manager = WrapContentLinearLayoutManager(this)
                manager.stackFromEnd
                recyclerView.layoutManager = manager

                adapter?.startListening()
            }
        }

        messageEditText.addTextChangedListener {
            if (it != null) {
                btnSend.isEnabled = it.isNotBlank()
            }
        }

        btnSend.setOnClickListener {
            val msg = Message(
                "text",
                userId,
                messageEditText.text.toString(),
                Calendar.getInstance().timeInMillis
            )
            val msgRef = messagesQuery.push()
            msgRef.setValue(msg).addOnSuccessListener {
                groupsReference.child(groupId).child(FirebaseNodes.GROUPS_LATEST_MESSAGE_NODE)
                    .setValue(msg).addOnFailureListener {
                        Log.d(TAG, "Failed to set new msg to group's latest message --> $msgRef")
                        it.printStackTrace()
                    }

                messageEditText.setText("")
                if (adapter != null) {
                    recyclerView.smoothScrollToPosition(adapter!!.itemCount.minus(1))
                }
            }.addOnFailureListener {
                Log.d(TAG, "Could not set new msg --> $msgRef")
                it.printStackTrace()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        adapter?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        adapter?.startListening()
    }

    companion object {
        const val KEY_GROUP_ID = "KEY_GROUP_ID"
        const val KEY_GROUP_NAME = "KEY_GROUP_NAME"
        private const val TAG = "Pln ConversationActivity"
    }
}