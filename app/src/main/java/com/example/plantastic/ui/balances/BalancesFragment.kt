package com.example.plantastic.ui.balances

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.plantastic.models.Groups
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantastic.databinding.FragmentBalancesBinding
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.ui.login.LoginActivity
import com.example.plantastic.utilities.FirebaseNodes
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class BalancesFragment : Fragment() {

    private var _binding: FragmentBalancesBinding? = null
    private lateinit var balancesViewModel: BalancesViewModel
    private lateinit var balancesAdapter: BalancesAdapter

    private val binding get() = _binding!!

    private lateinit var groupsRepository: GroupsRepository
    private lateinit var usersAuthRepository: UsersAuthRepository
    private lateinit var adapter: BalancesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBalancesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        balancesViewModel =
            ViewModelProvider(this)[BalancesViewModel::class.java]

        usersAuthRepository = UsersAuthRepository()
        val currUser = usersAuthRepository.getCurrentUser()
        if(currUser == null){
            navigateToLoginActivity()
        }
        Log.d("hfhfh", "${currUser!!.uid}")
        var firebaseDatabase: FirebaseDatabase =  FirebaseDatabase.getInstance()
        var groupsReference: DatabaseReference = firebaseDatabase.getReference("groups")
        val groupsQuery = groupsReference.orderByChild("participants/${currUser!!.uid}").equalTo(true)
        val options = FirebaseRecyclerOptions.Builder<Groups>().setQuery(groupsQuery, Groups::class.java).build()

        // Set up RecyclerView
        adapter = BalancesAdapter(options, currUser!!.uid)
        binding.balancesRecyclerView.adapter = adapter
        val manager = LinearLayoutManager(requireContext())
        binding.balancesRecyclerView.layoutManager = manager

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

    private fun navigateToLoginActivity() {
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
