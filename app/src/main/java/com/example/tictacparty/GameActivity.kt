package com.example.tictacparty

import Function.removeMatchmakingRoom
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Typeface
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
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
    lateinit var opponent: Player
    var localPlayer = GlobalVariables.player

    lateinit var username1: TextView
    lateinit var username2: TextView
    lateinit var gameInfo: TextView
    lateinit var exitImage: ImageView
    lateinit var helpImage: ImageView
    var gameResult = ""
    var localPlayerGivesUp : Boolean = false
    var roomId:String?=""
    lateinit var game: Game
    var buttons = mutableListOf<ImageButton>()
    val db = com.google.firebase.ktx.Firebase.firestore
    val playersCollection = db.collection("players")



    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        //do nothing
    }

    override fun onDestroy() {
        super.onDestroy()
        // Nullify the game variable to release its memory
        game = Game()
    }

    private fun setupGameSnapshotListener(roomId: String) {
        val db = Firebase.firestore
        val gameRef = db.collection("games").document(roomId)

        gameRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val updatedGame = snapshot.toObject(Game::class.java)
                if (updatedGame != null) {
                    game = updatedGame
                    updateUI(game) // Update the UI with the updated game state
                    Log.d("!!!", "updateUI is run in snapshotListener")
                    checkForWinner()
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

    override fun onClick(button: View?) {
        Log.d("!!!", "current player on click${currentPlayer.username}")
        if(currentPlayer.username == GlobalVariables.player?.username) {
            game.apply {
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
                    updateUI(game)
                    updateFilledPosInDatabase(game.documentId, clickedPos, filledPos[clickedPos], nextTurnPlayer)
                } else {
                    Toast.makeText(
                        this@GameActivity,
                        "This place is taken! 😅",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateMMRScore(gameResult: String) {
        /*
        Vinst mot bättre spelare : 25+ mmr score
        Förlust mot bättre spelare: 0 mmr score
        Vinst mot sämre spelare : 10+ mmr score
        Förlust mot sämre spelare : -5mmr score
        Oavgjort mot bättre spelare: +1 mmr
        Oavgjort mot sämre spelare: 0
        Om man avslutar match: samma som förlust
        OBS! Likvärdig motspelare = samma som bättre motspelare
         */
        if (gameResult == "Draw") {
            if (opponent.mmrScore >= localPlayer?.mmrScore!!) {
                localPlayer?.mmrScore = localPlayer?.mmrScore!! + 1
                Log.d("!!!", "Draw. Opponent is higher ranked, +1 MMR")
            } else {
                Log.d("!!!", "Draw. Opponent is lower ranked. ±0")
            }
        }
        else if (gameResult == "Win") {
            if (currentPlayer.username == localPlayer?.username || localPlayerGivesUp) {
                // opponent made the last move, ie opponent wins
                // or local player has quit the game (given up) = opponent wins
                localPlayer?.lost = localPlayer?.lost!! + 1
                if (opponent.mmrScore < localPlayer?.mmrScore!!) {
                    Log.d("!!!", "You lost against an inferior player. -5 MMR")
                    localPlayer?.mmrScore = localPlayer?.mmrScore!! - 5
                }
            } else {
                // local player wins
                localPlayer?.wins = localPlayer?.wins!! + 1
                if (opponent.mmrScore >= localPlayer?.mmrScore!!) {
                    Log.d("!!!", "You won against a superior player. +25 MMR")
                    localPlayer?.mmrScore = localPlayer?.mmrScore!! + 25
                } else {
                    Log.d("!!!", "You won against an inferior player. +10 MMR")
                    localPlayer?.mmrScore = localPlayer?.mmrScore!! + 10
                }
                }
            }
        localPlayer?.gamesPlayed = localPlayer?.gamesPlayed!! + 1
        val playerCopy = localPlayer!!.copy()
        updatePlayerInFirestore(playerCopy)

    }

    fun updatePlayerInFirestore(player: Player) {
        val playerRef = playersCollection.document(player.documentId)

        // Skapa en HashMap med de nya värdena för spelaren
        val updatedValues = player.toHashMap()

        // Uppdatera dokumentet i Firestore med de nya värdena
        playerRef.update(updatedValues)
            .addOnSuccessListener {
                // Uppdateringen lyckades
                Log.d("!!!", "Player updated successfully")
            }
            .addOnFailureListener { exception ->
                // Uppdateringen misslyckades, logga felet
                Log.d("!!!", "Error updating player", exception)
            }
    }



    fun updateUI(game: Game) {
        var nonActivePlayerUsername : String
        var userName: String
        if (game.nextTurnPlayer == playerOne.email) {
            userName = playerOne.username
            nonActivePlayerUsername= playerTwo.username
        } else if (game.nextTurnPlayer == playerTwo.email) {
            userName = playerTwo.username
            nonActivePlayerUsername= playerOne.username
        } else {
            userName = game.nextTurnPlayer
            nonActivePlayerUsername = "Unknown"
        }
        currentPlayer = if (game.nextTurnPlayer == playerOne.email) {
            playerOne
        } else {
            playerTwo
        }

        if(currentPlayer.username==GlobalVariables.player?.username)
        {
            gameInfo.text = "${currentPlayer.symbol} - Your turn"

        }
        else {gameInfo.text = "${currentPlayer.symbol} - ${userName.capitalize()}'s turn"}

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
            }
        }
        if (game.status == "finished") {
            playAgainButton.visibility = View.VISIBLE
            if(gameResult == "Draw"){
                gameInfo.text = "Game over, its a draw"
            } else {
                gameInfo.text = "${nonActivePlayerUsername} wins!"
            }
            gameInfo.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            gameInfo.setTypeface(null, Typeface.BOLD)

            playAgainButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            removeFinishedGames(game){
                updateUIAfterGameFinished(game)
//                startTimerGoToMainActivity()
            }
        }
    }

    fun updateUIAfterGameFinished(game: Game) {
        val db = FirebaseFirestore.getInstance()
        val gameRef = db.collection("games").document(game.documentId)

        gameRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val updatedGame = documentSnapshot.toObject(Game::class.java)
                    if (updatedGame != null) {
                        if (updatedGame.status == "finished") {
                            if (gameInfo.text.isEmpty()) {
                                gameInfo.text = "Game Over"
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Document does not exist")
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Failed to get document snapshot: $e")
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

        var gameFinished = false

        game.apply {
            for (i in winningPos) {
                if (
                    filledPos[i[0]] == filledPos[i[1]] &&
                    filledPos[i[1]] == filledPos[i[2]] &&
                    filledPos[i[0]].isNotEmpty()
                ) {
                    Log.d("!!!", "Den senaste spelaren vinner.")
                    status = "finished"
                    //gameInfo.text = "${currentPlayer.username.capitalize()} wins"
                    // ovanstående rad visas aldrig för texten uppdateras igen i updateUI
                    gameResult = "Win"
                    gameFinished = true
                    break
                }
            }

            if (!gameFinished && filledPos.none { it.isEmpty() }) {
                Log.d("!!!", "Det blev oavgjort.")
                status = "finished"
                gameResult = "Draw"
                //gameInfo.text = "Draw" //visas aldrig för texten uppdateras igen i updateUI
                gameFinished = true
            }
        }

        if (gameFinished) {
            updateDatabase(game)
            updateUI(game)
            updateMMRScore(gameResult)
        }
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

        documentRef.set(game)
            .addOnSuccessListener {
                Log.d("UpdateDatabase", "Game successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("UpdateDatabase", "Error updating game", e)
            }
    }
    fun removeFinishedGames(game: Game, onComplete: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val gameRef = db.collection("games").document(game.documentId)
        gameRef.delete()
            .addOnSuccessListener {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(5000) // Delay execution for 5 seconds
                    onComplete()
                    //startMainActivity()
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to remove game from Firestore", it)
            }

    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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
                localPlayerGivesUp=true
                updateMMRScore("Win") // "Win" because it's not a draw
                //TODO viktigt att avsluta nuvarande game! removeFinishedGame()?
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
//            player1_avatar.setImageResource(playerOne.avatarImage)
//            username1.text = "${playerOne.username.capitalize()}"
//
//            player2_avatar.setImageResource(playerTwo.avatarImage)
//            username2.text = "${playerTwo.username.capitalize()}"
            if(GlobalVariables!=null) {
                if (GlobalVariables.player!!.email == game.playerOneId) {
                    player1_avatar.setImageResource(playerOne.avatarImage)
                    username1.text = "${playerOne.username.capitalize()}"
                    player2_avatar.setImageResource(playerTwo.avatarImage)
                    username2.text = "${playerTwo.username.capitalize()}"
                } else if (GlobalVariables.player!!.email == game.playerTwoId) {
                    player1_avatar.setImageResource(playerTwo.avatarImage)
                    username1.text = "${playerTwo.username.capitalize()}"
                    player2_avatar.setImageResource(playerOne.avatarImage)
                    username2.text = "${playerOne.username.capitalize()}"
                }
            }
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
                        //checking here which player is the opponent
                        opponent = if (playerOne.username == GlobalVariables.player?.username) {
                            playerTwo
                        } else {
                            playerOne
                        }
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
            playerOne.email
        )
        currentPlayer = playerOne
        showGameViews()
        uploadGameToFirestore(game)

        addExitDialog()
        setupGameSnapshotListener(game.documentId)
        updateUI(game)
        updateDatabase(game)

    }

    fun updateFilledPosInDatabase(documentId: String, index: Int, newValue: String, nextTurnPlayer : String) {
        val db = Firebase.firestore
        val documentRef = db.collection("games").document(documentId)

        documentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val game = documentSnapshot.toObject(Game::class.java)
                if (game != null) {
                    val filledPos = game.filledPos ?: mutableListOf() // Provide a default value if filledPos is null
                    filledPos[index] = newValue

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
