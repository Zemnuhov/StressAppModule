package com.zemnuhov.testcompose

import androidx.compose.ui.graphics.Color

data class ChartSetting(
    val baseColor: Color = Color.Black,
    val minY: Float = -100f,
    val maxY: Float = 100f,
    val minPointsInScreen: Int = 50,
    val threshold: Float? = null,
    val secondColor: Color = Color.Red
)