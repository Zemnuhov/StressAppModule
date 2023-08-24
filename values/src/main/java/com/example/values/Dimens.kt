package com.example.values

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt


class Dimens(size: ScreenSize) {
    val indent = (size.width.toFloat() * (16F/460F)).toInt().dp
    val largeIndent = (size.width.toFloat() * (24F/460F)).toInt().dp
    val smallIndent = (size.width.toFloat() * (8F/460F)).toInt().dp
    val halfSmallIndent = (size.width.toFloat() * (4F/460F)).toInt().dp
    val textSize = (size.width.toFloat() * (16F/460F)).toInt().sp
    val smallTextSize = (size.width.toFloat() * (14F/460F)).toInt().sp
    val largeTextSize = (size.width.toFloat() * (30F/460F)).toInt().sp
    val scaleIndent = (size.width.toFloat() * (2F/460F)).toInt().dp
}