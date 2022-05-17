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
import com.example.recipepool.data.trendingCuisine
import com.example.recipepool.databinding.FragmentHomeBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterFoodCard
import com.example.recipepool.recycleradapter.RecyclerAdapterTrending
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

   /* private var foodies: List<FoodList> = arrayListOf(
        FoodList("Salad with Grilled Chicken", "40min", false, 3f),
        FoodList("Egg Curry", "15min", false, 4f),
        FoodList("Chocolate Chip Cookies", "50min", false, 4f),
        FoodList("Chicken Enchiladas", "40min", false, 5f),
        FoodList("Minestrone Soup", "15min", false, 4f)
    )*/

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
        }

         parentFragment?.activity?.findViewById<ChipGroup>(R.id.chip_group)

        searchByMeal("breakfast")


        parentFragment?.activity?.findViewById<Chip>(R.id.breakfast_chip)?.setOnClickListener {
            searchByMeal("breakfast")
        }
        parentFragment?.activity?.findViewById<Chip>(R.id.lunch_chip)?.setOnClickListener {
            searchByMeal("breakfast")
        }
        parentFragment?.activity?.findViewById<Chip>(R.id.snacks_chip)?.setOnClickListener{
            searchByMeal("snacks")
        }

        parentFragment?.activity?.findViewById<Chip>(R.id.dinner_chip)?.setOnClickListener{
            searchByMeal("dinner")
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
                Log.d("Trending Cuisine", t.message.toString())
            }


        }
        )
        getTrending()
    }

    fun searchByMeal ( query : String){

        val arr = ArrayList<String>()
        arr.add(query)
        val map = hashMapOf<String,ArrayList<String>>()
        map["meal"] = arr
        Log.d("meal filter",query)
        val call = rf.filterMealType(map)

        call.enqueue( object : Callback<ArrayList<FoodList>>{
            override fun onResponse(
                call: Call<ArrayList<FoodList>>,
                response: Response<ArrayList<FoodList>>
            ) {
                if (response.code() == 200){
                    Log.d("meal url","response.raw().request().url();"+response.raw().request().url())
                    Log.d("data",response.body().toString())
                    binding.recyclerViewFoodCard.adapter = RecyclerAdapterFoodCard(response.body()!!)
                }
                else{

                    Log.d("error",response.message())
                    Log.d("error",response.code().toString())
                    Log.d("url","response.raw().request().url();"+response.raw().request().url())
                }
            }

            override fun onFailure(call: Call<ArrayList<FoodList>>, t: Throwable) {
                TODO("Not yet implemented")
            }


        })
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