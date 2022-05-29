package com.example.recipepool.data

import com.google.gson.annotations.SerializedName

class Cuisine(
    @SerializedName("cuisine_name")
    val cuisineName: String?,
    @SerializedName("images")
    val images: String?,
    @SerializedName("dishType")
    val likes: Int?
)