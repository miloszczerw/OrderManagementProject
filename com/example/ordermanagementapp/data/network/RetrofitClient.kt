package com.example.ordermanagementapp.data.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://ordermanagementapinew-e6dueddba8gff6hw.polandcentral-01.azurewebsites.net/"

    private var apiService: OrderApiService? = null

    fun getApiService(context: Context): OrderApiService {
        if (apiService == null) {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token = sharedPref.getString("jwt_token", "") ?: ""

                    android.util.Log.d("RETROFIT_DEBUG", "Wysyłany token: Bearer $token")

                    val newRequest = chain.request().newBuilder()
                    if (token.isNotEmpty()) {
                        newRequest.addHeader("Authorization", "Bearer $token")
                    }
                    chain.proceed(newRequest.build())
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(OrderApiService::class.java)
        }
        return apiService!!
    }
}