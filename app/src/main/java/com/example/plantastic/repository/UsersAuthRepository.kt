package com.example.plantastic.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// Help from - https://firebase.google.com/docs/auth/android/password-auth
class UsersAuthRepository {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var usersRepository: UsersRepository = UsersRepository()

    fun createNewAuthUser(firstName: String, lastName: String, username: String, email: String, password: String, onComplete: (Boolean) -> Unit){

        // creating a new user also automatically signs them in
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Cannot be null because task was successful
                    // Help from - https://stackoverflow.com/questions/70283293/why-does-firebase-realtime-database-user-id-not-match-with-the-firebase-authenti
                    val currUserUid = getCurrentUser()!!.uid
                    usersRepository.createNewUser(currUserUid, firstName, lastName, username, email) { isSuccessful ->
                        onComplete(isSuccessful)
                    }
                } else {
                    onComplete(false)
                }
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun loginUser(email: String, password: String, onComplete: (Boolean) -> Unit){
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun logOutUser(){
        firebaseAuth.signOut()
    }
}