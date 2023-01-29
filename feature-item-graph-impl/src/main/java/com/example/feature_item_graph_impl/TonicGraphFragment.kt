package com.example.feature_item_graph_impl

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.feature_item_graph_impl.databinding.FragmentTonicGraphItemBinding
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Provider

class TonicGraphFragment: Fragment(R.layout.fragment_tonic_graph_item) {

    @Inject
    lateinit var factory: Lazy<GraphViewModel.Factory>
    private val viewModel: GraphViewModel by viewModels {
        factory.get()
    }

    private var _binding: FragmentTonicGraphItemBinding? = null
    private val binding get() = _binding!!

    private val maxPoint = 5_000

    private var tonicSeries = LineGraphSeries(emptyArray())

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ItemGraphComponent.get().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTonicGraphItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingGraph()
        setObservers()
        binding.tonicSwapButton.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(binding.tonicGraphHost.id, PhaseGraphFragment()).commit()
        }
    }

    private fun setObservers() {
        viewModel.tonicValue.observe(viewLifecycleOwner) {
            val x = it.time
            val y = it.value.toDouble()
            val point = DataPoint(x, y)
            if(tonicSeries.highestValueX < x.time) {
                tonicSeries.appendData(point, true, maxPoint)
                binding.tonicGraphMain.viewport.setMinY(y - 1000)
                binding.tonicGraphMain.viewport.setMaxY(y + 1000)
            }
        }
    }

    private fun settingGraph() {
        with(binding.tonicGraphMain){
            removeAllSeries()
            addSeries(tonicSeries)
            viewport.isYAxisBoundsManual = true
            viewport.isXAxisBoundsManual = false
            viewport.setMinX(0.0)
            viewport.setMaxX(30000.0)
            viewport.isScalable = true
            viewport.isScrollable = true
            viewport.setScalableY(false)
            viewport.setScrollableY(false)
            setBackgroundColor(Color.WHITE)
            gridLabelRenderer.gridColor = Color.WHITE
            gridLabelRenderer.isHorizontalLabelsVisible = false
            gridLabelRenderer.isVerticalLabelsVisible = false
            gridLabelRenderer.setHumanRounding(false)
            gridLabelRenderer.labelFormatter =
                DateAsXAxisLabelFormatter(activity)
            gridLabelRenderer.numHorizontalLabels = 3
        }
        tonicSeries.color = Color.BLACK
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ItemGraphComponent.clear()
    }

}