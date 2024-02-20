package com.example.tictacparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

object GlobalVariables {
    var loggedInUser: String? = null
    var loggedIn : Boolean = false
    var player : Player? = null
}
class MainActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth

    lateinit var temporaryLogOutButton : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth


        if(!GlobalVariables.loggedIn){
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = Firebase.firestore
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavView)

       // addMainFragment()
        val mainFragment = MainActivityFragment()
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer,mainFragment, "mainFragment")
        transaction.commit()

        temporaryLogOutButton = findViewById(R.id.temporaryLogOutButton)
        temporaryLogOutButton.setOnClickListener {
            GlobalVariables.loggedIn=false
        }

        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_play_game -> {
                    // Koden som körs när "Play game"-knappen klickas på
                    //temporary solution for testing, should lead to: ???
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_leaderboard -> {
                    // Koden som körs när "leaderboard"-knappen klickas på
                    val intent = Intent(this, LeaderboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    // Koden som körs när "profile"-knappen klickas på
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }


    }


}