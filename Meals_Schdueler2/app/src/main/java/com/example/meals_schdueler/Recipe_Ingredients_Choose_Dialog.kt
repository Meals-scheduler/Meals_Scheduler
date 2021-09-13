package com.example.meals_schdueler

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.ViewCompat
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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class Recipe_Ingredients_Choose_Dialog(
    recipeIngredients: Recipe_Ingredients_List,
    userIngredientss: ArrayList<Ingredient>,
    costList: ArrayList<Float>
) :
    DialogFragment() {


    lateinit var btnDone: Button
    lateinit var exit: ImageView


    private var progressBar: ProgressBar? = null
    private lateinit var searchView: SearchView
    private lateinit var noResultsTextView: TextView




    var l: Recipe_Ingredients_List = recipeIngredients

    // private var isFirstTime = isFirstTime
    private var ingredientList: ArrayList<Ingredient>? = userIngredientss
    private var costList: ArrayList<Float> = costList
    private var ingredietnsChoosenRecyclerViewAdapter: Recipe_Ingredients_Choose_RecyclerViewAdapter? =
        null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var view: View =
            inflater.inflate(R.layout.recipe_ingredients_choose_dialog, container, false)
        btnDone = view.findViewById(R.id.doneBtn)
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView
        recyclerView.setHasFixedSize(true);
        progressBar = view.findViewById(R.id.progress_bar)
        searchView = view.findViewById(R.id.search_bar)
        noResultsTextView = view.findViewById(R.id.tv_emptyTextView)
        ingredietnsChoosenRecyclerViewAdapter = Recipe_Ingredients_Choose_RecyclerViewAdapter(
            ingredientList!!,
            l.list!!,
            childFragmentManager,
            costList,
            requireContext(),
            recyclerView,
            progressBar,
            searchView,
            noResultsTextView
        )

        ingredietnsChoosenRecyclerViewAdapter!!.setmValues(ingredientList!!)
        ingredietnsChoosenRecyclerViewAdapter!!.startTask()
        exit = view.findViewById(R.id.imageViewX)
//        cost = rootView.findViewById(R.id.editTextCost)


        //nestedScroll.setOnScrollChangeListener(this)




        btnDone.setOnClickListener({

            dismiss()
        })

        exit.setOnClickListener({
            dismiss()
        })
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = ingredietnsChoosenRecyclerViewAdapter
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