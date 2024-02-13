package com.example.tictacparty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = Firebase.firestore
        val playNowButton = findViewById<Button>(R.id.playNowButton)
        val matchHistoryButton = findViewById<Button>(R.id.matchHistoryButton)
        val challengeAFriendButton = findViewById<Button>(R.id.challengeAFriendButton)
        
    }
}