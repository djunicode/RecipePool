package com.example.recipepool.screens

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.R
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.SearchList
import com.example.recipepool.databinding.ActivitySearchBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterSearchList
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Search : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var rv:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.searchToolbar)

        binding.chipyash.setOnCloseIconClickListener {

            val chip = binding.chipyash

            binding.chipGroupSearch.removeView(chip)
        }
        val chip = Chip(this)
        chip.text = "ABC"
        chip.text
        chip.isCloseIconVisible = true
        chip.setTextColor(resources.getColor(R.color.black))

        binding.chipGroupSearch.addView(chip)

        chip.setOnCloseIconClickListener {
            binding.chipGroupSearch.removeView(chip)
        }

        //
        val chip2 = Chip(this)
        chip2.text = "XYZ"
        chip2.isCloseIconVisible = true
        chip2.setTextColor(resources.getColor(R.color.black))

        binding.chipGroupSearch.addView(chip2)

        rv = binding.rvSearch
        rv.apply {
            layoutManager = GridLayoutManager(this@Search,2)
        }

        val array = intent.getStringArrayExtra("search")
        val arr = array as Array<String>
        hit(arr)

         binding.imageSearch.setOnClickListener {

             Log.d("button","clicked")

             val text = binding.searchActsearch.text.toString().trim()

             if (text.isEmpty()) {
                 return@setOnClickListener
             }

             val to_search = text.split("\\s".toRegex()).toTypedArray()

             hit(to_search)
             Log.d("to_search",to_search.size.toString() )
             /*val b = hashMapOf<String,Array<String>>()
             b["ingredient"] = to_search
             val searchs = rf.search(b)
             Log.d("searchs",searchs.toString())

             searchs.enqueue(object : Callback<ArrayList<SearchList>>{
                 override fun onResponse(
                     call: Call<ArrayList<SearchList>>,
                     response: Response<ArrayList<SearchList>>
                 ) {
                     Log.d("search","inside" )

                     if (response.code() == 200){
                         Log.d("url","response.raw().request().url();"+response.raw().request().url())

                         val adapt = RecyclerAdapterSearchList(response.body()!!)
                         adapt.notifyDataSetChanged()
                         rv.adapter = adapt

                         Log.d(" search response",response.toString())
                     }else{
                         Log.d("error",response.message())
                         Log.d("error",response.code().toString())
                     }
                 }

                 override fun onFailure(call: Call<ArrayList<SearchList>>, t: Throwable) {
                     Log.d(" search response",t.message.toString())
                 }

             })*/


         }

    }

    fun hit (a : Array<String>){

        val b = hashMapOf<String,Array<String>>()
        b["ingredient"] = a
        val searchs = rf.search(b)
        Log.d("searchs",searchs.toString())

        searchs.enqueue(object : Callback<ArrayList<SearchList>>{
            override fun onResponse(
                call: Call<ArrayList<SearchList>>,
                response: Response<ArrayList<SearchList>>
            ) {
                Log.d("search","inside" )

                if (response.code() == 200){
                    Log.d("url","response.raw().request().url();"+response.raw().request().url())

                    val adapt = RecyclerAdapterSearchList(response.body()!!)
                    adapt.notifyDataSetChanged()
                    rv.adapter = adapt

                    Log.d(" search response",response.toString())
                }else{
                    Log.d("error",response.message())
                    Log.d("error",response.code().toString())
                }
            }

            override fun onFailure(call: Call<ArrayList<SearchList>>, t: Throwable) {
                Log.d(" search response",t.message.toString())
            }

        })


    }
}