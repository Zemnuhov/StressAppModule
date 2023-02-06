package com.example.feature_main_screen_impl

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.core_foreground_service_api.ServiceApi
import com.example.feature_item_graph_api.ItemGraphApi
import com.example.feature_item_markup_api.ItemMarkupApi
import com.example.feature_main_screen_impl.databinding.FragmentMainBinding
import com.example.feature_main_screen_impl.di.MainScreenComponent
import com.example.feature_phase_info_api.ItemPhaseApi
import com.example.navigation_api.NavigationApi
import com.neurotech.core_bluetooth_comunication_api.ConnectionState
import com.neurotech.feature_tonic_info_api.ItemTonicApi
import javax.inject.Inject
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.neurotech.shared_view_id.R.id as menuR

class MainFragment: Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    @Inject
    lateinit var factory: Lazy<MainScreenViewModel.Factory>

    @Inject
    lateinit var itemTonicApi: ItemTonicApi

    @Inject
    lateinit var itemPhaseApi: ItemPhaseApi

    @Inject
    lateinit var itemGraphApi: ItemGraphApi

    @Inject
    lateinit var itemMarkupApi: ItemMarkupApi

    @Inject
    lateinit var navigationApi: NavigationApi

    @Inject
    lateinit var serviceApi: ServiceApi

    private val viewModel: MainScreenViewModel by viewModels{ factory.get() }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        MainScreenComponent.get().inject(this)
    }

    private fun menuController() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(menuR.menu_search).isVisible = false
                menu.findItem(menuR.menu_disconnect_device).isVisible = true
                menu.findItem(menuR.menu_disconnect_device).setOnMenuItemClickListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.disconnectDevice()
                        delay(500)
                        navigationApi.navigateMainToScan()
                    }
                    true
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                Log.e("AAA", menuItem.toString())
                return true
            }


        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        serviceApi.bindService()
        connectionObserver()
        fillFragment()
        menuController()
    }

    private fun connectionObserver(){
        viewModel.bluetoothState.observe(viewLifecycleOwner){
            if(it == ConnectionState.CONNECTED){
                binding.disconnectView.visibility = View.GONE
            }else{
                binding.disconnectView.alpha = 0F
                binding.disconnectView.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(binding.disconnectView, View.ALPHA,0F, 100F).apply {
                    duration = 10000
                    start()
                }
            }
        }
    }

    private fun fillFragment(){
        childFragmentManager.beginTransaction()
            .replace(R.id.current_and_avg_layout, itemTonicApi.getFragment())
            .commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.peaks_layout, itemPhaseApi.getFragment())
            .commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.graph_fragment_in_main, itemGraphApi.getFragment())
            .commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.statistic_layout, itemMarkupApi.getFragment())
            .commit()
    }
}