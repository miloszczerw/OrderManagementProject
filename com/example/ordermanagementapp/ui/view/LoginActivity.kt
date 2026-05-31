package com.example.ordermanagementapp.ui.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ordermanagementapp.databinding.ActivityLoginBinding
import com.example.ordermanagementapp.data.model.LoginRequest
import com.example.ordermanagementapp.data.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.getApiService(this@LoginActivity).login(LoginRequest(email, password))

                        if (response.isSuccessful) {
                            val token = response.body()?.token

                            if (!token.isNullOrEmpty()) {
                                val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

                                sharedPref.edit().putString("jwt_token", token).commit()

                                try {
                                    val jwt = com.auth0.android.jwt.JWT(token)

                                    val roleClaim = jwt.getClaim("role").asString()
                                        ?: jwt.getClaim("http://schemas.microsoft.com/ws/2008/06/identity/claims/role").asString()
                                        ?: "Customer"

                                    sharedPref.edit().putString("user_role", roleClaim).commit()
                                    android.util.Log.d("ROLE_DEBUG", "Zapisano rolę z JWT: $roleClaim")
                                } catch (e: Exception) {
                                    sharedPref.edit().putString("user_role", "Customer").commit()
                                    android.util.Log.e("ROLE_DEBUG", "Błąd dekodowania JWT, ustawiono Customer", e)
                                }

                                Toast.makeText(this@LoginActivity, "Zalogowano pomyślnie!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "Błąd: Serwer nie zwrócił tokenu", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string() ?: "Nieznany błąd serwera"
                            Toast.makeText(this@LoginActivity, "Serwer zwrócił: $errorBody", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@LoginActivity, "Błąd sieci: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this@LoginActivity, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }
    }
}