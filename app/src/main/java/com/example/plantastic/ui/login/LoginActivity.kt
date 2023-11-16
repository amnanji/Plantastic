package com.example.plantastic.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.plantastic.MainActivity
import com.example.plantastic.R
import com.example.plantastic.ui.signup.SignUpActivity


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginIdEditText: EditText = findViewById(R.id.editTextLoginId)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)
        val submitButton: Button = findViewById(R.id.buttonSubmit)
        val signUpButton: Button = findViewById(R.id.buttonSignUp)

        submitButton.setOnClickListener {
            // Perform login logic here
            if (performLogin(loginIdEditText.text.toString(), passwordEditText.text.toString())) {
                // If login is successful, navigate to the main activity
                navigateToMainActivity()
            } else {
                // Handle unsuccessful login
            }

        }
        signUpButton.setOnClickListener {
            navigateToSignUpActivity()
        }
    }

    private fun navigateToSignUpActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)

    }

    private fun performLogin(loginId: String, password: String): Boolean {

       // return (loginId == "your_username" && password == "your_password")
        return true

    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
