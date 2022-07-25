package com.example.recipepool.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.ArrayList


data class Recipe(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("cuisine")
    val cuisine: Cuisine?,
    @SerializedName("createdBy")
    val createdBy: Int?,
    @SerializedName("label")
    val recipeName: String?,
    @SerializedName("instructions")
    val instructions: String?,
    @SerializedName("steps_list")
    val stepsList: ArrayList<Steps>?,
    @SerializedName("totalTime")
    val totalTime: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("healthLabels")
    val healthLabels: String?,
    @SerializedName("totalNutrients")
    val totalNutrients: String?,
    @SerializedName("calories")
    val calories: Int?,
    @SerializedName("cuisineType")
    val cuisineType: String?,
    @SerializedName("mealType")
    val mealType: String?,
    @SerializedName("dishType")
    val dishType: String?,
    @SerializedName("likes")
    val likes: Int?,
    @SerializedName("missingIngredients")
    val missingIngredients: String?,
    @SerializedName("ingredient_list")
    val ingredientList: ArrayList<IngredientList>?
) : Parcelable {

    data class Cuisine(
        @SerializedName("cuisine_name")
        val cuisineName: String?,
        @SerializedName("images")
        val images: String?,
        @SerializedName("dishType")
        val likes: Int?
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int
        ) {
        }

        override fun describeContents(): Int {
            TODO("Not yet implemented")
        }

        override fun writeToParcel(p0: Parcel?, p1: Int) {
            TODO("Not yet implemented")
        }

        companion object CREATOR : Parcelable.Creator<Cuisine> {
            override fun createFromParcel(parcel: Parcel): Cuisine {
                return Cuisine(parcel)
            }

            override fun newArray(size: Int): Array<Cuisine?> {
                return arrayOfNulls(size)
            }
        }
    }


    data class Steps(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("steps")
        val step: String?,
        @SerializedName("recipe")
        val recipeId: Int?
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeValue(id)
            parcel.writeString(step)
            parcel.writeValue(recipeId)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Steps> {
            override fun createFromParcel(parcel: Parcel): Steps {
                return Steps(parcel)
            }

            override fun newArray(size: Int): Array<Steps?> {
                return arrayOfNulls(size)
            }
        }
    }


    data class IngredientList(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("quantity")
        val quantity: Double?,
        @SerializedName("recipe")
        val recipeId: Int?,
        @SerializedName("ingredient")
        val ingredient: Int?,
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeValue(id)
            parcel.writeString(name)
            parcel.writeValue(quantity)
            parcel.writeValue(recipeId)
            parcel.writeValue(ingredient)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<IngredientList> {
            override fun createFromParcel(parcel: Parcel): IngredientList {
                return IngredientList(parcel)
            }

            override fun newArray(size: Int): Array<IngredientList?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readParcelable(Cuisine::class.java.classLoader),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(Steps.CREATOR),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.createTypedArrayList(IngredientList.CREATOR)
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}