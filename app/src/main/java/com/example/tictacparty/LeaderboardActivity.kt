package com.example.tictacparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LeaderboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        lifecycleScope.launch {
            val sortedHighscore = getHighscore()

            //Printing to log for debuging
            for(item in sortedHighscore){
                Log.d("!!!", "Name: ${item.first}, Score: ${item.second}")
            }

        }
        bottomNavListener()
    }

    suspend fun getHighscore() : List<Pair<String, Int>> {
        return suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val playersCollection = db.collection("players")
            playersCollection
                .get()
                .addOnSuccessListener { documents ->
                    val highScore = mutableListOf<Pair<String, Int>>()
                    for (document in documents) {
                        // Convert the document data to a Player object
                        val userName = document.getString("username") ?: ""
                        val mmrScore = document.getLong("mmrScore")?.toInt() ?: 0
                        val userPair = Pair(userName, mmrScore)
                        highScore.add(userPair)
                    }
                    val sortedList = highScore.sortedByDescending { it.second }
                    continuation.resume(sortedList)
                }
                .addOnFailureListener { exception ->
                    Log.w("!!!", "Error getting documents: ", exception)
                    continuation.resumeWith(Result.failure(exception))
                }
        }
    }

    fun bottomNavListener(){
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavView)

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