package com.example.searchwithdebounceandcancellation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Exception

sealed interface UIState {
    object Loading : UIState
    data class Success(val stocklist: List<Stock>) : UIState
    data class Error(val message: String) : UIState
}

class StockViewModel(private val repository: StockRepository) : ViewModel() {
    private val uistatemutable = MutableStateFlow<UIState>(UIState.Loading)
    val uistate = uistatemutable.asStateFlow()

    init {
        getStockList()
    }

    fun getStockList() {
        viewModelScope.launch {
            uistatemutable.value = UIState.Loading
            try {
                val portfolio = repository.getPortFolio()
                if (portfolio.stocks.isEmpty()) {
                    uistatemutable.value = UIState.Error("stock list empty")
                } else {
                    uistatemutable.value = UIState.Success(portfolio.stocks)
                }
            } catch (e: Exception) {
                uistatemutable.value = UIState.Error("exception fetching stock")
            }

        }

    }
}