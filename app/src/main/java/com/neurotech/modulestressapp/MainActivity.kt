package com.neurotech.modulestressapp

import android.content.res.Resources.Theme
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.core_firebase_database_api.FirebaseDataApi
import com.example.core_screen_controller.ScreenControllerApi
import com.example.core_screen_controller.ScreenState
import com.example.navigation_api.NavigationApi
import com.example.navigation_api.ViewID
import com.example.values.Dimens
import com.example.values.ScreenSize
import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothWriterApi
import com.neurotech.core_database_api.*
import com.neurotech.modulestressapp.databinding.ActivityMainBinding
import com.neurotech.modulestressapp.databinding.StressAppToolbarBinding
import com.neurotech.modulestressapp.di.app.AppComponent
import com.neurotech.modulestressapp.di.FeatureComponent
import com.neurotech.modulestressapp.di.FeatureComponentDependencies
import com.neurotech.modulestressapp.di.FeatureComponentDependenciesStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.navigation.R as NavigationRes

class MainActivity : AppCompatActivity(), FeatureComponentDependencies {

    @Inject lateinit var connection: BluetoothConnectionApi
    @Inject lateinit var data: BluetoothDataApi
    @Inject lateinit var writer: BluetoothWriterApi
    @Inject lateinit var navigation: NavigationApi
    @Inject lateinit var setting: SettingApi
    @Inject lateinit var tonic: TonicApi
    @Inject lateinit var phase: PhaseApi
    @Inject lateinit var result: ResultApi
    @Inject lateinit var user: UserApi
    @Inject lateinit var relaxRecord: RelaxRecordApi
    @Inject lateinit var firebaseData: FirebaseDataApi
    @Inject lateinit var screenControllerApi: ScreenControllerApi

    override val activity: AppCompatActivity get() = this
    override val bluetoothConnection: BluetoothConnectionApi get() = connection
    override val bluetoothData: BluetoothDataApi get() = data
    override val bluetoothWriter: BluetoothWriterApi get() = writer
    override val viewID: ViewID get() = ViewID(R.id.bottomNavigationView)
    override val navigationApi: NavigationApi get() = navigation
    override val settingApi: SettingApi get() = setting
    override val tonicApi: TonicApi get() = tonic
    override val phaseApi: PhaseApi get() = phase
    override val resultApi: ResultApi get() = result
    override val userApi: UserApi get() = user
    override val relaxRecordApi: RelaxRecordApi get() = relaxRecord
    override val firebaseDataApi: FirebaseDataApi get() = firebaseData
    override val dimens: Dimens
        get() = Dimens(
            ScreenSize(resources.displayMetrics.run { widthPixels / density }.toInt(),
                resources.displayMetrics.run { heightPixels / density }.toInt())
        )


    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var toolbarBinding: StressAppToolbarBinding

    private val navController get() = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

    override fun onResume() {
        super.onResume()
        navigation.bind(navController)
        screenControllerApi.setState(ScreenState.RESUME)
    }

    override fun onPause() {
        super.onPause()
        navigation.unbind()
        screenControllerApi.setState(ScreenState.PAUSE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        //WindowCompat.setDecorFitsSystemWindows(window, false)
        AppComponent.get().inject(this)
        FeatureComponentDependenciesStore.dependencies = this
        FeatureComponent.init()
        FeatureComponent.provideDependencies()

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        toolbarBinding = StressAppToolbarBinding.bind(activityMainBinding.root)
        setContentView(activityMainBinding.root)
        navigation.bind(navController)
        toolbarBinding.upButton.setOnClickListener {
            navController.navigateUp()
        }

        val widthDp = resources.displayMetrics.run { widthPixels / density }
        val heightDp = resources.displayMetrics.run { heightPixels / density }

        Log.e("AAAAAAAAAAA", "${widthDp} ----- ${heightDp}")

        //setSupportActionBar(binding.toolbar)
    }

    override fun onStart() {
        super.onStart()
        screenControllerApi.setState(ScreenState.START)
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            toolbarBinding.titleTextView.text = destination.label
            when(destination.id){
                NavigationRes.id.mainFragment ->{
                    toolbarBinding.upButton.isVisible = false
                    activityMainBinding.bottomNavigationView.isVisible = true
                    activityMainBinding.bottomNavigationView.setupWithNavController(navController)
                    toolbarBinding.starLayout.isVisible = true
                }
                NavigationRes.id.scanFragment -> {
                    toolbarBinding.upButton.isVisible = false
                    activityMainBinding.bottomNavigationView.isVisible = false
                    toolbarBinding.starLayout.isVisible = false
                }
                else -> {
                    toolbarBinding.upButton.isVisible = true
                    toolbarBinding.starLayout.isVisible = false
                }
            }
        }
    }
}