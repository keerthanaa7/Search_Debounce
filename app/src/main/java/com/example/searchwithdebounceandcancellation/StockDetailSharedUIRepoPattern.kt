package com.example.searchwithdebounceandcancellation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StockDetailScreenSharedUIRepoPattern(
    ticker: String,
    viewModel: RepoSharedUIStockViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uistate.collectAsState()

    // The "Sync" happens here: we find the specific stock in the updated list
    val stock = (state as? UIState.Success)?.stocklist?.find { it.ticker == ticker }

    stock?.let { item ->
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text("< Back", modifier = Modifier.clickable { onBack() }, color = Color.Blue)

            Spacer(modifier = Modifier.height(20.dp))

            Text(item.name, style = MaterialTheme.typography.headlineLarge)
            Text(item.ticker, style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { viewModel.toggleFavorite(item.ticker) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (item.isFavorite) "Remove from Favorites" else "Add to Favorites")
            }
        }
    }
}