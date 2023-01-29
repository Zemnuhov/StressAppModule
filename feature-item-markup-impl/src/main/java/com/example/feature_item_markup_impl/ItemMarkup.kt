package com.example.feature_item_markup_impl

import androidx.fragment.app.Fragment
import com.example.feature_item_markup_api.ItemMarkupApi

class ItemMarkup: ItemMarkupApi {
    override fun getFragment(): Fragment {
        return ItemMarkupFragment()
    }
}