package com.example.tictacparty

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
import com.google.firebase.firestore.FirebaseFirestore


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
    lateinit var playAgainButton : FloatingActionButton
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)








        if (GlobalVariables.player != null) {
            playerOne = GlobalVariables.player!!

            playerOne.symbol = "X"
        }
        currentPlayer = playerOne
        android.os.Handler().postDelayed({

            val opponentUsername: String? = intent.getStringExtra("opponentsUsername")

            opponentUsername?.let { username ->
                fetchPlayerByUsername(username) { player ->
                    // Callback function to handle the result
                    if (player != null) {
                        // Player found, initialize playerTwo and proceed with the game setup
                        playerTwo = player
                        currentPlayer = playerOne
                        game = Game(
                            1,
                            playerOne,
                            playerTwo,
                            1,
                            "ongoing",
                            mutableListOf("", "", "", "", "", "", "", "", "")
                        )
                        iniatilizeViews()
                        showGameViews()
                        updateUI()
                        addingClickListeners()
                        updateDatabase()
                        Toast.makeText(this,"$opponentUsername", Toast.LENGTH_SHORT).show()
                    } else {
                        // Player not found, handle the error
                        println("No player found with opponent username: $username")
                    }
                }
            }

        }, 500)
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
        playAgainButton=findViewById(R.id.playAgainButton)
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

        addExitDialog()

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

            if (status != "ongoing") {
                return
            }
            //gameButton1.tag=1, gameButton2.tag=2 osv...
            //filledPos("","","","X","","","","","O"))
            //filledPos[clickedPos] fylls i med currentPlayer.symbol = antingen "X" eller "O"
            val clickedPos = (button?.tag as String).toInt() - 1
            if (filledPos[clickedPos] == "") {
                filledPos[clickedPos] = currentPlayer.symbol
                switchPlayers()
                updateUI()
                checkForWinner()
                updateDatabase()
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

    fun updateUI() {
        //index = filledPos[index]
        //Index(1, 2,  3  4  5  6  7  8  9
        //     ("","","","","","","","",""))
        // Value är vad det finns för värde i filledPos[index] t.ex "X" eller "O"
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
            if(status=="ongoing"){
                playAgainButton.visibility=View.INVISIBLE
                gameInfo.text = "${currentPlayer.symbol} - ${currentPlayer.username.capitalize()}'s turn"
            }
        }
        if(game.status=="finished"){
            playAgainButton.visibility=View.VISIBLE
            //startActivity(Intent(this,MatchMakingFragment::class.java))
            playAgainButton.setOnClickListener{

                //Temporary, should lead to matchmaking??
                val intent= Intent(this,GameActivity::class.java)
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
        updateUI()
    }

    private fun updateDatabase() {

        val db = Firebase.firestore


        val gameRef = db.collection("games").document(game.id.toString())


        val updates = hashMapOf<String, Any>(
            "id" to game.id,
            "playerOne" to game.playerOne.toHashMap(),
            "playerTwo" to game.playerTwo.toHashMap(),
            "nextMove" to game.nextMove,
            "status" to game.status,
            "filledPos" to game.filledPos
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
        player1_avatar.setImageResource(playerOne.avatarImage)
        username1.text = "${playerOne.username.capitalize()}"

        player2_avatar.setImageResource(playerTwo.avatarImage)
        username2.text = "${playerTwo.username.capitalize()}"

    }
}

