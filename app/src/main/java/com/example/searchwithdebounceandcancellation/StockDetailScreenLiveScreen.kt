package com.example.searchwithdebounceandcancellation

import android.widget.Space
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.util.TableInfo
import kotlinx.coroutines.delay

@Composable
fun StockDetailScreenLive(stockname: String?, stockViewModelLivePrice: StockViewModelLivePrice) {
    val liveuistate by stockViewModelLivePrice.liveprice.collectAsState()
    var timeleft by remember { mutableStateOf(5) }
    val stock = (liveuistate as? UIStatelive.Success)?.stocklist?.find {
        it.name == stockname
    }
    LaunchedEffect(key1 = stock?.currentPriceCents) {
        timeleft = 5
        while (timeleft > 0) {
            timeleft--
            delay(1000)
        }
    }
    Column {
        Spacer(Modifier.size(16.dp))
        Text(text = "Stock name is ${stockname}", fontSize = 16.sp)
        Text("Time left :${timeleft}")
    }

}