package com.example.recipepool.screens

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.R
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.Recipe
import com.example.recipepool.databinding.ActivityProfileNewBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterProfileNew
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class ProfileActivityNew : AppCompatActivity() {

    private lateinit var binding: ActivityProfileNewBinding
    private var cookBooksClicked by Delegates.notNull<Boolean>()
    private lateinit var rv : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.newProfileToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val pref = applicationContext.getSharedPreferences("SharedPref", MODE_PRIVATE)
        val token = pref.getString("refresh token", null)

        rv = binding.profileRv
        rv.apply {
            layoutManager = GridLayoutManager(this@ProfileActivityNew,2)
        }

         cookBooksClicked = true

        binding.buttonCookBooks.setOnClickListener {
            cookBooksClicked = true

            binding.buttonCookBooks.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.chip_stroke))
            binding.buttonSavedRecipes.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.white))

            recycler(token)

        }
        binding.buttonSavedRecipes.setOnClickListener {
            cookBooksClicked = false
            binding.buttonCookBooks.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.white))
            binding.buttonSavedRecipes.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.chip_stroke))


            recycler(token)
        }




    }

    private fun recycler(token: String?) {



        if (cookBooksClicked){

            val userRecipes = rf.userRecipes(token)

            userRecipes.enqueue(object :  Callback<ArrayList<Recipe>>{
                override fun onResponse(
                    call: Call<ArrayList<Recipe>>,
                    response: Response<ArrayList<Recipe>>
                ) {
                    var data = response.body()
                    if (data != null) {
                        Log.d("TAG", data.size.toString())
                    }
                    else{
                        Log.d("TAG", "data is null")
                    }
                    val adapt = RecyclerAdapterProfileNew(data, "cookBook")
                    rv.adapter = adapt
                }

                override fun onFailure(call: Call<ArrayList<Recipe>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })


        }


    }
}