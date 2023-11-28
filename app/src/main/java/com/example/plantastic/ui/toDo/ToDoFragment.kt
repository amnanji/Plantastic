package com.example.plantastic.ui.toDo
// ToDoFragment.kt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.databinding.FragmentToDoBinding
import com.example.plantastic.ui.toDo.ToDoAdapter
import com.example.plantastic.ui.toDo.ToDoViewModel

import com.example.plantastic.ui.toDo.ToDoItem

class ToDoFragment : Fragment() {

    private var _binding: FragmentToDoBinding? = null
    private lateinit var toDoViewModel: ToDoViewModel
    private lateinit var recyclerView: RecyclerView

    private val dummyTodoList = listOf(
        ToDoItem("Task 1", "Description 1"),
        ToDoItem("Task 2", "Description 2"),
        ToDoItem("Task 3", "Description 3")
    )

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToDoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        toDoViewModel =
            ViewModelProvider(this)[ToDoViewModel::class.java]

        //val textView = binding.toDoFragmentText
        toDoViewModel.text.observe(viewLifecycleOwner, Observer {
            //textView.text = it
        })

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ToDoAdapter(dummyTodoList)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
