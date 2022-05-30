package com.example.recipepool.apis

import com.example.recipepool.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    ): Call<ArrayList<SearchList>>

    // https://therecipepool.pythonanywhere.com/api/search
    @POST("api/search")
    fun searchRecipe(
        @Body recipe: HashMap<String, Any>
    ): Call<ArrayList<SearchList>>

    @POST("api/filter-meal/")
    fun filterMealType(
        @Body meal: HashMap<String, ArrayList<String>>
    ): Call<ArrayList<FoodList>>


//    @Multipart
//    @POST("api/recipe/0")
//    fun addRecipe(
//        @Header("Authorization") token: String?,
//        @Part image: MultipartBody.Part,
//        @Part ingredient:List<RequestBody>,
//        @Part step:List<RequestBody>,
//        @PartMap body : Map<String,RequestBody>
//    ):Call<AddNewRecipe>

    @POST("api/recipe/0/")
    fun addRecipe(
        @Header("Authorization") token: String?,
        @Body body:AddNewRecipe,
    ):Call<ResponseNewRecipe>

    @POST("account/token-refresh/")
    fun  refresh(
        @Body params: refresh
    ):Call<refresh>

}