package com.example.tictacparty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val playNowButton = findViewById<Button>(R.id.playNowButton)
        val matchHistoryButton = findViewById<Button>(R.id.matchHistoryButton)
        val challengeAFriendButton = findViewById<Button>(R.id.challengeAFriendButton)
        
    }
}