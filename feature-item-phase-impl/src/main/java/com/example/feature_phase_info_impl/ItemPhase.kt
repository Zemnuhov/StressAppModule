package com.example.feature_phase_info_impl

import androidx.fragment.app.Fragment
import com.example.feature_phase_info_api.ItemPhaseApi

class ItemPhase: ItemPhaseApi {
    override fun getFragment(): Fragment {
        return PhaseInfoFragment()
    }
}