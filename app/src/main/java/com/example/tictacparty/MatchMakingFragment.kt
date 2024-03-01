package com.example.tictacparty

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import androidx.appcompat.app.AlertDialog
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.Timer
import java.util.TimerTask

class MatchMakingFragment() : Fragment() {

    var animationSpinning = AnimationDrawable()


    lateinit var spinningWheel: ImageView
    lateinit var loggedInPlayer: ImageView
    lateinit var loggedInUsername: TextView


    val player = GlobalVariables.player
    val db = Firebase.firestore
    val playersRef = db.collection("players")
    var opponentFound = false
    var opponentsUserName: String = ""


    override fun onResume() {
        if (!GlobalVariables.loggedIn) {
            val intent = Intent(requireActivity(), StartActivity::class.java)
            startActivity(intent)
        }
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_matchmaking, container, false)


        loggedInPlayer = view.findViewById<ImageView>(R.id.loggedinPlayer)

        spinningWheel = view.findViewById<ImageView>(R.id.spinningWheel)
        loggedInUsername = view.findViewById<TextView>(R.id.loggedInUsername)


        updateMatchMakingFragment()

        //temp Test code
        if (isAdded()) {
            // Fragmentet är fäst vid sin aktivitet
            Log.d("FragmentStatus", "Fragmentet är fäst vid sin aktivitet")
        } else {
            // Fragmentet är inte fäst vid sin aktivitet
            Log.d("FragmentStatus", "Fragmentet är inte fäst vid sin aktivitet")
        }

        //more temp test code
        val mainFragment = activity?.supportFragmentManager?.findFragmentByTag("mainFragment")
        if (mainFragment != null) {
            Log.d("!!!", "MainActivityFragment finns i backstacken")
        } else {
            Log.d("!!!", "MainActivityFragment finns inte i backstacken")
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        player?.searchingOpponent = true
        player?.searchingOpponentStartTime = System.currentTimeMillis()
        if (player != null) {
            FirestoreHelper.updatePlayerInFirestore(player)
        }

        opponentSearchTimer()


    }

    fun handleBackPressed() {
        // This is to handle what happens when the back button is pressed while findOpponent() is running
        resetSearchingOpponent()
    }

    //TODO när man trycker cancel i dialogrutan kommer man inte tillbaka till MainActivity (beror på hur fragmentet är uppbyggt...)

    fun opponentSearchTimer() {
        Log.d("!!!", "Nu körs opponentSearchTimer()") //debug print, remove before release
        val timer = Timer()
        var seconds = 0
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Log.d("!!!", "Nu körs findOpponent() från opponentSearchTimer()")
                findOpponent { opponent ->
                    if (opponent.isEmpty()) {
                        //ev text "Searching for opponent..."
                    } else {
                        opponentsUserName = opponent
                        opponentFound = true
                        Log.d("!!!", "Matched with opponent: $opponentsUserName")
                        //(maybe necessary to check that activity is not null before requireActivity)
                        val intent = Intent(requireActivity(), GameActivity::class.java)
                        intent.putExtra("opponentsUsername", opponentsUserName)
                        startActivity(intent)
                        timer.cancel()
                    }
                }
                seconds++
                if (seconds > 59) {
                    if (opponentFound == false) {
                        showTimeoutDialog()
                    }
                    timer.cancel()
                }
            }
        }, 0, 1000)
    }

    fun findOpponent(callback: (String) -> Unit) {
        var lowestTimeMillis: Long = Long.MAX_VALUE // Initial value set to maximum possible value
        var opponentsUserName: String = ""
        var opponentFound = false // Flag to track if opponent is found
        playersRef.whereEqualTo("searchingOpponent", true).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var currentDocumentsStartTime: Any? =
                        document.get("searchingOpponentStartTime")
                    var currentDocumentsStartTimeAsLong: Long? =
                        currentDocumentsStartTime as? Long
                    // checks that startTime is not the default value 0
                    if (currentDocumentsStartTimeAsLong != null && currentDocumentsStartTimeAsLong != 0.toLong()) {
                        // Check if this player is not oneself
                        if (document.get("username").toString() != player?.username) {
                            // saves the username of the player object with the lowest searchingOpponentStartTime
                            if (currentDocumentsStartTimeAsLong < lowestTimeMillis) {
                                lowestTimeMillis = currentDocumentsStartTimeAsLong
                                opponentsUserName = document.get("username").toString()
                                // Reset searchingOpponent only when a match is found
                                resetSearchingOpponent()
                                opponentFound = true // Set flag to true when opponent is found
                                // still runs through the for-loop to check if there is a better match
                            }
                        }
                    }
                }
                if (opponentFound) { // Only invoke callback if opponent is found
                    callback(opponentsUserName)
                }
            }
    }


    fun showTimeoutDialog() {
        activity?.runOnUiThread {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Timeout")
            builder.setMessage("No opponent was found.")
            builder.setPositiveButton("Try again") { _, _ ->
                opponentSearchTimer()
            }
            builder.setNegativeButton("Cancel") { _, _ ->
                resetSearchingOpponent()
                //activity?.supportFragmentManager?.popBackStack("mainFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)

            }


            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    fun updateMatchMakingFragment() {


        spinningWheel.setBackgroundResource(R.drawable.animation_spinningwheel)
        val animationSpinning = spinningWheel.background as? AnimationDrawable
        animationSpinning?.start()


        if (GlobalVariables.player?.avatarImage != null) {
            loggedInPlayer.setImageResource(GlobalVariables.player!!.avatarImage)
            Log.d("!!!", "inMainActivity: ${GlobalVariables.player!!.avatarImage}")
        }
        //capitalize() - Skriver ut användarnamnet så att första bokstaven blir stor och resten blir små.
        //loggedInUsername.text = GlobalVariables.player?.username?.capitalize()

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
            FirestoreHelper.updatePlayerInFirestore(player)
        }
    }

    override fun onDetach() {
        super.onDetach()
        resetSearchingOpponent()
    }


}



