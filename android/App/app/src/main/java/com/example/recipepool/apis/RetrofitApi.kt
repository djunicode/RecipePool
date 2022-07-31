package com.example.recipepool.apis

import com.example.recipepool.data.*
import okhttp3.MultipartBody
import retrofit2.Call
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
        @Body ingredient: HashMap<String, ArrayList<String>>
    ): Call<ArrayList<Recipe>>

    // https://therecipepool.pythonanywhere.com/api/search
    @POST("api/search")
    fun searchRecipe(
        @Body recipe: HashMap<String, Any>
    ): Call<ArrayList<Recipe>>

    @POST("api/filter-meal/")
    fun filterMealType(
        @Body meal: HashMap<String, ArrayList<String>>
    ): Call<ArrayList<Recipe>>

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

    @POST("api/recipe/0/")
    fun addRecipe(
        @Header("Authorization") token: String?,
        @Body body:AddNewRecipe,
    ):Call<ResponseNewRecipe>

    @Multipart
    @PATCH("api/recipe/{id}/")
    fun addImage(
        @Header("Authorization") token: String?,
        @Path("id") id:Int,
        @Part image: MultipartBody.Part?
    ):Call<ResponseNewRecipe>

    @POST("account/token-refresh/")
    fun  refresh(
        @Body params: refresh
    ):Call<refresh>

    @POST("account/token-refresh/")
    fun refreshToken(
        @Body token: TokenRefresh
    ): Call<TokenRefresh>

    @GET("account/inventory/{id}/")
    fun getInventory(
        @Header("Authorization") token:String?,
        @Path("id") id:Int,
    ):Call<ArrayList<Inventory>>

    @POST("account/inventory/{id}/")
    fun addInventory(
        @Header("Authorization") token:String?,
        @Path("id") id:Int,
        @Body params: Inventory
    ):Call<Inventory>

    @PATCH("account/inventory/{id}/")
    fun updateInventory(
        @Header("Authorization") token:String?,
        @Path("id") id:Int,
        @Body params: Inventory
    ):Call<Inventory>

    @DELETE("account/inventory/{id}/")
    fun deleteInventory(
        @Header("Authorization") token:String?,
        @Path("id") id:Int,
    ):Call<String>
}