package com.example.tictacparty

import Function.removeMatchmakingRoom
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore


class GameActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var titleTextView: TextView

    lateinit var player1_avatar: ImageView
    lateinit var player2_avatar: ImageView
    lateinit var gamebutton1: ImageButton
    lateinit var gamebutton2: ImageButton
    lateinit var gamebutton3: ImageButton
    lateinit var gamebutton4: ImageButton
    lateinit var gamebutton5: ImageButton
    lateinit var gamebutton6: ImageButton
    lateinit var gamebutton7: ImageButton
    lateinit var gamebutton8: ImageButton
    lateinit var gamebutton9: ImageButton
    lateinit var playAgainButton: FloatingActionButton
    lateinit var playerOne: Player
    lateinit var playerTwo: Player
    lateinit var currentPlayer: Player

    lateinit var username1: TextView
    lateinit var username2: TextView
    lateinit var gameInfo: TextView
    lateinit var exitImage: ImageView
    lateinit var helpImage: ImageView
    var roomId:String?=""
    lateinit var game: Game
    var buttons = mutableListOf<ImageButton>()
    val db = com.google.firebase.ktx.Firebase.firestore
    val playersCollection = db.collection("players")

    private fun setupGameSnapshotListener(roomId: String) {
        val db = Firebase.firestore
        val gameRef = db.collection("games").document(roomId)

        gameRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val game = snapshot.toObject(Game::class.java)
                if (game != null) {
                    updateUI(game) // Update the UI with the updated game state
                } else {
                    Log.d(TAG, "Current data: null")
                }
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }

    private fun uploadGameToFirestore(game: Game) {
        val db = Firebase.firestore

        if (!::playerOne.isInitialized || !::playerTwo.isInitialized) {
            Log.d("!!!", "Players not initialized yet")
            return
        }

        val documentRef = db.collection("games").document(game.documentId)

        documentRef.set(game)
            .addOnSuccessListener {
                Log.d("!!!", "Game added to Firestore with document ID: ${game.documentId}")
                setupGameSnapshotListener(game.documentId)
            }
            .addOnFailureListener { e ->
                Log.d("!!!", "Error adding game to Firestore: $e")
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        iniatilizeViews()

        roomId = intent.getStringExtra("roomId")
        if (roomId != null) {
            fetchRoomAndPlayers(roomId!!)
        } else {
            val intent = Intent(this, MatchMakingFragment::class.java)
            startActivity(intent)
            finish() // Finish the current activity to prevent further execution
            return
        }
    }


    fun fetchRoomAndPlayers(roomId: String) {
        val db = FirebaseFirestore.getInstance()
        val roomRef = db.collection("matchmaking_rooms").document(roomId)

        roomRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val room = documentSnapshot.toObject(MatchmakingRoom::class.java)
                    if (room != null) {
                        val player1Id = room.player1Id
                        val player2Id = room.player2Id
                        if (player1Id != null && player2Id != null) {
                            fetchPlayers(player1Id, player2Id)
                        } else {
                            Log.d("!!!", "Player IDs are null in room document")
                        }
                    } else {
                        Log.d("!!!", "Room document is null")
                    }
                } else {
                    Log.d("!!!", "Room document does not exist")
                }
            }
            .addOnFailureListener { e ->
                Log.d("!!!", "Failed to fetch room document: $e")
            }
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
        playAgainButton = findViewById(R.id.playAgainButton)
        username1 = findViewById(R.id.username1)
        username2 = findViewById(R.id.username2)
        gameInfo = findViewById(R.id.gameInfo)
        exitImage = findViewById(R.id.exitImage)
        helpImage = findViewById(R.id.helpImage)



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
            button.setOnClickListener(this)
        }
    }

    fun addingClickListeners() {

        helpImage.setOnClickListener {

        }

    }

    fun switchPlayers() {
        currentPlayer = if (currentPlayer.email == playerOne.email) {
            playerTwo
        } else {
            playerOne
        }
    }

    override fun onClick(button: View?) {
        Log.d("!!!", "current player on click${currentPlayer.username}")
        game.apply {
           if (currentPlayer.email != game.playerOneId && currentPlayer.email != game.playerTwoId) {
                // If it's not the currentPlayer's turn, exit early and don't process the click
                return
            }


            //apply gör att man kan göra operationer direkt på ett objekt, i det här fallet game, så slipper man skriva game.filledPos
            Log.d("!!!", "on click : {$filledPos]")

            val clickedPos = (button?.tag as String).toInt() - 1
            if (filledPos[clickedPos].isEmpty()) {
                nextTurnPlayer = if (currentPlayer.email == playerOneId) playerTwoId else playerOneId
                Log.d("!!!","CurrentPlayer Email: ${currentPlayer.email}")
                Log.d("!!!","Player One ID ${playerOneId}")
                Log.d("!!!","Player Two ID ${playerTwoId}")

                filledPos[clickedPos] = currentPlayer.symbol
                checkForWinner()
                if (status == "ongoing") {
                    switchPlayers() // Move switchPlayers() inside this block
                }
                updateUI(game)
                updateFilledPosInDatabase(game.documentId, clickedPos, filledPos[clickedPos])
            } else {
                Toast.makeText(
                    this@GameActivity,
                    "This place is taken! 😅",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun updateUI(game: Game) {
        var userName : String
        if(game.nextTurnPlayer == playerOne.email){
            userName = playerOne.username
        }
        else if (game.nextTurnPlayer == playerTwo.email){
            userName = playerTwo.username
        }
        else{
            userName = game.nextTurnPlayer
        }
        gameInfo.text = "${currentPlayer.symbol} - ${userName.capitalize()}'s turn"
        //index = filledPos[index]
        //Index(1, 2,  3  4  5  6  7  8  9
        //     ("","","","","","","","",""))
        // Value är vad det finns för värde i filledPos[index] t.ex "X" eller "O"
        Log.d("!!!", "updateUI()")
        Log.d("!!!", "current player in ui${currentPlayer.username}")
        game.apply {
            for ((index, value) in filledPos.withIndex()) {
                val button = buttons[index]
                button.setImageResource(
                    when (value) {
                        "X" -> R.drawable.profile_icon
                        "O" -> R.drawable.vector__1_
                        else -> 0
                    }
                )
            }
            if (status == "ongoing") {
                playAgainButton.visibility = View.INVISIBLE
                //gameInfo.text =
                  //  "${currentPlayer.symbol} - ${currentPlayer.username.capitalize()}'s turn"
            }
        }
        if (game.status == "finished") {
            removeFinishedGames(game)
            playAgainButton.visibility = View.VISIBLE
            //startActivity(Intent(this,MatchMakingFragment::class.java))
            playAgainButton.setOnClickListener {

                //Temporary, should lead to matchmaking??
                val intent = Intent(this, MatchMakingFragment::class.java)
                startActivity(intent)

            }
        }
    }

    fun checkForWinner() {

        val winningPos = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6),

            )
        game.apply {
            for (i in winningPos) {
                if (
                    filledPos[i[0]] == filledPos[i[1]] &&
                    filledPos[i[1]] == filledPos[i[2]] &&
                    filledPos[i[0]].isNotEmpty()
                ) {
                    status = "finished"
                    gameInfo.text = "${currentPlayer.username.capitalize()} wins"
                } else if (filledPos.none { it.isEmpty() }) {
                    status = "finished"
                    gameInfo.text = "Draw"
                }
            }
        }
        updateUI(game)
    }

    private fun updateDatabase(game: Game) {

        val db = Firebase.firestore

        Log.d("!!!", "in updatebase${game.filledPos}")
        var documentRef = db.collection("games").document(game.documentId)
        val documentId = documentRef.id
        Log.d("!!!", "in update $documentId")

        val updates = hashMapOf<String, Any>(
            "playerOne" to game.playerOneId,
            "playerTwo" to game.playerTwoId,
            "status" to game.status,
            "filledPos" to game.filledPos,
            "nextTurnPlayer" to game.nextTurnPlayer
        )

        Log.d("UpdateDatabase", "updateDatabase startar")

        documentRef.set(updates)
            .addOnSuccessListener {
                Log.d("UpdateDatabase", "Game successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("UpdateDatabase", "Error updating game", e)
            }
    }
    fun removeFinishedGames(game : Game ){
        val db = FirebaseFirestore.getInstance()
        val gameRef = db.collection("games").document(game.documentId)
        gameRef.delete()
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }

    }

    fun fetchPlayer(playerId: String, onComplete: (Player?) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("players").document(playerId).get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val playerDocument = task.result
                    if (playerDocument != null && playerDocument.exists()) {
                        val player = playerDocument.toObject(Player::class.java)
                        onComplete(player)
                    } else {
                        onComplete(null) // Player document does not exist
                    }
                } else {
                    onComplete(null) // Error occurred while fetching player document
                }
            }
    }

    fun addExitDialog() {

        val addContactDialog = AlertDialog.Builder(this)
            .setTitle(" Exit game?")
            .setMessage("Do you want to exit the game? You will lose points by exiting")
            .setIcon(R.drawable.gameboard)
            .setPositiveButton("Yes") { _, _ ->
                Toast.makeText(this, "You exited!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton("No") { _, _ ->
                Toast.makeText(this, "You didn't exit.", Toast.LENGTH_SHORT).show()

            }.create()

        exitImage.setOnClickListener {
            addContactDialog.show()
        }
    }

    fun showGameViews() {

        if (playerOne != null && playerTwo != null) {
            player1_avatar.setImageResource(playerOne.avatarImage)
            username1.text = "${playerOne.username.capitalize()}"

            player2_avatar.setImageResource(playerTwo.avatarImage)
            username2.text = "${playerTwo.username.capitalize()}"
        }
    }

    fun fetchPlayers(player1Id: String, player2Id: String) {
        fetchPlayer(player1Id) { player1 ->
            if (player1 != null) {
                playerOne = player1
                playerOne.symbol = "X"
                fetchPlayer(player2Id) { player2 ->
                    if (player2 != null) {
                        playerTwo = player2
                        playerTwo.symbol = "O"
                        val tempRoomId = roomId
                        if(tempRoomId != null) {
                            removeMatchmakingRoom(tempRoomId)
                        }
                        initializeGame()
                    } else {
                        Log.d("!!!", "Player 2 does not exist")
                    }
                }
            } else {
                Log.d("!!!", "Player 1 does not exist")
            }
        }
    }

    fun initializeGame() {
        game = Game(
            roomId!!,
            playerOne.email,
            playerTwo.email,
            "ongoing",
            mutableListOf("", "", "", "", "", "", "", "", ""),
            playerOne.username
        )
        currentPlayer = playerOne
        showGameViews()
        uploadGameToFirestore(game)

        setupGameSnapshotListener(game.documentId)
        updateUI(game)
        updateDatabase(game)

    }

    fun updateFilledPosInDatabase(documentId: String, index: Int, newValue: String) {
        val db = Firebase.firestore
        val documentRef = db.collection("games").document(documentId)

        documentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val game = documentSnapshot.toObject(Game::class.java)
                if (game != null) {
                    val filledPos = game.filledPos ?: mutableListOf() // Provide a default value if filledPos is null
                    filledPos[index] = newValue

                    // Determine the next turn player
                    val nextTurnPlayer = if (game.nextTurnPlayer == game.playerOneId) game.playerTwoId else game.playerOneId ?: ""

                    val updates = hashMapOf<String, Any>(
                        "filledPos" to filledPos,
                        "nextTurnPlayer" to nextTurnPlayer
                    )

                    documentRef.update(updates)
                        .addOnSuccessListener {
                            Log.d("UpdateFilledPos", "FilledPos successfully updated!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("UpdateFilledPos", "Error updating filledPos", e)
                        }
                } else {
                    Log.d("UpdateFilledPos", "Failed to convert document snapshot to Game object")
                }
            } else {
                Log.d("UpdateFilledPos", "Document does not exist")
            }
        }.addOnFailureListener { e ->
            Log.d("UpdateFilledPos", "Failed to get document snapshot: $e")
        }
    }




}

