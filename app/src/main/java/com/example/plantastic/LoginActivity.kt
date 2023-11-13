package com.example.plantastic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText


class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.editTextLoginId)
        passwordEditText = findViewById(R.id.editTextPassword)
        submitButton = findViewById(R.id.buttonSubmit)
        signUpButton = findViewById(R.id.buttonSignUp)

        submitButton.setOnClickListener {
            if (isValidData()){
                navigateToMainActivity()
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

    private fun isValidData(): Boolean {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        var flag = true

        if(email.isBlank()){
            setNotBlankError(emailEditText)
            flag = false
        }

        if(password.isBlank()){
            setNotBlankError(passwordEditText)
            flag = false
        }

        //checking validity of an email
        //help from https://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            setEmailInvalidError(emailEditText)
            flag = false
        }

        return flag
    }

    private fun setEmailInvalidError(editText: EditText) {
        editText.error = getString(R.string.error_email_invalid)
    }

    private fun setNotBlankError(editText: EditText){
        editText.error = getString(R.string.error_blank)
    }
}
