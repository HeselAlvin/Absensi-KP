package id.co.psplauncher.data.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelHistory(
    val name: String,
    val date: String,
    val time: String,
    val status: String,
    val _id: String,
    val datetime: String,
    val face_image_url: String,
    val latitude: Double,
    val longitude: Double,
    val user_id: String
) : Parcelable