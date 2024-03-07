package com.example.tictacparty

import Function.removeMatchmakingRoom
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class MatchMakingFragment : Fragment() {

    lateinit var spinningWheel: ImageView
    lateinit var loggedInPlayer: ImageView
    lateinit var loggedInUsername: TextView

    val player = GlobalVariables.player
    val db = Firebase.firestore

    var roomId: String = ""

    var player1Id: String = ""
    var player2Id: String = ""
    lateinit var room: MatchmakingRoom

    override fun onResume() {
        if (!GlobalVariables.loggedIn) {
            val intent = Intent(requireActivity(), StartActivity::class.java)
            startActivity(intent)
        }
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_matchmaking, container, false)

        loggedInPlayer = view.findViewById<ImageView>(R.id.loggedinPlayer)
        spinningWheel = view.findViewById<ImageView>(R.id.spinningWheel)
        loggedInUsername = view.findViewById<TextView>(R.id.loggedInUsername)

        updateMatchMakingFragment()

        android.os.Handler().postDelayed({
            createOrJoinRoom()
        }, 1000)

        return view
    }

    fun createOrJoinRoom() {
        val db = FirebaseFirestore.getInstance()
        val roomsRef = db.collection("matchmaking_rooms")

        // Check for available rooms with only one player
        roomsRef.whereEqualTo("status", "waiting").get().addOnSuccessListener { documents ->
                for (document in documents) {
                    room = document.toObject(MatchmakingRoom::class.java)
                    if (room.player2Id.isEmpty()) {
                        // Found an available room with only one player, join this room
                        joinRoom(document.id)
                        return@addOnSuccessListener
                    }
                }
                // No available rooms found, create a new room
                createRoom()
            }.addOnFailureListener { e ->
                Log.w("!!!", "Error creation or join of room: ", e)
            }
    }

    fun createRoom() {
        val db = FirebaseFirestore.getInstance()
        val roomsRef = db.collection("matchmaking_rooms")

        if (player != null) {
            room = MatchmakingRoom("", player.documentId, "", "waiting")
        }

        roomsRef.add(room).addOnSuccessListener { documentReference ->
                // Room created successfully
                val roomId = documentReference.id
                monitorRoom(roomId)
                startTimer(roomId)
            }.addOnFailureListener { e ->
                Log.w("!!!", "Error create room: ", e)
            }
    }

    fun joinRoom(roomId: String) {
        val db = FirebaseFirestore.getInstance()
        val roomRef = db.collection("matchmaking_rooms").document(roomId)

        // Update the existing room document to include Player 2's ID
        roomRef.update("player2Id", player!!.documentId, "status", "matched").addOnSuccessListener {
                // Room updated successfully, transition to game activity
                transitionToGameActivity(room)
            }.addOnFailureListener { e ->
                Log.w("!!!", "Error join room: ", e)
            }
    }

    fun monitorRoom(roomId: String) {
        this.roomId = roomId
        val db = FirebaseFirestore.getInstance()
        val roomRef = db.collection("matchmaking_rooms").document(roomId)

        val listenerRegistration = roomRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("!!!", "Error when register listener: ", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val room = snapshot.toObject(MatchmakingRoom::class.java)
                if (room != null && room.player2Id.isNotEmpty()) {
                    // Båda spelarna har gått med i rummet, övergå till spelaktiviteten
                    Log.d("!!!", "Room: $player1Id $player2Id")
                    transitionToGameActivity(room)
                }
            }
        }

        // Ta bort lyssnaren när fragmentet förstörs
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                listenerRegistration.remove()
            }
        })
    }

    fun transitionToGameActivity(room: MatchmakingRoom) {
        if (room != null) {
            // Transition to GameActivity using Intent
            val intent = Intent(requireActivity(), GameActivity::class.java)
            intent.putExtra("roomId", room.roomId)
            intent.putExtra("player1Id", room.player1Id)
            intent.putExtra("player2Id", room.player2Id)
            Log.d("!!!", "Room id : ${room.roomId}roomId: $player1Id $player2Id")
            startActivity(intent)
            requireActivity().finish()
        } else {
            Log.e("Error", "Room is null")
        }
    }

    fun startTimer(roomId: String) {
        val timer = Timer()
        var seconds = 0
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                seconds++
                if (seconds > 59) {
                    showTimeoutDialog(roomId)
                    removeMatchmakingRoom(roomId)
                    timer.cancel()
                }
            }
        }, 0, 1000)
    }

    fun showTimeoutDialog(roomId: String) {
        activity?.runOnUiThread {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Timeout")
            builder.setMessage("No opponent was found.")

            builder.setPositiveButton("Try again") { _, _ ->
                createOrJoinRoom()
            }

            builder.setNegativeButton("Cancel") { _, _ ->
                removeMatchmakingRoom(roomId)
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    fun handleBackPressed() {
        // This is to handle what happens when the back button is pressed while findOpponent() is running
        //removeMatchmakingRoom(roomId)
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
        loggedInUsername.text = GlobalVariables.player?.username?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
    }

}