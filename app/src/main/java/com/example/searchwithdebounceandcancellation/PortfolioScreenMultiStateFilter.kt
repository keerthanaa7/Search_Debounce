package com.example.searchwithdebounceandcancellation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
// Multi-Filter UI: Combine multiple state sources (Price, Category) into one list.
@Composable
fun PortfolioScreenMultiState(stockViewModel: StockViewModelMultiStateFilter, onStockClick:(String) -> Unit){

    val searchstate by stockViewModel.searchquery.collectAsState()
    val uistate by stockViewModel.filteredlist.collectAsState()
    Column {
        TextField(value = searchstate.query, onValueChange = {stockViewModel.UpdateQuery(it)},
            label = {Text("enter input")})

        Row(modifier = Modifier.horizontalScroll(rememberScrollState())){
            listOf(null, "USD", "GBP", "UUR").forEach { currency ->
                Button(onClick = {stockViewModel.updateCurrency(currency)}) {
                    Text(currency?:"All")
                }
            }
        }
        Text("Max price :${searchstate.maxpriceCents/100}$")
        Slider(value = searchstate.maxpriceCents.toFloat(), onValueChange = {stockViewModel.updateMaxPriceCents(it.toInt())},
            valueRange = 0f..10000f)

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center){

            when(val state = uistate){
                is UIStateFilter.Loading ->
                    CircularProgressIndicator()
                is UIStateFilter.Error ->
                    Text(text = state.message, color = Color.Red)
                is UIStateFilter.Success ->
                    PortfolioSubScreenMultiFilter(state.stocklist){
                            stockticker -> onStockClick(stockticker)
                    }
            }
        }
    }

}

@Composable
fun PortfolioSubScreenMultiFilter(stocklist: List<Stock>, onStockClick:(String) -> Unit){
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(stocklist, key = {it.ticker}){
            stockitem -> DisplayEachStockMultiFilter(stockitem, onStockClick)
        }
    }

}

@Composable
fun DisplayEachStockMultiFilter(stock: Stock, onStockClick:(String) -> Unit){
    Column(modifier = Modifier.clickable{onStockClick(stock.name)}) {
        Row() {
            Column {
                Text(stock.name)
                Text(stock.ticker)

            }


        }
        HorizontalDivider(Modifier.size(5.dp))
    }

}