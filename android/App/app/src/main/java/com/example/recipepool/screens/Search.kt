package com.example.recipepool.screens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.R
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.Recipe
import com.example.recipepool.databinding.ActivitySearchBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterSearchList
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Search : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var rv:RecyclerView

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.searchToolbar)


        var filters = arrayListOf<String>()
        var searchText = ""
        //popup of FILTER
        val builder = AlertDialog.Builder(this)
            .create()
        val dialogView = layoutInflater.inflate(R.layout.filter_dialog,null)

        builder.setView(dialogView)
        builder.setCanceledOnTouchOutside(false)
        builder.window!!.attributes.verticalMargin = -0.6F
        builder.window!!.attributes.horizontalMargin = 0.2F


        rv = binding.rvSearch
        rv.apply {
            layoutManager = GridLayoutManager(this@Search,2)
        }

       /* val array = intent.getStringArrayExtra("search")
        val arr = array as Array<String>
        hit(arr)*/
        val toSearch = intent.getStringExtra("search")
        searchText = toSearch!!
        Log.d("string", toSearch)

        search(searchText,filters)

         binding.imageSearch.setOnClickListener {
             Log.d("button","clicked")

             val text = binding.searchActsearch.text.toString().trim()
             searchText = text

             if (text.isEmpty()) {
                 return@setOnClickListener
             }
             search(searchText,filters)
         }

        binding.imageFilter.setOnClickListener {
            Log.d("imagefilter","reached")
            builder.show()
        }


        // on close of dialog box
        dialogView.findViewById<ImageView>(R.id.filterDialog_cancel).setOnClickListener {
            builder.cancel()

            val chipGroup =  dialogView.findViewById<ChipGroup>(R.id.chip_group_filterDialog)

             binding.chipGroupSearch.removeAllViews()

            val list = chipGroup.checkedChipIds
            val selected = arrayListOf<String>()
            for ( i in list){
                val chip = dialogView.findViewById<Chip>(i)
                val chipText = chip.text.toString()
                    selected.add(chipText)

                val addChip = Chip(this)
                addChip.text = chipText
                addChip.setChipBackgroundColorResource(R.color.filter_chip)
                addChip.setChipStrokeColorResource(R.color.chip_stroke)
                addChip.isCloseIconEnabled = true

                addChip.setOnCloseIconClickListener {

                    binding.chipGroupSearch.removeView(addChip)
                    filters.remove(chipText)
                    search(searchText,filters)
                    chip.isChecked = false
                }

                binding.chipGroupSearch.addView(addChip)

            }
            Log.d("selected chip", selected.toString())

            filters = selected
            if (list.size != 0){
                search(searchText,filters)
            }





        }

        binding.asBack.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }


    }


    fun search ( query : String, filter : ArrayList<String> ){
        val map = HashMap<String,Any>()
        map["recipe"] = query
        map["filters"] = filter
        Log.d("string",map.toString())
        val search = rf.searchRecipe(map)

        search.enqueue(object : Callback<ArrayList<Recipe>>{
            override fun onResponse(
                call: Call<ArrayList<Recipe>>,
                response: Response<ArrayList<Recipe>>
            ) {
                when {
                    response.code() == 200 -> {
                        binding.noresultTv.isVisible = false
                        val adapt = RecyclerAdapterSearchList(response.body()!!)
                        adapt.notifyDataSetChanged()
                        rv.adapter = adapt

                        Log.d("TAG ",response.body().toString())

                    }
                    response.code() == 408 -> {
                        search(query,filter)
                    }
                    else -> {
                        Log.d("error",response.message())
                        Log.d("error",response.code().toString())
                        Log.d("url","response.raw().request().url();"+response.raw().request().url())
                        Toast.makeText(this@Search,"Check your Internet Connection",Toast.LENGTH_SHORT).show()
                        binding.noresultTv.isVisible = true
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Recipe>>, t: Throwable) {
                Log.d("error",t.message!!)
                binding.noresultTv.isVisible = true
            }

        })

    }

    /*fun hit (a : Array<String>){

        val b = hashMapOf<String,Array<String>>()
        b["ingredient"] = a
        val searchs = rf.search_ingredient(b)
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


    }*/
}