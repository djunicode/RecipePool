package com.example.recipepool.apis

import com.example.recipepool.data.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.*


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
    ): Call<google>

    @GET("api/trending/")
    fun getTrending(): Call<List<Trending>>

    @GET("api/trending-cuisine/")
    fun trendingCuisine(): Call<ArrayList<trendingCuisine>>


    @POST("api/filter-ingredient/")
    fun search_ingredient(
        //  @Body params : filter_ingredients
        @Body ingredient: HashMap<String, Array<String>>
    ): Call<ArrayList<SearchList>>

    // https://therecipepool.pythonanywhere.com/api/search
    @POST("api/search")
    fun searchRecipe(
        @Body recipe: HashMap<String, String>
    ): Call<ArrayList<SearchList>>

    @POST("api/filter-meal/")
    fun filterMealType(
        @Body meal: HashMap<String, ArrayList<String>>
    ): Call<ArrayList<FoodList>>

    @POST("api/recipe/{id}")
    fun addRecipe(
        @Body addRecipe: AddRecipe
    ): Call<AddRecipe>

    @GET("api/favourite/")
    fun getFavourite(
        @Header("Authorization") bearer: String?
    ): Call<ArrayList<Recipe>>

    @POST("api/favourite/")
    fun postFavourite(
        @Header("Authorization") bearer: String?,
        @Body params: Favourite
    ): Call<Favourite>

    @HTTP(method = "DELETE", path = "api/favourite/", hasBody = true)
    fun deleteFavourite(
        @Header("Authorization") bearer: String?,
        @Body params: Favourite
    ): Call<Favourite>

    @POST("account/token-refresh/")
    fun refreshToken(
        @Body token: TokenRefresh
    ): Call<TokenRefresh>
}