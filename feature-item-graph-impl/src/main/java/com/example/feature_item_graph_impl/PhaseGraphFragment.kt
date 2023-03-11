package com.example.feature_item_graph_impl

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.feature_item_graph_impl.databinding.FragmentPhaseGraphItemBinding
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import dagger.Lazy
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
        _binding = FragmentPhaseGraphItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingGraph()
        setObservers()
        binding.phaseSwapButton.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(binding.phaseGraphHost.id, TonicGraphFragment()).commit()
        }
    }

    private fun setObservers() {
        viewModel.phaseValue.observe(viewLifecycleOwner) {
            val x = it.time
            val y = it.value
            val point = DataPoint(x, y)
            if(phaseSeries.highestValueX < x.time && excessSeries.highestValueX < x.time) {
                if (y > threshold) {
                    excessSeries.appendData(point, true, maxPoint)
                }
                phaseSeries.appendData(point, true, maxPoint)

            }
        }
    }

    private fun settingGraph() {
        with(binding.phaseGraphMain){
            removeAllSeries()
            addSeries(phaseSeries)
            addSeries(excessSeries)
            viewport.isYAxisBoundsManual = true
            viewport.isXAxisBoundsManual = false
            viewport.setMinY(-30.0)
            viewport.setMaxY(30.0)
            viewport.setMinX(0.0)
            viewport.setMaxX(30000.0)
            viewport.isScalable = true
            viewport.isScrollable = true
            viewport.setScalableY(false)
            viewport.setScrollableY(false)
            setBackgroundColor(ContextCompat.getColor(requireContext(), values.color.card_background))
            gridLabelRenderer.gridColor = ContextCompat.getColor(requireContext(), values.color.card_background)
            gridLabelRenderer.isHorizontalLabelsVisible = false
            gridLabelRenderer.isVerticalLabelsVisible = false
            gridLabelRenderer.setHumanRounding(false)
            gridLabelRenderer.labelFormatter =
                DateAsXAxisLabelFormatter(activity)
            gridLabelRenderer.numHorizontalLabels = 3
        }
        excessSeries.color = Color.RED
        excessSeries.size = 3f
        phaseSeries.color = Color.BLACK
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ItemGraphComponent.clear()
    }
}