package com.example.tictacparty

import Function.getHighscore
import Function.getPlayerObject
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tictacparty.GlobalVariables.player
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var logOutImage: ImageView
    lateinit var profileUsername: TextView
    lateinit var profilePicture: ImageView
    lateinit var whichRank: TextView
    lateinit var rankingScore: TextView
    lateinit var gamesPlayed: TextView
    lateinit var gamesWon: TextView
    lateinit var gamesLost: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profilePicture = findViewById<ImageView>(R.id.profilePicture)
        profileUsername = findViewById(R.id.profileUsername)
        whichRank = findViewById(R.id.whichRank)
        rankingScore = findViewById(R.id.rankingScoreText)
        gamesPlayed = findViewById(R.id.gamesPlayedText)
        gamesWon = findViewById(R.id.gamesWonText)
        gamesLost = findViewById(R.id.gamesLostText)
        logOutImage = findViewById<ImageView>(R.id.logoutImage)

        updateProfile()
        addLogoutAlertDialog()
        bottomNavListener()
    }

    fun logout() {
        auth.signOut()
        GlobalVariables.loggedInUser = null
        GlobalVariables.loggedIn = false
        player = null
        Log.d("!!!", "Log out: ${player?.username}")

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun addLogoutAlertDialog() {
        val addLogoutDialog =
            AlertDialog.Builder(this).setTitle("Log out").setMessage("Do you want to log out?")
                .setIcon(R.drawable.pinkgameboard).setPositiveButton("Yes") { _, _ ->
                    Toast.makeText(this, "You logged out!", Toast.LENGTH_SHORT).show()
                    logout()
                    intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                .setNegativeButton("No") { _, _ ->
                    Toast.makeText(this, "You didn't log out", Toast.LENGTH_SHORT).show()

                }.create()

        logOutImage.setOnClickListener {
            addLogoutDialog.show()
        }
    }

    fun updateProfile() {
        if (player?.avatarImage != null) {
            profilePicture.setImageResource(player!!.avatarImage)
        }

        if (player != null) {
            lifecycleScope.launch {
                getPlayerObject(player?.userId)
            }

            profileUsername.text = player!!.username.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
            rankingScore.text = "Current Ranking Score: ${player!!.mmrScore}"
            gamesPlayed.text = "Games Played: ${player!!.gamesPlayed}"
            gamesWon.text = "Games Won: ${player!!.wins}"
            gamesLost.text = "Games Lost: ${player!!.lost}"

            lifecycleScope.launch {
                val sortedScores = getHighscore()
                var myIndex =
                    sortedScores.indexOfFirst { it.first == player?.username }
                myIndex++
                whichRank.text = myIndex.toString()
            }
        }
    }

    fun bottomNavListener() {
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavView)

        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_play_game -> {
                    // Koden som körs när "Play game"-knappen klickas på
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