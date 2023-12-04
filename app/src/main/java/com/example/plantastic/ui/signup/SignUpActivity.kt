package com.example.plantastic.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.repository.UsersRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.plantastic.utilities.FirebaseNodes
import com.example.plantastic.R
import com.example.plantastic.ui.login.LoginActivity

class SignUpActivity : AppCompatActivity() {

    private var usersAuthRepository: UsersAuthRepository = UsersAuthRepository()
    private var usersRepository: UsersRepository = UsersRepository()

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
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
        signUpButton = findViewById(R.id.buttonSubmitSignUp)

        signUpButton.setOnClickListener {

            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val cont = this

            CoroutineScope(Dispatchers.IO).launch {
                if (isValidSignUp()){
                    usersAuthRepository.createNewAuthUser(email, password) { isSuccessful ->
                        if (isSuccessful) {
                            // Cannot be null because task was successful
                            // Help from - https://stackoverflow.com/questions/70283293/why-does-firebase-realtime-database-user-id-not-match-with-the-firebase-authenti
                            val currUserUid = usersAuthRepository.getCurrentUser()!!.uid
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
            setNotBlankError(firstNameEditText)
            flag = false
        }

        if (lastName.isBlank()){
            setNotBlankError(lastNameEditText)
            flag = false
        }

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
            withContext(Dispatchers.Main){
                confirmPasswordEditText.error = getString(R.string.error_password_mismatch)
                passwordEditText.error = getString((R.string.error_password_mismatch))
            }
            flag = false
        }

        // checking password length requirements
        if (password.length < 6){
            withContext(Dispatchers.Main){
                confirmPasswordEditText.error = getString(R.string.error_password_length)
                passwordEditText.error = getString((R.string.error_password_length))
            }
            flag = false
        }

        if (email.isNotBlank() && !usersRepository.isFieldUnique(FirebaseNodes.EMAIL_NODE, email)) {
            withContext(Dispatchers.Main){
                emailEditText.error = getString(R.string.error_duplicate_email)
            }
            flag = false
        }

        if(username.isNotBlank() && !usersRepository.isFieldUnique(FirebaseNodes.USERNAME_NODE, username)){
            withContext(Dispatchers.Main){
                usernameEditText.error = getString(R.string.error_duplicate_username)
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

    private suspend fun setEmailInvalidError(editText: EditText) {
        withContext(Dispatchers.Main){
            editText.error = getString(R.string.error_email_invalid)
        }
    }
    private suspend fun setNotBlankError(editText: EditText){
        withContext(Dispatchers.Main){
            editText.error = getString(R.string.error_blank)
        }
    }

    private suspend fun makeSignUpErrorToast(){
        withContext(Dispatchers.Main){
            Toast.makeText(this@SignUpActivity, getString(R.string.error_unexpected), Toast.LENGTH_SHORT).show()
            if(usersAuthRepository.getCurrentUser() == null){
                usersAuthRepository.logOutUser()
            }
        }
    }
}
