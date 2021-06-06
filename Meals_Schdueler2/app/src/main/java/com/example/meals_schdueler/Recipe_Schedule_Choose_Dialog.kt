package com.example.meals_schdueler

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class Recipe_Schedule_Choose_Dialog(

    recipeIngredients: Recipe_Ingredients_List,
    userRecipes: ArrayList<Recipe>,
    recipeQuantities :Recipe_Ingredients_List,
    typeOfMeal : String
) : DialogFragment() {

    lateinit var exit: ImageView
    private var recipesList: ArrayList<Recipe>? = userRecipes
    private var l: Recipe_Ingredients_List = recipeIngredients
    private var quantities : Recipe_Ingredients_List = recipeQuantities
    private lateinit var btnDone : Button
    private var typeOfMeall = typeOfMeal
    lateinit var typeOfMeal : TextView


    private var recipesChoosenRecyclerViewAdapter: Recipe_Schedule_Choose_RecyclerViewAdapter? =
        null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        recipesChoosenRecyclerViewAdapter = Recipe_Schedule_Choose_RecyclerViewAdapter(
            recipesList!!, l.list!!, quantities.list!!, childFragmentManager
        )


        var rootView: View =
            inflater.inflate(R.layout.recipes_choose_list, container, false)
        btnDone = rootView.findViewById(R.id.doneBtn)
        val recyclerView = rootView.findViewById<View>(R.id.list) as RecyclerView
        recyclerView.adapter = recipesChoosenRecyclerViewAdapter
        recipesChoosenRecyclerViewAdapter!!.setmValues(recipesList!!)
        exit=rootView.findViewById(R.id.imageViewX)
        typeOfMeal = rootView.findViewById(R.id.textViewChoose)
        typeOfMeal.setText("Choose " + typeOfMeall +" Recipes :")
//        cost = rootView.findViewById(R.id.editTextCost)


        btnDone.setOnClickListener({

            dismiss()
        })

        exit.setOnClickListener({
            dismiss()
        })
        return rootView

    }

    // trigger the onDissmiss in AddRecipeFramgent to tell it that this dialog is dissmissed.
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val parentFragment: Fragment? = parentFragment
        if (parentFragment is DialogInterface.OnDismissListener) {
            (parentFragment as DialogInterface.OnDismissListener?)!!.onDismiss(dialog)
        }






    }




}