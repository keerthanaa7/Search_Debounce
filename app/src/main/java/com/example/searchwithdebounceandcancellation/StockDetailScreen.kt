package com.example.searchwithdebounceandcancellation

import android.widget.Space
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StockDetailScreen(stockname: String?){
    Spacer(Modifier.size(16.dp))
    Text(text = "Stock name is ${stockname}", fontSize = 16.sp)
}