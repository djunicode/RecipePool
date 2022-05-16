package com.example.recipepool

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.FoodList
import com.example.recipepool.data.Trending
import com.example.recipepool.databinding.FragmentHomeBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterFoodCard
import com.example.recipepool.recycleradapter.RecyclerAdapterTrending
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private var foodies: List<FoodList> = arrayListOf(
        FoodList("Salad with Grilled Chicken", "40min", false, 3f),
        FoodList("Egg Curry", "15min", false, 4f),
        FoodList("Chocolate Chip Cookies", "50min", false, 4f),
        FoodList("Chicken Enchiladas", "40min", false, 5f),
        FoodList("Minestrone Soup", "15min", false, 4f)
    )

    private var trending: List<Int> = arrayListOf(1, 2, 3, 4, 5)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.recyclerViewFoodCard.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = RecyclerAdapterFoodCard(foodies)
        }

        binding.recyclerViewTrendingRecipe.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = RecyclerAdapterTrending(trending)
        }

        getTrending()
    }

    private fun getTrending() {
        val trendingRecipe = rf.getTrending()

        trendingRecipe.enqueue(object : Callback<List<Trending>> {
            override fun onResponse(call: Call<List<Trending>>, response: Response<List<Trending>>) {
                if(response.isSuccessful) {
                    Log.d("Trending", response.message().toString() + " " + response.code().toString())

                    val result = response.body()
                    Log.d("TrendingResult", result.toString())
                }
            }

            override fun onFailure(call: Call<List<Trending>>, t: Throwable) {
                Log.d("Trending", t.message.toString())
            }

        })
    }
}