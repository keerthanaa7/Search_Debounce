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
//Multi-Filter UI: Combine multiple state sources (Price, Category) into one list.
sealed interface UIStateFilter {
    object Loading : UIStateFilter
    data class Success(val stocklist: List<Stock>) : UIStateFilter
    data class Error(val message: String) : UIStateFilter
}

class StockViewModelMultiStateFilter(private val repository: StockRepository) : ViewModel() {
    private val uistatemutable = MutableStateFlow<UIStateFilter>(UIStateFilter.Loading)
    val uistate = uistatemutable.asStateFlow()

    private val searchquerymutable = MutableStateFlow(StockFilter())
    val searchquery = searchquerymutable.asStateFlow()

    val filteredlist: StateFlow<UIStateFilter> = combine(uistatemutable, searchquerymutable){
        state, searchfilter ->
        if(state is UIStateFilter.Success){
            val finallist = state.stocklist.filter { stock->
                val searchmatch = stock.name.contains(searchfilter.query, ignoreCase = true)|| stock.ticker.contains(searchfilter.query, ignoreCase = true)
                val matchcurrency = searchfilter.currency ==null || stock.currency == searchfilter.currency
                val matchprice = stock.currentPriceCents <= searchfilter.maxpriceCents
                searchmatch && matchcurrency && matchprice
            }
            UIStateFilter.Success(finallist)
        }else{
            state
        }
        }.debounce(300L).distinctUntilChanged().stateIn(viewModelScope, SharingStarted.Lazily, UIStateFilter.Loading)

    fun UpdateQuery(newquery: String){
        searchquerymutable.value = searchquerymutable.value.copy(query = newquery)
    }

    fun updateCurrency(latestcurrency: String?){
        searchquerymutable.value = searchquerymutable.value.copy(currency = latestcurrency)
    }

    fun updateMaxPriceCents(price: Int){
        searchquerymutable.value = searchquerymutable.value.copy(maxpriceCents = price)
    }


    init {
        getStockList()
    }

    fun getStockList() {
        viewModelScope.launch {
            uistatemutable.value = UIStateFilter.Loading
            try {
                val portfolio = repository.getPortFolio()
                if (portfolio.stocks.isEmpty()) {
                    uistatemutable.value = UIStateFilter.Error("stock list empty")
                } else {
                    uistatemutable.value = UIStateFilter.Success(portfolio.stocks)
                }
            } catch (e: Exception) {
                uistatemutable.value = UIStateFilter.Error("exception fetching stock")
            }

        }

    }
}