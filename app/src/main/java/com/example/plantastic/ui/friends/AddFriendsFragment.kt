package com.example.plantastic.ui.friends

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.databinding.FragmentAddFriendsBinding
import com.example.plantastic.models.Users
import com.example.plantastic.utilities.FirebaseNodes
import com.example.plantastic.utilities.WrapContentLinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

class AddFriendsFragment : Fragment() {
    private var _binding: FragmentAddFriendsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddFriendsViewModel
    private lateinit var adapter: AddFriendsAdapter
    private var flag = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddFriendsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = root.findViewById(R.id.addFriendsRecyclerView)
        recyclerView.layoutManager = WrapContentLinearLayoutManager(requireContext())

        val editTextSearch = root.findViewById<EditText>(R.id.editTextSearch)
        // Initialize Firebase Database and the query
        val databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseNodes.USERS_NODE)
        val query = databaseReference.orderByChild(FirebaseNodes.USERNAME_NODE)

        // Set up FirebaseRecyclerOptions
        val options = FirebaseRecyclerOptions.Builder<Users>()
            .setQuery(query, Users::class.java)
            .build()

        // Set up TextWatcher to filter data based on search input
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = charSequence.toString().trim()

                // Update the query only if the search string is not empty
                if (searchText.isNotEmpty()) {
                    val newQuery = query.startAt(searchText).endAt(searchText + "\uf8ff")
                    val newOptions = FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(newQuery, Users::class.java)
                        .build()
                    if (flag){
                        adapter = AddFriendsAdapter(options)
                        recyclerView.adapter = adapter
                        flag = false
                    }
                    else{
                        adapter.stopListening()
                        adapter.updateOptions(newOptions)
                    }
                    adapter.startListening()
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
//        adapter.startListening()
    }

}