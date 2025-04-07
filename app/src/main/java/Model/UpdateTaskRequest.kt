package Model

data class UpdateTaskRequest(
    val feladat_id: Int,
    val mezo: String,
    val uj_ertek: String,
    val felhasznalo_email: String
)