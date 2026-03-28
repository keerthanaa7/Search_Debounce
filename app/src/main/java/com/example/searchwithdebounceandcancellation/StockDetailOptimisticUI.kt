package com.example.searchwithdebounceandcancellation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun StockDetailScreenOptimisticUI(
    stockticker: String?,
    viewModel: StockViewModelOptimisticUI,
    onBack: () -> Unit
) {
    // 1. Observe the unified state
    val state by viewModel.uistate.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val uiState = state) {
            is UIStateOptimisticUI.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UIStateOptimisticUI.Success -> {
                // 2. STABILIZED LOOKUP: Trim whitespace and ignore capitalization
                val selectedStock = uiState.stocklist.find {
                    it.ticker.trim().equals(stockticker?.trim(), ignoreCase = true)
                }

                if (selectedStock != null) {
                    Text(text = selectedStock.name, style = MaterialTheme.typography.headlineMedium)
                    Text(text = selectedStock.ticker, style = MaterialTheme.typography.bodyLarge)

                    Spacer(Modifier.size(32.dp))

                    // 3. THE FAVORITE BUTTON (Synced with Global State)
                    IconButton(
                        onClick = { viewModel.toggleFavorite(selectedStock) },
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            imageVector = if (selectedStock.isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle Favorite",
                            tint = if (selectedStock.isFavorite) Color.Red else Color.Gray,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Text(
                        text = if (selectedStock.isFavorite) "Saved to Favorites" else "Tap to Favorite",
                        style = MaterialTheme.typography.labelLarge
                    )
                } else {
                    // This shows the ticker we are looking for to help you debug
                    Text("Ticker '$stockticker' not found in current list.")
                    Text("Total stocks available: ${uiState.stocklist.size}", style = MaterialTheme.typography.bodySmall)
                }
            }
            is UIStateOptimisticUI.Error -> {
                Text("Error: ${uiState.message}", color = Color.Red)
            }
        }
    }
}