package com.example.tictacparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        var createAccountButton = findViewById<Button>(R.id.accountBtn)
        var loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            //TODO this is examplecode because of missing loginactivity
            //signIn("uu@uu.uu", "uuuuuu")

            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)

        }
        createAccountButton.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }


    }

    //TODO This function should be moved to "LoginActivity" when created
    private fun signIn(email: String, password: String) {
        lateinit var auth : FirebaseAuth
        val db = FirebaseFirestore.getInstance()
        val playersCollection = db.collection("players")


        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
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
                                Toast.makeText(this, "Authentication succeeded.", Toast.LENGTH_SHORT).show()
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