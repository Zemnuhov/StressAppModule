package com.example.feature_item_markup_impl

import android.graphics.Color
import com.neurotech.core_database_api.model.Cause

data class CountForCauseModelAdapter(
    val color: Int,
    val cause: Cause,
    val count: Int
)