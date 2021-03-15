package com.example.meals_schdueler

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class AddIngredientFragment : Fragment(), View.OnClickListener, NutritiousDialog.DialogListener  {
    // mDrawerLayout = findViewById<View>(R.id.drawerLayout) as DrawerLayout
    lateinit var recipeName: EditText
    lateinit var typeOfMeal: Spinner
    var type: String = ""
    lateinit var nutritiousBtn : Button
    lateinit var stam : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val x = inflater.inflate(R.layout.add_ingredients_layout, null)
        recipeName = x.findViewById(R.id.editTextRecipeName)
        typeOfMeal = x.findViewById(R.id.typeOfMealSpinner)
        nutritiousBtn = x.findViewById(R.id.buttonNutritious)
        stam=x.findViewById(R.id.textViewStam)



        nutritiousBtn.setOnClickListener(this)
        typeOfMeal.onItemSelectedListener = SpinnerActivity(type)


        //Toast.makeText(activity, "Toast", Toast.LENGTH_SHORT).show()

        return x
    }


    class SpinnerActivity(var type: String) : Activity(), AdapterView.OnItemSelectedListener {


        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
            type = parent.getItemAtPosition(pos).toString()
            // Log.v("Elad", "$type")
            // val activity = view?.context
//            when (pos) {
//                0 -> Toast.makeText(activity, "$type", Toast.LENGTH_SHORT).show()
//                1 -> Toast.makeText(activity, "$type", Toast.LENGTH_SHORT).show()
//                2 -> Toast.makeText(activity, "$type", Toast.LENGTH_SHORT).show()
//            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Another interface callback


        }
    }

    override fun onClick(p0: View?) {


        if(p0 == nutritiousBtn){
            val dialog = NutritiousDialog()

            dialog.show(childFragmentManager,"NutritiousDialog")



        }
    }

    override fun applyText(str: String) {
        Log.v("Elad","easdsad")
        stam.setText(str)
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }



   }


