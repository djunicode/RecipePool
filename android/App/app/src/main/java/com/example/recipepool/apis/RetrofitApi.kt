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

//    @Multipart
//    @POST("api/recipe/0/")
//    fun addRecipe2(
//        @Header("Authorization") token: String?,
//        @Part("calories") calorie:Int?,
//        @Part("cuisine") cuisine:String?,
//        @Part("cuisineType") cuisineType: String?,
//        @Part("dishType") dishTyp: String?,
//        @Part("healthLabels") healthLabels: String?,
//        @Part("instructions") instructions: String?,
//        @Part("label") label: String?,
//        @Part("mealType") mealType: String?,
//        @Part("missingIngredients") missingIngredients: String?,
//        @Part("totalNutrients") totalNutrients: String?,
//        @Part("totalTime") totalTime: String?,
//        @Part("likes") like:Int?,
//        @Part image: MultipartBody.Part,
//        @Query("ingredient_list") ingredient:List<AddRecipeIngredient>,
//        @Query("steps_list") steps: List<AddRecipeSteps>,
////        @PartMap body : Map<String,RequestBody>
//    ):Call<String>

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
        @Part image: MultipartBody.Part
    ):Call<ResponseNewRecipe>

    @POST("account/token-refresh/")
    fun  refresh(
        @Body params: refresh
    ):Call<refresh>

    @POST("account/token-refresh/")
    fun refreshToken(
        @Body token: TokenRefresh
    ): Call<TokenRefresh>
}