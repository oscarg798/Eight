package com.oscarg798.eight.ui.theme

import android.annotation.SuppressLint
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@SuppressLint("ConflictingOnColor")
private val LightColorPalette = lightColors(
    primary = Color(0xFFbd0041),
    onPrimary = Color(0xFFffffff),
    primaryVariant = Color(0xFFffdade),
    secondary = Color(0xFF6b3ada),
    secondaryVariant = Color(0xFFeaddff),
    background = Color(0xFFfa6f5c),
    surface = Color(0xFFc785f2),
    error = Color(0xFFB3261E),
    onSecondary = Color(0xFFffffff),
    onBackground = Color(0xFFFFFBFE),
    onSurface = Color(0xFFFFFBFE)
)

@Composable
fun EightTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColorPalette,
        typography = getTypography(normalFontSizes),
        shapes = Shapes,
        content = content
    )
}

val MaterialTheme.dimensions
get() = object: Dimensions {
    override val Small: Dp
        get() = 4.dp
    override val Medium: Dp
        get() = 8.dp
    override val Large: Dp
        get() = 16.dp
}