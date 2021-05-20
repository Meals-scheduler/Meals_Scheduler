package com.example.meals_schdueler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class RecipesFragments : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val x = inflater.inflate(R.layout.tab_layout, null)
        tabLayout = x.findViewById<View>(R.id.tabs) as TabLayout
        viewPager = x.findViewById<View>(R.id.viewpager) as ViewPager

        viewPager.adapter = myAdapter(childFragmentManager)
        tabLayout.post{ tabLayout.setupWithViewPager(viewPager)}
        return x
    }

    internal inner class myAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm){


        override fun getItem(position: Int): Fragment {
            when(position){
                0-> return AddRecipeFragment()
                1-> return MyRecipeFragment()
                2-> return AddRecipeFragment()
            }
            return AddIngredientFragment()
        }


        override fun getPageTitle(position: Int): CharSequence? {
            when(position){
                0-> return "Add Recipe"
                1-> return "My Recipes"
                2-> return "All Recipes"
            }
            return null
        }
        override fun getCount(): Int {
            return items
        }
    }

    companion object{ // similar to static in java - unique instance

        lateinit var tabLayout : TabLayout
        lateinit var  viewPager : ViewPager
        var  items = 3

    }
}