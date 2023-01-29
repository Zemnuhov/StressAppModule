package com.example.feature_item_markup_api

import androidx.fragment.app.Fragment

interface ItemMarkupApi {
    companion object{
        const val BUNDLE_KEY = "VISIBILITY"
    }
    fun getFragment(): Fragment
}