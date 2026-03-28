package com.example.searchwithdebounceandcancellation


import com.google.gson.annotations.SerializedName

data class Portfolio(
    @SerializedName("stocks")
    val stocks: List<Stock>
)