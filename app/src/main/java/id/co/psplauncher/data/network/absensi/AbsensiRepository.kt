package id.co.psplauncher.data.network.absensi

import id.co.psplauncher.data.local.UserPreferences
import id.co.psplauncher.data.network.BaseRepository
import id.co.psplauncher.data.network.model.ModelAbsensi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AbsensiRepository @Inject constructor(
    private val api: AbsensiAPI,
    private val userPreferences: UserPreferences
) : BaseRepository() {

    // Memanggil API cek lokasi
    suspend fun checkLocation(lat: Double, lng: Double) = safeApiCall(
        apiCall = { api.checklocation(  lat, lng) },
        userPreferences = userPreferences
    )

    // Memanggil API submit absen
    suspend fun submitAbsensi(model: ModelAbsensi) = safeApiCall(
        apiCall = { api.submitAbsensi(model) },
        userPreferences = userPreferences
    )

    // Memanggil API riwayat absen
    suspend fun getRiwayatAbsensi() = safeApiCall(
        apiCall = { api.getAbsenHistory() },
        userPreferences = userPreferences
    )

    // Memanggil API upload wajah
    suspend fun uploadFace(file: MultipartBody.Part) = safeApiCall(
        apiCall = { api.uploadface(file) },
        userPreferences = userPreferences
    )

    // Memanggil API upload izin
    suspend fun uploadIzin(file: MultipartBody.Part, reason: RequestBody) = safeApiCall(
        apiCall = { api.uploadizin(file, reason) },
        userPreferences = userPreferences
    )
}