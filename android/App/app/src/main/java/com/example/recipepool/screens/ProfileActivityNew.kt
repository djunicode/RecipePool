package com.example.recipepool.screens

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.R
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.Recipe
import com.example.recipepool.data.refresh
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
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var pref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.newProfileToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        pref = applicationContext.getSharedPreferences("SharedPref", MODE_PRIVATE)
        editor = pref.edit()
//        val token = pref.getString("access token", null)
//        Log.d("access token" , token.toString())

        binding.nameProfileNew.text = pref.getString("name", null)
        rv = binding.profileRv
        rv.apply {
            layoutManager = GridLayoutManager(this@ProfileActivityNew,2)
        }

        binding.profileSettings.setOnClickListener {

            val intent  = Intent(this , SettingsActivity::class.java)
            startActivity(intent)
        }
        binding.edtPrfButton.setOnClickListener {

            val intent  = Intent(this , ProfileActivity::class.java)
            startActivity(intent)
        }

         cookBooksClicked = true
          recycler()

        binding.buttonCookBooks.setOnClickListener {
            Log.d("button","clicked")
            cookBooksClicked = true
            binding.buttonCookBooks.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.chip_stroke))
            binding.buttonSavedRecipes.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.white))
            recycler()

        }
        binding.buttonSavedRecipes.setOnClickListener {
            cookBooksClicked = false
            binding.buttonCookBooks.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.white))
            binding.buttonSavedRecipes.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.chip_stroke))
            recycler()
        }




    }

    private fun recycler() {

            val rdata = refresh("", pref.getString("refresh token", null))
            Log.d("TAG r data", rdata.toString())
            val accessToken = rf.refresh(rdata)
            accessToken.enqueue(object : Callback<refresh>{
                override fun onResponse(call: Call<refresh>, response: Response<refresh>) {
                    if (response.code() == 200) {

                        editor.putString("access token", response.body()!!.access.toString())
                        editor.putString("refresh token", response.body()!!.refresh.toString())
                        editor.apply()
                        Log.d("tokens", "Tokens received successfully")
                        Log.d("refresh token", pref.getString("refresh token", null).toString())
                        Log.d("access token", pref.getString("access token", null).toString())

                        if (cookBooksClicked){

                            val userRecipes = rf.userRecipes("Bearer ${response.body()!!.access.toString()}")
                            Log.d("confirm token" , response.body()!!.access.toString())
                            // val userRecipes = rf.userRecipes("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNjU5NzAwMTQ0LCJpYXQiOjE2NTk0NDA5NDQsImp0aSI6IjhhODZiODQ3NDg0YzRlODlhMjA3N2E3YWJiODYzMTFjIiwidXNlcl9pZCI6MX0.rhCN90oUCxoAGRFonSweWhnlopIDwhZOrKJPBnZzXjc" )
                            userRecipes.enqueue(object :  Callback<ArrayList<Recipe>>{
                                override fun onResponse(
                                    call: Call<ArrayList<Recipe>>,
                                    response1: Response<ArrayList<Recipe>>
                                ) {

                                    val data = response1.body()
                                    if (data != null) {
                                        Log.d("TAG", data.toString())
                                    }
                                    else{
                                        Log.d("TAG", "data is null")
                                    }
                                    Log.d("TAG", "response.raw().request().url();" + response1.raw().request().url())
                                    Log.d("TAG" , response.code().toString())
                                    val adapt = RecyclerAdapterProfileNew(data, "cookBook")
                                    rv.adapter = adapt
                                }

                                override fun onFailure(call: Call<ArrayList<Recipe>>, t: Throwable) {
                                    Log.d("TAG list failure", t.message.toString())
                                }

                            })
                        }
                        else{

                            val favourites = rf.getFavourite("Bearer ${response.body()!!.access.toString()}")

                            favourites.enqueue(object :  Callback<ArrayList<Recipe>>{
                                override fun onResponse(
                                    call: Call<ArrayList<Recipe>>,
                                    response2: Response<ArrayList<Recipe>>
                                ) {
                                    val adapt = RecyclerAdapterProfileNew(response2.body(), "favourite")
                                    rv.adapter = adapt
                                }

                                override fun onFailure(
                                    call: Call<ArrayList<Recipe>>,
                                    t: Throwable
                                ) {
                                    TODO("Not yet implemented")
                                }
                            })
                        }

                    }
                    else{
                        Log.d("response code not 200 ", response.code().toString())
                    }
                }

                override fun onFailure(call: Call<refresh>, t: Throwable) {
                    Log.d("TAG token failure", t.message.toString())
                }

            })

    }
}