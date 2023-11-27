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
import com.example.plantastic.databinding.FragmentBalancesBinding
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.ui.login.LoginActivity
import com.example.plantastic.utilities.FirebaseNodes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class BalancesFragment : Fragment() {

    private var _binding: FragmentBalancesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var groupsRepository: GroupsRepository
    private lateinit var usersAuthRepository: UsersAuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val balancesViewModel =
            ViewModelProvider(this).get(BalancesViewModel::class.java)

        _binding = FragmentBalancesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        usersAuthRepository = UsersAuthRepository()
        val currUser = usersAuthRepository.getCurrentUser()
        if(currUser == null){
            navigateToLoginActivity()
        }

        var firebaseDatabase: FirebaseDatabase =  FirebaseDatabase.getInstance()
        var groupsReference: DatabaseReference = firebaseDatabase.getReference(FirebaseNodes.GROUPS_NODE)
        val groupsQuery = groupsReference.orderByChild("balances/${currUser!!.uid}").equalTo(true)


        val textView: TextView = binding.balancesFragmentText
        balancesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
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
}