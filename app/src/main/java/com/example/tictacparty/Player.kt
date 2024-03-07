package com.example.tictacparty

import com.google.firebase.firestore.DocumentId

class Player(
    @DocumentId val documentId: String = "",
    val email: String = "",
    val username: String = "",
    val userId: String = "",
    var wins: Int = 0,
    var lost: Int = 0,
    var gamesPlayed: Int = 0,
    var avatarImage: Int = 0,
    var mmrScore: Int = 0,
    var searchingOpponent: Boolean = false,
    var searchingOpponentStartTime: Long = 0,
    var symbol: String = ""
) {

    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "email" to email,
            "username" to username,
            "userId" to userId,
            "wins" to wins,
            "lost" to lost,
            "gamesPlayed" to gamesPlayed,
            "avatarImage" to avatarImage,
            "mmrScore" to mmrScore,
            "searchingOpponent" to searchingOpponent,
            "searchingOpponentStartTime" to searchingOpponentStartTime,
            "symbol" to symbol
        )
    }

    fun copy(
        documentId: String = this.documentId,
        email: String = this.email,
        username: String = this.username,
        userId: String = this.userId,
        wins: Int = this.wins,
        lost: Int = this.lost,
        gamesPlayed: Int = this.gamesPlayed,
        avatarImage: Int = this.avatarImage,
        mmrScore: Int = this.mmrScore,
        searchingOpponent: Boolean = this.searchingOpponent,
        searchingOpponentStartTime: Long = this.searchingOpponentStartTime,
        symbol: String = this.symbol
    ): Player {
        return Player(
            documentId,
            email,
            username,
            userId,
            wins,
            lost,
            gamesPlayed,
            avatarImage,
            mmrScore,
            searchingOpponent,
            searchingOpponentStartTime,
            symbol
        )
    }
}