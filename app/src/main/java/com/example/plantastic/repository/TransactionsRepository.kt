package com.example.plantastic.repository

import android.util.Log
import com.example.plantastic.utilities.FirebaseNodes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class TransactionsRepository {
    private var firebaseDatabase: FirebaseDatabase =  FirebaseDatabase.getInstance()
    private var transactionsReference: DatabaseReference = firebaseDatabase.getReference(FirebaseNodes.TRANSACTIONS_NODE)

    fun getTransactionsForGroup(groupId: String): Query {
        val query =  transactionsReference.orderByChild(FirebaseNodes.TRANSACTIONS_GROUP_NODE).equalTo(groupId)
        Log.d("TransactionsRepository", "Query Path: ${query.ref}, Group ID: $groupId")
//        query.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                   Log.d("TransactionsRepository", "${dataSnapshot}")
//                } else {
//                    println("User not found")
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                println("Error querying user: ${databaseError.message}")
//            }
//        })
        return query
    }
}