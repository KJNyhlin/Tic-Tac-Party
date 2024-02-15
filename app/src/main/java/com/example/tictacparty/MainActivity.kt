package com.example.tictacparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
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
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavView)

//        playNowButton.setOnClickListener {
//            //temporary solution, this button should lead to matchmaking
//            val intent = Intent(this, GameActivity::class.java)
//            startActivity(intent)
//        }


    }
    fun addMatchMakingFragment(view : View){
        val matchFragment = MatchMakingFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragmentContainer,matchFragment, "matchFragment")
        transaction.commit()
    }
    
    fun removeMatchMakingFragment(view : View){
        val matchFragment = supportFragmentManager.findFragmentByTag("matchFragment")

        if(matchFragment !=null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.remove(matchFragment)
            transaction.commit()
        }
        else{
            Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show()
        }
    }
    fun addMainFragment(view : View ){
        val mainFragment = MainFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragmentContainer,mainFragment, "mainFragment")
        transaction.commit()
    }
    
            bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_play_game -> {
                    // Koden som körs när "Play game"-knappen klickas på
                    //temporary solution for testing, should lead to: ???
                    val intent = Intent(this, GameActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_leaderboard -> {
                    // Koden som körs när "leaderboard"-knappen klickas på
                    true
                }
                R.id.navigation_profile -> {
                    // Koden som körs när "profile"-knappen klickas på
                    true
                }
                else -> false
            }
        }
}