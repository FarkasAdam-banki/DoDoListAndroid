package Model

import java.time.LocalDateTime

data class Task(
    val feladat_id: Int,
    val feladat_nev: String,
    val feladat_hatarido: String,
    val allapot_id: Int
)
