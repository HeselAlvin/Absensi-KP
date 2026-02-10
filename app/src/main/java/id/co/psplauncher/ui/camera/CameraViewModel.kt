package id.co.psplauncher.ui.camera

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import id.co.psplauncher.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

// Kita pakai AndroidViewModel karena butuh "Application" untuk akses folder file
class CameraViewModel(application: Application) : AndroidViewModel(application) {

    // 1. Variabel Penampung Lokasi
    var lat: Double = 0.0
    var lng: Double = 0.0

    // 2. Fungsi Membuat File Foto Baru
    fun createPhotoFile(): File {
        // Tentukan nama file: "JPEG_2024-02-10_12-00-00.jpg"
        val timeStamp = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis())
        val fileName = "JPEG_${timeStamp}.jpg"

        // Ambil folder penyimpanan khusus aplikasi
        val storageDir = getOutputDirectory()

        return File(storageDir, fileName)
    }

    // Fungsi Pembantu: Mencari folder aman di HP untuk simpan foto
    private fun getOutputDirectory(): File {
        val mediaDir = getApplication<Application>().externalMediaDirs.firstOrNull()?.let {
            File(it, getApplication<Application>().resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else getApplication<Application>().filesDir
    }
}