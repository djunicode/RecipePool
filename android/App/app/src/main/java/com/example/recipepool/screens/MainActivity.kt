package com.example.recipepool.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.recipepool.R
import com.example.recipepool.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawer: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var nav: NavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //code for setting status bar white
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // shared preferences to
        val pref = applicationContext.getSharedPreferences("SharedPref", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()

        nav = binding.leftNav

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        drawer = binding.drawer
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.isDrawerIndicatorEnabled = true
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.black)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // left nav navigation
        nav.setNavigationItemSelectedListener {
            drawer.closeDrawer(GravityCompat.START)
            when(it.itemId){
                R.id.Logout -> {
                    val intent = Intent(this,Login::class.java)
                    editor.clear()
                    editor.apply()
                    startActivity(intent)
                    finish()
                }
            }
            true
        }

    }

    // search bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val searchBtn = menu.findItem(R.id.search)
        val search = searchBtn?.actionView as SearchView
        search.queryHint = "Search Here"
        return true
    }

}