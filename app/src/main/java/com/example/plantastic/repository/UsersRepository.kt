package com.example.plantastic.repository

import com.example.plantastic.models.Users
import com.example.plantastic.utilities.FirebaseNodes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
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

        val user = Users(userId, firstName, lastName, username, email, HashMap())
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

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(Users::class.java)
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

    // Update the username of a user
    fun updateUsername(userId: String, username: String, callback: (Users?) -> Unit){
        val reference = usersReference.child(userId)
        reference.child(FirebaseNodes.USERNAME_NODE).setValue(username)
    }

    // Query all users with the same username, ensure no one else has that username
    fun isUsernameUnique(userId: String, username: String, callback: (Boolean?) -> Unit){
        val query: Query = usersReference.orderByChild(FirebaseNodes.USERNAME_NODE).equalTo(username)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    val count = dataSnapshot.children.count()
                    val users = dataSnapshot.children
                    if (count == 0){
                        callback(true)
                    } else if (count > 1){
                        callback(false)
                    } else {
                        var isOldUsername = false
                        for(user in users){
                            if(user.key.toString() == userId){
                                isOldUsername = true
                            }
                        }
                        callback(isOldUsername)
                    }
                } else {
                    callback(true)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
            }
        })
    }

    fun getUsernameQuery(searchString: String): Query {
        val usernameQuery = usersReference.orderByChild(FirebaseNodes.USERNAME_NODE)
        return usernameQuery.startAt(searchString).endAt("$searchString\uf8ff")
    }

    fun getInitialFriendsQuery(userId: String): Query {
        return usersReference.orderByChild("${FirebaseNodes.USERS_FRIENDS_NODE}/$userId").equalTo(true)
    }

    fun addFriends(userId1: String, userId2: String){
        this.getUserById(userId1){
            if (it != null){
                val userReference = firebaseDatabase.getReference("${FirebaseNodes.USERS_NODE}/$userId1")
                it.friends?.put(userId2, true)
                userReference.setValue(it)
            }
        }
        this.getUserById(userId2){
            if (it != null){
                val userReference = firebaseDatabase.getReference("${FirebaseNodes.USERS_NODE}/$userId2")
                it.friends?.put(userId1, true)
                userReference.setValue(it)
            }
        }
    }

    companion object {
        private const val TAG = "Pln UsersRepository"
    }
}