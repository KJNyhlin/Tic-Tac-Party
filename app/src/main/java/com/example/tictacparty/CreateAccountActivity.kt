package com.example.tictacparty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class CreateAccountActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val editName = findViewById<EditText>(R.id.editTextText)
        val editPassword = findViewById<EditText>(R.id.editTextTextPassword)
        val editPasswordConfirm = findViewById<EditText>(R.id.editTextTextPassword2)
        val editMail = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        val createButton = findViewById<Button>(R.id.Create)

        createButton.setOnClickListener {
            val password = editPassword.text.toString()
            val passwordConfirm = editPasswordConfirm.text.toString()
            if (password == passwordConfirm) {
                // Create Account
            } else {
                // Lösenorden matchar inte. 😞 Visa ett felmeddelande till användaren.
                Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show()
            }
        }

    }
}