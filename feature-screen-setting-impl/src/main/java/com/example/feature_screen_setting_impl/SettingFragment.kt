package com.example.feature_screen_setting_impl

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feature_screen_setting_impl.adapter.CauseAdapter
import com.example.feature_screen_setting_impl.adapter.DayPlanAdapter
import com.example.feature_screen_setting_impl.databinding.FragmentSettingBinding
import com.example.feature_screen_setting_impl.di.SettingComponent
import com.example.navigation_api.NavigationApi
import com.neurotech.core_database_api.model.Cause
import com.neurotech.core_database_api.model.Causes
import com.neurotech.core_database_api.model.DayPlan
import javax.inject.Inject
import dagger.Lazy

class SettingFragment :
    Fragment(R.layout.fragment_setting),
    CauseAdapter.CauseAdapterCallback,
    DayPlanAdapter.DayPlanAdapterCallback {

    @Inject
    lateinit var navigation: NavigationApi

    @Inject
    lateinit var factory: Lazy<SettingViewModel.Factory>

    private val viewModel: SettingViewModel by viewModels {
        factory.get()
    }

    private var _binding: FragmentSettingBinding? = null
    private val binding: FragmentSettingBinding get() = _binding!!

    private var causeAdapter: CauseAdapter? = null
    private var dayPlanAdapter: DayPlanAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        SettingComponent.get().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        causeAdapter = CauseAdapter()
        causeAdapter?.callback = this
        binding.causeRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.causeRecyclerView.adapter = causeAdapter


        dayPlanAdapter = DayPlanAdapter()
        dayPlanAdapter?.callback = this
        binding.dayPlanRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.dayPlanRecyclerView.adapter = dayPlanAdapter

        setObserver()
        setUserClickListener()
    }

    private fun setObserver(){
        viewModel.causes.observe(viewLifecycleOwner){
            causeAdapter?.submitList(it.values)
        }
        viewModel.dayPlans.observe(viewLifecycleOwner){
            dayPlanAdapter?.submitList(it.values)
        }
    }

    private fun setUserClickListener(){
        binding.addCauseButton.setOnClickListener {
            if(binding.causeEditText.text.isNotEmpty()){
                val causeName = binding.causeEditText.text.toString().trim()
                val causes = viewModel.causes.value?: Causes(emptyList())
                if (causeName !in causes.values.map { it.name }){
                    viewModel.addCause(Cause(binding.causeEditText.text.toString()))
                    binding.causeEditText.text.clear()
                }else{
                    Toast.makeText(requireContext(), "Данная причина стресса уже существует!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.addDayPlanButton.setOnClickListener {
            if (binding.dayPlanEditText.text.isNotEmpty()){
                val dayPlanName = binding.dayPlanEditText.text.toString().trim()
                viewModel.addDayPlan(dayPlanName)
                binding.dayPlanEditText.text.clear()
            }
        }
    }

    override fun deleteCause(cause: Cause) {
        viewModel.deleteCause(cause)
    }

    override fun deleteDayPlan(dayPlan: DayPlan) {
        viewModel.deleteDayPlan(dayPlan)
    }

    override fun clickToDayPlan(dayPlan: DayPlan) {
        navigation.navigateSettingToEditingDayPlan(dayPlan.planId)
    }
}