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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
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
fun PortfolioScreenSharedUIState(stockViewModel: SharedStockViewModel, onStockClick: (String) -> Unit) {
    val uistate by stockViewModel.uistate.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            when (val state = uistate) {
                is UIState.Loading -> CircularProgressIndicator()
                is UIState.Error -> Text(text = state.message, color = Color.Red)
                is UIState.Success -> {
                    PortfolioSubScreenSharedUIState(
                        stocklist = state.stocklist,
                        onStockClick = onStockClick,
                        // Pass the toggle logic down
                        onFavoriteToggle = { ticker -> stockViewModel.toggleFavorite(ticker) }
                    )
                }
            }
        }
    }
}

@Composable
fun PortfolioSubScreenSharedUIState(
    stocklist: List<Stock>,
    onStockClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit // New parameter
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(stocklist, key = { it.ticker }) { stockitem ->
            DisplayEachStockSharedUIstate(
                stock = stockitem,
                onStockClick = onStockClick,
                onFavoriteClick = { onFavoriteToggle(stockitem.ticker) } // Pass click up
            )
        }
    }
}

@Composable
fun DisplayEachStockSharedUIstate(
    stock: Stock,
    onStockClick: (String) -> Unit,
    onFavoriteClick: () -> Unit // New parameter
) {
    Column(modifier = Modifier.clickable { onStockClick(stock.ticker) }) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stock.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = stock.ticker, style = MaterialTheme.typography.bodySmall)
            }

            // The visual sync indicator
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (stock.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (stock.isFavorite) Color.Red else Color.Gray
                )
            }
        }
        HorizontalDivider(thickness = 1.dp)
    }
}