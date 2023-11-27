package com.example.plantastic.ui.balances


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Groups
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class BalancesAdapter(private val options: FirebaseRecyclerOptions<Groups>,
                      private val userId: String) :
    FirebaseRecyclerAdapter<Groups, BalancesAdapter.BalancesViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  BalancesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.custom_balances_layout, parent, false)
        return BalancesViewHolder(view)
    }

    override fun onBindViewHolder(holder: BalancesViewHolder, position: Int, model: Groups) {
        holder.userName.text = "hello world"
        holder.oweStatus.text = "abcd"
        holder.amount.text = "60"
    }

    inner class BalancesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.balancesUserName)
        val oweStatus: TextView = itemView.findViewById(R.id.balanacesOweStatus)
        val amount: TextView = itemView.findViewById(R.id.balancesAmount)
    }

}
