package com.neurotech.utils

import android.widget.Toast
import androidx.fragment.app.Fragment

data class WorkResult(
    val isError: Boolean,
    val message: String? = null
)

fun Fragment.process(workResult: WorkResult){
    if(workResult.isError){
        Toast.makeText(this.requireContext(), workResult.message, Toast.LENGTH_SHORT).show()
    }
}