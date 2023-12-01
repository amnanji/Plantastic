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
import com.example.plantastic.ui.balances.BalancesAdapter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class TransactionsAdapter(private var options: FirebaseRecyclerOptions<Transaction>) :
    FirebaseRecyclerAdapter<Transaction, TransactionsAdapter.TransactionViewHolder>(options) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {
        Log.d("TransactionsRepository", "inside on create View holder")
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.custom_transactions_layout, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TransactionViewHolder,
        position: Int,
        model: Transaction
    ) {
        Log.d("TransactionsRepository", "inside on bind view holder")
        holder.transactionDescription.text = "hello there"
        holder.transactionSentence.text = "hfhfhfgbfbhf"
        holder.transactionDate.text = "hfhfhhf"
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transactionDescription: TextView = itemView.findViewById(R.id.transactionsDescription)
        val transactionSentence: TextView = itemView.findViewById(R.id.transactionsSentence)
        val transactionDate: TextView = itemView.findViewById(R.id.transactionDate)
    }
}