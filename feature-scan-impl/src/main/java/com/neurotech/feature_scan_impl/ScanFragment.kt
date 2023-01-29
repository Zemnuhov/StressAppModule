package com.neurotech.feature_scan_impl

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigation_api.NavigationApi
import com.neurotech.core_ble_device_scan_api.Device
import com.neurotech.core_bluetooth_comunication_api.ConnectionState
import com.neurotech.feature_scan_impl.databinding.FragmentScanBinding
import com.neurotech.feature_scan_impl.di.ScanComponent
import dagger.Lazy
import javax.inject.Inject
import com.neurotech.shared_view_id.R as AppMenu

class ScanFragment: Fragment(R.layout.fragment_scan), ScanAdapter.ClickItemDevice{

    @Inject
    lateinit var navigation: NavigationApi

    @Inject
    internal lateinit var factory: Lazy<ScanViewModel.Factory>

    private val viewModel: ScanViewModel by viewModels {
        factory.get()
    }

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private var scanAdapter: ScanAdapter? = null



    private val bluetoothAdapter by lazy {
        (requireContext().getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }
    private val location by lazy {
        (requireContext().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager)
    }

    companion object {
        private var fragment: ScanFragment? = null
        fun getFragment(): Fragment?{
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ScanComponent.init()
        ScanComponent.get()?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerViewList.layoutManager = LinearLayoutManager(context)
        scanAdapter = ScanAdapter()
        scanAdapter?.callBack = this
        binding.recyclerViewList.adapter = scanAdapter

        menuControl()
        requestPermissions()
        setObservers()
    }

    private fun setObservers(){
        viewModel.devices.observe(viewLifecycleOwner){ devices ->
            scanAdapter?.submitList(devices.list)
        }
        binding.refreshListDevice.setOnRefreshListener {
            viewModel.startScan()
        }

        viewModel.scanState.observe(viewLifecycleOwner){
            binding.refreshListDevice.isRefreshing = it
        }

        viewModel.connectionState.observe(viewLifecycleOwner){
            when(it){
                ConnectionState.CONNECTED -> {
//                    val request = NavDeepLinkRequest.Builder
//                        .fromUri("android-app://com.example.feature_main_screen_impl.MainFragment".toUri())
//                        .build()
//                    findNavController().navigate(request)
                    navigation.navigateScanToMain()

                }
                ConnectionState.CONNECTING -> binding.connectProgress.isVisible = true
                else -> binding.connectProgress.isVisible = false
            }
        }

    }

    private fun requestPermissions() {
        val permissionsList = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_DENIED
        ) {
            permissionsList.add(Manifest.permission.BLUETOOTH)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_DENIED
            ) {
                permissionsList.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_DENIED
            ) {
                permissionsList.add(Manifest.permission.BLUETOOTH_SCAN)
            }
        }

        if(permissionsList.isNotEmpty()){
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsList.toTypedArray(),
                1
            )
        }


        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                if(it.resultCode != Activity.RESULT_OK){
                    Toast.makeText(requireContext(), "Блютуз не включен!", Toast.LENGTH_SHORT).show()
                }
            }
            resultLauncher.launch(enableBtIntent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if(!location.isLocationEnabled){
                Toast.makeText(requireContext(), "Требуется включить местоположение", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun menuControl(){
        requireActivity().addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(AppMenu.id.menu_search).isVisible = true
                menu.findItem(AppMenu.id.menu_disconnect_device).isVisible = false
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    AppMenu.id.menu_search -> viewModel.startScan()
                }
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ScanComponent.clear()
    }

    override fun clickItem(device: Device) {
        viewModel.connectToDevice(device.mac)
    }
}