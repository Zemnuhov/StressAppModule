package com.example.feature_item_graph_impl

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import com.example.feature_item_graph_impl.databinding.FragmentPhaseGraphItemBinding
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import com.neurotech.core_bluetooth_comunication_api.model.Phase
import com.neurotech.core_bluetooth_comunication_api.model.Tonic
import com.zemnuhov.testcompose.ChartSetting
import dagger.Lazy
import java.util.Date
import javax.inject.Inject
import com.example.values.R as values


class PhaseGraphFragment: Fragment(R.layout.fragment_phase_graph_item) {

    private val threshold: Double = 3.0

    @Inject
    lateinit var factory: Lazy<GraphViewModel.Factory>
    private val viewModel: GraphViewModel by viewModels {
        factory.get()
    }

    private var _binding: FragmentPhaseGraphItemBinding? = null
    private val binding get() = _binding!!

    private val maxPoint = 5_000

    private var phaseSeries = LineGraphSeries(emptyArray())
    private var excessSeries = PointsGraphSeries(emptyArray())

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ItemGraphComponent.get().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val isPhase = remember {
                    mutableStateOf(true)
                }
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)) {
                    if(isPhase.value){
                        LineChart(
                            listOf(),
                            setting = ChartSetting(
                                threshold = 3F,
                                minY = -30F,
                                maxY = 30F,
                                minPointsInScreen = 500
                            ),
                            modifier = Modifier.fillMaxSize())

                        val phase = viewModel.phaseValue.asFlow().collectAsState(initial = Phase(0.0, Date()))
                        addPoint(Point(phase.value.time.time, phase.value.value.toFloat()))
                    }else{
                        LineChart(
                            listOf(),
                            setting = ChartSetting(
                                minY = -30F,
                                maxY = 30F,
                                minPointsInScreen = 500
                            ),
                            modifier = Modifier.fillMaxSize())

                        val tonic = viewModel.tonicValue.asFlow().collectAsState(initial = Tonic(0, Date()))
                        setYDiapason(tonic.value.value.toFloat()-100F, tonic.value.value.toFloat()+100F)
                        addPoint(Point(tonic.value.time.time, tonic.value.value.toFloat()))
                    }
                    Image(
                        imageVector = Icons.Default.ArrowForward,
                        "aaa",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .clickable { isPhase.value = !isPhase.value }
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ItemGraphComponent.clear()
    }
}