package com.example.plantastic.ui.new_group

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.databinding.FragmentNewGroupBinding
import com.example.plantastic.models.Users
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.utilities.WrapContentLinearLayoutManager

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

    private lateinit var allFriendsList: ArrayList<Users>
    private lateinit var filteredFriendsList: ArrayList<Users>
    private lateinit var groupMembers: ArrayList<Users>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewGroupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        usersRepository = UsersRepository()
        usersAuthRepository = UsersAuthRepository()

        val currUser = usersAuthRepository.getCurrentUser()

        newGroupViewModel =
            ViewModelProvider(this)[NewGroupViewModel::class.java]
        newGroupViewModel.getFriendsList(currUser!!.uid)

        recyclerView = root.findViewById(R.id.addNewGroupRecyclerView)
        recyclerView.layoutManager = WrapContentLinearLayoutManager(requireContext())
        noUsersEditText = root.findViewById(R.id.noGroupFoundTextView)
        editTextSearch = root.findViewById(R.id.editTextSearchGroup)

        filteredFriendsList = ArrayList()
        groupMembers = ArrayList()

        adapter = NewGroupAdapter(filteredFriendsList, currUser.uid, newGroupViewModel)
        recyclerView.adapter = adapter

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
            }
        }

        return root
    }
}