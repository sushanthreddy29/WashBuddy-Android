package com.unh.washbuddy_android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.unh.washbuddy_android.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity2 : AppCompatActivity() {


    private lateinit var btnsignup: Button
    private lateinit var btnsignin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        btnsignup = findViewById(R.id.button)
        btnsignin = findViewById(R.id.button2)

        btnsignup.setOnClickListener{
            val intent = Intent(this, signup::class.java)
            startActivity(intent)
        }

        btnsignin.setOnClickListener{
            val intent = Intent(this,signin::class.java)
            startActivity(intent)
        }
    }
}