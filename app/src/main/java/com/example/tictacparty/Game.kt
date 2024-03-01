package com.example.tictacparty

import com.google.firebase.firestore.DocumentId

data class Game(
    @DocumentId val documentId : String = "",
    val playerOne: String="",
    val playerTwo: String="",
    var status: String="",
    var filledPos: MutableList<String> = mutableListOf("", "", "", "", "", "", "", "", "")
)
data class DavidsGame(
    @DocumentId val documentId : String = "",
    var filledPos: MutableList<String>?=null
)
{
}