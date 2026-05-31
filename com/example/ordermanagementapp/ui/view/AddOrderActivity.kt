package com.example.ordermanagementapp.ui.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ordermanagementapp.data.model.Order
import com.example.ordermanagementapp.databinding.ActivityAddOrderBinding
import com.example.ordermanagementapp.ui.viewmodel.OrderViewModel

class AddOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddOrderBinding
    private lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        binding.btnSaveOrder.setOnClickListener {
            val prodName = binding.etProductName.text.toString().trim()
            val qtyStr = binding.etQuantity.text.toString().trim()
            val priceStr = binding.etPrice.text.toString().trim()

            if (prodName.isNotEmpty() && qtyStr.isNotEmpty() && priceStr.isNotEmpty()) {

                val order = Order(
                    id = 0,
                    productName = prodName,
                    quantity = qtyStr.toInt(),
                    price = priceStr.toDouble(),
                    userId = null
                )

                orderViewModel.addOrder(this, order) { success, message ->
                    runOnUiThread {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        if (success) {
                            finish()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }
    }
}