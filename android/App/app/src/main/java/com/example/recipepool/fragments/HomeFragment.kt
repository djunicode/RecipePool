package com.example.recipepool.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.Recipe
import com.example.recipepool.databinding.FragmentHomeBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterFoodCard
import com.example.recipepool.recycleradapter.RecyclerAdapterTrending
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


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

        binding.homePG.visibility = View.INVISIBLE

        binding.recyclerViewFoodCard.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.homePG.visibility = View.VISIBLE
        binding.breakfastChip.isChecked = true
        searchByMeal("breakfast")

        binding.breakfastChip.setOnClickListener {
            binding.homePG.visibility = View.VISIBLE
            searchByMeal("breakfast")
        }
        binding.lunchChip.setOnClickListener {
            binding.homePG.visibility = View.VISIBLE
            searchByMeal("lunch")
        }
        binding.snacksChip.setOnClickListener {
            binding.homePG.visibility = View.VISIBLE
            searchByMeal("dessert")
        }
        binding.dinnerChip.setOnClickListener {
            binding.homePG.visibility = View.VISIBLE
            searchByMeal("dinner")
        }


        val trendingCuisine = rf.getTrending()
        var a = ArrayList<Recipe>()


        trendingCuisine.enqueue(object : Callback<ArrayList<Recipe>> {
            override fun onResponse(
                call: Call<ArrayList<Recipe>>,
                response: Response<ArrayList<Recipe>>
            ) {
                if (response.code() == 200) {
                    a = response.body()!!
                    binding.recyclerViewTrendingRecipe.apply {
                        layoutManager =
                            LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = RecyclerAdapterTrending(a)
                    }
                    Log.d("response", a.toString())
                } else {
                    Log.d("response error", response.message().toString())
                }
            }

            override fun onFailure(call: Call<ArrayList<Recipe>>, t: Throwable) {
                Log.d("Trending Cuisine", t.message.toString())
            }


        }
        )
        // getTrending()
    }

    private fun searchByMeal(query: String) {

        val arr = ArrayList<String>()
        arr.add(query)
        val map = hashMapOf<String, ArrayList<String>>()
        map["meal"] = arr
        Log.d("meal filter", query)
        val call = rf.filterMealType(map)


        call.enqueue(object : Callback<ArrayList<Recipe>> {
            override fun onResponse(
                call: Call<ArrayList<Recipe>>,
                response: Response<ArrayList<Recipe>>
            ) {
                if (query == "dinner" || query == "lunch"){
                    TimeUnit.SECONDS.sleep(2L)
                }

                if (response.code() == 200) {
                    binding.homePG.visibility = View.INVISIBLE
                    Log.d(
                        "chip",
                        "success"
                    )
                    Log.d("data", response.body()?.size.toString())
                    binding.recyclerViewFoodCard.adapter =
                        RecyclerAdapterFoodCard(response.body()!!)
                } else {
                    binding.homePG.visibility = View.INVISIBLE
                    Log.d("error", response.message())
                    Log.d("error", response.code().toString())
                    Log.d("url", "response.raw().request().url();" + response.raw().request().url())
                    searchByMeal(query)
                }
            }


            override fun onFailure(call: Call<ArrayList<Recipe>>, t: Throwable) {
                Log.d("FoodList Error", t.message.toString())
                binding.homePG.visibility = View.INVISIBLE
                Log.d("error", t.message.toString())
                searchByMeal(query)
            }


        })
    }

    /* private fun getTrending() {
         Log.d("getTrending","inside")
         val trendingRecipe = rf.getTrending()

         trendingRecipe.enqueue(object : Callback<List<Trending>> {
             override fun onResponse(call: Call<List<Trending>>, response: Response<List<Trending>>) {
                 if(response.isSuccessful) {
                     Log.d("Trending", response.message().toString() + " " + response.code().toString())

                     val result = response.body()
                   //  Log.d("TrendingResult", result.toString())
                 }else{
                     Log.d("trending",response.message())
                 }
             }

             override fun onFailure(call: Call<List<Trending>>, t: Throwable) {
                 Log.d("Trending", t.message.toString())
             }

         })
     }*/
}