package com.example.ordermanagementapp.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ordermanagementapp.data.model.Order
import com.example.ordermanagementapp.databinding.ItemOrderBinding

class OrderAdapter(
    private var orders: List<Order>,
    private val onItemClick: (Order) -> Unit,
    private val onItemLongClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        with(holder.binding) {
            tvOrderTitle.text = "${order.productName} (ID: #${order.id})"

            val currentStatus = order.status?.lowercase() ?: "pending"
            tvOrderStatus.text = currentStatus.uppercase()

            tvOrderDetails.text = "Ilość: ${order.quantity} | Cena: ${order.price} zł"

            val total = order.price * order.quantity
            tvOrderTotal.text = "Suma: $total zł"

            when (currentStatus) {
                "completed" -> tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
                "shipped" -> tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#1565C0"))
                else -> tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#E65100"))
            }

            root.setOnLongClickListener {
                onItemLongClick(order)
                true
            }
            root.setOnClickListener {
                onItemClick(order)
            }

            root.setOnLongClickListener {
                onItemLongClick(order)
                true
            }
        }
    }

    override fun getItemCount(): Int = orders.size

    fun updateData(newOrders: List<Order>) {
        this.orders = newOrders
        notifyDataSetChanged()
    }
}