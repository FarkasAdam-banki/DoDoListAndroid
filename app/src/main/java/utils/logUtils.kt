package com.example.dodolist.utils

fun getStatusColorClass(statusId: String): Int {
    return when (statusId) {
        "1" -> android.graphics.Color.RED
        "2" -> android.graphics.Color.YELLOW
        "3" -> android.graphics.Color.GREEN
        else -> android.graphics.Color.GRAY
    }
}

fun getStatusLabel(statusId: String): String {
    return when (statusId) {
        "1" -> "Teendő"
        "2" -> "Folyamatban"
        "3" -> "Kész"
        else -> "Ismeretlen"
    }
}
