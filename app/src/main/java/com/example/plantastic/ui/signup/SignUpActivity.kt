package com.example.plantastic.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.repository.UsersRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.plantastic.utilities.FirebaseNodes
import com.example.plantastic.R
import com.example.plantastic.ui.login.LoginActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : AppCompatActivity() {

    private var usersAuthRepository: UsersAuthRepository = UsersAuthRepository()
    private var usersRepository: UsersRepository = UsersRepository()

    private lateinit var firstNameEditText: TextInputEditText
    private lateinit var lastNameEditText: TextInputEditText
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText

    private lateinit var firstNameInputLayout: TextInputLayout
    private lateinit var lastNameInputLayout: TextInputLayout
    private lateinit var usernameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        firstNameEditText = findViewById(R.id.editTextFirstName)
        lastNameEditText = findViewById(R.id.editTextLastName)
        usernameEditText = findViewById(R.id.editTextUsername)
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        confirmPasswordEditText= findViewById(R.id.editTextConfirmPassword)

        firstNameInputLayout = findViewById(R.id.editTextInputLayoutFirstName)
        lastNameInputLayout= findViewById(R.id.editTextInputLayoutLastName)
        usernameInputLayout = findViewById(R.id.editTextInputLayoutUsername)
        emailInputLayout = findViewById(R.id.editTextInputLayoutEmail)
        passwordInputLayout= findViewById(R.id.editTextInputLayoutPassword)
        confirmPasswordInputLayout= findViewById(R.id.editTextInputLayoutConfirmPassword)
        signUpButton = findViewById(R.id.buttonSubmitSignUp)

        emailEditText.addTextChangedListener{
            emailInputLayout.error = null
        }

        passwordEditText.addTextChangedListener{
            passwordInputLayout.error = null
        }

        confirmPasswordEditText.addTextChangedListener{
            confirmPasswordInputLayout.error = null
        }

        usernameEditText.addTextChangedListener{
            usernameInputLayout.error = null
        }

        lastNameEditText.addTextChangedListener{
            lastNameInputLayout.error = null
        }

        firstNameEditText.addTextChangedListener{
            firstNameInputLayout.error = null
        }

        signUpButton.setOnClickListener {

            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val cont = this

            clearErrors()

            CoroutineScope(Dispatchers.IO).launch {
                if (isValidSignUp()){
                    usersAuthRepository.createNewAuthUser(email, password) { isSuccessful ->
                        if (isSuccessful) {
                            // Cannot be null because task was successful
                            // Help from - https://stackoverflow.com/questions/70283293/why-does-firebase-realtime-database-user-id-not-match-with-the-firebase-authenti

                            val currUser = usersAuthRepository.getCurrentUser()

                            usersAuthRepository.sendEmailVerification(this@SignUpActivity)

                            val currUserUid = currUser!!.uid
                            usersRepository.createNewUser(
                                cont,
                                currUserUid,
                                firstName,
                                lastName,
                                username,
                                email
                            ) { isSuccessful ->
                                if (isSuccessful) {
                                    navigateToLoginActivity()
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    private fun clearErrors(){
        firstNameInputLayout.error = null
        lastNameInputLayout.error = null
        usernameInputLayout.error = null
        emailInputLayout.error = null
        passwordInputLayout.error = null
        confirmPasswordInputLayout.error = null
    }

    private suspend fun isValidSignUp(): Boolean {

        val firstName = firstNameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        var flag = true

        // checking if all edit texts have been filled
        if (firstName.isBlank()){
            setNotBlankError(firstNameInputLayout)
            flag = false
        }

        if (lastName.isBlank()){
            setNotBlankError(lastNameInputLayout)
            flag = false
        }

        if (username.isBlank()){
            setNotBlankError(usernameInputLayout)
            flag = false
        }

        if (email.isBlank()){
            setNotBlankError(emailInputLayout)
            flag = false
        }


        if (password.isBlank()){
            setNotBlankError(passwordInputLayout)
            flag = false
        }


        if (confirmPassword.isBlank()){
            setNotBlankError(confirmPasswordInputLayout)
            flag = false
        }

        //checking validity of an email
        //help from https://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            setEmailInvalidError(emailInputLayout)
            flag = false
        }

        // checking if passwords match
        if (password != confirmPassword){
            withContext(Dispatchers.Main){
                confirmPasswordInputLayout.error = getString(R.string.error_password_mismatch)
                passwordInputLayout.error = getString(R.string.error_password_mismatch)
            }
            flag = false
        }

        // checking password length requirements
        if (password.length < 6){
            withContext(Dispatchers.Main){
                confirmPasswordInputLayout.error = getString(R.string.error_password_length)
                passwordInputLayout.error = getString(R.string.error_password_length)
            }
            flag = false
        }

        if (email.isNotBlank() && !usersRepository.isFieldUnique(FirebaseNodes.EMAIL_NODE, email)) {
            withContext(Dispatchers.Main){
                emailInputLayout.error = getString(R.string.error_duplicate_email)
            }
            flag = false
        }

        if(username.isNotBlank() && !usersRepository.isFieldUnique(FirebaseNodes.USERNAME_NODE, username)){
            withContext(Dispatchers.Main){
                usernameInputLayout.error = getString(R.string.error_duplicate_username)
            }
            flag = false
        }

        return flag
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private suspend fun setEmailInvalidError(editText: TextInputLayout) {
        withContext(Dispatchers.Main){
            editText.error = getString(R.string.error_email_invalid)
        }
    }
    private suspend fun setNotBlankError(editText: TextInputLayout){
        withContext(Dispatchers.Main){
            editText.error = getString(R.string.error_blank)
        }
    }
}
