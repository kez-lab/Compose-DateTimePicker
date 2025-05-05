package com.kez.picker.sample

import com.kez.picker.util.TimePeriod

/**
 * 시간 형식화를 위한 확장 함수 - 12시간제
 */
fun formatTime12(hour: Int?, minute: Int?, period: TimePeriod?): String {
    val h = hour ?: 12
    val m = minute ?: 0
    val p = period ?: TimePeriod.AM
    return "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')} $p"
}

/**
 * 시간 형식화를 위한 확장 함수 - 24시간제
 */
fun formatTime24(hour: Int?, minute: Int?): String {
    val h = hour ?: 0
    val m = minute ?: 0
    return "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"
}

/**
 * 월 이름 가져오기 함수
 */
fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "1월 (Jan)"
        2 -> "2월 (Feb)" 
        3 -> "3월 (Mar)"
        4 -> "4월 (Apr)"
        5 -> "5월 (May)"
        6 -> "6월 (Jun)"
        7 -> "7월 (Jul)"
        8 -> "8월 (Aug)"
        9 -> "9월 (Sep)"
        10 -> "10월 (Oct)"
        11 -> "11월 (Nov)"
        12 -> "12월 (Dec)"
        else -> "알 수 없음"
    }
} 