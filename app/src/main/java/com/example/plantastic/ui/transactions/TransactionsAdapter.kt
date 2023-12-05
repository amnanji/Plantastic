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
import com.example.plantastic.utilities.FirebaseNodes
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.Double as Double

class TransactionsAdapter(
    private var options: FirebaseRecyclerOptions<Transaction>,
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

    private val default_color = R.color.default_text_color
    private val pos_bal_color = R.color.i_am_owed_green
    private val neg_bal_color = Color.RED

    override fun onBindViewHolder(
        holder: TransactionViewHolder,
        position: Int,
        model: Transaction
    ) {
        val amountDue = if(model.transactionType == FirebaseNodes.TRANSACTIONS_GROUP_EXPENSE){
            model.totalAmount!! / numParticipants
        } else{
            model.totalAmount!!
        }
        holder.transactionDescription.text = model.description
        val roundedAmountDue = BigDecimal(amountDue.toString()).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        holder.transactionDate.text = DateTimeUtils.getDateString(model.timestamp!!)

        if (model.transactionType == FirebaseNodes.TRANSACTIONS_GROUP_EXPENSE) {
            if (model.moneyOwedTo == userId) {

                "You lent $${roundedAmountDue * (numParticipants - 1)}".also {
                    holder.transactionSentence.text = it
                }
                val color = holder.itemView.context.getColor(pos_bal_color)
                holder.transactionSentence.setTextColor(color)
            }

            else {
                usersRepository.getUserById(model.moneyOwedTo!!) { user ->
                    if (user != null) {
                        "You borrowed $$roundedAmountDue from ${user.username}".also {
                            holder.transactionSentence.text = it
                        }
                        holder.transactionSentence.setTextColor(neg_bal_color)
                    }
                }
            }
        }
        else if (model.transactionType == FirebaseNodes.TRANSACTIONS_GROUP_REIMBURSEMENT){

            if(model.moneyOwedTo != userId && model.moneyPaidTo!! != userId) {
                usersRepository.getUserById(model.moneyOwedTo!!) { user1 ->
                    if (user1 != null) {
                        usersRepository.getUserById(model.moneyPaidTo!!){ user2 ->
                            if(user2!= null){
                                val st = "${user1.username} paid ${user2.username} $${abs(model.totalAmount!!)}"
                                holder.transactionSentence.text = st
                                val color = holder.itemView.context.getColor(default_color)
                                holder.transactionSentence.setTextColor(color)
                            }
                        }
                    }
                }
            }

            else if (model.moneyPaidTo == userId){
                usersRepository.getUserById(model.moneyOwedTo!!) { user ->
                    if (user != null) {
                        val st = "${user.username} paid you $${model.totalAmount}"
                        holder.transactionSentence.text = st
                        val color = holder.itemView.context.getColor(default_color)
                        holder.transactionSentence.setTextColor(color)
                    }
                }
            }

            else if (model.moneyOwedTo  == userId){
                usersRepository.getUserById(model.moneyPaidTo!!) { user ->
                    if (user != null) {
                        val st = "You paid ${user.username} $${model.totalAmount}"
                        holder.transactionSentence.text = st
                        val color = holder.itemView.context.getColor(default_color)
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