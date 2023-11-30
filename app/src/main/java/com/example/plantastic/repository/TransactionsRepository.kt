package com.example.plantastic.repository

import android.util.Log
import com.example.plantastic.utilities.FirebaseNodes
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class TransactionsRepository {
    private var firebaseDatabase: FirebaseDatabase =  FirebaseDatabase.getInstance()
    private var transactionsReference: DatabaseReference = firebaseDatabase.getReference(FirebaseNodes.TRANSACTIONS_NODE)

    fun getTransactionsForGroup(groupId: String): Query {
        val query =  transactionsReference.orderByChild("groupId").equalTo(groupId)
        Log.d("TransactionsRepository", "Query Path: ${query.ref}, Group ID: $query")
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    // Transaction found, parse it into a Transaction object
                    Log.d("TransactionsRepository", "here 1 ")

                } else {
                    // Transaction with the given ID not found
                    Log.d("TransactionsRepository", "here 2 ${snapshot != null} ${snapshot.exists()}")
                }
            } else {
                // Handle the error
                Log.d("TransactionsRepository", "here 3")
                Log.d("", "")
            }
        }
        return query
    }
}