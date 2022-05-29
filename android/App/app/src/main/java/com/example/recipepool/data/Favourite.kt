package com.example.recipepool.data

import com.google.gson.annotations.SerializedName

data class Favourite(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("user")
    val userEmail: String?,
    @SerializedName("recipe")
    val recipeId: Int?
)