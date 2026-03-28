package com.example.searchwithdebounceandcancellation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StockDetailScreenShared(
    ticker: String?,
    viewModel: SharedStockViewModel,
    onBack: () -> Unit // Add this callback
) {
    val state by viewModel.uistate.collectAsState()
    val stock = (state as? UIState.Success)?.stocklist?.find { it.ticker == ticker }

    stock?.let { item ->
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Add a Back Button for easier testing
            androidx.compose.material3.TextButton(onClick = onBack) {
                Text("< Back to Portfolio")
            }

            Text("Stock: ${item.name}", style = MaterialTheme.typography.headlineMedium)
            Text("Ticker: ${item.ticker}")

            androidx.compose.material3.Button(
                onClick = { viewModel.toggleFavorite(item.ticker) }
            ) {
                Text(if (item.isFavorite) "Remove from Favorites" else "Add to Favorites")
            }
        }
    }
}