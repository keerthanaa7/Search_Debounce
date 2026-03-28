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

// Search with Debounce & Cancellation: Use Flow.debounce and distinctUntilChanged.
sealed interface UIStateDebounce {
    object Loading : UIStateDebounce
    data class Success(val stocklist: List<Stock>) : UIStateDebounce
    data class Error(val message: String) : UIStateDebounce
}

class StockViewModelDebounce(private val repository: StockRepository) : ViewModel() {
    private val uistatemutable = MutableStateFlow<UIStateDebounce>(UIStateDebounce.Loading)
    val uistate = uistatemutable.asStateFlow()

    private val searchquerymutable = MutableStateFlow("")
    val searchquery = searchquerymutable.asStateFlow()

   // Search with Debounce & Cancellation: Use Flow.debounce and distinctUntilChanged.
    val filteredlist: StateFlow<UIStateDebounce> = searchquerymutable.debounce(300L).distinctUntilChanged()
        .combine(uistatemutable){search, state ->
            if(state is UIStateDebounce.Success){
                if(search.isEmpty()){
                    state
                }else{
                    UIStateDebounce.Success(state.stocklist.filter {
                        it.ticker.contains(search) || it.name.contains(search)
                    })
                }
            }else{
                state
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, UIStateDebounce.Loading)

    fun UpdateQuery(newvalue: String){
        searchquerymutable.value = newvalue
    }


    init {
        getStockList()
    }

    fun getStockList() {
        viewModelScope.launch {
            uistatemutable.value = UIStateDebounce.Loading
            try {
                val portfolio = repository.getPortFolio()
                if (portfolio.stocks.isEmpty()) {
                    uistatemutable.value = UIStateDebounce.Error("stock list empty")
                } else {
                    uistatemutable.value = UIStateDebounce.Success(portfolio.stocks)
                }
            } catch (e: Exception) {
                uistatemutable.value = UIStateDebounce.Error("exception fetching stock")
            }

        }

    }
}