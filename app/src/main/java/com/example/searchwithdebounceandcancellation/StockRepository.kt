package com.example.searchwithdebounceandcancellation

class StockRepository() {
    suspend fun getPortFolio(): Portfolio{
        return RetrofitInstance.stockapi.getPortfolio()
    }

    // Mocking an update call
    suspend fun updateFavoriteOnServer(ticker: String, isFav: Boolean) {
        // networkApi.postFavorite(ticker, isFav)
        kotlinx.coroutines.delay(1000) // Simulate network lag
         throw Exception("Simulated Fail") // Uncomment this to test Rollback!
    }
}