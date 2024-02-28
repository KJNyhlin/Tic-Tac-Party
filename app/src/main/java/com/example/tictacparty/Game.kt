package com.example.tictacparty

data class Game(
    val id: Int,
    val playerOne: Player,
    val playerTwo: Player,
    var nextMove: Int,
    var status: String,
    var filledPos: MutableList<String> = mutableListOf("", "", "", "", "", "", "", "", "")
) {
}