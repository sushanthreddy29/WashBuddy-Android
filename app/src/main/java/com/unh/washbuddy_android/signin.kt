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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unh.washbuddy_android.databinding.ActivitySigninBinding
import com.unh.washbuddy_android.databinding.ActivitySignupBinding

class signin : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newuser = findViewById<TextView>(R.id.newuser)

        newuser.setOnClickListener{
            val intent = Intent(this,signup::class.java)
            startActivity(intent)
        }

        val forgotpassword = findViewById<TextView>(R.id.forgotpassword)

        forgotpassword.setOnClickListener{
            val intent = Intent(this, passwordreset::class.java)
            startActivity(intent)
        }

        binding.signin.setOnClickListener{
            validateAppLogin()
        }

    }

    private fun validateAppLogin(){
        val email = binding.email.text.toString()
        val userpassword = binding.password.text.toString()

        // Checking wheather email and username is empty or not before clicking button
        if(email.isEmpty() || userpassword.isEmpty()) {
            Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Checking email is valid or not
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // checking given email equals to database email
        db.collection("UserCredentials")
            .whereEqualTo("email",email)
            .get()
            .addOnSuccessListener { usercredentials ->
                if(usercredentials.isEmpty)
                {
                    Toast.makeText(this, "Email does not exist, try register.", Toast.LENGTH_SHORT).show()
                }
                else{
                    val userDetails = usercredentials.firstOrNull()
                    val password = userDetails?.getString("password")

                    if(password == userpassword)
                    {

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                        binding.email.setText("")
                        binding.password.setText("")

                    }
                    else{
                        Toast.makeText(this, "Password is incorrect.", Toast.LENGTH_SHORT).show()
                    }

                }
            }
            .addOnFailureListener{ exception ->
                Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show()
            }
    }
}