package com.example.tictacparty

class Player(
    val email : String,
    val username : String,
    val userId : String,
    var wins : Int,
    var lost : Int,
    var draw : Int,
    var avatarImage : Int,
    var mmrScore : Int,
    var searchingOpponent : Boolean
) {

    constructor() : this("", "", "",0, 0, 0, 0,
        0, false)

}
