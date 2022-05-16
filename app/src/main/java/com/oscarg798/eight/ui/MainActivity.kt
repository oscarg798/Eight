package com.oscarg798.eight.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.flowlayout.FlowRow
import com.oscarg798.eight.R
import com.oscarg798.eight.model.LetterItem
import com.oscarg798.eight.ui.theme.EightTheme
import com.oscarg798.eight.ui.theme.dimensions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import com.oscarg798.eight.ui.LetterItem as LetterItem1

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: CrosswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EightTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val configuration = LocalConfiguration.current
                    val orientation = remember {
                        mutableStateOf(Configuration.ORIENTATION_PORTRAIT)
                    }
                    val state by viewModel.state.collectAsState(initial = CrosswordViewModel.State())

                    val modifier = if (orientation.value == Configuration.ORIENTATION_PORTRAIT) {
                        Modifier
                            .fillMaxSize()
                    } else {
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    }

                    LaunchedEffect(configuration) {
                        snapshotFlow { configuration.orientation }
                            .collect {
                                orientation.value = it
                            }
                    }

                    if (state.currentLetters != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = modifier
                        ) {
                            ConstraintLayout(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(MaterialTheme.dimensions.Large)
                                    .wrapContentHeight()
                            ) {
                                val (indicator, icon) = createRefs()
                                Text(
                                    text = "${state.currentWordIndex + 1}/${state.game?.items?.size ?: 0}",
                                    style = MaterialTheme.typography.h4,
                                    modifier = Modifier
                                        .constrainAs(indicator) {
                                            top.linkTo(parent.top)
                                            start.linkTo(parent.start)
                                        }
                                )

                                IconButton(
                                    onClick = { viewModel.onShufflePressed() },
                                    enabled = !state.hasWon,
                                    modifier = Modifier
                                        .constrainAs(icon) {
                                            top.linkTo(parent.top)
                                            end.linkTo(parent.end)
                                        }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_shuffle),
                                        contentDescription = null
                                    )
                                }

                            }

                            CrosswordLayout(
                                letters = state.currentLetters!!,
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .padding(MaterialTheme.dimensions.Large),
                                onLetterClick = {
                                    viewModel.onLetterSelected(it)
                                }
                            )

                            Text(
                                text = state.selectedWord,
                                style = MaterialTheme.typography.h2,
                                modifier = Modifier
                                    .padding(MaterialTheme.dimensions.Large)
                            )

                            if (state.hasWon) {
                                Text(
                                    text = state.winningDescription,
                                    style = MaterialTheme.typography.h3,
                                    modifier = Modifier
                                        .padding(MaterialTheme.dimensions.Large)
                                )
                            }

                            Button(
                                enabled = state.hasWon,
                                onClick = { viewModel.onNextClicked() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(MaterialTheme.dimensions.Large)
                            ) {
                                Text(text = getString(R.string.btn_label))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CrosswordLayout(
    letters: List<LetterItem>,
    modifier: Modifier,
    onLetterClick: (LetterItem) -> Unit
) {
    FlowRow(modifier) {
        letters.map {
            LetterItem1(
                letterItem = it,
                modifier = Modifier.padding(8.dp),
                onLetterClick = onLetterClick
            )
        }
    }
}

@Preview
@Composable
private fun X() {

    val letters = remember {
        mutableStateOf(
            listOf(
                LetterItem(letter = "a", indexInWord = 0, selected = false),
                LetterItem(letter = "b", indexInWord = 1, selected = false),
                LetterItem(letter = "c", indexInWord = 2, selected = false)
            )
        )
    }
    CrosswordLayout(
        letters.value, Modifier.width(150.dp)
    ) {
        val currentList = letters.value.toMutableList()
        val clickedItemLetter = currentList.indexOf(it)
        currentList.removeAt(clickedItemLetter)
        currentList.add(clickedItemLetter, it.copy(selected = !it.selected))
        letters.value = currentList
    }
}

@Composable
private fun LetterItem(
    letterItem: LetterItem,
    modifier: Modifier = Modifier,
    onLetterClick: (LetterItem) -> Unit
) {
    Card(
        border = BorderStroke(
            4.dp,
            if (letterItem.selected) MaterialTheme.colors.secondary else MaterialTheme.colors.surface
        ),
        modifier = modifier
            .wrapContentHeight()
            .clickable {
                onLetterClick(letterItem)
            }
    ) {
        Text(
            text = letterItem.letter.uppercase(),
            modifier = Modifier
                .padding(MaterialTheme.dimensions.Large)
        )
    }
}