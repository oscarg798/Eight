package com.oscarg798.eight.usecase

import com.oscarg798.eight.model.CrosswordGame
import com.oscarg798.eight.repository.CrosswordRepository
import javax.inject.Inject

class GetCrosswordGame @Inject constructor(
    private val crosswordRepository: CrosswordRepository
){

    operator fun invoke() = CrosswordGame(
        crosswordRepository.getCrosswordItems().map {
            CrosswordGame.CrosswordItem(
                word = it.word,
                letters = it.word.mapIndexed() { index, char ->
                    Pair(index, char.toString())
                }.shuffled(),
                it.winningDescription
            )
        }
    )
}