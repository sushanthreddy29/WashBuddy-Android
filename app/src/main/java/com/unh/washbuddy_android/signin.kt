package com.unh.washbuddy_android

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unh.washbuddy_android.databinding.ActivitySigninBinding
import com.unh.washbuddy_android.databinding.ActivitySignupBinding

// Some parts of this code were generated with the assistance of ChatGPT by OpenAI.
// This includes implementations related to [briefly describe the feature or functionality, e.g., "RecyclerView item click handling" or "Firebase data retrieval"].

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

        binding.emailLayout.setEndIconOnClickListener {
            if(isFingerprintSignInEnabled()){
                showFingerprintPrompt()
            }
            else{
                Toast.makeText(this, "Fingerprint signin is not enabled", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signin.setOnClickListener{
            val email = binding.email.text.toString()
            val userpassword = binding.password.text.toString()
            validateAppLogin(email, userpassword)
        }

        setupFingerprintAuthentication()

    }

    private fun isFingerprintSignInEnabled(): Boolean {
        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        Log.d("BIO", sharedPreferences.getBoolean("fingerprint_sign_in", false).toString())
        return sharedPreferences.getBoolean("fingerprint_sign_in", false) // Default is false

    }

    private fun setupFingerprintAuthentication() {
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Biometric hardware is available and ready
                //showFingerprintPrompt()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(this, "No biometric hardware available", Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(this, "Biometric hardware unavailable", Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(this, "No biometric data enrolled. Please enroll fingerprints in settings.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showFingerprintPrompt() {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                val savedEmail = sharedPreferences.getString("email", null)
                val savedPassword = sharedPreferences.getString("password", null)
                if (savedEmail != null && savedPassword != null) {
                    validateAppLogin(savedEmail, savedPassword)
                } // Call login logic after successful authentication
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@signin, "Fingerprint Authentication Failed. Try again.", Toast.LENGTH_SHORT).show()
            }

        })

        //https://developer.android.com/identity/sign-in/biometric-auth
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate using your fingerprint")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    //https://developer.android.com/training/data-storage/shared-preferences
    private fun saveUserCredentials(email: String, password: String) {
        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    private fun validateAppLogin(email: String, userpassword: String) {

        val redColor = ContextCompat.getColor(this, R.color.warning_color)

        // Checking wheather email and username is empty or not before clicking button
        if (email.isEmpty() || userpassword.isEmpty()) {

            if(email.isEmpty()){
                binding.emailLayout.helperText = "Enter a valid email address"
                binding.emailLayout.setHelperTextColor(ColorStateList.valueOf(redColor))
            }
            if(userpassword.isEmpty()){
                binding.passwordLayout.helperText = "Enter your password"
                binding.passwordLayout.setHelperTextColor(ColorStateList.valueOf(redColor))
            }
            return
        }

        // Checking email is valid or not
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.helperText = "Enter a valid email address"
            binding.emailLayout.setHelperTextColor(ColorStateList.valueOf(redColor))
            return
        }

        //https://firebase.google.com/docs/auth/android/start
        //https://firebase.google.com/docs/firestore/query-data/get-data
        firebaseAuth.signInWithEmailAndPassword(email, userpassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Check if the email is the admin email

                saveUserCredentials(email, userpassword)

                if (email == "Admin@gmail.com" || email == "admin@gmail.com") {
                    val intent = Intent(this, AdminDashboard::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                } else {
                    val currentuser = firebaseAuth.currentUser
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
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}