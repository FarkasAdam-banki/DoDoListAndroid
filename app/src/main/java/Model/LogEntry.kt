package Model

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: List<LogEntry>,
    val timestamp: String
)

data class LogEntry(
    val log_id: Int,
    val feladat_id: Int,
    val felhasznalo_email: String,
    val modositas_datuma: String,
    val mezo_nev: String,
    val regi_ertek: String,
    val uj_ertek: String
)
