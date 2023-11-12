package com.example.plantastic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

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

            // Validate sign-up credentials (replace with your actual sign-up logic)
            if (isValidSignUp(username, email, password, confirmPassword)) {
                navigateToMainActivity()
            } else {
                Toast.makeText(this, "Invalid sign-up credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidSignUp(username: String, email: String, password: String, confirmPassword: String): Boolean {
        // TO DO: Implement validation logic
        return username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && password == confirmPassword
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
    }
}