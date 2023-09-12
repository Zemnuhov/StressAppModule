package com.example.feature_item_graph_impl

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.zemnuhov.testcompose.ChartSetting

data class Point(val x: Long, val y: Float)
data class GraphPoint(val x: Float, val y: Float)

private lateinit var values: SnapshotStateList<Point>

private lateinit var minY: MutableState<Float>
private lateinit var maxY: MutableState<Float>

fun Float.mapValueToDifferentRange(
    inMin: Float,
    inMax: Float,
    outMin: Float,
    outMax: Float
) = (this - inMin) * (outMax - outMin) / (inMax - inMin) + outMin

fun Long.mapValueToDifferentRange(
    inMin: Long,
    inMax: Long,
    outMin: Float,
    outMax: Float
) = (this - inMin) * (outMax - outMin) / (inMax - inMin) + outMin


fun addPoint(point: Point) {
    values.add(point)
}

fun setYDiapason(min: Float, max: Float){
    minY.value = min
    maxY.value = max
}

@Composable
fun LineChart(
    points: List<Point>,
    modifier: Modifier = Modifier.size(300.dp, 200.dp),
    setting: ChartSetting = ChartSetting(
        baseColor = Color.Black,
        minY = -50F,
        maxY = 50F
    ),
) {
    values = remember {
        points.toMutableStateList()
    }

    minY = remember {
        mutableStateOf(setting.minY)
    }

    maxY = remember {
        mutableStateOf(setting.maxY)
    }

    if (values.size > 0) {
        val maxXValue = values.maxOf { it.x }
        var scale by remember { mutableStateOf(1f) }
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }


        Canvas(
            modifier
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom
                        if (scale < 1) scale = 1F
                    }
                }
        ) {
            val pixelPoints = values.subList(
                (if ((values.size - setting.minPointsInScreen * scale) > 0) (values.size - setting.minPointsInScreen * scale).toInt() else 0),
                values.size - 1
            ).map {
                val x = it.x.mapValueToDifferentRange(
                    inMin = values[if ((values.size - setting.minPointsInScreen * scale) > 0) (values.size - setting.minPointsInScreen * scale).toInt() else 0].x,
                    inMax = maxXValue,
                    outMin = 0f,
                    outMax = size.width
                )
                val y = it.y.mapValueToDifferentRange(
                    inMin = minY.value,
                    inMax = maxY.value,
                    outMin = size.height,
                    outMax = 0f
                )
                GraphPoint(x, y)
            }
            Log.e("AAA", pixelPoints.toString())
            val basePath = Path()
            pixelPoints.forEachIndexed { index, point ->
                if (index == 0) {
                    basePath.moveTo(point.x, point.y)
                } else {
                    basePath.lineTo(point.x, point.y)
                }
            }

            drawPath(
                basePath,
                color = Color.Black,
                style = Stroke(width = 8f)
            )

            if(setting.threshold != null){
                val thresholdPath = Path()
                var previousIndex = -1
                val moveList = mutableListOf<GraphPoint>()
                val thresholdPoints = pixelPoints.filterIndexed() { index, value ->
                    if (previousIndex+1 != index){
                        moveList.add(value)
                    }
                    if(value.y<setting.threshold.mapValueToDifferentRange(
                            inMin = setting.minY,
                            inMax = setting.maxY,
                            outMin = size.height,
                            outMax = 0f)){
                        previousIndex = index
                    }

                    value.y<setting.threshold.mapValueToDifferentRange(
                    inMin = setting.minY,
                    inMax = setting.maxY,
                    outMin = size.height,
                    outMax = 0f) }

                thresholdPoints.forEachIndexed { index, point ->
                    if (index == 0 || point in moveList) {
                        thresholdPath.moveTo(point.x, point.y)
                    } else {
                        thresholdPath.lineTo(point.x, point.y)
                    }
                }
                drawPath(
                    thresholdPath,
                    color = setting.secondColor,
                    style = Stroke(width = 8f)
                )
            }
        }
    }
}