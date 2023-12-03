package com.example.plantastic.ui.newchat

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.plantastic.R

class NewChatFragment : Fragment() {

    companion object {
        fun newInstance() = NewChatFragment()
    }

    private lateinit var viewModel: NewChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_chat, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewChatViewModel::class.java)
        // TODO: Use the ViewModel
    }

}