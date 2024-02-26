package com.example.tictacparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

object GlobalVariables {
    var loggedInUser: String? = null
    var loggedIn : Boolean = false
    var player : Player? = null
    var opponentPlayer : Player? = null
}
class MainActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    lateinit var db : FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {

        db = FirebaseFirestore.getInstance()
        auth = Firebase.auth

        if(auth.currentUser != null){
            GlobalVariables.loggedIn = true
            GlobalVariables.loggedInUser = auth.currentUser?.email
            getPlayerObject(auth.currentUser?.uid)
        }

        if(!GlobalVariables.loggedIn){
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = Firebase.firestore


        addMainFragment()
        bottomNavListener()


    }
    fun bottomNavListener(){
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavView)

        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_play_game -> {
                    //temporary solution for testing, should lead to: ???
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_leaderboard -> {
                    val intent = Intent(this, LeaderboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }
    fun addMainFragment(){
        val mainFragment = MainActivityFragment()
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer,mainFragment, "mainFragment")
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun getPlayerObject(userId : String?) {
        if(userId != null) {
            val playersCollection = db.collection("players")
            var player: Player? = null
            playersCollection
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Convert the document data to a Player object
                        player = document.toObject(Player::class.java)
                        // Use the player object as needed
                        Log.d("!!!", "Player: $player")
                        if (player != null) {
                            GlobalVariables.player = player
                        }
                    }
                    GlobalVariables.loggedInUser = auth.currentUser?.email
                    GlobalVariables.loggedIn = true
                    Log.d("!!!","Authentication succeeded.")
                    // Proceed to next activity or handle authentication success

                }
                .addOnFailureListener { exception ->
                    Log.w("!!!", "Error getting documents: ", exception)
                }
        }
    }
}