package com.example.tictacparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        var createAccountButton = findViewById<Button>(R.id.accountBtn)
        var loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)

        }
        createAccountButton.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }


    }
}