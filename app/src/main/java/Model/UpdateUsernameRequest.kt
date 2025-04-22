package Model

data class UpdateUsernameRequest(
    val action: String,
    val email: String,
    val new_username: String
)