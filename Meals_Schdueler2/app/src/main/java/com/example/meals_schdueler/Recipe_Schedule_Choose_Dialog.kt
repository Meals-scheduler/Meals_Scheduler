package com.example.meals_schdueler

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Recipe_Schedule_Choose_Dialog(

    recipeIngredients: Recipe_Ingredients_List,
    userRecipes:  ArrayList<Recipe>,
    recipeQuantities :Recipe_Ingredients_List,
    typeOfMeal : String
) : DialogFragment(){

    lateinit var exit: ImageView
    private var recipesList:  ArrayList<Recipe> = userRecipes
    private var l: Recipe_Ingredients_List = recipeIngredients
    private var quantitiess : Recipe_Ingredients_List = recipeQuantities
    private lateinit var btnDone : Button
    private var typeOfMeall = typeOfMeal
    lateinit var typeOfMeal : TextView
    private var progressBar: ProgressBar? = null
    private lateinit var searchView: SearchView
    private lateinit var noResultsTextView: TextView



    private var recipesChoosenRecyclerViewAdapter: Recipe_Schedule_Choose_RecyclerViewAdapter? =
        null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {




        var view: View =
            inflater.inflate(R.layout.recipes_choose_list, container, false)
        btnDone = view.findViewById(R.id.doneBtn)
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView
        progressBar = view.findViewById(R.id.progress_bar)
        searchView = view.findViewById(R.id.search_bar)
        noResultsTextView = view.findViewById(R.id.tv_emptyTextView)

        recipesChoosenRecyclerViewAdapter = Recipe_Schedule_Choose_RecyclerViewAdapter(
            recipesList!!, l.list!!, quantitiess.list!!, childFragmentManager,progressBar,searchView,noResultsTextView,
            context!!
        )

        recipesChoosenRecyclerViewAdapter!!.setmValues(recipesList!!)
        recipesChoosenRecyclerViewAdapter!!.startTask()

        exit=view.findViewById(R.id.imageViewX)
        typeOfMeal = view.findViewById(R.id.textViewChoose)
        typeOfMeal.setText("Choose " + typeOfMeall +" Recipes :")
//        cost = rootView.findViewById(R.id.editTextCost)






        btnDone.setOnClickListener({

            dismiss()
        })

        exit.setOnClickListener({
            dismiss()
        })

        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = recipesChoosenRecyclerViewAdapter


        return view

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