package com.example.tictacparty

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

class GameActivity : AppCompatActivity() {

    lateinit var titleTextView: TextView

    lateinit var player1_avatar : ImageView
    lateinit var player2_avatar : ImageView
    lateinit var gamebutton1: ImageButton
    lateinit var gamebutton2: ImageButton
    lateinit var gamebutton3: ImageButton
    lateinit var gamebutton4: ImageButton
    lateinit var gamebutton5: ImageButton
    lateinit var gamebutton6: ImageButton
    lateinit var gamebutton7: ImageButton
    lateinit var gamebutton8: ImageButton
    lateinit var gamebutton9: ImageButton

    lateinit var username1: TextView
    lateinit var username2: TextView
    lateinit var gameInfo: TextView
    lateinit var exitImage: ImageView
    lateinit var helpImage: ImageView

    var buttons = mutableListOf<ImageButton>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val playerOne =
            Player("email@example.com", "Spelare 1", "id1", 0, 0, 0, 0, 0, false, 0, "kryss")
        val playerTwo =
            Player("email2@example.com", "Spelare 2", "id2", 0, 0, 0, 0, 0, false, 0, "runda")


        val game = Game(1, playerOne, playerTwo, 1, "ongoing")

        iniatilizeViews()
        addingClickListeners()

        makeMove(game)


    }

    fun iniatilizeViews() {
        titleTextView = findViewById(R.id.titleTextView)
        player1_avatar = findViewById(R.id.player1)
        player2_avatar = findViewById(R.id.player2)
        gamebutton1 = findViewById(R.id.gameButton1)
        gamebutton2 = findViewById(R.id.gameButton2)
        gamebutton3 = findViewById(R.id.gameButton3)
        gamebutton4 = findViewById(R.id.gameButton4)
        gamebutton5 = findViewById(R.id.gameButton5)
        gamebutton6 = findViewById(R.id.gameButton6)
        gamebutton7 = findViewById(R.id.gameButton7)
        gamebutton8 = findViewById(R.id.gameButton8)
        gamebutton9 = findViewById(R.id.gameButton9)
        username1 = findViewById(R.id.username1)
        username2 = findViewById(R.id.username2)
        gameInfo = findViewById(R.id.gameInfo)
        exitImage = findViewById(R.id.exitImage)
        helpImage = findViewById(R.id.helpImage)
    }

    fun addingClickListeners() {

        exitImage.setOnClickListener {

        }
        helpImage.setOnClickListener {

        }
    }

    fun determineWhoseTurnItIs(game: Game): Player {
        return if (game.nextMove == 1) game.playerOne else game.playerTwo
    }

    private fun makeMove(game: Game) {
        buttons.add(gamebutton1)
        buttons.add(gamebutton2)
        buttons.add(gamebutton3)
        buttons.add(gamebutton4)
        buttons.add(gamebutton5)
        buttons.add(gamebutton6)
        buttons.add(gamebutton7)
        buttons.add(gamebutton8)
        buttons.add(gamebutton9)

        for (button in buttons) {
            button.setOnClickListener {
                if (button.tag == null) {
                    val currentPlayer = determineWhoseTurnItIs(game)
                    if (currentPlayer.symbol == "kryss") {
                        button.setBackgroundResource(R.drawable.profile_icon)
                    } else {
                        button.setBackgroundResource(R.drawable.vector__1_)
                    }
                    button.tag = currentPlayer.symbol
                    game.nextMove = if (game.nextMove == 1) 2 else 1
                    updateDatabase(currentPlayer, game)
                } else {
                    Toast.makeText(
                        this@GameActivity,
                        "Knappen är redan tagen! 😅",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun updateDatabase(currentPlayer: Player, game: Game) {

        val db = Firebase.firestore


        val gameRef = db.collection("games").document(game.id.toString())


        val updates = hashMapOf<String, Any>(
            "id" to game.id,
            "playerOne" to game.playerOne.toHashMap(),
            "playerTwo" to game.playerTwo.toHashMap(),
            "nextMove" to game.nextMove,
            "status" to game.status
        )

        Log.d("UpdateDatabase", "updateDatabase startar")

        gameRef.set(updates)
            .addOnSuccessListener {
                Log.d("UpdateDatabase", "Game successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("UpdateDatabase", "Error updating game", e)
            }
    }

    fun fetchPlayer(userId: String, callback: (Player) -> Unit) {
        val db = Firebase.firestore
        val docRef = db.collection("players").document(userId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val player = document.toObject(Player::class.java)
                    if (player != null) {
                        callback(player)
                    }
                } else {
                    Log.d("FetchPlayer", "No such document")
                }
            }
    }

}

fun updateNewGameViews() {
    if (GlobalVariables.player?.avatarImage != null) {
        loggedInPlayerImage.setImageResource(GlobalVariables.player!!.avatarImage)
        Log.d("!!!", "inMainActivity: ${GlobalVariables.player!!.avatarImage}")
    }
    TOOD : We need to decide where the GameIcon for the players (X or O)  shall be stored.
    if (GlobalVariables.player != null) {
        loggedInUsername.text = "X - ${GlobalVariables.player!!.username}"
    }

}

