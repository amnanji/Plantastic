package com.example.plantastic.ui.transactions

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.plantastic.R
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.TransactionsRepository
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.utilities.DisplayFormatter
import com.example.plantastic.utilities.FirebaseNodes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class SettleBalanceDialog : DialogFragment() {
    private lateinit var participantsSpinner: Spinner
    private lateinit var balanceTextView: TextView
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var description: String
    private var amount by Delegates.notNull<Double>()

    private val usersRepository = UsersRepository()
    private val groupsRepository = GroupsRepository()
    private val transactionsRepository = TransactionsRepository()
    private var userId: String? = null
    private var groupId: String? = null
    private var participants: List<String> = ArrayList()
    private var balances: HashMap<String, Double> = hashMapOf()

    private val defaultColor = R.color.default_text_color
    private val posBalColor = R.color.i_am_owed_green
    private val negBalColor = Color.RED

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Referenced from: https://stackoverflow.com/questions/27965662/how-can-i-change-default-dialog-button-text-color-in-android-5
        val builder = AlertDialog.Builder(requireActivity(), R.style.DialogBoxTheme)
        val view: View =
            requireActivity().layoutInflater.inflate(R.layout.dialog_settle_balance, null)

        participantsSpinner = view.findViewById(R.id.spinnerAssignToTransaction)
        balanceTextView = view.findViewById(R.id.balanceTextView)
        btnCancel = view.findViewById(R.id.cancelButtonTodo)
        btnSave = view.findViewById(R.id.saveButtonTodo)

        updateButtonUI(false)

        val bundle = arguments
        userId = bundle?.getString(KEY_USER_ID)
        groupId = bundle?.getString(KEY_GROUP_ID)
        if (userId == null || groupId == null) {
            dismiss()
        }

        groupsRepository.getGroupById(groupId!!) { group ->
            if (group != null) {
                balances = group.balances!![userId]!!
                participants = group.participants!!.filterNot { it.key == userId }.keys.toList()
                CoroutineScope(Dispatchers.IO).launch {
                    val users = usersRepository.getUsersById(participants)
                    val participantNames = users.map {
                        requireContext().getString(
                            R.string.name_placeholder, it.firstName, it.lastName
                        )
                    }
                    withContext(Dispatchers.Main) {
                        val participantsAdapter = ArrayAdapter(
                            requireContext(), android.R.layout.simple_spinner_item, participantNames
                        )
                        participantsSpinner.adapter = participantsAdapter

                        if (participants.size <= 1) {
                            participantsSpinner.setSelection(0)
                            participantsSpinner.isEnabled = false
                        }
                    }
                }
            }
        }

        participantsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val thisBalance = balances[participants[position]]!!
                if (thisBalance > 0) {
                    usersRepository.getUserById(participants[position]) {
                        if (it != null) {
                            balanceTextView.text = getString(
                                R.string.is_paying_you_transaction,
                                it.firstName,
                                it.lastName,
                                DisplayFormatter.formatCurrency(thisBalance)
                            )
                            description = getString(
                                R.string.settled_balances_you, it.firstName, it.lastName
                            )
                            balanceTextView.setTextColor(
                                requireContext().getColor(
                                    posBalColor
                                )
                            )
                            amount = thisBalance
                        }
                        updateButtonUI(true)
                    }
                } else if (thisBalance < 0) {
                    usersRepository.getUserById(participants[position]) {
                        if (it != null) {
                            balanceTextView.text = getString(
                                R.string.you_are_paying_transactions,
                                it.firstName,
                                it.lastName,
                                DisplayFormatter.formatCurrency(thisBalance * -1.0)
                            )
                            description = getString(
                                R.string.you_settled_balances_with, it.firstName, it.lastName
                            )
                            balanceTextView.setTextColor(negBalColor)
                            amount = thisBalance * -1
                        }
                        updateButtonUI(true)
                    }
                } else {
                    balanceTextView.text =
                        getString(R.string.balance_with_this_user_is_already_settled)
                    updateButtonUI(false)
                    balanceTextView.setTextColor(requireContext().getColor(defaultColor))
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                balanceTextView.text = ""
                updateButtonUI(false)
            }
        }

        btnSave.setOnClickListener {
            if (amount > 0) {
                transactionsRepository.addTransaction(
                    groupId!!,
                    description,
                    participants[participantsSpinner.selectedItemPosition], //other
                    DisplayFormatter.roundToTwoDecimalPlaces(amount),
                    FirebaseNodes.TRANSACTIONS_GROUP_REIMBURSEMENT,
                    userId // this
                ) {
                    if (it != null) {
                        groupsRepository.updateBalanceForTransaction(it, userId)
                    }
                }
            } else {
                transactionsRepository.addTransaction(
                    groupId!!,
                    description,
                    userId!!, //other
                    DisplayFormatter.roundToTwoDecimalPlaces(amount * -1.0),
                    FirebaseNodes.TRANSACTIONS_GROUP_REIMBURSEMENT,
                    participants[participantsSpinner.selectedItemPosition] // this
                ) {
                    if (it != null) {
                        groupsRepository.updateBalanceForTransaction(it, userId)
                    }
                }
            }
            dialog?.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog?.dismiss()
        }

        builder.setView(view)
        builder.setTitle(getString(R.string.settle_balance))

        val dialog = builder.create()
        // Referenced from: https://stackoverflow.com/questions/18346920/change-the-background-color-of-a-pop-up-dialog
        dialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_borders_15dp)
        return dialog
    }

    private fun updateButtonUI(isEnabled: Boolean) {
        val textColor = if (isEnabled) {
            requireContext().resources.getColor(R.color.pastel_red)
        } else {
            requireContext().resources.getColor(R.color.dialog_positive_button_disabled_state)
        }
        btnSave.isEnabled = isEnabled
        btnSave.setTextColor(textColor)
    }

    companion object {
        private const val TAG = "Pln ToDoDialog"
        const val KEY_USER_ID = "KEY_USER_ID"
        const val KEY_GROUP_ID = "KEY_GROUP_ID"
        const val TAG_SETTLE_BALANCE = "TAG_SETTLE_BALANCE"
    }

}