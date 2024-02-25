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
import kotlin.concurrent.scheduleAtFixedRate

class MatchMakingFragment() : Fragment(){

    var animationSpinning = AnimationDrawable()

    lateinit var spinningWheel : ImageView
    lateinit var searchingOpponent : ImageView
    lateinit var searchingUsername : TextView


    val player = GlobalVariables.player
    val db = Firebase.firestore
    val playersRef = db.collection("players")
    val timer = Timer()
    var opponentFound = false
    var opponentsUserName : String = ""


    override fun onResume() {
        if(!GlobalVariables.loggedIn){
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


        searchingOpponent = view.findViewById<ImageView>(R.id.searchingOpponent)
        spinningWheel = view.findViewById<ImageView>(R.id.spinningWheel)
        searchingUsername = view.findViewById<TextView>(R.id.searchingUsername)


        updateMatchMakingFragment()


        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        player?.searchingOpponent = true
        player?.searchingOpponentStartTime = System.currentTimeMillis()


//        var opponent : String = ""
//        // the following needs to be fixed, should be possible to abort if no match is found
//        while (opponent == "") {
//            opponent = findOpponent()
//            // add prompt here: "No opponent was found. Try again / go back"
//        }

        opponentSearchTimer()




    }

    //TODO just nu kommer inte dialogrutan upp efter en minuts sökning
    //TODO när man trycker cancel i dialogrutan kommer man inte tillbaka till MainActivity (beror på hur fragmentet är uppbyggt...)
    private fun opponentSearchTimer() {
        var seconds = 0
        timer.scheduleAtFixedRate(0, 1000) {
            activity?.runOnUiThread {
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
            }
            seconds++
            if (seconds > 59) {
                timer.cancel()
                if (opponentFound == false) {
                    showTimeoutDialog()
                }
            }
        }
    }


    private fun findOpponent(callback: (String) -> Unit) {
        var lowestTimeMillis : Long = System.currentTimeMillis()
        var opponentsUserName : String = ""
        playersRef.whereEqualTo("searchingOpponent", true).get().addOnSuccessListener { documents ->
            for (document in documents) {
                var currentDocumentsStartTime : Any? = document.get("searchingOpponentStartTime")
                var currentDocumentsStartTimeAsLong : Long? = currentDocumentsStartTime as? Long
                // checks that startTime is not the default value 0
                if (currentDocumentsStartTimeAsLong != null && currentDocumentsStartTimeAsLong != 0.toLong()) {
                    // saves the username of the player object with the lowest timemillis
                    if (currentDocumentsStartTimeAsLong < lowestTimeMillis) {
                        lowestTimeMillis = currentDocumentsStartTimeAsLong
                        opponentsUserName = document.get("username").toString()
                    }
                }
            }
            callback(opponentsUserName)
        }
    }

    fun showTimeoutDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Timeout")
        builder.setMessage("No opponent was found.")
        builder.setPositiveButton("Try again") { dialog, which ->
            opponentSearchTimer()
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            parentFragmentManager.popBackStack()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
    }

    fun updatePlayerInFirestore(player: Player) {
        val db = Firebase.firestore
        val playerRef = GlobalVariables.player?.let { db.collection("players").document(it.username) }

        if (playerRef != null) {
            playerRef.update("searchingOpponent", GlobalVariables.player?.searchingOpponent)
                .addOnSuccessListener { Log.d("!!!", "DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Log.w("!!!", "Error updating document", e) }
        }
    }
    fun updateMatchMakingFragment(){

        spinningWheel.setBackgroundResource(R.drawable.animation_spinningwheel)
        val animationSpinning = spinningWheel.background as? AnimationDrawable
        animationSpinning?.start()

        if(GlobalVariables.player?.avatarImage!=null) {
            searchingOpponent.setImageResource(GlobalVariables.player!!.avatarImage)
            Log.d("!!!", "inMainActivity: ${GlobalVariables.player!!.avatarImage}")
        }
        //capitalize() - Skriver ut användarnamnet så att första bokstaven blir stor och resten blir små.
        searchingUsername.text = GlobalVariables.player?.username?.capitalize()

    }

}
