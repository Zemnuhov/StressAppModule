package com.example.feature_phase_info_impl

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.feature_phase_info_impl.databinding.FragmentPhaseInfoBinding
import com.example.feature_phase_info_impl.di.PhaseInfoComponent
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import javax.inject.Inject
import dagger.Lazy

class PhaseInfoFragment: Fragment(R.layout.fragment_phase_info) {

    @Inject
    lateinit var factory: Lazy<PhaseInfoViewModel.Factory>
    private val viewModel: PhaseInfoViewModel by viewModels { factory.get() }

    private var _binding: FragmentPhaseInfoBinding? = null
    private val binding get() = _binding!!

    private val timeInterval = arrayOf(Interval.TEN_MINUTE, Interval.HOUR, Interval.DAY)
    private var indexInterval = 0

    private var barSeries = BarGraphSeries(arrayOf<DataPoint>())

    override fun onAttach(context: Context) {
        super.onAttach(context)
        PhaseInfoComponent.get().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhaseInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setIntervalObserver()
    }

    private fun setIntervalObserver(){
        binding.timeRangePhase.text = timeInterval[indexInterval].string()
        binding.timeRangePhase.setOnClickListener {
            indexInterval++
            if (indexInterval == 3) {
                indexInterval = 0
            }
            binding.timeRangePhase.text = timeInterval[indexInterval].string()
            viewModel.setInterval(timeInterval[indexInterval])
        }
        viewModel.setInterval(timeInterval[indexInterval])
    }

    private fun setObservers() {
        viewModel.phaseCount.observe(viewLifecycleOwner) {
            binding.peaksCounter.text = it.toString()
        }

        viewModel.resultsInHour.observe(viewLifecycleOwner) { resultsTenMinute ->
            barSeries = BarGraphSeries(arrayOf<DataPoint>())
            resultsTenMinute.list.forEach {
                barSeries.appendData(
                    DataPoint(
                        it.time,
                        it.peakCount.toDouble()
                    ),
                    true,
                    6
                )
            }
            graphSetting()
        }
    }

    private fun graphSetting() {
        binding.peaksCounterGraph.apply {
            removeAllSeries()
            addSeries(barSeries)
            viewport.isXAxisBoundsManual = true
            viewport.isYAxisBoundsManual = true
            viewport.setMinX(barSeries.lowestValueX - 500000)
            viewport.setMaxX(barSeries.highestValueX + 500000)
            viewport.setMinY(0.0)
            viewport.setMaxY(barSeries.highestValueY + 2)
            viewport.isScalable = false
            viewport.isScrollable = false
            viewport.setScalableY(false)
            viewport.setScrollableY(false)
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.card_background))
            gridLabelRenderer.gridColor = Color.BLACK
            gridLabelRenderer.isVerticalLabelsVisible = false
            gridLabelRenderer.isHorizontalLabelsVisible = false
        }

        barSeries.spacing = 1
        barSeries.dataWidth = 500000.0
        val normal = viewModel.user.phaseNormal.toDouble()
        barSeries.setValueDependentColor { data: DataPoint ->
            if (data.y < normal) {
                return@setValueDependentColor ContextCompat.getColor(
                    requireContext(),
                    R.color.green_active
                )
            }
            if (data.y in normal..normal * 2) {
                return@setValueDependentColor ContextCompat
                    .getColor(requireContext(), R.color.yellow_active)
            }
            ContextCompat.getColor(requireContext(), R.color.red_active)
        }
        binding.peaksCounterGraph.invalidate()

    }

}

