package id.co.psplauncher.data.network.absensi

import id.co.psplauncher.data.network.model.ModelAbsensi
import id.co.psplauncher.data.network.response.AbsenHistoryResponse
import id.co.psplauncher.data.network.response.AbsenResponse
import id.co.psplauncher.data.network.response.AbsenUploadResponse
import id.co.psplauncher.data.network.response.LocationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AbsensiAPI {
    @GET("Location/check")
    suspend fun checklocation(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Response<LocationResponse>

    @POST("absen/submit")
    suspend fun submitAbsensi(
        @Body model: ModelAbsensi
    ): Response<AbsenResponse>

    @GET("absen/history")
    suspend fun getAbsenHistory(
    ): Response<AbsenHistoryResponse>

    @Multipart
    @POST("face/upload")
    suspend fun  uploadface(
        @Part file: MultipartBody.Part
    ): Response<AbsenUploadResponse>

    @Multipart
    @POST("izin/upload")
    suspend fun uploadizin(
        @Part file: MultipartBody.Part,
        @Part("reason") reason: RequestBody
    ): Response<AbsenUploadResponse>
}