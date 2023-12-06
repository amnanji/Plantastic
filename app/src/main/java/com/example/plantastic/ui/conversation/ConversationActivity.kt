package com.example.plantastic.ui.conversation

import android.app.ActionBar.LayoutParams
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.ai.requests.ChatGptMessaging
import com.example.plantastic.models.Groups
import com.example.plantastic.models.Message
import com.example.plantastic.models.Users
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.ui.events.AddEventsDialog
import com.example.plantastic.ui.login.LoginActivity
import com.example.plantastic.ui.toDo.AddTodoItemDialog
import com.example.plantastic.ui.transactions.ExpenseDialog
import com.example.plantastic.ui.transactions.SettleBalanceDialog
import com.example.plantastic.utilities.FirebaseNodes
import com.example.plantastic.utilities.WrapContentLinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class ConversationActivity : AppCompatActivity() {
    private lateinit var group: Groups
    private var adapter: ConversationAdapter? = null
    private var userId: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var btnAdd: ImageButton
    private var usersList: ArrayList<Users> = ArrayList()
    private var chatGPT: ChatGptMessaging = ChatGptMessaging(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        recyclerView = findViewById(R.id.conversationsRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        btnSend = findViewById(R.id.btnSend)
        btnAdd = findViewById(R.id.btnAdd)

        val chatName = intent.getStringExtra(KEY_GROUP_NAME)
        supportActionBar?.title = chatName ?: getString(R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnSend.isEnabled = false

        val groupId = intent.getStringExtra(KEY_GROUP_ID)

        val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
        val groupsRepository = GroupsRepository()
        val groupsReference: DatabaseReference =
            firebaseDatabase.getReference(FirebaseNodes.GROUPS_NODE)
        val usersRepository = UsersRepository()
        val messagesReference: DatabaseReference =
            firebaseDatabase.getReference(FirebaseNodes.MESSAGES_NODE)
        val messagesQuery = messagesReference.child(groupId!!)
        val orderedMessagesQuery =
            messagesReference.child(groupId).orderByChild(FirebaseNodes.MESSAGES_TIMESTAMP_NODE)

        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(orderedMessagesQuery, Message::class.java).build()

        val currUser = UsersAuthRepository().getCurrentUser()
        if (currUser == null) {
            navigateToLoginActivity()
        }
        userId = currUser!!.uid

        groupsRepository.getGroupById(groupId) {
            if (it != null) {
                group = it
                adapter = ConversationAdapter(options, userId!!, group.groupType == "group")
                recyclerView.adapter = adapter
                val manager = WrapContentLinearLayoutManager(this)
                manager.stackFromEnd = true
                recyclerView.layoutManager = manager

                adapter?.registerAdapterDataObserver(
                    ConversationScrollToBottomObserver(recyclerView, adapter!!, manager)
                )

                val userIds = ArrayList<String>()
                for (participantId in group.participants?.keys!!) {
                    userIds.add(participantId)
                }

                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("step 1", "conversationActivity:CoroutineScope")
                    usersList = usersRepository.getUsersById(userIds)
                    chatGPT.setPreferences(chatGPT.context, usersList)
                }
                adapter?.startListening()
            }
        }

        messageEditText.addTextChangedListener {
            if (it != null) {
                btnSend.isEnabled = it.isNotBlank()
            }
        }

        btnAdd.setOnClickListener {
            val popupView = layoutInflater.inflate(R.layout.dialog_chat_add_new, null)
            val popupWindow = PopupWindow(
                popupView,
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                true
            )

            popupView.findViewById<ImageButton>(R.id.addEventAddDialog).setOnClickListener {
                popupWindow.dismiss()
                openRelevantDialog(DIALOG_TYPE_NEW_EVENT)
            }

            popupView.findViewById<ImageButton>(R.id.addTodoAddDialog).setOnClickListener {
                popupWindow.dismiss()
                openRelevantDialog(DIALOG_TYPE_NEW_TODO)
            }

            popupView.findViewById<ImageButton>(R.id.addExpenseAddDialog).setOnClickListener {
                popupWindow.dismiss()
                openRelevantDialog(DIALOG_TYPE_NEW_EXPENSE)
            }

            popupView.findViewById<ImageButton>(R.id.settleBalanceAddDialog).setOnClickListener {
                popupWindow.dismiss()
                openRelevantDialog(DIALOG_TYPE_NEW_SETTLE_UP)
            }


            popupWindow.showAsDropDown(btnAdd, 0, -btnAdd.height - 210, Gravity.TOP)
        }

        btnSend.setOnClickListener {
            val msg = Message(
                "text",
                userId,
                messageEditText.text.toString(),
                Calendar.getInstance().timeInMillis
            )
            CoroutineScope(Dispatchers.IO).launch {
                if (msg.content!!.contains("@AI", ignoreCase = true)) {
                    chatGPT.getResponse(msg, groupId)
                } else {
                    Log.d("revs", "AI was not called")
                }
            }
            val msgRef = messagesQuery.push()
            msgRef.setValue(msg).addOnSuccessListener {
                groupsReference.child(groupId).child(FirebaseNodes.GROUPS_LATEST_MESSAGE_NODE)
                    .setValue(msg).addOnFailureListener {
                        Log.d(TAG, "Failed to set new msg to group's latest message --> $msgRef")
                        it.printStackTrace()
                    }

                messageEditText.setText("")
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openRelevantDialog(dialogType: String) {
        if (userId == null || group.id == null) {
            return
        }
        when (dialogType) {
            DIALOG_TYPE_NEW_TODO -> {
                val dialog = AddTodoItemDialog()
                val bundle = Bundle()
                bundle.putString(AddTodoItemDialog.KEY_USER_ID, userId)
                bundle.putString(AddTodoItemDialog.KEY_GROUP_ID, group.id)
                dialog.arguments = bundle

                dialog.show(supportFragmentManager, AddTodoItemDialog.TAG_ADD_TODO_ITEM)
            }

            DIALOG_TYPE_NEW_EVENT -> {
                val dialog = AddEventsDialog()
                val bundle = Bundle()
                bundle.putString(AddEventsDialog.KEY_GROUP_ID, group.id)
                dialog.arguments = bundle
                dialog.show(supportFragmentManager, AddEventsDialog.TAG_ADD_EVENT)
            }

            DIALOG_TYPE_NEW_EXPENSE -> {
                val dialog = ExpenseDialog()
                val bundle = Bundle()
                bundle.putString(ExpenseDialog.KEY_USER_ID, userId)
                bundle.putString(ExpenseDialog.KEY_GROUP_ID, group.id)
                dialog.arguments = bundle
                dialog.show(supportFragmentManager, ExpenseDialog.TAG_ADD_EXPENSE)
            }

            DIALOG_TYPE_NEW_SETTLE_UP -> {
                val settleBalancesDialog = SettleBalanceDialog()
                val bundle = Bundle()
                bundle.putString(SettleBalanceDialog.KEY_USER_ID, userId)
                bundle.putString(SettleBalanceDialog.KEY_GROUP_ID, group.id)
                settleBalancesDialog.arguments = bundle
                settleBalancesDialog.show(
                    supportFragmentManager,
                    SettleBalanceDialog.TAG_SETTLE_BALANCE
                )
            }
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        const val KEY_GROUP_ID = "KEY_GROUP_ID"
        const val KEY_GROUP_NAME = "KEY_GROUP_NAME"
        private const val DIALOG_TYPE_NEW_EVENT = "DIALOG_TYPE_NEW_EVENT"
        private const val DIALOG_TYPE_NEW_TODO = "DIALOG_TYPE_NEW_TODO"
        private const val DIALOG_TYPE_NEW_EXPENSE = "DIALOG_TYPE_NEW_EXPENSE"
        private const val DIALOG_TYPE_NEW_SETTLE_UP = "DIALOG_TYPE_NEW_SETTLE_UP"
        private const val TAG = "Pln ConversationActivity"
    }
}