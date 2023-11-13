package com.example.plantastic.repository

import android.content.Context
import com.example.plantastic.FirebaseNodes
import com.example.plantastic.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking

class UsersRepository {
    private var firebaseDatabase: FirebaseDatabase =  FirebaseDatabase.getInstance()
    private var usersReference: DatabaseReference = firebaseDatabase.getReference(FirebaseNodes.USERS_NODE)

    fun createNewUser(firstName: String, lastName: String, username: String, email: String, onComplete: (Boolean) -> Unit) {

        val user = Users(firstName, lastName, username, email)
        val userKey = usersReference.push().key
        userKey?.let {
            usersReference.child(it).setValue(user)
                .addOnSuccessListener {
                    onComplete(true)
                }
                .addOnFailureListener {
                    onComplete(false)
                }
        }
    }

    fun isFieldUnique(nodeName: String, value: String): Boolean {
        val deferred =  CompletableDeferred<Boolean>()
        usersReference.orderByChild(nodeName).equalTo(value).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                deferred.complete(!snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                deferred.completeExceptionally(error.toException())
            }
        })
        return runBlocking { deferred.await() }
    }
}