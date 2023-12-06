package com.example.plantastic.ui.chats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.MainActivity
import com.example.plantastic.R
import com.example.plantastic.databinding.FragmentChatsBinding
import com.example.plantastic.models.Groups
import com.example.plantastic.repository.UsersAuthRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChatsFragment : Fragment() {
    private lateinit var adapter: ChatsAdapter
    private lateinit var chatsViewModel: ChatsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var groupsList: ArrayList<Groups>

    private var _binding: FragmentChatsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        chatsViewModel = ViewModelProvider(this)[ChatsViewModel::class.java]

        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val chatsFab: FloatingActionButton = root.findViewById(R.id.chatsFab)

        val currUser = UsersAuthRepository().getCurrentUser()
        val userId = currUser!!.uid
        Log.d(TAG, "Curr user id --> $userId")

        groupsList = ArrayList()
        adapter = ChatsAdapter(groupsList, userId)
        recyclerView = binding.chatsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        chatsViewModel.groups.observe(requireActivity()){
            // DiffUtil calculates the difference between the 2 lists and updates the adapter accordingly
            // Referenced from Chat GPT. It suggested this solution instead of updating all views
            val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = groupsList.size
                override fun getNewListSize(): Int = it.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return groupsList[oldItemPosition].id == it[newItemPosition].id
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return groupsList[oldItemPosition] == it[newItemPosition]
                }
            })

            groupsList.clear()
            groupsList.addAll(it)

            // Updating the adapter with the results of the differences calculated by the DiffUtil
            diffResult.dispatchUpdatesTo(adapter)
        }

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
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    companion object {
        private const val TAG = "Pln ChatFragment"
    }
}