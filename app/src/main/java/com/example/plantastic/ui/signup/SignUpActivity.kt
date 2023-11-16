package com.example.plantastic.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.plantastic.MainActivity
import com.example.plantastic.R

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val usernameEditText: EditText = findViewById(R.id.editTextUsername)
        val emailEditText: EditText = findViewById(R.id.editTextEmail)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)
        val confirmPasswordEditText: EditText = findViewById(R.id.editTextConfirmPassword)
        val signUpButton: Button = findViewById(R.id.buttonSubmitSignUp)

        signUpButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (isValidSignUp(username, email, password, confirmPassword)) {
                // If sign-up is successful, navigate to the main activity
                navigateToMainActivity()
            } else {

                Toast.makeText(this, "Invalid sign-up credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidSignUp(username: String, email: String, password: String, confirmPassword: String): Boolean {
        // Implement sign-up validation logic
        return username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && password == confirmPassword
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
