package com.example.searchwithdebounceandcancellation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Exception
// Countdown / Polling Task: A lifecycle-aware timer using LaunchedEffect and while(true).
sealed interface UIStatelive {
    object Loading : UIStatelive
    data class Success(val stocklist: List<Stock>) : UIStatelive
    data class Error(val message: String) : UIStatelive
}

class StockViewModelLivePrice(private val repository: StockRepository) : ViewModel() {
    private val uistatemutable = MutableStateFlow<UIStatelive>(UIStatelive.Loading)
    val uistate = uistatemutable.asStateFlow()

    private val searchquerymutable = MutableStateFlow("")
    val searchquery = searchquerymutable.asStateFlow()

    val liveprice: StateFlow<UIStatelive> = flow {
        while(true){
            val price = repository.getPortFolio()
            emit(UIStatelive.Success(price.stocks))
            delay(5000)
        }
    }.flowOn(Dispatchers.IO).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UIStatelive.Loading)

    val filteredlist: StateFlow<UIStatelive> = searchquerymutable.debounce(300L).distinctUntilChanged()
        .combine(uistatemutable){search, state ->
            if(state is UIStatelive.Success){
                if(search.isEmpty()){
                    state
                }else{
                    UIStatelive.Success(state.stocklist.filter {
                        it.ticker.contains(search) || it.name.contains(search)
                    })
                }
            }else{
                state
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, UIStatelive.Loading)

    fun UpdateQuery(newvalue: String){
        searchquerymutable.value = newvalue
    }


    init {
        getStockList()
    }

    fun getStockList() {
        viewModelScope.launch {
            uistatemutable.value = UIStatelive.Loading
            try {
                val portfolio = repository.getPortFolio()
                if (portfolio.stocks.isEmpty()) {
                    uistatemutable.value = UIStatelive.Error("stock list empty")
                } else {
                    uistatemutable.value = UIStatelive.Success(portfolio.stocks)
                }
            } catch (e: Exception) {
                uistatemutable.value = UIStatelive.Error("exception fetching stock")
            }

        }

    }
}