package com.neurotech.modulestressapp.di

import com.example.core_foreground_service_api.ServiceApi
import com.example.core_foreground_service_impl.ServiceImpl
import com.example.feature_item_graph_api.ItemGraphApi
import com.example.feature_item_graph_impl.ItemGraph
import com.example.feature_item_markup_api.ItemMarkupApi
import com.example.feature_item_markup_impl.ItemMarkup
import com.example.feature_phase_info_api.ItemPhaseApi
import com.example.feature_phase_info_impl.ItemPhase
import com.neurotech.feature_tonic_info_api.ItemTonicApi
import com.neurotech.feature_tonic_info_impl.implementation.ItemTonic
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainScreenModule {

    @Provides
    @FeatureScope
    fun provideItemTonicApi(): ItemTonicApi{
        return ItemTonic()
    }

    @Provides
    @FeatureScope
    fun provideItemPhaseApi(): ItemPhaseApi{
        return ItemPhase()
    }

    @Provides
    @FeatureScope
    fun provideItemGraphApi(): ItemGraphApi{
        return ItemGraph()
    }

    @Provides
    @FeatureScope
    fun provideItemMarkupApi(): ItemMarkupApi{
        return ItemMarkup()
    }

    @Provides
    @FeatureScope
    fun provideServiceApi(): ServiceApi {
        return ServiceImpl()
    }

}