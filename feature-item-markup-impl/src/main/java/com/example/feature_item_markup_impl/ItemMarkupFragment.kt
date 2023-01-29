package com.example.feature_item_markup_impl

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
import com.example.feature_item_markup_impl.databinding.FragmentItemMarkupBinding
import com.example.feature_item_markup_impl.di.ItemMarkupComponent
import com.example.navigation_api.NavigationApi
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import javax.inject.Inject
import dagger.Lazy

class ItemMarkupFragment: Fragment(R.layout.fragment_item_markup) {

    companion object{
        const val BUNDLE_KEY = "VISIBILITY"
    }

    @Inject
    lateinit var navigation: NavigationApi

    @Inject
    lateinit var factory: Lazy<ItemMarkupViewModel.Factory>
    private val viewModel: ItemMarkupViewModel by viewModels {
        factory.get()
    }

    private var _binding: FragmentItemMarkupBinding? = null
    private val binding get() = _binding!!

    private val colors by lazy {
        listOf(
            ContextCompat.getColor(requireContext(), R.color.primary),
            ContextCompat.getColor(requireContext(), R.color.primary_dark),
            ContextCompat.getColor(requireContext(), R.color.primary_light),
            ContextCompat.getColor(requireContext(), R.color.secondary),
            ContextCompat.getColor(requireContext(), R.color.secondary_dark),
            ContextCompat.getColor(requireContext(), R.color.third_dark),
            ContextCompat.getColor(requireContext(), R.color.third)
        )
    }

    private var adapter: ItemMarkupAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ItemMarkupComponent.get().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemMarkupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.sourceLayoutStatistic.layoutManager = LinearLayoutManager(context)
        adapter = ItemMarkupAdapter()
        binding.sourceLayoutStatistic.adapter = adapter
        binding.sourceLayoutStatistic.isNestedScrollingEnabled = false

        if(arguments?.getString(BUNDLE_KEY) != null){
            binding.settingIcon.visibility = View.GONE
        }

        viewModel.countForEachReason.observe(viewLifecycleOwner) {
            val requireSizeList = it.list.sortedBy { it.count }.reversed().take(colors.size-1)
            val list = requireSizeList.mapIndexed { index, countForCause ->
                CountForCauseModelAdapter(
                    colors[index],
                    countForCause.cause,
                    countForCause.count
                )
            }
            adapter?.submitList(list)
            val entries = mutableListOf<PieEntry>()
            val entryColors = mutableListOf<Int>()
            entries.addAll(list.map { PieEntry(it.count.toFloat(), it.cause.name) })
            entryColors.addAll(list.map { it.color })
            if(entries.filter { it.value>0 }.isEmpty()) {
                entries.add(PieEntry(1F,""))
            }
            if(entryColors.isEmpty()){
                entryColors.add(colors[0])
            }
            fillPieChart(entries, entryColors)
        }
        binding.settingIcon.setOnClickListener {
            navigation.navigateMainToStatistic()
        }
    }

    private fun fillPieChart(entries: List<PieEntry>, entryColors: List<Int>){
        val set = PieDataSet(entries,"Statistic")
        set.colors = entryColors
        val dataSet = PieData(set)
        dataSet.setValueTextColor(Color.BLACK)
        binding.pieChart.data = dataSet
        binding.pieChart.setDrawSliceText(false)
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.description = null
        binding.pieChart
            .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.card_background))
        binding.pieChart
            .setHoleColor(ContextCompat.getColor(requireContext(), R.color.card_background))
        binding.pieChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ItemMarkupComponent.clear()
    }


}