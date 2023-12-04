package com.example.plantastic.ui.toDo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.ParseException
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.plantastic.R
import com.example.plantastic.models.Groups
import com.example.plantastic.models.ToDoItem
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.ToDoRepository
import com.example.plantastic.repository.UsersRepository
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import androidx.core.widget.addTextChangedListener

class AddTodoItemDialog : DialogFragment() {
    private lateinit var chatsSpinner: Spinner
    private lateinit var participantsSpinner: Spinner
    private lateinit var dueDateTextView: TextView
    private lateinit var dueDateBtn: Button
    private lateinit var titleTextView: TextInputEditText
    private lateinit var descriptionTextView: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private val usersRepository = UsersRepository()
    private val groupsRepository = GroupsRepository()
    private val toDoRepository = ToDoRepository()
    private var userId: String? = null
    private var groupId: String? = null
    private var groups: List<Groups?> = ArrayList()
    private var participants: List<String> = ArrayList()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Referenced from: https://stackoverflow.com/questions/27965662/how-can-i-change-default-dialog-button-text-color-in-android-5
        val builder = AlertDialog.Builder(requireActivity(), R.style.DialogBoxTheme)
        val view: View =
            requireActivity().layoutInflater.inflate(R.layout.dialog_add_new_todo_item, null)

        chatsSpinner = view.findViewById(R.id.spinnerGroupTodo)
        participantsSpinner = view.findViewById(R.id.spinnerAssignToTodo)
        dueDateTextView = view.findViewById(R.id.textViewDueDateTodo)
        dueDateBtn = view.findViewById(R.id.btnDueDateTodo)
        titleTextView = view.findViewById(R.id.editTextTitleTodo)
        descriptionTextView = view.findViewById(R.id.editTextDescriptionTodo)
        btnCancel = view.findViewById(R.id.cancelButtonTodo)
        btnSave = view.findViewById(R.id.saveButtonTodo)

        btnSave.isEnabled = false
        val textColor =
            requireContext().resources.getColor(R.color.dialog_positive_button_disabled_state)
        btnSave.setTextColor(textColor)

        val bundle = arguments
        userId = bundle?.getString(KEY_USER_ID)
        groupId = bundle?.getString(KEY_GROUP_ID)

        if (userId == null) {
            dismiss()
        }

        CoroutineScope(Dispatchers.IO).launch {
            groups = groupsRepository.getAllGroupsByUserWithChatNames(userId!!)
            withContext(Dispatchers.Main) {
                val groupNames = groups.map { it!!.name }
                val groupsAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    groupNames
                )
                chatsSpinner.adapter = groupsAdapter
            }
        }

        chatsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                val selectedGroup = groups[position]

                participants = selectedGroup!!.participants!!.map { it.key }
                CoroutineScope(Dispatchers.IO).launch {
                    val users = usersRepository.getUsersById(participants)
                    val participantNames = users.map { "${it.firstName} ${it.lastName}" }
                    withContext(Dispatchers.Main) {
                        val participantsAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            participantNames
                        )
                        participantsSpinner.adapter = participantsAdapter
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing
            }
        }

        dueDateBtn.setOnClickListener {
            val calendar = Calendar.getInstance()

            // If user had previously inputted some data, we try to use that value to show in the dialog
            if (dueDateTextView.text.isNotBlank()) {
                try {
                    calendar.timeInMillis = parseDate()
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    dueDateTextView.text =
                        getString(R.string.date_placeholder, year, (monthOfYear + 1), dayOfMonth)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)
            )
            datePickerDialog.show()
        }

        btnSave.setOnClickListener {
            val isDataComplete = validateData()
            if (isDataComplete) {
                val todoItem = ToDoItem(
                    null,
                    titleTextView.text.toString(),
                    descriptionTextView.text.toString(),
                    parseDate(),
                    null,
                    completed = false,
                    participants[participantsSpinner.selectedItemPosition]
                )
                toDoRepository.addTodoListItem(
                    todoItem,
                    groups[chatsSpinner.selectedItemPosition]?.id
                )
                dialog?.dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dialog?.dismiss()
        }

        dueDateTextView.addTextChangedListener {
            validateData()
        }

        titleTextView.addTextChangedListener {
            validateData()
        }

        descriptionTextView.addTextChangedListener {
            validateData()
        }

        builder.setView(view)
        builder.setTitle("Add New To Do Item")

        val dialog = builder.create()
        // Referenced from: https://stackoverflow.com/questions/18346920/change-the-background-color-of-a-pop-up-dialog
        dialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_borders_15dp)
        return dialog
    }

    private fun validateData(): Boolean {
        var flag = true
        if (titleTextView.text?.isBlank() == true) {
            flag = false
        }

        if (descriptionTextView.text?.isBlank() == true) {
            flag = false
        }

        if (dueDateTextView.text.isBlank()) {
            flag = false
        }

        btnSave.isEnabled = flag
        val textColor = if (flag) {
            requireContext().resources.getColor(R.color.pastel_red)
        } else {
            requireContext().resources.getColor(R.color.dialog_positive_button_disabled_state)
        }
        btnSave.setTextColor(textColor)
        return flag
    }

    private fun parseDate(): Long {
        // Choosing what data we are parsing based on the dialog we need to create (Date or Time)
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.CANADA)
        return sdf.parse(dueDateTextView.text.toString()).time
    }

    companion object {
        private const val TAG = "Pln ToDoDialog"
        const val KEY_USER_ID = "KEY_USER_ID"
        const val KEY_GROUP_ID = "KEY_GROUP_ID"
        const val TAG_ADD_TODO_ITEM = "TAG_ADD_TODO_ITEM"
    }
}