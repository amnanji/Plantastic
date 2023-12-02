package com.example.plantastic.ui.transactions

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Groups
import com.example.plantastic.models.Transaction
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.ui.balances.BalancesAdapter
import com.example.plantastic.utilities.DateTimeUtils
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import java.math.BigDecimal
import java.math.RoundingMode

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

    override fun onBindViewHolder(
        holder: TransactionViewHolder,
        position: Int,
        model: Transaction
    ) {
        val amountDue = model.totalAmount!! / numParticipants
        val roundedAmountDue = BigDecimal(amountDue.toString()).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        holder.transactionDescription.text = model.description
        holder.transactionDate.text = DateTimeUtils.getDateString(model.timestamp!!)
        if(model.moneyOwedTo == userId){
            "You lent ${roundedAmountDue * (numParticipants - 1)}".also {
                holder.transactionSentence.text = it
            }
        }
        else{
            usersRepository.getUserById(model.moneyOwedTo!!){ user ->
                if (user != null){
                    "You borrowed $roundedAmountDue from ${user.username}".also {
                        holder.transactionSentence.text = it
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