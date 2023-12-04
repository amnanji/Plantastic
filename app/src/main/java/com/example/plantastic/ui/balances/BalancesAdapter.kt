package com.example.plantastic.ui.balances

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Groups
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.ui.transactions.TransactionsActivity
import com.example.plantastic.utilities.CurrencyFormatter
import com.example.plantastic.utilities.IconUtil
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import java.math.BigDecimal

class BalancesAdapter(
    options: FirebaseRecyclerOptions<Groups>,
    private val userId: String
) :
    FirebaseRecyclerAdapter<Groups, BalancesAdapter.BalancesViewHolder>(options) {

    private var usersRepository = UsersRepository()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  BalancesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.custom_balances_layout, parent, false)
        return BalancesViewHolder(view)
    }

    override fun onBindViewHolder(holder: BalancesViewHolder, position: Int, model: Groups) {
        // money you have to pay
        var amountOwedByMe = 0.0
        //money you'll get
        var amountOwedByOthers = 0.0
        model.balances?.get(userId)?.forEach { (_, value) ->
            if (value < 0){
                amountOwedByMe += value
            }
            else{
                amountOwedByOthers += value
            }
        }

        val iconUtil = IconUtil(holder.itemView.context)

        amountOwedByMe = kotlin.math.abs(amountOwedByMe)
        if(model.groupType == "individual"){
            val participants = model.participants
            participants?.forEach { (key, _) ->
                if (key != userId){
                    usersRepository.getUserById(key){
                        holder.groupName.text = it?.username
                        val drawable = iconUtil.getIcon(
                            it!!.firstName!!,
                            it.lastName!!,
                            it.color!!
                        )
                        holder.imageViewHolder.setImageDrawable(drawable)
                    }
                }
            }
        }
        else{
            holder.groupName.text = model.name
            val drawable = iconUtil.getIcon(model.name!!, "", model.color!!)
            holder.imageViewHolder.setImageDrawable(drawable)
        }

        val epsilon = 1 - 1e-6

        holder.balanceOwedByYou.text =
            holder.itemView.context.getString(
                R.string.balance_placeholder,
                CurrencyFormatter.format(amountOwedByMe)
            )

        holder.balanceOwedByOthers.text =
            holder.itemView.context.getString(
                R.string.balance_placeholder,
                CurrencyFormatter.format(amountOwedByOthers)
            )

        val amountOwedByMeBigDecimal = BigDecimal.valueOf(amountOwedByMe)
        val amountOwedByOthersBigDecimal = BigDecimal.valueOf(amountOwedByOthers)

        val default_color = holder.itemView.context.getColor(R.color.default_text_color)

        if (amountOwedByMeBigDecimal.abs() > BigDecimal.valueOf(epsilon)) {
            holder.balanceOwedByYou.setTextColor(Color.RED)
        }
        else{
            holder.balanceOwedByYou.setTextColor(default_color)
        }

        if (amountOwedByOthersBigDecimal.abs() > BigDecimal.valueOf(epsilon)) {
            val color = holder.itemView.context.getColor(R.color.i_am_owed_green)
            holder.balanceOwedByOthers.setTextColor(color)
        }
        else{
            holder.balanceOwedByOthers.setTextColor(default_color)
        }


        holder.itemView.setOnClickListener{
            navigateToTransactionsActivity(holder.itemView.context, model.id!!, model.participants!!.size)
        }
    }

    inner class BalancesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.balancesGroupName)
        val balanceOwedByYou: TextView = itemView.findViewById(R.id.balancesAmountOwedByYou)
        val balanceOwedByOthers: TextView = itemView.findViewById(R.id.balancesAmountOwedByOthers)
        val imageViewHolder: ImageView = itemView.findViewById(R.id.balancesImageView)
    }

    private fun navigateToTransactionsActivity(context: Context, id: String, numParticipants: Int){
        val intent = Intent(context, TransactionsActivity::class.java)
        intent.putExtra(TransactionsActivity.GROUP_ID, id)
        intent.putExtra(TransactionsActivity.NUMBER_OF_MEMBERS, numParticipants)
        context.startActivity(intent)
    }
}
