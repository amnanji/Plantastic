package com.example.plantastic.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.plantastic.MainActivity
import com.example.plantastic.R
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.ui.signup.SignUpActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp


class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var submitButton: Button
    private lateinit var signUpButton: Button
    private lateinit var forgotPasswordTextView: TextView

    private var usersAuthRepository: UsersAuthRepository = UsersAuthRepository()

    override fun onStart() {
        super.onStart()
        val currentUser = usersAuthRepository.getCurrentUser()
        if (currentUser != null) {
            navigateToMainActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.editTextLoginId)
        passwordEditText = findViewById(R.id.editTextPasswordLogin)
        emailInputLayout = findViewById(R.id.emailTextInputLayoutLogin)
        passwordInputLayout= findViewById(R.id.passwordTextInputLayoutLogin)
        submitButton = findViewById(R.id.buttonSubmitLogin)
        signUpButton = findViewById(R.id.buttonSignUp)
        forgotPasswordTextView = findViewById(R.id.textViewForgotPassword)

        val verificationSnackBar = Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.verify_email),
            Snackbar.LENGTH_SHORT
        )
        verificationSnackBar.setAction(getString(R.string.resend_verification_email)) {
            usersAuthRepository.sendEmailVerification(this@LoginActivity)
            usersAuthRepository.logOutUser()
            verificationSnackBar.dismiss()
        }

        submitButton.setOnClickListener {
            if (isValidData()){
                usersAuthRepository.loginUser(emailEditText.text.toString(), passwordEditText.text.toString()){ isSuccessful ->
                    if(isSuccessful){
                        if (usersAuthRepository.getCurrentUser()!!.isEmailVerified){
                            navigateToMainActivity()
                        }
                        else{
                            verificationSnackBar.show()
                        }
                    }
                    else {
                        Toast.makeText(this,
                            getString(R.string.error_incorrect_email_password), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        passwordEditText.addTextChangedListener{
            passwordInputLayout.error = null
        }

        emailEditText.addTextChangedListener{
            emailInputLayout.error = null
        }

        signUpButton.setOnClickListener {
            navigateToSignUpActivity()
        }

        forgotPasswordTextView.setOnClickListener {
            val email = emailEditText.text.toString()

            var isValidData = true

            if(email.isBlank()){
                setNotBlankError(emailInputLayout)
                isValidData = false
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                setEmailInvalidError(emailInputLayout)
                isValidData = false
            }

            if(isValidData){
                usersAuthRepository.sendPasswordResetEmail(this, email)
            }
        }



        FirebaseApp.initializeApp(this)
    }

    private fun navigateToSignUpActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)

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

        emailInputLayout.error = null
        passwordInputLayout.error = null

        if(email.isBlank()){
            setNotBlankError(emailInputLayout)
            flag = false
        }

        if(password.isBlank()){
            setNotBlankError(passwordInputLayout)
            flag = false
        }

        //checking validity of an email
        //help from https://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            setEmailInvalidError(emailInputLayout)
            flag = false
        }

        return flag
    }

    private fun setEmailInvalidError(inputLayout: TextInputLayout) {
        inputLayout.error = getString(R.string.error_email_invalid)
    }

    private fun setNotBlankError(inputLayout: TextInputLayout){
        inputLayout.error = getString(R.string.error_blank)
    }
}
