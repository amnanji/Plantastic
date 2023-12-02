package com.example.plantastic.ui.friends

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.databinding.FragmentAddFriendsBinding
import com.example.plantastic.models.Users
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.utilities.WrapContentLinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions

class AddFriendsFragment : Fragment() {
    private var _binding: FragmentAddFriendsBinding? = null
    private val binding get() = _binding!!
    private var flag = true

    private lateinit var adapter: AddFriendsAdapter
    private lateinit var usersRepository: UsersRepository
    private lateinit var usersAuthRepository: UsersAuthRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddFriendsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = root.findViewById(R.id.addFriendsRecyclerView)
        recyclerView.layoutManager = WrapContentLinearLayoutManager(requireContext())

        val editTextSearch = root.findViewById<EditText>(R.id.editTextSearch)

        usersRepository = UsersRepository()
        usersAuthRepository = UsersAuthRepository()

        val currUser = usersAuthRepository.getCurrentUser()

        // Set up TextWatcher to filter data based on search input
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = charSequence.toString().trim()

                // Update the query only if the search string is not empty
                if (searchText.isNotEmpty()) {

                    val newOptions = FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(
                            usersRepository.getUsernameQuery(searchText),
                            Users::class.java)
                        .build()

                    if (flag){
                        adapter = AddFriendsAdapter(newOptions, currUser!!.uid)
                        recyclerView.adapter = adapter
                        flag = false
                    }
                    else{
                        adapter.stopListening()
                        adapter.updateOptions(newOptions)
                    }

                    adapter.startListening()
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                else{
                    if(!flag){
                        adapter.stopListening()
                        recyclerView.adapter = null
                    }
                    flag = true
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

    override fun onStart() {
        super.onStart()
        if(!flag){
            adapter.startListening()
        }
    }

    override fun onPause() {
        super.onPause()
        if(!flag) {
            adapter.stopListening()
        }
    }
}