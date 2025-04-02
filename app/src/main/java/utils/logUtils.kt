package com.example.dodolist.utils

fun getStatusColorClass(statusId: String): Int {
    return when (statusId) {
        "1" -> android.graphics.Color.GREEN
        "2" -> android.graphics.Color.RED
        "3" -> android.graphics.Color.YELLOW
        else -> android.graphics.Color.GRAY
    }
}

fun getStatusLabel(statusId: String): String {
    return when (statusId) {
        "1" -> "Elvégezve"
        "2" -> "Függő"
        "3" -> "Új"
        else -> "Ismeretlen"
    }
}
