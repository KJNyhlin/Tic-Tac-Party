package com.example.tictacparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore


class CreateAccountActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    var newPlayer = Player()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val editName = findViewById<EditText>(R.id.editTextText)
        val editPassword = findViewById<EditText>(R.id.editTextTextPassword)
        val editPasswordConfirm = findViewById<EditText>(R.id.editTextTextPassword2)
        val editMail = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        val createButton = findViewById<Button>(R.id.Create)

        addAvatarImageListeners()

        createButton.setOnClickListener {
            val Username = editName.text.toString()
            val password = editPassword.text.toString()
            val passwordConfirm = editPasswordConfirm.text.toString()
            val email = editMail.text.toString()
            var avatarImage = newPlayer.avatarImage


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
            } else if(avatarImage==0){
                Toast.makeText(this, "You must choose an avatar", Toast.LENGTH_SHORT).show()
            } else {
                userNameFree(Username) {isFree ->
                    if(isFree) {
                        createUser(Username, password, email, avatarImage)
                    } else {
                        Toast.makeText(this, "Your username is taken :(", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //Just for test/example how to call
        //createUser("andreas", "Passw0rd", "andreas@andreas.se")
    }

    private fun userNameFree(username: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val playersCollection = db.collection("players")
        playersCollection
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                val isFree = documents.isEmpty // Check if no documents match the query
                callback(isFree)
            }
            .addOnFailureListener { exception ->
                Log.w("!!!", "Error getting documents: ", exception)
                callback(false)
            }
    }
    private fun createUser(userName : String, password : String, email : String, avatarImage : Int) {
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

                    val player = Player(email, userName, uid.toString(), 0, 0, 0, avatarImage, 0, false, 0, "")
                    GlobalVariables.player = player
                    Log.d("!!!", "player.avatarimage=${player.avatarImage}")
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
    fun addAvatarImageListeners() {

        val avatars = listOf(
            Pair(findViewById<ImageView>(R.id.avatar1), R.drawable.avatar_1),
            Pair(findViewById<ImageView>(R.id.avatar2), R.drawable.avatar_2),
            Pair(findViewById<ImageView>(R.id.avatar3), R.drawable.avatar_3),
            Pair(findViewById<ImageView>(R.id.avatar4), R.drawable.avatar_4),
            Pair(findViewById<ImageView>(R.id.avatar5), R.drawable.avatar_5),
            Pair(findViewById<ImageView>(R.id.avatar6), R.drawable.avatar_6),
            Pair(findViewById<ImageView>(R.id.avatar7), R.drawable.avatar_7),
            Pair(findViewById<ImageView>(R.id.avatar8), R.drawable.avatar_8),
            Pair(findViewById<ImageView>(R.id.avatar9), R.drawable.avatar_9),
            Pair(findViewById<ImageView>(R.id.avatar10), R.drawable.avatar_10),
        )
        for((imageView, resId)in avatars){
            imageView.setOnClickListener {
                Log.d("!!!", "inAvatarListener - Old avatarImage: ${newPlayer.avatarImage}")
                newPlayer.avatarImage=resId
                Log.d("!!!", "inAvatarListener - New avatarImage: ${newPlayer.avatarImage}")

                //Added an animation, so that user "knows" that the avatar is clicked
                val scaleAnimation= ScaleAnimation(0.8f,1.0f,0.8f,1.0f)
                scaleAnimation.duration=500
                imageView.startAnimation(scaleAnimation)
            }
        }
    }
}