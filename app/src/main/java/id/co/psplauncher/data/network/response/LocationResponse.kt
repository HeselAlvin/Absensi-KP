package id.co.psplauncher.data.network.response

data class LocationResponse(
    val success: Boolean,
    val message: String,
    val is_within_radius: Boolean
)