package com.example.ordermanagementapp.data.model

import com.google.gson.annotations.SerializedName

data class OrderStats(
    @SerializedName("totalSales") val totalSales: Double,
    @SerializedName("pendingCount") val pendingCount: Int,
    @SerializedName("shippedCount") val shippedCount: Int,
    @SerializedName("completedCount") val completedCount: Int
)