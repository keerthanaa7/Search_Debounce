package com.example.searchwithdebounceandcancellation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Hahve consistent UI by using a repository pattern
class RepoSharedUIStockViewModel(private val repository: StockRepositorySharedUIState) : ViewModel() {

    // Transform the Repository's StateFlow into UIState
    val uistate: StateFlow<UIState> = repository.stocks
        .map { list ->
            if (list.isEmpty()) UIState.Loading
            else UIState.Success(list)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UIState.Loading
        )

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                repository.fetchPortfolio()
            } catch (e: Exception) {
                // Handle network error state here if needed
            }
        }
    }

    fun toggleFavorite(ticker: String) {
        repository.toggleFavorite(ticker)
    }
}