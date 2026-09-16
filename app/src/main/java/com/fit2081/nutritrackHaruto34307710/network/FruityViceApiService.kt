// Path: NutriTrack/app/src/main/java/com/fit2081/nutritrack/network/FruityViceApiService.kt
package com.fit2081.nutritrackHaruto34307710.network

import com.fit2081.nutritrackHaruto34307710.viewModel.FruitDetails
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface FruityViceApiService {
    @GET("api/fruit/{fruitName}")
    suspend fun getFruitDetails(@Path("fruitName") fruitName: String): Response<FruitDetails>

    companion object {
        private const val BASE_URL = "https://www.fruityvice.com/"

        val instance: FruityViceApiService by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit.create(FruityViceApiService::class.java)
        }
    }
}