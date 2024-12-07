package com.unh.washbuddy_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.unh.washbuddy_android.databinding.ActivityPasswordresetBinding

class passwordreset : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordresetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPasswordresetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val callback = this.onBackPressedDispatcher.addCallback(this){
            val intent = Intent(this@passwordreset,signin::class.java)
            startActivity(intent)
        }

        //https://firebase.google.com/docs/auth/android/manage-users
        binding.resetpassword.setOnClickListener {
            val email = binding.email.text.toString().trim()
            if(email != ""){
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("PasswordReset", "Email sent.")
                            val intent = Intent(this,signin::class.java)
                            startActivity(intent)
                        } else {
                            Log.e("PasswordReset", "Error: ${task.exception?.message}")
                        }
                    }

            }
            else{
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }

        }

    }
}