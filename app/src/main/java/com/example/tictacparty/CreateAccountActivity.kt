package com.example.tictacparty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class CreateAccountActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        //Just for test/example how to call
        //createUser("andreas", "Passw0rd", "andreas@andreas.se")
    }
    private fun createUser(userName : String, password : String, email : String) {
        // UserName cannot be stored, just email+password, might be added as a connected table in firestore as well somehow?
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful) {
                    Log.d("!!!", "create success")

                }
                else {
                    Log.d("!!!", "User not created ${task.exception}")

                }
            }
    }
}