package com.erkindilekci.chatapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.erkindilekci.chatapp.R
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignUp)
        progressBar = findViewById(R.id.progressBar)

        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                edtEmail.visibility = View.INVISIBLE
                edtPassword.visibility = View.INVISIBLE
                btnLogin.visibility = View.INVISIBLE
                btnSignUp.visibility = View.INVISIBLE

                login(email, password)
            } else {
                Toast.makeText(
                    this@Login,
                    "Email and password can't be empty.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@Login, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(edtPassword.windowToken, 0)

                    progressBar.visibility = View.INVISIBLE
                } else {
                    Toast.makeText(
                        this@Login,
                        "${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    progressBar.visibility = View.INVISIBLE
                    edtEmail.visibility = View.VISIBLE
                    edtPassword.visibility = View.VISIBLE
                    btnLogin.visibility = View.VISIBLE
                    btnSignUp.visibility = View.VISIBLE
                }
            }
    }
}
