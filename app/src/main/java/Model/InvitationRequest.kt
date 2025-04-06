package Model

data class InvitationRequest(
    val action: String,
    val feladat_id: Int,
    val email: String?
)
