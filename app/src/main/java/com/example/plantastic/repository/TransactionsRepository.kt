package com.example.plantastic.repository

import android.icu.util.Calendar
import com.example.plantastic.models.Transaction
import com.example.plantastic.utilities.FirebaseNodes
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class TransactionsRepository {
    private var firebaseDatabase: FirebaseDatabase =  FirebaseDatabase.getInstance()
    private var transactionsReference: DatabaseReference = firebaseDatabase.getReference(FirebaseNodes.TRANSACTIONS_NODE)

    fun getTransactionsForGroup(groupId: String): Query {
        return transactionsReference.orderByChild(FirebaseNodes.TRANSACTIONS_GROUP_NODE).equalTo(groupId)
    }

    fun addTransaction(
            groupId: String,
            description: String,
            userId: String,
            amount: Double,
            type: String,
            callback: (Transaction?) -> Unit
    ) {
        val reference: DatabaseReference = transactionsReference.push()
        val transactionId: String? = reference.key

        val transaction = Transaction(
            transactionId,
            groupId,
            Calendar.getInstance().timeInMillis,
            description,
            userId,
            amount,
            type
        )

        reference.setValue(transaction)
            .addOnSuccessListener {
                callback(transaction)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}