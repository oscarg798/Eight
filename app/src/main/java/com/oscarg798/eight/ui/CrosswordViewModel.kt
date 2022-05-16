package com.oscarg798.eight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscarg798.eight.model.CrosswordGame
import com.oscarg798.eight.model.LetterItem
import com.oscarg798.eight.model.indexInWord
import com.oscarg798.eight.model.letter
import com.oscarg798.eight.usecase.GetCrosswordGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CrosswordViewModel @Inject constructor(
    private val getCrosswordGame: GetCrosswordGame
) : ViewModel() {

    private val _state = MutableStateFlow(State())

    val state: Flow<State> = _state

    private val mutex = Mutex()

    init {
        getGame()
    }

    private fun getGame() {
        viewModelScope.launch {

            val game = withContext(Dispatchers.IO) {
                getCrosswordGame()
            }

            updateState {
                it.copy(
                    winningDescription = game.items[it.currentWordIndex].winningDescription,
                    currentLetters = game.getLetterItems(it.currentWordIndex),
                    game = game
                )
            }
        }
    }

    private fun CrosswordGame.getLetterItems(
        index: Int
    ) = items[index].letters.map { letter ->
        LetterItem(
            letter = letter.letter(),
            indexInWord = letter.indexInWord(),
            selected = false
        )
    }

    fun onLetterSelected(letterItem: LetterItem) {
        viewModelScope.launch {
            var selectedWord = _state.value.selectedWord ?: ""
            val currentLetters = _state.value.currentLetters?.toMutableList()
                ?: throw IllegalStateException("Can not click a letter without letters")
            val clickedItemLetter = currentLetters.indexOf(letterItem)
            currentLetters.removeAt(clickedItemLetter)
            currentLetters.add(clickedItemLetter, letterItem.copy(selected = !letterItem.selected))

            if (letterItem.selected) {
                val charIndex = selectedWord.indexOf(letterItem.letter)
                selectedWord = selectedWord.toMutableList().apply {
                    removeAt(charIndex)
                }.joinToString("")
            } else {
                selectedWord += letterItem.letter
            }

            updateState {
                it.copy(
                    currentLetters = currentLetters,
                    selectedWord = selectedWord,
                    hasWon = selectedWord == (it.game?.items?.get(it.currentWordIndex)?.word
                        ?: throw IllegalStateException("A game must exists a this point"))
                )
            }
        }
    }

    fun onNextClicked() {
        viewModelScope.launch {
            val game =
                _state.value.game ?: throw IllegalStateException("A game must exists a this point")
            val newIndex = _state.value.currentWordIndex + 1
            if (newIndex < game.items.size) {
                updateState {
                    it.copy(
                        selectedWord = "",
                        winningDescription = game.items[newIndex].winningDescription,
                        currentLetters = game.getLetterItems(newIndex),
                        hasWon = false,
                        currentWordIndex = _state.value.currentWordIndex + 1
                    )
                }
            }
        }
    }

    fun onShufflePressed() {
        viewModelScope.launch {

            updateState {
                it.copy(
                    currentLetters = it.currentLetters?.shuffled(),
                )
            }
        }
    }

    private suspend fun updateState(reducer: (State) -> State) {
        mutex.withLock {
            _state.value = reducer(_state.value)
        }
    }

    data class State(
        val currentLetters: List<LetterItem>? = null,
        val winningDescription: String = "",
        val game: CrosswordGame? = null,
        val selectedWord: String = "",
        val currentWordIndex: Int = 0,
        val hasWon: Boolean = false
    )
}