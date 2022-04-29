package com.example.recipepool.constants

import com.example.recipepool.apis.RetrofitApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConstants {
    const val baseUrl = "https://therecipepool.pythonanywhere.com/"

    val rf = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitApi::class.java)

}