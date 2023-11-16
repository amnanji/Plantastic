package com.example.plantastic.ui.toDo

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.plantastic.databinding.FragmentToDoBinding

class ToDoFragment : Fragment() {

    private var _binding: FragmentToDoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val toDoViewModel =
            ViewModelProvider(this).get(ToDoViewModel::class.java)

        _binding = FragmentToDoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.toDoFragmentText
        toDoViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}