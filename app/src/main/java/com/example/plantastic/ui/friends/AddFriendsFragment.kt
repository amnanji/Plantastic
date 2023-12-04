package com.example.plantastic.ui.friends

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
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
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var adapter: AddFriendsAdapter
    private lateinit var usersRepository: UsersRepository
    private lateinit var usersAuthRepository: UsersAuthRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var noUsersEditText: TextView
    private lateinit var editTextSearch: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFriendsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = root.findViewById(R.id.addFriendsRecyclerView)
        recyclerView.layoutManager = WrapContentLinearLayoutManager(requireContext())
        noUsersEditText = root.findViewById(R.id.noUsersFoundTextView)

        editTextSearch = root.findViewById<EditText>(R.id.editTextSearch)

        usersRepository = UsersRepository()
        usersAuthRepository = UsersAuthRepository()

        val currUser = usersAuthRepository.getCurrentUser()

        val options = FirebaseRecyclerOptions.Builder<Users>()
            .setQuery(
                usersRepository.getInitialFriendsQuery(currUser!!.uid),
                Users::class.java)
            .build()

        adapter = AddFriendsAdapter(options, currUser.uid)
        recyclerView.adapter = adapter

        // Set up TextWatcher to filter data based on search input
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            // Help from - https://developer.android.com/reference/android/text/TextWatcher
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = charSequence.toString().trim()


                // Update the query only if the search string is not empty
                if (searchText.isNotEmpty()) {

                    // query for getting all users with which have search text
                    val newOptions = FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(
                            usersRepository.getUsernameQuery(searchText),
                            Users::class.java)
                        .build()

                    //update ui and create new list for recycler view
                    restartAdapter(newOptions)
                }
                else{
                    // update ui and show friends
                    restartAdapter(options)
                }

                val backgroundThread = Thread {
                    // Post a delayed runnable to update UI on the main thread after 200ms
                    handler.postDelayed({
                        checkItemCount()
                    }, 200)
                }

                // this is used because data change callbacks are not called
                // when the database query is empty
                backgroundThread.start()
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
        adapter.startListening()
    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
    }

    // help from - https://firebaseopensource.com/projects/firebase/firebaseui-android/database/readme/
    @SuppressLint("NotifyDataSetChanged")
    private fun restartAdapter(options: FirebaseRecyclerOptions<Users>){
        adapter.stopListening()
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                // Check item count after data set changes
                checkItemCount()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                // Check item count after data set changes
                checkItemCount()
            }
        })
        adapter.updateOptions(options)
        adapter.startListening()
        adapter.notifyDataSetChanged()
    }

    fun checkItemCount() {
        if (editTextSearch.text.toString().isNotEmpty()){
            if (adapter.itemCount != 0) {
                noUsersEditText.visibility = View.GONE
            }
            else {
                noUsersEditText.visibility = View.VISIBLE
            }
        }
        else{
            noUsersEditText.visibility = View.GONE
        }
    }
}