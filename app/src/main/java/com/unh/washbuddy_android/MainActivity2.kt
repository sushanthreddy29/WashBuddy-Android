package com.unh.washbuddy_android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unh.washbuddy_android.AppData.email


class MainActivity2 : AppCompatActivity() {


    private lateinit var btnsignup: Button
    private lateinit var btnsignin: Button

    private val currentuser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        supportActionBar?.hide()

        btnsignup = findViewById(R.id.button)
        btnsignin = findViewById(R.id.button2)

        if (checkUsersignin()) {
            if(currentuser!!.uid == "8HF4Jx8ccHQ3Q296a9maTio0isW2"){
                val intent = Intent(this, AdminDashboard::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
                Toast.makeText(this, "Welcome, Admin!", Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Welcome, User!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        btnsignup.setOnClickListener{
            val intent = Intent(this, signup::class.java)
            startActivity(intent)
        }

        btnsignin.setOnClickListener{
            val intent = Intent(this,signin::class.java)
            startActivity(intent)
        }
    }

    private fun checkUsersignin(): Boolean {
        val currentuser = FirebaseAuth.getInstance().currentUser

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
                            usersignin.fingerprintsignin = users.getBoolean("Fingerprintsignin")!!
                            val biometric = usersignin.fingerprintsignin
                            Log.d("BIO", biometric.toString())
                            AppData.email = usersignin.email
                            val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("fingerprint_sign_in", biometric )
                            editor.apply()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("LOGINERROR", "error", e)
                }
        }
        return currentuser != null
    }

}