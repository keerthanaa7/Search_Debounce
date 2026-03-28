package com.example.searchwithdebounceandcancellation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PortfolioScreenSharedRepo(stockViewModel: RepoSharedUIStockViewModel, onStockClick: (String) -> Unit) {
    val uistate by stockViewModel.uistate.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            when (val state = uistate) {
                is UIState.Loading -> CircularProgressIndicator()
                is UIState.Error -> Text(text = state.message, color = Color.Red)
                is UIState.Success -> {
                    PortfolioSubScreenSharedUIRepo(
                        stocklist = state.stocklist,
                        onStockClick = onStockClick,
                        onFavClick = { ticker -> stockViewModel.toggleFavorite(ticker) }
                    )
                }
            }
        }
    }
}

@Composable
fun PortfolioSubScreenSharedUIRepo(
    stocklist: List<Stock>,
    onStockClick: (String) -> Unit,
    onFavClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(stocklist, key = { it.ticker }) { stockitem ->
            DisplayEachStockSharedUIRepoPattern(
                stock = stockitem,
                onStockClick = onStockClick,
                onFavClick = { onFavClick(stockitem.ticker) }
            )
        }
    }
}

@Composable
fun DisplayEachStockSharedUIRepoPattern(
    stock: Stock,
    onStockClick: (String) -> Unit,
    onFavClick: () -> Unit
) {
    // Wrap in a Row to handle the text click and the icon click separately
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStockClick(stock.ticker) } // Fixed: Use ticker for navigation
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = stock.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = stock.ticker, style = MaterialTheme.typography.bodySmall)
        }

        // Favorite Icon
        androidx.compose.material3.IconButton(onClick = onFavClick) {
            androidx.compose.material3.Icon(
                imageVector = if (stock.isFavorite)
                    androidx.compose.material.icons.Icons.Filled.Favorite
                else
                    androidx.compose.material.icons.Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (stock.isFavorite) Color.Red else Color.Gray
            )
        }
    }
    HorizontalDivider(thickness = 1.dp)
}