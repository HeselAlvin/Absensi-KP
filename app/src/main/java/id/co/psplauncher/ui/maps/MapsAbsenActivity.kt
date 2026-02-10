package id.co.psplauncher.ui.maps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import id.co.psplauncher.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import id.co.psplauncher.databinding.MapsAbsensiBinding // Pastikan nama ini sesuai nama XML kamu

class MapsAbsenActivity : AppCompatActivity() {

    private lateinit var binding: MapsAbsensiBinding
    private var myLocationOverlay: MyLocationNewOverlay? = null
    private var lastLat: Double = 0.0
    private var lastLong: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Konfigurasi OSM (Wajib)
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))

        // --- TAMBAHKAN BARIS INI (Fix Layar Biru) ---
        Configuration.getInstance().userAgentValue = packageName
        // --------------------------------------------

        // 2. Setup Binding
        binding = MapsAbsensiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateButtonState(false)

        setupMapView()
        setupButtons()
        checkPermissions()
    }

    private fun updateButtonState(isReady: Boolean) {
        binding.btnNext.isEnabled = isReady

        if (isReady) {
            val colorOrange = ContextCompat.getColor(this, R.color.Yellow) // Sesuaikan nama di XML
            val colorWhite = ContextCompat.getColor(this, R.color.white)

            binding.btnNext.backgroundTintList =ColorStateList.valueOf(colorOrange)
            binding.btnNext.setTextColor(colorWhite)
            binding.btnNext.iconTint = ColorStateList.valueOf(colorWhite)

        }else{
            val colorGrey = ContextCompat.getColor(this, R.color.Grey) // Sesuaikan nama di XML
            val colorBlack = ContextCompat.getColor(this, R.color.black)

            // 2. Pasang ke Tombol
            binding.btnNext.backgroundTintList = ColorStateList.valueOf(colorGrey)
            binding.btnNext.setTextColor(colorBlack)
            binding.btnNext.iconTint = ColorStateList.valueOf(colorBlack)
        }

    }

    private fun setupMapView() {
        binding.mapview.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapview.setMultiTouchControls(true)
        binding.mapview.controller.setZoom(18.0)
        binding.mapview.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnAktifkanGPS.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

        binding.btnNext.setOnClickListener {
            if (lastLat != 0.0 && lastLong != 0.0) {
                val intent = Intent(this, id.co.psplauncher.ui.camera.CameraActivity::class.java)

                intent.putExtra("extra_lat", lastLat)
                intent.putExtra("extra_lng", lastLong)

                startActivity(intent)

            } else {
                Toast.makeText(this, "Sedang mencari lokasi... Tunggu sebentar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }
        checkGpsStatus()
    }

    private fun checkGpsStatus() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (isGpsOn) {
            binding.cvWarning.isVisible = false
            startLocationUpdates()
        } else {
            binding.cvWarning.isVisible = true
           updateButtonState(false)
        }
    }

    private fun startLocationUpdates() {
        val provider = GpsMyLocationProvider(this)
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER)

        myLocationOverlay = MyLocationNewOverlay(provider, binding.mapview)
        myLocationOverlay?.enableMyLocation()
        myLocationOverlay?.enableFollowLocation()

        myLocationOverlay?.runOnFirstFix {
            runOnUiThread {
                val loc = myLocationOverlay?.myLocation
                if (loc != null) {
                    lastLat = loc.latitude
                    lastLong = loc.longitude

                    binding.mapview.controller.animateTo(loc)

                    updateButtonState(true)
                }
            }
        }
        binding.mapview.overlays.add(myLocationOverlay)
    }

    override fun onResume() {
        super.onResume()
        binding.mapview.onResume()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkGpsStatus()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.mapview.onPause()
        myLocationOverlay?.disableMyLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkPermissions()
        }
    }
}