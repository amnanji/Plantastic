package com.example.plantastic.ui.chats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.plantastic.databinding.FragmentChatsBinding
import com.example.plantastic.models.Chat
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.utilities.FirebaseNodes
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

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
        val userId = currUser?.uid

        val firebaseDatabase: FirebaseDatabase =  FirebaseDatabase.getInstance()
        val chatsReference: DatabaseReference = firebaseDatabase.getReference(FirebaseNodes.CHATS_NODE)
        val chatsRef = chatsReference.orderByChild("participants/$userId").equalTo(true)

        val options = FirebaseRecyclerOptions.Builder<Chat>().setQuery(chatsRef, Chat::class.java).build()
        adapter = ChatsAdapter(options, currUser!!.displayName)
        binding.chatsRecyclerView.adapter = adapter


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    public override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        adapter.startListening()
    }
}