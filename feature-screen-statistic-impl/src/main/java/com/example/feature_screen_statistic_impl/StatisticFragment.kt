package com.example.feature_screen_statistic_impl

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cesarferreira.tempo.beginningOfDay
import com.cesarferreira.tempo.endOfDay
import com.cesarferreira.tempo.toString
import com.example.feature_item_markup_api.ItemMarkupApi
import com.example.feature_screen_statistic_impl.databinding.FragmentStatisticBinding
import com.example.feature_screen_statistic_impl.di.StatisticComponent
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.neurotech.utils.StressLogger.log
import dagger.Lazy
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

class StatisticFragment : Fragment() {

    @Inject
    lateinit var factory: Lazy<StatisticViewModel.Factory>

    @Inject
    lateinit var markupItemApi: Lazy<ItemMarkupApi>

    private val viewModel: StatisticViewModel by viewModels {
        factory.get()
    }

    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!

    private var barSeries = BarGraphSeries(arrayOf<DataPoint>())
    private var tonicSeries = LineGraphSeries(arrayOf<DataPoint>())
    private var resultDateList = listOf<Date>()

    private var adapter: StatisticAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        StatisticComponent.get().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val statisticItem = markupItemApi.get().getFragment()
        statisticItem.arguments = bundleOf(ItemMarkupApi.BUNDLE_KEY to "GONE")
        childFragmentManager.beginTransaction()
            .replace(binding.mainStatisticLayout.id, statisticItem)
            .commit()
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = StatisticAdapter()
        adapter?.normalValue = viewModel.user.phaseNormal
        binding.recyclerView.adapter = adapter
        barSeries.dataWidth = 550000.0
        setObservers()
        buttonListeners()
    }

    private fun setObservers() {
        viewModel.results.observe(viewLifecycleOwner) {
            binding.recyclerView.setItemViewCacheSize(it.size)

            val normal = when (viewModel.state) {
                1 -> viewModel.user.phaseNormal
                2 -> viewModel.user.phaseInHourNormal
                else -> viewModel.user.phaseInDayNormal
            }

            adapter?.normalValue = normal
            adapter?.submitList(it)

            resultDateList = it.map { it.time }
        }
        viewModel.results.observe(viewLifecycleOwner) { resultModel ->
            barSeries = BarGraphSeries(arrayOf<DataPoint>())
            tonicSeries = LineGraphSeries(arrayOf<DataPoint>())
            resultModel.sortedBy { result -> result.time }
                .forEach { result ->
                    val time = result.time
                    val peaks = result.peakCount.toDouble()
                    val bar = DataPoint(time, peaks)
                    barSeries.appendData(bar, true, 10000)
                    val tonic = result.tonicAvg.toDouble()
                    val point =
                        DataPoint(time, mapValue(tonic, resultModel.maxOf { it.peakCount } - 5))
                    tonicSeries.appendData(point, true, 10000)
                }
            runBlocking {
                graphSettings()
            }
        }

        viewModel.dateFlow.observe(viewLifecycleOwner) {
            binding.graphDate.text = it
        }
    }

    private fun buttonListeners() {
        CoroutineScope(Dispatchers.IO).launch {
            binding.switchButton.state.collect {
                log(it.toString())
                when (it) {
                    1 -> {
                        viewModel.setDayResults()
                        barSeries.dataWidth = 550000.0

                    }
                    2 -> {
                        viewModel.setWeekResults()
                        barSeries.dataWidth =
                            (barSeries.highestValueX - barSeries.lowestValueX) / 300
                    }
                    else -> {
                        viewModel.setMonthResults()
                        barSeries.dataWidth =
                            (barSeries.highestValueX - barSeries.lowestValueX) / 100
                    }
                }
            }
        }
        binding.leftButton.setOnClickListener { viewModel.goToPrevious() }
        binding.rightButton.setOnClickListener { viewModel.goToNext() }
    }

    private fun scrollToClick(timeLong: Long) {
        val position = resultDateList.binarySearch(Date(timeLong))
        CoroutineScope(Dispatchers.IO).launch {
            launch(Dispatchers.Main) {
                binding.recyclerView.smoothScrollToPosition(position)
            }
            delay(1000)
            launch(Dispatchers.Main) {
                binding.recyclerView.scrollToPosition(position)
            }
        }

    }

    private fun clickToData(xValue: Double) {
        val normal = when (viewModel.state) {
            1 -> viewModel.user.phaseNormal
            2 -> viewModel.user.phaseInHourNormal
            else -> viewModel.user.phaseInDayNormal
        }
        barSeries.setValueDependentColor { data: DataPoint ->
            if (data.x != xValue) {
                if (data.y < normal) {
                    return@setValueDependentColor ContextCompat.getColor(
                        requireContext(),
                        R.color.green_active
                    )
                }
                if (normal <= data.y && data.y <= (normal * 2)) {
                    return@setValueDependentColor ContextCompat
                        .getColor(requireContext(), R.color.yellow_active)
                }
                return@setValueDependentColor ContextCompat.getColor(
                    requireContext(),
                    R.color.red_active
                )
            }
            if (data.y < normal) {
                return@setValueDependentColor ContextCompat.getColor(
                    requireContext(),
                    R.color.green_selected
                )
            }
            if (normal <= data.y && data.y <= (normal * 2)) {
                return@setValueDependentColor ContextCompat
                    .getColor(requireContext(), R.color.yellow_selected)
            }
            return@setValueDependentColor ContextCompat.getColor(
                requireContext(),
                R.color.red_selected
            )
        }
        barSeries.setOnDataPointTapListener { _, dataPoint ->
            scrollToClick(dataPoint.x.toLong())
            runBlocking {
                clickToData(dataPoint.x)
            }
        }
        binding.statisticGraph.removeSeries(barSeries)
        binding.statisticGraph.addSeries(barSeries)
    }

    private fun mapValue(value: Double, min: Int): Double {
        return value / 10000 * (min + barSeries.highestValueY - min) + min
    }

    private fun graphSettings() {
        val minX = Date(barSeries.lowestValueX.toLong()).beginningOfDay.time.toDouble() - 500000
        val maxX = Date(barSeries.highestValueX.toLong()).endOfDay.time.toDouble() + 500000
        binding.statisticGraph.apply {
            removeAllSeries()
            addSeries(tonicSeries)
            addSeries(barSeries)
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.card_background))
            viewport.isXAxisBoundsManual = true
            viewport.isYAxisBoundsManual = true
            viewport.setMinX(minX)
            viewport.setMaxX(maxX)
            viewport.maxXAxisSize = maxX - minX
            viewport.setMinY(0.0)
            viewport.setMaxY(tonicSeries.highestValueY + 2)
            viewport.isScalable = true
            viewport.isScrollable = true
            viewport.setScalableY(false)
            viewport.setScrollableY(false)
            gridLabelRenderer.padding = 16
            gridLabelRenderer.gridColor =
                ContextCompat.getColor(requireContext(), R.color.graph_grid)
            gridLabelRenderer.isVerticalLabelsVisible = false
            gridLabelRenderer.numHorizontalLabels = 10
            gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
                override fun formatLabel(value: Double, isValueX: Boolean): String {
                    val date = Date(value.toLong())
                    return when (date.toString("mm").toInt() % 10) {
                        0 -> {
                            when (viewModel.period) {
                                Interval.DAY -> date.toString("HH:mm")
                                else -> date.toString("dd HH:mm")
                            }
                        }
                        else -> ""
                    }
                }
            }
        }
        barSeries.apply {
            barSeries.spacing = 20
            val normal = when (viewModel.state) {
                1 -> viewModel.user.phaseNormal
                2 -> viewModel.user.phaseInHourNormal
                else -> viewModel.user.phaseInDayNormal
            }
            setValueDependentColor { data: DataPoint ->
                if (data.y < normal) {
                    return@setValueDependentColor ContextCompat.getColor(
                        requireContext(),
                        R.color.green_active
                    )
                }
                if (normal <= data.y && data.y <= (normal * 2)) {
                    return@setValueDependentColor ContextCompat
                        .getColor(requireContext(), R.color.yellow_active)
                }
                ContextCompat.getColor(requireContext(), R.color.red_active)
            }

            setOnDataPointTapListener { _, dataPoint ->
                scrollToClick(dataPoint.x.toLong())
                runBlocking {
                    clickToData(dataPoint.x)
                }
            }
        }

        tonicSeries.color = Color.BLACK
        binding.statisticGraph.invalidate()
    }

    override fun onPause() {
        super.onPause()
        adapter?.keepMap?.forEach { (time, keep) ->
            viewModel.setKeepByTime(time, keep)
        }

    }
}