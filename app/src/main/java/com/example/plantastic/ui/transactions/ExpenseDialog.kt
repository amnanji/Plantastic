package com.example.plantastic.ui.transactions

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.example.plantastic.R
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.TransactionsRepository
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.utilities.FirebaseNodes
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExpenseDialog : DialogFragment() {
    private lateinit var participantsSpinner: Spinner
    private lateinit var amountTextView: TextInputEditText
    private lateinit var descriptionTextView: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private val usersRepository = UsersRepository()
    private val groupsRepository = GroupsRepository()
    private val transactionsRepository = TransactionsRepository()
    private var userId: String? = null
    private var groupId: String? = null
    private var participants: List<String> = ArrayList()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Referenced from: https://stackoverflow.com/questions/27965662/how-can-i-change-default-dialog-button-text-color-in-android-5
        val builder = AlertDialog.Builder(requireActivity(), R.style.DialogBoxTheme)
        val view: View =
            requireActivity().layoutInflater.inflate(R.layout.dialog_new_expense, null)

        participantsSpinner = view.findViewById(R.id.spinnerAssignToTransaction)
        amountTextView = view.findViewById(R.id.editTextAmountTransaction)
        descriptionTextView = view.findViewById(R.id.editTextDescriptionTransaction)
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

        groupsRepository.getGroupById(groupId!!){ group ->
            if (group != null){
                participants = group.participants!!.map { it.key }
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
        }

        btnSave.setOnClickListener {
            if (validateData()) {
                transactionsRepository.addTransaction(
                    groupId!!,
                    descriptionTextView.text.toString(),
                    participants[participantsSpinner.selectedItemPosition],
                    amountTextView.text.toString().toDouble(),
                    FirebaseNodes.TRANSACTIONS_GROUP_EXPENSE,
                    null
                ){
                    if(it != null){
                        groupsRepository.updateBalanceForTransaction(it, null)
                    }
                }
                dialog?.dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dialog?.dismiss()
        }

        amountTextView.addTextChangedListener {
            val input = it.toString()

            if (input.isNotEmpty() && input != ".") {
                val decimalIndex = input.indexOf(".")

                if (decimalIndex != -1 && input.length - decimalIndex - 1 > 2) {
                    // Remove extra decimal places
                    it?.delete(decimalIndex + 1 + 2, it.length)
                }
            }
            validateData()
        }

        descriptionTextView.addTextChangedListener {
            validateData()
        }

        builder.setView(view)
        builder.setTitle(getString(R.string.add_new_group_expense))

        val dialog = builder.create()
        // Referenced from: https://stackoverflow.com/questions/18346920/change-the-background-color-of-a-pop-up-dialog
        dialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_borders_15dp)
        return dialog
    }

    private fun validateData(): Boolean {
        var flag = true
        if (amountTextView.text.isNullOrBlank()) {
            flag = false
        }

        if (descriptionTextView.text.isNullOrBlank()) {
            flag = false
        }

        if (flag && amountTextView.text.toString().toDouble() == 0.0) {
            flag = false
            amountTextView.error = getString(R.string.amount_cannot_be_zero)
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

    companion object {
        private const val TAG = "Pln ToDoDialog"
        const val KEY_USER_ID = "KEY_USER_ID"
        const val KEY_GROUP_ID = "KEY_GROUP_ID"
        const val TAG_ADD_EXPENSE = "TAG_ADD_EXPENSE"
    }
}