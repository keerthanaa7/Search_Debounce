package com.example.searchwithdebounceandcancellation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SharedStock(
    val ticker: String,
    val name: String,
    val isFavorite: Boolean = false // New field
)

class SharedStockViewModel(private val repository: StockRepository) : ViewModel() {
    private val _uistate = MutableStateFlow<UIState>(UIState.Loading)
    val uistate = _uistate.asStateFlow()

    init { getStockList() }

    fun getStockList() {
        viewModelScope.launch {
            _uistate.value = UIState.Loading
            try {
                val portfolio = repository.getPortFolio()
                _uistate.value = if (portfolio.stocks.isEmpty()) UIState.Error("Empty")
                else UIState.Success(portfolio.stocks)
            } catch (e: Exception) {
                _uistate.value = UIState.Error("Exception fetching stocks")
            }
        }
    }

    // THE SYNC ENGINE: Updates the list in-place
    fun toggleFavorite(ticker: String) {
        val currentState = _uistate.value
        if (currentState is UIState.Success) {
            val updatedList = currentState.stocklist.map {
                if (it.ticker == ticker) it.copy(isFavorite = !it.isFavorite) else it
            }
            _uistate.value = UIState.Success(updatedList)
        }
    }
}