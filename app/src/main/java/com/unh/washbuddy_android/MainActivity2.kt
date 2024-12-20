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

// Some parts of this code were generated with the assistance of ChatGPT by OpenAI.
// This includes implementations related to [briefly describe the feature or functionality, e.g., "RecyclerView item click handling" or "Firebase data retrieval"].

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
                finish()
            }
            else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
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

    //https://developer.android.com/reference/android/content/SharedPreferences
    //https://firebase.google.com/docs/auth/android/start
    //https://firebase.google.com/docs/firestore/query-data/get-data
    private fun checkUsersignin(): Boolean {
        val currentuser = FirebaseAuth.getInstance().currentUser

        if (currentuser != null) {
            AppData.useruid = currentuser.uid
            Firebase.firestore.collection("UserCredentials")
                .get()
                .addOnSuccessListener {result ->
                    for (users in result) {
                        val documentUserUID = users.getString("useruid")
                        if (AppData.useruid == documentUserUID) {
                            AppData.documentid =  users.id
                            AppData.firstname = users.getString("Firstname")!!
                            AppData.lastname = users.getString("Lastname")!!
                            AppData.username = users.getString("Username")!!
                            AppData.email = users.getString("email")!!
                            AppData.fingerprintsignin = users.getBoolean("Fingerprintsignin")!!
                            val biometric = AppData.fingerprintsignin
                            Log.d("BIO", biometric.toString())
                            AppData.email = AppData.email
                            val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            if (biometric != null) {
                                editor.putBoolean("fingerprint_sign_in", biometric )
                            }
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