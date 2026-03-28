package com.example.searchwithdebounceandcancellation

import androidx.compose.ui.unit.Constraints
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    }

    val stockapi: StockApiService by lazy {
        retrofit.create(StockApiService::class.java)
    }
}