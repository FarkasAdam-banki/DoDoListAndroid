package Model

data class UpdatePasswordRequest(
    val action: String,
    val email: String,
    val old_password: String,
    val new_password: String
)