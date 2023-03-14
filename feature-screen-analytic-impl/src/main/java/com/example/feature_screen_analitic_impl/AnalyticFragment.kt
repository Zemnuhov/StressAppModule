package com.example.feature_screen_analitic_impl

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cesarferreira.tempo.*
import com.example.feature_screen_analitic_impl.databinding.FragmentAnalyticBinding
import com.example.feature_screen_analitic_impl.di.AnalyticComponent
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.neurotech.core_database_api.model.CountForEachCause
import com.neurotech.core_database_api.model.ResultsDay
import javax.inject.Inject
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.values.R as values

class AnalyticFragment : Fragment() {

    @Inject
    lateinit var factory: Lazy<AnalyticViewModel.Factory>
    val viewModel: AnalyticViewModel by viewModels {
        factory.get()
    }

    private var _binding: FragmentAnalyticBinding? = null
    private val binding get() = _binding!!

    private val appColors by lazy {
        mutableListOf<Int>().apply {
            add(ContextCompat.getColor(requireContext(), values.color.green_active))
            add(ContextCompat.getColor(requireContext(), values.color.yellow_active))
            add(ContextCompat.getColor(requireContext(), values.color.red_active))
        }
    }

    private var ratingSeries = LineGraphSeries(emptyArray())
    private var correctedRatingSeries = LineGraphSeries(emptyArray())

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AnalyticComponent.get().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setUpCalendar()
        binding.previousMonth.setOnClickListener {
            viewModel.previousMonth()
        }
        binding.nextMonth.setOnClickListener {
            viewModel.nextMonth()
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(500)
            launch(Dispatchers.Main) {
                binding.calendarView.scrollToDate(Tempo.now)
            }
        }
    }

    private fun observeData() {
        viewModel.setInterval(
            Tempo.now.beginningOfDay,
            Tempo.now.endOfDay
        )
        viewModel.resultsInInterval.observe(viewLifecycleOwner) {
            settingIntervalGraph(getIntervalData(it))
        }
        viewModel.resultsInMonth.observe(viewLifecycleOwner) {
            settingMonthGraph(getMonthData(it))
        }
        viewModel.dayRatingList.observe(viewLifecycleOwner) {
            ratingSeries = LineGraphSeries()
            it.forEachIndexed { index, d ->
                ratingSeries.appendData(DataPoint(index + 1.toDouble(), d), true, 5)
            }
            settingRatingGraph()
        }

        viewModel.correctedDayRatingList.observe(viewLifecycleOwner) {
            correctedRatingSeries = LineGraphSeries()
            it.forEachIndexed { index, d ->
                correctedRatingSeries.appendData(DataPoint(index + 1.toDouble(), d), true, 5)
            }
            settingRatingGraph()
        }

        viewModel.userRating.observe(viewLifecycleOwner) {
            binding.ratingTextView.text = it.toString()
            binding.ratingTextView.setTextColor(
                if (it < 2) {

                    ContextCompat.getColor(
                        requireContext(),
                        values.color.green_active
                    )

                } else if (it in 2..4) {
                    ContextCompat.getColor(
                        requireContext(),
                        values.color.yellow_active
                    )
                } else {
                    ContextCompat.getColor(
                        requireContext(),
                        values.color.red_active
                    )
                }
            )
        }

    }

    private fun setUpCalendar() {
        val startCalendar = Tempo.now - 1.year
        val endOfCalendar = Tempo.now
        binding.calendarView.apply {
            setRangeDate(startCalendar.beginningOfDay, endOfCalendar.endOfDay)
            setSelectionDate(Tempo.now)
        }
        binding.calendarView.setOnRangeSelectedListener { startDate, endDate, _, _ ->
            viewModel.setInterval(
                startDate.beginningOfDay,
                endDate.endOfDay
            )
        }
        binding.calendarView.setOnStartSelectedListener { startDate, _ ->
            viewModel.setInterval(
                startDate.beginningOfDay,
                startDate.endOfDay
            )
        }
        binding.calendarView.invalidate()
    }

    private fun getIntervalData(intervalEntity: CountForEachCause): BarData {
        val dataSet = BarDataSet(arrayListOf(), "")
        val yValueList = mutableListOf<Float>()
        val sourceMap = mutableMapOf<Float, String>()
        val colorMap = mutableMapOf<Float, Int>()
        var id = 0.01F
        intervalEntity.list.forEachIndexed { index, entity ->
            val y = entity.count.toFloat()
            val x = index.toFloat()
            dataSet.addEntry(BarEntry(x, y))
            yValueList.add(y)
            sourceMap[x] = entity.cause.name
            id += 0.1F
        }

        yValueList.sortedBy { it }.reversed().forEachIndexed { index, fl ->
            when (index) {
                0 -> colorMap[fl] = appColors[2]
                1 -> colorMap[fl] = appColors[1]
                else -> colorMap[fl] = appColors[0]
            }
        }

        dataSet.apply {
            colors = mutableListOf<Int?>().apply {
                yValueList.forEach {
                    add(colorMap[it])
                }
            }
            valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value) {
                        0.0F -> ""
                        else -> "${value.toInt()}"
                    }
                }
            }
        }
        binding.rangeGraph.xAxis.labelCount = intervalEntity.list.size
        binding.rangeGraph.xAxis.valueFormatter = object : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return sourceMap[value] ?: ""
            }
        }
        return BarData(dataSet)
    }

    private fun settingIntervalGraph(data: BarData) {
        binding.rangeGraph.apply {
            setFitBars(true)
            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawAxisLine(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textSize = 7F
            xAxis.labelRotationAngle = 45f
            viewPortHandler.setMinMaxScaleX(1F, 1F)
            viewPortHandler.setMinMaxScaleY(1F, 1F)
            legend.isEnabled = false
            description = Description().apply { text = "" }
            setData(data)
            animateY(500)
            invalidate()
        }
    }

    private fun getMonthData(resultEntityList: ResultsDay): BarData {
        val barEntryList = mutableListOf<BarEntry>()
        val sourceMap = mutableMapOf<Float, String>()
        val yValueList = mutableListOf<Float>()
        var id = 0.01f
        resultEntityList.list.sortedBy { it.date }.forEach {
            val x = it.date.toString("dd").toFloat()
            val y = if (resultEntityList.list.any { it.peaks > 0 }) {
                it.peaks + id
            } else {
                it.peaks.toFloat()
            }
            barEntryList.add(BarEntry(x, y))
            sourceMap[y] = it.stressCause ?: ""
            yValueList.add(y)
            id += 0.01f
        }
        val dataSet = BarDataSet(barEntryList, "")

        dataSet.apply {
            valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    if (value.toInt() == 0) return ""
                    return sourceMap[value] ?: ""
                }
            }
            colors = mutableListOf<Int>().apply {
                yValueList.forEach { yValue ->
                    if (yValue < viewModel.user.phaseInDayNormal) {
                        add(appColors[0])
                    } else if (yValue > viewModel.user.phaseInDayNormal && yValue < viewModel.user.phaseInDayNormal * 2) {
                        add(appColors[1])
                    } else {
                        add(appColors[2])
                    }
                }
            }
        }
        binding.staticGraphMP.xAxis.labelCount = resultEntityList.list.size
        return BarData(dataSet)
    }

    private fun settingMonthGraph(data: BarData) {
        binding.staticGraphMP.apply {
            setFitBars(true)
            setData(data)
            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawAxisLine(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.labelRotationAngle = 45f
            xAxis.textSize = 7F
            data.barWidth = 0.7F
            xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}"
                }
            }
            viewPortHandler.setMinMaxScaleX(1F, 1F)
            viewPortHandler.setMinMaxScaleY(1F, 1F)
            legend.isEnabled = false
            description = Description().apply { text = "" }
            animateY(500)
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e != null) {
                        val day = viewModel.monthDate.with(day = e.x.toInt())
                        if (day < Tempo.now.endOfDay) {
                            binding.calendarView.scrollToDate(day)
                            binding.calendarView.setSelectionDate(day)
                            binding.calendarView.setSelectionDate(day)
                        }
                    }
                }

                override fun onNothingSelected() {

                }

            })
        }
    }

    private fun settingRatingGraph() {
        binding.ratingGraph.apply {
            removeAllSeries()
            addSeries(correctedRatingSeries)
            correctedRatingSeries.color = Color.GREEN
            addSeries(ratingSeries)
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    values.color.card_background
                )
            )
            viewport.isXAxisBoundsManual = true
            viewport.isYAxisBoundsManual = true
            viewport.setMinX(1.0)
            viewport.setMaxX(5.0)
            viewport.setMinY(0.0)
            viewport.setMaxY(5.0)
            viewport.isScalable = true
            viewport.isScrollable = true
            viewport.setScalableY(false)
            viewport.setScrollableY(false)
            gridLabelRenderer.padding = 16
            gridLabelRenderer.gridColor =
                ContextCompat.getColor(requireContext(), values.color.graph_grid)
            gridLabelRenderer.isVerticalLabelsVisible = false
            gridLabelRenderer.numHorizontalLabels = 10
            gridLabelRenderer.isHorizontalLabelsVisible = false
        }
        ratingSeries.color = Color.BLACK
        binding.ratingGraph.invalidate()
    }


    override fun onStop() {
        super.onStop()
        binding.calendarView.removeAllViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        AnalyticComponent.clear()
    }

}