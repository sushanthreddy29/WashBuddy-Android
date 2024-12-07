package com.unh.washbuddy_android

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

            binding.firstNameLayout.helperText = null
            binding.lastNameLayout.helperText = null
            binding.userNameLayout.helperText = null
            binding.emailLayout.helperText = null
            binding.passwordLayout.helperText = null
            binding.confirmpasswordLayout.helperText = null


            val redColor = ContextCompat.getColor(this, R.color.warning_color)

            if (firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()) {
                if (firstname.isEmpty()) {
                    binding.firstNameLayout.helperText = "Enter your Firstname"
                    binding.firstNameLayout.setHelperTextColor(ColorStateList.valueOf(redColor))
                }
                if (lastname.isEmpty()) {
                    binding.lastNameLayout.helperText = "Enter your Lastname"
                    binding.lastNameLayout.setHelperTextColor(ColorStateList.valueOf(redColor))
                }
                if (username.isEmpty()) {
                    binding.userNameLayout.helperText = "Enter your Username"
                    binding.userNameLayout.setHelperTextColor(ColorStateList.valueOf(redColor))
                }
                if (email.isEmpty()) {
                    binding.emailLayout.helperText = "Enter your Email Address"
                    binding.emailLayout.setHelperTextColor(ColorStateList.valueOf(redColor))
                }
                if (password.isEmpty()) {
                    binding.passwordLayout.helperText = "Enter your Password"
                    binding.passwordLayout.setHelperTextColor(ColorStateList.valueOf(redColor))
                }
                if (confirmpassword.isEmpty()) {
                    binding.confirmpasswordLayout.helperText = "Confirm your Password"
                    binding.confirmpasswordLayout.setHelperTextColor(ColorStateList.valueOf(redColor))
                }
            }

            if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailLayout.helperText = "Enter valid email address"
                binding.emailLayout.setHelperTextColor(ColorStateList.valueOf(redColor))
            }

            if (password != confirmpassword) {
                binding.confirmpasswordLayout.helperText = "Passwords do not match"
                binding.confirmpasswordLayout.setHelperTextColor(ColorStateList.valueOf(redColor))
            }

            //https://firebase.google.com/docs/firestore/manage-data/add-data#kotlin+ktx_4
            //https://firebase.google.com/docs/auth/android/password-auth
            if (firstname.isNotEmpty() && lastname.isNotEmpty() && username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmpassword.isNotEmpty() && password == confirmpassword) {
                db.collection("UserCredentials")
                    .whereEqualTo("Username", username)
                    .get()
                    .addOnSuccessListener { usercredentials ->
                        if (!usercredentials.isEmpty) {
                            Toast.makeText(this, "Username already exist, try login.", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            firebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        current_user = firebaseAuth.currentUser
                                        db.collection("UserCredentials")
                                            .whereEqualTo("email", email)
                                            .get()
                                            .addOnSuccessListener { usercredentials ->
                                                if (!usercredentials.isEmpty) {
                                                    Toast.makeText(this, "Email already exist, try login.", Toast.LENGTH_SHORT).show()
                                                }
                                                else {
                                                    val UserCredentials = hashMapOf(
                                                        "Firstname" to firstname,
                                                        "Lastname" to lastname,
                                                        "Username" to username,
                                                        "email" to email,
                                                        "useruid" to current_user!!.uid,
                                                        "Fingerprintsignin" to false,
                                                    )
                                                    db.collection("UserCredentials")
                                                        .add(UserCredentials)
                                                        .addOnSuccessListener { documentReference ->
                                                            Log.d(TAG,"DocumentSnapshot written successfully with ID: ${documentReference.id}")
                                                            val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                                                            val editor = sharedPreferences.edit()
                                                            editor.putBoolean("fingerprint_sign_in", false )
                                                            editor.apply()
                                                        }
                                                        .addOnFailureListener { exception ->
                                                            Log.w(TAG, "Error adding document", exception)
                                                        }
                                                    val intent = Intent(this, signin::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }


                                    } else {
                                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                                    }
                                }
                    }
                        }
            }

        }
    }
}


