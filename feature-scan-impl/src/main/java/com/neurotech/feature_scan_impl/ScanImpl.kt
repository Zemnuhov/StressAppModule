package com.neurotech.feature_scan_impl

import android.content.Context
import androidx.fragment.app.Fragment
import com.neurotech.feature_scan_api.ScanAPI

class ScanImpl(val context: Context): ScanAPI {
    override fun getFragment(): Fragment {
        return ScanFragment.getFragment()!!
    }

    fun test(){

    }
}