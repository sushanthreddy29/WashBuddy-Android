package com.unh.washbuddy_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unh.washbuddy_android.databinding.ActivitySigninBinding
import com.unh.washbuddy_android.databinding.ActivitySignupBinding

class signin : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()

        val newuser = findViewById<TextView>(R.id.newuser)

        newuser.setOnClickListener{
            val intent = Intent(this,signup::class.java)
            startActivity(intent)
        }

        val forgotpassword = findViewById<Button>(R.id.forgotpassword)

        forgotpassword.setOnClickListener{
            val intent = Intent(this, passwordreset::class.java)
            startActivity(intent)
        }

        binding.signin.setOnClickListener{
            validateAppLogin()
        }

    }

    private fun validateAppLogin() {
        val email = binding.email.text.toString()
        val userpassword = binding.password.text.toString()

        // Checking wheather email and username is empty or not before clicking button
        if (email.isEmpty() || userpassword.isEmpty()) {
            Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Checking email is valid or not
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, userpassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Check if the email is the admin email

                if (email == "Admin@gmail.com" || email == "admin@gmail.com") {
                    val intent = Intent(this, AdminDashboard::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    Toast.makeText(this, "Welcome, Admin!", Toast.LENGTH_SHORT).show()
                } else {
                    val currentuser = firebaseAuth.currentUser
                    if (currentuser != null) {
                        usersignin.useruid = currentuser.uid
                        Firebase.firestore.collection("UserCredentials")
                            .get()
                            .addOnSuccessListener {result ->
                                for (users in result) {
                                    val documentUserUID = users.getString("useruid")
                                    if (usersignin.useruid == documentUserUID) {
                                        usersignin.documentid =  users.id
                                        usersignin.firstname = users.getString("Firstname")!!
                                        usersignin.lastname = users.getString("Lastname")!!
                                        usersignin.username = users.getString("Username")!!
                                        usersignin.email = users.getString("email")!!
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w("LOGINERROR", "error", e)
                            }
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    Toast.makeText(this, "Welcome, User!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    this,
                    task.exception.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}