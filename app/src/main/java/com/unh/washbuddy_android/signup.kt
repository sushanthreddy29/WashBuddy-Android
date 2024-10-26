package com.unh.washbuddy_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.unh.washbuddy_android.databinding.ActivitySignupBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class signup : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val db = Firebase.firestore
    private var TAG = "WashBuddy-Android"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backtosignin = findViewById<TextView>(R.id.backtosignin)
        backtosignin.setOnClickListener{
            val intent = Intent(this,signin::class.java)
            startActivity(intent)
        }

        binding.signup.setOnClickListener{
            writeUserDetailsToFirebase()
        }

    }

    private fun writeUserDetailsToFirebase(){
        val firstname = binding.firstName
        val lastname = binding.lastName
        val username = binding.username
        val email = binding.email
        val password = binding.password
        val confirmpassword = binding.confirmpassword

        Log.d(TAG,"Variables: $firstname $lastname $username $email $password ")

        val UserCredentials = hashMapOf(
            "Firstname" to firstname.text.toString(),
            "Lastname" to lastname.text.toString(),
            "Username" to username.text.toString(),
            "email" to email.text.toString(),
            "password" to password.text.toString()
        )

        db.collection("UserCredentials")
            .add(UserCredentials)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG,"DocumentSnapshot written successfully with ID: ${documentReference.id}")
            }
            .addOnFailureListener{ exception ->
                Log.w(TAG, "Error adding document", exception)
            }
        firstname.setText("")
        lastname.setText("")
        username.setText("")
        email.setText("")
        password.setText("")
        confirmpassword.setText("")
    }
}
