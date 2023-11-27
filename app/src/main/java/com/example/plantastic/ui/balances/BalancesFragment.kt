package com.example.plantastic.ui.balances

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.plantastic.models.Groups
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantastic.databinding.FragmentBalancesBinding
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.ui.login.LoginActivity
import com.firebase.ui.database.FirebaseRecyclerOptions
class BalancesFragment : Fragment(){

    private var _binding: FragmentBalancesBinding? = null
    private lateinit var balancesViewModel: BalancesViewModel

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

        groupsRepository = GroupsRepository()

        val groupsQuery = groupsRepository.getAllGroupsQueryForUser(currUser!!.uid)
        val options = FirebaseRecyclerOptions.Builder<Groups>().setQuery(groupsQuery, Groups::class.java).build()

        // Set up RecyclerView
        adapter = BalancesAdapter(options, currUser!!.uid)
        binding.balancesRecyclerView.adapter = adapter
        val manager = LinearLayoutManager(requireContext())
        binding.balancesRecyclerView.layoutManager = manager

        adapter.startListening()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
