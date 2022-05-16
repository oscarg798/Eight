package com.oscarg798.eight.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.oscarg798.eight.model.CrosswordItem
import com.oscarg798.eight.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStreamReader
import java.lang.reflect.Type
import javax.inject.Inject

class CrosswordRepository @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    fun getCrosswordItems(): List<CrosswordItem> {
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<APICrosswordItem>>() {}.type
        val gameJson = context.resources.openRawResource(R.raw.game)
        return gson.fromJson<List<APICrosswordItem>>(InputStreamReader(gameJson), listType).map {
            CrosswordItem(it.word, it.winningDescription)
        }
    }

    data class APICrosswordItem(
        @SerializedName("word")
        val word: String,
        @SerializedName("winningDescription")
        val winningDescription: String
    )
}