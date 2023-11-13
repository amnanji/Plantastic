package com.example.plantastic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.plantastic.models.Users
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class SignUpActivity : AppCompatActivity() {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        usernameEditText = findViewById(R.id.editTextUsername)
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        confirmPasswordEditText= findViewById(R.id.editTextConfirmPassword)
        signUpButton = findViewById(R.id.buttonSubmitSignUp)

        firebaseDatabase = FirebaseDatabase.getInstance()
        usersRef = firebaseDatabase.getReference("users")

        signUpButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                if (isValidSignUp(username, email, password, confirmPassword)){
                    writeNewUser(username, email, password)
                    navigateToMainActivity()
                }
            }
        }
    }

    private fun writeNewUser(username: String, email: String, password: String) {
        val user = Users(username, email, password)
        val userKey = usersRef.push().key
        userKey?.let {
            usersRef.child(it).setValue(user)
                .addOnSuccessListener {
                    // User added successfully
                    // Add additional actions or callbacks here
                }
                .addOnFailureListener {
                    // Failed to add user
                    // Handle the error as needed
                }
        }
    }

    private suspend fun isValidSignUp(username: String, email: String, password: String, confirmPassword: String): Boolean {

        var flag = true

        // checking if all edit texts have been filled
        if (username.isBlank()){
            setNotBlankError(usernameEditText)
            flag = false
        }

        if (email.isBlank()){
            setNotBlankError(emailEditText)
            flag = false
        }


        if (password.isBlank()){
            setNotBlankError(passwordEditText)
            flag = false
        }


        if (confirmPassword.isBlank()){
            setNotBlankError(confirmPasswordEditText)
            flag = false
        }

        //checking validity of an email
        //help from https://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            setEmailInvalidError(emailEditText)
            flag = false
        }

        // checking if passwords match
        if (password != confirmPassword){
            confirmPasswordEditText.error = getString(R.string.error_password_mismatch)
            passwordEditText.error = getString((R.string.error_password_mismatch))
            flag = false
        }

        // checking password length requirements
        if (password.length < 6){
            confirmPasswordEditText.error = getString(R.string.error_password_length)
            passwordEditText.error = getString((R.string.error_password_length))
            flag = false
        }

        // early return so blank strings are no queried against
        if (!flag){
            return false
        }

        if (isFieldUnique("email", email)) {
            withContext(Dispatchers.Main){
                emailEditText.error = "Email is already in use"
            }
            flag = false
        }

        if(isFieldUnique("username", username)){
            withContext(Dispatchers.Main){
                usernameEditText.error = "Username is already in use"
            }
            flag = false
        }

        return flag
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isFieldUnique(nodeName: String, value: String): Boolean {
        val deferred =  CompletableDeferred<Boolean>()
        usersRef.orderByChild(nodeName).equalTo(value).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isUnique = snapshot.exists()
                deferred.complete(isUnique)
            }

            override fun onCancelled(error: DatabaseError) {
                deferred.completeExceptionally(error.toException())
            }
        })
        return runBlocking { deferred.await() }
    }

    private fun setEmailInvalidError(editText: EditText) {
        editText.error = getString(R.string.error_email_invalid)
    }
    private fun setNotBlankError(editText: EditText){
        editText.error = getString(R.string.error_blank)
    }
}
