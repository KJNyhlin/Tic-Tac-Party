package com.example.tictacparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = Firebase.firestore

        val playNowButton = findViewById<Button>(R.id.playNowButton)
        val matchHistoryButton = findViewById<Button>(R.id.matchHistoryButton)
        val challengeAFriendButton = findViewById<Button>(R.id.challengeAFriendButton)

//        playNowButton.setOnClickListener {
//            //temporary solution, this button should lead to matchmaking
//            val intent = Intent(this, GameActivity::class.java)
//            startActivity(intent)
//        }


    }
    fun addMatchMakingFragment(view : View){
        val matchFragment = MatchMakingFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragmentContainer,matchFragment, "matchFragment")
        transaction.commit()
    }
    fun removeMatchMakingFragment(view : View){
        val matchFragment = supportFragmentManager.findFragmentByTag("matchFragment")

        if(matchFragment !=null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.remove(matchFragment)
            transaction.commit()
        }
        else{
            Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show()
        }
    }
    fun addMain(view : View ){
        val mainFragment = MainFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragmentContainer,mainFragment, "mainFragment")
        transaction.commit()
    }
}