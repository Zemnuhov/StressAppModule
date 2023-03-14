package com.neurotech.modulestressapp

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.navigation.AppNavigation
import com.example.navigation_api.NavigationApi
import com.example.navigation_api.ViewID
import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothWriterApi
import com.neurotech.modulestressapp.databinding.ActivityMainBinding
import com.neurotech.modulestressapp.di.AppComponent
import com.neurotech.modulestressapp.di.FeatureComponent
import com.neurotech.modulestressapp.di.FeatureComponentDependencies
import com.neurotech.modulestressapp.di.FeatureComponentDependenciesStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.neurotech.shared_view_id.R as AppMenu

class MainActivity : AppCompatActivity(), FeatureComponentDependencies {

    @Inject
    lateinit var connection: BluetoothConnectionApi

    @Inject
    lateinit var data: BluetoothDataApi

    @Inject
    lateinit var writer: BluetoothWriterApi

    override val activity: AppCompatActivity
        get() = this

    override val bluetoothConnection: BluetoothConnectionApi
        get() = connection

    override val bluetoothData: BluetoothDataApi
        get() = data

    override val bluetoothWriter: BluetoothWriterApi
        get() = writer

    override val viewID: ViewID
        get() = ViewID(R.id.bottomNavigationView)


    private lateinit var binding: ActivityMainBinding

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            bluetoothWriter.writeNotifyFlag(true)
        }
    }

    override fun onPause() {
        super.onPause()
        CoroutineScope(Dispatchers.IO).launch {
            bluetoothWriter.writeNotifyFlag(false)
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        AppComponent.get().inject(this)

        FeatureComponentDependenciesStore.dependencies = this
        FeatureComponent.init()
        FeatureComponent.provideDependencies()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.bottomNavigationView.isVisible = false
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(AppMenu.menu.app_menu, menu)
        return true
    }

}