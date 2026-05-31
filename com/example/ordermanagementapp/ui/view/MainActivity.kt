package com.example.ordermanagementapp.ui.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ordermanagementapp.databinding.ActivityMainBinding
import com.example.ordermanagementapp.ui.viewmodel.OrderViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: OrderViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderAdapter = OrderAdapter(
            emptyList(),
            onItemClick = { order ->
                pokazOknoUsuwania(order.id, order.productName ?: "Produkt")
            },
            onItemLongClick = { order ->
                val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val userRole = sharedPref.getString("user_role", "Worker")

                if (userRole == "Admin") {
                    pokazOknoZmianyStatusu(order.id)
                }
            }
        )

        binding.rvOrders.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.rvOrders.adapter = orderAdapter

        binding.btnOpenProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        viewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userRole = sharedPref.getString("user_role", "Worker") ?: "Worker"

        if (userRole == "Admin") {
            binding.btnOpenStats.visibility = android.view.View.VISIBLE
            binding.btnOpenStats.setOnClickListener {
                startActivity(Intent(this, StatsActivity::class.java))
            }
        }

        viewModel.orders.observe(this) { ordersList ->
            if (ordersList.isNotEmpty()) {
                orderAdapter.updateData(ordersList)
            } else {
                Toast.makeText(this, "Brak zamówień do wyświetlenia", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }

        binding.btnFetchOrders.setOnClickListener {
            viewModel.fetchOrders(this)
        }

        binding.btnOpenAddOrder.setOnClickListener {
            startActivity(Intent(this, AddOrderActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchOrders(this)
    }

    private fun pokazOknoZmianyStatusu(orderId: Int) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Zmień status zamówienia #$orderId")

        val options = arrayOf("Pending", "Shipped", "Completed")
        var selectedStatus = options[0]

        val spinner = android.widget.Spinner(this).apply {
            setPadding(50, 40, 50, 40)
            adapter = android.widget.ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, options)
            onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                    selectedStatus = options[position]
                }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
        }

        builder.setView(spinner)
        builder.setPositiveButton("Zaktualizuj") { _, _ ->
            viewModel.updateStatus(this, orderId, selectedStatus) { success ->
                if (success) {
                    Toast.makeText(this, "Status zaktualizowany w Azure!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Błąd aktualizacji statusu", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("Anuluj", null)
        builder.show()
    }

    private fun pokazOknoUsuwania(orderId: Int, productName: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Usuń zamówienie")
            .setMessage("Czy na pewno chcesz usunąć zamówienie #$orderId ($productName)?")
            .setPositiveButton("Usuń") { _, _ ->
                viewModel.deleteOrder(this, orderId) { success ->
                    if (success) {
                        Toast.makeText(this, "Zamówienie zostało usunięte z Azure", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Nie udało się usunąć zamówienia", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }
}