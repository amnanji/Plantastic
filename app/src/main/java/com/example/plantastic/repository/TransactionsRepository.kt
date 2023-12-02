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
        return transactionsReference.orderByChild(FirebaseNodes.TRANSACTIONS_GROUP_NODE).equalTo(groupId)
    }
}