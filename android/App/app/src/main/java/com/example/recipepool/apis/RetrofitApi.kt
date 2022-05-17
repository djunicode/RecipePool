package com.example.recipepool.apis

import com.example.recipepool.data.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitApi {
    @POST("account/signup/")
    fun signup(
        @Body params: signup
    ): Call<signup>

    @POST("account/login/")
    fun login(
        @Body params: login
    ): Call<login>

    @GET("account/email-verify/?token=")
    fun emailVerify(
        @Query("token") token: String?
    ): Call<token>

    @POST("account/google/")
    fun google(
        @Body params: google
    ):Call<google>

    @GET("api/trending-cuisine/")
    fun trendingCuisine ():Call< ArrayList<trendingCuisine>>


    @POST("api/filter-ingredient/")
    fun search_ingredient(
      //  @Body params : filter_ingredients
         @Body ingredient : HashMap<String,Array<String>>
    ) : Call<ArrayList<SearchList>>

    // https://therecipepool.pythonanywhere.com/api/search
    @POST("api/search")
    fun searchRecipe(
        @Body recipe : HashMap<String,String>
    ) : Call<ArrayList<SearchList>>

    @POST("api/filter-meal/")
    fun filterMealType(
        @Body meal : HashMap<String,ArrayList<String>>
    ) : Call<ArrayList<FoodList>>

    @POST("api/recipe/{id}")
        fun addRecipe(
            @Body addRecipe: AddRecipe
        ):Call<AddRecipe>
}