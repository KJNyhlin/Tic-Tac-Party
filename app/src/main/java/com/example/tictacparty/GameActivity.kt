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
import com.google.firebase.firestore.FirebaseFirestore


class GameActivity : AppCompatActivity() , View.OnClickListener{

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

//       Förslag hur man använder GlobalVariables.player till PlayerOne
//        if(GlobalVariables.player!=null){
//            val playerOne = GlobalVariables.player
//        }


        playerOne =
            Player("", "email@example.com", "Spelare 1", "id1", 0, 0, 0, 0, 0, false, 0, "X")
        playerTwo =
            Player("", "email2@example.com", "Spelare 2", "id2", 0, 0, 0, 0, 0, false, 0, "O")

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
        addingClickListeners()

        makeMove()
        updateDatabase()

        Log.d("!!!", "Before calling testingFirestore()")
        testingFirestore()
        Log.d("!!!", "After calling testingFirestore()")


    }

    fun testingFirestore() {

        //ett test bara för att se hur man använder databasen
        val db = FirebaseFirestore.getInstance()
        val gamesCollection = db.collection("gamestest")

        val gamesList: List<Game> = listOf(
            Game(
                1,
                playerOne,
                playerTwo,
                1,
                "ongoing",
                mutableListOf("", "", "", "", "", "", "", "", "")
            ),
            // Add more Game objects as needed
        )
        for (game in gamesList) {
            val documentRef = gamesCollection.document()
            documentRef.set(game)
                .addOnSuccessListener { Log.d("!!!", "Gameuploaded sucessfully") }
                .addOnFailureListener { e ->
                    Log.d("!!!", "Error uploading game")
                }
        }


    }

    fun iniatilizeViews() {
        titleTextView = findViewById(R.id.titleTextView)
        player1_avatar = findViewById(R.id.player2)
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

        buttons.add(gamebutton1)
        buttons.add(gamebutton2)
        buttons.add(gamebutton3)
        buttons.add(gamebutton4)
        buttons.add(gamebutton5)
        buttons.add(gamebutton6)
        buttons.add(gamebutton7)
        buttons.add(gamebutton8)
        buttons.add(gamebutton9)
        for(button in buttons){
            button.setOnClickListener(this)
        }

    }

    fun addingClickListeners() {


        helpImage.setOnClickListener {

        }

        addExitDialog()

    }

    fun determineWhoseTurnItIs(game: Game): Player {
        return if (game.nextMove == 1) game.playerOne else game.playerTwo
    }

    fun switchPlayers() {
        //för mig kändes det lättare att hålla reda på currentPlayer när jag skrev koden,
        // men det är upp till dig vilken funktion du vill använda såklart!
        currentPlayer = if (currentPlayer == playerOne) {
            playerTwo
        } else {
            playerOne
        }
    }

    fun checkForWinner() {
        //inte en klar funktion, just nu gör den inte det den ska
        val winningPos = arrayOf(
            intArrayOf(1, 2, 3),
            intArrayOf(4, 5, 6),
            intArrayOf(7, 8, 9),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(3, 6, 9),
            intArrayOf(1, 5, 9),
            intArrayOf(3, 5, 7),
        )

    }
    override fun onClick(button: View?)
    //istället för maveMove() men den gör i princip det som makeMove() ska göra, det var för att slippa kalla på makeMove om och om igen.
    //Den här funktion kallas nu varje gång man klickar på en knapp istället
    //(Du får såklart välja vad du känner för att använda )
    {

        game.apply {
            //apply gör att man kan göra operationer direkt på ett objekt, i det här fallet game, så slipper man skriva game.filledPos

            //gameButton1.tag=1, gameButton2.tag=2 osv...
            //filledPos("","","","X","","","","","O"))
            //filledPos[clickedPos] fylls i med currentPlayer.symbol = antingen "X" eller "O"
            val clickedPos = (button?.tag as String).toInt()-1
            if(filledPos[clickedPos]==""){
                filledPos[clickedPos]=currentPlayer.symbol
                updateUI()
                checkForWinner()
                switchPlayers()
                updateDatabase()
                Log.d("!!!","$filledPos")
            }else {
                Toast.makeText(
                    this@GameActivity,
                      "This place is taken! 😅",
                           Toast.LENGTH_SHORT).show()
                    }
        }
    }
    fun updateUI(){
        //index = filledPos[index]
        //Index(1, 2,  3  4  5  6  7  8  9
        //     ("","","","","","","","",""))
        // Value är vad det finns för värde i filledPos[index] t.ex "X" eller "O"
        game.apply {
            for((index, value)in filledPos.withIndex()) {
                val button = buttons[index]
                button.setImageResource(
                    when (value) {
                        "X" -> R.drawable.profile_icon
                        "O" -> R.drawable.vector__1_
                        else->0
                    }
                )
            }
        }
    }

    private fun makeMove() {
//        }
//        for (button in buttons) {
//            Log.d("!!!","$filledPos")
//            button.setOnClickListener { button ->
//                val position = (button.tag as String).toInt()
//                if (filledPos[position].isEmpty()) {
//                    filledPos[position] = currentPlayer.symbol
//                    updateUI()
//                    checkForWinner()
//                    switchPlayers()
//                    updateDatabase()
//                } else {
//                    Toast.makeText(
//                        this@GameActivity,
//                        "This place is taken! 😅",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
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

    fun updateNewGameViews() {
        if (GlobalVariables.player?.avatarImage != null) {
            player1_avatar.setImageResource(GlobalVariables.player!!.avatarImage)
            Log.d("!!!", "inMainActivity: ${GlobalVariables.player!!.avatarImage}")
        }

        if (GlobalVariables.player != null) {
            username1.text =
                "${GlobalVariables.player!!.symbol} - ${GlobalVariables.player!!.username}"
        }

    }


}

