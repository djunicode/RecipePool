package com.example.recipepool.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipepool.constants.ApiConstants
import com.example.recipepool.data.Recipe
import com.example.recipepool.data.TokenRefresh
import com.example.recipepool.databinding.FragmentFavouriteBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterFavourite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavouriteFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteBinding

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = view.context.getSharedPreferences("SharedPref", AppCompatActivity.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        binding.recyclerViewFavourite.apply {
            layoutManager = GridLayoutManager(view.context, 2)
        }

        getFavourite()
    }

    private fun getFavourite() {
        // Getting new access-token
        val tokenData = TokenRefresh("", sharedPreferences.getString("refresh token", null))
        Log.d("Token", tokenData.refresh.toString())
        val accessToken = ApiConstants.rf.refreshToken(tokenData)

        accessToken.enqueue(object : Callback<TokenRefresh> {
            override fun onResponse(call: Call<TokenRefresh>, response: Response<TokenRefresh>) {
                if (response.code() == 200) {
                    val access = response.body()?.access.toString()
                    val refresh = response.body()?.refresh.toString()
                    Log.d("Access", access)
                    Log.d("Refresh", refresh)
                    editor.putString("access token", access)
                    editor.putString("refresh token", refresh)
                    editor.apply()

                    // Adding in favourites
                    val fetchFavourite = ApiConstants.rf.getFavourite("Bearer $access")

                    fetchFavourite.enqueue(object : Callback<ArrayList<Recipe>> {
                        override fun onResponse(
                            call: Call<ArrayList<Recipe>>,
                            response: Response<ArrayList<Recipe>>
                        ) {
                            if (response.code() == 200 || response.code() == 202) {
                                val responseList = response.body()
                                binding.recyclerViewFavourite.adapter = RecyclerAdapterFavourite(responseList)
                                Log.d("Get Successful", response.code().toString())
                            }
                            else {
                                Log.d("Get error", response.message().toString())
                                Log.d("Get error", response.code().toString())
                                Log.d("Get url", "response.raw().request().url(): " + response.raw().request().url())
                            }
                        }

                        override fun onFailure(call: Call<ArrayList<Recipe>>, t: Throwable) {
                            Log.d("Get Error", t.message.toString())
                        }

                    })

                } else {
                    Log.d("Get token error", response.message())
                    Log.d("Get token error", response.code().toString())
                    Log.d(
                        "Get token url",
                        "response.raw().request().url(): " + response.raw().request().url()
                    )
                }
            }

            override fun onFailure(call: Call<TokenRefresh>, t: Throwable) {
                Log.d("Get Access Error", t.message.toString())
            }

        })
    }
}