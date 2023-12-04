package com.example.plantastic.repository

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// Help from - https://firebase.google.com/docs/auth/android/password-auth
class UsersAuthRepository {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun createNewAuthUser(email: String, password: String, onComplete: (Boolean) -> Unit){

        // creating a new user also automatically signs them in
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
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

    fun sendEmailVerification(activity: Activity, user: FirebaseUser?, usersRepository: UsersRepository) {
        user?.sendEmailVerification()
            ?.addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Update the Realtime Database to indicate email verification status
                    usersRepository.updateUserVerificationStatus(user.uid, false)
                }
            }
    }
}