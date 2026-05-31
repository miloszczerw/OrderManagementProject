package com.example.ordermanagementapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ordermanagementapp.data.model.Order
import com.example.ordermanagementapp.data.network.RetrofitClient
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> get() = _orders

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchOrders(context: Context) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.getApiService(context.applicationContext).getOrders()
                if (response.isSuccessful) {
                    _orders.postValue(response.body() ?: emptyList())
                } else {
                    _error.postValue("Błąd serwera: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Błąd sieci: ${e.message}")
            }
        }
    }

    fun addOrder(context: Context, order: Order, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.getApiService(context.applicationContext).createOrder(order)

                if (response.isSuccessful) {
                    onResult(true, "Zamówienie dodane do Azure!")
                    fetchOrders(context)
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    val detailedError = "Kod: ${response.code()}, Opis: $errorBody"
                    android.util.Log.e("API_ERROR", detailedError)
                    onResult(false, detailedError)
                }
            } catch (e: Exception) {
                val networkError = "Wyjątek: ${e.message}"
                android.util.Log.e("API_ERROR", networkError, e)
                onResult(false, networkError)
            }
        }
    }
    fun updateStatus(context: Context, orderId: Int, newStatus: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val formattedStatus = "\"$newStatus\""

                val response = RetrofitClient.getApiService(context.applicationContext)
                    .updateOrderStatus(orderId, formattedStatus)

                if (response.isSuccessful) {
                    onResult(true)
                    fetchOrders(context)
                } else {
                    _error.postValue("Błąd zmiany statusu: ${response.code()}")
                    onResult(false)
                }
            } catch (e: Exception) {
                _error.postValue("Błąd sieci: ${e.message}")
                onResult(false)
            }
        }
    }

    fun deleteOrder(context: Context, orderId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.getApiService(context.applicationContext).deleteOrder(orderId)
                if (response.isSuccessful) {
                    onResult(true)
                    fetchOrders(context)
                } else {
                    _error.postValue("Błąd usuwania zamówienia: ${response.code()}")
                    onResult(false)
                }
            } catch (e: Exception) {
                _error.postValue("Błąd sieci: ${e.message}")
                onResult(false)
            }
        }
    }
}