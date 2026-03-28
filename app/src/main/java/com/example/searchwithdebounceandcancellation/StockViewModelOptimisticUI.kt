package com.example.searchwithdebounceandcancellation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UIStateOptimisticUI {
    object Loading : UIStateOptimisticUI
    data class Success(val stocklist: List<Stock>) : UIStateOptimisticUI
    data class Error(val message: String) : UIStateOptimisticUI
}

class StockViewModelOptimisticUI(private val repository: StockRepository) : ViewModel() {
    private val _uistate = MutableStateFlow<UIStateOptimisticUI>(UIStateOptimisticUI.Loading)
    val uistate = _uistate.asStateFlow()

    init {
        getStockList()
    }

    fun getStockList() {
        viewModelScope.launch {
            _uistate.value = UIStateOptimisticUI.Loading
            try {
                val portfolio = repository.getPortFolio()
                _uistate.value = UIStateOptimisticUI.Success(portfolio.stocks)
            } catch (e: Exception) {
                _uistate.value = UIStateOptimisticUI.Error("Exception fetching stocks")
            }
        }
    }

    fun toggleFavorite(targetStock: Stock) {
        val currentState = _uistate.value
        if (currentState !is UIStateOptimisticUI.Success) return

        // 1. Snapshot: Save current list for potential rollback
        val originalList = currentState.stocklist

        // 2. Optimistic Update: Change UI immediately
        val updatedList = originalList.map {
            if (it.ticker == targetStock.ticker) it.copy(isFavorite = !it.isFavorite) else it
        }
        _uistate.value = UIStateOptimisticUI.Success(updatedList)

        viewModelScope.launch {
            try {
                // 3. Network Call: Attempt to update backend
                repository.updateFavoriteOnServer(targetStock.ticker, !targetStock.isFavorite)
            } catch (e: Exception) {
                // 4. Rollback: Restore original state on failure
                val latestState = _uistate.value
                if (latestState is UIStateOptimisticUI.Success) {
                    _uistate.value = UIStateOptimisticUI.Success(originalList)
                }
                // In a real app, trigger a Side Effect here to show a Toast
            }
        }
    }
}