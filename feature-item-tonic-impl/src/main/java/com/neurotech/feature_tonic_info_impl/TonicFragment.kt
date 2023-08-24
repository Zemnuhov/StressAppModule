package com.neurotech.feature_tonic_info_impl

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import com.example.values.AppColors
import com.example.values.Dimens
import com.neurotech.feature_tonic_info_impl.di.TonicInfoComponent
import dagger.Lazy
import javax.inject.Inject

class TonicFragment : Fragment(R.layout.fragment_tonic) {

    @Inject
    internal lateinit var factory: Lazy<TonicViewModel.Factory>

    @Inject
    lateinit var dimens: Dimens

    private val viewModel: TonicViewModel by viewModels {
        factory.get()
    }

    private val timeInterval = arrayOf(Interval.TEN_MINUTE, Interval.HOUR, Interval.DAY)
    private var indexInterval = 0
    private val rectCount = 9

    override fun onAttach(context: Context) {
        super.onAttach(context)
        TonicInfoComponent.get().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DrawTonicItem()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        TonicInfoComponent.clear()
    }

    @Composable
    private fun DrawTonicItem() {
        viewModel.setInterval(timeInterval[indexInterval])
        val currentTonic = viewModel.tonicValue.asFlow().collectAsState(initial = 0)
        val averageTonic = viewModel.avgTonic.asFlow().collectAsState(initial = 0)
        var intervalText by remember {
            mutableStateOf(timeInterval[indexInterval].string())
        }
        Box {
            Row(modifier = Modifier.fillMaxSize()) {
                DrawScale(
                    Modifier
                        .fillMaxWidth(0.5F)
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically)
                        .padding(start = dimens.indent, top = dimens.indent, bottom = dimens.indent),
                    currentTonic.value
                )
                DrawInfo(currentTonic = currentTonic.value, averageTonic = averageTonic.value)

            }
            Text(
                text = intervalText,
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(dimens.indent)
                    .clickable {
                        indexInterval++
                        if (indexInterval == 3) {
                            indexInterval = 0
                        }
                        intervalText = timeInterval[indexInterval].string()
                        viewModel.setInterval(timeInterval[indexInterval])
                    },
                fontSize = dimens.textSize
            )
        }

    }

    @Composable
    private fun DrawInfo(currentTonic: Int, averageTonic: Int) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 16.dp, bottom = 16.dp), verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(
                    text = "Текущее\nзначение",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = dimens.smallTextSize
                )
                Text(
                    text = currentTonic.toString(),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight(600),
                    fontSize = dimens.textSize
                )
            }

            Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(
                    text = "Среднее\nзначение",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = dimens.smallTextSize
                )
                Text(
                    text = averageTonic.toString(),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight(600),
                    fontSize = dimens.textSize
                )
            }
        }

    }

    @Composable
    private fun DrawScaleRect(
        currentValue: Int,
        width: Dp,
        height:Dp,
        activateValue: Int,
        activeColor: Color,
        notActiveColor: Color){
        Canvas(
            modifier = Modifier
                .width(width)
                .height((height - dimens.scaleIndent * rectCount) / rectCount)
                .padding(dimens.scaleIndent),
            onDraw = {
                drawRoundRect(
                    if (currentValue in activateValue..10000) activeColor else notActiveColor,
                    cornerRadius = CornerRadius(12f, 12f)
                )
            }
        )
    }

    @Composable
    private fun DrawScale(modifier: Modifier, currentTonic: Int) {
        var height by remember {
            mutableStateOf(0.dp)
        }
        var width by remember {
            mutableStateOf(0.dp)
        }
        val density = LocalDensity.current
        Column(
            modifier = modifier.onGloballyPositioned { coordinates ->
                height = with(density) { coordinates.size.height.toDp() }
                width = with(density) { coordinates.size.width.toDp() }
            },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DrawScaleRect(currentTonic, width, height, 9000, AppColors.redActive, AppColors.redNotActive)
            DrawScaleRect(currentTonic, width*0.9F, height, 8000, AppColors.redActive, AppColors.redNotActive)
            DrawScaleRect(currentTonic, width*0.8F, height, 7000, AppColors.redActive, AppColors.redNotActive)
            DrawScaleRect(currentTonic, width*0.7F, height, 6000, AppColors.yellowActive, AppColors.yellowNotActive)
            DrawScaleRect(currentTonic, width*0.6F, height, 5000, AppColors.yellowActive, AppColors.yellowNotActive)
            DrawScaleRect(currentTonic, width*0.5F, height, 4000, AppColors.yellowActive, AppColors.yellowNotActive)
            DrawScaleRect(currentTonic, width*0.5F, height, 3000, AppColors.greenActive, AppColors.greenNotActive)
            DrawScaleRect(currentTonic, width*0.5F, height, 2000, AppColors.greenActive, AppColors.greenNotActive)
            DrawScaleRect(currentTonic, width*0.5F, height, 1000, AppColors.greenActive, AppColors.greenNotActive)
        }
    }
}