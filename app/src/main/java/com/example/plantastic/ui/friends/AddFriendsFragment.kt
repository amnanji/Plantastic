package com.example.plantastic.ui.friends

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.plantastic.R

class AddFriendsFragment : Fragment() {

    companion object {
        fun newInstance() = AddFriendsFragment()
    }

    private lateinit var viewModel: AddFriendsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_friends, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddFriendsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}