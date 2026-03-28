package com.example.searchwithdebounceandcancellation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PortfolioScreenPagination(
    stockViewModel: StockViewModelPagination,
    onStockClick: (String) -> Unit
) {
    // We observe the main uistate which now contains our paged list
    val uistate by stockViewModel.uistate.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            when (val state = uistate) {
                is UIStatePagination.Loading -> {
                    CircularProgressIndicator()
                }
                is UIStatePagination.Error -> {
                    Text(text = state.message, color = Color.Red)
                }
                is UIStatePagination.Success -> {
                    // Pass the whole state and the ViewModel to handle the scroll trigger
                    PortfolioSubScreenPagination(
                        state = state,
                        stockViewModel = stockViewModel,
                        onStockClick = onStockClick
                    )
                }
            }
        }
    }
}

@Composable
fun PortfolioSubScreenPagination(
    state: UIStatePagination.Success,
    stockViewModel: StockViewModelPagination,
    onStockClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // 1. Render the main list
        // Use itemsIndexed to detect when we reach the end
        itemsIndexed(
            items = state.stocklist,
            key = { _, stock -> stock.ticker }
        ) { index, stockitem ->

            // TRIGGER: If this is the last item in the current paged list,
            // and we aren't already loading more, and there is more to load...
            if (index >= state.stocklist.size - 1 && !state.isAppending && !state.isLastPage) {
                stockViewModel.loadMore()
            }

            DisplayEachStockPagination(stockitem, onStockClick)
        }

        // 2. The "Pagination Spinner"
        // This only shows at the very bottom while fetching the next chunk
        if (state.isAppending) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
fun DisplayEachStockPagination(stock: Stock, onStockClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStockClick(stock.name) }
            .padding(16.dp)
    ) {
        Row {
            Column {
                Text(text = stock.name)
                Text(text = stock.ticker, color = Color.Gray)
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            thickness = 0.5.dp,
            color = Color.LightGray
        )
    }
}