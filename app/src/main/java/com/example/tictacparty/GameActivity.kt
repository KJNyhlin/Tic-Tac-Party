package com.example.tictacparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class GameActivity : AppCompatActivity() {

    lateinit var titleTextView : TextView
    lateinit var loggedInPlayerImage : ImageView
    lateinit var otherPlayerImage : ImageView
    lateinit var gamebutton1 : ImageButton
    lateinit var gamebutton2 : ImageButton
    lateinit var gamebutton3 : ImageButton
    lateinit var gamebutton4 : ImageButton
    lateinit var gamebutton5 : ImageButton
    lateinit var gamebutton6 : ImageButton
    lateinit var gamebutton7 : ImageButton
    lateinit var gamebutton8 : ImageButton
    lateinit var gamebutton9 : ImageButton
    lateinit var loggedInUsername: TextView
    lateinit var otherUsername: TextView
    lateinit var gameInfo : TextView
    lateinit var exitImage : ImageView
    lateinit var helpImage : ImageView

    var buttons = mutableListOf<ImageButton>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)


        iniatilizeViews()
        addingClickListeners()
        updateNewGameViews()

    }
    fun iniatilizeViews(){
        titleTextView = findViewById(R.id.titleTextView)
        loggedInPlayerImage = findViewById(R.id.loggedInPlayerImage)
        otherPlayerImage = findViewById(R.id.otherPlayerImage)
        gamebutton1 = findViewById(R.id.gameButton1)
        gamebutton2  =findViewById(R.id.gameButton2)
        gamebutton3 = findViewById(R.id.gameButton3)
        gamebutton4  =findViewById(R.id.gameButton4)
        gamebutton5 = findViewById(R.id.gameButton5)
        gamebutton6  =findViewById(R.id.gameButton6)
        gamebutton7 = findViewById(R.id.gameButton7)
        gamebutton8  =findViewById(R.id.gameButton8)
        gamebutton9 = findViewById(R.id.gameButton9)
        loggedInUsername = findViewById(R.id.loggedInUsername)
        otherUsername = findViewById(R.id.otherUsername)
        gameInfo = findViewById(R.id.gameInfo)
        exitImage = findViewById(R.id.exitImage)
        helpImage = findViewById(R.id.helpImage)



    }
    fun addingClickListeners(){
        buttons.add(gamebutton1)
        buttons.add(gamebutton2)
        buttons.add(gamebutton3)
        buttons.add(gamebutton4)
        buttons.add(gamebutton6)
        buttons.add(gamebutton7)
        buttons.add(gamebutton7)
        buttons.add(gamebutton8)
        buttons.add(gamebutton9)

        for(button in buttons){
            button.setOnClickListener {

            }
        }

        helpImage.setOnClickListener {

        }
        addExitDialog()
    }
    fun addExitDialog() {

        val addContactDialog = AlertDialog.Builder(this)
            .setTitle(" Exit game?")
            .setMessage("Do you want to exit the game? You will lose points by exiting")
            .setIcon(R.drawable.gameboard)
            .setPositiveButton("Yes"){_, _->
                Toast.makeText(this, "You exited!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton("No"){_,_->
                Toast.makeText(this,"You didn't exit.", Toast.LENGTH_SHORT).show()

            }.create()

        exitImage.setOnClickListener {
            addContactDialog.show()
        }
    }
    fun updateNewGameViews(){

        if(GlobalVariables.player?.avatarImage!=null) {
            loggedInPlayerImage.setImageResource(GlobalVariables.player!!.avatarImage)
            Log.d("!!!", "inMainActivity: ${GlobalVariables.player!!.avatarImage}")
        }
        //TOOD : We need to decide where the GameIcon for the players ( X or O )  shall be stored.
        if(GlobalVariables.player!=null){
            loggedInUsername.text = "X - ${GlobalVariables.player!!.username}"
        }

    }
}
