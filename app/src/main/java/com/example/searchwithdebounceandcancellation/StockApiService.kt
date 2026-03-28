package com.example.searchwithdebounceandcancellation

import retrofit2.http.GET

interface StockApiService {

    @GET(Constants.PORTFOLIO_ENDPOINT)
    suspend fun getPortfolio(): Portfolio
}