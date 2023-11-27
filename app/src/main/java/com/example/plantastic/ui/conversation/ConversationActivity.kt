package com.example.plantastic.ui.conversation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Groups
import com.example.plantastic.models.Message
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.utilities.FirebaseNodes
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ConversationActivity : AppCompatActivity() {
    private lateinit var group: Groups
    private var adapter: ConversationAdapter? = null

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        recyclerView = findViewById(R.id.conversationsRecyclerView)

        val groupId = intent.getStringExtra(KEY_GROUP_ID)

        val firebaseDatabase: FirebaseDatabase =  FirebaseDatabase.getInstance()
        val groupsRepository = GroupsRepository()
        val messagesReference: DatabaseReference = firebaseDatabase.getReference(FirebaseNodes.MESSAGES_NODE)
        val messagesQuery = messagesReference.child(groupId!!)

        messagesQuery.get().addOnSuccessListener {
            Log.d(TAG, "messages --> $it")
        }

        val options = FirebaseRecyclerOptions.Builder<Message>().setQuery(messagesQuery, Message::class.java).build()

        val currUser = UsersAuthRepository().getCurrentUser()
        val userId = currUser!!.uid

        groupsRepository.getGroupById(groupId){
            if (it != null){
                group = it
                adapter = ConversationAdapter(options, userId, group.groupType == "group")
                recyclerView.adapter = adapter
                val manager = LinearLayoutManager(this)
                recyclerView.layoutManager = manager

                adapter?.startListening()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        adapter?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        adapter?.stopListening()
    }

    companion object {
        const val KEY_GROUP_ID = "KEY_GROUP_ID"
        private const val TAG = "Pln ConversationActivity"
    }
}