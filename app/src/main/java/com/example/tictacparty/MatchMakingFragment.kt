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
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.scheduleAtFixedRate

class MatchMakingFragment() : Fragment() {

    var animationSpinning = AnimationDrawable()
    val player = GlobalVariables.player
    val db = Firebase.firestore
    val playersRef = db.collection("players")
    var opponentFound = false
    var opponentsUserName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_matchmaking, container, false)

        val imageView = view.findViewById<ImageView>(R.id.spinningWheel)
        imageView.setBackgroundResource(R.drawable.animation_spinningwheel)
        val animationSpinning = imageView.background as? AnimationDrawable
        animationSpinning?.start()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        player?.searchingOpponent = true
        player?.searchingOpponentStartTime = System.currentTimeMillis()

        opponentSearchTimer()
    }

    //TODO när man trycker cancel i dialogrutan kommer man inte tillbaka till MainActivity (beror på hur fragmentet är uppbyggt...)

    //new version:
    fun opponentSearchTimer() {
        //debug print, remove before release
        Log.d("opponentSearchTimer", "Nu körs opponentSearchTimer()")
        val timer = Timer()
        var seconds = 0
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                findOpponent { opponent ->
                    if (opponent.isEmpty()) {
                        //ev text "Searching for opponent..."
                    } else {
                        opponentsUserName = opponent
                        opponentFound = true
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
        }

        //this function is never used here, could probably be removed
        fun updatePlayerInFirestore(player: Player) {
            val db = Firebase.firestore
            val playerRef =
                GlobalVariables.player?.let { db.collection("players").document(it.username) }

            if (playerRef != null) {
                playerRef.update("searchingOpponent", GlobalVariables.player?.searchingOpponent)
                    .addOnSuccessListener { Log.d("!!!", "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w("!!!", "Error updating document", e) }
            }
        }
    }

