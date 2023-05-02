package com.example.feature_screen_markup_impl

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cesarferreira.tempo.beginningOfDay
import com.cesarferreira.tempo.endOfDay
import com.cesarferreira.tempo.toString
import com.example.feature_screen_markup_impl.adapter.CauseAdapter
import com.example.feature_screen_markup_impl.databinding.FragmentMarkupBinding
import com.example.feature_screen_markup_impl.di.MarkupComponent
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.neurotech.core_database_api.model.Cause
import com.neurotech.core_database_api.model.ResultTenMinute
import com.neurotech.core_database_api.model.ResultsTenMinute
import com.neurotech.utils.StressLogger.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import javax.inject.Provider
import com.example.values.R as values

class MarkupFragment : Fragment(R.layout.fragment_markup),
    CauseAdapter.CauseCallback {
    @Inject
    lateinit var factory: Provider<MarkupViewModel.Factory>
    private val viewModel: MarkupViewModel by viewModels {
        factory.get()
    }
    private var _binding: FragmentMarkupBinding? = null
    private val binding get() = _binding!!
    private var causeAdapter: CauseAdapter? = null
    private var barSeries = BarGraphSeries(arrayOf<DataPoint>())
    private var tonicSeries = LineGraphSeries(arrayOf<DataPoint>())

    private val interval = mutableMapOf<String, Date?>("begin" to null, "end" to null)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MarkupComponent.get().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sourceRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        causeAdapter = CauseAdapter()
        causeAdapter?.callback = this
        binding.sourceRecyclerView.adapter = causeAdapter

        viewModel.causes.observe(viewLifecycleOwner) {
            causeAdapter?.submitList(it.values)
        }

        binding.goToPreviousDay.setOnClickListener {
            viewModel.goToPrevious()
        }

        binding.goToNextDay.setOnClickListener {
            viewModel.goToNext()
        }

        viewModel.resultForMarkup.observe(viewLifecycleOwner){}

        viewModel.dateFlow.observe(viewLifecycleOwner){
            binding.dateText.text = it
        }


        viewModel.results.observe(viewLifecycleOwner) {
            barSeries = BarGraphSeries(
                mutableListOf<DataPoint>().apply {
                    it.value.sortedBy { it.time }.forEach {
                        add(DataPoint(it.time, it.peakCount.toDouble()))
                    }
                }.toTypedArray()
            )
            tonicSeries = LineGraphSeries(
                mutableListOf<DataPoint>().apply {
                    it.value.sortedBy { it.time }.forEach {
                        add(
                            DataPoint(
                                it.time, mapValue(
                                    it.tonicAvg.toDouble(),
                                    0.0,
                                    10000.0,
                                    50.0,
                                    50.0 * 2
                                )
                            )
                        )
                    }
                }.toTypedArray()
            )
            CoroutineScope(Dispatchers.IO).launch {
                while (viewModel.resultForMarkup.value == null) {
                    delay(50)
                }
                launch(Dispatchers.Main) {
                    graphSettings()
                }
            }

        }

        intervalListener()

    }

    private fun intervalListener() {
        with(binding.beginTime) {
            text = if (interval["begin"] == null) {
                "Не выбрано"
            } else {
                interval["begin"]!!.toString("HH:mm")
            }
        }
        with(binding.endTime) {
            text = if (interval["end"] == null) {
                "Не выбрано"
            } else {
                interval["end"]!!.toString("HH:mm")
            }
        }
    }

    private fun mapValue(
        value: Double,
        oldBegin: Double,
        oldEnd: Double,
        newBegin: Double,
        newEnd: Double
    ): Double {
        return (value - oldBegin) / (oldEnd - oldBegin) * (newEnd - newBegin) + newBegin
    }


    override fun clickCause(cause: Cause) {
        if (interval["begin"] == null) {
            Toast.makeText(requireContext(), "Ничего не выбрано", Toast.LENGTH_SHORT).show()
        }
        if (interval["begin"] != null && interval["end"] == null) {
            viewModel.saveMarkups(
                ResultsTenMinute(
                    mutableListOf<ResultTenMinute>().apply {
                        viewModel.resultForMarkup.value!!.list.forEach {
                            if (it.time == interval["begin"]) {
                                add(
                                    ResultTenMinute(
                                        it.time,
                                        it.peakCount,
                                        it.tonicAvg,
                                        it.conditionAssessment,
                                        cause.name,
                                        it.keep
                                    )
                                )
                            }
                        }
                    }
                )
            )
        }
        if (interval["begin"] != null && interval["end"] != null) {
            viewModel.saveMarkups(
                ResultsTenMinute(
                    mutableListOf<ResultTenMinute>().apply {
                        viewModel.resultForMarkup.value!!.list.forEach {
                            if (it.time in interval["begin"]!!..interval["end"]!!) {
                                add(
                                    ResultTenMinute(
                                        it.time,
                                        it.peakCount,
                                        it.tonicAvg,
                                        it.conditionAssessment,
                                        cause.name,
                                        it.keep
                                    )
                                )
                            }
                        }
                    }
                )
            )
        }
        interval["begin"] = null
        interval["end"] = null
        intervalListener()
    }

    private fun graphSettings() {
        val minX = Date(barSeries.lowestValueX.toLong()).beginningOfDay.time.toDouble() - 500000
        val maxX = Date(barSeries.highestValueX.toLong()).endOfDay.time.toDouble() + 500000
        binding.markupGraph.apply {
            removeAllSeries()
            addSeries(tonicSeries)
            addSeries(barSeries)
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    values.color.card_background
                )
            )
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
                ContextCompat.getColor(requireContext(), values.color.graph_grid)
            gridLabelRenderer.isVerticalLabelsVisible = false
            gridLabelRenderer.numHorizontalLabels = 10
            gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
                override fun formatLabel(value: Double, isValueX: Boolean): String {
                    val date = Date(value.toLong())
                    return when (date.toString("mm").toInt() % 10) {
                        0 -> date.toString("HH:mm")
                        else -> ""
                    }
                }
            }
        }
        barSeries.spacing = 20
        setBarGraphColor()
        setOnBarDataClickListener()

        tonicSeries.color = Color.BLACK
        binding.markupGraph.invalidate()
    }

    private fun setBarGraphColor() {
        barSeries.apply {
            val normal = viewModel.user.phaseNormal
            log(normal.toString())
            setValueDependentColor { data: DataPoint ->
                val notMarkup = viewModel.resultForMarkup.value!!.list.map {
                    DataPoint(
                        it.time,
                        it.peakCount.toDouble()
                    )
                }
                if (data.x in notMarkup.map { it.x }) {
                    if (data.y < normal) {
                        return@setValueDependentColor ContextCompat.getColor(
                            requireContext(),
                            values.color.green_active
                        )
                    }
                    if (normal <= data.y && data.y <= (normal * 2)) {
                        return@setValueDependentColor ContextCompat
                            .getColor(requireContext(), values.color.yellow_active)
                    }
                    return@setValueDependentColor ContextCompat.getColor(
                        requireContext(),
                        values.color.red_active
                    )
                }
                return@setValueDependentColor ContextCompat.getColor(
                    requireContext(),
                    values.color.markup_not_active
                )
            }
        }
    }

    private fun baseColorLogic(data: DataPoint): Int {
        val normal = viewModel.user.phaseNormal
        if (data.x in viewModel.resultForMarkup.value!!.list.map { it.time.time.toDouble() }) {
            if (data.y < normal) {
                return ContextCompat.getColor(
                    requireContext(),
                    values.color.green_active
                )
            }
            if (normal <= data.y && data.y <= (normal * 2)) {
                return ContextCompat
                    .getColor(requireContext(), values.color.yellow_active)
            }
            return ContextCompat.getColor(
                requireContext(),
                values.color.red_active
            )
        }
        return ContextCompat.getColor(
            requireContext(),
            values.color.markup_not_active
        )
    }

    private fun fullSelectedLogicColors(data: DataPoint): Int {
        val normal = viewModel.user.phaseNormal
        if (data.x in viewModel.resultForMarkup.value!!.list.map { it.time.time.toDouble() }) {
            if (data.y < normal) {
                return ContextCompat.getColor(
                    requireContext(),
                    values.color.green_not_active
                )
            }
            if (normal <= data.y && data.y <= (normal * 2)) {
                return ContextCompat
                    .getColor(requireContext(), values.color.yellow_not_active)
            }
            return ContextCompat.getColor(
                requireContext(),
                values.color.red_not_active
            )
        }
        return ContextCompat.getColor(
            requireContext(),
            values.color.markup_not_active
        )
    }

    private fun setOnBarDataClickListener() {
        barSeries.apply {
            setOnDataPointTapListener { _, dataPoint ->
                if (interval["begin"] != null && interval["end"] != null) {
                    interval["begin"] = null
                    interval["end"] = null
                }
                val xPoint = Date(dataPoint.x.toLong())
                if (interval["begin"] == null) {
                    interval["begin"] = xPoint
                } else {
                    if (interval["end"] == null) {
                        if (interval["begin"]!! < xPoint) {
                            interval["end"] = xPoint
                        } else {
                            interval["begin"] = xPoint
                        }
                    } else {
                        interval["begin"] = xPoint
                    }
                }
                setColorAfterClick()
                intervalListener()
                binding.markupGraph.invalidate()
                log(interval.toString())
            }
        }
    }

    private fun setColorAfterClick() {
        barSeries.apply {
            setValueDependentColor { data: DataPoint ->
                val color = if (interval["begin"] != null) {
                    if (interval["end"] != null) {
                        if (data.x in interval["begin"]!!.time.toDouble()..interval["end"]!!.time.toDouble()) {
                            fullSelectedLogicColors(data)
                        } else {
                            baseColorLogic(data)
                        }
                    } else {
                        if (data.x == interval["begin"]!!.time.toDouble()) {
                            fullSelectedLogicColors(data)
                        } else {
                            baseColorLogic(data)
                        }
                    }

                } else {
                    baseColorLogic(data)
                }
                return@setValueDependentColor color
            }
        }
    }
}