package com.example.plantastic.ui.transactions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Message
import com.example.plantastic.models.Transaction
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.TransactionsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.ui.conversation.ConversationActivity
import com.example.plantastic.utilities.FirebaseNodes
import com.example.plantastic.utilities.WrapContentLinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TransactionsActivity : AppCompatActivity() {

    companion object {
        const val GROUP_ID = "GROUP_ID"
    }

    private lateinit var groupsRepository: GroupsRepository
    private lateinit var transactionsRepository: TransactionsRepository
    private lateinit var usersAuthRepository: UsersAuthRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        transactionsRepository = TransactionsRepository()
        usersAuthRepository = UsersAuthRepository()
        groupsRepository = GroupsRepository()

        recyclerView = findViewById(R.id.transactionsRecyclerView)

        val groupId = intent.getStringExtra(GROUP_ID)
        if (groupId == null){
            finish()
        }

//        val transactionQuery = transactionsRepository.getTransactionsForGroup(groupId!!)

        var firebaseDatabase: FirebaseDatabase =  FirebaseDatabase.getInstance()
        var transactionsReference: DatabaseReference = firebaseDatabase.getReference(FirebaseNodes.TRANSACTIONS_NODE)
        val transactionQuery =  transactionsReference.orderByChild(FirebaseNodes.TRANSACTIONS_GROUP_NODE).equalTo(groupId)

        val options = FirebaseRecyclerOptions.Builder<Transaction>()
            .setQuery(transactionQuery, Transaction::class.java).build()

        adapter = TransactionsAdapter(options)
        recyclerView.adapter = adapter
        val manager = WrapContentLinearLayoutManager(this)
        recyclerView.layoutManager = manager
        adapter.startListening()
    }

    override fun onPause() {
        super.onPause()
        adapter?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        adapter?.startListening()
    }

}