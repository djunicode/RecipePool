package com.example.recipepool.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.data.SearchList
import com.example.recipepool.databinding.ActivityTaggedRecipesBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterSearchList

class TaggedRecipes : AppCompatActivity() {

    private lateinit var binding: ActivityTaggedRecipesBinding
    private lateinit var taggedList: ArrayList<SearchList>
    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaggedRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taggedList = arrayListOf()

        rv = binding.rvTagged
        rv.apply {
            layoutManager = GridLayoutManager(this@TaggedRecipes, 2)
            adapter = RecyclerAdapterSearchList(taggedList)
        }

    }
}