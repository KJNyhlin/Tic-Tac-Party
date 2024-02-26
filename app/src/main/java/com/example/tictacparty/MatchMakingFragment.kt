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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.scheduleAtFixedRate

class MatchMakingFragment() : Fragment() {

    var animationSpinning = AnimationDrawable()


    lateinit var spinningWheel: ImageView
    lateinit var loggedInPlayer: ImageView
    lateinit var searchingUsername: TextView


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
        //loggedInUsername = view.findViewById<TextView>(R.id.searchingUsername)


        //updateMatchMakingFragment()


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        player?.searchingOpponent = true
        player?.searchingOpponentStartTime = System.currentTimeMillis()
        if (player != null) {
            updatePlayerInFirestore(player)
        }

        opponentSearchTimer()


    }

    //TODO när man trycker cancel i dialogrutan kommer man inte tillbaka till MainActivity (beror på hur fragmentet är uppbyggt...)

    //new version:
    fun opponentSearchTimer() {
        //debug print, remove before release
        Log.d("!!!", "Nu körs opponentSearchTimer()")
        val timer = Timer()
        var seconds = 0
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Log.d("!!!", "Nu körs findOpponent()")
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
        var lowestTimeMillis: Long = System.currentTimeMillis()
        var opponentsUserName: String = ""
        playersRef.whereEqualTo("searchingOpponent", true).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var currentDocumentsStartTime: Any? =
                        document.get("searchingOpponentStartTime")
                    var currentDocumentsStartTimeAsLong: Long? =
                        currentDocumentsStartTime as? Long
                    // checks that startTime is not the default value 0
                    if (currentDocumentsStartTimeAsLong != null && currentDocumentsStartTimeAsLong != 0.toLong()) {
                        // saves the username of the player object with the lowest timemillis
                        if (currentDocumentsStartTimeAsLong < lowestTimeMillis) {
                            lowestTimeMillis = currentDocumentsStartTimeAsLong
                            // checks that this player is not oneself
                            if (document.get("username").toString() != player?.username) {
                                opponentsUserName = document.get("username").toString()
                            }
                        }
                    }
                }
                callback(opponentsUserName)
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
                parentFragmentManager.popBackStack()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        fun updateMatchMakingFragment() {
        }


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
}

fun updatePlayerInFirestore(player: Player) {
    val db = Firebase.firestore
    val searchingOpponent : Boolean = player.searchingOpponent
    val searchingOpponentStartTime : Long = player.searchingOpponentStartTime
    val username = GlobalVariables.player?.username
    val playerRef = username.let {
        if (it != null) {
            db.collection("players").document(it)
        }
    }
    Log.d("!!!", "Searching for username $playerRef in the database")
    Log.d("!!!", "Användarnamn från GlobalVariables: $username")
    Log.d("!!!", "searchingOpponent is now $searchingOpponent")
    Log.d("!!!", "searchingOpponentStartTime is now $searchingOpponentStartTime")

    if (playerRef != null) {
        playerRef.update("searchingOpponent", searchingOpponent)
            .addOnSuccessListener {
                playerRef.update("searchingOpponentStartTime", searchingOpponentStartTime)
                    .addOnSuccessListener {
                        Log.d("!!!", "Both fields successfully updated!")
                    }
                    .addOnFailureListener { e ->
                        Log.w("!!!", "Error updating searchingOpponentStartTime", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("!!!", "Error updating searchingOpponent", e)
            }
    }
}


