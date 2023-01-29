package com.example.feature_phase_info_impl

enum class Interval {
    TEN_MINUTE{
        override fun string() = "10М"
    },
    HOUR{
        override fun string() = "1Ч"
    },
    DAY{
        override fun string() = "1Д"
    };

    abstract fun string(): String
}