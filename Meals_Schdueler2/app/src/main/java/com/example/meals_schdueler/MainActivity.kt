package com.example.meals_schdueler


//import android.widget.Toolbar
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

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
        mFragmentTransaction.replace(R.id.containerView, IngredientsFragment()).commit()



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



}