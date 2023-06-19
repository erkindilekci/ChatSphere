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
import com.erkindilekci.chatapp.BuildConfig
import com.erkindilekci.chatapp.R
import com.erkindilekci.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbDef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        edtName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSignUp = findViewById(R.id.btnSignUp)
        progressBar = findViewById(R.id.progressBar)

        btnSignUp.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                edtName.visibility = View.INVISIBLE
                edtEmail.visibility = View.INVISIBLE
                edtPassword.visibility = View.INVISIBLE
                btnSignUp.visibility = View.INVISIBLE

                signUp(name, email, password)
            } else {
                Toast.makeText(
                    this@SignUp,
                    "Email and password can't be empty.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun signUp(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(name, email, mAuth.currentUser?.uid!!)

                    val intent = Intent(this@SignUp, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(edtPassword.windowToken, 0)

                    progressBar.visibility = View.INVISIBLE
                } else {
                    Toast.makeText(
                        this@SignUp,
                        "${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    progressBar.visibility = View.INVISIBLE
                    edtName.visibility = View.VISIBLE
                    edtEmail.visibility = View.VISIBLE
                    edtPassword.visibility = View.VISIBLE
                    btnSignUp.visibility = View.VISIBLE
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
        mDbDef = FirebaseDatabase.getInstance(BuildConfig.DATABASE_URL).reference
        mDbDef.child("user").child(uid).setValue(User(name, email, uid))
    }
}
