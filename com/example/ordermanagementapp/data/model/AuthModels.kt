package com.example.ordermanagementapp.data.model
import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val email: String,
    val password: String,
    val companyName: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("token")
    val token: String
)

data class RegisterResponse(
    val message: String
)