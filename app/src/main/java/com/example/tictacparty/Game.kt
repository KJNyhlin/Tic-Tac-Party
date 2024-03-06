package com.example.tictacparty

import com.google.firebase.firestore.DocumentId

data class Game(
    @DocumentId val documentId: String = "",
    val playerOneId: String = "",
    val playerTwoId: String = "",
    var status: String = "",
    var filledPos: MutableList<String> = mutableListOf("", "", "", "", "", "", "", "", ""),
    var nextTurnPlayer: String = ""

) {
}