package com.example.plantastic.ui.balances


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R

class BalancesAdapter(private val balancesList: List<BalancesItem>) :
    RecyclerView.Adapter<BalancesAdapter.BalancesViewHolder>() {

    class BalancesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.balancesUserName)
        val oweStatus: TextView = itemView.findViewById(R.id.balanacesOweStatus)
        val amount: TextView = itemView.findViewById(R.id.balancesAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalancesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_balances_layout, parent, false)
        return BalancesViewHolder(view)
    }

    override fun onBindViewHolder(holder: BalancesViewHolder, position: Int) {
        val currentItem = balancesList[position]

        holder.userName.text = currentItem.name
        holder.oweStatus.text = currentItem.oweStatus
        holder.amount.text = currentItem.amount.toString()
    }

    override fun getItemCount(): Int {
        return balancesList.size
    }
}
