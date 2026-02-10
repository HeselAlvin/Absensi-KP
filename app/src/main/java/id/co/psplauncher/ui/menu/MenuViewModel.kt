package id.co.psplauncher.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.co.psplauncher.data.network.absensi.AbsensiAPI
import id.co.psplauncher.data.network.model.ModelHistory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MenuViewModel(private val apiService: AbsensiAPI) : ViewModel() {

    private val _historyList = MutableLiveData<List<ModelHistory>?>()
    val historyList: LiveData<List<ModelHistory>?> = _historyList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = _currentTime

    init {
        startLiveClock() // Jam jalan otomatis
    }

    fun getRiwayatAbsensi(namaUser: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = apiService.getAbsenHistory()

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!

                    if (!responseBody.data.isNullOrEmpty()) {

                        val mappedList = responseBody.data.map { itemServer ->

                            val (tgl, jam) = formatDateTime(itemServer.datetime)

                            val finalName = if (itemServer.name.isNullOrEmpty()) namaUser else itemServer.name

                            ModelHistory(
                                name = finalName,
                                date = tgl,
                                time = jam,
                                status = itemServer.status ?: "-",
                                _id = itemServer._id,
                                datetime = itemServer.datetime ?: "",
                                face_image_url = itemServer.face_image_url ?: "",
                                latitude = itemServer.latitude?.toDouble() ?: 0.0,
                                longitude = itemServer.longitude?.toDouble() ?: 0.0,
                                user_id = itemServer.user_id.toString()
                            )
                        }
                        _historyList.postValue(mappedList)
                    } else {
                        _errorMessage.postValue("Belum ada riwayat absensi.")
                    }
                } else {
                    _errorMessage.postValue("Gagal memuat: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun formatDateTime(isoString: String?): Pair<String, String> {
        if (isoString.isNullOrEmpty()) return Pair("-", "-")
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            val date = inputFormat.parse(isoString) ?: return Pair(isoString, "")

            val formatTanggal = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
            val formatJam = SimpleDateFormat("HH:mm", Locale.getDefault())

            Pair(formatTanggal.format(date), formatJam.format(date))
        } catch (e: Exception) {
            Pair(isoString, "")
        }
    }

    private fun startLiveClock() {
        viewModelScope.launch {
            while (true) {
                val sdf = SimpleDateFormat("HH:mm:ss 'WIB'", Locale("id", "ID"))
                _currentTime.postValue(sdf.format(Date()))
                delay(1000)
            }
        }
    }

    class MenuViewModelFactory(private val apiService: AbsensiAPI) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
                return MenuViewModel(apiService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


