package com.example.plantastic.ui.chats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.plantastic.MainActivity
import com.example.plantastic.R
import com.example.plantastic.databinding.FragmentChatsBinding
import com.example.plantastic.models.Groups
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.utilities.WrapContentLinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChatsFragment : Fragment() {
    private lateinit var adapter: ChatsAdapter

    private var _binding: FragmentChatsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var groupsRepository: GroupsRepository = GroupsRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val chatsViewModel = ViewModelProvider(this).get(ChatsViewModel::class.java)

        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val chatsFab: FloatingActionButton = root.findViewById(R.id.chatsFab)

        val currUser = UsersAuthRepository().getCurrentUser()
        val userId = currUser!!.uid
        Log.d(TAG, "Curr user id --> $userId")

        val groupsQuery = groupsRepository.getAllGroupsQueryForUser(userId)

        val options =
            FirebaseRecyclerOptions.Builder<Groups>().setQuery(groupsQuery, Groups::class.java)
                .build()

        adapter = ChatsAdapter(options, userId)
        binding.chatsRecyclerView.adapter = adapter
        val manager = WrapContentLinearLayoutManager(requireContext())
        binding.chatsRecyclerView.layoutManager = manager

        chatsFab.setOnClickListener {
            val mainActivity: MainActivity = (requireActivity() as MainActivity)

            // Get the nav controller from main activity
            val navController: NavController = mainActivity.navController
            val newChatFragmentId: Int = mainActivity.newChatsFragmentId
            if (newChatFragmentId != -1) {
                navController.navigate(newChatFragmentId)
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    companion object {
        private const val TAG = "Pln ChatFragment"
    }
}