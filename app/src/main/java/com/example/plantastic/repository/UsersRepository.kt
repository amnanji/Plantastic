package com.example.plantastic.repository

import com.example.plantastic.utilities.FirebaseNodes
import com.example.plantastic.models.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking

// Help from - https://firebase.google.com/docs/database/android/read-and-write
class UsersRepository {
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var usersReference: DatabaseReference =
        firebaseDatabase.getReference(FirebaseNodes.USERS_NODE)

    fun createNewUser(
        userId: String,
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        onComplete: (Boolean) -> Unit
    ) {

        val user = Users(firstName, lastName, username, email)
        usersReference.child(userId).setValue(user)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun isFieldUnique(nodeName: String, value: String): Boolean {
        val deferred = CompletableDeferred<Boolean>()
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

    fun getUserById(id: String, callback: (Users?) -> Unit) {
        val reference = usersReference.child(id)
//        Log.d(TAG, "Received request --> $id")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(Users::class.java)
//                Log.d(TAG, "got user inside usersRepository : $user")
                callback(user)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                callback(null)
            }
        })
    }

    fun getUsersById(ids: List<String>): ArrayList<Users> {
        val ret = ArrayList<Users>()

        for (id in ids) {
            val reference = usersReference.child(id)
            val deferred = CompletableDeferred<Users?>()

            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    deferred.complete(snapshot.getValue(Users::class.java))
                }

                override fun onCancelled(error: DatabaseError) {
                    deferred.completeExceptionally(error.toException())
                }
            })
            runBlocking { deferred.await() }?.let { ret.add(it) }
        }
        return ret
    }

    companion object {
        private const val TAG = "Pln UsersRepository"
    }
}