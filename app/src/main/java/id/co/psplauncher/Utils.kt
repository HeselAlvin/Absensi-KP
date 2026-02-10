package id.co.psplauncher

import android.app.Activity
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import id.co.psplauncher.data.network.Resource

object Utils {

    fun Activity.handleApiError(
        view: View,
        failure: Resource.Failure,
    ) {
        when {
            failure.isNetworkError -> {
                view.snackbar("Gagal koneksi. Silahkan check kembali koneksi jaringan anda")
            }
            failure.errorCode == 401 -> {
                // Tambahkan logika pindah ke LoginActivity jika session habis
            }
            else -> {
                // Kita ambil error mentah dari server supaya kamu tahu apa yang kurang (Misal: kurang user_id)
                val error = failure.errorBody?.string() ?: "Terjadi kesalahan sistem"
                view.snackbar(error)
            }
        }
    }

    fun View.snackbar(message: String) {
        val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        val sView = snackbar.view // Inisialisasi view snackbar

        // Casting ke FrameLayout agar bisa mengatur margin dan posisi
        val layoutParams = sView.layoutParams as FrameLayout.LayoutParams

        // SOLUSI ANTI MERAH: Cari ID snackbar_text menggunakan string nama paket
        val snackId = context.resources.getIdentifier("snackbar_text", "id", "com.google.android.material")
        val snackTextView = sView.findViewById<TextView>(snackId)

        layoutParams.setMargins(10, 10, 10, 10)
        layoutParams.gravity = Gravity.BOTTOM

        sView.setPadding(10, 10, 10, 10)
        snackTextView?.maxLines = 5 // Supaya pesan error panjang dari server kelihatan semua
        sView.layoutParams = layoutParams
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        snackbar.show()
    }

    fun View.visible(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
        Intent(this, activity).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }
}