package com.example.feature_screen_editing_day_plan_impl

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.feature_screen_editing_day_plan_impl.databinding.FragmentEditingDayPlanBinding
import com.example.feature_screen_editing_day_plan_impl.di.DaggerEditingDayPlanComponent
import com.example.feature_screen_editing_day_plan_impl.di.EditingDayPlanComponent
import com.neurotech.core_database_api.model.DayPlan
import com.neurotech.utils.StressLogger.log
import com.neurotech.utils.process
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class EditingDayPlanFragment: Fragment(R.layout.fragment_editing_day_plan) {

    private var _binding: FragmentEditingDayPlanBinding? = null
    private val binding get() = _binding!!

    private var focusFlag = false

    @Inject
    lateinit var factory: Lazy<EditingDayPlanViewModel.Factory>

    private var dayPlan: DayPlan? = null

    private val viewModel: EditingDayPlanViewModel by viewModels {
        factory.get()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        EditingDayPlanComponent.get().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditingDayPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dayPlanId = arguments?.getInt("ID")
        runBlocking {
            if(dayPlanId != null){
                dayPlan = viewModel.getDayPlanById(dayPlanId)
                binding.updateFragmentTitle.text = dayPlan!!.plan
                binding.beginTimeTextView.text = dayPlan!!.timeBegin ?: "00:00"
                binding.endTimeTextView.text = dayPlan!!.timeEnd ?: "00:00"
                binding.switch1.isChecked = dayPlan!!.autoMarkup
                fillSpinners()
                initTimeSettings()
            }
        }

        binding.updateButton.setOnClickListener {
            if(dayPlan != null){
                log(binding.switch1.isChecked.toString())
                viewModel.updateDayPlan(
                    DayPlan(
                        dayPlan!!.planId,
                        dayPlan!!.plan,
                        binding.beginTimeTextView.text.toString(),
                        binding.endTimeTextView.text.toString(),
                        binding.firstSourceSpinner.selectedItem as String,
                        binding.secondSourceSpinner.selectedItem as String,
                        binding.switch1.isChecked
                    )
                )
            }

        }

        viewModel.errorHandler.observe(viewLifecycleOwner){
            process(it)
        }
    }

    private fun fillSpinners() {
        viewModel.causes.observe(viewLifecycleOwner) {
            val causesList = it.values.map { it.name }.sorted()
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, causesList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.firstSourceSpinner.adapter = adapter
            binding.secondSourceSpinner.adapter = adapter
            if(dayPlan!!.firstSource != null){
                binding.firstSourceSpinner.setSelection(causesList.indexOf(dayPlan!!.firstSource))
            }
            if(dayPlan!!.secondSource != null){
                binding.secondSourceSpinner.setSelection(causesList.indexOf(dayPlan!!.secondSource))
            }

        }
    }

    private fun initTimeSettings() {
        binding.beginTimeTextView.background =
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.selected_background
            )
        binding.beginTimeTextView.setOnClickListener {
            it.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.selected_background
                )
            focusFlag = false
            binding.endTimeTextView.setBackgroundColor(Color.WHITE)
        }

        binding.endTimeTextView.setOnClickListener {
            it.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.selected_background
                )
            focusFlag = true
            binding.beginTimeTextView.setBackgroundColor(Color.WHITE)
        }

        binding.timeSpinner.setIs24HourView(true)
        binding.timeSpinner.setOnTimeChangedListener { _, hourOfDay, minuteOfHour ->
            val minute: String = if (minuteOfHour < 10) {
                "0$minuteOfHour"
            } else "$minuteOfHour"

            val hour: String = if (hourOfDay < 10) {
                "0$hourOfDay"
            } else "$hourOfDay"
            if (!focusFlag) {
                binding.beginTimeTextView.text = "$hour:$minute"
            } else {
                binding.endTimeTextView.text = "$hour:$minute"
            }
        }
    }
}