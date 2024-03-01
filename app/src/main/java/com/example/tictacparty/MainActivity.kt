package com.example.tictacparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.tictacparty.GlobalVariables.player
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.logging.Handler

object GlobalVariables {
    var loggedInUser: String? = null
    var loggedIn: Boolean = false
    var player: Player? = null         //player one = inloggade spelaren
    var opponentPlayer: Player? = null //player two = personen man matchas med
}

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {

        db = FirebaseFirestore.getInstance()
        auth = Firebase.auth

        if (auth.currentUser != null) {
            GlobalVariables.loggedIn = true
            GlobalVariables.loggedInUser = auth.currentUser?.email
            getPlayerObject(auth.currentUser?.uid)
        }

        if (!GlobalVariables.loggedIn) {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = Firebase.firestore

        bottomNavListener()


        android.os.Handler().postDelayed({
            addMainFragment()

        }, 500)
        //temp test code
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

        transaction.replace(R.id.fragmentContainer,mainFragment, "mainFragment")
        transaction.addToBackStack("mainFragment")

        transaction.commit()
    }

    fun getPlayerObject(userId: String?) {
        if (userId != null) {
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
                    Log.d("!!!", "Authentication succeeded.")
                    // Proceed to next activity or handle authentication success

                }
                .addOnFailureListener { exception ->
                    Log.w("!!!", "Error getting documents: ", exception)
                }
        }
    }
    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (fragment is MatchMakingFragment) {
            fragment.handleBackPressed()
        } else {
            super.onBackPressed()
        }
    }
    object FirestoreHelper {
        private val db = FirebaseFirestore.getInstance()
        private val playersCollection = db.collection("players")

        fun updatePlayerInFirestore(player: Player) {
            val playerRef = playersCollection.document(player.documentId)
            val username = player.username

            Log.d("!!!", "Nu körs updatePlayerInFirestore")
            playerRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val existingPlayer = documentSnapshot.toObject(Player::class.java)
                    existingPlayer?.let {
                        it.searchingOpponent = player.searchingOpponent
                        it.searchingOpponentStartTime = player.searchingOpponentStartTime

                        playerRef.set(it)
                            .addOnSuccessListener {
                                Log.d("!!!","Dokument för $username uppdaterades framgångsrikt.")
                            }
                            .addOnFailureListener { exception ->
                                Log.d("!!!","Fel vid uppdatering av dokument för $username: $exception")
                            }
                    }
                } else {
                    Log.d("!!!","Dokumentet för $username finns inte i databasen.")
                }
            }.addOnFailureListener { exception ->
                Log.d("!!!","Fel vid hämtning av dokument för $username: $exception")
            }
        }
    }

    fun resetSearchingOpponent() {
        player?.searchingOpponent = false
        player?.searchingOpponentStartTime = 0
        Log.d("!!!", "Nu anropas resetSearchingOpponent(). player.searchingOpponent är satt till ${player?.searchingOpponent}.")
        if (player != null) {
            FirestoreHelper.updatePlayerInFirestore(player!!)
        }
    }

    override fun onPause() {
        super.onPause()
        resetSearchingOpponent()
    }

}