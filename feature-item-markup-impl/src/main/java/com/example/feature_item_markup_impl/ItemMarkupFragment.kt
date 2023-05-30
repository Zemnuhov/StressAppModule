package com.example.feature_item_markup_impl

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feature_item_markup_impl.di.ItemMarkupComponent
import com.example.navigation_api.NavigationApi
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.himanshoe.charty.circle.CircleChart
import com.himanshoe.charty.circle.model.CircleData
import javax.inject.Inject
import dagger.Lazy
import com.example.values.R as values
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.himanshoe.charty.circle.config.CircleConfig
import com.himanshoe.charty.circle.config.StartPosition


class ItemMarkupFragment: Fragment(R.layout.fragment_item_markup) {

    companion object{
        const val BUNDLE_KEY = "VISIBILITY"
    }

    @Inject
    lateinit var navigation: NavigationApi

    @Inject
    lateinit var factory: Lazy<ItemMarkupViewModel.Factory>
    private val viewModel: ItemMarkupViewModel by viewModels {
        factory.get()
    }

    var isDrawingImage = true

    private val colors by lazy {
        listOf(
            ContextCompat.getColor(requireContext(), values.color.primary),
            ContextCompat.getColor(requireContext(), values.color.primary_dark),
            ContextCompat.getColor(requireContext(), values.color.primary_light),
            ContextCompat.getColor(requireContext(), values.color.secondary),
            ContextCompat.getColor(requireContext(), values.color.secondary_dark),
            ContextCompat.getColor(requireContext(), values.color.third_dark),
            ContextCompat.getColor(requireContext(), values.color.third)
        )
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        ItemMarkupComponent.get().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(arguments?.getString(BUNDLE_KEY) != null){
            isDrawingImage = false
        }
        return ComposeView(requireContext()).apply {
            setContent {
                DrawMarkupItem()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ItemMarkupComponent.clear()
    }

    @Composable
    fun DrawMarkupItem(){
        var data by remember {
            mutableStateOf(listOf(CircleData(0, 0F, Color(colors[0]))))
        }
        var maxValue by remember { mutableStateOf(0F) }

        viewModel.countForEachReason.observe(viewLifecycleOwner){
            val requireSizeList = it.list.sortedBy { it.count }.reversed().take(colors.size-1)
            maxValue = requireSizeList[0].count.toFloat()
            data = requireSizeList.mapIndexed { index, countForCause ->
                CircleData(
                    countForCause.cause.name,
                    countForCause.count.toFloat(),
                    Color(colors[index])
                )
            }.reversed()
        }
        Box(modifier = Modifier.fillMaxSize()){
            if (isDrawingImage){
                Image(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Подробности",
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).clickable { navigation.navigateMainToStatistic() }
                )
            }
            Row(
                modifier = Modifier.fillMaxSize()
            ){
                DrawDiagram(modifier = Modifier
                    .fillMaxWidth(0.5F)
                    .fillMaxHeight()
                    .padding(start = 16.dp),
                    data,
                    maxValue
                )
                DrawSourceList(data = data)
            }
        }

        
    }

    @Composable
    private fun DrawDiagram(modifier: Modifier, data: List<CircleData>, maxValue: Float){
        CircleChart(
            modifier = modifier,
            isAnimated = false,
            circleData = data,
            config = CircleConfig(StartPosition.Top, maxValue+200)
        )
    }

    @Composable
    private fun DrawSourceList(data: List<CircleData>){
        Box(modifier = Modifier.padding(start = 16.dp, end = 32.dp), contentAlignment = Alignment.CenterStart){
            Column( modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center){
                data.reversed().forEach{
                    Row( modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row{
                            Canvas(modifier = Modifier.size(16.dp), onDraw = { drawCircle(color = it.color!!) })
                            Text(text = it.xValue.toString(), modifier = Modifier.padding(start = 16.dp))
                        }
                        Text(text = it.yValue.toInt().toString(), modifier = Modifier.padding(start = 16.dp), fontWeight = FontWeight(600))
                    }
                    
                }
            }
        }

    }


}