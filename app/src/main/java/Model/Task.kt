package Model

import java.time.LocalDateTime

data class Task(
    val feladat_id: Int,
    val feladat_nev: String,
    val feladat_hatarido: String,
    val subTaskCount: Int,
    val collaborators: String
)
