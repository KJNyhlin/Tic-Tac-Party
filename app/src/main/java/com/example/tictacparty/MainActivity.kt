package com.example.tictacparty

import Function.getPlayerObject
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object GlobalVariables {
    var loggedInUser: String? = null
    var loggedIn: Boolean = false
    var player: Player? = null
    var sayHello: Boolean = true
}

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {

        db = FirebaseFirestore.getInstance()
        auth = Firebase.auth

        if (auth.currentUser == null) {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        GlobalVariables.loggedIn = true
        GlobalVariables.loggedInUser = auth.currentUser?.email
        GlobalScope.launch {
            val player = getPlayerObject(auth.currentUser?.uid)
            player?.let {
                GlobalVariables.player = it
                toastWelcome()
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = Firebase.firestore

        bottomNavListener()

        android.os.Handler().postDelayed({
            addMainFragment()

        }, 500)

        val mainFragment = supportFragmentManager.findFragmentByTag("mainFragment")

        if (mainFragment != null) {
            Log.d("MainActivity", "MainActivityFragment finns i backstacken")
        } else {
            Log.d("MainActivity", "MainActivityFragment finns inte i backstacken")
        }
    }

    fun bottomNavListener() {
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavView)

        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_play_game -> {
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

    fun addMainFragment() {
        val mainFragment = MainActivityFragment()
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        transaction.replace(R.id.fragmentContainer, mainFragment, "mainFragment")
        transaction.addToBackStack("mainFragment")

        transaction.commit()
    }


    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (fragment is MatchMakingFragment) {
            fragment.handleBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    fun toastWelcome() {
        if (GlobalVariables.sayHello) {
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Welcome ${GlobalVariables.player?.username?.capitalize()}",
                    Toast.LENGTH_SHORT
                ).show()
                GlobalVariables.sayHello = false
            }
        }
    }
}