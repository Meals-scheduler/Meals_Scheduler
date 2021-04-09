package com.example.meals_schdueler

import android.content.Intent
import android.content.Intent.getIntent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
    //var userID = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDrawerLayout = findViewById<View>(R.id.drawerLayout) as DrawerLayout
        nNavigationView = findViewById<View>(R.id.navView) as NavigationView

        mFragmentManager = supportFragmentManager
        mFragmentTransaction = mFragmentManager.beginTransaction()
        mFragmentTransaction.replace(R.id.containerView, IngredientsFragment()).commit()
        var i: Intent = getIntent()
        userID = i!!.extras!!.get("UserID") as Int


        // listener for the navigation
        nNavigationView.setNavigationItemSelectedListener { menuItem ->
            mDrawerLayout.closeDrawers()


            if (menuItem.itemId == R.id.nav_item_ingredients) {
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, IngredientsFragment()).commit()


            }

            if (menuItem.itemId == R.id.nav_item_sent) {
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, SentFragment()).commit()
            }

            if (menuItem.itemId == R.id.nav_item_draft) {
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, DraftFragment()).commit()
            }

            if(menuItem.itemId == R.id.nav_item_logout){
                var preferences : SharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE) // private access to the file, only our app can read it
                var editor : SharedPreferences.Editor = preferences.edit()
                editor.putString("remember","false")
                editor.apply()
                val i = Intent(applicationContext, MainActivity::class.java)
                startActivity(i)
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

        var userID : Int = 0
    }


}