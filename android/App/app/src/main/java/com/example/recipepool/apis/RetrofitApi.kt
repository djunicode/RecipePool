package com.example.recipepool.apis

import com.example.recipepool.data.login
import com.example.recipepool.data.signup
import com.example.recipepool.data.token
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
    ):Call<login>

    @POST("account/email-verify/")
    fun emailVerify(
        @Body params:token
    ):Call<token>

    @POST("account/token-refresh/?token=")
    fun refresh(
        @Query("token") token:String?
    ):Call<token>
}