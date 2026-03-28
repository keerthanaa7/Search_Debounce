package com.example.searchwithdebounceandcancellation


import com.google.gson.annotations.SerializedName

data class Stock(
    @SerializedName("currency")
    val currency: String,
    @SerializedName("current_price_cents")
    val currentPriceCents: Int,
    @SerializedName("current_price_timestamp")
    val currentPriceTimestamp: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("ticker")
    val ticker: String,
    val isFavorite: Boolean = false
)