package com.example.feature_screen_markup_impl

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.cesarferreira.tempo.toString
import com.example.feature_screen_markup_impl.adapter.CauseAdapter
import com.example.feature_screen_markup_impl.adapter.ResultAdapter
import com.example.feature_screen_markup_impl.adapter.ResultAdapterModel
import com.example.feature_screen_markup_impl.databinding.FragmentMarkupBinding
import com.example.feature_screen_markup_impl.di.MarkupComponent
import com.neurotech.core_database_api.model.Cause
import com.neurotech.core_database_api.model.ResultTenMinute
import com.neurotech.core_database_api.model.ResultsTenMinute
import com.neurotech.utils.TimeFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import com.example.values.R as values

class MarkupFragment: Fragment(R.layout.fragment_markup), ResultAdapter.ResultAdapterCallback, CauseAdapter.CauseCallback {

    @Inject
    lateinit var factory: Provider<MarkupViewModel.Factory>

    private val viewModel: MarkupViewModel by viewModels{
        factory.get()
    }


    private var _binding: FragmentMarkupBinding? = null
    private val binding get() = _binding!!

    private var resultAdapter: ResultAdapter? = null
    private var causeAdapter: CauseAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MarkupComponent.get().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkupBinding.inflate(inflater,container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.markupRecyclerView.layoutManager = LinearLayoutManager(context)
        resultAdapter = ResultAdapter()
        resultAdapter?.callback = this
        binding.markupRecyclerView.adapter = resultAdapter

        binding.sourceRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
        causeAdapter = CauseAdapter()
        causeAdapter?.callback = this
        binding.sourceRecyclerView.adapter = causeAdapter


        viewModel.resultForMarkup.observe(viewLifecycleOwner){
            if(it.list.isNotEmpty()){
                binding.markupRecyclerView.visibility = View.VISIBLE
                resultAdapter?.submitList(
                    it.list.map {
                        ResultAdapterModel(
                            it.time,
                            it.peakCount,
                            it.tonicAvg,
                            it.conditionAssessment,
                            it.stressCause,
                            it.keep,
                            false,
                            when(it.peakCount){
                                in 0..viewModel.user.phaseNormal -> {
                                    ContextCompat.getColor(requireContext(), values.color.green_active)
                                }
                                in viewModel.user.phaseNormal+1..viewModel.user.phaseNormal * 2 -> {
                                    ContextCompat.getColor(requireContext(), values.color.yellow_active)
                                }
                                else  -> {
                                    ContextCompat.getColor(requireContext(), values.color.red_active)
                                }
                            }
                        )
                    }
                )
            }else{
                binding.markupRecyclerView.visibility = View.GONE
            }

        }

        binding.timeSetButton.setOnClickListener {
            val beginPeriod = binding.beginTime.text.toString()
            val endPeriod = binding.endTime.text.toString()
            val list = resultAdapter?.currentList
            list?.forEachIndexed { index, resultAdapterModel ->
                val time = resultAdapterModel.time.toString(TimeFormat.timePattern)
                if(time in beginPeriod..endPeriod){
                    list[index].isChecked = true
                }
            }
            resultAdapter?.submitList(list)
            resultAdapter?.notifyDataSetChanged()
        }

        viewModel.causes.observe(viewLifecycleOwner){
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
    private fun setTimeChoiceListener(){
        binding.beginTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                activity,
                { _, hour, minute ->
                    binding.beginTime.text = "${if (hour<10){ "0$hour" }else hour}:${if (minute<10){ "0$minute" }else minute}"
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
                    binding.endTime.text = "${if (hour<10){ "0$hour" }else hour}:${if (minute<10){ "0$minute" }else minute}"
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    override fun isCheckedClick(position: Int, isCheck: Boolean) {
        val list = resultAdapter?.currentList
        if(list != null && position < list.size){
            list[position]?.isChecked = isCheck
        }
        resultAdapter?.submitList(list)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun clickCause(cause: Cause) {
        val list = resultAdapter?.currentList
        list?.forEachIndexed { index, resultAdapterModel ->
            if (resultAdapterModel.isChecked){
                list[index].isChecked = false
                list[index].stressCause = cause.name
            }
        }
        resultAdapter?.submitList(list)
        resultAdapter?.notifyDataSetChanged()
    }


}