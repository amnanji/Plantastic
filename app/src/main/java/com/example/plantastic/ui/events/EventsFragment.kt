package com.example.plantastic.ui.events

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantastic.databinding.FragmentEventsBinding
import com.example.plantastic.models.Events
import com.example.plantastic.repository.EventsCallback
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.ui.login.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser

class EventsFragment : Fragment(), EventsCallback {

    private var _binding: FragmentEventsBinding? = null
    private lateinit var groupsRepository: GroupsRepository
    private lateinit var usersAuthRepository: UsersAuthRepository
    private lateinit var fabAddBtn: FloatingActionButton
    private val binding get() = _binding!!
    private var currUser: FirebaseUser? = null

    private lateinit var eventsAdapter: EventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        usersAuthRepository = UsersAuthRepository()
        currUser = usersAuthRepository.getCurrentUser()
        groupsRepository = GroupsRepository()
        if(currUser == null) {
            navigateToLoginActivity()
        }
        if (currUser != null) {
            groupsRepository.getAllEventsListForUser(currUser!!.uid, this)
        }

//        eventsAdapter = EventsAdapter(ArrayList(), currUser!!.uid)

        fabAddBtn = binding.EventsfabAdd
        fabAddBtn.setOnClickListener{

            val dialog = AddEventsDialog()
            val bundle = Bundle()

            dialog.arguments = bundle
            dialog.show(requireActivity().supportFragmentManager, AddEventsDialog.TAG_ADD_EVENT)
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

        if (_binding != null) {
            eventsAdapter = EventsAdapter(events, currUser!!.uid)
            Log.d("Pln", "binding is null --> ${_binding == null}")
            Log.d("Pln", "binding.eventsRecyclerView is null --> ${_binding!!.eventsRecyclerView == null}")
            Log.d("Pln", "binding.eventsRecyclerView.layoutManager is null --> ${_binding!!.eventsRecyclerView.layoutManager == null}")
            Log.d("Pln", "binding.eventsRecyclerView.adapter is null --> ${_binding!!.eventsRecyclerView.adapter == null}")
            Log.d("Pln", "context is null --> ${context == null}")
            Log.d("Pln", "events is null --> ${events == null}")
            Log.d("Pln", "currUser is null --> ${currUser == null}")
            Log.d("Pln", "eventsAdapter is null --> ${eventsAdapter == null}")
            binding.eventsRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.eventsRecyclerView.adapter = eventsAdapter
        }
    }
}
