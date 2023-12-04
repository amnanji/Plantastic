package com.example.plantastic.ui.new_friend_chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.databinding.FragmentNewIndividualChatBinding
import com.example.plantastic.models.Users
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.utilities.WrapContentLinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions

class NewIndividualChatFragment : Fragment() {

    private var _binding: FragmentNewIndividualChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var friendsChatViewModel: FriendsChatViewModel

    private lateinit var adapter: FriendsChatAdapter
    private lateinit var usersRepository: UsersRepository
    private lateinit var usersAuthRepository: UsersAuthRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var noUsersEditText: TextView
    private lateinit var editTextSearch: EditText

    private lateinit var allFriendsList: ArrayList<Users>
    private lateinit var filteredFriendsList: ArrayList<Users>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewIndividualChatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        usersRepository = UsersRepository()
        usersAuthRepository = UsersAuthRepository()

        val currUser = usersAuthRepository.getCurrentUser()

        friendsChatViewModel =
            ViewModelProvider(this)[FriendsChatViewModel::class.java]
        friendsChatViewModel.getFriendsList(currUser!!.uid)

        recyclerView = root.findViewById(R.id.addFriendsChatRecyclerView)
        recyclerView.layoutManager = WrapContentLinearLayoutManager(requireContext())
        noUsersEditText = root.findViewById(R.id.noFriendsFoundTextView)
        editTextSearch = root.findViewById<EditText>(R.id.editTextSearchFriends)

        filteredFriendsList = ArrayList()

        adapter = FriendsChatAdapter(filteredFriendsList, currUser!!.uid)
        recyclerView.adapter = adapter

        // Set up TextWatcher to filter data based on search input
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            // Help from - https://developer.android.com/reference/android/text/TextWatcher
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = charSequence.toString().trim()

                // Update the query only if the search string is not empty
                if (searchText.isNotEmpty()) {
                    friendsChatViewModel.filterFriendsList(searchText)
                }
                // populate list with all friends
                else{
                    friendsChatViewModel.filterFriendsList("")
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        friendsChatViewModel.filteredFriendsList.observe(requireActivity()){
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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}