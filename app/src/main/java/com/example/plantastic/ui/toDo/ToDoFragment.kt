package com.example.plantastic.ui.toDo
// ToDoFragment.kt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.databinding.FragmentToDoBinding
import com.example.plantastic.models.ToDoItemForDisplay
import com.example.plantastic.repository.UsersAuthRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ToDoFragment : Fragment() {

    private var _binding: FragmentToDoBinding? = null
    private lateinit var toDoViewModel: ToDoViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var todoList: ArrayList<ToDoItemForDisplay>
    private lateinit var adapter: ToDoAdapter
    private lateinit var fabAddBtn: FloatingActionButton

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToDoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fabAddBtn = binding.TodoFabAdd
        fabAddBtn.setOnClickListener{
            val currUser = UsersAuthRepository().getCurrentUser()
            val userId = currUser!!.uid

            val dialog = AddTodoItemDialog()
            val bundle = Bundle()
            bundle.putString(AddTodoItemDialog.KEY_USER_ID, userId)
            dialog.arguments = bundle
            dialog.show(requireActivity().supportFragmentManager, AddTodoItemDialog.TAG_ADD_TODO_ITEM)
        }

        toDoViewModel =
            ViewModelProvider(this)[ToDoViewModel::class.java]

        todoList = ArrayList()
        adapter = ToDoAdapter(todoList)
        recyclerView = binding.toDoRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        toDoViewModel.toDoItems.observe(requireActivity()){
            // DiffUtil calculates the difference between the 2 lists and updates the adapter accordingly
            // Referenced from Chat GPT. It suggested this solution instead of updating all views
            val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = todoList.size
                override fun getNewListSize(): Int = it.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return todoList[oldItemPosition].id == it[newItemPosition].id
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return todoList[oldItemPosition] == it[newItemPosition]
                }
            })

            todoList.clear()
            todoList.addAll(it)

            // Updating the adapter with the results of the differences calculated by the DiffUtil
            diffResult.dispatchUpdatesTo(adapter)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "Pln ToDoFragment"
    }
}
