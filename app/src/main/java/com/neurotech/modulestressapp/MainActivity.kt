package com.neurotech.modulestressapp

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
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
import com.example.navigation.R as NavigationRes
import com.neurotech.shared_view_id.R as AppMenu

class MainActivity : AppCompatActivity(), FeatureComponentDependencies {

    @Inject
    lateinit var connection: BluetoothConnectionApi

    @Inject
    lateinit var data: BluetoothDataApi

    @Inject
    lateinit var writer: BluetoothWriterApi

    @Inject
    lateinit var navigation: NavigationApi

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

    override val navigationApi: NavigationApi
        get() = navigation


    private lateinit var binding: ActivityMainBinding

    private val navController get() = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

    override fun onResume() {
        super.onResume()
        navigation.bind(navController)
        CoroutineScope(Dispatchers.IO).launch {
            bluetoothWriter.writeNotifyFlag(true)
        }
    }

    override fun onPause() {
        super.onPause()
        navigation.unbind()
        CoroutineScope(Dispatchers.IO).launch {
            bluetoothWriter.writeNotifyFlag(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        AppComponent.get().inject(this)
        FeatureComponentDependenciesStore.dependencies = this
        FeatureComponent.init()
        FeatureComponent.provideDependencies()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigation.bind(navController)

        setSupportActionBar(binding.toolbar)
    }

    override fun onStart() {
        super.onStart()
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            when(destination.id){
                NavigationRes.id.mainFragment ->{
                    val appBarConfiguration = AppBarConfiguration(navController.graph)
                    setupActionBarWithNavController(navController, appBarConfiguration)
                    binding.bottomNavigationView.isVisible = true
                    binding.bottomNavigationView.setupWithNavController(navController)
                }
                NavigationRes.id.scanFragment -> {
                    val appBarConfiguration = AppBarConfiguration(navController.graph)
                    activity.setupActionBarWithNavController(navController, appBarConfiguration)
                    binding.bottomNavigationView.isVisible = false
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(AppMenu.menu.app_menu, menu)
        return true
    }

}