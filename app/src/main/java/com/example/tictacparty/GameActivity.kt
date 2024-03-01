package com.example.tictacparty

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
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.play.integrity.internal.i
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.DocumentId
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
    lateinit var game: Game
    var buttons = mutableListOf<ImageButton>()
    val db = com.google.firebase.ktx.Firebase.firestore
    val playersCollection = db.collection("players")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)


        iniatilizeViews()

        if (GlobalVariables.player != null) {
            playerOne = GlobalVariables.player!!

            playerOne.symbol = "X"
            currentPlayer = playerOne

        }

        val opponentDocumentId = intent.getStringExtra("opponentDocumentId")

        fetchPlayerByDocumentId(opponentDocumentId!!) { player ->
            if (player != null) {
                // Player retrieved successfully, use the player object
                playerTwo = player
                playerTwo.symbol = "O"
            } else {
                playerTwo = Player("", "", "Blomkrukan", "", 0, 0, 0, 2131230844, 0, false, 0, "O")
            }
        }

//        playerTwo = Player("", "", "Blomkrukan", "", 0, 0, 0, 2131230844, 0, false, 0, "O")
        currentPlayer = playerOne
        android.os.Handler().postDelayed({
            showGameViews()
            uploadToFirestoreAndSnapshotListener()
            updateDatabase(game)

        }, 1000)


    }

    fun uploadToFirestoreAndSnapshotListener() {


        val db = Firebase.firestore

        if (!::playerOne.isInitialized || !::playerTwo.isInitialized) {
            Log.d("!!!", "Players not initialized yet")
            return
        }

        val documentRef = db.collection("games").document()
        val documentId = documentRef.id

        game = Game(
            documentId,
            playerOne.email,
            playerTwo.email,
            "ongoing",
            mutableListOf("", "", "", "", "", "", "", "", "")
        )


        documentRef.set(game)
            .addOnSuccessListener {
                Log.d("!!!", "\"Game added to Firestore with document ID: $documentId")
            }
            .addOnFailureListener { e ->
                Log.d("!!!", " Error adding game to Firestore : ")
            }
        Log.d("!!!", "game.document : !: $documentId")

        documentRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("!!!", "Listen failed")
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val game = snapshot.toObject<Game>()
                Log.d("!!!", game.toString())
                if (game != null) {
                    updateUI(game)
                }
            } else {
                Log.d("!!!", "Current data: Null")
            }
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
        currentPlayer = if (currentPlayer == playerOne) {
            playerTwo
        } else {
            playerOne
        }
    }

    override fun onClick(button: View?) {
        Log.d("!!!", "current player on click${currentPlayer.username}")
        game.apply {
            //apply gör att man kan göra operationer direkt på ett objekt, i det här fallet game, så slipper man skriva game.filledPos
            Log.d("!!!", "on click : {$filledPos]")
            if (status != "ongoing") {
                return
            }
            //gameButton1.tag=1, gameButton2.tag=2 osv...
            //filledPos("","","","X","","","","","O"))
            //filledPos[clickedPos] fylls i med currentPlayer.symbol = antingen "X" eller "O"


            val clickedPos = (button?.tag as String).toInt() - 1
            Log.d("!!!", "clicked Pos : $clickedPos")
            if (filledPos[clickedPos] == "") {
                Log.d("!!!", "on click 2 : $filledPos]")
                filledPos[clickedPos] = currentPlayer.symbol
                switchPlayers()
                updateUI(game)
                updateDatabase(game)

                checkForWinner()

                Log.d("!!!", "$filledPos")
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
                gameInfo.text =
                    "${currentPlayer.symbol} - ${currentPlayer.username.capitalize()}'s turn"
            }
        }
        if (game.status == "finished") {
            playAgainButton.visibility = View.VISIBLE
            //startActivity(Intent(this,MatchMakingFragment::class.java))
            playAgainButton.setOnClickListener {

                //Temporary, should lead to matchmaking??
                val intent = Intent(this, GameActivity::class.java)
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
            "playerOne" to game.playerOne,
            "playerTwo" to game.playerTwo,
            "status" to game.status,
            "filledPos" to game.filledPos
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

    fun fetchPlayerByUsername(opponentUsername: String, callback: (Player?) -> Unit) {
        val db = Firebase.firestore
        val playersCollection = db.collection("players")

        playersCollection.whereEqualTo("opponentUsername", opponentUsername)
            .get()
            .addOnSuccessListener { documents ->
                val player = documents.toObjects(Player::class.java).firstOrNull()
                callback(player)
            }
            .addOnFailureListener { exception ->
                Log.e("fetchPlayerByUsername", "Error getting player by username", exception)
                callback(null)
            }
    }

    fun fetchPlayerByDocumentId(opponentDocumentId: String, callback: (Player?) -> Unit) {
        val db = Firebase.firestore
        val playersCollection = db.collection("players")

        val opponent = playersCollection.document(opponentDocumentId)

        opponent.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // The document exists, create a new Player object and assign the document's fields to its attributes
                    val opponentPlayerObject = Player(
                        opponentDocumentId,
                        document.getString("email") ?: "",
                        document.getString("username") ?: "",
                        document.getString("userId") ?: "",
                        document.getLong("wins")?.toInt() ?: 0,
                        document.getLong("lost")?.toInt() ?: 0,
                        document.getLong("gamesPlayed")?.toInt() ?: 0,
                        document.getLong("avatarImage")?.toInt() ?: 0,
                        document.getLong("mmrScore")?.toInt() ?: 0,
                        document.getBoolean("searchingOpponent") ?: false,
                        document.getLong("searchingOpponentStartTime") ?: 0,
                        document.getString("symbol") ?: ""
                    )
                    callback(opponentPlayerObject)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("fetchPlayerByDocumentId", "Error getting player by document ID", exception)
                callback(null)
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
}

