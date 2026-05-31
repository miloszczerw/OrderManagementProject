package com.example.ordermanagementapp.data.model

data class Order(
    val id: Int = 0,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val userId: Int? = null,
    val status: String? = null
)