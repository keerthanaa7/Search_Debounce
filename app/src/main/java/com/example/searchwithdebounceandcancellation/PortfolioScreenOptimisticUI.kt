package com.example.searchwithdebounceandcancellation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PortfolioScreenOptimistic(viewModel: StockViewModelOptimisticUI, onStockClick: (String) -> Unit) {
    val state by viewModel.uistate.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val uiState = state) {
            is UIStateOptimisticUI.Loading -> CircularProgressIndicator()
            is UIStateOptimisticUI.Error -> Text(uiState.message, color = Color.Red)
            is UIStateOptimisticUI.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.stocklist, key = { it.ticker }) { stock ->
                        StockItemOptimisticUI(
                            stock = stock,
                            onItemClick = { onStockClick(it) },
                            onFavoriteToggle = { viewModel.toggleFavorite(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StockItemOptimisticUI(stock: Stock, onItemClick: (String) -> Unit, onFavoriteToggle: (Stock) -> Unit) {
    Column(modifier = Modifier.clickable { onItemClick(stock.name) }) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stock.name, style = MaterialTheme.typography.titleMedium)
                Text(text = stock.ticker, style = MaterialTheme.typography.bodySmall)
            }

            // Interactive Heart Icon
            IconButton(onClick = { onFavoriteToggle(stock) }) {
                Icon(
                    imageVector = if (stock.isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (stock.isFavorite) Color.Red else Color.Gray
                )
            }
        }
        HorizontalDivider()
    }
}