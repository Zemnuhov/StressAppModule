package com.neurotech.feature_tonic_info_impl.implementation

import androidx.fragment.app.Fragment
import com.neurotech.feature_tonic_info_api.ItemTonicApi
import com.neurotech.feature_tonic_info_impl.TonicFragment

class ItemTonic: ItemTonicApi {
    override fun getFragment(): Fragment {
        return TonicFragment()
    }
}