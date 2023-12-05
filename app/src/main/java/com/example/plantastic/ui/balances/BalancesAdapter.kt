package com.example.plantastic.ui.balances

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.example.plantastic.utilities.IconUtil
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class BalancesAdapter(
    options: FirebaseRecyclerOptions<Groups>,
    private val userId: String
) :
    FirebaseRecyclerAdapter<Groups, BalancesAdapter.BalancesViewHolder>(options) {

    private var usersRepository = UsersRepository()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalancesViewHolder {
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
            if (value < 0) {
                amountOwedByMe += value
            } else {
                amountOwedByOthers += value
            }
        }

        val iconUtil = IconUtil(holder.itemView.context)

        amountOwedByMe = kotlin.math.abs(amountOwedByMe)
        if (model.groupType == "individual") {
            val participants = model.participants
            participants?.forEach { (key, _) ->
                if (key != userId) {
                    usersRepository.getUserById(key) {
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
        } else {
            holder.groupName.text = model.name
            val drawable = iconUtil.getIcon(model.name!!, "", model.color!!)
            holder.imageViewHolder.setImageDrawable(drawable)
        }
        holder.balanceOwedByYou.text =
            holder.itemView.context.getString(
                R.string.balance_placeholder,
                amountOwedByMe.toString()
            )
        if (amountOwedByMe > 0.0) holder.balanceOwedByYou.setTextColor(Color.RED)


        holder.balanceOwedByOthers.text =
            holder.itemView.context.getString(
                R.string.balance_placeholder,
                amountOwedByOthers.toString()
            )
        if (amountOwedByOthers > 0.0) {
            val color = holder.itemView.context.getColor(R.color.i_am_owed_green)
            holder.balanceOwedByOthers.setTextColor(color)
        }

        holder.itemView.setOnClickListener {
            navigateToTransactionsActivity(
                holder.itemView.context,
                model.id!!,
                model.participants!!.size
            )
        }
    }

    inner class BalancesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.balancesGroupName)
        val balanceOwedByYou: TextView = itemView.findViewById(R.id.balancesAmountOwedByYou)
        val balanceOwedByOthers: TextView = itemView.findViewById(R.id.balancesAmountOwedByOthers)
        val imageViewHolder: ImageView = itemView.findViewById<ImageView>(R.id.balancesImageView)
    }

    private fun navigateToTransactionsActivity(context: Context, id: String, numParticpants: Int) {
        val intent = Intent(context, TransactionsActivity::class.java)
        intent.putExtra(TransactionsActivity.GROUP_ID, id)
        intent.putExtra(TransactionsActivity.NUMBER_OF_MEMBERS, numParticpants)
        context.startActivity(intent)
    }
}
