package Model

data class InvitationResponse(
    val success: Boolean?,
    val error: String?,
    val users: List<UserItem>?
)
