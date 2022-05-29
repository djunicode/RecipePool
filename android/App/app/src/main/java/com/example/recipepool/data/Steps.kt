package com.example.recipepool.data

import com.google.gson.annotations.SerializedName

data class Steps(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("steps")
    val step: String?,
    @SerializedName("recipe")
    val recipeId: Int?
)