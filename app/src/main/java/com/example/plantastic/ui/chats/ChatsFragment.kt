package com.example.plantastic.ui.chats

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantastic.databinding.FragmentChatsBinding
import com.example.plantastic.models.Groups
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.ui.conversation.ConversationActivity
import com.example.plantastic.utilities.FirebaseNodes
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatsFragment : Fragment() {
    private lateinit var adapter: ChatsAdapter

    private var _binding: FragmentChatsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val chatsViewModel =
            ViewModelProvider(this).get(ChatsViewModel::class.java)

        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val currUser = UsersAuthRepository().getCurrentUser()
        val userId = currUser!!.uid
        Log.d(TAG, "Curr user id --> $userId")

        val firebaseDatabase: FirebaseDatabase =  FirebaseDatabase.getInstance()
        val groupsReference: DatabaseReference = firebaseDatabase.getReference(FirebaseNodes.GROUPS_NODE)
        val groupsQuery = groupsReference.orderByChild("participants/$userId").equalTo(true)

        val options = FirebaseRecyclerOptions.Builder<Groups>().setQuery(groupsQuery, Groups::class.java).build()
        adapter = ChatsAdapter(options, userId)
        binding.chatsRecyclerView.adapter = adapter
        val manager = LinearLayoutManager(requireContext())
        binding.chatsRecyclerView.layoutManager = manager

        adapter.startListening()
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

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    companion object {
        private const val TAG = "Pln ChatFragment"
    }
}