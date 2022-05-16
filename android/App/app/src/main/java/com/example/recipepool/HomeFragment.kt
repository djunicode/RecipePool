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
import com.example.recipepool.data.trendingCuisine
import com.example.recipepool.databinding.FragmentHomeBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterFoodCard
import com.example.recipepool.recycleradapter.RecyclerAdapterTrending
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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



        val trending_cuisine = rf.trendingCuisine()
        var a = ArrayList<trendingCuisine>()

        trending_cuisine.enqueue(object: Callback<ArrayList<trendingCuisine>>{
            override fun onResponse(
                call: Call<ArrayList<trendingCuisine>>,
                response: Response<ArrayList<trendingCuisine>>
            ) {
                if (response.code() == 200){
                    a = response.body()!!

                    binding.recyclerViewTrendingRecipe.apply {
                        layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = RecyclerAdapterTrending(a)
                    }
                    Log.d("response",a.toString())
                }
                else{
                    Log.d("response error", response.message().toString())
                }
            }
            override fun onFailure(call: Call<ArrayList<trendingCuisine>>, t: Throwable) {
                TODO("Not yet implemented")
            }


        }
        )
    }
}