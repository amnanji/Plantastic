package com.example.plantastic.utilities

import android.app.AlertDialog
import android.content.Context

class CustomAlertDialog(private val context: Context) {
    fun showAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK"){dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}