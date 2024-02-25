package com.example.tictacparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        val editMail = findViewById<EditText>(R.id.editTextEmail)
        val editPassword = findViewById<EditText>(R.id.editTextTextPassword3)
        val loginButton = findViewById<Button>(R.id.logInButton)

        loginButton.setOnClickListener {
            val email = editMail.text.toString()
            val password = editPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                //Empty fields
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            } else if (!"^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$".toRegex()
                    .matches(email)
            ) {
                Toast.makeText(this, "The email address is badly formatted.", Toast.LENGTH_SHORT).show()
            } else {
                signIn(email, password)
            }
        }

    }

    private fun signIn(email: String, password: String) {
        lateinit var auth : FirebaseAuth
        val db = FirebaseFirestore.getInstance()
        val playersCollection = db.collection("players")


        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //val user = auth.currentUser
                    if (auth.currentUser?.email != null) {
                        val userId = auth.currentUser?.uid.toString()
                        var player: Player? = null
                        playersCollection
                            .whereEqualTo("userId", userId)
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    // Convert the document data to a Player object
                                    player = document.toObject(Player::class.java)
                                    // Use the player object as needed
                                    Log.d("!!!", "Player: $player")
                                    if (player != null) {
                                        GlobalVariables.player = player
                                    }
                                }
                                GlobalVariables.loggedInUser = auth.currentUser?.email
                                GlobalVariables.loggedIn = true
                                Log.d("!!!", "Authentication succeeded.")
                                // Proceed to next activity or handle authentication success
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                Log.w("!!!", "Error getting documents: ", exception)
                            }
                    } else {
                        signIn(email, password)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}