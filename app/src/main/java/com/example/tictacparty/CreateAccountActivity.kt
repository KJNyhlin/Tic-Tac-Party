package com.example.tictacparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User


class CreateAccountActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val editName = findViewById<EditText>(R.id.editTextText)
        val editPassword = findViewById<EditText>(R.id.editTextTextPassword)
        val editPasswordConfirm = findViewById<EditText>(R.id.editTextTextPassword2)
        val editMail = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        val createButton = findViewById<Button>(R.id.Create)

        createButton.setOnClickListener {
            val Username = editName.text.toString()
            val password = editPassword.text.toString()
            val passwordConfirm = editPasswordConfirm.text.toString()
            val email = editMail.text.toString()

            if (Username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || email.isEmpty()) {
                //Empty fields
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            } else if (password != passwordConfirm) {
                //Password and Password confirm doesn't match
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            } else if (!"^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$".toRegex()
                    .matches(email)
            ) {
                Toast.makeText(this, "The email address is badly formatted.", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(this, "The given password is invalid. [ Password should be at least 6 characters ]", Toast.LENGTH_SHORT).show()
            } else {
                createUser(Username, password, email)
            }
        }

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
                    Log.d("!!!", "logged in user: ${auth.currentUser?.email}")
                    Log.d("!!!", "logged in user: ${auth.currentUser?.uid}")
                    Log.d("!!!", "logged in user: ${auth.currentUser?.uid.toString()}")
                    var uid = auth.currentUser?.uid ?: ""

                    val player = Player(email, userName, uid.toString(), 0, 0, 0, 0, 0, false)
                    GlobalVariables.player = player
                    db.collection("players")
                        .add(player)
                        .addOnSuccessListener { documentReference ->
                            Log.d("!!!", "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w("!!!", "Error adding document", e)
                        }
                    if(auth.currentUser?.email != null) {
                        GlobalVariables.loggedInUser = auth.currentUser?.email
                        GlobalVariables.loggedIn = true

                    } else {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    if (auth.currentUser?.email != null) {
                                        GlobalVariables.loggedInUser = auth.currentUser?.email
                                        GlobalVariables.loggedIn = true
                                        Toast.makeText(
                                            this,
                                            "Authentication succeeded.",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {
                                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                                }

                            }
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else {
                    Log.d("!!!", "User not created ${task.exception}")
                    if(task.exception.toString() == "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.") {
                        Toast.makeText(
                            this,
                            "Creation failed - The email address is already in use by another account.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }
}