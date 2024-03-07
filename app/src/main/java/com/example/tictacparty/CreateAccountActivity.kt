package com.example.tictacparty

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore


class CreateAccountActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    var newPlayer = Player()

    private fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard
        if (view !is EditText) {
            view.setOnTouchListener { v, _ ->
                hideKeyboard(this@CreateAccountActivity)
                v.performClick()
                false
            }
        }

        // If a layout container, iterate over children and seed recursion
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView: View = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)


        val rootLayout = findViewById<ConstraintLayout>(R.id.rootLayout)
        setupUI(rootLayout)

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
                Toast.makeText(this, "The email address is badly formatted.", Toast.LENGTH_SHORT)
                    .show()
            } else if (password.length < 6) {
                Toast.makeText(
                    this,
                    "The given password is invalid. [ Password should be at least 6 characters ]",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (avatarImage == 0) {
                Toast.makeText(this, "You must choose an avatar", Toast.LENGTH_SHORT).show()
            } else {
                userNameFree(Username) { isFree ->
                    if (isFree) {
                        createUser(Username, password, email, avatarImage)
                    } else {
                        Toast.makeText(this, "Your username is taken :(", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun userNameFree(username: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val playersCollection = db.collection("players")
        playersCollection.whereEqualTo("username", username).get()
            .addOnSuccessListener { documents ->
                val isFree = documents.isEmpty // Check if no documents match the query
                callback(isFree)
            }.addOnFailureListener { exception ->
                Log.w("!!!", "Error getting documents: ", exception)
                callback(false)
            }
    }

    private fun createUser(userName: String, password: String, email: String, avatarImage: Int) {
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("!!!", "create success")
                    Log.d("!!!", "logged in user: ${auth.currentUser?.email}")
                    Log.d("!!!", "logged in user: ${auth.currentUser?.uid}")
                    Log.d("!!!", "logged in user: ${auth.currentUser?.uid.toString()}")
                    var uid = auth.currentUser?.uid ?: ""

                    val player = Player(
                        "", email, userName, uid.toString(), 0, 0, 0, avatarImage, 0, false, 0, ""
                    )
                    GlobalVariables.player = player
                    Log.d("!!!", "player.avatarimage=${player.avatarImage}")
                    db.collection("players").add(player).addOnSuccessListener { documentReference ->
                            Log.d("!!!", "DocumentSnapshot added with ID: ${documentReference.id}")
                        }.addOnFailureListener { e ->
                            Log.w("!!!", "Error adding document", e)
                        }
                    if (auth.currentUser?.email != null) {
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
                                            this, "Authentication succeeded.", Toast.LENGTH_SHORT
                                        ).show()

                                    } else {
                                        Toast.makeText(
                                            baseContext,
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        baseContext, "Authentication failed.", Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.d("!!!", "User not created ${task.exception}")
                    if (task.exception.toString() == "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.") {
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

        var selectedImageView: ImageView? = null

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
        for ((imageView, resId) in avatars) {
            imageView.setOnClickListener {
                Log.d("!!!", "inAvatarListener - Old avatarImage: ${newPlayer.avatarImage}")
                newPlayer.avatarImage = resId
                Log.d("!!!", "inAvatarListener - New avatarImage: ${newPlayer.avatarImage}")

                selectedImageView?.let { deselectAvatar(it) }
                selectedImageView = imageView

                selectAvatar(imageView)
                //Added an animation, so that user "knows" that the avatar is clicked
                val scaleAnimation = ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f)
                scaleAnimation.duration = 500
                imageView.startAnimation(scaleAnimation)

            }
        }
    }

    private fun deselectAvatar(imageView: ImageView) {
        imageView.background = null // Ta bort kanten genom att sätta bakgrund till null
    }

    fun selectAvatar(imageView: ImageView) {
        val borderColor = Color.BLUE // Byt ut Color.RED mot önskad färg
        val borderWidth = 5 // Bredden på kanten

        val border = GradientDrawable()
        border.setColor(Color.TRANSPARENT) // Bakgrundsfärgen
        border.setStroke(borderWidth, borderColor) // Kantfärgen och bredden

        imageView.background = border // Sätt kanten som bakgrund för imageView
    }
}