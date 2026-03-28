package com.example.searchwithdebounceandcancellation

data class StockFilter(
    val query: String="",
    val currency: String?=null,
    val maxpriceCents: Int = 100000
)
