package com.neurotech.feature_tonic_info_impl

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.neurotech.feature_tonic_info_impl.databinding.FragmentTonicBinding
import com.neurotech.feature_tonic_info_impl.di.TonicInfoComponent
import dagger.Lazy
import javax.inject.Inject

class TonicFragment: Fragment(R.layout.fragment_tonic) {

    @Inject
    internal lateinit var factory: Lazy<TonicViewModel.Factory>

    private val viewModel: TonicViewModel by viewModels {
        factory.get()
    }

    private var _binding: FragmentTonicBinding? = null
    private val binding get()= _binding!!

    private val timeInterval = arrayOf(Interval.TEN_MINUTE, Interval.HOUR,Interval.DAY)
    private var indexInterval = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        TonicInfoComponent.get().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTonicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserves()
    }

    private fun initView(){
        binding.timeRangeTonic.text = timeInterval[indexInterval].string()
        binding.timeRangeTonic.setOnClickListener {
            indexInterval++
            if(indexInterval == 3){
                indexInterval = 0
            }
            binding.timeRangeTonic.text = timeInterval[indexInterval].string()
            viewModel.setInterval(timeInterval[indexInterval])
        }
        viewModel.setInterval(timeInterval[indexInterval])
    }

    private fun setObserves(){
        viewModel.tonicValue.observe(viewLifecycleOwner){
            binding.currentValue.text = it.toString()
            binding.scale.value = it
        }
        viewModel.avgTonic.observe(viewLifecycleOwner){
            binding.avgValue.text = it.toString()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        TonicInfoComponent.clear()
    }
}