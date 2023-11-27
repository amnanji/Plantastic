package com.example.plantastic.ui.balances

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Groups
import com.example.plantastic.repository.UsersRepository
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class BalancesAdapter(private val options: FirebaseRecyclerOptions<Groups>,
                      private val userId: String) :
    FirebaseRecyclerAdapter<Groups, BalancesAdapter.BalancesViewHolder>(options) {

    private var usersRepository = UsersRepository()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  BalancesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.custom_balances_layout, parent, false)
        return BalancesViewHolder(view)
    }

    override fun onBindViewHolder(holder: BalancesViewHolder, position: Int, model: Groups) {
        // money you have to pay
        var amountOwedByMe: Double = 0.0
        //money you'll get
        var amountOwedByOthers: Double = 0.0
        model.balances?.get(userId)?.forEach { (_, value) ->
            if (value < 0){
                amountOwedByMe += value
            }
            else{
                amountOwedByOthers += value
            }
        }
        amountOwedByMe *= -1.0
        if(model.groupType == "Individual"){
            var participants = model.participants
            participants?.forEach { (key, _) ->
                if (key != userId){
                    usersRepository.getUserById(key){
                        holder.groupName.text = it?.username
                    }
                }
            }
        }
        else{
            holder.groupName.text = model.name
        }
        holder.balanceOwedByYou.text = amountOwedByMe.toString()
        holder.balanceOwedByOthers.text = amountOwedByOthers.toString()
    }

    inner class BalancesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.balancesGroupName)
        val balanceOwedByYou: TextView = itemView.findViewById(R.id.balancesAmountOwedByYou)
        val balanceOwedByOthers: TextView = itemView.findViewById(R.id.balancesAmountOwedByOthers)
    }
}
