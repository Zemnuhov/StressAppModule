package com.neurotech.modulestressapp.di

import androidx.appcompat.app.AppCompatActivity
import com.example.core_database_control_impl.di.DatabaseControlDependencies
import com.example.core_database_control_impl.di.DatabaseControlDependenciesStore
import com.example.core_firebase_auth_impl.di.FirebaseAuthDependencies
import com.example.core_firebase_auth_impl.di.FirebaseAuthDependenciesStore
import com.example.core_firebase_controller_impl.di.FirebaseControllerDependencies
import com.example.core_firebase_controller_impl.di.FirebaseControllerDependenciesStore
import com.example.core_firebase_database_api.FirebaseDataApi
import com.example.core_foreground_service_impl.di.ServiceDependencies
import com.example.core_foreground_service_impl.di.ServiceDependenciesStore
import com.example.core_notification_controller_impl.di.NotificationControllerDependencies
import com.example.core_notification_controller_impl.di.NotificationControllerDependenciesStore
import com.example.core_signal_control_impl.SignalControlDependencies
import com.example.core_signal_control_impl.SignalControlDependenciesStore
import com.example.feature_item_graph_impl.ItemGraphDependencies
import com.example.feature_item_graph_impl.ItemGraphDependenciesStore
import com.example.feature_item_markup_impl.di.ItemMarkupDependencies
import com.example.feature_item_markup_impl.di.ItemMarkupDependenciesStore
import com.example.feature_main_screen_impl.di.MainScreenDependencies
import com.example.feature_main_screen_impl.di.MainScreenDependenciesStore
import com.example.feature_notification_impl.di.NotificationDependencies
import com.example.feature_notification_impl.di.NotificationDependenciesStore
import com.example.feature_phase_info_impl.di.ItemPhaseDependencies
import com.example.feature_phase_info_impl.di.PhaseInfoDependenciesStore
import com.example.feature_screen_analitic_impl.di.AnalyticDependencies
import com.example.feature_screen_analitic_impl.di.AnalyticDependenciesStore
import com.example.feature_screen_editing_day_plan_impl.di.EditingDayPlanDependencies
import com.example.feature_screen_editing_day_plan_impl.di.EditingDayPlanDependenciesStore
import com.example.feature_screen_markup_impl.di.MarkupComponentDependencies
import com.example.feature_screen_markup_impl.di.MarkupComponentDependenciesStore
import com.example.feature_screen_relax_impl.di.RelaxDependencies
import com.example.feature_screen_relax_impl.di.RelaxDependenciesStore
import com.example.feature_screen_setting_impl.di.SettingDependencies
import com.example.feature_screen_setting_impl.di.SettingDependenciesStore
import com.example.feature_screen_statistic_impl.di.StatisticDependencies
import com.example.feature_screen_statistic_impl.di.StatisticDependenciesStore
import com.example.feature_screen_user_impl.di.UserScreenDependencies
import com.example.feature_screen_user_impl.di.UserScreenDependenciesStore
import com.example.navigation.di.NavigationDependencies
import com.example.navigation.di.NavigationDependenciesStore
import com.example.navigation_api.NavigationApi
import com.example.navigation_api.ViewID
import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothWriterApi
import com.neurotech.core_database_api.*
import com.neurotech.core_database_impl.di.DatabaseComponentDependenciesStore
import com.neurotech.core_database_impl.di.DatabaseDependencies
import com.neurotech.feature_scan_impl.di.ScanDependencies
import com.neurotech.feature_scan_impl.di.ScanDependenciesStore
import com.neurotech.feature_tonic_info_impl.di.ItemTonicDependencies
import com.neurotech.feature_tonic_info_impl.di.TonicInfoDependenciesStore
import com.neurotech.modulestressapp.di.app.DatabaseModule
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import javax.inject.Singleton
import kotlin.properties.Delegates.notNull

@Component(
    dependencies = [FeatureComponentDependencies::class],
    modules = [FeatureScanModule::class, MainScreenModule::class, FeatureModule::class]
)
@Singleton
interface FeatureComponent :
    ScanDependencies,
    MainScreenDependencies,
    ItemTonicDependencies,
    ItemPhaseDependencies,
    ItemGraphDependencies,
    ItemMarkupDependencies,
    StatisticDependencies,
    SettingDependencies,
    AnalyticDependencies,
    MarkupComponentDependencies,
    FirebaseAuthDependencies,
    UserScreenDependencies,
    RelaxDependencies,
    EditingDayPlanDependencies,
    NavigationDependencies {


    @Builder
    interface FeatureComponentBuilder {
        fun provideDependencies(dependencies: FeatureComponentDependencies): FeatureComponentBuilder
        fun build(): FeatureComponent
    }

    companion object {
        private var component: FeatureComponent? = null
        fun init() {
            component = null
            component = DaggerFeatureComponent
                .builder()
                .provideDependencies(FeatureComponentDependenciesProvider.dependencies)
                .build()
        }

        fun clear(){
            component = null
        }


        fun provideDependencies() {
            ScanDependenciesStore.dependencies = component!!
            MainScreenDependenciesStore.dependencies = component!!
            TonicInfoDependenciesStore.dependencies = component!!
            PhaseInfoDependenciesStore.dependencies = component!!
            ItemGraphDependenciesStore.dependencies = component!!
            ItemMarkupDependenciesStore.dependencies = component!!
            NavigationDependenciesStore.dependencies = component!!
            StatisticDependenciesStore.dependencies = component!!
            SettingDependenciesStore.dependencies = component!!
            AnalyticDependenciesStore.dependencies = component!!
            MarkupComponentDependenciesStore.dependencies = component!!
            FirebaseAuthDependenciesStore.dependencies = component!!
            UserScreenDependenciesStore.dependencies = component!!
            RelaxDependenciesStore.dependencies = component!!
            EditingDayPlanDependenciesStore.dependencies = component!!

        }
    }
}

interface FeatureComponentDependencies {
    val activity: AppCompatActivity
    val bluetoothConnection: BluetoothConnectionApi
    val bluetoothData: BluetoothDataApi
    val bluetoothWriter: BluetoothWriterApi
    val viewID: ViewID
    val navigationApi: NavigationApi
    val settingApi: SettingApi
    val tonicApi: TonicApi
    val phaseApi: PhaseApi
    val resultApi: ResultApi
    val userApi: UserApi
    val relaxRecordApi: RelaxRecordApi
    val firebaseDataApi: FirebaseDataApi
}

interface FeatureComponentDependenciesProvider {
    val dependencies: FeatureComponentDependencies

    companion object : FeatureComponentDependenciesProvider by FeatureComponentDependenciesStore
}

object FeatureComponentDependenciesStore : FeatureComponentDependenciesProvider {
    override var dependencies: FeatureComponentDependencies by notNull()
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class FeatureScope