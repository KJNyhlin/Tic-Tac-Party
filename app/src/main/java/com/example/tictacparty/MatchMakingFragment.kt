package com.example.tictacparty

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MatchMakingFragment() : Fragment(){

    var animationSpinning = AnimationDrawable()
    val player = GlobalVariables.player
    val db = Firebase.firestore
    val playersRef = db.collection("players")

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

        var opponent : String = ""
        // this needs to be fixed, should be possible to abort if no match is found
        while (opponent == "") {
            opponent = findOpponent()
            // add prompt here: "No opponent was found. Try again / go back"
        }


    }

    private fun findOpponent(): String {
        var lowestTimeMillis : Long = System.currentTimeMillis()
        var opponentsUserName : String = ""
        playersRef.whereEqualTo("searchingOpponent", true).get().addOnSuccessListener { documents ->
            for (document in documents) {
                var currentDocumentsStartTime : Any? = document.get("searchingOpponentStartTime")
                var currentDocumentsStartTimeAsLong : Long? = currentDocumentsStartTime as? Long
                if (currentDocumentsStartTimeAsLong != null && currentDocumentsStartTimeAsLong != 0.toLong()) {
                    if (currentDocumentsStartTimeAsLong < lowestTimeMillis) {
                        lowestTimeMillis = currentDocumentsStartTimeAsLong
                        opponentsUserName = document.get("username").toString()
                    }
                }
            }
        }
        return opponentsUserName
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

}
