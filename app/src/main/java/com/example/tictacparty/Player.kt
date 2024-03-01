package com.example.tictacparty

import com.google.firebase.firestore.DocumentId

class Player(
    @DocumentId val documentId : String = "",
    val email : String = "",
    val username : String = "",
    val userId : String = "",
    var wins : Int = 0,
    var lost : Int = 0,
    var gamesPlayed : Int = 0,
    var avatarImage : Int = 0,
    var mmrScore : Int = 0,
    var searchingOpponent : Boolean = false,
    var searchingOpponentStartTime : Long = 0,
    var symbol : String = ""
)
{

    //constructor() : this("", "", "",0, 0, 0, 0,
    //    0,  false, 0, "")
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
}