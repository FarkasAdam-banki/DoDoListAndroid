package Network

import Model.Invitation
import Model.InvitationListResponse
import Model.InvitationRequest
import Model.InvitationResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface InvitationService {

    @POST("api/invitation/")
    suspend fun inviteUser(@Body request: InvitationRequest): Response<InvitationResponse>

    @POST("api/invitation/")
    suspend fun removeUser(@Body request: InvitationRequest): Response<InvitationResponse>

    @POST("api/invitation/")
    suspend fun getUsers(@Body request: InvitationRequest): Response<InvitationResponse>

    @GET("api/check_invites/")
    fun checkInvites(@Query("email") email: String): Call<List<Invitation>>

    @POST("api/invitation/")
    suspend fun acceptInvite(@Body request: InvitationRequest): Response<InvitationResponse>

    @POST("api/invitation/")
    suspend fun rejectInvite(@Body request: InvitationRequest): Response<InvitationResponse>
}
