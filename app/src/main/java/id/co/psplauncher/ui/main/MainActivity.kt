package id.co.psplauncher.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.Utils.handleApiError
import id.co.psplauncher.Utils.visible
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.databinding.ActivityLoginBinding
import id.co.psplauncher.ui.main.main.MainViewModel
import id.co.psplauncher.ui.menu.MenuActivity

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        with(binding) {
            btnLogin.setOnClickListener {
                val username = editUsername.text.toString().trim()
                val password = editPassword.text.toString().trim()

                when {
                    username.isEmpty() -> {
                        editUsername.error = "Username tidak boleh kosong"
                    }
                    password.isEmpty() -> {
                        editPassword.error = "Password tidak boleh kosong"
                    }
                    else -> {
                        viewModel.login(username, password)
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.loginResponse.observe(this) { resource ->
            with(binding) {
                when (resource) {
                    is Resource.Loading -> {
                        progressBar.visible(true)
                        btnLogin.isEnabled = false
                    }

                    is Resource.Success -> {
                        progressBar.visible(false)
                        btnLogin.isEnabled = true
                        Toast.makeText(this@MainActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, MenuActivity::class.java)
                        val usernameInput = binding.editUsername.text.toString()
                        intent.putExtra("USER_NAME", usernameInput)
                        intent.putExtra("USER_ID", usernameInput)
                        startActivity(intent)
                        finish()
                    }
                    is Resource.Failure -> {
                        progressBar.visible(false)
                        btnLogin.isEnabled = true
                        handleApiError(root, resource)
                    }
                }
            }
        }
    }
}