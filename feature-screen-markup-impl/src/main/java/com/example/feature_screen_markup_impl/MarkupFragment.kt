package com.example.feature_screen_markup_impl

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cesarferreira.tempo.beginningOfDay
import com.cesarferreira.tempo.endOfDay
import com.cesarferreira.tempo.toString
import com.example.feature_screen_markup_impl.adapter.CauseAdapter
import com.example.feature_screen_markup_impl.adapter.ResultAdapter
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
import com.neurotech.utils.TimeFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import com.example.values.R as values

class MarkupFragment : Fragment(R.layout.fragment_markup), ResultAdapter.ResultAdapterCallback,
    CauseAdapter.CauseCallback {

    @Inject
    lateinit var factory: Provider<MarkupViewModel.Factory>

    private val viewModel: MarkupViewModel by viewModels {
        factory.get()
    }


    private var _binding: FragmentMarkupBinding? = null
    private val binding get() = _binding!!

    private var resultAdapter: ResultAdapter? = null
    private var causeAdapter: CauseAdapter? = null

    private var barSeries = BarGraphSeries(arrayOf<DataPoint>())
    private var tonicSeries = LineGraphSeries(arrayOf<DataPoint>())

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


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resultAdapter = ResultAdapter()
        resultAdapter?.callback = this

        binding.sourceRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        causeAdapter = CauseAdapter()
        causeAdapter?.callback = this
        binding.sourceRecyclerView.adapter = causeAdapter


        viewModel.results.observe(viewLifecycleOwner) {

        }

        binding.timeSetButton.setOnClickListener {
            val beginPeriod = binding.beginTime.text.toString()
            val endPeriod = binding.endTime.text.toString()
            val list = resultAdapter?.currentList
            list?.forEachIndexed { index, resultAdapterModel ->
                val time = resultAdapterModel.time.toString(TimeFormat.timePattern)
                if (time in beginPeriod..endPeriod) {
                    list[index].isChecked = true
                }
            }
            resultAdapter?.submitList(list)
            resultAdapter?.notifyDataSetChanged()
        }

        viewModel.causes.observe(viewLifecycleOwner) {
            causeAdapter?.submitList(it.values)
        }

        binding.saveButton.setOnClickListener {
            viewModel.saveMarkups(
                ResultsTenMinute(
                    resultAdapter?.currentList!!
                        .filter { it.stressCause != null }
                        .map {
                            ResultTenMinute(
                                it.time,
                                it.peakCount,
                                it.tonicAvg,
                                it.conditionAssessment,
                                it.stressCause,
                                it.keep
                            )
                        }
                )
            )
        }

        setTimeChoiceListener()
    }

    @SuppressLint("SetTextI18n")
    private fun setTimeChoiceListener() {
        binding.beginTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                activity,
                { _, hour, minute ->
                    binding.beginTime.text = "${
                        if (hour < 10) {
                            "0$hour"
                        } else hour
                    }:${
                        if (minute < 10) {
                            "0$minute"
                        } else minute
                    }"
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.endTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                activity,
                { _, hour, minute ->
                    binding.endTime.text = "${
                        if (hour < 10) {
                            "0$hour"
                        } else hour
                    }:${
                        if (minute < 10) {
                            "0$minute"
                        } else minute
                    }"
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    override fun isCheckedClick(position: Int, isCheck: Boolean) {
        val list = resultAdapter?.currentList
        if (list != null && position < list.size) {
            list[position]?.isChecked = isCheck
        }
        resultAdapter?.submitList(list)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun clickCause(cause: Cause) {
        val list = resultAdapter?.currentList
        list?.forEachIndexed { index, resultAdapterModel ->
            if (resultAdapterModel.isChecked) {
                list[index].isChecked = false
                list[index].stressCause = cause.name
            }
        }
        resultAdapter?.submitList(list)
        resultAdapter?.notifyDataSetChanged()
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
        barSeries.apply {
            barSeries.spacing = 20
            val normal = viewModel.user.phaseNormal
            log(normal.toString())
            setValueDependentColor { data: DataPoint ->
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
                ContextCompat.getColor(requireContext(), values.color.red_active)
            }
        }

        tonicSeries.color = Color.BLACK
        binding.markupGraph.invalidate()
    }


}