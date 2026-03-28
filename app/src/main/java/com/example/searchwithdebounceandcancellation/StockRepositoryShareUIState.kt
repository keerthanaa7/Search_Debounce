package com.example.searchwithdebounceandcancellation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StockRepositorySharedUIState() {

    // 1. Internal Source of Truth (Private to prevent external modification)
    private val _stocks = MutableStateFlow<List<Stock>>(emptyList())

    // 2. Public Read-Only Stream for ViewModels to observe
    val stocks: StateFlow<List<Stock>> = _stocks.asStateFlow()

    /**
     * Fetches fresh data from the network and updates our Source of Truth.
     */
    suspend fun fetchPortfolio() {
        try {
            val response = RetrofitInstance.stockapi.getPortfolio() // Real Retrofit Call
            _stocks.value = response.stocks
        } catch (e: Exception) {
            // Log error or rethrow to be handled by the ViewModel
            throw e
        }
    }

    /**
     * Updates the state in-memory across the entire app.
     */
    fun toggleFavorite(ticker: String) {
        _stocks.value = _stocks.value.map {
            if (it.ticker == ticker) it.copy(isFavorite = !it.isFavorite) else it
        }
    }
}