package com.example.ordermanagementapp.data.network

import com.example.ordermanagementapp.data.model.Order
import com.example.ordermanagementapp.data.model.LoginRequest
import com.example.ordermanagementapp.data.model.LoginResponse
import com.example.ordermanagementapp.data.model.RegisterRequest
import com.example.ordermanagementapp.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.*

interface OrderApiService {

    @GET("api/orders")
    suspend fun getOrders(): Response<List<Order>>

    @POST("api/orders")
    suspend fun createOrder(@Body order: Order): Response<Order>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @PATCH("api/orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: Int,
        @Body newStatus: String
    ): retrofit2.Response<com.example.ordermanagementapp.data.model.Order>

    @GET("api/auth/profile")
    suspend fun getUserProfile(): retrofit2.Response<com.example.ordermanagementapp.data.model.User>

    @DELETE("api/orders/{id}")
    suspend fun deleteOrder(
        @Path("id") id: Int
    ): retrofit2.Response<okhttp3.ResponseBody>

    @GET("api/orders/stats")
    suspend fun getOrderStats(): retrofit2.Response<com.example.ordermanagementapp.data.model.OrderStats>
}