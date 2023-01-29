package com.example.feature_item_graph_impl

import androidx.fragment.app.Fragment
import com.example.feature_item_graph_api.ItemGraphApi
import javax.inject.Inject

class ItemGraph: ItemGraphApi {

    override fun getFragment(): Fragment {
        return PhaseGraphFragment()
    }
}