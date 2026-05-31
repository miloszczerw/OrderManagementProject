package com.example.ordermanagementapp.ui.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ordermanagementapp.databinding.ActivityRegisterBinding
import com.example.ordermanagementapp.data.model.RegisterRequest
import com.example.ordermanagementapp.data.network.RetrofitClient
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val email = binding.etRegisterEmail.text.toString()
            val company = binding.etRegisterCompanyName.text.toString()
            val password = binding.etRegisterPassword.text.toString()

            if (email.isNotEmpty() && company.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.getApiService(this@RegisterActivity).register(RegisterRequest(email, password, company))
                        if (response.isSuccessful) {
                            Toast.makeText(this@RegisterActivity, "Rejestracja udana! Zaloguj się.", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, "Błąd rejestracji: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@RegisterActivity, "Błąd sieci: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this@RegisterActivity, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }
    }
}