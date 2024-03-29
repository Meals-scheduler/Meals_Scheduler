package com.example.meals_schdueler

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView



class UserInterFace : AppCompatActivity() {

    lateinit var mDrawerLayout: DrawerLayout  // lateinit preventing the object from being null.
    lateinit var nNavigationView: NavigationView
    lateinit var mFragmentManager: FragmentManager
    lateinit var mFragmentTransaction: FragmentTransaction


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        mDrawerLayout = findViewById<View>(R.id.drawerLayout) as DrawerLayout
        nNavigationView = findViewById<View>(R.id.navView) as NavigationView
        mFragmentManager = supportFragmentManager
        mFragmentTransaction = mFragmentManager.beginTransaction()
        mFragmentTransaction.replace(R.id.containerView, IngredientsFragment()).commitNow()




        var i: Intent = getIntent()
        userID = i!!.extras!!.get("UserID") as Int


        // listener for the navigation
        nNavigationView.setNavigationItemSelectedListener { menuItem ->
            mDrawerLayout.closeDrawers()


            if (menuItem.itemId == R.id.nav_item_ingredients) {
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, IngredientsFragment()).commit()


            }

            if (menuItem.itemId == R.id.nav_item_recipes) {
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, RecipesFragments()).commit()
            }

            if (menuItem.itemId == R.id.nav_item_daily_schedule) {
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, DailyScheduleFragments()).commit()
            }

            if (menuItem.itemId == R.id.nav_item_upcoming_schedule) {
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, UpcomingScheduleFragment()).commit()
            }
            if (menuItem.itemId == R.id.nav_item_weekly_schedule) {
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, WeeklySchedukeFragments()).commit()
            }
            if (menuItem.itemId == R.id.nav_item_monthly_schedule) {
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, MonthlySchedukeFragments()).commit()
            }

            if (menuItem.itemId == R.id.nav_item_yearly_schedule) {
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, YearlySchedukeFragments()).commit()
            }

            if (menuItem.itemId == R.id.nav_item_events) {
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, EventFragments()).commit()
            }






            if (menuItem.itemId == R.id.nav_item_logout) {
                //when we loggd out , i want to destroy the fragments of the previous user
                //mFragmentTransaction.remove(AllingredientsFragment1())
                // when we click logout we write false into the file
                var preferences: SharedPreferences = getSharedPreferences(
                    "checkbox",
                    MODE_PRIVATE
                ) // private access to the file, only our app can read it
                var editor: SharedPreferences.Editor = preferences.edit()
                editor.putString("remember", "false")
                editor.apply()
                // change the singletone into null

                val i = Intent(applicationContext, MainActivity::class.java)
                startActivity(i)
                // to avoid constant loading of AllIngredients Data

            }
            false


        }

        // val toolbar: Toolbar = findViewById(R.id.toolbar)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar

        val mDrawerToggle = ActionBarDrawerToggle(
            this,
            mDrawerLayout,
            toolbar,
            R.string.app_name,
            R.string.app_name
        )

        mDrawerLayout.setDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()


    }


    companion object {

        var userID: Int = 0
    }


}