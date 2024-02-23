package com.example.tictacparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    lateinit var profileUsername : TextView
    lateinit var profilePicture : ImageView
    lateinit var whichRank : TextView
    lateinit var rankingScore : TextView
    lateinit var gamesPlayed : TextView
    lateinit var gamesWon : TextView
    lateinit var gamesLost : TextView
    lateinit var recentGames : TextView
    lateinit var recentGamesRecyclerView : RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profilePicture = findViewById<ImageView>(R.id.profilePicture)
        profileUsername = findViewById(R.id.profileUsername)
        whichRank = findViewById(R.id.whichRank)
        rankingScore = findViewById(R.id.rankingScoreText)
        gamesPlayed =findViewById(R.id.gamesPlayedText)
        gamesWon=findViewById(R.id.gamesWonText)
        gamesLost=findViewById(R.id.gamesLostText)
        recentGames = findViewById(R.id.recentGamesText)
        recentGamesRecyclerView = findViewById(R.id.recentGamesRecyclerView)

        updateProfile()

        val logOutImage = findViewById<ImageView>(R.id.logoutImage)
        logOutImage.setOnClickListener {
            logout()
        }
        bottomNavListener()


    }
    fun logout(){
        auth.signOut()
        GlobalVariables.loggedInUser = ""
        GlobalVariables.loggedIn = false
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    fun updateProfile(){

        //Which rank is missing eg. "#12" We need an function for that
        //And also the text for how many Games played, we need a attribute for that in the player class

        if(GlobalVariables.player?.avatarImage!=null) {
            profilePicture.setImageResource(GlobalVariables.player!!.avatarImage)
        }
        if(GlobalVariables.player!=null) {
            profileUsername.text = GlobalVariables.player!!.username.capitalize()
            rankingScore.text = "Current Ranking Score: ${GlobalVariables.player!!.mmrScore}"

            gamesWon.text = "Games Won: ${GlobalVariables.player!!.wins}"
            gamesLost.text = "Games Lost: ${GlobalVariables.player!!.lost}"
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