package com.example.ordermanagementapp.ui.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ordermanagementapp.databinding.ActivityStatsBinding
import com.example.ordermanagementapp.data.network.RetrofitClient
import kotlinx.coroutines.launch

class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService(this@StatsActivity).getOrderStats()
                if (response.isSuccessful && response.body() != null) {
                    val stats = response.body()!!

                    binding.tvTotalSales.text = String.format("%.2f zł", stats.totalSales)
                    binding.tvPendingCount.text = "Oczekujące (Pending): ${stats.pendingCount}"
                    binding.tvShippedCount.text = "Wysłane (Shipped): ${stats.shippedCount}"
                    binding.tvCompletedCount.text = "Zakończone (Completed): ${stats.completedCount}"

                    val totalOrders = stats.pendingCount + stats.shippedCount + stats.completedCount
                    if (totalOrders > 0) {
                        binding.root.post {
                            val maxWidth = binding.root.width - 128

                            val pendingWidth = (stats.pendingCount.toFloat() / totalOrders * maxWidth).toInt()
                            val shippedWidth = (stats.shippedCount.toFloat() / totalOrders * maxWidth).toInt()
                            val completedWidth = (stats.completedCount.toFloat() / totalOrders * maxWidth).toInt()

                            binding.barPending.layoutParams = binding.barPending.layoutParams.apply { width = pendingWidth }
                            binding.barShipped.layoutParams = binding.barShipped.layoutParams.apply { width = shippedWidth }
                            binding.barCompleted.layoutParams = binding.barCompleted.layoutParams.apply { width = completedWidth }
                        }
                    }
                } else {
                    Toast.makeText(this@StatsActivity, "Błąd pobierania statystyk", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@StatsActivity, "Błąd sieci: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}