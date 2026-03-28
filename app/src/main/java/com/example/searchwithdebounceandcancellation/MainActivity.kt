package com.example.searchwithdebounceandcancellation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.NavType.Companion.StringType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.searchwithdebounceandcancellation.ui.theme.SearchWithDebounceAndCancellationTheme

class MainActivity : ComponentActivity() {
    private val stockRepository = StockRepository()
    private val stockRepositoryRepo = StockRepositorySharedUIState()
    private val stockViewModel: StockViewModel by viewModels{
        viewModelFactory {
            initializer {
                StockViewModel(stockRepository)
            }
        }
    }

    private val stockViewModelMultiStateFilter: StockViewModelMultiStateFilter by viewModels {
        viewModelFactory {
            initializer {
                StockViewModelMultiStateFilter(stockRepository)
            }
        }
    }

    private val stockViewModelLivePrice: StockViewModelLivePrice by viewModels {
        viewModelFactory {
            initializer {
                StockViewModelLivePrice(stockRepository)
            }
        }
    }

    private val imageUploadViewModel: ImageUploadViewModel by viewModels {
        viewModelFactory {
            initializer {
                ImageUploadViewModel()
            }
        }
    }

    private val stockViewModelPagination: StockViewModelPagination by viewModels {
        viewModelFactory {
            initializer {
                StockViewModelPagination(stockRepository)
            }
        }
    }

    private val stockViewModelSearch: StockViewModelDebounce by viewModels {
        viewModelFactory {
            initializer {
                StockViewModelDebounce(stockRepository)
            }
        }
    }

    private val sharedStockViewModel: SharedStockViewModel by viewModels {
        viewModelFactory {
            initializer {
                SharedStockViewModel(stockRepository)
            }
        }
    }
    private val sharedStockViewModelStockViewModelRepo: RepoSharedUIStockViewModel by viewModels {
        viewModelFactory {
            initializer {
                RepoSharedUIStockViewModel(stockRepositoryRepo)
            }
        }
    }
    private val stockViewMOdelOptimisticUI: StockViewModelOptimisticUI by viewModels {
        viewModelFactory {
            initializer {
                StockViewModelOptimisticUI(stockRepository)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SearchWithDebounceAndCancellationTheme {
              //  StockScreen(stockViewModel)
               // MultiFilter(stockViewModelMultiStateFilter)
               // LiveScreen(stockViewModelLivePrice)
              //  PaginationScreen(stockViewModelPagination)
               // ImageUploadScreen(imageUploadViewModel)
               // StockScreenSearch(stockViewModelSearch)
               // SharedViewModel(sharedStockViewModel)
              //  SharedRepoPattern(sharedStockViewModelRepo)
                OptimisticUI(stockViewMOdelOptimisticUI)
            }
        }
    }
}

@Composable
fun OptimisticUI(stockViewModel: StockViewModelOptimisticUI) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "PortfolioScreen") {

        // --- PORTFOLIO LIST SCREEN ---
        composable(route = "PortfolioScreen") {
            PortfolioScreenOptimistic(stockViewModel) { ticker ->
                // NAVIGATE: Use the unique ticker as the ID
               // navController.navigate("StockDetailScreenOptimisticUI/${ticker}")
            }
        }


    }
}

@Composable
fun SharedRepoPattern(stockViewModel: RepoSharedUIStockViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "PortfolioScreen") {

        // --- PORTFOLIO LIST SCREEN ---
        composable(route = "PortfolioScreen") {
            PortfolioScreenSharedRepo(stockViewModel) { ticker ->
                // NAVIGATE: Use the unique ticker as the ID
                navController.navigate("StockDetail/${ticker}")
            }
        }

        // --- STOCK DETAIL SCREEN ---
        composable(
            route = "StockDetail/{ticker}",
            arguments = listOf(
                navArgument("ticker") { type = NavType.StringType }
            )
        ) { backstack ->
            val ticker = backstack.arguments?.getString("ticker") ?: ""

            // RENDER: Pass the ticker to the detail screen
            // The detail screen will use this ticker to find the stock in the repo-synced list
            StockDetailScreenSharedUIRepoPattern(
                ticker = ticker,
                viewModel = stockViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}


@Composable
fun SharedViewModel(stockViewModel: SharedStockViewModel){
    val navController = rememberNavController()

    NavHost(navController, startDestination = "PortfolioScreen") {
        composable(route= "PortfolioScreen"){
            PortfolioScreenSharedUIState(stockViewModel) { stockname ->
                navController.navigate("StockDetailScreenShared/${stockname}")
            }
        }
        composable(route = "StockDetailScreenShared/{stockname}", arguments = listOf(navArgument("stockname"){type = StringType})){
                backstack ->
            val stockname = backstack.arguments?.getString("stockname")
            StockDetailScreenShared(stockname, stockViewModel, onBack = { navController.popBackStack()})
        }
    }
}

@Composable
fun ImageUploadScreen(viewModel: ImageUploadViewModel) {
    val uploadList by viewModel.uploads.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        androidx.compose.material3.Button(
            onClick = { viewModel.startUploads(listOf("vacation.jpg", "selfie.png", "document.pdf")) },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Simulate Multi-Upload")
        }

        LazyColumn(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
           items(uploadList, key = {it.id}){
               item -> UploadRow(item)
           }
        }
    }
}

@Composable
fun UploadRow(item: UploadItem) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Text(text = item.fileName)
            Text(
                text = when(item.state) {
                    UploadState.COMPLETED -> "Done"
                    UploadState.UPLOADING -> "${(item.progress * 100).toInt()}%"
                    else -> "Waiting"
                },
                color = if (item.state == UploadState.COMPLETED) Color.Green else Color.Gray
            )
        }

        androidx.compose.material3.LinearProgressIndicator(
            progress = { item.progress },
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            color = if (item.state == UploadState.COMPLETED) Color.Green else Color.Blue
        )
    }
}

@Composable
fun PaginationScreen(stockViewModel: StockViewModelPagination) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "PortfolioScreenPagination") {
        composable(route = "PortfolioScreenPagination") {
            // This is the updated UI we built in the previous step
            PortfolioScreenPagination(stockViewModel) { stockname ->
                navController.navigate("StockDetailScreenPagination/${stockname}")
            }
        }
        composable(
            route = "StockDetailScreenPagination/{stockname}",
            arguments = listOf(navArgument("stockname") { type = StringType })
        ) { backstack ->
            val stockname = backstack.arguments?.getString("stockname")
            // Reusing your existing Detail Screen or creating a generic one
            StockDetailScreen(stockname)
        }
    }
}

@Composable
fun LiveScreen(stockViewModel: StockViewModelLivePrice){
    val navController = rememberNavController()

    NavHost(navController, startDestination = "PortfolioScreenLive") {
        composable(route= "PortfolioScreenLive"){
            PortfolioScreenLive(stockViewModel) { stockname ->
                navController.navigate("StockDetailScreenLive/${stockname}")
            }

        }
        composable(route = "StockDetailScreenLive/{stockname}", arguments = listOf(navArgument("stockname"){type = StringType})){
                backstack ->
            val stockname = backstack.arguments?.getString("stockname")
            StockDetailScreenLive(stockname, stockViewModel)
        }
    }
}

@Composable
fun MultiFilter(stockViewModel: StockViewModelMultiStateFilter){
    val navController = rememberNavController()

    NavHost(navController, startDestination = "PortfolioScreen") {
        composable(route= "PortfolioScreen"){
            PortfolioScreenMultiState (stockViewModel) { stockname ->
                navController.navigate("StockDetailScreen/${stockname}")
            }

        }
        composable(route = "StockDetailScreen/{stockname}", arguments = listOf(navArgument("stockname"){type = StringType})){
                backstack ->
            val stockname = backstack.arguments?.getString("stockname")
            StockDetailScreen(stockname)
        }
    }
}

@Composable
fun StockScreenSearch(stockViewModel: StockViewModelDebounce){
    val navController = rememberNavController()

    NavHost(navController, startDestination = "PortfolioScreen") {
        composable(route= "PortfolioScreen"){
            PortfolioScreenSearch(stockViewModel) { stockname ->
                navController.navigate("StockDetailScreen/${stockname}")
            }

        }
        composable(route = "StockDetailScreen/{stockname}", arguments = listOf(navArgument("stockname"){type = StringType})){
                backstack ->
            val stockname = backstack.arguments?.getString("stockname")
            StockDetailScreen(stockname)
        }
    }

}

@Composable
fun StockScreen(stockViewModel: StockViewModel){
    val navController = rememberNavController()

    NavHost(navController, startDestination = "PortfolioScreen") {
        composable(route= "PortfolioScreen"){
            PortfolioScreen(stockViewModel) { stockname ->
                navController.navigate("StockDetailScreen/${stockname}")
            }
        }
        composable(route = "StockDetailScreen/{stockname}", arguments = listOf(navArgument("stockname"){type = StringType})){
            backstack ->
            val stockname = backstack.arguments?.getString("stockname")
            StockDetailScreen(stockname)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SearchWithDebounceAndCancellationTheme {
        Greeting("Android")
    }
}