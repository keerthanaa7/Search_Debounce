package com.example.searchwithdebounceandcancellation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
// Pagination / Endless Scroll: Implement "Load More" at the bottom of a LazyColumn.
sealed interface UIStatePagination {
    object Loading : UIStatePagination
    data class Success(
        val stocklist: List<Stock>,
        val isAppending: Boolean = false,
        val isLastPage: Boolean = false
    ) : UIStatePagination
    data class Error(val message: String) : UIStatePagination
}

class StockViewModelPagination(private val repository: StockRepository) : ViewModel() {
    private val _uistate = MutableStateFlow<UIStatePagination>(UIStatePagination.Loading)
    val uistate = _uistate.asStateFlow()

    private var allStocks = listOf<Stock>()
    private var currentIndex = 0
    private val PAGE_SIZE = 12

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                // Fetch the full 1M+ list once
                val portfolio = repository.getPortFolio()
                allStocks = portfolio.stocks

                // Show the first "page"
                val initialPage = allStocks.take(PAGE_SIZE)
                _uistate.value = UIStatePagination.Success(initialPage)
                currentIndex = PAGE_SIZE
            } catch (e: Exception) {
                _uistate.value = UIStatePagination.Error("Failed to fetch data")
            }
        }
    }

    fun loadMore() {
        val state = _uistate.value
        // Guard: Don't load if already appending, or if we hit the end of the list
        if (state is UIStatePagination.Success && !state.isAppending && currentIndex < allStocks.size) {
            viewModelScope.launch {
                _uistate.value = state.copy(isAppending = true)

                delay(3000) // Simulated delay for interview visibility

                val nextLimit = (currentIndex + PAGE_SIZE).coerceAtMost(allStocks.size)
                val nextItems = allStocks.subList(currentIndex, nextLimit)

                _uistate.value = UIStatePagination.Success(
                    stocklist = state.stocklist + nextItems,
                    isAppending = false,
                    isLastPage = nextLimit >= allStocks.size
                )
                currentIndex = nextLimit
            }
        }
    }
}