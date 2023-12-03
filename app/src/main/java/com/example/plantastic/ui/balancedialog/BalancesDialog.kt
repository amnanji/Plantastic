package com.example.plantastic.ui.balancedialog

import android.app.AlertDialog
import android.content.Context
import com.example.plantastic.R
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BalancesDialog(context: Context, currUserId: String, balances: HashMap<String, Double>) {

    private val alertDialog: AlertDialog

    init {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.group_balances_dialog, null)
        builder.setView(view)

        // empty function closes dialog automatically
        builder.setPositiveButton(context.getString(R.string.ok)) { _, _ ->
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = BalancesDialogAdapter(context, balances.keys.toList(), balances)

        alertDialog = builder.create()
    }

    fun show() {
        alertDialog.show()
    }
}

