package com.example.plantastic.ui.toDo

// ToDoAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R

class ToDoAdapter(private val todoList: List<ToDoItem>) :
    RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.TODOtextTitle)
        val descTextView: TextView = itemView.findViewById(R.id.textDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_todo_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todoItem = todoList[position]
        holder.titleTextView.text = todoItem.title
        holder.descTextView.text = todoItem.description
    }

    override fun getItemCount(): Int {
        return todoList.size
    }
}
