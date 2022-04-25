package com.example.recipepool

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipepool.data.FoodList
import com.example.recipepool.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private var foodies: List<FoodList> = arrayListOf(
        FoodList("Salad with Grilled Chicken", "40min", false, 3f),
        FoodList("Egg Curry", "15min", false, 4f),
        FoodList("Chocolate Chip Cookies", "50min", false, 4f),
        FoodList("Chicken Enchiladas", "40min", false, 5f),
        FoodList("Minestrone Soup", "15min", false, 4f)
    )

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
    }
}