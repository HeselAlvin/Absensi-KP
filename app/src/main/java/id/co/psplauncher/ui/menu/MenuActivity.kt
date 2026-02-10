package id.co.psplauncher.ui.menu

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.co.psplauncher.data.network.RemoteDataSource
import id.co.psplauncher.data.network.absensi.AbsensiAPI
import id.co.psplauncher.data.network.model.ModelHistory
import id.co.psplauncher.databinding.ActivityMenuBinding
import id.co.psplauncher.ui.history.HistoryAdapter
import id.co.psplauncher.ui.main.MainActivity
import id.co.psplauncher.ui.maps.MapsAbsenActivity

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var viewModel: MenuViewModel
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        setupButtons()

        setupRecyclerView()

        val nameFromLogin = intent.getStringExtra("USER_NAME") ?: "User"
        binding.txtName.text = nameFromLogin

        observeData()

        viewModel.getRiwayatAbsensi(nameFromLogin)
    }

    private fun setupButtons() {
        binding.btnAbsen.setOnClickListener {
            val intent = Intent(this, MapsAbsenActivity::class.java)
            intent.putExtra("TIPE_ABSEN", "MASUK")
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun setupViewModel() {
        val remoteDataSource = RemoteDataSource(this)
        val apiService = remoteDataSource.buildApi(AbsensiAPI::class.java)
        val factory = MenuViewModel.MenuViewModelFactory(apiService)
        viewModel = ViewModelProvider(this, factory)[MenuViewModel::class.java]
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(arrayListOf())

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(this@MenuActivity)
            adapter = historyAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeData() {
        viewModel.historyList.observe(this) { listData ->
            if (listData != null) {
                val arrayList = ArrayList<ModelHistory>()
                arrayList.addAll(listData)
                historyAdapter.setHistoryList(arrayList)
                historyAdapter.notifyDataSetChanged()

                if (listData.isEmpty()) {
                    Toast.makeText(this, "Riwayat kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.errorMessage.observe(this) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
        }
    }

    private fun logoutUser() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}