package com.example.plantastic.ui.new_group

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.databinding.FragmentNewGroupBinding
import com.example.plantastic.models.Users
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.ui.conversation.ConversationActivity
import com.example.plantastic.utilities.WrapContentLinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.core.widget.addTextChangedListener

class NewGroupChatFragment : Fragment() {

    private var _binding: FragmentNewGroupBinding? = null
    private val binding get() = _binding!!

    private lateinit var newGroupViewModel: NewGroupViewModel

    private lateinit var adapter: NewGroupAdapter
    private lateinit var usersRepository: UsersRepository
    private lateinit var usersAuthRepository: UsersAuthRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var noUsersEditText: TextView
    private lateinit var editTextSearch: EditText
    private lateinit var groupMembersRecyclerView: RecyclerView
    private lateinit var groupsRepository: GroupsRepository
    private lateinit var groupNameEditText: TextInputEditText
    private lateinit var groupNameInputLayout: TextInputLayout

    private lateinit var filteredFriendsList: ArrayList<Users>
    private lateinit var groupMembers: ArrayList<Users>


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewGroupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        usersRepository = UsersRepository()
        usersAuthRepository = UsersAuthRepository()
        groupsRepository = GroupsRepository()

        val currUser = usersAuthRepository.getCurrentUser()

        newGroupViewModel =
            ViewModelProvider(this)[NewGroupViewModel::class.java]
        newGroupViewModel.getFriendsList(currUser!!.uid)

        recyclerView = root.findViewById(R.id.addNewGroupRecyclerView)
        recyclerView.layoutManager = WrapContentLinearLayoutManager(requireContext())
        noUsersEditText = root.findViewById(R.id.noGroupFoundTextView)
        editTextSearch = root.findViewById(R.id.editTextSearchGroup)
        groupNameEditText = root.findViewById(R.id.newGroupNameEditText)
        groupNameInputLayout = root.findViewById(R.id.newGroupNameInputLayout)

        groupNameEditText.addTextChangedListener {
            groupNameInputLayout.error = null
        }

        filteredFriendsList = ArrayList()
        groupMembers = ArrayList()

        adapter = NewGroupAdapter(filteredFriendsList, currUser.uid, newGroupViewModel)
        recyclerView.adapter = adapter

        val submitButton = root.findViewById<Button>(R.id.createGroupButton)

        // Set up TextWatcher to filter data based on search input
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            // Help from - https://developer.android.com/reference/android/text/TextWatcher
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = charSequence.toString().trim()

                // Update the query only if the search string is not empty
                if (searchText.isNotEmpty()) {
                    newGroupViewModel.filterFriendsList(searchText)
                }
                // populate list with all friends
                else{
                    newGroupViewModel.filterFriendsList("")
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        newGroupViewModel.filteredFriendsList.observe(requireActivity()){
            if (it != null){
                filteredFriendsList.clear()
                filteredFriendsList.addAll(it)
                adapter.notifyDataSetChanged()
                if(it.isEmpty()){
                    noUsersEditText.visibility = View.VISIBLE
                }
                else{
                    noUsersEditText.visibility = View.GONE
                }
            }
        }

        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            if(adapter.itemCount == 0){
                noUsersEditText.visibility = View.VISIBLE
            }
        }, 200)

        // Set up the RecyclerView and its adapter
        groupMembersRecyclerView = root.findViewById(R.id.addNewGroupHorizontalRecyclerView)
        val groupMemberAdapter = GroupMembersAdapter(groupMembers)

        groupMembersRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        groupMembersRecyclerView.adapter = groupMemberAdapter

        newGroupViewModel.groupMembersList.observe(requireActivity()){
            if (it != null){
                groupMembers.clear()
                groupMembers.addAll(it)
                groupMemberAdapter.notifyDataSetChanged()
                if (groupMembers.size == 0){
                    root.findViewById<TextView>(R.id.groupMemberLabelTextView).visibility = View.GONE
                }
                else{
                    root.findViewById<TextView>(R.id.groupMemberLabelTextView).visibility = View.VISIBLE
                }
            }
        }

        submitButton.setOnClickListener{
            var isValidGroup = true
            val groupName = groupNameEditText.text.toString()
            if(groupNameEditText.text.toString().isEmpty()){
                groupNameInputLayout.error = getString(R.string.group_name_cannot_be_empty)
                isValidGroup = false
            }

            if(groupMembers.size < 2){
                Toast.makeText(requireContext(),
                    getString(R.string.group_participants_error),
                    Toast.LENGTH_SHORT).show()
                 isValidGroup = false
            }

            if (isValidGroup){
                val userIdForGroup = ArrayList<String>()
                userIdForGroup.add(currUser.uid)
                groupMembers.forEach {
                    it.id?.let { it1 -> userIdForGroup.add(it1) }
                }
                groupsRepository.createGroupForUsers(userIdForGroup, groupName){
                    if(it != null){
                        navigateToConversationsActivity(it.toString(), groupName)
                    }
                }
            }
        }
        return root
    }

    private fun navigateToConversationsActivity(id: String, groupName: String){
        val intent = Intent(requireContext(), ConversationActivity::class.java)
        intent.putExtra(ConversationActivity.KEY_GROUP_ID, id)
        intent.putExtra(ConversationActivity.KEY_GROUP_NAME, groupName)
        requireContext().startActivity(intent)
    }
}