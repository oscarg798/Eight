package com.oscarg798.eight.model

class CrosswordGame(val items: List<CrosswordItem>) {

    data class CrosswordItem(
        val word: String,
        val letters: List<Pair<Int, String>>,
        val winningDescription: String
    )
}

fun Pair<Int, String>.indexInWord() = first
fun Pair<Int, String>.letter() = second