package com.example.plantastic.ui.toDo

// ToDoAdapter.kt
import android.graphics.Paint
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.ToDoItemForDisplay
import com.example.plantastic.repository.ToDoRepository
import java.util.Calendar


class ToDoAdapter(private val todoList: List<ToDoItemForDisplay>) :
    RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {
    private val toDoRepository = ToDoRepository()
    private val cardBackgroundColors = ArrayList(
        listOf(
            R.color.todo_list_item_color_1,
            R.color.todo_list_item_color_2,
            R.color.todo_list_item_color_3
        )
    )

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.toDoTitle)
        val descTextView: TextView = itemView.findViewById(R.id.toDoDescription)
        val groupNameTextView: TextView = itemView.findViewById(R.id.toDoGroupName)
        val dueDateTextView: TextView = itemView.findViewById(R.id.toDoDueDate)
        val checkbox: CheckBox = itemView.findViewById(R.id.toDoCheckBox)
        val cardView: CardView = itemView.findViewById(R.id.toDoCardView)
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
        holder.groupNameTextView.text = todoItem.groupName
        holder.dueDateTextView.text = todoItem.dueDate?.let { getDate(it) }

        // Setting the background color of the card from one of colors defined in the list above
        val backgroundColor =
            holder.itemView.context.resources.getColor(cardBackgroundColors[position % cardBackgroundColors.size])
        holder.cardView.setCardBackgroundColor(backgroundColor)

        if (todoItem.isCompleted != null) {
            // Setting setOnCheckedChangeListener to null to avoid triggering the OnCheckedChangeListener
            // Referenced from: https://stackoverflow.com/questions/15523157/change-checkbox-value-without-triggering-oncheckchanged
            holder.checkbox.setOnCheckedChangeListener(null)
            holder.checkbox.isChecked = todoItem.isCompleted
            updateTodoItemTitleStyle(todoItem.isCompleted, holder.titleTextView)
        }
        holder.checkbox.setOnCheckedChangeListener(checkedChangeListener(todoItem, holder))
    }

    private fun checkedChangeListener(
        todoItem: ToDoItemForDisplay,
        holder: ViewHolder
    ): CompoundButton.OnCheckedChangeListener {
        return CompoundButton.OnCheckedChangeListener { _, isChecked ->
            // Updating the data in the database
            toDoRepository.updateTodoListItem(todoItem.id, todoItem.groupId, isChecked)
            updateTodoItemTitleStyle(isChecked, holder.titleTextView)
        }
    }

    // Function to add a strikethrough line through the title of the todoItem if it is completed
    private fun updateTodoItemTitleStyle(isChecked: Boolean, textView: TextView) {
        // Referenced from: https://stackoverflow.com/questions/9786544/creating-a-strikethrough-text
        if (isChecked) {
            textView.paintFlags =
                textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            textView.paintFlags =
                textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    private fun getDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return DateFormat.format("MMM dd, yyyy", calendar).toString()
    }

    companion object {
        private const val TAG = "Pln ToDoAdapter"
    }
}
