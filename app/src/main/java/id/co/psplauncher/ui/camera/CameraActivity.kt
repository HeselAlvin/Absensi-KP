package id.co.psplauncher.ui.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import id.co.psplauncher.databinding.ActivityCameraBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    // Panggil ViewModel dengan cara ktx
    private val viewModel: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Tangkap Data Lokasi dari Maps & Simpan ke ViewModel
        // Kita simpan di ViewModel biar datanya "awet"
        if (viewModel.lat == 0.0) { // Cek biar gak ketimpa kalau layar muter
            viewModel.lat = intent.getDoubleExtra("extra_lat", 0.0)
            viewModel.lng = intent.getDoubleExtra("extra_lng", 0.0)
        }
        Log.d("CameraActivity", "Lokasi di ViewModel: ${viewModel.lat}, ${viewModel.lng}")

        // 2. Cek Izin Kamera
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // 3. Tombol Jepret
        binding.btnCapture.setOnClickListener {
            takePhoto()
        }

        // 4. Tombol Kembali
        binding.btnBack.setOnClickListener {
            finish()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview (Layar)
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // ImageCapture (Alat Foto)
            imageCapture = ImageCapture.Builder().build()

            // Pilih Kamera Depan (Selfie)
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Gagal memunculkan kamera", exc)
                Toast.makeText(this, "Gagal membuka kamera", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Minta ViewModel buatkan File kosong untuk tempat foto
        val photoFile = viewModel.createPhotoFile()

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Gagal jepret: ${exc.message}", exc)
                    Toast.makeText(baseContext, "Gagal ambil foto", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = android.net.Uri.fromFile(photoFile)
                    val msg = "Foto tersimpan: $savedUri"
                    Log.d(TAG, msg)

                    // Pindah ke Halaman Konfirmasi
                    goToConfirmation(photoFile.absolutePath)
                }
            }
        )
    }

    private fun goToConfirmation(photoPath: String) {
        // Kita oper path foto & lokasi ke halaman selanjutnya
        /*
        // UN-COMMENT BAGIAN INI KALAU SUDAH BUAT ConfirmationActivity.kt
        val intent = Intent(this, id.co.psplauncher.ui.confirmation.ConfirmationActivity::class.java)
        intent.putExtra("extra_photo_path", photoPath)
        intent.putExtra("extra_lat", viewModel.lat) // Ambil dari ViewModel
        intent.putExtra("extra_lng", viewModel.lng) // Ambil dari ViewModel
        startActivity(intent)
        finish()
        */

        // Sementara pakai Toast dulu
        Toast.makeText(this, "Berhasil! OTW Konfirmasi...", Toast.LENGTH_SHORT).show()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Izin ditolak.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}