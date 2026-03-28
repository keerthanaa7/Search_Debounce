package com.example.searchwithdebounceandcancellation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun PortfolioScreenLive(stockViewModel: StockViewModelLivePrice, onStockClick:(String) -> Unit){

    val searchstate by stockViewModel.searchquery.collectAsState()
    val uistate by stockViewModel.filteredlist.collectAsState()
    Column {
        TextField(value = searchstate, onValueChange = {stockViewModel.UpdateQuery(it)},
            label = {Text("enter input")})

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center){

            when(val state = uistate){
                is UIStatelive.Loading ->
                    CircularProgressIndicator()
                is UIStatelive.Error ->
                    Text(text = state.message, color = Color.Red)
                is UIStatelive.Success ->
                    PortfolioSubScreenLive(state.stocklist){
                            stockticker -> onStockClick(stockticker)
                    }

            }
        }
    }

}

@Composable
fun PortfolioSubScreenLive(stocklist: List<Stock>, onStockClick:(String) -> Unit){
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(stocklist, key = {it.ticker}){
            stockitem -> DisplayEachStockLive(stockitem, onStockClick)
        }
    }

}

@Composable
fun DisplayEachStockLive(stock: Stock, onStockClick:(String) -> Unit){
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