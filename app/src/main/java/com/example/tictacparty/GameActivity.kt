package com.example.tictacparty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class GameActivity : AppCompatActivity() {

    lateinit var titleTextView : TextView
    lateinit var player1_avatar : ImageView
    lateinit var player2_avatar : ImageView
    lateinit var gamebutton1 : ImageButton
    lateinit var gamebutton2 : ImageButton
    lateinit var gamebutton3 : ImageButton
    lateinit var gamebutton4 : ImageButton
    lateinit var gamebutton5 : ImageButton
    lateinit var gamebutton6 : ImageButton
    lateinit var gamebutton7 : ImageButton
    lateinit var gamebutton8 : ImageButton
    lateinit var gamebutton9 : ImageButton
    lateinit var username1: TextView
    lateinit var username2: TextView
    lateinit var gameInfo1 : TextView
    lateinit var gameInfo2 : TextView
    lateinit var exitImage : ImageView
    lateinit var helpImage : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)


        iniatilizeViews()
        exitImage.setOnClickListener {

        }
        helpImage.setOnClickListener {

        }
    }
    fun iniatilizeViews(){
        titleTextView = findViewById(R.id.titleTextView)
        player1_avatar = findViewById(R.id.player1)
        player2_avatar = findViewById(R.id.player2)
        gamebutton1 = findViewById(R.id.gameButton1)
        gamebutton2  =findViewById(R.id.gameButton2)
        gamebutton3 = findViewById(R.id.gameButton3)
        gamebutton4  =findViewById(R.id.gameButton4)
        gamebutton5 = findViewById(R.id.gameButton5)
        gamebutton6  =findViewById(R.id.gameButton6)
        gamebutton7 = findViewById(R.id.gameButton7)
        gamebutton8  =findViewById(R.id.gameButton8)
        gamebutton9 = findViewById(R.id.gameButton9)
        username1 = findViewById(R.id.username1)
        username2 = findViewById(R.id.username2)
        gameInfo1 = findViewById(R.id.gameInfo1)
        gameInfo2 = findViewById(R.id.gameInfo2)
        exitImage = findViewById(R.id.exitImage)
        helpImage = findViewById(R.id.helpImage)
    }
}
