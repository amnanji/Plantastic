package com.example.plantastic.ui.transactions

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Transaction
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.utilities.DateTimeUtils
import com.example.plantastic.utilities.DisplayFormatter
import com.example.plantastic.utilities.FirebaseNodes
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import java.math.BigDecimal
import java.math.RoundingMode

class TransactionsAdapter(
    options: FirebaseRecyclerOptions<Transaction>,
    private val userId: String,
    private val numParticipants: Int
) :
    FirebaseRecyclerAdapter<Transaction, TransactionsAdapter.TransactionViewHolder>(options) {

    private var usersRepository = UsersRepository()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.custom_transactions_layout, parent, false)
        return TransactionViewHolder(view)
    }

    private val defaultColor = R.color.default_text_color
    private val posBalColor = R.color.i_am_owed_green
    private val negBalColor = Color.RED

    override fun onBindViewHolder(
        holder: TransactionViewHolder,
        position: Int,
        model: Transaction
    ) {
        val amountDue = if (model.transactionType == FirebaseNodes.TRANSACTIONS_GROUP_EXPENSE) {
            model.totalAmount!! / numParticipants
        } else {
            model.totalAmount!!
        }
        holder.transactionDescription.text = model.description
        val roundedAmountDue =
            BigDecimal(amountDue.toString()).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        holder.transactionDate.text = DateTimeUtils.getDateString(model.timestamp!!)

        if (model.transactionType == FirebaseNodes.TRANSACTIONS_GROUP_EXPENSE) {
            if (model.moneyOwedTo == userId) {

                holder.itemView.context.getString(R.string.you_lent, (roundedAmountDue * (numParticipants - 1)).toString()).also {
                    holder.transactionSentence.text = it
                }
                val color = holder.itemView.context.getColor(posBalColor)
                holder.transactionSentence.setTextColor(color)
            } else {
                usersRepository.getUserById(model.moneyOwedTo!!) { user ->
                    if (user != null) {
                        holder.itemView.context.getString(
                            R.string.you_borrowed_from,
                            roundedAmountDue.toString(),
                            user.username
                        ).also {
                            holder.transactionSentence.text = it
                        }
                        holder.transactionSentence.setTextColor(negBalColor)
                    }
                }
            }
        } else if (model.transactionType == FirebaseNodes.TRANSACTIONS_GROUP_REIMBURSEMENT) {

            if (model.moneyOwedTo != userId && model.moneyPaidTo!! != userId) {
                usersRepository.getUserById(model.moneyOwedTo!!) { user1 ->
                    if (user1 != null) {
                        usersRepository.getUserById(model.moneyPaidTo!!) { user2 ->
                            if (user2 != null) {
                                val st = holder.itemView.context.getString(
                                    R.string.paid,
                                    user1.username,
                                    user2.username,
                                    DisplayFormatter.formatCurrency(model.totalAmount!!)
                                )
                                holder.transactionSentence.text = st
                                val color = holder.itemView.context.getColor(defaultColor)
                                holder.transactionSentence.setTextColor(color)
                            }
                        }
                    }
                }
            }

            else if (model.moneyPaidTo == userId){
                usersRepository.getUserById(model.moneyOwedTo!!) { user ->
                    if (user != null) {
                        val st =
                            holder.itemView.context.getString(
                                R.string.paid_you,
                                user.username,
                                DisplayFormatter.formatCurrency(model.totalAmount!!)
                            )
                        holder.transactionSentence.text = st
                        val color = holder.itemView.context.getColor(defaultColor)
                        holder.transactionSentence.setTextColor(color)
                    }
                }
            }

            else {
                usersRepository.getUserById(model.moneyPaidTo!!) { user ->
                    if (user != null) {
                        val st =
                            holder.itemView.context.getString(
                                R.string.you_paid,
                                user.username,
                                DisplayFormatter.formatCurrency(model.totalAmount!!)
                            )
                        holder.transactionSentence.text = st
                        val color = holder.itemView.context.getColor(defaultColor)
                        holder.transactionSentence.setTextColor(color)
                    }
                }
            }
        }
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transactionDescription: TextView = itemView.findViewById(R.id.transactionsDescription)
        val transactionSentence: TextView = itemView.findViewById(R.id.transactionsSentence)
        val transactionDate: TextView = itemView.findViewById(R.id.transactionDate)
    }
}