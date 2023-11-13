package com.example.plantastic.repository

import com.google.firebase.auth.FirebaseAuth

class UserAuthRepository {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var usersRepository: UsersRepository = UsersRepository()

    fun createNewAuthUser(firstName: String, lastName: String, username: String, email: String, password: String, onComplete: (Boolean) -> Unit){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    usersRepository.createNewUser(firstName, lastName, username, email) {isSuccessful ->
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
}