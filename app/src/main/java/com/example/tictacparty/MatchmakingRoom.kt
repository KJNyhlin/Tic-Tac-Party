package com.example.tictacparty

import com.google.firebase.firestore.DocumentId

data class MatchmakingRoom(
    @DocumentId val roomId: String = "",
    val player1Id: String = "",
    val player2Id: String = "",
    val status: String = ""
)