package com.example.recipepool.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.data.SearchList
import com.example.recipepool.databinding.ActivitySearchBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterSearchList

class Search : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchList:ArrayList<SearchList>
    private lateinit var rv:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.searchToolbar)

        searchList = arrayListOf()

        rv = binding.rvSearch
        rv.apply {
            layoutManager = GridLayoutManager(this@Search,2)
            adapter = RecyclerAdapterSearchList(searchList)
        }
    }
}