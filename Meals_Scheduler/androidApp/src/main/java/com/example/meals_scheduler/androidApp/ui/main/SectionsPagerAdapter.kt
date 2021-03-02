package com.example.meals_scheduler.androidApp.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.meals_scheduler.androidApp.*

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    private val TAB_TITLES = intArrayOf( // example of array of int with R.string values(should be int)
        R.string.fragment_matching_title,
        R.string.fragment_question_title,
        R.string.fragment_pickupline_title,
        R.string.fragment_profile_title
    )

    override fun getItem(position: Int): Fragment {


        when (position) { // example of switch case
            0 -> return BlueFragment.newInstance("","")
            1 -> return RedFragment.newInstance("","")
            2 -> return BlackFragment.newInstance("","")
            3 -> return PinkFragment.newInstance("","")
        }

        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position + 1)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return TAB_TITLES.size
    }
}