package com.example.ordermanagementapp.ui.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ordermanagementapp.databinding.ActivityProfileBinding
import com.example.ordermanagementapp.data.network.RetrofitClient
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService(this@ProfileActivity).getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    binding.tvProfileEmail.text = "Email: ${user.email}"
                    binding.tvProfileRole.text = "Rola w systemie: ${user.role}"
                } else {
                    Toast.makeText(this@ProfileActivity, "Błąd pobierania profilu", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Błąd sieci: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            sharedPref.edit().clear().commit()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}