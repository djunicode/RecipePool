package com.example.recipepool

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawer: DrawerLayout
    private lateinit var toolbar: Toolbar
    lateinit var nav: NavigationView
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        nav = binding.leftNav

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        drawer = binding.drawer
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.isDrawerIndicatorEnabled = true
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.black)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val search_btn = menu.findItem(R.id.search)
        val search = search_btn?.actionView as SearchView
        search.queryHint = "Search Here"
        return true
    }
}