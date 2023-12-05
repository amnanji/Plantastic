package com.example.plantastic.ui.transactions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Transaction
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.TransactionsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.utilities.WrapContentLinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TransactionsActivity : AppCompatActivity() {

    companion object {
        const val GROUP_ID = "GROUP_ID"
        const val NUMBER_OF_MEMBERS = "NUMBER_OF_MEMBERS"
    }

    private lateinit var groupsRepository: GroupsRepository
    private lateinit var usersRepository: UsersRepository
    private lateinit var transactionsRepository: TransactionsRepository
    private lateinit var usersAuthRepository: UsersAuthRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        supportActionBar?.title = getString(R.string.transactions)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        transactionsRepository = TransactionsRepository()
        usersAuthRepository = UsersAuthRepository()
        groupsRepository = GroupsRepository()
        usersRepository = UsersRepository()

        recyclerView = findViewById(R.id.transactionsRecyclerView)
        val groupNameTextView = findViewById<TextView>(R.id.transactionsGroupNameTextView)
        val viewBalanceButton = findViewById<Button>(R.id.transactionsViewBalancesButton)
        val transactionsFab: FloatingActionButton = findViewById(R.id.addTransactionFab)

        val currUser = usersAuthRepository.getCurrentUser()
        if(currUser == null){
            finish()
        }

        val groupId = intent.getStringExtra(GROUP_ID)
        val numbParticipants = intent.getIntExtra(NUMBER_OF_MEMBERS, -1)
        if (groupId == null || numbParticipants == -1){
            finish()
        }

        val transactionQuery = transactionsRepository.getTransactionsForGroup(groupId!!)

        val options = FirebaseRecyclerOptions.Builder<Transaction>()
            .setQuery(transactionQuery, Transaction::class.java).build()

        adapter = TransactionsAdapter(options, currUser!!.uid, numbParticipants)
        recyclerView.adapter = adapter
        val manager = WrapContentLinearLayoutManager(this)
        recyclerView.layoutManager = manager
        adapter.startListening()

        groupsRepository.getGroupById(groupId){ group ->
            if (group != null){
                if (group.groupType == "group"){
                    groupNameTextView.text = group.name
                } else {
                    val participants = group.participants!!.keys.toList()
                    val otherParticipantId =
                        if (participants[0] == currUser.uid) participants[1] else participants[0]
                    usersRepository.getUserById(otherParticipantId) {
                        if (it != null) {
                            val chatName = getString(
                                R.string.name_placeholder,
                                it.firstName,
                                it.lastName
                            )
                            groupNameTextView.text = chatName
                        }
                    }
                }
                val balances = group.balances!![currUser.uid]!!
                viewBalanceButton.setOnClickListener {
                    val settleBalancesDialog = SettleBalanceDialog(balances)
                    val bundle = Bundle()
                    bundle.putString(SettleBalanceDialog.KEY_USER_ID, currUser.uid)
                    bundle.putString(SettleBalanceDialog.KEY_GROUP_ID, groupId)
                    settleBalancesDialog.arguments = bundle
                    settleBalancesDialog.show(this.supportFragmentManager, SettleBalanceDialog.TAG_SETTLE_BALANCE)
                }
            }
        }

        transactionsFab.setOnClickListener{
            val dialog = ExpenseDialog()
            val bundle = Bundle()
            bundle.putString(ExpenseDialog.KEY_USER_ID, currUser.uid)
            bundle.putString(ExpenseDialog.KEY_GROUP_ID, groupId)
            dialog.arguments = bundle
            dialog.show(this.supportFragmentManager, ExpenseDialog.TAG_ADD_EXPENSE)
        }
    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}