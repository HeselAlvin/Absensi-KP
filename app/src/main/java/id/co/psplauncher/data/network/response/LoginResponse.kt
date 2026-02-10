package id.co.psplauncher.data.network.response

data class LoginResponse(
    val jwt: String,
    val success: Boolean,
    val user: User
)




