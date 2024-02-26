package com.example.tictacparty

class Player(
    val email : String,
    val username : String,
    val userId : String,
    var wins : Int,
    var lost : Int,
    var gamesPlayed : Int,
    var avatarImage : Int,
    var mmrScore : Int,
    var searchingOpponent : Boolean,
    var searchingOpponentStartTime : Long,
    var symbol : String
)
{

    constructor() : this("", "", "",0, 0, 0, 0,
        0,  false, 0, "")
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


