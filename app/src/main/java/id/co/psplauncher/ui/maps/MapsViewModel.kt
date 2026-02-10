package id.co.psplauncher.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.response.LocationResponse
import id.co.psplauncher.data.network.absensi.AbsensiRepository
// PENTING: Import class Resource/NetworkResult kamu disini!
// Biasanya ada di package .utils atau .data.network

import kotlinx.coroutines.launch

class MapsViewModel(private val repository: AbsensiRepository) : ViewModel() {

    private val _locationResult = MutableLiveData<Resource<LocationResponse>>()
    val locationResult: LiveData<Resource<LocationResponse>> = _locationResult

    fun checkLocation(lat: Double, lng: Double) {

        viewModelScope.launch {
            try {
                val result = repository.checkLocation(lat, lng)
                _locationResult.value = result

            } catch (e: Exception) {

            }
        }
    }
}