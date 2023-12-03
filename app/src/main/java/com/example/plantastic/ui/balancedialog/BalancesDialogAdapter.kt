package com.example.plantastic.ui.balancedialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.repository.UsersRepository

class BalancesDialogAdapter(private val context: Context, private val itemList: List<String>, private val balances: HashMap<String, Double>) : RecyclerView.Adapter<BalancesDialogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_balances_dialog_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val editText: TextView = itemView.findViewById(R.id.editText)
        private val usersRepository = UsersRepository()
        fun bind(id: String) {

            usersRepository.getUserById(id){
                var st: String
                if (it != null && balances[id]!! != 0.0){
                    if (balances[id]!! > 0){
                        // you get money
                        st = context.getString(R.string.owes_you, it.username, balances[id]!!.toString())
                        editText.setTextColor(ContextCompat.getColor(context, R.color.pastel_green))
                    }
                    else {
                        // you pay money
                        st = context.getString(R.string.you_owe, it.username, (balances[id]!! * -1).toString())
                        editText.setTextColor(ContextCompat.getColor(context, R.color.pastel_red))
                    }
                    editText.text = st
                }
            }

        }
    }
}