package com.example.plantastic.ui.events

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantastic.databinding.FragmentEventsBinding
import com.example.plantastic.models.Events
import com.example.plantastic.repository.EventsCallback
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.ui.login.LoginActivity
import com.firebase.ui.database.FirebaseRecyclerOptions

class EventsFragment : Fragment(), EventsCallback {

    private var _binding: FragmentEventsBinding? = null
    private lateinit var groupsRepository: GroupsRepository
    private lateinit var usersAuthRepository: UsersAuthRepository
    private val binding get() = _binding!!

    private lateinit var eventsViewModel: EventsViewModel
    private lateinit var eventsAdapter: EventsAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        eventsViewModel = ViewModelProvider(this)[EventsViewModel::class.java]
        usersAuthRepository = UsersAuthRepository()
        val currUser = usersAuthRepository.getCurrentUser()
        groupsRepository = GroupsRepository()
        if(currUser == null) {
            navigateToLoginActivity()
        }

            if (currUser != null) {
                groupsRepository.getAllEventsQueryForUser(currUser.uid, this)
            }


        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun navigateToLoginActivity() {
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onEventsLoaded(events: List<Events>) {
        eventsAdapter = EventsAdapter(events)
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.eventsRecyclerView.adapter = eventsAdapter
    }
}
