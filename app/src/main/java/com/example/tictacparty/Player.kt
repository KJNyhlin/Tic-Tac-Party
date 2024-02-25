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
    var searchingOpponentStartTime : Long
)
{

    constructor() : this("", "", "",0, 0, 0, 0,
        0,  false, 0)
}

