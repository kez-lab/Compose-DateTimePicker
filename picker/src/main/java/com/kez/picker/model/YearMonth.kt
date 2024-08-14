package com.kez.picker.model

import com.kez.picker.util.currentMonth
import com.kez.picker.util.currentYear

data class YearMonth(
    val year: Int,
    val month: Int
) {
    companion object {
        fun now() = YearMonth(currentYear, currentMonth)
    }
}
