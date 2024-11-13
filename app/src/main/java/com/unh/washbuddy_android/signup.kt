package com.unh.washbuddy_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.unh.washbuddy_android.databinding.ActivitySignupBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class signup : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private var TAG = "WashBuddy-Android"
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore
    private var current_user: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()


        val backtosignin = findViewById<TextView>(R.id.backtosignin)
        backtosignin.setOnClickListener {
            val intent = Intent(this, signin::class.java)
            startActivity(intent)
        }

        binding.signup.setOnClickListener {
            val firstname = binding.firstName.text.toString()
            val lastname = binding.lastName.text.toString()
            val username = binding.userName.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmpassword = binding.confirmpassword.text.toString()

            if (firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()) {
                Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show()
            }

            if (password != confirmpassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show()
            }
            if (password == confirmpassword) {
                db.collection("UserCredentials")
                    .whereEqualTo("Username", username)
                    .get()
                    .addOnSuccessListener { usercredentials ->
                        if (!usercredentials.isEmpty) {
                            Toast.makeText(
                                this,
                                "Username already exist, try login.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }else{

                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    current_user = firebaseAuth.currentUser
                                    db.collection("UserCredentials")
                                        .whereEqualTo("email", email)
                                        .get()
                                        .addOnSuccessListener { usercredentials ->
                                            if (!usercredentials.isEmpty) {
                                                Toast.makeText(
                                                    this,
                                                    "Email already exist, try login.",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            } else {
                                                val UserCredentials = hashMapOf(
                                                    "Firstname" to firstname,
                                                    "Lastname" to lastname,
                                                    "Username" to username,
                                                    "email" to email,
                                                    "useruid" to current_user!!.uid,
                                                )
                                                db.collection("UserCredentials")
                                                    .add(UserCredentials)
                                                    .addOnSuccessListener { documentReference ->
                                                        Log.d(
                                                            TAG,
                                                            "DocumentSnapshot written successfully with ID: ${documentReference.id}"
                                                        )
                                                    }
                                                    .addOnFailureListener { exception ->
                                                        Log.w(
                                                            TAG,
                                                            "Error adding document",
                                                            exception
                                                        )
                                                    }
                                                val intent = Intent(this, signin::class.java)
                                                startActivity(intent)
                                            }
                                        }


                                } else {
                                    Toast.makeText(
                                        this,
                                        it.exception.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                        }
            }

        }
    }
}


